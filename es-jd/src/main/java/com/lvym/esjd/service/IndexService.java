package com.lvym.esjd.service;


import com.alibaba.fastjson.JSON;
import com.lvym.esjd.entity.Content;
import com.lvym.esjd.utils.JsoupData;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Boolean parsejd(String keyword) throws IOException {


        List<Content> contents = new JsoupData().parseJD(keyword);

        BulkRequest bulkRequest = new BulkRequest();

        for (int i=0;i<contents.size();i++){
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();

    }

    /**
     * 未高亮
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
   /* public List<Map<String,Object>> searchindex(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=0){
            pageNo=1;
        }
          //创建SearchRequest
        SearchRequest searchRequest = new SearchRequest("jd_goods");
       //创建SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //精确查找
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", keyword);
              //
        searchSourceBuilder.query(termQuery);
        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //将searchSourceBuilder放入searchRequest
        searchRequest.source(searchSourceBuilder);
          //调用查找请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
       //封装结果
        ArrayList<Map<String,Object>> maps = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            maps.add(sourceAsMap);
        }
        return maps;
    }*/

    /**
     * 高亮
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String,Object>> searchindexhighlightBuilder(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=0){
            pageNo=1;
        }
        //创建SearchRequest
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        //创建SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //精确查找
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", keyword);
        //
        searchSourceBuilder.query(termQuery);
    //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //将searchSourceBuilder放入searchRequest
        searchRequest.source(searchSourceBuilder);
        //调用查找请求
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //封装结果
        ArrayList<Map<String,Object>> maps = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField name = highlightFields.get("name");//整个name[[name]]

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (name!=null){

                Text[] texts = name.fragments();//[name]

                String n_name="";
                for (Text text : texts) {

                    n_name+=text;
                    sourceAsMap.put("name",n_name);

                }
            }
           maps.add(sourceAsMap);

        }
        return maps;
    }


}