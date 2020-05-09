package mutual.exclusion.lamport.client;

public class ClientEntity {
	public int id, port;
	public String hostname;
	
	public ClientEntity(int id, String hostname, int port) {
		this.id = id;
		this.hostname = hostname;
		this.port = port;
	}
}