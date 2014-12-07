package com.guinetik.gchat.server;

import java.net.*;
import java.io.*;
public class ChatServerConnection extends Thread
{
    protected Socket socket;
    protected BufferedReader socketIn;
    protected PrintWriter socketOut;
    protected ChatServer server;
    public ChatServerConnection(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }
    public SocketAddress getRemoteAddress() {
        return this.socket.getRemoteSocketAddress();
    }
    protected void debug(String msg) {
        Main.debug("ChatServerConnection (" + this.socket.getRemoteSocketAddress() + ")", msg);
    }
    public void run() {
        try {
            this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketOut = new PrintWriter(this.socket.getOutputStream(), true);
            this.server.writeToAll(this.getRemoteAddress() + " has connected.");
            String line = this.socketIn.readLine();

            while (line != null) {
                debug("client says '" + line + "'");
                if (line.compareToIgnoreCase("\\quit") == 0) {
                    if (this.server.remove(this.getRemoteAddress())) {
                        this.finalize();
                        return;
                    }
                }

                this.server.writeToAll(line);
//                this.server.writeToAll(this.getRemoteAddress() + " diz: " + line);
                line = this.socketIn.readLine();
            }
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }
    public void write(String msg) {
        try {
            this.socketOut.write(msg + "\u0000");
            this.socketOut.flush();
        }
        catch (Exception e) {
            debug("Exception (write): " + e.getMessage());
        }
    }
    protected void finalize() {
        try {
            this.socketIn.close();
            this.socketOut.close();
            this.socket.close();
            debug("connection closed");
        }
        catch (Exception e) {
            debug("Exception (finalize): " + e.getMessage());
        }
    }
}
