import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
public class CustomProtocal implements Runnable {
	DoByProtocal task;
	Socket socket;
	Stopable stopable;
	public CustomProtocal(Socket socket, DoByProtocal task, Stopable stopable) {
		this.task=task;
		this.socket=socket;
		this.stopable=stopable;
	}
	public void run() {
		task.doByprotocal(socket,stopable);
	}
} 

