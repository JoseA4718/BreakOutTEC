package SocketClient;

import java.io.*;
import java.net.*;

public class Client extends Thread{ 
	String sentence;
	static String jsonReceived;
	String currentSentence;
	
	public Client(String sentence) {
		super();
		this.sentence = sentence;
		this.jsonReceived = new String();
		this.currentSentence = new String();
	}


	public void run(){

        String modifiedSentence = new String();
        
        BufferedReader inFromUser =
          new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket;
		try {
			clientSocket = new Socket("localhost", 8080);
		

	        DataOutputStream outToServer =
	          new DataOutputStream(clientSocket.getOutputStream());
	
	        BufferedReader inFromServer =
	          new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
	        try {
	        	while(true) {
	                	esperar(1);
	                	
	                	if(sentence != currentSentence) {
	                		System.out.println("SENTENCE: "+sentence);
			                
			                outToServer.writeBytes('/' + sentence +'\n');
			                
			                modifiedSentence = inFromServer.readLine();
			                
		                	this.setJsonReceived("{"+modifiedSentence.substring(1, modifiedSentence.length()-1)+"}");
			                System.out.println("FROM SERVER: "+this.jsonReceived+"\n");          
	                		
			                currentSentence = sentence;
	                	} 
		                
	            }
	        	
	        }catch (SocketException socketException){
	            System.out.println("Socket exception: " + socketException);
	        }
	        clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void esperar(int segundos){
        try {
            Thread.sleep(segundos * 20);
         } catch (Exception e) {
            System.out.println(e);
         }
    }

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public static String getJsonReceived() {
		return jsonReceived;
	}

	public void setJsonReceived(String jsonReceived) {
		this.jsonReceived = jsonReceived;
	}
	
	
}