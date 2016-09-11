package xyz.luxin.java.secourse;

import java.util.Iterator;
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
		if (op=='+' || op=='-' || op=='*' || op=='^') {
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
	
	
	public Monomial() {
		// TODO Auto-generated constructor stub
		constVaule = 1;
		varNumber = 0;
		varIndex = new TreeMap<String, Integer>();
		monIndex = 0;
	}
	
	public Monomial(Monomial o) {
		// TODO Auto-generated constructor stub
		this.constVaule = o.constVaule;
		this.varNumber = o.varNumber;
		this.varIndex = new TreeMap<String, Integer>(o.varIndex);
		this.monIndex = o.monIndex;
	}
	
	public Monomial(String expString, boolean isExtraNegative) throws ExpressionException {
		
		constVaule = 1;
		varNumber = 0;
		varIndex = new TreeMap<String, Integer>();
		monIndex = 0;
		
		if (isExtraNegative) {
			constVaule = -1;
		}
		
		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
		String pMonomial = "(" + pFactor + "(\\s*(\\*)?\\s*" + pFactor + ")*)";
		
		Pattern p1 = Pattern.compile(pMonomial);
		Matcher m1 = p1.matcher(expString);
		
		if (!m1.matches()) {
			throw new ExpressionException("Format Error");
		}
		
		
		Pattern p2 = Pattern.compile(pFactor);
		Matcher m2 = p2.matcher(expString);
		
		while (m2.find()) {
			String str = m2.group(0);
			char[] chars = str.toCharArray();
			if (chars[0]>='0' && chars[0]<='9') {
				//number
				if (str.contains("^")) {
					String[] nums = str.split("\\^");
					if (nums.length != 2) {
						throw new ExpressionException("Format Error");
					}
					
					constVaule *= (int)Math.pow(Integer.parseInt(nums[0]), Integer.parseInt(nums[1]));
				} else {
					constVaule *= Integer.parseInt(str);
				}
			} else {
				//var
				if (str.contains("^")) {
					String[] pair = str.split("\\^");
					if (pair.length != 2) {
						throw new ExpressionException("Format Error");
					}
					
					if (varIndex.containsKey(pair[0])) {
						varIndex.replace(pair[0], varIndex.get(pair[0])+Integer.parseInt(pair[1]));
					} else {
						varIndex.put(pair[0], Integer.parseInt(pair[1]));
					}
					
					monIndex += Integer.parseInt(pair[1]);
					
				} else {
					if (varIndex.containsKey(str)) {
						varIndex.replace(str, varIndex.get(str)+1);
					} else {
						varIndex.put(str, 1);
					}
					
					monIndex++;
				}
			}
		}
		
		varNumber = varIndex.size();
		
		return;
	}
	
	public Monomial simplify(TreeMap<String, Integer> pairs) {
		
		Monomial result = new Monomial(this);
		
		Iterator<Entry<String, Integer>> it = pairs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it.next();
			String key = entry.getKey();
			Integer value = entry.getValue();
			
			if (result.varIndex.containsKey(key)) {
				Integer index = result.varIndex.get(key);
				result.varIndex.remove(key);
				result.varNumber--;
				result.monIndex -= index;
				for (int i=0; i<index; i++) {
					result.constVaule *= value;
				}
			}
		}
		
		return result;
	}
	
	public Monomial derivative(String var) {
		
		Monomial result = new Monomial();

		if (!varIndex.containsKey(var)) {
			result.constVaule = 0;
			return result;
		}
		
		
		result = new Monomial(this);
		
		if (result.varIndex.get(var)==1) {
			result.varIndex.remove(var);
			result.varNumber--;
			result.monIndex--;
			return result;
		}
		
		result.monIndex--;
		result.constVaule = result.constVaule * result.varIndex.get(var);
		result.varIndex.replace(var, result.varIndex.get(var)-1);
		return result;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		if (varIndex.isEmpty()) {
			return String.valueOf(constVaule);
		}
		
		String result = "";
		
		if (constVaule == -1) {
			result += "-";
		} else if (constVaule != 1) {
			result += String.valueOf(constVaule);
			result += "*";
		}
		
		
		Iterator<Entry<String, Integer>> it = varIndex.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it.next();
			String key = entry.getKey();
			Integer value = entry.getValue();
			if (value==1) {
				result = result + key + "*";
			} else {
				result = result + key + "^" + String.valueOf(value) + "*";
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
		
		if (this.monIndex > o.monIndex) {
			return 1;
		} else if (this.monIndex < o.monIndex) {
			return -1;
		} else {
			if (this.varNumber > o.varNumber) {
				return 1;
			} else if (this.varNumber < o.varNumber) {
				return -1;
			} else {
				Iterator<Entry<String, Integer>> it1 = this.varIndex.entrySet().iterator();
				Iterator<Entry<String, Integer>> it2 = o.varIndex.entrySet().iterator();
				while (it1.hasNext() && it2.hasNext()) {
					Entry<String, Integer> entry1 = (Entry<String, Integer>)it1.next();
					Entry<String, Integer> entry2 = (Entry<String, Integer>)it2.next();
					String key1 = entry1.getKey();
					String key2 = entry2.getKey();
					if (key1.compareTo(key2) > 0) {
						return 1;
					} else if (key1.compareTo(key2) < 0) {
						return -1;
					}
				}
				return 0;
			}
		}
	}
}


