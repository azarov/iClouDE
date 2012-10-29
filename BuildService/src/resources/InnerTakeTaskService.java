package resources;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import taskManagement.KeyNotFoundException;
import taskManagement.TasksQueue;

import com.google.gson.Gson;
import com.sun.jersey.api.view.Viewable;

import entities.BuildResult;
import entities.Task;

public class InnerTakeTaskService {
	
	@POST
	@Path("/inner/take_task")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response takeTask(String buildResultJson)
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
