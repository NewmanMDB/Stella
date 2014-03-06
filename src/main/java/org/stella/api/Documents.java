package org.stella.api;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.stella.utils.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.stella.Main;

import static org.stella.Constants.*;

// TODO all request body that isn't delta-json needs to be moved to headers.
// reason: GET does not officially support a request-body, but it can take
// arbitrary header values. And for the sake of being consistent, moving
// 'revision' to X-STELLA-REVISION means that we should probably change
// 'action' and 'group' to X-STELLA-ACTION and X-STELLA-GROUP

@Path("api")
public class Documents {
	
	// Because we are embedding file paths with '/' in them in the url,
	// we accept any text as a path using the regex .*
	private static final String PATH_PATTERN = "{path:.*}";
	
	/**
	 * CORS is Cross-Origin Resource Sharing. From the client/editors' perspective,
	 * Stella is an external resource, so this must be set. The protocol is to
	 * first query the server with OPTIONS to see if further requests are allowed.
	 */
	@OPTIONS
	@Path(PATH_PATTERN)
	public Response checkCORS(@PathParam("path") String path, @HeaderParam("Access-Control-Request-Headers") String requestH){
		Main.log("OPTIONS {"+path+"}");
		return Response.status(Response.Status.OK)
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS") // all available verbs for this path
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", requestH)
				.build();
	}
	
	@PUT
	@Path(PATH_PATTERN)
	public Response createDocument(@PathParam("path") String path){
		Main.log("create {"+path+"}");
		// TODO even more thorough sanity check on inputs
		String fullpath = FileUtils.join(Main.config.getString(CONF_ROOT_JSON), path);
		File on_disk = new File(fullpath);
		boolean is_directory = path.charAt(path.length()-1) == File.separatorChar;
		boolean is_json_file = FileUtils.getExtension(path).equals("json");
		if(is_json_file){
			// TODO refactor disk-file management to a separate class
			if(!on_disk.exists()){
				on_disk.mkdirs();
				try {
					on_disk.createNewFile();
				} catch (IOException e) {
					Main.log("Could not create file " + fullpath, true);
				}
			}
		} else if(is_directory){
			on_disk.mkdirs();
		} else {
			return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
		}
		return Response.status(Response.Status.OK).build();
	}
	
	@DELETE
	@Path(PATH_PATTERN)
	public Response deleteDocument(@PathParam("path") String path){
		Main.log("delete {"+path+"}");
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Path(PATH_PATTERN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response interactDocument(@PathParam("path") String path){
		Main.log("edit/undo/redo {"+path+"}");
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Path(PATH_PATTERN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refreshDocument(@PathParam("path") String path, JsonNode req){
		Main.log("refresh {"+path+"}");
		return Response.status(Response.Status.OK).build();
	}
}
