package controlers;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import generators.GetSrcProject;
import generators.UnitTestGenerator;
import models.Constants;
import models.FileManager;
import spoon.Launcher;

import java.util.ArrayList;
import java.util.Properties;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * @author Julia Leven
 * @author Edmond Van-overtveldt
 * @author Jérôme Garcia
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		try {
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
			ArrayList<Result> results = tester.run();
			
			// ----- Stockage des logs
			int i = 1;
			FileManager.writeLog("", false);
			for(Result result : results) {
				Constants.debug("\n-------------------------------------------------------------------------------\n");
				Constants.debug("Class"+i+" ("+ (result.wasSuccessful()?"PASSED":"FAILED")+ ") -> NbRun : " + result.getRunCount() + " NbFailure " + result.getFailureCount() + " runtime : " + (float)result.getRunTime());
				for(Failure fail : result.getFailures()) {
					Constants.debug("\n--- Failure --- " + fail.getMessage() + " , " + fail.getTestHeader() + " , Exception :  " + fail.getException());
					for(StackTraceElement trace : fail.getException().getStackTrace()) {
						Constants.debug("\n--------Exception------- "+trace.toString() );
					}
				}
				i++;
			}
			
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}