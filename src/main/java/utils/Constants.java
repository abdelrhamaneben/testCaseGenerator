package utils;

import exceptions.UnSupportableConfig;
/**
 * 
 * @author Abdelrhamane Benhammou
 *
 */
public class Constants {

	// ----------initial config properties----------
	public static String junit 			= null;
	public static String mockito 		= null;
	public static String source 		= null;
	public static String lib 			= null;
	public static String verbose 		= "false";
	public static String tmpFolder		= ".tmpProject/";
	public static String log_file		= "log.txt"; 
	public static String config_file	= "config.properties";
	
	
	// ----------Name Config properties----------
	public static final String property_junit = "JunitJar";
	public static final String property_mockito = "MockitoJar";
	public static final String property_source = "ProjectSource";
	public static final String property_lib = "LibForlder";
	public static final String property_verbose = "Verbose";
	public static final String property_log = "LogFile";
	public static final String property_tmp_folder = "TmpFolder";
	
	
	//---------- UnitTestClass----------
	public static final String classUnitTest = "package main;public @interface TestUnit {java.lang.String given() default \"\";java.lang.String params() default \"\";java.lang.String oracle() default \"\";java.lang.String when() default \"\"; java.lang.String actual() default \"\";java.lang.String[] withMock() default { \"\" };}";
	public static final String classNameUnitTest = "TestUnit.java";
	
	// ---------- MESSAGES -----------
	
	public static final String configMessage = "Required : junit-4.12.jar et mockito-1.8.0.jar";
	public static final String annotationMessage = "Place this class("+classNameUnitTest+") into your project (main package)";
	public static final String helpMessage = "Command [option]*\n\n"
			+ "Options :\n"
			+ " --config : generate config file\n"
			+ " --init   : generate config file and annotation class\n"
			+ " --annotation : generate annotation class (.java)\n"
			+ " \n\n"
			+ "Config File parameters : \n"
			+ property_junit + ": (Required) specify the Junit jar file\n"
			+ property_mockito + ": (Required) specify the Mockito jar file\n"
			+ property_source + ": (Required) specify the java source of the project\n"
			+ property_lib + ": (Required) specify the library folder (containing Mockito and Junit)\n"
			+ property_verbose + ": enable verbose\n"
			+ property_log + ": specify Log filename\n"
			+ property_tmp_folder + ": speficy temporary file\n";
	
	
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
