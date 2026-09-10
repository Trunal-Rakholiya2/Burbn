package main;

import db.DB;
import user.User;
import feed.*;
import java.util.*;

public class MainApp {
    static User currentUser = null;

    public static void main(String[] args) throws Exception {
        DB.connect();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Login\n2. Signup\n3. Exit");
            int choice = sc.nextInt();
            sc.nextLine();
            if (choice == 1) {
                System.out.print("Username: ");
                String username = sc.nextLine();
                System.out.print("Password: ");
                String password = sc.nextLine();
                currentUser = User.login(username, password);
                if (currentUser != null) {
                    System.out.println("Login successful!");
                    NotificationService.showNotifications(currentUser.id);
                    dashboard(sc);
                    if (currentUser != null) {
                        User.logout(currentUser.username);
                    }
                } else {
                    System.out.println("Login failed.");
                }
            } else if (choice == 2) {
                System.out.print("Username: ");
                String username = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("Password: ");
                String password = sc.nextLine();
                System.out.print("Mobile number:");
                String mobile = sc.next();
                if (User.signUp(username, password, email, mobile)) {
                    System.out.println("Signup successful!");
                } else {
                    System.out.println("Signup failed.");
                }
            } else {
                break;
            }
        }
        DB.close();
        sc.close();
    }

    static void dashboard(Scanner sc) throws Exception {
        while (true) {
            System.out.println("\n--- Dashboard ---");
            System.out.println("1. Post\n2. Follow User\n3. View Feed\n4. Change Password\n5.FindUser\n6.Direct Messaging\n7.My Profile\n8.Recommended Post\n9.Post a Moment(24 hrs Post)\n10.View Moments\n11.Logout");
            int option = sc.nextInt();
            sc.nextLine();

            if (option == 1) {
                System.out.print("Enter post content: ");
                String content = sc.nextLine();
                if (Post.create(currentUser.id, content)) {
                    System.out.println("Post successful!");
                }
            } else if (option == 2) {
                System.out.print("Enter username to follow: ");
                String toFollow = sc.nextLine();
                if (User.follow(currentUser.id, toFollow)) {
                    System.out.println("Followed successfully!");
                } else {
                    System.out.println("Failed to follow user.");
                }
            } else if (option == 3) {
                List<String> feed = Feed.getFeed(currentUser.id);
                if (feed.isEmpty()) {
                    System.out.println("No feed to show.");
                } else {
                    for (String post : feed) {
                        System.out.println("--------------------------\n" + post);
                    }
                }
            } else if (option == 4) {
                System.out.println("Enter your registered mobile_no");
                String mobile = sc.next();
                System.out.println("Enter new password");
                String newpass = sc.next();
                if (User.changepassword(currentUser.id, mobile, newpass)) {
                    System.out.println("Password changed sucesfully!!!!!!!!");
                } else {
                    System.out.println("Password change failed");
                }

            } else if (option == 5) {
                System.out.print("Enter username to find: ");
                String name = sc.nextLine();
                User.viewUserProfile(sc, currentUser.id, name);
            }
            else if (option == 6) {
                System.out.println("1. View Inbox\n2. Send Message");
                int dmOption = sc.nextInt();
                sc.nextLine();
                if (dmOption == 1) {
                    MessageService.viewInbox(currentUser.id);
                } else if (dmOption == 2) {
                    System.out.print("Enter receiver's username: ");
                    String toUser = sc.nextLine();
                    System.out.print("Enter message: ");
                    String msg = sc.nextLine();
                    MessageService.sendMessage(currentUser.id, toUser, msg);
                }
            }
            else if (option == 7) {
                // View own profile
                User.viewUserProfile(sc, currentUser.id, currentUser.username);
            } else if (option==8) {
                Feed.getRecommendedPosts(currentUser.id);
            } else if (option==9) {
                System.out.print("Enter moment text (24h): ");
                String content = sc.nextLine();
                if (Post.createMoment(currentUser.id, content)) {
                    System.out.println("Moment posted! ⏳");
                } else {
                    System.out.println("Failed to post Moment.");
                }
            } else if (option==10) {
                Feed.viewMoments(sc, currentUser.id);
            } else if (option == 11) {
                if (currentUser != null) {
                    User.logout(currentUser.username);
                    System.out.println("Logged out successfully!");
                    currentUser = null;
                }
                break;
            }
        }
    }
}
