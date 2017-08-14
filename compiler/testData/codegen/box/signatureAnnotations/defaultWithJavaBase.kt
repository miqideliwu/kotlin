// IGNORE_BACKEND: JS, NATIVE

// FILE: A.java
import kotlin.internal.DefaultValue;

public class A {
    public int x(@DefaultValue(value = "42") int x) {
        return x;
    }
}

// FILE: B.kt
class B : A() {
    override fun x(x: Int): Int = x + 1
}

// FILE: box.kt
fun box(): String {
    if (B().x() != 43) {
        return "FAIL"
    }

    return "OK"
}
