package io.tokra.jaxrs;

import java.util.ServiceLoader;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tokra.swagger.annotations.ApiVersion;

/**
 * jaxrs-toolkit
 * @author tokra
 */
public final class RestRegistrationSupport {

	private static final Logger logger = LoggerFactory.getLogger(RestRegistrationSupport.class);

	private RestRegistrationSupport() {
	}
	
	/**
	 * Tries to return {@link ApiVersion} annotation from Jarxs
	 * {@link Application} class
	 * 
	 * @param clazz
	 * @return {@link ApiVersion} annotation
	 */
	public static ApiVersion getApiVersion(Class<? extends Application> clazz) {
		return clazz.getAnnotation(ApiVersion.class);
	}

	/**
	 * Method for simple registration of singletons to {@link Application},
	 * using {@link ServiceLoader}
	 * 
	 * @since 1.0
	 * @param singletons
	 * @param clazz
	 */
	public static <T> void registerCommonSingletonsOf(Set<Object> singletons, Class<T> clazz) {
		ServiceLoader<T> providers = ServiceLoader.load(clazz);
		if (providers != null) {
			for (T provider : providers) {
				ApiVersion apiVersionFromProvider = provider.getClass().getAnnotation(ApiVersion.class);
				if (apiVersionFromProvider == null) {
					logger.info("Registering common: {} -> {}", clazz.getSimpleName(), provider.getClass().getName());
					singletons.add(provider);
				}
			}
		}
	}
	
	/**
	 * Method for simple registration of singletons (versioned with
	 * {@link ApiVersion} annotation) to {@link Application}, using
	 * {@link ServiceLoader}
	 * 
	 * @since 1.0
	 * @param singletons
	 * @param clazz
	 */
	public static <T> void registerVersionedSingletonsOf(Set<Object> singletons, Class<T> clazz, ApiVersion apiVersion) {
		ServiceLoader<T> providers = ServiceLoader.load(clazz);
		if (providers != null) {
			for (T provider : providers) {
				ApiVersion apiVersionFromProvider = provider.getClass().getAnnotation(ApiVersion.class);
				if(apiVersionFromProvider != null && apiVersionFromProvider.version().equals(apiVersion.version())) {
					logger.info("Registering /{} versioned: {} -> {}", apiVersion.version(), clazz.getSimpleName(), provider.getClass().getName());
					singletons.add(provider);
				}
			}
		}
	}
	
}