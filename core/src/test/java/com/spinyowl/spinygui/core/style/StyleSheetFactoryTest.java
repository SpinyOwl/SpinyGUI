package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.converter.css3.StyleSheetException;
import org.junit.Test;

import static org.junit.Assert.*;

public class StyleSheetFactoryTest {

    @Test
    public void createFromCSS() throws StyleSheetException {

        String css = "container > button:hover" +
                "{" +
                "background: red;" +
                "}";



        var stylesheet = StyleSheetFactory.createFromCSS(css);



    }
}