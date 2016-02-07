package main;

import java.lang.annotation.Repeatable;

@Repeatable(TestUnits.class)
public @interface TestUnit {
    java.lang.String given() default "";

    java.lang.String params() default "";

    java.lang.String oracle() default "";

    java.lang.String when() default "";
    
    java.lang.String actual() default "";

    java.lang.String[] withMock() default { "" };
}

