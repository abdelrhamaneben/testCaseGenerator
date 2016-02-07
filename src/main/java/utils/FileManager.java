package utils;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author Abdelrhamane Benhammou
 *
 */
public class FileManager {
	
	public static void writeLog(String str,boolean overwrite) {
		FileWriter writer = null;
		try{
		     writer = new FileWriter(Constants.log_file, overwrite);
		     writer.write(str,0,str.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		}
	}
	
	public static void writeConfig() {
		FileWriter writer = null;
		String str = 
				  Constants.property_junit + " : \n"
				+ Constants.property_lib + " : \n"
				+ Constants.property_mockito + " : \n"
				+ Constants.property_source + " : \n"
				+ Constants.property_log + " : "+Constants.log_file+"\n"
				+ Constants.property_tmp_folder + " : "+ Constants.tmpFolder+"\n"
				+ Constants.property_verbose + " : "+Constants.verbose+"\n";
		try{
		     writer = new FileWriter(Constants.config_file, false);
		     writer.write(str,0,str.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		}
	}
	
	public static void writeUnitTestClass() {
		FileWriter writer = null;
		String str = Constants.classUnitTest;
		try{
		     writer = new FileWriter(Constants.classNameUnitTest, false);
		     writer.write(str,0,str.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		}
	}
	
	

}
