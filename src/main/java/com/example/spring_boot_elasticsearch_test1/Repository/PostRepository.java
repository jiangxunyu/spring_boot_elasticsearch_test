package com.example.spring_boot_elasticsearch_test1.Repository;

import com.example.spring_boot_elasticsearch_test1.model.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/
public interface PostRepository extends ElasticsearchRepository<Post,String> {
}
