package main;


@main.TestUnit
public class ExampleThree {
    private java.lang.String s;

    private char c;

    public ExampleThree(java.lang.String s ,char c) {
        this.s = s;
        this.c = c;
    }

    @main.TestUnit(given = "\"azer\",\'j\'", oracle = "\"azerty\"", params = "\"ty\"")
    public java.lang.String concatWithString(java.lang.String s2) {
        return ((s) + "") + s2;
    }

    @main.TestUnit(given = "\"azerty\",\'j\'", oracle = "\"azerty\"")
    public java.lang.String getS() {
        return s;
    }

    @main.TestUnit(given = "\"azerty\",\'j\'", oracle = "\'j\'")
    public char getC() {
        return c;
    }

    public void setS(java.lang.String s) {
        this.s = s;
    }

    public void setC(char c) {
        this.c = c;
    }
}

