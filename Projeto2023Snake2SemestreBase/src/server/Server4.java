package server;

import environment.LocalBoard;
import remote.ActionResult;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server4 extends Thread {
    private final LocalBoard board;

    public Server4(LocalBoard board) {
        this.board = board;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8885)) {
            while (!board.isGameOverV2()) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    System.out.println("Client connected.");

                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println("Received coordinates: " + line);
                        String[] parts = line.split(",");
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        ActionResult result = board.processCoordinates(x, y); 
                        System.out.println("Sending response: " + result);
                        out.writeObject(result);

                        if (board.isGameOverV2()) {
                            System.out.println("Game has ended on server side.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port: 8885");
        }
    }

    public static void main(String[] args) {
        LocalBoard board = new LocalBoard(); 
        new Server4(board).start();
    }
}
