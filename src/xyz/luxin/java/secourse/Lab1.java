package xyz.luxin.java.secourse;

class ExpressionTree {
	
	public String value;
	public ExpressionTree left, right;
	
	public ExpressionTree() {
		this.value = "";
		this.left = null;
		this.right = null;
	}
	
	public static boolean checkCharacter(String exp) {
		
		char[] chars = exp.toCharArray();
		
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
	
	public static void createTree(ExpressionTree t, String exp) throws Exception {

		char[] chars = exp.toCharArray();
		
		if (chars.length == 1) {
			if (chars[0]=='+' || chars[0]=='*') {
				throw new Exception();
			} 
			//it is leaf
			t.value = exp;
			return;
		}
		
		if (chars.length == 2) {
			if (!(chars[0]>='0'&&chars[0]<='9'&&chars[1]>='0'&&chars[1]<='9')) {
				throw new Exception();
			}
			//it is leaf
			t.value = exp;
			return;
		}
		

		//Find middle near +
		
		int i = chars.length/2;
		int j = chars.length/2;

		while (i>=0 || j<=chars.length-1) {
			
			if (i>=0 && chars[i]=='+') {
				if (i==0) {
					throw new Exception();
				} else {
					t.value = "+";
					t.left = new ExpressionTree();
					t.right = new ExpressionTree();
					createTree(t.left, new String(chars, 0, i));
					createTree(t.right, new String(chars, i+1, chars.length-i-1));
					return;
				}
			}

			if (j<=chars.length-1 && chars[i]=='+') {
				if (j==chars.length-1) {
					throw new Exception();
				} else {
					t.value = "+";
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
		t.value = exp;
		return;
	}
	
	public static void preOrder(ExpressionTree t) {
		if(null != t) {
			System.out.print(t.value + " ");
			preOrder(t.left);
			preOrder(t.right);
		}
	}
	
	public static void midOrder(ExpressionTree t) {
		if(null != t) {
			midOrder(t.left);
			System.out.print(t.value + " ");
			midOrder(t.right);
		}
	}

	public static void lastOrder(ExpressionTree t) {
		if(null != t) {
			lastOrder(t.left);
			lastOrder(t.right);
			System.out.print(t.value + " ");
		}
	}
}

public class Lab1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ExpressionTree root = new ExpressionTree();
		try {
			ExpressionTree.createTree(root, "2+3*x*y+4+x*x*y+y");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Wrong!");
			return;
		}
		
		System.out.println("OK!");
		
		ExpressionTree.preOrder(root);
		System.out.println("");
		
		ExpressionTree.midOrder(root);
		System.out.println("");
		
		ExpressionTree.lastOrder(root);
		System.out.println("");
		
		System.out.println("Yes!");
	}

}
