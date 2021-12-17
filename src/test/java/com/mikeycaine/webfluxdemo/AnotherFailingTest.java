package com.mikeycaine.webfluxdemo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class AnotherFailingTest {

    @Test
    public void testShouldFail() {
        fail();
    }
}
