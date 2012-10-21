package resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class UploadFileService {
	
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

		if (fileInfo == null)
		{
			respStatus = Response.Status.INTERNAL_SERVER_ERROR;
		}
		else
		{
			final String fileName = fileInfo.getFileName();
			String uploadedFileLocation = FILE_UPLOAD_PATH + File.separator
			        + fileName;

			try
			{
				saveToDisc(uploadedInputStream, uploadedFileLocation);
			}
			catch (Exception e)
			{
				respStatus = Response.Status.INTERNAL_SERVER_ERROR;
				e.printStackTrace();
			}
		}

		return Response.status(respStatus).build();
	}

	// save uploaded file to the specified location
	private void saveToDisc(final InputStream fileInputStream,
	        final String fileUploadPath) throws IOException
	{

		final OutputStream out = new FileOutputStream(new File(fileUploadPath));
		int read = 0;
		byte[] bytes = new byte[BUFFER_SIZE];

		while ((read = fileInputStream.read(bytes)) != -1)
		{
			out.write(bytes, 0, read);
		}

		out.flush();
		out.close();
	}
}
