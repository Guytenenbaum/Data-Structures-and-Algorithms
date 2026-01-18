public class Node {
    String stockId;
    long timestamp;
    float price;
    TTTimeStamp ttTimeStamp;
    Node left;
    Node right;
    Node middle;
    Node parent;
    int size;
    // Constructor
    public Node(String stockId, long timestamp, float price) {
        this.stockId = stockId;
        this.timestamp = timestamp;
        this.price = price;
        this.left = this.right = this.middle = this.parent = null;
        this.ttTimeStamp = new TTTimeStamp();
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
