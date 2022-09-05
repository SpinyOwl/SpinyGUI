// Generated from
// core/src/main/resources/grammars\CSS3.g4 by ANTLR 4.10.1
package com.spinyowl.spinygui.core.parser.impl.css.parser.antlr;

import java.util.List;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CSS3Parser extends Parser {
  static {
    RuntimeMetaData.checkVersion("4.10.1", RuntimeMetaData.VERSION);
  }

  protected static final DFA[] _decisionToDFA;
  protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
  public static final int T__0 = 1,
      T__1 = 2,
      T__2 = 3,
      T__3 = 4,
      T__4 = 5,
      T__5 = 6,
      T__6 = 7,
      T__7 = 8,
      T__8 = 9,
      T__9 = 10,
      T__10 = 11,
      T__11 = 12,
      T__12 = 13,
      T__13 = 14,
      T__14 = 15,
      Comment = 16,
      Space = 17,
      Cdo = 18,
      Cdc = 19,
      Includes = 20,
      DashMatch = 21,
      Hash = 22,
      Import = 23,
      Page = 24,
      Media = 25,
      Namespace = 26,
      Charset = 27,
      Important = 28,
      Percentage = 29,
      Uri = 30,
      UnicodeRange = 31,
      MediaOnly = 32,
      Not = 33,
      And = 34,
      Dimension = 35,
      UnknownDimension = 36,
      Plus = 37,
      Minus = 38,
      Greater = 39,
      Comma = 40,
      Tilde = 41,
      PseudoNot = 42,
      Number = 43,
      String = 44,
      PrefixMatch = 45,
      SuffixMatch = 46,
      SubstringMatch = 47,
      FontFace = 48,
      Supports = 49,
      Or = 50,
      Keyframes = 51,
      From = 52,
      To = 53,
      Calc = 54,
      Viewport = 55,
      CounterStyle = 56,
      FontFeatureValues = 57,
      DxImageTransform = 58,
      Variable = 59,
      Var = 60,
      Ident = 61,
      Function = 62;
  public static final int RULE_stylesheet = 0,
      RULE_charset = 1,
      RULE_imports = 2,
      RULE_namespace = 3,
      RULE_namespacePrefix = 4,
      RULE_media = 5,
      RULE_mediaQueryList = 6,
      RULE_mediaQuery = 7,
      RULE_mediaType = 8,
      RULE_mediaExpression = 9,
      RULE_mediaFeature = 10,
      RULE_page = 11,
      RULE_pseudoPage = 12,
      RULE_selectorGroup = 13,
      RULE_selector = 14,
      RULE_combinator = 15,
      RULE_simpleSelectorSequence = 16,
      RULE_typeSelector = 17,
      RULE_typeNamespacePrefix = 18,
      RULE_elementName = 19,
      RULE_universal = 20,
      RULE_className = 21,
      RULE_attrib = 22,
      RULE_pseudo = 23,
      RULE_functionalPseudo = 24,
      RULE_expression = 25,
      RULE_negation = 26,
      RULE_negationArg = 27,
      RULE_operator = 28,
      RULE_property = 29,
      RULE_ruleset = 30,
      RULE_declarationList = 31,
      RULE_declaration = 32,
      RULE_prio = 33,
      RULE_value = 34,
      RULE_expr = 35,
      RULE_term = 36,
      RULE_function = 37,
      RULE_dxImageTransform = 38,
      RULE_hexcolor = 39,
      RULE_number = 40,
      RULE_percentage = 41,
      RULE_dimension = 42,
      RULE_unknownDimension = 43,
      RULE_any = 44,
      RULE_atRule = 45,
      RULE_atKeyword = 46,
      RULE_unused = 47,
      RULE_block = 48,
      RULE_nestedStatement = 49,
      RULE_groupRuleBody = 50,
      RULE_supportsRule = 51,
      RULE_supportsCondition = 52,
      RULE_supportsConditionInParens = 53,
      RULE_supportsNegation = 54,
      RULE_supportsConjunction = 55,
      RULE_supportsDisjunction = 56,
      RULE_supportsDeclarationCondition = 57,
      RULE_generalEnclosed = 58,
      RULE_var = 59,
      RULE_calc = 60,
      RULE_calcSum = 61,
      RULE_calcProduct = 62,
      RULE_calcValue = 63,
      RULE_fontFaceRule = 64,
      RULE_fontFaceDeclaration = 65,
      RULE_keyframesRule = 66,
      RULE_keyframesBlocks = 67,
      RULE_keyframeSelector = 68,
      RULE_viewport = 69,
      RULE_counterStyle = 70,
      RULE_fontFeatureValuesRule = 71,
      RULE_fontFamilyNameList = 72,
      RULE_fontFamilyName = 73,
      RULE_featureValueBlock = 74,
      RULE_featureType = 75,
      RULE_featureValueDefinition = 76,
      RULE_ident = 77,
      RULE_ws = 78;

  private static String[] makeRuleNames() {
    return new String[] {
      "stylesheet",
      "charset",
      "imports",
      "namespace",
      "namespacePrefix",
      "media",
      "mediaQueryList",
      "mediaQuery",
      "mediaType",
      "mediaExpression",
      "mediaFeature",
      "page",
      "pseudoPage",
      "selectorGroup",
      "selector",
      "combinator",
      "simpleSelectorSequence",
      "typeSelector",
      "typeNamespacePrefix",
      "elementName",
      "universal",
      "className",
      "attrib",
      "pseudo",
      "functionalPseudo",
      "expression",
      "negation",
      "negationArg",
      "operator",
      "property",
      "ruleset",
      "declarationList",
      "declaration",
      "prio",
      "value",
      "expr",
      "term",
      "function",
      "dxImageTransform",
      "hexcolor",
      "number",
      "percentage",
      "dimension",
      "unknownDimension",
      "any",
      "atRule",
      "atKeyword",
      "unused",
      "block",
      "nestedStatement",
      "groupRuleBody",
      "supportsRule",
      "supportsCondition",
      "supportsConditionInParens",
      "supportsNegation",
      "supportsConjunction",
      "supportsDisjunction",
      "supportsDeclarationCondition",
      "generalEnclosed",
      "var",
      "calc",
      "calcSum",
      "calcProduct",
      "calcValue",
      "fontFaceRule",
      "fontFaceDeclaration",
      "keyframesRule",
      "keyframesBlocks",
      "keyframeSelector",
      "viewport",
      "counterStyle",
      "fontFeatureValuesRule",
      "fontFamilyNameList",
      "fontFamilyName",
      "featureValueBlock",
      "featureType",
      "featureValueDefinition",
      "ident",
      "ws"
    };
  }

  public static final String[] ruleNames = makeRuleNames();

  private static String[] makeLiteralNames() {
    return new String[] {
      null,
      "';'",
      "'('",
      "':'",
      "')'",
      "'{'",
      "'}'",
      "'*'",
      "'|'",
      "'.'",
      "'['",
      "'='",
      "']'",
      "'/'",
      "'_'",
      "'@'",
      null,
      null,
      "'<!--'",
      "'-->'",
      "'~='",
      "'|='",
      null,
      null,
      null,
      null,
      null,
      "'@charset '",
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "'+'",
      "'-'",
      "'>'",
      "','",
      "'~'",
      null,
      null,
      null,
      "'^='",
      "'$='",
      "'*='",
      null,
      null,
      null,
      null,
      null,
      null,
      "'calc('",
      null,
      null,
      null,
      null,
      null,
      "'var('"
    };
  }

  private static final String[] _LITERAL_NAMES = makeLiteralNames();

  private static String[] makeSymbolicNames() {
    return new String[] {
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      "Comment",
      "Space",
      "Cdo",
      "Cdc",
      "Includes",
      "DashMatch",
      "Hash",
      "Import",
      "Page",
      "Media",
      "Namespace",
      "Charset",
      "Important",
      "Percentage",
      "Uri",
      "UnicodeRange",
      "MediaOnly",
      "Not",
      "And",
      "Dimension",
      "UnknownDimension",
      "Plus",
      "Minus",
      "Greater",
      "Comma",
      "Tilde",
      "PseudoNot",
      "Number",
      "String",
      "PrefixMatch",
      "SuffixMatch",
      "SubstringMatch",
      "FontFace",
      "Supports",
      "Or",
      "Keyframes",
      "From",
      "To",
      "Calc",
      "Viewport",
      "CounterStyle",
      "FontFeatureValues",
      "DxImageTransform",
      "Variable",
      "Var",
      "Ident",
      "Function"
    };
  }

  private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
  public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

  /**
   * @deprecated Use {@link #VOCABULARY} instead.
   */
  @Deprecated public static final String[] tokenNames;

  static {
    tokenNames = new String[_SYMBOLIC_NAMES.length];
    for (int i = 0; i < tokenNames.length; i++) {
      tokenNames[i] = VOCABULARY.getLiteralName(i);
      if (tokenNames[i] == null) {
        tokenNames[i] = VOCABULARY.getSymbolicName(i);
      }

      if (tokenNames[i] == null) {
        tokenNames[i] = "<INVALID>";
      }
    }
  }

  @Override
  @Deprecated
  public String[] getTokenNames() {
    return tokenNames;
  }

  @Override
  public Vocabulary getVocabulary() {
    return VOCABULARY;
  }

  @Override
  public String getGrammarFileName() {
    return "CSS3.g4";
  }

  @Override
  public String[] getRuleNames() {
    return ruleNames;
  }

  @Override
  public String getSerializedATN() {
    return _serializedATN;
  }

  @Override
  public ATN getATN() {
    return _ATN;
  }

  public CSS3Parser(TokenStream input) {
    super(input);
    _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
  }

  public static class StylesheetContext extends ParserRuleContext {
    public List<NamespaceContext> namespace() {
      return getRuleContexts(NamespaceContext.class);
    }

    public NamespaceContext namespace(int i) {
      return getRuleContext(NamespaceContext.class, i);
    }

    public List<NestedStatementContext> nestedStatement() {
      return getRuleContexts(NestedStatementContext.class);
    }

    public NestedStatementContext nestedStatement(int i) {
      return getRuleContext(NestedStatementContext.class, i);
    }

    public List<TerminalNode> Comment() {
      return getTokens(CSS3Parser.Comment);
    }

    public TerminalNode Comment(int i) {
      return getToken(CSS3Parser.Comment, i);
    }

    public List<TerminalNode> Space() {
      return getTokens(CSS3Parser.Space);
    }

    public TerminalNode Space(int i) {
      return getToken(CSS3Parser.Space, i);
    }

    public List<TerminalNode> Cdo() {
      return getTokens(CSS3Parser.Cdo);
    }

    public TerminalNode Cdo(int i) {
      return getToken(CSS3Parser.Cdo, i);
    }

    public List<TerminalNode> Cdc() {
      return getTokens(CSS3Parser.Cdc);
    }

    public TerminalNode Cdc(int i) {
      return getToken(CSS3Parser.Cdc, i);
    }

    public StylesheetContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_stylesheet;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterStylesheet(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitStylesheet(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitStylesheet(this);
      else return visitor.visitChildren(this);
    }
  }

  public final StylesheetContext stylesheet() throws RecognitionException {
    StylesheetContext _localctx = new StylesheetContext(_ctx, getState());
    enterRule(_localctx, 0, RULE_stylesheet);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(167);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == Namespace) {
          {
            {
              setState(158);
              namespace();
              setState(162);
              _errHandler.sync(this);
              _la = _input.LA(1);
              while ((((_la) & ~0x3f) == 0
                  && ((1L << _la) & ((1L << Comment) | (1L << Space) | (1L << Cdo) | (1L << Cdc)))
                      != 0)) {
                {
                  {
                    setState(159);
                    _la = _input.LA(1);
                    if (!((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                & ((1L << Comment) | (1L << Space) | (1L << Cdo) | (1L << Cdc)))
                            != 0))) {
                      _errHandler.recoverInline(this);
                    } else {
                      if (_input.LA(1) == Token.EOF) matchedEOF = true;
                      _errHandler.reportMatch(this);
                      consume();
                    }
                  }
                }
                setState(164);
                _errHandler.sync(this);
                _la = _input.LA(1);
              }
            }
          }
          setState(169);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(179);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__1)
                        | (1L << T__2)
                        | (1L << T__4)
                        | (1L << T__6)
                        | (1L << T__7)
                        | (1L << T__8)
                        | (1L << T__9)
                        | (1L << T__14)
                        | (1L << Includes)
                        | (1L << DashMatch)
                        | (1L << Hash)
                        | (1L << Page)
                        | (1L << Media)
                        | (1L << Percentage)
                        | (1L << Uri)
                        | (1L << UnicodeRange)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Dimension)
                        | (1L << UnknownDimension)
                        | (1L << Plus)
                        | (1L << Minus)
                        | (1L << PseudoNot)
                        | (1L << Number)
                        | (1L << String)
                        | (1L << FontFace)
                        | (1L << Supports)
                        | (1L << Or)
                        | (1L << Keyframes)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Viewport)
                        | (1L << CounterStyle)
                        | (1L << FontFeatureValues)
                        | (1L << Ident)
                        | (1L << Function)))
                != 0)) {
          {
            {
              setState(170);
              nestedStatement();
              setState(174);
              _errHandler.sync(this);
              _la = _input.LA(1);
              while ((((_la) & ~0x3f) == 0
                  && ((1L << _la) & ((1L << Comment) | (1L << Space) | (1L << Cdo) | (1L << Cdc)))
                      != 0)) {
                {
                  {
                    setState(171);
                    _la = _input.LA(1);
                    if (!((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                & ((1L << Comment) | (1L << Space) | (1L << Cdo) | (1L << Cdc)))
                            != 0))) {
                      _errHandler.recoverInline(this);
                    } else {
                      if (_input.LA(1) == Token.EOF) matchedEOF = true;
                      _errHandler.reportMatch(this);
                      consume();
                    }
                  }
                }
                setState(176);
                _errHandler.sync(this);
                _la = _input.LA(1);
              }
            }
          }
          setState(181);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CharsetContext extends ParserRuleContext {
    public CharsetContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_charset;
    }

    public CharsetContext() {}

    public void copyFrom(CharsetContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class BadCharsetContext extends CharsetContext {
    public TerminalNode Charset() {
      return getToken(CSS3Parser.Charset, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public BadCharsetContext(CharsetContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBadCharset(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBadCharset(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBadCharset(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class GoodCharsetContext extends CharsetContext {
    public TerminalNode Charset() {
      return getToken(CSS3Parser.Charset, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public GoodCharsetContext(CharsetContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGoodCharset(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGoodCharset(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGoodCharset(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CharsetContext charset() throws RecognitionException {
    CharsetContext _localctx = new CharsetContext(_ctx, getState());
    enterRule(_localctx, 2, RULE_charset);
    try {
      setState(194);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
        case 1:
          _localctx = new GoodCharsetContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(182);
            match(Charset);
            setState(183);
            ws();
            setState(184);
            match(String);
            setState(185);
            ws();
            setState(186);
            match(T__0);
            setState(187);
            ws();
          }
          break;
        case 2:
          _localctx = new BadCharsetContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(189);
            match(Charset);
            setState(190);
            ws();
            setState(191);
            match(String);
            setState(192);
            ws();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ImportsContext extends ParserRuleContext {
    public ImportsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_imports;
    }

    public ImportsContext() {}

    public void copyFrom(ImportsContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class BadImportContext extends ImportsContext {
    public TerminalNode Import() {
      return getToken(CSS3Parser.Import, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public MediaQueryListContext mediaQueryList() {
      return getRuleContext(MediaQueryListContext.class, 0);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public TerminalNode Uri() {
      return getToken(CSS3Parser.Uri, 0);
    }

    public BadImportContext(ImportsContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBadImport(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBadImport(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBadImport(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class GoodImportContext extends ImportsContext {
    public TerminalNode Import() {
      return getToken(CSS3Parser.Import, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public MediaQueryListContext mediaQueryList() {
      return getRuleContext(MediaQueryListContext.class, 0);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public TerminalNode Uri() {
      return getToken(CSS3Parser.Uri, 0);
    }

    public GoodImportContext(ImportsContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGoodImport(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGoodImport(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGoodImport(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ImportsContext imports() throws RecognitionException {
    ImportsContext _localctx = new ImportsContext(_ctx, getState());
    enterRule(_localctx, 4, RULE_imports);
    int _la;
    try {
      setState(222);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 5, _ctx)) {
        case 1:
          _localctx = new GoodImportContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(196);
            match(Import);
            setState(197);
            ws();
            setState(198);
            _la = _input.LA(1);
            if (!(_la == Uri || _la == String)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(199);
            ws();
            setState(200);
            mediaQueryList();
            setState(201);
            match(T__0);
            setState(202);
            ws();
          }
          break;
        case 2:
          _localctx = new GoodImportContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(204);
            match(Import);
            setState(205);
            ws();
            setState(206);
            _la = _input.LA(1);
            if (!(_la == Uri || _la == String)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(207);
            ws();
            setState(208);
            match(T__0);
            setState(209);
            ws();
          }
          break;
        case 3:
          _localctx = new BadImportContext(_localctx);
          enterOuterAlt(_localctx, 3);
          {
            setState(211);
            match(Import);
            setState(212);
            ws();
            setState(213);
            _la = _input.LA(1);
            if (!(_la == Uri || _la == String)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(214);
            ws();
            setState(215);
            mediaQueryList();
          }
          break;
        case 4:
          _localctx = new BadImportContext(_localctx);
          enterOuterAlt(_localctx, 4);
          {
            setState(217);
            match(Import);
            setState(218);
            ws();
            setState(219);
            _la = _input.LA(1);
            if (!(_la == Uri || _la == String)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(220);
            ws();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NamespaceContext extends ParserRuleContext {
    public NamespaceContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_namespace;
    }

    public NamespaceContext() {}

    public void copyFrom(NamespaceContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class GoodNamespaceContext extends NamespaceContext {
    public TerminalNode Namespace() {
      return getToken(CSS3Parser.Namespace, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public TerminalNode Uri() {
      return getToken(CSS3Parser.Uri, 0);
    }

    public NamespacePrefixContext namespacePrefix() {
      return getRuleContext(NamespacePrefixContext.class, 0);
    }

    public GoodNamespaceContext(NamespaceContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGoodNamespace(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGoodNamespace(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGoodNamespace(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class BadNamespaceContext extends NamespaceContext {
    public TerminalNode Namespace() {
      return getToken(CSS3Parser.Namespace, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public TerminalNode Uri() {
      return getToken(CSS3Parser.Uri, 0);
    }

    public NamespacePrefixContext namespacePrefix() {
      return getRuleContext(NamespacePrefixContext.class, 0);
    }

    public BadNamespaceContext(NamespaceContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBadNamespace(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBadNamespace(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBadNamespace(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NamespaceContext namespace() throws RecognitionException {
    NamespaceContext _localctx = new NamespaceContext(_ctx, getState());
    enterRule(_localctx, 6, RULE_namespace);
    int _la;
    try {
      setState(246);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
        case 1:
          _localctx = new GoodNamespaceContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(224);
            match(Namespace);
            setState(225);
            ws();
            setState(229);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Ident)))
                    != 0)) {
              {
                setState(226);
                namespacePrefix();
                setState(227);
                ws();
              }
            }

            setState(231);
            _la = _input.LA(1);
            if (!(_la == Uri || _la == String)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(232);
            ws();
            setState(233);
            match(T__0);
            setState(234);
            ws();
          }
          break;
        case 2:
          _localctx = new BadNamespaceContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(236);
            match(Namespace);
            setState(237);
            ws();
            setState(241);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Ident)))
                    != 0)) {
              {
                setState(238);
                namespacePrefix();
                setState(239);
                ws();
              }
            }

            setState(243);
            _la = _input.LA(1);
            if (!(_la == Uri || _la == String)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(244);
            ws();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NamespacePrefixContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public NamespacePrefixContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_namespacePrefix;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterNamespacePrefix(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitNamespacePrefix(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitNamespacePrefix(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NamespacePrefixContext namespacePrefix() throws RecognitionException {
    NamespacePrefixContext _localctx = new NamespacePrefixContext(_ctx, getState());
    enterRule(_localctx, 8, RULE_namespacePrefix);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(248);
        ident();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class MediaContext extends ParserRuleContext {
    public TerminalNode Media() {
      return getToken(CSS3Parser.Media, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public MediaQueryListContext mediaQueryList() {
      return getRuleContext(MediaQueryListContext.class, 0);
    }

    public GroupRuleBodyContext groupRuleBody() {
      return getRuleContext(GroupRuleBodyContext.class, 0);
    }

    public MediaContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_media;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterMedia(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitMedia(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitMedia(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MediaContext media() throws RecognitionException {
    MediaContext _localctx = new MediaContext(_ctx, getState());
    enterRule(_localctx, 10, RULE_media);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(250);
        match(Media);
        setState(251);
        ws();
        setState(252);
        mediaQueryList();
        setState(253);
        groupRuleBody();
        setState(254);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class MediaQueryListContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<MediaQueryContext> mediaQuery() {
      return getRuleContexts(MediaQueryContext.class);
    }

    public MediaQueryContext mediaQuery(int i) {
      return getRuleContext(MediaQueryContext.class, i);
    }

    public List<TerminalNode> Comma() {
      return getTokens(CSS3Parser.Comma);
    }

    public TerminalNode Comma(int i) {
      return getToken(CSS3Parser.Comma, i);
    }

    public MediaQueryListContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_mediaQueryList;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterMediaQueryList(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitMediaQueryList(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitMediaQueryList(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MediaQueryListContext mediaQueryList() throws RecognitionException {
    MediaQueryListContext _localctx = new MediaQueryListContext(_ctx, getState());
    enterRule(_localctx, 12, RULE_mediaQueryList);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(266);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
          case 1:
            {
              setState(256);
              mediaQuery();
              setState(263);
              _errHandler.sync(this);
              _la = _input.LA(1);
              while (_la == Comma) {
                {
                  {
                    setState(257);
                    match(Comma);
                    setState(258);
                    ws();
                    setState(259);
                    mediaQuery();
                  }
                }
                setState(265);
                _errHandler.sync(this);
                _la = _input.LA(1);
              }
            }
            break;
        }
        setState(268);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class MediaQueryContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public MediaTypeContext mediaType() {
      return getRuleContext(MediaTypeContext.class, 0);
    }

    public List<TerminalNode> And() {
      return getTokens(CSS3Parser.And);
    }

    public TerminalNode And(int i) {
      return getToken(CSS3Parser.And, i);
    }

    public List<MediaExpressionContext> mediaExpression() {
      return getRuleContexts(MediaExpressionContext.class);
    }

    public MediaExpressionContext mediaExpression(int i) {
      return getRuleContext(MediaExpressionContext.class, i);
    }

    public TerminalNode MediaOnly() {
      return getToken(CSS3Parser.MediaOnly, 0);
    }

    public TerminalNode Not() {
      return getToken(CSS3Parser.Not, 0);
    }

    public MediaQueryContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_mediaQuery;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterMediaQuery(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitMediaQuery(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitMediaQuery(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MediaQueryContext mediaQuery() throws RecognitionException {
    MediaQueryContext _localctx = new MediaQueryContext(_ctx, getState());
    enterRule(_localctx, 14, RULE_mediaQuery);
    int _la;
    try {
      setState(295);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case Comment:
        case Space:
        case MediaOnly:
        case Not:
        case And:
        case Or:
        case From:
        case To:
        case Ident:
          enterOuterAlt(_localctx, 1);
          {
            setState(271);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
              case 1:
                {
                  setState(270);
                  _la = _input.LA(1);
                  if (!(_la == MediaOnly || _la == Not)) {
                    _errHandler.recoverInline(this);
                  } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                  }
                }
                break;
            }
            setState(273);
            ws();
            setState(274);
            mediaType();
            setState(275);
            ws();
            setState(282);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while (_la == And) {
              {
                {
                  setState(276);
                  match(And);
                  setState(277);
                  ws();
                  setState(278);
                  mediaExpression();
                }
              }
              setState(284);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
          break;
        case T__1:
          enterOuterAlt(_localctx, 2);
          {
            setState(285);
            mediaExpression();
            setState(292);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while (_la == And) {
              {
                {
                  setState(286);
                  match(And);
                  setState(287);
                  ws();
                  setState(288);
                  mediaExpression();
                }
              }
              setState(294);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class MediaTypeContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public MediaTypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_mediaType;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterMediaType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitMediaType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitMediaType(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MediaTypeContext mediaType() throws RecognitionException {
    MediaTypeContext _localctx = new MediaTypeContext(_ctx, getState());
    enterRule(_localctx, 16, RULE_mediaType);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(297);
        ident();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class MediaExpressionContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public MediaFeatureContext mediaFeature() {
      return getRuleContext(MediaFeatureContext.class, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public MediaExpressionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_mediaExpression;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterMediaExpression(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitMediaExpression(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitMediaExpression(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MediaExpressionContext mediaExpression() throws RecognitionException {
    MediaExpressionContext _localctx = new MediaExpressionContext(_ctx, getState());
    enterRule(_localctx, 18, RULE_mediaExpression);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(299);
        match(T__1);
        setState(300);
        ws();
        setState(301);
        mediaFeature();
        setState(306);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == T__2) {
          {
            setState(302);
            match(T__2);
            setState(303);
            ws();
            setState(304);
            expr();
          }
        }

        setState(308);
        match(T__3);
        setState(309);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class MediaFeatureContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public MediaFeatureContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_mediaFeature;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterMediaFeature(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitMediaFeature(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitMediaFeature(this);
      else return visitor.visitChildren(this);
    }
  }

  public final MediaFeatureContext mediaFeature() throws RecognitionException {
    MediaFeatureContext _localctx = new MediaFeatureContext(_ctx, getState());
    enterRule(_localctx, 20, RULE_mediaFeature);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(311);
        ident();
        setState(312);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PageContext extends ParserRuleContext {
    public TerminalNode Page() {
      return getToken(CSS3Parser.Page, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public PseudoPageContext pseudoPage() {
      return getRuleContext(PseudoPageContext.class, 0);
    }

    public List<DeclarationContext> declaration() {
      return getRuleContexts(DeclarationContext.class);
    }

    public DeclarationContext declaration(int i) {
      return getRuleContext(DeclarationContext.class, i);
    }

    public PageContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_page;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterPage(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitPage(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitPage(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PageContext page() throws RecognitionException {
    PageContext _localctx = new PageContext(_ctx, getState());
    enterRule(_localctx, 22, RULE_page);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(314);
        match(Page);
        setState(315);
        ws();
        setState(317);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == T__2) {
          {
            setState(316);
            pseudoPage();
          }
        }

        setState(319);
        match(T__4);
        setState(320);
        ws();
        setState(322);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__6)
                        | (1L << T__13)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Variable)
                        | (1L << Ident)))
                != 0)) {
          {
            setState(321);
            declaration();
          }
        }

        setState(331);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__0) {
          {
            {
              setState(324);
              match(T__0);
              setState(325);
              ws();
              setState(327);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if ((((_la) & ~0x3f) == 0
                  && ((1L << _la)
                          & ((1L << T__6)
                              | (1L << T__13)
                              | (1L << MediaOnly)
                              | (1L << Not)
                              | (1L << And)
                              | (1L << Or)
                              | (1L << From)
                              | (1L << To)
                              | (1L << Variable)
                              | (1L << Ident)))
                      != 0)) {
                {
                  setState(326);
                  declaration();
                }
              }
            }
          }
          setState(333);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(334);
        match(T__5);
        setState(335);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PseudoPageContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public PseudoPageContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_pseudoPage;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterPseudoPage(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitPseudoPage(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitPseudoPage(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PseudoPageContext pseudoPage() throws RecognitionException {
    PseudoPageContext _localctx = new PseudoPageContext(_ctx, getState());
    enterRule(_localctx, 24, RULE_pseudoPage);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(337);
        match(T__2);
        setState(338);
        ident();
        setState(339);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SelectorGroupContext extends ParserRuleContext {
    public List<SelectorContext> selector() {
      return getRuleContexts(SelectorContext.class);
    }

    public SelectorContext selector(int i) {
      return getRuleContext(SelectorContext.class, i);
    }

    public List<TerminalNode> Comma() {
      return getTokens(CSS3Parser.Comma);
    }

    public TerminalNode Comma(int i) {
      return getToken(CSS3Parser.Comma, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public SelectorGroupContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_selectorGroup;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterSelectorGroup(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSelectorGroup(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSelectorGroup(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SelectorGroupContext selectorGroup() throws RecognitionException {
    SelectorGroupContext _localctx = new SelectorGroupContext(_ctx, getState());
    enterRule(_localctx, 26, RULE_selectorGroup);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(341);
        selector();
        setState(348);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == Comma) {
          {
            {
              setState(342);
              match(Comma);
              setState(343);
              ws();
              setState(344);
              selector();
            }
          }
          setState(350);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SelectorContext extends ParserRuleContext {
    public List<SimpleSelectorSequenceContext> simpleSelectorSequence() {
      return getRuleContexts(SimpleSelectorSequenceContext.class);
    }

    public SimpleSelectorSequenceContext simpleSelectorSequence(int i) {
      return getRuleContext(SimpleSelectorSequenceContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<CombinatorContext> combinator() {
      return getRuleContexts(CombinatorContext.class);
    }

    public CombinatorContext combinator(int i) {
      return getRuleContext(CombinatorContext.class, i);
    }

    public SelectorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_selector;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterSelector(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSelector(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSelector(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SelectorContext selector() throws RecognitionException {
    SelectorContext _localctx = new SelectorContext(_ctx, getState());
    enterRule(_localctx, 28, RULE_selector);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(351);
        simpleSelectorSequence();
        setState(352);
        ws();
        setState(359);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << Space) | (1L << Plus) | (1L << Greater) | (1L << Tilde)))
                != 0)) {
          {
            {
              setState(353);
              combinator();
              setState(354);
              simpleSelectorSequence();
              setState(355);
              ws();
            }
          }
          setState(361);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CombinatorContext extends ParserRuleContext {
    public TerminalNode Plus() {
      return getToken(CSS3Parser.Plus, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public TerminalNode Greater() {
      return getToken(CSS3Parser.Greater, 0);
    }

    public TerminalNode Tilde() {
      return getToken(CSS3Parser.Tilde, 0);
    }

    public TerminalNode Space() {
      return getToken(CSS3Parser.Space, 0);
    }

    public CombinatorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_combinator;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterCombinator(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitCombinator(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitCombinator(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CombinatorContext combinator() throws RecognitionException {
    CombinatorContext _localctx = new CombinatorContext(_ctx, getState());
    enterRule(_localctx, 30, RULE_combinator);
    try {
      setState(370);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case Plus:
          enterOuterAlt(_localctx, 1);
          {
            setState(362);
            match(Plus);
            setState(363);
            ws();
          }
          break;
        case Greater:
          enterOuterAlt(_localctx, 2);
          {
            setState(364);
            match(Greater);
            setState(365);
            ws();
          }
          break;
        case Tilde:
          enterOuterAlt(_localctx, 3);
          {
            setState(366);
            match(Tilde);
            setState(367);
            ws();
          }
          break;
        case Space:
          enterOuterAlt(_localctx, 4);
          {
            setState(368);
            match(Space);
            setState(369);
            ws();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SimpleSelectorSequenceContext extends ParserRuleContext {
    public TypeSelectorContext typeSelector() {
      return getRuleContext(TypeSelectorContext.class, 0);
    }

    public UniversalContext universal() {
      return getRuleContext(UniversalContext.class, 0);
    }

    public List<TerminalNode> Hash() {
      return getTokens(CSS3Parser.Hash);
    }

    public TerminalNode Hash(int i) {
      return getToken(CSS3Parser.Hash, i);
    }

    public List<ClassNameContext> className() {
      return getRuleContexts(ClassNameContext.class);
    }

    public ClassNameContext className(int i) {
      return getRuleContext(ClassNameContext.class, i);
    }

    public List<AttribContext> attrib() {
      return getRuleContexts(AttribContext.class);
    }

    public AttribContext attrib(int i) {
      return getRuleContext(AttribContext.class, i);
    }

    public List<PseudoContext> pseudo() {
      return getRuleContexts(PseudoContext.class);
    }

    public PseudoContext pseudo(int i) {
      return getRuleContext(PseudoContext.class, i);
    }

    public List<NegationContext> negation() {
      return getRuleContexts(NegationContext.class);
    }

    public NegationContext negation(int i) {
      return getRuleContext(NegationContext.class, i);
    }

    public SimpleSelectorSequenceContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_simpleSelectorSequence;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterSimpleSelectorSequence(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitSimpleSelectorSequence(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSimpleSelectorSequence(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SimpleSelectorSequenceContext simpleSelectorSequence() throws RecognitionException {
    SimpleSelectorSequenceContext _localctx = new SimpleSelectorSequenceContext(_ctx, getState());
    enterRule(_localctx, 32, RULE_simpleSelectorSequence);
    int _la;
    try {
      setState(395);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case T__6:
        case T__7:
        case MediaOnly:
        case Not:
        case And:
        case Or:
        case From:
        case To:
        case Ident:
          enterOuterAlt(_localctx, 1);
          {
            setState(374);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 23, _ctx)) {
              case 1:
                {
                  setState(372);
                  typeSelector();
                }
                break;
              case 2:
                {
                  setState(373);
                  universal();
                }
                break;
            }
            setState(383);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__2)
                            | (1L << T__8)
                            | (1L << T__9)
                            | (1L << Hash)
                            | (1L << PseudoNot)))
                    != 0)) {
              {
                setState(381);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                  case Hash:
                    {
                      setState(376);
                      match(Hash);
                    }
                    break;
                  case T__8:
                    {
                      setState(377);
                      className();
                    }
                    break;
                  case T__9:
                    {
                      setState(378);
                      attrib();
                    }
                    break;
                  case T__2:
                    {
                      setState(379);
                      pseudo();
                    }
                    break;
                  case PseudoNot:
                    {
                      setState(380);
                      negation();
                    }
                    break;
                  default:
                    throw new NoViableAltException(this);
                }
              }
              setState(385);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
          }
          break;
        case T__2:
        case T__8:
        case T__9:
        case Hash:
        case PseudoNot:
          enterOuterAlt(_localctx, 2);
          {
            setState(391);
            _errHandler.sync(this);
            _la = _input.LA(1);
            do {
              {
                setState(391);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                  case Hash:
                    {
                      setState(386);
                      match(Hash);
                    }
                    break;
                  case T__8:
                    {
                      setState(387);
                      className();
                    }
                    break;
                  case T__9:
                    {
                      setState(388);
                      attrib();
                    }
                    break;
                  case T__2:
                    {
                      setState(389);
                      pseudo();
                    }
                    break;
                  case PseudoNot:
                    {
                      setState(390);
                      negation();
                    }
                    break;
                  default:
                    throw new NoViableAltException(this);
                }
              }
              setState(393);
              _errHandler.sync(this);
              _la = _input.LA(1);
            } while ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__2)
                            | (1L << T__8)
                            | (1L << T__9)
                            | (1L << Hash)
                            | (1L << PseudoNot)))
                    != 0));
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class TypeSelectorContext extends ParserRuleContext {
    public ElementNameContext elementName() {
      return getRuleContext(ElementNameContext.class, 0);
    }

    public TypeNamespacePrefixContext typeNamespacePrefix() {
      return getRuleContext(TypeNamespacePrefixContext.class, 0);
    }

    public TypeSelectorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_typeSelector;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterTypeSelector(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitTypeSelector(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitTypeSelector(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TypeSelectorContext typeSelector() throws RecognitionException {
    TypeSelectorContext _localctx = new TypeSelectorContext(_ctx, getState());
    enterRule(_localctx, 34, RULE_typeSelector);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(398);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 29, _ctx)) {
          case 1:
            {
              setState(397);
              typeNamespacePrefix();
            }
            break;
        }
        setState(400);
        elementName();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class TypeNamespacePrefixContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public TypeNamespacePrefixContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_typeNamespacePrefix;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterTypeNamespacePrefix(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitTypeNamespacePrefix(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitTypeNamespacePrefix(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TypeNamespacePrefixContext typeNamespacePrefix() throws RecognitionException {
    TypeNamespacePrefixContext _localctx = new TypeNamespacePrefixContext(_ctx, getState());
    enterRule(_localctx, 36, RULE_typeNamespacePrefix);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(404);
        _errHandler.sync(this);
        switch (_input.LA(1)) {
          case MediaOnly:
          case Not:
          case And:
          case Or:
          case From:
          case To:
          case Ident:
            {
              setState(402);
              ident();
            }
            break;
          case T__6:
            {
              setState(403);
              match(T__6);
            }
            break;
          case T__7:
            break;
          default:
            break;
        }
        setState(406);
        match(T__7);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ElementNameContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public ElementNameContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_elementName;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterElementName(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitElementName(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitElementName(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ElementNameContext elementName() throws RecognitionException {
    ElementNameContext _localctx = new ElementNameContext(_ctx, getState());
    enterRule(_localctx, 38, RULE_elementName);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(408);
        ident();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class UniversalContext extends ParserRuleContext {
    public TypeNamespacePrefixContext typeNamespacePrefix() {
      return getRuleContext(TypeNamespacePrefixContext.class, 0);
    }

    public UniversalContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_universal;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUniversal(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUniversal(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUniversal(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UniversalContext universal() throws RecognitionException {
    UniversalContext _localctx = new UniversalContext(_ctx, getState());
    enterRule(_localctx, 40, RULE_universal);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(411);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 31, _ctx)) {
          case 1:
            {
              setState(410);
              typeNamespacePrefix();
            }
            break;
        }
        setState(413);
        match(T__6);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ClassNameContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public ClassNameContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_className;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterClassName(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitClassName(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitClassName(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ClassNameContext className() throws RecognitionException {
    ClassNameContext _localctx = new ClassNameContext(_ctx, getState());
    enterRule(_localctx, 42, RULE_className);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(415);
        match(T__8);
        setState(416);
        ident();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class AttribContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<IdentContext> ident() {
      return getRuleContexts(IdentContext.class);
    }

    public IdentContext ident(int i) {
      return getRuleContext(IdentContext.class, i);
    }

    public TypeNamespacePrefixContext typeNamespacePrefix() {
      return getRuleContext(TypeNamespacePrefixContext.class, 0);
    }

    public TerminalNode PrefixMatch() {
      return getToken(CSS3Parser.PrefixMatch, 0);
    }

    public TerminalNode SuffixMatch() {
      return getToken(CSS3Parser.SuffixMatch, 0);
    }

    public TerminalNode SubstringMatch() {
      return getToken(CSS3Parser.SubstringMatch, 0);
    }

    public TerminalNode Includes() {
      return getToken(CSS3Parser.Includes, 0);
    }

    public TerminalNode DashMatch() {
      return getToken(CSS3Parser.DashMatch, 0);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public AttribContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_attrib;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterAttrib(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitAttrib(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitAttrib(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AttribContext attrib() throws RecognitionException {
    AttribContext _localctx = new AttribContext(_ctx, getState());
    enterRule(_localctx, 44, RULE_attrib);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(418);
        match(T__9);
        setState(419);
        ws();
        setState(421);
        _errHandler.sync(this);
        switch (getInterpreter().adaptivePredict(_input, 32, _ctx)) {
          case 1:
            {
              setState(420);
              typeNamespacePrefix();
            }
            break;
        }
        setState(423);
        ident();
        setState(424);
        ws();
        setState(433);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__10)
                        | (1L << Includes)
                        | (1L << DashMatch)
                        | (1L << PrefixMatch)
                        | (1L << SuffixMatch)
                        | (1L << SubstringMatch)))
                != 0)) {
          {
            setState(425);
            _la = _input.LA(1);
            if (!((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__10)
                            | (1L << Includes)
                            | (1L << DashMatch)
                            | (1L << PrefixMatch)
                            | (1L << SuffixMatch)
                            | (1L << SubstringMatch)))
                    != 0))) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
            setState(426);
            ws();
            setState(429);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
              case MediaOnly:
              case Not:
              case And:
              case Or:
              case From:
              case To:
              case Ident:
                {
                  setState(427);
                  ident();
                }
                break;
              case String:
                {
                  setState(428);
                  match(String);
                }
                break;
              default:
                throw new NoViableAltException(this);
            }
            setState(431);
            ws();
          }
        }

        setState(435);
        match(T__11);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PseudoContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public FunctionalPseudoContext functionalPseudo() {
      return getRuleContext(FunctionalPseudoContext.class, 0);
    }

    public PseudoContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_pseudo;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterPseudo(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitPseudo(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitPseudo(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PseudoContext pseudo() throws RecognitionException {
    PseudoContext _localctx = new PseudoContext(_ctx, getState());
    enterRule(_localctx, 46, RULE_pseudo);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(437);
        match(T__2);
        setState(439);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == T__2) {
          {
            setState(438);
            match(T__2);
          }
        }

        setState(443);
        _errHandler.sync(this);
        switch (_input.LA(1)) {
          case MediaOnly:
          case Not:
          case And:
          case Or:
          case From:
          case To:
          case Ident:
            {
              setState(441);
              ident();
            }
            break;
          case Function:
            {
              setState(442);
              functionalPseudo();
            }
            break;
          default:
            throw new NoViableAltException(this);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FunctionalPseudoContext extends ParserRuleContext {
    public TerminalNode Function() {
      return getToken(CSS3Parser.Function, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public ExpressionContext expression() {
      return getRuleContext(ExpressionContext.class, 0);
    }

    public FunctionalPseudoContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_functionalPseudo;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFunctionalPseudo(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFunctionalPseudo(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFunctionalPseudo(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FunctionalPseudoContext functionalPseudo() throws RecognitionException {
    FunctionalPseudoContext _localctx = new FunctionalPseudoContext(_ctx, getState());
    enterRule(_localctx, 48, RULE_functionalPseudo);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(445);
        match(Function);
        setState(446);
        ws();
        setState(447);
        expression();
        setState(448);
        match(T__3);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ExpressionContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<TerminalNode> Plus() {
      return getTokens(CSS3Parser.Plus);
    }

    public TerminalNode Plus(int i) {
      return getToken(CSS3Parser.Plus, i);
    }

    public List<TerminalNode> Minus() {
      return getTokens(CSS3Parser.Minus);
    }

    public TerminalNode Minus(int i) {
      return getToken(CSS3Parser.Minus, i);
    }

    public List<TerminalNode> Dimension() {
      return getTokens(CSS3Parser.Dimension);
    }

    public TerminalNode Dimension(int i) {
      return getToken(CSS3Parser.Dimension, i);
    }

    public List<TerminalNode> UnknownDimension() {
      return getTokens(CSS3Parser.UnknownDimension);
    }

    public TerminalNode UnknownDimension(int i) {
      return getToken(CSS3Parser.UnknownDimension, i);
    }

    public List<TerminalNode> Number() {
      return getTokens(CSS3Parser.Number);
    }

    public TerminalNode Number(int i) {
      return getToken(CSS3Parser.Number, i);
    }

    public List<TerminalNode> String() {
      return getTokens(CSS3Parser.String);
    }

    public TerminalNode String(int i) {
      return getToken(CSS3Parser.String, i);
    }

    public List<IdentContext> ident() {
      return getRuleContexts(IdentContext.class);
    }

    public IdentContext ident(int i) {
      return getRuleContext(IdentContext.class, i);
    }

    public ExpressionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_expression;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterExpression(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitExpression(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitExpression(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExpressionContext expression() throws RecognitionException {
    ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
    enterRule(_localctx, 50, RULE_expression);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(460);
        _errHandler.sync(this);
        _la = _input.LA(1);
        do {
          {
            {
              setState(457);
              _errHandler.sync(this);
              switch (_input.LA(1)) {
                case Plus:
                  {
                    setState(450);
                    match(Plus);
                  }
                  break;
                case Minus:
                  {
                    setState(451);
                    match(Minus);
                  }
                  break;
                case Dimension:
                  {
                    setState(452);
                    match(Dimension);
                  }
                  break;
                case UnknownDimension:
                  {
                    setState(453);
                    match(UnknownDimension);
                  }
                  break;
                case Number:
                  {
                    setState(454);
                    match(Number);
                  }
                  break;
                case String:
                  {
                    setState(455);
                    match(String);
                  }
                  break;
                case MediaOnly:
                case Not:
                case And:
                case Or:
                case From:
                case To:
                case Ident:
                  {
                    setState(456);
                    ident();
                  }
                  break;
                default:
                  throw new NoViableAltException(this);
              }
              setState(459);
              ws();
            }
          }
          setState(462);
          _errHandler.sync(this);
          _la = _input.LA(1);
        } while ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Dimension)
                        | (1L << UnknownDimension)
                        | (1L << Plus)
                        | (1L << Minus)
                        | (1L << Number)
                        | (1L << String)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Ident)))
                != 0));
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NegationContext extends ParserRuleContext {
    public TerminalNode PseudoNot() {
      return getToken(CSS3Parser.PseudoNot, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public NegationArgContext negationArg() {
      return getRuleContext(NegationArgContext.class, 0);
    }

    public NegationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_negation;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterNegation(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitNegation(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitNegation(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NegationContext negation() throws RecognitionException {
    NegationContext _localctx = new NegationContext(_ctx, getState());
    enterRule(_localctx, 52, RULE_negation);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(464);
        match(PseudoNot);
        setState(465);
        ws();
        setState(466);
        negationArg();
        setState(467);
        ws();
        setState(468);
        match(T__3);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NegationArgContext extends ParserRuleContext {
    public TypeSelectorContext typeSelector() {
      return getRuleContext(TypeSelectorContext.class, 0);
    }

    public UniversalContext universal() {
      return getRuleContext(UniversalContext.class, 0);
    }

    public TerminalNode Hash() {
      return getToken(CSS3Parser.Hash, 0);
    }

    public ClassNameContext className() {
      return getRuleContext(ClassNameContext.class, 0);
    }

    public AttribContext attrib() {
      return getRuleContext(AttribContext.class, 0);
    }

    public PseudoContext pseudo() {
      return getRuleContext(PseudoContext.class, 0);
    }

    public NegationArgContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_negationArg;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterNegationArg(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitNegationArg(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitNegationArg(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NegationArgContext negationArg() throws RecognitionException {
    NegationArgContext _localctx = new NegationArgContext(_ctx, getState());
    enterRule(_localctx, 54, RULE_negationArg);
    try {
      setState(476);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 39, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(470);
            typeSelector();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(471);
            universal();
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(472);
            match(Hash);
          }
          break;
        case 4:
          enterOuterAlt(_localctx, 4);
          {
            setState(473);
            className();
          }
          break;
        case 5:
          enterOuterAlt(_localctx, 5);
          {
            setState(474);
            attrib();
          }
          break;
        case 6:
          enterOuterAlt(_localctx, 6);
          {
            setState(475);
            pseudo();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class OperatorContext extends ParserRuleContext {
    public OperatorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_operator;
    }

    public OperatorContext() {}

    public void copyFrom(OperatorContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class BadOperatorContext extends OperatorContext {
    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public BadOperatorContext(OperatorContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBadOperator(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBadOperator(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBadOperator(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class GoodOperatorContext extends OperatorContext {
    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public TerminalNode Comma() {
      return getToken(CSS3Parser.Comma, 0);
    }

    public TerminalNode Space() {
      return getToken(CSS3Parser.Space, 0);
    }

    public GoodOperatorContext(OperatorContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGoodOperator(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGoodOperator(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGoodOperator(this);
      else return visitor.visitChildren(this);
    }
  }

  public final OperatorContext operator() throws RecognitionException {
    OperatorContext _localctx = new OperatorContext(_ctx, getState());
    enterRule(_localctx, 56, RULE_operator);
    try {
      setState(486);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case T__12:
          _localctx = new GoodOperatorContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(478);
            match(T__12);
            setState(479);
            ws();
          }
          break;
        case Comma:
          _localctx = new GoodOperatorContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(480);
            match(Comma);
            setState(481);
            ws();
          }
          break;
        case Space:
          _localctx = new GoodOperatorContext(_localctx);
          enterOuterAlt(_localctx, 3);
          {
            setState(482);
            match(Space);
            setState(483);
            ws();
          }
          break;
        case T__10:
          _localctx = new BadOperatorContext(_localctx);
          enterOuterAlt(_localctx, 4);
          {
            setState(484);
            match(T__10);
            setState(485);
            ws();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PropertyContext extends ParserRuleContext {
    public PropertyContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_property;
    }

    public PropertyContext() {}

    public void copyFrom(PropertyContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class BadPropertyContext extends PropertyContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public BadPropertyContext(PropertyContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBadProperty(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBadProperty(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBadProperty(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class GoodPropertyContext extends PropertyContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public TerminalNode Variable() {
      return getToken(CSS3Parser.Variable, 0);
    }

    public GoodPropertyContext(PropertyContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGoodProperty(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGoodProperty(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGoodProperty(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PropertyContext property() throws RecognitionException {
    PropertyContext _localctx = new PropertyContext(_ctx, getState());
    enterRule(_localctx, 58, RULE_property);
    try {
      setState(497);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case MediaOnly:
        case Not:
        case And:
        case Or:
        case From:
        case To:
        case Ident:
          _localctx = new GoodPropertyContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(488);
            ident();
            setState(489);
            ws();
          }
          break;
        case Variable:
          _localctx = new GoodPropertyContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(491);
            match(Variable);
            setState(492);
            ws();
          }
          break;
        case T__6:
          _localctx = new BadPropertyContext(_localctx);
          enterOuterAlt(_localctx, 3);
          {
            setState(493);
            match(T__6);
            setState(494);
            ident();
          }
          break;
        case T__13:
          _localctx = new BadPropertyContext(_localctx);
          enterOuterAlt(_localctx, 4);
          {
            setState(495);
            match(T__13);
            setState(496);
            ident();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class RulesetContext extends ParserRuleContext {
    public RulesetContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_ruleset;
    }

    public RulesetContext() {}

    public void copyFrom(RulesetContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class UnknownRulesetContext extends RulesetContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<AnyContext> any() {
      return getRuleContexts(AnyContext.class);
    }

    public AnyContext any(int i) {
      return getRuleContext(AnyContext.class, i);
    }

    public DeclarationListContext declarationList() {
      return getRuleContext(DeclarationListContext.class, 0);
    }

    public UnknownRulesetContext(RulesetContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUnknownRuleset(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUnknownRuleset(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnknownRuleset(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class KnownRulesetContext extends RulesetContext {
    public SelectorGroupContext selectorGroup() {
      return getRuleContext(SelectorGroupContext.class, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public DeclarationListContext declarationList() {
      return getRuleContext(DeclarationListContext.class, 0);
    }

    public KnownRulesetContext(RulesetContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterKnownRuleset(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitKnownRuleset(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKnownRuleset(this);
      else return visitor.visitChildren(this);
    }
  }

  public final RulesetContext ruleset() throws RecognitionException {
    RulesetContext _localctx = new RulesetContext(_ctx, getState());
    enterRule(_localctx, 60, RULE_ruleset);
    int _la;
    try {
      setState(522);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 45, _ctx)) {
        case 1:
          _localctx = new KnownRulesetContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(499);
            selectorGroup();
            setState(500);
            match(T__4);
            setState(501);
            ws();
            setState(503);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__0)
                            | (1L << T__6)
                            | (1L << T__13)
                            | (1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Variable)
                            | (1L << Ident)))
                    != 0)) {
              {
                setState(502);
                declarationList();
              }
            }

            setState(505);
            match(T__5);
            setState(506);
            ws();
          }
          break;
        case 2:
          _localctx = new UnknownRulesetContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(511);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__1)
                            | (1L << T__2)
                            | (1L << T__9)
                            | (1L << Includes)
                            | (1L << DashMatch)
                            | (1L << Hash)
                            | (1L << Percentage)
                            | (1L << Uri)
                            | (1L << UnicodeRange)
                            | (1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Dimension)
                            | (1L << UnknownDimension)
                            | (1L << Plus)
                            | (1L << Minus)
                            | (1L << Number)
                            | (1L << String)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Ident)
                            | (1L << Function)))
                    != 0)) {
              {
                {
                  setState(508);
                  any();
                }
              }
              setState(513);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
            setState(514);
            match(T__4);
            setState(515);
            ws();
            setState(517);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__0)
                            | (1L << T__6)
                            | (1L << T__13)
                            | (1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Variable)
                            | (1L << Ident)))
                    != 0)) {
              {
                setState(516);
                declarationList();
              }
            }

            setState(519);
            match(T__5);
            setState(520);
            ws();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DeclarationListContext extends ParserRuleContext {
    public List<DeclarationContext> declaration() {
      return getRuleContexts(DeclarationContext.class);
    }

    public DeclarationContext declaration(int i) {
      return getRuleContext(DeclarationContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public DeclarationListContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_declarationList;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterDeclarationList(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitDeclarationList(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitDeclarationList(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclarationListContext declarationList() throws RecognitionException {
    DeclarationListContext _localctx = new DeclarationListContext(_ctx, getState());
    enterRule(_localctx, 62, RULE_declarationList);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(528);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__0) {
          {
            {
              setState(524);
              match(T__0);
              setState(525);
              ws();
            }
          }
          setState(530);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(531);
        declaration();
        setState(532);
        ws();
        setState(540);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 48, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(533);
                match(T__0);
                setState(534);
                ws();
                setState(536);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 47, _ctx)) {
                  case 1:
                    {
                      setState(535);
                      declaration();
                    }
                    break;
                }
              }
            }
          }
          setState(542);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 48, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DeclarationContext extends ParserRuleContext {
    public DeclarationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_declaration;
    }

    public DeclarationContext() {}

    public void copyFrom(DeclarationContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class UnknownDeclarationContext extends DeclarationContext {
    public PropertyContext property() {
      return getRuleContext(PropertyContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public ValueContext value() {
      return getRuleContext(ValueContext.class, 0);
    }

    public UnknownDeclarationContext(DeclarationContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUnknownDeclaration(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUnknownDeclaration(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnknownDeclaration(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class KnownDeclarationContext extends DeclarationContext {
    public PropertyContext property() {
      return getRuleContext(PropertyContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public PrioContext prio() {
      return getRuleContext(PrioContext.class, 0);
    }

    public KnownDeclarationContext(DeclarationContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterKnownDeclaration(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitKnownDeclaration(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKnownDeclaration(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DeclarationContext declaration() throws RecognitionException {
    DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
    enterRule(_localctx, 64, RULE_declaration);
    int _la;
    try {
      setState(555);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 50, _ctx)) {
        case 1:
          _localctx = new KnownDeclarationContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(543);
            property();
            setState(544);
            match(T__2);
            setState(545);
            ws();
            setState(546);
            expr();
            setState(548);
            _errHandler.sync(this);
            _la = _input.LA(1);
            if (_la == Important) {
              {
                setState(547);
                prio();
              }
            }
          }
          break;
        case 2:
          _localctx = new UnknownDeclarationContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(550);
            property();
            setState(551);
            match(T__2);
            setState(552);
            ws();
            setState(553);
            value();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PrioContext extends ParserRuleContext {
    public TerminalNode Important() {
      return getToken(CSS3Parser.Important, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public PrioContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_prio;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterPrio(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitPrio(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitPrio(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PrioContext prio() throws RecognitionException {
    PrioContext _localctx = new PrioContext(_ctx, getState());
    enterRule(_localctx, 66, RULE_prio);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(557);
        match(Important);
        setState(558);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ValueContext extends ParserRuleContext {
    public List<AnyContext> any() {
      return getRuleContexts(AnyContext.class);
    }

    public AnyContext any(int i) {
      return getRuleContext(AnyContext.class, i);
    }

    public List<BlockContext> block() {
      return getRuleContexts(BlockContext.class);
    }

    public BlockContext block(int i) {
      return getRuleContext(BlockContext.class, i);
    }

    public List<AtKeywordContext> atKeyword() {
      return getRuleContexts(AtKeywordContext.class);
    }

    public AtKeywordContext atKeyword(int i) {
      return getRuleContext(AtKeywordContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public ValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_value;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterValue(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitValue(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitValue(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ValueContext value() throws RecognitionException {
    ValueContext _localctx = new ValueContext(_ctx, getState());
    enterRule(_localctx, 68, RULE_value);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(565);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
            case 1:
              {
                setState(565);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                  case T__1:
                  case T__2:
                  case T__9:
                  case Includes:
                  case DashMatch:
                  case Hash:
                  case Percentage:
                  case Uri:
                  case UnicodeRange:
                  case MediaOnly:
                  case Not:
                  case And:
                  case Dimension:
                  case UnknownDimension:
                  case Plus:
                  case Minus:
                  case Number:
                  case String:
                  case Or:
                  case From:
                  case To:
                  case Ident:
                  case Function:
                    {
                      setState(560);
                      any();
                    }
                    break;
                  case T__4:
                    {
                      setState(561);
                      block();
                    }
                    break;
                  case T__14:
                    {
                      setState(562);
                      atKeyword();
                      setState(563);
                      ws();
                    }
                    break;
                  default:
                    throw new NoViableAltException(this);
                }
              }
              break;
            default:
              throw new NoViableAltException(this);
          }
          setState(567);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 52, _ctx);
        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ExprContext extends ParserRuleContext {
    public List<TermContext> term() {
      return getRuleContexts(TermContext.class);
    }

    public TermContext term(int i) {
      return getRuleContext(TermContext.class, i);
    }

    public List<OperatorContext> operator() {
      return getRuleContexts(OperatorContext.class);
    }

    public OperatorContext operator(int i) {
      return getRuleContext(OperatorContext.class, i);
    }

    public ExprContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_expr;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterExpr(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitExpr(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitExpr(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ExprContext expr() throws RecognitionException {
    ExprContext _localctx = new ExprContext(_ctx, getState());
    enterRule(_localctx, 70, RULE_expr);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(569);
        term();
        setState(576);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 54, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(571);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if ((((_la) & ~0x3f) == 0
                    && ((1L << _la)
                            & ((1L << T__10) | (1L << T__12) | (1L << Space) | (1L << Comma)))
                        != 0)) {
                  {
                    setState(570);
                    operator();
                  }
                }

                setState(573);
                term();
              }
            }
          }
          setState(578);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 54, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class TermContext extends ParserRuleContext {
    public TermContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_term;
    }

    public TermContext() {}

    public void copyFrom(TermContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class BadTermContext extends TermContext {
    public DxImageTransformContext dxImageTransform() {
      return getRuleContext(DxImageTransformContext.class, 0);
    }

    public BadTermContext(TermContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBadTerm(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBadTerm(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBadTerm(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class KnownTermContext extends TermContext {
    public NumberContext number() {
      return getRuleContext(NumberContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public PercentageContext percentage() {
      return getRuleContext(PercentageContext.class, 0);
    }

    public DimensionContext dimension() {
      return getRuleContext(DimensionContext.class, 0);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public TerminalNode UnicodeRange() {
      return getToken(CSS3Parser.UnicodeRange, 0);
    }

    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public VarContext var() {
      return getRuleContext(VarContext.class, 0);
    }

    public TerminalNode Uri() {
      return getToken(CSS3Parser.Uri, 0);
    }

    public HexcolorContext hexcolor() {
      return getRuleContext(HexcolorContext.class, 0);
    }

    public CalcContext calc() {
      return getRuleContext(CalcContext.class, 0);
    }

    public FunctionContext function() {
      return getRuleContext(FunctionContext.class, 0);
    }

    public KnownTermContext(TermContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterKnownTerm(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitKnownTerm(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKnownTerm(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class UnknownTermContext extends TermContext {
    public UnknownDimensionContext unknownDimension() {
      return getRuleContext(UnknownDimensionContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public UnknownTermContext(TermContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUnknownTerm(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUnknownTerm(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnknownTerm(this);
      else return visitor.visitChildren(this);
    }
  }

  public final TermContext term() throws RecognitionException {
    TermContext _localctx = new TermContext(_ctx, getState());
    enterRule(_localctx, 72, RULE_term);
    try {
      setState(605);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 55, _ctx)) {
        case 1:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(579);
            number();
            setState(580);
            ws();
          }
          break;
        case 2:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(582);
            percentage();
            setState(583);
            ws();
          }
          break;
        case 3:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 3);
          {
            setState(585);
            dimension();
            setState(586);
            ws();
          }
          break;
        case 4:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 4);
          {
            setState(588);
            match(String);
            setState(589);
            ws();
          }
          break;
        case 5:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 5);
          {
            setState(590);
            match(UnicodeRange);
            setState(591);
            ws();
          }
          break;
        case 6:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 6);
          {
            setState(592);
            ident();
            setState(593);
            ws();
          }
          break;
        case 7:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 7);
          {
            setState(595);
            var();
          }
          break;
        case 8:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 8);
          {
            setState(596);
            match(Uri);
            setState(597);
            ws();
          }
          break;
        case 9:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 9);
          {
            setState(598);
            hexcolor();
          }
          break;
        case 10:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 10);
          {
            setState(599);
            calc();
          }
          break;
        case 11:
          _localctx = new KnownTermContext(_localctx);
          enterOuterAlt(_localctx, 11);
          {
            setState(600);
            function();
          }
          break;
        case 12:
          _localctx = new UnknownTermContext(_localctx);
          enterOuterAlt(_localctx, 12);
          {
            setState(601);
            unknownDimension();
            setState(602);
            ws();
          }
          break;
        case 13:
          _localctx = new BadTermContext(_localctx);
          enterOuterAlt(_localctx, 13);
          {
            setState(604);
            dxImageTransform();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FunctionContext extends ParserRuleContext {
    public TerminalNode Function() {
      return getToken(CSS3Parser.Function, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public FunctionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_function;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFunction(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFunction(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FunctionContext function() throws RecognitionException {
    FunctionContext _localctx = new FunctionContext(_ctx, getState());
    enterRule(_localctx, 74, RULE_function);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(607);
        match(Function);
        setState(608);
        ws();
        setState(609);
        expr();
        setState(610);
        match(T__3);
        setState(611);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DxImageTransformContext extends ParserRuleContext {
    public TerminalNode DxImageTransform() {
      return getToken(CSS3Parser.DxImageTransform, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public DxImageTransformContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_dxImageTransform;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterDxImageTransform(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitDxImageTransform(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitDxImageTransform(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DxImageTransformContext dxImageTransform() throws RecognitionException {
    DxImageTransformContext _localctx = new DxImageTransformContext(_ctx, getState());
    enterRule(_localctx, 76, RULE_dxImageTransform);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(613);
        match(DxImageTransform);
        setState(614);
        ws();
        setState(615);
        expr();
        setState(616);
        match(T__3);
        setState(617);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class HexcolorContext extends ParserRuleContext {
    public TerminalNode Hash() {
      return getToken(CSS3Parser.Hash, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public HexcolorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_hexcolor;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterHexcolor(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitHexcolor(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitHexcolor(this);
      else return visitor.visitChildren(this);
    }
  }

  public final HexcolorContext hexcolor() throws RecognitionException {
    HexcolorContext _localctx = new HexcolorContext(_ctx, getState());
    enterRule(_localctx, 78, RULE_hexcolor);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(619);
        match(Hash);
        setState(620);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NumberContext extends ParserRuleContext {
    public TerminalNode Number() {
      return getToken(CSS3Parser.Number, 0);
    }

    public TerminalNode Plus() {
      return getToken(CSS3Parser.Plus, 0);
    }

    public TerminalNode Minus() {
      return getToken(CSS3Parser.Minus, 0);
    }

    public NumberContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_number;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterNumber(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitNumber(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitNumber(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NumberContext number() throws RecognitionException {
    NumberContext _localctx = new NumberContext(_ctx, getState());
    enterRule(_localctx, 80, RULE_number);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(623);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == Plus || _la == Minus) {
          {
            setState(622);
            _la = _input.LA(1);
            if (!(_la == Plus || _la == Minus)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
          }
        }

        setState(625);
        match(Number);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class PercentageContext extends ParserRuleContext {
    public TerminalNode Percentage() {
      return getToken(CSS3Parser.Percentage, 0);
    }

    public TerminalNode Plus() {
      return getToken(CSS3Parser.Plus, 0);
    }

    public TerminalNode Minus() {
      return getToken(CSS3Parser.Minus, 0);
    }

    public PercentageContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_percentage;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterPercentage(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitPercentage(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitPercentage(this);
      else return visitor.visitChildren(this);
    }
  }

  public final PercentageContext percentage() throws RecognitionException {
    PercentageContext _localctx = new PercentageContext(_ctx, getState());
    enterRule(_localctx, 82, RULE_percentage);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(628);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == Plus || _la == Minus) {
          {
            setState(627);
            _la = _input.LA(1);
            if (!(_la == Plus || _la == Minus)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
          }
        }

        setState(630);
        match(Percentage);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class DimensionContext extends ParserRuleContext {
    public TerminalNode Dimension() {
      return getToken(CSS3Parser.Dimension, 0);
    }

    public TerminalNode Plus() {
      return getToken(CSS3Parser.Plus, 0);
    }

    public TerminalNode Minus() {
      return getToken(CSS3Parser.Minus, 0);
    }

    public DimensionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_dimension;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterDimension(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitDimension(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitDimension(this);
      else return visitor.visitChildren(this);
    }
  }

  public final DimensionContext dimension() throws RecognitionException {
    DimensionContext _localctx = new DimensionContext(_ctx, getState());
    enterRule(_localctx, 84, RULE_dimension);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(633);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == Plus || _la == Minus) {
          {
            setState(632);
            _la = _input.LA(1);
            if (!(_la == Plus || _la == Minus)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
          }
        }

        setState(635);
        match(Dimension);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class UnknownDimensionContext extends ParserRuleContext {
    public TerminalNode UnknownDimension() {
      return getToken(CSS3Parser.UnknownDimension, 0);
    }

    public TerminalNode Plus() {
      return getToken(CSS3Parser.Plus, 0);
    }

    public TerminalNode Minus() {
      return getToken(CSS3Parser.Minus, 0);
    }

    public UnknownDimensionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_unknownDimension;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUnknownDimension(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUnknownDimension(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnknownDimension(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UnknownDimensionContext unknownDimension() throws RecognitionException {
    UnknownDimensionContext _localctx = new UnknownDimensionContext(_ctx, getState());
    enterRule(_localctx, 86, RULE_unknownDimension);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(638);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if (_la == Plus || _la == Minus) {
          {
            setState(637);
            _la = _input.LA(1);
            if (!(_la == Plus || _la == Minus)) {
              _errHandler.recoverInline(this);
            } else {
              if (_input.LA(1) == Token.EOF) matchedEOF = true;
              _errHandler.reportMatch(this);
              consume();
            }
          }
        }

        setState(640);
        match(UnknownDimension);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class AnyContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public NumberContext number() {
      return getRuleContext(NumberContext.class, 0);
    }

    public PercentageContext percentage() {
      return getRuleContext(PercentageContext.class, 0);
    }

    public DimensionContext dimension() {
      return getRuleContext(DimensionContext.class, 0);
    }

    public UnknownDimensionContext unknownDimension() {
      return getRuleContext(UnknownDimensionContext.class, 0);
    }

    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public TerminalNode Uri() {
      return getToken(CSS3Parser.Uri, 0);
    }

    public TerminalNode Hash() {
      return getToken(CSS3Parser.Hash, 0);
    }

    public TerminalNode UnicodeRange() {
      return getToken(CSS3Parser.UnicodeRange, 0);
    }

    public TerminalNode Includes() {
      return getToken(CSS3Parser.Includes, 0);
    }

    public TerminalNode DashMatch() {
      return getToken(CSS3Parser.DashMatch, 0);
    }

    public TerminalNode Function() {
      return getToken(CSS3Parser.Function, 0);
    }

    public List<AnyContext> any() {
      return getRuleContexts(AnyContext.class);
    }

    public AnyContext any(int i) {
      return getRuleContext(AnyContext.class, i);
    }

    public List<UnusedContext> unused() {
      return getRuleContexts(UnusedContext.class);
    }

    public UnusedContext unused(int i) {
      return getRuleContext(UnusedContext.class, i);
    }

    public AnyContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_any;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterAny(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitAny(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitAny(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AnyContext any() throws RecognitionException {
    AnyContext _localctx = new AnyContext(_ctx, getState());
    enterRule(_localctx, 88, RULE_any);
    int _la;
    try {
      setState(707);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 66, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(642);
            ident();
            setState(643);
            ws();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(645);
            number();
            setState(646);
            ws();
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(648);
            percentage();
            setState(649);
            ws();
          }
          break;
        case 4:
          enterOuterAlt(_localctx, 4);
          {
            setState(651);
            dimension();
            setState(652);
            ws();
          }
          break;
        case 5:
          enterOuterAlt(_localctx, 5);
          {
            setState(654);
            unknownDimension();
            setState(655);
            ws();
          }
          break;
        case 6:
          enterOuterAlt(_localctx, 6);
          {
            setState(657);
            match(String);
            setState(658);
            ws();
          }
          break;
        case 7:
          enterOuterAlt(_localctx, 7);
          {
            setState(659);
            match(Uri);
            setState(660);
            ws();
          }
          break;
        case 8:
          enterOuterAlt(_localctx, 8);
          {
            setState(661);
            match(Hash);
            setState(662);
            ws();
          }
          break;
        case 9:
          enterOuterAlt(_localctx, 9);
          {
            setState(663);
            match(UnicodeRange);
            setState(664);
            ws();
          }
          break;
        case 10:
          enterOuterAlt(_localctx, 10);
          {
            setState(665);
            match(Includes);
            setState(666);
            ws();
          }
          break;
        case 11:
          enterOuterAlt(_localctx, 11);
          {
            setState(667);
            match(DashMatch);
            setState(668);
            ws();
          }
          break;
        case 12:
          enterOuterAlt(_localctx, 12);
          {
            setState(669);
            match(T__2);
            setState(670);
            ws();
          }
          break;
        case 13:
          enterOuterAlt(_localctx, 13);
          {
            setState(671);
            match(Function);
            setState(672);
            ws();
            setState(677);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__0)
                            | (1L << T__1)
                            | (1L << T__2)
                            | (1L << T__4)
                            | (1L << T__9)
                            | (1L << T__14)
                            | (1L << Cdo)
                            | (1L << Cdc)
                            | (1L << Includes)
                            | (1L << DashMatch)
                            | (1L << Hash)
                            | (1L << Percentage)
                            | (1L << Uri)
                            | (1L << UnicodeRange)
                            | (1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Dimension)
                            | (1L << UnknownDimension)
                            | (1L << Plus)
                            | (1L << Minus)
                            | (1L << Number)
                            | (1L << String)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Ident)
                            | (1L << Function)))
                    != 0)) {
              {
                setState(675);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                  case T__1:
                  case T__2:
                  case T__9:
                  case Includes:
                  case DashMatch:
                  case Hash:
                  case Percentage:
                  case Uri:
                  case UnicodeRange:
                  case MediaOnly:
                  case Not:
                  case And:
                  case Dimension:
                  case UnknownDimension:
                  case Plus:
                  case Minus:
                  case Number:
                  case String:
                  case Or:
                  case From:
                  case To:
                  case Ident:
                  case Function:
                    {
                      setState(673);
                      any();
                    }
                    break;
                  case T__0:
                  case T__4:
                  case T__14:
                  case Cdo:
                  case Cdc:
                    {
                      setState(674);
                      unused();
                    }
                    break;
                  default:
                    throw new NoViableAltException(this);
                }
              }
              setState(679);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
            setState(680);
            match(T__3);
            setState(681);
            ws();
          }
          break;
        case 14:
          enterOuterAlt(_localctx, 14);
          {
            setState(683);
            match(T__1);
            setState(684);
            ws();
            setState(689);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__0)
                            | (1L << T__1)
                            | (1L << T__2)
                            | (1L << T__4)
                            | (1L << T__9)
                            | (1L << T__14)
                            | (1L << Cdo)
                            | (1L << Cdc)
                            | (1L << Includes)
                            | (1L << DashMatch)
                            | (1L << Hash)
                            | (1L << Percentage)
                            | (1L << Uri)
                            | (1L << UnicodeRange)
                            | (1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Dimension)
                            | (1L << UnknownDimension)
                            | (1L << Plus)
                            | (1L << Minus)
                            | (1L << Number)
                            | (1L << String)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Ident)
                            | (1L << Function)))
                    != 0)) {
              {
                setState(687);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                  case T__1:
                  case T__2:
                  case T__9:
                  case Includes:
                  case DashMatch:
                  case Hash:
                  case Percentage:
                  case Uri:
                  case UnicodeRange:
                  case MediaOnly:
                  case Not:
                  case And:
                  case Dimension:
                  case UnknownDimension:
                  case Plus:
                  case Minus:
                  case Number:
                  case String:
                  case Or:
                  case From:
                  case To:
                  case Ident:
                  case Function:
                    {
                      setState(685);
                      any();
                    }
                    break;
                  case T__0:
                  case T__4:
                  case T__14:
                  case Cdo:
                  case Cdc:
                    {
                      setState(686);
                      unused();
                    }
                    break;
                  default:
                    throw new NoViableAltException(this);
                }
              }
              setState(691);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
            setState(692);
            match(T__3);
            setState(693);
            ws();
          }
          break;
        case 15:
          enterOuterAlt(_localctx, 15);
          {
            setState(695);
            match(T__9);
            setState(696);
            ws();
            setState(701);
            _errHandler.sync(this);
            _la = _input.LA(1);
            while ((((_la) & ~0x3f) == 0
                && ((1L << _la)
                        & ((1L << T__0)
                            | (1L << T__1)
                            | (1L << T__2)
                            | (1L << T__4)
                            | (1L << T__9)
                            | (1L << T__14)
                            | (1L << Cdo)
                            | (1L << Cdc)
                            | (1L << Includes)
                            | (1L << DashMatch)
                            | (1L << Hash)
                            | (1L << Percentage)
                            | (1L << Uri)
                            | (1L << UnicodeRange)
                            | (1L << MediaOnly)
                            | (1L << Not)
                            | (1L << And)
                            | (1L << Dimension)
                            | (1L << UnknownDimension)
                            | (1L << Plus)
                            | (1L << Minus)
                            | (1L << Number)
                            | (1L << String)
                            | (1L << Or)
                            | (1L << From)
                            | (1L << To)
                            | (1L << Ident)
                            | (1L << Function)))
                    != 0)) {
              {
                setState(699);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                  case T__1:
                  case T__2:
                  case T__9:
                  case Includes:
                  case DashMatch:
                  case Hash:
                  case Percentage:
                  case Uri:
                  case UnicodeRange:
                  case MediaOnly:
                  case Not:
                  case And:
                  case Dimension:
                  case UnknownDimension:
                  case Plus:
                  case Minus:
                  case Number:
                  case String:
                  case Or:
                  case From:
                  case To:
                  case Ident:
                  case Function:
                    {
                      setState(697);
                      any();
                    }
                    break;
                  case T__0:
                  case T__4:
                  case T__14:
                  case Cdo:
                  case Cdc:
                    {
                      setState(698);
                      unused();
                    }
                    break;
                  default:
                    throw new NoViableAltException(this);
                }
              }
              setState(703);
              _errHandler.sync(this);
              _la = _input.LA(1);
            }
            setState(704);
            match(T__11);
            setState(705);
            ws();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class AtRuleContext extends ParserRuleContext {
    public AtRuleContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_atRule;
    }

    public AtRuleContext() {}

    public void copyFrom(AtRuleContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class UnknownAtRuleContext extends AtRuleContext {
    public AtKeywordContext atKeyword() {
      return getRuleContext(AtKeywordContext.class, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public BlockContext block() {
      return getRuleContext(BlockContext.class, 0);
    }

    public List<AnyContext> any() {
      return getRuleContexts(AnyContext.class);
    }

    public AnyContext any(int i) {
      return getRuleContext(AnyContext.class, i);
    }

    public UnknownAtRuleContext(AtRuleContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUnknownAtRule(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUnknownAtRule(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnknownAtRule(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AtRuleContext atRule() throws RecognitionException {
    AtRuleContext _localctx = new AtRuleContext(_ctx, getState());
    enterRule(_localctx, 90, RULE_atRule);
    int _la;
    try {
      _localctx = new UnknownAtRuleContext(_localctx);
      enterOuterAlt(_localctx, 1);
      {
        setState(709);
        atKeyword();
        setState(710);
        ws();
        setState(714);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__1)
                        | (1L << T__2)
                        | (1L << T__9)
                        | (1L << Includes)
                        | (1L << DashMatch)
                        | (1L << Hash)
                        | (1L << Percentage)
                        | (1L << Uri)
                        | (1L << UnicodeRange)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Dimension)
                        | (1L << UnknownDimension)
                        | (1L << Plus)
                        | (1L << Minus)
                        | (1L << Number)
                        | (1L << String)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Ident)
                        | (1L << Function)))
                != 0)) {
          {
            {
              setState(711);
              any();
            }
          }
          setState(716);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(720);
        _errHandler.sync(this);
        switch (_input.LA(1)) {
          case T__4:
            {
              setState(717);
              block();
            }
            break;
          case T__0:
            {
              setState(718);
              match(T__0);
              setState(719);
              ws();
            }
            break;
          default:
            throw new NoViableAltException(this);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class AtKeywordContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public AtKeywordContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_atKeyword;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterAtKeyword(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitAtKeyword(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitAtKeyword(this);
      else return visitor.visitChildren(this);
    }
  }

  public final AtKeywordContext atKeyword() throws RecognitionException {
    AtKeywordContext _localctx = new AtKeywordContext(_ctx, getState());
    enterRule(_localctx, 92, RULE_atKeyword);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(722);
        match(T__14);
        setState(723);
        ident();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class UnusedContext extends ParserRuleContext {
    public BlockContext block() {
      return getRuleContext(BlockContext.class, 0);
    }

    public AtKeywordContext atKeyword() {
      return getRuleContext(AtKeywordContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public TerminalNode Cdo() {
      return getToken(CSS3Parser.Cdo, 0);
    }

    public TerminalNode Cdc() {
      return getToken(CSS3Parser.Cdc, 0);
    }

    public UnusedContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_unused;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterUnused(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitUnused(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnused(this);
      else return visitor.visitChildren(this);
    }
  }

  public final UnusedContext unused() throws RecognitionException {
    UnusedContext _localctx = new UnusedContext(_ctx, getState());
    enterRule(_localctx, 94, RULE_unused);
    try {
      setState(735);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case T__4:
          enterOuterAlt(_localctx, 1);
          {
            setState(725);
            block();
          }
          break;
        case T__14:
          enterOuterAlt(_localctx, 2);
          {
            setState(726);
            atKeyword();
            setState(727);
            ws();
          }
          break;
        case T__0:
          enterOuterAlt(_localctx, 3);
          {
            setState(729);
            match(T__0);
            setState(730);
            ws();
          }
          break;
        case Cdo:
          enterOuterAlt(_localctx, 4);
          {
            setState(731);
            match(Cdo);
            setState(732);
            ws();
          }
          break;
        case Cdc:
          enterOuterAlt(_localctx, 5);
          {
            setState(733);
            match(Cdc);
            setState(734);
            ws();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class BlockContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<DeclarationListContext> declarationList() {
      return getRuleContexts(DeclarationListContext.class);
    }

    public DeclarationListContext declarationList(int i) {
      return getRuleContext(DeclarationListContext.class, i);
    }

    public List<NestedStatementContext> nestedStatement() {
      return getRuleContexts(NestedStatementContext.class);
    }

    public NestedStatementContext nestedStatement(int i) {
      return getRuleContext(NestedStatementContext.class, i);
    }

    public List<AnyContext> any() {
      return getRuleContexts(AnyContext.class);
    }

    public AnyContext any(int i) {
      return getRuleContext(AnyContext.class, i);
    }

    public List<BlockContext> block() {
      return getRuleContexts(BlockContext.class);
    }

    public BlockContext block(int i) {
      return getRuleContext(BlockContext.class, i);
    }

    public List<AtKeywordContext> atKeyword() {
      return getRuleContexts(AtKeywordContext.class);
    }

    public AtKeywordContext atKeyword(int i) {
      return getRuleContext(AtKeywordContext.class, i);
    }

    public BlockContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_block;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterBlock(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitBlock(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitBlock(this);
      else return visitor.visitChildren(this);
    }
  }

  public final BlockContext block() throws RecognitionException {
    BlockContext _localctx = new BlockContext(_ctx, getState());
    enterRule(_localctx, 96, RULE_block);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(737);
        match(T__4);
        setState(738);
        ws();
        setState(750);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__0)
                        | (1L << T__1)
                        | (1L << T__2)
                        | (1L << T__4)
                        | (1L << T__6)
                        | (1L << T__7)
                        | (1L << T__8)
                        | (1L << T__9)
                        | (1L << T__13)
                        | (1L << T__14)
                        | (1L << Includes)
                        | (1L << DashMatch)
                        | (1L << Hash)
                        | (1L << Page)
                        | (1L << Media)
                        | (1L << Percentage)
                        | (1L << Uri)
                        | (1L << UnicodeRange)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Dimension)
                        | (1L << UnknownDimension)
                        | (1L << Plus)
                        | (1L << Minus)
                        | (1L << PseudoNot)
                        | (1L << Number)
                        | (1L << String)
                        | (1L << FontFace)
                        | (1L << Supports)
                        | (1L << Or)
                        | (1L << Keyframes)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Viewport)
                        | (1L << CounterStyle)
                        | (1L << FontFeatureValues)
                        | (1L << Variable)
                        | (1L << Ident)
                        | (1L << Function)))
                != 0)) {
          {
            setState(748);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 70, _ctx)) {
              case 1:
                {
                  setState(739);
                  declarationList();
                }
                break;
              case 2:
                {
                  setState(740);
                  nestedStatement();
                }
                break;
              case 3:
                {
                  setState(741);
                  any();
                }
                break;
              case 4:
                {
                  setState(742);
                  block();
                }
                break;
              case 5:
                {
                  setState(743);
                  atKeyword();
                  setState(744);
                  ws();
                }
                break;
              case 6:
                {
                  setState(746);
                  match(T__0);
                  setState(747);
                  ws();
                }
                break;
            }
          }
          setState(752);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(753);
        match(T__5);
        setState(754);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class NestedStatementContext extends ParserRuleContext {
    public RulesetContext ruleset() {
      return getRuleContext(RulesetContext.class, 0);
    }

    public MediaContext media() {
      return getRuleContext(MediaContext.class, 0);
    }

    public PageContext page() {
      return getRuleContext(PageContext.class, 0);
    }

    public FontFaceRuleContext fontFaceRule() {
      return getRuleContext(FontFaceRuleContext.class, 0);
    }

    public KeyframesRuleContext keyframesRule() {
      return getRuleContext(KeyframesRuleContext.class, 0);
    }

    public SupportsRuleContext supportsRule() {
      return getRuleContext(SupportsRuleContext.class, 0);
    }

    public ViewportContext viewport() {
      return getRuleContext(ViewportContext.class, 0);
    }

    public CounterStyleContext counterStyle() {
      return getRuleContext(CounterStyleContext.class, 0);
    }

    public FontFeatureValuesRuleContext fontFeatureValuesRule() {
      return getRuleContext(FontFeatureValuesRuleContext.class, 0);
    }

    public AtRuleContext atRule() {
      return getRuleContext(AtRuleContext.class, 0);
    }

    public NestedStatementContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_nestedStatement;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterNestedStatement(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitNestedStatement(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitNestedStatement(this);
      else return visitor.visitChildren(this);
    }
  }

  public final NestedStatementContext nestedStatement() throws RecognitionException {
    NestedStatementContext _localctx = new NestedStatementContext(_ctx, getState());
    enterRule(_localctx, 98, RULE_nestedStatement);
    try {
      setState(766);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case T__1:
        case T__2:
        case T__4:
        case T__6:
        case T__7:
        case T__8:
        case T__9:
        case Includes:
        case DashMatch:
        case Hash:
        case Percentage:
        case Uri:
        case UnicodeRange:
        case MediaOnly:
        case Not:
        case And:
        case Dimension:
        case UnknownDimension:
        case Plus:
        case Minus:
        case PseudoNot:
        case Number:
        case String:
        case Or:
        case From:
        case To:
        case Ident:
        case Function:
          enterOuterAlt(_localctx, 1);
          {
            setState(756);
            ruleset();
          }
          break;
        case Media:
          enterOuterAlt(_localctx, 2);
          {
            setState(757);
            media();
          }
          break;
        case Page:
          enterOuterAlt(_localctx, 3);
          {
            setState(758);
            page();
          }
          break;
        case FontFace:
          enterOuterAlt(_localctx, 4);
          {
            setState(759);
            fontFaceRule();
          }
          break;
        case Keyframes:
          enterOuterAlt(_localctx, 5);
          {
            setState(760);
            keyframesRule();
          }
          break;
        case Supports:
          enterOuterAlt(_localctx, 6);
          {
            setState(761);
            supportsRule();
          }
          break;
        case Viewport:
          enterOuterAlt(_localctx, 7);
          {
            setState(762);
            viewport();
          }
          break;
        case CounterStyle:
          enterOuterAlt(_localctx, 8);
          {
            setState(763);
            counterStyle();
          }
          break;
        case FontFeatureValues:
          enterOuterAlt(_localctx, 9);
          {
            setState(764);
            fontFeatureValuesRule();
          }
          break;
        case T__14:
          enterOuterAlt(_localctx, 10);
          {
            setState(765);
            atRule();
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class GroupRuleBodyContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<NestedStatementContext> nestedStatement() {
      return getRuleContexts(NestedStatementContext.class);
    }

    public NestedStatementContext nestedStatement(int i) {
      return getRuleContext(NestedStatementContext.class, i);
    }

    public GroupRuleBodyContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_groupRuleBody;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGroupRuleBody(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGroupRuleBody(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGroupRuleBody(this);
      else return visitor.visitChildren(this);
    }
  }

  public final GroupRuleBodyContext groupRuleBody() throws RecognitionException {
    GroupRuleBodyContext _localctx = new GroupRuleBodyContext(_ctx, getState());
    enterRule(_localctx, 100, RULE_groupRuleBody);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(768);
        match(T__4);
        setState(769);
        ws();
        setState(773);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__1)
                        | (1L << T__2)
                        | (1L << T__4)
                        | (1L << T__6)
                        | (1L << T__7)
                        | (1L << T__8)
                        | (1L << T__9)
                        | (1L << T__14)
                        | (1L << Includes)
                        | (1L << DashMatch)
                        | (1L << Hash)
                        | (1L << Page)
                        | (1L << Media)
                        | (1L << Percentage)
                        | (1L << Uri)
                        | (1L << UnicodeRange)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Dimension)
                        | (1L << UnknownDimension)
                        | (1L << Plus)
                        | (1L << Minus)
                        | (1L << PseudoNot)
                        | (1L << Number)
                        | (1L << String)
                        | (1L << FontFace)
                        | (1L << Supports)
                        | (1L << Or)
                        | (1L << Keyframes)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Viewport)
                        | (1L << CounterStyle)
                        | (1L << FontFeatureValues)
                        | (1L << Ident)
                        | (1L << Function)))
                != 0)) {
          {
            {
              setState(770);
              nestedStatement();
            }
          }
          setState(775);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(776);
        match(T__5);
        setState(777);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsRuleContext extends ParserRuleContext {
    public TerminalNode Supports() {
      return getToken(CSS3Parser.Supports, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public SupportsConditionContext supportsCondition() {
      return getRuleContext(SupportsConditionContext.class, 0);
    }

    public GroupRuleBodyContext groupRuleBody() {
      return getRuleContext(GroupRuleBodyContext.class, 0);
    }

    public SupportsRuleContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsRule;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterSupportsRule(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSupportsRule(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsRule(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsRuleContext supportsRule() throws RecognitionException {
    SupportsRuleContext _localctx = new SupportsRuleContext(_ctx, getState());
    enterRule(_localctx, 102, RULE_supportsRule);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(779);
        match(Supports);
        setState(780);
        ws();
        setState(781);
        supportsCondition();
        setState(782);
        ws();
        setState(783);
        groupRuleBody();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsConditionContext extends ParserRuleContext {
    public SupportsNegationContext supportsNegation() {
      return getRuleContext(SupportsNegationContext.class, 0);
    }

    public SupportsConjunctionContext supportsConjunction() {
      return getRuleContext(SupportsConjunctionContext.class, 0);
    }

    public SupportsDisjunctionContext supportsDisjunction() {
      return getRuleContext(SupportsDisjunctionContext.class, 0);
    }

    public SupportsConditionInParensContext supportsConditionInParens() {
      return getRuleContext(SupportsConditionInParensContext.class, 0);
    }

    public SupportsConditionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsCondition;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterSupportsCondition(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSupportsCondition(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsCondition(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsConditionContext supportsCondition() throws RecognitionException {
    SupportsConditionContext _localctx = new SupportsConditionContext(_ctx, getState());
    enterRule(_localctx, 104, RULE_supportsCondition);
    try {
      setState(789);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 74, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(785);
            supportsNegation();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(786);
            supportsConjunction();
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(787);
            supportsDisjunction();
          }
          break;
        case 4:
          enterOuterAlt(_localctx, 4);
          {
            setState(788);
            supportsConditionInParens();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsConditionInParensContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public SupportsConditionContext supportsCondition() {
      return getRuleContext(SupportsConditionContext.class, 0);
    }

    public SupportsDeclarationConditionContext supportsDeclarationCondition() {
      return getRuleContext(SupportsDeclarationConditionContext.class, 0);
    }

    public GeneralEnclosedContext generalEnclosed() {
      return getRuleContext(GeneralEnclosedContext.class, 0);
    }

    public SupportsConditionInParensContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsConditionInParens;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterSupportsConditionInParens(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitSupportsConditionInParens(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsConditionInParens(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsConditionInParensContext supportsConditionInParens()
      throws RecognitionException {
    SupportsConditionInParensContext _localctx =
        new SupportsConditionInParensContext(_ctx, getState());
    enterRule(_localctx, 106, RULE_supportsConditionInParens);
    try {
      setState(799);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 75, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(791);
            match(T__1);
            setState(792);
            ws();
            setState(793);
            supportsCondition();
            setState(794);
            ws();
            setState(795);
            match(T__3);
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(797);
            supportsDeclarationCondition();
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(798);
            generalEnclosed();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsNegationContext extends ParserRuleContext {
    public TerminalNode Not() {
      return getToken(CSS3Parser.Not, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode Space() {
      return getToken(CSS3Parser.Space, 0);
    }

    public SupportsConditionInParensContext supportsConditionInParens() {
      return getRuleContext(SupportsConditionInParensContext.class, 0);
    }

    public SupportsNegationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsNegation;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterSupportsNegation(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSupportsNegation(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsNegation(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsNegationContext supportsNegation() throws RecognitionException {
    SupportsNegationContext _localctx = new SupportsNegationContext(_ctx, getState());
    enterRule(_localctx, 108, RULE_supportsNegation);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(801);
        match(Not);
        setState(802);
        ws();
        setState(803);
        match(Space);
        setState(804);
        ws();
        setState(805);
        supportsConditionInParens();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsConjunctionContext extends ParserRuleContext {
    public List<SupportsConditionInParensContext> supportsConditionInParens() {
      return getRuleContexts(SupportsConditionInParensContext.class);
    }

    public SupportsConditionInParensContext supportsConditionInParens(int i) {
      return getRuleContext(SupportsConditionInParensContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<TerminalNode> Space() {
      return getTokens(CSS3Parser.Space);
    }

    public TerminalNode Space(int i) {
      return getToken(CSS3Parser.Space, i);
    }

    public List<TerminalNode> And() {
      return getTokens(CSS3Parser.And);
    }

    public TerminalNode And(int i) {
      return getToken(CSS3Parser.And, i);
    }

    public SupportsConjunctionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsConjunction;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterSupportsConjunction(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSupportsConjunction(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsConjunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsConjunctionContext supportsConjunction() throws RecognitionException {
    SupportsConjunctionContext _localctx = new SupportsConjunctionContext(_ctx, getState());
    enterRule(_localctx, 110, RULE_supportsConjunction);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(807);
        supportsConditionInParens();
        setState(817);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
            case 1:
              {
                {
                  setState(808);
                  ws();
                  setState(809);
                  match(Space);
                  setState(810);
                  ws();
                  setState(811);
                  match(And);
                  setState(812);
                  ws();
                  setState(813);
                  match(Space);
                  setState(814);
                  ws();
                  setState(815);
                  supportsConditionInParens();
                }
              }
              break;
            default:
              throw new NoViableAltException(this);
          }
          setState(819);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 76, _ctx);
        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsDisjunctionContext extends ParserRuleContext {
    public List<SupportsConditionInParensContext> supportsConditionInParens() {
      return getRuleContexts(SupportsConditionInParensContext.class);
    }

    public SupportsConditionInParensContext supportsConditionInParens(int i) {
      return getRuleContext(SupportsConditionInParensContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<TerminalNode> Space() {
      return getTokens(CSS3Parser.Space);
    }

    public TerminalNode Space(int i) {
      return getToken(CSS3Parser.Space, i);
    }

    public List<TerminalNode> Or() {
      return getTokens(CSS3Parser.Or);
    }

    public TerminalNode Or(int i) {
      return getToken(CSS3Parser.Or, i);
    }

    public SupportsDisjunctionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsDisjunction;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterSupportsDisjunction(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitSupportsDisjunction(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsDisjunction(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsDisjunctionContext supportsDisjunction() throws RecognitionException {
    SupportsDisjunctionContext _localctx = new SupportsDisjunctionContext(_ctx, getState());
    enterRule(_localctx, 112, RULE_supportsDisjunction);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(821);
        supportsConditionInParens();
        setState(831);
        _errHandler.sync(this);
        _alt = 1;
        do {
          switch (_alt) {
            case 1:
              {
                {
                  setState(822);
                  ws();
                  setState(823);
                  match(Space);
                  setState(824);
                  ws();
                  setState(825);
                  match(Or);
                  setState(826);
                  ws();
                  setState(827);
                  match(Space);
                  setState(828);
                  ws();
                  setState(829);
                  supportsConditionInParens();
                }
              }
              break;
            default:
              throw new NoViableAltException(this);
          }
          setState(833);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 77, _ctx);
        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class SupportsDeclarationConditionContext extends ParserRuleContext {
    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public DeclarationContext declaration() {
      return getRuleContext(DeclarationContext.class, 0);
    }

    public SupportsDeclarationConditionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_supportsDeclarationCondition;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterSupportsDeclarationCondition(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitSupportsDeclarationCondition(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitSupportsDeclarationCondition(this);
      else return visitor.visitChildren(this);
    }
  }

  public final SupportsDeclarationConditionContext supportsDeclarationCondition()
      throws RecognitionException {
    SupportsDeclarationConditionContext _localctx =
        new SupportsDeclarationConditionContext(_ctx, getState());
    enterRule(_localctx, 114, RULE_supportsDeclarationCondition);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(835);
        match(T__1);
        setState(836);
        ws();
        setState(837);
        declaration();
        setState(838);
        match(T__3);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class GeneralEnclosedContext extends ParserRuleContext {
    public TerminalNode Function() {
      return getToken(CSS3Parser.Function, 0);
    }

    public List<AnyContext> any() {
      return getRuleContexts(AnyContext.class);
    }

    public AnyContext any(int i) {
      return getRuleContext(AnyContext.class, i);
    }

    public List<UnusedContext> unused() {
      return getRuleContexts(UnusedContext.class);
    }

    public UnusedContext unused(int i) {
      return getRuleContext(UnusedContext.class, i);
    }

    public GeneralEnclosedContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_generalEnclosed;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterGeneralEnclosed(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitGeneralEnclosed(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitGeneralEnclosed(this);
      else return visitor.visitChildren(this);
    }
  }

  public final GeneralEnclosedContext generalEnclosed() throws RecognitionException {
    GeneralEnclosedContext _localctx = new GeneralEnclosedContext(_ctx, getState());
    enterRule(_localctx, 116, RULE_generalEnclosed);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(840);
        _la = _input.LA(1);
        if (!(_la == T__1 || _la == Function)) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(845);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__0)
                        | (1L << T__1)
                        | (1L << T__2)
                        | (1L << T__4)
                        | (1L << T__9)
                        | (1L << T__14)
                        | (1L << Cdo)
                        | (1L << Cdc)
                        | (1L << Includes)
                        | (1L << DashMatch)
                        | (1L << Hash)
                        | (1L << Percentage)
                        | (1L << Uri)
                        | (1L << UnicodeRange)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Dimension)
                        | (1L << UnknownDimension)
                        | (1L << Plus)
                        | (1L << Minus)
                        | (1L << Number)
                        | (1L << String)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Ident)
                        | (1L << Function)))
                != 0)) {
          {
            setState(843);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
              case T__1:
              case T__2:
              case T__9:
              case Includes:
              case DashMatch:
              case Hash:
              case Percentage:
              case Uri:
              case UnicodeRange:
              case MediaOnly:
              case Not:
              case And:
              case Dimension:
              case UnknownDimension:
              case Plus:
              case Minus:
              case Number:
              case String:
              case Or:
              case From:
              case To:
              case Ident:
              case Function:
                {
                  setState(841);
                  any();
                }
                break;
              case T__0:
              case T__4:
              case T__14:
              case Cdo:
              case Cdc:
                {
                  setState(842);
                  unused();
                }
                break;
              default:
                throw new NoViableAltException(this);
            }
          }
          setState(847);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(848);
        match(T__3);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class VarContext extends ParserRuleContext {
    public TerminalNode Var() {
      return getToken(CSS3Parser.Var, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode Variable() {
      return getToken(CSS3Parser.Variable, 0);
    }

    public VarContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_var;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterVar(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitVar(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitVar(this);
      else return visitor.visitChildren(this);
    }
  }

  public final VarContext var() throws RecognitionException {
    VarContext _localctx = new VarContext(_ctx, getState());
    enterRule(_localctx, 118, RULE_var);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(850);
        match(Var);
        setState(851);
        ws();
        setState(852);
        match(Variable);
        setState(853);
        ws();
        setState(854);
        match(T__3);
        setState(855);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CalcContext extends ParserRuleContext {
    public TerminalNode Calc() {
      return getToken(CSS3Parser.Calc, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public CalcSumContext calcSum() {
      return getRuleContext(CalcSumContext.class, 0);
    }

    public CalcContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_calc;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterCalc(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitCalc(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitCalc(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CalcContext calc() throws RecognitionException {
    CalcContext _localctx = new CalcContext(_ctx, getState());
    enterRule(_localctx, 120, RULE_calc);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(857);
        match(Calc);
        setState(858);
        ws();
        setState(859);
        calcSum();
        setState(860);
        match(T__3);
        setState(861);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CalcSumContext extends ParserRuleContext {
    public List<CalcProductContext> calcProduct() {
      return getRuleContexts(CalcProductContext.class);
    }

    public CalcProductContext calcProduct(int i) {
      return getRuleContext(CalcProductContext.class, i);
    }

    public List<TerminalNode> Space() {
      return getTokens(CSS3Parser.Space);
    }

    public TerminalNode Space(int i) {
      return getToken(CSS3Parser.Space, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<TerminalNode> Plus() {
      return getTokens(CSS3Parser.Plus);
    }

    public TerminalNode Plus(int i) {
      return getToken(CSS3Parser.Plus, i);
    }

    public List<TerminalNode> Minus() {
      return getTokens(CSS3Parser.Minus);
    }

    public TerminalNode Minus(int i) {
      return getToken(CSS3Parser.Minus, i);
    }

    public CalcSumContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_calcSum;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterCalcSum(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitCalcSum(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitCalcSum(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CalcSumContext calcSum() throws RecognitionException {
    CalcSumContext _localctx = new CalcSumContext(_ctx, getState());
    enterRule(_localctx, 122, RULE_calcSum);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(863);
        calcProduct();
        setState(874);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == Space) {
          {
            {
              setState(864);
              match(Space);
              setState(865);
              ws();
              setState(866);
              _la = _input.LA(1);
              if (!(_la == Plus || _la == Minus)) {
                _errHandler.recoverInline(this);
              } else {
                if (_input.LA(1) == Token.EOF) matchedEOF = true;
                _errHandler.reportMatch(this);
                consume();
              }
              setState(867);
              ws();
              setState(868);
              match(Space);
              setState(869);
              ws();
              setState(870);
              calcProduct();
            }
          }
          setState(876);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CalcProductContext extends ParserRuleContext {
    public List<CalcValueContext> calcValue() {
      return getRuleContexts(CalcValueContext.class);
    }

    public CalcValueContext calcValue(int i) {
      return getRuleContext(CalcValueContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<NumberContext> number() {
      return getRuleContexts(NumberContext.class);
    }

    public NumberContext number(int i) {
      return getRuleContext(NumberContext.class, i);
    }

    public CalcProductContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_calcProduct;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterCalcProduct(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitCalcProduct(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitCalcProduct(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CalcProductContext calcProduct() throws RecognitionException {
    CalcProductContext _localctx = new CalcProductContext(_ctx, getState());
    enterRule(_localctx, 124, RULE_calcProduct);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(877);
        calcValue();
        setState(889);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__6 || _la == T__12) {
          {
            setState(887);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
              case T__6:
                {
                  setState(878);
                  match(T__6);
                  setState(879);
                  ws();
                  setState(880);
                  calcValue();
                }
                break;
              case T__12:
                {
                  setState(882);
                  match(T__12);
                  setState(883);
                  ws();
                  setState(884);
                  number();
                  setState(885);
                  ws();
                }
                break;
              default:
                throw new NoViableAltException(this);
            }
          }
          setState(891);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CalcValueContext extends ParserRuleContext {
    public NumberContext number() {
      return getRuleContext(NumberContext.class, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public DimensionContext dimension() {
      return getRuleContext(DimensionContext.class, 0);
    }

    public UnknownDimensionContext unknownDimension() {
      return getRuleContext(UnknownDimensionContext.class, 0);
    }

    public PercentageContext percentage() {
      return getRuleContext(PercentageContext.class, 0);
    }

    public CalcSumContext calcSum() {
      return getRuleContext(CalcSumContext.class, 0);
    }

    public CalcValueContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_calcValue;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterCalcValue(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitCalcValue(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitCalcValue(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CalcValueContext calcValue() throws RecognitionException {
    CalcValueContext _localctx = new CalcValueContext(_ctx, getState());
    enterRule(_localctx, 126, RULE_calcValue);
    try {
      setState(910);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 83, _ctx)) {
        case 1:
          enterOuterAlt(_localctx, 1);
          {
            setState(892);
            number();
            setState(893);
            ws();
          }
          break;
        case 2:
          enterOuterAlt(_localctx, 2);
          {
            setState(895);
            dimension();
            setState(896);
            ws();
          }
          break;
        case 3:
          enterOuterAlt(_localctx, 3);
          {
            setState(898);
            unknownDimension();
            setState(899);
            ws();
          }
          break;
        case 4:
          enterOuterAlt(_localctx, 4);
          {
            setState(901);
            percentage();
            setState(902);
            ws();
          }
          break;
        case 5:
          enterOuterAlt(_localctx, 5);
          {
            setState(904);
            match(T__1);
            setState(905);
            ws();
            setState(906);
            calcSum();
            setState(907);
            match(T__3);
            setState(908);
            ws();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FontFaceRuleContext extends ParserRuleContext {
    public TerminalNode FontFace() {
      return getToken(CSS3Parser.FontFace, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<FontFaceDeclarationContext> fontFaceDeclaration() {
      return getRuleContexts(FontFaceDeclarationContext.class);
    }

    public FontFaceDeclarationContext fontFaceDeclaration(int i) {
      return getRuleContext(FontFaceDeclarationContext.class, i);
    }

    public FontFaceRuleContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fontFaceRule;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFontFaceRule(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFontFaceRule(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFontFaceRule(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FontFaceRuleContext fontFaceRule() throws RecognitionException {
    FontFaceRuleContext _localctx = new FontFaceRuleContext(_ctx, getState());
    enterRule(_localctx, 128, RULE_fontFaceRule);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(912);
        match(FontFace);
        setState(913);
        ws();
        setState(914);
        match(T__4);
        setState(915);
        ws();
        setState(917);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__6)
                        | (1L << T__13)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Variable)
                        | (1L << Ident)))
                != 0)) {
          {
            setState(916);
            fontFaceDeclaration();
          }
        }

        setState(926);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__0) {
          {
            {
              setState(919);
              match(T__0);
              setState(920);
              ws();
              setState(922);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if ((((_la) & ~0x3f) == 0
                  && ((1L << _la)
                          & ((1L << T__6)
                              | (1L << T__13)
                              | (1L << MediaOnly)
                              | (1L << Not)
                              | (1L << And)
                              | (1L << Or)
                              | (1L << From)
                              | (1L << To)
                              | (1L << Variable)
                              | (1L << Ident)))
                      != 0)) {
                {
                  setState(921);
                  fontFaceDeclaration();
                }
              }
            }
          }
          setState(928);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(929);
        match(T__5);
        setState(930);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FontFaceDeclarationContext extends ParserRuleContext {
    public FontFaceDeclarationContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fontFaceDeclaration;
    }

    public FontFaceDeclarationContext() {}

    public void copyFrom(FontFaceDeclarationContext ctx) {
      super.copyFrom(ctx);
    }
  }

  public static class KnownFontFaceDeclarationContext extends FontFaceDeclarationContext {
    public PropertyContext property() {
      return getRuleContext(PropertyContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public ExprContext expr() {
      return getRuleContext(ExprContext.class, 0);
    }

    public KnownFontFaceDeclarationContext(FontFaceDeclarationContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterKnownFontFaceDeclaration(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitKnownFontFaceDeclaration(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKnownFontFaceDeclaration(this);
      else return visitor.visitChildren(this);
    }
  }

  public static class UnknownFontFaceDeclarationContext extends FontFaceDeclarationContext {
    public PropertyContext property() {
      return getRuleContext(PropertyContext.class, 0);
    }

    public WsContext ws() {
      return getRuleContext(WsContext.class, 0);
    }

    public ValueContext value() {
      return getRuleContext(ValueContext.class, 0);
    }

    public UnknownFontFaceDeclarationContext(FontFaceDeclarationContext ctx) {
      copyFrom(ctx);
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterUnknownFontFaceDeclaration(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitUnknownFontFaceDeclaration(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitUnknownFontFaceDeclaration(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FontFaceDeclarationContext fontFaceDeclaration() throws RecognitionException {
    FontFaceDeclarationContext _localctx = new FontFaceDeclarationContext(_ctx, getState());
    enterRule(_localctx, 130, RULE_fontFaceDeclaration);
    try {
      setState(942);
      _errHandler.sync(this);
      switch (getInterpreter().adaptivePredict(_input, 87, _ctx)) {
        case 1:
          _localctx = new KnownFontFaceDeclarationContext(_localctx);
          enterOuterAlt(_localctx, 1);
          {
            setState(932);
            property();
            setState(933);
            match(T__2);
            setState(934);
            ws();
            setState(935);
            expr();
          }
          break;
        case 2:
          _localctx = new UnknownFontFaceDeclarationContext(_localctx);
          enterOuterAlt(_localctx, 2);
          {
            setState(937);
            property();
            setState(938);
            match(T__2);
            setState(939);
            ws();
            setState(940);
            value();
          }
          break;
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class KeyframesRuleContext extends ParserRuleContext {
    public TerminalNode Keyframes() {
      return getToken(CSS3Parser.Keyframes, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public TerminalNode Space() {
      return getToken(CSS3Parser.Space, 0);
    }

    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public KeyframesBlocksContext keyframesBlocks() {
      return getRuleContext(KeyframesBlocksContext.class, 0);
    }

    public KeyframesRuleContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_keyframesRule;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterKeyframesRule(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitKeyframesRule(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKeyframesRule(this);
      else return visitor.visitChildren(this);
    }
  }

  public final KeyframesRuleContext keyframesRule() throws RecognitionException {
    KeyframesRuleContext _localctx = new KeyframesRuleContext(_ctx, getState());
    enterRule(_localctx, 132, RULE_keyframesRule);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(944);
        match(Keyframes);
        setState(945);
        ws();
        setState(946);
        match(Space);
        setState(947);
        ws();
        setState(948);
        ident();
        setState(949);
        ws();
        setState(950);
        match(T__4);
        setState(951);
        ws();
        setState(952);
        keyframesBlocks();
        setState(953);
        match(T__5);
        setState(954);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class KeyframesBlocksContext extends ParserRuleContext {
    public List<KeyframeSelectorContext> keyframeSelector() {
      return getRuleContexts(KeyframeSelectorContext.class);
    }

    public KeyframeSelectorContext keyframeSelector(int i) {
      return getRuleContext(KeyframeSelectorContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<DeclarationListContext> declarationList() {
      return getRuleContexts(DeclarationListContext.class);
    }

    public DeclarationListContext declarationList(int i) {
      return getRuleContext(DeclarationListContext.class, i);
    }

    public KeyframesBlocksContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_keyframesBlocks;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterKeyframesBlocks(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitKeyframesBlocks(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKeyframesBlocks(this);
      else return visitor.visitChildren(this);
    }
  }

  public final KeyframesBlocksContext keyframesBlocks() throws RecognitionException {
    KeyframesBlocksContext _localctx = new KeyframesBlocksContext(_ctx, getState());
    enterRule(_localctx, 134, RULE_keyframesBlocks);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(967);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << Percentage) | (1L << From) | (1L << To))) != 0)) {
          {
            {
              setState(956);
              keyframeSelector();
              setState(957);
              match(T__4);
              setState(958);
              ws();
              setState(960);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if ((((_la) & ~0x3f) == 0
                  && ((1L << _la)
                          & ((1L << T__0)
                              | (1L << T__6)
                              | (1L << T__13)
                              | (1L << MediaOnly)
                              | (1L << Not)
                              | (1L << And)
                              | (1L << Or)
                              | (1L << From)
                              | (1L << To)
                              | (1L << Variable)
                              | (1L << Ident)))
                      != 0)) {
                {
                  setState(959);
                  declarationList();
                }
              }

              setState(962);
              match(T__5);
              setState(963);
              ws();
            }
          }
          setState(969);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class KeyframeSelectorContext extends ParserRuleContext {
    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<TerminalNode> From() {
      return getTokens(CSS3Parser.From);
    }

    public TerminalNode From(int i) {
      return getToken(CSS3Parser.From, i);
    }

    public List<TerminalNode> To() {
      return getTokens(CSS3Parser.To);
    }

    public TerminalNode To(int i) {
      return getToken(CSS3Parser.To, i);
    }

    public List<TerminalNode> Percentage() {
      return getTokens(CSS3Parser.Percentage);
    }

    public TerminalNode Percentage(int i) {
      return getToken(CSS3Parser.Percentage, i);
    }

    public List<TerminalNode> Comma() {
      return getTokens(CSS3Parser.Comma);
    }

    public TerminalNode Comma(int i) {
      return getToken(CSS3Parser.Comma, i);
    }

    public KeyframeSelectorContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_keyframeSelector;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterKeyframeSelector(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitKeyframeSelector(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitKeyframeSelector(this);
      else return visitor.visitChildren(this);
    }
  }

  public final KeyframeSelectorContext keyframeSelector() throws RecognitionException {
    KeyframeSelectorContext _localctx = new KeyframeSelectorContext(_ctx, getState());
    enterRule(_localctx, 136, RULE_keyframeSelector);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(970);
        _la = _input.LA(1);
        if (!((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << Percentage) | (1L << From) | (1L << To))) != 0))) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
        setState(971);
        ws();
        setState(979);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == Comma) {
          {
            {
              setState(972);
              match(Comma);
              setState(973);
              ws();
              setState(974);
              _la = _input.LA(1);
              if (!((((_la) & ~0x3f) == 0
                  && ((1L << _la) & ((1L << Percentage) | (1L << From) | (1L << To))) != 0))) {
                _errHandler.recoverInline(this);
              } else {
                if (_input.LA(1) == Token.EOF) matchedEOF = true;
                _errHandler.reportMatch(this);
                consume();
              }
              setState(975);
              ws();
            }
          }
          setState(981);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class ViewportContext extends ParserRuleContext {
    public TerminalNode Viewport() {
      return getToken(CSS3Parser.Viewport, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public DeclarationListContext declarationList() {
      return getRuleContext(DeclarationListContext.class, 0);
    }

    public ViewportContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_viewport;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterViewport(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitViewport(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitViewport(this);
      else return visitor.visitChildren(this);
    }
  }

  public final ViewportContext viewport() throws RecognitionException {
    ViewportContext _localctx = new ViewportContext(_ctx, getState());
    enterRule(_localctx, 138, RULE_viewport);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(982);
        match(Viewport);
        setState(983);
        ws();
        setState(984);
        match(T__4);
        setState(985);
        ws();
        setState(987);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__0)
                        | (1L << T__6)
                        | (1L << T__13)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Variable)
                        | (1L << Ident)))
                != 0)) {
          {
            setState(986);
            declarationList();
          }
        }

        setState(989);
        match(T__5);
        setState(990);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class CounterStyleContext extends ParserRuleContext {
    public TerminalNode CounterStyle() {
      return getToken(CSS3Parser.CounterStyle, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public DeclarationListContext declarationList() {
      return getRuleContext(DeclarationListContext.class, 0);
    }

    public CounterStyleContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_counterStyle;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterCounterStyle(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitCounterStyle(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitCounterStyle(this);
      else return visitor.visitChildren(this);
    }
  }

  public final CounterStyleContext counterStyle() throws RecognitionException {
    CounterStyleContext _localctx = new CounterStyleContext(_ctx, getState());
    enterRule(_localctx, 140, RULE_counterStyle);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(992);
        match(CounterStyle);
        setState(993);
        ws();
        setState(994);
        ident();
        setState(995);
        ws();
        setState(996);
        match(T__4);
        setState(997);
        ws();
        setState(999);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << T__0)
                        | (1L << T__6)
                        | (1L << T__13)
                        | (1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Variable)
                        | (1L << Ident)))
                != 0)) {
          {
            setState(998);
            declarationList();
          }
        }

        setState(1001);
        match(T__5);
        setState(1002);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FontFeatureValuesRuleContext extends ParserRuleContext {
    public TerminalNode FontFeatureValues() {
      return getToken(CSS3Parser.FontFeatureValues, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public FontFamilyNameListContext fontFamilyNameList() {
      return getRuleContext(FontFamilyNameListContext.class, 0);
    }

    public List<FeatureValueBlockContext> featureValueBlock() {
      return getRuleContexts(FeatureValueBlockContext.class);
    }

    public FeatureValueBlockContext featureValueBlock(int i) {
      return getRuleContext(FeatureValueBlockContext.class, i);
    }

    public FontFeatureValuesRuleContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fontFeatureValuesRule;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterFontFeatureValuesRule(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitFontFeatureValuesRule(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFontFeatureValuesRule(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FontFeatureValuesRuleContext fontFeatureValuesRule() throws RecognitionException {
    FontFeatureValuesRuleContext _localctx = new FontFeatureValuesRuleContext(_ctx, getState());
    enterRule(_localctx, 142, RULE_fontFeatureValuesRule);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(1004);
        match(FontFeatureValues);
        setState(1005);
        ws();
        setState(1006);
        fontFamilyNameList();
        setState(1007);
        ws();
        setState(1008);
        match(T__4);
        setState(1009);
        ws();
        setState(1013);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while (_la == T__14) {
          {
            {
              setState(1010);
              featureValueBlock();
            }
          }
          setState(1015);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(1016);
        match(T__5);
        setState(1017);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FontFamilyNameListContext extends ParserRuleContext {
    public List<FontFamilyNameContext> fontFamilyName() {
      return getRuleContexts(FontFamilyNameContext.class);
    }

    public FontFamilyNameContext fontFamilyName(int i) {
      return getRuleContext(FontFamilyNameContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<TerminalNode> Comma() {
      return getTokens(CSS3Parser.Comma);
    }

    public TerminalNode Comma(int i) {
      return getToken(CSS3Parser.Comma, i);
    }

    public FontFamilyNameListContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fontFamilyNameList;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFontFamilyNameList(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFontFamilyNameList(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFontFamilyNameList(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FontFamilyNameListContext fontFamilyNameList() throws RecognitionException {
    FontFamilyNameListContext _localctx = new FontFamilyNameListContext(_ctx, getState());
    enterRule(_localctx, 144, RULE_fontFamilyNameList);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(1019);
        fontFamilyName();
        setState(1027);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 94, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(1020);
                ws();
                setState(1021);
                match(Comma);
                setState(1022);
                ws();
                setState(1023);
                fontFamilyName();
              }
            }
          }
          setState(1029);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 94, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FontFamilyNameContext extends ParserRuleContext {
    public TerminalNode String() {
      return getToken(CSS3Parser.String, 0);
    }

    public List<IdentContext> ident() {
      return getRuleContexts(IdentContext.class);
    }

    public IdentContext ident(int i) {
      return getRuleContext(IdentContext.class, i);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public FontFamilyNameContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_fontFamilyName;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFontFamilyName(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFontFamilyName(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFontFamilyName(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FontFamilyNameContext fontFamilyName() throws RecognitionException {
    FontFamilyNameContext _localctx = new FontFamilyNameContext(_ctx, getState());
    enterRule(_localctx, 146, RULE_fontFamilyName);
    try {
      int _alt;
      setState(1040);
      _errHandler.sync(this);
      switch (_input.LA(1)) {
        case String:
          enterOuterAlt(_localctx, 1);
          {
            setState(1030);
            match(String);
          }
          break;
        case MediaOnly:
        case Not:
        case And:
        case Or:
        case From:
        case To:
        case Ident:
          enterOuterAlt(_localctx, 2);
          {
            setState(1031);
            ident();
            setState(1037);
            _errHandler.sync(this);
            _alt = getInterpreter().adaptivePredict(_input, 95, _ctx);
            while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
              if (_alt == 1) {
                {
                  {
                    setState(1032);
                    ws();
                    setState(1033);
                    ident();
                  }
                }
              }
              setState(1039);
              _errHandler.sync(this);
              _alt = getInterpreter().adaptivePredict(_input, 95, _ctx);
            }
          }
          break;
        default:
          throw new NoViableAltException(this);
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FeatureValueBlockContext extends ParserRuleContext {
    public FeatureTypeContext featureType() {
      return getRuleContext(FeatureTypeContext.class, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<FeatureValueDefinitionContext> featureValueDefinition() {
      return getRuleContexts(FeatureValueDefinitionContext.class);
    }

    public FeatureValueDefinitionContext featureValueDefinition(int i) {
      return getRuleContext(FeatureValueDefinitionContext.class, i);
    }

    public FeatureValueBlockContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_featureValueBlock;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFeatureValueBlock(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFeatureValueBlock(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFeatureValueBlock(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FeatureValueBlockContext featureValueBlock() throws RecognitionException {
    FeatureValueBlockContext _localctx = new FeatureValueBlockContext(_ctx, getState());
    enterRule(_localctx, 148, RULE_featureValueBlock);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(1042);
        featureType();
        setState(1043);
        ws();
        setState(1044);
        match(T__4);
        setState(1045);
        ws();
        setState(1047);
        _errHandler.sync(this);
        _la = _input.LA(1);
        if ((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Ident)))
                != 0)) {
          {
            setState(1046);
            featureValueDefinition();
          }
        }

        setState(1057);
        _errHandler.sync(this);
        _la = _input.LA(1);
        while ((((_la) & ~0x3f) == 0
            && ((1L << _la) & ((1L << T__0) | (1L << Comment) | (1L << Space))) != 0)) {
          {
            {
              setState(1049);
              ws();
              setState(1050);
              match(T__0);
              setState(1051);
              ws();
              setState(1053);
              _errHandler.sync(this);
              _la = _input.LA(1);
              if ((((_la) & ~0x3f) == 0
                  && ((1L << _la)
                          & ((1L << MediaOnly)
                              | (1L << Not)
                              | (1L << And)
                              | (1L << Or)
                              | (1L << From)
                              | (1L << To)
                              | (1L << Ident)))
                      != 0)) {
                {
                  setState(1052);
                  featureValueDefinition();
                }
              }
            }
          }
          setState(1059);
          _errHandler.sync(this);
          _la = _input.LA(1);
        }
        setState(1060);
        match(T__5);
        setState(1061);
        ws();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FeatureTypeContext extends ParserRuleContext {
    public AtKeywordContext atKeyword() {
      return getRuleContext(AtKeywordContext.class, 0);
    }

    public FeatureTypeContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_featureType;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterFeatureType(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitFeatureType(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFeatureType(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FeatureTypeContext featureType() throws RecognitionException {
    FeatureTypeContext _localctx = new FeatureTypeContext(_ctx, getState());
    enterRule(_localctx, 150, RULE_featureType);
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(1063);
        atKeyword();
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class FeatureValueDefinitionContext extends ParserRuleContext {
    public IdentContext ident() {
      return getRuleContext(IdentContext.class, 0);
    }

    public List<WsContext> ws() {
      return getRuleContexts(WsContext.class);
    }

    public WsContext ws(int i) {
      return getRuleContext(WsContext.class, i);
    }

    public List<NumberContext> number() {
      return getRuleContexts(NumberContext.class);
    }

    public NumberContext number(int i) {
      return getRuleContext(NumberContext.class, i);
    }

    public FeatureValueDefinitionContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_featureValueDefinition;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).enterFeatureValueDefinition(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener)
        ((CSS3Listener) listener).exitFeatureValueDefinition(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitFeatureValueDefinition(this);
      else return visitor.visitChildren(this);
    }
  }

  public final FeatureValueDefinitionContext featureValueDefinition() throws RecognitionException {
    FeatureValueDefinitionContext _localctx = new FeatureValueDefinitionContext(_ctx, getState());
    enterRule(_localctx, 152, RULE_featureValueDefinition);
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(1065);
        ident();
        setState(1066);
        ws();
        setState(1067);
        match(T__2);
        setState(1068);
        ws();
        setState(1069);
        number();
        setState(1075);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 100, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(1070);
                ws();
                setState(1071);
                number();
              }
            }
          }
          setState(1077);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 100, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class IdentContext extends ParserRuleContext {
    public TerminalNode Ident() {
      return getToken(CSS3Parser.Ident, 0);
    }

    public TerminalNode MediaOnly() {
      return getToken(CSS3Parser.MediaOnly, 0);
    }

    public TerminalNode Not() {
      return getToken(CSS3Parser.Not, 0);
    }

    public TerminalNode And() {
      return getToken(CSS3Parser.And, 0);
    }

    public TerminalNode Or() {
      return getToken(CSS3Parser.Or, 0);
    }

    public TerminalNode From() {
      return getToken(CSS3Parser.From, 0);
    }

    public TerminalNode To() {
      return getToken(CSS3Parser.To, 0);
    }

    public IdentContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_ident;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterIdent(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitIdent(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor)
        return ((CSS3Visitor<? extends T>) visitor).visitIdent(this);
      else return visitor.visitChildren(this);
    }
  }

  public final IdentContext ident() throws RecognitionException {
    IdentContext _localctx = new IdentContext(_ctx, getState());
    enterRule(_localctx, 154, RULE_ident);
    int _la;
    try {
      enterOuterAlt(_localctx, 1);
      {
        setState(1078);
        _la = _input.LA(1);
        if (!((((_la) & ~0x3f) == 0
            && ((1L << _la)
                    & ((1L << MediaOnly)
                        | (1L << Not)
                        | (1L << And)
                        | (1L << Or)
                        | (1L << From)
                        | (1L << To)
                        | (1L << Ident)))
                != 0))) {
          _errHandler.recoverInline(this);
        } else {
          if (_input.LA(1) == Token.EOF) matchedEOF = true;
          _errHandler.reportMatch(this);
          consume();
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static class WsContext extends ParserRuleContext {
    public List<TerminalNode> Comment() {
      return getTokens(CSS3Parser.Comment);
    }

    public TerminalNode Comment(int i) {
      return getToken(CSS3Parser.Comment, i);
    }

    public List<TerminalNode> Space() {
      return getTokens(CSS3Parser.Space);
    }

    public TerminalNode Space(int i) {
      return getToken(CSS3Parser.Space, i);
    }

    public WsContext(ParserRuleContext parent, int invokingState) {
      super(parent, invokingState);
    }

    @Override
    public int getRuleIndex() {
      return RULE_ws;
    }

    @Override
    public void enterRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).enterWs(this);
    }

    @Override
    public void exitRule(ParseTreeListener listener) {
      if (listener instanceof CSS3Listener) ((CSS3Listener) listener).exitWs(this);
    }

    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
      if (visitor instanceof CSS3Visitor) return ((CSS3Visitor<? extends T>) visitor).visitWs(this);
      else return visitor.visitChildren(this);
    }
  }

  public final WsContext ws() throws RecognitionException {
    WsContext _localctx = new WsContext(_ctx, getState());
    enterRule(_localctx, 156, RULE_ws);
    int _la;
    try {
      int _alt;
      enterOuterAlt(_localctx, 1);
      {
        setState(1083);
        _errHandler.sync(this);
        _alt = getInterpreter().adaptivePredict(_input, 101, _ctx);
        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
          if (_alt == 1) {
            {
              {
                setState(1080);
                _la = _input.LA(1);
                if (!(_la == Comment || _la == Space)) {
                  _errHandler.recoverInline(this);
                } else {
                  if (_input.LA(1) == Token.EOF) matchedEOF = true;
                  _errHandler.reportMatch(this);
                  consume();
                }
              }
            }
          }
          setState(1085);
          _errHandler.sync(this);
          _alt = getInterpreter().adaptivePredict(_input, 101, _ctx);
        }
      }
    } catch (RecognitionException re) {
      _localctx.exception = re;
      _errHandler.reportError(this, re);
      _errHandler.recover(this, re);
    } finally {
      exitRule();
    }
    return _localctx;
  }

  public static final String _serializedATN =
      "\u0004\u0001>\u043f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"
          + "\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"
          + "\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"
          + "\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"
          + "\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"
          + "\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"
          + "\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"
          + "\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"
          + "\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"
          + "\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"
          + "\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"
          + "#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"
          + "(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"
          + "-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"
          + "2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"
          + "7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"
          + "<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"
          + "A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007E\u0002"
          + "F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007J\u0002"
          + "K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0001\u0000\u0001\u0000"
          + "\u0005\u0000\u00a1\b\u0000\n\u0000\f\u0000\u00a4\t\u0000\u0005\u0000\u00a6"
          + "\b\u0000\n\u0000\f\u0000\u00a9\t\u0000\u0001\u0000\u0001\u0000\u0005\u0000"
          + "\u00ad\b\u0000\n\u0000\f\u0000\u00b0\t\u0000\u0005\u0000\u00b2\b\u0000"
          + "\n\u0000\f\u0000\u00b5\t\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"
          + "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"
          + "\u0001\u0001\u0001\u0001\u0001\u0003\u0001\u00c3\b\u0001\u0001\u0002\u0001"
          + "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"
          + "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"
          + "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"
          + "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"
          + "\u0002\u0003\u0002\u00df\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001"
          + "\u0003\u0001\u0003\u0003\u0003\u00e6\b\u0003\u0001\u0003\u0001\u0003\u0001"
          + "\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"
          + "\u0003\u0001\u0003\u0003\u0003\u00f2\b\u0003\u0001\u0003\u0001\u0003\u0001"
          + "\u0003\u0003\u0003\u00f7\b\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001"
          + "\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"
          + "\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006\u0106\b\u0006\n"
          + "\u0006\f\u0006\u0109\t\u0006\u0003\u0006\u010b\b\u0006\u0001\u0006\u0001"
          + "\u0006\u0001\u0007\u0003\u0007\u0110\b\u0007\u0001\u0007\u0001\u0007\u0001"
          + "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007\u0119"
          + "\b\u0007\n\u0007\f\u0007\u011c\t\u0007\u0001\u0007\u0001\u0007\u0001\u0007"
          + "\u0001\u0007\u0001\u0007\u0005\u0007\u0123\b\u0007\n\u0007\f\u0007\u0126"
          + "\t\u0007\u0003\u0007\u0128\b\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001"
          + "\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t\u0133\b\t\u0001\t\u0001\t\u0001"
          + "\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b"
          + "\u013e\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0143\b"
          + "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b\u0148\b\u000b\u0005"
          + "\u000b\u014a\b\u000b\n\u000b\f\u000b\u014d\t\u000b\u0001\u000b\u0001\u000b"
          + "\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001"
          + "\r\u0001\r\u0005\r\u015b\b\r\n\r\f\r\u015e\t\r\u0001\u000e\u0001\u000e"
          + "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u0166\b\u000e"
          + "\n\u000e\f\u000e\u0169\t\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001"
          + "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0003\u000f\u0173"
          + "\b\u000f\u0001\u0010\u0001\u0010\u0003\u0010\u0177\b\u0010\u0001\u0010"
          + "\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u017e\b\u0010"
          + "\n\u0010\f\u0010\u0181\t\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"
          + "\u0010\u0001\u0010\u0004\u0010\u0188\b\u0010\u000b\u0010\f\u0010\u0189"
          + "\u0003\u0010\u018c\b\u0010\u0001\u0011\u0003\u0011\u018f\b\u0011\u0001"
          + "\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0003\u0012\u0195\b\u0012\u0001"
          + "\u0012\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0003\u0014\u019c"
          + "\b\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001"
          + "\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01a6\b\u0016\u0001\u0016\u0001"
          + "\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01ae"
          + "\b\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u01b2\b\u0016\u0001\u0016"
          + "\u0001\u0016\u0001\u0017\u0001\u0017\u0003\u0017\u01b8\b\u0017\u0001\u0017"
          + "\u0001\u0017\u0003\u0017\u01bc\b\u0017\u0001\u0018\u0001\u0018\u0001\u0018"
          + "\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"
          + "\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u01ca\b\u0019\u0001\u0019"
          + "\u0004\u0019\u01cd\b\u0019\u000b\u0019\f\u0019\u01ce\u0001\u001a\u0001"
          + "\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001b\u0001"
          + "\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0003\u001b\u01dd"
          + "\b\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001"
          + "\u001c\u0001\u001c\u0001\u001c\u0003\u001c\u01e7\b\u001c\u0001\u001d\u0001"
          + "\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d\u0001"
          + "\u001d\u0001\u001d\u0003\u001d\u01f2\b\u001d\u0001\u001e\u0001\u001e\u0001"
          + "\u001e\u0001\u001e\u0003\u001e\u01f8\b\u001e\u0001\u001e\u0001\u001e\u0001"
          + "\u001e\u0001\u001e\u0005\u001e\u01fe\b\u001e\n\u001e\f\u001e\u0201\t\u001e"
          + "\u0001\u001e\u0001\u001e\u0001\u001e\u0003\u001e\u0206\b\u001e\u0001\u001e"
          + "\u0001\u001e\u0001\u001e\u0003\u001e\u020b\b\u001e\u0001\u001f\u0001\u001f"
          + "\u0005\u001f\u020f\b\u001f\n\u001f\f\u001f\u0212\t\u001f\u0001\u001f\u0001"
          + "\u001f\u0001\u001f\u0001\u001f\u0001\u001f\u0003\u001f\u0219\b\u001f\u0005"
          + "\u001f\u021b\b\u001f\n\u001f\f\u001f\u021e\t\u001f\u0001 \u0001 \u0001"
          + " \u0001 \u0001 \u0003 \u0225\b \u0001 \u0001 \u0001 \u0001 \u0001 \u0003"
          + " \u022c\b \u0001!\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0001"
          + "\"\u0004\"\u0236\b\"\u000b\"\f\"\u0237\u0001#\u0001#\u0003#\u023c\b#\u0001"
          + "#\u0005#\u023f\b#\n#\f#\u0242\t#\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"
          + "$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"
          + "$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001$\u0001"
          + "$\u0003$\u025e\b$\u0001%\u0001%\u0001%\u0001%\u0001%\u0001%\u0001&\u0001"
          + "&\u0001&\u0001&\u0001&\u0001&\u0001\'\u0001\'\u0001\'\u0001(\u0003(\u0270"
          + "\b(\u0001(\u0001(\u0001)\u0003)\u0275\b)\u0001)\u0001)\u0001*\u0003*\u027a"
          + "\b*\u0001*\u0001*\u0001+\u0003+\u027f\b+\u0001+\u0001+\u0001,\u0001,\u0001"
          + ",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"
          + ",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"
          + ",\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"
          + ",\u0005,\u02a4\b,\n,\f,\u02a7\t,\u0001,\u0001,\u0001,\u0001,\u0001,\u0001"
          + ",\u0001,\u0005,\u02b0\b,\n,\f,\u02b3\t,\u0001,\u0001,\u0001,\u0001,\u0001"
          + ",\u0001,\u0001,\u0005,\u02bc\b,\n,\f,\u02bf\t,\u0001,\u0001,\u0001,\u0003"
          + ",\u02c4\b,\u0001-\u0001-\u0001-\u0005-\u02c9\b-\n-\f-\u02cc\t-\u0001-"
          + "\u0001-\u0001-\u0003-\u02d1\b-\u0001.\u0001.\u0001.\u0001/\u0001/\u0001"
          + "/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0001/\u0003/\u02e0\b/\u0001"
          + "0\u00010\u00010\u00010\u00010\u00010\u00010\u00010\u00010\u00010\u0001"
          + "0\u00050\u02ed\b0\n0\f0\u02f0\t0\u00010\u00010\u00010\u00011\u00011\u0001"
          + "1\u00011\u00011\u00011\u00011\u00011\u00011\u00011\u00031\u02ff\b1\u0001"
          + "2\u00012\u00012\u00052\u0304\b2\n2\f2\u0307\t2\u00012\u00012\u00012\u0001"
          + "3\u00013\u00013\u00013\u00013\u00013\u00014\u00014\u00014\u00014\u0003"
          + "4\u0316\b4\u00015\u00015\u00015\u00015\u00015\u00015\u00015\u00015\u0003"
          + "5\u0320\b5\u00016\u00016\u00016\u00016\u00016\u00016\u00017\u00017\u0001"
          + "7\u00017\u00017\u00017\u00017\u00017\u00017\u00017\u00047\u0332\b7\u000b"
          + "7\f7\u0333\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u00018\u0001"
          + "8\u00018\u00048\u0340\b8\u000b8\f8\u0341\u00019\u00019\u00019\u00019\u0001"
          + "9\u0001:\u0001:\u0001:\u0005:\u034c\b:\n:\f:\u034f\t:\u0001:\u0001:\u0001"
          + ";\u0001;\u0001;\u0001;\u0001;\u0001;\u0001;\u0001<\u0001<\u0001<\u0001"
          + "<\u0001<\u0001<\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001=\u0001"
          + "=\u0001=\u0005=\u0369\b=\n=\f=\u036c\t=\u0001>\u0001>\u0001>\u0001>\u0001"
          + ">\u0001>\u0001>\u0001>\u0001>\u0001>\u0005>\u0378\b>\n>\f>\u037b\t>\u0001"
          + "?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001"
          + "?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001?\u0001?\u0003?\u038f\b?\u0001"
          + "@\u0001@\u0001@\u0001@\u0001@\u0003@\u0396\b@\u0001@\u0001@\u0001@\u0003"
          + "@\u039b\b@\u0005@\u039d\b@\n@\f@\u03a0\t@\u0001@\u0001@\u0001@\u0001A"
          + "\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0001A\u0003"
          + "A\u03af\bA\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001B\u0001"
          + "B\u0001B\u0001B\u0001B\u0001C\u0001C\u0001C\u0001C\u0003C\u03c1\bC\u0001"
          + "C\u0001C\u0001C\u0005C\u03c6\bC\nC\fC\u03c9\tC\u0001D\u0001D\u0001D\u0001"
          + "D\u0001D\u0001D\u0001D\u0005D\u03d2\bD\nD\fD\u03d5\tD\u0001E\u0001E\u0001"
          + "E\u0001E\u0001E\u0003E\u03dc\bE\u0001E\u0001E\u0001E\u0001F\u0001F\u0001"
          + "F\u0001F\u0001F\u0001F\u0001F\u0003F\u03e8\bF\u0001F\u0001F\u0001F\u0001"
          + "G\u0001G\u0001G\u0001G\u0001G\u0001G\u0001G\u0005G\u03f4\bG\nG\fG\u03f7"
          + "\tG\u0001G\u0001G\u0001G\u0001H\u0001H\u0001H\u0001H\u0001H\u0001H\u0005"
          + "H\u0402\bH\nH\fH\u0405\tH\u0001I\u0001I\u0001I\u0001I\u0001I\u0005I\u040c"
          + "\bI\nI\fI\u040f\tI\u0003I\u0411\bI\u0001J\u0001J\u0001J\u0001J\u0001J"
          + "\u0003J\u0418\bJ\u0001J\u0001J\u0001J\u0001J\u0003J\u041e\bJ\u0005J\u0420"
          + "\bJ\nJ\fJ\u0423\tJ\u0001J\u0001J\u0001J\u0001K\u0001K\u0001L\u0001L\u0001"
          + "L\u0001L\u0001L\u0001L\u0001L\u0001L\u0005L\u0432\bL\nL\fL\u0435\tL\u0001"
          + "M\u0001M\u0001N\u0005N\u043a\bN\nN\fN\u043d\tN\u0001N\u0000\u0000O\u0000"
          + "\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"
          + "\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084"
          + "\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a\u009c"
          + "\u0000\t\u0001\u0000\u0010\u0013\u0002\u0000\u001e\u001e,,\u0001\u0000"
          + " !\u0003\u0000\u000b\u000b\u0014\u0015-/\u0001\u0000%&\u0002\u0000\u0002"
          + "\u0002>>\u0002\u0000\u001d\u001d45\u0004\u0000 \"2245==\u0001\u0000\u0010"
          + "\u0011\u049b\u0000\u00a7\u0001\u0000\u0000\u0000\u0002\u00c2\u0001\u0000"
          + "\u0000\u0000\u0004\u00de\u0001\u0000\u0000\u0000\u0006\u00f6\u0001\u0000"
          + "\u0000\u0000\b\u00f8\u0001\u0000\u0000\u0000\n\u00fa\u0001\u0000\u0000"
          + "\u0000\f\u010a\u0001\u0000\u0000\u0000\u000e\u0127\u0001\u0000\u0000\u0000"
          + "\u0010\u0129\u0001\u0000\u0000\u0000\u0012\u012b\u0001\u0000\u0000\u0000"
          + "\u0014\u0137\u0001\u0000\u0000\u0000\u0016\u013a\u0001\u0000\u0000\u0000"
          + "\u0018\u0151\u0001\u0000\u0000\u0000\u001a\u0155\u0001\u0000\u0000\u0000"
          + "\u001c\u015f\u0001\u0000\u0000\u0000\u001e\u0172\u0001\u0000\u0000\u0000"
          + " \u018b\u0001\u0000\u0000\u0000\"\u018e\u0001\u0000\u0000\u0000$\u0194"
          + "\u0001\u0000\u0000\u0000&\u0198\u0001\u0000\u0000\u0000(\u019b\u0001\u0000"
          + "\u0000\u0000*\u019f\u0001\u0000\u0000\u0000,\u01a2\u0001\u0000\u0000\u0000"
          + ".\u01b5\u0001\u0000\u0000\u00000\u01bd\u0001\u0000\u0000\u00002\u01cc"
          + "\u0001\u0000\u0000\u00004\u01d0\u0001\u0000\u0000\u00006\u01dc\u0001\u0000"
          + "\u0000\u00008\u01e6\u0001\u0000\u0000\u0000:\u01f1\u0001\u0000\u0000\u0000"
          + "<\u020a\u0001\u0000\u0000\u0000>\u0210\u0001\u0000\u0000\u0000@\u022b"
          + "\u0001\u0000\u0000\u0000B\u022d\u0001\u0000\u0000\u0000D\u0235\u0001\u0000"
          + "\u0000\u0000F\u0239\u0001\u0000\u0000\u0000H\u025d\u0001\u0000\u0000\u0000"
          + "J\u025f\u0001\u0000\u0000\u0000L\u0265\u0001\u0000\u0000\u0000N\u026b"
          + "\u0001\u0000\u0000\u0000P\u026f\u0001\u0000\u0000\u0000R\u0274\u0001\u0000"
          + "\u0000\u0000T\u0279\u0001\u0000\u0000\u0000V\u027e\u0001\u0000\u0000\u0000"
          + "X\u02c3\u0001\u0000\u0000\u0000Z\u02c5\u0001\u0000\u0000\u0000\\\u02d2"
          + "\u0001\u0000\u0000\u0000^\u02df\u0001\u0000\u0000\u0000`\u02e1\u0001\u0000"
          + "\u0000\u0000b\u02fe\u0001\u0000\u0000\u0000d\u0300\u0001\u0000\u0000\u0000"
          + "f\u030b\u0001\u0000\u0000\u0000h\u0315\u0001\u0000\u0000\u0000j\u031f"
          + "\u0001\u0000\u0000\u0000l\u0321\u0001\u0000\u0000\u0000n\u0327\u0001\u0000"
          + "\u0000\u0000p\u0335\u0001\u0000\u0000\u0000r\u0343\u0001\u0000\u0000\u0000"
          + "t\u0348\u0001\u0000\u0000\u0000v\u0352\u0001\u0000\u0000\u0000x\u0359"
          + "\u0001\u0000\u0000\u0000z\u035f\u0001\u0000\u0000\u0000|\u036d\u0001\u0000"
          + "\u0000\u0000~\u038e\u0001\u0000\u0000\u0000\u0080\u0390\u0001\u0000\u0000"
          + "\u0000\u0082\u03ae\u0001\u0000\u0000\u0000\u0084\u03b0\u0001\u0000\u0000"
          + "\u0000\u0086\u03c7\u0001\u0000\u0000\u0000\u0088\u03ca\u0001\u0000\u0000"
          + "\u0000\u008a\u03d6\u0001\u0000\u0000\u0000\u008c\u03e0\u0001\u0000\u0000"
          + "\u0000\u008e\u03ec\u0001\u0000\u0000\u0000\u0090\u03fb\u0001\u0000\u0000"
          + "\u0000\u0092\u0410\u0001\u0000\u0000\u0000\u0094\u0412\u0001\u0000\u0000"
          + "\u0000\u0096\u0427\u0001\u0000\u0000\u0000\u0098\u0429\u0001\u0000\u0000"
          + "\u0000\u009a\u0436\u0001\u0000\u0000\u0000\u009c\u043b\u0001\u0000\u0000"
          + "\u0000\u009e\u00a2\u0003\u0006\u0003\u0000\u009f\u00a1\u0007\u0000\u0000"
          + "\u0000\u00a0\u009f\u0001\u0000\u0000\u0000\u00a1\u00a4\u0001\u0000\u0000"
          + "\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000"
          + "\u0000\u00a3\u00a6\u0001\u0000\u0000\u0000\u00a4\u00a2\u0001\u0000\u0000"
          + "\u0000\u00a5\u009e\u0001\u0000\u0000\u0000\u00a6\u00a9\u0001\u0000\u0000"
          + "\u0000\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a7\u00a8\u0001\u0000\u0000"
          + "\u0000\u00a8\u00b3\u0001\u0000\u0000\u0000\u00a9\u00a7\u0001\u0000\u0000"
          + "\u0000\u00aa\u00ae\u0003b1\u0000\u00ab\u00ad\u0007\u0000\u0000\u0000\u00ac"
          + "\u00ab\u0001\u0000\u0000\u0000\u00ad\u00b0\u0001\u0000\u0000\u0000\u00ae"
          + "\u00ac\u0001\u0000\u0000\u0000\u00ae\u00af\u0001\u0000\u0000\u0000\u00af"
          + "\u00b2\u0001\u0000\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b1"
          + "\u00aa\u0001\u0000\u0000\u0000\u00b2\u00b5\u0001\u0000\u0000\u0000\u00b3"
          + "\u00b1\u0001\u0000\u0000\u0000\u00b3\u00b4\u0001\u0000\u0000\u0000\u00b4"
          + "\u0001\u0001\u0000\u0000\u0000\u00b5\u00b3\u0001\u0000\u0000\u0000\u00b6"
          + "\u00b7\u0005\u001b\u0000\u0000\u00b7\u00b8\u0003\u009cN\u0000\u00b8\u00b9"
          + "\u0005,\u0000\u0000\u00b9\u00ba\u0003\u009cN\u0000\u00ba\u00bb\u0005\u0001"
          + "\u0000\u0000\u00bb\u00bc\u0003\u009cN\u0000\u00bc\u00c3\u0001\u0000\u0000"
          + "\u0000\u00bd\u00be\u0005\u001b\u0000\u0000\u00be\u00bf\u0003\u009cN\u0000"
          + "\u00bf\u00c0\u0005,\u0000\u0000\u00c0\u00c1\u0003\u009cN\u0000\u00c1\u00c3"
          + "\u0001\u0000\u0000\u0000\u00c2\u00b6\u0001\u0000\u0000\u0000\u00c2\u00bd"
          + "\u0001\u0000\u0000\u0000\u00c3\u0003\u0001\u0000\u0000\u0000\u00c4\u00c5"
          + "\u0005\u0017\u0000\u0000\u00c5\u00c6\u0003\u009cN\u0000\u00c6\u00c7\u0007"
          + "\u0001\u0000\u0000\u00c7\u00c8\u0003\u009cN\u0000\u00c8\u00c9\u0003\f"
          + "\u0006\u0000\u00c9\u00ca\u0005\u0001\u0000\u0000\u00ca\u00cb\u0003\u009c"
          + "N\u0000\u00cb\u00df\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005\u0017\u0000"
          + "\u0000\u00cd\u00ce\u0003\u009cN\u0000\u00ce\u00cf\u0007\u0001\u0000\u0000"
          + "\u00cf\u00d0\u0003\u009cN\u0000\u00d0\u00d1\u0005\u0001\u0000\u0000\u00d1"
          + "\u00d2\u0003\u009cN\u0000\u00d2\u00df\u0001\u0000\u0000\u0000\u00d3\u00d4"
          + "\u0005\u0017\u0000\u0000\u00d4\u00d5\u0003\u009cN\u0000\u00d5\u00d6\u0007"
          + "\u0001\u0000\u0000\u00d6\u00d7\u0003\u009cN\u0000\u00d7\u00d8\u0003\f"
          + "\u0006\u0000\u00d8\u00df\u0001\u0000\u0000\u0000\u00d9\u00da\u0005\u0017"
          + "\u0000\u0000\u00da\u00db\u0003\u009cN\u0000\u00db\u00dc\u0007\u0001\u0000"
          + "\u0000\u00dc\u00dd\u0003\u009cN\u0000\u00dd\u00df\u0001\u0000\u0000\u0000"
          + "\u00de\u00c4\u0001\u0000\u0000\u0000\u00de\u00cc\u0001\u0000\u0000\u0000"
          + "\u00de\u00d3\u0001\u0000\u0000\u0000\u00de\u00d9\u0001\u0000\u0000\u0000"
          + "\u00df\u0005\u0001\u0000\u0000\u0000\u00e0\u00e1\u0005\u001a\u0000\u0000"
          + "\u00e1\u00e5\u0003\u009cN\u0000\u00e2\u00e3\u0003\b\u0004\u0000\u00e3"
          + "\u00e4\u0003\u009cN\u0000\u00e4\u00e6\u0001\u0000\u0000\u0000\u00e5\u00e2"
          + "\u0001\u0000\u0000\u0000\u00e5\u00e6\u0001\u0000\u0000\u0000\u00e6\u00e7"
          + "\u0001\u0000\u0000\u0000\u00e7\u00e8\u0007\u0001\u0000\u0000\u00e8\u00e9"
          + "\u0003\u009cN\u0000\u00e9\u00ea\u0005\u0001\u0000\u0000\u00ea\u00eb\u0003"
          + "\u009cN\u0000\u00eb\u00f7\u0001\u0000\u0000\u0000\u00ec\u00ed\u0005\u001a"
          + "\u0000\u0000\u00ed\u00f1\u0003\u009cN\u0000\u00ee\u00ef\u0003\b\u0004"
          + "\u0000\u00ef\u00f0\u0003\u009cN\u0000\u00f0\u00f2\u0001\u0000\u0000\u0000"
          + "\u00f1\u00ee\u0001\u0000\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000"
          + "\u00f2\u00f3\u0001\u0000\u0000\u0000\u00f3\u00f4\u0007\u0001\u0000\u0000"
          + "\u00f4\u00f5\u0003\u009cN\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000\u00f6"
          + "\u00e0\u0001\u0000\u0000\u0000\u00f6\u00ec\u0001\u0000\u0000\u0000\u00f7"
          + "\u0007\u0001\u0000\u0000\u0000\u00f8\u00f9\u0003\u009aM\u0000\u00f9\t"
          + "\u0001\u0000\u0000\u0000\u00fa\u00fb\u0005\u0019\u0000\u0000\u00fb\u00fc"
          + "\u0003\u009cN\u0000\u00fc\u00fd\u0003\f\u0006\u0000\u00fd\u00fe\u0003"
          + "d2\u0000\u00fe\u00ff\u0003\u009cN\u0000\u00ff\u000b\u0001\u0000\u0000"
          + "\u0000\u0100\u0107\u0003\u000e\u0007\u0000\u0101\u0102\u0005(\u0000\u0000"
          + "\u0102\u0103\u0003\u009cN\u0000\u0103\u0104\u0003\u000e\u0007\u0000\u0104"
          + "\u0106\u0001\u0000\u0000\u0000\u0105\u0101\u0001\u0000\u0000\u0000\u0106"
          + "\u0109\u0001\u0000\u0000\u0000\u0107\u0105\u0001\u0000\u0000\u0000\u0107"
          + "\u0108\u0001\u0000\u0000\u0000\u0108\u010b\u0001\u0000\u0000\u0000\u0109"
          + "\u0107\u0001\u0000\u0000\u0000\u010a\u0100\u0001\u0000\u0000\u0000\u010a"
          + "\u010b\u0001\u0000\u0000\u0000\u010b\u010c\u0001\u0000\u0000\u0000\u010c"
          + "\u010d\u0003\u009cN\u0000\u010d\r\u0001\u0000\u0000\u0000\u010e\u0110"
          + "\u0007\u0002\u0000\u0000\u010f\u010e\u0001\u0000\u0000\u0000\u010f\u0110"
          + "\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111\u0112"
          + "\u0003\u009cN\u0000\u0112\u0113\u0003\u0010\b\u0000\u0113\u011a\u0003"
          + "\u009cN\u0000\u0114\u0115\u0005\"\u0000\u0000\u0115\u0116\u0003\u009c"
          + "N\u0000\u0116\u0117\u0003\u0012\t\u0000\u0117\u0119\u0001\u0000\u0000"
          + "\u0000\u0118\u0114\u0001\u0000\u0000\u0000\u0119\u011c\u0001\u0000\u0000"
          + "\u0000\u011a\u0118\u0001\u0000\u0000\u0000\u011a\u011b\u0001\u0000\u0000"
          + "\u0000\u011b\u0128\u0001\u0000\u0000\u0000\u011c\u011a\u0001\u0000\u0000"
          + "\u0000\u011d\u0124\u0003\u0012\t\u0000\u011e\u011f\u0005\"\u0000\u0000"
          + "\u011f\u0120\u0003\u009cN\u0000\u0120\u0121\u0003\u0012\t\u0000\u0121"
          + "\u0123\u0001\u0000\u0000\u0000\u0122\u011e\u0001\u0000\u0000\u0000\u0123"
          + "\u0126\u0001\u0000\u0000\u0000\u0124\u0122\u0001\u0000\u0000\u0000\u0124"
          + "\u0125\u0001\u0000\u0000\u0000\u0125\u0128\u0001\u0000\u0000\u0000\u0126"
          + "\u0124\u0001\u0000\u0000\u0000\u0127\u010f\u0001\u0000\u0000\u0000\u0127"
          + "\u011d\u0001\u0000\u0000\u0000\u0128\u000f\u0001\u0000\u0000\u0000\u0129"
          + "\u012a\u0003\u009aM\u0000\u012a\u0011\u0001\u0000\u0000\u0000\u012b\u012c"
          + "\u0005\u0002\u0000\u0000\u012c\u012d\u0003\u009cN\u0000\u012d\u0132\u0003"
          + "\u0014\n\u0000\u012e\u012f\u0005\u0003\u0000\u0000\u012f\u0130\u0003\u009c"
          + "N\u0000\u0130\u0131\u0003F#\u0000\u0131\u0133\u0001\u0000\u0000\u0000"
          + "\u0132\u012e\u0001\u0000\u0000\u0000\u0132\u0133\u0001\u0000\u0000\u0000"
          + "\u0133\u0134\u0001\u0000\u0000\u0000\u0134\u0135\u0005\u0004\u0000\u0000"
          + "\u0135\u0136\u0003\u009cN\u0000\u0136\u0013\u0001\u0000\u0000\u0000\u0137"
          + "\u0138\u0003\u009aM\u0000\u0138\u0139\u0003\u009cN\u0000\u0139\u0015\u0001"
          + "\u0000\u0000\u0000\u013a\u013b\u0005\u0018\u0000\u0000\u013b\u013d\u0003"
          + "\u009cN\u0000\u013c\u013e\u0003\u0018\f\u0000\u013d\u013c\u0001\u0000"
          + "\u0000\u0000\u013d\u013e\u0001\u0000\u0000\u0000\u013e\u013f\u0001\u0000"
          + "\u0000\u0000\u013f\u0140\u0005\u0005\u0000\u0000\u0140\u0142\u0003\u009c"
          + "N\u0000\u0141\u0143\u0003@ \u0000\u0142\u0141\u0001\u0000\u0000\u0000"
          + "\u0142\u0143\u0001\u0000\u0000\u0000\u0143\u014b\u0001\u0000\u0000\u0000"
          + "\u0144\u0145\u0005\u0001\u0000\u0000\u0145\u0147\u0003\u009cN\u0000\u0146"
          + "\u0148\u0003@ \u0000\u0147\u0146\u0001\u0000\u0000\u0000\u0147\u0148\u0001"
          + "\u0000\u0000\u0000\u0148\u014a\u0001\u0000\u0000\u0000\u0149\u0144\u0001"
          + "\u0000\u0000\u0000\u014a\u014d\u0001\u0000\u0000\u0000\u014b\u0149\u0001"
          + "\u0000\u0000\u0000\u014b\u014c\u0001\u0000\u0000\u0000\u014c\u014e\u0001"
          + "\u0000\u0000\u0000\u014d\u014b\u0001\u0000\u0000\u0000\u014e\u014f\u0005"
          + "\u0006\u0000\u0000\u014f\u0150\u0003\u009cN\u0000\u0150\u0017\u0001\u0000"
          + "\u0000\u0000\u0151\u0152\u0005\u0003\u0000\u0000\u0152\u0153\u0003\u009a"
          + "M\u0000\u0153\u0154\u0003\u009cN\u0000\u0154\u0019\u0001\u0000\u0000\u0000"
          + "\u0155\u015c\u0003\u001c\u000e\u0000\u0156\u0157\u0005(\u0000\u0000\u0157"
          + "\u0158\u0003\u009cN\u0000\u0158\u0159\u0003\u001c\u000e\u0000\u0159\u015b"
          + "\u0001\u0000\u0000\u0000\u015a\u0156\u0001\u0000\u0000\u0000\u015b\u015e"
          + "\u0001\u0000\u0000\u0000\u015c\u015a\u0001\u0000\u0000\u0000\u015c\u015d"
          + "\u0001\u0000\u0000\u0000\u015d\u001b\u0001\u0000\u0000\u0000\u015e\u015c"
          + "\u0001\u0000\u0000\u0000\u015f\u0160\u0003 \u0010\u0000\u0160\u0167\u0003"
          + "\u009cN\u0000\u0161\u0162\u0003\u001e\u000f\u0000\u0162\u0163\u0003 \u0010"
          + "\u0000\u0163\u0164\u0003\u009cN\u0000\u0164\u0166\u0001\u0000\u0000\u0000"
          + "\u0165\u0161\u0001\u0000\u0000\u0000\u0166\u0169\u0001\u0000\u0000\u0000"
          + "\u0167\u0165\u0001\u0000\u0000\u0000\u0167\u0168\u0001\u0000\u0000\u0000"
          + "\u0168\u001d\u0001\u0000\u0000\u0000\u0169\u0167\u0001\u0000\u0000\u0000"
          + "\u016a\u016b\u0005%\u0000\u0000\u016b\u0173\u0003\u009cN\u0000\u016c\u016d"
          + "\u0005\'\u0000\u0000\u016d\u0173\u0003\u009cN\u0000\u016e\u016f\u0005"
          + ")\u0000\u0000\u016f\u0173\u0003\u009cN\u0000\u0170\u0171\u0005\u0011\u0000"
          + "\u0000\u0171\u0173\u0003\u009cN\u0000\u0172\u016a\u0001\u0000\u0000\u0000"
          + "\u0172\u016c\u0001\u0000\u0000\u0000\u0172\u016e\u0001\u0000\u0000\u0000"
          + "\u0172\u0170\u0001\u0000\u0000\u0000\u0173\u001f\u0001\u0000\u0000\u0000"
          + "\u0174\u0177\u0003\"\u0011\u0000\u0175\u0177\u0003(\u0014\u0000\u0176"
          + "\u0174\u0001\u0000\u0000\u0000\u0176\u0175\u0001\u0000\u0000\u0000\u0177"
          + "\u017f\u0001\u0000\u0000\u0000\u0178\u017e\u0005\u0016\u0000\u0000\u0179"
          + "\u017e\u0003*\u0015\u0000\u017a\u017e\u0003,\u0016\u0000\u017b\u017e\u0003"
          + ".\u0017\u0000\u017c\u017e\u00034\u001a\u0000\u017d\u0178\u0001\u0000\u0000"
          + "\u0000\u017d\u0179\u0001\u0000\u0000\u0000\u017d\u017a\u0001\u0000\u0000"
          + "\u0000\u017d\u017b\u0001\u0000\u0000\u0000\u017d\u017c\u0001\u0000\u0000"
          + "\u0000\u017e\u0181\u0001\u0000\u0000\u0000\u017f\u017d\u0001\u0000\u0000"
          + "\u0000\u017f\u0180\u0001\u0000\u0000\u0000\u0180\u018c\u0001\u0000\u0000"
          + "\u0000\u0181\u017f\u0001\u0000\u0000\u0000\u0182\u0188\u0005\u0016\u0000"
          + "\u0000\u0183\u0188\u0003*\u0015\u0000\u0184\u0188\u0003,\u0016\u0000\u0185"
          + "\u0188\u0003.\u0017\u0000\u0186\u0188\u00034\u001a\u0000\u0187\u0182\u0001"
          + "\u0000\u0000\u0000\u0187\u0183\u0001\u0000\u0000\u0000\u0187\u0184\u0001"
          + "\u0000\u0000\u0000\u0187\u0185\u0001\u0000\u0000\u0000\u0187\u0186\u0001"
          + "\u0000\u0000\u0000\u0188\u0189\u0001\u0000\u0000\u0000\u0189\u0187\u0001"
          + "\u0000\u0000\u0000\u0189\u018a\u0001\u0000\u0000\u0000\u018a\u018c\u0001"
          + "\u0000\u0000\u0000\u018b\u0176\u0001\u0000\u0000\u0000\u018b\u0187\u0001"
          + "\u0000\u0000\u0000\u018c!\u0001\u0000\u0000\u0000\u018d\u018f\u0003$\u0012"
          + "\u0000\u018e\u018d\u0001\u0000\u0000\u0000\u018e\u018f\u0001\u0000\u0000"
          + "\u0000\u018f\u0190\u0001\u0000\u0000\u0000\u0190\u0191\u0003&\u0013\u0000"
          + "\u0191#\u0001\u0000\u0000\u0000\u0192\u0195\u0003\u009aM\u0000\u0193\u0195"
          + "\u0005\u0007\u0000\u0000\u0194\u0192\u0001\u0000\u0000\u0000\u0194\u0193"
          + "\u0001\u0000\u0000\u0000\u0194\u0195\u0001\u0000\u0000\u0000\u0195\u0196"
          + "\u0001\u0000\u0000\u0000\u0196\u0197\u0005\b\u0000\u0000\u0197%\u0001"
          + "\u0000\u0000\u0000\u0198\u0199\u0003\u009aM\u0000\u0199\'\u0001\u0000"
          + "\u0000\u0000\u019a\u019c\u0003$\u0012\u0000\u019b\u019a\u0001\u0000\u0000"
          + "\u0000\u019b\u019c\u0001\u0000\u0000\u0000\u019c\u019d\u0001\u0000\u0000"
          + "\u0000\u019d\u019e\u0005\u0007\u0000\u0000\u019e)\u0001\u0000\u0000\u0000"
          + "\u019f\u01a0\u0005\t\u0000\u0000\u01a0\u01a1\u0003\u009aM\u0000\u01a1"
          + "+\u0001\u0000\u0000\u0000\u01a2\u01a3\u0005\n\u0000\u0000\u01a3\u01a5"
          + "\u0003\u009cN\u0000\u01a4\u01a6\u0003$\u0012\u0000\u01a5\u01a4\u0001\u0000"
          + "\u0000\u0000\u01a5\u01a6\u0001\u0000\u0000\u0000\u01a6\u01a7\u0001\u0000"
          + "\u0000\u0000\u01a7\u01a8\u0003\u009aM\u0000\u01a8\u01b1\u0003\u009cN\u0000"
          + "\u01a9\u01aa\u0007\u0003\u0000\u0000\u01aa\u01ad\u0003\u009cN\u0000\u01ab"
          + "\u01ae\u0003\u009aM\u0000\u01ac\u01ae\u0005,\u0000\u0000\u01ad\u01ab\u0001"
          + "\u0000\u0000\u0000\u01ad\u01ac\u0001\u0000\u0000\u0000\u01ae\u01af\u0001"
          + "\u0000\u0000\u0000\u01af\u01b0\u0003\u009cN\u0000\u01b0\u01b2\u0001\u0000"
          + "\u0000\u0000\u01b1\u01a9\u0001\u0000\u0000\u0000\u01b1\u01b2\u0001\u0000"
          + "\u0000\u0000\u01b2\u01b3\u0001\u0000\u0000\u0000\u01b3\u01b4\u0005\f\u0000"
          + "\u0000\u01b4-\u0001\u0000\u0000\u0000\u01b5\u01b7\u0005\u0003\u0000\u0000"
          + "\u01b6\u01b8\u0005\u0003\u0000\u0000\u01b7\u01b6\u0001\u0000\u0000\u0000"
          + "\u01b7\u01b8\u0001\u0000\u0000\u0000\u01b8\u01bb\u0001\u0000\u0000\u0000"
          + "\u01b9\u01bc\u0003\u009aM\u0000\u01ba\u01bc\u00030\u0018\u0000\u01bb\u01b9"
          + "\u0001\u0000\u0000\u0000\u01bb\u01ba\u0001\u0000\u0000\u0000\u01bc/\u0001"
          + "\u0000\u0000\u0000\u01bd\u01be\u0005>\u0000\u0000\u01be\u01bf\u0003\u009c"
          + "N\u0000\u01bf\u01c0\u00032\u0019\u0000\u01c0\u01c1\u0005\u0004\u0000\u0000"
          + "\u01c11\u0001\u0000\u0000\u0000\u01c2\u01ca\u0005%\u0000\u0000\u01c3\u01ca"
          + "\u0005&\u0000\u0000\u01c4\u01ca\u0005#\u0000\u0000\u01c5\u01ca\u0005$"
          + "\u0000\u0000\u01c6\u01ca\u0005+\u0000\u0000\u01c7\u01ca\u0005,\u0000\u0000"
          + "\u01c8\u01ca\u0003\u009aM\u0000\u01c9\u01c2\u0001\u0000\u0000\u0000\u01c9"
          + "\u01c3\u0001\u0000\u0000\u0000\u01c9\u01c4\u0001\u0000\u0000\u0000\u01c9"
          + "\u01c5\u0001\u0000\u0000\u0000\u01c9\u01c6\u0001\u0000\u0000\u0000\u01c9"
          + "\u01c7\u0001\u0000\u0000\u0000\u01c9\u01c8\u0001\u0000\u0000\u0000\u01ca"
          + "\u01cb\u0001\u0000\u0000\u0000\u01cb\u01cd\u0003\u009cN\u0000\u01cc\u01c9"
          + "\u0001\u0000\u0000\u0000\u01cd\u01ce\u0001\u0000\u0000\u0000\u01ce\u01cc"
          + "\u0001\u0000\u0000\u0000\u01ce\u01cf\u0001\u0000\u0000\u0000\u01cf3\u0001"
          + "\u0000\u0000\u0000\u01d0\u01d1\u0005*\u0000\u0000\u01d1\u01d2\u0003\u009c"
          + "N\u0000\u01d2\u01d3\u00036\u001b\u0000\u01d3\u01d4\u0003\u009cN\u0000"
          + "\u01d4\u01d5\u0005\u0004\u0000\u0000\u01d55\u0001\u0000\u0000\u0000\u01d6"
          + "\u01dd\u0003\"\u0011\u0000\u01d7\u01dd\u0003(\u0014\u0000\u01d8\u01dd"
          + "\u0005\u0016\u0000\u0000\u01d9\u01dd\u0003*\u0015\u0000\u01da\u01dd\u0003"
          + ",\u0016\u0000\u01db\u01dd\u0003.\u0017\u0000\u01dc\u01d6\u0001\u0000\u0000"
          + "\u0000\u01dc\u01d7\u0001\u0000\u0000\u0000\u01dc\u01d8\u0001\u0000\u0000"
          + "\u0000\u01dc\u01d9\u0001\u0000\u0000\u0000\u01dc\u01da\u0001\u0000\u0000"
          + "\u0000\u01dc\u01db\u0001\u0000\u0000\u0000\u01dd7\u0001\u0000\u0000\u0000"
          + "\u01de\u01df\u0005\r\u0000\u0000\u01df\u01e7\u0003\u009cN\u0000\u01e0"
          + "\u01e1\u0005(\u0000\u0000\u01e1\u01e7\u0003\u009cN\u0000\u01e2\u01e3\u0005"
          + "\u0011\u0000\u0000\u01e3\u01e7\u0003\u009cN\u0000\u01e4\u01e5\u0005\u000b"
          + "\u0000\u0000\u01e5\u01e7\u0003\u009cN\u0000\u01e6\u01de\u0001\u0000\u0000"
          + "\u0000\u01e6\u01e0\u0001\u0000\u0000\u0000\u01e6\u01e2\u0001\u0000\u0000"
          + "\u0000\u01e6\u01e4\u0001\u0000\u0000\u0000\u01e79\u0001\u0000\u0000\u0000"
          + "\u01e8\u01e9\u0003\u009aM\u0000\u01e9\u01ea\u0003\u009cN\u0000\u01ea\u01f2"
          + "\u0001\u0000\u0000\u0000\u01eb\u01ec\u0005;\u0000\u0000\u01ec\u01f2\u0003"
          + "\u009cN\u0000\u01ed\u01ee\u0005\u0007\u0000\u0000\u01ee\u01f2\u0003\u009a"
          + "M\u0000\u01ef\u01f0\u0005\u000e\u0000\u0000\u01f0\u01f2\u0003\u009aM\u0000"
          + "\u01f1\u01e8\u0001\u0000\u0000\u0000\u01f1\u01eb\u0001\u0000\u0000\u0000"
          + "\u01f1\u01ed\u0001\u0000\u0000\u0000\u01f1\u01ef\u0001\u0000\u0000\u0000"
          + "\u01f2;\u0001\u0000\u0000\u0000\u01f3\u01f4\u0003\u001a\r\u0000\u01f4"
          + "\u01f5\u0005\u0005\u0000\u0000\u01f5\u01f7\u0003\u009cN\u0000\u01f6\u01f8"
          + "\u0003>\u001f\u0000\u01f7\u01f6\u0001\u0000\u0000\u0000\u01f7\u01f8\u0001"
          + "\u0000\u0000\u0000\u01f8\u01f9\u0001\u0000\u0000\u0000\u01f9\u01fa\u0005"
          + "\u0006\u0000\u0000\u01fa\u01fb\u0003\u009cN\u0000\u01fb\u020b\u0001\u0000"
          + "\u0000\u0000\u01fc\u01fe\u0003X,\u0000\u01fd\u01fc\u0001\u0000\u0000\u0000"
          + "\u01fe\u0201\u0001\u0000\u0000\u0000\u01ff\u01fd\u0001\u0000\u0000\u0000"
          + "\u01ff\u0200\u0001\u0000\u0000\u0000\u0200\u0202\u0001\u0000\u0000\u0000"
          + "\u0201\u01ff\u0001\u0000\u0000\u0000\u0202\u0203\u0005\u0005\u0000\u0000"
          + "\u0203\u0205\u0003\u009cN\u0000\u0204\u0206\u0003>\u001f\u0000\u0205\u0204"
          + "\u0001\u0000\u0000\u0000\u0205\u0206\u0001\u0000\u0000\u0000\u0206\u0207"
          + "\u0001\u0000\u0000\u0000\u0207\u0208\u0005\u0006\u0000\u0000\u0208\u0209"
          + "\u0003\u009cN\u0000\u0209\u020b\u0001\u0000\u0000\u0000\u020a\u01f3\u0001"
          + "\u0000\u0000\u0000\u020a\u01ff\u0001\u0000\u0000\u0000\u020b=\u0001\u0000"
          + "\u0000\u0000\u020c\u020d\u0005\u0001\u0000\u0000\u020d\u020f\u0003\u009c"
          + "N\u0000\u020e\u020c\u0001\u0000\u0000\u0000\u020f\u0212\u0001\u0000\u0000"
          + "\u0000\u0210\u020e\u0001\u0000\u0000\u0000\u0210\u0211\u0001\u0000\u0000"
          + "\u0000\u0211\u0213\u0001\u0000\u0000\u0000\u0212\u0210\u0001\u0000\u0000"
          + "\u0000\u0213\u0214\u0003@ \u0000\u0214\u021c\u0003\u009cN\u0000\u0215"
          + "\u0216\u0005\u0001\u0000\u0000\u0216\u0218\u0003\u009cN\u0000\u0217\u0219"
          + "\u0003@ \u0000\u0218\u0217\u0001\u0000\u0000\u0000\u0218\u0219\u0001\u0000"
          + "\u0000\u0000\u0219\u021b\u0001\u0000\u0000\u0000\u021a\u0215\u0001\u0000"
          + "\u0000\u0000\u021b\u021e\u0001\u0000\u0000\u0000\u021c\u021a\u0001\u0000"
          + "\u0000\u0000\u021c\u021d\u0001\u0000\u0000\u0000\u021d?\u0001\u0000\u0000"
          + "\u0000\u021e\u021c\u0001\u0000\u0000\u0000\u021f\u0220\u0003:\u001d\u0000"
          + "\u0220\u0221\u0005\u0003\u0000\u0000\u0221\u0222\u0003\u009cN\u0000\u0222"
          + "\u0224\u0003F#\u0000\u0223\u0225\u0003B!\u0000\u0224\u0223\u0001\u0000"
          + "\u0000\u0000\u0224\u0225\u0001\u0000\u0000\u0000\u0225\u022c\u0001\u0000"
          + "\u0000\u0000\u0226\u0227\u0003:\u001d\u0000\u0227\u0228\u0005\u0003\u0000"
          + "\u0000\u0228\u0229\u0003\u009cN\u0000\u0229\u022a\u0003D\"\u0000\u022a"
          + "\u022c\u0001\u0000\u0000\u0000\u022b\u021f\u0001\u0000\u0000\u0000\u022b"
          + "\u0226\u0001\u0000\u0000\u0000\u022cA\u0001\u0000\u0000\u0000\u022d\u022e"
          + "\u0005\u001c\u0000\u0000\u022e\u022f\u0003\u009cN\u0000\u022fC\u0001\u0000"
          + "\u0000\u0000\u0230\u0236\u0003X,\u0000\u0231\u0236\u0003`0\u0000\u0232"
          + "\u0233\u0003\\.\u0000\u0233\u0234\u0003\u009cN\u0000\u0234\u0236\u0001"
          + "\u0000\u0000\u0000\u0235\u0230\u0001\u0000\u0000\u0000\u0235\u0231\u0001"
          + "\u0000\u0000\u0000\u0235\u0232\u0001\u0000\u0000\u0000\u0236\u0237\u0001"
          + "\u0000\u0000\u0000\u0237\u0235\u0001\u0000\u0000\u0000\u0237\u0238\u0001"
          + "\u0000\u0000\u0000\u0238E\u0001\u0000\u0000\u0000\u0239\u0240\u0003H$"
          + "\u0000\u023a\u023c\u00038\u001c\u0000\u023b\u023a\u0001\u0000\u0000\u0000"
          + "\u023b\u023c\u0001\u0000\u0000\u0000\u023c\u023d\u0001\u0000\u0000\u0000"
          + "\u023d\u023f\u0003H$\u0000\u023e\u023b\u0001\u0000\u0000\u0000\u023f\u0242"
          + "\u0001\u0000\u0000\u0000\u0240\u023e\u0001\u0000\u0000\u0000\u0240\u0241"
          + "\u0001\u0000\u0000\u0000\u0241G\u0001\u0000\u0000\u0000\u0242\u0240\u0001"
          + "\u0000\u0000\u0000\u0243\u0244\u0003P(\u0000\u0244\u0245\u0003\u009cN"
          + "\u0000\u0245\u025e\u0001\u0000\u0000\u0000\u0246\u0247\u0003R)\u0000\u0247"
          + "\u0248\u0003\u009cN\u0000\u0248\u025e\u0001\u0000\u0000\u0000\u0249\u024a"
          + "\u0003T*\u0000\u024a\u024b\u0003\u009cN\u0000\u024b\u025e\u0001\u0000"
          + "\u0000\u0000\u024c\u024d\u0005,\u0000\u0000\u024d\u025e\u0003\u009cN\u0000"
          + "\u024e\u024f\u0005\u001f\u0000\u0000\u024f\u025e\u0003\u009cN\u0000\u0250"
          + "\u0251\u0003\u009aM\u0000\u0251\u0252\u0003\u009cN\u0000\u0252\u025e\u0001"
          + "\u0000\u0000\u0000\u0253\u025e\u0003v;\u0000\u0254\u0255\u0005\u001e\u0000"
          + "\u0000\u0255\u025e\u0003\u009cN\u0000\u0256\u025e\u0003N\'\u0000\u0257"
          + "\u025e\u0003x<\u0000\u0258\u025e\u0003J%\u0000\u0259\u025a\u0003V+\u0000"
          + "\u025a\u025b\u0003\u009cN\u0000\u025b\u025e\u0001\u0000\u0000\u0000\u025c"
          + "\u025e\u0003L&\u0000\u025d\u0243\u0001\u0000\u0000\u0000\u025d\u0246\u0001"
          + "\u0000\u0000\u0000\u025d\u0249\u0001\u0000\u0000\u0000\u025d\u024c\u0001"
          + "\u0000\u0000\u0000\u025d\u024e\u0001\u0000\u0000\u0000\u025d\u0250\u0001"
          + "\u0000\u0000\u0000\u025d\u0253\u0001\u0000\u0000\u0000\u025d\u0254\u0001"
          + "\u0000\u0000\u0000\u025d\u0256\u0001\u0000\u0000\u0000\u025d\u0257\u0001"
          + "\u0000\u0000\u0000\u025d\u0258\u0001\u0000\u0000\u0000\u025d\u0259\u0001"
          + "\u0000\u0000\u0000\u025d\u025c\u0001\u0000\u0000\u0000\u025eI\u0001\u0000"
          + "\u0000\u0000\u025f\u0260\u0005>\u0000\u0000\u0260\u0261\u0003\u009cN\u0000"
          + "\u0261\u0262\u0003F#\u0000\u0262\u0263\u0005\u0004\u0000\u0000\u0263\u0264"
          + "\u0003\u009cN\u0000\u0264K\u0001\u0000\u0000\u0000\u0265\u0266\u0005:"
          + "\u0000\u0000\u0266\u0267\u0003\u009cN\u0000\u0267\u0268\u0003F#\u0000"
          + "\u0268\u0269\u0005\u0004\u0000\u0000\u0269\u026a\u0003\u009cN\u0000\u026a"
          + "M\u0001\u0000\u0000\u0000\u026b\u026c\u0005\u0016\u0000\u0000\u026c\u026d"
          + "\u0003\u009cN\u0000\u026dO\u0001\u0000\u0000\u0000\u026e\u0270\u0007\u0004"
          + "\u0000\u0000\u026f\u026e\u0001\u0000\u0000\u0000\u026f\u0270\u0001\u0000"
          + "\u0000\u0000\u0270\u0271\u0001\u0000\u0000\u0000\u0271\u0272\u0005+\u0000"
          + "\u0000\u0272Q\u0001\u0000\u0000\u0000\u0273\u0275\u0007\u0004\u0000\u0000"
          + "\u0274\u0273\u0001\u0000\u0000\u0000\u0274\u0275\u0001\u0000\u0000\u0000"
          + "\u0275\u0276\u0001\u0000\u0000\u0000\u0276\u0277\u0005\u001d\u0000\u0000"
          + "\u0277S\u0001\u0000\u0000\u0000\u0278\u027a\u0007\u0004\u0000\u0000\u0279"
          + "\u0278\u0001\u0000\u0000\u0000\u0279\u027a\u0001\u0000\u0000\u0000\u027a"
          + "\u027b\u0001\u0000\u0000\u0000\u027b\u027c\u0005#\u0000\u0000\u027cU\u0001"
          + "\u0000\u0000\u0000\u027d\u027f\u0007\u0004\u0000\u0000\u027e\u027d\u0001"
          + "\u0000\u0000\u0000\u027e\u027f\u0001\u0000\u0000\u0000\u027f\u0280\u0001"
          + "\u0000\u0000\u0000\u0280\u0281\u0005$\u0000\u0000\u0281W\u0001\u0000\u0000"
          + "\u0000\u0282\u0283\u0003\u009aM\u0000\u0283\u0284\u0003\u009cN\u0000\u0284"
          + "\u02c4\u0001\u0000\u0000\u0000\u0285\u0286\u0003P(\u0000\u0286\u0287\u0003"
          + "\u009cN\u0000\u0287\u02c4\u0001\u0000\u0000\u0000\u0288\u0289\u0003R)"
          + "\u0000\u0289\u028a\u0003\u009cN\u0000\u028a\u02c4\u0001\u0000\u0000\u0000"
          + "\u028b\u028c\u0003T*\u0000\u028c\u028d\u0003\u009cN\u0000\u028d\u02c4"
          + "\u0001\u0000\u0000\u0000\u028e\u028f\u0003V+\u0000\u028f\u0290\u0003\u009c"
          + "N\u0000\u0290\u02c4\u0001\u0000\u0000\u0000\u0291\u0292\u0005,\u0000\u0000"
          + "\u0292\u02c4\u0003\u009cN\u0000\u0293\u0294\u0005\u001e\u0000\u0000\u0294"
          + "\u02c4\u0003\u009cN\u0000\u0295\u0296\u0005\u0016\u0000\u0000\u0296\u02c4"
          + "\u0003\u009cN\u0000\u0297\u0298\u0005\u001f\u0000\u0000\u0298\u02c4\u0003"
          + "\u009cN\u0000\u0299\u029a\u0005\u0014\u0000\u0000\u029a\u02c4\u0003\u009c"
          + "N\u0000\u029b\u029c\u0005\u0015\u0000\u0000\u029c\u02c4\u0003\u009cN\u0000"
          + "\u029d\u029e\u0005\u0003\u0000\u0000\u029e\u02c4\u0003\u009cN\u0000\u029f"
          + "\u02a0\u0005>\u0000\u0000\u02a0\u02a5\u0003\u009cN\u0000\u02a1\u02a4\u0003"
          + "X,\u0000\u02a2\u02a4\u0003^/\u0000\u02a3\u02a1\u0001\u0000\u0000\u0000"
          + "\u02a3\u02a2\u0001\u0000\u0000\u0000\u02a4\u02a7\u0001\u0000\u0000\u0000"
          + "\u02a5\u02a3\u0001\u0000\u0000\u0000\u02a5\u02a6\u0001\u0000\u0000\u0000"
          + "\u02a6\u02a8\u0001\u0000\u0000\u0000\u02a7\u02a5\u0001\u0000\u0000\u0000"
          + "\u02a8\u02a9\u0005\u0004\u0000\u0000\u02a9\u02aa\u0003\u009cN\u0000\u02aa"
          + "\u02c4\u0001\u0000\u0000\u0000\u02ab\u02ac\u0005\u0002\u0000\u0000\u02ac"
          + "\u02b1\u0003\u009cN\u0000\u02ad\u02b0\u0003X,\u0000\u02ae\u02b0\u0003"
          + "^/\u0000\u02af\u02ad\u0001\u0000\u0000\u0000\u02af\u02ae\u0001\u0000\u0000"
          + "\u0000\u02b0\u02b3\u0001\u0000\u0000\u0000\u02b1\u02af\u0001\u0000\u0000"
          + "\u0000\u02b1\u02b2\u0001\u0000\u0000\u0000\u02b2\u02b4\u0001\u0000\u0000"
          + "\u0000\u02b3\u02b1\u0001\u0000\u0000\u0000\u02b4\u02b5\u0005\u0004\u0000"
          + "\u0000\u02b5\u02b6\u0003\u009cN\u0000\u02b6\u02c4\u0001\u0000\u0000\u0000"
          + "\u02b7\u02b8\u0005\n\u0000\u0000\u02b8\u02bd\u0003\u009cN\u0000\u02b9"
          + "\u02bc\u0003X,\u0000\u02ba\u02bc\u0003^/\u0000\u02bb\u02b9\u0001\u0000"
          + "\u0000\u0000\u02bb\u02ba\u0001\u0000\u0000\u0000\u02bc\u02bf\u0001\u0000"
          + "\u0000\u0000\u02bd\u02bb\u0001\u0000\u0000\u0000\u02bd\u02be\u0001\u0000"
          + "\u0000\u0000\u02be\u02c0\u0001\u0000\u0000\u0000\u02bf\u02bd\u0001\u0000"
          + "\u0000\u0000\u02c0\u02c1\u0005\f\u0000\u0000\u02c1\u02c2\u0003\u009cN"
          + "\u0000\u02c2\u02c4\u0001\u0000\u0000\u0000\u02c3\u0282\u0001\u0000\u0000"
          + "\u0000\u02c3\u0285\u0001\u0000\u0000\u0000\u02c3\u0288\u0001\u0000\u0000"
          + "\u0000\u02c3\u028b\u0001\u0000\u0000\u0000\u02c3\u028e\u0001\u0000\u0000"
          + "\u0000\u02c3\u0291\u0001\u0000\u0000\u0000\u02c3\u0293\u0001\u0000\u0000"
          + "\u0000\u02c3\u0295\u0001\u0000\u0000\u0000\u02c3\u0297\u0001\u0000\u0000"
          + "\u0000\u02c3\u0299\u0001\u0000\u0000\u0000\u02c3\u029b\u0001\u0000\u0000"
          + "\u0000\u02c3\u029d\u0001\u0000\u0000\u0000\u02c3\u029f\u0001\u0000\u0000"
          + "\u0000\u02c3\u02ab\u0001\u0000\u0000\u0000\u02c3\u02b7\u0001\u0000\u0000"
          + "\u0000\u02c4Y\u0001\u0000\u0000\u0000\u02c5\u02c6\u0003\\.\u0000\u02c6"
          + "\u02ca\u0003\u009cN\u0000\u02c7\u02c9\u0003X,\u0000\u02c8\u02c7\u0001"
          + "\u0000\u0000\u0000\u02c9\u02cc\u0001\u0000\u0000\u0000\u02ca\u02c8\u0001"
          + "\u0000\u0000\u0000\u02ca\u02cb\u0001\u0000\u0000\u0000\u02cb\u02d0\u0001"
          + "\u0000\u0000\u0000\u02cc\u02ca\u0001\u0000\u0000\u0000\u02cd\u02d1\u0003"
          + "`0\u0000\u02ce\u02cf\u0005\u0001\u0000\u0000\u02cf\u02d1\u0003\u009cN"
          + "\u0000\u02d0\u02cd\u0001\u0000\u0000\u0000\u02d0\u02ce\u0001\u0000\u0000"
          + "\u0000\u02d1[\u0001\u0000\u0000\u0000\u02d2\u02d3\u0005\u000f\u0000\u0000"
          + "\u02d3\u02d4\u0003\u009aM\u0000\u02d4]\u0001\u0000\u0000\u0000\u02d5\u02e0"
          + "\u0003`0\u0000\u02d6\u02d7\u0003\\.\u0000\u02d7\u02d8\u0003\u009cN\u0000"
          + "\u02d8\u02e0\u0001\u0000\u0000\u0000\u02d9\u02da\u0005\u0001\u0000\u0000"
          + "\u02da\u02e0\u0003\u009cN\u0000\u02db\u02dc\u0005\u0012\u0000\u0000\u02dc"
          + "\u02e0\u0003\u009cN\u0000\u02dd\u02de\u0005\u0013\u0000\u0000\u02de\u02e0"
          + "\u0003\u009cN\u0000\u02df\u02d5\u0001\u0000\u0000\u0000\u02df\u02d6\u0001"
          + "\u0000\u0000\u0000\u02df\u02d9\u0001\u0000\u0000\u0000\u02df\u02db\u0001"
          + "\u0000\u0000\u0000\u02df\u02dd\u0001\u0000\u0000\u0000\u02e0_\u0001\u0000"
          + "\u0000\u0000\u02e1\u02e2\u0005\u0005\u0000\u0000\u02e2\u02ee\u0003\u009c"
          + "N\u0000\u02e3\u02ed\u0003>\u001f\u0000\u02e4\u02ed\u0003b1\u0000\u02e5"
          + "\u02ed\u0003X,\u0000\u02e6\u02ed\u0003`0\u0000\u02e7\u02e8\u0003\\.\u0000"
          + "\u02e8\u02e9\u0003\u009cN\u0000\u02e9\u02ed\u0001\u0000\u0000\u0000\u02ea"
          + "\u02eb\u0005\u0001\u0000\u0000\u02eb\u02ed\u0003\u009cN\u0000\u02ec\u02e3"
          + "\u0001\u0000\u0000\u0000\u02ec\u02e4\u0001\u0000\u0000\u0000\u02ec\u02e5"
          + "\u0001\u0000\u0000\u0000\u02ec\u02e6\u0001\u0000\u0000\u0000\u02ec\u02e7"
          + "\u0001\u0000\u0000\u0000\u02ec\u02ea\u0001\u0000\u0000\u0000\u02ed\u02f0"
          + "\u0001\u0000\u0000\u0000\u02ee\u02ec\u0001\u0000\u0000\u0000\u02ee\u02ef"
          + "\u0001\u0000\u0000\u0000\u02ef\u02f1\u0001\u0000\u0000\u0000\u02f0\u02ee"
          + "\u0001\u0000\u0000\u0000\u02f1\u02f2\u0005\u0006\u0000\u0000\u02f2\u02f3"
          + "\u0003\u009cN\u0000\u02f3a\u0001\u0000\u0000\u0000\u02f4\u02ff\u0003<"
          + "\u001e\u0000\u02f5\u02ff\u0003\n\u0005\u0000\u02f6\u02ff\u0003\u0016\u000b"
          + "\u0000\u02f7\u02ff\u0003\u0080@\u0000\u02f8\u02ff\u0003\u0084B\u0000\u02f9"
          + "\u02ff\u0003f3\u0000\u02fa\u02ff\u0003\u008aE\u0000\u02fb\u02ff\u0003"
          + "\u008cF\u0000\u02fc\u02ff\u0003\u008eG\u0000\u02fd\u02ff\u0003Z-\u0000"
          + "\u02fe\u02f4\u0001\u0000\u0000\u0000\u02fe\u02f5\u0001\u0000\u0000\u0000"
          + "\u02fe\u02f6\u0001\u0000\u0000\u0000\u02fe\u02f7\u0001\u0000\u0000\u0000"
          + "\u02fe\u02f8\u0001\u0000\u0000\u0000\u02fe\u02f9\u0001\u0000\u0000\u0000"
          + "\u02fe\u02fa\u0001\u0000\u0000\u0000\u02fe\u02fb\u0001\u0000\u0000\u0000"
          + "\u02fe\u02fc\u0001\u0000\u0000\u0000\u02fe\u02fd\u0001\u0000\u0000\u0000"
          + "\u02ffc\u0001\u0000\u0000\u0000\u0300\u0301\u0005\u0005\u0000\u0000\u0301"
          + "\u0305\u0003\u009cN\u0000\u0302\u0304\u0003b1\u0000\u0303\u0302\u0001"
          + "\u0000\u0000\u0000\u0304\u0307\u0001\u0000\u0000\u0000\u0305\u0303\u0001"
          + "\u0000\u0000\u0000\u0305\u0306\u0001\u0000\u0000\u0000\u0306\u0308\u0001"
          + "\u0000\u0000\u0000\u0307\u0305\u0001\u0000\u0000\u0000\u0308\u0309\u0005"
          + "\u0006\u0000\u0000\u0309\u030a\u0003\u009cN\u0000\u030ae\u0001\u0000\u0000"
          + "\u0000\u030b\u030c\u00051\u0000\u0000\u030c\u030d\u0003\u009cN\u0000\u030d"
          + "\u030e\u0003h4\u0000\u030e\u030f\u0003\u009cN\u0000\u030f\u0310\u0003"
          + "d2\u0000\u0310g\u0001\u0000\u0000\u0000\u0311\u0316\u0003l6\u0000\u0312"
          + "\u0316\u0003n7\u0000\u0313\u0316\u0003p8\u0000\u0314\u0316\u0003j5\u0000"
          + "\u0315\u0311\u0001\u0000\u0000\u0000\u0315\u0312\u0001\u0000\u0000\u0000"
          + "\u0315\u0313\u0001\u0000\u0000\u0000\u0315\u0314\u0001\u0000\u0000\u0000"
          + "\u0316i\u0001\u0000\u0000\u0000\u0317\u0318\u0005\u0002\u0000\u0000\u0318"
          + "\u0319\u0003\u009cN\u0000\u0319\u031a\u0003h4\u0000\u031a\u031b\u0003"
          + "\u009cN\u0000\u031b\u031c\u0005\u0004\u0000\u0000\u031c\u0320\u0001\u0000"
          + "\u0000\u0000\u031d\u0320\u0003r9\u0000\u031e\u0320\u0003t:\u0000\u031f"
          + "\u0317\u0001\u0000\u0000\u0000\u031f\u031d\u0001\u0000\u0000\u0000\u031f"
          + "\u031e\u0001\u0000\u0000\u0000\u0320k\u0001\u0000\u0000\u0000\u0321\u0322"
          + "\u0005!\u0000\u0000\u0322\u0323\u0003\u009cN\u0000\u0323\u0324\u0005\u0011"
          + "\u0000\u0000\u0324\u0325\u0003\u009cN\u0000\u0325\u0326\u0003j5\u0000"
          + "\u0326m\u0001\u0000\u0000\u0000\u0327\u0331\u0003j5\u0000\u0328\u0329"
          + "\u0003\u009cN\u0000\u0329\u032a\u0005\u0011\u0000\u0000\u032a\u032b\u0003"
          + "\u009cN\u0000\u032b\u032c\u0005\"\u0000\u0000\u032c\u032d\u0003\u009c"
          + "N\u0000\u032d\u032e\u0005\u0011\u0000\u0000\u032e\u032f\u0003\u009cN\u0000"
          + "\u032f\u0330\u0003j5\u0000\u0330\u0332\u0001\u0000\u0000\u0000\u0331\u0328"
          + "\u0001\u0000\u0000\u0000\u0332\u0333\u0001\u0000\u0000\u0000\u0333\u0331"
          + "\u0001\u0000\u0000\u0000\u0333\u0334\u0001\u0000\u0000\u0000\u0334o\u0001"
          + "\u0000\u0000\u0000\u0335\u033f\u0003j5\u0000\u0336\u0337\u0003\u009cN"
          + "\u0000\u0337\u0338\u0005\u0011\u0000\u0000\u0338\u0339\u0003\u009cN\u0000"
          + "\u0339\u033a\u00052\u0000\u0000\u033a\u033b\u0003\u009cN\u0000\u033b\u033c"
          + "\u0005\u0011\u0000\u0000\u033c\u033d\u0003\u009cN\u0000\u033d\u033e\u0003"
          + "j5\u0000\u033e\u0340\u0001\u0000\u0000\u0000\u033f\u0336\u0001\u0000\u0000"
          + "\u0000\u0340\u0341\u0001\u0000\u0000\u0000\u0341\u033f\u0001\u0000\u0000"
          + "\u0000\u0341\u0342\u0001\u0000\u0000\u0000\u0342q\u0001\u0000\u0000\u0000"
          + "\u0343\u0344\u0005\u0002\u0000\u0000\u0344\u0345\u0003\u009cN\u0000\u0345"
          + "\u0346\u0003@ \u0000\u0346\u0347\u0005\u0004\u0000\u0000\u0347s\u0001"
          + "\u0000\u0000\u0000\u0348\u034d\u0007\u0005\u0000\u0000\u0349\u034c\u0003"
          + "X,\u0000\u034a\u034c\u0003^/\u0000\u034b\u0349\u0001\u0000\u0000\u0000"
          + "\u034b\u034a\u0001\u0000\u0000\u0000\u034c\u034f\u0001\u0000\u0000\u0000"
          + "\u034d\u034b\u0001\u0000\u0000\u0000\u034d\u034e\u0001\u0000\u0000\u0000"
          + "\u034e\u0350\u0001\u0000\u0000\u0000\u034f\u034d\u0001\u0000\u0000\u0000"
          + "\u0350\u0351\u0005\u0004\u0000\u0000\u0351u\u0001\u0000\u0000\u0000\u0352"
          + "\u0353\u0005<\u0000\u0000\u0353\u0354\u0003\u009cN\u0000\u0354\u0355\u0005"
          + ";\u0000\u0000\u0355\u0356\u0003\u009cN\u0000\u0356\u0357\u0005\u0004\u0000"
          + "\u0000\u0357\u0358\u0003\u009cN\u0000\u0358w\u0001\u0000\u0000\u0000\u0359"
          + "\u035a\u00056\u0000\u0000\u035a\u035b\u0003\u009cN\u0000\u035b\u035c\u0003"
          + "z=\u0000\u035c\u035d\u0005\u0004\u0000\u0000\u035d\u035e\u0003\u009cN"
          + "\u0000\u035ey\u0001\u0000\u0000\u0000\u035f\u036a\u0003|>\u0000\u0360"
          + "\u0361\u0005\u0011\u0000\u0000\u0361\u0362\u0003\u009cN\u0000\u0362\u0363"
          + "\u0007\u0004\u0000\u0000\u0363\u0364\u0003\u009cN\u0000\u0364\u0365\u0005"
          + "\u0011\u0000\u0000\u0365\u0366\u0003\u009cN\u0000\u0366\u0367\u0003|>"
          + "\u0000\u0367\u0369\u0001\u0000\u0000\u0000\u0368\u0360\u0001\u0000\u0000"
          + "\u0000\u0369\u036c\u0001\u0000\u0000\u0000\u036a\u0368\u0001\u0000\u0000"
          + "\u0000\u036a\u036b\u0001\u0000\u0000\u0000\u036b{\u0001\u0000\u0000\u0000"
          + "\u036c\u036a\u0001\u0000\u0000\u0000\u036d\u0379\u0003~?\u0000\u036e\u036f"
          + "\u0005\u0007\u0000\u0000\u036f\u0370\u0003\u009cN\u0000\u0370\u0371\u0003"
          + "~?\u0000\u0371\u0378\u0001\u0000\u0000\u0000\u0372\u0373\u0005\r\u0000"
          + "\u0000\u0373\u0374\u0003\u009cN\u0000\u0374\u0375\u0003P(\u0000\u0375"
          + "\u0376\u0003\u009cN\u0000\u0376\u0378\u0001\u0000\u0000\u0000\u0377\u036e"
          + "\u0001\u0000\u0000\u0000\u0377\u0372\u0001\u0000\u0000\u0000\u0378\u037b"
          + "\u0001\u0000\u0000\u0000\u0379\u0377\u0001\u0000\u0000\u0000\u0379\u037a"
          + "\u0001\u0000\u0000\u0000\u037a}\u0001\u0000\u0000\u0000\u037b\u0379\u0001"
          + "\u0000\u0000\u0000\u037c\u037d\u0003P(\u0000\u037d\u037e\u0003\u009cN"
          + "\u0000\u037e\u038f\u0001\u0000\u0000\u0000\u037f\u0380\u0003T*\u0000\u0380"
          + "\u0381\u0003\u009cN\u0000\u0381\u038f\u0001\u0000\u0000\u0000\u0382\u0383"
          + "\u0003V+\u0000\u0383\u0384\u0003\u009cN\u0000\u0384\u038f\u0001\u0000"
          + "\u0000\u0000\u0385\u0386\u0003R)\u0000\u0386\u0387\u0003\u009cN\u0000"
          + "\u0387\u038f\u0001\u0000\u0000\u0000\u0388\u0389\u0005\u0002\u0000\u0000"
          + "\u0389\u038a\u0003\u009cN\u0000\u038a\u038b\u0003z=\u0000\u038b\u038c"
          + "\u0005\u0004\u0000\u0000\u038c\u038d\u0003\u009cN\u0000\u038d\u038f\u0001"
          + "\u0000\u0000\u0000\u038e\u037c\u0001\u0000\u0000\u0000\u038e\u037f\u0001"
          + "\u0000\u0000\u0000\u038e\u0382\u0001\u0000\u0000\u0000\u038e\u0385\u0001"
          + "\u0000\u0000\u0000\u038e\u0388\u0001\u0000\u0000\u0000\u038f\u007f\u0001"
          + "\u0000\u0000\u0000\u0390\u0391\u00050\u0000\u0000\u0391\u0392\u0003\u009c"
          + "N\u0000\u0392\u0393\u0005\u0005\u0000\u0000\u0393\u0395\u0003\u009cN\u0000"
          + "\u0394\u0396\u0003\u0082A\u0000\u0395\u0394\u0001\u0000\u0000\u0000\u0395"
          + "\u0396\u0001\u0000\u0000\u0000\u0396\u039e\u0001\u0000\u0000\u0000\u0397"
          + "\u0398\u0005\u0001\u0000\u0000\u0398\u039a\u0003\u009cN\u0000\u0399\u039b"
          + "\u0003\u0082A\u0000\u039a\u0399\u0001\u0000\u0000\u0000\u039a\u039b\u0001"
          + "\u0000\u0000\u0000\u039b\u039d\u0001\u0000\u0000\u0000\u039c\u0397\u0001"
          + "\u0000\u0000\u0000\u039d\u03a0\u0001\u0000\u0000\u0000\u039e\u039c\u0001"
          + "\u0000\u0000\u0000\u039e\u039f\u0001\u0000\u0000\u0000\u039f\u03a1\u0001"
          + "\u0000\u0000\u0000\u03a0\u039e\u0001\u0000\u0000\u0000\u03a1\u03a2\u0005"
          + "\u0006\u0000\u0000\u03a2\u03a3\u0003\u009cN\u0000\u03a3\u0081\u0001\u0000"
          + "\u0000\u0000\u03a4\u03a5\u0003:\u001d\u0000\u03a5\u03a6\u0005\u0003\u0000"
          + "\u0000\u03a6\u03a7\u0003\u009cN\u0000\u03a7\u03a8\u0003F#\u0000\u03a8"
          + "\u03af\u0001\u0000\u0000\u0000\u03a9\u03aa\u0003:\u001d\u0000\u03aa\u03ab"
          + "\u0005\u0003\u0000\u0000\u03ab\u03ac\u0003\u009cN\u0000\u03ac\u03ad\u0003"
          + "D\"\u0000\u03ad\u03af\u0001\u0000\u0000\u0000\u03ae\u03a4\u0001\u0000"
          + "\u0000\u0000\u03ae\u03a9\u0001\u0000\u0000\u0000\u03af\u0083\u0001\u0000"
          + "\u0000\u0000\u03b0\u03b1\u00053\u0000\u0000\u03b1\u03b2\u0003\u009cN\u0000"
          + "\u03b2\u03b3\u0005\u0011\u0000\u0000\u03b3\u03b4\u0003\u009cN\u0000\u03b4"
          + "\u03b5\u0003\u009aM\u0000\u03b5\u03b6\u0003\u009cN\u0000\u03b6\u03b7\u0005"
          + "\u0005\u0000\u0000\u03b7\u03b8\u0003\u009cN\u0000\u03b8\u03b9\u0003\u0086"
          + "C\u0000\u03b9\u03ba\u0005\u0006\u0000\u0000\u03ba\u03bb\u0003\u009cN\u0000"
          + "\u03bb\u0085\u0001\u0000\u0000\u0000\u03bc\u03bd\u0003\u0088D\u0000\u03bd"
          + "\u03be\u0005\u0005\u0000\u0000\u03be\u03c0\u0003\u009cN\u0000\u03bf\u03c1"
          + "\u0003>\u001f\u0000\u03c0\u03bf\u0001\u0000\u0000\u0000\u03c0\u03c1\u0001"
          + "\u0000\u0000\u0000\u03c1\u03c2\u0001\u0000\u0000\u0000\u03c2\u03c3\u0005"
          + "\u0006\u0000\u0000\u03c3\u03c4\u0003\u009cN\u0000\u03c4\u03c6\u0001\u0000"
          + "\u0000\u0000\u03c5\u03bc\u0001\u0000\u0000\u0000\u03c6\u03c9\u0001\u0000"
          + "\u0000\u0000\u03c7\u03c5\u0001\u0000\u0000\u0000\u03c7\u03c8\u0001\u0000"
          + "\u0000\u0000\u03c8\u0087\u0001\u0000\u0000\u0000\u03c9\u03c7\u0001\u0000"
          + "\u0000\u0000\u03ca\u03cb\u0007\u0006\u0000\u0000\u03cb\u03d3\u0003\u009c"
          + "N\u0000\u03cc\u03cd\u0005(\u0000\u0000\u03cd\u03ce\u0003\u009cN\u0000"
          + "\u03ce\u03cf\u0007\u0006\u0000\u0000\u03cf\u03d0\u0003\u009cN\u0000\u03d0"
          + "\u03d2\u0001\u0000\u0000\u0000\u03d1\u03cc\u0001\u0000\u0000\u0000\u03d2"
          + "\u03d5\u0001\u0000\u0000\u0000\u03d3\u03d1\u0001\u0000\u0000\u0000\u03d3"
          + "\u03d4\u0001\u0000\u0000\u0000\u03d4\u0089\u0001\u0000\u0000\u0000\u03d5"
          + "\u03d3\u0001\u0000\u0000\u0000\u03d6\u03d7\u00057\u0000\u0000\u03d7\u03d8"
          + "\u0003\u009cN\u0000\u03d8\u03d9\u0005\u0005\u0000\u0000\u03d9\u03db\u0003"
          + "\u009cN\u0000\u03da\u03dc\u0003>\u001f\u0000\u03db\u03da\u0001\u0000\u0000"
          + "\u0000\u03db\u03dc\u0001\u0000\u0000\u0000\u03dc\u03dd\u0001\u0000\u0000"
          + "\u0000\u03dd\u03de\u0005\u0006\u0000\u0000\u03de\u03df\u0003\u009cN\u0000"
          + "\u03df\u008b\u0001\u0000\u0000\u0000\u03e0\u03e1\u00058\u0000\u0000\u03e1"
          + "\u03e2\u0003\u009cN\u0000\u03e2\u03e3\u0003\u009aM\u0000\u03e3\u03e4\u0003"
          + "\u009cN\u0000\u03e4\u03e5\u0005\u0005\u0000\u0000\u03e5\u03e7\u0003\u009c"
          + "N\u0000\u03e6\u03e8\u0003>\u001f\u0000\u03e7\u03e6\u0001\u0000\u0000\u0000"
          + "\u03e7\u03e8\u0001\u0000\u0000\u0000\u03e8\u03e9\u0001\u0000\u0000\u0000"
          + "\u03e9\u03ea\u0005\u0006\u0000\u0000\u03ea\u03eb\u0003\u009cN\u0000\u03eb"
          + "\u008d\u0001\u0000\u0000\u0000\u03ec\u03ed\u00059\u0000\u0000\u03ed\u03ee"
          + "\u0003\u009cN\u0000\u03ee\u03ef\u0003\u0090H\u0000\u03ef\u03f0\u0003\u009c"
          + "N\u0000\u03f0\u03f1\u0005\u0005\u0000\u0000\u03f1\u03f5\u0003\u009cN\u0000"
          + "\u03f2\u03f4\u0003\u0094J\u0000\u03f3\u03f2\u0001\u0000\u0000\u0000\u03f4"
          + "\u03f7\u0001\u0000\u0000\u0000\u03f5\u03f3\u0001\u0000\u0000\u0000\u03f5"
          + "\u03f6\u0001\u0000\u0000\u0000\u03f6\u03f8\u0001\u0000\u0000\u0000\u03f7"
          + "\u03f5\u0001\u0000\u0000\u0000\u03f8\u03f9\u0005\u0006\u0000\u0000\u03f9"
          + "\u03fa\u0003\u009cN\u0000\u03fa\u008f\u0001\u0000\u0000\u0000\u03fb\u0403"
          + "\u0003\u0092I\u0000\u03fc\u03fd\u0003\u009cN\u0000\u03fd\u03fe\u0005("
          + "\u0000\u0000\u03fe\u03ff\u0003\u009cN\u0000\u03ff\u0400\u0003\u0092I\u0000"
          + "\u0400\u0402\u0001\u0000\u0000\u0000\u0401\u03fc\u0001\u0000\u0000\u0000"
          + "\u0402\u0405\u0001\u0000\u0000\u0000\u0403\u0401\u0001\u0000\u0000\u0000"
          + "\u0403\u0404\u0001\u0000\u0000\u0000\u0404\u0091\u0001\u0000\u0000\u0000"
          + "\u0405\u0403\u0001\u0000\u0000\u0000\u0406\u0411\u0005,\u0000\u0000\u0407"
          + "\u040d\u0003\u009aM\u0000\u0408\u0409\u0003\u009cN\u0000\u0409\u040a\u0003"
          + "\u009aM\u0000\u040a\u040c\u0001\u0000\u0000\u0000\u040b\u0408\u0001\u0000"
          + "\u0000\u0000\u040c\u040f\u0001\u0000\u0000\u0000\u040d\u040b\u0001\u0000"
          + "\u0000\u0000\u040d\u040e\u0001\u0000\u0000\u0000\u040e\u0411\u0001\u0000"
          + "\u0000\u0000\u040f\u040d\u0001\u0000\u0000\u0000\u0410\u0406\u0001\u0000"
          + "\u0000\u0000\u0410\u0407\u0001\u0000\u0000\u0000\u0411\u0093\u0001\u0000"
          + "\u0000\u0000\u0412\u0413\u0003\u0096K\u0000\u0413\u0414\u0003\u009cN\u0000"
          + "\u0414\u0415\u0005\u0005\u0000\u0000\u0415\u0417\u0003\u009cN\u0000\u0416"
          + "\u0418\u0003\u0098L\u0000\u0417\u0416\u0001\u0000\u0000\u0000\u0417\u0418"
          + "\u0001\u0000\u0000\u0000\u0418\u0421\u0001\u0000\u0000\u0000\u0419\u041a"
          + "\u0003\u009cN\u0000\u041a\u041b\u0005\u0001\u0000\u0000\u041b\u041d\u0003"
          + "\u009cN\u0000\u041c\u041e\u0003\u0098L\u0000\u041d\u041c\u0001\u0000\u0000"
          + "\u0000\u041d\u041e\u0001\u0000\u0000\u0000\u041e\u0420\u0001\u0000\u0000"
          + "\u0000\u041f\u0419\u0001\u0000\u0000\u0000\u0420\u0423\u0001\u0000\u0000"
          + "\u0000\u0421\u041f\u0001\u0000\u0000\u0000\u0421\u0422\u0001\u0000\u0000"
          + "\u0000\u0422\u0424\u0001\u0000\u0000\u0000\u0423\u0421\u0001\u0000\u0000"
          + "\u0000\u0424\u0425\u0005\u0006\u0000\u0000\u0425\u0426\u0003\u009cN\u0000"
          + "\u0426\u0095\u0001\u0000\u0000\u0000\u0427\u0428\u0003\\.\u0000\u0428"
          + "\u0097\u0001\u0000\u0000\u0000\u0429\u042a\u0003\u009aM\u0000\u042a\u042b"
          + "\u0003\u009cN\u0000\u042b\u042c\u0005\u0003\u0000\u0000\u042c\u042d\u0003"
          + "\u009cN\u0000\u042d\u0433\u0003P(\u0000\u042e\u042f\u0003\u009cN\u0000"
          + "\u042f\u0430\u0003P(\u0000\u0430\u0432\u0001\u0000\u0000\u0000\u0431\u042e"
          + "\u0001\u0000\u0000\u0000\u0432\u0435\u0001\u0000\u0000\u0000\u0433\u0431"
          + "\u0001\u0000\u0000\u0000\u0433\u0434\u0001\u0000\u0000\u0000\u0434\u0099"
          + "\u0001\u0000\u0000\u0000\u0435\u0433\u0001\u0000\u0000\u0000\u0436\u0437"
          + "\u0007\u0007\u0000\u0000\u0437\u009b\u0001\u0000\u0000\u0000\u0438\u043a"
          + "\u0007\b\u0000\u0000\u0439\u0438\u0001\u0000\u0000\u0000\u043a\u043d\u0001"
          + "\u0000\u0000\u0000\u043b\u0439\u0001\u0000\u0000\u0000\u043b\u043c\u0001"
          + "\u0000\u0000\u0000\u043c\u009d\u0001\u0000\u0000\u0000\u043d\u043b\u0001"
          + "\u0000\u0000\u0000f\u00a2\u00a7\u00ae\u00b3\u00c2\u00de\u00e5\u00f1\u00f6"
          + "\u0107\u010a\u010f\u011a\u0124\u0127\u0132\u013d\u0142\u0147\u014b\u015c"
          + "\u0167\u0172\u0176\u017d\u017f\u0187\u0189\u018b\u018e\u0194\u019b\u01a5"
          + "\u01ad\u01b1\u01b7\u01bb\u01c9\u01ce\u01dc\u01e6\u01f1\u01f7\u01ff\u0205"
          + "\u020a\u0210\u0218\u021c\u0224\u022b\u0235\u0237\u023b\u0240\u025d\u026f"
          + "\u0274\u0279\u027e\u02a3\u02a5\u02af\u02b1\u02bb\u02bd\u02c3\u02ca\u02d0"
          + "\u02df\u02ec\u02ee\u02fe\u0305\u0315\u031f\u0333\u0341\u034b\u034d\u036a"
          + "\u0377\u0379\u038e\u0395\u039a\u039e\u03ae\u03c0\u03c7\u03d3\u03db\u03e7"
          + "\u03f5\u0403\u040d\u0410\u0417\u041d\u0421\u0433\u043b";
  public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

  static {
    _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
    for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
      _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
    }
  }
}
