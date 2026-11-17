package org.delcom.ifs25039springbootcashflow.framework.view;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/** Notifikasi konsisten untuk sukses dan gagal. */
final class Notices {

    private Notices() {
    }

    static void success(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.TOP_END);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    static void error(String message) {
        Notification n = Notification.show(message, 6000, Notification.Position.TOP_END);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
