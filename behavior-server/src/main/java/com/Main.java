package com;

import com.handge.bigdata.Application;
import com.handge.bigdata.pools.EnvironmentContainer;

public class Main {
    public static void main(String[] args) throws Exception {

        EnvironmentContainer.setENV();
        Application.main(args);
    }
}