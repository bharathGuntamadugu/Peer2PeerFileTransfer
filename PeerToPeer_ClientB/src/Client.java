import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	
	protected static int ServerPort = 0;// = 11111;
	// Can be changes to the actual Server IP Address.
	protected static String ServerIpAddress = null;// = "127.0.0.1";
	protected static int ClientPort=0;// = 11113;
	protected static String ClientName=null;// =  "ClientB";
	
	static ServerSocket ClientServerSock;
	static Socket requestClientSoc;
	Socket socket;
	Response serverResponse;
	Response peerResponse;
	// made request and response public 
	Request request;
	Request otherPeer;
	//boolean peerConversation=false;
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Give input in this format");
		System.out.println("Client.jar <Port> <Name> <Server Ip> <Server port>");
		Client.ClientPort = Integer.parseInt(args[0]);
		Client.ClientName = args[1];
		Client.ServerIpAddress = args[2];
		Client.ServerPort = Integer.parseInt(args[3]);

		try 
		{
			clientRequestThread requestThread = new clientRequestThread();
			requestThread.start();
			
			ClientServerSock=new ServerSocket(ClientPort);
			System.out.println("\n Client listening on "+ClientPort);
			while(true)
			{
				requestClientSoc=ClientServerSock.accept();
				clientResponseThread thread=new clientResponseThread(requestClientSoc, ClientName);
				thread.start();
			}
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
		
	public void run() throws IOException 
	{
		Scanner scanner=new Scanner(System.in);
		while(true)
		{
			System.out.println("1. Register");
			System.out.println("2. Get Peer List");
			System.out.println("3. Unregister");
			System.out.println("4. Exit from Server Connection");
			int input = scanner.nextInt();
			if(input==1)
				Register();
			else if(input==2)
				RequestPeerList();
			else if(input==3)
				UnRegister();
			else if(input==4)
			{	
				//scanner.close();
				break; //changed from return to break;
			}
			socket.close(); // closing server and client socket here, so that I can use same socket for other peer
		}
		
		//wrote this while loop
		while(true)
		{
			System.out.println("1. Contact other peers to look for file you want? Please be registered before searching");
			System.out.println("2. Close the program");
			int check = scanner.nextInt();
			if(check==2)
			{
				scanner.close();
				return;
			}
			else if(check==1)
			{
				System.out.println("Enter file name with extension");
				String FileName = null;
				if(scanner.hasNext())
					FileName=scanner.next();
				request = new Request();
				request.setIpAddr(InetAddress.getLocalHost());
				request.setPort(ClientPort);
				request.setHostName(ClientName);
				request.setFileName(FileName);
				String see =request.getFileName();
				
				//request.setRequestType(RequestType.RequestFile);
				contactPeers(request);		
			}
			return;
		}	
	}
	
	private void Register() throws UnknownHostException
	{
		request = new Request();
		request.setIpAddr(InetAddress.getLocalHost());
		request.setPort(ClientPort);
		request.setHostName(ClientName);
		request.setRequestType(RequestType.Register);
		sendObject2Server(request);
		returnResponse(true);
	}
	
	private void UnRegister()
	{
		request = new Request();
		request.setRequestType(RequestType.Unregister);
		request.setPort(ClientPort);
		sendObject2Server(request);
		returnResponse(true);
	}
	
	private void RequestPeerList()
	{
		request = new Request();
		request.setRequestType(RequestType.RequestPeerList);
		sendObject2Server(request);
		returnResponse(true);
	}
	
	private void sendObject2Server(Request request)
	{
		try
		{
			socket = new Socket(ServerIpAddress,ServerPort);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(request);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private void sendObject2Client(Request request, int port)
	{
		try 
		{
			socket=new Socket("127.0.0.1",port);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(request);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	private int returnResponse(boolean fromServer)
	{
		try 
		{	
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			if(fromServer)
			{	
				serverResponse = (Response) ois.readObject();
				System.out.println(serverResponse.getMessage());
				System.out.println("");
				socket.close();
				return 0;
			}
			else
			{
				peerResponse = (Response) ois.readObject();
				if(peerResponse.isfileExists())
				{
					System.out.println("File Exists...\n");
					socket.close();
					return 1;
					
				}
				else return 0;
			}
		}
		catch (IOException | ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		return 0;
	}

	@SuppressWarnings("resource")
	
	private void contactPeers(Request request) throws UnknownHostException, IOException
	{
		for(int i=0;i<serverResponse.getPeerList().size();i++)
		{
			otherPeer=serverResponse.getPeerList().get(i);
			if(otherPeer.getPort()!=ClientPort)
			{
				sendObject2Client(request, otherPeer.getPort());
				
				if(returnResponse(false)==1)
				{
					int x=receiveFile();
					if(x==0)
					{
						System.out.println("File received");
						return;
					}
					else if(x==1)
					{
						System.out.println("You aborted connections with peers \n");
						return;
					}
					else if(x==2)
					{
						System.out.println("Looking in other Peers \n");
					}
				}
			}
		}
		System.out.println("File not available");
	}
	
		
	
	private int receiveFile() throws UnknownHostException, IOException
	{
		System.out.println("1. Download the file");
		System.out.println("2. Exit the peer connection");
		Scanner scanner=new Scanner(System.in);
		int scan=scanner.nextInt();
		if(scan==1)
		{
			request.setDownload(true);
			request.setRequestType(RequestType.RequestFile);
			//peerConversation=true;
			System.out.println("sending request to peer");
			sendObject2Client(request, otherPeer.getPort());
			System.out.println("request sent");
			FileOutputStream OS=null; 
			InputStream IS=null;
			// have to either get file or file not found
			IS=socket.getInputStream();
			OS= new FileOutputStream("D:/P2P Project/ClientA/"+request.getFileName());
			int bytesRead=0;
			byte[] b=new byte[512000]; 
			while((bytesRead=IS.read(b, 0, b.length))!=-1)
			{ 
				OS.write(b, 0, bytesRead);	
			}
			return 0;
		}
		else if(scan==2)
		{
			request.setDownload(false);
			request.setRequestType(RequestType.DontRequestFile);
			//peerConversation=true;
			sendObject2Client(request, otherPeer.getPort());
			return 1;
		}
		else
			return 2;
	}
}
