package dsa.project.tree;

//a single node in the stock price BST.
//each node stores one {@link StockEntry} and references to
//its left child (earlier dates) and right child (later dates).
 
public class BSTNode {

    //the stock record held by this node
    StockEntry entry;

    //left subtree — entries with earlier dates
    BSTNode left;

    //right subtree — entries with later dates
    BSTNode right;

    //constructs a leaf node holding the given entry.
    // @param entry the stock record to store
     
    public BSTNode(StockEntry entry) {
        this.entry = entry;
        this.left  = null;
        this.right = null;
    }
}
