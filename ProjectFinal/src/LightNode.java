public class LightNode {
    Float diff;
    Long timestamp;
    LightNode left;
    LightNode right;
    LightNode middle;
    LightNode parent;

    public LightNode(Long timestamp, Float diff) {
        this.timestamp = timestamp;
        this.diff = diff;
    }

    public float getDiff() {
        return diff;
    }

}