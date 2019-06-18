/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.irev.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.springframework.stereotype.Component;

/**
 *
 * @author smanne
 */
@Component
public class IrevUtil {

    public String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } catch (Exception e) {
            return "";
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
