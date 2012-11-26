package builder.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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

import entities.Task;


public abstract class AbstractBuilder implements IBuilder{
	
	public final static String IO_ERROR_AT_BUILD_INIT=
			"io error at build init";
	
	private final static Logger logger = LogManager.getLogger(
			AbstractBuilder.class.getName());
	
	private Task task;
	
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
			fis = new FileInputStream(getTask().getFullPathToZip() + "");
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
			rm_rf(result);
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
	
	private void rm_rf(File dir) throws IOException {
		Path p = Paths.get(dir.toURI());
		Files.walkFileTree(p, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				if (exc == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw exc;
				}
			}

		});
	}
}
