package models;

import exceptions.UnSupportableConfig;

public class Constants {

	public static String junit 			= null;
	public static String mockito 		= null;
	public static String source 		= null;
	public static String lib 			= null;
	public static String verbose 		= "false";
	public static String tmpFolder		= ".tmpProject/";
	public static String log_file		= "log.txt"; 
	public static String config_file	= "config.properties";
	
	public static final String property_junit = "JunitJar";
	public static final String property_mockito = "MockitoJar";
	public static final String property_source = "ProjectSource";
	public static final String property_lib = "LibForlder";
	public static final String property_verbose = "Verbose";
	public static final String property_log = "LogFile";
	public static final String property_tmp_folder = "TmpFolder";
	
	public static void ready () throws UnSupportableConfig {
		if(junit == null) throw new UnSupportableConfig(property_junit);
		if(mockito == null) throw new UnSupportableConfig(property_mockito);
		if(source == null) throw new UnSupportableConfig(property_source);
		if(lib == null) throw new UnSupportableConfig(property_lib);
	}
	
	public static void debug(String str) {
		if(verbose.equalsIgnoreCase("true")) {
			System.out.println(str);
		}
		FileManager.writeLog(str, true);
	}
}
