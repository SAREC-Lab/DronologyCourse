package edu.nd.dronology.ui.cc.main.runnable;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import net.mv.logging.ILogger;
import net.mv.logging.LoggerProvider;

public abstract class AbstractUIRunnable implements IRunnableWithProgress {

	private static final ILogger LOGGER = LoggerProvider.getLogger(AbstractUIRunnable.class);
	private static final long SLEEP_TIME = 500;

	private int numTasks;
	protected IProgressMonitor monitor;
	private int worked = 0;
	protected String task1;
	protected String task2;
	protected String task3;
	protected String task4;
	protected String task5;

	public AbstractUIRunnable(int numTasks) {
		this.numTasks = numTasks;
		task1 = task2 = task3 = task4 = task5 = null;
	}

	@Override
	public void run(IProgressMonitor monitor) {
		this.monitor = monitor;
		monitor.beginTask("Working...", numTasks);
		try {
			Thread.sleep(SLEEP_TIME);
			worked = 1;

			doWork();

		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	protected void doWork() throws InterruptedException {
		if (task1 != null) {
			updateTask(task1);
			doTask1();
		}
		if (task2 != null) {
			updateTask(task2);
			doTask2();
		}
		if (task3 != null) {
			updateTask(task3);
			doTask3();
		}
		if (task4 != null) {
			updateTask(task4);
			doTask4();
		}
		if (task5 != null) {
			updateTask(task5);
			doTask5();
		}
	}

	/**
	 * Override me and do some work!...
	 */
	protected void doTask5() {
	}

	/**
	 * Override me and do some work!...
	 */
	protected void doTask4() {
	}

	/**
	 * Override me and do some work!...
	 */
	protected void doTask3() {
	}

	/**
	 * Override me and do some work!...
	 */
	protected void doTask2() {
	}

	/**
	 * Implement me and do some work!...
	 */
	protected void doTask1() {
	}

	protected void updateTask(String taskName) throws InterruptedException {
		worked++;
		monitor.setTaskName(taskName);
		monitor.worked(1);
		Thread.sleep(SLEEP_TIME);
	}
	
	protected void updateTask(String taskName, int sleeptime) throws InterruptedException {
		worked++;
		monitor.setTaskName(taskName);
		monitor.worked(1);
		Thread.sleep(sleeptime);
	}

	public void runRunnable() {
		try {
			new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, this);
		} catch (InvocationTargetException | InterruptedException e) {
			LOGGER.error(e);
		}
	}
	


}
