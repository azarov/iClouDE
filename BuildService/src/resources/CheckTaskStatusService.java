package resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;
import utils.Protocol;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import entities.ClientRequestInfo;
import entities.GsonProvider;
import entities.Status;
import entities.Task;
import entities.TaskStatus;

/**
 * Service that returns well-formed response of task status. 
 * 
 * @author Andrew Azarov
 *
 */
@Path("/checktaskstatus")
public class CheckTaskStatusService {

	private final Gson gson = GsonProvider.getGson();
	private final Logger logger = LoggerFactory.getLogger(CheckTaskStatusService.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response checktaskstatus(String criJson)
	{
		Status status = null;
		ClientRequestInfo cri = null; 

		try {
			cri = gson.fromJson(criJson, ClientRequestInfo.class);
		} catch (JsonSyntaxException e) {
			logger.error("Json transformation checktaskstatus request parameter problem", e);
			return Response.noContent().build();
		}
		
		if (cri == null) {
			logger.error("checktaskstatus request null argument");
			return Response.noContent().build();
		}
		
		if (!Protocol.checkVersion(cri.getProtocolVersion())) {
			return Response.noContent().build();
		}
		
		String zipId = cri.getZipID();
		
		if (zipId == null) {
			logger.warn("Checking status. ZipId is null");
			return Response.noContent().build();
		}
		
		Task task = Storage.getInstance().getTask(zipId);
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
