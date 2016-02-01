package controlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import exceptions.UnTestableException;

/**
 * 
 * @author abdelrhamanebenhammou
 *
 */
public class TestLauncher {
	
	public static final String tmpFolder = ".tmpProject/";
	public String project;
	
	public String COMPILER_PATH;
	private URLClassLoader classLoader;
	/**
	 * Remise à Zero du dossier temporaire
	 * @throws UnTestableException
	 */
	public void init(String compiler,String project) throws UnTestableException {
		try {
			COMPILER_PATH = compiler;
			this.project = project;
			this.resetTmpFolder();
		} catch (Exception e) {
			throw new UnTestableException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * vider le répertoire temporaire
	 * @throws SecurityException
	 * @throws IOException
	 */
	private void resetTmpFolder() throws SecurityException, IOException{
		File theDir = new File(tmpFolder);
		// if the directory does not exist, create it
		if (!theDir.exists()) {
			theDir.mkdir();
		}
		else {
			FileUtils.deleteDirectory(theDir);
			this.resetTmpFolder();
		}
	}
	
	private  List<String> findTestFolder(String path) {
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		for (File f : files) {
			if (f.isDirectory())
				pathToSources.addAll(findTestFolder(f.getAbsolutePath()));
			else if (f.getName().endsWith(".java"))
				pathToSources.add(f.getAbsolutePath());
		}

		return pathToSources;
	}
	
	private  String convertToClassName(String file) {
		String pattern = Pattern.quote(File.separator);
		int ind = file.split(pattern).length;
		return file.split(pattern)[ind-2] + "." + file.split(pattern)[ind -1].replace(".java", "");
	}

	private  List<String> findJavaFiles(String path, Boolean test) throws UnTestableException{
		File[] files = new File(path).listFiles();
		List<String> pathToSources = new ArrayList<String>();
		if(files == null) throw new UnTestableException();
		for(File f : files){
			if(f.isDirectory())
				pathToSources.addAll(findJavaFiles(f.getAbsolutePath(), test));
			else if((f.getName().contains("TestCase") && test && f.getName().endsWith(".java"))) {
				pathToSources.add(f.getAbsolutePath());
			}
			else if((!f.getName().contains("TestCase") && !test && f.getName().endsWith(".java"))) {
				pathToSources.add(f.getAbsolutePath());
			}
				
		}
		return pathToSources;
	}
	
	public void run() throws UnTestableException {

		try {
			JUnitCore junit = new JUnitCore();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			List<String> listTestFiles = findJavaFiles(this.tmpFolder , true);
			List<String> listSourceFiles = findJavaFiles(this.tmpFolder , false);
	
			
			for(String file : listTestFiles) {
				compiler.run(null, null, null, "-cp", "/Users/abdelrhamanebenhammou/workspace/testCaseGenerator/lib/mockito-1.8.0.jar:/Users/abdelrhamanebenhammou/workspace/testCaseGenerator/lib/junit-4.12.jar:"+ this.tmpFolder, file);
			}
			
			classLoader = URLClassLoader.newInstance(new URL[] {
					new File(this.tmpFolder ).toURI().toURL(), new File(this.COMPILER_PATH).toURI().toURL()
			});
	      
	  		for(String file : listSourceFiles) {
	  			System.out.println(file);
	  			Class.forName(convertToClassName(file), true, classLoader);
	  		}
	    	
	  		for(String file : listTestFiles){
	  			
	  			Result results = junit.run(Class.forName(convertToClassName(file), true, classLoader));
	  			System.out.println(results.getFailures().size());
	  		}
	       }
	       catch(Exception e) {
	    	  e.printStackTrace();
	    	  throw new UnTestableException(e.getLocalizedMessage());
	       }
	}

}
