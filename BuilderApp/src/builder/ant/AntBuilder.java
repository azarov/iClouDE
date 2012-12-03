package builder.ant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import builder.common.AbstractBuilder;
import builder.common.BuildException;

import entities.BuildResult;
import entities.BuildStatus;
import entities.Task;

/**
 * Builds java sources with Ant. Creates {@link org.apache.tools.ant.Project}
 * from build.xml and executes compilation target
 * 
 * build.xml is located in the root project directory
 */
public class AntBuilder extends AbstractBuilder implements
		Callable<BuildResult> {
	private org.apache.tools.ant.Project p;
	private PrintStream logStream;
	private final static Logger logger = LogManager.getLogger(AntBuilder.class
			.getName());

	public AntBuilder(Task task) throws BuildException {
		super(task);

		logger.entry();
		p = antProject(new File("build.xml"));

		// Add build listener:
		try {
			logStream = new PrintStream(getLogFile());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		}

		logStream.println("message from ant proj constructor");

		DefaultLogger fileLogger = new DefaultLogger();
		// errors will be printed to output stream too
		fileLogger.setErrorPrintStream(logStream);
		fileLogger.setOutputPrintStream(logStream);
		fileLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(fileLogger);
		
		p.setProperty("proj.dir", getTaskFolder().getAbsolutePath());

		Properties props = new Properties();
		props.setProperty("src.dir", getSrcFolder().getAbsolutePath());
		props.setProperty("build.dir", getBinFolder().getAbsolutePath());
		try {
			FileOutputStream out = new FileOutputStream(getTaskFolder()
					.getPath() + "/" + "task.properties");
			props.store(out, "--- task properties ---");
			out.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		}
		// logStream.close();
		logger.exit();

	}

	private File getBinFolder() {
		File dir = new File(getTaskFolder().getPath() + "/bin");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	@Override
	public BuildResult call() {
		try {
			p.fireBuildStarted();
			p.executeTarget("compile.all");
			AntBuilder.logger.debug("build finished");
		} catch (Exception e) {
			p.fireBuildFinished(e);
			logger.error(e.getMessage(), e);
			builder.common.BuildException buildException = new BuildException(
					"", e, this);
			return buildException.constructBuildResult();
		}

		BuildResult r = new BuildResult(getTask().getId(),
				BuildStatus.SUCCESSFUL, "BUILD SUCCESSFUL");
		r.setLogs(getLogFile().toURI());
		r.setCompiledSourcesPath(getBinFolder().toURI());
		return r;
	}

	private Project antProject(File buildxml) {
		Project p = new Project();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildxml);
		p.init();
		return p;
	}

	@Override
	public RunnableFuture<BuildResult> build() {
		return new FutureTask<BuildResult>(this);
	}

}
