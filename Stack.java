/*
 * Winslow Conneen
 * COSC 3355 Assignment 1
 * 2.23.2021
 * Objective: create virtual state memory that stores processor state
 */

public class Stack {

	private static int ac;
	private static String reg;
	private static String ir;
	private static String pc;
	
	
	public static int getAc() {
		return ac;
	}

	public static void setAc(int ac) {
		Stack.ac = ac;
	}

	public static String getReg() {
		return reg;
	}

	public static void setReg(String reg) {
		Stack.reg = reg;
	}

	public static String getIr() {
		return ir;
	}

	public static void setIr(String ir) {
		Stack.ir = ir;
	}

	public static String getPc() {
		return pc;
	}

	public static void setPc(String pc) {
		Stack.pc = pc;
	}
}
