package edu.gmu.cs.mason.wizards.agent;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.wizards.MasonWizardConstants;
import edu.gmu.cs.mason.wizards.WorkBenchRunnable;

public class AgentWizard extends Wizard implements INewWizard {

	private NewAgentWizardPage agentPage;
    private boolean openEditorOnceFinish;

	
	
	private IWorkbench workBench;
	private IStructuredSelection selection;

	public AgentWizard() {
		this(null, true);
	}
	
	public AgentWizard(NewAgentWizardPage page, boolean openEditorOnFinish) {
		setNeedsProgressMonitor(true);
		// TODO need to add a ImageDescriptor, use setDefaultPageImageDescriptor method
		// setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(MasonWizardConstants.Text.MASON_AGENT_WIZARD_TITLE);
		
		agentPage = page;
		openEditorOnceFinish = openEditorOnFinish;
	}

	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		workBench = workbench;
		selection = currentSelection;
	}

	public void addPages() {
		super.addPages();
		if (agentPage == null) {
			agentPage = new NewAgentWizardPage();
			agentPage.setWizard(this);
			agentPage.init(getSelection());
		}
		addPage(agentPage);
	}

	
	
	

	protected void openResource(final IFile resource) {
		final IWorkbenchPage activePage = getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		if (activePage != null) {
			final Display display = getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							IDE.openEditor(activePage, resource, true);
						} catch (PartInitException e) {
							Activator.log("cannot open the resource", e);
						}
					}
				});
			}
		}
	}

	/**
	 * Create the new agent file
	 * 
	 * @param monitor the progress monitor
	 * @throws InterruptedException when the operation is cancelled
	 * @throws CoreException if the element cannot be created
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException
	{
		agentPage.createType(monitor); // use the full progress monitor
	}

	/**
	 * Returns the scheduling rule for creating the element.
	 * @return returns the scheduling rule
	 */
	protected ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
	}



	public IJavaElement getCreatedElement()
	{
		return agentPage.getCreatedType();
	}


	public boolean onPerformFinish() {
		
		IWorkspaceRunnable op = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
				try {
					finishPage(monitor);
				} catch (InterruptedException e) {
					throw new OperationCanceledException(e.getMessage());
				}
			}
		};
		try {
			ISchedulingRule rule = null;
			Job job = Job.getJobManager().currentJob();
			if (job != null)
				rule = job.getRule();
			IRunnableWithProgress runnable = null;
			if (rule != null)
				runnable = new WorkBenchRunnable(op, rule, true);
			else
				runnable = new WorkBenchRunnable(op, getSchedulingRule());
			getContainer().run(true, true, runnable);
		} catch (InvocationTargetException e) {
			Activator.log("cannot create the agent class", e);
			return false;
		} catch  (InterruptedException e) {
			return false;
		}
		return true;
	}

	
	@Override
	public boolean performFinish() {
		boolean res = this.onPerformFinish();
		
		// highlight the resource and open in the editor
		if (res) {
			IResource resource = agentPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (openEditorOnceFinish) {
					openResource((IFile) resource);
				}
			}
		}
		return res;
	}
	
	
	
	
	public IStructuredSelection getSelection() {
		return this.selection;
	}

	public IWorkbench getWorkbench() {
		return this.workBench;
	}

	// highlight the create resource in all windows
	protected void selectAndReveal(IResource newResource) {
		BasicNewResourceWizard.selectAndReveal(newResource, workBench.getActiveWorkbenchWindow());
	}

	
}
