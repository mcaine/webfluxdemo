package com.mikeycaine.webfluxdemo;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Ignore("This tries to connect to the configured production server. But it will fail if run with other tests because CassandraSimpleIntegrationTest messes with the system properties")
class WebfluxdemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
