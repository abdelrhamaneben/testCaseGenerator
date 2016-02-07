package testcasegenerator.utils;

import org.junit.Test;

import exceptions.UnSupportableConfig;
import junit.framework.Assert;
import junit.framework.TestCase;
import utils.Constants;

public class ConstantsTest extends TestCase{
	@Test
	public void testConstructor()  {
		try{
			Constants.ready();
			Assert.fail();
		}catch(UnSupportableConfig e) {
			Assert.assertTrue("Léve bien l'exception UnSupportableConfig sans junit , mockito , lib  et source", true);
		}
		Constants.junit = "test";
		try{
			Constants.ready();
			Assert.fail();
		}catch(UnSupportableConfig e) {
			Assert.assertTrue("Léve bien l'exception UnSupportableConfig sans mockito , lib  et source", true);
		}
		//   , 
		Constants.mockito = "test";
		try{
			Constants.ready();
			Assert.fail();
		}catch(UnSupportableConfig e) {
			Assert.assertTrue("Léve bien l'exception UnSupportableConfig sans lib  et source", true);
		}
		Constants.source = "test";
		try{
			Constants.ready();
			Assert.fail();
		}catch(UnSupportableConfig e) {
			Assert.assertTrue("Léve bien l'exception UnSupportableConfig sans source", true);
		}
		Constants.lib = "lk";
		try{
			Constants.ready();
			Assert.assertTrue("ne léve pas l'exception", true);
		}catch(UnSupportableConfig e) {
			Assert.fail();
		}
	}
}
