package com.handge.bigdata.pools.es;


import com.handge.bigdata.pools.common.AbstractPool;
import com.handge.bigdata.pools.common.PoolConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;


/**
 * Created by cloud computing on 2016/9/21 0021.
 */
public class EsConnectionPool extends AbstractPool<TransportClient> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9126420905798370263L;


    public EsConnectionPool(final PoolConfig poolConfig, final Settings settings, Collection<InetSocketTransportAddress> transportAddress, Class<? extends Plugin>... plugins) {

        super(poolConfig, new EsConnectionFactory(settings, transportAddress, plugins));

    }


    @Override
    public TransportClient getConnection() {

        return super.getResource();
    }

    @Override
    public void returnConnection(TransportClient client) {

        super.returnResource(client);
    }

    @Override
    public void invalidateConnection(TransportClient client) {

        super.invalidateResource(client);
    }

}