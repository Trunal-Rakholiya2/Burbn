package feed;

import db.DB;
import java.sql.*;
import java.util.*;

public class Feed {
    public static List<String> getFeed(int userId) {
        Scanner sc=new Scanner(System.in);
        List<String> feed = new ArrayList<>();
        try {
            String sql = "SELECT p.post_id, u.username, p.content, p.timestamp, " +
                    "(SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) AS like_count " +
                    "FROM posts p " +
                    "JOIN users u ON p.user_id = u.user_id " +
                    "WHERE p.user_id IN (SELECT following_id FROM followers WHERE follower_id = ?) " +
                    "ORDER BY p.timestamp ";

            PreparedStatement ps = DB.con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            int count=1;
            while (rs.next())
            {
                int postId = rs.getInt("post_id");
                String username = rs.getString("username");
                Timestamp time = rs.getTimestamp("timestamp");
                String content = rs.getString("content");
                int likes=rs.getInt("like_count");

                System.out.println("\nPost #" + count++);
                System.out.println(username + " @ " + time);
                System.out.println(content);
                System.out.println("Likes: "+likes);
                // Fetch usernames who liked this post
                PreparedStatement likerStmt = DB.con.prepareStatement("SELECT username FROM likes WHERE post_id = ?");
                likerStmt.setInt(1, postId);
                ResultSet likers = likerStmt.executeQuery();

                System.out.print("Liked by: ");
                boolean anyLikers = false;
                while (likers.next()) {
                    anyLikers = true;
                    System.out.print(likers.getString("username") + "  ");
                }
                if (!anyLikers) {
                    System.out.print("No likes yet");
                }
                System.out.println();
                PreparedStatement commentStmt = DB.con.prepareStatement(
                        "SELECT c.content, u.username, c.timestamp " +
                                "FROM comments c JOIN users u ON c.user_id = u.user_id " +
                                "WHERE c.post_id = ? ORDER BY c.timestamp");
                commentStmt.setInt(1, postId);
                ResultSet comments = commentStmt.executeQuery();

                System.out.println("Comments:");
                boolean anyComments = false;
                while (comments.next()) {
                    anyComments = true;
                    String commenter = comments.getString("username");
                    String commContent = comments.getString("content");
                    Timestamp commTime = comments.getTimestamp("timestamp");
                    System.out.println(" - " + commenter + " (" + commTime + "): " + commContent);
                }
                if (!anyComments) {
                    System.out.println(" No comments yet.");
                }
                System.out.println();// New line

                System.out.println("Choose action: 1. Like  2. Comment  0. Skip");
                int action = sc.nextInt();
                sc.nextLine();
                if (action == 1) {
                    likepost(userId, postId);
                } else if (action==2) {
                    System.out.println("Enter comment");
                    String comment = sc.nextLine();
                    commentonpost(userId, postId, comment);
                }
            }
            if (feed.isEmpty())
            {
                System.out.println("no posts to show");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feed;
    }
    // In Feed.java
    public static void getRecommendedPosts(int userId) {
        try {
            System.out.println("\n--- Recommended Posts For You ---");

            // 1. Mutual followers' posts
            String sqlMutual =
                    "SELECT p.post_id, u.username, p.content, p.timestamp, " +
                            "(SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) AS like_count " +
                            "FROM posts p " +
                            "JOIN users u ON p.user_id = u.user_id " +
                            "WHERE p.user_id IN ( " +
                            "   SELECT f2.following_id " +
                            "   FROM followers f1 " +
                            "   JOIN followers f2 ON f1.following_id = f2.follower_id " +
                            "   WHERE f1.follower_id = ? " +
                            ") " +
                            "AND p.user_id NOT IN (SELECT following_id FROM followers WHERE follower_id = ?) " +
                            "ORDER BY p.timestamp DESC LIMIT 5";

            PreparedStatement ps1 = DB.con.prepareStatement(sqlMutual);
            ps1.setInt(1, userId);
            ps1.setInt(2, userId);
            ResultSet rs1 = ps1.executeQuery();

            boolean found = false;
            while (rs1.next()) {
                found = true;
                System.out.println("\n👥 Mutual Connection Post");
                System.out.println(rs1.getString("username") + " @ " + rs1.getTimestamp("timestamp"));
                System.out.println(rs1.getString("content"));
                System.out.println("Likes: " + rs1.getInt("like_count"));
            }

            // 2. Popular posts (top liked overall)
            String sqlPopular =
                    "SELECT p.post_id, u.username, p.content, p.timestamp, " +
                            "(SELECT COUNT(*) FROM likes l WHERE l.post_id = p.post_id) AS like_count " +
                            "FROM posts p " +
                            "JOIN users u ON p.user_id = u.user_id " +
                            "ORDER BY like_count DESC LIMIT 3";

            PreparedStatement ps2 = DB.con.prepareStatement(sqlPopular);
            ResultSet rs2 = ps2.executeQuery();

            while (rs2.next()) {
                found = true;
                System.out.println("\n🔥 Popular Post");
                System.out.println(rs2.getString("username") + " @ " + rs2.getTimestamp("timestamp"));
                System.out.println(rs2.getString("content"));
                System.out.println("Likes: " + rs2.getInt("like_count"));
            }

            if (!found) {
                System.out.println("No recommendations available right now.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static String timeLeftString(Timestamp expiresAt) {
        long msLeft = expiresAt.getTime() - System.currentTimeMillis();
        if (msLeft <= 0) return "expired";
        long mins = msLeft / 60000;
        long hrs = mins / 60;
        long remMins = mins % 60;
        return hrs + "h " + remMins + "m left";
    }

    public static void viewMoments(Scanner sc, int userId) {
        try {
            System.out.println("\n--- Moments (24h) ---");

            String sql =
                    "SELECT p.post_id, u.username, p.content, p.expires_at " +
                            "FROM posts p " +
                            "JOIN users u ON p.user_id = u.user_id " +
                            "WHERE p.is_moment = 1 " +
                            "AND p.expires_at > NOW() " +
                            "AND p.user_id IN (SELECT following_id FROM followers WHERE follower_id = ?) " +
                            "ORDER BY p.expires_at ASC";

            PreparedStatement ps = DB.con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            boolean any = false;
            while (rs.next()) {
                any = true;
                int postId = rs.getInt("post_id");
                String username = rs.getString("username");
                String content = rs.getString("content");
                Timestamp expiresAt = rs.getTimestamp("expires_at");

                System.out.println("\n@" + username + " — " + timeLeftString(expiresAt));
                System.out.println(content);

                System.out.println("React? 1. ❤️  2. 🔥  3. 😂  4. 😮  0. Skip");
                int choice = sc.nextInt(); sc.nextLine();
                if (choice >= 1 && choice <= 4) {
                    String[] emojis = {"❤️","🔥","😂","😮"};
                    String emoji = emojis[choice - 1];

                    // find owner id
                    PreparedStatement ownerStmt = DB.con.prepareStatement(
                            "SELECT user_id FROM posts WHERE post_id=?"
                    );
                    ownerStmt.setInt(1, postId);
                    ResultSet ownerRs = ownerStmt.executeQuery();
                    if (ownerRs.next()) {
                        int ownerId = ownerRs.getInt(1);
                        MessageService.sendMomentReaction(userId, ownerId, emoji);
                    }
                    System.out.println("Sent reaction " + emoji + "!");
                }
            }
            if (!any) System.out.println("No active Moments right now.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void likepost(int userId, int postId) throws SQLException {
        {
            // Fetch username of the liker from the users table
            PreparedStatement getUser = DB.con.prepareStatement("SELECT username FROM users WHERE user_id = ?");
            getUser.setInt(1, userId);
            ResultSet rs = getUser.executeQuery();

            String likerUsername = null;
            if (rs.next()) {
                likerUsername = rs.getString("username");
            }

            if (likerUsername == null) {
                System.out.println("User not found.");
                return;
            }

            // Insert like
            PreparedStatement ps = DB.con.prepareStatement("INSERT IGNORE INTO likes(user_id, post_id, username) VALUES (?, ?, ?)");
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            ps.setString(3, likerUsername);  // Now correctly inserting liker’s name
            ps.executeUpdate();
            PreparedStatement ownerStmt = DB.con.prepareStatement("SELECT user_id FROM posts WHERE post_id = ?");
            ownerStmt.setInt(1, postId);
            ResultSet ownerRs = ownerStmt.executeQuery();
            if (ownerRs.next()) {
                int ownerId = ownerRs.getInt("user_id");
                if (ownerId != userId) {
                    NotificationService.pushNotification(ownerId, "Your post was liked by " + likerUsername);
                }
            }

            System.out.println("POST LIKED by " + likerUsername + "!");
        }
    }
    public static void commentonpost(int userId,int postId,String comment) throws SQLException {
        {
            PreparedStatement ps=DB.con.prepareStatement("insert into comments(user_id,post_id,content) values (?,?,?)");
            ps.setInt(1,userId);
            ps.setInt(2,postId);
            ps.setString(3,comment);
            ps.executeUpdate();
            PreparedStatement ownerStmt = DB.con.prepareStatement("SELECT user_id FROM posts WHERE post_id = ?");
            ownerStmt.setInt(1, postId);
            ResultSet ownerRs = ownerStmt.executeQuery();
            if (ownerRs.next()) {
                int ownerId = ownerRs.getInt("user_id");
                if (ownerId != userId) {
                    NotificationService.pushNotification(ownerId, "Your post was commented on by user ID " + userId);
                }
            }
            System.out.println("Comment Added!!");
        }
    }
}
