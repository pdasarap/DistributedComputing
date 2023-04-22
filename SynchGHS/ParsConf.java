import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Scanner;
class NodeInfo{
	String hstName;
	int prtNumber;
	NodeInfo(String hstName, int prtNumber)
	{
		this.hstName= hstName;
		this.prtNumber= prtNumber;
	}
}
public class ParsConf {
	ParsConf(TreeNode curr, String path,String args)
	{
		
		
		
		String myMachine="";		
		try {
			myMachine = InetAddress.getLocalHost().getHostName();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		HashMap<Integer, NodeInfo> temp_hm = new HashMap<Integer, NodeInfo>();		
		try {
				Scanner scanner  = new Scanner(new FileReader(path));
				String readLine;
				while (scanner.hasNext()) {
					readLine = scanner.nextLine().trim();
					if (readLine.isEmpty() || readLine.charAt(0) == '#')
						continue;
				    
					String[] str = readLine.split("\\s+");
					if(str.length==3)
					{
						temp_hm.put(Integer.parseInt(str[0]), new NodeInfo(str[1],Integer.parseInt(str[2])));
						if(args.equals(str[0]))
						{
							
							curr.hstName=str[1];
							curr.UnqID = Integer.parseInt(str[0]);
							curr.mwogEdge= new MinWghtOGE();
							curr.ldrUnqID = curr.UnqID;
							curr.prtNumber=Integer.parseInt(str[2]);
						}
					}
					else if(str.length==1)
					{
						curr.ttlNoOfNodes=Integer.parseInt(str[0]);
					}
					else if(str.length==2)
					{
						str[0]=str[0].replaceAll("\\(", "").replaceAll("\\)","");
						String[] nbrs = str[0].split(",");
						int yy = Integer.parseInt(nbrs[1]);
						int xx =Integer.parseInt(nbrs[0]);
						int n=-1;
						if(xx==curr.UnqID)
						{
							n=yy;
						}
						else if(yy==curr.UnqID)
						{
							n=xx;
						}
						if(n!=-1)
						{
							int wgt = Integer.parseInt(str[1]);
							NodeInfo nbsDetails=temp_hm.get(n);
							NgbrNode nbsNode = new NgbrNode(n,nbsDetails.hstName,nbsDetails.prtNumber,wgt);
							curr.nbs.put(n, nbsNode);
						}
						
					}
							    
				} 
				scanner.close();
		}
			catch (Exception e) {
			e.printStackTrace();
		}
	}
}
