package generators;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import utils.Constants;

/**
 * @author Julia Leven
 * @author Edmond Van-overtveldt
 * @author Jérôme Garcia
 * @author Abdelrhamane Benhammou
 */
public class UnitTestGenerator extends AbstractProcessor<CtClass<?>> {
	
	private String initialNameClass = "";
	public static ArrayList<String> classtoremove = new ArrayList<String>();
	
	/**
	 * 
	 */
	@Override
	public void process(CtClass<?> classe) {
		initialNameClass = classe.getSimpleName();
		/* -------------- Renommage de la classe --------------- */
		classe.getFactory().getEnvironment().setAutoImports(true);
		String nameClass = classe.getSimpleName();
		classe.setSimpleName("TestCase_"+nameClass);
		removeAllConstructors(classe);
		
		/* -------------- Verifie que la classe est bien à tester --------------- */
		boolean mustTest = false;
		for(CtAnnotation annotation  : classe.getAnnotations()){
			if (annotation.getSignature().equals("@main.TestUnit")) {
				classe.removeAnnotation(annotation);
				mustTest = true;
				break;
			}
		}
		// Cette classe n'est pas à tester
		if(!mustTest) {
			classtoremove.add(classe.getSimpleName());
			return;
		}
		
		/* -------------- Construction de la classe de test --------------- */
		extendsTestCase(classe);
		
		for(CtMethod method : classe.getAllMethods()) {
			CtTypeReference voidType = getFactory().Type().VOID_PRIMITIVE;
			method.setType(voidType);
			if(!method.getAnnotations().isEmpty()) {
				// genere les Methodes à tester	
				analyseTestMethod(classe, method);
			}
			// supprimer l'ancienne méthode
			voidMethod(method);
			classe.removeMethod(method);
			//removeAnnotation(method);
		}
		addsetup(classe);
		setAttributesClasseTest(classe);
	}
	
	public void analyseTestMethod(CtClass classe, CtMethod method) {
		int i = 0;
		for(CtAnnotation annotation : method.getAnnotations()) {
			if (annotation.getSignature().equals("@main.TestUnit")) {
				CtMethod testMethod = classe.getFactory().Core().createMethod().setSimpleName("testcase_"+method.getSimpleName()+"_"+i);
				addContentTestMethod(testMethod,annotation, method.getSimpleName());
				classe.addMethod(testMethod);
				i++;
			}
		}
	}
	
	/**
	 * TODO
	 * Cette méthode supprime tout les constructeurs d'une classe
	 * @param classe la classe
	 */
	public void removeAllConstructors(CtClass<?> classe) {
		List<CtConstructor<?>> tmp = new ArrayList<CtConstructor<?>>();
		
		for (CtConstructor c : classe.getConstructors() ) {
			tmp.add(c);
		}
		
		for (CtConstructor c : tmp) {
			classe.removeConstructor(c);
		}
	}
	
	/**
	 * Lecture des parametres de l'annotation et transcription dans la méthode de test
	 * @param classe
	 * @param methodTest
	 * @param annotation
	 */
	public void addContentTestMethod(CtMethod methodTest , CtAnnotation annotation, String methodname ){
		String params = "";
		String oracle ="";
		String body = "";
		String actual = "";
		
		addAnnotation(methodTest,"org.junit.Test");
		CtTypeReference voidType = getFactory().Type().VOID_PRIMITIVE;
		methodTest.setType(voidType);
		methodTest.setModifiers(getFactory().Code().modifiers(ModifierKind.PUBLIC));
		
		// ----------- Initialisation du CONSTRUCTEUR --------
		body = initialNameClass + " instance = new "+initialNameClass+"(";
		String stringParam = (String) annotation.getElementValue("given");
			String[] initTab = stringParam.split(",");
			for (int i = 0; i <  initTab.length; i++) {
				body += initTab[i];
				if (i != initTab.length-1)
					body += ", ";
			}
			body += ");\n\t\t";
			// DELETE PARAMS
			
			// WHEN 
			String when = (String) annotation.getElementValue("when");
			if(when.length() > 3) {
				body += when + ";\n\t\t";
			}
			
			// ASSERT
			params = (String) annotation.getElementValue("params");
			actual = (String) annotation.getElementValue("actual");
			
			if(!actual.isEmpty()){
				body += "junit.framework.Assert.assertEquals("+actual+",";
			}else {
				body += "junit.framework.Assert.assertEquals(instance."+methodname+"("+params+"),";
			}
			
			oracle = (String) annotation.getElementValue("oracle");
			initTab = oracle.split(",");
			for (int i = 0; i <  initTab.length; i++) {
				body += initTab[i];
				if (i != initTab.length-1)
					body += ", ";
			}
			body += ")";
			
			// WITHMOCK
			String[] withMock = (String[]) annotation.getElementValue("withMock");
			for (int i = 0; i < withMock.length; i++) {
				String[] tmp = withMock[i].split(",");
				if (tmp.length > 1) 
				body = "org.mockito.Mockito.when("+tmp[0]+").thenReturn("+tmp[1]+");\n\t\t" + body;
			}
			CtBlock emptyBlock = getFactory().Core().createBlock();
			CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
			snippet.setValue(body);
			emptyBlock.insertBegin(snippet);
			methodTest.setBody(emptyBlock);
	}
	
