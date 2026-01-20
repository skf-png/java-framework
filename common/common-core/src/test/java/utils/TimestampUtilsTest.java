package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimestampUtilsTest {

    @Test
    void getSecondsLasterSecond() {
        System.out.println(TimestampUtils.getCurrentSeconds());
//        System.out.println(TimestampUtils.getSecondsLasterSecond(100));
    }
}