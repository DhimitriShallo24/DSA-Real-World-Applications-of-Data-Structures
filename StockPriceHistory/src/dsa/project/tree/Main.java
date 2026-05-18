package dsa.project.tree;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

 
public class Main {

    private static final StockBST tree = new StockBST();
    private static final Scanner  sc   = new Scanner(System.in);

    // full path of the currently loaded CSV file. Null if none loaded yet
    private static String currentCSVPath = null;

    // true when data was loaded from generated data, not a CSV
    private static boolean usingGeneratedData = false;

    public static void main(String[] args) {
        printBanner();
        runMenu();
        System.out.println("\n  Goodbye!");
        sc.close();
    }

    
    // main menu
    

    private static void runMenu() {
        boolean running = true;
        while (running) {
            printDivider();
            printDataSourceStatus();
            printDivider();
            System.out.println("  MAIN MENU");
            printDivider();
            System.out.println("  1. Load CSV file ");
            System.out.println("  2. Load generated data");
            System.out.println("  3. List all records");
            System.out.println("  4. Search by date");
            System.out.println("  5. Insert a new record");
            System.out.println("  6. Update an existing record");
            System.out.println("  7. Delete a record");
            System.out.println("  8. Range query  (all prices between two dates)");
            System.out.println("  9. Min price in a date window");
            System.out.println(" 10. Max price in a date window");
            System.out.println("  0. Exit");
            printDivider();
            System.out.print("  Choose an option: ");

            String choice = sc.nextLine().trim();
            printDivider();

            switch (choice) {
                case "1":  handleLoadCSV();       break;
                case "2":  handleGenerateData();  break;
                case "3":  handleListAll();        break;
                case "4":  handleSearch();         break;
                case "5":  handleInsert();         break;
                case "6":  handleUpdate();         break;
                case "7":  handleDelete();         break;
                case "8":  handleRangeQuery();     break;
                case "9":  handleMinInRange();     break;
                case "10": handleMaxInRange();     break;
                case "0":  running = false;        break;
                default:
                    System.out.println("  Invalid option. Please enter 0-10.");
            }
        }
    }

    
    // status bar
   

    //prints the current data source and record count at the top of each menu
    private static void printDataSourceStatus() {
        if (tree.isEmpty()) {
            System.out.println("  Data source : (none loaded — use option 1 to load CSV)");
        } else if (usingGeneratedData) {
            System.out.printf("  Data source : Generated data  |  %d records%n", tree.size());
        } else {
            System.out.printf("  Data source : %s  |  %d records%n", currentCSVPath, tree.size());
        }
    }

    
    // load csv
  

    private static void handleLoadCSV() {
        System.out.println("  Tip: place the CSV in the same folder as the project,");
        System.out.println("  then just type the filename (e.g. AAPL_2023_2024.csv).");
        System.out.println("  Or paste the full path (e.g. C:\\Users\\You\\Downloads\\AAPL_2023_2024.csv)");
        System.out.print("\n  CSV file path: ");
        String path = sc.nextLine().trim();

        // remove surrounding quotes that Windows "copy as path" adds
        if (path.startsWith("\"") && path.endsWith("\"")) {
            path = path.substring(1, path.length() - 1);
        }

        if (path.isEmpty()) {
            System.out.println("  No path entered. Cancelled.");
            return;
        }

        // clear the tree before loading new data
        StockBST fresh = new StockBST();
        try {
            DataLoader.loadFromCSV(fresh, path);

            if (fresh.isEmpty()) {
                System.out.println("  Warning: file loaded but no valid records found. Check format.");
                return;
            }

            // commit: replace tree contents
            replaceTree(fresh);
            currentCSVPath   = path;
            usingGeneratedData = false;

            System.out.printf("  Ready! %d records loaded. Tree height: %d.%n",
                    tree.size(), tree.height());
            System.out.println("  All changes (insert / update / delete) will auto-save to this file.");

        } catch (IOException e) {
            System.out.println("  Error: could not read file.");
            System.out.println("  Details: " + e.getMessage());
            System.out.println("  Tip: use 'Shift + Right-click' on the file → 'Copy as path', then paste here.");
        }
    }

    
    // load generated data
    

    private static void handleGenerateData() {
        System.out.println("  (Generated data is for demo only — changes are NOT saved to any file.)");
        System.out.print("  Ticker symbol (e.g. AAPL): ");
        String ticker = sc.nextLine().trim().toUpperCase();
        if (ticker.isEmpty()) ticker = "AAPL";

        int startYear = readInt("  Start year (e.g. 2022): ", 2000, 2100);
        int endYear   = readInt("  End year   (e.g. 2024): ", startYear, 2100);
        double price  = readDouble("  Starting price (e.g. 150.00): ", 0.01, 1_000_000);

        System.out.println();
        StockBST fresh = new StockBST();
        DataLoader.generateData(fresh, ticker, startYear, endYear, price);
        replaceTree(fresh);

        currentCSVPath     = null;
        usingGeneratedData = true;
        System.out.printf("  Tree ready — %d entries, height %d.%n", tree.size(), tree.height());
    }

    
    // list all
    

    private static void handleListAll() {
        if (tree.isEmpty()) { printEmpty(); return; }

        List<StockEntry> entries = tree.inOrder();
        System.out.printf("  %-12s  %10s%n", "Date", "Close ($)");
        System.out.println("  " + "-".repeat(26));
        for (StockEntry e : entries) {
            System.out.println("  " + e);
        }
        System.out.printf("%n  Total: %d records%n", entries.size());
    }

    
    //  search
   

