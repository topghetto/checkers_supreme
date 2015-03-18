package uk.ac.kcl.www;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * General tree API.
 * 
 * @author David Matuszek
 * @version January 30, 2007
 * @param <V> The type of value allowed in Tree nodes.
 */
public class Tree<V> {
    private V value;
    private Tree<V> parent;
    private ArrayList<Tree<V>> children;
    private int myIndex;
    
// Constructors
    
    /**
     * Constructor for Tree objects. Creates a single node containing
     * the specified value.
     * 
     * @param value The value to put in the new tree node.
     */
    public Tree(V value) {
        this.value = value;
        parent = null;
        children = new ArrayList<Tree<V>>();
        myIndex = -1;
    }

// Values
    
    /**
     * Returns the value in this node of the tree.
     * 
     * @return The value in this node of the tree.
     */
    public V getValue() {
        return value;
    }
    
    /**
     * Sets the value in this node of the tree.
     * 
     * @param value The value to put in this node.
     */
    public void setValue(V value) {
        this.value = value;
    }

// Tests
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tree)) return false;
        Tree<?> that = (Tree) o;
        if (this.value == null) return that.value == null;
        if (!this.value.equals(that.value)) return false;
        return this.value.equals(that.value)
                && this.children.equals(that.children);
    }
    
    /**
     * Tests whether this tree node is the root.
     * 
     * @return <code>true</code> if this node has no parent.
     */
    public boolean isRoot() {
        return parent == null;
    }
    
    /**
     * Tests whether this node is a leaf.
     * 
     * @return <code>true</code> if this node has no children.
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    /**
     * Tests whether this node is a leaf.
     * 
     * @return <code>true</code> if this node has children.
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    
    /**
     * Tests whether there are nodes "to the right" of this node.
     * @return <code>true</code> if this node is not the last child.
     */
    public boolean hasNextSibling() {
        return myIndex >= 0 &&
               myIndex < parent.children.size() - 1;
    }
    
    /**
     * Tests whether there are nodes "to the left" of this node.
     * @return <code>true</code> if this node is not the first child.
     */
    public boolean hasPreviousSibling() {
        return myIndex > 0;
    }
    
// Getters (structure)
    
    /**
     * Returns the parent of this node, or <code>null</code> if
     * this is a root node.
     * @return The parent of this node.
     */
    public Tree<V> parent() {
        return parent;
    }

    /**
     * Returns the first child of this node, or <code>null</code>
     * if this node has no children.
     * @return The first child of this node.
     */
    public Tree<V> firstChild() {
        if (children.isEmpty()) return null;
        else return children.get(0);
    }
    
    /**
     * Returns the last child of this node, or <code>null</code>
     * if this node has no children.
     * @return The last child of this node.
     */
    public Tree<V> lastChild() {
        if (children.isEmpty()) return null;
        else return children.get(children.size() - 1);
    }
    
    /**
     * Returns the index-th child of this node (zero based), or
     * <code>null</code> if index is too large or too small.
     * @param index The index of a child to retrieve.
     * @return The index-th child of this node, counting from zero.
     */
    public Tree<V> child(int index) {
        if (index < 0 || index >= children.size()) return null;
        else return children.get(index);
    }
    
    /**
     * Returns a genericized list of the children of
     * this node. If the node has no children, an empty
     * list is returned.
     * @return The children of this node.
     */
    public ArrayList<Tree<V>> children() {
        return children;
    }
    
    /**
     * Returns the next sibling of this node, or <code>null</code>
     * if this node is the rightmost node.
     * @return The next sibling of this node.
     */
    public Tree<V> nextSibling() {
        if (hasNextSibling()) {
            return parent.children.get(myIndex + 1);
        }
        else return null;
    }
    
    /**
     * Returns the previous sibling of this node, or <code>null</code>
     * if this node is the leftmost node.
     * @return The previous sibling of this node.
     */
    public Tree<V> previousSibling() {
        if (hasPreviousSibling()) {
            return parent.children.get(myIndex - 1);
        }
        else return null;
    }
    
    /**
     * Returns the depth of this node in the tree; that is,
     * it returns the distance to the root.
     * @return The depth of this node.
     */
    public int depth() {
        if (parent == null) return 0;
        else return 1 + parent.depth();
    }
    
    /**
     * Tests if the given node is the same as this node, or is
     * an ancestor of this node. 
     * @param ancestor The node that may be an ancestor of this node.
     * @return True if the given node is an ancestor.
     */
    public boolean hasAncestor(Tree<V> ancestor) { // including itself
        Tree<V> temp = this;
        while (temp != ancestor) {
            if (temp == null) return false;
            temp = temp.parent;
        }
        return true;
    }
    
