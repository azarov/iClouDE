package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import taskManagement.TasksQueue;

import com.google.gson.Gson;

import entities.Task;

@Path("/inner/get_task")
public class InnerGetTaskService {

	Logger logger = LogManager.getLogger(InnerGetTaskService.class.getName());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTask()
	{
		TasksQueue taskManager = TasksQueue.getInstance();
		Response.Status status = Status.OK; 
		
		Task nextTask = taskManager.getNext();
		
		String message = composeMessage(nextTask);
		
		return Response.status(status).entity(message).build();
	}
	
	public String composeMessage(Task task)
	{
		Gson gson = new Gson();
		return gson.toJson(task);
	}
}
