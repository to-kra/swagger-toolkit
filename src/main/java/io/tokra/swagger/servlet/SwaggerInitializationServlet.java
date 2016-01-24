package io.tokra.swagger.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.models.Swagger;
import io.tokra.jaxrs.JaxrsAnnotationsSupport;
import io.tokra.swagger.annotations.ApiVersion;
import io.tokra.swagger.provider.SwaggerProvider;

public class SwaggerInitializationServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(SwaggerInitializationServlet.class);
	private static final long serialVersionUID = -6762342175262304345L;
	public static String KEY_SWAGGER_INSTANCE = "swagger.instance.";

	@Override
	public void init(ServletConfig servletConfig) {
		try {
			super.init(servletConfig);
			String contextPath = getServletContext().getContextPath();
			String apiPath = JaxrsAnnotationsSupport.getApplicationPath(getApplicationClass());
			ApiVersion apiVersionAnnotation = getApiVersionAnnotation();
			String apiVersion = getApiVersionFromAnnotation();
			
			if (StringUtils.isNotBlank(apiPath) && apiVersionAnnotation != null && StringUtils.isNotBlank(apiVersion)) {
				logger.info(">>> Configuration of Swagger[{}]: '{}'...", apiVersion, apiPath);
				SwaggerProvider swaggerProvider = new SwaggerProvider();	
				swaggerProvider.setSc(getServletContext());
				swaggerProvider.setAnnotation(apiVersionAnnotation);
				swaggerProvider.setBasePath(contextPath + apiPath);
				swaggerProvider.setVersion(apiVersion);
				swaggerProvider.setScan(true);
				Swagger swagger = swaggerProvider.getSwagger();
				servletConfig.getServletContext().setAttribute(KEY_SWAGGER_INSTANCE + apiVersion, swagger);
				logger.info(">>> Swagger[{}] initialized", apiVersion);
			}
						
		} catch (ServletException e) {
			logger.error("Unable to initialize servlet", e);
		}
	}
	
	protected String getApplicationClass() {
		return getInitParameter("rest.application.class");
	}
	
	protected String getResourcePackage() {
		return getInitParameter("resource.package");
	}

	protected String getApiVersionFromAnnotation() {
		String appClass = getApplicationClass();
		if (StringUtils.isBlank(appClass)) {
			logger.error("Missing init-parameters: 'application.class'");
			return "";
		}
		try {
			ApiVersion annotation = Class.forName(appClass).getAnnotation(ApiVersion.class);
			if (annotation == null) {
				logger.error("Missing annotation: '{}', on Application class", ApiVersion.class.getSimpleName());
				return "";
			}
			return annotation.version();
			
		} catch (ClassNotFoundException e) {
			logger.error("Cannot resolve class: '{}'", appClass);
		}
		return "";
	}
	
	protected ApiVersion getApiVersionAnnotation() {
		String appClass = getApplicationClass();
		if (StringUtils.isBlank(appClass)) {
			logger.error("Missing init-parameters: 'application.class'");
			return null;
		}
		try {
			ApiVersion annotation = Class.forName(appClass).getAnnotation(ApiVersion.class);
			if (annotation == null) {
				logger.error("Missing annotation: '{}', on Application class", ApiVersion.class.getSimpleName());
				return null;
			}
			return annotation;
			
		} catch (ClassNotFoundException e) {
			logger.error("Cannot resolve class: '{}'", appClass);
		}
		return null;
	}
}