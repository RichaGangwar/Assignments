import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;


class State{
	char boardpos[][]=new char[8][8];
	int evalfunc=0;
	int depth =0;
	int value =0;
	String name;
	public State(int a,int b){
		char c=(char)(b+97);
		a++;
		String d = String.valueOf(a);
		name = c+d;
	}
	public State() {
		// TODO Auto-generated constructor stub
		name="root";
		depth =0;
		value = Integer.MIN_VALUE;
	}
}

class moves{
	private char oppPlayer;
	private char player;
	private int depth;
	private int value;
	static LinkedList<State> listroot = new LinkedList<State>();
	
	public LinkedList<State> legalMoves(State obj,char Player){
		LinkedList<State> list = new LinkedList<State>();
		depth = obj.depth+1;
		if(obj.depth == ParsingData.depth){
			calulateEval(obj);
			obj.value=obj.evalfunc;
			return list;
		}
		char state[][]=obj.boardpos;
		player =Player;
		if(player == ParsingData.player){
			value = Integer.MAX_VALUE;
			oppPlayer = ParsingData.oppPlayer;
		}
		else{
			oppPlayer = ParsingData.player;
			value = Integer.MIN_VALUE;
		}
		
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
			{
				if(state[i][j] =='*'){
					State newState = legal_move_do(state, i, j);
					if(newState!=null)
						list.add(newState);
				}
			}
		
		if(obj.name == "root"){
			listroot = list;
		}
		return list;
	}
	
	public State legal_move_do(char state[][],int r,int c){
		int stepcount;
		int i,j;
		int found=0;
		State obj = new State(r,c);
		for(int y=0;y<8;y++){
	    	for(int z=0;z<8;z++){
	    		obj.boardpos[y][z] = state[y][z];
	    	}
	    		
	    }
		obj.depth = depth;
		obj.value=value;
		for (int xdir=-1; xdir < 2; xdir++)
			for (int ydir=-1; ydir < 2; ydir++)
			{
			  stepcount = 0;
			  do
			  {
				stepcount++;
				
				i = r + stepcount*xdir; // so many steps along x-axis
				j = c + stepcount*ydir; // so many steps along y-axis
			   }
			  while ( (i >= 0) && (i <= 7) && (j >= 0) && (j <= 7) && (state[i][j] == oppPlayer));
		
			  if (( i >= 0) && (i <= 7) && (j >= 0) && (j <= 7) && (stepcount > 1) && (state[i][j] == player) )
				// You must move more than one step for legal move--(stepcount > 1)
				{ 
				  found =1;
				  obj.boardpos[r][c] = player;
				  for (int k = 1; k < stepcount; k++)
					obj.boardpos[r+xdir*k][c+ydir*k] = player;
				}
	}
		if(found==0){
			return null;
		}
	
	calulateEval(obj);
	return obj;
}
	void calulateEval(State st){
		
		int playersum = 0;
		int oppplayersum = 0;
		for(int a=0;a<8;a++){
			for(int b=0;b<8;b++){
				if(st.boardpos[a][b] == ParsingData.player)
					playersum = playersum+ Gameplay.weight[a][b];
				else if(st.boardpos[a][b] == ParsingData.oppPlayer)
					oppplayersum = oppplayersum+ Gameplay.weight[a][b];
			}
		}
		st.evalfunc =playersum-oppplayersum;
		
	}
	
	boolean check(State s){
		int player1=checkifsame(s,ParsingData.player);
		int player2=checkifsame(s,ParsingData.oppPlayer);
		if(player1 == 1 || player2 ==1)
			return true;
		else return false;
	}
	int checkifsame(State s,char player){
		int c=0;
		for(int a=0;a<8;a++){
			for(int b=0;b<8;b++){
				if(s.boardpos[a][b] == '*' || s.boardpos[a][b] == player)
					c=1;
				else{
					c=2;
					break;
				}	
			}
			if(c == 2)break;
		}
		
		return c;
		
	}
	
