package com.irev.persistence;

import com.irev.common.Logger;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class: DataSourceForJdbcTemplate - creates a JdbcTemplate object from params
 * Author: Sri Creation Date: 1/16/2018
 */
@Primary
@Component
public class DataSourceForJdbcTemplate {

    private String driverClassName = null;
    private String url = null;
    private String port = null;
    private String database = null;
    private String username = null;
    private String pwd = null;
    private Logger logger;
    private DataSource dataSource;

    /**
     * Constructor
     *
     * @param sDriver - (String) driver class name
     * @param sConnURL - (String) connection url
     * @param sPort - (String) connection port
     * @param sDatabaseName - (String) database to connect to
     * @param sUsername - (String) username for connection
     * @param sPwd - (String) password for connection
     */
    @Autowired
    public DataSourceForJdbcTemplate(@Value("${db.driver}") String sDriver,
            @Value("${db.url}") String sConnURL,
            @Value("${db.port}") String sPort,
            @Value("${db.name}") String sDatabaseName,
            @Value("${db.user}") String sUsername,
            @Value("${db.pass}") String sPwd,
            Logger logger) {

        this.driverClassName = sDriver;
        this.url = sConnURL;
        this.port = sPort;
        this.database = sDatabaseName;
        this.username = sUsername;
        this.pwd = sPwd;
        this.logger = logger;
    }

    /**
     * Provides a JdbcTemplate object
     *
     * @return JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate() {
        JdbcTemplate template = null;
        try {
            template = new JdbcTemplate();
            template.setDataSource(getDataSource("standard"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return template;
    }

    /**
     * Returns a DataSource object created from params
     *
     * @param dsType - (String) Data Source Type ("standard" | "readonly").
     * @return DriverManagerDataSource
     */
    private DriverManagerDataSource getDataSource(String dsType) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        try {           
            dataSource.setDriverClassName(this.driverClassName);
            dataSource.setUrl(this.url + ":" + this.port + "/" + this.database);
            dataSource.setUsername(this.username);
            dataSource.setPassword(this.pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}
