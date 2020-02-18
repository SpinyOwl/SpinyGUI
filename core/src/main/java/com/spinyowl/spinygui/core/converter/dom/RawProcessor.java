package com.spinyowl.spinygui.core.converter.dom;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.util.NamespaceStack;

import java.io.IOException;
import java.io.Writer;

/**
 * Used only for marshalling node to xml.
 */
public class RawProcessor extends AbstractXMLOutputProcessor {

    public static final String PREFORMATTED_ATTRIBUTE = "_pre";

    //TODO implement preformatted style search - node should be updated by style engine so we can check if it contains pre style

    @Override
    public void process(Writer out, Format format, Element element) throws IOException {
        Format formatToUse = format;
        if (Boolean.parseBoolean(element.getAttributeValue(PREFORMATTED_ATTRIBUTE))) {
            formatToUse = Format.getRawFormat();
        }
        super.process(out, formatToUse, element);
    }

    @Override
    protected void printElement(Writer out, FormatStack fstack, NamespaceStack nstack, Element element) throws IOException {
        if (Boolean.parseBoolean(element.getAttributeValue(PREFORMATTED_ATTRIBUTE))) {
            fstack.setTextMode(Format.TextMode.PRESERVE);
        }
        super.printElement(out, fstack, nstack, element);
    }
}
