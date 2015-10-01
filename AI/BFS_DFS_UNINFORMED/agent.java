import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.AlreadyConnectedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import javax.print.attribute.standard.Chromaticity;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
class Node {
	 
	  String name;
	  int num;
	  int cost;
	   public Node(String str,int i)
	   {
	      this.name=str;
	      this.num = i;
	      this.cost =0;
	   }
	}

class ALPHABETICAL_ORDER implements Comparator<Node>
{
	@Override
	public int compare(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		int res = arg0.name.compareToIgnoreCase(arg1.name);
		return res;
	}
}

class costComparator implements Comparator<Node>
{
	@Override
	public int compare(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		int res =0;
		if (arg0.cost < arg1.cost)
            res =  -1;
        if (arg0.cost > arg1.cost)
            res = 1;
		if (arg0.cost == arg1.cost){
			res = arg0.name.compareToIgnoreCase(arg1.name);
				}
		return res;
	}
}

class DFSComparator implements Comparator<Node>
{
	@Override
	public int compare(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		int res =0;
		if (arg0.cost < arg1.cost)
            res =  1;
        if (arg0.cost > arg1.cost)
            res = -1;
		if (arg0.cost == arg1.cost){
			res = arg0.name.compareToIgnoreCase(arg1.name);
				}
		return res;
	}
}


