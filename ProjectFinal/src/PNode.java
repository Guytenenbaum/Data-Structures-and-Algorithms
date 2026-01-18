public class PNode implements Comparable<PNode> {
    String stockId;
    long timestamp;
    float price;
    PNode left;
    PNode right;
    PNode middle;
    PNode parent;
    int size;
    PNode nextLeaf;
    // Constructor
    public PNode(String stockId, long timestamp, float price, int size) {
        this.stockId = stockId;
        this.timestamp = timestamp;
        this.price = price;
        this.size = size;
        this.left = this.right = this.middle = this.parent = null;
        this.nextLeaf = null;
    }


    // Implement compareTo for comparison logic
    @Override
    public int compareTo(PNode other) {
        if (this.price >= other.price) {
            return 1;
        }
        if (this.price < other.price) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Node{" +
                "stockId='" + this.stockId + '\'' +
                ", timestamp=" + this.timestamp +
                ", price=" + this.price +
                '}';
    }

    public void updatePrice(float diff) {
        this.price += diff;
    }
    public float getPrice(){
        return this.price;
    }
}

