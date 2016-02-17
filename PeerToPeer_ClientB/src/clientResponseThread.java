import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class clientResponseThread extends Thread
{
	Socket requestClientSoc;
	String clientName;
	Request peerRequest;
	Response response;
	ObjectInputStream ois=null;
	FileInputStream fi=null;
	BufferedInputStream BS=null;
	OutputStream OS=null;
	public clientResponseThread(Socket requestClientSocket, String ClientName)
	{
		this.requestClientSoc=requestClientSocket;
		this.clientName=ClientName;
	}
	
	public void run()
	{
		try 
		{
			readRequestObject();
			String FileName=peerRequest.getFileName();
			File clientFolder= new File("D:/P2P Project/"+clientName);
			File[] clientFiles=clientFolder.listFiles();
			for(File file:clientFiles)
			{
				if(file.isFile()&& file.getName().equals(FileName))
				{
					if(peerRequest.getRequestType()==null)
					{
						doesFileExist();
						requestClientSoc.close();
						break;
					}
					else if(peerRequest.getRequestType()==RequestType.RequestFile)
					{
						sendFile(file);
						requestClientSoc.close();
						break;
					}
					else if(peerRequest.getRequestType()==RequestType.DontRequestFile)
					{
						requestClientSoc.close();
						break;
					}
				}
			}	
		}
	

		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			System.out.println("No host found");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Something went wrong, check IP and Port");
		}
	}

	private void doesFileExist()
	{
		ObjectOutputStream oos;
		try 
		{
			oos = new ObjectOutputStream(requestClientSoc.getOutputStream());
			response = new Response();
			response.setfileExits(true);
			oos.writeObject(response);
			System.out.println("sent that fie exists");
			return;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
	}
	
	private void sendFile(File file) throws IOException
	{
		byte[] by= new byte[512000];		
		fi=new FileInputStream(file);
		BS=new BufferedInputStream(fi);
		OS=requestClientSoc.getOutputStream();
		int bytesRead=0;
		while((bytesRead=BS.read(by, 0, by.length))!=-1)
		{
			OS.write(by, 0, bytesRead);
		}
		OS.close(); //have to close this port to end the outputstream, else will through 
					// stack exception
		System.out.println("File Sent");
		return;
	}

	private void readRequestObject()
	{
		try 
		{
				//System.out.println("came to reading the object");
				ois=new ObjectInputStream(requestClientSoc.getInputStream());
				peerRequest=(Request) ois.readObject();
				//System.out.println("read the request first");	
		} 
		catch (IOException | ClassNotFoundException e) 
		{
			
			e.printStackTrace();
		}
		
	}
}

