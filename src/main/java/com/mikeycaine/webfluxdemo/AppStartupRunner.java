package com.mikeycaine.webfluxdemo;

import com.mikeycaine.webfluxdemo.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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

        Book javaBook = new Book(UUID.fromString("33333333-6467-4a59-8c9e-333333333333"), "Head First Java", "O'Reilly Media", "9780596009205");
        Book dPatternBook = new Book(UUID.fromString("44444444-6467-4a59-8c9e-444444444444"), "Head First Design Patterns", "O'Reilly Media", "9780596007126");

        bookRepository.deleteAll().block();
        bookRepository.insert(Arrays.asList(javaBook, dPatternBook)).blockLast();

        log.info("Saved books?");
        bookRepository.count().doOnNext(count -> log.info("Book count = " + count)).block();
    }
}
