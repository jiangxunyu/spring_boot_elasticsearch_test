package com.example.spring_boot_elasticsearch_test1.Repository;

import com.example.spring_boot_elasticsearch_test1.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/
public interface BookRepository extends ElasticsearchRepository<Book,String> {
    Page<Book> findByAuthor(String author, Pageable pageable);

    Page<Book> findByTitle(String title, Pageable pageable);
}
