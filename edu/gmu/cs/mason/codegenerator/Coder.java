package edu.gmu.cs.mason.codegenerator;


import java.util.Comparator;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Coder {

	
	public static void packageDef(CompilationUnit unit, AST ast, String packageName)
	{
		PackageDeclaration packageDeclaration = ast.newPackageDeclaration();
		Name name = ast.newName(packageName);
		packageDeclaration.setName(name);
		unit.setPackage(packageDeclaration);
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static TypeDeclaration classDef(CompilationUnit unit, AST ast, String unitName, String superClasses, String[] interfaces)
	{
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setInterface(false);
		type.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		type.setName(ast.newSimpleName(unitName));
		
		if(superClasses!=null)
		{
			Type superclassType = ast.newSimpleType(ast.newSimpleName(superClasses));
			type.setSuperclassType(superclassType);
		}
		
		if(interfaces!=null&&interfaces.length!=0)
		{
			for(int i = 0;i<interfaces.length;++i)
			{
				Type superInterfaceType = ast.newSimpleType(ast.newSimpleName(interfaces[i]));
				type.superInterfaceTypes().add(superInterfaceType);
			}
		}
		
		unit.types().add(type);
		return type;
	}
	
	public static CompilationUnit beginManipulateCode(Document doc) {
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(doc.get().toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		
		//parse classDeclaration and startMethod here
		
		
		// start record of the modifications
		unit.recordModifications();
		
		return unit;
	}

	public static void endManipulateCode(CompilationUnit unit, Document doc) {
		TextEdit edits = unit.rewrite(doc, null);
		try {
			edits.apply(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} 		
	}

	@SuppressWarnings("unchecked")
	public static MethodDeclaration constructorDef(AST ast, String typeName, String[] parameters) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(true);
		methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		methodDeclaration.setName(ast.newSimpleName(typeName));
		
		if(parameters!=null&&parameters.length!=0)
		{
			for(int i = 0;i<parameters.length;i+=2)
			{
				SingleVariableDeclaration variableDeclaration = singleVariableDef(ast, parameters[i], parameters[i+1]);
				methodDeclaration.parameters().add(variableDeclaration);
			}
			
		}
		
		
		Block block = ast.newBlock();
		methodDeclaration.setBody(block);
		
		
		return methodDeclaration;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static FieldDeclaration addFieldDeclaration(AST ast,String modifier, String fieldType, String fieldName, String initializer)
	{
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(fieldName));
		
		if(initializer!=null)
		{
			fragment.setInitializer(Coder.expressionDef(ast, initializer));
		}
		
		
		FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(fragment);
		if(modifier.equals("public"))
		{
			fieldDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		}
		else if(modifier.equals("private"))
		{
			fieldDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		}
		fieldDeclaration.setType(ast.newSimpleType(ast.newSimpleName(fieldType)));
		
		
		return fieldDeclaration;
	}
	
	
	
	public static SingleVariableDeclaration singleVariableDef(AST ast, String typeStr, String identifier)
	{
		SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
		if(typeStr.endsWith("[]"))
		{
			typeStr = typeStr.substring(0, typeStr.length()-2);
			Type type = null;
			if(null==PrimitiveType.toCode(typeStr))
			{
				type = ast.newSimpleType(ast.newSimpleName(typeStr));
			}
			else {
				type = ast.newPrimitiveType(PrimitiveType.toCode(typeStr));
			}
			variableDeclaration.setType(ast.newArrayType(type));
		}
		else {
			Type type = null;
			if(null==PrimitiveType.toCode(typeStr))
			{
				type = ast.newSimpleType(ast.newSimpleName(typeStr));
			}
			else {
				type = ast.newPrimitiveType(PrimitiveType.toCode(typeStr));
			}
			variableDeclaration.setType(type);
		}

		variableDeclaration.setName(ast.newSimpleName(identifier));

		
		return variableDeclaration;
	}

	//this may take a little time
	public static void addNeededImports(ICompilationUnit cu) {
		

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		IWorkbenchSite site = page.getActivePart().getSite();
		
		OrganizeImportsAction oia = new OrganizeImportsAction(site);
		
		oia.run(cu);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static MethodDeclaration methodDef(AST ast, String[] modifiers, String returnType, String name, String[] parameters) {
		MethodDeclaration methodDeclaration = ast.newMethodDeclaration();
		methodDeclaration.setConstructor(false);

		for(int i = 0;i<modifiers.length;++i)
		{
			methodDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.toKeyword(modifiers[i])));
		}
		
		methodDeclaration.setName(ast.newSimpleName(name));
		if(null==PrimitiveType.toCode(returnType))
		{
			methodDeclaration.setReturnType2(ast.newSimpleType(ast.newSimpleName(returnType)));
		}
		else {
			methodDeclaration.setReturnType2(ast.newPrimitiveType(PrimitiveType.toCode(returnType)));
		}

		if(parameters!=null&&parameters.length!=0)
		{
			for(int i = 0;i<parameters.length;i+=2)
			{
				SingleVariableDeclaration variableDeclaration = singleVariableDef(ast, parameters[i], parameters[i+1]);
				methodDeclaration.parameters().add(variableDeclaration);
			}
			
		}
		
		Block block = ast.newBlock();
		methodDeclaration.setBody(block);
		
		return methodDeclaration;
	}

	
	/**
	 * the source code pass to this method must not end with semicolon
	 * @param ast the code to be parsed
	 * @param source the code to be parsed
	 * @return
	 */
	public static Expression expressionDef(AST ast, String source)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_EXPRESSION);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		parser.setCompilerOptions(options);
		
		Expression expression = (Expression) parser.createAST(null);
		expression = (Expression) ASTNode.copySubtree(ast, expression);
		
		
		return expression;
	}
	
	/**
	 * The source code pass to this method must have a semicolon
	 * @param ast the code to be parsed
	 * @param source the code to be parsed
	 * @return
	 */
	public static Block statementsDef(AST ast, String source)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_STATEMENTS);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
		parser.setCompilerOptions(options);

		Block block = (Block) parser.createAST(null);
		block = (Block)ASTNode.copySubtree(ast, block);
				
		return block;
	}

	
	@SuppressWarnings("unchecked")
	public static SuperConstructorInvocation superConstructorInvocation(AST ast, String[] arguments) {
		SuperConstructorInvocation superConstructorInvocation = ast.newSuperConstructorInvocation();
		if(arguments!=null&&arguments.length>0)
		{
			for(int i = 0;i<arguments.length;++i)
			{
				superConstructorInvocation.arguments().add(Coder.expressionDef(ast, arguments[i]));
			}
		}
		return superConstructorInvocation;
	}

	
	@SuppressWarnings("unchecked")
	public static SuperMethodInvocation superMethodInvocation(AST ast, String identifier, String[] arguments) {
		SuperMethodInvocation superMethodInvocation = ast.newSuperMethodInvocation();
		superMethodInvocation.setName(ast.newSimpleName(identifier));
		if(arguments!=null&&arguments.length>0)
		{
			for(int i = 0;i<arguments.length;++i)
			{
				superMethodInvocation.arguments().add(Coder.expressionDef(ast, arguments[i]));
			}
		}
		return superMethodInvocation;
	}


	
	@SuppressWarnings("unchecked")
	public static void addSerialNumber(AST ast, TypeDeclaration classDeclaration)
	{
		VariableDeclarationFragment fragment = ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName("serialVersionUID"));
		NumberLiteral num = ast.newNumberLiteral();
		num.setToken("1");
		fragment.setInitializer(num);
		FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(fragment);
		fieldDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		fieldDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		fieldDeclaration.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
		fieldDeclaration.setType(ast.newPrimitiveType(PrimitiveType.toCode("long")));
		
	
		classDeclaration.bodyDeclarations().add(0,fieldDeclaration);
	}

	
	/**
	 * Add the comment as the last statement to the specified method
	 * 
	 * @param doc  The document holding this source code
	 * @param method the name of method that this comment is adding to
	 * @param comment the content of the comment, depends on the content of the comment, it could be line comment or block comment
	 */
	
	public static void addMethodComment(Document doc, String method, String comment) {
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(doc.get().toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		
		ASTRewrite rewrite= ASTRewrite.create(unit.getAST());
		
		unit.accept(new MethodCommentVisitor(rewrite, method, comment));
		
 		TextEdit textEdits = rewrite.rewriteAST(doc, null);
		
		try {
			textEdits.apply(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static String lowerFirstLetter(String string)
	{
		String str = string.substring(0,1).toLowerCase() + string.substring(1,string.length());
		return str;
	}


	public static void addMethodBody(AST ast, MethodDeclaration method, String source) {
		Block block = Coder.statementsDef(ast, source);
		method.setBody(block);
	}


	@SuppressWarnings("unchecked")
	public static void importPackage(CompilationUnit unit, AST ast, String string, boolean onDemand) {
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		importDeclaration.setName(ast.newName(string));
		importDeclaration.setOnDemand(onDemand);
		
		unit.imports().add(importDeclaration);
	}


	@SuppressWarnings("unchecked")
	public static MethodInvocation methodInvocation(AST ast, String expression, String identifier, Expression[] arguments) {
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		
		if(expression!=null)
			methodInvocation.setExpression(Coder.expressionDef(ast, expression));
		methodInvocation.setName(ast.newSimpleName(identifier));
		if(arguments==null)
			return methodInvocation;
		
		for(int i = 0;i<arguments.length;++i)
		{
			methodInvocation.arguments().add(arguments[i]);
		}
		
		return methodInvocation;
		
	}


	@SuppressWarnings("unchecked")
	public static ClassInstanceCreation classInstance(AST ast, String type, Expression[] arguments) {
		ClassInstanceCreation classInstance = ast.newClassInstanceCreation();
		classInstance.setType(ast.newSimpleType(ast.newSimpleName(type)));
		for(int i = 0;i<arguments.length;++i)
		{
			classInstance.arguments().add(arguments[i]);
		}

		return classInstance;
		
	}






	
}


class StatementComparator implements Comparator<Object>
{

	public int compare(Object arg0, Object arg1) {
		if(arg0 instanceof FieldDeclaration)
			return -1;
		else if(arg1 instanceof FieldDeclaration)
			return 1;
		else 
			return 0;
	}
	
}


class MethodCommentVisitor extends ASTVisitor
{
	private String comment;
	private String method;
	private ASTRewrite rewrite;
	public MethodCommentVisitor(ASTRewrite rewrite, String method, String comment)
	{
		this.method = method;
		this.comment = comment;
		this.rewrite = rewrite;
	}
	
	@Override
	public boolean visit(MethodDeclaration node)
	{
		String name = node.getName().toString();
		if(name.equals(this.method))
		{
			Block block = node.getBody();
			
			ListRewrite listRewrite= rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
			
			Statement commentHolder = (Statement) rewrite.createStringPlaceholder(comment,ASTNode.EMPTY_STATEMENT);
			listRewrite.insertLast(commentHolder, null);

		}

		return false;
	}
}