class Sorting{
	BufferedWriter writer = null;
	public void BFS() {
		Comparator<Node> comparator = new ALPHABETICAL_ORDER();
		Comparator<Node> cost_comparator = new costComparator();
		PriorityQueue<Node> queue = new PriorityQueue<Node>(ParsingData.noOfMembers,cost_comparator);
		//Queue<Node> queue = new LinkedList<Node>();
		Queue<Node> path = new LinkedList<Node>();
		int pathcost_tot =0;
		int[] visited = new int[ParsingData.noOfMembers];
		int[] enqueued = new int[ParsingData.noOfMembers];
		int[] parent = new int[ParsingData.noOfMembers];
		//int[] pathcost =new int[ParsingData.noOfMembers];
		int i,elem;
		int end =0;
		Node element;
		Node sourceNode;
		
        sourceNode =ParsingData.node[ParsingData.source_num];
		//visited[ParsingData.source_num] = 1;
        parent[ParsingData.source_num] = ParsingData.source_num;
        queue.add(sourceNode);
        enqueued[ParsingData.source_num] =1;
        //pathcost[ParsingData.source_num] = 0;
        //int lastvisitednode;
        while (!queue.isEmpty())
        {
        	if(end == 1){
        		break;
        	}
            element = queue.remove();
            Node element2 = queue.peek();
            if(element2 != null)
            System.out.println(element.name+"--"+element.cost+"--"+element2.name+"--"+element2.cost);
            if(element.name.equals(ParsingData.destination)){
            	
            	end =1;
            	
            }
            path.add(element);
            i = element.num;
            //pathcost_tot =pathcost_tot + element.cost;
            visited[i] = 1;
            
            
            //elem =i;
            //System.out.print(i + "-");
           // lastvisitednode = queue.size();
            PriorityQueue<Node> subqueue = new PriorityQueue<Node>(ParsingData.noOfMembers,comparator);
            for(int j =0; j<ParsingData.noOfMembers; j++ )
            {
                if (ParsingData.adj[i][j] >0)
                {
                	Node n;
                	n = ParsingData.node[j];
                    subqueue.add(n);
                    //parent[j] = i;
                    //n.cost = element.cost+1;
                }
                
            }
            while(!subqueue.isEmpty()){
            	Node n1 = subqueue.remove();
            	if(enqueued[n1.num]==0){
            		
            		n1.cost =element.cost+1;
            		queue.add(n1);
            		parent[n1.num] = i;
            		enqueued[n1.num]=1;
            	}
            		
            	else if(enqueued[n1.num]==1 && visited[n1.num]==0){
            		if(n1.cost > element.cost+1){
            			n1.cost =element.cost+1;
            			parent[n1.num]=i;
            			enqueued[n1.num]=1;
            		}
            	}
            	
            	else if(enqueued[n1.num]==1 && visited[n1.num]==1){
            		if(n1.cost > element.cost +1){
            			
            			n1.cost =element.cost+1;
            			queue.add(n1);
            			parent[n1.num]=i;
            			enqueued[n1.num]=1;
            		}
            	}
            	
            }
            
        }
        try {
			writer = new BufferedWriter(new FileWriter("D:/Masters/AI/HW1_AI/HW1/example1/output.txt"));
		
			if(end ==0){
				writer.write("Nopathavailable");
				writer.close();
				System.exit(1);
			}
        while(!path.isEmpty()){
        	Node p = path.remove();
        	writer.write(p.name);
        	if((p.name).equals(ParsingData.destination)){
        		break;
        	}
        	writer.write("-");
        }
        //System.out.println();
        writer.write("\r\n");
        Stack st = new Stack();
        st.push(ParsingData.destination_num);
        int dest_parent = parent[ParsingData.destination_num];
        while(dest_parent != ParsingData.source_num){
        	st.push(dest_parent);
        	dest_parent = parent[dest_parent];
        }
        //st.push(ParsingData.source_num);
        //System.out.print(ParsingData.source+ "-");
        writer.write(ParsingData.source+ "-");
        int s = ParsingData.source_num;
        while(!st.isEmpty()){
        	int p =(int) st.pop();
        	
        	//System.out.print(ParsingData.node[p].name+ "-");
        	writer.write(ParsingData.node[p].name);
        	pathcost_tot = pathcost_tot+ParsingData.adj[s][p];
        	System.out.print(pathcost_tot+ "-");
        	if((ParsingData.node[p].name).equals(ParsingData.destination)){
        		break;
        	}
        	writer.write("-");
        	s=p;
        }
        writer.write("\r\n");
        writer.write(Integer.toString(pathcost_tot));
        writer.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
	}
	
	
	public void DFS(){
		Comparator<Node> comparator = new ALPHABETICAL_ORDER();
		Comparator<Node> DFS_comparator = new DFSComparator();
		PriorityQueue<Node> queue = new PriorityQueue<Node>(ParsingData.noOfMembers,DFS_comparator);
		//Queue<Node> queue = new LinkedList<Node>();
		Queue<Node> path = new LinkedList<Node>();
		int pathcost_tot =0;
		int[] visited = new int[ParsingData.noOfMembers];
		int[] enqueued = new int[ParsingData.noOfMembers];
		int[] parent = new int[ParsingData.noOfMembers];
		//int[] pathcost =new int[ParsingData.noOfMembers];
		int i,elem;
		int end =0;
		Node element;
		Node sourceNode;
		
        sourceNode =ParsingData.node[ParsingData.source_num];
		//visited[ParsingData.source_num] = 1;
        parent[ParsingData.source_num] = ParsingData.source_num;
        queue.add(sourceNode);
        enqueued[ParsingData.source_num] =1;
        //pathcost[ParsingData.source_num] = 0;
        //int lastvisitednode;
        while (!queue.isEmpty())
        {
        	if(end == 1){
        		break;
        	}
            element = queue.remove();
            Node element2 = queue.peek();
            if(element2 != null)
            System.out.println(element.name+"--"+element.cost+"--"+element2.name+"--"+element2.cost);
            
            if(element.name.equals(ParsingData.destination)){
            	
            	end =1;
            	
            }
            path.add(element);
            i = element.num;
            //pathcost_tot =pathcost_tot + element.cost;
            visited[i] = 1;
            
            
            //elem =i;
            //System.out.print(i + "-");
           // lastvisitednode = queue.size();
            PriorityQueue<Node> subqueue = new PriorityQueue<Node>(ParsingData.noOfMembers,comparator);
            for(int j =0; j<ParsingData.noOfMembers; j++ )
            {
                if (ParsingData.adj[i][j] >0)
                {
                	Node n;
                	n = ParsingData.node[j];
                    subqueue.add(n);
                    //parent[j] = i;
                    //n.cost = element.cost+1;
                }
                
            }
            while(!subqueue.isEmpty()){
            	Node n1 = subqueue.remove();
            	if(enqueued[n1.num]==0){
            		
            		n1.cost =element.cost+1;
            		queue.add(n1);
            		parent[n1.num] = i;
            		enqueued[n1.num]=1;
            	}
            		
            	else if(enqueued[n1.num]==1 && visited[n1.num]==0){
            		if(n1.cost > element.cost+1){
            			n1.cost =element.cost+1;
            			parent[n1.num]=i;
            			enqueued[n1.num]=1;
            		}
            	}
            	
            	else if(enqueued[n1.num]==1 && visited[n1.num]==1){
            		if(n1.cost > element.cost +1){
            			n1.cost =element.cost+1;
            			queue.add(n1);
            			
            			parent[n1.num]=i;
            			enqueued[n1.num]=1;
            		}
            	}
            	
            }
            
        }
        
        try {
			writer = new BufferedWriter(new FileWriter("D:/Masters/AI/HW1_AI/HW1/example1/output.txt"));
		
			if(end ==0){
				writer.write("Nopathavailable");
				writer.close();
				System.exit(1);
			}
        while(!path.isEmpty()){
        	Node p = path.remove();
        	writer.write(p.name);
        	if((p.name).equals(ParsingData.destination)){
        		break;
        	}
        	writer.write("-");
        }
        writer.write("\r\n");
        //System.out.println();
        Stack st = new Stack();
        st.push(ParsingData.destination_num);
        int dest_parent = parent[ParsingData.destination_num];
        while(dest_parent != ParsingData.source_num){
        	st.push(dest_parent);
        	dest_parent = parent[dest_parent];
        }
        //st.push(ParsingData.source_num);
        //System.out.print(ParsingData.source+ "-");
        writer.write(ParsingData.source+ "-");
        int s = ParsingData.source_num;
        while(!st.isEmpty()){
        	int p =(int) st.pop();
        	
        	writer.write(ParsingData.node[p].name);
        	pathcost_tot = pathcost_tot+ParsingData.adj[s][p];
        	if((ParsingData.node[p].name).equals(ParsingData.destination)){
        		break;
        	}
        	writer.write("-");
        	s=p;
        }
        writer.write("\r\n");
        writer.write(Integer.toString(pathcost_tot));
        writer.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	
	public void uniformCostSearch(){
		Comparator<Node> comparator = new ALPHABETICAL_ORDER();
		Comparator<Node> cost_comparator = new costComparator();
		PriorityQueue<Node> queue = new PriorityQueue<Node>(ParsingData.noOfMembers,cost_comparator);
		//Queue<Node> queue = new LinkedList<Node>();
		Queue<Node> path = new LinkedList<Node>();
		int pathcost_tot =0;
		int[] visited = new int[ParsingData.noOfMembers];
		int[] enqueued = new int[ParsingData.noOfMembers];
		int[] parent = new int[ParsingData.noOfMembers];
		//int[] pathcost =new int[ParsingData.noOfMembers];
		int i,elem;
		int end =0;
		Node element;
		Node sourceNode;
		
        sourceNode =ParsingData.node[ParsingData.source_num];
		//visited[ParsingData.source_num] = 1;
        parent[ParsingData.source_num] = ParsingData.source_num;
        queue.add(sourceNode);
        enqueued[ParsingData.source_num] =1;
        //pathcost[ParsingData.source_num] = 0;
        //int lastvisitednode;
        while (!queue.isEmpty())
        {
        	if(end == 1){
        		break;
        	}
            element = queue.remove();
            Node element2 = queue.peek();
            if(element2 != null)
            System.out.println(element.name+"--"+element.cost+"--"+element2.name+"--"+element2.cost);
            if(element.name.equals(ParsingData.destination)){
            	
            	end =1;
            	
            }
            path.add(element);
            i = element.num;
            //pathcost_tot =pathcost_tot + element.cost;
            visited[i] = 1;
            
            
            //elem =i;
            //System.out.print(i + "-");
           // lastvisitednode = queue.size();
            
         /*   PriorityQueue<Node> subqueue = new PriorityQueue<Node>(ParsingData.noOfMembers,comparator);
            for(int j =0; j<ParsingData.noOfMembers; j++ )
            {
                if (ParsingData.adj[i][j] >0)
                {
                	Node n;
                	n = ParsingData.node[j];
                    subqueue.add(n);
                    //parent[j] = i;
                    //n.cost = element.cost+1;
                }
                
            }
            while(!subqueue.isEmpty()){
            	Node n1 = subqueue.remove();
            	if(enqueued[n1.num]==0){
            		
            		n1.cost =element.cost+ParsingData.adj[i][n1.num];
            		queue.add(n1);
            		parent[n1.num] = i;
            		enqueued[n1.num]=1;
            	}
            		
            	else if(enqueued[n1.num]==1 && visited[n1.num]==0){
            		if(n1.cost > element.cost+ParsingData.adj[i][n1.num]){
            			n1.cost =element.cost+ParsingData.adj[i][n1.num];
            			parent[n1.num]=i;
            			enqueued[n1.num]=1;
            		}
            	}
            	
            	else if(enqueued[n1.num]==1 && visited[n1.num]==1){
            		if(n1.cost > element.cost+ParsingData.adj[i][n1.num]){
            			
            			
            			n1.cost =element.cost+ParsingData.adj[i][n1.num];
            			queue.add(n1);
            			parent[n1.num]=i;
            			enqueued[n1.num]=1;
            		}
            	}
            	
            }*/
            for(int j =0; j<ParsingData.noOfMembers; j++ )
            {
                if (ParsingData.adj[i][j] >0)
                {
                	Node n1;
                	n1 = ParsingData.node[j];
                	if(enqueued[n1.num]==0){
                		
                		n1.cost =element.cost+ParsingData.adj[i][j];
                		queue.add(n1);
                		parent[n1.num] = i;
                		enqueued[n1.num]=1;
                	}
                		
                	else if(enqueued[n1.num]==1 && visited[n1.num]==0){
                		if(n1.cost > element.cost+ParsingData.adj[i][j]){
                			queue.remove(n1);
                			n1.cost =element.cost+ParsingData.adj[i][j];
                			queue.add(n1);
                			parent[n1.num]=i;
                			enqueued[n1.num]=1;
                		}
                	}
                	
                	else if(enqueued[n1.num]==1 && visited[n1.num]==1){
                		if(n1.cost > element.cost+ParsingData.adj[i][j]){
                			
                			path.remove(n1);
                			n1.cost =element.cost+ParsingData.adj[i][j];
                			queue.add(n1);
                			parent[n1.num]=i;
                			enqueued[n1.num]=1;
                		}
                	}
                	
                }
                
            }
            
        }
        try {
			writer = new BufferedWriter(new FileWriter("D:/Masters/AI/HW1_AI/HW1/example1/output.txt"));
		
			if(end ==0){
				writer.write("Nopathavailable");
				writer.close();
				System.exit(1);
			}
        while(!path.isEmpty()){
        	Node p = path.remove();
        	writer.write(p.name);
        	if((p.name).equals(ParsingData.destination)){
        		break;
        	}
        	writer.write("-");
        }
        //System.out.println();
        writer.write("\r\n");
        Stack st = new Stack();
        st.push(ParsingData.destination_num);
        int dest_parent = parent[ParsingData.destination_num];
        while(dest_parent != ParsingData.source_num){
        	st.push(dest_parent);
        	dest_parent = parent[dest_parent];
        }
        //st.push(ParsingData.source_num);
        //System.out.print(ParsingData.source+ "-");
        writer.write(ParsingData.source+ "-");
        int s = ParsingData.source_num;
        while(!st.isEmpty()){
        	int p =(int) st.pop();
        	
        	//System.out.print(ParsingData.node[p].name+ "-");
        	writer.write(ParsingData.node[p].name);
        	pathcost_tot = pathcost_tot+ParsingData.adj[s][p];
        	if((ParsingData.node[p].name).equals(ParsingData.destination)){
        		break;
        	}
        	writer.write("-");
        	s=p;
        }
        writer.write("\r\n");
        writer.write(Integer.toString(pathcost_tot));
        writer.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}
	
	
}

class ParsingData {

	static String searchMethod;
	static String source;
	static int source_num;
	static int destination_num;
	static String destination;
	static int noOfMembers;
	int counter =1;
	int checkMembers=0;
	int checkLinesofData =0;
	static Node node[];
	static int adj[][];
	public void parse(String line){	
		//System.out.println(counter);
		switch(counter){
		case 1:
		{
			searchMethod = line;
			counter++;
			break;
		}	
		case 2:
		{
			source = line;
			counter++;
			break;			
		}	
		case 3:
		{
			destination = line;
			if(source.equals(destination)){
				System.out.println("Source and destination cannot be same");
				
			}
			counter++;
			break;
		}
		
		case 4:
		{
			noOfMembers = Integer.parseInt(line);
			//System.out.println("noofmembers"+noOfMembers);
			counter++;
			node = new Node[noOfMembers];
			adj = new int[noOfMembers][noOfMembers];
			break;
		}	
		case 5:
		{
			node[checkMembers]= new Node(line,checkMembers);
			if(line.equalsIgnoreCase(source)){
				source_num = checkMembers;
			}else if (line.equalsIgnoreCase(destination)) {
				destination_num = checkMembers;
			}
			checkMembers++;
			if(checkMembers == noOfMembers){
				counter++;
			}
			break;
		}
		case 6:
		{
			String[] result = line.split("\\s");
			System.out.println();
			for(int i=0;i<noOfMembers;i++){
				adj[checkLinesofData][i]= Integer.parseInt(result[i]);
				//System.out.println(adj[checkLinesofData][i]);
			}
			checkLinesofData++;
			break;
		}
			
		}
	}
}

class ReadInputFile {

	public void read(String filepath)throws IOException{
		
		ParsingData obj = new ParsingData(); 
	    FileReader in = new FileReader(filepath);
	    BufferedReader br = new BufferedReader(in);
	    String line = br.readLine();
	    while (line!=null) {
	    	//System.out.println(line);
	        obj.parse(line);
	        line = br.readLine();
	    }
	    in.close();
	}	
}

public class agent{
	
	public static void main(String...args){
		
		ReadInputFile objRIF = new ReadInputFile();
		Sorting objSort = new Sorting();
		try {
			objRIF.read("D:/Masters/AI/HW1_AI/HW1/example1/input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		if((ParsingData.source).equals(ParsingData.destination)){
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("D:/Masters/AI/HW1_AI/HW1/example1/output.txt"));
				writer.write(ParsingData.source);
				writer.write("\r\n");
				writer.write(ParsingData.destination);
				writer.write("\r\n");
				System.out.print(ParsingData.adj[ParsingData.source_num][ParsingData.destination_num]);
				int n=ParsingData.adj[ParsingData.source_num][ParsingData.destination_num];
				writer.write(n);
				
				writer.close();
				System.exit(1);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		switch(ParsingData.searchMethod)
		{
		case "1":
		{
			objSort.BFS();
			break;
		}
		case "2":
		{
			objSort.DFS();
			break;
		}
		
		case "3":
		{
			objSort.uniformCostSearch();
			break;
		}
		}
		
	}
}

