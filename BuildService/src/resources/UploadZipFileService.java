package resources;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;

import com.google.gson.Gson;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import entities.GsonProvider;
import entities.Task;
import entities.UploadFileResponse;

@Path("/uploadzipfile")
public class UploadZipFileService {
	
	private final Logger logger = LoggerFactory.getLogger(UploadZipFileService.class);
	private final Logger mainLogger = LoggerFactory.getLogger("mainLogger");
	private Gson gson = GsonProvider.getGson();
	
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
			try
			{
				Storage storage = Storage.getInstance(); 
				Task task = storage.saveFile(uploadedInputStream);
				
				UploadFileResponse response = new UploadFileResponse(task.getId(), true);
				
				output = gson.toJson(response);
				
				logger.info("File uploaded to : " + task.getFullPathToZip().toString());
				mainLogger.info("Received new project. It was given a {} id", task.getId());
			}
			catch (IOException e)
			{
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				logger.error("Can't save uploaded file to storage", e);
			}
			catch (Exception e)
			{
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				logger.error("Unknown error during making response to upload file request", e);
			}
		}
		
		return Response.status(respStatus).entity(output).build();
	}
}
