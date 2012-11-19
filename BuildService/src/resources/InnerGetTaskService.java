package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import taskManagement.TasksQueue;

import com.google.gson.Gson;

import entities.GsonProvider;
import entities.Task;

@Path("/inner/get_task")
public class InnerGetTaskService {
	
	private final Gson gson = GsonProvider.getGson();
	
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
	
	//if task is null, it makes empty json
	public String composeMessage(Task task)
	{
		return gson.toJson(task);
	}
}
