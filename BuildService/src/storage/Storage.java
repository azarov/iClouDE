package storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.PropertiesManager;
import entities.Task;

/**
 * This class saves input file. 
 *
 */
public class Storage {
	
	private static volatile Storage instance = null;
	private static Object lockObject = new Object();
	
	private final static String DefaultFileUploadPath = "D:/uploaded/";  
	private String fileUploadPath;
	private final Logger logger = LoggerFactory.getLogger(Storage.class);
	private static final int BUFFER_SIZE = 1024;
	
	public ConcurrentHashMap<String, Task> tasks;

	private Storage()
	{
		tasks = new ConcurrentHashMap<String, Task>();		
		fileUploadPath = PropertiesManager.getInstance().getProperty("fileUploadPath", DefaultFileUploadPath);
	}
	
	public static Storage getInstance()
	{
		if (instance == null) {
			synchronized (lockObject) {
				if (instance == null) {
					instance = new Storage();
				}
			}
		}
		return instance;
	}
	
	public Task saveFile(InputStream inputStream, String fileName) throws IOException, URISyntaxException
	{
		String uploadedFileLocation = fileUploadPath + fileName;
		saveToDisc(inputStream, uploadedFileLocation);
		URI uri = new URI("file:///"+uploadedFileLocation);
		Task task = new Task(uri);
		tasks.put(task.getId(), task);
		return task;
	}
	
	public Task getTask(String taskId)
	{
		return tasks.get(taskId);
	}
	
	// save uploaded file to the specified location
	private void saveToDisc(final InputStream fileInputStream,
	        final String fileUploadPath) throws IOException
	{

		OutputStream out = null;
		try {
			out = new FileOutputStream(new File(fileUploadPath));
		
			int read = 0;
			byte[] bytes = new byte[BUFFER_SIZE];
	
			while ((read = fileInputStream.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}
	
			out.flush();
			
		} finally  {
			if (out != null) {				
				out.close();
			}
		}
	}
}
