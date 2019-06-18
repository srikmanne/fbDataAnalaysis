package com.irev.controller;

import com.irev.common.JsonResponse;
import com.irev.common.Logger;
import com.irev.services.GetDataFromFbService;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.Page;
import com.restfb.types.User;

@Controller
public class LoginController {

    private Logger logger;
    final String sController = "LoginController";
    private GetDataFromFbService getReviewsFromFbService;

    /**
     * Constructor
     *
     * @param logger (Logger)
     *
     */
    @Autowired
    public LoginController(Logger logger,
            GetDataFromFbService getReviewsFromFbService) {
        this.logger = logger;
        this.getReviewsFromFbService = getReviewsFromFbService;

    }

    //--------------------------------------------------------------------------
    /**
     *
     * @param req - (HttpServletRequest)
     * @param resp - (HttpServletResponse)
     * @return ResponseEntity
     */
    @RequestMapping(value = "/fb/login", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> init(HttpServletRequest req, HttpServletResponse resp) {
        JsonResponse response = new JsonResponse();
        String sWhere = sController + "::init";
        //-Setup response headers
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json; charset=utf-8");
        responseHeaders.set("Access-Control-Allow-Methods", "POST");
        responseHeaders.set("Access-Control-Allow-Headers", "Content-Type,Authorization");

        response = getReviewsFromFbService.parse(req);

        return new ResponseEntity<String>(response.getString(), responseHeaders, HttpStatus.valueOf(response.getStatusCode()));

    }

}
