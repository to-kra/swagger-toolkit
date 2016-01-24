package io.tokra.example.jaxrs;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.tokra.example.jaxrs.provider.RestExtensionProvider;
import io.tokra.example.jaxrs.provider.RestProvider;
import io.tokra.jaxrs.RestRegistrationSupport;
import io.tokra.swagger.annotations.ApiVersion;

@ApiVersion(version = "v0", excludeFromScan = true)
@ApplicationPath("/v0")
public class RestApplicationV0 extends Application {

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new LinkedHashSet<Object>();
		RestRegistrationSupport.registerCommonSingletonsOf(singletons, RestProvider.class);
		RestRegistrationSupport.registerCommonSingletonsOf(singletons, RestExtensionProvider.class);
		RestRegistrationSupport.registerVersionedSingletonsOf(singletons, RestProvider.class, RestRegistrationSupport.getApiVersion(RestApplicationV0.class));
		RestRegistrationSupport.registerVersionedSingletonsOf(singletons, RestExtensionProvider.class, RestRegistrationSupport.getApiVersion(RestApplicationV0.class));
		return singletons;
	}
	
}
	