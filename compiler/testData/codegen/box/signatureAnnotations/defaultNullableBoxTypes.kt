// IGNORE_BACKEND: JS, NATIVE

// FILE: A.java
import kotlin.internal.*;
import org.jetbrains.annotations.*;

public class A {

    public Integer a(@Nullable @DefaultValue(value = "42") Integer arg) {
        return arg;
    }

    public Float b(@Nullable @DefaultValue(value = "42.5") Float arg) {
        return arg;
    }

    public Boolean c(@Nullable @DefaultValue(value = "true") Boolean arg) {
        return arg;
    }

    public Byte d(@Nullable @DefaultValue(value = "42") Byte arg) {
        return arg;
    }

    public Character e(@Nullable @DefaultValue(value = "o") Character arg) {
        return arg;
    }

    public Double f(@Nullable @DefaultValue(value = "1e12") Double arg) {
        return arg;
    }

    public Long g(@Nullable @DefaultValue(value = "42424242424242") Long arg) {
        return arg;
    }

    public Short h(@Nullable @DefaultValue(value = "123") Short arg) {
        return arg;
    }
}

// FILE: test.kt
fun box(): String {
    val a = A()

    if (a.a() != 42) {
        return "FAIL Int: ${a.a()}"
    }

    if (a.b() != 42.5f) {
        return "FAIL Float: ${a.b()}"
    }

    if (!a.c()) {
        return "FAIL Boolean: ${a.c()}"
    }

    if (a.d() != 42.toByte()) {
        return "FAIL Byte: ${a.d()}"
    }

    if (a.e() != 'o') {
        return "FAIL Char: ${a.e()}"
    }

    if (a.f() != 1e12) {
        return "FAIl Double: ${a.f()}"
    }

    if (a.g() != 42424242424242) {
        return "FAIL Long: ${a.g()}"
    }

    if (a.h() != 123.toShort()) {
        return "FAIL Short: ${a.h()}"
    }

    return "OK"
}