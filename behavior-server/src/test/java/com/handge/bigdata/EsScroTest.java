package com.handge.bigdata;

import com.handge.bigdata.base.ComponentBaseHandler;
import com.handge.bigdata.pools.EnvironmentContainer;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by MaJianfu on 2018/5/10.
 */
public class EsScroTest extends ComponentBaseHandler {
    public static void main(String[] args) {
        EnvironmentContainer.setENV();
        new EsScroTest().test();

    }

    public void test() {
        new WrapTaskUseEsTrspCli<Object>() {
            @Override
            public Object call() throws Exception {
                WrapperQueryBuilder query = QueryBuilders.wrapperQuery(String.valueOf("{\"match_all\": {}}"));
                SearchResponse scrollResp = this.transportClient.prepareSearch("dataflow")
                        .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                        .setScroll(new TimeValue(60000))
                        .setQuery(query)
                        .setSize(100).get(); //max of 100 hits will be returned for each scroll
//Scroll until no hits are returned
                do {
                    String id = scrollResp.getScrollId();
                    for (SearchHit hit : scrollResp.getHits().getHits()) {
                        //Handle the hit...
                    }
                    System.out.println(id);
                    scrollResp = this.transportClient.prepareSearchScroll(id).setScroll(new TimeValue(60000)).execute().actionGet();
                } while (scrollResp.getHits().getHits().length != 0);

                return null;
            }

        }.run();
    }
}
