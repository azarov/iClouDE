package resources;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import storage.Storage;
import taskManagement.TasksQueue;

import com.google.gson.Gson;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import entities.UploadFileResponse;
import entities.IdProvider;
import entities.Task;

@Path("/uploadzipfile")
public class UploadZipFileService {
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fileUpload(
	        @FormDataParam("file") InputStream uploadedInputStream,
	        @FormDataParam("file") FormDataContentDisposition fileInfo)
	        throws IOException
	{
		Response.Status respStatus = Response.Status.OK;
		String output = "";

		if (fileInfo == null)
		{
			respStatus = Response.Status.INTERNAL_SERVER_ERROR;
		}
		else
		{
			final String fileName = fileInfo.getFileName();
	
			try
			{
				Storage storage = Storage.getInstance(); 
				Task task = storage.saveFile(uploadedInputStream, fileName);
				
				UploadFileResponse response = new UploadFileResponse(task.getId(), true);
				Gson gson = new Gson();
				output = gson.toJson(response);
				
				//TODO: to log it
				//output = "File uploaded to : " + task.getFullPathToZip().toString();
				
				//TasksQueue taskManager = TasksQueue.getInstance();
				//taskManager.add(task);
			}
			catch (IOException e)
			{
				//TODO: to think about another way to handling such type exceptions
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				e.printStackTrace();
			}
			catch (Exception e)
			{
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				e.printStackTrace();
			}
		}
		
		return Response.status(respStatus).entity(output).build();
	}
}