package com.guinetik.gchat.server;

import java.util.*;
import javax.swing.*;
public class UpdateClientCountTask extends TimerTask {
    protected JLabel clientsLabel;
    protected ChatServer chatServer;
    public UpdateClientCountTask(ChatServer chatServer, JLabel clientsLabel) {
        this.chatServer = chatServer;
        this.clientsLabel = clientsLabel;
    }
    public void run() {
        int count = this.chatServer.getClientCount();
        String msg = count + " client" + ((count != 1) ? "s" : "");
        this.clientsLabel.setText(msg);
    }
}
