package xyz.luxin.java.secourse;

import java.util.ArrayList;
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
	public int monIndex;
	
	public Monomial(String expString) throws ExpressionException {
		
		constVaule = 1;
		varNumber = 0;
		varIndex = new TreeMap<String, Integer>();
		monIndex  = 0;
	
		
		String pFactor = "(\\d+|[a-zA-Z])";
		String pMonomial = "(" + pFactor + "(\\*" + pFactor + ")*)";

		Pattern p1 = Pattern.compile(pMonomial);
		Matcher m1 = p1.matcher(expString);
		if (!m1.matches()) {
			throw new ExpressionException("Format Error");
		}
		
		Pattern p2 = Pattern.compile(pFactor);
		Matcher m2 = p2.matcher(expString);	
		while (m2.find()) {
			if (m2.group(0).length()==1) {
				if ((m2.group(0).toCharArray())[0]>='0' && (m2.group(0).toCharArray())[0]<='9') {
					constVaule = constVaule * Integer.parseInt(m2.group(0));
				} else {
					if (varIndex.containsKey(m2.group(0))) {
						varIndex.replace(m2.group(0), varIndex.get(m2.group(0))+1);
					} else {
						varIndex.put(m2.group(0), 1);
					}
					monIndex++;
				}
			} else {
				constVaule = constVaule * Integer.parseInt(m2.group(0));
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
		
		//monomial index
		if (this.monIndex > o.monIndex) {
			return 1;
		} else if (this.monIndex < o.monIndex) {
			return -1;
		} else {
			//var number
			if (this.varNumber > o.varNumber) {
				return 1;
			} else if (this.varNumber < o.varNumber) {
				return -1;
			} else {
				if (this.monIndex > o.monIndex) {
					return 1;
				} else if (this.monIndex < o.monIndex) {
					
				} else {
					
				}
			}
		}
		
		
		return 0;
	}
}


class Polynomial extends Expression {
	
}


public class Lab1 {
	
	public static Polynomial analysisPolynomial(String expString) throws ExpressionException {
		
		ArrayList<Monomial> array = new ArrayList<Monomial>();
		
		String pFactor = "(\\d+|[a-zA-Z])";
		String pMonomial = "(" + pFactor + "(\\*" + pFactor + ")*)";
		String pPolynomial = "(" + pMonomial + "(\\+" + pMonomial + ")*)";
		
		
		Pattern p1 = Pattern.compile(pPolynomial);
		Matcher m1 = p1.matcher(expString);		
		if (!m1.matches()) {
			throw new ExpressionException("Format Error");
		}
			
		
		Pattern p2 = Pattern.compile(pMonomial);
		Matcher m2 = p2.matcher(expString);	
		while (m2.find()) {
			array.add(new Monomial(m2.group(0)));
		}
		
		
		String printStr = new String();
		Iterator<Monomial> it = array.iterator();
		while (it.hasNext()) {
			Monomial m = it.next();
			printStr = printStr + m + " + ";
		}
		System.out.println(printStr.substring(0, printStr.length()-3));
		
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		System.out.println("y+k*70+i+2+3*x*y*7+14+x*x*y+y*i*o");
		try {
			analysisPolynomial("y+k*70+i+2+3*x*y*7+14+x*x*y+y*i*o");
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			return;
		}
		*/
		
		
//		String pFactor = "(\\d+|[a-zA-Z]+)";
//		String pMonomial = "(" + pFactor + "((\\*)?" + pFactor + ")*)";
//		String pPolynomial = "(" + pMonomial + "([\\+\\-]" + pMonomial + ")*)";
		
		
//		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
//		String pMonomial = "(" + pFactor + "((\\*)?" + pFactor + ")*)";
//		String pPolynomial = "(" + pMonomial + "([\\+\\-]" + pMonomial + ")*)";
		
		
//		String pFactor = "(\\s*((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))\\s*)";
//		String pMonomial = "(\\s*(" + pFactor + "((\\*)?" + pFactor + ")*)\\s*)";
//		String pPolynomial = "(\\s*(" + pMonomial + "([\\+\\-]" + pMonomial + ")*)\\s*)";
		
		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
		String pMonomial = "(" + pFactor + "(\\s*(\\*)?\\s*" + pFactor + ")*)";
		String pPolynomial = "(\\s*(" + pMonomial + "(\\s*[\\+\\-]\\s*" + pMonomial + ")*)\\s*)";
		
		//String str = "8*ferfre34^67gfgf6ferfg^5345-2*py96-9lk7p9*90*p96*70+i-9u*po3ok*9-6+2-3*x*3y*7+14+x3*x*y+y-i*o";
		//String str = " 89 yu^90 	*ui*	op^7 *u^8	+ad	90*	k^2*9*u*	8^9	*u	";
		String str = "2- 3 x*y +6*x^2y^4*z-y 	y*y	- z^7 ";
		Pattern p = Pattern.compile(pPolynomial);
		Matcher m = p.matcher(str);	
		
		Pattern pOp = Pattern.compile("([\\+\\-])");
		Matcher mOp = pOp.matcher(str);
		
		System.out.println(str);
		
		if (m.matches()) {
			System.out.println("Format Right");
			
			Pattern p1 = Pattern.compile(pMonomial);
			Matcher m1 = p1.matcher(str);
			
			if (m1.find()) {
				System.out.print(m1.group(0));
				System.out.print(" : ");
				
				Pattern p2 = Pattern.compile(pFactor);
				Matcher m2 = p2.matcher(m1.group(0));
				
				while (m2.find()) {
					System.out.print(m2.group(0));
					System.out.print("  ");
				}
				
				System.out.println();
			}
			
			while (m1.find() && mOp.find()) {
				System.out.print(mOp.group(0) + "#" + m1.group(0));
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
