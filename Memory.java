/*
 * Winslow Conneen
 * COSC 3355 Assignment 1
 * 2.23.2021
 * Objective: create virtual memory that stores instructions
 */

public class Memory {

	private static int [] memoryRegisters = new int [3072];
	
	//retrieves values at given memory location
	public static String fetch(String hexa)
	{
		int memLoc = hexToDec(hexa);
		return decToHex(memoryRegisters[memLoc - 1024]);
	}

	//stores values at given memory location
	public static void store(String hexa, String val)
	{
		int memLoc = hexToDec(hexa);
		memoryRegisters[memLoc - 1024] = hexToDec(val);
	}
	
	//converts hexadecimal to decimal
	public static int hexToDec(String hexa)
	{
		return Integer.decode("0x" + hexa);
	}
	
	//converts decimal to hexadecimal
	public static String decToHex(int deca)
	{
		return Integer.toHexString(deca);
	}
}
