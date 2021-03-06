package controlers;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import generators.GetSrcProject;
import generators.UnitTestGenerator;
import utils.Constants;
import utils.FileManager;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Julia Leven
 * @author Edmond Van-overtveldt
 * @author Jérôme Garcia
 * @author Abdelrhamane Benhammou
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		try {
			
			for(String arg : args){
				if(arg.equalsIgnoreCase("--config")) {
					FileManager.writeConfig();
					System.out.println(Constants.configMessage);
					System.exit(0);
				}
				else if (arg.equalsIgnoreCase("--annotation")) {
					FileManager.writeUnitTestClass();
					System.out.println(Constants.annotationMessage);
					System.exit(0);
				}
				else if (arg.equalsIgnoreCase("--init")) {
					FileManager.writeUnitTestClass();
					FileManager.writeConfig();
					System.out.println(Constants.helpMessage);
					System.exit(0);
				}
				else if (arg.equalsIgnoreCase("--help")) {
					System.out.println(Constants.helpMessage);
					System.exit(0);
				}
			}
			
			// -------- Gestion du fichier de config
			Properties prop = new Properties();
			InputStream input = new FileInputStream(Constants.config_file);
			prop.load(input);
			Constants.source = prop.getProperty(Constants.property_source);
			Constants.junit = prop.getProperty(Constants.property_junit);
			Constants.mockito = prop.getProperty(Constants.property_mockito);
			Constants.lib = prop.getProperty(Constants.property_lib);
			if(prop.getProperty(Constants.property_verbose) != null)
				Constants.verbose = prop.getProperty(Constants.property_verbose);
			if(prop.getProperty(Constants.property_log) != null)
				Constants.log_file = prop.getProperty(Constants.property_log);
			if(prop.getProperty(Constants.tmpFolder) != null)
				Constants.tmpFolder = prop.getProperty(Constants.property_tmp_folder);
			Constants.ready();
			
			// --------- Lancement des tests unitaires
			TestLauncher tester = new TestLauncher();
			tester.init();
			GetSrcProject.main(args);
			UnitTestGenerator.main(args);
			Map<String, Result> results = tester.run();
			
			// ----- Stockage des logs
			FileManager.writeLog("", false);
			long timestamp = 0;
			boolean success = true;
			Constants.debug("-------------------------------------------------------\nT E S T S\n-------------------------------------------------------\n");
			for(String filename : results.keySet()) {
				Result result = results.get(filename);
				String classe =  getClassNameWhithoutPath(filename);
				timestamp += (float)result.getRunTime();
				success &= result.wasSuccessful();
				Constants.debug("\n------------------------------" +  classe  + "----------------------------------------\n");
				Constants.debug(""+ (result.wasSuccessful()?"PASSED":"FAILED")+ " -> Tests run : " + result.getRunCount() + ", Failures : " + result.getFailureCount() + ", Time elapsed : " + (float)result.getRunTime()+" sec\n");
				for(Failure fail : result.getFailures()) {
					Constants.debug("\n--- Failure --- " + fail.getMessage() + " , " + fail.getTestHeader() + " , Exception :  " + fail.getException() + "\n");
				}
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println();
			Constants.debug("[INFO] ------------------------------------------------------------------------\n");
			Constants.debug("[INFO] BUILD " + (success?"SUCCESS":"FAILED")+ "\n");
			Constants.debug("[INFO] ------------------------------------------------------------------------\n");
			Constants.debug("[INFO] Total time: "+timestamp+" s\n");
			Constants.debug("[INFO] Finished at: "+dateFormat.format(date)+"\n");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public static String getClassNameWhithoutPath(String filename) {
		return filename.replace("TestCase_", "").replace(".java", "").substring(filename.indexOf(Constants.tmpFolder)).replace(Constants.tmpFolder, "");
	}

}