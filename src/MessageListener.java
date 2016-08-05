/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Titas
 */
public class MessageListener extends Thread {
    
    ServerSocket server;
    int port;
    WritableGUI gui;
    
    public MessageListener(WritableGUI gui, int port){
        this.port = port;
        this.gui = gui;
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Socket clientSocket;
        try {
            while((clientSocket = server.accept()) != null){
                InputStream i = clientSocket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(i)); 
                String line = br.readLine();
                if(line != null){
                    gui.write(line);
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
