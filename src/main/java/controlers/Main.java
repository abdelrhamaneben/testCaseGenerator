package controlers;
import java.io.File;

import generators.GetSrcProject;
import generators.UnitTestGenerator;
import models.Constants;
import spoon.Launcher;

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
		
		if (args.length < 3) {
			System.out.println("Usage :\njava -jar testCaseGenerator Project_Path  JunitJar_Path MockitoJar_Path\n");
		}
		else {
			Constants.source = args[0];
			Constants.junit = args[1];
			Constants.mockito = args[2];
			Constants.lib = args[3];
			TestLauncher tester = new TestLauncher();
			tester.init();
			GetSrcProject.main(args);
			UnitTestGenerator.main(args);
			tester.run();
			System.out.println("Tests created in \"test/\"");
		}
	}

}