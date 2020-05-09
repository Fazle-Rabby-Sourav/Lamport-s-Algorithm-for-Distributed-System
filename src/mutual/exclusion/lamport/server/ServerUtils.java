package mutual.exclusion.lamport.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mutual.exclusion.lamport.Utils;

public class ServerUtils {
	private static ServerUtils serverUtil;
	
	private ServerUtils() {}
	
	synchronized public static ServerUtils getInstance() {
		if (serverUtil == null) {
			serverUtil = new ServerUtils();
		}
		return serverUtil;
	}
	
	public List<String> getAllFiles(String rootDirectory) {
		final File root = new File(rootDirectory);
		List<String> allFiles = new ArrayList<String>();
		if (root.isDirectory()) {
			for (final File file: root.listFiles()) {
				allFiles.add(file.getName());
			}
		}
		return allFiles;
	}
	
	public String readFile(String file) {
		final File f = new File(file);
		String lastLine = "";
		if (!f.isDirectory()) {
			BufferedReader reader = null;
			FileReader freader = null; 
			try {
				freader = new FileReader(f);
				reader = new BufferedReader(freader);
				String line;
				while((line = reader.readLine()) != null) {
					lastLine = line;
				}
			}
			catch(IOException io_ex) {
				io_ex.printStackTrace();
			}
			finally {
				try {
					if (reader != null) {
						reader.close();
					}
					if (freader != null) {
						freader.close();
					}
				}
				catch(IOException io_ex) {
					io_ex.printStackTrace();
				}
			}
		}
		return lastLine;
	}
	
	public boolean appendToFile(String file, String content) {
		final File f = new File(file);
		boolean fileWritten = true;
		if (!f.isDirectory()) {
			BufferedWriter writer = null;
			FileWriter fwriter = null;
			try {
				fwriter = new FileWriter(f, true);
				writer = new BufferedWriter(fwriter);
				Utils.write(writer, content);
			}
			catch(IOException io_ex) {
				fileWritten = false;
				io_ex.printStackTrace();
			}
			finally {
				try {
					if (writer != null) {
						writer.close();
					}
					if(fwriter != null) {
						fwriter.close();
					}
				}
				catch(IOException io_ex) {
					io_ex.printStackTrace();
				}
			}
		}
		return fileWritten;
	} 
}
