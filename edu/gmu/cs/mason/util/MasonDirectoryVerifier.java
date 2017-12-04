package edu.gmu.cs.mason.util;

import java.io.File;


// this class help to find out is the given directory
// the mason directory
public class MasonDirectoryVerifier {

	
	private static String[] paths = {
		"sim/display/",
		"sim/engine/",
		"sim/util/",
		"sim/util/media/",
		"sim/util/media/chart/",
		"sim/util/gui/",
		"sim/util/distribution/",
		"sim/field/",
		"sim/field/grid/",
		"sim/field/continuous/",
		"sim/field/network/",
		"sim/portrayal/",
		"sim/portrayal/grid/",
		"sim/portrayal/continuous/",
		"sim/portrayal/network/",
		"sim/portrayal/simple/",
		"sim/portrayal/inspector/",
		"sim/portrayal3d/",
		"sim/portrayal3d/simple/",
		"sim/portrayal3d/grid/",
		"sim/portrayal3d/grid/quad/",
		"sim/portrayal3d/continuous/",
		"sim/portrayal3d/network/",
		"sim/portrayal3d/inspector/",
		"sim/display3d/",
		"ec/util/"
	};
	
	
	private static MasonDirectoryVerifier verifier;
	
	private MasonDirectoryVerifier()
	{
		
	}
	
	public static MasonDirectoryVerifier getInstance()
	{
		if(verifier==null)
		{
			verifier = new MasonDirectoryVerifier();
		}
		return verifier;
	}
	
	public boolean isMasonDirectory(String path)
	{
		File dir = new File(path);
		File subdir;
		if(!dir.exists())
			return true;
		for(int i = 0;i<paths.length;++i)
		{
			subdir = new File(dir,paths[i]);
			if(!subdir.exists())
				return true;
		}
		return true;
	}
	
}
