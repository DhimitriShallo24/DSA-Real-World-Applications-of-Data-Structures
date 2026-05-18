package dsa.project.tree;

//represents a single stock price record keyed by date
//dtes are stored in yyyy-mm-dd format so that lexicographic
//ordering matches chronological ordering
public class StockEntry implements Comparable<StockEntry> {

    //trading date in yyyy-mm-dd format 
    //acts as the BST key
    private final String date;

    //closing price for the trading day (USD)
    private double closingPrice;

    
     //constructs a new stockentry.
     //@param date    trading date in yyyy-mm-dd format
     //@param closingPrice closing price in USD
     
    public StockEntry(String date, double closingPrice) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date must be in YYYY-MM-DD format: " + date);
        }
        if (closingPrice < 0) {
            throw new IllegalArgumentException("Closing price cannot be negative.");
        }
        this.date = date;
        this.closingPrice = closingPrice;
    }

    //returns the trading date string (yyyy-mm-dd)
    public String getDate() {
        return date;
    }

    //returns the closing price in USD
    public double getClosingPrice() {
        return closingPrice;
    }

     //updates the closing price for this entry
     //@param newPrice new closing price in USD
    public void setClosingPrice(double newPrice) {
        if (newPrice < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.closingPrice = newPrice;
    }

     //compares this entry to another by date in chronological order
     //@param other the other stockentry
     //@return negative, zero, or positive as this date is earlier, equal, or later
     
    @Override
    public int compareTo(StockEntry other) {
        return this.date.compareTo(other.date);
    }

    @Override
    public String toString() {
        return String.format("%-12s  $%8.2f", date, closingPrice);
    }
}
