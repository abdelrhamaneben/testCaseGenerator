package generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import utils.Constants;

/**
 * @author Julia Leven
 * @author Edmond Van-overtveldt
 * @author Jérôme Garcia
 * @author Abdelrhamane Benhammou
 */
public class GetSrcProject extends AbstractProcessor<CtClass<?>> {
	
	/**
	 * Cette méthode récupà¨re la liste des nouveaux types créés
	 * @return la liste des nouveaux types créés
	 */
	public List<CtType<?>> getListCreatedType() {
		return getFactory().Type().getAll();
	}
	
	/**
	 * Cette méthode permet de savoir si la classe est une classe de tests
	 * @param classe la classe
	 * @return true la classe est une classe de tests
	 */
	public boolean isTestClass(CtClass<?> classe) {
		return ((classe.getSuperclass() != null ) && (classe.getSuperclass().getQualifiedName().equals("junit.framework.TestCase")));
	}
	
	/**
	 * Cette méthode récupà¨re la liste des nouveaux types créés qui ne sont pas des interfaces et n'implémentent pas d'interface
	 * @return les nouveaux types créés qui ne sont pas des interfaces et n'implémentent pas d'interface
	 */
	public List<CtTypeReference<?>> getListTypeAndNotInterface() {
		List<CtTypeReference<?>> newListType = new ArrayList<CtTypeReference<?>>();
		List<CtType<?>> listType = getListCreatedType();
		// Parcours de tous les types créés 
		for (CtType<?> t : listType) {
			CtTypeReference<?> typeRef = t.getReference();
			// Le nouveau type créé n'est pas une interface et il n'implémente pas d'interface
			if ((!typeRef.isInterface()) && (typeRef.getSuperInterfaces().size() == 0) ) {
				newListType.add(typeRef);
			}
		}
		return newListType;
	}
	
	/**
	 * Cette méthode permet d'ajouter "implements ..." à  une classe
	 * @param classe  : la classe dans laquelle on ajoute "implements"
	 */
	public void implementsInterface(CtClass<?> classe) {
		String nameInterface = classe.getSimpleName();
		CtInterface<?> newInterface = getFactory().Core().createInterface();
		newInterface.setSimpleName(nameInterface);
		Set<CtTypeReference<?>> setInterface = new HashSet<CtTypeReference<?>>();
		setInterface.add(newInterface.getReference());
		classe.setSuperInterfaces(setInterface);
	}
	
	/**
	 * Cette méthode ajoute toutes les signatures des méthodes dans une interface
	 * @param classe la classe qui va implémenter la nouvelle interface
	 * @param newInterface la nouvelle interface
	 * @param modifier
	 * @param pck le package de l'interface
	 */
	public void addMethodToInterface(CtClass<?> classe, CtInterface<?> newInterface) {
		Set<ModifierKind> smk = new HashSet<ModifierKind>();
		smk.add(ModifierKind.PUBLIC);
		for (CtMethod<?> m : classe.getAllMethods()) {
			getFactory().Method().create(newInterface, smk, m.getType(), m.getSimpleName(), m.getParameters(), null);	
		}
	}
	
	/**
	 * Créer une interface.
	 * 
	 * /!\ Si un package est précisé, l'interface sera créée 2 fois => pb à  régler /!\
	 * 
	 * @param modifier
	 * @param pck
	 */
	public void createInterface(CtClass<?> classe, ModifierKind modifier, CtPackage pck) {
		String nameInterface = classe.getSimpleName();
		
		//Création de l'interface
		CtInterface<?> newItf = getFactory().Core().createInterface();
		newItf.setParent(pck);
		newItf.setSimpleName(nameInterface);
		newItf.addModifier(modifier);

		// Ajout des méthodes dans l'interface
		//newItf.compileAndReplaceSnippets()
		addMethodToInterface(classe,newItf);
	}
	
	/**
	 * Cette méthode permet de créer l'interface d'une classe, en y ajoutant les signatures des méthodes
	 * @param attribut l'attribut.
	 */
	public void createAndImplementsInterface(CtField<?> attribut) {
		CtClass<?> classeImplem = getFactory().Class().get(attribut.getType().getQualifiedName());
		createInterface(classeImplem, ModifierKind.PUBLIC, classeImplem.getPackage());
		//implementsInterface(classeImplem);
	}
	
	/**
	 * Regarde pas c'est encore tout dégueu' !
	 */
	@Override
	public void process(CtClass<?> classe) {
		// Ce n'est pas une classe de tests
		if (!isTestClass(classe)) {
			List<CtField<?>> attributes = classe.getFields();
			List<CtTypeReference<?>> listType = getListTypeAndNotInterface();
			// Création de l'interface si le type de l'attribut est un nouveau type rencontré
			for (CtField<?> attribut : attributes) {
				if (listType.contains(attribut.getType())) {
					createAndImplementsInterface(attribut);

					CtTypeReference typeRef = getFactory().Type().createReference(attribut.getType().getSimpleName()); 
					
					for (CtConstructor<?> c : classe.getConstructors()) {
						for (CtParameter<?> param : c.getParameters()) {
							if (param.getType().getSimpleName().equals(attribut.getType().getSimpleName())) {
								param.setType(typeRef);
							}
						}
					}
					
					for (CtMethod<?> m : classe.getMethods()) {
						for (CtParameter<?> param : m.getParameters()) {
							if (param.getType().getSimpleName().equals(attribut.getType().getSimpleName())) {
								param.setType(typeRef);
							}
						}
						if (m.getType().getSimpleName().equals(attribut.getType().getSimpleName())) {
							m.setType(typeRef);
						}
					}
					attribut.setType(typeRef);
				}
			} 
		}
		deleteAnnotation(classe);
		for(CtMethod m : classe.getAllMethods()) {
			deleteAnnotation(m);
		}
	}
	
	/**
	 * @param element
	 *
	 */
	public void deleteAnnotation(CtElement element){
		ArrayList<CtAnnotation<?>> tmpAnnotationList = new ArrayList<CtAnnotation<?>>();
		
		for (CtAnnotation<?> a : element.getAnnotations()) {
			tmpAnnotationList.add(a);
		}
		
		for (CtAnnotation<?> a : tmpAnnotationList) {
			element.removeAnnotation(a);
		}
	}
	
	public static void main(String[] args) throws Exception {
		spoon.Launcher.main(new String[] {
				"--compile","-p",  GetSrcProject.class.getCanonicalName(),"-i",Constants.source,"-o", Constants.tmpFolder , "-d" , Constants.tmpFolder
        });
	}

}