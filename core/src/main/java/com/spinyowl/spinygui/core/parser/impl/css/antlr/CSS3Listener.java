// Generated from java-escape by ANTLR 4.11.1
package com.spinyowl.spinygui.core.parser.impl.css.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/** This interface defines a complete listener for a parse tree produced by {@link CSS3Parser}. */
public interface CSS3Listener extends ParseTreeListener {
  /**
   * Enter a parse tree produced by {@link CSS3Parser#stylesheet}.
   *
   * @param ctx the parse tree
   */
  void enterStylesheet(CSS3Parser.StylesheetContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#stylesheet}.
   *
   * @param ctx the parse tree
   */
  void exitStylesheet(CSS3Parser.StylesheetContext ctx);
  /**
   * Enter a parse tree produced by the {@code goodCharset} labeled alternative in {@link
   * CSS3Parser#charset}.
   *
   * @param ctx the parse tree
   */
  void enterGoodCharset(CSS3Parser.GoodCharsetContext ctx);
  /**
   * Exit a parse tree produced by the {@code goodCharset} labeled alternative in {@link
   * CSS3Parser#charset}.
   *
   * @param ctx the parse tree
   */
  void exitGoodCharset(CSS3Parser.GoodCharsetContext ctx);
  /**
   * Enter a parse tree produced by the {@code badCharset} labeled alternative in {@link
   * CSS3Parser#charset}.
   *
   * @param ctx the parse tree
   */
  void enterBadCharset(CSS3Parser.BadCharsetContext ctx);
  /**
   * Exit a parse tree produced by the {@code badCharset} labeled alternative in {@link
   * CSS3Parser#charset}.
   *
   * @param ctx the parse tree
   */
  void exitBadCharset(CSS3Parser.BadCharsetContext ctx);
  /**
   * Enter a parse tree produced by the {@code goodImport} labeled alternative in {@link
   * CSS3Parser#imports}.
   *
   * @param ctx the parse tree
   */
  void enterGoodImport(CSS3Parser.GoodImportContext ctx);
  /**
   * Exit a parse tree produced by the {@code goodImport} labeled alternative in {@link
   * CSS3Parser#imports}.
   *
   * @param ctx the parse tree
   */
  void exitGoodImport(CSS3Parser.GoodImportContext ctx);
  /**
   * Enter a parse tree produced by the {@code badImport} labeled alternative in {@link
   * CSS3Parser#imports}.
   *
   * @param ctx the parse tree
   */
  void enterBadImport(CSS3Parser.BadImportContext ctx);
  /**
   * Exit a parse tree produced by the {@code badImport} labeled alternative in {@link
   * CSS3Parser#imports}.
   *
   * @param ctx the parse tree
   */
  void exitBadImport(CSS3Parser.BadImportContext ctx);
  /**
   * Enter a parse tree produced by the {@code goodNamespace} labeled alternative in {@link
   * CSS3Parser#namespace_}.
   *
   * @param ctx the parse tree
   */
  void enterGoodNamespace(CSS3Parser.GoodNamespaceContext ctx);
  /**
   * Exit a parse tree produced by the {@code goodNamespace} labeled alternative in {@link
   * CSS3Parser#namespace_}.
   *
   * @param ctx the parse tree
   */
  void exitGoodNamespace(CSS3Parser.GoodNamespaceContext ctx);
  /**
   * Enter a parse tree produced by the {@code badNamespace} labeled alternative in {@link
   * CSS3Parser#namespace_}.
   *
   * @param ctx the parse tree
   */
  void enterBadNamespace(CSS3Parser.BadNamespaceContext ctx);
  /**
   * Exit a parse tree produced by the {@code badNamespace} labeled alternative in {@link
   * CSS3Parser#namespace_}.
   *
   * @param ctx the parse tree
   */
  void exitBadNamespace(CSS3Parser.BadNamespaceContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#namespacePrefix}.
   *
   * @param ctx the parse tree
   */
  void enterNamespacePrefix(CSS3Parser.NamespacePrefixContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#namespacePrefix}.
   *
   * @param ctx the parse tree
   */
  void exitNamespacePrefix(CSS3Parser.NamespacePrefixContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#media}.
   *
   * @param ctx the parse tree
   */
  void enterMedia(CSS3Parser.MediaContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#media}.
   *
   * @param ctx the parse tree
   */
  void exitMedia(CSS3Parser.MediaContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#mediaQueryList}.
   *
   * @param ctx the parse tree
   */
  void enterMediaQueryList(CSS3Parser.MediaQueryListContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#mediaQueryList}.
   *
   * @param ctx the parse tree
   */
  void exitMediaQueryList(CSS3Parser.MediaQueryListContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#mediaQuery}.
   *
   * @param ctx the parse tree
   */
  void enterMediaQuery(CSS3Parser.MediaQueryContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#mediaQuery}.
   *
   * @param ctx the parse tree
   */
  void exitMediaQuery(CSS3Parser.MediaQueryContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#mediaType}.
   *
   * @param ctx the parse tree
   */
  void enterMediaType(CSS3Parser.MediaTypeContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#mediaType}.
   *
   * @param ctx the parse tree
   */
  void exitMediaType(CSS3Parser.MediaTypeContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#mediaExpression}.
   *
   * @param ctx the parse tree
   */
  void enterMediaExpression(CSS3Parser.MediaExpressionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#mediaExpression}.
   *
   * @param ctx the parse tree
   */
  void exitMediaExpression(CSS3Parser.MediaExpressionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#mediaFeature}.
   *
   * @param ctx the parse tree
   */
  void enterMediaFeature(CSS3Parser.MediaFeatureContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#mediaFeature}.
   *
   * @param ctx the parse tree
   */
  void exitMediaFeature(CSS3Parser.MediaFeatureContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#page}.
   *
   * @param ctx the parse tree
   */
  void enterPage(CSS3Parser.PageContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#page}.
   *
   * @param ctx the parse tree
   */
  void exitPage(CSS3Parser.PageContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#pseudoPage}.
   *
   * @param ctx the parse tree
   */
  void enterPseudoPage(CSS3Parser.PseudoPageContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#pseudoPage}.
   *
   * @param ctx the parse tree
   */
  void exitPseudoPage(CSS3Parser.PseudoPageContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#selectorGroup}.
   *
   * @param ctx the parse tree
   */
  void enterSelectorGroup(CSS3Parser.SelectorGroupContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#selectorGroup}.
   *
   * @param ctx the parse tree
   */
  void exitSelectorGroup(CSS3Parser.SelectorGroupContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#selector}.
   *
   * @param ctx the parse tree
   */
  void enterSelector(CSS3Parser.SelectorContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#selector}.
   *
   * @param ctx the parse tree
   */
  void exitSelector(CSS3Parser.SelectorContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#combinator}.
   *
   * @param ctx the parse tree
   */
  void enterCombinator(CSS3Parser.CombinatorContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#combinator}.
   *
   * @param ctx the parse tree
   */
  void exitCombinator(CSS3Parser.CombinatorContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#simpleSelectorSequence}.
   *
   * @param ctx the parse tree
   */
  void enterSimpleSelectorSequence(CSS3Parser.SimpleSelectorSequenceContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#simpleSelectorSequence}.
   *
   * @param ctx the parse tree
   */
  void exitSimpleSelectorSequence(CSS3Parser.SimpleSelectorSequenceContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#typeSelector}.
   *
   * @param ctx the parse tree
   */
  void enterTypeSelector(CSS3Parser.TypeSelectorContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#typeSelector}.
   *
   * @param ctx the parse tree
   */
  void exitTypeSelector(CSS3Parser.TypeSelectorContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#typeNamespacePrefix}.
   *
   * @param ctx the parse tree
   */
  void enterTypeNamespacePrefix(CSS3Parser.TypeNamespacePrefixContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#typeNamespacePrefix}.
   *
   * @param ctx the parse tree
   */
  void exitTypeNamespacePrefix(CSS3Parser.TypeNamespacePrefixContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#elementName}.
   *
   * @param ctx the parse tree
   */
  void enterElementName(CSS3Parser.ElementNameContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#elementName}.
   *
   * @param ctx the parse tree
   */
  void exitElementName(CSS3Parser.ElementNameContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#universal}.
   *
   * @param ctx the parse tree
   */
  void enterUniversal(CSS3Parser.UniversalContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#universal}.
   *
   * @param ctx the parse tree
   */
  void exitUniversal(CSS3Parser.UniversalContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#className}.
   *
   * @param ctx the parse tree
   */
  void enterClassName(CSS3Parser.ClassNameContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#className}.
   *
   * @param ctx the parse tree
   */
  void exitClassName(CSS3Parser.ClassNameContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#attrib}.
   *
   * @param ctx the parse tree
   */
  void enterAttrib(CSS3Parser.AttribContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#attrib}.
   *
   * @param ctx the parse tree
   */
  void exitAttrib(CSS3Parser.AttribContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#pseudo}.
   *
   * @param ctx the parse tree
   */
  void enterPseudo(CSS3Parser.PseudoContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#pseudo}.
   *
   * @param ctx the parse tree
   */
  void exitPseudo(CSS3Parser.PseudoContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#functionalPseudo}.
   *
   * @param ctx the parse tree
   */
  void enterFunctionalPseudo(CSS3Parser.FunctionalPseudoContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#functionalPseudo}.
   *
   * @param ctx the parse tree
   */
  void exitFunctionalPseudo(CSS3Parser.FunctionalPseudoContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#expression}.
   *
   * @param ctx the parse tree
   */
  void enterExpression(CSS3Parser.ExpressionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#expression}.
   *
   * @param ctx the parse tree
   */
  void exitExpression(CSS3Parser.ExpressionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#negation}.
   *
   * @param ctx the parse tree
   */
  void enterNegation(CSS3Parser.NegationContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#negation}.
   *
   * @param ctx the parse tree
   */
  void exitNegation(CSS3Parser.NegationContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#negationArg}.
   *
   * @param ctx the parse tree
   */
  void enterNegationArg(CSS3Parser.NegationArgContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#negationArg}.
   *
   * @param ctx the parse tree
   */
  void exitNegationArg(CSS3Parser.NegationArgContext ctx);
  /**
   * Enter a parse tree produced by the {@code goodOperator} labeled alternative in {@link
   * CSS3Parser#operator_}.
   *
   * @param ctx the parse tree
   */
  void enterGoodOperator(CSS3Parser.GoodOperatorContext ctx);
  /**
   * Exit a parse tree produced by the {@code goodOperator} labeled alternative in {@link
   * CSS3Parser#operator_}.
   *
   * @param ctx the parse tree
   */
  void exitGoodOperator(CSS3Parser.GoodOperatorContext ctx);
  /**
   * Enter a parse tree produced by the {@code badOperator} labeled alternative in {@link
   * CSS3Parser#operator_}.
   *
   * @param ctx the parse tree
   */
  void enterBadOperator(CSS3Parser.BadOperatorContext ctx);
  /**
   * Exit a parse tree produced by the {@code badOperator} labeled alternative in {@link
   * CSS3Parser#operator_}.
   *
   * @param ctx the parse tree
   */
  void exitBadOperator(CSS3Parser.BadOperatorContext ctx);
  /**
   * Enter a parse tree produced by the {@code goodProperty} labeled alternative in {@link
   * CSS3Parser#property_}.
   *
   * @param ctx the parse tree
   */
  void enterGoodProperty(CSS3Parser.GoodPropertyContext ctx);
  /**
   * Exit a parse tree produced by the {@code goodProperty} labeled alternative in {@link
   * CSS3Parser#property_}.
   *
   * @param ctx the parse tree
   */
  void exitGoodProperty(CSS3Parser.GoodPropertyContext ctx);
  /**
   * Enter a parse tree produced by the {@code badProperty} labeled alternative in {@link
   * CSS3Parser#property_}.
   *
   * @param ctx the parse tree
   */
  void enterBadProperty(CSS3Parser.BadPropertyContext ctx);
  /**
   * Exit a parse tree produced by the {@code badProperty} labeled alternative in {@link
   * CSS3Parser#property_}.
   *
   * @param ctx the parse tree
   */
  void exitBadProperty(CSS3Parser.BadPropertyContext ctx);
  /**
   * Enter a parse tree produced by the {@code knownRuleset} labeled alternative in {@link
   * CSS3Parser#ruleset}.
   *
   * @param ctx the parse tree
   */
  void enterKnownRuleset(CSS3Parser.KnownRulesetContext ctx);
  /**
   * Exit a parse tree produced by the {@code knownRuleset} labeled alternative in {@link
   * CSS3Parser#ruleset}.
   *
   * @param ctx the parse tree
   */
  void exitKnownRuleset(CSS3Parser.KnownRulesetContext ctx);
  /**
   * Enter a parse tree produced by the {@code unknownRuleset} labeled alternative in {@link
   * CSS3Parser#ruleset}.
   *
   * @param ctx the parse tree
   */
  void enterUnknownRuleset(CSS3Parser.UnknownRulesetContext ctx);
  /**
   * Exit a parse tree produced by the {@code unknownRuleset} labeled alternative in {@link
   * CSS3Parser#ruleset}.
   *
   * @param ctx the parse tree
   */
  void exitUnknownRuleset(CSS3Parser.UnknownRulesetContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#declarationList}.
   *
   * @param ctx the parse tree
   */
  void enterDeclarationList(CSS3Parser.DeclarationListContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#declarationList}.
   *
   * @param ctx the parse tree
   */
  void exitDeclarationList(CSS3Parser.DeclarationListContext ctx);
  /**
   * Enter a parse tree produced by the {@code knownDeclaration} labeled alternative in {@link
   * CSS3Parser#declaration}.
   *
   * @param ctx the parse tree
   */
  void enterKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx);
  /**
   * Exit a parse tree produced by the {@code knownDeclaration} labeled alternative in {@link
   * CSS3Parser#declaration}.
   *
   * @param ctx the parse tree
   */
  void exitKnownDeclaration(CSS3Parser.KnownDeclarationContext ctx);
  /**
   * Enter a parse tree produced by the {@code unknownDeclaration} labeled alternative in {@link
   * CSS3Parser#declaration}.
   *
   * @param ctx the parse tree
   */
  void enterUnknownDeclaration(CSS3Parser.UnknownDeclarationContext ctx);
  /**
   * Exit a parse tree produced by the {@code unknownDeclaration} labeled alternative in {@link
   * CSS3Parser#declaration}.
   *
   * @param ctx the parse tree
   */
  void exitUnknownDeclaration(CSS3Parser.UnknownDeclarationContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#prio}.
   *
   * @param ctx the parse tree
   */
  void enterPrio(CSS3Parser.PrioContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#prio}.
   *
   * @param ctx the parse tree
   */
  void exitPrio(CSS3Parser.PrioContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#value}.
   *
   * @param ctx the parse tree
   */
  void enterValue(CSS3Parser.ValueContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#value}.
   *
   * @param ctx the parse tree
   */
  void exitValue(CSS3Parser.ValueContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#expr}.
   *
   * @param ctx the parse tree
   */
  void enterExpr(CSS3Parser.ExprContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#expr}.
   *
   * @param ctx the parse tree
   */
  void exitExpr(CSS3Parser.ExprContext ctx);
  /**
   * Enter a parse tree produced by the {@code knownTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   */
  void enterKnownTerm(CSS3Parser.KnownTermContext ctx);
  /**
   * Exit a parse tree produced by the {@code knownTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   */
  void exitKnownTerm(CSS3Parser.KnownTermContext ctx);
  /**
   * Enter a parse tree produced by the {@code unknownTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   */
  void enterUnknownTerm(CSS3Parser.UnknownTermContext ctx);
  /**
   * Exit a parse tree produced by the {@code unknownTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   */
  void exitUnknownTerm(CSS3Parser.UnknownTermContext ctx);
  /**
   * Enter a parse tree produced by the {@code badTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   */
  void enterBadTerm(CSS3Parser.BadTermContext ctx);
  /**
   * Exit a parse tree produced by the {@code badTerm} labeled alternative in {@link
   * CSS3Parser#term}.
   *
   * @param ctx the parse tree
   */
  void exitBadTerm(CSS3Parser.BadTermContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#function_}.
   *
   * @param ctx the parse tree
   */
  void enterFunction_(CSS3Parser.Function_Context ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#function_}.
   *
   * @param ctx the parse tree
   */
  void exitFunction_(CSS3Parser.Function_Context ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#dxImageTransform}.
   *
   * @param ctx the parse tree
   */
  void enterDxImageTransform(CSS3Parser.DxImageTransformContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#dxImageTransform}.
   *
   * @param ctx the parse tree
   */
  void exitDxImageTransform(CSS3Parser.DxImageTransformContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#hexcolor}.
   *
   * @param ctx the parse tree
   */
  void enterHexcolor(CSS3Parser.HexcolorContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#hexcolor}.
   *
   * @param ctx the parse tree
   */
  void exitHexcolor(CSS3Parser.HexcolorContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#number}.
   *
   * @param ctx the parse tree
   */
  void enterNumber(CSS3Parser.NumberContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#number}.
   *
   * @param ctx the parse tree
   */
  void exitNumber(CSS3Parser.NumberContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#percentage}.
   *
   * @param ctx the parse tree
   */
  void enterPercentage(CSS3Parser.PercentageContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#percentage}.
   *
   * @param ctx the parse tree
   */
  void exitPercentage(CSS3Parser.PercentageContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#dimension}.
   *
   * @param ctx the parse tree
   */
  void enterDimension(CSS3Parser.DimensionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#dimension}.
   *
   * @param ctx the parse tree
   */
  void exitDimension(CSS3Parser.DimensionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#unknownDimension}.
   *
   * @param ctx the parse tree
   */
  void enterUnknownDimension(CSS3Parser.UnknownDimensionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#unknownDimension}.
   *
   * @param ctx the parse tree
   */
  void exitUnknownDimension(CSS3Parser.UnknownDimensionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#any_}.
   *
   * @param ctx the parse tree
   */
  void enterAny_(CSS3Parser.Any_Context ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#any_}.
   *
   * @param ctx the parse tree
   */
  void exitAny_(CSS3Parser.Any_Context ctx);
  /**
   * Enter a parse tree produced by the {@code unknownAtRule} labeled alternative in {@link
   * CSS3Parser#atRule}.
   *
   * @param ctx the parse tree
   */
  void enterUnknownAtRule(CSS3Parser.UnknownAtRuleContext ctx);
  /**
   * Exit a parse tree produced by the {@code unknownAtRule} labeled alternative in {@link
   * CSS3Parser#atRule}.
   *
   * @param ctx the parse tree
   */
  void exitUnknownAtRule(CSS3Parser.UnknownAtRuleContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#atKeyword}.
   *
   * @param ctx the parse tree
   */
  void enterAtKeyword(CSS3Parser.AtKeywordContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#atKeyword}.
   *
   * @param ctx the parse tree
   */
  void exitAtKeyword(CSS3Parser.AtKeywordContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#unused}.
   *
   * @param ctx the parse tree
   */
  void enterUnused(CSS3Parser.UnusedContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#unused}.
   *
   * @param ctx the parse tree
   */
  void exitUnused(CSS3Parser.UnusedContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#block}.
   *
   * @param ctx the parse tree
   */
  void enterBlock(CSS3Parser.BlockContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#block}.
   *
   * @param ctx the parse tree
   */
  void exitBlock(CSS3Parser.BlockContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#nestedStatement}.
   *
   * @param ctx the parse tree
   */
  void enterNestedStatement(CSS3Parser.NestedStatementContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#nestedStatement}.
   *
   * @param ctx the parse tree
   */
  void exitNestedStatement(CSS3Parser.NestedStatementContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#groupRuleBody}.
   *
   * @param ctx the parse tree
   */
  void enterGroupRuleBody(CSS3Parser.GroupRuleBodyContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#groupRuleBody}.
   *
   * @param ctx the parse tree
   */
  void exitGroupRuleBody(CSS3Parser.GroupRuleBodyContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsRule}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsRule(CSS3Parser.SupportsRuleContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsRule}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsRule(CSS3Parser.SupportsRuleContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsCondition}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsCondition(CSS3Parser.SupportsConditionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsCondition}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsCondition(CSS3Parser.SupportsConditionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsConditionInParens}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsConditionInParens(CSS3Parser.SupportsConditionInParensContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsConditionInParens}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsConditionInParens(CSS3Parser.SupportsConditionInParensContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsNegation}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsNegation(CSS3Parser.SupportsNegationContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsNegation}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsNegation(CSS3Parser.SupportsNegationContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsConjunction}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsConjunction(CSS3Parser.SupportsConjunctionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsConjunction}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsConjunction(CSS3Parser.SupportsConjunctionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsDisjunction}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsDisjunction(CSS3Parser.SupportsDisjunctionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsDisjunction}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsDisjunction(CSS3Parser.SupportsDisjunctionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#supportsDeclarationCondition}.
   *
   * @param ctx the parse tree
   */
  void enterSupportsDeclarationCondition(CSS3Parser.SupportsDeclarationConditionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#supportsDeclarationCondition}.
   *
   * @param ctx the parse tree
   */
  void exitSupportsDeclarationCondition(CSS3Parser.SupportsDeclarationConditionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#generalEnclosed}.
   *
   * @param ctx the parse tree
   */
  void enterGeneralEnclosed(CSS3Parser.GeneralEnclosedContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#generalEnclosed}.
   *
   * @param ctx the parse tree
   */
  void exitGeneralEnclosed(CSS3Parser.GeneralEnclosedContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#var_}.
   *
   * @param ctx the parse tree
   */
  void enterVar_(CSS3Parser.Var_Context ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#var_}.
   *
   * @param ctx the parse tree
   */
  void exitVar_(CSS3Parser.Var_Context ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#calc}.
   *
   * @param ctx the parse tree
   */
  void enterCalc(CSS3Parser.CalcContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#calc}.
   *
   * @param ctx the parse tree
   */
  void exitCalc(CSS3Parser.CalcContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#calcSum}.
   *
   * @param ctx the parse tree
   */
  void enterCalcSum(CSS3Parser.CalcSumContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#calcSum}.
   *
   * @param ctx the parse tree
   */
  void exitCalcSum(CSS3Parser.CalcSumContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#calcProduct}.
   *
   * @param ctx the parse tree
   */
  void enterCalcProduct(CSS3Parser.CalcProductContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#calcProduct}.
   *
   * @param ctx the parse tree
   */
  void exitCalcProduct(CSS3Parser.CalcProductContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#calcValue}.
   *
   * @param ctx the parse tree
   */
  void enterCalcValue(CSS3Parser.CalcValueContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#calcValue}.
   *
   * @param ctx the parse tree
   */
  void exitCalcValue(CSS3Parser.CalcValueContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#fontFaceRule}.
   *
   * @param ctx the parse tree
   */
  void enterFontFaceRule(CSS3Parser.FontFaceRuleContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#fontFaceRule}.
   *
   * @param ctx the parse tree
   */
  void exitFontFaceRule(CSS3Parser.FontFaceRuleContext ctx);
  /**
   * Enter a parse tree produced by the {@code knownFontFaceDeclaration} labeled alternative in
   * {@link CSS3Parser#fontFaceDeclaration}.
   *
   * @param ctx the parse tree
   */
  void enterKnownFontFaceDeclaration(CSS3Parser.KnownFontFaceDeclarationContext ctx);
  /**
   * Exit a parse tree produced by the {@code knownFontFaceDeclaration} labeled alternative in
   * {@link CSS3Parser#fontFaceDeclaration}.
   *
   * @param ctx the parse tree
   */
  void exitKnownFontFaceDeclaration(CSS3Parser.KnownFontFaceDeclarationContext ctx);
  /**
   * Enter a parse tree produced by the {@code unknownFontFaceDeclaration} labeled alternative in
   * {@link CSS3Parser#fontFaceDeclaration}.
   *
   * @param ctx the parse tree
   */
  void enterUnknownFontFaceDeclaration(CSS3Parser.UnknownFontFaceDeclarationContext ctx);
  /**
   * Exit a parse tree produced by the {@code unknownFontFaceDeclaration} labeled alternative in
   * {@link CSS3Parser#fontFaceDeclaration}.
   *
   * @param ctx the parse tree
   */
  void exitUnknownFontFaceDeclaration(CSS3Parser.UnknownFontFaceDeclarationContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#keyframesRule}.
   *
   * @param ctx the parse tree
   */
  void enterKeyframesRule(CSS3Parser.KeyframesRuleContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#keyframesRule}.
   *
   * @param ctx the parse tree
   */
  void exitKeyframesRule(CSS3Parser.KeyframesRuleContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#keyframesBlocks}.
   *
   * @param ctx the parse tree
   */
  void enterKeyframesBlocks(CSS3Parser.KeyframesBlocksContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#keyframesBlocks}.
   *
   * @param ctx the parse tree
   */
  void exitKeyframesBlocks(CSS3Parser.KeyframesBlocksContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#keyframeSelector}.
   *
   * @param ctx the parse tree
   */
  void enterKeyframeSelector(CSS3Parser.KeyframeSelectorContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#keyframeSelector}.
   *
   * @param ctx the parse tree
   */
  void exitKeyframeSelector(CSS3Parser.KeyframeSelectorContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#viewport}.
   *
   * @param ctx the parse tree
   */
  void enterViewport(CSS3Parser.ViewportContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#viewport}.
   *
   * @param ctx the parse tree
   */
  void exitViewport(CSS3Parser.ViewportContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#counterStyle}.
   *
   * @param ctx the parse tree
   */
  void enterCounterStyle(CSS3Parser.CounterStyleContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#counterStyle}.
   *
   * @param ctx the parse tree
   */
  void exitCounterStyle(CSS3Parser.CounterStyleContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#fontFeatureValuesRule}.
   *
   * @param ctx the parse tree
   */
  void enterFontFeatureValuesRule(CSS3Parser.FontFeatureValuesRuleContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#fontFeatureValuesRule}.
   *
   * @param ctx the parse tree
   */
  void exitFontFeatureValuesRule(CSS3Parser.FontFeatureValuesRuleContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#fontFamilyNameList}.
   *
   * @param ctx the parse tree
   */
  void enterFontFamilyNameList(CSS3Parser.FontFamilyNameListContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#fontFamilyNameList}.
   *
   * @param ctx the parse tree
   */
  void exitFontFamilyNameList(CSS3Parser.FontFamilyNameListContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#fontFamilyName}.
   *
   * @param ctx the parse tree
   */
  void enterFontFamilyName(CSS3Parser.FontFamilyNameContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#fontFamilyName}.
   *
   * @param ctx the parse tree
   */
  void exitFontFamilyName(CSS3Parser.FontFamilyNameContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#featureValueBlock}.
   *
   * @param ctx the parse tree
   */
  void enterFeatureValueBlock(CSS3Parser.FeatureValueBlockContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#featureValueBlock}.
   *
   * @param ctx the parse tree
   */
  void exitFeatureValueBlock(CSS3Parser.FeatureValueBlockContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#featureType}.
   *
   * @param ctx the parse tree
   */
  void enterFeatureType(CSS3Parser.FeatureTypeContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#featureType}.
   *
   * @param ctx the parse tree
   */
  void exitFeatureType(CSS3Parser.FeatureTypeContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#featureValueDefinition}.
   *
   * @param ctx the parse tree
   */
  void enterFeatureValueDefinition(CSS3Parser.FeatureValueDefinitionContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#featureValueDefinition}.
   *
   * @param ctx the parse tree
   */
  void exitFeatureValueDefinition(CSS3Parser.FeatureValueDefinitionContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#ident}.
   *
   * @param ctx the parse tree
   */
  void enterIdent(CSS3Parser.IdentContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#ident}.
   *
   * @param ctx the parse tree
   */
  void exitIdent(CSS3Parser.IdentContext ctx);
  /**
   * Enter a parse tree produced by {@link CSS3Parser#ws}.
   *
   * @param ctx the parse tree
   */
  void enterWs(CSS3Parser.WsContext ctx);
  /**
   * Exit a parse tree produced by {@link CSS3Parser#ws}.
   *
   * @param ctx the parse tree
   */
  void exitWs(CSS3Parser.WsContext ctx);
}