package xyz.luxin.java.secourse;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@SuppressWarnings("serial")
class ExpressionException extends Exception
{
	public ExpressionException(String msg)
	{
		super(msg);
	}
} 

abstract class Expression {
	//Expression Abstract Class
}

class Operator extends Expression {
	
	public char op;
	
	public Operator(char op) throws ExpressionException {
		if (op=='+' || op=='*') {
			this.op = op;
		} else {
			throw new ExpressionException("Operator Illegal");
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.valueOf(op);
	}
}

class Monomial extends Expression implements Comparable<Monomial> {
	
	public int constVaule;
	public int varNumber;
	public TreeMap<String, Integer> varIndex;
	
	public Monomial(String expString) throws ExpressionException {
		
		constVaule = 1;
		varNumber = 0;
		varIndex = new TreeMap<String, Integer>();
		

		Pattern p = Pattern.compile("(\\d+|[a-zA-Z])|(((\\d+|[a-zA-Z])\\*)+(\\d+|[a-zA-Z]))");
		Matcher m = p.matcher(expString);
		
		if (!m.matches()) {
			throw new ExpressionException("Format Error");
		}
		

		String[] parts = expString.split("\\*");
		
		for (int i=0; i<parts.length; i++) {
			if (parts[i].length()==1) {
				if ((parts[i].toCharArray())[0]>='0' && (parts[i].toCharArray())[0]<='9') {
					constVaule = constVaule * Integer.parseInt(parts[i]);
				} else {
					if (varIndex.containsKey(parts[i])) {
						varIndex.replace(parts[i], varIndex.get(parts[i])+1);
					} else {
						varIndex.put(parts[i], 1);
					}
				}
			} else {
				constVaule = constVaule * Integer.parseInt(parts[i]);
			}
		}
		
		return;
	}
	
	public Monomial findDerivative(String var) throws ExpressionException {
		return null;
	}
	
	public Monomial evaluation(Map<String, Integer> pairs) throws ExpressionException {
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
			
		if (varIndex.isEmpty()) {
			return String.valueOf(constVaule);
		}
		
		String result = "";
		
		if (constVaule != 1) {
			result += String.valueOf(constVaule);
			result += "*";
		}
		
		Iterator<Entry<String, Integer>> it = varIndex.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it.next();
			String key = entry.getKey();
			Integer value = entry.getValue();
			for (int i=0; i<value; i++) {
				result += key;
				result += "*";
			}
		}
			
		if ((result.toCharArray())[result.length()-1]=='*') {
			result = result.substring(0, result.length()-1);
		}
		
		return result;
	}

	@Override
	public int compareTo(Monomial o) {
		// TODO Auto-generated method stub
		return 0;
	}
}

class Polynomial extends Expression {
	
}


class ExpressionTree {

	public Expression exp;
	public ExpressionTree left, right;
	
	public ExpressionTree() {
		this.exp = null;
		this.left = null;
		this.right = null;
	}
	
	public static boolean checkCharacter(String expString) {
		
		char[] chars = expString.toCharArray();
		
		for (int i=0; i<chars.length; i++) {
			if (!((chars[i]=='+') || 
				(chars[i]=='*') || 
				(chars[i]>='0'&&chars[i]<='9') || 
				(chars[i]>='a'&&chars[i]<='z') || 
				(chars[i]>='A'&&chars[i]<='Z')
				)) {
				return false;
			}
		}	
		
		return true;
	}
	
