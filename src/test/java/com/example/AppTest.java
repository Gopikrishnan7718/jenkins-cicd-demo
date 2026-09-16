package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void testApplicationMessage() {
        String message = "Hello from Java Maven CI/CD";

        assertEquals(
            "Hello from Java Maven CI/CD",
            message
        );
    }
}