class Polynomial extends Expression {
	
	private TreeMap<Monomial, Integer> mMonos;
	
	public Polynomial() {
		mMonos = new TreeMap<Monomial, Integer>();
	}
	
	public Polynomial(String expString) throws ExpressionException {
		mMonos = new TreeMap<Monomial, Integer>();
		expression(expString);
	}
	
	public Polynomial(Monomial m) {
		mMonos = new TreeMap<Monomial, Integer>();
		mMonos.put(m, m.constVaule);
	}
	
	public Polynomial(TreeMap<Monomial, Integer> monos) {
		mMonos = monos;
	}
	
	public void expression(String expString) throws ExpressionException {
		
		String pFactor = "((\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
		String pMonomial = "(" + pFactor + "(\\s*(\\*)?\\s*" + pFactor + ")*)";
		String pPolynomial = "(\\s*(" + pMonomial + "(\\s*[\\+\\-]\\s*" + pMonomial + ")*)\\s*)";
		
		Pattern p = Pattern.compile(pPolynomial);
		Matcher m = p.matcher(expString);	
		
		Pattern pOp = Pattern.compile("([\\+\\-])");
		Matcher mOp = pOp.matcher(expString);
		
		if (!m.matches()) {
			throw new ExpressionException("Format Error");
		}
		
			
		Pattern p1 = Pattern.compile(pMonomial);
		Matcher m1 = p1.matcher(expString);
		
		if (m1.find()) {	
			Monomial mono = new Monomial(m1.group(0), false);
			
			if (mMonos.containsKey(mono)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = mMonos.get(mono);
				mono.constVaule = mono.constVaule + n;
				mMonos.remove(mono);
			}
			if (mono.constVaule != 0) {
				mMonos.put(mono, mono.constVaule);
			}
		}
			
		while (m1.find() && mOp.find()) {

			boolean isExtraNegative = false;

			if (mOp.group(0).equals("-")) {
				isExtraNegative = true;
			}
			
			Monomial mono = new Monomial(m1.group(0), isExtraNegative);
			
			if (mMonos.containsKey(mono)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = mMonos.get(mono);
				mono.constVaule = mono.constVaule + n;
				mMonos.remove(mono);
			} 
			if (mono.constVaule != 0) {
				mMonos.put(mono, mono.constVaule);
			}
		}
		
		return;
	}
	
	public Polynomial simplify(TreeMap<String, Integer> pairs) {
		
		TreeMap<Monomial, Integer> result = new TreeMap<Monomial, Integer>();
		
		Iterator<Entry<Monomial, Integer>> it = mMonos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)it.next();
			Monomial m = entry.getKey().simplify(pairs);

			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				m.constVaule = m.constVaule + n;
				result.remove(m);
			}
			if (m.constVaule != 0) {
				result.put(m, m.constVaule);
			}
		}

		return new Polynomial(result);
	}
	
	public Polynomial derivative(String var) {

		TreeMap<Monomial, Integer> result = new TreeMap<Monomial, Integer>();
		
		Iterator<Entry<Monomial, Integer>> it = mMonos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)it.next();
			Monomial m = entry.getKey().derivative(var);
			
			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				m.constVaule = m.constVaule + n;
				result.remove(m);
			}
			if (m.constVaule != 0) {
				result.put(m, m.constVaule);
			}
		}

		return new Polynomial(result);
	}
	
	public static Polynomial arithmetic(Polynomial p1, Polynomial p2, Operator op) {
		//coding...
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		
		if (mMonos.isEmpty()) {
			return new String("0");
		}
		
		String result = new String("");
		
		Iterator<Entry<Monomial, Integer>> it = mMonos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)it.next();
			Monomial m = entry.getKey();
			if (m.constVaule < 0) {
				result = result + m.toString();
			} else if (m.constVaule > 0) {
				result = result + "+" + m.toString();
			}
		}
		
		if ((result.toCharArray())[0]=='+') {
			result = result.substring(1, result.length());
		}
		
		return result;
	}
	
}


public class Lab1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			Polynomial poly = new Polynomial("12-3z*xx*yyy +6*xx^2yyy^4*z-yyy yyy*yyy- z^7 - 9 -22yyy*xx*z");
			System.out.println(poly);
			System.out.println(poly.derivative("xx"));
			System.out.println(poly.derivative("xx").derivative("z"));
			System.out.println(poly.derivative("xx").derivative("z").derivative("yyy"));
			System.out.println(poly.derivative("xx").derivative("z").derivative("yyy").derivative("yyy"));
			System.out.println(poly.derivative("x"));
			System.out.println();
			
			TreeMap<String, Integer> vars1 = new TreeMap<String, Integer>();
			vars1.put("x", 3);
			vars1.put("xx", 3);
			vars1.put("z", -2);
			System.out.println(poly.simplify(vars1));
			System.out.println();
			
			TreeMap<String, Integer> vars2 = new TreeMap<String, Integer>();
			vars2.put("x", 3);
			vars2.put("xx", 3);
			vars2.put("z", -2);
			vars2.put("yyy", 1);
			System.out.println(poly.simplify(vars2).simplify(vars2));
			System.out.println();
			
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return;
		}
	}
	
}
