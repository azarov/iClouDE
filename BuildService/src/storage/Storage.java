package storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import entities.Task;

/**
 * This class saves input file. 
 *
 */
public class Storage {
	
	private static final String FILE_UPLOAD_PATH = "D:/uploaded/";
	private static final int BUFFER_SIZE = 1024;
	
	public Task saveFile(InputStream inputStream, String fileName) throws IOException, URISyntaxException
	{
		String uploadedFileLocation = FILE_UPLOAD_PATH + fileName;
		saveToDisc(inputStream, uploadedFileLocation);
		URI uri = new URI("file:///"+uploadedFileLocation);
		Task task = new Task(uri);
		return task;
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
