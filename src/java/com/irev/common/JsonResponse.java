package com.irev.common;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Class		: JsonResponse - standard JSON response object
 * Author		: Srikanth Manne
 * Creation Date: 1/17/2019
 */
public class JsonResponse  {
	private JSONObject json = new JSONObject();

    /**
     * Constructor
     */
	public JsonResponse () {
		this.json.put("error_msg","");
		this.json.put("error_code",0);
		this.json.put("error_location","");
		this.json.put("data","");
		this.json.put("status_code",200);
	}

    /**
     * Set an error message and code
     * 
     * @param sMsg  - (String) error message
     * @param iErrCode  - (int) error code
     * @param sLocation  - (String) where in the code this occurred
     *
     */	
	public void setError(String sMsg, int iErrCode, String sLocation) {
		this.setErrorCode(iErrCode);
		this.setErrorMsg(sMsg);
		this.setErrorLocation(sLocation);
	}

    /**
     * Set an error message
     * 
     * @param sMsg  - (String) error message
     *
     */	
	public void setErrorMsg(String sMsg) {
		this.json.put("error_msg",sMsg);
	}

    /**
     * Set an error code
     * 
     * @param iErrCode  - (int) error code
     *
     */	
	public void setErrorCode(int iErrCode) {
		this.json.put("error_code",iErrCode);
	}

    /**
     * Set the error location
     * 
     * @param sLocation  - (String) error location
     *
     */	
	public void setErrorLocation(String sLocation) {
		this.json.put("error_location",sLocation);
	}

    /**
     * Create a new JSON element
     * 
     * @param sElementName  - (String) element name
     * @param sValue  - (String) element value
     *
     */	
	public void setElement(String sElementName, String sValue) {
		this.json.put(sElementName, sValue);
	}

    /**
     * Set standard return data element
     * 
     * @param sValue  - (String)
     *
     */	
	public void setData(String sValue) {
		this.json.put("data",sValue);
	}

    /**
     * Set standard return data element
     * 
     * @param jsonObj  - (JSONObject)
     *
     */ 
    public void setData(JSONObject jsonObj) {
        this.json.put("data", jsonObj);
    }

    /**
     * Set standard return data element
     * 
     * @param jsonArray  - (JSONArray)
     *
     */ 
    public void setData(JSONArray jsonArray) {
        this.json.put("data", jsonArray);
    }

    /**
     * Set the HTTP response status code
     * 
     * @param iStatus  - (int) status code
     *
     */	
	public void setResponseStatus(int iStatus) {
		this.json.put("status_code",iStatus);
	}

    /**
     * Get the status code
     * 
     * @return int
     *
     */	
	public int getStatusCode() {
        int iRet = 0;
        try {
            iRet = Integer.parseInt(this.json.get("status_code").toString());
        } catch (Exception e) {
            iRet = 101;  //-General Exception
        }
		return iRet;
	}

    /**
     * Get the standard return data element
     * 
     * @return String
     *
     */	
	public String getValue() {
		return this.json.get("data").toString();
	}

    /**
     * Get the error code
     * 
     * @return int
     *
     */	
	public int getErrorCode() {
        int iRet = 0;
        try {
            iRet = Integer.parseInt(this.json.get("error_code").toString());
        } catch (Exception e) {
            iRet = 101;  //-General Exception
        }
		return iRet;
	}

    /**
     * Get the entire response object as a string
     * 
     * @return String
     *
     */	
	public String getString() {
		return this.json.toString();
	}
}
