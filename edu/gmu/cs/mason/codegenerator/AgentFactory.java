package edu.gmu.cs.mason.codegenerator;


import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

public class AgentFactory {
	private static AgentFactory instance = null;
	private AgentInformation agentInfo = null;
	private ICompilationUnit cu = null;
	private TypeDeclaration classDeclaration = null;
	private MethodDeclaration stepMethodDeclaration = null;
	private String packegeName;
	private String SimStateUnit;
	//public HashMap<InsertionPoint, InsertionLocation> insertionPoints;

	
	private AgentFactory(ICompilationUnit cu, AgentInformation info,ProjectInformation projectInfo)
	{
		this.agentInfo = info;
		this.cu = cu;
		this.packegeName = info.getPackageName();
		this.SimStateUnit = projectInfo.simStateClassName;
		//this.insertionPoints = new HashMap<InsertionPoint, InsertionLocation>();
	}
	
	public static AgentFactory getInstance(ICompilationUnit cu, AgentInformation info, ProjectInformation projectInfo)
	{
		if(instance==null)
		{
			instance = new AgentFactory(cu,info,projectInfo);
		}
		else {
			instance.agentInfo = info;
			instance.cu = cu;
			instance.packegeName = info.getPackageName();
			instance.SimStateUnit = projectInfo.simStateClassName;
		}
		
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void generateAgentFile() {
		
		String typeName = agentInfo.getAgentName();
		String[] superInterfaceName = {"Steppable"};

		
		Document doc;
		try {
			doc = new Document(cu.getBuffer().getContents());
		
		
			CompilationUnit unit = Coder.beginManipulateCode(doc);
			
			
			//add package
			
			Coder.packageDef(unit, unit.getAST(), this.packegeName);
			
			//add class
			classDeclaration = Coder.classDef(unit, unit.getAST(), typeName, null, superInterfaceName);
			
			
			// add Serial Number
			Coder.addSerialNumber(unit.getAST(), classDeclaration);

			// add constructor
			MethodDeclaration method = Coder.constructorDef(unit.getAST(), typeName, null);
			classDeclaration.bodyDeclarations().add(method);
			
			// add main method
			String[] modifiers = {"public"};
			String[] parameters = new String[]{"SimState","state"};
			stepMethodDeclaration = Coder.methodDef(unit.getAST(), modifiers, "void", "step", parameters);
			String source = this.SimStateUnit;
			source += " "+ Coder.lowerFirstLetter(this.SimStateUnit);
			source += "= ("+this.SimStateUnit+")state;\n";

			
					
			Block block = Coder.statementsDef(unit.getAST(),source);
			stepMethodDeclaration.setBody(block);
			classDeclaration.bodyDeclarations().add(stepMethodDeclaration);
			
			
			Coder.endManipulateCode(unit,doc);
			
			
			Coder.addMethodComment(doc,"step","// TODO Add your code here\n");
			
			
			//save the changes
			cu.getBuffer().setContents(doc.get());
			cu.getBuffer().save(null, false);
		
		} catch (JavaModelException e) {
			e.printStackTrace();
			Activator.log("agent file create error", e);
		}
		
		Coder.addNeededImports(cu);
		/*
		try {
			searchInsertionPoint(cu.getSource());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}*/
	}
	
	/**
	 * this method is responsible of finding two insertion point in the agent file
	 * @param content
	 */
	/*private void searchInsertionPoint(String content)
	{
		// find the step method statement insertion point
		String target = new String("// TODO Add your code here");
		int offset = content.lastIndexOf(target);
		offset += (target.length()+2);	
		this.insertionPoints.put(new InsertionPoint(InsertionPoint.Point.AgentStepStatement,agentInfo.getAgentName()),
				new InsertionLocation(offset, this.cu));
		
		
		// find the field and method insertion point
		target = new String("private static final long serialVersionUID = 1;");
		offset = content.lastIndexOf(target);
		offset += target.length();
		this.insertionPoints.put(new InsertionPoint(InsertionPoint.Point.AgentClass, agentInfo.getAgentName()),
				new InsertionLocation(offset,this.cu));
	}
	
	public HashMap<InsertionPoint, InsertionLocation> getInsertionPoint()
	{
		return this.insertionPoints;
	}*/
	
}
