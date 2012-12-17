package builder.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.FutureTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utils.Utils;

import entities.Task;

/**
 * The abstract class for the builder
 * 
 * The class initializes build environment for the task: 
 * constructs common directories structure 
 * and unpacks zip with sources into sources folder.
 * 
 * @author vitalii
 *
 */
public abstract class AbstractBuilder implements IBuilder{
	
	public final static String IO_ERROR_AT_BUILD_INIT=
			"io error at build init";
	
	private final static Logger logger = LogManager.getLogger(
			AbstractBuilder.class.getName());
	
	public final Task task;
	
	/**
	 * Initializes build environment for the task:
	 * 	- creates output folder with the same name as this task's id
	 *  - unpacks zip with sources into <task id>/src folder
	 *  - sets <task id>/bin as output folder for compiled files
	 *  
	 *  @throws BuildException 
	 */  
	public AbstractBuilder(Task task) throws BuildException {
		this.task = task;
		try {
			makeTaskFolder();
		} catch (IOException e) {
			logger.error("could not create task folder", e);
			throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		}

		unzip(getSrcFolder());
	}
	

	public File getSrcFolder() {
		File dir = new File(getTaskFolder().getPath() + "/src");
		if (!dir.exists()) {
			dir.mkdir();
		}

		return dir;
	}
	
	private void unzip(File outDir) throws BuildException {
		InputStream fis = null;
		try {
			File archive = new File(getTask().getFullPathToZip());
			fis = new FileInputStream(archive);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		}
		ZipInputStream zis = new ZipInputStream(fis);
		byte[] buffer = new byte[4096];
		ZipEntry ze;
		try {
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					new File(outDir.getPath() + "/" + ze.getName()).mkdir();
					continue;
				}
				FileOutputStream fos = new FileOutputStream(outDir.getPath()
						+ "/" + ze.getName());
				int numBytes;
				while ((numBytes = zis.read(buffer, 0, buffer.length)) != -1)
					fos.write(buffer, 0, numBytes);
				zis.closeEntry();
				fos.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		}
	}
	
	@Override
	public File getLogFile(){
		File log = new File(getTaskFolder().getPath() + "/log");
		return log;
	}
	
	private File makeTaskFolder() throws IOException {
		File result = getTaskFolder();
		if (result.exists()) {
			Utils.rm_rf(result);
		}
		result.mkdirs();
		return result;
	}
	
	@Override
	public File getTaskFolder() {
		return new File(getTask().getId());
	}
	
	@Override
	public Task getTask() {
		return task;
	}
	
	
}
