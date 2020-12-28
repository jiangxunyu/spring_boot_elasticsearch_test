package com.example.spring_boot_elasticsearch_test1.controller;

import com.example.spring_boot_elasticsearch_test1.Repository.BookRepository;
import com.example.spring_boot_elasticsearch_test1.model.Book;
import com.example.spring_boot_elasticsearch_test1.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: jxy
 * @DATE: 2020/12/25
 * @Description:
 **/
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public void add(@RequestBody Book book) {
        bookService.save(book);
    }

    @GetMapping("/get")
    public void get(){
        Iterable<Book> books = bookService.findAll();
        for (Book book : books) {
            System.out.println(book);
        }
    }
}