	boolean boardFilled(State s){
		int counter =0;
		for(int a=0;a<8;a++){
			for(int b=0;b<8;b++){
				if(s.boardpos[a][b] == '*')
				{
					counter=1;
					break;
					}
			}
			if(counter ==1) break;
		}
			
			if(counter ==1)
				return false;
			else return true;
		}
		
	}


class Minimax{
	int pass =0;
	int minmax(int d,State st,char Player){
		
		int bestscore =(Player == ParsingData.player) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		int currentscore =0;
		State bestState;
		moves obj = new moves();
		/*System.out.println(st.name+",,,"+st.depth+","+st.value);*/
		LinkedList<State> list = obj.legalMoves(st,Player);
		if(list.isEmpty()){
			
		boolean allSameTile = obj.check(st);
		boolean isBoardFilled = obj.boardFilled(st);
		if(allSameTile == true || isBoardFilled == true){
			st.value = st.evalfunc;
			bestscore = st.evalfunc;
			bestState =st;
			/*System.out.println(st.name+","+st.depth+","+st.value);*/
			Gameplay.str.append(st.name+","+st.depth+","+st.value+"\r\n");
			return bestscore;
		}else{
			if(st.depth == ParsingData.depth ){
				bestscore = st.evalfunc;
				bestState =st;
				/*System.out.println(st.name+","+st.depth+","+st.value);*/
				Gameplay.str.append(st.name+","+st.depth+","+st.value+"\r\n");
				return bestscore;
			}else{
				if(pass == 2){
					
					st.value = st.evalfunc;
					bestscore = st.evalfunc;
					bestState =st;
					
					/*System.out.println(st.name+",//"+st.depth+","+st.value);*/
					Gameplay.str.append(st.name+","+st.depth+","+st.value+"\r\n");
					return bestscore;
				}
				State newState =new State();
				if(st.name =="pass")
					pass =2;
				else pass =0;
				for(int y=0;y<8;y++){
			    	for(int z=0;z<8;z++){
			    		newState.boardpos[y][z] = st.boardpos[y][z];
			    	}		
			    }
				
				newState.name = "pass";
				newState.depth = st.depth+1;
				newState.value=(Player == ParsingData.player) ? Integer.MAX_VALUE : Integer.MIN_VALUE;;
				obj.calulateEval(newState);
				list.add(newState);
				
			}
		}
		}
		
		/*System.out.println(st.name+",,,"+st.depth+","+st.value);*/
		Gameplay.str.append(st.name+","+st.depth+","+st.value+"\r\n");
		for(Iterator<State> iterator = list.iterator(); iterator.hasNext();) {
			
			State child = iterator.next();
			if(Player == ParsingData.player){
				
				currentscore = minmax(d+1,child,ParsingData.oppPlayer);
				
				if (currentscore > bestscore) 
	                  bestscore = currentscore;
						child.value = bestscore;
						st.value =bestscore;
						bestState = child;
						
						/*System.out.println(st.name+",++"+st.depth+","+st.value);*/
						Gameplay.str.append(st.name+","+st.depth+","+st.value+"\r\n");
			}
			
			else if(Player == ParsingData.oppPlayer){
				currentscore = minmax(d+1,child,ParsingData.player);
				
				
				if (currentscore < bestscore) 
	                  bestscore = currentscore;
						child.value = bestscore;
						st.value =bestscore;
						bestState = child;
						
						/*System.out.println(st.name+","+st.depth+","+st.value);*/
						Gameplay.str.append(st.name+","+st.depth+","+st.value+"\r\n");
			}
		}
		
		return bestscore;
		
	}
}

class AlphaBetaPruning{
	
