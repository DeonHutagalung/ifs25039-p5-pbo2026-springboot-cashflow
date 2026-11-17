package org.delcom.ifs25039springbootcashflow.framework.auth;

import org.delcom.ifs25039springbootcashflow.domain.entity.User;
import org.delcom.ifs25039springbootcashflow.domain.repository.IUserRepository;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Sesi login: VaadinSession + HTTP session + cookie bertanda tangan (HMAC).
 * Flag LOGGED_OUT_KEY mencegah sesi dipulihkan dari cookie setelah logout.
 */
public final class AuthSession {

    public static final String COOKIE_NAME = "cashflow_uid";
    public static final String LOGGED_OUT_KEY = "cashflow.logged.out";
    private static final String USER_KEY = "cashflow.user.id";
    private static final int COOKIE_MAX_AGE = 8 * 60 * 60;
    private static final byte[] SECRET = new byte[32];

    static {
        new SecureRandom().nextBytes(SECRET);
    }

    private AuthSession() {
    }

    public static void login(User user) {
        VaadinSession session = VaadinSession.getCurrent();
        session.setAttribute(LOGGED_OUT_KEY, null);
        session.setAttribute(USER_KEY, user.id());
        WrappedSession http = session.getSession();
        if (http != null) {
            http.removeAttribute(LOGGED_OUT_KEY);
            http.setAttribute(USER_KEY, user.id());
        }
        writeCookie(sign(user.id()), COOKIE_MAX_AGE);
    }

    public static void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        session.setAttribute(USER_KEY, null);
        session.setAttribute(LOGGED_OUT_KEY, Boolean.TRUE);
        WrappedSession http = session.getSession();
        if (http != null) {
            http.removeAttribute(USER_KEY);
            http.setAttribute(LOGGED_OUT_KEY, Boolean.TRUE);
        }
        writeCookie("", 0);
    }

    /** Pengguna yang sedang login; memulihkan sesi dari cookie setelah refresh bila belum logout. */
    public static Optional<User> currentUser(IUserRepository userRepository) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null || Boolean.TRUE.equals(session.getAttribute(LOGGED_OUT_KEY))) {
            return Optional.empty();
        }
        if (session.getAttribute(USER_KEY) instanceof Integer id) {
            return userRepository.findById(id);
        }
        Optional<User> restored = readCookie().flatMap(userRepository::findById);
        restored.ifPresent(user -> session.setAttribute(USER_KEY, user.id()));
        return restored;
    }

    private static void writeCookie(String value, int maxAge) {
        VaadinResponse response = VaadinResponse.getCurrent();
        if (response instanceof VaadinServletResponse servletResponse) {
            Cookie cookie = new Cookie(COOKIE_NAME, value);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAge);
            cookie.setAttribute("SameSite", "Lax");
            servletResponse.getHttpServletResponse().addCookie(cookie);
        }
    }

    private static Optional<Integer> readCookie() {
        VaadinRequest request = VaadinRequest.getCurrent();
        if (!(request instanceof VaadinServletRequest servletRequest)) {
            return Optional.empty();
        }
        Cookie[] cookies = servletRequest.getHttpServletRequest().getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return verify(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    private static String sign(int id) {
        return id + "." + hmac(String.valueOf(id));
    }

    private static Optional<Integer> verify(String value) {
        int dot = value == null ? -1 : value.indexOf('.');
        if (dot <= 0) {
            return Optional.empty();
        }
        String idPart = value.substring(0, dot);
        String expected = hmac(idPart);
        boolean valid = MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                value.substring(dot + 1).getBytes(StandardCharsets.UTF_8));
        if (!valid) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(idPart));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
