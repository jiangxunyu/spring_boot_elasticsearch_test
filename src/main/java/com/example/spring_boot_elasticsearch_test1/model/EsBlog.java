package com.example.spring_boot_elasticsearch_test1.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/

@Document(indexName = "blog", type = "blog")
@Data
public class EsBlog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id  // 主键,注意这个搜索是id类型是string，与我们常用的不同
    private String id;  //@Id注解加上后，在Elasticsearch里相应于该列就是主键了，在查询时就可以直接用主键查询
    @Field(analyzer = "false")
    private Long blogId; // Blog 实体的 id，这儿增加了一个blog的id属性
    private String title;
    private String summary;
    private String content;
    private String springdemo;
    private Long commentSize;
    private String tags;
    private String username;
}
