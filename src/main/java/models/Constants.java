package models;

public class Constants {

	public static String junit = null;
	public static String mockito = null;
	public static String source = null;
	public static String lib = null;
	public static String tmpFolder = ".tmpProject/";
	
	public static boolean ready () {
		if(junit == null) return false;
		if(mockito == null) return false;
		if(source == null) return false;
		if(lib == null) return false;
		return true;
	}
}
