package net.javafiddle.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SystemOutput extends PrintStream {

	public SystemOutput(OutputStream out) {
		super(out, true);
		
		// TODO Auto-generated constructor stub
	}
	
		
	

}
