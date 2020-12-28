package com.example.spring_boot_elasticsearch_test1;

import com.example.spring_boot_elasticsearch_test1.model.Book;
import com.example.spring_boot_elasticsearch_test1.service.BookService;
import javafx.application.Application;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import javax.annotation.PostConstruct;
import java.util.Map;

@SpringBootApplication
public class SpringBootElasticsearchTest1Application{

    @Autowired
    private ElasticsearchOperations es;

    @PostConstruct
    private void printElasticSearchInfo() {
        System.out.println("--ElasticSearch-->");
        Client client = es.getClient();
        Map<String, String> asMap = client.settings().getAsMap();
        asMap.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
        System.out.println("<--ElasticSearch--");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootElasticsearchTest1Application.class, args);
    }

}
