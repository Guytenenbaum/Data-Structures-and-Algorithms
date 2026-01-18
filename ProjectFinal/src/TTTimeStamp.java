public class TTTimeStamp {
    private LightNode root;

    public TTTimeStamp() {
        root = new LightNode(Long.MAX_VALUE, 0f);
        root.middle = new LightNode(Long.MAX_VALUE, 0f);
        root.left = new LightNode(Long.MIN_VALUE, 0f);
        root.left.parent = root;
        root.middle.parent = root;
    }

    public void InitTTTimeStamp() {
        new TTTimeStamp();
    }

    public LightNode getRoot() {
        return root;
    }


    public boolean is_leaf(LightNode node) {
        return node.middle == null;
    }

    public LightNode search(LightNode root, long timestamp) {
        if (is_leaf(root)) {
            if (root.timestamp == timestamp) {
                return root;
            } else {
                return null;
            }
        }
        if (root.left.timestamp >= timestamp) {
            return search(root.left, timestamp);
        } else if (root.middle.timestamp >= timestamp) {
            return search(root.middle, timestamp);
        }
        return search(root.right, timestamp);
    }

    public void update_Key(LightNode node) {
        node.timestamp = node.left.timestamp;
        node.diff = node.left.diff;
        if (node.middle != null) {
            node.timestamp = node.middle.timestamp;
            node.diff = node.middle.diff;
        }
        if (node.right != null) {
            node.timestamp = node.right.timestamp;
            node.diff = node.right.diff;
        }
    }
    public void set_children(LightNode node, LightNode left, LightNode middle, LightNode right) {
        node.left = left;
        node.middle = middle;
        node.right = right;

        if (left != null) {
            left.parent = node;
        }
        if (middle != null) {
            middle.parent = node;
        }
        if (right != null) {
            right.parent = node;
        }
        // Ensure the node's price is updated after setting children
        update_Key(node);
    }


    public LightNode insert_and_split(LightNode x, LightNode z) {
        LightNode left = x.left;
        LightNode middle = x.middle;
        LightNode right = x.right;

        if (right == null) {
            if (z.timestamp.compareTo(left.timestamp) < 0) {
                // z is smaller than left, so it becomes the new left
                set_children(x, z, left, middle);
            } else if (z.timestamp.compareTo(middle.timestamp) < 0) {
                // z is between left and middle
                set_children(x, left, z, middle);
            } else {
                // z is larger than or equal to middle, so it becomes the new right
                set_children(x, left, middle, z);
            }
            update_Key(x);
            return null; // Return null when there's no need to split
        }
//here used to be stock id null
        LightNode y = new LightNode(Long.MAX_VALUE,0f);

        if (z.timestamp.compareTo(left.timestamp) < 0) {
            set_children(x, z, left, null);
            set_children(y, middle, right, null);
        } else if (z.timestamp.compareTo(middle.timestamp) < 0 ) {
            set_children(x, left, z, null);
            set_children(y, middle, right, null);
        } else if (z.timestamp.compareTo(right.timestamp) < 0) {
            set_children(x, left, middle, null);
            set_children(y, z, right, null);
        } else {
            set_children(x, left, middle, null);
            set_children(y, right, z, null);
        }
        update_Key(x);
        update_Key(y);
        return y; // Return the new split node
    }


    public void Insert(TTTimeStamp T, LightNode newLeaf ) {
        if(newLeaf.diff == 0) {
            throw new IllegalArgumentException("Cannot have a diff of 0");
        }
        if(newLeaf.timestamp <= 0 ) {
            throw new IllegalArgumentException("Cannot have a timestamp less than  or equal to 0");
        }
        LightNode y = T.root ;
        while (!(is_leaf(y))) {
            if (newLeaf.timestamp.compareTo(y.left.timestamp) < 0) {
                y = y.left;
            } else if (y.middle != null && newLeaf.timestamp.compareTo(y.middle.timestamp) < 0 ) {
                y = y.middle;
            } else if (y.right != null){
                y = y.right;
            }
        }
        LightNode x = y.parent;
        newLeaf = insert_and_split(x, newLeaf);
//x != null &&
        while (x != T.root) {
            x = x.parent;
            if(newLeaf != null) {
                newLeaf = insert_and_split(x, newLeaf);
            } else {
                update_Key(x);
            }
        }
        if(newLeaf != null) {
            LightNode newRoot = new LightNode(Long.MAX_VALUE, 0f);
            set_children(newRoot, x, newLeaf, null);
            T.root = newRoot;
        }
    }


    public LightNode Borrow_Or_Merge(LightNode y) {
        LightNode z = y.parent;
        if (y == z.left) {
            LightNode x = z.middle;
            if (x.right != null) {
                set_children(y, y.left, x.left, null);
                set_children(x, x.middle, x.right, null);
            } else {
                set_children(x, y.left, x.left, x.middle);
                // TODO: delete x (?)
                update_Key(y);
                set_children(z, x, z.right, null);
            }
            return z;
        }
        if (y == z.middle) {
            LightNode x = z.left;
            if (x.right != null) {
                set_children(y, x.right, y.left, null);
                set_children(x, x.left, x.middle, null);
            } else {
                set_children(x, x.left, x.middle, y.left);
                //TODO: delete y (?)
                update_Key(y);
            }
            return z;
        }
        y = z.right;
        LightNode x = z.middle;
        if (x.right != null) {
            set_children(y, x.right, y.left, null);
            set_children(x, x.left, x.middle, null);
        } else {
            set_children(x, x.left, x.middle, y.left);
            update_Key(y);
            set_children(z, z.left, x, null);
        }
        return z;
    }


    public void Delete(TTTimeStamp T, LightNode x) {
        LightNode y = x.parent;
        if (x == y.left) {
            set_children(y, y.middle, y.right, null);
        } else if (x == y.middle) {
            set_children(y, y.left, y.right, null);
        } else {
            set_children(y, y.left, y.middle, null);
        }
        // TODO: delete x (?)
        while (y != null) {
            if (y.middle != null) {
                update_Key(y);
                y = y.parent;
            } else if (y != T.root) {
                y = Borrow_Or_Merge(y);
            } else {
                T.root = y.left;
                y.left.parent = null;
                //TODO: delete y (?)
            }
        }
    }
}



