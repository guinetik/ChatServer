package com.guinetik.gchat.server;

/**
 * Main is used to start the servers and handle the debug messages.
 *
 * @author Nuno Freitas (nunofreitas@gmail.com)
 */
public class Main {
    public static final boolean DEBUG = true;
    public static ChatServer chatServer;
    public static ChatServerGUI chatServerGUI;
    public static PolicyServer policyServer;
    public static void debug(String label, String msg) {
        if (DEBUG && Main.chatServerGUI != null) {
            Main.chatServerGUI.write(label + ": " + msg);
        }
    }
    public static void main(String[] args) {
        try {
            int chatPort = 5555;
            int policyPort = chatPort + 1;

            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    chatPort = Integer.parseInt(args[i]);
                }

                if (i == 1) {
                    policyPort = Integer.parseInt(args[i]);
                }
            }
            PolicyServer policyServer = new PolicyServer(policyPort);
            policyServer.start();

            ChatServer chatServer = new ChatServer(chatPort);
            chatServer.start();

            ChatServerGUI gui = new ChatServerGUI(chatServer);
            gui.setTitle("ChatServer");
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);

            Main.chatServer = chatServer;
            Main.chatServerGUI = gui;
            Main.policyServer = policyServer;
        }
        catch (Exception e) {
            debug("Main", "Exception (main)" + e.getMessage());
        }
    }
}
