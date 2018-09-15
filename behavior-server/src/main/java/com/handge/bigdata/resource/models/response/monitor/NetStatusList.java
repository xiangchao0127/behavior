package com.handge.bigdata.resource.models.response.monitor;

import java.util.ArrayList;

public class NetStatusList {
    private ArrayList<NetStatus> netStatuses;

    public ArrayList<NetStatus> getNetStatuses() {
        return netStatuses;
    }

    public void setNetStatuses(ArrayList<NetStatus> netStatuses) {
        this.netStatuses = netStatuses;
    }

    @Override
    public String toString() {
        return "NetStatusList{" +
                "netStatuses=" + netStatuses +
                '}';
    }
}
