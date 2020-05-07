package com.focustech.gateway.site.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit.*;

public class DateUtils {
    public static long getEpoch(TimeUnit timeUnit) {
        switch (timeUnit) {
            case SECONDS:
                return System.currentTimeMillis() / 1000;
            case MINUTES:
                return System.currentTimeMillis() / 1000 / 60;
            case HOURS:
                return System.currentTimeMillis() / 1000 / 60 / 60;
            case DAYS:
                return System.currentTimeMillis() / 1000 / 60 / 60 / 24;
            default:
                throw new IllegalStateException("unsupported timeUnit:" + timeUnit);
        }
    }

    public static void main(String[] args) {
        System.out.println(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        System.out.println(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()));
        System.out.println(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()));
        System.out.println(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));
        System.out.println(getEpoch(TimeUnit.SECONDS));
        System.out.println(getEpoch(TimeUnit.MINUTES));
        System.out.println(getEpoch(TimeUnit.HOURS));
        System.out.println(getEpoch(TimeUnit.DAYS));
    }
}
