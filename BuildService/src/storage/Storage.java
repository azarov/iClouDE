package storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Task;

/**
 * This class saves input file. 
 *
 */
public class Storage {
	
	private static volatile Storage instance = null;
	private static Object lockObject = new Object();
	
	private String FILE_UPLOAD_PATH = "D:/uploaded/";
	private final Logger logger = LoggerFactory.getLogger(Storage.class);
	private static final int BUFFER_SIZE = 1024;
	
	public ConcurrentHashMap<String, Task> tasks;

	private Storage()
	{
		tasks = new ConcurrentHashMap<String, Task>();		
		readFileUploadPath();
	}

	private void readFileUploadPath() {
		Properties properties = new Properties();
		
		try {
			properties.load(this.getClass().getResourceAsStream("/conf.properties"));
		} catch (IOException e) {
			logger.error("Can't load properties file", e);
			return;
		}
		
		FILE_UPLOAD_PATH = properties.getProperty("fileUploadPath");
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
		String uploadedFileLocation = FILE_UPLOAD_PATH + fileName;
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
