package utils;

import framework.utils.TimestampUtils;
import org.junit.jupiter.api.Test;

class TimestampUtilsTest {

    @Test
    void getSecondsLasterSecond() {
        System.out.println(TimestampUtils.getCurrentSeconds());
//        System.out.println(TimestampUtils.getSecondsLasterSecond(100));
    }
}