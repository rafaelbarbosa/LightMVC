package com.lightmvc.views;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspView extends View {
    Map<String,String> responseParams;
	private String jspFile;
	public JspView(String jspFile, Map<String, String> response) {
		super();
        responseParams = response;
		this.jspFile = jspFile;  
	}
	
	@Override
	public void forward(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String destination = "/WEB-INF/"+this.jspFile;
 
		RequestDispatcher rd = this.getServletContext().getRequestDispatcher(destination);
		rd.forward(request, response);
		
		
	}
	
}