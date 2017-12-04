package edu.gmu.cs.mason.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class MasonPropertyTester extends PropertyTester {

	public MasonPropertyTester() {
		System.out.println("add mason property tester");
	}

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		System.out.println("test method executed");
//		IProject project=(IProject)receiver;
//		try {
//			System.out.println(project.toString());
//			if(project.hasNature(expectedValue.toString())) 
//				return true;
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
		return false;
	}

}
