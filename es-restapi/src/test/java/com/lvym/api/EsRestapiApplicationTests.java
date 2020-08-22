package com.lvym.api;

import com.lvym.api.entity.Goods;
import com.lvym.api.entity.SearchAttr;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@SpringBootTest
class EsRestapiApplicationTests {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {

     elasticsearchRestTemplate.createIndex(Goods.class);

        SearchAttr searchAttr = new SearchAttr();
        searchAttr.setAttrId(1l);
        searchAttr.setAttrName("j快乐暑假打工");
        searchAttr.setAttrValue("jkbhkjgkjhkj");
        Goods goods = new Goods();
        goods.setAttrs(Arrays.asList(searchAttr));
        goods.setBrandId(1L);
          goods.setBrandName("apple");
            goods.setCategoryId(1L);
            goods.setCategoryName("手机");
            goods.setCreateTime(new Date());
            goods.setPic("jhfdjkdhg");
            goods.setPrice(36.5);
            goods.setSale(155l);
            goods.setSkuId(1l);
            goods.setStore(true);
            goods.setTitle("sdklhgdejflgn");


    }


    @Test
    void contextLoads02() throws IOException {

        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title","幂"));
        boolQueryBuilder.filter(QueryBuilders.termQuery("brandId",1));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gt(3).lt(100));
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(search.toString());



    }
}
