package org.delcom.ifs25039springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;

/** Modal zoom: klik gambar untuk toggle 1.0x / 1.8x; tombol Perkecil, Perbesar, Tutup di kanan footer. */
public class ImagePreviewDialog extends AppDialog {

    private static final double MIN_ZOOM = 1.0;
    private static final double MAX_ZOOM = 3.0;
    private static final double STEP = 0.4;
    private static final double TOGGLE_ZOOM = 1.8;

    private final Image image;
    private double zoom = MIN_ZOOM;

    public ImagePreviewDialog(String url, String altText) {
        super("Bukti transaksi", "Pratinjau");
        image = new Image(url, altText);
        image.addClickListener(e -> setZoom(zoom > MIN_ZOOM ? MIN_ZOOM : TOGGLE_ZOOM));
        Div box = new Div(image);
        box.addClassName("cf-zoom-box");
        body.add(box);

        Button smaller = new Button("Perkecil", e -> setZoom(zoom - STEP));
        Button larger = new Button("Perbesar", e -> setZoom(zoom + STEP));
        getFooter().add(smaller, larger, closeButton("Tutup"));
    }

    private void setZoom(double value) {
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, value));
        image.getStyle().set("transform", "scale(" + zoom + ")");
    }
}
