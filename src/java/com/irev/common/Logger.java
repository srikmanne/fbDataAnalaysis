package com.irev.common;

import java.util.HashMap;
import java.util.Map;
import java.io.StringWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import com.irev.common.*;

/**
 * Class		: Logger - handles application logging 
 * Author		: Srikanth Manne
 * Creation Date: 6/10/2019
 */
@Component
public class Logger  {
	



	/**
	 * Set debug log messages
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param sMsg  - (String) the message to log
	 *
	 */
	public void debug (String sFrom, String sMsg) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("debug", sMsg);
		System.out.println(sFrom+":"+ data);
	}

	/**
	 * Set info log messages
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param sMsg  - (String) the message to log
	 *
	 */
	public void info (String sFrom, String sMsg) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("info", sMsg);
		System.out.println(sFrom+":"+ data);
	}

	/**
	 * Set error log messages
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param sMsg  - (String) the message to log
	 *
	 */
	public void error (String sFrom, String sMsg) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("error", sMsg);
		System.out.println(sFrom+":"+ data);
	}

	/**
	 * Set error log messages, and save to db
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param sMsg  - (String) the message to log	
	 * @param req - (HttpServletRequest) for user-agent info
	 *
	 */
	public void error (String sFrom, String sMsg, HttpServletRequest req) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("error", sMsg);
		System.out.println(sFrom+":"+ data);		
	}

	/**
	 * Set error log messages
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param e  - (Exception) will get a string of this exception's printStackTrace to log
	 *
	 */
	public void error (String sFrom, Exception e) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("error", this.getStackTrace(e));
		System.out.println(sFrom+":"+ data);
	}

	/**
	 * Set error log messages, and save to db
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param e  - (Exception) will get a string of this exception's printStackTrace to log
	 * @param user - (User) user object for db conn and user info
	 * @param req - (HttpServletRequest) for user-agent info
	 *
	 */
	public void error (String sFrom, Exception e, HttpServletRequest req) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("error", this.getStackTrace(e));
		System.out.println(sFrom+":"+ data);		
	}

	/**
	 * Set front-end log messages
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param sMsg  - (String) the message to log
	 *
	 */
	public void frontend (String sFrom, String sMsg) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("front-end", sMsg);
		System.out.println(sFrom+":"+ data);
	}

	/**
	 * Set front-end log messages, and save to db
	 *
	 * @param sFrom  - (String) the method sending the message
	 * @param sMsg  - (String) the message to log
	 * @param user - (User) user object for db conn and user info
	 * @param req - (HttpServletRequest) for user-agent info
	 *
	 */
	public void frontend (String sFrom, String sMsg, HttpServletRequest req) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("front-end", sMsg);
		System.out.println(sFrom+":"+ data);
	}

	/**
	 * Given an Exception object, returns a string of the printStackTrace
	 *
	 * @param e  - (Exception)
	 * @return String  - printStackTrace of exception
	 *
	 */
	private String getStackTrace (Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
