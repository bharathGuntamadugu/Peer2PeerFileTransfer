import java.io.*;
import java.net.*;

public class ServerThread extends Thread{
	Socket ClientSock;
	ObjectInputStream ISR=null;
	
	public ServerThread(Socket clientSock)
	{
		this.ClientSock=clientSock;
	}
	
	public void run()
	{
		try 
		{
			ObjectInputStream ois= new ObjectInputStream(ClientSock.getInputStream());
			Request request = (Request) ois.readObject();
			RequestType requestType = request.getRequestType();
			if(requestType==RequestType.Register)
			{
				//Inserting request into Array
				boolean repeatRequest=false;
				for(int i=0;i<Server.peerList.size();i++)
				{
					Request print=Server.peerList.get(i);
					if(print.getPort()==request.getPort())
					{
						repeatRequest=true;
						break;
					}
				}
				if(repeatRequest)
				{ 
					Response response=new Response();
					response.setSuccess(false);
					if(!response.isSuccess())
						response.setMessage("You have already been registered with Server");
					response.setPeerList(null);	
					sendObject(response,true);
				}
					
				else
				{
					Server.peerList.add(request); 
					Response response=new Response();
					response.setSuccess(true);
					if(response.isSuccess())
						response.setMessage("Successfully Registered!");
					else
						response.setMessage("Failed to registered. Please try again");
					response.setPeerList(null);	
					sendObject(response,true);
				}
			}
			else if(requestType==RequestType.RequestPeerList)
			{
				// delete this for loop after all erros				
				for(int i=0;i<Server.peerList.size();i++)
				{
					Request print=Server.peerList.get(i);
					System.out.println(print.getPort());
				}
				Response response=new Response();
				response.setMessage("PeerList Received");
				response.setSuccess(true);
				response.setPeerList(Server.peerList);	
				sendObject(response,true);
			}
			else if(requestType==RequestType.Unregister)
			{
				Response response=new Response();
				response.setSuccess(unregisterClient(request.getPort()));
				if(response.isSuccess())
				{
					response.setMessage("Unregistered Successfully");
				}
				else
				{
					response.setMessage("You are not registered with Server");
				}
				sendObject(response,true);
			}

		} catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}
	private boolean unregisterClient(int clientPort)
	{
		if(Server.peerList.isEmpty())
			return false;
		else
		{
			for(int i=0; i<Server.peerList.size();i++)
			{
				Request client=Server.peerList.get(i);
				if(client.getPort()==clientPort)
				{
					Server.peerList.remove(i);
					return true;
				}
				else if(i==Server.peerList.size())
					return false;
			}
		}
		return false;
	}
	
	private void sendObject(Response response, boolean fromServer) // added boolean response true for server
	{
		try
		{
			if(fromServer)
			{
				ObjectOutputStream oos = new ObjectOutputStream(ClientSock.getOutputStream());
				oos.writeObject(response);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
