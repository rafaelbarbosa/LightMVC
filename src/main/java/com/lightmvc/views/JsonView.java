package com.lightmvc.views;

import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: rafael
 * Date: 09-02-2012
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class JsonView extends View {
    ObjectMapper mapper = new ObjectMapper();
    private Object output = new Object();
    public JsonView(Object output){

        this.output = output;
    }
    @Override
    public void forward(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("application/json");

        mapper.writeValue(response.getOutputStream(),this.output);
        response.getOutputStream().close();
        response.getOutputStream().flush();
    }
}
