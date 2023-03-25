package org.tyler.husher.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ThreadUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtils.class);

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LOGGER.warn("Sleep interrupted ({}ms)", ms);
        }
    }

    public static void sleep(Duration duration) {
        sleep(duration.toMillis());
    }
}