	public static void createTree(ExpressionTree t, String expString) throws ExpressionException {

		char[] chars = expString.toCharArray();
		
		if (chars.length == 1) {
			if (chars[0]=='+' || chars[0]=='*') {
				throw new ExpressionException("Format Error");
			} 
			//it is leaf
			t.exp = new Monomial(expString);
			return;
		}
		
		if (chars.length == 2) {
			if (!(chars[0]>='0'&&chars[0]<='9'&&chars[1]>='0'&&chars[1]<='9')) {
				throw new ExpressionException("Format Error");
			}
			//it is leaf
			t.exp = new Monomial(expString);
			return;
		}
		

		//Find middle near +
		
		int i = chars.length/2;
		int j = chars.length/2;

		while (i>=0 || j<=chars.length-1) {
			
			if (i>=0 && chars[i]=='+') {
				if (i==0) {
					throw new ExpressionException("Format Error");
				} else {
					t.exp = new Operator('+');
					t.left = new ExpressionTree();
					t.right = new ExpressionTree();
					createTree(t.left, new String(chars, 0, i));
					createTree(t.right, new String(chars, i+1, chars.length-i-1));
					return;
				}
			}

			if (j<=chars.length-1 && chars[j]=='+') {
				if (j==chars.length-1) {
					throw new ExpressionException("Format Error");
				} else {
					t.exp = new Operator('+');
					t.left = new ExpressionTree();
					t.right = new ExpressionTree();
					createTree(t.left, new String(chars, 0, j));
					createTree(t.right, new String(chars, j+1, chars.length-j-1));
					return;
				}
			}
			
			i--;
			j++;
		}
		
		
		//no +, it is leaf
		t.exp = new Monomial(expString);
		return;
	}
	
	public static void preOrder(ExpressionTree t) {
		if(null != t) {
			System.out.print(t.exp + " ");
			preOrder(t.left);
			preOrder(t.right);
		}
	}
	
	public static void midOrder(ExpressionTree t) {
		if(null != t) {
			midOrder(t.left);
			System.out.print(t.exp + " ");
			midOrder(t.right);
		}
	}

	public static void lastOrder(ExpressionTree t) {
		if(null != t) {
			lastOrder(t.left);
			lastOrder(t.right);
			System.out.print(t.exp + " ");
		}
	}
}

public class Lab1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ExpressionTree root = new ExpressionTree();
		try {
			ExpressionTree.createTree(root, "y+k*7+i+2+3*x*y*7+4+x*x*y+y");
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(e.getMessage());
			return;
		}
		
		System.out.println("OK!");
		
		//ExpressionTree.preOrder(root);
		//System.out.println("");
		
		ExpressionTree.midOrder(root);
		System.out.println("");
		
		//ExpressionTree.lastOrder(root);
		//System.out.println("");
		
		System.out.println("Yes!\n\n\n\n");
		
		
		/*
		String pFactor = "(\\d+|[a-zA-Z])";
		String pMonomial = "("+pFactor+"|(("+pFactor+"\\*)+"+pFactor+"))";
		String pPolynomial = "("+pMonomial+"|(("+pMonomial+"\\+)+"+pMonomial+"))";
		*/
		
		String pFactor = "(\\d+|[a-zA-Z])";
		String pMonomial = "(" + pFactor + "(\\*" + pFactor + ")*)";
		String pPolynomial = "(" + pMonomial + "(\\+" + pMonomial + ")*)";
		
		
		Pattern p = Pattern.compile(pPolynomial);
		Matcher m = p.matcher("y+k*7+i+2+3*x*y*7+4+x*x*y+y*i*o");
		
		System.out.println("y+k*7+i+2+3*x*y*7+4+x*x*y+y*i*o");
		
		if (m.matches()) {
			System.out.println("Format Right");
			
			Pattern p1 = Pattern.compile(pMonomial);
			Matcher m1 = p1.matcher("y+k*7+i+2+3*x*y*7+4+x*x*y+y*i*o");
			
			while (m1.find()) {
				System.out.print(m1.group(0));
				System.out.print(" : ");
				
				Pattern p2 = Pattern.compile(pFactor);
				Matcher m2 = p2.matcher(m1.group(0));
				
				while (m2.find()) {
					System.out.print(m2.group(0));
					System.out.print("  ");
				}
				
				System.out.println("");
			}
	
		} else {
			System.out.println("Format Error");
		}
		
	}

}
