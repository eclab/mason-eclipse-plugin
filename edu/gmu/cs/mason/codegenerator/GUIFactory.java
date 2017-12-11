package edu.gmu.cs.mason.codegenerator;



import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation.PortrayalType;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

public class GUIFactory {

	private static GUIFactory instance = null;
	private ICompilationUnit cu = null;
	private ProjectInformation projectInfo;
	private String packegeName;
	private String GUIUnit;

	private GUIFactory(ICompilationUnit cu, ProjectInformation projectInfo) {
		this.cu = cu;
		this.packegeName = projectInfo.packageName;
		this.GUIUnit = projectInfo.GUIClassName;
		this.projectInfo = projectInfo;
	}

	public static GUIFactory getInstance(ICompilationUnit cu, ProjectInformation projectInfo) {
		if (instance == null) {
			instance = new GUIFactory(cu, projectInfo);
		} else {
			instance.cu = cu;
			instance.packegeName = projectInfo.packageName;
			instance.GUIUnit = projectInfo.GUIClassName;
			instance.projectInfo = projectInfo;
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	public void generateGUIFile() {
		
		String typeName = GUIUnit;
		String superClassName = "GUIState";
		
		
		Document doc;
		try {
			doc = new Document(cu.getBuffer().getContents());

			CompilationUnit unit = Coder.beginManipulateCode(doc);

			// add package
			Coder.packageDef(unit, unit.getAST(), this.packegeName);

			
			Coder.importPackage(unit, unit.getAST(), "sim.display", true);
			
			
			//add class
			TypeDeclaration classDeclaration = Coder.classDef(unit, unit.getAST(), typeName, superClassName, null);
			
			//add display and JFrame
			FieldDeclaration fieldDeclaration = Coder.addFieldDeclaration(unit.getAST(), "public", "Display2D", "display", null);
			classDeclaration.bodyDeclarations().add(0,fieldDeclaration);
			fieldDeclaration = Coder.addFieldDeclaration(unit.getAST(), "public", "JFrame", "displayFrame", null);
			classDeclaration.bodyDeclarations().add(0,fieldDeclaration);
			
			//add constructor
			String[] parameters = {"SimState","state"};
			MethodDeclaration method = Coder.constructorDef(unit.getAST(), typeName, parameters);
			String[] arguments = {"state"};
			SuperConstructorInvocation invocation = Coder.superConstructorInvocation(unit.getAST(),arguments);
			method.getBody().statements().add(invocation);
			classDeclaration.bodyDeclarations().add(method);
			

			method = Coder.constructorDef(unit.getAST(), typeName, null);
			arguments = new String[]{"new "+projectInfo.simStateClassName+"(System.currentTimeMillis())"};
			invocation = Coder.superConstructorInvocation(unit.getAST(),arguments);
			method.getBody().statements().add(invocation);
			classDeclaration.bodyDeclarations().add(method);
			
			
			//add main method
			String[] modifiers = {"public","static"};
			parameters = new String[]{"String[]","args"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "main", parameters);
			String source = "new "+ typeName + "().createController();"; 
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			
			//add getName method
			modifiers = new String[]{"public","static"};
			method = Coder.methodDef(unit.getAST(), modifiers, "String", "getName", null);
			source = "return \""+ projectInfo.simStateClassName + "\";"; 
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			// add Model Inspector
			modifiers = new String[]{"public"};
			method = Coder.methodDef(unit.getAST(), modifiers, "Object", "getSimulationInspectedObject", null);
			source = "return state;";
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			//add quit method
			modifiers = new String[]{"public"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "quit", null);
			
			// TODO use StringBuffer instead
			source = "super.quit();" +
					"if(displayFrame != null){" +
					"displayFrame.dispose();}" + 
					"displayFrame = null;" + 
					"display = null;";
			
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			// add createFieldPortrayals method
			modifiers = new String[]{"public"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "createFieldPortrayals", null);
			source = projectInfo.simStateClassName+ " " + Coder.lowerFirstLetter(projectInfo.simStateClassName) + " = (" + 
					projectInfo.simStateClassName + ")state;";
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			//add setupPortrayls method
			modifiers = new String[]{"public"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "setupPortrayals", null);
			source = "createFieldPortrayals();" +
					"display.reset();" + 
					"display.setBackdrop(Color.white);" + 
					"display.repaint();";
			
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			
			//add start and load method
			modifiers = new String[]{"public"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "start", null);
			source = "super.start();setupPortrayals();";
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			
			modifiers = new String[]{"public"};
			parameters = new String[]{"SimState","state"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "load", null);
			source = "super.load(state);setupPortrayals();";
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			
			//add init method
			modifiers = new String[]{"public"};
			parameters = new String[]{"Controller","c"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "init", parameters);
			source = "super.init(c);" +
					"display = new Display2D("+ projectInfo.displayWidth + "," +projectInfo.displayHeight +",this);" + 
					"display.setClipping(false);" + 
					"displayFrame = display.createFrame();" + 
					"displayFrame.setTitle(\""+Coder.lowerFirstLetter(projectInfo.simStateClassName)+"\");" +
					"c.registerFrame(displayFrame);" + 
					"displayFrame.setVisible(true);";
			
			Coder.addMethodBody(unit.getAST(), method, source);
			classDeclaration.bodyDeclarations().add(method);
			
			
			
			//add field portrayal
			int length = projectInfo.fieldInfoList.size();
			for(int i = 0;i<length;++i)
			{
				FieldInformation fieldInfo = projectInfo.fieldInfoList.get(i);
				if(fieldInfo.getPortrayal()!=PortrayalType.None)
				{
					addFieldPortrayal(unit,
							fieldInfo.getPortrayal().toString(),
							fieldInfo.getFieldName(), 
							fieldInfo.getPortrayalName(), classDeclaration,
							"createFieldPortrayals", projectInfo.simStateClassName);
				}
			}
			
			
			//add agent portrayal
//			length = projectInfo.agentInfoList.size();
//			for(int i = 0;i<length;++i)
//			{
//				AgentInformation agentInfo = projectInfo.agentInfoList.get(i);
//				if(agentInfo.getPortrayal()!=SimplePortrayal.None)
//				{
//					addAgentPortrayal(unit, agentInfo, "setupPortrayals");
//				}
//			}

			

			
			Coder.endManipulateCode(unit, doc);

			// save the changes
			cu.getBuffer().setContents(doc.get());
			cu.getBuffer().save(null, false);

		} catch (JavaModelException e) {
			e.printStackTrace();
			Activator.log("GUI file create error", e);
		}
		
		Coder.addNeededImports(cu);
		
	}

	private void addAgentPortrayal(CompilationUnit unit, AgentInformation agentInfo, String method) {
		
		unit.accept(new AgentClassPortrayalVisitor(agentInfo, method));		
	}

	@SuppressWarnings("unchecked")
	private void addFieldPortrayal(CompilationUnit unit,String fieldPortrayal, String fieldName, String fieldPortrayalName, TypeDeclaration classDeclaration, String method, String simState) {
		String portrayal = fieldName + "Portrayal";
		
		String initializer = "new "+ fieldPortrayal + "()";
		
		FieldDeclaration fieldDeclaration = Coder.addFieldDeclaration(unit.getAST(), "public", fieldPortrayal, portrayal, initializer);
		
		classDeclaration.bodyDeclarations().add(0, fieldDeclaration);
		
		unit.accept(new FieldPortrayalVisitor(portrayal, fieldName, fieldPortrayalName, simState, method));
		
	}
}



class FieldPortrayalVisitor extends ASTVisitor{
	
	private String portrayal;
	private String fieldName;
	private String fieldPortrayalName;
	private String simState;
	private String method;
	
	
	public FieldPortrayalVisitor(String portrayal, String fieldName, String fieldPotrayalName, String simState, String method)
	{
		this.portrayal = portrayal;
		this.fieldName = fieldName;
		if(fieldPotrayalName==null||fieldPotrayalName.isEmpty())
			this.fieldPortrayalName = this.fieldName.toUpperCase();
		else {
			this.fieldPortrayalName = fieldPotrayalName;
		}
		this.simState = simState;
		this.method = method;
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodDeclaration node) {
		String name = node.getName().toString();
		if(name.equals("init"))
		{
			String source = "display.attach("+portrayal+", \""+this.fieldPortrayalName+"\")";
			
			Expression expression = Coder.expressionDef(node.getAST(), source);
			node.getBody().statements().add(node.getAST().newExpressionStatement(expression));
		}
		
		if(name.equals(this.method))
		{
			String source = this.portrayal+".setField("+Coder.lowerFirstLetter(simState)+"."+fieldName+")";
			Expression expression = Coder.expressionDef(node.getAST(), source);
			node.getBody().statements().add(node.getAST().newExpressionStatement(expression));
		}
		
		
		
		return false;
	}
	
}



class AgentClassPortrayalVisitor extends ASTVisitor{

	private AgentInformation agentInfo;
	private String method;
	
	public AgentClassPortrayalVisitor(AgentInformation agentInfo, String method) {
		this.method = method;
		this.agentInfo = agentInfo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(MethodDeclaration node) {
		
		String name = node.getName().toString();
		if(name.equals(this.method))
		{
			Expression[] arguments = new Expression[3];
			arguments[0] = Coder.expressionDef(node.getAST(), agentInfo.getColor().red+"");
			arguments[1] = Coder.expressionDef(node.getAST(), agentInfo.getColor().green+"");
			arguments[2] = Coder.expressionDef(node.getAST(), agentInfo.getColor().blue+"");
			ClassInstanceCreation instanceCreation = Coder.classInstance(node.getAST(),"Color",arguments);
			
			
			arguments = new Expression[1];
			arguments[0] = instanceCreation;
			instanceCreation = Coder.classInstance(node.getAST(), agentInfo.getPortrayal().toString(), arguments);
			
			if(agentInfo.isMovable())
			{
				arguments = new Expression[1];
				arguments[0] = instanceCreation;
				instanceCreation = Coder.classInstance(node.getAST(), "MovablePortrayal2D", arguments);
			}
			if(agentInfo.isOriented())
			{
				arguments = new Expression[1];
				arguments[0] = instanceCreation;
				instanceCreation = Coder.classInstance(node.getAST(), "OrientedPortrayal2D", arguments);
			}
			if(agentInfo.getLabel()!=null)
			{
				arguments = new Expression[4];
				arguments[0] = instanceCreation;
				if(agentInfo.getLabel().equals("null"))
				{
					arguments[1] = Coder.expressionDef(node.getAST(), "null");
				}
				else {
					arguments[1] = Coder.expressionDef(node.getAST(), "\""+agentInfo.getLabel()+"\"");
				}
				arguments[2] = Coder.expressionDef(node.getAST(), "Color.blue");
				arguments[3] = Coder.expressionDef(node.getAST(), "true");
				instanceCreation = Coder.classInstance(node.getAST(), "LabelledPortrayal2D", arguments);
			}
			
			
			
			
			arguments = new Expression[2];
			arguments[0] = Coder.expressionDef(node.getAST(), agentInfo.getAgentName()+".class");
			arguments[1] = instanceCreation;
			String fieldPortrayal = agentInfo.getField().getFieldName()+"Portrayal";
			MethodInvocation invocation = Coder.methodInvocation(node.getAST(), fieldPortrayal,"setPortrayalForClass",arguments);
			
			node.getBody().statements().add(node.getAST().newExpressionStatement(invocation));
			
		}
		
		return false;
	}
	
	
}


