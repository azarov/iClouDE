package resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;
import taskManagement.TasksQueue;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import entities.BuildResult;
import entities.ClientRequestInfo;
import entities.GsonProvider;
import entities.Task;

@Path("/downloadbuildzipfilez")
public class DownloadBuildZipFilesService {

	private Gson gson = GsonProvider.getGson();
	private Logger logger = LoggerFactory.getLogger(DownloadBuildZipFilesService.class);
	
	@GET
	@Produces("application/zip")
	public Response DownloadBuildZipFiles(@QueryParam("protocolVersion") String protocolVersion, @QueryParam("zipID") String zipId)
	{
		StreamingOutput stream = null;
		
		Task task = Storage.getInstance().getTask(zipId);
		if(task != null)
		{
			final BuildResult br = TasksQueue.getInstance().getResult(task.getId());
			if (br == null) {
				return Response.noContent().build();
			}
			
			final URI zipUri = br.getCompiledSourcesPath();
			if (zipUri == null) {
				return Response.noContent().build();
			}
			
			stream = new StreamingOutput() {
				
				@Override
				public void write(OutputStream outputStream) throws IOException,
						WebApplicationException {
					copyStream(zipUri.toURL().openStream(), outputStream);
					outputStream.close();
				}
			};
		}
		else 
		{
			logger.warn("Checking status from task with unknown id");
			return Response.noContent().build();
		}
		
		return Response.ok(stream).build();
	}
	
	private static void copyStream(InputStream input, OutputStream output)
		    throws IOException
		{
		    byte[] buffer = new byte[1024];
		    int bytesRead;
		    while ((bytesRead = input.read(buffer)) != -1)
		    {
		        output.write(buffer, 0, bytesRead);
		    }
		    
		    output.flush();
		}
}
