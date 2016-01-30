package testcasegenerator;
import java.io.File;

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
			System.out.println("Usage :\njava -jar testCaseGenerator PATH\n\t- PATH : the path to the source folder");
		}
		else {
			TestLauncher launcher = new TestLauncher();
			launcher.init(args[1]);
			args[1] = launcher.tmpFolder;
			GetSrcProject.main(args);
			//UnitTestGenerator.main(args);
			//launcher.run();
			System.out.println("Tests created in \"test/\"");
		}
	}

}