package testcasegenerator.exceptions;

import org.junit.Test;

import exceptions.UnSupportableConfig;
import junit.framework.Assert;
import junit.framework.TestCase;

public class UnSupportableConfigTest extends TestCase{

	@Test
	public void testConstructor()  {
		UnSupportableConfig except = new UnSupportableConfig();
		Assert.assertEquals("Impossible de lire le fichier de configuration.",except.getMessage());
		
		except = new UnSupportableConfig("test");
		Assert.assertEquals("Impossible de lire le fichier de configuration, erreur au niveau de la propriété :test",except.getMessage());
	}
}
