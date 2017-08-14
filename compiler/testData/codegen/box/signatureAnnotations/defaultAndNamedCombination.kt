// IGNORE_BACKEND: JS, NATIVE

// FILE: A.java
import kotlin.internal.*;

class A {
    public int first(@ParameterName(name = "first") @DefaultValue(value = "42") int a,
                     @ParameterName(name = "second") @DefaultValue(value = "1") int b) {
        return 100 * a + b;
    }
}

// FILE: main.kt
fun box(): String {
    val a = A()
    if (a.first() != 100 * 42 + 1) {
        return "FAIL 1"
    }

    if (a.first(second = 2) != 100 * 42 + 2) {
        return "FAIL 2"
    }

    if (a.first(first = 2) != 100 * 2 + 1) {
        return "FAIL 3"
    }

    if (a.first(second = 2, first = 5) != 100 * 5 + 2) {
        return "FAIL 4"
    }

    return "OK"
}
