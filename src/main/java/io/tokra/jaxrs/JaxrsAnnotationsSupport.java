package io.tokra.jaxrs;

import java.lang.annotation.Annotation;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of library: jaxrs-toolkit //FIXME will be linked after jaxrs-toolkit deployed to maven
 * @author tokra
 */
public final class JaxrsAnnotationsSupport {

	private static Logger logger = LoggerFactory.getLogger(JaxrsAnnotationsSupport.class);

	private JaxrsAnnotationsSupport() {
	}

	/**
	 * Returns {@link ApplicationPath} value from {@link Application}
	 * application
	 * 
	 * @since 1.0
	 * @param applicationClazz
	 * @return {@link String} applicationPath
	 */
	public static String getApplicationPath(Class<?> applicationClazz) {
		String applicationPath = "";
		if (applicationClazz != Application.class && Application.class.isAssignableFrom(applicationClazz)) {
			Annotation[] annotations = applicationClazz.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof ApplicationPath) {
					ApplicationPath annotationApplicationPath = (ApplicationPath) annotation;
					applicationPath = annotationApplicationPath.value();
				}
			}
		}
		return applicationPath;
	}

	/**
	 * Returns {@link ApplicationPath} value from {@link Application}
	 * application
	 * 
	 * @since 1.0
	 * @param className
	 * @return {@link String} applicationPath
	 */
	public static String getApplicationPath(String className) {
		String applicationPath = "";
		if (StringUtils.isNotBlank(className)) {
			try {
				Class<?> applicationClazz = Class.forName(className);
				applicationPath = getApplicationPath(applicationClazz);
			} catch (ClassNotFoundException e) {
				logger.error("Class {} not found:", className, e);
			}
		}
		return applicationPath;
	}

	/**
	 * Gets rest api {@link Path} path from its resource class
	 * 
	 * @since 1.0
	 * @param apiClazz
	 * @return String rest api path
	 */
	public static String getRestApiPath(Class<?> apiClazz) {
		Annotation[] annotations = apiClazz.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Path) {
				Path pathAnnotation = (Path) annotation;
				return pathAnnotation.value();
			}
		}
		return "";
	}
}