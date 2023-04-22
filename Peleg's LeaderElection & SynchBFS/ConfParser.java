import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;

class NbrNode {
	String HName;
	int PNumber;

	NbrNode(String HName, int PNumber) {
		this.PNumber = PNumber;
		this.HName = HName;
		
	}
}

class ConfParser {
	final static HashMap<String, Node> nodeList = new HashMap<>();

	public static Node read(String Path, String HName) throws Exception {
		System.out.println(HName);
		HashMap<Integer, NbrNode> map;
		BufferedReader br = new BufferedReader(new FileReader(Path));
		String readLine = "";

		br.readLine();
		int noOfNodes = Integer.parseInt(br.readLine());
		map = new HashMap<Integer, NbrNode>(noOfNodes);

		br.readLine();
		br.readLine();
		int myUID = -1;

		Node node = new Node();
		HashMap<Integer, NbrNode> UIDNgbrs = new HashMap<Integer, NbrNode>();

		try {
			while ((readLine = br.readLine().trim()) != null) {
				if (readLine.equals("")) {
					br.readLine();
					try {
						while ((readLine = br.readLine().trim()) != null) {
							System.out.println(readLine);
							String[] val = readLine.split("\\s+");
							if (myUID == Integer.parseInt(val[0])) {
								for (int i = 1; i < val.length; i++) {
									UIDNgbrs.put(Integer.parseInt(val[i]), map.get(Integer.parseInt(val[i])));
									System.out.println(val[0] + val[i]);
								}
								break;
							}
							
						}
						break;

					} catch (Exception e) {
						break;

					}
				} else {
					System.out.println("I am here");
					String[] val = readLine.split("\\s+");
					for(int i=0;i<val.length;i++)
					System.out.println(i+":"+val[i]);
					String HName1 = val[1];
					int UID = Integer.parseInt(val[0]);
					int Port = Integer.parseInt(val[2]);
					map.put(UID, new NbrNode(HName1, Port));
					if (HName.equals(HName1))
						myUID = UID;
					nodeList.put(HName1, new Node(UID, Port, HName1, UIDNgbrs, 0));
				}

			}
			
			node = nodeList.get(HName);

		} finally {
			br.close();
		}

		return node;
	}
}