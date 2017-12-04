package edu.gmu.cs.mason.codegenerator;

import edu.gmu.cs.mason.wizards.model.ProjectInformation;

import org.eclipse.jdt.core.ICompilationUnit;

public class TemplateInserter {
	private static TemplateInserter instance = null;


	private TemplateInserter() {

	}

	public static TemplateInserter getInstance(ICompilationUnit cu, ProjectInformation projectInfo) {
		if (instance == null) {
			instance = new TemplateInserter();
		} else {

		}

		return instance;
	}
}
