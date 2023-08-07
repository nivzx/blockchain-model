package org.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        String ip1 = "172.20.10.3";
        String ip2 = "172.20.10.7";
        String ip3 = "172.20.10.4";

        Node node1 = new Node(ip1, 5000);
        Node node2 = new Node(ip2, 5001);
        Node node3 = new Node(ip3, 5002);

        node2.joinNetwork(ip1);
        node2.joinNetwork(ip3);

        BroadcastAPI api2 = new BroadcastAPI(node2, 8000);
        api2.start();
    }
}
