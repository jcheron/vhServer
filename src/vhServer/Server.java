package vhServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	private int port;
	private String address;
	private ServerSocket serverSocket;
	private ServerStatus status;

	public Server(int port, String address) {
		this.port = port;
		this.address = address;
		this.status = ServerStatus.started;
	}

	public static void main(String[] args) throws Exception {
		int port = 9001;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Argument " + args[0] + " is not a valid port number.");
				System.exit(1);
			}
		}
		startServer(port);
	}

	private static void startServer(int port) throws UnknownHostException, IOException, InterruptedException {
		Server s = new Server(port, "localhost");
		s.start();
	}

	private void stopServer() throws IOException {
		serverSocket.close();
	}

	public void start() throws UnknownHostException, IOException, InterruptedException {
		serverSocket = new ServerSocket(port, 100, InetAddress.getByName(address));
		System.out.println("Server started  at:  " + serverSocket);

		while (ServerStatus.started.equals(status)) {
			System.out.println("Waiting for a  connection...");
			final Socket activeSocket = serverSocket.accept();
			System.out.println("Received a  connection from  " + activeSocket);
			Runnable runnable = () -> handleClientRequest(activeSocket);
			Thread t = new Thread(runnable);
			t.start();
			t.join();
			if (!status.equals(ServerStatus.started))
				stopServer();
		}
		if (status.equals(ServerStatus.toRestart)) {
			startServer(port);
		}
	}

	public void handleClientRequest(Socket socket) {
		try {
			BufferedReader socketReader = null;
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String inMsg = null;
			while ((inMsg = socketReader.readLine()) != null) {
				System.out.println("Received from  client: " + inMsg);
				ServerProtocol sp = ServerProtocol.processInput(inMsg);
				sp.execute(this);
				String message = sp.getResponse();
				System.out.println(message);
				out.println(message);
			}
			out.close();
			socket.close();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void toRestart() {
		status = ServerStatus.toRestart;
	}

	public void toStop() {
		status = ServerStatus.toStop;
	}
}
