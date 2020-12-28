package com.example.spring_boot_elasticsearch_test1.service;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/
import com.example.spring_boot_elasticsearch_test1.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface BookService {

    Book save(Book book);

    void delete(Book book);

    Iterable<Book> findAll();

    Page<Book> findByAuthor(String author, PageRequest pageRequest);

    Page<Book> findByTitle(String title, PageRequest pageRequest);

}
