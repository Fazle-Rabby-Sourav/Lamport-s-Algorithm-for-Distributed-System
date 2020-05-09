import java.util.List;

import mutual.exclusion.lamport.ConfigUtils;
import mutual.exclusion.lamport.client.ClientEntity;
import mutual.exclusion.lamport.client.ClientService;
import mutual.exclusion.lamport.server.ServerEntity;

public class Client {

	public static void main(String[] args) {
		int clientId = Integer.parseInt(args[0]);
		String clientConfigFilePath = args[1];
		String serverConfigFilePath = args[2];
		ClientEntity clientEntity = ConfigUtils.getClientMetaData(clientConfigFilePath, clientId);
		ClientService clientService = new ClientService(clientEntity);
		List<ClientEntity> clientEntityList = ConfigUtils.getAllClients(clientConfigFilePath);
		List<ServerEntity> serverEntityList = ConfigUtils.getAllServers(serverConfigFilePath);
		clientService.startClient(serverEntityList, clientEntityList);
	}
}
