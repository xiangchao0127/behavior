/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-analyser
 * Class : BehaviorDataSetIterator
 * User : XueFei Wang
 * Date : 6/6/18 3:42 PM
 * Modified :6/6/18 3:42 PM
 * Todo :
 *
 */

package com.handge.bigdata.datastore;


import org.nd4j.linalg.dataset.api.iterator.BaseDatasetIterator;

public class BehaviorDataSetIterator extends BaseDatasetIterator {

    public BehaviorDataSetIterator(int batch, int numExamples, BehaviorDataFetcher fetcher) {
        super(batch, numExamples, fetcher);
    }
}
