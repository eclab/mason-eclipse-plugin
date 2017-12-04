package edu.gmu.cs.mason.util;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import edu.gmu.cs.mason.Activator;

public class CodeUtil {

	/**
	 * Returns the fully qualified type name of the given signature, with any
	 * type parameters and arrays erased.
	 *
	 * @param signature the signature
	 * @return the fully qualified type name of the signature
	 * @throws IllegalArgumentException if the signature is syntactically incorrect
	 */
	public static String stripSignatureToFullQualifiedName(String signature) throws IllegalArgumentException {
		signature = Signature.getTypeErasure(signature);
		signature = Signature.getElementType(signature);
		return Signature.toString(signature);
	}
	
	
	public static ASTNode getNodeToInsertBefore(ListRewrite listRewrite, IJavaElement sibling) throws JavaModelException {
		if (sibling instanceof IMember) {
			ISourceRange sourceRange= ((IMember) sibling).getSourceRange();
			if (sourceRange == null) {
				return null;
			}
			int insertPos= sourceRange.getOffset();

			List<? extends ASTNode> members= listRewrite.getOriginalList();
			for (int i= 0; i < members.size(); i++) {
				ASTNode curr= members.get(i);
				if (curr.getStartPosition() >= insertPos) {
					return curr;
				}
			}
		}
		return null;
	}

	
	
	@SuppressWarnings("unchecked")
	public static String formatCodeSnippet(int kind, String code, IJavaProject project)
	{		
		// take default Eclipse formatting options
		Map<String, String> options = project != null ? project.getOptions(true) : null;
		
		// instantiate the default code formatter with the given options
		CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);

		// use options to format the code //FIXME this returns null, so let's see why
		TextEdit edit = codeFormatter.format(
			kind,
			code, // source to format
			0, // starting position
			code.length(), // length
			0, // initial indentation
			System.getProperty("line.separator") // line separator
		);

		IDocument document = new Document(code);
		try {
			if(edit!=null)
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
}
