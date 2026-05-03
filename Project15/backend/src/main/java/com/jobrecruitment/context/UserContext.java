package com.jobrecruitment.context;

public class UserContext {
    private static final ThreadLocal<UserInfo> USER_INFO = new ThreadLocal<>();

    public static void setUserInfo(UserInfo userInfo) {
        USER_INFO.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return USER_INFO.get();
    }

    public static Long getUserId() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public static String getUsername() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    public static Integer getRole() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getRole() : null;
    }

    public static void clear() {
        USER_INFO.remove();
    }

    public static class UserInfo {
        private Long userId;
        private String username;
        private Integer role;

        public UserInfo(Long userId, String username, Integer role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public Integer getRole() {
            return role;
        }
    }
}
