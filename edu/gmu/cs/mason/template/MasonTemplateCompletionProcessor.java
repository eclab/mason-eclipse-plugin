package edu.gmu.cs.mason.template;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

public class MasonTemplateCompletionProcessor extends TemplateCompletionProcessor {

	@Override
	protected Template[] getTemplates(String contextTypeId) {
		
		return null;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		
		return null;
	}

	@Override
	protected Image getImage(Template template) {
		
		return null;
	}

	
	
	
}
