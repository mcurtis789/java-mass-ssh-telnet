/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 *
 * @author Michael Curtis
 */
package com.ccg.main;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.net.telnet.TelnetClient;

public class Telnet {
	private TelnetClient tc = new TelnetClient();
	private InputStream in;
	private PrintStream out;
	private File file;
	private FileOutputStream fos;
	private PrintStream ps;
	
	private String prompt = "#";
	private String hostname ;
	private String hosts;
	private boolean discoverHost=false;
	private boolean boolDisconnect=false;
	Date date = new Date();
    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
    
    
	public void TelnetMain(String username, String password, String hosts, ArrayList<String> commandsList) {
		try {
			this.hosts=hosts;
			System.out.println("Status: " + hosts + "\t" + "Starting\t");
			// Connect to the specified server
            tc.connect(hosts, 23);
            tc.setKeepAlive(true);
            
            // Get input and output stream references
			file = new File(hosts + "-" + ft.format(date) + ".log");
            fos = new FileOutputStream(file, true);
            ps = new PrintStream(fos);
			in = tc.getInputStream();
			out = new PrintStream(tc.getOutputStream());
			
			// Log the user on
			readUntil("Username: ");
			write(username);
			readUntil("Password: ");
			write(password);
			// Advance to a prompt
			if(discoverHost==false){
				readUntil(prompt);
				this.discoverHost=true;
				write(" ");
			}
			//readUntil(hostname);
			System.out.println("Status: " + hosts + "\t" + "Running\t");
			for(int i=0;i<commandsList.size();i++){
				if(commandsList.get(i).toLowerCase().contains("exit")){
					disconnect();
				}else {
					sendCommand(commandsList.get(i));
					sendCommand(" ");	
				}
			}
			while(in.read() !=-1){
				//Thread.sleep(100);
				readUntil(hostname);
			}
			if (tc.isConnected()==false){
					
			}else{
				disconnect();
			}
		}catch (Exception e) {
			if (e.toString().contains("Login invalid")){
				System.out.println("Status: " + hosts + "\t" +"Login invalid\t");
			}else{
				e.printStackTrace();
			}
			disconnect();
		}	
	}
		
	public String readUntil(String pattern) throws Exception {
		try {
			char lastChar = pattern.charAt(pattern.length()- 1); //
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			while (true) {
				sb.append(ch);
				if (sb.toString().contains("% Login invalid")) {
					boolDisconnect=true;
					throw new WordContainsException("Login invalid");

				}
				if (sb.toString().contains("% Username:  timeout expired")||
					sb.toString().contains("% Password:  timeout expired")) {
					boolDisconnect=true;
					throw new WordContainsException("Login invalid");
				}
				//if(){
				//	throw new WordContainsException("Input Stream Closed");
				//}
				if(ch=='#'&&discoverHost==false){
					this.hostname=(sb.toString().substring(2, sb.length()));
				}
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						ps.flush();
						fos.flush();
						out.flush();
						return sb.toString();
					}
				}
				ch = (char) in.read();
				ps.print(ch);
			}
		}catch (Exception e) {
			throw new WordContainsException(e.toString());
			
		}
	}
		
	public void write(String value) {
		try {
			out.println(value);
			out.flush();
			//System.out.println(value);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public String sendCommand(String command) {
		try {
			write(command);
			return readUntil(hostname);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public void disconnect() {
		try {
			boolDisconnect=true;
			in.close();
			ps.close();
			fos.close();
			ps.close();
			out.close();
			tc.disconnect();
			System.out.println("Status: " + this.hosts + "\t" + "Done\t");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean getStatus() throws WordContainsException {
		boolean value = true ;
		try {
			sendCommand(" ");
		}catch (Exception e) {
			value = false;
			throw new WordContainsException("Status Check Failed");
		}
		return value;
	}
    class WordContainsException extends Exception
	{
	      //Parameterless Constructor
	      public WordContainsException() {}

	      //Constructor that accepts a message
	      public WordContainsException(String message)
	      {
	         super(message);
	      }
	 }
} 
