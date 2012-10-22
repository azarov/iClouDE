package resources;

import java.util.NoSuchElementException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;

import storage.TaskManager;

@Path("/inner")
public class TaskService {

	@GET
	@Path("/get_task")
	@Produces("plain/text")
	public Response getTask()
	{
		TaskManager taskManager = TaskManager.getInstance();
		Response.Status status = Status.OK; 
		String path = "";
		
		try {
			path = taskManager.getNext().getFullPathToZip();
		} catch (NoSuchElementException e) {
			status = Status.NO_CONTENT;
			return Response.status(status).build();
		}
		
		return Response.status(status).type(MediaType.TEXT_PLAIN).entity(path).build();
	}
	
	@POST
	@Path("take_task")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response takeTask()
	{
		Response.Status status = Status.OK;
		String filesList = "";
		
		return Response.status(status).type(MediaType.TEXT_PLAIN).entity(filesList).build();
	}
}
