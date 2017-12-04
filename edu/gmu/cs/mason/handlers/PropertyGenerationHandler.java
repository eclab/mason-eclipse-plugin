package edu.gmu.cs.mason.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.handlers.ui.dialogs.PropertyGenerationDialog;
import edu.gmu.cs.mason.util.CodeUtil;


/* TODO several item we would like to improve this functionality 
 * 1. Add progress monitor
 * 2. Able to determine what method already have generated and avoid generate duplicate method
 * 3. A better selection method, instead of just select the text of the field name, may be 
 *    we can have a wide range than that
 * 4. Only generate code when the context is proper
 * 5. The insertion point first showed up may need to adjust according to the selection
 */



public class PropertyGenerationHandler extends AbstractHandler {

	private PropertyGenerationDialog dialog;
	private ICompilationUnit unit;
	private IEditorPart editor;
	private IField field;

	public enum DomainApplicable
	{
		INTEGER,
		DOUBLE,
		LONG,
		OTHER
	};
	
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		editor = HandlerUtil.getActiveEditor(event); 
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection instanceof ITextSelection) 
		{
			ITextSelection textSelection = (ITextSelection)selection;
			unit = getCompilationUnitFromEditor(editor);
			field = getField(textSelection, unit);
			if(field!=null)
			{
				openDialog(shell, field);
				modifyCode(field.getDeclaringType());
				return null;
			}
			
			// use a message box to show the error
			MessageDialog.openError(shell, "Error in Selection", "The selection are invalid");
			return null;
		}
		
				
		return null;
	}
	
	
	private void modifyCode(IType type) {
		// create a syntax tree from the type element
		CompilationUnit unit = parse(type.getCompilationUnit(), null, true,
				false, false, null);

		// Group the following changes into an undo command by using IRewriteTarget
		IRewriteTarget target = (IRewriteTarget) editor.getAdapter(IRewriteTarget.class);
		if (target != null) {
			target.beginCompoundChange();
		}
		try {
			generateCode(type, unit);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (target != null) {
				target.endCompoundChange();
			}
		}
	}

	

	private void generateCode(IType type, CompilationUnit root) throws JavaModelException {
		ICompilationUnit unit = type.getCompilationUnit();
		
		// create instance of manipulation of given AST tree
		ASTRewrite astRewrite = ASTRewrite.create(root.getAST());
		 
		
		// Some nodes with properties that include a list of children (for example, Block whose statements property is a list of statements)
		// The ListRewrite describing manipulations to a child list property of an AST node.
		ListRewrite listRewriter = null;
		
		// use the name range of the type to find the node of this type
		ASTNode node = NodeFinder.perform(root, type.getNameRange());
		
		
		
		do {
			node= node.getParent();
		} while (node != null && !AbstractTypeDeclaration.class.isInstance(node));

		AbstractTypeDeclaration declaration = (AbstractTypeDeclaration) node;
		
		if (declaration != null)
			listRewriter = astRewrite.getListRewrite(declaration, declaration.getBodyDeclarationsProperty());
		
		if (listRewriter == null) {
			Activator.log(new Status(Status.ERROR, Activator.PLUGIN_ID, "error in generating code for property"));
			return;
		}

		// start to generate code
		if(dialog.hideName())
		{
			ASTNode n = CodeUtil.getNodeToInsertBefore(listRewriter, dialog.getElementPosition());
			addNewAccessor(type, getHidePropertyMethod(field), listRewriter,  n);
		}
		
		if(dialog.hasDescription())
		{
			ASTNode n = CodeUtil.getNodeToInsertBefore(listRewriter, dialog.getElementPosition());
			addNewAccessor(type, getDesPropertyMethod(field), listRewriter, n);
		}
		
		if(dialog.reviseName())
		{
			ASTNode n = CodeUtil.getNodeToInsertBefore(listRewriter, dialog.getElementPosition());
			addNewAccessor(type, getRevisedNamePropertyMethod(field), listRewriter, n);
		}
		
		if(dialog.hasDomain()&&dialog.useSlider())
		{
			ASTNode n = CodeUtil.getNodeToInsertBefore(listRewriter, dialog.getElementPosition());
			addNewAccessor(type, getSliderPropertyMethod(field), listRewriter, n);
		}
		
		if(dialog.hasDomain()&&!dialog.useSlider())
		{
			ASTNode n = CodeUtil.getNodeToInsertBefore(listRewriter, dialog.getElementPosition());
			addNewAccessor(type, getDomainPropertyMethod(field), listRewriter, n);
		}
		
		
		TextEdit edit = astRewrite.rewriteAST();
		
		unit.applyTextEdit(edit, null);
		
		
	}
	
	
	/**
	 * Adds a new accessor for the specified field.
	 *
	 * @param type the type
	 * @param field the field
	 * @param contents the contents of the accessor method
	 * @param rewrite the list rewrite to use
	 * @param insertion the insertion point
	 * @throws JavaModelException if an error occurs
	 */
	private void addNewAccessor(IType type, String source, ListRewrite rewrite, ASTNode insertion) throws JavaModelException {
		String method = CodeUtil.formatCodeSnippet(CodeFormatter.K_CLASS_BODY_DECLARATIONS, source, type.getJavaProject());
		MethodDeclaration declaration = (MethodDeclaration) rewrite
				.getASTRewrite()
				.createStringPlaceholder(method,ASTNode.METHOD_DECLARATION);
		if (insertion != null)
			rewrite.insertBefore(declaration, insertion, null);
		else
			rewrite.insertLast(declaration, null);
	}
	
	
	
	
	
	
	
	
	
	public CompilationUnit parse(ITypeRoot typeRoot, WorkingCopyOwner owner, boolean resolveBindings, boolean statementsRecovery, boolean bindingsRecovery, IProgressMonitor pm) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		
		parser.setResolveBindings(resolveBindings);
		parser.setStatementsRecovery(statementsRecovery);
		parser.setBindingsRecovery(bindingsRecovery);
		parser.setSource(typeRoot);
		if (owner != null)
			parser.setWorkingCopyOwner(owner);
		
		CompilationUnit result = (CompilationUnit) parser.createAST(pm);
		return result;
	}
	

	

	

	private void openDialog(Shell shell, IField field) {
		IType type = field.getDeclaringType();
		try {
			dialog = new PropertyGenerationDialog(shell, type, field);
			dialog.open();
		} catch (JavaModelException e) {
			Activator.log("PropertyGenerationDialog error", e);
		}
		
		
	}





	private IField getField(ITextSelection textSelection, ICompilationUnit unit) {
		try {
			reconcileUnit(unit);
			IJavaElement e = unit.getElementAt(textSelection.getOffset());
			if(e instanceof IField)
				return (IField)e;
			
			return null;

		} catch (JavaModelException e) {
			Activator.log("cannot find the valid element from selection", e);
		}
		return null;
	}




	/**
	 * From the the current active editor, get the compilation unit from it
	 * @param editor
	 * @return
	 */
	private ICompilationUnit getCompilationUnitFromEditor(IEditorPart editor) {
		if (editor != null) {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput != null) {
				ICompilationUnit unit = (ICompilationUnit) JavaUI
						.getEditorInputJavaElement(editorInput);
				if (unit != null)
					return unit;
				else {
					throw new NullPointerException(
							"The Active Compilation Unit doesn't contain an IJavaElement");
				}
			}
		}
		return null;
	}
	
	
	
	private void reconcileUnit(ICompilationUnit unit) throws JavaModelException {
		// force a reconcile of the unit
		unit.reconcile(ICompilationUnit.NO_AST, 
				false, // don't force problem detection
				null, // use primary owner 
				null); // no progress monitor 
	}
	
	
	private String getHidePropertyMethod(IField field)
	{
		StringBuffer buffer = new StringBuffer();
		String lineDelim = "\n";
		buffer.append("public boolean ");
		buffer.append(getMethodName(field, "hide"));
		buffer.append("() {");
		buffer.append(lineDelim);
		buffer.append("return true;");
		buffer.append(lineDelim);
		buffer.append("}");
		
		return buffer.toString();
	}
	
	
	private String getDesPropertyMethod(IField field)
	{
		StringBuffer buffer = new StringBuffer();
		String lineDelim = "\n";
		buffer.append("public String ");
		buffer.append(getMethodName(field, "des"));
		buffer.append("() {");
		buffer.append(lineDelim);
		buffer.append("return ");
		buffer.append("\""+dialog.getDesText()+"\";");
		buffer.append(lineDelim);
		buffer.append("}");
		
		return buffer.toString();
	}
	
	private String getRevisedNamePropertyMethod(IField field)
	{
		StringBuffer buffer = new StringBuffer();
		String lineDelim = "\n";
		buffer.append("public String ");
		buffer.append(getMethodName(field, "name"));
		buffer.append("() {");
		buffer.append(lineDelim);
		buffer.append("return ");
		buffer.append("\""+dialog.getNameReviseText()+"\";");
		buffer.append(lineDelim);
		buffer.append("}");
		
		return buffer.toString();
	}
	
	private String getSliderPropertyMethod(IField field)
	{
		StringBuffer buffer = new StringBuffer();
		String lineDelim = "\n";
		buffer.append("public Object ");
		buffer.append(getMethodName(field, "dom"));
		buffer.append("() {");
		buffer.append(lineDelim);
		
		buffer.append("return new sim.util.Interval(");
		buffer.append(dialog.getMinText());
		buffer.append(", ");
		buffer.append(dialog.getMaxText());
		buffer.append(");");
		
		buffer.append(lineDelim);
		buffer.append("}");
		
		return buffer.toString();
	}
	

	private String getDomainPropertyMethod(IField field)
	{
		StringBuffer buffer = new StringBuffer();
		String lineDelim = "\n";
		buffer.append("public Object ");
		buffer.append(getMethodName(field, "dom"));
		buffer.append("() {");
		buffer.append(lineDelim);
		
		buffer.append("return new String[] {");
		
		String[] strings = dialog.getDomainText();
		for(int i = 0;i<strings.length;++i)
		{
			String item = new String();
			if(i!=0)
			{
				item+=",";
			}
			item += ("\""+strings[i]+"\"");
			buffer.append(item);
		}
		
		
		buffer.append("};");
		
		buffer.append(lineDelim);
		buffer.append("}");
		
		return buffer.toString();
	}
	
	
	private String getMethodName(IField field, String prefix)
	{
		String name = field.getElementName();
		String first = name.substring(0, 1).toUpperCase();
		String second = new String();
		if(name.length()>1)
		{
			second = name.substring(1);
		}
		return prefix+first+second;
	}
	
	
}
