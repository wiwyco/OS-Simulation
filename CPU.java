/*
 * Winslow Conneen
 * COSC 3355 Assignment 1
 * 2.23.2021
 * Objective: create virtual computer that runs instructions
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class CPU {

	private static String programCounter;
	private static int accumulator;
	private static String instructionRegister;
	private static String generalRegister;
	private static int subNum = 0;
	private static int numProg = 0;
	
	public static String getGeneralRegister()
	{
		return generalRegister;
	}
	
	public static void setGeneralRegister(String generalRegisterX)
	{
		generalRegister = generalRegisterX;
	}
	public static String getProgramCounter() {
		return programCounter;
	}
	public static void setProgramCounter(String programCounterX) {
		programCounter = programCounterX;
	}
	public static int getAccumulator() {
		return accumulator;
	}
	public static void setAccumulator(int accumulatorX) {
		accumulator = accumulatorX;
	}
	public static String getInstructionRegister() {
		return instructionRegister;
	}
	public static void setInstructionRegister(String instructionRegisterX) {
		instructionRegister = instructionRegisterX;
	}
	
	public static void main(String [] args) throws IOException {
		
		//Clear Output file
		PrintWriter writer = new PrintWriter(new File ("winslow_conneen_output.txt"));
		writer.print("");
		writer.close();
		
		//----------------------------------------------------------------------------
		//Begin data extraction from input.txt
		
		String [] script = null;
		
		//create file scanner and populate script with input strings
		try {
			File myObj = new File ("winslow_conneen_input.txt");
			
			int x = 0;
			Scanner myCounter = new Scanner(myObj);
			while (myCounter.hasNextLine())
			{
				x++;
				myCounter.nextLine();
			}
			myCounter.close();
			
			script = new String [x];
			
			Scanner myReader = new Scanner(myObj);
			for(int i = 0; i < x; i++)
			{
				String line = myReader.nextLine();
				script[i] = line;
			}
			myReader.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		//isolate main and subroutine strings and delete all but memory location and instruction values
		
		int x = 0;
		int y = 0;
		while(!(script[y].contains("Subroutine 1")) )
		{
			if(script[y].length() > 0 && script[y].charAt(0) != '=')
			{
				x++;
			}
			y++;
		}
		
		String [] mainProgram = new String [x];
		int i = 0;
		for(int j = 0; j < y; j++)
		{
			if(script[j].length() > 0 && script[j].charAt(0) != '=')
			{
				mainProgram[i] = script[j].substring(4, 12);
				i++;
			}
		}
		
		int last = y;
		x = 0;
		while(!(script[y].contains("Subroutine 2")) )
		{
			if(script[y].length() > 0 && script[y].charAt(0) != '=')
			{
				x++;
			}
			y++;
		}
		
		String [] subroutine1 = new String [x];
		i = 0;
		for(int j = last; j < y; j++)
		{
			if(script[j].length() > 0 && script[j].charAt(0) != '=')
			{
				subroutine1[i] = script[j].substring(3, 11);
				i++;
			}
		}
		
		last = y;
		x = 0;
		while(!(script[y].contains("Memory Data")) )
		{
			if(script[y].length() > 0 && script[y].charAt(0) != '=')
			{
				x++;
			}
			y++;
		}
		
		String [] subroutine2 = new String [x];
		i = 0;
		for(int j = last; j < y; j++)
		{
			if(script[j].length() > 0 && script[j].charAt(0) != '=')
			{
				subroutine2[i] = script[j].substring(3, 11);
				i++;
			}
		}
		
		last = y;
		x = 0;
		while(y < script.length)
		{
			if(script[y].length() > 0 && script[y].charAt(0) != '=')
			{
				x++;
			}
			y++;
		}
		
		String [] memory = new String [x];
		i = 0;
		for(int j = last; j < y; j++)
		{
			if(script[j].length() > 0 && script[j].charAt(0) != '=')
			{
				memory[i] = script[j].substring(3, 11);
				i++;
			}
		}
		
		//END OF DATA EXTRACTION
		//-------------------------------------------------------------------
		
		//run config
		StartingConfig(memory, mainProgram, subroutine1, subroutine2);
		
		//run program
		while(getInstructionRegister().charAt(0) != 'F' || getInstructionRegister().charAt(0) != 'f')
		{
			if(getInstructionRegister().charAt(0) == 'f')
			{
				break;
			}
			ALU(getInstructionRegister());
			int num = hexToDec(getProgramCounter());
			num++;
			setProgramCounter(decToHex(num));
		}
		
		//print main program
		printState(false, 0);
		
	}
	
	//StartingConfig sets the configuration to the instruction set in the input
	public static void StartingConfig(String memory[], String mainProgram[], String subroutine1[], String subroutine2[])
	{
		//set memory locations (940,941,942)
		String [] memLoc = new String [memory.length];
		String [] memData = new String [memory.length];
		
		for(int i = 0; i < memory.length; i++)
		{
			memLoc[i] = memory[i].substring(0,3);
			memData[i] = memory[i].substring(4);
			
			Memory.store(memLoc[i], memData[i]);
		}
		
		//set main instruction set at specified location
		String [] mainLoc = new String [mainProgram.length];
		String [] mainData = new String [mainProgram.length];
		
		for(int i = 0; i < mainProgram.length; i++)
		{
			mainLoc[i] = mainProgram[i].substring(0,3);
			mainData[i] = mainProgram[i].substring(4);
			
			Memory.store(mainLoc[i], mainData[i]);
		}
		
		//set IR and PC
		CPU.setInstructionRegister(mainData[0]);
		CPU.setProgramCounter(mainLoc[0]);
		
		//set Sub 1 instruction set at specified location
		String [] sub1Loc = new String [subroutine1.length];
		String [] sub1Data = new String[subroutine1.length];
		
		for(int i = 0; i < subroutine1.length; i++)
		{
			sub1Loc[i] = subroutine1[i].substring(0,3);
			sub1Data[i] = subroutine1[i].substring(4);
			
			Memory.store(sub1Loc[i], sub1Data[i]);
		}
		
		//set Sub 2 instruction set at specified location
		String [] sub2Loc = new String [subroutine2.length];
		String [] sub2Data = new String[subroutine2.length];
		
		for(int i = 0; i < subroutine2.length; i++)
		{
			sub2Loc[i] = subroutine2[i].substring(0,3);
			sub2Data[i] = subroutine2[i].substring(4);
			
			Memory.store(sub2Loc[i], sub2Data[i]);
		}
	}
	
	//ALU executes opcodes on data in the instruction register
	public static void ALU(String instruction) throws IOException

	
	{
		String data = instruction.substring(1);
		char opcode = instruction.charAt(0);
		
		switch (opcode)
		{
		//Load AC from memory
		case '1':
			
			setAccumulator(hexToDec(Memory.fetch(data)));
			System.out.println("Accumulator set as " + getAccumulator());
			
			break;
		//Store AC to memory
		case '2':
			
			Memory.store(data, decToHex(getAccumulator()));
			System.out.println("Accumulator value, " + getAccumulator() + ", was stored in memory at location " + data);
			
			break;
		//Load AC from REG
		case '3':
			
			setAccumulator(hexToDec(getGeneralRegister()));
			System.out.println("Register sets accumulator at " + getAccumulator());
			
			break;
		//Store AC to REG
		case '4':
			
			setGeneralRegister(decToHex(getAccumulator()));
			System.out.println("Accumulator value, " + getAccumulator() + " is stored to register");
			
			break;
		//Add to AC from memory
		case '5':
			
			setAccumulator(getAccumulator() + hexToDec(Memory.fetch(data)));
			System.out.println("Accumulator now set to " + getAccumulator());
			
			break;
		//Load REG with operand
		case '6':
			
			setGeneralRegister(data);
			System.out.println("General register set to " + getGeneralRegister());
			
			break;
		//Add REG to AC
		case '7':
			
			setAccumulator(getAccumulator() + hexToDec(getGeneralRegister()));
			System.out.println("Accumulator + Gneral Register = " + getAccumulator());
			
			break;
		//Multiply REG to AC
		case '8':
			
			setAccumulator(getAccumulator() * hexToDec(getGeneralRegister()));
			System.out.println("Accumulator * General Registers = " + getAccumulator());
			
			break;
		//Subtract REG from AC
		case '9':
			
			setAccumulator(getAccumulator() - hexToDec(getGeneralRegister()));
			System.out.println("Accumulator - General Registers = " + getAccumulator());
			
			break;
		//Divide AC by REG value (integer division)
		case 'a':
			
			setAccumulator(getAccumulator() / hexToDec(getGeneralRegister()));
			System.out.println("Accumulator / General Registers = " + getAccumulator());
			
			break;
		//Jump to subroutine
		case 'b':
			
			subNum++;
			
			Stack.setAc(getAccumulator());
			Stack.setReg(getGeneralRegister());
			Stack.setIr(getInstructionRegister());
			Stack.setPc(getProgramCounter());
			
			System.out.println(data);
			setProgramCounter(data);
			System.out.println("Subroutine began");
			
			break;
		//return from subroutine
		case 'c':
			
			printState(true, subNum);
			
			setAccumulator(Stack.getAc());
			setGeneralRegister(Stack.getReg());
			setInstructionRegister(Stack.getIr());
			setProgramCounter(Stack.getPc());
			Stack.setAc(0);
			Stack.setReg("0");
			Stack.setIr("0");
			Stack.setPc("0");
			
			System.out.println("Subroutine ended");
			
			break;
		//Halt
		case 'f':

			System.out.println("End of program");
			
			break;
		}
		
		numProg++;
		setInstructionRegister(Memory.fetch(getProgramCounter()));
	}
	
	//Print state prints the state of the computer to the output file
	public static void printState(boolean sub, int subNum)
	throws IOException
	{
			FileWriter fw = new FileWriter("winslow_conneen_output.txt", true);
			PrintWriter pw = new PrintWriter(fw);
			
			if(sub == true)
			{
				pw.println("======Before Return from Subroutine " + subNum + " Status======");
			}
			else
			{
				pw.println("======End of Program Status======");
			}
			
			pw.println("=============Stack Status=============");
			if(Stack.getAc() == 0 && Stack.getIr().equals("0") && Stack.getPc().equals("0") && Stack.getReg().equals("0"))
			{
				pw.println("No Data in Stack!\n");
			}
			else
			{
				pw.println("Stack contents at 3FC = " + Stack.getAc());
				pw.println("Stack contents at 3FD = " + Stack.getReg());
				pw.println("Stack contents at 3FE = " + Stack.getIr());
				pw.println("Stack contents at 3FF = " + Stack.getPc());
			}
			pw.println("=============Registers & Memory Status=============");
			if(sub == true)
			{
				pw.println("PC = " + decToHex(hexToDec(getProgramCounter()) - 1));
			}
			else
			{
				pw.println("PC = " + getProgramCounter());
			}
			pw.println("IR = " + getInstructionRegister());
			pw.println("AC = " + decToHex(getAccumulator()));
			pw.println("REG = " + getGeneralRegister());
			pw.println("Memory 940 = " + Memory.fetch("940"));
			pw.println("Memory 941 = " + Memory.fetch("941"));
			pw.println("Memory 942 = " + Memory.fetch("942"));
			pw.println("Number of instructions executed = " + numProg);
			pw.println(" ");
			pw.close();
	}

	//converts String hex to int dec
	public static int hexToDec(String hexa)
	{
		return Integer.decode("0x" + hexa);
	}
	
	//converts int dec to String hex
	public static String decToHex(int deca)
	{
		return Integer.toHexString(deca);
	}
}

