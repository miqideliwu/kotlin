// IGNORE_BACKEND: JS, NATIVE

// FILE: A.java
import kotlin.internal.*;

class A {
    public static String withDefault(@DefaultValue(value = "OK") arg) {
        return arg;
    }
}

// FILE: test.kt
fun box(): String {
    return A.withDefault();
}
