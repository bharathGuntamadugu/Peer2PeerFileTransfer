import java.io.Serializable;
import java.net.InetAddress;

public class Request implements Serializable {

	public final InetAddress getIpAddr() {
		return ipAddr;
	}
	public final void setIpAddr(InetAddress ipAddr) {
		this.ipAddr = ipAddr;
	}
	public final int getPort() {
		return port;
	}
	public final void setPort(int port) {
		this.port = port;
	}
	public final String getHostName() {
		return hostName;
	}
	public final void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public final String getFileName() {
		return fileName;
	}
	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public final RequestType getRequestType() {
		return requestType;
	}
	public final void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public final boolean isDownload() {
		return download;
	}
	public final void setDownload(boolean download) {
		this.download = download;
	}
	private InetAddress ipAddr;
	private int port;
	private String hostName;
	private String fileName;
	private RequestType requestType;
	private boolean download;
	
}

