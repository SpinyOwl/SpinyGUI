package com.spinyowl.spinygui.core.converter.css3.visitor;

import com.spinyowl.spinygui.core.converter.css3.CSS3BaseVisitor;
import com.spinyowl.spinygui.core.converter.css3.CSS3Parser;
import com.spinyowl.spinygui.core.converter.css3.StyleReflectionHandler;
import com.spinyowl.spinygui.core.style.selector.ClassNameSelector;
import com.spinyowl.spinygui.core.style.selector.StyleSelector;
import com.spinyowl.spinygui.core.style.selector.TypeSelector;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class SelectorVisitor extends CSS3BaseVisitor<List<StyleSelector>> {

    @Override
    public List<StyleSelector> visitSelectorGroup(CSS3Parser.SelectorGroupContext ctx) {

        List<StyleSelector> selectorVisitors = new ArrayList<>();

        for (CSS3Parser.SelectorContext selectorContext : ctx.selector()) {

            var selectors = visit(selectorContext);
            if (selectors != null)
                selectorVisitors.addAll(selectors);
        }

        return selectorVisitors;
    }

    @Override
    public List<StyleSelector> visitSelector(CSS3Parser.SelectorContext ctx) {
        var list = new ArrayList<StyleSelector>();
        var firstSelectorSequence = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(0)).get(0);
        for (int i = 1; i < ctx.simpleSelectorSequence().size(); i++) {
            final var selectorSequence = visitSimpleSelectorSequence(ctx.simpleSelectorSequence(i)).get(0);

            if (ctx.combinator(i - 1).Space() != null) {
                firstSelectorSequence = selectorSequence.child(firstSelectorSequence);
            } else if (ctx.combinator(i - 1).Greater() != null) {
                firstSelectorSequence = selectorSequence.immediateChild(firstSelectorSequence);
            } else if (ctx.combinator(i - 1).Plus() != null) {
                firstSelectorSequence = selectorSequence.immediateNext(firstSelectorSequence);
            } else if (ctx.combinator(i - 1).Tilde() != null) {
                //TODO: General Sibling Selector
            }
        }
        list.add(firstSelectorSequence);
        return list;
    }

    @Override
    public List<StyleSelector> visitSimpleSelectorSequence(CSS3Parser.SimpleSelectorSequenceContext ctx) {
        var list = new ArrayList<StyleSelector>();

        StyleSelector current = null;
        for (ParseTree child : ctx.children) {
            var s = visit(child).get(0);
            if (current == null) current = s;
            else current = current.and(s);
        }
        list.add(current);

        return list;
    }

    @Override
    public List<StyleSelector> visitTypeSelector(CSS3Parser.TypeSelectorContext ctx) {
        var list = new ArrayList<StyleSelector>();
        var clazz = StyleReflectionHandler.getTypeSelector(ctx.getText());
        list.add(new TypeSelector(clazz));
        return list;
    }

    @Override
    public List<StyleSelector> visitPseudo(CSS3Parser.PseudoContext ctx) {
        var list = new ArrayList<StyleSelector>();
        var clazz = StyleReflectionHandler.getPseudoSelector(ctx.getText());
        try {
            list.add((StyleSelector) clazz.getConstructor().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<StyleSelector> visitClassName(CSS3Parser.ClassNameContext ctx) {
        var list = new ArrayList<StyleSelector>();

        var clazz = ctx.ident().getText();

        list.add(new ClassNameSelector(clazz));

        return list;
    }
}
