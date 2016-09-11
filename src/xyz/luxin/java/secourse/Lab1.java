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


class ExpressionTree {

	public Expression exp;
	public ExpressionTree left, right;//, father;
	
	public ExpressionTree() {
		this.exp = null;
		this.left = null;
		this.right = null;
		//this.father = null;
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
			}
		}
		return null;
	}
	
	public static void createTree(ExpressionTree t, String expString) throws ExpressionException {
		
		//System.out.println(expString);
		
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
			//t.exp = new Polynomial(expString);
			throw new ExpressionException("Format Error");
		} else {
			t.exp = new Operator(chars[opIndex]);
			t.left = new ExpressionTree();
			t.right = new ExpressionTree();
			//t.left.father = t;
			//t.right.father = t;
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
	
	public Monomial(String expString, boolean isExtraNegative, boolean isNegEx) throws ExpressionException {
		
		if (!isNegEx) {
			//check character
			Pattern p0 = Pattern.compile("[^a-zA-Z0-9\\+\\-\\*\\^\\(\\)\\s]");
			Matcher m0 = p0.matcher(expString);	
			if (m0.find()) {
				throw new ExpressionException("Unknown Character");
			}
		}
		
		constVaule = 1;
		varNumber = 0;
		varIndex = new TreeMap<String, Integer>();
		monIndex = 0;
		
		if (isExtraNegative) {
			constVaule = -1;
		}
		
		String pFactor = "(#|(\\d+\\^\\d+)|([a-zA-Z]+\\^\\d+)|(\\d+)|([a-zA-Z]+))";
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
			if (chars[0]=='#') {
				constVaule *= -1;
			} else if (chars[0]>='0' && chars[0]<='9') {
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
	
	public Monomial(String expString, boolean isExtraNegative) throws ExpressionException {
		//Does not support "Negative Extension"(#) by default.
		this(expString, isExtraNegative, false);
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
	
	public Monomial multiplication(Monomial a){//////////////////////////////////////////////////////////
		Monomial result1 = new Monomial();
		result1.constVaule = this.constVaule*a.constVaule;
		result1.monIndex = 0;
		TreeMap<String, Integer>result = new TreeMap<String, Integer>();

		Iterator<Entry<String, Integer>> it = this.varIndex.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it.next();
			String m = entry.getKey();
			int nAll = this.varIndex.get(m);
			result1.monIndex += nAll;
			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				nAll += n;
				result.remove(m);
			}
			if (nAll != 0) {
				result.put(m, nAll);
			}
		}

		//System.out.println(a.toString());
		Iterator<Entry<String, Integer>> it1 = a.varIndex.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>)it1.next();
			String m = entry.getKey();
			int nAll = a.varIndex.get(m);
			result1.monIndex += nAll;
			if (result.containsKey(m)) {
				//Map中已存在的单项式与get参数的单项式不是一个对象，两者仅系数不同
				Integer n = result.get(m);
				nAll += n;
				result.remove(m);
			}
			if (nAll != 0) {
				result.put(m, nAll);
			}
		}
		
		result1.varNumber = result.size();
		result1.varIndex = result;


		return result1;
		
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
		
//		System.out.println(expString);
//		throw new ExpressionException("Break Point");
		
		ExpressionTree tree = new ExpressionTree();
		ExpressionTree.createTree(tree, expString);
		
		System.out.print("midOrder: ");
		ExpressionTree.midOrder(tree);
		System.out.println();
		
		System.out.print("preOrder: ");
		ExpressionTree.preOrder(tree);
		System.out.println();
		
		System.out.print("lastOrder: ");
		ExpressionTree.lastOrder(tree);
		System.out.println();
		
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
			/*
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
			*/
			
//			String a = "	# ";
//			Pattern p1 = Pattern.compile("\\s*(#)\\s*");
//			Matcher m1 = p1.matcher(a);
//			if (m1.matches()) {
//				System.out.println(m1.group(1));
//				return;
//			}
			
			
			Polynomial poly2 = new Polynomial();
			//poly2.expressionBracket("2	+7x7yt(x+	 t(6+9(7*9)) + 6 	(g-6)(2-(z-x)+6(x-z))(c+c))");
			//poly2.expressionBracket("2x (x+y)+2x^3y(x^2	+(y+3x)*(2y)(3y))");
			//poly2.expressionBracket("2x (x+y)+2x*x*x*y(x*x	+(y+3x)*(2y)(3y))");
			//poly2.expressionBracket("x(x*x	+(y+3x)*(2y)(3y))");
			poly2.expressionBracket("x^2	+(y+3x)*(2y)(3y*(x+y+z+3x))");
			System.out.println();
			System.out.println(poly2);

		} catch (ExpressionException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return;
		}
		
	}
	
}
