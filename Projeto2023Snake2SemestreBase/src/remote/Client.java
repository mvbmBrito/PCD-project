package remote;

import java.io.*;
import java.net.Socket;
import java.util.Random;

public class Client extends Thread {
    private Socket socket;
    private PrintWriter out;
    private ObjectInputStream ois;

    public Client(String addr, int port) {
        try {
            this.socket = new Socket(addr, port);
            this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            
            Random random = new Random();
            while (true) {
                int x = random.nextInt(7);  
                int y = random.nextInt(7);
                String coord = x + "," + y;
                System.out.println("Sending coordinates: " + coord);
                out.println(coord);  

                ActionResult result = (ActionResult) ois.readObject();
                System.out.println("Received response: " + result);

                if (result.gameEnded()) {
                    System.out.println("Game has ended.");
                    break;
                }

                Thread.sleep(1000); 
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 8885).start();
    }
}
