import java.io.*;
import java.util.*;
import java.util.logging.*;

public class ReadFile {

	final static Logger logger = Logger.getLogger(ReadFile.class.getName());
	static{
		logger.setUseParentHandlers(false);

		Handler[] previousHandlers = logger.getHandlers();
		for(Handler handler : previousHandlers)
		{
				if(handler.getClass() == ConsoleHandler.class)
					logger.removeHandler(handler);
		}
		logger.setLevel(Level.FINEST);
		ConsoleHandler handler = new ConsoleHandler(); 
		handler.setLevel(Level.FINEST);
		logger.addHandler(handler);
	}
	public HashMap<Integer, ArrayList<String>> readFile(int uid, String fileLocation) {
		File file = new File(fileLocation);
		HashMap<Integer, ArrayList<String>> idHostNameMap = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!(line.length() > 0 && !line.startsWith("#")))
					continue;
				Main.totalNodes = Integer.parseInt(line);
				break;
			}
			int n = Main.totalNodes;
			while ((line = br.readLine()) != null && n > 0) {
				if (!(line.length() > 0 && !line.startsWith("#")))
					continue;
				line = line.trim();
				String[] temp = line.split("\\s+");
				idHostNameMap.put(Integer.parseInt(temp[0]), new ArrayList<String>());
				idHostNameMap.get(Integer.parseInt(temp[0])).add(temp[1]);
				idHostNameMap.get(Integer.parseInt(temp[0])).add(temp[2]);
				n--;
			}

			logger.fine(""+idHostNameMap.toString());

			while ((line = br.readLine()) != null) {
				if (!(line.length() > 0 && !line.startsWith("#")))
					continue;
				String[] edge = (line.trim()).split("\\s+");
				String vertex = edge[0].split("[\\(\\)]")[1];
				int weight = Integer.parseInt(edge[1]);
				int v1 = Integer.parseInt(vertex.split(",")[0]);
				int v2 = Integer.parseInt(vertex.split(",")[1]);
				if (Main.uid == v1 || Main.uid == v2) {
					int oppositeID = Main.uid == v1 ? v2 : v1;
					String oppositeHost = idHostNameMap.get(oppositeID).get(0);
					String oppositePort = idHostNameMap.get(oppositeID).get(1);
					Main.basicEdges.add(new Edge(weight, v1, v2, oppositeHost, Integer.parseInt(oppositePort)));
				}
			}
			logger.fine("" + Main.basicEdges.toString());

		} catch (IOException e) {
			logger.severe("File not found");
		}
		return idHostNameMap;
	}

}
