package controlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import exceptions.UnTestableException;
import utils.Constants;

/**
 * 
 * @author abdelrhamanebenhammou
 *
 */
public class TestLauncher {
	
	
	private URLClassLoader classLoader;
	/**
	 * Remise à Zero du dossier temporaire
	 * @throws UnTestableException
	 */
	public void init() throws UnTestableException {
		try {
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
		File theDir = new File(Constants.tmpFolder);
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
	
	public Map<String , Result> run() throws UnTestableException {

		Map<String, Result> results = new HashMap();
		try {
			JUnitCore junit = new JUnitCore();
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			List<String> listTestFiles = findJavaFiles(Constants.tmpFolder , true);
			List<String> listSourceFiles = findJavaFiles(Constants.tmpFolder, false);
	
			
			for(String file : listTestFiles) {
				compiler.run(null, null, null, "-cp", Constants.mockito+":"+Constants.junit+":"+ Constants.tmpFolder, file);
			}
			
			classLoader = URLClassLoader.newInstance(new URL[] {
					new File(Constants.tmpFolder ).toURI().toURL(), new File(Constants.lib).toURI().toURL()
			});
	      
	  		for(String file : listSourceFiles) {
	  			Class.forName(convertToClassName(file), true, classLoader);
	  		}
	    	
	  		for(String file : listTestFiles){
	  			Result result = junit.run(Class.forName(convertToClassName(file), true, classLoader));
	  			results.put(file, result);
	  		}
	       }
	       catch(Exception e) {
	    	  e.printStackTrace();
	    	  throw new UnTestableException(e.getLocalizedMessage());
	       }
		return results;
	}

}
