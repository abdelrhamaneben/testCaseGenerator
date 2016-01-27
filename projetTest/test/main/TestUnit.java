package main;


public @interface TestUnit {
    String given() default "";

    String params() default "";

    String oracle() default "";

    String[] withMock() default { "" };
}

