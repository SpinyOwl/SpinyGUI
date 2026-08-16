package com.spinyowl.spinygui.core.system.font;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FontSemanticContractTest {
  private static final String ROBOTO_REGULAR_PATH = "fonts/Roboto-Regular.ttf";
  private static final String NOTO_EMOJI_PATH = "fonts/NotoEmoji-Regular.ttf";

  @TempDir Path fontDirectory;

  @AfterEach
  void closeProductionOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
  }

  @DisplayName("P2 T2 aliases: built-ins publish only during explicit service installation")
  @Test
  void builtInDescriptorsInstallThroughTheProductionOwner() {
    FontServiceImpl service = new FontServiceImpl(new FontStorageImpl(), false);
    SemanticFontOwner owner = service.installSemanticOwner();
    List<Font> registered = owner.registeredFonts();
    Font equivalentRegular =
        new Font(
            Font.ROBOTO_REGULAR.fontFamily(),
            Font.ROBOTO_REGULAR.style(),
            Font.ROBOTO_REGULAR.stretch(),
            Font.ROBOTO_REGULAR.weight(),
            Font.ROBOTO_REGULAR.path());
    Font differentLocator =
        new Font(
            Font.ROBOTO_REGULAR.fontFamily(),
            Font.ROBOTO_REGULAR.style(),
            Font.ROBOTO_REGULAR.stretch(),
            Font.ROBOTO_REGULAR.weight(),
            "fonts/other-regular.ttf");

    assertAll(
        () ->
            assertTrue(
                registered.containsAll(
                    List.of(
                        Font.ROBOTO_LIGHT,
                        Font.ROBOTO_BOLD,
                        Font.ROBOTO_REGULAR,
                        Font.NOTO_SANS_CJK_SC_REGULAR))),
        () -> assertEquals(Font.ROBOTO_REGULAR, equivalentRegular),
        () -> assertNotSame(Font.ROBOTO_REGULAR, equivalentRegular),
        () -> assertNotEquals(Font.ROBOTO_REGULAR, differentLocator),
        () ->
            assertNotEquals(
                Font.ROBOTO_REGULAR,
                new Font(
                    Font.ROBOTO_REGULAR.fontFamily().toLowerCase(),
                    Font.ROBOTO_REGULAR.style(),
                    Font.ROBOTO_REGULAR.stretch(),
                    Font.ROBOTO_REGULAR.weight(),
                    Font.ROBOTO_REGULAR.path())),
        () -> assertSame(owner, Font.semanticOwner()),
        () -> assertTrue(service.isFontAvailable(Font.ROBOTO_REGULAR)),
        () -> assertThrows(UnsupportedOperationException.class, () -> registered.clear()));
  }

  @DisplayName("P2 T2 aliases: custom storage rejects before any production owner is installed")
  @Test
  void customStorageInstallationRejectsWithoutPublishingAnyState() throws Exception {
    Field ownerField = Font.class.getDeclaredField("semanticOwner");
    ownerField.setAccessible(true);
    SemanticFontOwner previous = (SemanticFontOwner) ownerField.get(null);
    SemanticFontOwner.Observation previousObservation =
        previous == null ? null : previous.observation();
    FontStorage storage = mock(FontStorage.class);
    FontServiceImpl service = new FontServiceImpl(storage, false);

    ownerField.set(null, null);
    try {
      IllegalStateException failure =
          assertThrows(IllegalStateException.class, service::installSemanticOwner);

      assertAll(
          () -> assertTrue(failure.getMessage().contains("FontStorageImpl")),
          () -> assertFalse(Font.hasSemanticOwner()),
          () -> assertThrows(IllegalStateException.class, Font::fonts),
          () -> assertFalse(service.isFontAvailable(Font.DEFAULT)),
          () -> verifyNoMoreInteractions(storage));
    } finally {
      ownerField.set(null, previous);
    }

    if (previous != null) {
      assertEquals(previousObservation, previous.observation());
    }
  }

  @DisplayName("P2 T2 aliases: descriptor-only static add rejects before mutation")
  @Test
  void staticAddIsDeprecatedAndRejectedAtomically() {
    FontServiceImpl service = new FontServiceImpl(new FontStorageImpl(), false);
    SemanticFontOwner owner = service.installSemanticOwner();
    Font candidate = face("Rejected Alias", "fonts/m3-p2-t2-rejected.ttf");
    SemanticFontOwner.Observation before = owner.observation();

    assertAll(
        () -> assertThrows(UnsupportedOperationException.class, () -> Font.addFont(candidate)),
        () -> assertEquals(before, owner.observation()),
        () -> assertFalse(Font.hasFont(candidate.fontFamily())));
  }

  @DisplayName("P2 T2 aliases: service owns success, duplicate, reload, failure, and storage publication")
  @Test
  void serviceLoadOwnsAtomicMutationAndStoragePublication() throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    Path fontPath = copyResourceFont(ROBOTO_REGULAR_PATH, "semantic reload.ttf");
    String locator = fontPath.toString();
    long before = owner.generation();

    Font loaded = service.loadFont(locator);
    long afterLoad = owner.generation();
    Font duplicate = service.loadFont(locator);
    long afterDuplicate = owner.generation();
    Files.write(fontPath, new byte[] {0}, java.nio.file.StandardOpenOption.APPEND);
    Font reloaded = service.loadFont(locator);
    long afterReload = owner.generation();
    String missing = fontDirectory.resolve("missing font.ttf").toString();

    assertAll(
        () -> assertEquals(before + 1, afterLoad),
        () -> assertEquals(afterLoad, afterDuplicate),
        () -> assertEquals(afterDuplicate + 1, afterReload),
        () -> assertEquals(loaded, duplicate),
        () -> assertEquals(loaded, reloaded),
        () ->
            assertSame(
                reloaded,
                Font.find(
                        loaded.fontFamily(),
                        loaded.style(),
                        loaded.weight(),
                        loaded.stretch())
                    .stream()
                    .filter(font -> locator.equals(font.path()))
                    .findFirst()
                    .orElseThrow()),
        () -> assertNotNull(storage.getFontData(locator)),
        () -> assertTrue(storage.getFontData(locator).isReadOnly()),
        () -> assertTrue(service.isFontAvailable(reloaded)),
        () -> assertThrows(FontLoadingException.class, () -> service.loadFont(missing)),
        () -> assertEquals(afterReload, owner.generation()),
        () -> assertEquals(null, storage.getFontData(missing)));
  }

  @DisplayName("P2 T4 service observation is immutable, backend-neutral, and owner-thread confined")
  @Test
  void serviceExposesProductionSemanticObservationWithoutOwnerInternals() throws Exception {
    FontService uninstalled = new FontServiceImpl(new FontStorageImpl(), false);
    FontService service = new FontServiceImpl(new FontStorageImpl(), false);
    service.installSemanticOwner();
    FontSemanticObservation observation = service.semanticObservation();
    AtomicReference<Throwable> offThreadFailure = new AtomicReference<>();
    Thread offThread =
        new Thread(
            () -> {
              try {
                service.semanticObservation();
              } catch (Throwable failure) {
                offThreadFailure.set(failure);
              }
            },
            "font-semantic-observation-off-thread");
    offThread.start();
    offThread.join();

    assertAll(
        () ->
            assertEquals(
                FontSemanticObservation.class,
                FontService.class.getMethod("semanticObservation").getReturnType()),
        () -> assertTrue(FontService.class.getMethod("semanticObservation").isDefault()),
        () -> assertTrue(FontSemanticObservation.class.isRecord()),
        () ->
            assertEquals(
                List.of(long.class, List.class),
                Arrays.stream(FontSemanticObservation.class.getRecordComponents())
                    .map(java.lang.reflect.RecordComponent::getType)
                    .toList()),
        () ->
            assertTrue(
                Arrays.stream(FontSemanticObservation.Identity.class.getRecordComponents())
                    .allMatch(component -> component.getType().equals(String.class))),
        () -> assertThrows(UnsupportedOperationException.class, observation.identities()::clear),
        () -> assertThrows(IllegalStateException.class, uninstalled::semanticObservation),
        () -> assertInstanceOf(IllegalStateException.class, offThreadFailure.get()));
  }

  @DisplayName("P2 T4 production observation is a stable downstream key across mutation outcomes")
  @Test
  void serviceObservationSupportsM4AndM5KeysWithoutATestBridge() throws Exception {
    FontService service = new FontServiceImpl(new FontStorageImpl(), false);
    service.installSemanticOwner();
    FontSemanticObservation before = service.semanticObservation();
    FontConsumerKey beforeKey = new FontConsumerKey("inline-pass", before);
    Path changedPath = copyResourceFont(ROBOTO_REGULAR_PATH, "t4 observed regular.ttf");
    Path invalidHeader = fontDirectory.resolve("t4 invalid header.ttf");
    Path invalidStructure = fontDirectory.resolve("t4 invalid structure.ttf");
    Files.write(invalidHeader, new byte[12]);
    Files.write(
        invalidStructure,
        new byte[] {0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

    try {
      Font changedDescriptor = service.loadFont(changedPath.toString());
      FontSemanticObservation changed = service.semanticObservation();
      FontConsumerKey changedKey = new FontConsumerKey("inline-pass", changed);
      Font duplicateDescriptor = service.loadFont(changedPath.toString());
      FontSemanticObservation afterDuplicate = service.semanticObservation();
      String missing = fontDirectory.resolve("t4 missing.ttf").toString();

      assertThrows(FontLoadingException.class, () -> service.loadFont(missing));
      FontSemanticObservation afterLoadFailure = service.semanticObservation();
      assertThrows(
          FontLoadingException.class, () -> service.loadFont(invalidHeader.toString()));
      FontSemanticObservation afterValidationFailure = service.semanticObservation();
      assertThrows(
          FontLoadingException.class, () -> service.loadFont(invalidStructure.toString()));
      FontSemanticObservation afterParseFailure = service.semanticObservation();

      assertAll(
          () -> assertNotEquals(beforeKey, changedKey),
          () -> assertEquals(before.generation() + 1, changed.generation()),
          () -> assertSame(changedDescriptor, duplicateDescriptor),
          () -> assertEquals(changed, afterDuplicate),
          () -> assertEquals(changed, afterLoadFailure),
          () -> assertEquals(changed, afterValidationFailure),
          () -> assertEquals(changed, afterParseFailure),
          () -> assertEquals(changedKey, new FontConsumerKey("inline-pass", afterParseFailure)),
          () ->
              assertTrue(
                  service.fontChainResolver()
                      .resolve(
                          List.of(changedDescriptor.fontFamily()),
                          changedDescriptor.style(),
                          changedDescriptor.weight(),
                          changedDescriptor.stretch())
                      .contains(changedDescriptor)));
    } finally {
      service.loadFont(Font.ROBOTO_REGULAR.path());
    }
  }

  @DisplayName("P2 T2 aliases: equivalent locators return one canonical descriptor and cache entry")
  @Test
  void serviceNormalizesLocatorAliasesWithoutDuplicatePublication() throws Exception {
    Path directory = createFontDirectory("locator aliases with spaces");
    Path fontPath = copyResourceFont(directory, NOTO_EMOJI_PATH, "semantic alias font.ttf");
    String backslashAndSpace = fontPath.toString();
    String forwardSlash = backslashAndSpace.replace('\\', '/');
    int fileNameSeparator = forwardSlash.lastIndexOf('/');
    String dotSegment =
        forwardSlash.substring(0, fileNameSeparator)
            + "/./"
            + forwardSlash.substring(fileNameSeparator + 1);
    String encodedSpaces = forwardSlash.replace(" ", "%20");
    String canonicalLocator = SemanticFontOwner.normalizeLocator(backslashAndSpace);
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    int storageBefore = privateMapSize(storage, "dataMap");
    int serviceBefore = privateMapSize(service, "fontInfoMap");

    Font canonical = service.loadFont(backslashAndSpace);
    long publishedGeneration = owner.generation();
    int storageAfterPublish = privateMapSize(storage, "dataMap");
    int serviceAfterPublish = privateMapSize(service, "fontInfoMap");
    List<Font> aliasResults =
        List.of(
            service.loadFont(forwardSlash),
            service.loadFont(dotSegment),
            service.loadFont(encodedSpaces));
    List<Font> registeredMatchingFace =
        owner.registeredFonts().stream()
            .filter(font -> font.fontFamily().equals(canonical.fontFamily()))
            .filter(font -> font.style().equals(canonical.style()))
            .filter(font -> font.weight().equals(canonical.weight()))
            .filter(font -> font.stretch().equals(canonical.stretch()))
            .toList();
    List<Font> resolved =
        FontChainResolver.DEFAULT.resolve(
            List.of(canonical.fontFamily()),
            canonical.style(),
            canonical.weight(),
            canonical.stretch());
    SemanticFontOwner.Identity canonicalIdentity =
        owner.observation().identities().stream()
            .filter(identity -> identity.key().family().equals(canonical.fontFamily().toLowerCase()))
            .findFirst()
            .orElseThrow();

    assertAll(
        () -> assertEquals(backslashAndSpace, canonical.path()),
        () -> assertEquals(canonicalLocator, canonicalIdentity.normalizedLocator()),
        () -> aliasResults.forEach(alias -> assertSame(canonical, alias)),
        () -> assertEquals(publishedGeneration, owner.generation()),
        () -> assertEquals(List.of(canonical), registeredMatchingFace),
        () -> assertEquals(List.of(canonical), resolved),
        () -> assertSame(canonical, registeredMatchingFace.getFirst()),
        () -> assertSame(canonical, resolved.getFirst()),
        () -> assertTrue(service.isFontAvailable(canonical)),
        () -> assertEquals(storageBefore + 1, storageAfterPublish),
        () -> assertEquals(serviceBefore + 1, serviceAfterPublish),
        () -> assertEquals(storageAfterPublish, privateMapSize(storage, "dataMap")),
        () -> assertEquals(serviceAfterPublish, privateMapSize(service, "fontInfoMap")),
        () -> assertNotNull(storage.getFontData(backslashAndSpace)),
        () -> assertNotNull(storage.getFontData(forwardSlash)),
        () -> assertNotNull(storage.getFontData(dotSegment)),
        () -> assertNotNull(storage.getFontData(encodedSpaces)),
        () -> assertNotNull(IOUtil.resourceAsByteBuffer(canonical.path())));
  }

  @DisplayName("P2 T2 aliases: direct storage mutation rejects and semantic clear advances once")
  @Test
  void directStorageRejectsAndClearUsesTheOwnerTransaction() {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    long before = owner.generation();

    assertAll(
        () -> assertThrows(UnsupportedOperationException.class, () -> storage.loadFont(ROBOTO_REGULAR_PATH)),
        () -> assertEquals(before, owner.generation()));

    SemanticFontOwner.Mutation cleared = Font.clear();
    SemanticFontOwner.Mutation repeated = Font.clear();

    assertAll(
        () -> assertEquals(SemanticFontOwner.MutationOutcome.CHANGED, cleared.outcome()),
        () -> assertEquals(before + 1, cleared.generation()),
        () -> assertEquals(SemanticFontOwner.MutationOutcome.UNCHANGED, repeated.outcome()),
        () -> assertEquals(cleared.generation(), repeated.generation()),
        () -> assertTrue(Font.fonts().isEmpty()),
        () -> assertFalse(service.isFontAvailable(Font.ROBOTO_REGULAR)));

  }

  @DisplayName("P2 T2 aliases: service mutation rejects before explicit installation")
  @Test
  void serviceLoadRejectsBeforeInstallationWithoutPublishingStorage() {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);

    assertAll(
        () ->
            assertThrows(
                IllegalStateException.class, () -> service.loadFont(ROBOTO_REGULAR_PATH)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> storage.getFontData(ROBOTO_REGULAR_PATH)));
  }

  @DisplayName("P2 T2 aliases: system loader delegates each face exactly once and isolates failures")
  @Test
  void systemFontLoaderPublishesOnlyServiceSuccesses() throws Exception {
    Path successADirectory = createFontDirectory("01-success-a");
    Path parseFailureDirectory = createFontDirectory("02-parse-failure");
    Path successBDirectory = createFontDirectory("03-success-b");
    Path storageFailureDirectory = createFontDirectory("04-storage-failure");
    String successA = createFontFile(successADirectory, "success-a.ttf");
    String parseFailure = createFontFile(parseFailureDirectory, "parse-failure.ttf");
    String successB = createFontFile(successBDirectory, "success-b.ttf");
    String storageFailure = createFontFile(storageFailureDirectory, "storage-failure.ttf");
    FontStorage storage = mock(FontStorage.class);
    FontService service = mock(FontService.class);
    Font successFaceA =
        new Font(
            Font.ROBOTO_REGULAR.fontFamily(),
            FontStyle.ITALIC,
            FontStretch.NORMAL,
            FontWeight.MEDIUM,
            successA);
    Font successFaceB =
        new Font(
            Font.ROBOTO_REGULAR.fontFamily(),
            FontStyle.OBLIQUE,
            FontStretch.NORMAL,
            FontWeight.SEMI_BOLD,
            successB);

    when(service.loadFont(successA)).thenReturn(successFaceA);
    when(service.loadFont(successB)).thenReturn(successFaceB);
    when(service.loadFont(parseFailure))
        .thenThrow(new FontLoadingException("characterized parse failure"));
    when(service.loadFont(storageFailure))
        .thenThrow(new FontLoadingException("characterized storage failure"));

    SystemFontLoader loader =
        SystemFontLoader.builder()
            .fontStorage(storage)
            .fontService(service)
            .fontDirectoriesProvider(
                () ->
                    List.of(
                        successADirectory.toString(),
                        parseFailureDirectory.toString(),
                        successBDirectory.toString(),
                        storageFailureDirectory.toString()))
            .build();

    List<String> published = loader.loadSystemFonts();
    var invocations = inOrder(service);

    assertAll(
        () -> assertEquals(List.of(successA, successB), published),
        () -> {
          invocations.verify(service).loadFont(successA);
          invocations.verify(service).loadFont(parseFailure);
          invocations.verify(service).loadFont(successB);
          invocations.verify(service).loadFont(storageFailure);
        },
        () -> verifyNoMoreInteractions(storage),
        () -> verifyNoMoreInteractions(storage, service));
  }

  @DisplayName("P2 T2 aliases: real system loading publishes successful faces independently")
  @Test
  void systemFontLoaderLeavesNoPartialStateAndContinuesAfterFailure() throws Exception {
    Path successADirectory = createFontDirectory("01-real-success-a");
    Path failureDirectory = createFontDirectory("02-real-failure");
    Path successBDirectory = createFontDirectory("03-real-success-b");
    String successA =
        copyResourceFont(successADirectory, "fonts/Roboto-Light.ttf", "font a.ttf").toString();
    String failure = createFontFile(failureDirectory, "invalid.ttf");
    String successB =
        copyResourceFont(successBDirectory, NOTO_EMOJI_PATH, "font b.ttf").toString();
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    long before = owner.generation();
    SystemFontLoader loader =
        SystemFontLoader.builder()
            .fontStorage(storage)
            .fontService(service)
            .fontDirectoriesProvider(
                () ->
                    List.of(
                        successADirectory.toString(),
                        failureDirectory.toString(),
                        successBDirectory.toString()))
            .build();

    List<String> published = loader.loadSystemFonts();

    assertAll(
        () -> assertEquals(List.of(successA, successB), published),
        () -> assertEquals(before + 2, owner.generation()),
        () -> assertNotNull(storage.getFontData(successA)),
        () -> assertEquals(null, storage.getFontData(failure)),
        () -> assertNotNull(storage.getFontData(successB)),
        () -> assertFalse(service.isFontAvailable(face("Invalid", failure))),
        () -> assertTrue(Font.fonts().stream().noneMatch(font -> failure.equals(font.path()))));
  }

  @DisplayName("P2 T2 aliases: mutation and compatibility queries reject off the install thread")
  @Test
  void migratedAliasesRejectOffThreadWithoutMutation() throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    SemanticFontOwner.Observation before = owner.observation();
    AtomicReference<Throwable> queryFailure = new AtomicReference<>();
    AtomicReference<Throwable> loadFailure = new AtomicReference<>();
    Thread worker =
        new Thread(
            () -> {
              try {
                Font.fonts();
              } catch (Throwable failure) {
                queryFailure.set(failure);
              }
              try {
                service.loadFont(ROBOTO_REGULAR_PATH);
              } catch (Throwable failure) {
                loadFailure.set(failure);
              }
            });
    worker.start();
    worker.join();

    assertAll(
        () -> assertInstanceOf(IllegalStateException.class, queryFailure.get()),
        () -> assertInstanceOf(IllegalStateException.class, loadFailure.get()),
        () -> assertEquals(before, owner.observation()));
  }

  @DisplayName("P4 T1: registered backend preflight rejects replacement before publication")
  @Test
  void backendMutationPreflightPreservesSemanticAndResourceStateUntilUnregistered()
      throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    Path initial = copyResourceFont(ROBOTO_REGULAR_PATH, "active backend initial.ttf");
    Font active = service.loadFont(initial.toString());
    Path replacement = copyResourceFont(ROBOTO_REGULAR_PATH, "active backend replacement.ttf");
    FontSemanticObservation before = service.semanticObservation();
    FontResourceObservation resourcesBefore = service.resourceObservation();
    AtomicInteger replacementChecks = new AtomicInteger();
    SemanticFontOwner.MutationPreflightRegistration registration =
        owner.registerMutationPreflight(
            (previous, candidate) -> {
              replacementChecks.incrementAndGet();
              throw new IllegalStateException("active backend face");
            });

    Font duplicate = service.loadFont(initial.toString());
    IllegalStateException rejection =
        assertThrows(
            IllegalStateException.class,
            () -> service.loadFont(replacement.toString()));

    assertAll(
        () -> assertSame(active, duplicate),
        () -> assertEquals("active backend face", rejection.getMessage()),
        () -> assertEquals(1, replacementChecks.get()),
        () -> assertEquals(before, service.semanticObservation()),
        () -> assertEquals(resourcesBefore, service.resourceObservation()));

    registration.close();
    Font accepted = service.loadFont(replacement.toString());

    assertAll(
        () -> assertEquals(before.generation() + 1, service.semanticObservation().generation()),
        () -> assertEquals(replacement.toString(), accepted.path()),
        () -> assertEquals(1, replacementChecks.get()));
  }

  @DisplayName("P2 T2 aliases: descriptors stay immutable and no separate static registry remains")
  @Test
  void descriptorsAndProductionRegistryOwnershipRemainBackendNeutral() {
    new FontServiceImpl(new FontStorageImpl(), false).installSemanticOwner();
    List<Field> descriptorFields =
        Arrays.stream(Font.class.getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .toList();
    List<Font> resolved =
        FontChainResolver.DEFAULT.resolve(
            List.of(Font.ROBOTO_REGULAR.fontFamily()),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);

    assertAll(
        () ->
            assertEquals(
                Set.of("fontFamily", "style", "stretch", "weight", "path"),
                descriptorFields.stream().map(Field::getName).collect(Collectors.toSet())),
        () ->
            assertTrue(
                descriptorFields.stream().allMatch(field -> Modifier.isFinal(field.getModifiers()))),
        () -> assertThrows(UnsupportedOperationException.class, () -> resolved.clear()),
        () -> assertTrue(resolved.contains(Font.ROBOTO_REGULAR)),
        () ->
            assertFalse(
                descriptorFields.stream()
                    .map(field -> field.getName().toLowerCase())
                    .anyMatch(
                        name ->
                            name.contains("context")
                                || name.contains("faceid")
                                 || name.contains("facename")
                                || name.contains("nanovg"))),
        () ->
            assertTrue(
                Arrays.stream(Font.class.getDeclaredFields())
                    .filter(field -> Modifier.isStatic(field.getModifiers()))
                    .noneMatch(field -> Map.class.isAssignableFrom(field.getType()))));
  }

  @DisplayName("P2 target: semantic key normalization and identity revisions")
  @Test
  void p2TargetSemanticIdentityUsesNormalizedFaceKeyLocatorAndByteRevision() {
    SemanticRegistryTarget target = p2Target();
    FontCandidate initial = candidate(" Roboto ", "fonts/Roboto-Regular.ttf", "sha256:a", true);

    MutationResult added = target.add(initial);
    SemanticIdentity initialIdentity = added.identities().getFirst();
    MutationResult duplicate =
        target.add(candidate("rObOtO", "fonts/Roboto-Regular.ttf", "sha256:a", true));
    MutationResult relocated =
        target.add(candidate("ROBOTO", "fonts/alternate.ttf", "sha256:a", true));
    MutationResult revised =
        target.reload(candidate("roboto", "fonts/alternate.ttf", "sha256:b", true));

    assertAll(
        () -> assertEquals(new SemanticFaceKey("roboto", "normal", "regular", "normal"), initialIdentity.key()),
        () -> assertEquals("fonts/Roboto-Regular.ttf", initialIdentity.normalizedLocator()),
        () -> assertEquals(byteRevision(initial), initialIdentity.byteRevision()),
        () -> assertMutation(duplicate, MutationOutcome.UNCHANGED, 1),
        () -> assertMutation(relocated, MutationOutcome.CHANGED, 2),
        () -> assertMutation(revised, MutationOutcome.CHANGED, 3),
        () -> assertNotEquals(initialIdentity, revised.identities().getFirst()));
  }

  @DisplayName("P2 target: generation zero and atomic built-in bootstrap")
  @Test
  void p2TargetInitialGenerationAndBuiltInBootstrapAreAtomic() {
    SemanticRegistryTarget target = p2Target();
    List<FontCandidate> builtIns =
        List.of(
            candidate("Roboto", "fonts/Roboto-Regular.ttf", "sha256:regular", true),
            candidate("Roboto", "fonts/Roboto-Bold.ttf", "sha256:bold", true)
                .withWeight("bold"));

    assertEquals(0, target.generation());
    MutationResult bootstrap = target.bootstrap(builtIns);
    MutationResult repeated = target.bootstrap(builtIns);

    assertAll(
        () -> assertMutation(bootstrap, MutationOutcome.CHANGED, 1),
        () -> assertEquals(2, target.identities().size()),
        () -> assertMutation(repeated, MutationOutcome.UNCHANGED, 1));
  }

  @DisplayName("P2 target: bootstrap is atomic at load, parse, and validation failures")
  @Test
  void p2TargetBootstrapFailuresPublishNothingAtEveryPreparationStage() {
    for (FailurePoint failurePoint : preparationFailurePoints()) {
      SemanticRegistryTarget target = p2Target();
      PreparationAttempts before = target.preparationAttempts();

      MutationResult failure =
          target.bootstrap(
              List.of(
                  candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:regular", true),
                  failingCandidate("Broken", failurePoint)));

      assertAll(
          () -> assertMutation(failure, MutationOutcome.REJECTED, 0),
          () -> assertTrue(target.identities().isEmpty()),
          () ->
              assertFailurePreparationDelta(
                  before, target.preparationAttempts(), failurePoint, 1));
    }
  }

  @DisplayName("P2 target: add, replacement, reload, duplicate, and clear transitions")
  @Test
  void p2TargetSingleMutationTransitionsAdvanceExactlyOnce() {
    SemanticRegistryTarget target = p2Target();
    FontCandidate initial = candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:a", true);

    assertMutation(target.add(initial), MutationOutcome.CHANGED, 1);
    assertMutation(target.add(initial), MutationOutcome.UNCHANGED, 1);
    assertMutation(target.reload(initial), MutationOutcome.UNCHANGED, 1);
    assertMutation(
        target.add(candidate("Roboto", "fonts/alternate.ttf", "sha256:a", true)),
        MutationOutcome.CHANGED,
        2);
    assertMutation(
        target.reload(candidate("Roboto", "fonts/alternate.ttf", "sha256:b", true)),
        MutationOutcome.CHANGED,
        3);
    assertMutation(target.clear(), MutationOutcome.CHANGED, 4);
    assertMutation(target.clear(), MutationOutcome.UNCHANGED, 4);
  }

  @DisplayName("P2 target: every system font is an independent semantic transaction")
  @Test
  void p2TargetSystemFontTransactionsIsolateNoOpsFailuresAndSuccessfulSiblings() {
    SemanticRegistryTarget target = p2Target();
    FontCandidate original = candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:a", true);
    target.add(original);

    List<MutationResult> results =
        target.loadSystemFonts(
            List.of(
                original,
                candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:b", true),
                candidate("New Face", "fonts/new.ttf", "sha256:new", true),
                candidate("Broken", "fonts/broken.ttf", "sha256:broken", false)));

    assertAll(
        () -> assertMutation(results.get(0), MutationOutcome.UNCHANGED, 1),
        () -> assertMutation(results.get(1), MutationOutcome.CHANGED, 2),
        () -> assertMutation(results.get(2), MutationOutcome.CHANGED, 3),
        () -> assertMutation(results.get(3), MutationOutcome.REJECTED, 3),
        () -> assertEquals(2, target.identities().size()));
  }

  @DisplayName("P2 target: system-font load, parse, and validation failures are per-face")
  @Test
  void p2TargetSystemFontFailurePointsDoNotAffectSuccessfulSiblings() {
    SemanticRegistryTarget target = p2Target();

    List<MutationResult> results =
        target.loadSystemFonts(
            List.of(
                candidate("First", "fonts/first.ttf", "sha256:first", true),
                failingCandidate("Load Failure", FailurePoint.STORAGE_LOAD),
                candidate("Second", "fonts/second.ttf", "sha256:second", true),
                failingCandidate("Parse Failure", FailurePoint.PARSE),
                candidate("Third", "fonts/third.ttf", "sha256:third", true),
                failingCandidate("Validation Failure", FailurePoint.VALIDATION)));

    assertAll(
        () -> assertMutation(results.get(0), MutationOutcome.CHANGED, 1),
        () -> assertMutation(results.get(1), MutationOutcome.REJECTED, 1),
        () -> assertMutation(results.get(2), MutationOutcome.CHANGED, 2),
        () -> assertMutation(results.get(3), MutationOutcome.REJECTED, 2),
        () -> assertMutation(results.get(4), MutationOutcome.CHANGED, 3),
        () -> assertMutation(results.get(5), MutationOutcome.REJECTED, 3),
        () -> assertEquals(3, target.identities().size()));
  }

  @DisplayName("P2 target: add is atomic at load, parse, and validation failures")
  @Test
  void p2TargetAddFailuresPublishNothingAtEveryPreparationStage() {
    for (FailurePoint failurePoint : preparationFailurePoints()) {
      SemanticRegistryTarget target = p2Target();
      PreparationAttempts before = target.preparationAttempts();

      MutationResult failure = target.add(failingCandidate("Broken Add", failurePoint));

      assertAll(
          () -> assertMutation(failure, MutationOutcome.REJECTED, 0),
          () -> assertTrue(target.identities().isEmpty()),
          () ->
              assertFailurePreparationDelta(
                  before, target.preparationAttempts(), failurePoint, 0));
    }
  }

  @DisplayName("P2 target: replacement is atomic at load, parse, and validation failures")
  @Test
  void p2TargetReplacementFailuresRetainPriorStateAtEveryPreparationStage() {
    for (FailurePoint failurePoint : preparationFailurePoints()) {
      SemanticRegistryTarget target = p2Target();
      target.add(candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:a", true));
      List<SemanticIdentity> beforeFailure = target.identities();
      PreparationAttempts before = target.preparationAttempts();

      MutationResult failure =
          target.add(
              failingCandidate(
                  "Roboto", "fonts/replacement-broken.ttf", "sha256:broken", failurePoint));

      assertAll(
          () -> assertMutation(failure, MutationOutcome.REJECTED, 1),
          () -> assertEquals(beforeFailure, target.identities()),
          () ->
              assertFailurePreparationDelta(
                  before, target.preparationAttempts(), failurePoint, 0));
    }
  }

  @DisplayName("P2 target: reload is atomic at load, parse, and validation failures")
  @Test
  void p2TargetReloadFailuresRetainPriorStateAtEveryPreparationStage() {
    for (FailurePoint failurePoint : preparationFailurePoints()) {
      SemanticRegistryTarget target = p2Target();
      target.add(candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:a", true));
      List<SemanticIdentity> beforeFailure = target.identities();
      PreparationAttempts before = target.preparationAttempts();

      MutationResult failure =
          target.reload(
              failingCandidate(
                  "Roboto", ROBOTO_REGULAR_PATH, "sha256:broken", failurePoint));

      assertAll(
          () -> assertMutation(failure, MutationOutcome.REJECTED, 1),
          () -> assertEquals(beforeFailure, target.identities()),
          () ->
              assertFailurePreparationDelta(
                  before, target.preparationAttempts(), failurePoint, 0));
    }
  }

  @DisplayName("P2 target: arbitrary single-face removal is rejected before mutation")
  @Test
  void p2TargetSingleFaceRemovalIsUnsupportedAndNonMutating() {
    SemanticRegistryTarget target = p2Target();
    target.add(candidate("Roboto", ROBOTO_REGULAR_PATH, "sha256:a", true));
    List<SemanticIdentity> beforeRemoval = target.identities();

    MutationResult removal =
        target.remove(new SemanticFaceKey("roboto", "normal", "regular", "normal"));

    assertAll(
        () -> assertMutation(removal, MutationOutcome.REJECTED, 1),
        () -> assertEquals(beforeRemoval, target.identities()));
  }

  @DisplayName("P2 target: generation overflow rejects before preparation or publication")
  @Test
  void p2TargetOverflowIsRejectedBeforePreparationOrPublication() {
    SemanticRegistryTarget target = p2Target();
    target.forceGenerationForTest(Long.MAX_VALUE);
    PreparationAttempts preparationAttempts = target.preparationAttempts();
    List<SemanticIdentity> beforeMutation = target.identities();

    MutationResult overflow =
        target.add(candidate("Overflow", "fonts/overflow.ttf", "sha256:overflow", true));

    assertAll(
        () -> assertMutation(overflow, MutationOutcome.REJECTED, Long.MAX_VALUE),
        () -> assertEquals(preparationAttempts, target.preparationAttempts()),
        () -> assertEquals(beforeMutation, target.identities()));
  }

  @DisplayName("P2 target: descriptor constants remain usable before explicit owner installation")
  @Test
  void p2TargetPreInstallRegistryOperationsRejectWithoutBlockingDescriptorConstants()
      throws Exception {
    Font workerDescriptor = callOnWorker(() -> Font.DEFAULT);
    assertAll(
        () -> assertNotNull(Font.ROBOTO_LIGHT),
        () -> assertNotNull(Font.ROBOTO_BOLD),
        () -> assertNotNull(Font.ROBOTO_REGULAR),
        () -> assertNotNull(Font.NOTO_SANS_CJK_SC_REGULAR),
        () -> assertSame(Font.DEFAULT, workerDescriptor));

    for (OwnerOperation operation : OwnerOperation.values()) {
      SemanticOwnerTarget target = p2OwnerTarget();
      assertThrows(
          IllegalStateException.class,
          () -> target.perform(operation),
          () -> operation + " must reject before owner installation");
    }
    for (ReadUseKind kind : ReadUseKind.values()) {
      SemanticOwnerTarget target = p2OwnerTarget();
      assertThrows(
          IllegalStateException.class,
          () -> target.openReadUseScope(kind),
          () -> kind + " scope must reject before owner installation");
    }
  }

  @DisplayName("P2 target: install binds current UI thread and atomically bootstraps built-ins")
  @Test
  void p2TargetInstallBindsCurrentThreadAndBootstrapsAsOneTransaction() {
    SemanticOwnerTarget target = p2OwnerTarget();
    Thread installationThread = Thread.currentThread();

    OwnerInstallation installation = target.install();
    MutationResult stateBeforeSecondInstall = target.semanticState();
    IllegalStateException secondInstallFailure =
        assertThrows(IllegalStateException.class, target::install);

    assertAll(
        () -> assertSame(installationThread, installation.ownerThread()),
        () -> assertSame(installationThread, target.ownerThread()),
        () -> assertMutation(installation.bootstrap(), MutationOutcome.CHANGED, 1),
        () -> assertEquals(expectedBuiltInKeys(), semanticKeys(installation.bootstrap())),
        () -> assertEquals(4, installation.bootstrap().identities().size()),
        () -> assertNotNull(secondInstallFailure),
        () -> assertEquals(stateBeforeSecondInstall, target.semanticState()),
        () -> assertSame(installationThread, target.ownerThread()));
  }

  @DisplayName("P2 target: every semantic owner operation succeeds on the exact install thread")
  @Test
  void p2TargetExactOwnerThreadSupportsRegistryReadUseMutationAndCacheAccess() {
    for (OwnerOperation operation : OwnerOperation.values()) {
      SemanticOwnerTarget target = p2OwnerTarget();
      target.install();

      assertDoesNotThrow(
          () -> target.perform(operation),
          () -> operation + " must be supported on the exact owner thread");
    }
  }

  @DisplayName("P2 target: off-thread calls reject and cannot migrate owner identity")
  @Test
  void p2TargetEveryOffThreadOperationAndReinstallationRejectWithoutOwnerMigration()
      throws Exception {
    Thread installationThread = Thread.currentThread();
    for (OwnerOperation operation : OwnerOperation.values()) {
      SemanticOwnerTarget target = p2OwnerTarget();
      target.install();
      MutationResult stateBeforeFailure = target.semanticState();

      Throwable failure = captureWorkerFailure(() -> target.perform(operation));

      assertAll(
          () -> assertInstanceOf(IllegalStateException.class, failure),
          () -> assertSame(installationThread, target.ownerThread()),
          () -> assertEquals(stateBeforeFailure, target.semanticState()),
          () -> assertDoesNotThrow(() -> target.perform(OwnerOperation.REGISTRY_OBSERVATION)));
    }

    for (ReadUseKind kind : ReadUseKind.values()) {
      SemanticOwnerTarget target = p2OwnerTarget();
      target.install();
      Throwable openFailure = captureWorkerFailure(() -> target.openReadUseScope(kind));
      ReadUseScope scope = target.openReadUseScope(kind);
      Throwable closeFailure = captureWorkerFailure(scope::close);
      assertAll(
          () -> assertInstanceOf(IllegalStateException.class, openFailure),
          () -> assertInstanceOf(IllegalStateException.class, closeFailure),
          () -> assertSame(installationThread, target.ownerThread()));
      scope.close();
    }

    SemanticOwnerTarget target = p2OwnerTarget();
    target.install();
    MutationResult stateBeforeReinstall = target.semanticState();
    Throwable reinstallFailure = captureWorkerFailure(target::install);
    assertAll(
        () -> assertInstanceOf(IllegalStateException.class, reinstallFailure),
        () -> assertSame(installationThread, target.ownerThread()),
        () -> assertEquals(stateBeforeReinstall, target.semanticState()));
  }

  @DisplayName("P2 target: read-use scopes may nest across measurement, layout, and render")
  @Test
  void p2TargetNestedReadUseScopesAllowOwnerThreadReadsAndUses() {
    SemanticOwnerTarget target = p2OwnerTarget();
    target.install();

    try (ReadUseScope measurement = target.openReadUseScope(ReadUseKind.MEASUREMENT);
        ReadUseScope layout = target.openReadUseScope(ReadUseKind.LAYOUT);
        ReadUseScope render = target.openReadUseScope(ReadUseKind.RENDER)) {
      assertAll(
          () -> assertDoesNotThrow(() -> target.perform(OwnerOperation.REGISTRY_OBSERVATION)),
          () -> assertDoesNotThrow(() -> target.perform(OwnerOperation.RESOLUTION)),
          () -> assertDoesNotThrow(() -> target.perform(OwnerOperation.MEASUREMENT)),
          () ->
              assertDoesNotThrow(
                  () -> target.perform(OwnerOperation.FONT_STORAGE_BYTE_CACHE_ACCESS)),
          () ->
              assertDoesNotThrow(
                  () -> target.perform(OwnerOperation.FONT_SERVICE_INFO_CACHE_ACCESS)),
          () ->
              assertDoesNotThrow(
                  () -> target.perform(OwnerOperation.FUTURE_SEMANTIC_CACHE_ACCESS)));
    }
  }

  @DisplayName("P2 target: active measurement, layout, and render scopes reject mutation")
  @Test
  void p2TargetEveryActiveReadUseScopeRejectsMutation() {
    for (ReadUseKind kind : ReadUseKind.values()) {
      SemanticOwnerTarget target = p2OwnerTarget();
      target.install();

      try (ReadUseScope ignored = target.openReadUseScope(kind)) {
        assertAll(
            () ->
                assertThrows(
                    IllegalStateException.class,
                    () -> target.perform(OwnerOperation.SEMANTIC_MUTATION)));
      }

      assertDoesNotThrow(() -> target.perform(OwnerOperation.SEMANTIC_MUTATION));
    }
  }

  @DisplayName("P2 T1 owner observations are immutable and expose a post-teardown close guard")
  @Test
  void p2T1OwnerObservationsAreImmutableAndCloseGuardRejectsFurtherUse() {
    MutablePreparationAttempts attempts = new MutablePreparationAttempts();
    SemanticFontOwner owner = new SemanticFontOwner(List.of());
    SemanticFontOwner.Installation installation = owner.install();
    SemanticFontOwner.Mutation mutation =
        owner.add(
            request(
                candidate("Immutable", "fonts/immutable.ttf", "immutable-content", true),
                attempts));
    SemanticFontOwner.Observation observation = owner.observation();

    assertAll(
        () -> assertEquals(0, installation.bootstrap().generation()),
        () -> assertEquals(1, observation.generation()),
        () -> assertEquals(mutation.identities(), observation.identities()),
        () -> assertThrows(UnsupportedOperationException.class, mutation.identities()::clear),
        () -> assertThrows(UnsupportedOperationException.class, observation.identities()::clear));

    owner.completeCloseAfterResourceTeardown();

    assertAll(
        () -> assertThrows(IllegalStateException.class, owner::generation),
        () -> assertThrows(IllegalStateException.class, owner::observation),
        () -> assertThrows(IllegalStateException.class, owner::verifyUse),
        () -> assertDoesNotThrow(owner::completeCloseAfterResourceTeardown));
  }

  @DisplayName("P2 T1 failed installation publishes nothing and permits one clean retry")
  @Test
  void p2T1FailedInstallationLeavesOwnerUninstalledAndRetryPublishesOnce() {
    MutablePreparationAttempts attempts = new MutablePreparationAttempts();
    SemanticFontOwner.FontRequest first =
        request(
            candidate("Install", "fonts/install-regular.ttf", "install-regular", true),
            attempts);
    SemanticFontOwner.FontRequest second =
        request(
            candidate("Install", "fonts/install-bold.ttf", "install-bold", true)
                .withWeight("bold"),
            attempts);
    AtomicBoolean failSecondLoad = new AtomicBoolean(true);
    SemanticFontOwner.FontRequest flakySecond =
        new SemanticFontOwner.FontRequest(
            second.family(),
            second.style(),
            second.weight(),
            second.stretch(),
            second.locator(),
            () -> failSecondLoad.getAndSet(false) ? null : second.loader().load(),
            second.parser(),
            second.validator());
    SemanticFontOwner owner = new SemanticFontOwner(List.of(first, flakySecond));

    assertThrows(IllegalStateException.class, owner::install);
    assertThrows(IllegalStateException.class, owner::verifyUse);

    SemanticFontOwner.Installation retry = owner.install();

    assertAll(
        () -> assertEquals(1, retry.bootstrap().generation()),
        () -> assertEquals(SemanticFontOwner.MutationOutcome.CHANGED, retry.bootstrap().outcome()),
        () -> assertEquals(2, retry.bootstrap().identities().size()),
        () -> assertEquals(retry.bootstrap().identities(), owner.observation().identities()));
  }

  @DisplayName("P2 T1 every mutation surface rejects reentrant preparation callbacks")
  @Test
  void p2T1EveryMutationSurfaceRejectsReentrancyAtEveryPreparationStage() {
    for (PreparationStage stage : PreparationStage.values()) {
      MutablePreparationAttempts attempts = new MutablePreparationAttempts();
      SemanticFontOwner owner = new SemanticFontOwner(List.of());
      owner.install();
      SemanticFontOwner.FontRequest outer =
          requestWithCallback(
              candidate(
                  "Outer " + stage,
                  "fonts/outer-" + stage.name().toLowerCase() + ".ttf",
                  "outer-" + stage,
                  true),
              attempts,
              stage,
              () -> {
                for (ReentrantMutationSurface surface : ReentrantMutationSurface.values()) {
                  assertThrows(
                      IllegalStateException.class,
                      () -> performReentrantMutation(owner, surface, attempts),
                      () -> surface + " must reject during " + stage + " preparation");
                }
              });

      SemanticFontOwner.Mutation mutation = owner.add(outer);

      assertAll(
          () -> assertMutation(result(mutation), MutationOutcome.CHANGED, 1),
          () -> assertEquals(1, mutation.identities().size()),
          () -> assertEquals("outer " + stage.name().toLowerCase(), mutation.identities().getFirst().key().family()));
    }
  }

  @DisplayName("P2 T1 batch and system-font transactions remain atomic after rejected reentrancy")
  @Test
  void p2T1BatchAndSystemFontTransactionsRemainAtomicAfterRejectedReentrancy() {
    for (PreparationStage stage : PreparationStage.values()) {
      MutablePreparationAttempts batchAttempts = new MutablePreparationAttempts();
      SemanticFontOwner batchOwner = new SemanticFontOwner(List.of());
      batchOwner.install();
      SemanticFontOwner.FontRequest batchCallback =
          requestWithCallback(
              candidate("Batch Second", "fonts/batch-second.ttf", "batch-second", true),
              batchAttempts,
              stage,
              () ->
                  assertThrows(
                      IllegalStateException.class,
                      () ->
                          batchOwner.add(
                              request(
                                  candidate(
                                      "Batch Reentrant",
                                      "fonts/batch-reentrant.ttf",
                                      "batch-reentrant",
                                      true),
                                  batchAttempts))));

      SemanticFontOwner.Mutation batch =
          batchOwner.bootstrap(
              List.of(
                  request(
                      candidate("Batch First", "fonts/batch-first.ttf", "batch-first", true),
                      batchAttempts),
                  batchCallback));

      assertAll(
          () -> assertMutation(result(batch), MutationOutcome.CHANGED, 1),
          () -> assertEquals(2, batch.identities().size()));

      MutablePreparationAttempts systemAttempts = new MutablePreparationAttempts();
      SemanticFontOwner systemOwner = new SemanticFontOwner(List.of());
      systemOwner.install();
      SemanticFontOwner.FontRequest systemCallback =
          requestWithCallback(
              candidate("System First", "fonts/system-first.ttf", "system-first", true),
              systemAttempts,
              stage,
              () ->
                  assertThrows(
                      IllegalStateException.class,
                      systemOwner::clear));

      List<SemanticFontOwner.Mutation> system =
          systemOwner.loadSystemFonts(
              List.of(
                  systemCallback,
                  request(
                      candidate("System Second", "fonts/system-second.ttf", "system-second", true),
                      systemAttempts)));

      assertAll(
          () -> assertMutation(result(system.get(0)), MutationOutcome.CHANGED, 1),
          () -> assertMutation(result(system.get(1)), MutationOutcome.CHANGED, 2),
          () -> assertEquals(2, systemOwner.observation().identities().size()));
    }
  }

  @DisplayName("P2 T1 mutation guard recovers after callback rejection and failure")
  @Test
  void p2T1MutationGuardRecoversAfterCallbackRejectionAndFailure() {
    for (PreparationStage stage : PreparationStage.values()) {
      MutablePreparationAttempts rejectionAttempts = new MutablePreparationAttempts();
      SemanticFontOwner rejectionOwner = new SemanticFontOwner(List.of());
      rejectionOwner.install();
      SemanticFontOwner.FontRequest rejected =
          requestWithCallback(
              candidate("Rejected", "fonts/rejected.ttf", "rejected", true),
              rejectionAttempts,
              stage,
              () ->
                  rejectionOwner.reload(
                      request(
                          candidate(
                              "Reentrant", "fonts/reentrant.ttf", "reentrant", true),
                          rejectionAttempts)));

      SemanticFontOwner.Mutation rejection = rejectionOwner.add(rejected);
      SemanticFontOwner.Mutation afterRejection =
          rejectionOwner.add(
              request(
                  candidate("Recovery", "fonts/recovery.ttf", "recovery", true),
                  rejectionAttempts));

      assertAll(
          () -> assertMutation(result(rejection), MutationOutcome.REJECTED, 0),
          () -> assertMutation(result(afterRejection), MutationOutcome.CHANGED, 1),
          () -> assertEquals(1, afterRejection.identities().size()));

      MutablePreparationAttempts failureAttempts = new MutablePreparationAttempts();
      SemanticFontOwner failureOwner = new SemanticFontOwner(List.of());
      failureOwner.install();
      SemanticFontOwner.FontRequest failed =
          requestWithCallback(
              candidate("Failed", "fonts/failed.ttf", "failed", true),
              failureAttempts,
              stage,
              () -> {
                throw new IllegalArgumentException("Synthetic callback failure");
              });

      SemanticFontOwner.Mutation failure = failureOwner.add(failed);
      SemanticFontOwner.Mutation afterFailure =
          failureOwner.add(
              request(
                  candidate(
                      "Failure Recovery",
                      "fonts/failure-recovery.ttf",
                      "failure-recovery",
                      true),
                  failureAttempts));

      assertAll(
          () -> assertMutation(result(failure), MutationOutcome.REJECTED, 0),
          () -> assertMutation(result(afterFailure), MutationOutcome.CHANGED, 1),
          () -> assertEquals(1, afterFailure.identities().size()));
    }
  }

  @DisplayName("P2 T1 read-use scopes reject during every preparation stage")
  @Test
  void p2T1ReadUseScopesRejectDuringEveryPreparationStageAndRecoverAfterward() {
    for (PreparationStage stage : PreparationStage.values()) {
      MutablePreparationAttempts attempts = new MutablePreparationAttempts();
      SemanticFontOwner owner = new SemanticFontOwner(List.of());
      owner.install();
      SemanticFontOwner.FontRequest request =
          requestWithCallback(
              candidate(
                  "Scoped " + stage,
                  "fonts/scoped-" + stage.name().toLowerCase() + ".ttf",
                  "scoped-" + stage,
                  true),
              attempts,
              stage,
              () ->
                  assertAll(
                      () ->
                          assertThrows(
                              IllegalStateException.class,
                              () ->
                                  owner.openReadUseScope(
                                      SemanticFontOwner.ReadUseKind.MEASUREMENT)),
                      () -> assertThrows(IllegalStateException.class, owner::verifyUse),
                      () ->
                          assertThrows(
                              IllegalStateException.class,
                              owner::completeCloseAfterResourceTeardown)));

      SemanticFontOwner.Mutation mutation = owner.add(request);

      assertMutation(result(mutation), MutationOutcome.CHANGED, 1);
      try (SemanticFontOwner.ReadUseScope measurement =
              owner.openReadUseScope(SemanticFontOwner.ReadUseKind.MEASUREMENT);
          SemanticFontOwner.ReadUseScope layout =
              owner.openReadUseScope(SemanticFontOwner.ReadUseKind.LAYOUT)) {
        assertDoesNotThrow(owner::verifyUse);
      }
    }
  }

  @DisplayName("P2 T1 normalizes locator spaces and Windows dot segments")
  @Test
  void p2T1NormalizedLocatorsAcceptWindowsAndFilePathsWithSpaces() {
    MutablePreparationAttempts attempts = new MutablePreparationAttempts();
    SemanticFontOwner owner = new SemanticFontOwner(List.of());
    owner.install();

    SemanticFontOwner.Mutation windowsPath =
        owner.add(
            request(
                candidate(
                    "Windows Path",
                    "C:\\Program Files\\Spiny Fonts\\Face.ttf",
                    "windows-path",
                    true),
                attempts));
    SemanticFontOwner.Mutation encodedDuplicate =
        owner.add(
            request(
                candidate(
                    "Windows Path",
                    "C:/Program%20Files/Spiny%20Fonts/Face.ttf",
                    "windows-path",
                    true),
                attempts));
    SemanticFontOwner.Mutation windowsDotSegmentDuplicate =
        owner.add(
            request(
                candidate(
                    "Windows Path",
                    "C:/Program%20Files/Spiny%20Fonts/./Nested/../Face.ttf",
                    "windows-path",
                    true),
                attempts));
    SemanticFontOwner.Mutation fileUri =
        owner.add(
            request(
                candidate(
                    "File URI",
                    "file:///C:/Program Files/Spiny Fonts/Face.ttf",
                    "file-uri",
                    true),
                attempts));
    assertAll(
        () -> assertMutation(result(windowsPath), MutationOutcome.CHANGED, 1),
        () ->
            assertEquals(
                "C:/Program%20Files/Spiny%20Fonts/Face.ttf",
                windowsPath.identities().getFirst().normalizedLocator()),
        () -> assertMutation(result(encodedDuplicate), MutationOutcome.UNCHANGED, 1),
        () -> assertMutation(result(windowsDotSegmentDuplicate), MutationOutcome.UNCHANGED, 1),
        () -> assertEquals(encodedDuplicate.identities(), windowsDotSegmentDuplicate.identities()),
        () -> assertMutation(result(fileUri), MutationOutcome.CHANGED, 2),
        () ->
            assertEquals(
                "file:///C:/Program%20Files/Spiny%20Fonts/Face.ttf",
                fileUri.identities().get(1).normalizedLocator()));
  }

  @DisplayName("P3 T2: guarded close lifecycle and post-close rejection")
  @Test
  void p3TargetCloseRequiresOwnerIdleScopeAndRejectsEveryPostCloseOperation()
      throws Exception {
    SemanticOwnerTarget preInstall = p2OwnerTarget();
    assertThrows(IllegalStateException.class, preInstall::close);

    SemanticOwnerTarget offThread = p2OwnerTarget();
    offThread.install();
    MutationResult beforeOffThreadClose = offThread.semanticState();
    Throwable offThreadFailure = captureWorkerFailure(offThread::close);
    assertAll(
        () -> assertInstanceOf(IllegalStateException.class, offThreadFailure),
        () -> assertEquals(beforeOffThreadClose, offThread.semanticState()));
    offThread.close();

    for (ReadUseKind kind : ReadUseKind.values()) {
      SemanticOwnerTarget activeScope = p2OwnerTarget();
      activeScope.install();
      try (ReadUseScope ignored = activeScope.openReadUseScope(kind)) {
        assertThrows(IllegalStateException.class, activeScope::close);
      }
      assertDoesNotThrow(activeScope::close);
    }

    SemanticOwnerTarget target = p2OwnerTarget();
    target.install();

    assertDoesNotThrow(target::close);
    for (OwnerOperation operation : OwnerOperation.values()) {
      assertThrows(
          IllegalStateException.class,
          () -> target.perform(operation),
          () -> operation + " must reject after owner close");
    }
    for (ReadUseKind kind : ReadUseKind.values()) {
      assertThrows(
          IllegalStateException.class,
          () -> target.openReadUseScope(kind),
          () -> kind + " scope must reject after owner close");
    }
    assertDoesNotThrow(target::close);
    assertThrows(IllegalStateException.class, target::semanticState);
    assertNotNull(Font.DEFAULT);
  }

  private Font face(String family, String path) {
    return new Font(
        family, FontStyle.NORMAL, FontStretch.NORMAL, FontWeight.REGULAR, path);
  }

  private Path createFontDirectory(String name) throws Exception {
    return Files.createDirectory(fontDirectory.resolve(name));
  }

  private String createFontFile(Path directory, String name) throws Exception {
    return Files.createFile(directory.resolve(name)).toFile().getAbsolutePath();
  }

  private Path copyResourceFont(String resource, String name) throws Exception {
    return copyResourceFont(fontDirectory, resource, name);
  }

  private Path copyResourceFont(Path directory, String resource, String name) throws Exception {
    Path target = directory.resolve(name);
    try (var stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
      assertNotNull(stream, "test font resource must exist");
      Files.copy(stream, target);
    }
    return target;
  }

  private static int privateMapSize(Object owner, String fieldName) throws Exception {
    Field field = owner.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return ((Map<?, ?>) field.get(owner)).size();
  }

  private static FontCandidate candidate(
      String family, String locator, String byteContent, boolean valid) {
    return new FontCandidate(
        family,
        "normal",
        "regular",
        "normal",
        locator,
        byteContent,
        valid ? FailurePoint.NONE : FailurePoint.VALIDATION);
  }

  private static FontCandidate failingCandidate(String family, FailurePoint failurePoint) {
    return failingCandidate(
        family, "fonts/failure.ttf", "sha256:failure", failurePoint);
  }

  private static FontCandidate failingCandidate(
      String family, String locator, String byteContent, FailurePoint failurePoint) {
    return new FontCandidate(
        family,
        "normal",
        "regular",
        "normal",
        locator,
        byteContent,
        failurePoint);
  }

  private static List<FailurePoint> preparationFailurePoints() {
    return List.of(FailurePoint.STORAGE_LOAD, FailurePoint.PARSE, FailurePoint.VALIDATION);
  }

  private static void assertFailurePreparationDelta(
      PreparationAttempts before,
      PreparationAttempts after,
      FailurePoint failurePoint,
      int successfulCandidatesBeforeFailure) {
    assertEquals(successfulCandidatesBeforeFailure + 1, after.loads() - before.loads());
    assertEquals(
        successfulCandidatesBeforeFailure + (failurePoint == FailurePoint.STORAGE_LOAD ? 0 : 1),
        after.parses() - before.parses());
    assertEquals(
        successfulCandidatesBeforeFailure + (failurePoint == FailurePoint.VALIDATION ? 1 : 0),
        after.validations() - before.validations());
  }

  private static Set<SemanticFaceKey> expectedBuiltInKeys() {
    return Set.of(
        new SemanticFaceKey("roboto", "normal", "light", "normal"),
        new SemanticFaceKey("roboto", "normal", "bold", "normal"),
        new SemanticFaceKey("roboto", "normal", "regular", "normal"),
        new SemanticFaceKey("noto sans cjk sc", "normal", "regular", "normal"));
  }

  private static Set<SemanticFaceKey> semanticKeys(MutationResult result) {
    return result.identities().stream()
        .map(SemanticIdentity::key)
        .collect(Collectors.toUnmodifiableSet());
  }

  private static <T> T callOnWorker(Callable<T> callable) throws Exception {
    AtomicReference<T> result = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread worker =
        new Thread(
            () -> {
              try {
                result.set(callable.call());
              } catch (Throwable throwable) {
                failure.set(throwable);
              }
            },
            "m3-font-contract-worker");
    worker.start();
    worker.join();
    if (failure.get() instanceof Exception exception) throw exception;
    if (failure.get() instanceof Error error) throw error;
    if (failure.get() != null) throw new AssertionError("Worker failed", failure.get());
    return result.get();
  }

  private static Throwable captureWorkerFailure(Runnable operation) throws InterruptedException {
    AtomicReference<Throwable> failure = new AtomicReference<>();
    Thread worker =
        new Thread(
            () -> {
              try {
                operation.run();
              } catch (Throwable throwable) {
                failure.set(throwable);
              }
            },
            "m3-font-contract-worker");
    worker.start();
    worker.join();
    return failure.get();
  }

  private static void assertMutation(
      MutationResult mutation, MutationOutcome outcome, long generation) {
    assertAll(
        () -> assertEquals(outcome, mutation.outcome()),
        () -> assertEquals(generation, mutation.generation()));
  }

  private SemanticRegistryTarget p2Target() {
    MutablePreparationAttempts attempts = new MutablePreparationAttempts();
    SemanticFontOwner owner = new SemanticFontOwner(List.of());
    owner.install();
    return new ProductionSemanticRegistryTarget(owner, attempts);
  }

  private SemanticOwnerTarget p2OwnerTarget() {
    MutablePreparationAttempts attempts = new MutablePreparationAttempts();
    List<FontCandidate> builtIns =
        List.of(
            candidate("Roboto", "fonts/Roboto-Light.ttf", "built-in-light", true)
                .withWeight("light"),
            candidate("Roboto", "fonts/Roboto-Bold.ttf", "built-in-bold", true)
                .withWeight("bold"),
            candidate("Roboto", ROBOTO_REGULAR_PATH, "built-in-regular", true),
            candidate(
                "Noto Sans CJK SC",
                "fonts/NotoSansCJKsc-Regular.otf",
                "built-in-cjk",
                true));
    SemanticFontOwner owner =
        new SemanticFontOwner(
            builtIns.stream().map(candidate -> request(candidate, attempts)).toList());
    return new ProductionSemanticOwnerTarget(owner, attempts);
  }

  private static SemanticFontOwner.FontRequest request(
      FontCandidate candidate, MutablePreparationAttempts attempts) {
    byte[] content = candidate.byteContent().getBytes(StandardCharsets.UTF_8);
    return new SemanticFontOwner.FontRequest(
        candidate.family(),
        candidate.style(),
        candidate.weight(),
        candidate.stretch(),
        candidate.locator(),
        () -> {
          attempts.loads++;
          if (candidate.failurePoint() == FailurePoint.STORAGE_LOAD) {
            return null;
          }
          return ByteBuffer.wrap(content).asReadOnlyBuffer();
        },
        ignored -> {
          attempts.parses++;
          if (candidate.failurePoint() == FailurePoint.PARSE) {
            throw new IllegalArgumentException("Synthetic parse failure");
          }
        },
        (ignoredRequest, ignoredBytes) -> {
          attempts.validations++;
          if (candidate.failurePoint() == FailurePoint.VALIDATION) {
            throw new IllegalArgumentException("Synthetic validation failure");
          }
        });
  }

  private static SemanticFontOwner.FontRequest requestWithCallback(
      FontCandidate candidate,
      MutablePreparationAttempts attempts,
      PreparationStage stage,
      Runnable callback) {
    SemanticFontOwner.FontRequest delegate = request(candidate, attempts);
    return new SemanticFontOwner.FontRequest(
        delegate.family(),
        delegate.style(),
        delegate.weight(),
        delegate.stretch(),
        delegate.locator(),
        () -> {
          if (stage == PreparationStage.LOAD) callback.run();
          return delegate.loader().load();
        },
        bytes -> {
          if (stage == PreparationStage.PARSE) callback.run();
          delegate.parser().parse(bytes);
        },
        (request, bytes) -> {
          if (stage == PreparationStage.VALIDATION) callback.run();
          delegate.validator().validate(request, bytes);
        });
  }

  private static void performReentrantMutation(
      SemanticFontOwner owner,
      ReentrantMutationSurface surface,
      MutablePreparationAttempts attempts) {
    SemanticFontOwner.FontRequest request =
        request(
            candidate(
                "Reentrant " + surface,
                "fonts/reentrant-" + surface.name().toLowerCase() + ".ttf",
                "reentrant-" + surface,
                true),
            attempts);
    switch (surface) {
      case BOOTSTRAP -> owner.bootstrap(List.of(request));
      case ADD -> owner.add(request);
      case RELOAD -> owner.reload(request);
      case SYSTEM_FONTS -> owner.loadSystemFonts(List.of(request));
      case CLEAR -> owner.clear();
      case REMOVE ->
          owner.remove(new SemanticFontOwner.FaceKey("reentrant", "normal", "regular", "normal"));
    }
  }

  private static String byteRevision(FontCandidate candidate) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] revision = digest.digest(candidate.byteContent().getBytes(StandardCharsets.UTF_8));
      return "sha256:" + HexFormat.of().formatHex(revision);
    } catch (NoSuchAlgorithmException exception) {
      throw new AssertionError("SHA-256 must be available", exception);
    }
  }

  private static MutationResult result(SemanticFontOwner.Mutation mutation) {
    return new MutationResult(
        MutationOutcome.valueOf(mutation.outcome().name()),
        mutation.generation(),
        mutation.identities().stream().map(FontSemanticContractTest::identity).toList());
  }

  private static MutationResult result(SemanticFontOwner.Observation observation) {
    return new MutationResult(
        MutationOutcome.UNCHANGED,
        observation.generation(),
        observation.identities().stream().map(FontSemanticContractTest::identity).toList());
  }

  private record FontConsumerKey(String inputIdentity, FontSemanticObservation fonts) {}

  private static SemanticIdentity identity(SemanticFontOwner.Identity identity) {
    SemanticFontOwner.FaceKey key = identity.key();
    return new SemanticIdentity(
        new SemanticFaceKey(key.family(), key.style(), key.weight(), key.stretch()),
        identity.normalizedLocator(),
        identity.byteRevision());
  }

  private static SemanticFontOwner.FaceKey faceKey(SemanticFaceKey key) {
    return new SemanticFontOwner.FaceKey(
        key.family(), key.style(), key.weight(), key.stretch());
  }

  private static void forceGeneration(SemanticFontOwner owner, long generation) {
    try {
      Field snapshotField = SemanticFontOwner.class.getDeclaredField("snapshot");
      snapshotField.setAccessible(true);
      Object current = snapshotField.get(owner);
      Method identities = current.getClass().getDeclaredMethod("identities");
      identities.setAccessible(true);
      Object currentIdentities = identities.invoke(current);
      var constructor = current.getClass().getDeclaredConstructor(long.class, Map.class);
      constructor.setAccessible(true);
      snapshotField.set(owner, constructor.newInstance(generation, currentIdentities));
    } catch (ReflectiveOperationException exception) {
      throw new AssertionError("Semantic owner snapshot structure changed", exception);
    }
  }

  private static final class ProductionSemanticRegistryTarget implements SemanticRegistryTarget {
    private final SemanticFontOwner owner;
    private final MutablePreparationAttempts attempts;

    private ProductionSemanticRegistryTarget(
        SemanticFontOwner owner, MutablePreparationAttempts attempts) {
      this.owner = owner;
      this.attempts = attempts;
    }

    @Override
    public long generation() {
      return owner.generation();
    }

    @Override
    public List<SemanticIdentity> identities() {
      return owner.observation().identities().stream()
          .map(FontSemanticContractTest::identity)
          .toList();
    }

    @Override
    public PreparationAttempts preparationAttempts() {
      return attempts.snapshot();
    }

    @Override
    public MutationResult bootstrap(List<FontCandidate> candidates) {
      return result(
          owner.bootstrap(candidates.stream().map(candidate -> request(candidate, attempts)).toList()));
    }

    @Override
    public MutationResult add(FontCandidate candidate) {
      return result(owner.add(request(candidate, attempts)));
    }

    @Override
    public MutationResult reload(FontCandidate candidate) {
      return result(owner.reload(request(candidate, attempts)));
    }

    @Override
    public List<MutationResult> loadSystemFonts(List<FontCandidate> candidates) {
      return owner
          .loadSystemFonts(
              candidates.stream().map(candidate -> request(candidate, attempts)).toList())
          .stream()
          .map(FontSemanticContractTest::result)
          .toList();
    }

    @Override
    public MutationResult clear() {
      return result(owner.clear());
    }

    @Override
    public MutationResult remove(SemanticFaceKey key) {
      return result(owner.remove(faceKey(key)));
    }

    @Override
    public void forceGenerationForTest(long generation) {
      forceGeneration(owner, generation);
    }
  }

  private static final class ProductionSemanticOwnerTarget implements SemanticOwnerTarget {
    private final SemanticFontOwner owner;
    private final MutablePreparationAttempts attempts;
    private int mutationIndex;

    private ProductionSemanticOwnerTarget(
        SemanticFontOwner owner, MutablePreparationAttempts attempts) {
      this.owner = owner;
      this.attempts = attempts;
    }

    @Override
    public OwnerInstallation install() {
      SemanticFontOwner.Installation installation = owner.install();
      return new OwnerInstallation(installation.ownerThread(), result(installation.bootstrap()));
    }

    @Override
    public Thread ownerThread() {
      return owner.ownerThread();
    }

    @Override
    public MutationResult semanticState() {
      return result(owner.observation());
    }

    @Override
    public void perform(OwnerOperation operation) {
      if (operation == OwnerOperation.SEMANTIC_MUTATION) {
        int index = ++mutationIndex;
        owner.add(
            request(
                candidate(
                    "Owner Mutation " + index,
                    "fonts/owner-mutation-" + index + ".ttf",
                    "owner-mutation-" + index,
                    true),
                attempts));
      } else if (operation == OwnerOperation.REGISTRY_OBSERVATION) {
        owner.observation();
      } else {
        owner.verifyUse();
      }
    }

    @Override
    public ReadUseScope openReadUseScope(ReadUseKind kind) {
      SemanticFontOwner.ReadUseScope scope =
          owner.openReadUseScope(SemanticFontOwner.ReadUseKind.valueOf(kind.name()));
      return scope::close;
    }

    @Override
    public void close() {
      owner.completeCloseAfterResourceTeardown();
    }
  }

  private static final class MutablePreparationAttempts {
    private int loads;
    private int parses;
    private int validations;

    private PreparationAttempts snapshot() {
      return new PreparationAttempts(loads, parses, validations);
    }
  }

  private interface SemanticRegistryTarget {
    long generation();

    List<SemanticIdentity> identities();

    PreparationAttempts preparationAttempts();

    MutationResult bootstrap(List<FontCandidate> candidates);

    MutationResult add(FontCandidate candidate);

    MutationResult reload(FontCandidate candidate);

    List<MutationResult> loadSystemFonts(List<FontCandidate> candidates);

    MutationResult clear();

    MutationResult remove(SemanticFaceKey key);

    void forceGenerationForTest(long generation);
  }

  private interface SemanticOwnerTarget {
    OwnerInstallation install();

    Thread ownerThread();

    MutationResult semanticState();

    void perform(OwnerOperation operation);

    ReadUseScope openReadUseScope(ReadUseKind kind);

    void close();
  }

  private interface ReadUseScope extends AutoCloseable {
    @Override
    void close();
  }

  private record FontCandidate(
      String family,
      String style,
      String weight,
      String stretch,
      String locator,
      String byteContent,
      FailurePoint failurePoint) {
    FontCandidate withWeight(String replacementWeight) {
      return new FontCandidate(
          family, style, replacementWeight, stretch, locator, byteContent, failurePoint);
    }
  }

  private record SemanticFaceKey(String family, String style, String weight, String stretch) {}

  private record SemanticIdentity(
      SemanticFaceKey key, String normalizedLocator, String byteRevision) {}

  private record MutationResult(
      MutationOutcome outcome, long generation, List<SemanticIdentity> identities) {}

  private record PreparationAttempts(int loads, int parses, int validations) {}

  private record OwnerInstallation(Thread ownerThread, MutationResult bootstrap) {}

  private enum MutationOutcome {
    CHANGED,
    UNCHANGED,
    REJECTED
  }

  private enum FailurePoint {
    NONE,
    STORAGE_LOAD,
    PARSE,
    VALIDATION
  }

  private enum PreparationStage {
    LOAD,
    PARSE,
    VALIDATION
  }

  private enum ReentrantMutationSurface {
    BOOTSTRAP,
    ADD,
    RELOAD,
    SYSTEM_FONTS,
    CLEAR,
    REMOVE
  }

  private enum OwnerOperation {
    REGISTRY_OBSERVATION,
    RESOLUTION,
    MEASUREMENT,
    FONT_STORAGE_BYTE_CACHE_ACCESS,
    FONT_SERVICE_INFO_CACHE_ACCESS,
    FUTURE_SEMANTIC_CACHE_ACCESS,
    SEMANTIC_MUTATION
  }

  private enum ReadUseKind {
    MEASUREMENT,
    LAYOUT,
    RENDER
  }
}
