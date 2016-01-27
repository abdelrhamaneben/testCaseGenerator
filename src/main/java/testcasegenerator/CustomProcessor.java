package testcasegenerator;

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
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * @author Julia Leven
 * @author Edmond Van-overtveldt
 * @author Jérà´me Garcia
 */
public class CustomProcessor extends AbstractProcessor<CtClass<?>> {
	
	/**
	 *  Cette méthode supprime toutes les méthodes de la classe créée.
	 * @param classe la classe
	 */
	public void removeAllMethods(CtClass<?> classe) {
		Set<CtMethod<?>> methods = classe.getMethods();
				
		// permet de récupérer toutes les méthodes à  supprimer. 
		ArrayList<CtMethod<?>> test = new ArrayList<CtMethod<?>>();
		
		for (CtMethod<?> m : methods) {
			// ajout de la méthode dans la liste des méthodes à  supprimer
			test.add(m);
		}
		
		// suppression des méthodes du corps de la classe créée.
		for (CtMethod<?> m : test) {

			if (m.getAnnotations().size() == 0) {
				System.out.println("méthode :"+m.getSimpleName());
				classe.removeMethod(m);
			}
			else {
				System.out.println("méthode :"+m.getSimpleName()+" "+m.getAnnotations().getClass());
				m.setSimpleName("test"+m.getSimpleName());
				List<CtAnnotation<?>> emptyListAnnotation = new ArrayList<CtAnnotation<?>>();
				m.setAnnotations(emptyListAnnotation);
				
				CtBlock emptyBlock = getFactory().Core().createBlock();
				CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
				snippet.setValue("return null");
				emptyBlock.insertBegin(snippet);
				m.setBody(emptyBlock);
				

			}
		}
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
	 * Ajoute une méthode à la classe passée en paramètre.
	 * @param classe
	 * @param modifiers : public/protected/private et/ou final/static etc...
	 * @param type : type de retour de la méthode
	 * @param name : nom de la méthode
	 * @param body : corps de la méthode
	 */
	public void addMethod(CtClass<?> classe, Set<ModifierKind> modifiers, CtTypeReference<?> type, String name, String body) {
		CtBlock block = getFactory().Core().createBlock();
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		snippet.setValue(body);
		block.insertBegin(snippet);
		CtMethod m = getFactory().Method().create(classe, modifiers, type, name, null, null, block);
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
	
	/**
	 * Cette méthode permet d'initialiser l'objet dans une méthode de la classe passée en paramà¨tre
	 * @param classe le nom de la classe à  tester 
	 * @return une chaà®ne de caractà¨re permettant d'initialiser l'objet à  tester
	 */
	public String initConstructor(String nameClasse,CtMethod<?> m) {
		String body = "inst"+nameClasse+" = new "+nameClasse+"(";
		String stringParam = "";
		if (m.getAnnotations().get(0).getSignature().equals("@main.TestUnit")) {
			stringParam = m.getAnnotations().get(0).getElementValue("init");
			String[] initTab = stringParam.split(",");
			for (int i = 0; i <  initTab.length; i++) {
				body += initTab[i];
				if (i != initTab.length-1)
					body += ", ";
			}
			body += ")";
		}
		return body;
	}
	
	/**
	 * Créer un bloc contenant les tests
	 * @param nameClasse
	 * @param m
	 * @return
	 */
	public String initTestBody(String nameClasse, CtMethod<?> m) {
		String params = "";
		String oracle ="";
		String body = "";
		
		for (CtAnnotation<?> annotation : m.getAnnotations()) {
			if (annotation.getSignature().equals("@main.TestUnit")) {
				//PARAMS
				params = annotation.getElementValue("params");
				ArrayList<CtParameter<?>> tmpParameters = new ArrayList<CtParameter<?>>(); 
				
				for (CtParameter<?> p : m.getParameters()) {
					tmpParameters.add(p);
				}
				
				for (CtParameter<?> p : tmpParameters) {
					m.removeParameter(p);
				}
				
				// ASSERT
				body = "junit.framework.Assert.assertEquals(inst"+nameClasse+"."+m.getSimpleName()+"("+params+"),";
				oracle = annotation.getElementValue("oracle");
				String[] initTab = oracle.split(",");
				for (int i = 0; i <  initTab.length; i++) {
					body += initTab[i];
					if (i != initTab.length-1)
						body += ", ";
				}
				body += ")";
				
				// WHEN
				String[] when = annotation.getElementValue("when");
				for (int i = 0; i < when.length; i++) {
					String[] tmp = when[i].split(",");
					if (tmp.length > 1) 
					body = "org.mockito.Mockito.when("+tmp[0]+").thenReturn("+tmp[1]+");\n\t\t" + body;
				}
				
			}
		}
		return body;
	}
	
	/**
	 * Insà¨re le code nécessaire dans le corps de la méthode.
	 * @param nameClasse
	 * @param m
	 */
	public void setBodyMethodToTest(String nameClasse, CtMethod<?> m) {
		CtBlock emptyBlock = getFactory().Core().createBlock();
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		snippet.setValue(initConstructor(nameClasse,m)+";\n\t\t"+initTestBody(nameClasse, m));
		emptyBlock.insertBegin(snippet);
		m.setBody(emptyBlock);
	}
	
	/**
	 * Cette méthode supprime toutes les méthodes qui ne doivent pas àªtre testée de la classe créée 
	 * et change le contenu des méthodes qui doivent àªtre testée.
	 * @param classe la classe de tests
	 * @param le nom de la classe à  tester
	 * @return la liste des méthodes qui devront àªtre testées
	 */
	public Set<CtMethod<?>> setBodyClass(CtClass<?> classe, String nameClassTest) {
		// On récupà¨re toutes les méthodes
		Set<CtMethod<?>> methodsStart = classe.getMethods();
		ArrayList<CtMethod<?>> test = new ArrayList<CtMethod<?>>();
		
		for (CtMethod<?> m : methodsStart) {
			// ajout de la méthode dans la liste des méthodes à  supprimer
			test.add(m);
		}
		
		for (CtMethod<?> m : test) {
			// Une annotation est disponible, c'est une méthode à  tester
			if ((m.getAnnotations().size() > 0) && (m.getAnnotations().get(0).getSignature().equals("@main.TestUnit"))) {
				setBodyMethodToTest(nameClassTest, m);
				
				// Ajout de l'annotation test */
				addAnnotation(m,"org.junit.Test");
				
				// Modification du type de retour ainsi que du nom de la méthode
				m.setSimpleName("test"+m.getSimpleName().substring(0, 1).toUpperCase()+m.getSimpleName().substring(1, m.getSimpleName().length()));
				CtTypeReference voidType = getFactory().Type().VOID_PRIMITIVE;
				m.setType(voidType);
				
			// Pas d'annotation : ce n'est pas une méthode  à  tester 
			} else {
				classe.removeMethod(m);
			}
		}
		return methodsStart;
	}

	@Override
	public void process(CtClass<?> classe) {
		classe.getFactory().getEnvironment().setAutoImports(true);
		
		ArrayList<CtAnnotation<?>> tmpAnnotationList = new ArrayList<CtAnnotation<?>>();
		
		for (CtAnnotation<?> a : classe.getAnnotations()) {
			tmpAnnotationList.add(a);
		}
		
		for (CtAnnotation<?> annotation : tmpAnnotationList) {
			if (annotation.getSignature().equals("@main.TestUnit")) {
				classe.removeAnnotation(annotation);
				// Récupération du nom de la classe que l'on souhaite tester
				String nameClass = classe.getSimpleName();
				
				// Changer nom du fichier créé :
				classe.setSimpleName("Test"+nameClass);
				
				// suppression des attributs
				setAttributesClasseTest(classe);
				
				removeAllConstructors(classe);
				
				// étend la classe TestCase
				extendsTestCase(classe);


				// Set composé du modifier "public"
				Set<ModifierKind> smk = new HashSet<ModifierKind>();
				smk.add(ModifierKind.PUBLIC);
				
				// Type "ExampleTest"
				CtTypeParameterReference testType = getFactory().Type().createTypeParameterReference(nameClass);
				
				// ajout d'un attribut
				addField(classe, smk, testType, "inst"+nameClass);
				
				// ajout d'une méthode
				String mockitoInitMocks = "org.mockito.MockitoAnnotations.initMocks(this)";
				addMethod(classe, smk, getFactory().Type().VOID, "setUp", mockitoInitMocks);
				
				// Recherche de la méthode "setup" et ajout de l'annotation
				for (CtMethod<?> m : classe.getAllMethods()) {
					if (m.getSimpleName().equals("setUp")) {
						addAnnotation(m, "org.junit.Before");
					}
				}
				
				// modification du contenu des méthodes annotées
				setBodyClass(classe,nameClass);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		spoon.Launcher.main(new String[] {
        		"-p", "testcasegenerator.CustomProcessor","-i",args[0],"-o", args[1]
        });
		
//		spoon.Launcher.main(new String[] {
//        		"-p", "spoon.CustomProcessor","-i","exampleProject/main","-o", "test"
//        });
	}
	
}