package dsa.project.tree;

import java.util.ArrayList;
import java.util.List;

//a binary search tree that indexes stock price history by trading date.
 
 //<p>keys are yyyy-mm-dd date strings; lexicographic ordering matches
 //chronological ordering, so the standard BST invariant gives us
 //chronologically sorted data for free.</p>

 //<p>supported operations:
 //<ul>
 //  <li>insert / update a daily closing price</li>
 //  <li>delete a record by date</li>
 //  <li>search for a specific date's price</li>
 //  <li>in-order traversal (chronological listing)</li>
 //   <li>range query — all entries between two dates</li>
 //   <li>min / max closing price over a date window</li>
   //</ul>
  //</p>
 
public class StockBST {

    //root of the bst; null when the tree is empty
    private BSTNode root;

    //number of entries currently in the tree
    private int size;

    //constructs an empty stockbst
    public StockBST() {
        root = null;
        size = 0;
    }

    
    // insert
   

     //inserts a new stock entry into the tree.
     //if an entry with the same date already exists, its price is updated.
     // @param entry the stock record to insert or update
     
    public void insert(StockEntry entry) {
        root = insertRec(root, entry);
    }

    //recursive helper for insert
    private BSTNode insertRec(BSTNode node, StockEntry entry) {
        if (node == null) {
            size++;
            return new BSTNode(entry);
        }
        int cmp = entry.getDate().compareTo(node.entry.getDate());
        if (cmp < 0) {
            node.left = insertRec(node.left, entry);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, entry);
        } else {
            //duplicate date — update price instead of inserting a new node
            node.entry.setClosingPrice(entry.getClosingPrice());
        }
        return node;
    }

    
    //  search 
    

    //searches for the stock entry recorded on the given date
     //@param date target date in yyyy-mm-dd format
     // @return the matching {@link StockEntry}, or {@code null} if not found
     
    public StockEntry search(String date) {
        BSTNode node = searchRec(root, date);
        return (node == null) ? null : node.entry;
    }

    //recursive helper for search
    private BSTNode searchRec(BSTNode node, String date) {
        if (node == null) return null;
        int cmp = date.compareTo(node.entry.getDate());
        if (cmp < 0)  return searchRec(node.left,  date);
        if (cmp > 0)  return searchRec(node.right, date);
        return node;
    }

    
    // delete
    

    //removes the entry for the specified date from the tree.
    //if no entry exists for that date, the tree is unchanged.
    //@param date the date to remove, in yyyy-mm-dd format
    //@return {@code true} if an entry was removed, {@code false} otherwise
     
    public boolean delete(String date) {
        int before = size;
        root = deleteRec(root, date);
        return size < before;
    }

    //recursive helper for delete
    private BSTNode deleteRec(BSTNode node, String date) {
        if (node == null) return null;// date not found

        int cmp = date.compareTo(node.entry.getDate());

        if (cmp < 0) {
            node.left = deleteRec(node.left, date);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, date);
        } else {
            // found the node to delete
            size--;
            if (node.left == null)  return node.right;
            if (node.right == null) return node.left;

            // two children: replace with in-order successor smallest in right subtree
            BSTNode successor = findMin(node.right);
            node.entry = successor.entry;
            // delete the successor from the right subtree
            // we already decremented size above, so re-increment temporarily
            size++;
            node.right = deleteRec(node.right, successor.entry.getDate());
        }
        return node;
    }

   
    //  in order traversal
   

    //returns all stock entries in chronological order in-order traversal.
    //@return list of entries sorted by date ascending
     
    public List<StockEntry> inOrder() {
        List<StockEntry> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    //recursive in-order traversal
    private void inOrderRec(BSTNode node, List<StockEntry> result) {
        if (node == null) return;
        inOrderRec(node.left,  result);
        result.add(node.entry);
        inOrderRec(node.right, result);
    }

    
    // range query
    

    
     //returns all entries whose date falls within [startDate, endDate] 
     //the search is pruned: subtrees outside the range are not visited
     //@param startDate start of the window, yyyy-mm-dd 
     //@param endDate   end   of the window, yyyy-mm-dd 
     //@return chronologically sorted list of matching entries; empty if none found
     
    public List<StockEntry> rangeQuery(String startDate, String endDate) {
        List<StockEntry> result = new ArrayList<>();
        rangeRec(root, startDate, endDate, result);
        return result;
    }

    //recursive helper that prunes branches outside the target range
    private void rangeRec(BSTNode node, String lo, String hi, List<StockEntry> result) {
        if (node == null) return;

        String d = node.entry.getDate();

        //only go left if this node's date is > lo earlier dates might be in range
        if (d.compareTo(lo) > 0) {
            rangeRec(node.left, lo, hi, result);
        }

        // include this node if it falls within [lo, hi]
        if (d.compareTo(lo) >= 0 && d.compareTo(hi) <= 0) {
            result.add(node.entry);
        }

        // only go right if this node's date is < hi later dates might be in range
        if (d.compareTo(hi) < 0) {
            rangeRec(node.right, lo, hi, result);
        }
    }

    
    //  min / max over a date window
    

    //finds the entry with the lowest closing price in the given date window
     //@param startDate start of the window, yyyy-mm-dd (inclusive)
     //@param endDate   end   of the window, yyyy-mm-dd (inclusive)
     //@return the entry with the minimum closing price, or {@code null} if the window is empty
     
    public StockEntry minInRange(String startDate, String endDate) {
        List<StockEntry> entries = rangeQuery(startDate, endDate);
        if (entries.isEmpty()) return null;
        StockEntry min = entries.get(0);
        for (StockEntry e : entries) {
            if (e.getClosingPrice() < min.getClosingPrice()) min = e;
        }
        return min;
    }

     //finds the entry with the highest closing price in the given date window
     //@param startDate start of the window, yyyy-mm-dd (inclusive)
     //@param endDate   end   of the window, yyyy-mm-dd (inclusive)
     //@return the entry with the maximum closing price, or {@code null} if the window is empty
     
    public StockEntry maxInRange(String startDate, String endDate) {
        List<StockEntry> entries = rangeQuery(startDate, endDate);
        if (entries.isEmpty()) return null;
        StockEntry max = entries.get(0);
        for (StockEntry e : entries) {
            if (e.getClosingPrice() > max.getClosingPrice()) max = e;
        }
        return max;
    }

    
    //  utility helpers
   

    //returns the entry with the globally smallest date (earliest record)
    //@return earliest StockEntry, or {@code null} if the tree is empty
    public StockEntry globalMin() {
        if (root == null) return null;
        return findMin(root).entry;
    }

     //returns the entry with the globally largest date (most recent record)
     //@return most recent StockEntry, or {@code null} if the tree is empty
    
    public StockEntry globalMax() {
        if (root == null) return null;
        return findMax(root).entry;
    }

    //finds the leftmost node in the subtree rooted at {@code node}
    private BSTNode findMin(BSTNode node) {
        while (node.left != null) node = node.left;
        return node;
    }

    //finds the rightmost node in the subtree rooted at {@code node}
    private BSTNode findMax(BSTNode node) {
        while (node.right != null) node = node.right;
        return node;
    }

    
     //returns the number of entries currently stored in the tree.
     //@return entry count
     
    public int size() {
        return size;
    }

    //returns {@code true} if the tree contains no entries.
    //@return {@code true} when empty
    
    public boolean isEmpty() {
        return size == 0;
    }

     //returns the height of the BST (longest path from root to a leaf)
     //an empty tree has height 0
     //@return tree height
     
    public int height() {
        return heightRec(root);
    }

    //recursive height computation
    private int heightRec(BSTNode node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRec(node.left), heightRec(node.right));
    }
}
