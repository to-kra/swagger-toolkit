package io.tokra.example.jaxrs.v0;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import io.tokra.example.jaxrs.provider.RestProvider;
import io.tokra.swagger.annotations.ApiVersion;

@Api(value = "/test")
@SwaggerDefinition(
   tags = {
           @Tag(name = "test", description = "Test API")
   }
)
@ApiVersion(version = "v0")
@Path("/test")
public class TestService implements RestProvider {
	
	@ApiOperation(value = "It works test !", 
			      notes = "It works test !",
		          produces = MediaType.TEXT_PLAIN)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response itWorks(){
		return Response.ok("REST: It works !").build();
	}

}
