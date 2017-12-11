package edu.gmu.cs.mason.codegenerator;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.model.PropertyInformation;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.DesStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.DomainStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.HiddenStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.NameStatus;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Dimension;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Type;

public class SimStateFactory
{

	private static final String PUBLIC_MODIFIER = "public";
	
	
	private static SimStateFactory instance = null;
	private ProjectInformation projectInfo = null;
	private ICompilationUnit cu = null;
	private TypeDeclaration classDeclaration = null;
	private MethodDeclaration startMethod = null;
	private MethodDeclaration createFieldMethod = null;
	// public HashMap<InsertionPoint, InsertionLocation> insertionPoints;

	private SimStateFactory(ICompilationUnit cu, ProjectInformation projectInfo)
	{
		this.projectInfo = projectInfo;
		this.cu = cu;
		// this.insertionPoints = new HashMap<InsertionPoint,
		// InsertionLocation>();
	}

	public static SimStateFactory getInstance(ICompilationUnit cu, ProjectInformation projectInfo)
	{
		if (instance == null)
		{
			instance = new SimStateFactory(cu, projectInfo);
		}
		else
		{
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
		try
		{
			doc = new Document(cu.getBuffer().getContents());

			CompilationUnit unit = Coder.beginManipulateCode(doc);

			// add package
			Coder.packageDef(unit, unit.getAST(), projectInfo.packageName);

			// add class
			classDeclaration = Coder.classDef(unit, unit.getAST(), typeName, superClassName, null);

			// add serial number
			Coder.addSerialNumber(unit.getAST(), classDeclaration);

			// add constructor
			String[] parameters = { "long", "seed" };
			MethodDeclaration method = Coder.constructorDef(unit.getAST(), typeName, parameters);
			String[] arguments = { "seed" };
			SuperConstructorInvocation invocation = Coder.superConstructorInvocation(unit.getAST(), arguments);
			method.getBody().statements().add(invocation);
			classDeclaration.bodyDeclarations().add(method);

			// add main method
			String[] modifiers = { "static", "public" };
			parameters = new String[] { "String[]", "args" };
			method = Coder.methodDef(unit.getAST(), modifiers, "void", "main", parameters);
			String source = "doLoop(" + typeName + ".class,args);System.exit(0);";
			Block block = Coder.statementsDef(unit.getAST(), source);
			method.setBody(block);
			classDeclaration.bodyDeclarations().add(method);

			// add create field method
			modifiers = new String[] { "public" };
			createFieldMethod = Coder.methodDef(unit.getAST(), modifiers, "void", "createField", null);
			classDeclaration.bodyDeclarations().add(createFieldMethod);

			// add start method
			modifiers = new String[] { "public" };
			startMethod = Coder.methodDef(unit.getAST(), modifiers, "void", "start", null);
			SuperMethodInvocation methodInvocation = Coder.superMethodInvocation(unit.getAST(), "start", null);
			startMethod.getBody().statements().add(unit.getAST().newExpressionStatement(methodInvocation));

			MethodInvocation methodInvocation2 = Coder.methodInvocation(unit.getAST(), null, "createField", null);
			startMethod.getBody().statements().add(unit.getAST().newExpressionStatement(methodInvocation2));
			classDeclaration.bodyDeclarations().add(startMethod);

			// add fields
			for (int i = 0; i < projectInfo.fieldInfoList.size(); ++i)
			{
				FieldInformation fieldInfo = projectInfo.fieldInfoList.get(i);
				this.addSimulationField(unit.getAST(), classDeclaration, createFieldMethod, fieldInfo);
			}

			// add Properties
			for (int i = 0; i < projectInfo.propertiesInfoList.size(); ++i)
			{
				PropertyInformation propertyInfo = projectInfo.propertiesInfoList.get(i);
				addProperty(unit.getAST(), classDeclaration, propertyInfo);
			}

			// FIXME may need sort another time to deal with the insertion of
			// the template code
			// try{
			// Collections.sort(classDeclaration.bodyDeclarations(), new
			// StatementComparator());
			// }
			// catch(Exception e)
			// {
			// e.printStackTrace();
			// }

			Coder.endManipulateCode(unit, doc);

			// save the changes
			cu.getBuffer().setContents(doc.get());
			cu.getBuffer().save(null, false);
		} catch (JavaModelException e)
		{
			e.printStackTrace();
			Activator.log("SimState file create error", e);
		}

		Coder.addNeededImports(cu);

		// search template insertion point in SimState file
		/*
		 * try { searchInsertionPoint(cu.getSource()); } catch
		 * (JavaModelException e) { e.printStackTrace(); }
		 */
	}

	@SuppressWarnings("unchecked")
	private void addProperty(AST ast, TypeDeclaration classDeclaration, PropertyInformation propertyInfo)
	{
		// add property to the class
		String propertyType = propertyInfo.getPropertyType().toString();
		PrimitiveType type = ast.newPrimitiveType(PrimitiveType.toCode(propertyType));
		
		String propertyVar = propertyInfo.getPropertyVarName();
		String propertyVarUpperCase = capitalizeFirstLetter(propertyVar);
		FieldDeclaration propertyDeclaration = Coder.addFieldDeclaration(ast, PUBLIC_MODIFIER, 
				type, propertyVar, null); 
		// alway add to the first position
		classDeclaration.bodyDeclarations().add(1, propertyDeclaration);
		
		// add related methods
		// add getter and setter
		String[] modifiers = new String[] { "public" };
		MethodDeclaration getterMethod = Coder.methodDef(ast, modifiers, propertyType, "get" + propertyVarUpperCase, null);
		String source = "return " + propertyVar + ";";
		Block block = Coder.statementsDef(ast, source);
		getterMethod.setBody(block);
		classDeclaration.bodyDeclarations().add(getterMethod);
		
		String[] parameters = new String[] { propertyType, "value" };
		MethodDeclaration setterMethod = Coder.methodDef(ast, modifiers, "void", "set" + propertyVarUpperCase, parameters);
		source = propertyVar + "= value;";
		block = Coder.statementsDef(ast, source);
		setterMethod.setBody(block);
		classDeclaration.bodyDeclarations().add(setterMethod);

		if(propertyInfo.getHiddenStatus() != HiddenStatus.NONE)
		{
			MethodDeclaration hiddenMethod = Coder.methodDef(ast, modifiers, "boolean", "hide" + propertyVarUpperCase, null);
			source = "return " + propertyInfo.getHiddenStatusString() + ";";
			block = Coder.statementsDef(ast, source);
			hiddenMethod.setBody(block);
			classDeclaration.bodyDeclarations().add(hiddenMethod);
		}
		
		if(propertyInfo.getNameStatus() != NameStatus.NONE)
		{
			MethodDeclaration nameMethod = Coder.methodDef(ast, modifiers, "String", "name" + propertyVarUpperCase, null);
			source = "return \"" + propertyInfo.getName() + "\";";
			block = Coder.statementsDef(ast, source);
			nameMethod.setBody(block);
			classDeclaration.bodyDeclarations().add(nameMethod);
		}
		
		if(propertyInfo.getDesStatus() != DesStatus.NONE)
		{
			MethodDeclaration desMethod = Coder.methodDef(ast, modifiers, "String", "des" + propertyVarUpperCase, null);
			source = "return \"" + propertyInfo.getDescription() + "\";";
			block = Coder.statementsDef(ast, source);
			desMethod.setBody(block);
			classDeclaration.bodyDeclarations().add(desMethod);
		}
		
		if(propertyInfo.getDomainStatus() == DomainStatus.SLIDER)
		{
			MethodDeclaration domainMethod = Coder.methodDef(ast, modifiers, "Object", "dom" + propertyVarUpperCase, null);
			source  = "return new sim.util.Interval(";
			if (!propertyType.equals("double"))
				source +=  "(long)" + ((long)propertyInfo.getMinValue()) + ", (long)" + ((long)propertyInfo.getMaxValue());
			else
				source += propertyInfo.getMinValue() + ", " + propertyInfo.getMaxValue();
			source += ");";
			
			block = Coder.statementsDef(ast, source);
			domainMethod.setBody(block);
			classDeclaration.bodyDeclarations().add(domainMethod);
		}
		else if(propertyInfo.getDomainStatus() == DomainStatus.MENU)
		{
			MethodDeclaration domainMethod = Coder.methodDef(ast, modifiers, "Object", "dom" + propertyVarUpperCase, null);
			source = "return new String[] {" + propertyInfo.getMenuOptionsStringWithQuotes() + "};";
			block = Coder.statementsDef(ast, source);
			domainMethod.setBody(block);
			classDeclaration.bodyDeclarations().add(domainMethod);
		}
		
		
		
	}
	
	public String capitalizeFirstLetter(String original) {
	    if (original == null || original.length() == 0) {
	        return original;
	    }
	    return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	@SuppressWarnings("unchecked")
	private void addSimulationField(AST ast, TypeDeclaration classDeclaration, MethodDeclaration methodDeclaration,
			FieldInformation fieldInfo)
	{
		// add field to the class
		String fieldType = fieldInfo.getFieldType().toString() + fieldInfo.getDimension().toString();
		System.out.println(fieldType);
		FieldDeclaration fieldDeclaration = Coder.addFieldDeclaration(ast, "public", fieldType,
				fieldInfo.getFieldName(), null);
		classDeclaration.bodyDeclarations().add(1, fieldDeclaration);

		// FIXME add initialization
		String source = new String();
		source = "this." + fieldInfo.getFieldName() + "= new " + fieldInfo.getFieldType().toString()
				+ fieldInfo.getDimension().toString() + "(";
		if (fieldInfo.getFieldType() == Type.Continuous)
		{
			source += (fieldInfo.getDiscretization() + ",");
		}
		source += (fieldInfo.getWidthStr() + "," + fieldInfo.getHeightStr());
		if (fieldInfo.getDimension() == Dimension.threeD)
		{
			source += ("," + fieldInfo.getLengthStr());
		}
		source += ");";

		Block block = Coder.statementsDef(ast, source);
		for (int i = 0; i < block.statements().size(); ++i)
		{
			Statement statement = (Statement) block.statements().get(i);

			// remove the node from the parent, to make sure it can be add to
			// the body of the method
			statement.delete();
			methodDeclaration.getBody().statements().add(0, statement); // add to the front
		}
	}

	public void addSimulationField(FieldInformation fieldInfo)
	{
		Document doc;
		try
		{
			doc = new Document(cu.getBuffer().getContents());
			CompilationUnit unit = Coder.beginManipulateCode(doc);

			this.addSimulationField(unit.getAST(), this.classDeclaration, this.startMethod, fieldInfo);

			Coder.endManipulateCode(unit, doc);

			// save the changes
			cu.getBuffer().setContents(doc.get());
			cu.getBuffer().save(null, false);
		} catch (JavaModelException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * search the content of the SimState file to identify the insertion point
	 * 
	 * @param content
	 */
	/*
	 * private void searchInsertionPoint(String content) {
	 * 
	 * // find the start method statement insertion point // we first find the
	 * "super.start();" string, and we find the last semicolon after this string
	 * final String target = new String("super.start();"); int offset =
	 * content.lastIndexOf(target); offset += target.length(); String secondStr
	 * = content.substring(offset);
	 * 
	 * //we are actually insert after the semicolon, so we plus 1 here int
	 * offset2 = secondStr.lastIndexOf(";")+1;
	 * 
	 * //this.insertionPoints.put(new
	 * InsertionPoint(InsertionPoint.Point.SimStateStart,"SimState"), // new
	 * InsertionLocation(offset+offset2+1, this.cu));
	 * 
	 * }
	 */

	/*
	 * public Map<InsertionPoint, InsertionLocation> getInsertionPoint() {
	 * return this.insertionPoints; }
	 * 
	 */

}
