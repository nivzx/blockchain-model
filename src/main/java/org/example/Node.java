package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    private List<String> peersIPAddresses;
    private ServerSocket serverSocket;
    private String ipAddress;
    private List<Block> blockchain;

    public Node(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        peersIPAddresses = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinNetwork(String peerIPAddress) {
        peersIPAddresses.add(peerIPAddress);
    }

    public void broadcastMessage(String message) {
        for (String peerIPAddress : peersIPAddresses) {
            sendMessage(peerIPAddress, message);
        }
    }

    private void acceptConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleConnection(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message = in.readLine();
            System.out.println("Received message from peer " + clientSocket.getInetAddress() + ": " + message);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String peerIPAddress, String message) {
        try {
            Socket socket = new Socket(peerIPAddress, getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
