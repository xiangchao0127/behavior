package com.handge.bigdata.pools.es;


import com.handge.bigdata.pools.common.ConnectionPool;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by cloud computing on 2016/9/21 0021.
 */
public class EsSharedConnPool implements ConnectionPool<TransportClient> {

    private static final AtomicReference<EsSharedConnPool> POOL = new AtomicReference<EsSharedConnPool>();

    private final TransportClient client;

    private EsSharedConnPool(Collection<InetSocketTransportAddress> transportAddress, Settings settings, Class<? extends Plugin>[] plugins) {

        this.client = getTransportClient(transportAddress, settings, plugins);

    }


    public synchronized static EsSharedConnPool getInstance(Collection<InetSocketTransportAddress> transportAddress, Settings settings, Class<? extends Plugin>[] plugins) {
        if (POOL.get() == null)

        {
            POOL.set(new EsSharedConnPool(transportAddress, settings, plugins));
        }

        return POOL.get();
    }

    private TransportClient getTransportClient(Collection<InetSocketTransportAddress> transportAddress, Settings settings, Class<? extends Plugin>[] plugins) {

        TransportClient client = new PreBuiltTransportClient(settings, plugins);

        for (InetSocketTransportAddress TA : transportAddress) {

            client.addTransportAddress(TA);
        }

        return client;

    }

    @Override
    public TransportClient getConnection() {
        return client;
    }

    @Override
    public void returnConnection(TransportClient client) {


    }

    @Override
    public void invalidateConnection(TransportClient client) {
        try {
            if (client != null)

            {
                client.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
