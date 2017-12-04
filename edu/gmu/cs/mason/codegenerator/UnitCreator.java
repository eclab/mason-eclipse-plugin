package edu.gmu.cs.mason.codegenerator;


import java.util.ArrayList;
import java.util.Map;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.nature.MasonProjectNature;
import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jface.text.*;
import org.eclipse.jdt.launching.JavaRuntime;



public class UnitCreator {
	private ProjectInformation projectInfo;
	private IJavaProject jProject;
	private IPackageFragment mainFragment; // this package store the State class
	private IPackageFragmentRoot srcFolder; // this is the src folder
	private ICompilationUnit stateFile;
	private ICompilationUnit GUIFile;
	private ArrayList<ICompilationUnit> agentFileList;
	//private HashMap<InsertionPoint, InsertionLocation> insertionOffsetMap;

	
	
	public UnitCreator(ProjectInformation info)
	{
		projectInfo = info;
		agentFileList = new ArrayList<ICompilationUnit>();
		//insertionOffsetMap = new HashMap<InsertionPoint, InsertionLocation>();
	}
	
	
	public IJavaProject createMasonJavaProject()
	{
        Assert.isNotNull(projectInfo.projectName);
        Assert.isTrue(projectInfo.projectName.trim().length() > 0);
		
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectInfo.projectName);
		
