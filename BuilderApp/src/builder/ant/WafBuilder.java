package builder.ant;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

import builder.common.AbstractBuilder;
import builder.common.BuildException;
import entities.BuildResult;
import entities.Task;

public class WafBuilder extends AbstractBuilder implements
		Callable<BuildResult> {

	public WafBuilder(Task task) throws BuildException {
		super(task);
		// TODO Auto-generated constructor stub
	}

	@Override
	public RunnableFuture<BuildResult> build() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BuildResult call() {
		// TODO Auto-generated method stub
		return null;
	}

}
