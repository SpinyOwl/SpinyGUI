package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.PreparedRange;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodType;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.CodeElement;
import java.lang.classfile.MethodModel;
import java.lang.classfile.Opcode;
import java.lang.classfile.instruction.BranchInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.LabelTarget;
import java.lang.classfile.instruction.ReturnInstruction;
import java.lang.classfile.instruction.TypeCheckInstruction;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class FontServiceImplStructuralContractTest {
  private static final String FONT_SERVICE =
      "com/spinyowl/spinygui/core/system/font/impl/FontServiceImpl";
  private static final String INTERNAL_FONT =
      "com/spinyowl/spinygui/core/system/font/internal/";
  private static final String TEXT_METRICS =
      "com/spinyowl/spinygui/core/system/font/TextMetrics";
  private static final String TEXT_LINE_METRICS =
      "com/spinyowl/spinygui/core/system/font/TextLineMetrics";
  private static final String RESOLVED_TEXT_RUN =
      "com/spinyowl/spinygui/core/system/font/ResolvedTextRun";
  private static final String RESOLVED_GLYPH =
      "com/spinyowl/spinygui/core/system/font/ResolvedGlyph";
  private static final String FONT_METRICS =
      "com/spinyowl/spinygui/core/system/font/FontMetrics";
  private static final String TEXT_CARET_METRICS =
      "com/spinyowl/spinygui/core/system/font/TextCaretMetrics";
  private static final String RANGE_DESCRIPTOR =
      "(Ljava/lang/String;IIFLjava/util/List;FFFZ)"
          + "Lcom/spinyowl/spinygui/core/system/font/internal/ResolvedMeasurement;";
  private static final MethodKey PRODUCTION_RANGE =
      new MethodKey(FONT_SERVICE, "measureRange", RANGE_DESCRIPTOR);
  private static final MethodKey COMPLETE_MEASUREMENT =
      new MethodKey(
          FONT_SERVICE,
          "completeMeasurement",
          "(Lcom/spinyowl/spinygui/core/system/font/internal/PreparedRange;)"
              + "Lcom/spinyowl/spinygui/core/system/font/internal/ResolvedMeasurement;");
  private static final MethodKey PREPARE_RANGE =
      new MethodKey(
          FONT_SERVICE,
          "prepareRange",
          "(Lcom/spinyowl/spinygui/core/system/font/internal/PreparedRange;)"
              + "Lcom/spinyowl/spinygui/core/system/font/impl/"
              + "FontServiceImpl$PrivatePreparedMeasurement;");
  private static final MethodKey RESOLVE_PRIMITIVES =
      new MethodKey(
          FONT_SERVICE,
          "resolvePrimitives",
          "(Ljava/lang/String;IILjava/util/List;F)"
              + "Lcom/spinyowl/spinygui/core/system/font/impl/"
              + "FontServiceImpl$ResolvedPrimitiveSequence;");
  private static final MethodKey WRAP_PLAN =
      new MethodKey(FONT_SERVICE + "$PrivateWrapPlanner", "plan", "()Ljava/util/List;");
  private static final MethodKey PRIVATE_RESULT_FREEZE =
      new MethodKey(
          FONT_SERVICE + "$PrivateResultBuilder",
          "freeze",
          "()Lcom/spinyowl/spinygui/core/system/font/impl/"
              + "FontServiceImpl$PrivatePreparedMeasurement;");
  private static final MethodKey FINAL_MATERIALIZE =
      new MethodKey(
          FONT_SERVICE + "$FinalMeasurementMaterializer",
          "materialize",
          "()Lcom/spinyowl/spinygui/core/system/font/internal/ResolvedMeasurement;");
  private static final MethodKey FINAL_LINE_SNAPSHOT =
      new MethodKey(
          FONT_SERVICE + "$FinalMeasurementMaterializer",
          "materializeLine",
          "(Lcom/spinyowl/spinygui/core/system/font/impl/"
              + "FontServiceImpl$PrivatePreWrapLine;)V");
  private static final Invocation STRING_SUBSTRING =
      new Invocation("java/lang/String", "substring", "(II)Ljava/lang/String;");
  private static final Set<String> PRODUCTION_RESULT_OWNERS =
      Set.of(
          TEXT_METRICS,
          TEXT_LINE_METRICS,
          RESOLVED_TEXT_RUN,
          RESOLVED_GLYPH,
          FONT_METRICS,
          TEXT_CARET_METRICS);

  @Test
  void everyApprovedP1DecisionRowMapsToAnActiveExecutableFixture() throws Exception {
    Map<P1Decision, List<Fixture>> fixtures = approvedDecisionFixtures();

    assertEquals(EnumSet.allOf(P1Decision.class), fixtures.keySet());
    for (Map.Entry<P1Decision, List<Fixture>> entry : fixtures.entrySet()) {
      assertFalse(entry.getValue().isEmpty(), entry.getKey() + " must retain executable evidence");
      for (Fixture fixture : entry.getValue()) {
        Class<?> fixtureClass = Class.forName(fixture.className());
        Method method = fixtureClass.getDeclaredMethod(fixture.methodName());
        assertFalse(
            fixtureClass.isAnnotationPresent(Disabled.class), fixtureClass + " must remain active");
        assertNotNull(method.getAnnotation(Test.class), fixture + " must remain a JUnit test");
        assertFalse(method.isAnnotationPresent(Disabled.class), fixture + " must remain active");
      }
    }
  }

  @Test
  void productionRangeCallGraphRejectsRangeMaterializationOutsideFinalLineSnapshot()
      throws Exception {
    assertTrue(RangeTextMeasurerCapability.class.isAssignableFrom(FontServiceImpl.class));
    CallGraph graph = inspectProductionCallGraph(PRODUCTION_RANGE);

    assertTrue(graph.reachableMethods().contains(COMPLETE_MEASUREMENT));
    assertTrue(graph.reachableMethods().contains(PREPARE_RANGE));
    assertTrue(graph.reachableMethods().contains(RESOLVE_PRIMITIVES));
    assertTrue(graph.reachableMethods().contains(WRAP_PLAN));
    assertTrue(graph.reachableMethods().contains(PRIVATE_RESULT_FREEZE));
    assertTrue(graph.reachableMethods().contains(FINAL_MATERIALIZE));
    assertTrue(graph.reachableMethods().contains(FINAL_LINE_SNAPSHOT));
    assertEquals(
        List.of(new CallSite(FINAL_LINE_SNAPSHOT, STRING_SUBSTRING)),
        graph.approvedFinalSnapshots());
    assertTrue(
        graph.rangeMaterializationViolations().isEmpty(),
        () -> "production range materialization operations: " + graph.rangeMaterializationViolations());
  }

  @Test
  void adapterBytecodeReturnsCapabilityResultBeforeLegacyOnlySubstringFallback() throws Exception {
    String adapterOwner =
        "com/spinyowl/spinygui/core/system/font/internal/RangeTextMeasurerAdapter";
    MethodKey adapterMethod =
        new MethodKey(
            adapterOwner,
            "measureRange",
            "(Lcom/spinyowl/spinygui/core/system/font/TextMeasurer;"
                + "Ljava/lang/String;IIFLjava/util/List;FFFZ)"
                + "Lcom/spinyowl/spinygui/core/system/font/TextMetrics;");
    List<CodeElement> elements = methodModel(adapterMethod).code().orElseThrow().elementList();
    int capabilityCheck =
        indexOf(
            elements,
            element ->
                element instanceof TypeCheckInstruction check
                    && check.opcode() == Opcode.INSTANCEOF
                    && check.type().asInternalName().equals(INTERNAL_FONT + "RangeTextMeasurerCapability"));
    int fallbackBranch =
        indexOfAfter(
            elements,
            capabilityCheck,
            element -> element instanceof BranchInstruction branch && branch.opcode() == Opcode.IFEQ);
    BranchInstruction branch = (BranchInstruction) elements.get(fallbackBranch);
    int fallbackTarget =
        indexOfAfter(
            elements,
            fallbackBranch,
            element -> element instanceof LabelTarget label && label.label().equals(branch.target()));
    Invocation capabilityInvocation =
        new Invocation(INTERNAL_FONT + "RangeTextMeasurerCapability", "measureRange", RANGE_DESCRIPTOR);
    int capabilityCall = indexOfInvocation(elements, capabilityInvocation);
    Invocation capabilityMetrics =
        new Invocation(
            INTERNAL_FONT + "ResolvedMeasurement",
            "metrics",
            "()Lcom/spinyowl/spinygui/core/system/font/TextMetrics;");
    int capabilityMetricsCall = indexOfInvocation(elements, capabilityMetrics);
    int capabilityReturn =
        indexOfAfter(
            elements,
            capabilityMetricsCall,
            element -> element instanceof ReturnInstruction value && value.opcode() == Opcode.ARETURN);
    int legacySubstring = indexOfInvocation(elements, STRING_SUBSTRING);
    Invocation legacyMeasure =
        new Invocation(
            "com/spinyowl/spinygui/core/system/font/TextMeasurer",
            "measureText",
            "(Ljava/lang/String;FLjava/util/List;FFFZ)"
                + "Lcom/spinyowl/spinygui/core/system/font/TextMetrics;");
    int legacyMeasureCall = indexOfInvocation(elements, legacyMeasure);
    Invocation legacyTranslate =
        new Invocation(
            adapterOwner,
            "translate",
            "(Lcom/spinyowl/spinygui/core/system/font/TextMetrics;I)"
                + "Lcom/spinyowl/spinygui/core/system/font/TextMetrics;");
    int legacyTranslateCall = indexOfInvocation(elements, legacyTranslate);

    assertTrue(capabilityCheck < fallbackBranch);
    assertTrue(fallbackBranch < capabilityCall);
    assertTrue(capabilityCall < capabilityMetricsCall);
    assertTrue(capabilityMetricsCall < capabilityReturn);
    assertTrue(capabilityReturn < fallbackTarget);
    assertTrue(fallbackTarget < legacySubstring);
    assertTrue(legacySubstring < legacyMeasureCall);
    assertTrue(legacyMeasureCall < legacyTranslateCall);
    assertEquals(
        1L,
        elements.stream()
            .filter(InvokeInstruction.class::isInstance)
            .map(InvokeInstruction.class::cast)
            .map(Invocation::from)
            .filter(STRING_SUBSTRING::equals)
            .count());
  }

  @Test
  void publicResultConstructorsBuildersAccessorsAndRecordComponentsRemainExact() {
    assertExactPublicConstructors(
        TextMetrics.class,
        constructorDescriptor(List.class, float.class, float.class, float.class, FontMetrics.class));
    assertExactPublicMethods(
        TextMetrics.class,
        method("builder", TextMetrics.TextMetricsBuilder.class),
        method("equals", boolean.class, Object.class),
        method("hashCode", int.class),
        method("lines", List.class),
        method("width", float.class),
        method("height", float.class),
        method("lineHeight", float.class),
        method("fontMetrics", FontMetrics.class),
        method("toString", String.class));
    assertExactPublicMethods(
        TextMetrics.TextMetricsBuilder.class,
        method("line", TextMetrics.TextMetricsBuilder.class, TextLineMetrics.class),
        method("lines", TextMetrics.TextMetricsBuilder.class, Collection.class),
        method("clearLines", TextMetrics.TextMetricsBuilder.class),
        method("width", TextMetrics.TextMetricsBuilder.class, float.class),
        method("height", TextMetrics.TextMetricsBuilder.class, float.class),
        method("lineHeight", TextMetrics.TextMetricsBuilder.class, float.class),
        method("fontMetrics", TextMetrics.TextMetricsBuilder.class, FontMetrics.class),
        method("build", TextMetrics.class),
        method("toString", String.class));

    assertExactPublicConstructors(
        TextLineMetrics.class,
        constructorDescriptor(
            CharSequence.class,
            int.class,
            int.class,
            int.class,
            float.class,
            float.class,
            float.class,
            FontMetrics.class,
            List.class));
    assertExactPublicMethods(
        TextLineMetrics.class,
        method("builder", TextLineMetrics.TextLineMetricsBuilder.class),
        method("equals", boolean.class, Object.class),
        method("hashCode", int.class),
        method("characters", CharSequence.class),
        method("startIndex", int.class),
        method("endIndex", int.class),
        method("charCount", int.class),
        method("width", float.class),
        method("height", float.class),
        method("baseline", float.class),
        method("fontMetrics", FontMetrics.class),
        method("runs", List.class),
        method("toString", String.class));
    assertExactPublicMethods(
        TextLineMetrics.TextLineMetricsBuilder.class,
        method("characters", TextLineMetrics.TextLineMetricsBuilder.class, CharSequence.class),
        method("startIndex", TextLineMetrics.TextLineMetricsBuilder.class, int.class),
        method("endIndex", TextLineMetrics.TextLineMetricsBuilder.class, int.class),
        method("charCount", TextLineMetrics.TextLineMetricsBuilder.class, int.class),
        method("width", TextLineMetrics.TextLineMetricsBuilder.class, float.class),
        method("height", TextLineMetrics.TextLineMetricsBuilder.class, float.class),
        method("baseline", TextLineMetrics.TextLineMetricsBuilder.class, float.class),
        method("fontMetrics", TextLineMetrics.TextLineMetricsBuilder.class, FontMetrics.class),
        method("runs", TextLineMetrics.TextLineMetricsBuilder.class, List.class),
        method("build", TextLineMetrics.class),
        method("toString", String.class));

    assertExactPublicConstructors(
        ResolvedTextRun.class,
        constructorDescriptor(int.class, int.class, Font.class, List.class, float.class));
    assertExactRecordComponents(
        ResolvedTextRun.class,
        new Component("sourceStart", int.class),
        new Component("sourceEnd", int.class),
        new Component("font", Font.class),
        new Component("glyphs", List.class),
        new Component("advance", float.class));
    assertExactPublicMethods(
        ResolvedTextRun.class,
        method("replacementMarker", boolean.class),
        method("renderedText", String.class),
        method("equals", boolean.class, Object.class),
        method("hashCode", int.class),
        method("toString", String.class),
        method("sourceStart", int.class),
        method("sourceEnd", int.class),
        method("font", Font.class),
        method("glyphs", List.class),
        method("advance", float.class));
    assertExactPublicConstructors(
        ResolvedGlyph.class,
        constructorDescriptor(
            int.class, int.class, int.class, int.class, Font.class, boolean.class));
    assertExactRecordComponents(
        ResolvedGlyph.class,
        new Component("sourceStart", int.class),
        new Component("sourceEnd", int.class),
        new Component("sourceCodePoint", int.class),
        new Component("renderedCodePoint", int.class),
        new Component("font", Font.class),
        new Component("replacement", boolean.class));
    assertExactPublicMethods(
        ResolvedGlyph.class,
        method("equals", boolean.class, Object.class),
        method("hashCode", int.class),
        method("toString", String.class),
        method("sourceStart", int.class),
        method("sourceEnd", int.class),
        method("sourceCodePoint", int.class),
        method("renderedCodePoint", int.class),
        method("font", Font.class),
        method("replacement", boolean.class));
    assertExactPublicConstructors(
        FontMetrics.class,
        constructorDescriptor(
            float.class, float.class, float.class, float.class, float.class));
    assertExactPublicMethods(
        FontMetrics.class,
        method("equals", boolean.class, Object.class),
        method("hashCode", int.class),
        method("toString", String.class),
        method("ascent", float.class),
        method("descent", float.class),
        method("lineGap", float.class),
        method("lineHeight", float.class),
        method("baseline", float.class));
    assertExactPublicConstructors(
        TextCaretMetrics.class, constructorDescriptor(int.class, float.class));
    assertExactPublicMethods(
        TextCaretMetrics.class,
        method("equals", boolean.class, Object.class),
        method("hashCode", int.class),
        method("toString", String.class),
        method("charIndex", int.class),
        method("x", float.class));
  }

  @Test
  void measurementLocalMutableAccumulatorTypesArePrivateAndDoNotEscapePublicMethods() {
    Set<String> expectedNames =
        Set.of(
            "PrivateResultBuilder",
            "PrivateWrapPlanner",
            "FinalMeasurementMaterializer",
            "FinalRunBuilder",
            "PrivateLineBuilder",
            "ResolvedPrimitiveBuilder");
    Set<Class<?>> accumulators =
        Arrays.stream(FontServiceImpl.class.getDeclaredClasses())
            .filter(type -> expectedNames.contains(type.getSimpleName()))
            .collect(java.util.stream.Collectors.toUnmodifiableSet());

    assertEquals(
        expectedNames,
        accumulators.stream()
            .map(Class::getSimpleName)
            .collect(java.util.stream.Collectors.toUnmodifiableSet()));
    for (Class<?> accumulator : accumulators) {
      int modifiers = accumulator.getModifiers();
      assertTrue(Modifier.isPrivate(modifiers), accumulator + " must remain private");
      assertTrue(Modifier.isStatic(modifiers), accumulator + " must remain static");
      assertTrue(Modifier.isFinal(modifiers), accumulator + " must remain final");
      assertTrue(
          Arrays.stream(accumulator.getDeclaredConstructors())
              .allMatch(constructor -> Modifier.isPrivate(constructor.getModifiers())),
          accumulator + " constructors must remain private");
      assertTrue(
          Arrays.stream(accumulator.getDeclaredMethods())
              .allMatch(method -> Modifier.isPrivate(method.getModifiers())),
          accumulator + " methods must remain private");
      assertTrue(
          Arrays.stream(accumulator.getDeclaredFields())
              .filter(field -> Collection.class.isAssignableFrom(field.getType()))
              .allMatch(
                  field ->
                      Modifier.isPrivate(field.getModifiers())
                          && Modifier.isFinal(field.getModifiers())),
          accumulator + " collection storage must remain private final");
    }

    for (Method method : FontServiceImpl.class.getDeclaredMethods()) {
      if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
        assertFalse(accumulators.contains(method.getReturnType()), method + " exposes an accumulator");
        assertTrue(
            Arrays.stream(method.getParameterTypes()).noneMatch(accumulators::contains),
            method + " accepts an accumulator");
      }
    }
  }

  @Test
  void productionServiceRetainsNoPersistentTextMeasurementOrRangeCache() {
    Set<Class<?>> forbiddenRetainedTypes =
        Set.of(
            String.class,
            TextMetrics.class,
            TextLineMetrics.class,
            ResolvedTextRun.class,
            ResolvedGlyph.class,
            TextCaretMetrics.class,
            PreparedRange.class,
            ResolvedMeasurement.class,
            FinalLineCaretStops.class);
    Set<String> mapFields =
        Arrays.stream(FontServiceImpl.class.getDeclaredFields())
            .filter(field -> Map.class.isAssignableFrom(field.getType()))
            .map(field -> field.getName() + ":" + field.getGenericType().getTypeName())
            .collect(java.util.stream.Collectors.toUnmodifiableSet());

    assertEquals(
        Set.of(
            "fontInfoMap:java.util.Map<java.lang.String, org.lwjgl.stb.STBTTFontinfo>"),
        mapFields);
    assertTrue(
        Arrays.stream(FontServiceImpl.class.getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .noneMatch(
                field ->
                    forbiddenRetainedTypes.contains(field.getType())
                        || Collection.class.isAssignableFrom(field.getType())
                        || field.getType().isArray()),
        "FontServiceImpl must not retain source, range, result, collection, or array state");
  }

  private void assertExactPublicConstructors(Class<?> type, String... expectedDescriptors) {
    Set<String> actualDescriptors =
        Arrays.stream(type.getConstructors())
            .map(
                constructor ->
                    MethodType.methodType(void.class, constructor.getParameterTypes())
                        .descriptorString())
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    assertEquals(Set.of(expectedDescriptors), actualDescriptors, type.getName());
  }

  private void assertExactPublicMethods(Class<?> type, PublicMethod... expectedMethods) {
    Set<PublicMethod> actualMethods =
        Arrays.stream(type.getDeclaredMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .map(
                method ->
                    new PublicMethod(
                        method.getName(),
                        MethodType.methodType(
                                method.getReturnType(), method.getParameterTypes())
                            .descriptorString()))
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    assertEquals(Set.of(expectedMethods), actualMethods, type.getName());
  }

  private void assertExactRecordComponents(Class<?> type, Component... expectedComponents) {
    List<Component> actualComponents =
        Arrays.stream(type.getRecordComponents())
            .map(component -> new Component(component.getName(), component.getType()))
            .toList();
    assertEquals(List.of(expectedComponents), actualComponents, type.getName());
  }

  private String constructorDescriptor(Class<?>... parameterTypes) {
    return MethodType.methodType(void.class, parameterTypes).descriptorString();
  }

  private PublicMethod method(String name, Class<?> returnType, Class<?>... parameterTypes) {
    return new PublicMethod(
        name, MethodType.methodType(returnType, parameterTypes).descriptorString());
  }

  private CallGraph inspectProductionCallGraph(MethodKey root) throws Exception {
    Deque<MethodKey> pending = new ArrayDeque<>();
    Set<MethodKey> reachable = new HashSet<>();
    List<CallSite> approvedFinalSnapshots = new ArrayList<>();
    List<CallSite> violations = new ArrayList<>();
    pending.add(root);
    while (!pending.isEmpty()) {
      MethodKey caller = pending.removeFirst();
      if (!reachable.add(caller)) {
        continue;
      }
      MethodModel method = methodModel(caller);
      for (CodeElement element : method.code().orElseThrow().elementList()) {
        if (!(element instanceof InvokeInstruction instruction)) {
          continue;
        }
        Invocation invocation = Invocation.from(instruction);
        if (isRangeMaterialization(invocation)) {
          CallSite callSite = new CallSite(caller, invocation);
          if (caller.equals(FINAL_LINE_SNAPSHOT) && invocation.equals(STRING_SUBSTRING)) {
            approvedFinalSnapshots.add(callSite);
          } else {
            violations.add(callSite);
          }
        }
        if (isProductionHelperOwner(invocation.owner())) {
          pending.addLast(invocation.asMethodKey());
        }
      }
    }
    return new CallGraph(
        Set.copyOf(reachable), List.copyOf(approvedFinalSnapshots), List.copyOf(violations));
  }

  private boolean isProductionHelperOwner(String owner) {
    return owner.equals(FONT_SERVICE)
        || owner.startsWith(FONT_SERVICE + "$")
        || owner.startsWith(INTERNAL_FONT)
        || PRODUCTION_RESULT_OWNERS.contains(owner);
  }

  private boolean isRangeMaterialization(Invocation invocation) {
    if (invocation.owner().equals("java/lang/String")) {
      return invocation.name().equals("<init>")
          || invocation.name().equals("substring")
          || invocation.name().equals("subSequence")
          || invocation.name().equals("copyValueOf")
          || (invocation.name().equals("valueOf")
              && (invocation.descriptor().startsWith("([C")
                  || invocation.descriptor().startsWith("([B")))
          || invocation.name().equals("getChars")
          || invocation.name().equals("getBytes");
    }
    if (invocation.owner().equals("java/lang/CharSequence")) {
      return invocation.name().equals("subSequence");
    }
    if (invocation.owner().equals("java/util/Arrays")) {
      return invocation.name().equals("copyOfRange");
    }
    if (invocation.owner().equals("java/lang/StringBuilder")
        || invocation.owner().equals("java/lang/StringBuffer")
        || invocation.owner().equals("java/lang/AbstractStringBuilder")) {
      return invocation.name().equals("append")
          && (invocation.descriptor().contains("Ljava/lang/CharSequence;II")
              || invocation.descriptor().contains("[CII"));
    }
    return false;
  }

  private MethodModel methodModel(MethodKey key) throws Exception {
    ClassModel model = ClassFile.of().parse(classBytes(key.owner()));
    List<MethodModel> matches =
        model.methods().stream()
            .filter(method -> method.methodName().equalsString(key.name()))
            .filter(method -> method.methodType().equalsString(key.descriptor()))
            .toList();
    assertEquals(1, matches.size(), "exact bytecode method " + key);
    return matches.get(0);
  }

  private int indexOfInvocation(List<CodeElement> elements, Invocation expected) {
    return indexOf(
        elements,
        element ->
            element instanceof InvokeInstruction instruction
                && Invocation.from(instruction).equals(expected));
  }

  private int indexOf(List<CodeElement> elements, Predicate<CodeElement> predicate) {
    return indexOfAfter(elements, -1, predicate);
  }

  private int indexOfAfter(
      List<CodeElement> elements, int precedingIndex, Predicate<CodeElement> predicate) {
    for (int index = precedingIndex + 1; index < elements.size(); index++) {
      if (predicate.test(elements.get(index))) {
        return index;
      }
    }
    throw new AssertionError("Required bytecode element was not found after " + precedingIndex);
  }

  private Map<P1Decision, List<Fixture>> approvedDecisionFixtures() {
    String contract = FontServiceImplMeasurementContractTest.class.getName();
    String input = "com.spinyowl.spinygui.core.system.input.TextInputBehaviorTest";
    String textarea = "com.spinyowl.spinygui.core.system.input.TextareaBehaviorTest";
    Map<P1Decision, List<Fixture>> fixtures = new EnumMap<>(P1Decision.class);
    fixtures.put(
        P1Decision.WRAPPING,
        List.of(
            new Fixture(contract, "wordWrapTrue_usesWordBoundary"),
            new Fixture(contract, "wordWrapFalse_usesCharacterBoundary"),
            new Fixture(
                contract, "wordWrapTrue_withoutWordBoundary_fallsBackToCharacterBoundary")));
    fixtures.put(
        P1Decision.EMPTY_FONT_CHAIN,
        List.of(
            new Fixture(
                contract, "emptyFontChain_isEquivalentToDefaultFontChainIncludingRunEvidence")));
    fixtures.put(
        P1Decision.MISSING_GLYPH,
        List.of(
            new Fixture(contract, "missingSource_usesFirstFaceContainingReplacementGlyph"),
            new Fixture(
                contract,
                "missingSourceAndReplacement_usesPrimaryNotdefAndRetainsSourceEvidence")));
    fixtures.put(
        P1Decision.NEWLINES,
        List.of(new Fixture(contract, "crlfSeparator_isAtomicAndExcludedFromLineRanges")));
    fixtures.put(
        P1Decision.NUMERIC_INPUTS,
        List.of(
            new Fixture(contract, "invalidNumericInputs_areRejected"),
            new Fixture(contract, "zeroWidth_isValidAndMakesProgress")));
    fixtures.put(
        P1Decision.VERTICAL_METRICS,
        List.of(new Fixture(contract, "fallbackGlyph_doesNotChangePrimaryFaceVerticalMetrics")));
    fixtures.put(
        P1Decision.ROUNDING_AND_ACCUMULATION,
        List.of(
            new Fixture(
                contract,
                "fractionalKerningFallbackAndMultilineAccumulation_matchNanoVgFontStashOrder")));
    fixtures.put(
        P1Decision.SOURCE_INDICES,
        List.of(
            new Fixture(
                contract,
                "supplementaryCodePoint_remainsAtomicAcrossWrappedLineRunGlyphAndCaretBoundaries")));
    fixtures.put(
        P1Decision.EXTERNAL_CARET_SELECTION_INDICES,
        List.of(
            new Fixture(
                input,
                "externallyAssignedIndices_snapBackwardFromSurrogateInterior"),
            new Fixture(
                textarea,
                "externallyAssignedIndices_snapBackwardFromSurrogateInterior")));
    fixtures.put(
        P1Decision.CARET_MIDPOINT_TIE,
        List.of(
            new Fixture(
                contract,
                "caretMidpoint_belowStopsBeforeAndExactTieAdvancesToFollowingBoundary")));
    fixtures.put(
        P1Decision.COORDINATES_AND_OFFSET,
        List.of(
            new Fixture(
                contract,
                "firstLineOffset_reducesFiniteWrappingCapacityAndOnlyAffectsOccupiedExtent")));
    fixtures.put(
        P1Decision.CARET_REPRESENTATION,
        List.of(
            new Fixture(
                contract,
                "finalLineCaretRepresentation_isRebasedPerLineAndLookupIsLogarithmicWithoutRemeasurement")));
    fixtures.put(
        P1Decision.IMMUTABILITY,
        List.of(
            new Fixture(contract, "textLineMetrics_constructorAndBuilderAreDeepCanonicalSnapshots"),
            new Fixture(contract, "textMetrics_constructorAndBuilderAreDeepCanonicalSnapshots")));
    fixtures.put(
        P1Decision.RANGE_MEASUREMENT,
        List.of(
            new Fixture(
                contract,
                "wholeStringDirectCapabilityAndAdapterBranchesHaveExactNestedParity")));
    return fixtures;
  }

  private byte[] classBytes(String internalName) throws IOException {
    String resource = internalName + ".class";
    try (InputStream input = FontServiceImpl.class.getClassLoader().getResourceAsStream(resource)) {
      if (input == null) {
        throw new IOException("Class bytes are unavailable for " + internalName);
      }
      return input.readAllBytes();
    }
  }

  private record MethodKey(String owner, String name, String descriptor) {}

  private record Invocation(String owner, String name, String descriptor) {
    private static Invocation from(InvokeInstruction instruction) {
      return new Invocation(
          instruction.owner().asInternalName(),
          instruction.name().stringValue(),
          instruction.type().stringValue());
    }

    private MethodKey asMethodKey() {
      return new MethodKey(owner, name, descriptor);
    }
  }

  private record CallSite(MethodKey caller, Invocation invocation) {}

  private record CallGraph(
      Set<MethodKey> reachableMethods,
      List<CallSite> approvedFinalSnapshots,
      List<CallSite> rangeMaterializationViolations) {}

  private record PublicMethod(String name, String descriptor) {}

  private record Component(String name, Class<?> type) {}

  private enum P1Decision {
    WRAPPING,
    EMPTY_FONT_CHAIN,
    MISSING_GLYPH,
    NEWLINES,
    NUMERIC_INPUTS,
    VERTICAL_METRICS,
    ROUNDING_AND_ACCUMULATION,
    SOURCE_INDICES,
    EXTERNAL_CARET_SELECTION_INDICES,
    CARET_MIDPOINT_TIE,
    COORDINATES_AND_OFFSET,
    CARET_REPRESENTATION,
    IMMUTABILITY,
    RANGE_MEASUREMENT
  }

  private record Fixture(String className, String methodName) {}
}
