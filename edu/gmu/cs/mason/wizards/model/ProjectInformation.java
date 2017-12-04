package edu.gmu.cs.mason.wizards.model;

import java.net.URI;
import java.util.ArrayList;

public class ProjectInformation {
	public String masonPath;
	public String projectName;
	public String packageName;
	public String simStateClassName;
	public String GUIClassName;
	public URI projectLocation;
	public boolean singleFolder;
	public boolean withGUI;
	
	public int displayWidth = 600;
	public int displayHeight = 600;
	
	
	public ArrayList<FieldInformation> fieldInfoList;
	public ArrayList<AgentInformation> agentInfoList;
	
}
