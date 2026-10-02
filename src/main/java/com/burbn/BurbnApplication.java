package com.burbn;

import com.burbn.config.DatabaseConfig;
import com.burbn.model.Post;
import com.burbn.model.User;
import com.burbn.service.FeedService;
import com.burbn.service.MessageService;
import com.burbn.service.NotificationService;
import com.burbn.ui.SocialMediaSimulatorGUI;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Main application entry point for Burbn - Social Media Feed Simulator.
 * Provides launch choices for interactive CLI Mode or Swing GUI Mode.
 */
public class BurbnApplication {
    static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   BURBN - SOCIAL MEDIA FEED SIMULATOR (v2.0)   ");
        System.out.println("=================================================");

        DatabaseConfig.connect();

        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            launchGUI();
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Select Interface Mode:");
        System.out.println("1. Launch Graphical User Interface (GUI)");
        System.out.println("2. Launch Interactive Command Line Interface (CLI)");
        System.out.print("Enter choice (1/2): ");

        int mode = 1;
        if (sc.hasNextInt()) {
            mode = sc.nextInt();
            sc.nextLine();
        }

        if (mode == 1) {
            launchGUI();
        } else {
            runCLIMode(sc);
        }
    }

    private static void launchGUI() {
        System.out.println("Launching Swing Graphical User Interface...");
        SwingUtilities.invokeLater(() -> new SocialMediaSimulatorGUI());
    }

    private static void runCLIMode(Scanner sc) {
        System.out.println("\nStarting Interactive CLI Session...");
        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Login");
            System.out.println("2. Signup");
            System.out.println("3. Reset Password");
            System.out.println("4. Launch GUI Interface");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                System.out.print("Username: ");
                String username = sc.nextLine();
                System.out.print("Password: ");
                String password = sc.nextLine();
                currentUser = User.login(username, password);
                if (currentUser != null) {
                    System.out.println("Login successful! Welcome @" + currentUser.username);
                    NotificationService.showNotifications(currentUser.id);
                    dashboard(sc);
                    if (currentUser != null) {
                        User.logout(currentUser.username);
                    }
                } else {
                    System.out.println("Login failed! Check credentials or active session.");
                }
            } else if (choice == 2) {
                System.out.print("Username: ");
                String username = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("Mobile: ");
                String mobile = sc.nextLine();
                System.out.print("Password: ");
                String password = sc.nextLine();
                if (User.signUp(username, password, email, mobile)) {
                    System.out.println("Signup successful! You can now log in.");
                } else {
                    System.out.println("Signup failed.");
                }
            } else if (choice == 3) {
                System.out.print("User ID: ");
                int userId = sc.nextInt();
                sc.nextLine();
                System.out.print("Registered Mobile: ");
                String mobile = sc.nextLine();
                System.out.print("New Password: ");
                String newPass = sc.nextLine();
                try {
                    if (User.changePassword(userId, mobile, newPass)) {
                        System.out.println("Password updated successfully!");
                    }
                } catch (Exception e) {
                    System.out.println("Password update failed: " + e.getMessage());
                }
            } else if (choice == 4) {
                launchGUI();
            } else if (choice == 5) {
                System.out.println("Exiting Burbn Social Media Simulator. Goodbye!");
                DatabaseConfig.close();
                break;
            }
        }
    }

    private static void dashboard(Scanner sc) {
        while (currentUser != null) {
            System.out.println("\n=================================");
            System.out.println(" DASHBOARD (@" + currentUser.username + ")");
            System.out.println("=================================");
            System.out.println("1. View Timeline Feed");
            System.out.println("2. View Recommended Posts");
            System.out.println("3. View 24h Moments ⏳");
            System.out.println("4. Create Standard Post");
            System.out.println("5. Create 24h Moment");
            System.out.println("6. Follow User");
            System.out.println("7. View User Profile / Unfollow");
            System.out.println("8. Send Direct Message (DM)");
            System.out.println("9. View DM Inbox");
            System.out.println("10. View Notifications (Stack)");
            System.out.println("11. Logout");
            System.out.print("Select action: ");

            int ch = sc.nextInt();
            sc.nextLine();

            try {
                switch (ch) {
                    case 1:
                        FeedService.getFeed(currentUser.id);
                        break;
                    case 2:
                        FeedService.getRecommendedPosts(currentUser.id);
                        break;
                    case 3:
                        FeedService.viewMoments(sc, currentUser.id);
                        break;
                    case 4:
                        System.out.print("Enter post content: ");
                        String content = sc.nextLine();
                        if (Post.create(currentUser.id, content)) {
                            System.out.println("Post published!");
                        }
                        break;
                    case 5:
                        System.out.print("Enter 24h Moment content: ");
                        String momentContent = sc.nextLine();
                        if (Post.createMoment(currentUser.id, momentContent)) {
                            System.out.println("24h Moment published!");
                        }
                        break;
                    case 6:
                        System.out.print("Enter username to follow: ");
                        String toFollow = sc.nextLine();
                        if (User.follow(currentUser.id, toFollow)) {
                            System.out.println("You are now following @" + toFollow);
                        } else {
                            System.out.println("Could not follow user.");
                        }
                        break;
                    case 7:
                        System.out.print("Enter username to search profile: ");
                        String findName = sc.nextLine();
                        User.viewUserProfile(sc, currentUser.id, findName);
                        break;
                    case 8:
                        System.out.print("Receiver username: ");
                        String receiver = sc.nextLine();
                        System.out.print("Message: ");
                        String msg = sc.nextLine();
                        MessageService.sendMessage(currentUser.id, receiver, msg);
                        break;
                    case 9:
                        MessageService.viewInbox(currentUser.id);
                        break;
                    case 10:
                        NotificationService.showNotifications(currentUser.id);
                        break;
                    case 11:
                        System.out.println("Logging out @" + currentUser.username);
                        currentUser = null;
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("Dashboard Error: " + e.getMessage());
            }
        }
    }
}
