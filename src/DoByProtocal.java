
import java.net.*;

public interface DoByProtocal{
	public void doByprotocal(Socket socket, Stopable stopable);
}
interface Stopable {
	boolean shouldStop();
}