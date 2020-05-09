package mutual.exclusion.lamport.client;
import mutual.exclusion.lamport.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Random;

public class ClientServiceHandler extends Thread {
	private Socket[] serverSockets;
	private Socket clientSocket;
	private ClientService clientService;
	private int connectionType;
	
	public ClientServiceHandler(ClientService clientService, int type, Socket socket) {
		this.clientService = clientService;
		this.connectionType = type;
		this.clientSocket = socket;
	}
	
	public ClientServiceHandler(ClientService clientService, int type, Socket[] sockets) {
		this.clientService = clientService;
		this.connectionType = type;
		this.serverSockets = sockets;
	}
	
	private void clientServerProcess() {
		BufferedReader[] socketBufferedReader = new BufferedReader[this.serverSockets.length];
		InputStreamReader[] socketInputStreamReader = new InputStreamReader[this.serverSockets.length];
		BufferedWriter[] socketBufferedWriter = new BufferedWriter[this.serverSockets.length];
		OutputStreamWriter[] socketOutputStreamWriter = new OutputStreamWriter[this.serverSockets.length];
		try {
			for (int i = 0 ; i < this.serverSockets.length ;i++) {
				socketInputStreamReader[i] = new InputStreamReader(this.serverSockets[i].getInputStream());
				socketBufferedReader[i] = new BufferedReader(socketInputStreamReader[i]);
				socketOutputStreamWriter[i] = new OutputStreamWriter(this.serverSockets[i].getOutputStream());
				socketBufferedWriter[i] = new BufferedWriter(socketOutputStreamWriter[i]);
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return;
		}
		Random random = new Random();
		String[] files;
		try {
			Utils.write(socketBufferedWriter[0], Utils.ENQUIRE_CMD);
			String file = Utils.read(socketBufferedReader[0]);
			System.out.println("file : "+ file);
			files = file.split(";");
		}
		catch(IOException ioEx) {
			ioEx.printStackTrace();
			return;
		}
		
		try {
			Thread.sleep(random.nextInt(5)*1000);
			while(true) {
				int server_idx = random.nextInt(this.serverSockets.length);
				int cmd_index = random.nextInt(Utils.COMMANDS.length);
				int file_index = random.nextInt(files.length);
				while(this.clientService.replyCount != 1 || this.clientService.isPresentInQueue(files[file_index], this.clientService.getId())) {
					Thread.sleep(500);
				}
				{
					this.clientService.clock += this.clientService.clockDelta;
					this.broadcastToClients(Utils.REQUEST_CMD, files[file_index]);
					this.clientService.enqueue(files[file_index], this.clientService.getId(), this.clientService.clock);
					while(this.clientService.replyCount <= this.clientService.clientSockets.keySet().size() && !this.checkIfIamNext(files[file_index])) {
						System.out.println("Waiting for "+files[file_index]+" to be released...");
						Thread.sleep(2000);
					}
				}
				if (Utils.COMMANDS[cmd_index].equals(Utils.READ_CMD)) {
//					System.out.println("Read File Operation on the "+files[file_index]);
					this.clientService.clock += this.clientService.clockDelta;
					Utils.write(socketBufferedWriter[server_idx], Utils.READ_CMD+" "+files[file_index]+" "+this.clientService.getId());
					String message = Utils.read(socketBufferedReader[server_idx]);
					System.out.println("Read File Operation on the "+files[file_index]+ ", Content : (" + message+")");
				}
				else if (Utils.COMMANDS[cmd_index].equals(Utils.WRITE_CMD)) {
					this.clientService.clock += this.clientService.clockDelta;
					System.out.println("Write File Operation on the "+files[file_index]+", Content : ("+this.clientService.getId()+":"+this.clientService.clock + ")");
					for (BufferedWriter bf: socketBufferedWriter) {
						Utils.write(bf, Utils.WRITE_CMD+" "+files[file_index]+" "+this.clientService.getId()+":"+this.clientService.clock);
					}
					for (BufferedReader br: socketBufferedReader) {
						Utils.read(br);
					}
				}
				Thread.sleep(5000);
				this.clientService.clock += this.clientService.clockDelta;
				this.broadcastToClients(Utils.RELEASE_CMD, files[file_index]);
				this.clientService.replyCount = 1;
				Integer processIdAtHead = this.clientService.getHeadOfQueue(files[file_index]);
				if(processIdAtHead != null && processIdAtHead == this.clientService.getId()) {
					this.clientService.dequeue(files[file_index]);
				}
				System.out.println("Released the "+files[file_index]);
			}
		}
		catch(IOException ioEx) {
			ioEx.printStackTrace();
			return;
		}
		catch(InterruptedException interruptedEx) {
			interruptedEx.printStackTrace();
		}
	}
	private void broadcastToClients(String command, String file) throws IOException {
		Iterator<Integer> clients = this.clientService.clientSockets.keySet().iterator();
		while(clients.hasNext()) {
			Integer cTemp = clients.next();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.clientService.clientSockets.get(cTemp).getOutputStream());
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
			Utils.write(bufferedWriter, command+" "+file+" "+this.clientService.getId()+" "+this.clientService.clock);
		}
	}
	private boolean checkIfIamNext(String file) {
		if (this.clientService.getHeadOfQueue(file) == null || this.clientService.getHeadOfQueue(file) == this.clientService.getId()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void clientClientProcess() {
		InputStreamReader clientSockISR = null;
		BufferedReader clientSockBR = null;
		OutputStreamWriter clientSockOSW = null;
		BufferedWriter	clientSockBW = null;
		try {
			clientSockISR = new InputStreamReader(this.clientSocket.getInputStream());
			clientSockBR  = new BufferedReader(clientSockISR);
			clientSockOSW = new OutputStreamWriter(this.clientSocket.getOutputStream());
			clientSockBW = new BufferedWriter(clientSockOSW);
			
			while(true) {
				String line = clientSockBR.readLine();
				if (line != null) {
					String[] tokens = line.split(" ");
					
					if (Utils.REQUEST_CMD.equalsIgnoreCase(tokens[0])) {
						String file = tokens[1];
						String process = tokens[2];
						String timestamp = tokens[3];
						this.clientService.clock = Math.max(Long.parseLong(timestamp), this.clientService.clock) + this.clientService.clockDelta;
						this.clientService.enqueue(file, Integer.parseInt(process), Long.parseLong(timestamp));
						this.clientService.clock += this.clientService.clockDelta;
						Utils.write(clientSockBW, Utils.REPLY_CMD+" "+this.clientService.clock);
					}
					else if (Utils.RELEASE_CMD.equalsIgnoreCase(tokens[0])) {
						String file = tokens[1];
						String process = tokens[2];
						String timestamp = tokens[3];
						this.clientService.clock = Math.max(Long.parseLong(timestamp), this.clientService.clock) + this.clientService.clockDelta;
						Integer processIdAtHead = this.clientService.getHeadOfQueue(file);
						if(processIdAtHead != null && processIdAtHead == Integer.parseInt(process) ) {
							this.clientService.dequeue(file);
						}
					}
					else if (Utils.REPLY_CMD.equalsIgnoreCase(tokens[0])) {
						String timestamp = tokens[1];
						this.clientService.clock = Math.max(Long.parseLong(timestamp), this.clientService.clock) + this.clientService.clockDelta;
						this.clientService.replyCount += 1;
					}
				}
			}
		} catch (IOException e) {
			try {
				if(clientSockISR != null) {
					clientSockISR.close();
				}
				if (clientSockBR != null) {
					clientSockBR.close();
				}
				e.printStackTrace();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void run() {
		if (this.connectionType == Utils.CLIENT_SERVER_CONN) {
			this.clientServerProcess();
		}
		else if (this.connectionType == Utils.PEER_TO_PEER_CONN) {
			this.clientClientProcess();
		}
	}
}
