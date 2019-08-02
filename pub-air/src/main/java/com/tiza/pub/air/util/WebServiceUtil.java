package com.tiza.pub.air.util;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class WebServiceUtil {

    /**
     * 2019-07-19 by lbc
     * @param: url
     * @param: jsonObj
     * @Description: 用于从webservice中读取put信息:提交body信息
     */
    public static String doPut( String url, String jsonObj){
        String resStr = null;
        HttpClient htpClient = new HttpClient();
        PutMethod putMethod = new PutMethod(url);
        putMethod.addRequestHeader( "Content-Type","application/json" );
        putMethod.getParams().setParameter( HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8" );
        putMethod.setRequestBody( jsonObj );
        try{
            int statusCode = htpClient.executeMethod( putMethod );
//            log.info(statusCode);
            if(statusCode != HttpStatus.SC_OK){
                System.out.println("Method failed: "+putMethod.getStatusLine());
                return null;
            }
            byte[] responseBody = putMethod.getResponseBody();
            resStr = new String(responseBody,"UTF-8");

            System.out.println(resStr);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            putMethod.releaseConnection();
        }
        return resStr;
    }


}
