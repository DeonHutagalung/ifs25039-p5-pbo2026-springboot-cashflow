package org.delcom.ifs25039springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;

/** Dialog dasar: header biru primer + badge, footer abu-abu dengan tombol di kanan. */
public class AppDialog extends Dialog {

    protected final Div body = new Div();

    public AppDialog(String title, String badge) {
        addClassName("cf-dialog");
        H2 heading = new H2(title);
        heading.addClassName("cf-dialog-title");
        Span tag = new Span(badge);
        tag.addClassName("cf-dialog-badge");
        getHeader().add(heading, tag);
        body.addClassName("cf-dialog-body");
        add(body);
    }

    protected Button closeButton(String text) {
        Button button = new Button(text, e -> close());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }
}
