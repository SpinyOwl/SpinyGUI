package com.spinyowl.spinygui.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassKeyMapTest {

  // Key classes
  private static class A {}
  private static class B extends A {}
  private static class C extends A {}
  private static class D extends B {}
  private static class E extends B {}
  private static class F extends B {}
  private static class G extends D {}
  private static class H extends E {}
  private static class I extends A {}

  // Value classes
  private static abstract class P<T extends A> {
    String execute(T source) { return source.getClass().getSimpleName() + this.getClass().getSimpleName(); }
  }
  private static class Q extends P<A> {}
  private static class R extends P<B> {}
  private static class S extends P<C> {}
  private static class T extends P<D> {}
  private static class U extends P<E> {}

  private final ClassKeyMap<A, P<? extends A>> classKeyMap = new ClassKeyMap<>(A.class);

  @BeforeEach
  void setUp() {
    classKeyMap.put(A.class, new Q());
    classKeyMap.put(B.class, new R());
    classKeyMap.put(C.class, new S());
    classKeyMap.put(D.class, new T());
    classKeyMap.put(E.class, new U());
  }

  @Test
  void testClassKeyMap_for_A_returns_Q_andExecutionIs_AQ() {
    test("AQ", new A());
  }

  @Test
  void testClassKeyMap_for_B_returns_R_andExecutionIs_BR() {
    test("BR", new B());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_CS() {
    test("CS", new C());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_DT() {
    test("DT", new D());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_EU() {
    test("EU", new E());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_FR() {
    test("FR", new F());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_GT() {
    test("GT", new G());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_HU() {
    test("HU", new H());
  }

  @Test
  void testClassKeyMap_for_C_returns_S_andExecutionIs_IQ() {
    test("IQ", new I());
  }

  private void test(String expected, A source) {
    P processor = classKeyMap.recursiveGet(source.getClass());
    String actual = processor.execute(source);
    Assertions.assertEquals(expected, actual);
  }
}