package edu.gmu.cs.mason.template;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.ui.text.template.contentassist.MultiVariable;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.util.CodeUtil;
import edu.gmu.cs.mason.wizards.project.ui.field.FieldPortrayalEditingSupport;

@SuppressWarnings("restriction")
public class FieldVariableResolver extends TemplateVariableResolver{
	private String defaultType = "java.lang.Object";
	

	private String[] result = null;
	private HashMap<String, Integer> map;

	public FieldVariableResolver() {
		this("java.lang.Object"); 
		
		
		
	}

	public FieldVariableResolver(String defaultType) {
		this.defaultType = defaultType;
	}

	
	
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context) {
		
		JavaContext jc = (JavaContext)context;

		ArrayList<String> fieldList = new ArrayList<String>();
		try {

			
			// get all type and all fields to test if this fits our needs
			IType[] types = jc.getCompilationUnit().getAllTypes();
			for(IType t:types)
			{
				IField[] fields = t.getFields();
				for(IField f:fields)
				{
					String fieldType = CodeUtil.stripSignatureToFullQualifiedName(f.getTypeSignature());
					// we reuse the field portrayal map
					if(FieldPortrayalEditingSupport.fieldPortrayMap.containsKey(fieldType))
						fieldList.add(f.getElementName());
					
				}
			}
			
			this.result = new String[fieldList.size()];
			result = fieldList.toArray(result);
			
			MultiVariable jv = (MultiVariable) variable;
			
			if(fieldList.size()>0)
			{
				jv.setChoices(result);
				jc.markAsUsed(result[0]);
				if(fieldList.size()>1)
					jv.setUnambiguous(false);
				else
					jv.setUnambiguous(isUnambiguous(context));
			}
			
			
			
			
		} catch (JavaModelException e) {
			Activator.log("resolve field variable error", e);
		}
	}

	
	/**
	 * Returns all possible bindings available in context. 
	 * The default implementation simply returns an array which
	 * contains the result of resolve(TemplateContext), or an empty array 
	 * if that call returns null.
	 */
	@Override
	protected String[] resolveAll(TemplateContext context) {
		if(this.result!=null)
		{
			return this.result;
		}
		
		return new String[0];
			
	}
}
