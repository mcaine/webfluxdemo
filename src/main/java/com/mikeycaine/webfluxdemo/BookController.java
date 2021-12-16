package com.mikeycaine.webfluxdemo;

import com.mikeycaine.webfluxdemo.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping("/{id}")
    public Mono<Book> getBookById(@PathVariable UUID id) {
        return bookRepository.findById(id);
    }

    @GetMapping
    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
