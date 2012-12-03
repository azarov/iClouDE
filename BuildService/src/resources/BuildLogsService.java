package resources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storage.Storage;
import taskManagement.TasksQueue;

import com.sun.jersey.api.view.Viewable;

import entities.BuildResult;
import entities.BuildStatus;
import entities.Task;
import entities.TaskStatus;

@Path("/buildLogs")
public class BuildLogsService {

	public final Logger logger = LoggerFactory.getLogger(BuildLogsService.class);
	public final Logger mainLogger = LoggerFactory.getLogger("mainLogger");
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response GetAllTasks(@QueryParam("id") String id)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		Task task = Storage.getInstance().getTask(id);
		
		if (task == null) {
			logger.warn("Checking build logs. No task with id {}", id);
			return Response.noContent().build();
		}
		
		if(task.getStatus() != TaskStatus.BUILDED)
		{
			map.put("buildLog", "Project wasn't builded yet.");
			
		}
		else
		{			
			BuildResult br = TasksQueue.getInstance().getResult(task.getId());
			if (br == null) 
			{
				logger.warn("Task {} has already built but there is no results", task.getId());
				return Response.noContent().build();
			}
			else 
			{
				try {
					String buildLogs = getBuildLogs(br.getLogs());
					buildLogs = transformToHTMLViewableFormat(buildLogs);
					map.put("buildLog", buildLogs);
				} catch (MalformedURLException e) {
					logger.error("Build result for task " + task.getId() + " contains invalid link to logs", e);
					return Response.noContent().build();
				} catch (IOException e) {
					logger.error("Can't read build logs from '"+ br.getLogs()+"'", e);
					return Response.noContent().build();
				} catch (Exception e)
				{
					logger.error("Unknown error during processing build logs..", e);
					return Response.noContent().build();
				}
			}
		}
		
		return Response.ok().entity(new Viewable("/buildLogsView", map)).build();
	}

	private String getBuildLogs(URI pathToLogs) throws MalformedURLException, IOException
	{
		InputStream in = pathToLogs.toURL().openStream();
		return convertStreamToString(in);
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private String transformToHTMLViewableFormat(String buildLogs) {
		return buildLogs.replaceAll("\n", "<br/>");
	}
}
