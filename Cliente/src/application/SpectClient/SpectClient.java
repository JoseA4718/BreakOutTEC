package application.SpectClient;

import java.io.*;
import java.net.*;
 
public class SpectClient extends Thread{
	
    public void run(){
        int port=8080;
        while(true) {
            try {
                Socket clientSocket = new Socket("localhost", port);
 
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
 
                System.out.println("Send: Hi");
                out.println("Hi");
                System.out.println("Receive: " + in.readLine());
 
                out.close();
                in.close();
                clientSocket.close();
            } catch (UnknownHostException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}