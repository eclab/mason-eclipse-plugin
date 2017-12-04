package edu.gmu.cs.mason.template.context;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

public class MasonContextType extends TemplateContextType {

	public MasonContextType() {
		
		// these resolver will add template variables to the context
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
	}

	public MasonContextType(String id) {
		super(id);
		
	}

	public MasonContextType(String id, String name) {
		super(id, name);
		
	}

}
