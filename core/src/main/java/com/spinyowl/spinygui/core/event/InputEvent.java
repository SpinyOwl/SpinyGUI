package com.spinyowl.spinygui.core.event;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents an event notifying of editable content change.
 */
@Getter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class InputEvent extends Event {

  /**
   * Inserted characters (if present).
   */
  @NonNull
  private final CharSequence data;

  /**
   * the type of the change.
   */
  @NonNull
  private final InputType inputType;

  @Getter
  @ToString
  @EqualsAndHashCode
  public static final class InputType {

    private static final Map<String, InputType> VALUES = new ConcurrentHashMap<>();

    /**
     * Insert typed plain text
     */
    public static final InputType
        INSERT_TEXT = create("insertText");

    /**
     * Replace existing text by means of a spell checker, auto-correct or similar
     */
    public static final InputType
        INSERT_REPLACEMENT_TEXT = create("insertReplacementText");

    /**
     * Insert a line break
     */
    public static final InputType
        INSERT_LINE_BREAK = create("insertLineBreak");

    /**
     * Insert a paragraph break
     */
    public static final InputType
        INSERT_PARAGRAPH = create("insertParagraph");

    /**
     * Insert a numbered list
     */
    public static final InputType
        INSERT_ORDERED_LIST = create("insertOrderedList");

    /**
     * Insert a bulleted list
     */
    public static final InputType
        INSERT_UNORDERED_LIST = create("insertUnorderedList");

    /**
     * Insert a horizontal rule
     */
    public static final InputType
        INSERT_HORIZONTAL_RULE = create("insertHorizontalRule");

    /**
     * Replace the current selection with content stored in a kill buffer
     */
    public static final InputType
        INSERT_FROM_YANK = create("insertFromYank");

    /**
     * Insert content into the DOM by means of drop
     */
    public static final InputType
        INSERT_FROM_DROP = create("insertFromDrop");

    /**
     * Paste
     */
    public static final InputType
        INSERT_FROM_PASTE = create("insertFromPaste");

    /**
     * Paste content as a quotation
     */
    public static final InputType
        INSERT_FROM_PASTE_AS_QUOTATION = create("insertFromPasteAsQuotation");

    /**
     * Transpose the last two characters that were entered
     */
    public static final InputType
        INSERT_TRANSPOSE = create("insertTranspose");

    /**
     * Replace the current composition string
     */
    public static final InputType
        INSERT_COMPOSITION_TEXT = create("insertCompositionText");

    /**
     * Insert a link
     */
    public static final InputType
        INSERT_LINK = create("insertLink");

    /**
     * Delete a word directly before the caret position
     */
    public static final InputType
        DELETE_WORD_BACKWARD = create("deleteWordBackward");

    /**
     * Delete a word directly after the caret position
     */
    public static final InputType
        DELETE_WORD_FORWARD = create("deleteWordForward");

    /**
     * Delete from the caret to the nearest visual line break before the caret position
     */
    public static final InputType
        DELETE_SOFT_LINE_BACKWARD = create("deleteSoftLineBackward");

    /**
     * Delete from the caret to the nearest visual line break after the caret position
     */
    public static final InputType
        DELETE_SOFT_LINE_FORWARD = create("deleteSoftLineForward");

    /**
     * Delete from to the nearest visual line break before the caret position to the nearest visual
     * line break after the caret position
     */
    public static final InputType
        DELETE_ENTIRE_SOFT_LINE = create("deleteEntireSoftLine");

    /**
     * Delete from the caret to the nearest beginning of a block element or br element before the
     * caret position
     */
    public static final InputType
        DELETE_HARD_LINE_BACKWARD = create("deleteHardLineBackward");

    /**
     * Delete from the caret to the nearest end of a block element or br element after the caret
     * position
     */
    public static final InputType
        DELETE_HARD_LINE_FORWARD = create("deleteHardLineForward");

    /**
     * Remove content from the DOM by means of drag
     */
    public static final InputType
        DELETE_BY_DRAG = create("deleteByDrag");

    /**
     * Remove the current selection as part of a cut
     */
    public static final InputType
        DELETE_BY_CUT = create("deleteByCut");

    /**
     * Delete the selection without specifying the direction of the deletion and this intention is
     * not covered by another inputType
     */
    public static final InputType
        DELETE_CONTENT = create("deleteContent");

    /**
     * Delete the content directly before the caret position and this intention is not covered by
     * another inputType or delete the selection with the selection collapsing to its start after
     * the deletion
     */
    public static final InputType
        DELETE_CONTENT_BACKWARD = create("deleteContentBackward");

    /**
     * Delete the content directly after the caret position and this intention is not covered by
     * another inputType or delete the selection with the selection collapsing to its end after the
     * deletion
     */
    public static final InputType
        DELETE_CONTENT_FORWARD = create("deleteContentForward");

    /**
     * Undo the last editing action
     */
    public static final InputType
        HISTORY_UNDO = create("historyUndo");

    /**
     * To redo the last undone editing action
     */
    public static final InputType
        HISTORY_REDO = create("historyRedo");

    /**
     * Initiate bold text
     */
    public static final InputType
        FORMAT_BOLD = create("formatBold");

    /**
     * Initiate italic text
     */
    public static final InputType
        FORMAT_ITALIC = create("formatItalic");

    /**
     * Initiate underline text
     */
    public static final InputType
        FORMAT_UNDERLINE = create("formatUnderline");

    /**
     * Initiate stricken through text
     */
    public static final InputType
        FORMAT_STRIKE_THROUGH = create("formatStrikeThrough");

    /**
     * Initiate superscript text
     */
    public static final InputType
        FORMAT_SUPERSCRIPT = create("formatSuperscript");

    /**
     * Initiate subscript text
     */
    public static final InputType
        FORMAT_SUBSCRIPT = create("formatSubscript");

    /**
     * Make the current selection fully justified
     */
    public static final InputType
        FORMAT_JUSTIFY_FULL = create("formatJustifyFull");

    /**
     * Center align the current selection
     */
    public static final InputType
        FORMAT_JUSTIFY_CENTER = create("formatJustifyCenter");

    /**
     * Right align the current selection
     */
    public static final InputType
        FORMAT_JUSTIFY_RIGHT = create("formatJustifyRight");

    /**
     * Left align the current selection
     */
    public static final InputType
        FORMAT_JUSTIFY_LEFT = create("formatJustifyLeft");

    /**
     * Indent the current selection
     */
    public static final InputType
        FORMAT_INDENT = create("formatIndent");

    /**
     * Outdent the current selection
     */
    public static final InputType
        FORMAT_OUTDENT = create("formatOutdent");

    /**
     * Remove all formatting from the current selection
     */
    public static final InputType
        FORMAT_REMOVE = create("formatRemove");

    /**
     * Set the text block direction
     */
    public static final InputType
        FORMAT_SET_BLOCK_TEXT_DIRECTION = create("formatSetBlockTextDirection");

    /**
     * Set the text inline direction
     */
    public static final InputType
        FORMAT_SET_INLINE_TEXT_DIRECTION = create("formatSetInlineTextDirection");

    /**
     * Change the background color
     */
    public static final InputType
        FORMAT_BACK_COLOR = create("formatBackColor");

    /**
     * Change the font color
     */
    public static final InputType
        FORMAT_FONT_COLOR = create("formatFontColor");

    /**
     * Change the font-family
     */
    public static final InputType
        FORMAT_FONT_NAME = create("formatFontName");


    /**
     * Name of input type (should be same as in HTML DOM InputEvent object specification).
     */
    private final String name;

    /**
     * Creates input type element with specified name.
     *
     * @param name name of input type (should be same as in HTML DOM InputEvent object
     *             specification).
     */
    private InputType(String name) {
      this.name = name;
    }

    /**
     * Used to create new input type element with specified name. Note that it should be the same as
     * in HTML DOM InputEvent object specification).
     *
     * @param name name of display element.
     * @return new InputType element (or existing one).
     */
    public static InputType create(String name) {
      Objects.requireNonNull(name);
      return VALUES.computeIfAbsent(name, InputType::new);
    }

    /**
     * Used to find display element with specified name. Note that name will be converted to lower
     * case and it should be the same as names of css InputType property in css specification.
     *
     * @param name name of display element.
     * @return existing InputType element or null.
     */
    public static InputType find(String name) {
      return VALUES.get(name);
    }

    /**
     * Returns set of all available display values.
     *
     * @return set of all available display values.
     */
    public static Set<InputType> values() {
      return Set.copyOf(VALUES.values());
    }

    /**
     * Returns true if there is a display value wth specified name.
     *
     * @param name display name.
     * @return true if there is a display value wth specified name.
     */
    public static boolean contains(String name) {
      if (name == null) {
        return false;
      }
      return values().stream().map(InputType::name)
          .anyMatch(v -> v.equalsIgnoreCase(name));
    }
  }

}
