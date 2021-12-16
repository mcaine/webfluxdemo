package com.mikeycaine.webfluxdemo;

import com.mikeycaine.webfluxdemo.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppStartupRunner implements ApplicationRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Application started with option names : {}", args.getOptionNames());

        Book javaBook = new Book(UUID.randomUUID(), "Head First Java", "O'Reilly Media", "9780596009205");
        Book dPatternBook = new Book(UUID.randomUUID(), "Head First Design Patterns", "O'Reilly Media", "9780596007126");

        bookRepository.deleteAll().block();
        bookRepository.insert(Arrays.asList(javaBook, dPatternBook)).blockLast();

        log.info("Saved books?");

        Mono<Long> bookCount = bookRepository.count();
        bookCount.subscribe(count -> log.info("Book count = " + count));

    }
}
