package mutual.exclusion.lamport.server;
import mutual.exclusion.lamport.Utils;

import java.util.List;
import java.net.Socket;
import java.util.Iterator;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class ServerServiceHandler extends Thread {

	ServerService server;
	private Socket socket;
	
	public ServerServiceHandler(ServerService server, Socket sock) {
		this.server = server;
		this.socket = sock;
	}

	private void closeResources(Socket socket, BufferedReader socketBufferedReader, BufferedWriter socketBufferedWriter, InputStreamReader socketInputStreamReader, OutputStreamWriter socketOutputStreamWriter) throws IOException {
		if (socketBufferedReader != null)
			socketBufferedReader.close();
		if (socketInputStreamReader != null)
			socketInputStreamReader.close();
		if (socketOutputStreamWriter != null)
			socketOutputStreamWriter.close();
		if (socketBufferedWriter != null)
			socketBufferedWriter.close();
		if (socket != null)
			socket.close();
	}
	@Override
	public void run() {
		BufferedReader socketBufferedReader = null;
		InputStreamReader socketInputStreamReader = null;
		BufferedWriter socketBufferedWriter = null;
		OutputStreamWriter socketOutputStreamWriter = null;
		try {
			socketInputStreamReader = new InputStreamReader(this.socket.getInputStream());
			socketBufferedReader = new BufferedReader(socketInputStreamReader);
			socketOutputStreamWriter = new OutputStreamWriter(this.socket.getOutputStream());
			socketBufferedWriter = new BufferedWriter(socketOutputStreamWriter);
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}
		while(true) {
			try {
				String request = socketBufferedReader.readLine();
				System.out.println("Request received: "+request);
				if ((request == null) || (request.equalsIgnoreCase(Utils.TERMINATE_CMD))) {
					this.closeResources(socket, socketBufferedReader, socketBufferedWriter, socketInputStreamReader, socketOutputStreamWriter);
					return;
				}
				else {
					String[] tokens = request.split(" ");
					if (tokens[0].equalsIgnoreCase(Utils.ENQUIRE_CMD)) {
						List<String> files = ServerUtils.getInstance().getAllFiles(this.server.getRootDirectory());
						Iterator<String> filesIterator = files.iterator();
						StringBuilder allFiles = new StringBuilder();
						while(filesIterator.hasNext()) {
							allFiles.append(filesIterator.next());
							if(filesIterator.hasNext()) {
								allFiles.append(";");
							}
						}
						Utils.write(socketBufferedWriter, allFiles.toString());
					}
					// Read
					else if (tokens[0].equalsIgnoreCase(Utils.READ_CMD)) {
						String fileName = null;
						try {
							fileName = tokens[1];
							fileName = Paths.get(this.server.getRootDirectory(),fileName).toString();
						}
						catch(ArrayIndexOutOfBoundsException outOfBoundsExInstance) {
							outOfBoundsExInstance.printStackTrace();
						}
						if(fileName != null) {
							Utils.write(socketBufferedWriter, ServerUtils.getInstance().readFile(fileName));
						}
						else {
							Utils.write(socketBufferedWriter, Utils.FAILURE);
						}
					}
					// Write
					else if (tokens[0].equalsIgnoreCase(Utils.WRITE_CMD)) {
						String fileName = null;
						String content = null;
						try {
							fileName = tokens[1];
							fileName = Paths.get(this.server.getRootDirectory(),fileName).toString();
							content = tokens[2];
						}
						catch(ArrayIndexOutOfBoundsException outOfBoundsEx) {
							outOfBoundsEx.printStackTrace();
						}
						if(ServerUtils.getInstance().appendToFile(fileName, content)) {
							Utils.write(socketBufferedWriter, Utils.SUCCESS);
						}
						else {
							Utils.write(socketBufferedWriter, Utils.FAILURE);
						}
					}
				}
			}
			catch(IOException ex) {
				ex.printStackTrace();
				try {
					this.closeResources(socket, socketBufferedReader, socketBufferedWriter, socketInputStreamReader, socketOutputStreamWriter);
				}
				catch(IOException io_ex) {
					io_ex.printStackTrace();
				}
				return;
			}
		}
	}
}
