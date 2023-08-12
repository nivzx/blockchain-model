package org.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        String ip1 = "172.20.10.8"; //inushi
        String ip2 = "172.20.10.6"; //nivin
        String ip3 = "170.20.10.14"; //tharani

        Node node2 = new Node(ip2, 5001);

        node2.joinNetwork(ip1);
        node2.joinNetwork(ip3);

        BroadcastAPI api1 = new BroadcastAPI(node2, 8000);
        api1.start();

        while(true) {
            if (!node2.getTxPool().isEmpty()) {
                node2.mineBlock();
            }
        }
    }
}
