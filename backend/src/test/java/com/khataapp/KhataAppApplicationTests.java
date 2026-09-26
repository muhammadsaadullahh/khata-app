package com.khataapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"JWT_SECRET=test-only-secret-that-is-at-least-32-bytes-long",
		"AWS_REGION=eu-north-1",
		"AWS_DYNAMODB_TABLE_NAME=Khataapp",
		"AWS_DYNAMODB_ENDPOINT="
})
class KhataAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
