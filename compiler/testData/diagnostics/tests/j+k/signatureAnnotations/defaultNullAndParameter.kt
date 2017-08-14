
// FILE: A.java
import kotlin.internal.*;

public class A {
    public void foo(@DefaultNull Integer i) {}

    public void bar(@DefaultNull Integer a) {}

    public void bam(@DefaultNull Integer a) {}

    public void baz(@DefaultValue(value = "42") Integer a) {}
}

// FILE: AInt.java
import kotlin.internal.*;

public interface AInt {
    public void foo(@DefaultValue(value = "42") Integer i) {}
    public void bar(@DefaultNull Integer a) {}
}

// FILE: B.java
import kotlin.internal.*;

public class B extends A {
    public void foo(Integer i) {}

    public void bar(@DefaultValue(value = "42") Integer a) {}

    public void bam(@DefaultNull @DefaultValue(value = "42") Integer a) {}
}

// FILE: C.java
public class C extends A implements AInt {
}

// FILE: test.kt

fun test(b: B, c: C) {
    b.foo()
    b.foo(5)
    b.bar()
    b.bar(5)
    b.bam()
    b.bam(5)

    c.foo(<!NO_VALUE_FOR_PARAMETER!>)<!>
    c.foo(5)
    c.bar(<!NO_VALUE_FOR_PARAMETER!>)<!>
    c.bar(5)
    c.bam()
    c.bam(5)
    c.baz()
    c.baz(42)
}
