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
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
public class Server 
{
    static DB db = new DB();
    public static void main(String args[])
    {
        try
        {
            String username;
            String password;
            PrintWriter p;
            Scanner s;
            final int port = 1808;
            ServerSocket SERVER = null;
            try
            {
                SERVER = new ServerSocket(port);
                System.out.println("Authorization Server Started");
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            System.out.println("Waiting for clients...");

            while(true)
            {
                try
                {
                   Socket sock = SERVER.accept();
                   s = new Scanner(sock.getInputStream());
                   p = new PrintWriter(sock.getOutputStream());
                   String str = s.nextLine();
                   if(str.equals("S"))
                   {
                       SignUp(sock,s,p);
                   }
                   else if(str.equals("L"))
                   {
                       Login(sock, s, p);
                   }
                   else if(str.equals("M"))
                   {
                      getPort(sock, s, p);
                   }
                   else if(str.equals("A"))
                   {
                       addFriend(sock, s, p);
                   }
                   else if(str.equals("R"))
                   {
                       RefreshUsers(sock,s,p);
                   }
                   else if(str.equals("F"))
                   {
                       RefreshFriends(sock,s,p);
                   }
                   else if(str.equals("O"))
                   {
                       Logout(sock,s,p);
                   }
                   sock.close();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    System.out.println(ex);
                }
               
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }
    }
    static void SignUp(Socket sock,Scanner s, PrintWriter p)
    {
        String username;
        String password;
        int connect = 0;
        username = s.nextLine();
        password = s.nextLine();
        String sql = "select * from Record where UserName = '"+username+"'";
        ResultSet rs;
        try {
            rs = Server.db.runSql(sql);
            if(rs.next())
            {
                p.println("Not Ok");
                p.flush();
            }
            else
            {
                String sql_1 = "INSERT INTO Record (UserName, Password, IPAddress, isConnected, Friends) VALUES ('"+username+"','"+password+"','"+sock.getInetAddress().getHostAddress()+"','"+connect+"','"+username+"')";
                Server.db.runSql2(sql_1);
                p.println("Ok");
                p.flush();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
        
                
 
    }
    static void Login(Socket sock, Scanner s, PrintWriter p)
    {
        String username;
        String password;
        int port;
        port = Integer.parseInt(s.nextLine());
        username = s.nextLine();
        password = s.nextLine();
        String sql = "select * from Record where UserName = '"+username+"'";
        try {
            ResultSet rs = Server.db.runSql(sql);
            if(rs.next())
            {
                if(rs.getString("Password").equals(password))
                {
                    int connect = 1;
                    String sql_1 = "Update Record Set Port = "+port+" where UserName = '"+username+"'";
                    String sql_2 = "Update Record Set isConnected = "+connect+" where UserName = '"+username+"'";
                    Server.db.runSql2(sql_1);
                    Server.db.runSql2(sql_2);
                    p.println("Ok");
                    p.flush();
                    System.out.println("Client connected from: " + sock.getLocalAddress().getHostName());
                }
                else
                {
                    p.println("Not Ok");
                    p.flush();
                }
            }
            else
            {
                p.println("Not Ok");
                p.flush();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }  
    }
    static void getPort(Socket sock, Scanner s, PrintWriter p)
    {
        int port = 0;
        String username;
        String hostname;
        String to_friend = s.nextLine();
        username = s.nextLine();
        String sql = "select * from Record where UserName = '"+to_friend+"'";
        String sql_1 = "select * from Record where Username = '"+username+"'";
        try {
            ResultSet rs = Server.db.runSql(sql);
            ResultSet rs_1 = Server.db.runSql(sql_1);
            if(rs.next() && rs_1.next())
            {
                if(rs.getInt("isConnected") == 1)
                {
                   // p.println(rs.getInt("isConnected"));
                    port = rs.getInt("Port");
                    hostname = rs.getString("IPAddress");
                   // p.println(port);
                    p.println("Ok\n"+port+"\n"+hostname);
                    p.flush();
                }
                else
                {
                    p.println("Not Ok");
                    p.flush();
                }
                //p.println("Ok");
                //p.flush();
                boolean b = false;
                port = rs.getInt("Port");
                String x = rs.getString("Friends");
                String[] arr = x.split(" ");
                
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals(username))
                    {
                        b = true;
                        break;
                    }
                }
                if(!b)
                {
                    x = x +" "+username;
                    String y = rs_1.getString("Friends");
                    y = y +" "+to_friend;
                    String s_1 = "Update Record Set Friends = '"+x+"' where UserName = '"+to_friend+"'";
                    Server.db.runSql2(s_1);
                    String s_2 = "Update Record Set Friends = '"+y+"' where UserName = '"+username+"'";
                    Server.db.runSql2(s_2);
                }
                
            }
            else
            {
                p.println("Not Ok");
                p.flush();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
    }
    static void addFriend(Socket sock, Scanner s, PrintWriter p)
    {
        boolean b = false;
        String to_friend = s.nextLine();
        String username = s.nextLine();
        String sql = "select * from Record where UserName = '"+to_friend+"'";
        String sql_1 = "select * from Record where UserName = '"+username+"'";
        try
        {
            ResultSet rs = Server.db.runSql(sql);
            ResultSet rs_1 = Server.db.runSql(sql_1);            
            if(rs.next() && rs_1.next())
            {  
                String x = rs.getString("Friends");
                String y = rs_1.getString("Friends");
                String[] arr = y.split(" ");
                
                for (int i = 0; i < arr.length; i++) {
                    if(arr[i].equals(to_friend))
                    {
                        b = true;
                        break;
                    }
                }
                if(b)
                {
                   p.println("Not Ok");
                   p.flush(); 
                }
                else
                {
                    x = x +" "+ username;
                    y = y +" "+to_friend;
                    String s_1 = "Update Record Set Friends = '"+x+"' where UserName = '"+to_friend+"'";
                    Server.db.runSql2(s_1);
                    String s_2 = "Update Record Set Friends = '"+y+"' where UserName = '"+username+"'";
                    Server.db.runSql2(s_2);
                    p.println("Ok");
                    p.flush();
                }
            }
            else
            {
                p.println("Not Ok");
                p.flush();
            }
        } 
            catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
        }
    }
    static void RefreshUsers(Socket sock, Scanner s, PrintWriter p)
    {
        String username = s.nextLine();
        String sql = "select * from Record";
        ResultSet rs;
        String users = "";
        try {
            rs = Server.db.runSql(sql);
            while(rs.next())
            {
                users += rs.getString("UserName")+" ";                
            }
            p.println(users);
            p.flush();
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
    }
    static void RefreshFriends(Socket sock, Scanner s, PrintWriter p)
    {
        String username = s.nextLine();
        String sql = "select * from Record where UserName = '"+username+"'";
        try {
            ResultSet rs = Server.db.runSql(sql);
            if(rs.next())
            {
                String friends = rs.getString("Friends");
                p.println(friends);
                p.flush();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    static void Logout(Socket sock, Scanner s, PrintWriter p)
    {
        String username = s.nextLine();
        String sql = "select * from Record where UserName = '"+username+"'";
        try {
            ResultSet rs = Server.db.runSql(sql);
            if(rs.next())
                {
                
                if(rs.getInt("isConnected") == 1)
                {
                    int connect = 0;
                    String sql_1 = "Update Record Set isConnected = "+connect+" where UserName = '"+username+"'";
                    Server.db.runSql2(sql_1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
        
    }
}

//class Client implements Runnable
//{
//    
//    public Client(Socket sock) throws SQLException,IOException
//    {
//	this.sock = sock;
//        s = new Scanner(sock.getInputStream());
//        p = new PrintWriter(sock.getOutputStream());
//    }
//    public void SignUp(Socket sock)
//    {
//        
//    }
//    public void run()
//    {
//        try
//        {
//            s = new Scanner(sock.getInputStream());
//            p = new PrintWriter(sock.getOutputStream());
//            SignUp(sock)
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//}

class DB {
	 
	public static Connection conn = null;
 
	public DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/P2P_Chat_Server?zeroDateTimeBehavior=convertToNull";
			conn = DriverManager.getConnection(url, "root", "");
			System.out.println("conn built");
		} catch (SQLException e) {
                    System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		}
	}
 
	public ResultSet runSql(String sql) throws SQLException {
		Statement sta = conn.createStatement();
		return sta.executeQuery(sql);
	}
 
	public void runSql2(String sql) throws SQLException {
		Statement sta = conn.createStatement();
		sta.executeUpdate(sql);
	}
 
	@Override
	protected void finalize() throws Throwable {
		if (conn != null || !conn.isClosed()) {
			conn.close();
		}
	}
}