// Setters (structure)
    
    /**
     * Adds the given node as the new last child of this node.
     * @param newChild The node to be added.
     */
    public void addChild(Tree<V> newChild) {
        if (this.hasAncestor(newChild)) {
            String message = this + " is already in " + newChild;
            throw new IllegalArgumentException(message);
        }
        int count = children.size();
        children.add(newChild);
        newChild.parent = this;
        newChild.myIndex = count;
    }
    
    /**
     * Adds the given nodes as new children of this node. The nodes
     * are added after any existing nodes.
     * @param newChildren The nodes to be added.
     */
    public void addChildren(ArrayList<Tree<V>> newChildren) {
        for (Iterator<Tree<V>> iter = newChildren.iterator(); iter.hasNext();) {
            addChild(iter.next());            
        }        
    }
    
    /**
     * Removes this node from whatever tree it may be in. The children
     * of this node (if any) stay with this node.
     */
    public void remove() {
        if (parent == null) return;
        int decrement = 0;
        for (Iterator<Tree<V>> iter = parent.children.iterator(); iter.hasNext();) {
            Tree<V> element = iter.next();
            element.myIndex -= decrement;
            if (element == this) {
                iter.remove();
                decrement = 1;
            }
        }
    }
    
// Iterator
    
    /**
     * Returns an iterator that will step through this tree
     * in preorder.
     * 
     * @return A preorder iterator.
     */
    public Iterator<Tree<V>> iterator() {
        return new PreorderIterator(this);
    }
    
    /**
     * Implements a preorder iterator for Trees.
     * @author Dave Matuszek
     * @version Jan 30, 2007
     */
    private class PreorderIterator implements Iterator<Tree<V>> {
        Tree<V> position;
        Tree<V> limit;
        
        PreorderIterator(Tree<V> root) {
            position = limit = root;
        }
        
        /**
         * Returns <code>true</code> if this iterator can produce
         * another value.
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return position != null;
        }
        
        /**
         * Returns the next value found by this iterator.
         * @see java.util.Iterator#next()
         */
        public Tree<V> next() {
            Tree<V> result = position;
            if (!position.isLeaf()) {
                position = position.firstChild();
            } else if (position.hasNextSibling()) {
                position = position.nextSibling();
            } else {
                do {
                    position = position.parent();
                } while (!position.hasNextSibling() && position != limit);
                if (position == limit) {
                    position = null;
                } else {
                    position = position.nextSibling();
                }
            }
            return result;
        }

        /**
         * Unsupported operation.
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();            
        }        
    }
    
// I/O
    
    /**
     * Returns a string representing this tree. The string does
     * not contain newlines. The general form of the output is:<br>
     * <code>value(child, child, ..., child)</code>.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isLeaf()) {
            return value.toString();
        }
        String result = value + "(";
        for (Iterator<Tree<V>> iter = children.iterator(); iter.hasNext();) {
            Tree<V> child = iter.next();
            result += child + (child.hasNextSibling() ? ", " : "");
        }
        return result + ")";
    }
    
    /**
     * Prints this tree as an indented structure.
     */
    public void print() {
        print(this, "");
    }   
    
    /**
     * Prints the given tree as an indented structure, with the
     * given node indented by the given amount.
     * @param node The root of the tree or subtree to be printed.
     * @param indent The amount to indent the root.
     */
    private static void print(Tree<?> node, String indent) {
        if (node == null) return;
        System.out.println(indent + node.value);
        for (Iterator<?> iter = node.children.iterator(); iter.hasNext();) {
            print((Tree)iter.next(), indent + "   ");
        }
    }
    
    /**
     * Parses a string of the general form
     *   <b><code>value(child, child, ..., child)</code></b>
     * and returns the corresponding tree. Children may be separated
     * by commas and/or spaces. Node values are all Strings.
     * @param s The String to be parsed.
     * @return The resultant Tree<String>.
     */
    public static Tree<String> parse(String s) {
        StringTokenizer tokenizer = new StringTokenizer(s, " ,()", true);
        Pair result = null;
        
        if (tokenizer.hasMoreTokens()) {
            result = parse2(s, tokenizer, tokenizer.nextToken());
            if (")".equals(result.token)) {
                throw new RuntimeException("Unbalanced parentheses in \"" + s + "\"\n");
            }
            return result.tree;
//            if (result.tree instanceof Tree) {
//                return result.tree;
//            }
        }
        throw new RuntimeException("Error parsing \"" + s +
                                   "\" at \"" + result + "\"\n");
    }
    
    /**
     * Helper method for the <code>parse(String)</code> method.
     * @param s The String being parsed.
     * @param tokenizer The tokenizer being used.
     * @param token The most recently read token.
     * @return An Object[Tree, next token] pair.
     */
    private static Pair parse2(String s, StringTokenizer tokenizer, String token) {        
        //   <tree> ::= <value> [ "(" { <tree> } ")" ] 
        Tree<String> subtree = null;
     
        // Returns an Object[Tree, next token] pair
        
        // Get root value
        if (token.equals("(") || token.equals(")")) return new Pair(null, token);
        Tree<String> root = new Tree<String>(token);
       
        // check for left paren
        token = nextRealToken(tokenizer);  
        if (!"(".equals(token)) return new Pair(root, token); // end of parse
        token = nextRealToken(tokenizer);        
        
        // saw left paren, get subtrees
        Pair subResult = null;
        while (token != null) {
            if (token.equals(")")) {
                // consume ")" and return
                return new Pair(root, nextRealToken(tokenizer));
            }
            subResult = parse2(s, tokenizer, token);
            subtree = subResult.tree;
            token = subResult.token;
            
            if (subtree == null) return new Pair(root, token);
            else root.addChild(subtree);
        }
        
        // check for final right paren
        if (!")".equals(token)) {
            throw new RuntimeException("Unbalanced parentheses in: \"" + s + "\"\n");
        }
        return new Pair(root, null);
    }
    
    /**
     * A class to treat a pair of objects, one of which is a String
     * and the other a Tree<String>, as a single entity.
     * @author Dave Matuszek
     * @version Jan 30, 2007
     */
    private static class Pair {
        Tree<String> tree;
        String token;
        
        /**
         * Constructs a pair.
         * @param tree One member of the pair.
         * @param token The other member of the pair.
         */
        Pair(Tree<String> tree, String token) {
            this.tree = tree;
            this.token = token;
        }
    }
    
    /**
     * Returns next token produced by the given tokenizer, ignoring
     * blanks and commas.
     * @param tokenizer The tokenizer being used.
     * @return The next nonblank, non-comma token.
     */
    private static String nextRealToken(StringTokenizer tokenizer) {
        final String SPACE = " ";
        final String COMMA = ",";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.equals(SPACE) && !token.equals(COMMA)) return token;
        }
        return null;
    }
        
// Test driver
    
    /**
     * Tests parsing and printing of trees; this is only a test
     * method, not to be used for other purposes.<p>
     * 
     * @param args Unused.
     */
    public static void main(String[] args) {
        String[] tests = new String[] {
                                       "abc",
                                       "a (b c)",
                                       "1 (2 3)",
                                       "a (b c d)",
                                       "a (b c d e f g)",
                                       "a(b, c)",
                                       "a ( b (c d) e f () g(h(i(j))))",
                                       "a(b(c, d), e, f, g(h(i(j))))",
                                       "a (b c",
                                       "a (b c))",
                                       "",
                                       "a (b() c() <= == != >= d)"
                                      };
        for (int i = 0; i < tests.length; i++) {
            try {
                String string = tests[i];
                System.out.println("Testing: " + string);
                Tree<String> tree = parse(string);
                System.out.println("Result:  " + tree);
                System.out.println();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        parse("a(b(d) c(e f))").print();
        
        System.out.println("\nAll tests completed.");
    }
}