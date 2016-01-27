package main;

import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;

public class TestExampleThree extends TestCase {
    @Test
    public void testConcatWithString() {
        instExampleThree = new ExampleThree("azer", 'j');
		junit.framework.Assert.assertEquals(instExampleThree.concatWithString("ty"),"azerty");
    }

    @Test
    public void testGetS() {
        instExampleThree = new ExampleThree("azerty", 'j');
		junit.framework.Assert.assertEquals(instExampleThree.getS(),"azerty");
    }

    @Test
    public void testGetC() {
        instExampleThree = new ExampleThree("azerty", 'j');
		junit.framework.Assert.assertEquals(instExampleThree.getC(),'j');
    }

    public void setS(String s) {
        this.s = s;
    }

    public void setC(char c) {
        this.c = c;
    }

    public ExampleThree instExampleThree;

    @Before
    public Void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }
}

