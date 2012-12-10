package builder.ant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.status.StatusLogger;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import builder.common.AbstractBuilder;
import builder.common.BuildException;

import entities.BuildResult;
import entities.BuildStatus;
import entities.OperationType;
import entities.Task;

/**
 * Builds java sources with Ant. Creates {@link org.apache.tools.ant.Project}
 * from build.xml and executes compilation target
 * 
 */
public class AntBuilder extends AbstractBuilder implements
		Callable<BuildResult> {

	private org.apache.tools.ant.Project p;

	// private PrintStream logStream;

	private final static Logger logger = LogManager.getLogger(AntBuilder.class
			.getName());
	// StatusLogger.getLogger();
	private static final Marker TASK_LOG = MarkerManager.getMarker("TASK_LOG");

	// log messages types
	// private static final String BUILD_LOG_MSG = "Build";
	// private static final String RUN_LOG_MSG = "Run";
	private static final String LOG_MSG_TYPE = "";

	// private static Message buildMsg(Task task, String msg) {
	// return new StructuredDataMessage(task.getId(), msg, LOG_MSG_TYPE);
	// }

	private void taskDebug(String msg) {
		logger.debug(TASK_LOG, new StructuredDataMessage(task.getId(), msg,
				LOG_MSG_TYPE));
	}

	private static void taskDebug(Task task, String msg) {
		logger.debug(TASK_LOG, new StructuredDataMessage(task.getId(), msg,
				LOG_MSG_TYPE));
	}

	private class AntBuildListener implements BuildListener {

		@Override
		public void taskStarted(BuildEvent event) {
		}

		@Override
		public void taskFinished(BuildEvent event) {
		}

		@Override
		public void targetStarted(BuildEvent event) {
		}

		@Override
		public void targetFinished(BuildEvent event) {
		}

		@Override
		public void messageLogged(BuildEvent event) {
			taskDebug(event.getMessage());
		}

		@Override
		public void buildStarted(BuildEvent event) {
			taskDebug("Ant build started");
		}

		@Override
		public void buildFinished(BuildEvent event) {
			taskDebug("Ant build finished");
			// TODO can't see this in the log
		}
	};

	public AntBuilder(Task task) throws BuildException {
		super(task);

		taskDebug(task, "Building task " + task.getId() + " using Ant");
		taskDebug(
				task,
				"Build parameters: \n" + "Main class: "
						+ task.getEntryPointPath());
		// taskDebug(msg)

		 InputStream is = ClassLoader.getSystemResourceAsStream("build.xml");
		 File buildxml = new File("toDelete.xml");
		 try {
			FileUtils.copyInputStreamToFile(is, buildxml);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//	URL buildXml = Thread.currentThread().getContextClassLoader()
				//.getResource("/build.xml");
		//try {
			p = antProject(buildxml);
		/*} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e); // should be msg to task log
			throw new BuildException("Problem with build.xml URI", e, this);
		}*/

		// Add build listener:
		// try {
		// logStream = new PrintStream(getLogFile());
		// } catch (FileNotFoundException e) {
		// logger.error(e.getMessage(), e);
		// throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		// }

		// logStream.println("message from ant proj constructor");

		// DefaultLogger fileLogger = new DefaultLogger();
		// // errors will be printed to output stream too
		// fileLogger.setErrorPrintStream(logStream);
		// fileLogger.setOutputPrintStream(logStream);
		// fileLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(new AntBuildListener());
		// TODO setMessageOutputLevel

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
			logger.error(e.getMessage(), e); // should be msg to task log
			throw new BuildException(IO_ERROR_AT_BUILD_INIT, e, this);
		}
		// logStream.close();

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
			if (task.getOperation() == OperationType.BUILD
					|| task.getOperation() == OperationType.BUILD_AND_RUN) {
				p.executeTarget("compile.all");
			}
			if (task.getOperation() == OperationType.BUILD_AND_RUN) {
				p.setProperty("work.dir", getBinFolder().getPath());
				p.setProperty("classname", task.getEntryPointPath());
				p.executeTarget("run-single");
			}
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
