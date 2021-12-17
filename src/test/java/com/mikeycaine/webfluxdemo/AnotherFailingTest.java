package com.mikeycaine.webfluxdemo;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

@Ignore
public class AnotherFailingTest {

    @Test
    public void testShouldFail() {
        fail();
    }
}
