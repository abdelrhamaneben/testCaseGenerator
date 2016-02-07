package exceptions;

/**
 * 
 * @author Abdelrhamane Benhammou
 *
 */
public class UnSupportableConfig extends Exception{
	public UnSupportableConfig(String PropertyName) {
		super("Impossible de lire le fichier de configuration, erreur au niveau de la propriété :" + PropertyName);
	}
	
	public UnSupportableConfig() {
		super("Impossible de lire le fichier de configuration.");
	}
}
