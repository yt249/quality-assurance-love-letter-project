package edu.cmu.f24qa.loveletter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    // to learn more about JUnit 5:
    // https://junit.org/junit5/docs/current/user-guide/#writing-tests
    @Test
    void testExpectedValues() {
        assertEquals(Card.GUARD.getValue(), 1);
    }
}
