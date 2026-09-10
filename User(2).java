package user;

import db.DB;
import feed.NotificationService;
import util.FileUtil;

import java.sql.*;
import java.util.*;

public class User {
    public int id;
    public String username;
    private static final Set<String> active_users = new HashSet<>();

    public int getId() {
        return id;
    }

    // ✅ Getter for username
    public String getUsername() {
        return username;
    }
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }
    public static int getUserIdByUsername(String username) {
        try {
            PreparedStatement ps = DB.con.prepareStatement(
                    "SELECT user_id FROM users WHERE username=?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static synchronized User login(String username, String password) {
        synchronized (active_users) {
            if (active_users.contains(username)) {
                System.out.println("User is already logged in from another session.");
                return null;
            }
        }
        try {
            PreparedStatement ps = DB.con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                synchronized (active_users)
                {
                    active_users.add(username);
                }
                return new User(rs.getInt("user_id"), rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized void logout(String username) {
        synchronized (active_users) {
            active_users.remove(username);
        }
    }

    public static boolean signUp(String username, String password, String email,String mobile) {
        try {
            PreparedStatement ps = DB.con.prepareStatement("INSERT INTO users (username, password, email,mobile_no) VALUES (?, ?, ?,?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4,mobile);
            ps.executeUpdate();
            FileUtil.writeToFile("users.txt", "New user: " + username + ", Email: " + email);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public static boolean changepassword(int user_id, String mobile, String newPass) throws Exception {
        PreparedStatement verify = DB.con.prepareStatement("select * from users where user_id=? and mobile_no=?");
        verify.setInt(1, user_id);
        verify.setString(2, mobile);
        ResultSet rs = verify.executeQuery();
        if (rs.next()) {
            PreparedStatement update = DB.con.prepareStatement("update users set password=? where user_id=?");
            update.setString(1, newPass);
            update.setInt(2, user_id);
            update.executeUpdate();
            return true;
        } else {
            System.out.println("Mobile verification failed");
        }
        return false;
    }
    public static boolean follow(int userId, String toFollow) {
        try {
            PreparedStatement ps = DB.con.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            ps.setString(1, toFollow);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int followId = rs.getInt("user_id");
                PreparedStatement insert = DB.con.prepareStatement("INSERT INTO followers (follower_id, following_id) VALUES (?, ?)");
                insert.setInt(1, userId);
                insert.setInt(2, followId);
                insert.executeUpdate();
                PreparedStatement nameStmt = DB.con.prepareStatement("SELECT username FROM users WHERE user_id = ?");
                nameStmt.setInt(1, userId);
                ResultSet nameRs = nameStmt.executeQuery();
                if (nameRs.next()) {
                    String followerUsername = nameRs.getString("username");
                    //  Send follow notification
                    NotificationService.pushNotification(followId, followerUsername + " followed you!");
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void unfollowUser(int followerId, int followeeId) throws SQLException {
        PreparedStatement pst = DB.con.prepareStatement(
                "DELETE FROM followers WHERE follower_id = ? AND following_id = ?"
        );

        pst.setInt(1, followerId);
        pst.setInt(2, followeeId);
        pst.executeUpdate();
    }

    public static boolean isFollowing(int currentUserId, int targetUserId) throws SQLException {
        PreparedStatement ps = DB.con.prepareStatement(
                "SELECT 1 FROM followers WHERE follower_id = ? AND following_id = ?"
        );
        ps.setInt(1, currentUserId);
        ps.setInt(2, targetUserId);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public static void showUserStats(int targetUserId) throws Exception {
        // Username
        PreparedStatement u = DB.con.prepareStatement("SELECT username FROM users WHERE user_id = ?");
        u.setInt(1, targetUserId);
        ResultSet ru = u.executeQuery();
        if (ru.next()) {
            System.out.println("Username: " + ru.getString("username"));
        }

        // Followers
        PreparedStatement f1 = DB.con.prepareStatement("SELECT COUNT(*) FROM followers WHERE following_id = ?");
        f1.setInt(1, targetUserId);
        ResultSet r1 = f1.executeQuery();
        r1.next();
        System.out.println("Followers: " + r1.getInt(1));

        // Following
        PreparedStatement f2 = DB.con.prepareStatement("SELECT COUNT(*) FROM followers WHERE follower_id = ?");
        f2.setInt(1, targetUserId);
        ResultSet r2 = f2.executeQuery();
        r2.next();
        System.out.println("Following: " + r2.getInt(1));

        // Post count
        PreparedStatement p = DB.con.prepareStatement("SELECT COUNT(*) FROM posts WHERE user_id = ?");
        p.setInt(1, targetUserId);
        ResultSet rp = p.executeQuery();
        rp.next();
        System.out.println("Posts: " + rp.getInt(1));

        //Aura Points
        int Zpoints=calculateZPoints(targetUserId);
        System.out.println("Aura Points:"+Zpoints);

        //Badge Earned
        System.out.println("Badge: " + User.getBadge(targetUserId));
    }
    public static void viewUserProfile(Scanner sc, int currentUserId, String usernameToFind) throws Exception {
        PreparedStatement ps = DB.con.prepareStatement("SELECT user_id FROM users WHERE username = ?");
        ps.setString(1, usernameToFind);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int targetUserId = rs.getInt("user_id");

            System.out.println("\n--- Profile: " + usernameToFind + " ---");
            showUserStats(targetUserId);

            boolean follows = isFollowing(currentUserId, targetUserId);

            if (follows) {
                System.out.println("\nYou follow this user. Displaying posts:");
                PreparedStatement posts = DB.con.prepareStatement(
                        "SELECT post_id, content, timestamp FROM posts WHERE user_id = ? ORDER BY timestamp DESC"
                );
                posts.setInt(1, targetUserId);
                ResultSet pr = posts.executeQuery();
                int count = 0;
                while (pr.next()) {
                    System.out.println("Post #" + (count++));
                    System.out.println(pr.getString("content"));
                    System.out.println("Posted on: " + pr.getTimestamp("timestamp"));
                    System.out.println("----------------------");
                }
                if (count == 0  ) {
                    System.out.println("No posts to display.");
                }
                System.out.println("Do you want to unfollow this user? (yes/no): ");
                String choice = sc.nextLine().trim().toLowerCase();
                if (choice.equals("yes")) {
                    unfollowUser(currentUserId, targetUserId);
                    System.out.println("You have unfollowed " + usernameToFind);
                }

            } else {
                System.out.println("\nYou do not follow this user.");
                System.out.print("Do you want to follow? (yes/no): ");
                String followChoice = sc.nextLine().toLowerCase();
                if (followChoice.equals("yes")) {
                    if (follow(currentUserId, usernameToFind)) {
                        System.out.println("You are now following " + usernameToFind + "!");
                    } else {
                        System.out.println("Failed to follow.");
                    }
                }
            }

        } else {
            System.out.println("User not found.");
        }
    }
    // Function to calculate Aura Points
    public static int calculateZPoints(int userId) throws Exception {
        int ZPoints = 0;

        // Points from posts
        PreparedStatement postStmt = DB.con.prepareStatement(
                "SELECT COUNT(*) AS post_count FROM posts WHERE user_id = ?");
        postStmt.setInt(1, userId);
        ResultSet postRs = postStmt.executeQuery();
        postRs.next();
        int postCount = postRs.getInt("post_count");
        ZPoints += postCount * 10; // 10 points per post

        // Points from likes received on user's posts
        PreparedStatement likeStmt = DB.con.prepareStatement(
                "SELECT COUNT(*) AS like_count FROM likes l " +
                        "JOIN posts p ON l.post_id = p.post_id WHERE p.user_id = ?");
        likeStmt.setInt(1, userId);
        ResultSet likeRs = likeStmt.executeQuery();
        likeRs.next();
        int likeCount = likeRs.getInt("like_count");
        ZPoints += likeCount * 2; // 2 points per like

        // Points from comments received on user's posts
        PreparedStatement commentStmt = DB.con.prepareStatement(
                "SELECT COUNT(*) AS comment_count FROM comments c " +
                        "JOIN posts p ON c.post_id = p.post_id WHERE p.user_id = ?");
        commentStmt.setInt(1, userId);
        ResultSet commentRs = commentStmt.executeQuery();
        commentRs.next();
        int commentCount = commentRs.getInt("comment_count");
        ZPoints += commentCount * 3; // 3 points per comment

        PreparedStatement updateStmt = DB.con.prepareStatement(
                "UPDATE users SET aura_points = ? WHERE user_id = ?");
        updateStmt.setInt(1, ZPoints);
        updateStmt.setInt(2, userId);
        updateStmt.executeUpdate();

        return ZPoints;
    }
    public static String getBadge(int userId) {
        try {
            String sql = "SELECT aura_points FROM users WHERE user_id = ?";
            PreparedStatement ps = DB.con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int points = rs.getInt("aura_points");

                if (points >= 500) return "👑 Legend";
                else if (points >= 300) return "🥇 Gold";
                else if (points >= 150) return "🥈 Silver";
                else if (points >= 50) return "🥉 Bronze";
                else return "🌱 Beginner";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "🌱 Beginner";
    }
}
