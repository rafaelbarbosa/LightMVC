package com.lightmvc.servlet;

import com.lightmvc.annotations.Action;
import com.lightmvc.annotations.Controller;
import com.lightmvc.annotations.Index;
import com.lightmvc.views.View;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);
	private static final long serialVersionUID = 1L;
	private Set<Class<?>> controllers;
	private Map<Pattern,Class<?>> mappings;
	

    private Pattern replacePlaceholders(String path){
      return Pattern.compile(path.replaceAll("\\{.*\\}","(.*)"));
    }
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
	    String controllerPath;
        Action action;
        String actionPath;


        logger.info("Starting lightMVC dispatcher servlet");
		
	    Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forClassLoader(ClasspathHelper.getContextClassLoader())));
	 	
	
		controllers = reflections.getTypesAnnotatedWith(Controller.class);
		
		mappings = new ConcurrentHashMap<Pattern, Class<?>>();
		
		for(Class<?> controller :controllers){
            controllerPath = controller.getAnnotation(Controller.class).path();
            mappings.put(replacePlaceholders(controllerPath), controller);
            logger.info("Added mapping {} , {} ", controllerPath, controller.getName());
            for(Method method : controller.getMethods()){
                action = method.getAnnotation(Action.class);
                if(action != null){
                    actionPath = controllerPath + action.path();
                    mappings.put(replacePlaceholders(actionPath), controller);
                    logger.info("Added mapping {} , {} ", actionPath, controller.getName());
                }
            }
		}

    }
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String                              path                = req.getRequestURI().substring(req.getContextPath().length());
		Iterator<Entry<Pattern, Class<?>>>   iterator            = mappings.entrySet().iterator();


        //Pattern urlPattrn = Pattern.compile("(/\\{([a-z0-9]).*\\}).*");
        Entry<Pattern, Class<?>> entry= null;
		while(iterator.hasNext()){
			entry = iterator.next();
			if(entry.getKey().matcher(path).matches()){
                    break;
            }
            entry = null;
		}
		if(entry == null){
			resp.setStatus(404);
            resp.flushBuffer();
            super.doGet(req, resp);
		}
		
		Class<?> clazz = entry.getValue();
		try {
			Object controller = clazz.newInstance();
			Method[] methods = clazz.getDeclaredMethods();
			for(Method method : methods){
				if(entry.getKey().pattern().equals(path) &&
				   method.isAnnotationPresent(Index.class)){

					View view = (View) method.invoke(controller, null);
					try {
						view.setServletContext(getServletContext());
						view.forward(req, resp);
						return;
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					
				}
				
				else if(matchedPath.equals(path)&&method.isAnnotationPresent(Action.class)){
					if(method.getReturnType().isAssignableFrom(View.class)){


                        View view = (View) method.invoke(controller, null);

                        try {
                            view.setServletContext(getServletContext());
                            view.forward(req, resp);
                            return;
                        } catch (Exception e) {
                            logger.error(e.getMessage(),e);
                        }
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        super.doGet(req, resp);
		
	}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
