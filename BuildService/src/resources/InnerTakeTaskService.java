package resources;

import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;
import com.sun.jersey.api.view.Viewable;

public class InnerTakeTaskService {
	
	@POST
	@Path("/inner/take_task")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response takeTask(String filesListJson)
	{
		String[] filesList = null;
		HashMap<String, Object> model = new HashMap<String, Object>();
		
		Gson gson = new Gson();
		filesList = gson.fromJson(filesListJson, String[].class);
		
		model.put("filesList", filesList);
		
		return Response.ok(new Viewable("/fileListView", model)).build();	
	}
}
