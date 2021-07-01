// Generated from
// C:/Users/Marvin/Documents/SpinyGui/SpinyGUI/core/src/main/resources/grammars\CSS3.g4 by ANTLR 4.7
package com.spinyowl.spinygui.core.parser.impl.css.parser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced by {@link
 * CSS3Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public interface CSS3Visitor<T> extends ParseTreeVisitor<T> {

  /**
   * Visit a parse tree produced by {@link CSS3Parser#stylesheet}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitStylesheet(CSS3Parser.StylesheetContext ctx);

  /**
   * Visit a parse tree produced by the {@code goodCharset} labeled alternative in {@link
   * CSS3Parser#charset}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGoodCharset(CSS3Parser.GoodCharsetContext ctx);

  /**
   * Visit a parse tree produced by the {@code badCharset} labeled alternative in {@link
   * CSS3Parser#charset}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBadCharset(CSS3Parser.BadCharsetContext ctx);

  /**
   * Visit a parse tree produced by the {@code goodImport} labeled alternative in {@link
   * CSS3Parser#imports}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGoodImport(CSS3Parser.GoodImportContext ctx);

  /**
   * Visit a parse tree produced by the {@code badImport} labeled alternative in {@link
   * CSS3Parser#imports}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBadImport(CSS3Parser.BadImportContext ctx);

  /**
   * Visit a parse tree produced by the {@code goodNamespace} labeled alternative in {@link
   * CSS3Parser#namespace}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGoodNamespace(CSS3Parser.GoodNamespaceContext ctx);

  /**
   * Visit a parse tree produced by the {@code badNamespace} labeled alternative in {@link
   * CSS3Parser#namespace}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBadNamespace(CSS3Parser.BadNamespaceContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#namespacePrefix}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNamespacePrefix(CSS3Parser.NamespacePrefixContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#media}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMedia(CSS3Parser.MediaContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#mediaQueryList}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMediaQueryList(CSS3Parser.MediaQueryListContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#mediaQuery}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMediaQuery(CSS3Parser.MediaQueryContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#mediaType}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMediaType(CSS3Parser.MediaTypeContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#mediaExpression}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMediaExpression(CSS3Parser.MediaExpressionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#mediaFeature}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitMediaFeature(CSS3Parser.MediaFeatureContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#page}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPage(CSS3Parser.PageContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#pseudoPage}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPseudoPage(CSS3Parser.PseudoPageContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#selectorGroup}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSelectorGroup(CSS3Parser.SelectorGroupContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#selector}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSelector(CSS3Parser.SelectorContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#combinator}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCombinator(CSS3Parser.CombinatorContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#simpleSelectorSequence}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSimpleSelectorSequence(CSS3Parser.SimpleSelectorSequenceContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#typeSelector}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTypeSelector(CSS3Parser.TypeSelectorContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#typeNamespacePrefix}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitTypeNamespacePrefix(CSS3Parser.TypeNamespacePrefixContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#elementName}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitElementName(CSS3Parser.ElementNameContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#universal}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUniversal(CSS3Parser.UniversalContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#className}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitClassName(CSS3Parser.ClassNameContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#attrib}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAttrib(CSS3Parser.AttribContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#pseudo}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPseudo(CSS3Parser.PseudoContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#functionalPseudo}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunctionalPseudo(CSS3Parser.FunctionalPseudoContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#expression}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpression(CSS3Parser.ExpressionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#negation}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNegation(CSS3Parser.NegationContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#negationArg}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNegationArg(CSS3Parser.NegationArgContext ctx);

  /**
   * Visit a parse tree produced by the {@code goodOperator} labeled alternative in {@link
   * CSS3Parser#operator}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGoodOperator(CSS3Parser.GoodOperatorContext ctx);

  /**
   * Visit a parse tree produced by the {@code badOperator} labeled alternative in {@link
   * CSS3Parser#operator}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBadOperator(CSS3Parser.BadOperatorContext ctx);

  /**
   * Visit a parse tree produced by the {@code goodProperty} labeled alternative in {@link
   * CSS3Parser#property}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGoodProperty(CSS3Parser.GoodPropertyContext ctx);

  /**
   * Visit a parse tree produced by the {@code badProperty} labeled alternative in {@link
   * CSS3Parser#property}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBadProperty(CSS3Parser.BadPropertyContext ctx);

  /**
   * Visit a parse tree produced by the {@code knownRuleset} labeled alternative in {@link
   * CSS3Parser#ruleset}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKnownRuleset(CSS3Parser.KnownRulesetContext ctx);

  /**
   * Visit a parse tree produced by the {@code unknownRuleset} labeled alternative in {@link
   * CSS3Parser#ruleset}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnknownRuleset(CSS3Parser.UnknownRulesetContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#declarationList}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDeclarationList(CSS3Parser.DeclarationListContext ctx);

  /**
   * Visit a parse tree produced by the {@code knownDeclaration} labeled alternative in {@link
   * CSS3Parser#declaration}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx);

  /**
   * Visit a parse tree produced by the {@code unknownDeclaration} labeled alternative in {@link
   * CSS3Parser#declaration}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnknownDeclaration(CSS3Parser.UnknownDeclarationContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#prio}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPrio(CSS3Parser.PrioContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#value}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitValue(CSS3Parser.ValueContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#expr}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitExpr(CSS3Parser.ExprContext ctx);

  /**
   * Visit a parse tree produced by the {@code knownTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKnownTerm(CSS3Parser.KnownTermContext ctx);

  /**
   * Visit a parse tree produced by the {@code unknownTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnknownTerm(CSS3Parser.UnknownTermContext ctx);

  /**
   * Visit a parse tree produced by the {@code badTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBadTerm(CSS3Parser.BadTermContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#function}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFunction(CSS3Parser.FunctionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#dxImageTransform}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDxImageTransform(CSS3Parser.DxImageTransformContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#hexcolor}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitHexcolor(CSS3Parser.HexcolorContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#number}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNumber(CSS3Parser.NumberContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#percentage}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitPercentage(CSS3Parser.PercentageContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#dimension}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitDimension(CSS3Parser.DimensionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#unknownDimension}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnknownDimension(CSS3Parser.UnknownDimensionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#any}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAny(CSS3Parser.AnyContext ctx);

  /**
   * Visit a parse tree produced by the {@code unknownAtRule} labeled alternative in {@link
   * CSS3Parser#atRule}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnknownAtRule(CSS3Parser.UnknownAtRuleContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#atKeyword}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitAtKeyword(CSS3Parser.AtKeywordContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#unused}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnused(CSS3Parser.UnusedContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#block}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitBlock(CSS3Parser.BlockContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#nestedStatement}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitNestedStatement(CSS3Parser.NestedStatementContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#groupRuleBody}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGroupRuleBody(CSS3Parser.GroupRuleBodyContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsRule}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsRule(CSS3Parser.SupportsRuleContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsCondition}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsCondition(CSS3Parser.SupportsConditionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsConditionInParens}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsConditionInParens(CSS3Parser.SupportsConditionInParensContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsNegation}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsNegation(CSS3Parser.SupportsNegationContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsConjunction}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsConjunction(CSS3Parser.SupportsConjunctionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsDisjunction}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsDisjunction(CSS3Parser.SupportsDisjunctionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#supportsDeclarationCondition}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitSupportsDeclarationCondition(CSS3Parser.SupportsDeclarationConditionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#generalEnclosed}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitGeneralEnclosed(CSS3Parser.GeneralEnclosedContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#var}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitVar(CSS3Parser.VarContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#calc}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCalc(CSS3Parser.CalcContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#calcSum}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCalcSum(CSS3Parser.CalcSumContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#calcProduct}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCalcProduct(CSS3Parser.CalcProductContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#calcValue}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCalcValue(CSS3Parser.CalcValueContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#fontFaceRule}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFontFaceRule(CSS3Parser.FontFaceRuleContext ctx);

  /**
   * Visit a parse tree produced by the {@code knownFontFaceDeclaration} labeled alternative in
   * {@link CSS3Parser#fontFaceDeclaration}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKnownFontFaceDeclaration(CSS3Parser.KnownFontFaceDeclarationContext ctx);

  /**
   * Visit a parse tree produced by the {@code unknownFontFaceDeclaration} labeled alternative in
   * {@link CSS3Parser#fontFaceDeclaration}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitUnknownFontFaceDeclaration(CSS3Parser.UnknownFontFaceDeclarationContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#keyframesRule}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKeyframesRule(CSS3Parser.KeyframesRuleContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#keyframesBlocks}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKeyframesBlocks(CSS3Parser.KeyframesBlocksContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#keyframeSelector}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitKeyframeSelector(CSS3Parser.KeyframeSelectorContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#viewport}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitViewport(CSS3Parser.ViewportContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#counterStyle}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitCounterStyle(CSS3Parser.CounterStyleContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#fontFeatureValuesRule}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFontFeatureValuesRule(CSS3Parser.FontFeatureValuesRuleContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#fontFamilyNameList}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFontFamilyNameList(CSS3Parser.FontFamilyNameListContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#fontFamilyName}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFontFamilyName(CSS3Parser.FontFamilyNameContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#featureValueBlock}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFeatureValueBlock(CSS3Parser.FeatureValueBlockContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#featureType}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFeatureType(CSS3Parser.FeatureTypeContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#featureValueDefinition}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitFeatureValueDefinition(CSS3Parser.FeatureValueDefinitionContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#ident}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitIdent(CSS3Parser.IdentContext ctx);

  /**
   * Visit a parse tree produced by {@link CSS3Parser#ws}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  T visitWs(CSS3Parser.WsContext ctx);
}
