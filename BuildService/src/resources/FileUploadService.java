package resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.soap.AddressingFeature.Responses;

import storage.Storage;
import storage.TasksQueue;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import entities.Task;

@Path("/file")
public class FileUploadService {
	
	private static final String FILE_UPLOAD_PATH = "d:/uploaded/";
	private static final int BUFFER_SIZE = 1024;

	@GET
	@Produces("text/plain")
	public String getClichedMessage() {
		return "Hello World";
	}
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
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
				Storage storage = new Storage(); //TODO: may be singleton will be better 
				Task task = storage.saveFile(uploadedInputStream, fileName);
				output = "File uploaded to : " + task.getFullPathToZip().toString();
				
				TasksQueue taskManager = TasksQueue.getInstance();
				taskManager.add(task);
			}
			catch (IOException e)
			{
				//TODO: think about another way to handling such type exceptions
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				output = e.getMessage();
				e.printStackTrace();
			}
			catch (Exception e)
			{
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				output = e.getMessage();
				e.printStackTrace();
			}
		}
		
		return Response.status(respStatus).entity(output).build();
	}
}
