package com.spinyowl.spinygui.core.system.font;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FontChainResolverTest {
  private FontServiceImpl fontService;

  @BeforeEach
  void installProductionOwner() {
    if (Font.hasSemanticOwner()) {
      Font.semanticService().close();
    }
    fontService = new FontServiceImpl(new FontStorageImpl(), false);
    fontService.installSemanticOwner();
  }

  @Test
  void productionServiceAndCompatibilityDefaultDelegateToTheOwnersSingleResolver() {
    Font custom =
        register(
            new Font(
                "T3 Owner Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t3-owner.ttf"));

    assertAllResolversReturn(custom);
    assertSame(Font.semanticOwner().resolver(), fontService.fontChainResolver());
  }

  @SuppressWarnings("deprecation")
  @Test
  void compatibilityServiceConstructorCannotInstallAnIndependentResolver() {
    FontChainResolver independent = (families, style, weight, stretch) -> List.of();
    FontServiceImpl compatibility =
        new FontServiceImpl(new FontStorageImpl(), false, independent);
    compatibility.installSemanticOwner();

    assertNotSame(independent, compatibility.fontChainResolver());
    assertSame(Font.semanticOwner().resolver(), compatibility.fontChainResolver());
  }

  @Test
  void resolve_prefersCssFamilyOrderOverRegistryInsertionOrder() {
    Font second =
        register(
            new Font(
                "T2 Second Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-second.ttf"));
    Font first =
        register(
            new Font(
                "T2 First Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-first.ttf"));

    List<Font> resolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 First Family", "T2 Second Family"),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);

    assertEquals(List.of(first, second), resolved);
  }

  @Test
  void resolve_skipsUnavailableFamiliesWithoutSelectingOtherRegisteredFonts() {
    List<Font> unavailableOnly =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Unavailable Family"),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);
    List<Font> resolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Unavailable Family", Font.DEFAULT.fontFamily()),
            FontStyle.NORMAL,
            FontWeight.REGULAR,
            FontStretch.NORMAL);

    assertTrue(unavailableOnly.isEmpty());
    assertEquals(Font.DEFAULT, resolved.getFirst());
  }

  @Test
  void resolve_retainsNamedLatinIconAndEmojiPrimaryFaces() {
    Font icon =
        register(
            new Font(
                "T2 Icon Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-icon.ttf"));
    Font emoji =
        register(
            new Font(
                "T2 Emoji Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.REGULAR,
                "fonts/t2-emoji.ttf"));

    assertEquals(
        Font.DEFAULT,
        resolve(List.of(Font.DEFAULT.fontFamily(), icon.fontFamily(), emoji.fontFamily()))
            .getFirst());
    assertEquals(icon, resolve(List.of(icon.fontFamily(), emoji.fontFamily())).getFirst());
    assertEquals(emoji, resolve(List.of(emoji.fontFamily(), icon.fontFamily())).getFirst());
  }

  @Test
  void resolve_prefersExactFaceThenDeterministicNearestFaceWithinAFamily() {
    register(
        new Font(
            "T2 Matching Family",
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.BOLD,
            "fonts/t2-fallback.ttf"));
    Font exact =
        register(
            new Font(
                "T2 Matching Family",
                FontStyle.ITALIC,
                FontStretch.NORMAL,
                FontWeight.BOLD,
                "fonts/t2-exact.ttf"));
    register(
        new Font(
            "T2 Nearest Family",
            FontStyle.NORMAL,
            FontStretch.NORMAL,
            FontWeight.REGULAR,
            "fonts/t2-nearest-regular.ttf"));
    Font nearest =
        register(
            new Font(
                "T2 Nearest Family",
                FontStyle.NORMAL,
                FontStretch.NORMAL,
                FontWeight.BOLD,
                "fonts/t2-nearest-bold.ttf"));

    List<Font> exactResolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Matching Family"),
            FontStyle.ITALIC,
            FontWeight.BOLD,
            FontStretch.NORMAL);
    List<Font> fallbackResolved =
        FontChainResolver.DEFAULT.resolve(
            List.of("T2 Nearest Family"),
            FontStyle.ITALIC,
            FontWeight.BOLD,
            FontStretch.NORMAL);

    assertEquals(exact, exactResolved.getFirst());
    assertEquals(nearest, fallbackResolved.getFirst());
  }

  private List<Font> resolve(List<String> families) {
    return FontChainResolver.DEFAULT.resolve(
        families, FontStyle.NORMAL, FontWeight.REGULAR, FontStretch.NORMAL);
  }

  private Font register(Font font) {
    SemanticFontOwner.Mutation mutation =
        Font.semanticOwner()
            .add(
                SemanticFontOwner.FontRequest.from(
                    font,
                    () ->
                        ByteBuffer.wrap(font.path().getBytes(StandardCharsets.UTF_8)),
                    bytes -> {},
                    (request, bytes) -> {}));
    assertTrue(mutation.outcome() != SemanticFontOwner.MutationOutcome.REJECTED);
    return font;
  }

  private void assertAllResolversReturn(Font font) {
    List<Font> ownerResolved = resolveWith(Font.semanticOwner().resolver(), font.fontFamily());
    List<Font> serviceResolved = resolveWith(fontService.fontChainResolver(), font.fontFamily());
    List<Font> compatibilityResolved = resolveWith(FontChainResolver.DEFAULT, font.fontFamily());

    assertEquals(List.of(font), ownerResolved);
    assertEquals(ownerResolved, serviceResolved);
    assertEquals(ownerResolved, compatibilityResolved);
  }

  private List<Font> resolveWith(FontChainResolver resolver, String family) {
    return resolver.resolve(
        List.of(family), FontStyle.NORMAL, FontWeight.REGULAR, FontStretch.NORMAL);
  }
}
