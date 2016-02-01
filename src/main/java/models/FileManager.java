package models;

import java.io.FileWriter;
import java.io.IOException;

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
}
