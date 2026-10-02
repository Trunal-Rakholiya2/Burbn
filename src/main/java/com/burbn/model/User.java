package com.burbn.model;

import com.burbn.config.DatabaseConfig;
import com.burbn.service.NotificationService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * User model representing authenticated users, profile stats, follows, and Aura gamification points.
 */
public class User {
    public int id;
    public String username;
    
    private static final Set<String> activeUsers = new HashSet<>();

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public static synchronized User login(String username, String password) {
        synchronized (activeUsers) {
            if (activeUsers.contains(username)) {
                System.out.println("User is already logged in from another session.");
                return null;
            }
        }
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                synchronized (activeUsers) {
                    activeUsers.add(username);
                }
                return new User(rs.getInt("user_id"), rs.getString("username"));
            }
        } catch (Exception e) {
            System.err.println("[Auth Error] Login failed: " + e.getMessage());
        }
        return null;
    }

    public static synchronized void logout(String username) {
        synchronized (activeUsers) {
            activeUsers.remove(username);
        }
    }

    public static boolean signUp(String username, String password, String email, String mobile) {
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                    "INSERT INTO users (username, password, email, mobile_no) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, mobile);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("[Auth Error] Signup failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean changePassword(int userId, String mobile, String newPass) throws Exception {
        PreparedStatement verify = DatabaseConfig.connect().prepareStatement(
                "SELECT * FROM users WHERE user_id=? AND mobile_no=?"
        );
        verify.setInt(1, userId);
        verify.setString(2, mobile);
        ResultSet rs = verify.executeQuery();
        if (rs.next()) {
            PreparedStatement update = DatabaseConfig.connect().prepareStatement(
                    "UPDATE users SET password=? WHERE user_id=?"
            );
            update.setString(1, newPass);
            update.setInt(2, userId);
            update.executeUpdate();
            return true;
        } else {
            System.out.println("Mobile verification failed.");
        }
        return false;
    }

    public static boolean follow(int userId, String toFollow) {
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                    "SELECT user_id FROM users WHERE username = ?"
            );
            ps.setString(1, toFollow);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int followId = rs.getInt("user_id");
                PreparedStatement insert = DatabaseConfig.connect().prepareStatement(
                        "INSERT INTO followers (follower_id, following_id) VALUES (?, ?)"
                );
                insert.setInt(1, userId);
                insert.setInt(2, followId);
                insert.executeUpdate();

                PreparedStatement nameStmt = DatabaseConfig.connect().prepareStatement(
                        "SELECT username FROM users WHERE user_id = ?"
                );
                nameStmt.setInt(1, userId);
                ResultSet nameRs = nameStmt.executeQuery();
                if (nameRs.next()) {
                    String followerUsername = nameRs.getString("username");
                    NotificationService.pushNotification(followId, followerUsername + " started following you! 🎉");
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("[Follow Error] Could not follow user: " + e.getMessage());
        }
        return false;
    }

    public static void unfollowUser(int followerId, int followeeId) throws SQLException {
        PreparedStatement pst = DatabaseConfig.connect().prepareStatement(
                "DELETE FROM followers WHERE follower_id = ? AND following_id = ?"
        );
        pst.setInt(1, followerId);
        pst.setInt(2, followeeId);
        pst.executeUpdate();
    }

    public static boolean isFollowing(int currentUserId, int targetUserId) throws SQLException {
        PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                "SELECT 1 FROM followers WHERE follower_id = ? AND following_id = ?"
        );
        ps.setInt(1, currentUserId);
        ps.setInt(2, targetUserId);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public static void showUserStats(int targetUserId) throws Exception {
        PreparedStatement u = DatabaseConfig.connect().prepareStatement(
                "SELECT username FROM users WHERE user_id = ?"
        );
        u.setInt(1, targetUserId);
        ResultSet ru = u.executeQuery();
        if (ru.next()) {
            System.out.println("Username: " + ru.getString("username"));
        }

        PreparedStatement f1 = DatabaseConfig.connect().prepareStatement(
                "SELECT COUNT(*) FROM followers WHERE following_id = ?"
        );
        f1.setInt(1, targetUserId);
        ResultSet r1 = f1.executeQuery();
        r1.next();
        System.out.println("Followers: " + r1.getInt(1));

        PreparedStatement f2 = DatabaseConfig.connect().prepareStatement(
                "SELECT COUNT(*) FROM followers WHERE follower_id = ?"
        );
        f2.setInt(1, targetUserId);
        ResultSet r2 = f2.executeQuery();
        r2.next();
        System.out.println("Following: " + r2.getInt(1));

        PreparedStatement p = DatabaseConfig.connect().prepareStatement(
                "SELECT COUNT(*) FROM posts WHERE user_id = ?"
        );
        p.setInt(1, targetUserId);
        ResultSet rp = p.executeQuery();
        rp.next();
        System.out.println("Posts: " + rp.getInt(1));

        int zPoints = calculateZPoints(targetUserId);
        System.out.println("Aura Points: " + zPoints);
        System.out.println("Badge: " + User.getBadge(targetUserId));
    }

    public static void viewUserProfile(Scanner sc, int currentUserId, String usernameToFind) throws Exception {
        PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                "SELECT user_id FROM users WHERE username = ?"
        );
        ps.setString(1, usernameToFind);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int targetUserId = rs.getInt("user_id");

            System.out.println("\n👤 --- Profile: @" + usernameToFind + " ---");
            showUserStats(targetUserId);

            boolean follows = isFollowing(currentUserId, targetUserId);

            if (follows) {
                System.out.println("\nYou follow this user. Displaying posts:");
                PreparedStatement posts = DatabaseConfig.connect().prepareStatement(
                        "SELECT post_id, content, timestamp FROM posts WHERE user_id = ? ORDER BY timestamp DESC"
                );
                posts.setInt(1, targetUserId);
                ResultSet pr = posts.executeQuery();
                int count = 0;
                while (pr.next()) {
                    System.out.println("Post #" + (++count));
                    System.out.println(pr.getString("content"));
                    System.out.println("Posted on: " + pr.getTimestamp("timestamp"));
                    System.out.println("----------------------");
                }
                if (count == 0) {
                    System.out.println("No posts to display.");
                }
                System.out.print("Do you want to unfollow this user? (yes/no): ");
                String choice = sc.nextLine().trim().toLowerCase();
                if (choice.equals("yes")) {
                    unfollowUser(currentUserId, targetUserId);
                    System.out.println("You have unfollowed @" + usernameToFind);
                }
            } else {
                System.out.println("\nYou do not follow this user.");
                System.out.print("Do you want to follow? (yes/no): ");
                String followChoice = sc.nextLine().toLowerCase();
                if (followChoice.equals("yes")) {
                    if (follow(currentUserId, usernameToFind)) {
                        System.out.println("You are now following @" + usernameToFind + "!");
                    } else {
                        System.out.println("Failed to follow.");
                    }
                }
            }
        } else {
            System.out.println("User not found.");
        }
    }

    public static int calculateZPoints(int userId) throws Exception {
        int zPoints = 0;

        PreparedStatement postStmt = DatabaseConfig.connect().prepareStatement(
                "SELECT COUNT(*) AS post_count FROM posts WHERE user_id = ?"
        );
        postStmt.setInt(1, userId);
        ResultSet postRs = postStmt.executeQuery();
        postRs.next();
        zPoints += postRs.getInt("post_count") * 10;

        PreparedStatement likeStmt = DatabaseConfig.connect().prepareStatement(
                "SELECT COUNT(*) AS like_count FROM likes l JOIN posts p ON l.post_id = p.post_id WHERE p.user_id = ?"
        );
        likeStmt.setInt(1, userId);
        ResultSet likeRs = likeStmt.executeQuery();
        likeRs.next();
        zPoints += likeRs.getInt("like_count") * 2;

        PreparedStatement commentStmt = DatabaseConfig.connect().prepareStatement(
                "SELECT COUNT(*) AS comment_count FROM comments c JOIN posts p ON c.post_id = p.post_id WHERE p.user_id = ?"
        );
        commentStmt.setInt(1, userId);
        ResultSet commentRs = commentStmt.executeQuery();
        commentRs.next();
        zPoints += commentRs.getInt("comment_count") * 3;

        PreparedStatement updateStmt = DatabaseConfig.connect().prepareStatement(
                "UPDATE users SET aura_points = ? WHERE user_id = ?"
        );
        updateStmt.setInt(1, zPoints);
        updateStmt.setInt(2, userId);
        updateStmt.executeUpdate();

        return zPoints;
    }

    public static String getBadge(int userId) {
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                    "SELECT aura_points FROM users WHERE user_id = ?"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int points = rs.getInt("aura_points");
                if (points >= 500) return "👑 Legend";
                if (points >= 300) return "🥇 Gold";
                if (points >= 150) return "🥈 Silver";
                if (points >= 50) return "🥉 Bronze";
                return "🌱 Beginner";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "🌱 Beginner";
    }
}
