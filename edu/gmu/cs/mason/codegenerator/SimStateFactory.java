package edu.gmu.cs.mason.codegenerator;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Dimension;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Type;

public class SimStateFactory {
	
	
	private static SimStateFactory instance = null;
	private ProjectInformation projectInfo = null;
	private ICompilationUnit cu = null;
	private TypeDeclaration classDeclaration = null;
	private MethodDeclaration startMethod = null;
	private MethodDeclaration createFieldMethod = null;
	//public HashMap<InsertionPoint, InsertionLocation> insertionPoints;

	
	private SimStateFactory(ICompilationUnit cu, ProjectInformation projectInfo)
	{
		this.projectInfo = projectInfo;
		this.cu = cu;
		//this.insertionPoints = new HashMap<InsertionPoint, InsertionLocation>();
	}
	
	public static SimStateFactory getInstance(ICompilationUnit cu, ProjectInformation projectInfo)
	{
		if(instance==null)
		{
			instance = new SimStateFactory(cu,projectInfo);
		}
		else {
			instance.projectInfo = projectInfo;
			instance.cu = cu;
		}
		
		return instance;
	}
	
	
	@SuppressWarnings("unchecked")
	public void generateSimStateFile()
	{
		String typeName = projectInfo.simStateClassName;
		String superClassName = "SimState";

		
		Document doc;
		try {
			doc = new Document(cu.getBuffer().getContents());
		
		
			CompilationUnit unit = Coder.beginManipulateCode(doc);
			
		
			//add package
			Coder.packageDef(unit, unit.getAST(), projectInfo.packageName);
			
			//add class
			classDeclaration = Coder.classDef(unit, unit.getAST(), typeName, superClassName, null);
			
			//add serial number
			Coder.addSerialNumber(unit.getAST(), classDeclaration);
			
			//add constructor
			String[] parameters = {"long","seed"};
			MethodDeclaration method = Coder.constructorDef(unit.getAST(), typeName, parameters);
			String[] arguments = {"seed"};
			SuperConstructorInvocation invocation = Coder.superConstructorInvocation(unit.getAST(),arguments);
			method.getBody().statements().add(invocation);
			classDeclaration.bodyDeclarations().add(method);
			
			
			//add main method
			String[] modifiers = {"static","public"};
			parameters = new String[]{"String[]","args"};
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "main", parameters);
			String source = "doLoop("+typeName+".class,args);System.exit(0);";
			Block block = Coder.statementsDef(unit.getAST(),source);
			method.setBody(block);
			classDeclaration.bodyDeclarations().add(method);
			
						
			// add create field method
			modifiers = new String[]{"public"};
			createFieldMethod = Coder.methodDef(unit.getAST(), modifiers, "void", "createField", null);
			classDeclaration.bodyDeclarations().add(createFieldMethod);
			
			//add start method
			modifiers = new String[]{"public"};
			startMethod = Coder.methodDef(unit.getAST(), modifiers, "void", "start", null);
			SuperMethodInvocation methodInvocation = Coder.superMethodInvocation(unit.getAST(),"start",null);
			startMethod.getBody().statements().add(unit.getAST().newExpressionStatement(methodInvocation));
			
			MethodInvocation methodInvocation2 = Coder.methodInvocation(unit.getAST(), null, "createField", null);
			startMethod.getBody().statements().add(unit.getAST().newExpressionStatement(methodInvocation2));
			classDeclaration.bodyDeclarations().add(startMethod);
			
			
			//add fields
			for(int i = 0;i<projectInfo.fieldInfoList.size();++i)
			{
				FieldInformation fieldInfo = projectInfo.fieldInfoList.get(i);
				this.addSimulationField(unit.getAST(),classDeclaration, createFieldMethod, fieldInfo);
			}

			
			// FIXME may need sort another time to deal with the insertion of the template code
//			try{
//			Collections.sort(classDeclaration.bodyDeclarations(), new StatementComparator());
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}

			Coder.endManipulateCode(unit,doc);

			//save the changes
			cu.getBuffer().setContents(doc.get());
			cu.getBuffer().save(null, false);
		} catch (JavaModelException e) {
			e.printStackTrace();
			Activator.log("SimState file create error", e);
		}
		
		Coder.addNeededImports(cu);
		
		// search template insertion point in SimState file
		/*try {
			searchInsertionPoint(cu.getSource());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}*/
	}

	@SuppressWarnings("unchecked")
	private void addSimulationField(AST ast, TypeDeclaration classDeclaration, MethodDeclaration methodDeclaration,FieldInformation fieldInfo) {
		//add field to the class		
		String fieldType = fieldInfo.getFieldType().toString()+fieldInfo.getDimension().toString();
		FieldDeclaration fieldDeclaration = Coder.addFieldDeclaration(ast, "public", fieldType, fieldInfo.getFieldName(), null);
		classDeclaration.bodyDeclarations().add(1,fieldDeclaration);
		
		
		
		
		//FIXME add initialization
		String source = new String();
		source = "this."+fieldInfo.getFieldName()+"= new "+fieldInfo.getFieldType().toString()+fieldInfo.getDimension().toString()+"(";
		if(fieldInfo.getFieldType()==Type.Continuous)
		{
			source += (fieldInfo.getDiscretization()+",");
		}
		source += (fieldInfo.getWidthStr()+","+fieldInfo.getHeightStr());
		if(fieldInfo.getDimension()==Dimension.threeD)
		{
			source += (","+fieldInfo.getLengthStr());
		}
		source += ");";
		
		Block block = Coder.statementsDef(ast, source);
		for(int i = 0;i<block.statements().size();++i)
		{
			Statement statement = (Statement) block.statements().get(i);
			
			// remove the node from the parent, to make sure it can be add to the body of the method
			statement.delete();
			methodDeclaration.getBody().statements().add(0,statement); // add to the front
		}

		
		
		
	}

	
	public void addSimulationField(FieldInformation fieldInfo)
	{
		Document doc;
		try {
			doc = new Document(cu.getBuffer().getContents());
			CompilationUnit unit = Coder.beginManipulateCode(doc);
			
			this.addSimulationField(unit.getAST(),this.classDeclaration, this.startMethod, fieldInfo);
			
			
			Coder.endManipulateCode(unit,doc);

			//save the changes
			cu.getBuffer().setContents(doc.get());
			cu.getBuffer().save(null, false);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	
		
	}
	
	/**
	 * search the content of the SimState file to identify the insertion point
	 * @param content
	 */
	/*
	private void searchInsertionPoint(String content) {
		
		// find the start method statement insertion point
		// we first find the "super.start();" string, and we find the last semicolon after this string
		final String target = new String("super.start();");
		int offset = content.lastIndexOf(target);
		offset += target.length();	
		String secondStr = content.substring(offset);
		
		//we are actually insert after the semicolon, so we plus 1 here
		int offset2 = secondStr.lastIndexOf(";")+1;
		
		//this.insertionPoints.put(new InsertionPoint(InsertionPoint.Point.SimStateStart,"SimState"),
		//		new InsertionLocation(offset+offset2+1, this.cu));
		
	}
	*/
	
	/*
	public Map<InsertionPoint, InsertionLocation> getInsertionPoint() {
		return this.insertionPoints;
	}

	*/
	
}
