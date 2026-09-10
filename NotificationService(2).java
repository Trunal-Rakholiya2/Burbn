package feed;


import util.FileUtil;
import util.MyStack;
import java.util.*;

public class NotificationService {
    private static final Map<Integer, MyStack<String>> notificationStacks = new HashMap<>();

    public static synchronized void pushNotification(int userId, String message) {
        notificationStacks.putIfAbsent(userId, new MyStack<>());
        notificationStacks.get(userId).push(message);
        FileUtil.writeToFile("notifications.txt", "Notification for User " + userId + ": " + message);
    }

    public static synchronized void showNotifications(int userId) {
        MyStack<String> stack = notificationStacks.get(userId);
        if (stack == null || stack.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }
        System.out.println("\n--- Your Notifications ---");
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
    public static synchronized MyStack<String> getStack(int userId) {
        return notificationStacks.get(userId);
    }
}
