package com.burbn.service;

import com.burbn.util.CustomStack;
import com.burbn.util.FileLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe notification service using custom LIFO Stack per user.
 */
public class NotificationService {
    private static final Map<Integer, CustomStack<String>> notificationStacks = new HashMap<>();

    /**
     * Pushes a new notification message onto a user's notification stack.
     */
    public static synchronized void pushNotification(int userId, String message) {
        notificationStacks.putIfAbsent(userId, new CustomStack<>());
        notificationStacks.get(userId).push(message);
        FileLogger.log("notifications.txt", "User #" + userId + ": " + message);
    }

    /**
     * Prints and clears all pending notifications for a user (CLI mode).
     */
    public static synchronized void showNotifications(int userId) {
        CustomStack<String> stack = notificationStacks.get(userId);
        if (stack == null || stack.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }
        System.out.println("\n🔔 --- Your Notifications ---");
        while (!stack.isEmpty()) {
            System.out.println(" • " + stack.pop());
        }
    }

    /**
     * Retrieves all pending notifications as a list for GUI rendering.
     */
    public static synchronized String[] getNotificationList(int userId) {
        CustomStack<String> stack = notificationStacks.get(userId);
        if (stack == null || stack.isEmpty()) {
            return new String[0];
        }
        String[] result = new String[stack.size()];
        int idx = 0;
        while (!stack.isEmpty()) {
            result[idx++] = stack.pop();
        }
        return result;
    }
}