    private static void handleSearch() {
        if (tree.isEmpty()) { printEmpty(); return; }
        String date = readDate("  Enter date (YYYY-MM-DD): ");
        StockEntry result = tree.search(date);
        if (result == null) {
            System.out.println("  No record found for " + date + ".");
        } else {
            System.out.println("  Found: " + result);
        }
    }

    
    //  insert
    

    private static void handleInsert() {
        requireData();
        String date = readDate("  Enter date (YYYY-MM-DD): ");

        // Check if it already exists
        if (tree.search(date) != null) {
            System.out.println("  A record for " + date + " already exists.");
            System.out.println("  Use option 6 (Update) to change its price.");
            return;
        }

        double price = readDouble("  Enter closing price ($): ", 0.01, 1_000_000);
        tree.insert(new StockEntry(date, price));
        System.out.printf("  Inserted: %s at $%.2f.%n", date, price);
        autosave();
    }

    
    //  update  existing record only
    

    private static void handleUpdate() {
        requireData();
        String date = readDate("  Enter date to update (YYYY-MM-DD): ");

        StockEntry existing = tree.search(date);
        if (existing == null) {
            System.out.println("  No record found for " + date + ".");
            System.out.println("  Use option 5 (Insert) to add a new record.");
            return;
        }

        System.out.printf("  Current price for %s: $%.2f%n", date, existing.getClosingPrice());
        double newPrice = readDouble("  Enter new closing price ($): ", 0.01, 1_000_000);
        tree.insert(new StockEntry(date, newPrice)); // insert handles update internally
        System.out.printf("  Updated %s: $%.2f → $%.2f.%n",
                date, existing.getClosingPrice(), newPrice);
        autosave();
    }

    
    //  delete
    

    private static void handleDelete() {
        requireData();
        String date = readDate("  Enter date to delete (YYYY-MM-DD): ");

        StockEntry existing = tree.search(date);
        if (existing == null) {
            System.out.println("  No record found for " + date + ". Nothing deleted.");
            return;
        }

        System.out.printf("  Found: %s — are you sure you want to delete it? (yes/no): ", existing);
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("  Cancelled.");
            return;
        }

        tree.delete(date);
        System.out.printf("  Deleted record for %s. Records remaining: %d.%n",
                date, tree.size());
        autosave();
    }

    
    //  range query
    

    private static void handleRangeQuery() {
        if (tree.isEmpty()) { printEmpty(); return; }
        String from = readDate("  From date (YYYY-MM-DD): ");
        String to   = readDate("  To   date (YYYY-MM-DD): ");

        if (from.compareTo(to) > 0) {
            System.out.println("  Start date must be on or before end date.");
            return;
        }

        List<StockEntry> results = tree.rangeQuery(from, to);
        if (results.isEmpty()) {
            System.out.println("  No records found between " + from + " and " + to + ".");
            return;
        }

        System.out.printf("  Records from %s to %s (%d entries):%n", from, to, results.size());
        System.out.printf("  %-12s  %10s%n", "Date", "Close ($)");
        System.out.println("  " + "-".repeat(26));
        for (StockEntry e : results) {
            System.out.println("  " + e);
        }
    }

    
    //  min/max in window
   

    private static void handleMinInRange() {
        if (tree.isEmpty()) { printEmpty(); return; }
        String from = readDate("  From date (YYYY-MM-DD): ");
        String to   = readDate("  To   date (YYYY-MM-DD): ");
        StockEntry min = tree.minInRange(from, to);
        if (min == null) System.out.println("  No records in that range.");
        else             System.out.println("  Lowest  price: " + min);
    }

    private static void handleMaxInRange() {
        if (tree.isEmpty()) { printEmpty(); return; }
        String from = readDate("  From date (YYYY-MM-DD): ");
        String to   = readDate("  To   date (YYYY-MM-DD): ");
        StockEntry max = tree.maxInRange(from, to);
        if (max == null) System.out.println("  No records in that range.");
        else             System.out.println("  Highest price: " + max);
    }

   
    //  autosave
   
    private static void autosave() {
        if (currentCSVPath == null) {
            System.out.println("  (Generated data — changes not saved to file.)");
            return;
        }
        try {
            DataLoader.saveToCSV(tree, currentCSVPath);
            System.out.println("  CSV updated successfully.");
        } catch (IOException e) {
            System.out.println("  Warning: could not save to CSV: " + e.getMessage());
        }
    }

    
    // helpers

    private static void replaceTree(StockBST fresh) {
        // Clear existing by deleting all
        for (StockEntry e : tree.inOrder()) {
            tree.delete(e.getDate());
        }
        for (StockEntry e : fresh.inOrder()) {
            tree.insert(e);
        }
    }

    // warns the user and returns early if no data is loaded
    private static void requireData() {
        if (tree.isEmpty()) {
            printEmpty();
        }
    }

    //Reads a valid yyyy-mm-dd date from stdin
    private static String readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) return input;
            System.out.println("  Invalid format. Please use YYYY-MM-DD (e.g. 2024-03-15).");
        }
    }

    //Reads an integer in [min, max] from stdin
    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                System.out.printf("  Enter a value between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid whole number.");
            }
        }
    }

    //reads a double in [min, max] from stdin
    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val >= min && val <= max) return val;
                System.out.printf("  Enter a value between %.2f and %.2f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a valid number (e.g. 185.64).");
            }
        }
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  -----------------------------------------------------");
        System.out.println("       Stock Price History — BST Implementation");
        System.out.println("       Data Structures & Algorithms  |  Spring 2025");
        System.out.println("  -----------------------------------------------------");
    }

    private static void printDivider() {
        System.out.println("  ─────────────────────────────────────────────────────");
    }

    private static void printEmpty() {
        System.out.println("  No data loaded. Use option 1 to load the CSV file first.");
    }
}
