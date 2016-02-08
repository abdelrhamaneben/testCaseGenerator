package main;


@main.TestUnit
public class ExampleOne {
    private int a;

    private int b;

    private main.ExampleTwo exampleMock;

    public ExampleOne(int a ,int b) {
        this.a = a;
        this.b = b;
        this.exampleMock = new main.ExampleTwo();
    }

    @main.TestUnit(given = "1,2", oracle = "3" , when = "int c = instance.addition()", actual = "c")
    public int addition() {
        return (a) + (b);
    }

    public int getTwo() {
       return a + b;
    }
    
    @main.TestUnit(given = "-3,1", oracle = "-5")
    @main.TestUnit(given = "3,1", oracle = "2")
    public int soustraction() {
        return (a) - (b);
    }

    @main.TestUnit(given = "3,1", oracle = "3")
    public int getA() {
        return a;
    }

    @main.TestUnit(given = "3,1", oracle = "1")
    public int getB() {
        return b;
    }

    @main.TestUnit(given = "2,3", oracle = "5", withMock = { "exampleMock.getBool(), true" })
    public int additionOnlyIfTrue() {
        if (exampleMock.getBool()) {
            return (a) + (b);
        } else {
            return -1;
        }
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setB(int b) {
        this.b = b;
    }
}

