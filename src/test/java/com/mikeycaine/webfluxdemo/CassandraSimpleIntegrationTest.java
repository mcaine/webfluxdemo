package com.mikeycaine.webfluxdemo;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.mikeycaine.webfluxdemo.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@Slf4j
class CassandraSimpleIntegrationTest {

//    @LocalServerPort
//    private int port;



    private static final String KEYSPACE_NAME = "books";


    //@Autowired
    //private MockMvc mockMvc;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BookRepository bookRepository;


    @Container
    private static final CassandraContainer cassandra = (CassandraContainer) new CassandraContainer("cassandra:4.0.1")
            .withExposedPorts(9042);

    @BeforeAll
    static void setupCassandraConnectionProperties() {
        System.setProperty("cassandra.keyspace-name", KEYSPACE_NAME);
        System.setProperty("cassandra.contact-points", cassandra.getContainerIpAddress());
        System.setProperty("cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));
//
        createKeyspace(cassandra.getCluster());
    }

    static void createKeyspace(Cluster cluster) {
        try(Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME + " WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    @Test
    void givenCassandraContainer_whenSpringContextIsBootstrapped_thenContainerIsRunningWithNoExceptions() {
        assertThat(cassandra.isRunning()).isTrue();
    }

    void insertBooks() {
        Book javaBook = new Book(UUID.fromString("004dcec9-6467-4a59-8c9e-111111111111"), "Head First Java", "O'Reilly Media", "9780596009205");
        Book dPatternBook = new Book(UUID.fromString("004dcec9-6467-4a59-8c9e-222222222222"), "Head First Design Patterns", "O'Reilly Media", "9780596007126");

        bookRepository.deleteAll().block();
        bookRepository.insert(Arrays.asList(javaBook, dPatternBook)).blockLast();

        log.info("Saved books?");

        Mono<Long> bookCount = bookRepository.count();
        bookCount.subscribe(count -> log.info("Book count = " + count));

    }

    @Test
    public void testCreateRetrieveWithMockMVC() throws Exception {
        insertBooks();

        //this.mockMvc.perform(get("/books/004dcec9-6467-4a59-8c9e-111111111111")).andDo(print()).andExpect(status().isOk());
        this.webTestClient
                .get()
                .uri("/books/004dcec9-6467-4a59-8c9e-111111111111")
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Book.class)
                .consumeWith(book -> {
                    log.info("Got book: " + book);
                });;


    }

}
