package resources;

import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;
import taskManagement.TasksQueue;
import utils.Protocol;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import entities.GsonProvider;
import entities.NewTaskRequest;
import entities.OperationType;
import entities.Status;
import entities.Task;
import entities.TaskStatus;

@Path("/newbuildandruntask")
public class NewBuildAndRunTaskService {

	private final Gson gson = GsonProvider.getGson();
	private Logger logger = LoggerFactory.getLogger(NewBuildAndRunTaskService.class);
	private Logger mainLogger = LoggerFactory.getLogger("mainLogger");
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newBuildAndRunTask(String newTaskRequestMessage)
	{
		Response.Status respStatus = Response.Status.OK;
		Status status = new Status(false, "Unknown problem");
		String output = "";
		try {
			NewTaskRequest newTaskRequest = gson.fromJson(newTaskRequestMessage, NewTaskRequest.class);
			
			if (!Protocol.checkVersion(newTaskRequest.getProtocolVersion())) {
				status = new Status(false, "Unsupported protocol version. " + newTaskRequest.getProtocolVersion());
				logger.warn("Unsupported protocol version. {}", newTaskRequest.getProtocolVersion());
			}
			else {
				Task task = Storage.getInstance().getTask(newTaskRequest.getZipID());
							
				if (task != null) {
					TasksQueue taskManager = TasksQueue.getInstance();
	
					setAdditionalParameters(task, newTaskRequest);
					taskManager.add(task);
					task.setQueuingTime(Calendar.getInstance().getTime());
					mainLogger.info("Task {} was placed in the queue at {}.", task.getId(), task.getQueuingTime());
					status =  new Status(true, "Success");
				}
				else {
					respStatus = Response.Status.NO_CONTENT;
					logger.warn("No tasks with id "+newTaskRequest.getZipID());
					status =  new Status(false, "Bad id");
				}
			}
			
		} catch (JsonSyntaxException e) {
			//can't parse incoming json
			logger.error("Can't parse json", e);
			status = new Status(false, "Can't parse json");
		}
		catch (Exception e) {
			logger.error("newBuildAndRunTask error", e);
		}
		
		return Response.status(respStatus).entity(gson.toJson(status)).build();
	}

	private void setAdditionalParameters(Task task, NewTaskRequest newTaskRequest) {
		task.setCompilator(newTaskRequest.getCompilator());
		task.setCompileParameters(newTaskRequest.getCompileParameters());
		task.setLanguageType(newTaskRequest.getLanguageType());
		task.setOperation(OperationType.getOperationType(newTaskRequest.getOperation()));
		task.setEntryPointPath(newTaskRequest.getEntryPointPath());
	}
}
