package resources;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import taskManagement.TasksQueue;

import com.sun.jersey.api.view.Viewable;

@Path("/monitoring")
public class MonitoringService {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response GetAllTasks()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tasks", TasksQueue.getInstance().getAllTasks());
		
		return Response.ok().entity(new Viewable("/tasksMonitor", map)).build();
	}
}
