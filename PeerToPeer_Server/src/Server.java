 import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

	public final static int ServerPort = 1111;	
	public final static ArrayList<Request> peerList=new ArrayList<Request>();
	ServerSocket ServerSock;
	
	public static void main(String[] args)
	{
		Server server=new Server();  
		server.run();
	}
		
	public void run()
	{
		try 
		{
			ServerSock=new ServerSocket(ServerPort);
			System.out.println("Server listening on "+ServerPort);
			while(true)
			{
				Socket clientSoc=ServerSock.accept();
				ServerThread thread=new ServerThread(clientSoc);
				thread.start();
			}	
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

