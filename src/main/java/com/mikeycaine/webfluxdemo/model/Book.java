package com.mikeycaine.webfluxdemo.model;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table
@ToString
public class Book {

    public Book (UUID id, String title, String publisher, String isbn) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.isbn = isbn;
    }

    @PrimaryKeyColumn(
        name = "id",
        type = PrimaryKeyType.PARTITIONED,
        ordering = Ordering.DESCENDING
    )
    @Id
    @Getter
    private UUID id;

    @Column
    @Getter
    private String title;

    @Column
    @Getter
    private String publisher;

    @Column
    @Getter
    private String isbn;

    @Column
    private Set<String> tags = new HashSet<>();
}
