import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;


public class EchoServer implements DoByProtocal{

	@Override
	public void doByprotocal(Socket socket, Stopable stopable) {
		if (socket==null || stopable==null) {
			return;
		}
		try {
			BufferedReader reader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			writer.write("welcome to echo server\n");
			writer.flush();
			String line;
			while (!stopable.shouldStop())  {
				try {
					if ((line=reader.readLine())!=null) {
						if (line.equals("quit")) {
							System.out.println("session ended");
							break;
						}
						String msg=String.format("Received message:%s from:%s, port:%d",line, socket.getInetAddress(),socket.getPort());
						System.out.println(msg);
						writer.write("Server Echo:"+line+"\n");
						writer.flush();
					}
				}
				catch(SocketTimeoutException ioe) {
					//do nothing
				}
			}
			reader.close();
			writer.close();
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args) {
		try {
			TcpServer server=new TcpServer(8888,  4, new EchoServer());
			Thread t = new Thread(server);
			t.start();
			String cmd=null;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while((cmd=br.readLine())!=null) {
				if (cmd.equals("bye")) {
					break;
				}
			}
			server.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
