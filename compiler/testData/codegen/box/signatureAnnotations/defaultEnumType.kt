// IGNORE_BACKEND: JS, NATIVE

// FILE: Signs.java
public enum Signs {
    HELLO,
    WORLD;
}

// FILE: B.kt
enum class B {
    X,
    Y;
}

// FILE: A.java
import kotlin.internal.*;

class A {
    public Signs a(@DefaultValue(value = "HELLO") Signs arg)  {
        return arg;
    }

    public B b(@DefaultValue(value = "Y") B arg) {
        return arg;
    }
}

// FILE: test.kt
fun box(): String {
    val a = A()
    if (a.a() != Signs.HELLO) {
        return "FAIL: enums Java"
    }

    if (a.b() != B.Y) {
        return "FAIL: enums Kotlin"
    }

    return "OK"
}
