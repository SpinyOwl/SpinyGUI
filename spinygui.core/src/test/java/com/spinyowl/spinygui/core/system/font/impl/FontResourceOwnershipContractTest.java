package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.FontLoadingException;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import com.spinyowl.spinygui.core.system.font.FontSemanticObservation;
import com.spinyowl.spinygui.core.system.font.FontService;
import com.spinyowl.spinygui.core.system.font.FontStorage;
import com.spinyowl.spinygui.core.system.font.SemanticFontOwner;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FontResourceOwnershipContractTest {
  private static final String ROBOTO_REGULAR_PATH = "fonts/Roboto-Regular.ttf";

  @TempDir Path fontDirectory;

  @AfterEach
  void closeProductionOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
  }

  @DisplayName("P3 T2: core maps retain natural owner scope behind service lifecycle")
  @Test
  void currentCoreRetentionIsNaturallyScopedWithoutAnLruOrArbitraryCap() throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    Map<?, ?> bytes = privateMap(storage, "dataMap");
    Map<?, ?> infos = privateMap(service, "fontInfoMap");

    assertAll(
        () -> assertSame(HashMap.class, bytes.getClass()),
        () -> assertSame(HashMap.class, infos.getClass()),
        () -> assertEquals(owner.observation().identities().size(), bytes.size()),
        () -> assertEquals(owner.observation().identities().size(), infos.size()),
        () -> assertTrue(AutoCloseable.class.isAssignableFrom(FontService.class)),
        () -> assertFalse(AutoCloseable.class.isAssignableFrom(FontStorage.class)),
        () -> assertTrue(hasPublicMethod(FontService.class, "clear")),
        () -> assertTrue(hasPublicMethod(FontService.class, "close")),
        () -> assertFalse(hasPublicMethod(FontStorage.class, "clear")),
        () -> assertFalse(hasPublicMethod(FontStorage.class, "close")));
  }

  @DisplayName("P3 T1 current: storage returns read-only aliases with JVM-managed shared lifetime")
  @Test
  void storageAliasesShareDirectBackingAndSurviveEntryReplacement() throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    new FontServiceImpl(storage, false).installSemanticOwner();
    int retainedBefore = privateMap(storage, "dataMap").size();
    ByteBuffer original = IOUtil.asByteBuffer(new byte[] {1, 2, 3, 4});
    storage.commitFontData("fonts/owned face.ttf", original);

    ByteBuffer firstAlias = storage.getFontData("fonts/owned face.ttf");
    ByteBuffer secondAlias = storage.getFontData("fonts/./owned face.ttf");
    original.put(1, (byte) 9);

    ByteBuffer replacement = IOUtil.asByteBuffer(new byte[] {5, 6});
    storage.commitFontData("fonts/owned face.ttf", replacement);
    ByteBuffer currentAlias = storage.getFontData("fonts/owned face.ttf");
    Map<?, ?> retained = privateMap(storage, "dataMap");

    assertAll(
        () -> assertNotNull(firstAlias),
        () -> assertNotNull(secondAlias),
        () -> assertNotSame(firstAlias, secondAlias),
        () -> assertTrue(firstAlias.isDirect()),
        () -> assertTrue(firstAlias.isReadOnly()),
        () -> assertTrue(secondAlias.isReadOnly()),
        () -> assertEquals(9, Byte.toUnsignedInt(firstAlias.get(1))),
        () -> assertEquals(9, Byte.toUnsignedInt(secondAlias.get(1))),
        () -> assertThrows(ReadOnlyBufferException.class, () -> firstAlias.put(0, (byte) 7)),
        () -> assertEquals(retainedBefore + 1, retained.size()),
        () ->
            assertSame(
                replacement,
                retained.get(SemanticFontOwner.normalizeLocator("fonts/owned face.ttf"))),
        () -> assertEquals(1, Byte.toUnsignedInt(firstAlias.get(0))),
        () -> assertEquals(5, Byte.toUnsignedInt(currentAlias.get(0))));
  }

  @DisplayName("P3 T3: public storage reads obey aggregate lifecycle and owner-thread guards")
  @Test
  void publicStorageReadsRejectBeforeInstallOffThreadAndAfterCloseWithoutIssuingAliases()
      throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    storage.commitFontData(
        ROBOTO_REGULAR_PATH, IOUtil.asByteBuffer(new byte[] {1, 2, 3, 4}));

    assertAll(
        () -> assertThrows(IllegalStateException.class, () -> storage.getFontData(ROBOTO_REGULAR_PATH)),
        () -> assertThrows(IllegalStateException.class, () -> storage.getFontData("missing.ttf")),
        () -> assertEquals(0, storage.resourceSnapshot().issuedExternalAliasViews()));

    FontServiceImpl service = new FontServiceImpl(storage, false);
    service.installSemanticOwner();
    ByteBuffer ownerThreadAlias = storage.getFontData(ROBOTO_REGULAR_PATH);
    int ownerThreadFirstByte = Byte.toUnsignedInt(ownerThreadAlias.get(0));
    FontResourceObservation afterOwnerRead = service.resourceObservation();
    long issuedAfterOwnerRead = storage.resourceSnapshot().issuedExternalAliasViews();
    Throwable offThreadExisting =
        captureWorkerFailure(() -> storage.getFontData(ROBOTO_REGULAR_PATH));
    Throwable offThreadMissing = captureWorkerFailure(() -> storage.getFontData("missing.ttf"));

    assertAll(
        () -> assertNotNull(ownerThreadAlias),
        () -> assertTrue(ownerThreadAlias.isReadOnly()),
        () -> assertEquals(1, issuedAfterOwnerRead),
        () -> assertInstanceOf(IllegalStateException.class, offThreadExisting),
        () -> assertInstanceOf(IllegalStateException.class, offThreadMissing),
        () -> assertEquals(afterOwnerRead, service.resourceObservation()),
        () ->
            assertEquals(
                issuedAfterOwnerRead,
                storage.resourceSnapshot().issuedExternalAliasViews()));

    service.close();

    assertAll(
        () -> assertThrows(IllegalStateException.class, () -> storage.getFontData(ROBOTO_REGULAR_PATH)),
        () -> assertThrows(IllegalStateException.class, () -> storage.getFontData("missing.ttf")),
        () ->
            assertEquals(
                issuedAfterOwnerRead,
                storage.resourceSnapshot().issuedExternalAliasViews()),
        () -> assertEquals(ownerThreadFirstByte, Byte.toUnsignedInt(ownerThreadAlias.get(0))));
  }

  @DisplayName("P3 T1 current: failed validation retains no byte or font-info entry")
  @Test
  void failedFontPreparationDoesNotRetainPartialCoreResources() throws Exception {
    FontStorageImpl storage = new FontStorageImpl();
    FontServiceImpl service = new FontServiceImpl(storage, false);
    SemanticFontOwner owner = service.installSemanticOwner();
    FontSemanticObservation before = service.semanticObservation();
    int byteEntriesBefore = privateMap(storage, "dataMap").size();
    int infoEntriesBefore = privateMap(service, "fontInfoMap").size();
    Path invalidFont = fontDirectory.resolve("invalid owner resource.ttf");
    Files.write(invalidFont, new byte[12]);

    assertThrows(FontLoadingException.class, () -> service.loadFont(invalidFont.toString()));

    assertAll(
        () -> assertEquals(before, service.semanticObservation()),
        () -> assertEquals(before.generation(), owner.generation()),
        () -> assertEquals(byteEntriesBefore, privateMap(storage, "dataMap").size()),
        () -> assertEquals(infoEntriesBefore, privateMap(service, "fontInfoMap").size()),
        () -> assertNull(storage.getFontData(invalidFont.toString())));
  }

  @DisplayName("P3 T2: services join one aggregate without restaging or displacing ownership")
  @Test
  void secondServiceSharesTheInstalledAggregateWithoutOrphaningNativeInfo() throws Exception {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
    FontStorageImpl primaryStorage = new FontStorageImpl();
    ResourceLifecycleRecorder primaryRecorder = new ResourceLifecycleRecorder();
    FontServiceImpl primary =
        new FontServiceImpl(
            primaryStorage, false, DiagnosticSession.disabled(), primaryRecorder);
    SemanticFontOwner owner = primary.installSemanticOwner();
    int primaryInfoCount = privateMap(primary, "fontInfoMap").size();
    long generation = owner.generation();

    FontStorageImpl aliasStorage = new FontStorageImpl();
    ResourceLifecycleRecorder aliasRecorder = new ResourceLifecycleRecorder();
    FontServiceImpl alias =
        new FontServiceImpl(aliasStorage, true, DiagnosticSession.disabled(), aliasRecorder);
    SemanticFontOwner joined = alias.installSemanticOwner();

    assertAll(
        () -> assertSame(owner, joined),
        () -> assertSame(Font.semanticService(), primary),
        () -> assertEquals(generation, joined.generation()),
        () -> assertSame(privateMap(primary, "fontInfoMap"), privateMap(alias, "fontInfoMap")),
        () -> assertEquals(primaryInfoCount, privateMap(alias, "fontInfoMap").size()),
        () -> assertTrue(privateMap(aliasStorage, "dataMap").isEmpty()),
        () ->
            assertThrows(
                IllegalStateException.class,
                () -> aliasStorage.getFontData(ROBOTO_REGULAR_PATH)),
        () -> assertEquals(0, aliasStorage.resourceSnapshot().issuedExternalAliasViews()),
        () -> assertTrue(aliasRecorder.preparationEvents().isEmpty()),
        () -> assertDoesNotThrow(() -> alias.getFontMetrics(Font.DEFAULT, 16, 1.2f)));

    alias.close();

    assertAll(
        () -> assertFalse(Font.hasSemanticOwner()),
        () -> assertTrue(privateMap(primary, "fontInfoMap").isEmpty()),
        () -> assertTrue(privateMap(primaryStorage, "dataMap").isEmpty()),
        () -> assertTrue(privateMap(aliasStorage, "dataMap").isEmpty()),
        () -> assertThrows(IllegalStateException.class, primary::semanticObservation),
        () -> assertThrows(IllegalStateException.class, alias::semanticObservation));
  }

  @SuppressWarnings("removal")
  @DisplayName("P3 T2: public owner-only installation cannot create an orphan global owner")
  @Test
  void ownerOnlyInstallationRejectsWithoutPublishingAnyGlobalBinding() {
    SemanticFontOwner owner = new SemanticFontOwner(List.of());
    owner.install();

    UnsupportedOperationException failure =
        assertThrows(UnsupportedOperationException.class, () -> Font.installSemanticOwner(owner));

    assertAll(
        () -> assertTrue(failure.getMessage().contains("FontService")),
        () -> assertFalse(Font.hasSemanticOwner()),
        () -> assertThrows(IllegalStateException.class, Font::semanticService));
    owner.completeCloseAfterResourceTeardown();
  }

  @DisplayName("P3 T2: custom storage measurement rejects before native allocation")
  @Test
  void customStorageCannotCreateMallocOwnedMeasurementInfoWithoutLifecycle() throws Exception {
    AtomicInteger reads = new AtomicInteger();
    FontStorage customStorage =
        new FontStorage() {
          @Override
          public ByteBuffer getFontData(String path) {
            reads.incrementAndGet();
            return IOUtil.asByteBuffer(new byte[12]);
          }

          @Override
          public ByteBuffer loadFont(String fontPath) {
            throw new UnsupportedOperationException();
          }
        };
    ResourceLifecycleRecorder recorder = new ResourceLifecycleRecorder();
    FontServiceImpl service =
        new FontServiceImpl(customStorage, false, DiagnosticSession.disabled(), recorder);

    IllegalStateException failure =
        assertThrows(
            IllegalStateException.class,
            () -> service.getFontMetrics(Font.DEFAULT, 16, 1.2f));

    assertAll(
        () -> assertTrue(failure.getMessage().contains("FontStorageImpl")),
        () -> assertEquals(0, reads.get()),
        () -> assertTrue(recorder.preparationEvents().isEmpty()),
        () -> assertTrue(privateMap(service, "fontInfoMap").isEmpty()));
  }

  @DisplayName("P3 T2: bootstrap cache and binding failures free every staged native info")
  @Test
  void bootstrapPublicationFailuresRetainNoOwnerCacheOrNativeTransfer() throws Exception {
    verifyBootstrapPublicationFailure(
        FontServiceImpl.TransactionStage.BEFORE_CACHE_PUBLICATION,
        new IllegalStateException("injected cache publication failure"));
    verifyBootstrapPublicationFailure(
        FontServiceImpl.TransactionStage.BEFORE_OWNER_BINDING,
        new AssertionError("injected owner binding failure"));
  }

  @DisplayName("P3 T2: load publication failures free staged info and preserve semantic state")
  @Test
  void loadPublicationFailuresAreAtomicForRuntimeExceptionAndError() throws Exception {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
    FontStorageImpl storage = new FontStorageImpl();
    ResourceLifecycleRecorder recorder = new ResourceLifecycleRecorder();
    FailOnceTransactionInjector injector = new FailOnceTransactionInjector();
    FontServiceImpl service =
        new FontServiceImpl(
            storage, false, DiagnosticSession.disabled(), recorder, injector);
    service.installSemanticOwner();
    Path retained = copyResourceFont("publication retained regular.ttf");
    Path candidate = copyResourceFont("publication failure regular.ttf");
    service.loadFont(retained.toString());

    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.AFTER_NATIVE_ALLOCATION,
        new AssertionError("injected post-allocation failure"),
        AssertionError.class);
    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.AFTER_DESCRIPTOR_PARSE,
        new IllegalStateException("injected descriptor failure"),
        FontLoadingException.class);
    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.BEFORE_RETAINED_RETIREMENT,
        new IllegalStateException("injected retirement failure"),
        IllegalStateException.class);
    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.BEFORE_RETAINED_INFO_FREE,
        new AssertionError("injected retained-info free failure"),
        AssertionError.class);
    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.BEFORE_BYTE_CACHE_COMMIT,
        new IllegalStateException("injected byte commit failure"),
        IllegalStateException.class);
    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.BEFORE_INFO_MAP_PUT,
        new AssertionError("injected info-map put failure"),
        AssertionError.class);
    verifyLoadPublicationFailure(
        service,
        storage,
        recorder,
        injector,
        candidate,
        FontServiceImpl.TransactionStage.BEFORE_RESOURCE_TRANSFER,
        new IllegalStateException("injected transfer failure"),
        IllegalStateException.class);
  }

  @DisplayName("P3 T2 target: replacement retires owner entries but preserves old public aliases")
  @Test
  void replacementRetainsOnlyCurrentOwnerResourcesInTeardownOrder() throws Exception {
    CoreResourceLifecycleTarget target = p3ResourceTarget();
    Path firstPath = copyResourceFont("first regular.ttf");
    Path replacementPath = copyResourceFont("replacement regular.ttf");
    Font first = target.load(firstPath.toString());
    ByteBuffer externalAlias = target.alias(first.path());
    byte retainedByte = externalAlias.get(0);
    target.resetLifecycleEvents();

    target.load(replacementPath.toString());
    ResourceRetention retention = target.currentRetention();

    assertAll(
        () -> assertEquals(target.currentSemanticFaceCount(), retention.ownerByteEntries()),
        () -> assertEquals(target.currentSemanticFaceCount(), retention.ownerStbInfoEntries()),
        () -> assertEquals(STAGED_SUCCESS_ORDER, target.preparationEvents()),
        () -> assertEquals(RETAINED_TEARDOWN_ORDER, target.teardownEvents()),
        () -> assertThrows(FontLoadingException.class, () -> target.measure(first)),
        () -> assertEquals(retainedByte, externalAlias.get(0)));
  }

  @DisplayName("P3 T2 target: failed preparation frees its transaction-owned staged STB once")
  @Test
  void failedPreparationFreesStagedOwnerControlledInfoWithoutPublishing() throws Exception {
    CoreResourceLifecycleTarget target = p3ResourceTarget();
    ResourceRetention beforeRetention = target.currentRetention();
    FontSemanticObservation beforeObservation = target.observe();
    Path invalidFont = fontDirectory.resolve("failed staged stb init.ttf");
    Files.write(
        invalidFont,
        new byte[] {0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
    target.resetLifecycleEvents();

    assertThrows(FontLoadingException.class, () -> target.load(invalidFont.toString()));

    assertAll(
        () -> assertEquals(beforeRetention, target.currentRetention()),
        () -> assertEquals(beforeObservation, target.observe()),
        () -> assertEquals(STAGED_FAILURE_ORDER, target.preparationEvents()),
        () -> assertTrue(target.teardownEvents().isEmpty()));
  }

  @DisplayName("P3 T2 target: clear is idempotent and preserves caller-retained aliases")
  @Test
  void clearReleasesOwnerResourcesExactlyOnceWithoutInvalidatingAliases() throws Exception {
    CoreResourceLifecycleTarget target = p3ResourceTarget();
    ByteBuffer externalAlias = target.alias(ROBOTO_REGULAR_PATH);
    byte retainedByte = externalAlias.get(0);
    long generationBefore = target.observe().generation();
    target.resetLifecycleEvents();

    assertDoesNotThrow(target::clear);
    assertDoesNotThrow(target::clear);

    ResourceRetention retention = target.currentRetention();
    assertAll(
        () -> assertEquals(0, retention.ownerByteEntries()),
        () -> assertEquals(0, retention.ownerBytes()),
        () -> assertEquals(0, retention.ownerStbInfoEntries()),
        () -> assertEquals(RETAINED_TEARDOWN_ORDER, target.teardownEvents()),
        () -> assertEquals(retainedByte, externalAlias.get(0)),
        () -> assertEquals(generationBefore + 1, target.observe().generation()));

    assertDoesNotThrow(() -> target.load(ROBOTO_REGULAR_PATH));
  }

  @DisplayName("P3 T2 target: close is owner-thread idempotent and rejects later service use")
  @Test
  void closeRejectsUnsafeUseAndPreservesOnlyJvmManagedExternalAliases() throws Exception {
    CoreResourceLifecycleTarget activeScopeTarget = p3ResourceTarget();
    try (SemanticFontOwner.ReadUseScope ignored = activeScopeTarget.openMeasurementScope()) {
      assertThrows(IllegalStateException.class, activeScopeTarget::close);
      assertTrue(activeScopeTarget.teardownEvents().isEmpty());
    }

    CoreResourceLifecycleTarget target = p3ResourceTarget();
    ByteBuffer externalAlias = target.alias(ROBOTO_REGULAR_PATH);
    byte retainedByte = externalAlias.get(0);
    FontChainResolver resolver = target.resolver();
    Throwable offThreadFailure = captureWorkerFailure(target::close);
    assertInstanceOf(IllegalStateException.class, offThreadFailure);
    assertTrue(target.teardownEvents().isEmpty());

    assertDoesNotThrow(target::close);
    assertDoesNotThrow(target::close);

    ResourceRetention retention = target.currentRetention();
    assertAll(
        () -> assertEquals(0, retention.ownerByteEntries()),
        () -> assertEquals(0, retention.ownerBytes()),
        () -> assertEquals(0, retention.ownerStbInfoEntries()),
        () -> assertEquals(RETAINED_TEARDOWN_ORDER, target.teardownEvents()),
        () -> assertThrows(IllegalStateException.class, target::observe),
        () -> assertThrows(IllegalStateException.class, () -> target.load(ROBOTO_REGULAR_PATH)),
        () -> assertThrows(IllegalStateException.class, () -> target.measure(Font.ROBOTO_REGULAR)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () ->
                    resolver.resolve(
                        List.of("Roboto"),
                        FontStyle.NORMAL,
                        FontWeight.REGULAR,
                        FontStretch.NORMAL)),
        () -> assertEquals(retainedByte, externalAlias.get(0)));
  }

  @DisplayName("P4 T3: registered backend dependency rejects core close before teardown")
  @Test
  void backendCloseDependencyKeepsCoreCloseAtomicAndRecoverable() {
    FontServiceImpl service = new FontServiceImpl(new FontStorageImpl(), false);
    SemanticFontOwner owner = service.installSemanticOwner();
    FontSemanticObservation semanticBefore = service.semanticObservation();
    FontResourceObservation resourcesBefore = service.resourceObservation();
    SemanticFontOwner.ResourceCloseDependencyRegistration dependency =
        owner.registerResourceCloseDependency("test backend context one");
    SemanticFontOwner.ResourceCloseDependencyRegistration secondDependency =
        owner.registerResourceCloseDependency("test backend context two");

    IllegalStateException rejected =
        assertThrows(IllegalStateException.class, service::close);

    assertAll(
        () -> assertTrue(rejected.getMessage().contains("test backend context one")),
        () -> assertTrue(rejected.getMessage().contains("test backend context two")),
        () -> assertEquals(semanticBefore, service.semanticObservation()),
        () -> assertEquals(resourcesBefore, service.resourceObservation()),
        () -> assertSame(service, Font.semanticService()));

    dependency.close();
    dependency.close();
    IllegalStateException stillRejected =
        assertThrows(IllegalStateException.class, service::close);
    assertTrue(stillRejected.getMessage().contains("test backend context two"));
    secondDependency.close();
    assertDoesNotThrow(service::close);
  }

  @DisplayName("P3 T3 target: diagnostics distinguish owner resources from issued aliases")
  @Test
  void retentionDiagnosticsNeverClaimExternalAliasesWereDeterministicallyFreed() throws Exception {
    CoreResourceLifecycleTarget target = p3ResourceTarget();
    ResourceRetention before = target.retentionDiagnostics();
    target.alias(ROBOTO_REGULAR_PATH);
    target.alias(ROBOTO_REGULAR_PATH);
    ResourceRetention afterAliases = target.retentionDiagnostics();

    target.clear();
    ResourceRetention afterClear = target.retentionDiagnostics();

    assertAll(
        () -> assertEquals(before.ownerByteEntries(), afterAliases.ownerByteEntries()),
        () -> assertEquals(before.ownerBytes(), afterAliases.ownerBytes()),
        () -> assertEquals(before.issuedExternalAliasViews() + 2, afterAliases.issuedExternalAliasViews()),
        () ->
            assertEquals(
                FontResourceObservation.AliasLifetime.JVM_MANAGED_CALLER_RETAINABLE,
                afterAliases.aliasLifetime()),
        () -> assertEquals(0, afterClear.ownerByteEntries()),
        () -> assertEquals(0, afterClear.ownerBytes()),
        () -> assertEquals(0, afterClear.ownerStbInfoEntries()),
        () -> assertEquals(afterAliases.issuedExternalAliasViews(), afterClear.issuedExternalAliasViews()),
        () ->
            assertEquals(
                FontResourceObservation.AliasLifetime.JVM_MANAGED_CALLER_RETAINABLE,
                afterClear.aliasLifetime()));
  }

  @DisplayName("P3 T3: owner thread matrix and post-close behavior are deterministic")
  @Test
  void ownerThreadOperationsWorkAndOffThreadOrPostCloseOperationsReject() throws Exception {
    CoreResourceLifecycleTarget target = p3ResourceTarget();
    Path candidate = copyResourceFont("thread matrix regular.ttf");
    FontChainResolver resolver = target.resolver();
    FontSemanticObservation before = target.observe();
    ResourceRetention retainedBefore = target.retentionDiagnostics();

    List<Throwable> offThreadFailures = new ArrayList<>();
    offThreadFailures.add(captureWorkerFailure(() -> target.load(candidate.toString())));
    offThreadFailures.add(
        captureWorkerFailure(
            () ->
                resolver.resolve(
                    List.of("Roboto"),
                    FontStyle.NORMAL,
                    FontWeight.REGULAR,
                    FontStretch.NORMAL)));
    offThreadFailures.add(captureWorkerFailure(() -> target.measure(Font.ROBOTO_REGULAR)));
    offThreadFailures.add(captureWorkerFailure(target::clear));
    offThreadFailures.add(captureWorkerFailure(target::close));
    offThreadFailures.add(captureWorkerFailure(target::retentionDiagnostics));

    assertAll(
        () -> offThreadFailures.forEach(failure -> assertInstanceOf(IllegalStateException.class, failure)),
        () -> assertEquals(before, target.observe()),
        () -> assertEquals(retainedBefore, target.retentionDiagnostics()));

    Font loaded = target.load(candidate.toString());
    assertDoesNotThrow(() -> target.measure(loaded));
    assertDoesNotThrow(
        () ->
            target
                .resolver()
                .resolve(
                    List.of(loaded.fontFamily()),
                    loaded.style(),
                    loaded.weight(),
                    loaded.stretch()));

    long beforeClear = target.observe().generation();
    target.clear();
    long afterClear = target.observe().generation();
    target.clear();
    assertEquals(afterClear, target.observe().generation());
    assertEquals(beforeClear + 1, afterClear);

    target.load(ROBOTO_REGULAR_PATH);
    target.close();
    target.close();

    assertAll(
        () -> assertThrows(IllegalStateException.class, target::observe),
        () -> assertThrows(IllegalStateException.class, target::retentionDiagnostics),
        () -> assertThrows(IllegalStateException.class, target::clear),
        () -> assertThrows(IllegalStateException.class, () -> target.load(candidate.toString())),
        () -> assertThrows(IllegalStateException.class, () -> target.measure(Font.ROBOTO_REGULAR)),
        () ->
            assertThrows(
                IllegalStateException.class,
                () ->
                    resolver.resolve(
                        List.of("Roboto"),
                        FontStyle.NORMAL,
                        FontWeight.REGULAR,
                        FontStretch.NORMAL)));
  }

  @DisplayName("P3 T3: churn retains current resources and preserves pre-close measurement")
  @Test
  void highChurnRetainsOnlyCurrentResourcesAndNeverReusesRetiredInfo() throws Exception {
    CoreResourceLifecycleTarget target = p3ResourceTarget();
    FontMetrics expectedMetrics = target.metrics(Font.ROBOTO_REGULAR);
    List<ByteBuffer> externalAliases = new ArrayList<>();
    Font previous = null;

    for (int index = 0; index < 20; index++) {
      if (index % 4 == 0) {
        Path invalid = fontDirectory.resolve("churn invalid " + index + ".ttf");
        Files.write(invalid, new byte[12]);
        FontSemanticObservation beforeFailure = target.observe();
        ResourceRetention retentionBeforeFailure = target.retentionDiagnostics();
        assertThrows(FontLoadingException.class, () -> target.load(invalid.toString()));
        assertEquals(beforeFailure, target.observe());
        assertEquals(retentionBeforeFailure, target.retentionDiagnostics());
      }

      Path currentPath = copyResourceFont("churn current " + index + ".ttf");
      Font current = target.load(currentPath.toString());
      ByteBuffer alias = target.alias(current.path());
      assertNotNull(alias);
      externalAliases.add(alias);

      if (previous != null) {
        Font retired = previous;
        assertThrows(FontLoadingException.class, () -> target.measure(retired));
      }

      ResourceRetention retention = target.retentionDiagnostics();
      assertAll(
          () -> assertEquals(target.currentSemanticFaceCount(), retention.ownerByteEntries()),
          () -> assertEquals(target.currentSemanticFaceCount(), retention.ownerStbInfoEntries()),
          () -> assertTrue(retention.ownerBytes() > 0),
          () -> assertEquals(expectedMetrics, target.metrics(current)),
          () ->
              assertEquals(
                  externalAliases.size(), retention.issuedExternalAliasViews()),
          () ->
              assertEquals(
                  FontResourceObservation.AliasLifetime.JVM_MANAGED_CALLER_RETAINABLE,
                  retention.aliasLifetime()));
      previous = current;
    }

    byte expectedFirstByte = externalAliases.getFirst().get(0);
    assertAll(
        () -> assertEquals(20, externalAliases.size()),
        () ->
            externalAliases.forEach(
                alias -> assertEquals(expectedFirstByte, alias.get(0))));
  }

  private void verifyBootstrapPublicationFailure(
      FontServiceImpl.TransactionStage stage, Throwable injected) throws Exception {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
    FontStorageImpl storage = new FontStorageImpl();
    ResourceLifecycleRecorder recorder = new ResourceLifecycleRecorder();
    FailOnceTransactionInjector injector = new FailOnceTransactionInjector();
    injector.arm(stage, injected);
    FontServiceImpl service =
        new FontServiceImpl(
            storage, false, DiagnosticSession.disabled(), recorder, injector);

    Throwable failure = assertThrows(Throwable.class, service::installSemanticOwner);

    assertAll(
        () -> {
          if (injected instanceof Error) {
            assertSame(injected, failure);
          } else {
            assertInstanceOf(IllegalStateException.class, failure);
          }
        },
        () -> assertFalse(Font.hasSemanticOwner()),
        () -> assertTrue(privateMap(storage, "dataMap").isEmpty()),
        () -> assertTrue(privateMap(service, "fontInfoMap").isEmpty()),
        () ->
            assertEquals(
                4,
                recorder.count(
                    CoreResourceEvent.ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO)),
        () ->
            assertEquals(
                4,
                recorder.count(
                    CoreResourceEvent.FREE_TRANSACTION_OWNED_STAGED_STBTT_FONT_INFO)),
        () ->
            assertEquals(
                4, recorder.count(CoreResourceEvent.DROP_STAGED_BYTE_OWNER_REFERENCE)),
        () ->
            assertEquals(
                0,
                recorder.count(
                    CoreResourceEvent
                        .TRANSFER_STAGED_STBTT_FONT_INFO_AFTER_SUCCESSFUL_PUBLICATION)));
  }

  private void verifyLoadPublicationFailure(
      FontServiceImpl service,
      FontStorageImpl storage,
      ResourceLifecycleRecorder recorder,
      FailOnceTransactionInjector injector,
      Path candidate,
      FontServiceImpl.TransactionStage stage,
      Throwable injected,
      Class<? extends Throwable> expectedFailure)
      throws Exception {
    FontSemanticObservation before = service.semanticObservation();
    List<Font> registeredBefore = Font.fonts();
    Map<String, ?> bytesBefore = Map.copyOf(privateMap(storage, "dataMap"));
    Map<String, ?> infosBefore = Map.copyOf(privateMap(service, "fontInfoMap"));
    recorder.reset();
    injector.arm(stage, injected);

    Throwable failure = assertThrows(expectedFailure, () -> service.loadFont(candidate.toString()));
    boolean failedAfterTransfer =
        stage == FontServiceImpl.TransactionStage.BEFORE_RETAINED_RETIREMENT
            || stage == FontServiceImpl.TransactionStage.BEFORE_RETAINED_INFO_FREE;

    assertAll(
        () -> {
          if (injected instanceof Error) {
            assertSame(injected, failure);
          }
        },
        () -> assertEquals(before, service.semanticObservation()),
        () -> assertEquals(registeredBefore, Font.fonts()),
        () -> assertSameMapEntries(bytesBefore, privateMap(storage, "dataMap")),
        () -> assertSameMapEntries(infosBefore, privateMap(service, "fontInfoMap")),
        () ->
            assertEquals(
                1,
                recorder.count(
                    CoreResourceEvent.ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO)),
        () ->
            assertEquals(
                failedAfterTransfer ? 0 : 1,
                recorder.count(
                    CoreResourceEvent.FREE_TRANSACTION_OWNED_STAGED_STBTT_FONT_INFO)),
        () ->
            assertEquals(
                failedAfterTransfer ? 0 : 1,
                recorder.count(CoreResourceEvent.DROP_STAGED_BYTE_OWNER_REFERENCE)),
        () ->
            assertEquals(
                failedAfterTransfer ? 1 : 0,
                recorder.count(
                    CoreResourceEvent
                        .TRANSFER_STAGED_STBTT_FONT_INFO_AFTER_SUCCESSFUL_PUBLICATION)),
        () ->
            assertEquals(
                failedAfterTransfer ? 1 : 0,
                recorder.count(
                    CoreResourceEvent.FREE_ROLLED_BACK_TRANSFERRED_STBTT_FONT_INFO)),
        () ->
            assertEquals(
                failedAfterTransfer ? 1 : 0,
                recorder.count(
                    CoreResourceEvent.DROP_ROLLED_BACK_TRANSFERRED_BYTE_OWNER_REFERENCE)));
  }

  private Path copyResourceFont(String fileName) throws Exception {
    ByteBuffer source = IOUtil.resourceAsByteBuffer(ROBOTO_REGULAR_PATH);
    assertNotNull(source);
    byte[] bytes = new byte[source.remaining()];
    source.get(bytes);
    return Files.write(fontDirectory.resolve(fileName), bytes);
  }

  private static boolean hasPublicMethod(Class<?> type, String name) {
    return java.util.Arrays.stream(type.getMethods()).anyMatch(method -> method.getName().equals(name));
  }

  private static void assertSameMapEntries(Map<String, ?> expected, Map<String, ?> actual) {
    assertEquals(expected.keySet(), actual.keySet());
    expected.forEach((key, value) -> assertSame(value, actual.get(key), key));
  }

  @SuppressWarnings("unchecked")
  private static Map<String, ?> privateMap(Object owner, String fieldName) throws Exception {
    Field field = owner.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return (Map<String, ?>) field.get(owner);
  }

  private static Throwable captureWorkerFailure(ThrowingRunnable action) throws InterruptedException {
    List<Throwable> failures = new ArrayList<>();
    Thread worker =
        new Thread(
            () -> {
              try {
                action.run();
              } catch (Throwable failure) {
                failures.add(failure);
              }
            },
            "font-resource-lifecycle-off-thread");
    worker.start();
    worker.join();
    return failures.isEmpty() ? null : failures.getFirst();
  }

  private CoreResourceLifecycleTarget p3ResourceTarget() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
    FontStorageImpl storage = new FontStorageImpl();
    ResourceLifecycleRecorder recorder = new ResourceLifecycleRecorder();
    FontServiceImpl service =
        new FontServiceImpl(storage, false, DiagnosticSession.disabled(), recorder);
    service.installSemanticOwner();
    return new CurrentResourceLifecycleTarget(storage, service, recorder);
  }

  private static final List<CoreResourceEvent> STAGED_SUCCESS_ORDER =
      List.of(
          CoreResourceEvent.ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO,
          CoreResourceEvent.DISCARD_STAGED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
          CoreResourceEvent.TRANSFER_STAGED_STBTT_FONT_INFO_AFTER_SUCCESSFUL_PUBLICATION);

  private static final List<CoreResourceEvent> STAGED_FAILURE_ORDER =
      List.of(
          CoreResourceEvent.ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO,
          CoreResourceEvent.DISCARD_STAGED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
          CoreResourceEvent.FREE_TRANSACTION_OWNED_STAGED_STBTT_FONT_INFO,
          CoreResourceEvent.DROP_STAGED_BYTE_OWNER_REFERENCE);

  private static final List<CoreResourceEvent> RETAINED_TEARDOWN_ORDER =
      List.of(
          CoreResourceEvent.STOP_USE,
          CoreResourceEvent.CLEAR_DEPENDENT_MEASUREMENT_AND_INFO_REFERENCES,
          CoreResourceEvent.DISCARD_RETAINED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
          CoreResourceEvent.FREE_RETAINED_OWNER_CONTROLLED_STBTT_FONT_INFO,
          CoreResourceEvent.DROP_RETAINED_BYTE_OWNER_REFERENCES);

  private enum CoreResourceEvent {
    ALLOCATE_TRANSACTION_OWNED_STBTT_FONT_INFO,
    DISCARD_STAGED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
    TRANSFER_STAGED_STBTT_FONT_INFO_AFTER_SUCCESSFUL_PUBLICATION,
    FREE_TRANSACTION_OWNED_STAGED_STBTT_FONT_INFO,
    DROP_STAGED_BYTE_OWNER_REFERENCE,
    FREE_ROLLED_BACK_TRANSFERRED_STBTT_FONT_INFO,
    DROP_ROLLED_BACK_TRANSFERRED_BYTE_OWNER_REFERENCE,
    STOP_USE,
    CLEAR_DEPENDENT_MEASUREMENT_AND_INFO_REFERENCES,
    DISCARD_RETAINED_BORROWED_STB_NAME_TABLE_AND_BYTE_BUFFER_VIEWS,
    FREE_RETAINED_OWNER_CONTROLLED_STBTT_FONT_INFO,
    DROP_RETAINED_BYTE_OWNER_REFERENCES
  }

  private record ResourceRetention(
      int ownerByteEntries,
      long ownerBytes,
      int ownerStbInfoEntries,
      long issuedExternalAliasViews,
      FontResourceObservation.AliasLifetime aliasLifetime) {}

  private interface CoreResourceLifecycleTarget {
    Font load(String path) throws Exception;

    ByteBuffer alias(String path);

    int currentSemanticFaceCount();

    ResourceRetention currentRetention() throws Exception;

    ResourceRetention retentionDiagnostics();

    FontSemanticObservation observe();

    SemanticFontOwner.ReadUseScope openMeasurementScope();

    FontChainResolver resolver();

    void measure(Font font);

    FontMetrics metrics(Font font);

    void clear() throws Exception;

    void close() throws Exception;

    void resetLifecycleEvents();

    List<CoreResourceEvent> preparationEvents();

    List<CoreResourceEvent> teardownEvents();
  }

  private static final class CurrentResourceLifecycleTarget implements CoreResourceLifecycleTarget {
    private final FontStorageImpl storage;
    private final FontServiceImpl service;
    private final ResourceLifecycleRecorder recorder;

    private CurrentResourceLifecycleTarget(
        FontStorageImpl storage,
        FontServiceImpl service,
        ResourceLifecycleRecorder recorder) {
      this.storage = storage;
      this.service = service;
      this.recorder = recorder;
    }

    @Override
    public Font load(String path) throws Exception {
      return service.loadFont(path);
    }

    @Override
    public ByteBuffer alias(String path) {
      return storage.getFontData(path);
    }

    @Override
    public int currentSemanticFaceCount() {
      return service.semanticObservation().identities().size();
    }

    @Override
    public ResourceRetention currentRetention() throws Exception {
      Map<String, ?> bytes = privateMap(storage, "dataMap");
      Map<String, ?> infos = privateMap(service, "fontInfoMap");
      long retainedBytes =
          bytes.values().stream().map(ByteBuffer.class::cast).mapToLong(ByteBuffer::capacity).sum();
      return new ResourceRetention(
          bytes.size(),
          retainedBytes,
          infos.size(),
          0,
          FontResourceObservation.AliasLifetime.JVM_MANAGED_CALLER_RETAINABLE);
    }

    @Override
    public ResourceRetention retentionDiagnostics() {
      FontResourceObservation observation = service.resourceObservation();
      return new ResourceRetention(
          observation.ownerByteEntries(),
          observation.ownerByteCapacity(),
          observation.ownerStbInfoEntries(),
          observation.issuedExternalAliasViews(),
          observation.aliasLifetime());
    }

    @Override
    public FontSemanticObservation observe() {
      return service.semanticObservation();
    }

    @Override
    public SemanticFontOwner.ReadUseScope openMeasurementScope() {
      return service.installSemanticOwner().openReadUseScope(SemanticFontOwner.ReadUseKind.MEASUREMENT);
    }

    @Override
    public FontChainResolver resolver() {
      return service.fontChainResolver();
    }

    @Override
    public void measure(Font font) {
      service.getFontMetrics(font, 16, 1.2f);
    }

    @Override
    public FontMetrics metrics(Font font) {
      return service.getFontMetrics(font, 16, 1.2f);
    }

    @Override
    public void clear() throws Exception {
      service.clear();
    }

    @Override
    public void close() throws Exception {
      service.close();
    }

    @Override
    public void resetLifecycleEvents() {
      recorder.reset();
    }

    @Override
    public List<CoreResourceEvent> preparationEvents() {
      return recorder.preparationEvents();
    }

    @Override
    public List<CoreResourceEvent> teardownEvents() {
      return recorder.teardownEvents();
    }
  }

  private static final class ResourceLifecycleRecorder
      extends FontServiceImpl.ResourceLifecycleObserver {
    private final List<CoreResourceEvent> preparationEvents = new ArrayList<>();
    private final List<CoreResourceEvent> teardownEvents = new ArrayList<>();

    @Override
    void preparation(FontServiceImpl.ResourceLifecycleEvent event) {
      preparationEvents.add(CoreResourceEvent.valueOf(event.name()));
    }

    @Override
    void teardown(FontServiceImpl.ResourceLifecycleEvent event) {
      teardownEvents.add(CoreResourceEvent.valueOf(event.name()));
    }

    private void reset() {
      preparationEvents.clear();
      teardownEvents.clear();
    }

    private List<CoreResourceEvent> preparationEvents() {
      return List.copyOf(preparationEvents);
    }

    private List<CoreResourceEvent> teardownEvents() {
      return List.copyOf(teardownEvents);
    }

    private long count(CoreResourceEvent event) {
      return preparationEvents.stream().filter(event::equals).count()
          + teardownEvents.stream().filter(event::equals).count();
    }
  }

  private static final class FailOnceTransactionInjector
      extends FontServiceImpl.TransactionFailureInjector {
    private FontServiceImpl.TransactionStage stage;
    private Throwable failure;

    private void arm(FontServiceImpl.TransactionStage stage, Throwable failure) {
      this.stage = stage;
      this.failure = failure;
    }

    @Override
    void before(FontServiceImpl.TransactionStage currentStage) {
      if (currentStage != stage || failure == null) {
        return;
      }
      Throwable selected = failure;
      failure = null;
      if (selected instanceof RuntimeException runtimeException) {
        throw runtimeException;
      }
      if (selected instanceof Error error) {
        throw error;
      }
      throw new AssertionError("Unsupported injected throwable", selected);
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }
}
