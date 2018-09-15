/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : TraversalAction
 * User : XueFei Wang
 * Date : 6/27/18 2:22 PM
 * Modified :7/31/17 6:19 PM
 * Todo :
 *
 */

package com.handge.bigdata.tree;


public interface TraversalAction<T extends TreeNode> {


	void perform(T node);


	boolean isCompleted();

}
