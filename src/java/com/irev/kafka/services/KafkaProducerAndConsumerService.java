/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.irev.kafka.services;

/**
 *
 * @author smanne
 */
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.irev.common.JsonResponse;
import com.irev.common.Logger;

import com.irev.common.*;
import com.irev.persistence.DataSourceForJdbcTemplate;
import com.irev.services.AwsS3Service;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import com.restfb.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Primary
@Component
public class KafkaProducerAndConsumerService {

    private Logger logger;
    private IrevUtil irevUtil;
    private JdbcTemplate jdbcTemplate;
    private AwsS3Service awsS3Service;
    private Environment env;

    /**
     * Constructor
     *
     * @param logger (Logger)
     * @param irevUtil (IrevUtil)
     * @param ds (DataSourceForJdbcTemplate)
     * @param awsS3Service
     * @param env
     */
    @Autowired
    public KafkaProducerAndConsumerService(Logger logger,
            IrevUtil irevUtil,
            DataSourceForJdbcTemplate ds,
            AwsS3Service awsS3Service,
            Environment env) {
        this.logger = logger;
        this.irevUtil = irevUtil;
        this.logger = logger;
        this.jdbcTemplate = ds.getJdbcTemplate();
        this.awsS3Service = awsS3Service;
        this.env = env;

    }

    /**
     * Sends messages to user
     *
     * @return JsonResponse
     */
    public void consume() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-group");

        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("fb-data");
        kafkaConsumer.subscribe(topics);
        try {

            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord record : records) {
                // System.out.println(String.format("Topic - %s, Partition - %d, Value: %s", record.topic(), record.partition(), record.value()));
                String jFbData = record.value().toString();

                InputStream stream = new ByteArrayInputStream(jFbData.getBytes(StandardCharsets.UTF_8));

                //-Create relative path to file
                String sRelativePath = "2e23407f-aea8-4eb0-ba49-106a04410371/fbUser.json";

                String sKeyName = "/data/files/clients/" + sRelativePath;
                awsS3Service.uploadInputStreamToS3(stream, "", sKeyName, 2283);

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            kafkaConsumer.close();
        }
    }

    public void produce(String sData) {
        try {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", "localhost:9092");
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            KafkaProducer kafkaProducer = new KafkaProducer(properties);

            kafkaProducer.send(new ProducerRecord("fb-data", "0", " " + sData));

            InputStream stream = new ByteArrayInputStream("StreamData".getBytes(StandardCharsets.UTF_8));
            //-Create relative path to file
            String sRelativePath = "2e23407f-aea8-4eb0-ba49-106a04410371" + "/" + "data" + "/";

            String sKeyName = env.getProperty("s3.client.storage") + "/" + sRelativePath;
            awsS3Service.uploadInputStreamToS3(stream, "fb-data.txt", sKeyName, 0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
