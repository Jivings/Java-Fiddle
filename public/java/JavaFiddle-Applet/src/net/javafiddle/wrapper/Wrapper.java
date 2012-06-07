package net.javafiddle.wrapper;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.awt.BorderLayout;

public final class Wrapper extends Applet {
	public Wrapper() {
	}

	private static final long serialVersionUID = 7975053053984139634L;

	final TextArea output = new TextArea();
	
	
	public void init() {
		
		final String[] args;
		final String classname = getParameter("classname");       
		final String argList = getParameter("args");
		if (argList != null) {
			args = argList.split(",");
		}
		
				
		System.setOut(new SystemOutput(new OutputStream() {
		    @Override
			public void write(int b) throws IOException {
		    	print(b);
			}
		}));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		add(new Button("Load"){;
			@Override
			public boolean action(Event evt, Object what) {
				try {
					loadClass("http://ivings.org.uk/~james/classload/", "HelloWorld");
				} catch(Exception e) {
					e.printStackTrace();
				}
				return true;
				
			}
		}, c);
		c.gridy = 1;
		output.setEditable(false);
		add(output, c);
    }
	
	private void print(int b) {
		char c = (char) b;
		String o = output.getText().concat(Character.toString(c));
		output.setText(o);
	}
	
	private void loadClass(String id, String classname) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException { 
		
		URLClassLoader loader = new URLClassLoader(new URL[] { new URL(id) });
	    Class<?> c = loader.loadClass (classname);

	    Method main = c.getMethod("main", String[].class);
	    String[] args = new String[]{"TestArg"};
	    main.invoke(null, (Object) args );
	}
}
