package com.spinyowl.spinygui.core.system.font;

import static org.slf4j.LoggerFactory.getLogger;
import com.spinyowl.spinygui.core.font.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Builder
public class SystemFontLoader {
  private static final Logger LOG = getLogger(SystemFontLoader.class);

  @NonNull private final FontService fontService;
  @NonNull private final FontDirectoriesProvider fontDirectoriesProvider;

  /** Returns list of files in given directory. */
  private List<String> getAllFilesInDirectory(String directory) {
    Collection<File> files = FileUtils.listFiles(new File(directory), new String[] {"ttf"}, true);
    return files.stream().map(File::getAbsolutePath).toList();
  }

  public List<Font> loadSystemFonts() {
    List<String> fonts =
        fontDirectoriesProvider.getFontDirectories().stream()
            .map(this::getAllFilesInDirectory)
            .flatMap(List::stream)
            .toList();

    List<Font> list = new ArrayList<>();
    for (String font : fonts) {
      try {

        Font loadFont = fontService.loadFont(font);
        if (loadFont != null) {
          list.add(loadFont);
        }
      } catch (RuntimeException e) {
        // we don't want to fail on font loading, so we just log it.
        LOG.error(e.getMessage(), e);
      }
    }
    return list;
  }

  public void loadAndRegisterSystemFonts() {
    loadSystemFonts().forEach(Font::addFont);
  }
}
