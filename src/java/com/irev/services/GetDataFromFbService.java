package com.irev.services;

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
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import com.irev.kafka.services.KafkaProducerAndConsumerService;

@Primary
@Component
public class GetDataFromFbService {

    private Logger logger;
    private IrevUtil irevUtil;
    private JdbcTemplate jdbcTemplate;
    private KafkaProducerAndConsumerService kafkaConsumerService;

    /**
     * Constructor
     *
     * @param logger (Logger)
     * @param irevUtil (IrevUtil)
     * @param ds (DataSourceForJdbcTemplate)
     * @param kafkaConsumerService
     */
    @Autowired
    public GetDataFromFbService(Logger logger,
            IrevUtil irevUtil,
            DataSourceForJdbcTemplate ds,
            KafkaProducerAndConsumerService kafkaConsumerService) {
        this.logger = logger;
        this.irevUtil = irevUtil;
        this.logger = logger;
        this.jdbcTemplate = ds.getJdbcTemplate();
        this.kafkaConsumerService = kafkaConsumerService;
    }

    /**
     * Sends messages to user
     *
     * @return JsonResponse
     */
    public JsonResponse parse(HttpServletRequest req) {
        final String sWhere = "GetReviewsFromFbService.parse()";
        JsonResponse response = new JsonResponse();
        JSONObject msg = new JSONObject();
        FacebookClient fbClient;

        try {

            String sAccessToken = "";
            if (req.getParameterMap().containsKey("accessToken")) {
                sAccessToken = req.getParameter("accessToken").trim();
            }

            fbClient = new DefaultFacebookClient(sAccessToken);            

            JsonObject picture = fbClient.fetchObject("me/picture", JsonObject.class, Parameter.with("type", "large"), Parameter.with("redirect", "false"));
            JsonObject pictureObject = picture.getJsonObject("data");
            msg.put("profilepic", pictureObject.get("url"));

            // -Get fb user meta data
            User user = fbClient.fetchObject("me/", User.class, Parameter.with("metadata", "1"));

            FacebookType.Metadata m = user.getMetadata();//          
            FacebookType.Metadata.Connections c = m.getConnections();
            String content = "";
            msg.put("firstname", user.getName());
            //-url movies
            JSONObject json = new JSONObject(irevUtil.readUrl(c.getMovies()));
            JSONArray jsonArray = json.getJSONArray("data");
            msg.put("Movies", jsonArray);

            json = new JSONObject(irevUtil.readUrl(c.getTelevision()));
            jsonArray = json.getJSONArray("data");
            msg.put("TV", jsonArray);

            json = new JSONObject(irevUtil.readUrl(c.getMusic()));
            jsonArray = json.getJSONArray("data");
            msg.put("Music", jsonArray);

            jsonArray = new JSONArray();
            content += "<h3>Places</h3>"; //-created_time,story,shares,message,comments.summary(true),link,picture,id,status_type,type,likes.limit(1).summary(true),full_picture , shares failing for some reason
            Connection<Post> myFeed = fbClient.fetchConnection("me/feed", Post.class, Parameter.with("fields", "created_time,story,link,picture,id,status_type,type,full_picture"));
            // Connections support paging and are iterable
            //
            //// Iterate over the feed to access the particular pages
            int placeCount = 0;
            for (List<Post> myFeedPage : myFeed) {
                // Iterate over the list of contained data                
                for (Post post : myFeedPage) {
                    if (post.getStory() != null) {
                        if (post.getStory().contains(" at ")) {
                            json = new JSONObject();
                            json.put("pic", post.getFullPicture());
                            logger.info(sWhere, "121resp::" + post.getFullPicture());
                            String[] loc = new String[2];
                            loc = post.getStory().split(" at ");
                            json.put("name", loc[1]);
                            jsonArray.put(json);
                            content += post.getStory() + "<br>";
                            placeCount++;
                        }
                    }
                }
            }
            msg.put("Places", jsonArray);     
            
            //-Load the user
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("SELECT uuid, email FROM irev.users");

            String sSql = sBuilder.toString();
            logger.debug(sWhere, "sql = " + sSql);
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sSql);
            if (!results.isEmpty()) {
                for (Map<String, Object> result : results) {
                    String sUserRegId = result.get("uuid").toString();
                    String sStatusId = result.get("email").toString();
                    logger.info(sWhere, "email::" + sStatusId);
                }
            }
            //-Produce the data came from FB
            kafkaConsumerService.produce(msg.toString());
            //-Consume the data and upload it to AWS S3 bucket
            kafkaConsumerService.consume();

            response.setData(msg);

        } catch (Exception ex) {     //So that you can see what went wrong
            logger.error(sWhere, ex);  //in case you did anything incorrectly
        }
        return response;
    }
}
