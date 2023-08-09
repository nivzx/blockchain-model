package org.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {

        String ip1 = "192.168.8.100"; //inushi
        String ip2 = "192.168.8.113"; //nivin
        String ip3 = "192.168.8.103"; // tharu

        Node node1 = new Node(ip1, 5000);
        Node node2 = new Node(ip1, 5001);
        Node node3 = new Node(ip1, 5002);

        node1.joinNetwork(ip2);
        node1.joinNetwork(ip3);

        BroadcastAPI api1 = new BroadcastAPI(node1, 8000);
        api1.start();

        // Start broadcasting messages
//        node1.broadcastMessage("Hello from node 1!");
    }
}
