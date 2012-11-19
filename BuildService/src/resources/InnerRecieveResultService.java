package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import taskManagement.KeyNotFoundException;
import taskManagement.TasksQueue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import entities.BuildResult;
import entities.GsonProvider;

@Path("/inner/receive_result")
public class InnerRecieveResultService {
	
	private final Logger logger = LoggerFactory.getLogger(InnerRecieveResultService.class);
	private final Gson gson = GsonProvider.getGson();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveResult(String buildResultJson)
	{
		BuildResult buildResult = null;
		
		try {
			buildResult = gson.fromJson(buildResultJson, BuildResult.class);
			
		} catch (JsonSyntaxException e1) {
			logger.error("Json transformation receive_result request parameter problem", e1);
			return Response.serverError().build();
		}
		
		TasksQueue tasksQueue = TasksQueue.getInstance();
		
		try {
			tasksQueue.saveResult(buildResult);
		} catch (KeyNotFoundException e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
		
		return Response.ok().build();	
	}
}
