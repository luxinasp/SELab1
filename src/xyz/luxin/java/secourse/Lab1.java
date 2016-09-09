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

	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
}


class Monomial extends Expression implements Comparable<Monomial> {
	
	public int constVaule;
	public int varNumber;
	public TreeMap<String, Integer> varIndex;
	public int monIndex;
	
	public Monomial(String expString) throws ExpressionException {
		
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
		return null;
	}

	@Override
	public int compareTo(Monomial o) {
		// TODO Auto-generated method stub
		
		return 0;
	}
}


class Polynomial extends Expression {
	
}


public class Lab1 {
	
	public static void reference() {
		
//		String pFactor = "(\\d+|[a-zA-Z]+)";
//		String pMonomial = "(" + pFactor + "((\\*)?" + pFactor + ")*)";
//		String pPolynomial = "(" + pMonomial + "([\\+\\-]" + pMonomial + ")*)";
		
		
//		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
//		String pMonomial = "(" + pFactor + "((\\*)?" + pFactor + ")*)";
//		String pPolynomial = "(" + pMonomial + "([\\+\\-]" + pMonomial + ")*)";
		
		
//		String pFactor = "(\\s*((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))\\s*)";
//		String pMonomial = "(\\s*(" + pFactor + "((\\*)?" + pFactor + ")*)\\s*)";
//		String pPolynomial = "(\\s*(" + pMonomial + "([\\+\\-]" + pMonomial + ")*)\\s*)";
		
		String str = "2- 3 x*y +6*x^2y^4*z-y 	y*y	- z^7 ";
		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
		String pMonomial = "(" + pFactor + "(\\s*(\\*)?\\s*" + pFactor + ")*)";
		String pPolynomial = "(\\s*(" + pMonomial + "(\\s*[\\+\\-]\\s*" + pMonomial + ")*)\\s*)";
		
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		reference();
	}

}
