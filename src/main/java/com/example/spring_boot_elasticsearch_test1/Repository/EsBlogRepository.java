package com.example.spring_boot_elasticsearch_test1.Repository;

import com.example.spring_boot_elasticsearch_test1.model.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog, String> {
    //下面是我们根据 spring data jpa 的命名规范额外创建的两个查询方法
    /**
     * 模糊查询(去重)，根据标题，简介，描述和标签查询（含有即可）Containing
     * @param title
     * @param Summary
     * @param content
     * @param tags
     * @param pageable
     * @return
     */
    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title, String Summary, String content, String tags, Pageable pageable);

    /**
     * 根据 Blog 的id 查询 EsBlog
     * @param blogId
     * @return
     */
    EsBlog findByBlogId(Long blogId);
}
