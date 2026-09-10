package gui;

import user.User;
import feed.*;
import db.DB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SocialMediaSimulator extends JFrame {

    private User currentUser = null;

    private JPanel loginPanel, dashboardPanel;
    private JTextField loginUserField, signupUserField, signupEmailField, followUserField, unfollowUserField, findUserField;
    private JPasswordField loginPassField, signupPassField;
    private JTextField postContentField, dmReceiverField;
    private JTextArea notifArea, dmArea;
    private JButton loginBtn, signupBtn, logoutBtn;

    public SocialMediaSimulator() {
        setTitle("Smart Social Media Simulator");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        loginPanel.setBounds(0,0,900,700);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(300,200,100,25);
        loginPanel.add(lblUser);

        loginUserField = new JTextField();
        loginUserField.setBounds(400,200,150,25);
        loginUserField.setName("loginUserField");
        loginPanel.add(loginUserField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(300,250,100,25);
        loginPanel.add(lblPass);

        loginPassField = new JPasswordField();
        loginPassField.setBounds(400,250,150,25);
        loginPassField.setName("loginPassField");
        loginPanel.add(loginPassField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(300,300,120,30);
        loginBtn.addActionListener(e -> login());
        loginPanel.add(loginBtn);

        JLabel lblSignupUser = new JLabel("New Username:");
        lblSignupUser.setBounds(300,400,100,25);
        loginPanel.add(lblSignupUser);

        signupUserField = new JTextField();
        signupUserField.setBounds(400,400,150,25);
        signupUserField.setName("signupUserField");
        loginPanel.add(signupUserField);

        JLabel lblSignupEmail = new JLabel("Email:");
        lblSignupEmail.setBounds(300,440,100,25);
        loginPanel.add(lblSignupEmail);

        signupEmailField = new JTextField();
        signupEmailField.setBounds(400,440,150,25);
        signupEmailField.setName("signupEmailField");
        loginPanel.add(signupEmailField);

        JLabel lblSignupPass = new JLabel("Password:");
        lblSignupPass.setBounds(300,480,100,25);
        loginPanel.add(lblSignupPass);

        signupPassField = new JPasswordField();
        signupPassField.setBounds(400,480,150,25);
        signupPassField.setName("signupPassField");
        loginPanel.add(signupPassField);

        signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(300,520,120,30);
        signupBtn.addActionListener(e -> signUp());
        loginPanel.add(signupBtn);
    }

    private void createDashboardPanel() {
        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(null);
        dashboardPanel.setBounds(0,0,900,700);

        // Post content
        JLabel lblPost = new JLabel("Enter content:");
        lblPost.setBounds(50,30,100,25);
        dashboardPanel.add(lblPost);

        postContentField = new JTextField();
        postContentField.setBounds(150,30,400,25);
        postContentField.setName("postContentField");
        dashboardPanel.add(postContentField);

        JButton postBtn = new JButton("Post Options");
        postBtn.setBounds(570,30,150,25);
        postBtn.addActionListener(e -> postOptions());
        dashboardPanel.add(postBtn);

        // Follow / Unfollow
        JLabel lblFollow = new JLabel("Follow username:");
        lblFollow.setBounds(50,70,120,25);
        dashboardPanel.add(lblFollow);

        followUserField = new JTextField();
        followUserField.setBounds(170,70,150,25);
        followUserField.setName("followUserField");
        dashboardPanel.add(followUserField);

        JButton btnFollow = new JButton("Follow");
        btnFollow.setBounds(330,70,80,25);
        btnFollow.addActionListener(e -> followUser());
        dashboardPanel.add(btnFollow);

        JLabel lblUnfollow = new JLabel("Unfollow username:");
        lblUnfollow.setBounds(430,70,130,25);
        dashboardPanel.add(lblUnfollow);

        unfollowUserField = new JTextField();
        unfollowUserField.setBounds(560,70,150,25);
        unfollowUserField.setName("unfollowUserField");
        dashboardPanel.add(unfollowUserField);

        JButton btnUnfollow = new JButton("Unfollow");
        btnUnfollow.setBounds(720,70,100,25);
        btnUnfollow.addActionListener(e -> unfollowUser());
        dashboardPanel.add(btnUnfollow);

        // Find User
        JLabel lblFindUser = new JLabel("Find username:");
        lblFindUser.setBounds(50,110,100,25);
        dashboardPanel.add(lblFindUser);

        findUserField = new JTextField();
        findUserField.setBounds(150,110,150,25);
        findUserField.setName("findUserField");
        dashboardPanel.add(findUserField);

        JButton btnFindUser = new JButton("Find");
        btnFindUser.setBounds(310,110,80,25);
        btnFindUser.addActionListener(e -> findUser());
        dashboardPanel.add(btnFindUser);

        // Notifications area
        JLabel lblNotif = new JLabel("Notifications:");
        lblNotif.setBounds(50,150,100,25);
        dashboardPanel.add(lblNotif);

        notifArea = new JTextArea();
        notifArea.setBounds(50,180,300,150);
        notifArea.setEditable(false);
        notifArea.setName("notifArea");
        JScrollPane notifScroll = new JScrollPane(notifArea);
        notifScroll.setBounds(50,180,300,150);
        dashboardPanel.add(notifScroll);

        JButton btnRefreshNotif = new JButton("Refresh Notifications");
        btnRefreshNotif.setBounds(50,340,200,25);
        btnRefreshNotif.addActionListener(e -> refreshNotifications());
        dashboardPanel.add(btnRefreshNotif);

        // DM area
        JLabel lblDM = new JLabel("Direct Messages:");
        lblDM.setBounds(400,150,150,25);
        dashboardPanel.add(lblDM);

        dmArea = new JTextArea();
        dmArea.setBounds(400,180,450,150);
        dmArea.setEditable(false);
        dmArea.setName("dmArea");
        JScrollPane dmScroll = new JScrollPane(dmArea);
        dmScroll.setBounds(400,180,450,150);
        dashboardPanel.add(dmScroll);

        JLabel lblDMTo = new JLabel("To username:");
        lblDMTo.setBounds(400,340,100,25);
        dashboardPanel.add(lblDMTo);

        dmReceiverField = new JTextField();
        dmReceiverField.setBounds(500,340,150,25);
        dmReceiverField.setName("dmReceiverField");
        dashboardPanel.add(dmReceiverField);

        JButton btnSendDM = new JButton("Send DM");
        btnSendDM.setBounds(670,340,100,25);
        btnSendDM.addActionListener(e -> sendDM());
        dashboardPanel.add(btnSendDM);

        JButton btnInbox = new JButton("View Inbox");
        btnInbox.setBounds(400,370,150,25);
        btnInbox.addActionListener(e -> viewInbox());
        dashboardPanel.add(btnInbox);

        // Logout
        logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(750,30,100,30);
        logoutBtn.addActionListener(e -> logout());
        dashboardPanel.add(logoutBtn);
    }

    // ---------- Action Methods ----------
    private void login() {
        try {
            String username = loginUserField.getText();
            String password = new String(loginPassField.getPassword());
            DB.connect();
            currentUser = User.login(username,password);
            if(currentUser!=null){
                JOptionPane.showMessageDialog(this,"Login successful!");
                loginPanel.setVisible(false);
                dashboardPanel.setVisible(true);
                refreshNotifications();
            } else {
                JOptionPane.showMessageDialog(this,"Login failed");
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void signUp() {
        try {
            String username = signupUserField.getText();
            String email = signupEmailField.getText();
            String password = new String(signupPassField.getPassword());
            if(User.signUp(username,password,email,"0000000000")){
                JOptionPane.showMessageDialog(this,"Signup successful!");
            } else {
                JOptionPane.showMessageDialog(this,"Signup failed!");
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void postOptions() {
        String content = postContentField.getText();
        String[] options = {"Post", "Post a Moment", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,"Choose post type:","Post Options",
                JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
        try {
            if(choice==0){
                if(Post.create(currentUser.id, content)){
                    JOptionPane.showMessageDialog(this,"Post successful!");
                }
            } else if(choice==1){
                if(Post.createMoment(currentUser.id, content)){
                    JOptionPane.showMessageDialog(this,"Moment posted! ⏳");
                }
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void followUser() {
        String user = followUserField.getText();
        if(User.follow(currentUser.id,user)){
            JOptionPane.showMessageDialog(this,"Followed "+user);
        } else {
            JOptionPane.showMessageDialog(this,"Failed to follow "+user);
        }
    }

    private void unfollowUser() {
        String user = unfollowUserField.getText();
        try{
            PreparedStatement ps = db.DB.con.prepareStatement("SELECT user_id FROM users WHERE username=?");
            ps.setString(1,user);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int followeeId = rs.getInt("user_id");
                User.unfollowUser(currentUser.id,followeeId);
                JOptionPane.showMessageDialog(this,"Unfollowed "+user);
            } else {
                JOptionPane.showMessageDialog(this,"User not found");
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void findUser() {
        String user = findUserField.getText();
        try{
            User.viewUserProfile(new java.util.Scanner(System.in),currentUser.id,user);
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void sendDM() {
        String toUser = dmReceiverField.getText();
        String msg = JOptionPane.showInputDialog("Enter message to "+toUser);
        try{
            MessageService.sendMessage(currentUser.id,toUser,msg);
            JOptionPane.showMessageDialog(this,"Message sent!");
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void viewInbox() {
        dmArea.setText("");
        try{
            java.io.PrintStream ps = new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b)
                { dmArea.append(String.valueOf((char)b)); }
            });
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            MessageService.viewInbox(currentUser.id);
            System.setOut(old);
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void refreshNotifications() {
        notifArea.setText("");
        try{
            java.io.PrintStream ps = new java.io.PrintStream(new java.io.OutputStream() {
                @Override
                public void write(int b){ notifArea.append(String.valueOf((char)b)); }
            });
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            NotificationService.showNotifications(currentUser.id);
            System.setOut(old);
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void logout() {
        try{
            User.logout(currentUser.username);
            currentUser=null;
            loginUserField.setText("");
            loginPassField.setText("");
            signupUserField.setText("");
            signupPassField.setText("");
            signupEmailField.setText("");
            postContentField.setText("");
            followUserField.setText("");
            unfollowUserField.setText("");
            findUserField.setText("");
            dmReceiverField.setText("");
            notifArea.setText("");
            dmArea.setText("");

            dashboardPanel.setVisible(false);
            loginPanel.setVisible(true);
        } catch(Exception e){ e.printStackTrace(); }
    }

    public static void main(String[] args) {
        new SocialMediaSimulator();
    }
}
