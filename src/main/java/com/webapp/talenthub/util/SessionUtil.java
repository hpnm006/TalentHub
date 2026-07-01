package com.webapp.talenthub.util;

import com.webapp.talenthub.entity.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    public static final String LOGIN_USER = "LOGIN_USER";

    public static void login(HttpSession session, User user) {
        session.setAttribute(LOGIN_USER, user);
    }

    public static User getUser(HttpSession session) {
        Object obj = session.getAttribute(LOGIN_USER);

        if (obj instanceof User) {
            return (User) obj;
        }

        return null;
    }

    public static void logout(HttpSession session) {
        session.invalidate();
    }

    public static boolean isLogin(HttpSession session) {
        return getUser(session) != null;
    }
}