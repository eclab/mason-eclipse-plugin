package edu.gmu.cs.mason.wizards.project.ui;


import edu.gmu.cs.mason.codegenerator.UnitCreator;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.project.ui.wizardpage.AgentsPage;
import edu.gmu.cs.mason.wizards.project.ui.wizardpage.GUIClassPage;
import edu.gmu.cs.mason.wizards.project.ui.wizardpage.ProjectInfoPage;
import edu.gmu.cs.mason.wizards.project.ui.wizardpage.SimulationClassPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;



public class MasonProjectWizard extends Wizard implements INewWizard {

	public static final String WIZARD_NAME = "MASON Project Wizard";
	
	public static final int PAGE_WIDTH = 650;
	public static final int PAGE_HEIGHT = 570;
	
	// Our wizard pages
	private ProjectInfoPage infoPage;
	private SimulationClassPage classPage;
	private AgentsPage agentsPage;
	private GUIClassPage guiClassPage;
	
	// Our model of the project
	public ProjectInformation projectInfo;
	
	// Our creator of the project and code. 
	// The function of generating may further transfer to the be the function of model
	private UnitCreator creator;
	
	
	
	public MasonProjectWizard() {
		projectInfo = new ProjectInformation();
		this.setWindowTitle(WIZARD_NAME);
		
	

	}
	
		
	
	@Override
	public void addPages() {
	    super.addPages();	    
	    
	    //add basic info page of project
	    this.infoPage = new ProjectInfoPage("InfoPage",projectInfo);
	    addPage(this.infoPage);
	    infoPage.setWizard(this);
	    
	    // This page gather the information of SimState class
	    // The information could be the name of the class, the information of field
	    this.classPage = new SimulationClassPage("ClassPage",projectInfo);
	    addPage(this.classPage);
	    classPage.setWizard(this);

	    // This page gather the information of Agents.
	    // The information includes how the agents is scheduled, and which field it scheduled.
	    this.agentsPage = new AgentsPage("AgentsPage",projectInfo);
	    addPage(this.agentsPage);
	    agentsPage.setWizard(this);
	    
	    // The information of Display and how to portray the field and agent
	    this.guiClassPage = new GUIClassPage("GUIClassPage",projectInfo);
	    addPage(this.guiClassPage);
	    guiClassPage.setWizard(this);
	    
	}

	
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	
	// After we collect the information of the project, we start to generate code here
	@Override
	public boolean performFinish() {

		//first, dispose the the resource
		this.guiClassPage.disposeResource();
		
		

		creator = new UnitCreator(this.projectInfo);
	    creator.createMasonJavaProject();
	    creator.createPackage();
	    creator.createSimStateSrc();
	    creator.createAgentSrcs();
	    creator.createGUISrc();
	    //creator.insertTemplates();


	     
	    return true;
	}

}
	
	