	int pass=0;
	 int pruning(int d,State st,char Player,int alpha,int beta){
		 /*System.out.println(st.name);*/
			int score=(Player == ParsingData.player) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
			
			State bestState;
			moves obj = new moves();
			/*System.out.println(st.name);*/
			LinkedList<State> list = obj.legalMoves(st,Player);
			if(list.isEmpty()){
				/*System.out.println("empty");*/
				boolean allSameTile = obj.check(st);
				boolean isBoardFilled = obj.boardFilled(st);
				if(allSameTile == true || isBoardFilled == true){
					st.value = st.evalfunc;
					score = st.evalfunc;
					bestState =st;
					/*System.out.println(st.name+","+st.depth+","+st.value+","+alpha+","+beta);*/
					Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
					return score;
				}else{
					if(st.depth == ParsingData.depth ){
						score = st.evalfunc;
						bestState =st;
						/*System.out.println(st.name+","+st.depth+","+st.value+","+alpha+","+beta);*/
						Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
						return score;
					}else{
						if(pass == 2){
							score = st.evalfunc;
							st.value = st.evalfunc;
							bestState =st;
							
							/*System.out.println(st.name+",//"+st.depth+","+st.value+","+alpha+","+beta);*/
							Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
							return score;
						}
						State newState =new State();
						if(st.name =="pass")
							pass =2;
						else pass =0;
						for(int y=0;y<8;y++){
					    	for(int z=0;z<8;z++){
					    		newState.boardpos[y][z] = st.boardpos[y][z];
					    	}		
					    }
						
						newState.name = "pass";
						newState.depth = st.depth+1;
						newState.value=(Player == ParsingData.player) ? Integer.MAX_VALUE : Integer.MIN_VALUE;;
						obj.calulateEval(newState);
						list.add(newState);
						
					}
				}}
			
				
				Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
			for(Iterator<State> iterator = list.iterator(); iterator.hasNext();) {
				
				State child = iterator.next();
				if(Player == ParsingData.player){
					
					int newscore = pruning(d+1,child,ParsingData.oppPlayer,alpha,beta);
					
					if(newscore>score)
						score=newscore;
					st.value = score;
					
					
					if(score>=beta){
						Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
						return score;
					}
					if(score>alpha)
						alpha=score;
					Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
					
				}
				
				else if(Player == ParsingData.oppPlayer){
					
					int newscore = pruning(d+1,child,ParsingData.player,alpha,beta);
					
					if(newscore<score)
						score=newscore;
					st.value = score;
					/*System.out.println(st.name+",,,,"+st.depth+","+st.value+","+alpha+","+beta);*/
					
					if(score<=alpha){
						Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
						return score;}
					if(score<beta)
						beta=score;
					Gameplay.str.append(st.name+","+st.depth+","+st.value+","+alpha+","+beta+"\r\n");
					
			}
			
	}
			return score;
}
}
class Gameplay{
	static int weight[][]={{99,-8,8,6,6,8,-8,99},{-8,-24,-4,-3,-3,-4,-24,-8},{8,-4,7,4,4,7,-4,8},{6,-3,4,0,0,4,-3,6},{6,-3,4,0,0,4,-3,6},{8,-4,7,4,4,7,-4,8},{-8,-24,-4,-3,-3,-4,-24,-8},{99,-8,8,6,6,8,-8,99}};
	moves obj = new moves();
	static StringBuilder str = new StringBuilder();
	writeData wr = new writeData();
	public void Greedy(){
		obj.calulateEval(ParsingData.sourceState);
		int largest = Integer.MIN_VALUE;
		State nextState =null;
		LinkedList<State> list = obj.legalMoves(ParsingData.sourceState,ParsingData.player);
		
		if(list.isEmpty()){
			
			wr.writingFile(ParsingData.sourceState,null);
		}else{
			
		for(Iterator<State> iterator = list.iterator(); iterator.hasNext();) {
			State child = iterator.next();
			/*System.out.println(child.name);*/
			if(child.evalfunc >largest){
				largest =child.evalfunc;
				nextState =child;
			}
		}
		/*System.out.println(nextState.name);*/
		wr.writingFile(nextState,null);
		}
		
	}
	
