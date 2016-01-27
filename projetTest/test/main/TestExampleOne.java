package main;

import org.junit.Before;
import org.mockito.Mock;
import org.junit.Test;
import junit.framework.TestCase;

public class TestExampleOne extends TestCase {
    @Mock
    private ExampleTwo exampleMock;

    @Test
    public void testAddition() {
        instExampleOne = new ExampleOne(1, 2);
		junit.framework.Assert.assertEquals(instExampleOne.addition(),3);
    }

    @Test
    public void testSoustraction() {
        instExampleOne = new ExampleOne(3, 1);
		junit.framework.Assert.assertEquals(instExampleOne.soustraction(),2);
    }

    @Test
    public void testGetA() {
        instExampleOne = new ExampleOne(3, 1);
		junit.framework.Assert.assertEquals(instExampleOne.getA(),3);
    }

    @Test
    public void testGetB() {
        instExampleOne = new ExampleOne(3, 1);
		junit.framework.Assert.assertEquals(instExampleOne.getB(),1);
    }

    @Test
    public void testAdditionOnlyIfTrue() {
        instExampleOne = new ExampleOne(2, 3);
		org.mockito.Mockito.when(exampleMock.getBool()).thenReturn( true);
		junit.framework.Assert.assertEquals(instExampleOne.additionOnlyIfTrue(),5);
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public ExampleOne instExampleOne;

    @Before
    public Void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }
}

