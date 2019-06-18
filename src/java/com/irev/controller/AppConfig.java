package com.irev.controller;

import java.util.List;

import javax.sql.DataSource;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DriverManagerDataSource;




@EnableWebMvc
@Configuration
@EnableScheduling
@PropertySources({
    @PropertySource(value = { "classpath:dev.properties" })
})
@ComponentScan(basePackages={"com.irev.controller","com.irev.common","com.irev.services","com.irev.persistence","com.irev.kafka.services","com.irev.services"},
    excludeFilters={@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE,value=AppConfig.class)})
public class AppConfig extends WebMvcConfigurerAdapter {
    
    @Autowired
    private Environment env;

    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // equivalent to <mvc:message-converters>
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {        
        // In case we decide to serve statics from webapp.
        //Commented out as not currently in use.
       //  registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
         registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
         //registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
         
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    @Bean
    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        return converter;
    }
    
    @Bean
    public StandardServletMultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    /**
     * Profile is configured via system property -Dspring.profiles.active
     * Note: This is set in the /opt/tomcat/bin/sentenv.sh file when using Tomcat.
     */
    @PostConstruct
    public void logProfile() {
  
//        if (env.getActiveProfiles().length == 0) {
//            logger.info("AppConfig::logProfile()", "No active Spring profile configured, using default profile.");
//        } else {
//            for (String profile : env.getActiveProfiles()) {
//                logger.info("AppConfig::logProfile()", "Detected Spring profile: "+profile+"!!!");
//            }
//        }
    }
}
