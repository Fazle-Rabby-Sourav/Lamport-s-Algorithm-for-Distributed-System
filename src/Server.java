import mutual.exclusion.lamport.ConfigUtils;
import mutual.exclusion.lamport.server.ServerEntity;
import mutual.exclusion.lamport.server.ServerService;

public class Server {

	public static void main(String[] args) {
		int serverId = Integer.parseInt(args[0]);
		String serverConfigFilePath = args[1];
		int capacity = 5;
		if (args.length > 2) {
			capacity = Integer.parseInt(args[2]);
		}
		ServerEntity serverEntity = ConfigUtils.getServerMetaData(serverConfigFilePath, serverId);
		ServerService serverService = new ServerService(serverId, serverEntity.hostname, serverEntity.port, serverEntity.rootDirectory, capacity);
		serverService.startServer();
	}
}
