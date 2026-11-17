package org.delcom.ifs25039springbootcashflow.framework.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

/** Kartu konten halaman. */
public class PageCard extends Div {

    public PageCard(String title) {
        addClassName("cf-card");
        if (title != null) {
            add(new H2(title));
        }
    }

    public PageCard(String title, Component... content) {
        this(title);
        add(content);
    }
}
