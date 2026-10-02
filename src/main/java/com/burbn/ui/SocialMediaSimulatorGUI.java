package com.burbn.ui;

import com.burbn.config.DatabaseConfig;
import com.burbn.model.Post;
import com.burbn.model.User;
import com.burbn.service.MessageService;
import com.burbn.service.NotificationService;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Modernized Swing GUI interface for Burbn - Social Media Feed Simulator.
 */
public class SocialMediaSimulatorGUI extends JFrame {

    private User currentUser = null;

    private JPanel loginPanel, dashboardPanel;
    private JTextField loginUserField, signupUserField, signupEmailField, signupMobileField;
    private JTextField followUserField, unfollowUserField, findUserField;
    private JPasswordField loginPassField, signupPassField;
    private JTextField postContentField, dmReceiverField;
    private JTextArea notifArea, dmArea;

    public SocialMediaSimulatorGUI() {
        setTitle("Burbn - Social Media Feed Simulator");
        setSize(920, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        createLoginPanel();
        createDashboardPanel();

        loginPanel.setVisible(true);
        dashboardPanel.setVisible(false);

        add(loginPanel);
        add(dashboardPanel);
        setVisible(true);
    }

    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBounds(0, 0, 900, 680);
        loginPanel.setBackground(new Color(245, 247, 250));

        JLabel headerLabel = new JLabel("Burbn Social Media Simulator", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(40, 53, 147));
        headerLabel.setBounds(200, 30, 500, 40);
        loginPanel.add(headerLabel);

        // Login Card
        JPanel loginCard = new JPanel(null);
        loginCard.setBounds(80, 100, 340, 480);
        loginCard.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Existing User Login "));
        loginCard.setBackground(Color.WHITE);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(30, 50, 100, 25);
        loginCard.add(lblUser);

        loginUserField = new JTextField();
        loginUserField.setBounds(30, 80, 280, 30);
        loginCard.add(loginUserField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 130, 100, 25);
        loginCard.add(lblPass);

        loginPassField = new JPasswordField();
        loginPassField.setBounds(30, 160, 280, 30);
        loginCard.add(loginPassField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(30, 220, 280, 35);
        loginBtn.setBackground(new Color(40, 53, 147));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.addActionListener(e -> login());
        loginCard.add(loginBtn);

        loginPanel.add(loginCard);

        // Sign Up Card
        JPanel signupCard = new JPanel(null);
        signupCard.setBounds(480, 100, 340, 480);
        signupCard.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Create New Account "));
        signupCard.setBackground(Color.WHITE);

        JLabel lblSignUser = new JLabel("Username:");
        lblSignUser.setBounds(30, 40, 100, 25);
        signupCard.add(lblSignUser);

        signupUserField = new JTextField();
        signupUserField.setBounds(30, 65, 280, 30);
        signupCard.add(signupUserField);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(30, 105, 100, 25);
        signupCard.add(lblEmail);

        signupEmailField = new JTextField();
        signupEmailField.setBounds(30, 130, 280, 30);
        signupCard.add(signupEmailField);

        JLabel lblMobile = new JLabel("Mobile No:");
        lblMobile.setBounds(30, 170, 100, 25);
        signupCard.add(lblMobile);

        signupMobileField = new JTextField("9999999999");
        signupMobileField.setBounds(30, 195, 280, 30);
        signupCard.add(signupMobileField);

        JLabel lblSignPass = new JLabel("Password:");
        lblSignPass.setBounds(30, 235, 100, 25);
        signupCard.add(lblSignPass);

        signupPassField = new JPasswordField();
        signupPassField.setBounds(30, 260, 280, 30);
        signupCard.add(signupPassField);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(30, 320, 280, 35);
        signupBtn.setBackground(new Color(46, 125, 50));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signupBtn.addActionListener(e -> signUp());
        signupCard.add(signupBtn);

        loginPanel.add(signupCard);
    }

    private void createDashboardPanel() {
        dashboardPanel = new JPanel(null);
        dashboardPanel.setBounds(0, 0, 900, 680);
        dashboardPanel.setBackground(new Color(245, 247, 250));

        // Post options bar
        JLabel lblPost = new JLabel("Create Post:");
        lblPost.setBounds(40, 25, 100, 25);
        dashboardPanel.add(lblPost);

        postContentField = new JTextField();
        postContentField.setBounds(140, 25, 420, 30);
        dashboardPanel.add(postContentField);

        JButton postBtn = new JButton("Post Options");
        postBtn.setBounds(570, 25, 150, 30);
        postBtn.addActionListener(e -> postOptions());
        dashboardPanel.add(postBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(740, 25, 110, 30);
        logoutBtn.setBackground(new Color(198, 40, 40));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());
        dashboardPanel.add(logoutBtn);

        // Follow / Unfollow row
        JLabel lblFollow = new JLabel("Follow @:");
        lblFollow.setBounds(40, 75, 80, 25);
        dashboardPanel.add(lblFollow);

        followUserField = new JTextField();
        followUserField.setBounds(120, 75, 130, 28);
        dashboardPanel.add(followUserField);

        JButton btnFollow = new JButton("Follow");
        btnFollow.setBounds(260, 75, 80, 28);
        btnFollow.addActionListener(e -> followUser());
        dashboardPanel.add(btnFollow);

        JLabel lblUnfollow = new JLabel("Unfollow @:");
        lblUnfollow.setBounds(370, 75, 90, 25);
        dashboardPanel.add(lblUnfollow);

        unfollowUserField = new JTextField();
        unfollowUserField.setBounds(465, 75, 130, 28);
        dashboardPanel.add(unfollowUserField);

        JButton btnUnfollow = new JButton("Unfollow");
        btnUnfollow.setBounds(605, 75, 95, 28);
        btnUnfollow.addActionListener(e -> unfollowUser());
        dashboardPanel.add(btnUnfollow);

        // Find User Profile
        JLabel lblFindUser = new JLabel("Search Profile @:");
        lblFindUser.setBounds(40, 118, 120, 25);
        dashboardPanel.add(lblFindUser);

        findUserField = new JTextField();
        findUserField.setBounds(160, 118, 140, 28);
        dashboardPanel.add(findUserField);

        JButton btnFindUser = new JButton("View Profile");
        btnFindUser.setBounds(310, 118, 110, 28);
        btnFindUser.addActionListener(e -> findUser());
        dashboardPanel.add(btnFindUser);

        // Notifications area
        JLabel lblNotif = new JLabel("Notifications (Stack LIFO):");
        lblNotif.setBounds(40, 160, 200, 25);
        dashboardPanel.add(lblNotif);

        notifArea = new JTextArea();
        notifArea.setEditable(false);
        notifArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane notifScroll = new JScrollPane(notifArea);
        notifScroll.setBounds(40, 190, 380, 390);
        dashboardPanel.add(notifScroll);

        JButton btnRefreshNotif = new JButton("Refresh Notifications");
        btnRefreshNotif.setBounds(40, 595, 200, 30);
        btnRefreshNotif.addActionListener(e -> refreshNotifications());
        dashboardPanel.add(btnRefreshNotif);

        // DM area
        JLabel lblDM = new JLabel("Direct Messages & Reactions:");
        lblDM.setBounds(460, 160, 220, 25);
        dashboardPanel.add(lblDM);

        dmArea = new JTextArea();
        dmArea.setEditable(false);
        dmArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane dmScroll = new JScrollPane(dmArea);
        dmScroll.setBounds(460, 190, 390, 350);
        dashboardPanel.add(dmScroll);

        JLabel lblDMTo = new JLabel("Send DM To @:");
        lblDMTo.setBounds(460, 555, 110, 25);
        dashboardPanel.add(lblDMTo);

        dmReceiverField = new JTextField();
        dmReceiverField.setBounds(565, 555, 140, 28);
        dashboardPanel.add(dmReceiverField);

        JButton btnSendDM = new JButton("Send DM");
        btnSendDM.setBounds(715, 555, 95, 28);
        btnSendDM.addActionListener(e -> sendDM());
        dashboardPanel.add(btnSendDM);

        JButton btnInbox = new JButton("View Inbox");
        btnInbox.setBounds(460, 595, 140, 30);
        btnInbox.addActionListener(e -> viewInbox());
        dashboardPanel.add(btnInbox);
    }

    private void login() {
        try {
            String username = loginUserField.getText().trim();
            String password = new String(loginPassField.getPassword());
            DatabaseConfig.connect();
            currentUser = User.login(username, password);
            if (currentUser != null) {
                JOptionPane.showMessageDialog(this, "Welcome back @" + username + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                loginPanel.setVisible(false);
                dashboardPanel.setVisible(true);
                refreshNotifications();
                viewInbox();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or user already logged in!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void signUp() {
        try {
            String username = signupUserField.getText().trim();
            String email = signupEmailField.getText().trim();
            String mobile = signupMobileField.getText().trim();
            String password = new String(signupPassField.getPassword());
            if (User.signUp(username, password, email, mobile)) {
                JOptionPane.showMessageDialog(this, "Account created successfully for @" + username + "! You can now log in.", "Signup Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Signup failed! Username or Email might be taken.", "Signup Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postOptions() {
        if (currentUser == null) return;
        String content = postContentField.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter post content first!");
            return;
        }

        String[] options = {"Standard Post", "24h Ephemeral Moment ⏳", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose publication type:", "Publish Content",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        try {
            if (choice == 0) {
                if (Post.create(currentUser.id, content)) {
                    JOptionPane.showMessageDialog(this, "Post published successfully!");
                    postContentField.setText("");
                }
            } else if (choice == 1) {
                if (Post.createMoment(currentUser.id, content)) {
                    JOptionPane.showMessageDialog(this, "24h Moment published! ⏳");
                    postContentField.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void followUser() {
        if (currentUser == null) return;
        String targetUser = followUserField.getText().trim();
        if (User.follow(currentUser.id, targetUser)) {
            JOptionPane.showMessageDialog(this, "Now following @" + targetUser + "!");
            followUserField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Could not follow @" + targetUser, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void unfollowUser() {
        if (currentUser == null) return;
        String targetUser = unfollowUserField.getText().trim();
        try {
            PreparedStatement ps = DatabaseConfig.connect().prepareStatement("SELECT user_id FROM users WHERE username=?");
            ps.setString(1, targetUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int followeeId = rs.getInt("user_id");
                User.unfollowUser(currentUser.id, followeeId);
                JOptionPane.showMessageDialog(this, "Unfollowed @" + targetUser);
                unfollowUserField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "User @" + targetUser + " not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findUser() {
        if (currentUser == null) return;
        String targetUser = findUserField.getText().trim();
        try {
            User.viewUserProfile(new java.util.Scanner(System.in), currentUser.id, targetUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDM() {
        if (currentUser == null) return;
        String toUser = dmReceiverField.getText().trim();
        if (toUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter receiver username!");
            return;
        }
        String msg = JOptionPane.showInputDialog(this, "Enter direct message to @" + toUser + ":");
        if (msg != null && !msg.trim().isEmpty()) {
            try {
                MessageService.sendMessage(currentUser.id, toUser, msg);
                JOptionPane.showMessageDialog(this, "Message delivered to @" + toUser + "!");
                viewInbox();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void viewInbox() {
        if (currentUser == null) return;
        dmArea.setText("");
        try {
            java.io.PrintStream ps = new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) {
                    dmArea.append(String.valueOf((char) b));
                }
            });
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            MessageService.viewInbox(currentUser.id);
            System.setOut(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshNotifications() {
        if (currentUser == null) return;
        notifArea.setText("");
        try {
            java.io.PrintStream ps = new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b) {
                    notifArea.append(String.valueOf((char) b));
                }
            });
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            NotificationService.showNotifications(currentUser.id);
            System.setOut(old);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        if (currentUser != null) {
            User.logout(currentUser.username);
            currentUser = null;
        }
        loginUserField.setText("");
        loginPassField.setText("");
        signupUserField.setText("");
        signupPassField.setText("");
        signupEmailField.setText("");
        signupMobileField.setText("");
        postContentField.setText("");
        followUserField.setText("");
        unfollowUserField.setText("");
        findUserField.setText("");
        dmReceiverField.setText("");
        notifArea.setText("");
        dmArea.setText("");

        dashboardPanel.setVisible(false);
        loginPanel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SocialMediaSimulatorGUI());
    }
}
