package builder.ant;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import org.apache.logging.log4j.message.StructuredDataMessage;

import org.apache.tools.ant.BuildEvent;

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
 * from build.xml and executes ant targets (build and possibly run)
 * 
 * Class isn't required to be Callable, it's an implementation detail
 * 
 */
public class AntBuilder extends AbstractBuilder implements
		Callable<BuildResult> {

	private org.apache.tools.ant.Project p;

	private final static Logger logger = LogManager.getLogger(AntBuilder.class
			.getName());

	private static final Marker TASK_LOG = MarkerManager.getMarker("TASK_LOG");

	private static final String LOG_MSG_TYPE = "";

	private void taskDebug(String msg) {
		logger.debug(TASK_LOG, new StructuredDataMessage(task.getId(), msg,
				LOG_MSG_TYPE));
	}

	private static void taskDebug(Task task, String msg) {
		logger.debug(TASK_LOG, new StructuredDataMessage(task.getId(), msg,
				LOG_MSG_TYPE));
	}

	private class TaskBuildListener implements
			org.apache.tools.ant.BuildListener {

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

	/**
	 * Creates org.apache.tools.ant.Project and sets required properties for it
	 * (like base folder). Creates task properties file with src & bin dirs locations
	 * which will be referenced from build.xml
	 * 
	 * @param task
	 * @throws BuildException
	 */
	public AntBuilder(Task task) throws BuildException {
		super(task);

		taskDebug(task, "Building task " + task.getId() + " using Ant");
		taskDebug(
				task,
				"Build parameters: \n" + "Main class: "
						+ task.getEntryPointPath());

		InputStream is = ClassLoader.getSystemResourceAsStream("build.xml");
		File buildxml = new File("tmp/build.xml");
		try {
			FileUtils.copyInputStreamToFile(is, buildxml);
		} catch (IOException e1) {
			logger.error("Could not extract build.xml file", e1);
		}

		p.addBuildListener(new TaskBuildListener());
		// TODO setMessageOutputLevel
		p.setBaseDir(getTaskFolder().getAbsoluteFile().getParentFile());
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

	}

	private File getBinFolder() {
		File dir = new File(getTaskFolder().getPath() + "/bin");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}
	
	/**
	 * Executes "build" and possibly "run" ant targets
	 * @return BuildResult
	 */
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
