package xyz.luxin.java.secourse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionTree {

	public Expression exp;
	public ExpressionTree left, right;
	
	public ExpressionTree() {
		this.exp = null;
		this.left = null;
		this.right = null;
	}
	
	public static Polynomial obtainPolynomial(ExpressionTree t) throws ExpressionException  {
		
		if(t != null) {
			obtainPolynomial(t.left);
			obtainPolynomial(t.right);
			if (t.exp instanceof Operator) {
				if (!(t.left.exp instanceof Polynomial) || !(t.right.exp instanceof Polynomial)) {
					throw new ExpressionException("Internal Error");
				}
				t.exp = Polynomial.arithmetic((Polynomial)t.left.exp, (Polynomial)t.right.exp, (Operator)t.exp);
				t.left = null;
				t.right = null;
				return (Polynomial)t.exp;
			} else if (t.exp instanceof Polynomial) {
				if (t.left!=null || t.right!=null) {
					throw new ExpressionException("Internal Error");
				}
				return (Polynomial)t.exp;
			}
		}
		return null;
	}
	
	public static void createTree(ExpressionTree t, String expString) throws ExpressionException {
	
		Pattern p;
		Matcher m;
		char[] chars = expString.toCharArray();
		
		//no bracket
		if (!expString.contains("(") && !expString.contains(")")) {
			t.exp = new Polynomial(expString, true);
			return;
		}
		
		//check only both ends bracket
		p = Pattern.compile("\\s*\\(([^\\(\\)]*)\\)\\s*");
		m = p.matcher(expString);
		if (m.matches()) {
			t.exp = new Polynomial(m.group(1), true);
			return;
		}
		
		//remove both ends paired bracket
		p = Pattern.compile("\\s*\\((.*)\\)\\s*");
		m = p.matcher(expString);
		if (m.matches()) {
			//check paired
			boolean flag = true;
			char[] tmpChars = m.group(1).toCharArray();
			int count = 0;
			
			for (int i=0; i<m.group(1).length(); i++) {
				if (tmpChars[i]=='(') {
					count++;
				}
				if (tmpChars[i]==')') {
					count--;
				}
				if (count < 0) {
					flag = false;
				}
			}
			if (count != 0) {
				throw new ExpressionException("Bracket Error");
			}
			
			if (flag) {
				expString = m.group(1);
				chars = expString.toCharArray();
			}
		}
		
		//find available op
		int opIndex = -1;
		int opLevel = 0;
		int level = 0;
		for (int i=0; i<chars.length; i++) {
			if (chars[i]=='+') {
				if (level + 3  > opLevel) {
					opIndex = i;
					opLevel = level + 3;
				}
			}
			if (chars[i]=='*') {
				if (level + 2  > opLevel) {
					opIndex = i;
					opLevel = level + 2;
				}
			}
			if (chars[i]=='^') {
				if (level + 1  > opLevel) {
					opIndex = i;
					opLevel = level + 1;
				}
			}
			if (chars[i]=='(') {
				level = level - 4;
			}
			if (chars[i]==')') {
				level = level + 4;
			}
		}
		
		if (opIndex == -1) {
			throw new ExpressionException("Format Error");
		} else {
			t.exp = new Operator(chars[opIndex]);
			t.left = new ExpressionTree();
			t.right = new ExpressionTree();
			String left = expString.substring(0, opIndex);
			String right = expString.substring(opIndex+1, expString.length());
			createTree(t.left, left);
			createTree(t.right, right);
		}

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
			System.out.print("(");
			midOrder(t.left);
			System.out.print(t.exp);
			midOrder(t.right);
			System.out.print(")");
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