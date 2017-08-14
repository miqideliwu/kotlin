// FILE: A.java
import kotlin.internal.*;

class A {
    public void first(@DefaultValue(value = "42") int arg) {
    }
}

// FILE: B.java
class B {
    public void first(int arg) {
    }
}

// FILE: C.java
import kotlin.internal.*;

class C extends A {
    public void first(@DefaultValue(value = "73") int arg) {
    }
}

// FILE: D.java
import kotlin.internal.*;

class D extends B {
    public void first(@DefaultValue(value = "37") int arg) {
    }
}

// FILE: E.java
import kotlin.internal.*;

class E extends A {
    public void first(int arg) {
    }
}

// FILE: F.kt
open class F {
    open fun foo(x: String = "0") {
    }
}

// FILE: G.java
class G extends F {
    public void foo(String y) {
    }
}

// FILE: K.java
import kotlin.internal.*;

public interface K {
    public void foo(@DefaultValue(value = "1") String x) { }
}

// FILE: L.java
import kotlin.internal.*;

public interface L {
    public void foo(@DefaultValue(value = "1") String x) { }
}

// FILE: M.java
public class M implements K, L {
    public void foo(String x) {
    }
}

// FILE: main.kt
fun main() {
    val a = A()
    val c = C()
    val d = D()
    val e = E()

    val ac: A = C()
    val bd: B = D()

    a.first()
    c.first()
    ac.first()

    d.first(<!NO_VALUE_FOR_PARAMETER!>)<!>
    bd.first(<!NO_VALUE_FOR_PARAMETER!>)<!>

    e.first()

    val g = G()
    g.foo()
    g.foo("ok")

    val m = M()
    m.foo(<!NO_VALUE_FOR_PARAMETER!>)<!>

}

