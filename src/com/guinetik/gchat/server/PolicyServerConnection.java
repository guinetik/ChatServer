package com.guinetik.gchat.server;

import java.io.*;
import java.net.*;
public class PolicyServerConnection extends Thread {
    protected Socket socket;
    protected BufferedReader socketIn;
    protected PrintWriter socketOut;
    public PolicyServerConnection(Socket socket) {
        this.socket = socket;
    }
    protected void debug(String msg) {
        Main.debug("PolicyServerConnection (" + this.socket.getRemoteSocketAddress() + ")", msg);
    }
    public void run() {
        try {
            this.socketIn = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.socketOut = new PrintWriter(this.socket.getOutputStream(), true);
            readPolicyRequest();
        }
        catch (Exception e) {
            debug("Exception (run): " + e.getMessage());
        }
    }
    protected void readPolicyRequest() {
        try {
            String request = read();
            debug("client says '" + request + "'");

            if (request.equals(PolicyServer.POLICY_REQUEST)) {
               writePolicy();
            }
        }
        catch (Exception e) {
            debug("Exception (readPolicyRequest): " + e.getMessage());
        }
        finalize();
    }
    protected void writePolicy() {
        try {
            this.socketOut.write(PolicyServer.POLICY_XML + "\u0000");
            this.socketOut.close();
            debug("policy sent to client");
        }
        catch (Exception e) {
            debug("Exception (writePolicy): " + e.getMessage());
        }
    }
    protected String read() {
        StringBuffer buffer = new StringBuffer();
        int codePoint;
        boolean zeroByteRead = false;

        try {
            do {
                codePoint = this.socketIn.read();

                if (codePoint == 0) {
                    zeroByteRead = true;
                }
                else if (Character.isValidCodePoint(codePoint)) {
                    buffer.appendCodePoint(codePoint);
                }
            }
            while (!zeroByteRead && buffer.length() < 200);
        }
        catch (Exception e) {
            debug("Exception (read): " + e.getMessage());
        }

        return buffer.toString();
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
