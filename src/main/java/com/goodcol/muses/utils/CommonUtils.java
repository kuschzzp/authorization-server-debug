package com.goodcol.muses.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * 常用的一些东西
 *
 * @author Zhangzp
 * @date 2022年12月13日 17:30
 */
@Slf4j
public class CommonUtils {

    public static Date instantToDate(Instant instant) {
        return Optional.ofNullable(instant)
                .map(item -> Date.from(instant))
                .orElse(null);
    }

    public static Instant dateToInstant(Date date) {
        return Optional.ofNullable(date)
                .map(item -> date.toInstant())
                .orElse(null);
    }

}
