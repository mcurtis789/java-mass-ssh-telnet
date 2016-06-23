package com.ccg.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Michael Curtis
 */
public class Main {
		
        public static void main (String[] args) throws IOException {
        	String username = null;
        	String password = null;
        	Scanner keyboard = new Scanner(System.in);
        	System.out.println("enter Username");
        	username = keyboard.nextLine();
        	System.out.println("enter Password");
        	password = keyboard.nextLine();
        	keyboard.close();
        	String hostListFileName = "hostList.txt";
        	String commandListFileName = "commandList.txt";
        	String line = null;
        	ArrayList<String> hostList = new ArrayList<>();
        	ArrayList<String> commandList = new ArrayList<>();
        	
        try {
        	FileReader hostListReader = new FileReader(hostListFileName);
        	BufferedReader hostListBufferedReader = new BufferedReader(hostListReader);
        	while((line = hostListBufferedReader.readLine()) != null ) {
        		hostList.add(line);
        	}
        	hostListBufferedReader.close();
        	ExecutorService executor = Executors.newFixedThreadPool(hostList.size());
           	FileReader commandListListReader = new FileReader(commandListFileName);
        	BufferedReader commandListBufferedReader = new BufferedReader(commandListListReader);  
        	while((line = commandListBufferedReader.readLine()) != null ) {
        		commandList.add(line);
        	}
        	commandListBufferedReader.close();
        	System.out.println("Starting Processes");
        	
        	for (int i = 0; i < hostList.size(); i++) {
            	String host = hostList.get(i);
            	Runnable worker = new MyRunnable(host,username,password,commandList);
            	executor.execute(worker);
        	}
        	executor.shutdown();
        	System.out.println("all Processes started waiting for \"isTerminated\"\n"
        			+ "depending your commands this prompt may sit here forever.\n"
        			+ "it may be necessary to force quit this application as a result.");
        	// Wait until all threads are finish
        	while (!executor.isTerminated()) {
            	System.out.print("\\\r");
            	Thread.sleep(100);
            	System.out.print("|\r");
            	Thread.sleep(100);
            	System.out.print("/\r");
            	Thread.sleep(100);
            	System.out.print("-\r");
            	Thread.sleep(100);
            	
        	}
        }
        catch (IOException ioe){
        	System.out.println(ioe);
        	System.out.println("attempting to create needed files\n" + hostListFileName +"\n"+ commandListFileName);
            PrintWriter commandListwriter = new PrintWriter("commandList.txt", "UTF-8");
        	commandListwriter.println("term length 0 ");
        	commandListwriter.println("show run");
        	commandListwriter.println("exit");
        	commandListwriter.close();
            PrintWriter hostListwriter = new PrintWriter("hostList.txt", "UTF-8");
        	hostListwriter.println("10.10.10.1");
        	hostListwriter.println("10.10.9.1");
        	hostListwriter.close();
        }
        catch (Exception e){
        	System.out.println(e);
        }
    }
 
  
    public static class MyRunnable implements Runnable {
    	private final String hostname;
        private final String username;
        private final String password;
        private final ArrayList<String> commandsList;
        
        MyRunnable(String hostname, String username, String password,ArrayList<String> commands) {
          	this.hostname = hostname;
            this.username = username;
            this.password = password;
            this.commandsList = commands;

        }
    
        @Override
        public void run() {
            String user = username;
            String host = hostname;
            JSch jsch=new JSch();
            Date date = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
            System.out.println("Status: " + hostname + "\t" + "Starting\t");
            try {
              Session session=jsch.getSession(user, host, 22);
              session.setPassword(password);
              session.setConfig("StrictHostKeyChecking", "no");
              session.setTimeout(30000);;
              session.connect();
              Channel channel=session.openChannel("shell");
              //Create filename 
              File file = new File(host + "-" + ft.format(date) + ".log");
              FileOutputStream fos = new FileOutputStream(file, true);
              PrintStream ps = new PrintStream(fos);
              //sets system.out to write to text file
              
              OutputStream inputstream_for_the_channel = channel.getOutputStream();
              PrintStream commander = new PrintStream(inputstream_for_the_channel, true);
              channel.setOutputStream(ps, true);
              channel.connect();
              for (int i = 0; i < commandsList.size(); i++) {
            	  String command = commandsList.get(i);
            	  commander.println(command);    
              }
              System.out.println("Status: " + hostname + "\t" + "Running\t");
              do {
                Thread.sleep(1000);
              } 
              while(!channel.isEOF());
                session.disconnect();
                commander.close();
                System.out.println("Status: " + hostname + "\t" + "Done\t");
            }
            catch (Exception e){
                System.out.println(host + e);
            }
            
        }    
    }
}



