package utils;

import framework.core.utils.TimestampUtils;
import org.junit.jupiter.api.Test;

class TimestampUtilsTest {

    @Test
    void getSecondsLasterSecond() {
        System.out.println(TimestampUtils.getCurrentSeconds());
//        System.out.println(TimestampUtils.getSecondsLasterSecond(100));
    }
}