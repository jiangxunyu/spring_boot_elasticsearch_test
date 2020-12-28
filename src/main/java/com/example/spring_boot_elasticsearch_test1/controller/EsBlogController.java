package com.example.spring_boot_elasticsearch_test1.controller;

import com.example.spring_boot_elasticsearch_test1.Repository.EsBlogRepository;
import com.example.spring_boot_elasticsearch_test1.model.EsBlog;
import org.apache.lucene.index.Term;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/
@RestController
@RequestMapping("blog")
public class EsBlogController {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 非聚合复杂查询
     * @return
     */
    @RequestMapping("test")
    public List<EsBlog> test(){
        //1.创建QueryBuilder(即设置查询条件)这儿创建的是组合查询(也叫多条件查询),后面会介绍更多的查询方法
        /*组合查询BoolQueryBuilder
         * must(QueryBuilders)   :AND
         * mustNot(QueryBuilders):NOT
         * should:               :OR
         */
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //设置模糊搜索,博客的简诉中有学习两个字
        builder.must(QueryBuilders.fuzzyQuery("sumary","学习"));
        //设置要查询博客的标题中含有关键字
        builder.must(new QueryStringQueryBuilder("man").field("springdemo"));

        //按照博客的评论数的排序是依次降低
        FieldSortBuilder sort = SortBuilders.fieldSort("commentSize").order(SortOrder.DESC);
        //设置分页(从第一页开始，一页显示10条)
        //注意开始是从0开始，有点类似sql中的方法limit 的查询
        //PageRequest pageRequest = new PageRequest(0, 10); 这个方法已过时，用下面的方法
        PageRequest pageRequest = PageRequest.of(0, 10);

        //2.构建查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //将搜索条件设置到构建中
        nativeSearchQueryBuilder.withQuery(builder);
        //将分页设置到构建中
        nativeSearchQueryBuilder.withPageable(pageRequest);
        //将排序设置到构建中
        nativeSearchQueryBuilder.withSort(sort);
        //生产NativeSearchQuery
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();

        //3.执行方法1
        Page<EsBlog> page = esBlogRepository.search(nativeSearchQuery);

        //执行方法2：注意，这儿执行的时候还有个方法那就是使用elasticsearchTemplate
        //执行方法2的时候需要加上注解
        //@Autowired
        //private ElasticsearchTemplate elasticsearchTemplate;
        List<EsBlog> blogList  = elasticsearchTemplate.queryForList(nativeSearchQuery, EsBlog.class);

        //4.获取总条数(用于前端分页)
        int total = (int) page.getTotalElements();

        //5.获取查询到的数据内容（返回给前端）
        List<EsBlog> content = page.getContent();

        return content;
    }

    @RequestMapping("test1")
    public List<EsBlog> test1(){
        //目标：搜索写博客写得最多的用户（一个博客对应一个用户），通过搜索博客中的用户名的频次来达到想要的结果
        //首先新建一个用于存储数据的集合
        List<String> ueserNameList=new ArrayList<>();
        //1.创建查询条件，也就是QueryBuild
        MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
        //2.构建查询
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //2.0 设置QueryBuilder
        nativeSearchQueryBuilder.withQuery(matchAllQuery);
        //2.1设置搜索类型，默认值就是QUERY_THEN_FETCH，参考https://blog.csdn.net/wulex/article/details/71081042
        nativeSearchQueryBuilder.withSearchType(SearchType.QUERY_THEN_FETCH);//指定索引的类型，只先从各分片中查询匹配的文档，再重新排序和排名，取前size个文档
        //2.2指定索引库和文档类型
        nativeSearchQueryBuilder.withIndices("blog").withTypes("blog");//指定要查询的索引库的名称和类型，其实就是我们文档@Document中设置的indedName和type
        //2.3重点来了！！！指定聚合函数,本例中以某个字段分组聚合为例（可根据你自己的聚合查询需求设置）
        //该聚合函数解释：计算该字段(假设为username)在所有文档中的出现频次，并按照降序排名（常用于某个字段的热度排名）
        TermsAggregationBuilder termsAggregation  = AggregationBuilders.terms("给聚合查询取的名").field("username").order(Terms.Order.count(false));
        nativeSearchQueryBuilder.addAggregation(termsAggregation);
        //2.4构建查询对象
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        //3.执行查询
        //3.1方法1,通过reporitory执行查询,获得有Page包装了的结果集
        Page<EsBlog> search = esBlogRepository.search(nativeSearchQuery);
        List<EsBlog> content = search.getContent();
        for (EsBlog esBlog : content) {
            ueserNameList.add(esBlog.getUsername());
        }
        //获得对应的文档之后我就可以获得该文档的作者，那么就可以查出最热门用户了
        //3.2方法2,通过elasticSearch模板elasticsearchTemplate.queryForList方法查询
        List<EsBlog> queryForList = elasticsearchTemplate.queryForList(nativeSearchQuery, EsBlog.class);

        //3.3方法3,通过elasticSearch模板elasticsearchTemplate.query()方法查询,获得聚合(常用)
        Aggregations aggregations = elasticsearchTemplate.query(nativeSearchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse searchResponse) {
                return searchResponse.getAggregations();
            }
        });
        //转成map集合
        Map<String, Aggregation> aggregationMap = aggregations.asMap();
        //获得对应的聚合函数的聚合子类，该聚合子类也是个map集合,里面的value就是桶Bucket，我们要获得Bucket
        StringTerms stringTerms = (StringTerms) aggregationMap.get("给聚合查询取的名");
        //获得所有的桶
        List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
        //将集合转换成迭代器遍历桶,当然如果你不删除buckets中的元素，直接foreach遍历就可以了
        Iterator<StringTerms.Bucket> iterator = buckets.iterator();

        while(iterator.hasNext()) {
            //bucket桶也是一个map对象，我们取它的key值就可以了
            String username = iterator.next().getKeyAsString();//或者bucket.getKey().toString();
            //根据username去结果中查询即可对应的文档，添加存储数据的集合
            ueserNameList.add(username);
        }
        //最后根据ueserNameList搜索对应的结果集
//        List<User> listUsersByUsernames = userService.listUsersByUsernames(ueserNameList);
        return null;
    }

}
