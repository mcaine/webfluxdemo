package com.mikeycaine.webfluxdemo;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.mikeycaine.webfluxdemo.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
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
@AutoConfigureWebTestClient
@Slf4j
class CassandraSimpleIntegrationTest {

    private static final String KEYSPACE_NAME = "test";
    private static final String CASSANDRA_VERSION = "4.0.1";
    private static final int CASSANDRA_PORT = 9042;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BookRepository bookRepository;

    @Container
    private static final CassandraContainer cassandra =
            (CassandraContainer) new CassandraContainer("cassandra:" + CASSANDRA_VERSION)
            .withExposedPorts(CASSANDRA_PORT);

    @BeforeAll
    static void setupCassandraConnectionProperties() {
        System.setProperty("cassandra.contact-points", cassandra.getContainerIpAddress());
        System.setProperty("cassandra.port", String.valueOf(cassandra.getMappedPort(CASSANDRA_PORT)));
        System.setProperty("cassandra.keyspace", KEYSPACE_NAME);

        createKeyspace(cassandra.getCluster());
    }

    static void createKeyspace(Cluster cluster) {
        try(Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME + " WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    @Nested
    class ApplicationContextIntegrationTest {
        @Test
        void testContainer_isRunning() {
            assertThat(cassandra.isRunning()).isTrue();
        }
    }

    @Nested
    class RepoIntegrationTest {

        void insertBooks() {
            Book javaBook = new Book(UUID.fromString("004dcec9-6467-4a59-8c9e-111111111111"), "TEST BOOK 1", "O'Reilly Media", "9780596009205");
            Book dPatternBook = new Book(UUID.fromString("004dcec9-6467-4a59-8c9e-222222222222"), "TEST BOOK 2", "O'Reilly Media", "9780596007126");

            bookRepository.insert(Arrays.asList(javaBook, dPatternBook)).blockLast();

            Mono<Long> bookCount = bookRepository.count();
            bookCount.subscribe(count -> log.info("Book count = " + count));
        }

        @Test
        public void testRetrieveBook() throws Exception {
            insertBooks();

            webTestClient
                    .get()
                    .uri("/books/004dcec9-6467-4a59-8c9e-111111111111")
                    .header(ACCEPT, APPLICATION_JSON_VALUE)
                    .exchange()
                    .expectStatus()
                    .is2xxSuccessful()
                    .expectBody(Book.class)
                    .consumeWith(response -> {
                        log.info(""+ response);
                    });
        }
    }
}
