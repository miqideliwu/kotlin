// IGNORE_BACKEND: JS, NATIVE

// FILE: A.java
import kotlin.internal.*;

public class A {
    public Long first(@DefaultValue(value = "0x1F") Long value) {
        return value;
    }

    public Long second(@DefaultValue(value = "0X1F") Long value) {
        return value;
    }

    public Long third(@DefaultValue(value = "0b1010") Long value) {
        return value;
    }

    public Long fourth(@DefaultValue(value = "0B1010") Long value) {
        return value;
    }
}

// FILE: test.kt
fun box(): String {
    val a = A()

    if (a.first() != 0x1F.toLong()) {
        return "FAIL 1"
    }

    if (a.second() != 0x1F.toLong()) {
        return "FAIL 2"
    }

    if (a.third() != 0b1010.toLong()) {
        return "FAIL 3"
    }

    if (a.fourth() != 0b1010.toLong()) {
        return "FAIL 4"
    }

    return "OK"
}

