//package my.util;
//
//import com.alibaba.fastjson.JSON;
//import org.apache.http.HttpHost;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.delete.DeleteRequest;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.action.update.UpdateRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class ESUtil {
//
//    private static RestHighLevelClient getESClient(String esHostName, Integer esPort, String esScheme){
//
//        return new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost(esHostName, esPort, esScheme)));
//    }
//
//    private static RestHighLevelClient client = getESClient("127.0.0.1", 9500, "http");
//
//    private static <T>List<T> getQueryResultList(String index, BoolQueryBuilder boolQueryBuilder, Class<T> clazz) throws IOException {
//
//        SearchRequest searchRequest = new SearchRequest(index);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQueryBuilder);
//        searchRequest.source(searchSourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//        List<T> queryResultList = new ArrayList();
//        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
//            queryResultList.add(JSON.parseObject(documentFields.getSourceAsString(), clazz));
//        }
//
//        return queryResultList;
//    }
//
//    /**
//     *
//     * 多条件查询:交集
//     * @param index
//     * @param queryMap
//     * @param clazz
//     * @throws IOException
//     */
//    public static <T>List<T> queryByFieldAndMatch(String index, Map<String, Object> queryMap, Class<T> clazz) throws IOException {
//
//        BoolQueryBuilder boolQueryBuilder = andMatchUnionWithList(queryMap);
//
//        return getQueryResultList(index, boolQueryBuilder, clazz);
//
//    }
//
//    /**
//     *
//     * 多条件查询:并集
//     * @param index
//     * @param queryMap
//     * @param clazz
//     * @throws IOException
//     */
//    public static <T>List<T> queryByFieldOrMatch(String index, Map<String, List<Object>> queryMap, Class<T> clazz) throws IOException {
//
//        BoolQueryBuilder boolQueryBuilder = orMatchUnionWithList(queryMap);
//
//        return getQueryResultList(index, boolQueryBuilder, clazz);
//
//    }
//
//    /**
//     * 多条件检索交集
//     * @param queryMap
//     * @return
//     */
//    private static BoolQueryBuilder andMatchUnionWithList(Map<String, Object> queryMap){
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        queryMap.forEach((fieldKey, fieldValue) -> {
//            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(String.valueOf(fieldKey), fieldValue));
//        });
//
//        return boolQueryBuilder;
//
//    }
//
//    /**
//     * 多条件检索并集
//     * @param queryMap
//     * @return
//     */
//    private static BoolQueryBuilder orMatchUnionWithList(Map<String, List<Object>> queryMap){
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        queryMap.forEach((fieldKey, fieldValue) -> {
//            for (int i = 0; i < fieldValue.size(); i++){
//                boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(String.valueOf(fieldKey), fieldValue.get(i)));
//            }
//        });
//
//        return boolQueryBuilder;
//
//    }
//
//    /***
//     *
//     * 批量添加数据
//     * @param index
//     * @param type
//     * @param objectList JSONObject; Entity
//     * @throws IOException
//     */
//    public static void saveAll(String index, String type, List<Object> objectList) throws IOException {
//
//        BulkRequest bulkRequest = new BulkRequest();
//        for (int i = 0; i < objectList.size(); i++){
//            bulkRequest.add(
//                    new IndexRequest(index, type)
//                            .source(JSON.toJSONString(objectList.get(i)), XContentType.JSON)
//            );
//        }
//        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//
//    }
//
//    /***
//     *
//     * 根据Id批量删除数据
//     * @param index
//     * @param type
//     * @param entityIdList
//     * @throws IOException
//     */
//    public static void deleteById(String index, String type, List<String> entityIdList) throws IOException {
//
//        BulkRequest bulkRequest = new BulkRequest();
//        for (String entityId : entityIdList){
//            bulkRequest.add(
//                    new DeleteRequest(index, type, entityId)
//            );
//        }
//        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//
//    }
//
//    /***
//     *
//     * 根据Id批量更新数据
//     * @param index
//     * @param type
//     * @param entityMap
//     * @throws IOException
//     */
//    public static void updateById(String index, String type, Map<String, Object> entityMap) throws IOException {
//
//        BulkRequest bulkRequest = new BulkRequest();
//        entityMap.forEach((entityId, entity) -> {
//            bulkRequest.add(
//                    new UpdateRequest(index, type, entityId)
//                            .doc(JSON.toJSONString(entity), XContentType.JSON)
//            );
//        });
//        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//
//    }
//    
//}
