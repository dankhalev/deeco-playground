package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.executor.ExecutionListener;
import cz.cuni.mff.d3s.deeco.executor.Executor;
import cz.cuni.mff.d3s.deeco.model.runtime.api.Trigger;
import cz.cuni.mff.d3s.deeco.task.Task;

/**
 * An executor that does nothing. Not a single thing.
 *
 * @author Danylo Khalyeyev
 */
class DoNothingExecutor implements Executor {

    @Override
    public void execute(Task task, Trigger trigger) {

    }

    @Override
    public void setExecutionListener(ExecutionListener executionListener) {

    }
}
