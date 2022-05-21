package cn.felord.spring.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 代理 {@link org.springframework.security.provisioning.UserDetailsManager} 所有功能
 *
 * @author dax
 */
public class UserDetailsRepository {
    /**
     * 替换为 抽象DAO接口可进行用户持久化操作
     */
    private Map<String, UserDetails> users = new HashMap<>();


    /**
     * Create user.
     *
     * @param user the user
     */
    public void createUser(UserDetails user) {
        users.putIfAbsent(user.getUsername(), user);
    }


    /**
     * Update user.
     *
     * @param user the user
     */
    public void updateUser(UserDetails user) {
        users.put(user.getUsername(), user);
    }


    /**
     * Delete user.
     *
     * @param username the username
     */
    public void deleteUser(String username) {
        users.remove(username);
    }


    /**
     * Change password.
     *
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        String username = currentUser.getName();

        UserDetails user = users.get(username);


        if (user == null) {
            throw new IllegalStateException("Current user doesn't exist in database.");
        }

        // 实现具体的更新密码逻辑
    }


    /**
     * User exists boolean.
     *
     * @param username the username
     * @return the boolean
     */
    public boolean userExists(String username) {

        return users.containsKey(username);
    }


    /**
     * Load user by username user details.
     *
     * @param username the username
     * @return the user details
     * @throws UsernameNotFoundException the username not found exception
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.get(username);
    }


}
