package resources;

import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import storage.TasksQueue;

@Path("/inner/get_task")
public class InnerGetTaskService {

	@GET
	@Produces("plain/text")
	public Response getTask()
	{
		TasksQueue taskManager = TasksQueue.getInstance();
		Response.Status status = Status.OK; 
		URI path = null;
		
		//TODO: change return value of getNext() if queue is empty to null
		try {
			path = taskManager.getNext().getFullPathToZip();
		} catch (NoSuchElementException e) {
			status = Status.NO_CONTENT;
			return Response.status(status).build();
		}
		
		return Response.status(status).type(MediaType.TEXT_PLAIN).entity(path).build();
	}
}
