package exceptions;

/**
 * 
 * @author Abdelrhamane Benhammou
 *
 */
public class UnTestableException extends Exception{
	public UnTestableException(String msg) {
		super("Impossible d'exécuter les tests unitaires : " + msg);
	}
	
	public UnTestableException() {
		super("Impossible d'exécuter les tests unitaires.");
	}
}