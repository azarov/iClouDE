package resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import entities.ClientRequestInfo;
import entities.GsonProvider;
import entities.Status;
import entities.Task;
import entities.TaskStatus;

@Path("/checktaskstatus")
public class CheckTaskStatusService {

	private final Gson gson = GsonProvider.getGson();
	private final Logger logger = LoggerFactory.getLogger(CheckTaskStatusService.class);
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response checktaskstatus(String taskInfo)
	{
		ClientRequestInfo cri = null;
		Status status = null;
		
		try {
			cri = gson.fromJson(taskInfo, ClientRequestInfo.class);
		} catch (JsonSyntaxException e) {
			logger.error("Can't parse json", e);
			return Response.serverError().build();
		}
		
		Task task = Storage.getInstance().getTask(cri.getZipID());
		if(task != null)
		{
			status = createStatusResponse(task);
		}
		else 
		{
			logger.warn("Checking status from task with unknown id");
			status = new Status(false, "Bad id");
		}
		
		return Response.ok().entity(gson.toJson(status)).build();
	}

	//this method is not thread-safe
	private Status createStatusResponse(Task task) {
		TaskStatus taskStatus = task.getStatus();
		return new Status(taskStatus.isTaskFinished(), taskStatus.getDescription());
	}
}
