package org.delcom.ifs25039springbootcashflow.framework.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;

/** Gaya grid yang konsisten. */
final class GridStyles {

    private GridStyles() {
    }

    static void apply(Grid<?> grid, String ariaLabel) {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setWidthFull();
        grid.setHeight("26rem");
        grid.getElement().setAttribute("aria-label", ariaLabel);
    }
}
