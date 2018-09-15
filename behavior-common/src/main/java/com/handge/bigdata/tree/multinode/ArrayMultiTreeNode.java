/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : ArrayMultiTreeNode
 * User : XueFei Wang
 * Date : 6/27/18 2:22 PM
 * Modified :7/31/17 6:19 PM
 * Todo :
 *
 */

package com.handge.bigdata.tree.multinode;

import com.handge.bigdata.tree.TreeNode;
import com.handge.bigdata.tree.TraversalAction;
import com.handge.bigdata.tree.TreeNodeException;

import java.util.*;

public class ArrayMultiTreeNode<T> extends MultiTreeNode<T> {


	private static final long serialVersionUID = 1L;


	private static final int DEFAULT_BRANCHING_FACTOR = 10;


	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


	private Object[] subtrees;

	private Set subtreesDatas;


	private int subtreesSize;


	private final int branchingFactor;


	public ArrayMultiTreeNode(T data) {
		super(data);
		this.branchingFactor = DEFAULT_BRANCHING_FACTOR;
		this.subtrees = new Object[branchingFactor];
		this.subtreesDatas = new HashSet();
	}


	public ArrayMultiTreeNode(T data, int branchingFactor) {
		super(data);
		if (branchingFactor < 0) {
			throw new IllegalArgumentException("Branching factor can not be negative");
		}
		this.branchingFactor = branchingFactor;
		this.subtrees = new Object[branchingFactor];
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends TreeNode<T>> subtrees() {
		if (isLeaf()) {
			return Collections.emptySet();
		}
		Collection<TreeNode<T>> subtrees = new LinkedHashSet<>(subtreesSize);
		for (int i = 0; i < subtreesSize; i++) {
			TreeNode<T> subtree = (TreeNode<T>) this.subtrees[i];
			subtrees.add(subtree);
		}
		return subtrees;
	}


	@Override
	public boolean add(TreeNode<T> subtree) {
		if (subtree == null) {
			return false;
		}
		linkParent(subtree, this);
		ensureSubtreesCapacity(subtreesSize + 1);
		if (subtreesDatas.contains(subtree.data())){
			return false;
		}
		subtreesDatas.add(subtree.data());
		subtrees[subtreesSize++] = subtree;
		return true;
	}


	private void ensureSubtreesCapacity(int minSubtreesCapacity) {
		if (minSubtreesCapacity > subtrees.length) {
			increaseSubtreesCapacity(minSubtreesCapacity);
		}
	}


	private void increaseSubtreesCapacity(int minSubtreesCapacity) {
		int oldSubtreesCapacity = subtrees.length;
		int newSubtreesCapacity = oldSubtreesCapacity + (oldSubtreesCapacity >> 1);
		if (newSubtreesCapacity < minSubtreesCapacity) {
			newSubtreesCapacity = minSubtreesCapacity;
		}
		if (newSubtreesCapacity > MAX_ARRAY_SIZE) {
			if (minSubtreesCapacity < 0) {
				throw new OutOfMemoryError();
			}
			newSubtreesCapacity = minSubtreesCapacity > MAX_ARRAY_SIZE ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
		}
		subtrees = Arrays.copyOf(subtrees, newSubtreesCapacity);
	}


	@Override
	public boolean dropSubtree(TreeNode<T> subtree) {
		if (subtree == null
				|| isLeaf()
				|| subtree.isRoot()) {
			return false;
		}
		int mSubtreeIndex = indexOf(subtree);
		if (mSubtreeIndex < 0) {
			return false;
		}
		int mNumShift = subtreesSize - mSubtreeIndex - 1;
		if (mNumShift > 0) {
			System.arraycopy(subtrees, mSubtreeIndex + 1, subtrees, mSubtreeIndex, mNumShift);
		}
		subtrees[--subtreesSize] = null;
		unlinkParent(subtree);
		return true;
	}


	@SuppressWarnings("unchecked")
	private int indexOf(TreeNode<T> subtree) {
		for (int i = 0; i < subtreesSize; i++) {
			TreeNode<T> mSubtree = (TreeNode<T>) subtrees[i];
			if (mSubtree.equals(subtree)) {
				return i;
			}
		}
		return -1;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		if (!isLeaf()) {
			for (int i = 0; i < subtreesSize; i++) {
				TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
				unlinkParent(subtree);
			}
			subtrees = new Object[branchingFactor];
			subtreesSize = 0;
		}
	}


	@Override
	public TreeNodeIterator iterator() {
		return new TreeNodeIterator() {

			/**
			 * Returns the leftmost node of the current tree node if the
			 * current tree node is not a leaf
			 *
			 * @return leftmost node of the current tree node if the current
			 *         tree node is not a leaf
			 * @throws TreeNodeException an exception that is thrown in case
			 *                           if the current tree node is a leaf
			 */
			@SuppressWarnings("unchecked")
			@Override
			protected TreeNode<T> leftMostNode() {
				return (TreeNode<T>) subtrees[0];
			}

			/**
			 * Returns the right sibling node of the current tree node if the
			 * current tree node is not root
			 *
			 * @return right sibling node of the current tree node if the current
			 *         tree node is not root
			 * @throws TreeNodeException an exception that may be thrown in case if
			 *                           the current tree node is root
			 */
			@Override
			@SuppressWarnings("unchecked")
			protected TreeNode<T> rightSiblingNode() {
				ArrayMultiTreeNode<T> mParent = (ArrayMultiTreeNode<T>) parent;
				int rightSiblingNodeIndex = mParent.indexOf(ArrayMultiTreeNode.this) + 1;
				return rightSiblingNodeIndex < mParent.subtreesSize ?
						(TreeNode<T>) mParent.subtrees[rightSiblingNodeIndex] : null;
			}
		};
	}


	@Override
	public boolean isLeaf() {
		return subtreesSize == 0;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean hasSubtree(TreeNode<T> subtree) {
		if (subtree == null
				|| isLeaf()
				|| subtree.isRoot()) {
			return false;
		}
		for (int i = 0; i < subtreesSize; i++) {
			TreeNode<T> mSubtree = (TreeNode<T>) subtrees[i];
			if (subtree.equals(mSubtree)) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean containsWithSubLevel(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		for (int i = 0; i < subtreesSize; i++) {
			TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
			if (subtree.equals(node)) {
				return true;
			}
			if (subtree.containsWithSubLevel(node)) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(TreeNode<T> node) {
		if (node == null
				|| isLeaf()
				|| node.isRoot()) {
			return false;
		}
		if (dropSubtree(node)) {
			return true;
		}
		for (int i = 0; i < subtreesSize; i++) {
			TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
			if (subtree.remove(node)) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void traversePreOrder(TraversalAction<TreeNode<T>> action) {
		if (!action.isCompleted()) {
			action.perform(this);
			if (!isLeaf()) {
				for (int i = 0; i < subtreesSize; i++) {
					TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
					subtree.traversePreOrder(action);
				}
			}
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void traversePostOrder(TraversalAction<TreeNode<T>> action) {
		if (!action.isCompleted()) {
			if (!isLeaf()) {
				for (int i = 0; i < subtreesSize; i++) {
					TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
					subtree.traversePostOrder(action);
				}
			}
			action.perform(this);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public int height() {
		if (isLeaf()) {
			return 0;
		}
		int height = 0;
		for (int i = 0; i < subtreesSize; i++) {
			TreeNode<T> subtree = (TreeNode<T>) subtrees[i];
			height = Math.max(height, subtree.height());
		}
		return height + 1;
	}


	@Override
	public boolean addSubtrees(Collection<? extends MultiTreeNode<T>> subtrees) {
		if (areAllNulls(subtrees)) {
			return false;
		}
		for (MultiTreeNode<T> subtree : subtrees) {
			linkParent(subtree, this);
		}
		Object[] subtreesArray = subtrees.toArray();
		int subtreesArrayLength = subtreesArray.length;
		ensureSubtreesCapacity(subtreesSize + subtreesArrayLength);
		System.arraycopy(subtreesArray, 0, this.subtrees, subtreesSize, subtreesArrayLength);
		subtreesSize += subtreesArrayLength;
		return subtreesArrayLength != 0;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends MultiTreeNode<T>> siblings() {
		if (isRoot()) {
			String message = String.format("Unable to find the siblings. The tree node %1$s is root", root());
			throw new TreeNodeException(message);
		}
		ArrayMultiTreeNode<T> mParent = (ArrayMultiTreeNode<T>) parent;
		int parentSubtreesSize = mParent.subtreesSize;
		if (parentSubtreesSize == 1) {
			return Collections.emptySet();
		}
		Object[] parentSubtreeObjects = mParent.subtrees;
		Collection<MultiTreeNode<T>> siblings = new LinkedHashSet<>(parentSubtreesSize - 1);
		for (int i = 0; i < parentSubtreesSize; i++) {
			MultiTreeNode<T> parentSubtree = (MultiTreeNode<T>) parentSubtreeObjects[i];
			if (!parentSubtree.equals(this)) {
				siblings.add(parentSubtree);
			}
		}
		return siblings;
	}

}