		try {
			project.create(null);
			project.open(null);
	 
			//set the Java nature
			IProjectDescription description = project.getDescription();
			
			// should add both Java nature and Mason nature to it
			description.setNatureIds(new String[] { JavaCore.NATURE_ID, MasonProjectNature.NATURE_ID });
			
			//create the project
			project.setDescription(description, null);
			this.jProject = JavaCore.create(project);
	 
			
			IClasspathEntry entry = addSourceFolder();
			
			addBinFolder();
			
			//set the build path
			initBuildPath(entry);
 
			
			
		} catch (CoreException e) {
			e.printStackTrace();
			Activator.log("mason project creation error", e);
			
		}
		return jProject;
	}
	
	
	private void addBinFolder() {
		IWorkspaceRoot root= jProject.getProject().getWorkspace().getRoot();
		
		IPath path = new Path(projectInfo.projectName).makeAbsolute();
		
		if(!projectInfo.singleFolder)
		{
			path = path.append("bin");
		}
		
		if (path.segmentCount() > 1) {
			IFolder folder = root.getFolder(path);
			try {
				folder.create(IResource.FORCE|IResource.DERIVED, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
				Activator.log("Bin folder creation error", e);
			}
		}
		
		
		try {
			jProject.setOutputLocation(path, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
			Activator.log("Bin folder creation error", e);
		}
	}


	private IClasspathEntry addSourceFolder() {
		
		IWorkspaceRoot root= jProject.getProject().getWorkspace().getRoot();
	
		IPath path = new Path(projectInfo.projectName).makeAbsolute();
		if(!projectInfo.singleFolder)
		{
			path = path.append("src");
		}
		
		
		if (path.segmentCount() > 1) {
			IFolder folder = root.getFolder(path);
			try {
				folder.create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
				Activator.log("Src folder creation error", e);
			}
		}
		
		return JavaCore.newSourceEntry(path);
		
	}


	public void initBuildPath(IClasspathEntry sourceEntry)
	{
		ArrayList<IClasspathEntry> buildPath = new ArrayList<IClasspathEntry>();
		
		//add source folder
		buildPath.add(sourceEntry);
		
		//add JRE
		buildPath.add(JavaRuntime.getDefaultJREContainerEntry());
		
		//add mason path
		buildPath.add(JavaCore.newLibraryEntry(new Path(projectInfo.masonPath),null,null));

		
		IClasspathEntry[] entries= buildPath.toArray(new IClasspathEntry[buildPath.size()]);
		try {
			jProject.setRawClasspath(entries, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
			Activator.log("build path initialization error", e);
		}
	}
	
	
	public ICompilationUnit createGUISrc()
	{
		System.out.println(projectInfo.withGUI);
		if(!projectInfo.withGUI)
			return null;
		
		String unitName = projectInfo.GUIClassName+".java";
		String source = new String();

		try {
			GUIFile = mainFragment.createCompilationUnit(unitName, source, false, null);
			
			GUIFactory factory = GUIFactory.getInstance(GUIFile,projectInfo);
			factory.generateGUIFile();
			 
		} catch (JavaModelException e) {
			e.printStackTrace();
			Activator.log("GUI file creation error", e);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
			Activator.log("GUI file creation error", e);
		}
		return GUIFile;
	}
	
	
	
	
	
	public ICompilationUnit createSimStateSrc()
	{
		String unitName = projectInfo.simStateClassName+".java";
		String source = new String();

		try {
			stateFile = mainFragment.createCompilationUnit(unitName, source, false, null);
			
			SimStateFactory factory = SimStateFactory.getInstance(stateFile,projectInfo);
			factory.generateSimStateFile();
			//this.insertionOffsetMap.putAll(factory.getInsertionPoint()); 
			
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		}
		return stateFile;
	}
	


	
	
	// using default template to format the code
	public String formatCode(String code)
	{		
		// take default Eclipse formatting options

		@SuppressWarnings("unchecked")
		Map<String, String> options = DefaultCodeFormatterConstants.getEclipseDefaultSettings();

		// initialize the compiler settings to be able to format 1.5 code
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);

		// change the option to wrap each enum constant on a new line
		options.put(
			DefaultCodeFormatterConstants.FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
			DefaultCodeFormatterConstants.createAlignmentValue(
					true,
					DefaultCodeFormatterConstants.WRAP_ONE_PER_LINE,
					DefaultCodeFormatterConstants.INDENT_ON_COLUMN)
			);

		// instantiate the default code formatter with the given options
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

		// use options to format the code
		TextEdit edit = codeFormatter.format(
			CodeFormatter.K_COMPILATION_UNIT|CodeFormatter.F_INCLUDE_COMMENTS, // format a compilation unit
			code, // source to format
			0, // starting position
			code.length(), // length
			0, // initial indentation
			System.getProperty("line.separator") // line separator
		);

		IDocument document = new Document(code);
		try {
			edit.apply(document);
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		

		return document.get();
	}


	public void createPackage() {
		IProject project = jProject.getProject();

		
		
		if(projectInfo.singleFolder)
			this.srcFolder = jProject.getPackageFragmentRoot(project);
		else
			this.srcFolder = jProject.getPackageFragmentRoot(project.getFolder("src"));
		//create package fragment
		try {

			mainFragment = srcFolder.createPackageFragment(projectInfo.packageName, true, null);
			
			
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	// TODO: remove the creation of agents in the wizard, this seems to be a bad choice.
	public ArrayList<ICompilationUnit> createAgentSrcs()
	{
		ArrayList<ICompilationUnit> agentUnits = new ArrayList<ICompilationUnit>();
		if (projectInfo.agentInfoList != null)
		{
			for(int i = 0;i<projectInfo.agentInfoList.size();++i)
			{
				AgentInformation info = projectInfo.agentInfoList.get(i);
				ICompilationUnit unit = this.createAgentSrc(info.getAgentName(), info, projectInfo);
				agentUnits.add(unit);
			}
			this.agentFileList = agentUnits;
		}
		
		return agentUnits;		
	}
	
	public ICompilationUnit createAgentSrc(String agentIdentifier, AgentInformation info, ProjectInformation projectInfo)
	{
		// first make sure the package exist, if not, create it
		IPackageFragment fragment = null;
		try {
			fragment = srcFolder.createPackageFragment(info.getPackageName(), true, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		
		String unitName = agentIdentifier+".java";
		String source = new String();
		ICompilationUnit unit = null;
		try {
			unit = fragment.createCompilationUnit(unitName, source, false, null);
			AgentFactory factory = AgentFactory.getInstance(unit,info, projectInfo);
			factory.generateAgentFile();
			//this.insertionOffsetMap.putAll(factory.getInsertionPoint());
			 
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedTreeException e) {
			e.printStackTrace();
		}
		return unit;
	}

	/**
	 * Insert the selected template into the code file
	 */
	/*public void insertTemplates()
	{
		// FIXME currently, we only have one insertion point
		for(int i = 0;i<projectInfo.agentInfoList.size();++i)
		{
			AgentInformation info = projectInfo.agentInfoList.get(i);
			for(int j = 0;j<info.insertionList.size();++j)
			{
				TemplateInsertion insertion = info.insertionList.get(j);
				Template template = insertion.template;
				InsertionLocation loc = this.insertionOffsetMap.get(insertion.insertionPoint);
				Document doc = null;
				try {
					doc = new Document(loc.cu.getBuffer().getContents());
					MasonTemplateWizardProposal proposal = new MasonTemplateWizardProposal(doc, loc.offset, template);
					proposal.apply();
					String source = doc.get();
					source = formatCode(source);
					loc.cu.getBuffer().setContents(source);
					loc.cu.getBuffer().save(null, true);
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
				
			}
			
		}		
	}*/
	
}
