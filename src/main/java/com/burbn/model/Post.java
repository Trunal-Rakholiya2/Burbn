package com.burbn.model;

import com.burbn.config.DatabaseConfig;
import com.burbn.service.NotificationService;
import com.burbn.util.FileLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Post model handling creation of persistent posts and 24-hour expiring ephemeral Moments.
 */
public class Post {

    public static boolean create(int userId, String content) {
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                    "INSERT INTO posts (user_id, content) VALUES (?, ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();
            FileLogger.log("posts.txt", "User #" + userId + " created post: " + content);
            return true;
        } catch (Exception e) {
            System.err.println("[Post Error] Could not create post: " + e.getMessage());
            return false;
        }
    }

    public static boolean createMoment(int userId, String content) {
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                    "INSERT INTO posts (user_id, content, is_moment, expires_at) " +
                            "VALUES (?, ?, 1, DATE_ADD(NOW(), INTERVAL 1 DAY))"
            );
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();

            PreparedStatement foll = DatabaseConfig.connect().prepareStatement(
                    "SELECT follower_id FROM followers WHERE following_id = ?"
            );
            foll.setInt(1, userId);
            ResultSet r = foll.executeQuery();

            String authorName = null;
            PreparedStatement meStmt = DatabaseConfig.connect().prepareStatement(
                    "SELECT username FROM users WHERE user_id = ?"
            );
            meStmt.setInt(1, userId);
            ResultSet meRs = meStmt.executeQuery();
            if (meRs.next()) {
                authorName = meRs.getString("username");
            }

            while (r.next()) {
                int followerId = r.getInt(1);
                NotificationService.pushNotification(
                        followerId,
                        (authorName != null ? authorName : "Someone") + " posted a new 24h Moment ⏳"
                );
            }

            FileLogger.log("posts.txt", "User #" + userId + " created 24h Moment: " + content);
            return true;
        } catch (SQLException e) {
            System.err.println("[Moment Error] Could not create moment: " + e.getMessage());
            return false;
        }
    }
}
