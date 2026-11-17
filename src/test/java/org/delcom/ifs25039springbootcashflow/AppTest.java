package org.delcom.ifs25039springbootcashflow;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AppTest {

    @Autowired
    private IUserRepository userRepository;

    @Test
    void contextLoadsAndSeedsRootAdmin() {
        assertTrue(userRepository.findByUsername("admin").orElseThrow().isAdmin());
    }
}
