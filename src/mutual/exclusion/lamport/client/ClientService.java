package mutual.exclusion.lamport.client;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mutual.exclusion.lamport.Utils;
import mutual.exclusion.lamport.server.ServerEntity;

public class ClientService {
	public long clock;
	public int replyCount, clockDelta;
	public boolean inCriticalSection;
	private ClientEntity clientEntity;
	private HashMap<String, Queue<Request>> queues;
	public HashMap<Integer, Socket> clientSockets;

	private class Request implements Comparable<Request>{
		public long timestamp;
		public int id;
		public Request(int id, long timestamp) {
			this.id = id;
			this.timestamp = timestamp;
		}
		@Override
		public int compareTo(Request x) {
			if (x.timestamp < this.timestamp) {
				return 1;
			}
			else if (x.timestamp == this.timestamp) {
				if (x.id < this.id)
					return 1;
				else
					return -1;
			}
			else return -1;
		}
		
		@Override
		public boolean equals(Object req) {
			return (this.id == ((Request)req).id);
		}
	}
	
	public ClientService(ClientEntity clientEntity) {
		this.clock = 0;
		this.replyCount = 1;
		this.clockDelta = 1;
		this.inCriticalSection = false;
		this.clientEntity = clientEntity;
		this.queues = new HashMap<String, Queue<Request>>();
		this.clientSockets = new HashMap<Integer, Socket>();
	}

	public void startClient(List<ServerEntity> serverEntityList, List<ClientEntity> clientEntityList) {
		ClientService clientService = this;
		Thread clientHandlerThread = new Thread () {
			@Override
			public synchronized void run() {
				Socket socket;
				ServerSocket serverSocket;
				List<ClientServiceHandler> clients = new ArrayList<ClientServiceHandler>();
				try {
					serverSocket = new ServerSocket(clientService.clientEntity.port);
					int numberOfOtherClients = clientEntityList.size() - 1;
					while(numberOfOtherClients > 0) {
						socket = serverSocket.accept();
						ClientServiceHandler handlerTmp = new ClientServiceHandler(clientService, Utils.PEER_TO_PEER_CONN, socket);
						handlerTmp.start();
						clients.add(handlerTmp);
						numberOfOtherClients--;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Iterator<ClientServiceHandler> it = clients.iterator();
				while(it.hasNext()) {
					ClientServiceHandler clientServiceHandler = it.next();
					try {
						clientServiceHandler.join();
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		clientHandlerThread.start();
		try {
			// Sleep for 60 seconds
			Thread.sleep(Utils.sleepTime);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		// connect to other clients 
		Iterator<ClientEntity> clientIt = clientEntityList.iterator();
		while(clientIt.hasNext()) {
			ClientEntity clientEntity = clientIt.next();
			if (clientEntity.id != this.clientEntity.id) {
				try {
					this.clientSockets.put(clientEntity.id, new Socket(clientEntity.hostname, clientEntity.port));
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Socket[] sockets = new Socket[serverEntityList.size()];
		try {
			Iterator<ServerEntity> it = serverEntityList.iterator();
			int i = 0;
			while (it.hasNext()) {
				ServerEntity sb = it.next();
				sockets[i++] = new Socket(sb.hostname, sb.port);
			}
		}
		catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
		ClientServiceHandler serverHandlerThread = new ClientServiceHandler(this, Utils.CLIENT_SERVER_CONN, sockets);
		serverHandlerThread.start();
		
		try {
			clientHandlerThread.join();
			serverHandlerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return this.clientEntity.id;
	}
	
	public int getPort() {
		return this.clientEntity.port;
	}
	
	public void enqueue(String file, int id, long timestamp) {
		if(!this.queues.containsKey(file)) {
			this.queues.put(file, new PriorityQueue<Request>());
		}
		Request request = new Request(id, timestamp);
		Queue<Request> queue = this.queues.get(file);
		queue.add(request);
	}

	public void dequeue(String file) {
		if(this.queues.containsKey(file)) {
			Queue<Request> queue = this.queues.get(file);
			if (queue != null) {
				queue.poll();
			}
		}
	}

	public boolean isPresentInQueue(String file, int id) {
		boolean isPresent = false;
		if(this.queues.containsKey(file)) {
			Queue<Request> q = this.queues.get(file);
			return q.contains(id);
		}
		return isPresent;
	}

	public Integer getHeadOfQueue(String file) {
		if(!this.queues.containsKey(file)) {
			return null;
		}
		Queue<Request> queue = this.queues.get(file);
		if (queue == null) {
			return null;
		}
		Request nextRequest = queue.peek();
		if(nextRequest == null) {
			return null;
		}
 		return nextRequest.id;
	}
}
