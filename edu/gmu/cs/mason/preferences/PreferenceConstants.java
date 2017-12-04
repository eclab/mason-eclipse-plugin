package edu.gmu.cs.mason.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public class PreferenceKey
	{
		public static final String DIRECTORY = "mason.preferences.directory";

		public static final String PACKAGE_PREFIX = "mason.preferences.packageprefix";
	
		public static final String AGENT_PACKAGE = "mason.preferences.agentpackage";
	}
	
	public class PreferenceValue
	{
		public static final String DEFAULT_DIR = "";

		public static final String DEFAULT_PACKAGE_PREFIX = "sim.app";
	
		public static final String DEFAULT_AGENT_PACKAGE = "agents";
	}
	
	class Text
	{
		public static final String PREFERENCE_DESCRIPTION = "MASON Preferences";
		
		public static final String MASON_DIRECTORY = "Mason Directory:";
		
		public static final String PACKAGE_PREFIX = "Default Package:";
		
		public static final String AGENT_PACKAGE = "Agents Package:";
	}
	
	
	class Message
	{
		public static final String DIRECTORY_ERROR_MESSAGE = "Mason directory doesn't exists";
	}
}
