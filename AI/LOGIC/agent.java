import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class returnedPredicate{
	
	boolean ismatch=false;
	String returnedx =null;
	ArrayList totalXpossible =new ArrayList();
}
class Predicate{
	String name;
	String arg1 = null;
	String arg2 = null;
	
	public Predicate(String n, String a1, String a2){
		name = n;
		arg1 = a1;
		arg2 = a2;
	}
	
	public Predicate(String n, String a1){
		name = n;
		arg1 = a1;
	}
	
	public Predicate(){
		name =null;
	}
	returnedPredicate match(Predicate a,Clause clause){
		returnedPredicate obj = new returnedPredicate();
		boolean isarg1match = false;
		boolean isarg2match = false;
		boolean arg2present = false;
		Predicate b = clause.rhs;
		if(a.name.equals(b.name)){
			
			
			if(a.arg1.equals(b.arg1) || a.arg1.equals("x") || b.arg1.equals("x")){
				System.out.println("match arguments:"+a.arg1+"...."+b.arg1);
				isarg1match =true;
			}
			if(a.arg2 != null && b.arg2 != null){
				arg2present = true;
				if(a.arg2.equals(b.arg2) || a.arg2.equals("x") || b.arg2.equals("x")){
					isarg2match =true;
				}
			}
			
			if(isarg1match ==true){
				
				if(arg2present && !isarg2match){
					obj.ismatch =false;
					return obj;
				}
					
				
				
				/*if(clause.isFact == true ){
					
					System.out.println("isfact");
					return true;
				}*/
				obj.ismatch =true;
				String value = getValuex(a, b);
				System.out.println("the value of x:"+value);
				if(value != null){
					System.out.println("the value of x:"+value);
					if(clause.isFact == false){
					replaceX(value, b);
					
					System.out.println("replaced:"+b.name+",,,"+b.arg1+",,"+b.arg2);
					
					
					int len = clause.lhs.length;
					System.out.println("length:"+len);
					
					for(int i=0;i<len;i++){
						System.out.println("inside for");
						replaceX(value, clause.lhs[i]);
					}
				}
					
					obj.returnedx =value;
				}
				obj.returnedx =value;
				/*obj.totalXpossible.add(value);*/
				return obj;
			}
		}
		return obj;
	}
	
	String getValuex(Predicate a,Predicate b){
		
		String value=null;
		
		if(a.arg1.equals("x") && !(b.arg1.equals("x"))){
			value = b.arg1;
		}else if(!(a.arg1.equals("x")) && b.arg1.equals("x")){
			value = a.arg1;
		}
		if(a.arg2 != null && b.arg2 != null){
			
		if(a.arg2.equals("x") && !(b.arg2.equals("x"))){
			value = b.arg2;
		}else if(!(a.arg2.equals("x")) && b.arg2.equals("x")){
			value = a.arg2;
		}
			
		}	
		return value;
	}
	
	void replaceX(String value,Predicate p){
		
		if((p.arg1).equals("x")){
			System.out.println("555");
			p.arg1 =value;
		}
		if(p.arg2 != null){
		
			if((p.arg2).equals("x")){
			p.arg2 = value;
		}
	}
	}
}


class Clause{
	
	Predicate lhs[];
	Predicate rhs;
	Boolean isFact = false;
	
}

class BackwardChaining{
	
	Predicate obj= new Predicate();
	
	returnedPredicate OR_SEARCH(Predicate query){
		returnedPredicate ans =null;
		returnedPredicate OR=new returnedPredicate();
		int i =0;
		for(i=0;i<ParsingData.noOfClauses;i++){
			
			ans = obj.match(query, ParsingData.KB[i]);
			if(ans.ismatch ==true){
				OR.ismatch =true;
				System.out.println("OR-SEARCH");
				System.out.println(ParsingData.KB[i].rhs.name+"("+ParsingData.KB[i].rhs.arg1+","+ParsingData.KB[i].rhs.arg2+")");

				String c = ans.returnedx;
				if(c!=null)
					OR.totalXpossible.add(c);
				
				if(ParsingData.KB[i].isFact){
					
					System.out.println("here");
					continue;
				}
					boolean r = AND_SEARCH(ParsingData.KB[i].lhs);
					OR.ismatch =r;
					/*return OR;*/
				
			}
		}
		/*System.out.println("in false????");
		System.out.println(",,,"+ans.ismatch);
		if(OR.ismatch ==true){
			System.out.println("???in true????");
			return OR;
		}
		OR.ismatch =false;*/
		return OR;
		
	}
	
