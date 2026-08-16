package com.spinyowl.spinygui.core.system.font;

import static org.slf4j.LoggerFactory.getLogger;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Builder
public class SystemFontLoader {
  private static final Logger LOG = getLogger(SystemFontLoader.class);

  /** Retained only for builder/source compatibility; mutation is owned by {@link FontService}. */
  @Deprecated(forRemoval = true)
  @NonNull
  private final FontStorage fontStorage;
  @NonNull private final FontService fontService;
  @NonNull private final FontDirectoriesProvider fontDirectoriesProvider;

  /**
   * Discovers {@code .ttf} files and asks the explicitly installed {@link FontService} to stage,
   * validate, and publish each face as one independent semantic transaction. A failed face is logged
   * without publishing storage/service/registry state and does not prevent later paths from being
   * attempted.
   *
   * @return paths successfully published by the semantic owner
   */
  public List<String> loadSystemFonts() {
    List<String> fontPaths =
        fontDirectoriesProvider.getFontDirectories().stream()
            .map(this::getAllFilesInDirectory)
            .flatMap(List::stream)
            .toList();

    List<String> loadedFonts = new LinkedList<>();
    for (String fontPath : fontPaths) {
      if (loadFontSafe(fontPath)) {
        loadedFonts.add(fontPath);
      }
    }

    return List.copyOf(loadedFonts);
  }

  private boolean loadFontSafe(String font) {
    try {
      fontService.loadFont(font);
      return true;
    } catch (Exception e) {
      LOG.error("Can't load font {}", font, e);
      return false;
    }
  }

  /** Returns list of files in given directory. */
  private List<String> getAllFilesInDirectory(String directory) {
    Collection<File> files = FileUtils.listFiles(new File(directory), new String[] {"ttf"}, true);
    return files.stream().map(File::getAbsolutePath).toList();
  }
}
