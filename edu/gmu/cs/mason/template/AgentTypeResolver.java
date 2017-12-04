package edu.gmu.cs.mason.template;


import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.ui.text.template.contentassist.MultiVariable;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

import edu.gmu.cs.mason.Activator;



@SuppressWarnings("restriction")
public class AgentTypeResolver extends TemplateVariableResolver {

	private String defaultType = "sim.engine.Steppable";
	

	private String[] result = null;

	public AgentTypeResolver() {
		this("sim.engine.Steppable"); 
	}

	public AgentTypeResolver(String defaultType) {
		this.defaultType = defaultType;
		
	}

	
	
	@Override
	public void resolve(TemplateVariable variable, TemplateContext context) {
		JavaContext jc = (JavaContext)context;
		IJavaProject javaProject = jc.getCompilationUnit().getJavaProject();
		
		IType type;
		try {
			type = javaProject.findType(defaultType);
			ITypeHierarchy typeHierarchy = type.newTypeHierarchy(javaProject, null);
			IType[] subTypes = typeHierarchy.getAllSubtypes(type);
			
			MultiVariable jv = (MultiVariable) variable;
			
			this.result = new String[subTypes.length];
			for (int i = 0; i < subTypes.length; i++) {
				result[i] = subTypes[i].getElementName();
			}
			
			
			if(subTypes.length>0)
			{
				jv.setChoices(result);
				jc.markAsUsed(result[0]);
				if(subTypes.length>1)
					jv.setUnambiguous(false);
				else
					jv.setUnambiguous(isUnambiguous(context));
			}
			
			
		} catch (JavaModelException e) {
			Activator.log("resolve agent type error", e);
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