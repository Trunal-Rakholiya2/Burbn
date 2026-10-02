package com.burbn.service;

import com.burbn.config.DatabaseConfig;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MessageService manages direct peer-to-peer messaging and emoji reaction dispatch.
 */
public class MessageService {

    public static void sendMessage(int senderId, String receiverUsername, String content) throws SQLException {
        PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                "SELECT user_id FROM users WHERE username = ?"
        );
        ps.setString(1, receiverUsername);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int receiverId = rs.getInt("user_id");
            PreparedStatement insert = DatabaseConfig.connect().prepareStatement(
                    "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)"
            );
            insert.setInt(1, senderId);
            insert.setInt(2, receiverId);
            insert.setString(3, content);
            insert.executeUpdate();

            String senderName = null;
            PreparedStatement senderStmt = DatabaseConfig.connect().prepareStatement(
                    "SELECT username FROM users WHERE user_id = ?"
            );
            senderStmt.setInt(1, senderId);
            ResultSet senderRs = senderStmt.executeQuery();
            if (senderRs.next()) {
                senderName = senderRs.getString("username");
            }
            
            NotificationService.pushNotification(receiverId, "💬 New message from @" + senderName);
            System.out.println("Message sent to @" + receiverUsername + "!");
        } else {
            System.out.println("User @" + receiverUsername + " not found.");
        }
    }

    public static void sendMomentReaction(int senderId, int receiverId, String emoji) throws SQLException {
        String senderName = null;
        PreparedStatement s = DatabaseConfig.connect().prepareStatement(
                "SELECT username FROM users WHERE user_id = ?"
        );
        s.setInt(1, senderId);
        ResultSet sr = s.executeQuery();
        if (sr.next()) {
            senderName = sr.getString(1);
        }

        PreparedStatement insert = DatabaseConfig.connect().prepareStatement(
                "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)"
        );
        insert.setInt(1, senderId);
        insert.setInt(2, receiverId);
        insert.setString(3, (senderName != null ? "@" + senderName : "Someone") + " reacted to your Moment: " + emoji);
        insert.executeUpdate();

        NotificationService.pushNotification(receiverId, "💬 New reaction to your Moment " + emoji);
    }

    public static void viewInbox(int userId) throws SQLException {
        PreparedStatement ps = DatabaseConfig.connect().prepareStatement(
                "SELECT m.message_id, u.username AS sender, m.content, m.timestamp FROM messages m " +
                        "JOIN users u ON m.sender_id = u.user_id " +
                        "WHERE m.receiver_id = ? ORDER BY m.timestamp DESC"
        );
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        System.out.println("\n📬 --- INBOX ---");
        boolean any = false;
        while (rs.next()) {
            any = true;
            System.out.println("From: @" + rs.getString("sender") + " | At: " + rs.getTimestamp("timestamp"));
            System.out.println("Message: " + rs.getString("content"));
            System.out.println("---------------------------");
        }
        if (!any) {
            System.out.println("No messages in inbox.");
        }
    }
}
