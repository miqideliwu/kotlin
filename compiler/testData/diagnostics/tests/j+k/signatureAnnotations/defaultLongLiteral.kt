// FILE: A.java
import kotlin.internal.*;

public class A {
    public void first(@DefaultValue(value = "0x1F") Long value) {
    }

    public void second(@DefaultValue(value = "0X1F") Long value) {
    }

    public void third(@DefaultValue(value = "0b1010") Long value) {
    }

    public void fourth(@DefaultValue(value = "0B1010") Long value) {
    }
}

// FILE: B.java
import kotlin.internal.*;

public class B {
    public void first(@DefaultValue(value = "0x") Long value) {
    }

    public void second(@DefaultValue(value = "0xZZ") Long value) {
    }

    public void third(@DefaultValue(value = "0b") Long value) {
    }

    public void fourth(@DefaultValue(value = "0B1234") Long value) {
    }
}

// FILE: test.kt
fun main(a: A, b: B) {
    a.first()
    a.second()
    a.third()
    a.fourth()

    b.first(<!NO_VALUE_FOR_PARAMETER!>)<!>
    b.second(<!NO_VALUE_FOR_PARAMETER!>)<!>
    b.third(<!NO_VALUE_FOR_PARAMETER!>)<!>
    b.fourth(<!NO_VALUE_FOR_PARAMETER!>)<!>
}

