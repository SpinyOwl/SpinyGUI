package org.liquidengine.legui.core;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Document document = new Document("");

        DocumentType documentType = new DocumentType("html", "", "");
        Node html = new Element("html");
        Element body = new Element("body");

        ((Element) html).appendChild(new Element("head"));
        ((Element) html).appendChild(body);

        body.appendChild(new Element("div"));

        document.appendChild(documentType);
        document.appendChild(html);
        document.toString();
    }
}