	/**
	 * Ajoute une nouvelle annotation
	 * @param m : méthode à  traiter
	 * @param annotationName : nom de l'annotation
	 */
	public void addAnnotation(CtTypedElement<?> elmt, String annotationName) {
		CtAnnotation<?> annotation = (CtAnnotation<?>) getFactory().Core().createAnnotation();
		
		CtTypeReference<Annotation> annotationBody = getFactory().Type().createReference(annotationName);
		
		annotation.setAnnotationType(annotationBody);
		
		ArrayList<CtAnnotation<?>> listAnnotation = new ArrayList<CtAnnotation<?>>();
		listAnnotation.add(annotation);
		elmt.setAnnotations(listAnnotation);
	}
	
	/**
	 * Supprime le contenu de la méthode (body)
	 * @param m
	 */
	public void voidMethod(CtMethod m) {
		CtBlock emptyBlock = getFactory().Core().createBlock();
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		snippet.setValue("return");
		emptyBlock.insertBegin(snippet);
		m.setBody(emptyBlock);
	}
	
	/**
	 * Cette méthode ajoute "extends TestCase" avec l'import qu'il faut à  la classe de test créée
	 * @param classe
	 */
	public void extendsTestCase(CtClass<?> classe) {
		// Créer référence vers la classe TestCase de junit.
		CtTypeReference<Object> extendsClass = getFactory().Type().createReference("junit.framework.TestCase");

		// Etend la classe TestCase de junit
		classe.setSuperclass(extendsClass);
	}
	
	/**
	 * Ajoute un attribut à  la classe
	 * @param classe
	 * @param modifiers
	 * @param type
	 * @param name
	 */
	public void addField(CtClass<?> classe, Set<ModifierKind> modifiers, CtTypeReference<?> type, String name) {
		// Vérifier, si pas type primitif -> change type pour interface associée + ajouter annotation @Mock
		List<CtTypeReference<?>> types = getListType();
	
		CtField<?> f = getFactory().Field().create(classe, modifiers, type, name, null);
		
		classe.addField(f);
	}

	public List<CtTypeReference<?>> getListType() {
		List<CtTypeReference<?>> newListType = new ArrayList<CtTypeReference<?>>();
		List<CtType<?>> listType = getFactory().Type().getAll();
		// Parcours de tous les types créés 
		for (CtType<?> t : listType) {
			CtTypeReference<?> typeRef = t.getReference();
			newListType.add(typeRef);
		}
		return newListType;
	}
	
	public void addsetup(CtClass classe) {
		// Set composé du modifier "public"
		Set<ModifierKind> smk = new HashSet<ModifierKind>();
		smk.add(ModifierKind.PUBLIC);
		// Type "ExampleTest"
		CtTypeParameterReference testType = getFactory().Type().createTypeParameterReference(initialNameClass);
		
		// ajout d'un attribut
		addField(classe, smk, testType, "instance");	
		
		// modification du contenu des méthodes annotées
		//setBodyClass(classe,nameClass);
		String mockitoInitMocks = "org.mockito.MockitoAnnotations.initMocks(this)";
		
		CtBlock block = getFactory().Core().createBlock();
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		snippet.setValue(mockitoInitMocks);
		block.insertBegin(snippet);
		CtMethod m = getFactory().Method().create(classe, smk, getFactory().Type().VOID_PRIMITIVE, "setUp" , null, null, block);
		addAnnotation(m, "org.junit.Before");
	}
	
	/**
	 * Cette méthode supprime tout les attributs de type primitif d'une classe
	 * @param classe la classe
	 */
	public void setAttributesClasseTest(CtClass<?> classe) {
		List<CtField<?>> fields = classe.getFields();
		List<CtField<?>> tmp = new ArrayList<CtField<?>>();
		List<CtTypeReference<?>> types = getListType();
		
		for (CtField<?> f : fields) {
			tmp.add(f);
		}
		
		// Parcours de tous les attributs de la classe
		for (CtField<?> f : tmp) {
			// Attribut primitif donc on l'enlà¨ve de la classe de tests
			if (!types.contains(f.getType())) {
				classe.removeField(f);
			// Attribut non primitif, on change son type par le type de l'interface correspondante
			} else {
				//Ajout de l'annotation
				addAnnotation(f,"org.mockito.Mock");
				//Changement de type de l'attribut
				CtTypeReference newSimpleName = getFactory().Type().createReference(f.getType().getSimpleName());
				f.setType(newSimpleName);
			}
		}
	}

	
	
	/* ---------------------------- LANCEUR DU PROCESSEUR  --------------------------------- */
	public static void main(String[] args) throws Exception {
		spoon.Launcher.main(new String[] {
				"-p",  UnitTestGenerator.class.getCanonicalName(),"-i",Constants.source , "-o" , Constants.tmpFolder
        });
		
		for(String file : classtoremove) {
			findandremove(Constants.tmpFolder,file);
		}
	}
	public static boolean findandremove(String folder, String file) {
		File parent = new File(folder);
		for(File f : parent.listFiles()) {
			if(f.getName().contains(file)) {
				f.delete();
				return true;
			}
			else if(f.isDirectory()){
				 return findandremove(f.getAbsolutePath(), file);
			}
		}
		return false;
	}
	
}