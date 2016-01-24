package io.tokra.swagger.listing;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.config.FilterFactory;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Swagger;
import io.tokra.swagger.annotations.ApiVersion;
import io.tokra.swagger.servlet.SwaggerInitializationServlet;

public class AbstractApiListingResource extends ApiListingResource {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractApiListingResource.class);
	
	/**
	 * Handles swagger's listing json from versioned {@link Swagger} instance
	 * 
	 * @since 1.0
	 */
	@Override
	public Response getListingJson(Application app, ServletConfig config, HttpHeaders headers, UriInfo uriInfo) {
		if (StringUtils.isNotBlank(getApiVersion())) {
			Swagger swagger = (Swagger) config.getServletContext().getAttribute(SwaggerInitializationServlet.KEY_SWAGGER_INSTANCE + getApiVersion());
			if (swagger != null) {
				swagger = updateSwagger(config, headers, uriInfo, swagger);
				return Response.ok().entity(swagger).build();
			}
		}
		return Response.status(404).build();		
	}

	/**
	 * Returns registered versioned {@link Swagger} instance from
	 * {@link ServletContext}
	 * 
	 * @since 1.0
	 * @param config
	 * @param headers
	 * @param uriInfo
	 * @param swagger
	 * @return {@link Swagger} instance
	 */
	protected Swagger updateSwagger(ServletConfig config, HttpHeaders headers, UriInfo uriInfo, Swagger swagger) {
		SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
		if (filterImpl != null) {
			SpecFilter f = new SpecFilter();
			swagger = f.filter(swagger, filterImpl, getQueryParams(uriInfo.getQueryParameters()), getCookies(headers), getHeaders(headers));
		}
		config.getServletContext().setAttribute(SwaggerInitializationServlet.KEY_SWAGGER_INSTANCE + getApiVersion(), swagger);
		return swagger;
	}

	/**
	 * Returns {@link ApiVersion} annotation string from
	 * {@link AbstractApiListingResource} implementation
	 * 
	 * @since 1.0
	 * @return {@link ApiVersion} annotation string
	 */
	protected String getApiVersion() {
		ApiVersion apiVersionAnnotation = getClass().getAnnotation(ApiVersion.class);
		String apiVersion = "";
		if (apiVersionAnnotation != null) {
			apiVersion = apiVersionAnnotation.version();
		}
		if (StringUtils.isBlank(apiVersion)) {
			logger.error("Missing annotation: '{}', on resource class: '{}'", ApiVersion.class.getName(), getClass().getName());
		}
		return apiVersion;
	}

}