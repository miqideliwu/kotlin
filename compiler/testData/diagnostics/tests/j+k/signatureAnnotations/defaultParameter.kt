// FILE: A.java

import kotlin.internal.*;

class A {
    public void first(@DefaultValue(value = "hello") String value) {
    }

    public void second(@DefaultValue(value = "first") String a, @kotlin.internal.DefaultValue(value = "second") String b) {
    }

    public void third(@DefaultValue(value = "first") String a, String b) {
    }

    public void fourth(String first, @DefaultValue(value = "second") String second) {
    }

    public void wrong(@DefaultValue(value = "hello") Integer i) {
    }
}


// FILE: test.kt
fun main() {
    val a = A()

    a.first()
    a.first("arg")

    a.second()
    a.second("arg")
    a.second("first", "second")

    a.third("OK"<!NO_VALUE_FOR_PARAMETER!>)<!>
    a.third("first", "second")

    a.fourth(<!NO_VALUE_FOR_PARAMETER!>)<!>
    a.fourth("first")
    a.fourth("first", "second")

    a.wrong(<!NO_VALUE_FOR_PARAMETER!>)<!>
    a.wrong(42)
}

