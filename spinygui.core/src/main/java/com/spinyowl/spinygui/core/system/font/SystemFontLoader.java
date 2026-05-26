package com.spinyowl.spinygui.core.system.font;

import static org.slf4j.LoggerFactory.getLogger;
import com.spinyowl.spinygui.core.font.Font;
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

  @NonNull private final FontStorage fontStorage;
  @NonNull private final FontService fontService;
  @NonNull private final FontDirectoriesProvider fontDirectoriesProvider;

  /**
   * Loads all system fonts to {@link FontStorage}
   *
   * @return list of fonts loaded to font storage.
   */
  public List<String> loadSystemFonts() {
    List<String> fontPaths =
        fontDirectoriesProvider.getFontDirectories().stream()
            .map(this::getAllFilesInDirectory)
            .flatMap(List::stream)
            .toList();

    List<String> loadedFonts = new LinkedList<>();
    for (String fontPath : fontPaths) {
      if (fontStorage.loadFont(fontPath) != null) loadedFonts.add(fontPath);
    }

    loadedFonts.forEach(this::loadFontSafe);

    return loadedFonts;
  }

  private void loadFontSafe(String font) {
    try {
      Font.addFont(fontService.loadFont(font));
    } catch (Exception e) {
      LOG.error("Can't load font {}", font, e);
    }
  }

  /** Returns list of files in given directory. */
  private List<String> getAllFilesInDirectory(String directory) {
    Collection<File> files = FileUtils.listFiles(new File(directory), new String[] {"ttf"}, true);
    return files.stream().map(File::getAbsolutePath).toList();
  }
}
