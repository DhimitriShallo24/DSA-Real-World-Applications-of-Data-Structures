package dsa.project.tree;

import java.io.*;
import java.util.List;
import java.util.Random;

// utility class for loading and saving stock data.
// supports full CSV persistence — load from file, save back after every change.
 
public class DataLoader {

        // csv loading
    

    
     //loads stock data from a CSV file into the tree.
     //expected format (with header): date,close
     //@param tree     the tree to populate
     //@param filePath path to the CSV file
     //@return number of entries successfully inserted
     //@throws IOException if the file cannot be opened
     
    public static int loadFromCSV(StockBST tree, String filePath) throws IOException {
        int count   = 0;
        int skipped = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // skip header
            if (line == null) return 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) { skipped++; continue; }

                try {
                    String date  = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    tree.insert(new StockEntry(date, price));
                    count++;
                } catch (Exception e) {
                    skipped++;
                }
            }
        }

        System.out.printf("[DataLoader] Loaded %d entries from '%s'", count, filePath);
        if (skipped > 0) System.out.printf(" (%d rows skipped)", skipped);
        System.out.println(".");
        return count;
    }

    
    //  csv saving
    
    //saves the entire tree back to a CSV file, overwriting it completely.
    //records are written in chronological order.
    //@param tree     the BST whose contents to save
    //@param filePath path to the CSV file to write
    //@throws IOException if the file cannot be written
     
    public static void saveToCSV(StockBST tree, String filePath) throws IOException {
        List<StockEntry> entries = tree.inOrder();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("date,close");
            bw.newLine();
            for (StockEntry e : entries) {
                bw.write(String.format("%s,%.2f", e.getDate(), e.getClosingPrice()));
                bw.newLine();
            }
        }

        System.out.printf("[DataLoader] Saved %d entries to '%s'.%n", entries.size(), filePath);
    }

    
    //  generated data
    

    //fills a StockBST with a simulated trading-day price series.
    //uses a geometric random walk. Generated data is NOT auto-saved to CSV.
    //@param tree       the tree to populate
    //@param ticker     ticker symbol 
    //@param startYear  first year of data
    //@param endYear    last year of data
    //@param startPrice opening price on the first trading day
    //@return number of entries inserted
     
    public static int generateData(StockBST tree, String ticker,
                                   int startYear, int endYear, double startPrice) {
        Random rng   = new Random(42);
        double price = startPrice;
        int count    = 0;

        for (int year = startYear; year <= endYear; year++) {
            for (int month = 1; month <= 12; month++) {
                int days = daysInMonth(month, year);
                for (int day = 1; day <= days; day++) {
                    if (isWeekend(year, month, day)) continue;

                    double mu    =  0.0003;
                    double sigma =  0.012;
                    price *= Math.exp(mu + sigma * rng.nextGaussian());
                    price  = Math.round(price * 100.0) / 100.0;

                    String date = String.format("%04d-%02d-%02d", year, month, day);
                    tree.insert(new StockEntry(date, price));
                    count++;
                }
            }
        }
        System.out.printf("[DataLoader] Generated %d trading days for %s (%d-%d).%n",
                count, ticker, startYear, endYear);
        return count;
    }

    
    //  calendar helpers


    private static boolean isWeekend(int year, int month, int day) {
        int[] t = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
        if (month < 3) year--;
        int dow = (year + year/4 - year/100 + year/400 + t[month-1] + day) % 7;
        return dow == 0 || dow == 6;
    }

    private static int daysInMonth(int month, int year) {
        switch (month) {
            case 4: case 6: case 9: case 11: return 30;
            case 2: return isLeapYear(year) ? 29 : 28;
            default: return 31;
        }
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
