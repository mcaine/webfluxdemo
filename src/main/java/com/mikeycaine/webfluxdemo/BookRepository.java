package com.mikeycaine.webfluxdemo;

import com.mikeycaine.webfluxdemo.model.Book;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends ReactiveCassandraRepository<Book, UUID> {

}
