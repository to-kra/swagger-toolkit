package io.tokra.example.jaxrs.v0;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.tokra.example.jaxrs.provider.RestProvider;
import io.tokra.swagger.annotations.ApiVersion;
import io.tokra.swagger.listing.AbstractApiListingResource;

@ApiVersion(version = "v0", excludeFromScan = true)
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ApiListingResourceV0 extends AbstractApiListingResource implements RestProvider {

}