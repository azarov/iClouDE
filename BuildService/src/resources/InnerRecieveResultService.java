package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import taskManagement.KeyNotFoundException;
import taskManagement.TasksQueue;

import com.google.gson.Gson;

import entities.BuildResult;

public class InnerRecieveResultService {
	
	@POST
	@Path("/inner/receive_result")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response receiveResult(String buildResultJson)
	{
		Gson gson = new Gson();
		BuildResult buildResult = gson.fromJson(buildResultJson, BuildResult.class);
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
