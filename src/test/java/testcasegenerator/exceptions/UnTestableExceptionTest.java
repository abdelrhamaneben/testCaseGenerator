package testcasegenerator.exceptions;

import org.junit.Test;

import exceptions.UnTestableException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class UnTestableExceptionTest extends TestCase{

	@Test
	public void testConstructor()  {
		UnTestableException except = new UnTestableException();
		Assert.assertEquals("Impossible d'exécuter les tests unitaires.",except.getMessage());
		
		except = new UnTestableException("test");
		Assert.assertEquals("Impossible d'exécuter les tests unitaires : test",except.getMessage());
	}
}