	boolean AND_SEARCH(Predicate arr[]){
		returnedPredicate obj;
		String prevx=null;
		int len = arr.length;
		int oneQueryFalse=0;
		
		for(int i =0;i<len;i++){
			
			System.out.println("AND_SEARCH");
			System.out.println(arr[i].name+"("+arr[i].arg1+","+arr[i].arg2+")");
			obj = OR_SEARCH(arr[i]);
			System.out.println("obj value in and"+obj.ismatch);
			if(obj.ismatch == false){
				System.out.println("in false??? And search");
				oneQueryFalse =1;
				break;
			}
			if(obj.ismatch ==true){
				/*System.out.println("in true??? And search");
				System.out.println("value of obj"+obj.returnedx);
				System.out.println("value of prevx"+prevx);
				if(prevx!=null && obj.returnedx !=null){
					
					if(!(prevx.equals(obj.returnedx))){
						System.out.println("false***");
						oneQueryFalse =1;
						break;
						continue;
					}
					
				}
				if(obj.returnedx != null)
					prevx = obj.returnedx;
				System.out.println("value of prevx"+prevx);*/
				if(ParsingData.AND.isEmpty()){
					ParsingData.AND.addAll(obj.totalXpossible);
				}
				else{
					ParsingData.AND.retainAll(obj.totalXpossible);
					if(ParsingData.AND.isEmpty()){
						oneQueryFalse = 1;
						break;
					}
				}
				
			}
		}
		if(oneQueryFalse ==1)
			return false;
		else 
			return true;
	}
}

class createClause{
	public Clause createClauseMethod(String str){
		Clause clause=null;
		String s[] = str.split("=>");
		
		if(s.length == 1){
			clause = new Clause();
			String p1 = s[0];
			Predicate p = createpredicate(p1);
			
			clause.rhs = p;
			clause.isFact =true;
		}
		
		else if(s.length ==2){
			clause = new Clause();
			
			String p1 = s[1];
			Predicate p = createpredicate(p1);
			clause.rhs = p;
			
			String p2[] = s[0].split("&");
			int len = p2.length;
			Predicate prArray[] = new Predicate[len];
			int i =0;
			
			while(i != len){
				prArray[i] = createpredicate(p2[i]);
				i++;
			}
			
			clause.lhs = prArray;
			
		}else{
			System.out.print("Invalid format");
		}
		return clause;
	}
	
	Predicate createpredicate(String str){
		
		String args1,args2;
		Predicate obj=null;
		String p1[] = str.split("\\(");
		String name = p1[0];
		/*String args = pp1[1];*/
		String args[] = p1[1].split(",");
		if(args.length == 1){
			args1 = args[0].substring(0, args[0].length()-1);
			obj = new Predicate(name,args1);
			
		}else if(args.length == 2){
			args1 = args[0];
			args2 = args[1].substring(0, args[1].length()-1);
			obj = new Predicate(name,args1,args2);
		}
		return obj;
	}
}




class ParsingData {
	int counter =1;
	createClause obj = new createClause();
	static Clause goal ;
	static Clause KB[];
	static int noOfClauses = 0;
	static ArrayList AND = new ArrayList();
	int i=0;
	public void parse(String line){	
		switch(counter){
		
			case 1:
				goal = obj.createClauseMethod(line);
				goal.isFact = false;
				counter++;
				break;
				
			case 2:
				noOfClauses = Integer.parseInt(line);
				KB= new Clause[noOfClauses];
				counter++;
				break;
				
			case 3:
				KB[i] = obj.createClauseMethod(line);
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
	    in.close();
	}	
}

class writeData{
	void writingFile(boolean b) throws IOException{
		BufferedWriter writer = null;
		
			writer = new BufferedWriter(new FileWriter("D:/Masters/AI/HW3/output.txt"));
			
			if(b == true)
				writer.write("TRUE");	
			else if(b == false)
				writer.write("FALSE");
			writer.close();
		
}
	}

public class agent {
	
	public static void main(String...args){
		
		ReadInputFile objRIF = new ReadInputFile();
		
		try {
			objRIF.read("D:/Masters/AI/HW3/input.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(ParsingData.noOfClauses);
		System.out.println(ParsingData.goal.rhs.name+"("+ParsingData.goal.rhs.arg1+","+ParsingData.goal.rhs.arg2+")");
		
		for(int i=0;i<ParsingData.KB.length;i++){
			if(ParsingData.KB[i].lhs != null)
			for(int j=0;j<ParsingData.KB[i].lhs.length;j++){
				System.out.println(ParsingData.KB[i].lhs[j].name+"("+ParsingData.KB[i].lhs[j].arg1+","+ParsingData.KB[i].lhs[j].arg2+")");
				System.out.print(" & ");
			}
			System.out.print(" => ");
			System.out.println(ParsingData.KB[i].rhs.name+"("+ParsingData.KB[i].rhs.arg1+","+ParsingData.KB[i].rhs.arg2+")");
		}
		
		BackwardChaining bc = new BackwardChaining();
		returnedPredicate b=bc.OR_SEARCH(ParsingData.goal.rhs);
		
		writeData wd = new writeData();
		try {
			wd.writingFile(b.ismatch);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
}
}
