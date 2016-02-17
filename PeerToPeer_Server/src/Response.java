import java.io.Serializable;
import java.util.ArrayList;


public class Response implements Serializable{

	public final String getMessage() {
		return message;
	}
	public final void setMessage(String message) {
		this.message = message;
	}
	public final boolean isSuccess() {
		return success;
	}
	public final void setSuccess(boolean success) {
		this.success = success;
	}
	public final ArrayList<Request> getPeerList() {
		return peerList;
	}
	public final void setPeerList(ArrayList<Request> peerList) {
		this.peerList = peerList;
	}
	
	public final boolean isfileExists() {
		return fileExists;
	}
	public final void setfileExits(boolean fileExists) {
		this.fileExists = fileExists;
	}
	private String message;
	private boolean success;
	private ArrayList<Request> peerList; 
	private boolean fileExists;
}
