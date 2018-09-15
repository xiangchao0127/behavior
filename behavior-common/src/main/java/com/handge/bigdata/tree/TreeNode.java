/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : TreeNode
 * User : XueFei Wang
 * Date : 6/27/18 2:22 PM
 * Modified :7/31/17 6:19 PM
 * Todo :
 *
 */

package com.handge.bigdata.tree;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>Terminology</h1>
 * <ul>
 *     <li><b>Node</b> - a single point of a tree</li>
 *     <li><b>Edge</b> - line, which connects two distinct nodes</li>
 *     <li><b>Root</b> - top node of the tree, which has no parent</li>
 *     <li><b>Parent</b> - a node, other than the root, which is connected to other successor
 *                         nodes</li>
 *     <li><b>Child</b> - a node, other than the root, which is connected to predecessor</li>
 *     <li><b>Leaf</b> - a node without children</li>
 *     <li><b>Path</b> - a sequence of nodes and edges connecting a node with a
 *                       descendant</li>
 *     <li><b>Path Length</b> - number of nodes in the path - 1</li>
 *     <li><b>Ancestor</b> - the top parent node of the path</li>
 *     <li><b>Descendant</b> - the bottom child node of the path</li>
 *     <li><b>Siblings</b> - nodes, which have the same parent</li>
 *     <li><b>Subtree</b> - a node in a tree with all of its proper descendants, if any</li>
 *     <li><b>Node Height</b> - the number of edges on the longest downward path between that
 *                              node and a leaf</li>
 *     <li><b>Tree Height</b> - the number of edges on the longest downward path between the
 *                              root and a leaf (root height)</li>
 *     <li><b>Depth (Level)</b> - the path length between the root and the current node</li>
 *     <li><b>Ordered Tree</b> - tree in which nodes has the children ordered</li>
 *     <li><b>Labeled Tree</b> - tree in which a label or value is associated with each node
 *                               of the tree</li>
 *     <li><b>Expression Tree</b> - tree which specifies the association of an expression�s
 *                                  operands and its operators in a uniform way, regardless
 *                                  of whether the association is required by the placement
 *                                  of parentheses in the expression or by the precedence and
 *                                  associativity rules for the operators involved</li>
 *     <li><b>Branching Factor</b> - maximum number of children a node can have</li>
 *     <li><b>Pre order</b> - a form of tree traversal, where the action is called firstly on
 *                           the current node, and then the pre order function is called again
 *                           recursively on each of the subtree from left to right</li>
 *     <li><b>Post order</b> - a form of tree traversal, where the post order function is called
 *                            recursively on each subtree from left to right and then the
 *                            action is called</li>
 * </ul>
 *
 */
