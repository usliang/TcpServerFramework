import java.net.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;


public class TcpServer implements Runnable {
	private ServerSocket serverSocket;
	private int port;
	private InetAddress inetAddress;
	private ExecutorService executorSerivce;
	private volatile boolean isServerListening;
	private DoByProtocal task;
	private Collection<Stopper> stoppers;
	private ArrayList<CustomProtocal> workers;
	private static final int TIME_OUT=10*1000;//10 second
	
	public TcpServer(int port, int conCurrentThreadCount, DoByProtocal task) throws IOException {
		this.port=port;
		isServerListening=false;
		this.task=task;
		stoppers=new ArrayList<>();
		serverSocket=new ServerSocket(port);
		serverSocket.setSoTimeout(TIME_OUT);
		executorSerivce=Executors.newFixedThreadPool(conCurrentThreadCount);
		workers=new ArrayList<>();
	}
	public synchronized boolean start() {
		boolean ret=false;
		if (!isServerListening) {
			try {
				isServerListening=true;
				while (isServerListening) {
					try {
						Socket clientSocket=serverSocket.accept();
						clientSocket.setSoTimeout(TIME_OUT);
						String msg=String.format("Get connected from:%s, port:%d", clientSocket.getInetAddress(),port);
						System.out.println(msg);
						Stopper stopper=new Stopper(false);
						stoppers.add(stopper);
						CustomProtocal customProtocal= new CustomProtocal(clientSocket,task,stopper);
						workers.add(customProtocal);
						executorSerivce.submit(customProtocal);
						ret=true;
					} catch (SocketTimeoutException e) {
						//do nothing
					}
				}
				
			} catch (Exception e) {
				isServerListening=false;
				System.out.println(e.getMessage());
			}
		}
		return ret;
	}
	@Override
	public void run() {
		start();
	}
	public void stop() {
		for(Stopper stopper : stoppers) {
			if (stopper!=null) {
				stopper.stop();
			}
		}
		executorSerivce.shutdownNow();
		isServerListening=false;
		Socket socket=null;
		for(CustomProtocal worker:workers) {
			try {
				socket=worker.socket;
				if (socket!=null & !socket.isClosed()) {
					socket.shutdownInput();
					socket.shutdownOutput();
					socket.close();
				}
			} catch (IOException ioe) {
				
			}
		}
		workers.clear();
	}
}

class Stopper implements Stopable{
	private volatile boolean stopSignal;
	public Stopper(boolean stopSignal) {
		this.stopSignal=stopSignal;
	}
	@Override
	public boolean shouldStop() {
		return stopSignal;
	}
	public void stop() {
		stopSignal=true;
	}
}


