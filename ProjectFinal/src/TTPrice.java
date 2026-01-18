public class TTPrice {
    private PNode root;

    public TTPrice() {
        root = new PNode("~", 0, Float.POSITIVE_INFINITY , 0);
        root.middle = new PNode("~", 0, Float.POSITIVE_INFINITY, 0);
        root.left = new PNode("~", 0, Float.NEGATIVE_INFINITY, 0);
        root.left.parent = root;
        root.middle.parent = root;

    }

    public PNode getRoot() {
        return root;
    }

    public boolean is_leaf(PNode node) {
        return node.left == null;
    }

    public void update_size(PNode pNode) {
        if (pNode == null) return;

        // Update children first, so their sizes are correct
        if (pNode.left   != null) update_size(pNode.left);
        if (pNode.middle != null) update_size(pNode.middle);
        if (pNode.right  != null) update_size(pNode.right);

        // Now compute this node's size
        if (is_leaf(pNode)) {
            if (pNode.price == Float.POSITIVE_INFINITY || pNode.price == Float.NEGATIVE_INFINITY) {
                pNode.size = 0; // sentinel leaf
            } else {
                pNode.size = 1; // real leaf
            }
        } else {
            pNode.size = 0;
            if (pNode.left   != null) pNode.size += pNode.left.size;
            if (pNode.middle != null) pNode.size += pNode.middle.size;
            if (pNode.right  != null) pNode.size += pNode.right.size;
        }
    }


    public PNode search(PNode root, String stockId, float price) {
        if (is_leaf(root)) {
            if (root.price == price && root.stockId.compareTo(stockId) == 0) {
                return root;
            } else {
                return null;
            }
        }
        if (price < root.left.price || ((root.left.price ==price && (stockId.compareTo(root.left.stockId) <= 0)))) {
            return search(root.left, stockId, price);
        } else if (price < root.middle.price || ((root.middle.price ==price && root.middle.price != Float.POSITIVE_INFINITY && (stockId.compareTo(root.middle.stockId) <= 0)))) {
            return search(root.middle, stockId, price);
        }
        return search(root.right, stockId, price);
    }

    public boolean Node_Compare(PNode node1, PNode node2) {
        if (node1.price < node2.price) {
            return true;
        } else if (node1.price > node2.price) {
            return false;
        } else if (node1.stockId.compareTo(node2.stockId) < 0) {
            return true;
        } else return false;
    }


    public void update_Price(PNode node) {
        if (node == null || node.left == null) {
            return;
        }

        // Start by copying the left child's (price, stockId)
        node.price = node.left.price;
        node.stockId = node.left.stockId;

        // 1) Check the middle child
        if (node.middle != null) {
            if (node.middle.price > node.price) {
                // Middle's price is strictly bigger
                node.price = node.middle.price;
                node.stockId = node.middle.stockId;
            } else if (node.middle.price == node.price) {
                // Same price => pick lexicographically larger stockId
                if (node.middle.stockId.compareTo(node.stockId) > 0) {
                    node.stockId = node.middle.stockId;
                }
            }
        }

        // 2) Check the right child
        if (node.right != null) {
            if (node.right.price > node.price) {
                // Right's price is strictly bigger
                node.price = node.right.price;
                node.stockId = node.right.stockId;
            } else if (node.right.price == node.price) {
                // Same price => pick lexicographically larger stockId
                if (node.right.stockId.compareTo(node.stockId) > 0) {
                    node.stockId = node.right.stockId;
                }
            }
        }
    }



    public void set_children(PNode node, PNode left, PNode middle, PNode right) {
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
        //new addition thats for some reason wa deleted
        update_Price(node);

    }



    public PNode insert_and_split(PNode x, PNode z) {
        PNode left = x.left;
        PNode middle = x.middle;
        PNode right = x.right;

        // Case: No split needed (x has space for z)
        if (right == null) {
            if (Node_Compare(z, left)) {
                set_children(x, z, left, middle);
            } else if (Node_Compare(z, middle)) {
                set_children(x, left, z, middle);
            } else {
                set_children(x, left, middle, z);
            }
            update_Price(x);
            update_size(x);
            return null;
        }

        // Case: Split required
        PNode y = new PNode(null, 0, Float.NEGATIVE_INFINITY , 0); // New node for splitting
        if (Node_Compare(z, left)) {
            set_children(x, z, left, null);
            set_children(y, middle, right, null);
        } else if (Node_Compare(z, middle)) {
            set_children(x, left, z, null);
            set_children(y, middle, right, null);
        } else if (Node_Compare(z, right)) {
            set_children(x, left, middle, null);
            set_children(y, z, right, null);
        } else {
            set_children(x, left, middle, null);
            set_children(y, right, z, null);
        }

        // Update prices of both nodes
        update_Price(x);
        update_Price(y);
        update_size(x);
        update_size(y);
        return y; // Return the new split node
    }


    public void Insert(PNode newLeaf) {
        System.out.println("Inserting stock: " + newLeaf.stockId + " with price: " + newLeaf.price);
        PNode p = newLeaf;
        PNode y = this.root;
        if (newLeaf == null) {
            throw new IllegalArgumentException("Cannot insert a null Stock");
        } else if (newLeaf.price <= 0) {
            throw new IllegalArgumentException("Cannot insert a negative price");
        }
        //maybe we would need to change the place of the line below
        LightNode timestampleaf = new LightNode(newLeaf.timestamp, newLeaf.getPrice());
        while (!(is_leaf(y))) {
            if (Node_Compare(newLeaf, y.left)) {
                y = y.left;
            } else if (Node_Compare(newLeaf, y.middle)) {
                y = y.middle;
            } else {
                y = y.right;
            }
        }
        PNode x = y.parent;
        newLeaf = insert_and_split(x, newLeaf);
        while (x != this.root) {
            if (x.parent == null && x != this.root) {
                throw new IllegalStateException("Parent pointer is null in the middle of the tree!");
            }
            x = x.parent;
            if (newLeaf != null) {
                newLeaf = insert_and_split(x, newLeaf);
            } else {
                update_Price(x);
                update_size(x);
            }
        }
        if (newLeaf != null) {
            PNode newRoot = new PNode(newLeaf.stockId, 0, Float.POSITIVE_INFINITY, 0);
            set_children(newRoot, x, newLeaf, null);
            this.root = newRoot;
            update_size(newRoot);
        }
        if (this.root.size != 1) {
            PNode predecessor = TTPrice_Predecessor(p);

            if (predecessor != null) {
                // Update pointers to maintain correct order
                p.nextLeaf = predecessor.nextLeaf;
                predecessor.nextLeaf = p;
            } else {
                // This node is the smallest; set as the first leaf
                p.nextLeaf = this.root.left.nextLeaf;
                this.root.left.nextLeaf = p;// assuming root.left is the smallest node
            }
        } else {
            // Handle case when the root itself contains only two children
            this.root.left.nextLeaf = p;
            p.nextLeaf = this.root.right;
        }
    }



    public PNode Borrow_Or_Merge(PNode y) {
        PNode z = y.parent;
        if (y == z.left ) {
            PNode x = z.middle;
            if (x.right != null) {
                set_children(y, y.left, x.left, null);
                set_children(x, x.middle, x.right, null);
                update_Price(x);
                update_size(y);
            } else {
                set_children(x, y.left, x.left, x.middle);
                // TODO: delete x (?)
                update_Price(y);
                set_children(z, x, z.right, null);
                update_size(z);
                update_size(x);
            }
            return z;
        }
        if (y == z.middle) {
            PNode x = z.left;
            if (x.right != null) {
                set_children(y, x.right, y.left, null);
                set_children(x, x.left, x.middle, null);
                update_size(x);
                update_size(y);
            } else {
                set_children(x, x.left, x.middle, y.left);
                //TODO: delete y (?)
                update_Price(y);
                update_size(x);
                update_size(y);
            }
            return z;
        }
        y = z.right;
        PNode x = z.middle;
        if (x.right != null) {
            set_children(y, x.right, y.left, null);
            set_children(x, x.left, x.middle, null);
            update_size(x);
            update_size(y);
        } else {
            set_children(x, x.left, x.middle, y.left);
            update_Price(y);
            set_children(z, z.left, x, null);
            update_size(x);
            update_size(y);
        }
        return z;
    }

    public void Delete(TTPrice T, PNode x) {
        if (x == null || is_leaf(x) == false) return; // Ensure valid leaf node

        // Update the predecessor's next leaf link
        PNode predecessor = TTPrice_Predecessor(x);
        if (predecessor != null) {
            predecessor.nextLeaf = x.nextLeaf;
        }

        PNode y = x.parent;

        // Remove x from parent
        if (x == y.left) {
            set_children(y, y.middle, y.right, null);
        } else if (x == y.middle) {
            set_children(y, y.left, y.right, null);
        } else {
            set_children(y, y.left, y.middle, null);
        }

        update_size(y);
        update_Price(y);

        // Rebalance the tree if needed
        while (y != null) {
            if (y.middle != null) {
                update_Price(y);
                update_size(y);
                y = y.parent;
            } else if (y != T.root) {
                y = Borrow_Or_Merge(y);
            } else {
                T.root = y.left != null ? y.left : y.middle;
                if (T.root != null) {
                    T.root.parent = null;
                    update_size(T.root);
                }
                break;
            }
        }

        // Nullify deleted node's fields
        x.left = x.middle = x.right = x.parent = null;
        x.size = 0;
        x.price = Float.NaN;
        x.stockId = null;

        printTree(this.root, "");
    }


    public int Rank(PNode x) {
        int rank = 1;
        PNode y = x.parent;
        while (y != null) {
            if (x == y.middle) {
                rank = rank + y.left.size;
            } else if (x == y.right) {
                rank = rank + y.left.size + y.middle.size;
            }
            x = y;
            y = y.parent;
        }
        return rank;
    }

    public PNode Search_Min(PNode root, Float price) {
        if (is_leaf(root)) {
            return root;
        }
        if(is_leaf(root.left)){
            if (root.middle.price > price ) {
                return root.left;
            }
        }
        if (price <= root.left.price) {
            return Search_Min(root.left, price);
        } else if (price <= root.middle.price) {
            return Search_Min(root.middle, price);
        } else {
            return Search_Min(root.right, price);
        }
    }

    public PNode Search_Max(PNode root, float price) {
        if (is_leaf(root)) {
            return root;
        }
        if(is_leaf(root.left)){
            if(price < root.middle.price) {
                return root.left;
            }
            if(price > root.middle.price) {
                if(root.right ==null || price < root.right.price){
                    return root.middle;
                }
                return root.right;
            }
        }
        if (price < root.left.price || (price < root.middle.left.price && is_leaf(root.middle.left))) {
            return Search_Max(root.left, price);
        } else if (price < root.middle.price || price < root.right.left.price && is_leaf(root.right.left)) {
            return Search_Max(root.middle, price);
        } else {
            return Search_Max(root.right, price);
        }
    }








    public PNode TTPrice_Predecessor(PNode x) {
        PNode y;
        PNode z = x.parent;
        while (x == z.left ) {
            x = z;
            z = z.parent;
        }
        if (x == z.right) {
            y = z.middle;
        } else {
            y = z.left;
        }
        while (!is_leaf(y)) {
            if(y.right != null) {
                y = y.right;
            } else {
                y = y.middle;
            }
        }
        return y;
    }


    public void printTree(PNode node, String indent) {
        if (node == null) return;
        System.out.println(indent + "Node: " +node.stockId+ " " + "price:"+ node.price  + " " +"Size: "+ node.size);
        printTree(node.left, indent + "  L-");
        printTree(node.middle, indent + "  M-");
        printTree(node.right, indent + "  R-");
    }
}
