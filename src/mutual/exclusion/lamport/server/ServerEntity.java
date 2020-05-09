package mutual.exclusion.lamport.server;

public class ServerEntity {
	public int id, port;
	public String hostname, rootDirectory;
	
	public ServerEntity() {}
	public ServerEntity(int id, String hostname, int port, String rootDir ) {
		this.id = id;
		this.hostname = hostname;
		this.port = port;
		this.rootDirectory = rootDir;
	} 
}
