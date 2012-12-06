package resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;
import taskManagement.TasksQueue;
import utils.Protocol;

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
	
	@POST
	@Produces("application/zip")
//	public Response DownloadBuildZipFiles(@QueryParam("protocolVersion") int protocolVersion, @QueryParam("zipID") String zipId)
	public Response DownloadBuildZipFiles(String criJson)
	{
		StreamingOutput stream = null;
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
					try {
						copyStream(zipUri.toURL().openStream(), outputStream);
					} catch (IOException e) {
						logger.error("Error writing in a stream.", e);
					}
					finally
					{						
						outputStream.close();
					}
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
	
	private void copyStream(InputStream input, OutputStream output)
		    throws IOException
		{
		    byte[] buffer = new byte[1024];
		    int bytesRead = 0;
		    int streamSize = 0;
		    while ((bytesRead = input.read(buffer)) != -1)
		    {
		        output.write(buffer, 0, bytesRead);
		        streamSize += bytesRead;
		    }
		    
		    output.flush();
		    logger.debug("Readed zip file size : {}", streamSize); 
		}
}