	public void MinimaxAlgo(){
		
		obj.calulateEval(ParsingData.sourceState);
		str.append("Node,Depth,Value"+"\r\n");
		
		Minimax objmm = new Minimax();
		objmm.minmax(0,ParsingData.sourceState,ParsingData.player);
		int largest = Integer.MIN_VALUE;
		State bestState =null;
		if(moves.listroot.isEmpty()){
			wr.writingFile(ParsingData.sourceState,str);
		}else{
		for(Iterator<State> iterator = moves.listroot.iterator(); iterator.hasNext();) {
			State child = iterator.next();
			/*System.out.println(child.name+","+child.value);*/
			if(child.value >largest){
				largest =child.value;
				bestState =child;
			}
		}
		/*System.out.println(bestState.name);*/
		wr.writingFile(bestState,str);
	}
	}
	public void AlphaBetaPruningAlgo(){
		obj.calulateEval(ParsingData.sourceState);
		str.append("Node,Depth,Value,Alpha,Beta"+"\r\n");
		
		int alpha =Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		AlphaBetaPruning objab = new AlphaBetaPruning();
		objab.pruning(0,ParsingData.sourceState,ParsingData.player,alpha,beta);
		State bestState =null;
		int largest = Integer.MIN_VALUE;
		if(moves.listroot.isEmpty()){
			wr.writingFile(ParsingData.sourceState,str);
		}else{
		for(Iterator<State> iterator = moves.listroot.iterator(); iterator.hasNext();) {
			State child = iterator.next();
			/*System.out.println(child.name+","+child.value);*/
			if(child.value >largest){
				largest =child.value;
				bestState =child;
			}
		}
		/*System.out.println(bestState.name);*/
		wr.writingFile(bestState,str);
	}
	}
	
}

class writeData{
	void writingFile(State st,StringBuilder str){
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("D:/Masters/AI/HW2/output.txt"));
			for(int i=0;i<8;i++){
		    	for(int j=0;j<8;j++)
		    		writer.write(st.boardpos[i][j]);
		    	writer.write("\r\n");
		    }
			if(str!=null){
				String s=str.toString();
				String t=s.replace("2147483647","Infinity");
				String m=t.replace("-2147483648","-Infinity");
				writer.write(m);
			}
			writer.close();
			/*System.exit(1);*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ParsingData {
	int counter =1;
	static int TaskNo;
	static char player;
	static char oppPlayer;
	static int depth;
	static State sourceState =new State();
	int i=0;
	public void parse(String line){	
		switch(counter){
		
			case 1:
				TaskNo = Integer.parseInt(line);
				counter++;
				break;
				
			case 2:
				player = line.charAt(0);
				if(player =='X')
					oppPlayer = 'O';
				else
					oppPlayer = 'X';
				counter++;
				break;
				
			case 3:
				depth = Integer.parseInt(line);
				counter++;
				break;
				
			case 4:
				
				sourceState.boardpos[i] = line.toCharArray();
				i++;
				break;
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
	    	
	        obj.parse(line);
	        line = br.readLine();
	    }
	   
	    /*for(int i=0;i<8;i++){
	    	for(int j=0;j<8;j++)
	    		System.out.print(ParsingData.sourceState.boardpos[i][j]);
	    	System.out.println();
	    }*/
	    in.close();
	}	
}
public class agent {
	public static void main(String...args){
		
		ReadInputFile objRIF = new ReadInputFile();
		
		try {
			objRIF.read("D:/Masters/AI/HW2/input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		Gameplay objplay =new Gameplay();
		switch(ParsingData.TaskNo)
		{
		case 1:
		{
			objplay.Greedy();
			break;
		}
		case 2:
		{
			
			objplay.MinimaxAlgo();
			break;
		}
		
		case 3:
		{
			objplay.AlphaBetaPruningAlgo();
			break;
		}
		}
		
	}
	
}
