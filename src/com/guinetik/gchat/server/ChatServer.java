package com.guinetik.gchat.server;

import java.net.*;
import java.io.*;
import java.util.*;
public class ChatServer extends Thread {
    protected ServerSocket socketServer;
    protected int port;
    protected boolean listening;
    protected Vector<ChatServerConnection> clientConnections;
    public ChatServer(int serverPort) {
        this.port = serverPort;
        this.clientConnections = new Vector<ChatServerConnection>();
        this.listening = false;
    }
    public int getPort() {
        return this.port;
    }
    public int getClientCount() {
        return this.clientConnections.size();
    }
    protected void debug(String msg) {
        Main.debug("ChatServer (" + this.port + ")", msg);
    }
    public void run() {
        try {
            this.socketServer = new ServerSocket(this.port);
            this.listening = true;
            debug("listening");

            while (listening) {
                Socket socket = this.socketServer.accept();
                debug("client connection from " + socket.getRemoteSocketAddress());
                ChatServerConnection socketConnection = new ChatServerConnection(socket, this);
                socketConnection.start();
                this.clientConnections.add(socketConnection);
            };
        }
        catch (Exception e) {
            debug(e.getMessage());
        }
    }
    public void writeToAll(String msg) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
                ChatServerConnection client = this.clientConnections.get(i);
                client.write(msg);
            }
            debug("broadcast message '" + msg + "' was sent");
        }
        catch (Exception e) {
            debug("Exception (writeToAll): " + e.getMessage());
        }
    }
    public boolean remove(SocketAddress remoteAddress) {
        try {
            for (int i = 0; i < this.clientConnections.size(); i++) {
                ChatServerConnection client = this.clientConnections.get(i);

                if (client.getRemoteAddress().equals(remoteAddress)) {
                    this.clientConnections.remove(i);
                    debug("client " + remoteAddress + " was removed");
                    writeToAll(remoteAddress + " has disconnected.");

                    return true;
                }
            }
        }
        catch (Exception e) {
            debug("Exception (remove): " + e.getMessage());
        }

        return false;
    }
    protected void finalize() {
        try {
            this.socketServer.close();
            this.listening = false;
            debug("stopped");
        }
        catch (Exception e) {
            debug("Exception (finalize): " + e.getMessage());
        }
    }
}
