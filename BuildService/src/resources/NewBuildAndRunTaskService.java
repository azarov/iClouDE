package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import storage.Storage;
import taskManagement.TasksQueue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import entities.NewTaskRequest;
import entities.OperationType;
import entities.Task;

@Path("/newbuildandruntask")
public class NewBuildAndRunTaskService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newBuildAndRunTask(String newTaskRequestMessage)
	{
		Response.Status respStatus = Response.Status.OK;
		String output = "";
		Gson gson = new Gson();
		try {
			NewTaskRequest newTaskRequest = gson.fromJson(newTaskRequestMessage, NewTaskRequest.class);
			
			Task task = Storage.getInstance().getTask(newTaskRequest.getZipId());
						
			if (task != null) {
				TasksQueue taskManager = TasksQueue.getInstance();

				setAdditionalParameters(task, newTaskRequest);
				taskManager.add(task);
			}
			else {
				//TODO: to log it
				//task with such id not found
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
			}
			
		} catch (JsonSyntaxException e) {
			//can't parse incoming json
			respStatus = Response.Status.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		
		return Response.status(respStatus).entity(output).build();
	}

	private void setAdditionalParameters(Task task, NewTaskRequest newTaskRequest) {
		task.setCompilator(newTaskRequest.getCompilator());
		task.setCompileParameters(newTaskRequest.getCompileParameters());
		task.setLanguageType(newTaskRequest.getLanguageType());
		task.setOperation(OperationType.valueOf(newTaskRequest.getOperation()));
		
	}
}
