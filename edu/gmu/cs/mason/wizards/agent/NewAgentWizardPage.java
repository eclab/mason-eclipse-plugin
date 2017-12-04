package edu.gmu.cs.mason.wizards.agent;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.gmu.cs.mason.wizards.MasonWizardConstants;

public class NewAgentWizardPage extends NewTypeWizardPage {

	
	
	private final static String PAGE_NAME = "NewAgentWizardPage"; //$NON-NLS-1$


	public NewAgentWizardPage() {
		super(true, PAGE_NAME);

		setTitle(MasonWizardConstants.Text.MASON_AGENT_WIZARD_PAGE_TITLE);
		setDescription(MasonWizardConstants.Text.MASON_AGENT_WIZARD_PAGE_DESCRIPTION);
		
	}

	
	
	/**
	 * The wizard owning this page is responsible for calling this method with the
	 * current selection. The selection is used to initialize the fields of the wizard
	 * page.
	 *
	 * @param selection used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement element = getInitialJavaElement(selection);
		initContainerPage(element);
		initTypePage(element);
		doStatusUpdate();	
	}

	
	
	
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fContainerStatus,
			isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
			fTypeNameStatus,
			fModifierStatus,
			fSuperClassStatus,
			fSuperInterfacesStatus
		};

		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}


	
	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);

		doStatusUpdate();
	}


	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);

		createTypeNameControls(composite, nColumns);


		setControl(composite);

		Dialog.applyDialogFont(composite);
		// use following method to set the help method
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, "help content here");
		
		// we have too many control, so let's resize the wizard
		Point size = getShell().computeSize(MasonWizardConstants.AGENT_WIZARD_PAGE_WIDTH, MasonWizardConstants.AGENT_WIZARD_PAGE_HEIGHT);
		getShell().setSize( size );

	}

	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			setFocus();
	}


	/**
	 * create the new class of agent
	 */
	@Override
	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		boolean createConstr = true;
		
		// force to add the unimplemented methods
		boolean createInherited = true; 
		
		
		createInheritedMethods(type, createConstr, createInherited, imports,
				new SubProgressMonitor(monitor, 1));

		String serialStr = "private static final long serialVersionUID = 1;";
		
		type.createField(serialStr, null, false, null);

//		StringBuffer buf = new StringBuffer();
//		final String lineDelim = "\n"; // OK, since content is formatted
//										// afterwards
//
//		buf.append("public void step("); 
//		buf.append(imports.addImport("sim.engine.SimState")); 
//		buf.append(" state) {"); 
//		buf.append(lineDelim);
//		final String content = CodeGeneration.getMethodBodyContent(
//				type.getCompilationUnit(), type.getTypeQualifiedName('.'),
//				"step", false, "Test test = (Test) state;", lineDelim);
//		if (content != null && content.length() != 0)
//			buf.append(content);
//		buf.append(lineDelim);
//		buf.append("}");
//		type.createMethod(buf.toString(), null, false, null);

		if (monitor != null) {
			monitor.done();
		}
	}

	// we only have one interface to implement
	// so we reuse this getter
	public List<String> getSuperInterfaces() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("sim.engine.Steppable");
		return list;
	}

	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
		
		super.createType(monitor);
	}
	


}
