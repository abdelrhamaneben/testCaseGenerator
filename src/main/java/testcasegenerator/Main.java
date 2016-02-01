package testcasegenerator;
import java.io.File;

import spoon.Launcher;

/**
 * @author Julia Leven
 * @author Edmond Van-overtveldt
 * @author JÃ©rÃ´me Garcia
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if (args.length < 1) {
			System.out.println("Usage :\njava -jar testCaseGenerator Project_Path  JunitJar_Path\n");
		}
		else {
			TestLauncher tester = new TestLauncher();
			tester.init(args[1],args[0]);
			args[1] = tester.tmpFolder;
			GetSrcProject.main(args);
			UnitTestGenerator.main(args);
			tester.run();
			System.out.println("Tests created in \"test/\"");
		}
	}

}