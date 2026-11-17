package org.delcom.ifs25039springbootcashflow.config;

import org.delcom.ifs25039springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25039springbootcashflow.adapter.repository.JdbcTransactionRepository;
import org.delcom.ifs25039springbootcashflow.adapter.repository.JdbcUserRepository;
import org.delcom.ifs25039springbootcashflow.adapter.repository.SchemaInitializer;
import org.delcom.ifs25039springbootcashflow.adapter.storage.FileStorage;
import org.delcom.ifs25039springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25039springbootcashflow.usecase.AuthUseCase;
import org.delcom.ifs25039springbootcashflow.usecase.FinanceUseCase;
import org.delcom.ifs25039springbootcashflow.usecase.UserUseCase;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Composition root: seluruh wiring bean aplikasi. */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public AppConfig(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Bean
    public IUserRepository userRepository(DataSource dataSource) {
        SchemaInitializer.initialize(dataSource);
        return new JdbcUserRepository(dataSource);
    }

    @Bean
    public ITransactionRepository transactionRepository(DataSource dataSource, IUserRepository userRepository) {
        // userRepository dijadikan dependensi agar tabel users selalu dibuat lebih dulu.
        return new JdbcTransactionRepository(dataSource);
    }

    @Bean
    public AuthUseCase authUseCase(IUserRepository userRepository) {
        return new AuthUseCase(userRepository);
    }

    @Bean
    public UserUseCase userUseCase(IUserRepository userRepository) {
        return new UserUseCase(userRepository);
    }

    @Bean
    public FinanceUseCase financeUseCase(ITransactionRepository transactionRepository) {
        return new FinanceUseCase(transactionRepository);
    }

    @Bean
    public FinancePresenter financePresenter() {
        return new FinancePresenter();
    }

    @Bean
    public FileStorage fileStorage() {
        return new FileStorage(uploadDir);
    }

    @Bean
    public ApplicationRunner seedRootAdmin(
            AuthUseCase authUseCase,
            @Value("${app.root-username}") String rootUsername,
            @Value("${app.root-password}") String rootPassword) {
        return args -> authUseCase.ensureRootAdmin(rootUsername, rootPassword);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fileStorage().locationUri());
    }
}
