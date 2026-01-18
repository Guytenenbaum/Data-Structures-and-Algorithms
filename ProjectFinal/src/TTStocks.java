public class TTStocks {
    private Node root;

    public TTStocks() {
        root = new Node("~", 0, 0f);
        root.middle = new Node("~", 0, 0f);
        root.left = new Node("", 0, 0f);
        root.left.parent = root;
        root.middle.parent = root;

    }

    public Node getRoot() {
        return this.root;
    }

    public boolean is_leaf(Node node) {
        return node.left == null;
    }


    public Node search(Node root, String stockId) {
        if (is_leaf(root)) {
            if (root.stockId.compareTo(stockId) == 0) {
                return root;
            } else {
                return null;
            }
        }
        if (stockId.compareTo(root.left.stockId) <= 0) {
            return search(root.left, stockId);
        } else if (stockId.compareTo(root.middle.stockId) <= 0) {
            return search(root.middle, stockId);
        }
        return search(root.right, stockId);
    }


    public void update_Key(Node node) {
        node.stockId = node.left.stockId;
        if (node.right != null) {
            node.stockId = node.right.stockId;
        }
        if (node.middle != null && node.middle.stockId.compareTo(node.stockId) > 0) {
            node.stockId = node.middle.stockId;
        }

    }


    public void set_children(Node node, Node left, Node middle, Node right) {
        node.left = left;
        node.right = right;
        node.middle = middle;
        left.parent = node;
        if (middle != null) {
            middle.parent = node;
        }
        if (right != null) {
            right.parent = node;
        }
    }


    public Node insert_and_split(Node x, Node z) {
        Node left = x.left;
        Node middle = x.middle;
        Node right = x.right;

        if (right == null) {
            if (z.stockId.compareTo(left.stockId) < 0) {
                // z is smaller than left, so it becomes the new left
                set_children(x, z, left, middle);
            } else if (z.stockId.compareTo(middle.stockId) < 0) {
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
        Node y = new Node("", 0, 0f);

        if (z.stockId.compareTo(left.stockId) < 0) {
            set_children(x, z, left, null);
            set_children(y, middle, right, null);
        } else if (z.stockId.compareTo(middle.stockId) < 0) {
            set_children(x, left, z, null);
            set_children(y, middle, right, null);
        } else if (z.stockId.compareTo(right.stockId) < 0) {
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


    public void Insert(TTStocks T, Node newLeaf) {
        Node y = T.root;
        if (newLeaf == null) {
            throw new IllegalArgumentException("Cannot insert a null Stock");
        } else if (newLeaf.price <= 0) {
            throw new IllegalArgumentException("Cannot insert a negative price");
        }
        //maybe we would need to change the place of the line below
        LightNode timestampleaf = new LightNode(newLeaf.timestamp, newLeaf.getPrice());
        newLeaf.ttTimeStamp.Insert(newLeaf.ttTimeStamp, timestampleaf);
        while (!(is_leaf(y))) {
            if (newLeaf.stockId.compareTo(y.left.stockId) < 0) {
                y = y.left;
            } else if (y.middle != null && newLeaf.stockId.compareTo(y.middle.stockId) < 0) {
                y = y.middle;
            } else if (y.right != null) {
                y = y.right;
            }
        }
        Node x = y.parent;
        newLeaf = insert_and_split(x, newLeaf);
//x != null &&
        while (x != T.root) {
            x = x.parent;
            if (newLeaf != null) {
                newLeaf = insert_and_split(x, newLeaf);
            } else {
                update_Key(x);
            }
        }
        if (newLeaf != null) {
            Node newRoot = new Node("~", 0, 0f);
            set_children(newRoot, x, newLeaf, null);
            T.root = newRoot;
        }
    }

    public Node Borrow_Or_Merge(Node y) {
        Node z = y.parent;

        if (y == z.left) {
            Node x = z.middle;
            if (x.right != null) {
                set_children(y, y.left, x.left, null);
                set_children(x, x.middle, x.right, null);
            } else {
                set_children(x, y.left, x.left, x.middle);
                set_children(z, x, z.right, null);
            }
            update_Key(y);
        } else if (y == z.middle) {
            Node x = z.left;
            if (x.right != null) {
                set_children(y, x.right, y.left, null);
                set_children(x, x.left, x.middle, null);
            } else {
                set_children(x, x.left, x.middle, y.left);
                set_children(z, x, z.right, null);
            }
            update_Key(x);
        } else { // y == z.right
            Node x = z.middle;
            if (x.right != null) {
                set_children(y, x.right, y.left, null);
                set_children(x, x.left, x.middle, null);
            } else {
                set_children(x, x.left, x.middle, y.left);
                set_children(z, z.left, x, null);
            }
            update_Key(x);
        }

        update_Key(z);
        return z;
    }


    public void Delete(Node x) {
        Node y = x.parent;
        if (x == y.left) {
            set_children(y, y.middle, y.right, null);
        } else if (x == y.middle) {
            set_children(y, y.left, y.right, null);
        } else {
            set_children(y, y.left, y.middle, null);
        }
        update_Key(y);
        // TODO: delete x (?)
        while (y != null) {
            if (y.middle != null) {
                update_Key(y);
                y = y.parent;
            } else {
                if (y != this.root) {
                    y = Borrow_Or_Merge(y);
                } else {
                    this.root = y.left;
                    y.left.parent = null;
                }
            }
        }
    }
}

//    public void printTree(Node node, String indent) {
//        if (node == null) return;
//        System.out.println(indent + "Node: " + node.stockId);
//        printTree(node.left, indent + "  L-");
//        printTree(node.middle, indent + "  M-");
//        printTree(node.right, indent + "  R-");
//    }
//}
