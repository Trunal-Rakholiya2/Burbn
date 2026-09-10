package feed;

import db.DB;
import util.FileUtil;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Post {
    public static boolean create(int userId, String content) {
        try {
            PreparedStatement ps = DB.con.prepareStatement("INSERT INTO posts (user_id, content) VALUES (?, ?)");
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();
            FileUtil.writeToFile("posts.txt", "User " + userId + " posted: " + content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean createMoment(int userId, String content) {
        try {
            PreparedStatement ps = DB.con.prepareStatement(
                    "INSERT INTO posts (user_id, content, is_moment, expires_at) " +
                            "VALUES (?, ?, 1, DATE_ADD(NOW(), INTERVAL 1 DAY))"
            );
            ps.setInt(1, userId);
            ps.setString(2, content);
            ps.executeUpdate();

            // Notify all followers that a new Moment was posted
            PreparedStatement foll = DB.con.prepareStatement(
                    "SELECT follower_id FROM followers WHERE following_id = ?"
            );
            foll.setInt(1, userId);
            ResultSet r = foll.executeQuery();

            String me = null;
            PreparedStatement meStmt = DB.con.prepareStatement("SELECT username FROM users WHERE user_id=?");
            meStmt.setInt(1, userId);
            ResultSet meRs = meStmt.executeQuery();
            if (meRs.next()) me = meRs.getString("username");

            while (r.next()) {
                int followerId = r.getInt(1);
                NotificationService.pushNotification(followerId,
                        (me != null ? me : "Someone") + " posted a Moment ⏳");
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

