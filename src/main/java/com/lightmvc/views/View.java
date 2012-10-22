package com.lightmvc.views;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class View {
	
	private Map<String,String> responseParams;
	private ServletContext servletContext;


	
	public void setServletContext(ServletContext servletContext){
		this.servletContext = servletContext;
	}
	public ServletContext getServletContext(){
		return this.servletContext;
	}
	
	
	public abstract void forward(HttpServletRequest request, HttpServletResponse response ) throws Exception;
	


}
