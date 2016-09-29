package xyz.luxin.java.secourse;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Polynomial extends Expression {
	
	private TreeMap<Monomial, Integer> mMonos;
	
	public Polynomial() {
		mMonos = new TreeMap<Monomial, Integer>();
	}
	
	public Polynomial(String expString, boolean isNegEx) throws ExpressionException {
		mMonos = new TreeMap<Monomial, Integer>();
		if (isNegEx) {
			expression(expString, true);
		} else {
			expression(expString, false);
		}
	}
	
	public Polynomial(String expString) throws ExpressionException {
		//Does not support "Negative Extension"(#) by default.
		this(expString, false);
	}
	
	public Polynomial(Monomial m) {
		mMonos = new TreeMap<Monomial, Integer>();
		mMonos.put(m, m.constVaule);
	}
	
	public Polynomial(TreeMap<Monomial, Integer> monos) {
		mMonos = monos;
	}
	
	public void expressionBracket(String expString) throws ExpressionException {
		
		Pattern p;
		Matcher m;
		StringBuffer sb;
		char[] chars = expString.toCharArray();

		
		//check character
		Pattern p1 = Pattern.compile("[^a-zA-Z0-9\\+\\-\\*\\^\\(\\)\\s]");
		Matcher m1 = p1.matcher(expString);	
		if (m1.find()) {
			throw new ExpressionException("Unknown Character");
		}
		
		
		//check bracket
		int count = 0;
		for (int i=0; i<expString.length(); i++) {
			if (chars[i]=='(') {
				count++;
			}
			if (chars[i]==')') {
				count--;
			}
			if (count < 0) {
				throw new ExpressionException("Bracket Error");
			}
		}
		if (count != 0) {
			throw new ExpressionException("Bracket Error");
		}
		
		
		//fix bracket *
		p = Pattern.compile("(\\d+|[a-zA-Z]+)\\s*\\(");
		m = p.matcher(expString);	
		sb = new StringBuffer();
		while(m.find()) {
			String str = m.group().replaceAll("\\(", "*(");
			m.appendReplacement(sb, str);
		}
		m.appendTail(sb);
		expString = sb.toString();
		chars = expString.toCharArray();
		
		p = Pattern.compile("\\)\\s*(\\d+|[a-zA-Z]+)");
		m = p.matcher(expString);	
		sb = new StringBuffer();
		while(m.find()) {
			String str = m.group().replaceAll("\\)", ")*");
			m.appendReplacement(sb, str);
		}
		m.appendTail(sb);
		expString = sb.toString();
		chars = expString.toCharArray();
		
		p = Pattern.compile("\\)\\s*\\(");
		m = p.matcher(expString);	
		sb = new StringBuffer();
		while(m.find()) {
			String str = m.group().replaceAll("\\)", ")*");
			m.appendReplacement(sb, str);
		}
		m.appendTail(sb);
		expString = sb.toString();
		chars = expString.toCharArray();
		
		
		//check - and fix - to +#*
		int realCount = 0;
		for (int i=0; i<chars.length; i++) {
			if (chars[i]=='-') {
				realCount++;
			}
		}
		
		int matchCount = 0;
		p = Pattern.compile("(\\d+|[a-zA-Z]+|\\))\\s*\\-\\s*(\\d+|[a-zA-Z]+|\\()");
		m = p.matcher(expString);	
		sb = new StringBuffer();
		while(m.find()) {
			String str = m.group().replaceAll("\\-", "+#*");
			m.appendReplacement(sb, str);
			matchCount++;
		}
		m.appendTail(sb);
		expString = sb.toString();
		chars = expString.toCharArray();
		
		if (realCount != matchCount) {
			throw new ExpressionException("Format Error");
		}

		
		ExpressionTree tree = new ExpressionTree();
		ExpressionTree.createTree(tree, expString);
		
		this.mMonos = ExpressionTree.obtainPolynomial(tree).mMonos;
	}
	
	public void expression(String expString, boolean isNegEx) throws ExpressionException {
		
		if (!isNegEx) {
			//check character
			Pattern p0 = Pattern.compile("[^a-zA-Z0-9\\+\\-\\*\\^\\(\\)\\s]");
			Matcher m0 = p0.matcher(expString);	
			if (m0.find()) {
				throw new ExpressionException("Unknown Character");
			}
		}
		
		String pFactor = "(#|(\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
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
			Monomial mono = new Monomial(m1.group(0), false, isNegEx);
			
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
			
			Monomial mono = new Monomial(m1.group(0), isExtraNegative, isNegEx);
			
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
	
	public void expression(String expString) throws ExpressionException {
		//Does not support "Negative Extension"(#) by default.
		expression(expString, false);
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
	
	public static Polynomial add(Polynomial p1, Polynomial p2){
		
		TreeMap<Monomial, Integer> result = new TreeMap<Monomial, Integer>();
		
		Iterator<Entry<Monomial, Integer>> p1_it = p1.mMonos.entrySet().iterator();
		while (p1_it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)p1_it.next();
			Monomial m = new Monomial(entry.getKey());
			
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
		//System.out.println(p2.toString());
		Iterator<Entry<Monomial, Integer>> p2_it = p2.mMonos.entrySet().iterator();
		while (p2_it.hasNext()) {
			Entry<Monomial, Integer> entry = (Entry<Monomial, Integer>)p2_it.next();
			Monomial m = new Monomial(entry.getKey());

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

	public static Polynomial multiplication(Polynomial p1, Polynomial p2){
		
		TreeMap<Monomial, Integer> result = new TreeMap<Monomial, Integer>();
			
		Iterator<Entry<Monomial, Integer>> p1_it = p1.mMonos.entrySet().iterator();
		while (p1_it.hasNext()) {
			Entry<Monomial, Integer> p1Entry = (Entry<Monomial, Integer>)p1_it.next();
			Monomial m1 = p1Entry.getKey();
			//System.out.println(m1.toString());
			Iterator<Entry<Monomial, Integer>> p2_it = p2.mMonos.entrySet().iterator();
			while (p2_it.hasNext()) {
				Entry<Monomial, Integer> p2Entry = (Entry<Monomial, Integer>)p2_it.next();
				Monomial m2 = m1.multiplication(p2Entry.getKey());

				if (result.containsKey(m2)) {
					//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
					Integer n = result.get(m2);
					m2.constVaule = m2.constVaule + n;
					result.remove(m2);
				}
				if (m2.constVaule != 0) {
					result.put(m2, m2.constVaule);
				}
			}
		}
		return new Polynomial(result);
	}
	
	public static Polynomial arithmetic(Polynomial p1, Polynomial p2, Operator op) throws ExpressionException {
		//coding...
		Polynomial p3 = new Polynomial();
		if (op.toString().compareTo("+")==0){
			p3 = add(p1,p2);
		}else if (op.toString().compareTo("*")==0){
			p3 = multiplication(p1,p2);
		}else if (op.toString().compareTo("-")==0){
			p2 = multiplication(new Polynomial("1-2"),p2);
			p3 = add(p1,p2);
		}else{
			String integer = "(\\d+)";
			
			Pattern p = Pattern.compile(integer);
			Matcher m = p.matcher(p2.toString());
			
			if (!m.matches()) {
				throw new ExpressionException("Power Non Integer");
			}
			
			int power = Integer.parseInt(p2.toString());
			Polynomial pTempt = new Polynomial(p1.toString());
			
			for(int i = 0 ; i < power-1; i++){
				pTempt = multiplication(pTempt,p1);
			}
			p3 = pTempt;
		}
		return p3;
	}

	@Override
	public String toString() {
		
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
