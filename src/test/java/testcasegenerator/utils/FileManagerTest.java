package testcasegenerator.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import exceptions.UnSupportableConfig;
import junit.framework.Assert;
import junit.framework.TestCase;
import utils.Constants;
import utils.FileManager;

public class FileManagerTest	 extends TestCase{
	private String file_log = "./ressources/logTest.txt";
	@Test
	public void testConstructor()  {
		/*Constants.log_file = file_log; 
		FileManager.writeLog("blabla", true);
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file_log));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    Assert.assertEquals("blabla", everything.trim());
		    br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}*/
	}
}
