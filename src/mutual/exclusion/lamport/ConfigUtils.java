package mutual.exclusion.lamport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mutual.exclusion.lamport.client.ClientEntity;
import mutual.exclusion.lamport.server.ServerEntity;

public class ConfigUtils {
	
	public static List<ServerEntity> getAllServers(String serverConfigFile) {
		List<ServerEntity> serverEntityList = new ArrayList<ServerEntity>();
		File f = new File(serverConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 4) { 
						serverEntityList.add(new ServerEntity(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), tokens[3]));
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return serverEntityList;
	}
	
	public static ServerEntity getServerMetaData(String serverConfigFile, int serverId) {
		ServerEntity serverEntity = null;
		File f = new File(serverConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 4 && Integer.parseInt(tokens[0]) == serverId) { 
						serverEntity = new ServerEntity(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]), tokens[3]);
						break;
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return serverEntity;
	}
	
	public static ClientEntity getClientMetaData(String clientConfigFile, int clientId) {
		ClientEntity clientEntity = null;
		File f = new File(clientConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 3 && Integer.parseInt(tokens[0]) == clientId) { 
						clientEntity = new ClientEntity(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2]));
						break;
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return clientEntity;
	}
	
	public static List<ClientEntity> getAllClients(String clientConfigFile) {
		List<ClientEntity> clientEntityList = new ArrayList<ClientEntity>();
		File f = new File(clientConfigFile);
		if (f.isFile()) {
			BufferedReader reader = null;
			FileReader freader = null;
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(",");
					if (tokens.length == 3) { 
						clientEntityList.add(new ClientEntity(Integer.parseInt(tokens[0]), tokens[1], Integer.parseInt(tokens[2])));
					}
				}
			}
			catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
			finally {
				try {
					if (freader != null)
						freader.close();
					if(reader != null)
						reader.close();
				}
				catch(IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}
		return clientEntityList;
	}
}
