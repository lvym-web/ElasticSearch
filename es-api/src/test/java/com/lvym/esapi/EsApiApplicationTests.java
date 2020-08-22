package com.lvym.esapi;

import com.alibaba.fastjson.JSON;
import com.lvym.esapi.entity.User;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 创建索引
     * @throws IOException
     */
    @Test
    void createIndex() throws IOException {

        CreateIndexRequest test4 = new CreateIndexRequest("lvym");
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(test4, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);

    }

    /**
     *   获取索引
     * @throws IOException
     */
    @Test
    void getIndex() throws IOException {
        GetIndexRequest test4 = new GetIndexRequest("test4");
        boolean exists = restHighLevelClient.indices().exists(test4, RequestOptions.DEFAULT);
        System.out.println("判断索引是否存在:"+exists);
    }

    /**
     *   删除索引
     * @throws IOException
     */
    @Test
    void delIndex() throws IOException {
        DeleteIndexRequest test4 = new DeleteIndexRequest("test4");
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(test4, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    /**
     * 添加文档
     * @throws IOException
     */
    @Test
    void addDocument() throws IOException {

       //创建对象
        User user = new User("卢浮宫",10000);
        //创建请求
        IndexRequest request = new IndexRequest("lvym");
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));

        //将数据放入请求
        request.source(JSON.toJSON(user), XContentType.JSON);
        //发送请求
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(index.toString());
        System.out.println(index.status());

    }

    /**
     * 判断文档是否存在
     * @throws IOException
     */
    @Test
    void existsDocument() throws IOException {

        GetRequest getRequest = new GetRequest("lvym","1");
        //不返回获取的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
       // getRequest.storedFields("_none_");

        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     *   获取文档
     * @throws IOException
     */
    @Test
    void getDocument() throws IOException {

        GetRequest getRequest = new GetRequest("lvym","1");
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSource());

    }

    /**
     * 更新文档
     * @throws IOException
     */
    @Test
    void updateDocument() throws IOException {

        UpdateRequest updateRequest = new UpdateRequest("lvym", "1");

        User user = new User("gfdg", 10000);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(update.status());

    }

    /**
     * 删除文档
      * @throws IOException
     */
    @Test
    void delDocument() throws IOException {

        DeleteRequest deleteRequest = new DeleteRequest("lvym", "1");
        deleteRequest.timeout("1s");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete);

    }

    /**
     *   批量插入
     * @throws IOException
     */
    @Test
    void bulkDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        ArrayList<User> objects = new ArrayList<>();
        objects.add(new User("幂",12));
        objects.add(new User("幂2",13));
        objects.add(new User("幂3",14));
        objects.add(new User("幂4",15));
        for (int i=0;i<objects.size();i++){
            bulkRequest.add(new IndexRequest("lvym").id(""+(i+1)).source(JSON.toJSONString(objects.get(i)),XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.status());

    }

    /**
     * 批量删除
     * @throws IOException
     */
    @Test
    void bulkDocument2() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        bulkRequest.add(new DeleteRequest("lvym", "1"));
        bulkRequest.add(new DeleteRequest("lvym", "2"));
        bulkRequest.add(new DeleteRequest("lvym", "3"));
        BulkResponse delete = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
    }

    /**
     *   SearchRequest 搜索请求
     *   SearchSourceBuilder 条件构造
     *  QueryBuilders 可以点出
     *       QueryBuilders.matchAllQueryBuilder  全匹配
     *       QueryBuilders.termQueryBuilder    精确查找
     *       QueryBuilders.boolQueryBuilder    条件查找
     *    searchSourceBuilder.HighlightBuilder   高亮
     * @throws IOException
     */
    @Test
    void queryDocument() throws IOException {

        SearchRequest searchRequest = new SearchRequest("lvym");

        SearchSourceBuilder builder = new SearchSourceBuilder();



//  分页
        //        builder.from();
//        builder.size();

        builder.query();


        searchRequest.source(builder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : search.getHits().getHits()) {
            System.out.println(hit.getSourceAsString());
        }



    }



}