public abstract class TreeNode<T> implements Iterable<TreeNode<T>>, Serializable, Cloneable {


	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);


	private final long id = ID_GENERATOR.getAndIncrement();


	protected TreeNode<T> parent;


	protected T data;


	public TreeNode(T data) {
		this.data = data;
	}


	public TreeNode() {
	}


	public abstract Collection<? extends TreeNode<T>> subtrees();


	public abstract boolean add(TreeNode<T> subtree);



	public abstract TreeNode<T> addAndGet(TreeNode<T> subtree);



	public abstract boolean dropSubtree(TreeNode<T> subtree);


	public abstract void clear();


	public abstract TreeNodeIterator iterator();


	public T data() {
		return data;
	}

	public Long id(){
		return id;
	}


	public void setData(T data) {
		this.data = data;
	}


	public boolean isRoot() {
		return parent == null;
	}


	public TreeNode<T> root() {
		if (isRoot()) {
			return this;
		}
		TreeNode<T> node = this;
		do {
			node = node.parent();
		} while (!node.isRoot());
		return node;
	}


	public TreeNode<T> parent() {
		return parent;
	}


	public boolean isLeaf() {
		return subtrees().isEmpty();
	}


	public TreeNode<T> find(final T data) {
		if (isLeaf()) {
			return (data() == null ? data == null : data().equals(data)) ? this : null;
		}
		final TreeNode<T>[] searchedNode = (TreeNode<T>[]) Array.newInstance(getClass(), 1);
		traversePreOrder(new TraversalAction<TreeNode<T>>() {
			@Override
			public void perform(TreeNode<T> node) {
				if ((node.data() == null ?
						data == null : node.data().equals(data))) {
					searchedNode[0] = node;
				}
			}

			@Override
			public boolean isCompleted() {
				return searchedNode[0] != null;
			}
		});
		return searchedNode[0];
	}


	public TreeNode<T> findInNextLevel(final T data) {
		if (isLeaf()) {
			return (data() == null ? data == null : data().equals(data)) ? this : null;
		}

		for (TreeNode<T> subtree : subtrees()){
			T d = subtree.data();
			if (d.equals(data)){
				return  subtree;
			}
		}
		return null;
	}





	public boolean hasSubtree(TreeNode<T> subtree) {
		if (subtree == null
				|| isLeaf()
				|| subtree.isRoot()) {
			return false;
		}
		for (TreeNode<T> mSubtree : subtrees()) {
			if (mSubtree.equals(subtree)) {
				return true;
			}
		}
		return false;
	}


	public boolean containsWithSubLevel(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		for (TreeNode<T> subtree : subtrees()) {
			if (subtree.equals(node)
					|| subtree.containsWithSubLevel(node)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsWithNextLevel(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		for (TreeNode<T> subtree : subtrees()) {
			if (subtree.equals(node)) {
				return true;
			}
		}
		return false;
	}


	public boolean containsValueWithNextLevel(TreeNode<T> node){
		for (TreeNode<T> subtree : subtrees()) {
			if (subtree.data().equals(node.data())) {
				return true;
			}
		}
		return false;
	}

	public boolean containsValueWithSubLevel(TreeNode<T> node){
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		for (TreeNode<T> subtree : subtrees()) {
			if (subtree.data().equals(node.data()) || subtree.containsValueWithSubLevel(node)) {
				return true;
			}
		}
		return false;
	}





	public boolean remove(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		if (dropSubtree(node)) {
			return true;
		}
		for (TreeNode<T> subtree : subtrees()) {
			if (subtree.remove(node)) {
				return true;
			}
		}
		return false;
	}


	public boolean removeAll(Collection<TreeNode<T>> nodes) {
		if (isLeaf()
				|| areAllNulls(nodes)) {
			return false;
		}
		boolean result = false;
		for (TreeNode<T> node : nodes) {
			boolean currentResult = remove(node);
			if (!result && currentResult) {
				result = true;
			}
		}
		return result;
	}


	public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
		if (!action.isCompleted()) {
			action.perform(this);
			if (!isLeaf()) {
				for (TreeNode<T> subtree : subtrees()) {
					subtree.traversePreOrder(action);
				}
			}
		}
	}


	public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
		if (!action.isCompleted()) {
			if (!isLeaf()) {
				for (TreeNode<T> subtree : subtrees()) {
					subtree.traversePostOrder(action);
				}
			}
			action.perform(this);
		}
	}


	public Collection<TreeNode<T>> preOrdered() {
		if (isLeaf()) {
			return Collections.singleton(this);
		}
		final Collection<TreeNode<T>> mPreOrdered = new ArrayList<>();
		TraversalAction<TreeNode<T>> action = populateAction(mPreOrdered);
		traversePreOrder(action);
		return mPreOrdered;
	}


	public Collection<TreeNode<T>> postOrdered() {
		if (isLeaf()) {
			return Collections.singleton(this);
		}
		final Collection<TreeNode<T>> mPostOrdered = new ArrayList<>();
		TraversalAction<TreeNode<T>> action = populateAction(mPostOrdered);
		traversePostOrder(action);
		return mPostOrdered;
	}


	public Collection<? extends TreeNode<T>> path(TreeNode<T> descendant) {
		if (descendant == null
				|| isLeaf()
				|| this.equals(descendant)) {
			return Collections.singletonList(this);
		}
		String errorMessage = "Unable to build the path between tree nodes. ";
		if (descendant.isRoot()) {
			String message = String.format(errorMessage + "Current node %1$s is root", descendant);
			throw new TreeNodeException(message);
		}
		List<TreeNode<T>> path = new LinkedList<>();
		TreeNode<T> node = descendant;
		path.add(node);
		do {
			node = node.parent();
			path.add(0, node);
			if (this.equals(node)) {
				return path;
			}
		} while (!node.isRoot());
		String message = String.format(errorMessage +
				"The specified tree node %1$s is not the descendant of tree node %2$s", descendant, this);
		throw new TreeNodeException(message);
	}

	/**
	 * Returns the common ancestor of the current node and the node specified
	 *
	 * @param node node, which the common ancestor is determined for,
	 *             along with the current node
	 * @return common ancestor of the current node and the node specified
	 * @throws TreeNodeException exception that may be thrown in case if the
	 *                          specified tree node is null or the specified tree node
	 *                          does not belong to the current tree or if any of the tree
	 *                          nodes either the current one or the specified one is root
	 */
	public TreeNode<T> commonAncestor(TreeNode<T> node) {
		String errorMessage = "Unable to find the common ancestor between tree nodes. ";
		if (node == null) {
			String message = errorMessage + "The specified tree node is null";
			throw new TreeNodeException(message);
		}
		if (!this.root().containsWithSubLevel(node)) {
			String message = String.format(errorMessage +
					"The specified tree node %1$s was not found in the current tree node %2$s", node, this);
			throw new TreeNodeException(message);
		}
		if (this.isRoot()
				|| node.isRoot()) {
			String message = String.format(errorMessage + "The tree node %1$s is root", this.isRoot() ? this : node);
			throw new TreeNodeException(message);
		}
		if (this.equals(node)
				|| node.isSiblingOf(this)) {
			return parent();
		}
		int thisNodeLevel = this.level();
		int thatNodeLevel = node.level();
		return thisNodeLevel > thatNodeLevel ? node.parent() : this.parent();
	}




	public boolean isSiblingOf(TreeNode<T> node) {
		return node != null
				&& !isRoot()
				&& !node.isRoot()
				&& this.parent().equals(node.parent());
	}


	public boolean isAncestorOf(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()
				|| this.equals(node)) {
			return false;
		}
		TreeNode<T> mNode = node;
		do {
			mNode = mNode.parent();
			if (this.equals(mNode)) {
				return true;
			}
		} while (!mNode.isRoot());
		return false;
	}


	public boolean isDescendantOf(TreeNode<T> node) {
		if (node == null
				|| this.isRoot()
				|| node.isLeaf()
				|| this.equals(node)) {
			return false;
		}
		TreeNode<T> mNode = this;
		do {
			mNode = mNode.parent();
			if (node.equals(mNode)) {
				return true;
			}
		} while (!mNode.isRoot());
		return false;
	}


	public long size() {
		if (isLeaf()) {
			return 1;
		}
		final long[] count = {0};
		TraversalAction<TreeNode<T>> action = new TraversalAction<TreeNode<T>>() {
			@Override
			public void perform(TreeNode<T> node) {
				count[0]++;
			}

			@Override
			public boolean isCompleted() {
				return false;
			}
		};
		traversePreOrder(action);
		return count[0];
	}


	public int height() {
		if (isLeaf()) {
			return 0;
		}
		int height = 0;
		for (TreeNode<T> subtree : subtrees()) {
			height = Math.max(height, subtree.height());
		}
		return height + 1;
	}


	public int level() {
		if (isRoot()) {
			return 0;
		}
		int level = 0;
		TreeNode<T> node = this;
		do {
			node = node.parent();
			level++;
		} while (!node.isRoot());
		return level;
	}


	@SuppressWarnings("unchecked")
	@Override
	public TreeNode<T> clone() {
		try {
			return (TreeNode<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			String message = "Unable to clone the current tree node";
			throw new TreeNodeException(message, e);
		}
	}

	/**
	 * Indicates whether some object equals to this one
	 *
	 * @param obj the reference object with which to compare
	 * @return {@code true} if this object is the same as the obj
	 *         argument; {@code false} otherwise
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {

		if (obj == null
				|| getClass() != obj.getClass()) {
			return false;
		}
		TreeNode<T> that = (TreeNode<T>) obj;
		if (this.data.equals(that.data)){
			return true;
		}
		return this.id == that.id;
	}


	public boolean valueEquals(TreeNode<T> that){
		return this.data.equals(that.data);
	}




	@Override
	public int hashCode() {
		return (int) (this.id ^ (this.id >>> 32));
	}


	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("\n");
		final int topNodeLevel = level();
		TraversalAction<TreeNode<T>> action = new TraversalAction<TreeNode<T>>() {
			@Override
			public void perform(TreeNode<T> node) {
				int nodeLevel = node.level() - topNodeLevel;
				for (int i = 0; i < nodeLevel; i++) {
					builder.append("|  ");
				}
				builder
						.append("+- ")
						.append(node.data())
						.append("\n");
			}

			@Override
			public boolean isCompleted() {
				return false;
			}
		};
		traversePreOrder(action);
		return builder.toString();
	}


	protected static <T> TraversalAction<TreeNode<T>> populateAction(final Collection<TreeNode<T>> collection) {
		return new TraversalAction<TreeNode<T>>() {
			@Override
			public void perform(TreeNode<T> node) {
				collection.add(node);
			}

			@Override
			public boolean isCompleted() {
				return false;
			}
		};
	}


	protected static <T> void linkParent(TreeNode<T> node, TreeNode<T> parent) {
		if (node != null) {
			node.parent = parent;
		}
	}


	protected static <T> void unlinkParent(TreeNode<T> node) {
		node.parent = null;
	}


	protected static <T> boolean isAnyNotNull(Collection<T> collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		for (T item : collection) {
			if (item != null) {
				return true;
			}
		}
		return false;
	}


	protected static <T> boolean areAllNulls(Collection<T> collection) {
		return !isAnyNotNull(collection);
	}


	protected abstract class TreeNodeIterator implements Iterator<TreeNode<T>> {


		private long expectedSize = size();


		private TreeNode<T> currentNode;


		private TreeNode<T> nextNode = TreeNode.this;


		private boolean nextNodeAvailable = true;


		protected abstract TreeNode<T> leftMostNode();


		protected abstract TreeNode<T> rightSiblingNode();


		private TreeNode<T> checkAndGetLeftMostNode() {
			if (isLeaf()) {
				throw new TreeNodeException("Leftmost node can't be obtained. Current tree node is a leaf");
			} else {
				return leftMostNode();
			}
		}

		private TreeNode<T> checkAndGetRightSiblingNode() {
			if (isRoot()) {
				throw new TreeNodeException("Right sibling node can't be obtained. Current tree node is root");
			} else {
				return rightSiblingNode();
			}
		}


		@Override
		public boolean hasNext() {
			return nextNodeAvailable;
		}


		@Override
		public TreeNode<T> next() {
			checkForConcurrentModification();
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			currentNode = nextNode;
			if (nextNode.isLeaf()) {
				if (nextNode.isRoot()) {
					nextNodeAvailable = false;
				} else {
					do {
						TreeNode<T> currentNode = nextNode;
						nextNode = nextNode.parent();
						if (currentNode.equals(TreeNode.this)) {
							nextNodeAvailable = false;
							break;
						}
						TreeNode<T> nextSibling = currentNode.iterator().checkAndGetRightSiblingNode();
						if (nextSibling != null) {
							nextNode = nextSibling;
							break;
						}
					} while (true);
				}
			} else {
				nextNode = nextNode.iterator().checkAndGetLeftMostNode();
			}
			return currentNode;
		}


		private void checkForConcurrentModification() {
			if (expectedSize != size()) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void remove() {
			String errorMessage = "Failed to remove the tree node. ";
			if (!isIterationStarted()) {
				throw new IllegalStateException(errorMessage + "The iteration has not been performed yet");
			}
			if (currentNode.isRoot()) {
				String message = String.format(errorMessage + "The tree node %1$s is root", currentNode);
				throw new TreeNodeException(message);
			}
			if (currentNode.equals(TreeNode.this)) {
				throw new TreeNodeException(errorMessage + "The starting node can't be removed");
			}
			checkForConcurrentModification();
			TreeNode<T> currentNode = this.currentNode;
			while (true) {
				if (currentNode.isRoot()) {
					nextNodeAvailable = false;
					break;
				}
				TreeNode<T> rightSiblingNode = currentNode.iterator().checkAndGetRightSiblingNode();
				if (rightSiblingNode != null) {
					nextNode = rightSiblingNode;
					break;
				}
				currentNode = currentNode.parent;
			}
			TreeNode<T> parent = this.currentNode.parent();
			parent.dropSubtree(this.currentNode);
			this.currentNode = parent;
			expectedSize = size();
		}


		private boolean isIterationStarted() {
			return currentNode != null;
		}

	}

}
