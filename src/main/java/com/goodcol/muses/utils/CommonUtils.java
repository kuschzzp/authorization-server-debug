package com.goodcol.muses.utils;

import com.goodcol.muses.constants.DataSourceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.SQLException;
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

    public static Optional<String> getDatabaseType(JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        Assert.isTrue(null != dataSource, "请先配置数据源！");

        String driver;
        try {
            driver = dataSource.getConnection().getMetaData().getDriverName().toUpperCase();
            if (driver.contains(DataSourceConstants.MYSQL)) {
                return Optional.of(DataSourceConstants.MYSQL);
            } else if (driver.contains(DataSourceConstants.ORACLE)) {
                return Optional.of(DataSourceConstants.ORACLE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.error("内置实现方式不支持该数据源，请自定义 AuthorizationRepository、ClientRepository、AuthorizationConsentRepository");
        return Optional.empty();
    }

}
