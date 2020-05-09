package mutual.exclusion.lamport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;


public class Utils {
	public static int CLIENT_SERVER_CONN = 1;
	public static int PEER_TO_PEER_CONN = 2;
	
	public static String READ_CMD = "read";
	public static String WRITE_CMD = "write";
	public static String ENQUIRE_CMD = "enquire";
	public static String TERMINATE_CMD = "terminate";
	public static String REQUEST_CMD = "request";
	public static String RELEASE_CMD = "release";
	public static String REPLY_CMD = "reply";
	
	public static String SUCCESS = "success";
	public static String FAILURE = "failure";
	public static String[] COMMANDS = {Utils.READ_CMD, Utils.WRITE_CMD};

	public static int sleepTime = 6000;
	
	public static void write(BufferedWriter bufferedWriter, String message) throws IOException {
		bufferedWriter.write(message);
		bufferedWriter.newLine();
		bufferedWriter.flush();
	}
	public static String read(BufferedReader bufferedReader) throws IOException {
		return bufferedReader.readLine();
	}
}
