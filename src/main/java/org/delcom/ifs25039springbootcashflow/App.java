package org.delcom.ifs25039springbootcashflow;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("styles.css")
@PWA(name = "Cashflow", shortName = "Cashflow", iconPath = "icons/icon.png")
public class App implements AppShellConfigurator {

    public static void main(String[] args) throws Exception {
        DotEnv.load();
        SpringApplication.run(App.class, args);
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addMetaTag("description",
                "Cashflow: aplikasi pencatat pemasukan dan pengeluaran pribadi dengan saldo otomatis.");
        settings.addFavIcon("icon", "icons/icon.png", "512x512");
    }
}
