package org.delcom.ifs25039springbootcashflow.framework.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/** Komponen upload gambar tunggal; berkas ditahan di memori sampai tombol simpan ditekan. */
public class ImageUploader extends Div {

    private static final int MAX_BYTES = 5 * 1024 * 1024;

    private String fileName;
    private byte[] data;

    public ImageUploader() {
        Upload upload = new Upload(UploadHandler.inMemory((metadata, bytes) -> {
            this.fileName = metadata.fileName();
            this.data = bytes;
        }));
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/gif", "image/webp");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(MAX_BYTES);
        upload.addFileRemovedListener(e -> reset());
        add(upload);
    }

    public boolean hasFile() {
        return data != null;
    }

    public String fileName() {
        return fileName;
    }

    public InputStream content() {
        return new ByteArrayInputStream(data);
    }

    public void reset() {
        fileName = null;
        data = null;
    }
}
