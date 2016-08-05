/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author titas
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Sender extends Thread {

    String message, hostname, to_user;
    int port;
    
    public Sender(String message, String hostname, int port, String to_user) {
        this.message = to_user+": "+message;
        this.hostname = hostname;
        this.port = port;
        this.to_user = to_user;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(hostname, port);
            PrintWriter p = new PrintWriter(s.getOutputStream());
            p.println(message);
            p.flush();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(MessageTransmitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
