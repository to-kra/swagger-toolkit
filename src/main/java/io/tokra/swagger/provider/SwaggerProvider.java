package io.tokra.swagger.provider;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.collections4.CollectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.config.BeanConfig;
import io.tokra.swagger.annotations.ApiVersion;

public class SwaggerProvider extends BeanConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SwaggerProvider.class);
	private ApiVersion annotation;
	private ServletContext sc;
	private static final String DEFAULT_PACKAGE = "com.ibm.kc";
	
	public SwaggerProvider() {
	}
	
	public SwaggerProvider(ServletContext sc, ApiVersion annotation) {
		this.annotation = annotation;
		this.sc = sc;
	}

	@Override
	public Set<Class<?>> classes() {
		Collection<Class<?>> output = classesPackageScan();
		if (CollectionUtils.isEmpty(output)) { //fallback: scan webinf classes
			output = classesWebInfClassesScan();
		}
		if(CollectionUtils.isEmpty(output)) { //fallback: scan webinf libs
			output = classesWebInfLibsScan();
		}
		if (CollectionUtils.isEmpty(output)) {
			logger.warn(">>> Swagger[{}] scanner not found any REST resources", annotation.version());
		} 
		return new HashSet<>(output);
	}

	protected Collection<Class<?>> classesPackageScan() {
		Reflections reflections = getResourcePackage() != null
				? reflectionBuilder(ClasspathHelper.forPackage(getResourcePackage()))
				: reflectionBuilder(ClasspathHelper.forPackage(DEFAULT_PACKAGE));
		Collection<Class<?>> classes = getSwaggerClasses(reflections);
		Collection<Class<?>> output = filterApiVersionedClassed(classes);
		if (CollectionUtils.isNotEmpty(output)) {
			logger.debug(">>> Swagger[{}] reflections used: 'package scan'", annotation.version());
			logger.info(">>> Swagger[{}] scanner found: '{}' REST resources", annotation.version(), output.size());
		}
		return output;
	}
	
	protected Collection<Class<?>> classesWebInfClassesScan() {
		Reflections reflections = reflectionBuilder(ClasspathHelper.forWebInfClasses(sc));
		Collection<Class<?>> classes = getSwaggerClasses(reflections);
		Collection<Class<?>> output = filterApiVersionedClassed(classes);
		if (CollectionUtils.isNotEmpty(output)) {
			logger.debug(">>> Swagger[{}] reflections used: 'WEB-INF/classes scan'", annotation.version());
			logger.info(">>> Swagger[{}] scanner found: '{}' REST resources", annotation.version(), output.size());
		}
		return output;
	}
	
	protected Collection<Class<?>> classesWebInfLibsScan() {
		Reflections reflections = reflectionBuilder(ClasspathHelper.forWebInfLib(sc));
		Collection<Class<?>> classes = getSwaggerClasses(reflections);
		Collection<Class<?>> output = filterApiVersionedClassed(classes);
		if (CollectionUtils.isNotEmpty(output)) {
			logger.debug(">>> Swagger[{}] reflections used: 'WEB-INF/lib scan'", annotation.version());
			logger.info(">>> Swagger[{}] scanner found: '{}' REST resources", annotation.version(), output.size());
		}
		return output;
	}
	
	protected Reflections reflectionBuilder(Collection<URL> resourceUrls) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.addUrls(resourceUrls);
		config.setScanners(new ResourcesScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());
		return new Reflections(config);
	}
	
	protected Reflections reflectionBuilder(URL resourceUrl) {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.addUrls(resourceUrl);
		config.setScanners(new ResourcesScanner(), new TypeAnnotationsScanner(), new SubTypesScanner());
		return new Reflections(config);
	}
	
	protected Collection<Class<?>> getSwaggerClasses(Reflections reflections) {
		Collection<Class<?>> classes = reflections.getTypesAnnotatedWith(Api.class);
		classes.addAll(reflections.getTypesAnnotatedWith(javax.ws.rs.Path.class));
		classes.addAll(reflections.getTypesAnnotatedWith(SwaggerDefinition.class));
		return classes;
	}
	
	protected Collection<Class<?>> filterApiVersionedClassed(Collection<Class<?>> classes) {
		Collection<Class<?>> output = new HashSet<Class<?>>();
        for (Class<?> cls : classes) {
        	ApiVersion clsAnnotation = cls.getAnnotation(ApiVersion.class);
            if (clsAnnotation != null) {
            	if (annotation.version().equals(clsAnnotation.version()) && !clsAnnotation.excludeFromScan()) {
            		output.add(cls);
            	}
            }
        }
		return output;
	}

	/**
	 * @return the annotation
	 */
	public ApiVersion getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(ApiVersion annotation) {
		this.annotation = annotation;
	}

	/**
	 * @return the sc
	 */
	public ServletContext getSc() {
		return sc;
	}

	/**
	 * @param sc the sc to set
	 */
	public void setSc(ServletContext sc) {
		this.sc = sc;
	}

	/**
	 * @return the defaultPackage
	 */
	public static String getDefaultPackage() {
		return DEFAULT_PACKAGE;
	}

	@Override
	public String getResourcePackage() {
		return super.getResourcePackage() != null ? super.getResourcePackage() : getDefaultPackage();
	}
	
}
