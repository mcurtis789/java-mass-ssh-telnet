package com.ccg.main;
/*
*  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
*  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
*  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
*  INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
*  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
*  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
*  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.*;
import java.util.*;
import java.text.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 *
 * @author Michael Curtis
 */
public class ThreadControl {
		
        public void RunMain (String username, String password, String[] hosts, String[] commands,String Operation) throws IOException {

        	String line = null;
        	ArrayList<String> hostList = new ArrayList<>();
        	ArrayList<String> commandList = new ArrayList<>();
        	
        try {

        	for(int i =0;i < hosts.length;i++) {
        		hostList.add(hosts[i]);
        	}

        	ExecutorService executor = Executors.newFixedThreadPool(hostList.size());
           	for(int i=0;i<commands.length;i++) {
        		commandList.add(commands[i]);
        	}
           	System.out.println(Arrays.toString(commands));
           	System.out.println("Starting Processes");
        	System.out.println("all Processes started. \n"
        			+ "depending your commands this proccess may not teminate.\n"
        			+ "it may be necessary to quit this application as a result.");
        	// Wait until all threads are finish
           	if (Operation.equals("SSH")){
	        	for (int i = 0; i < hostList.size(); i++) {
	            	String host = hostList.get(i);
	            	Runnable worker = new SSHMyRunnable(host,username,password,commandList);
	            	executor.execute(worker);
	        	}
	        	executor.shutdown();
	        	while (!executor.isTerminated()) {
	        	    
	        	    MainWindow.lblStatus.setText("Running: \\\r");
	        	    Thread.sleep(100);
	        	    
	        	    MainWindow.lblStatus.setText("Running: |\r");
	        	    Thread.sleep(100);
	        	    
	        	    MainWindow.lblStatus.setText("Running: /\r");
	        	    Thread.sleep(100);
	        	    
	        	    MainWindow.lblStatus.setText("Running: -\r");
	        	    Thread.sleep(100);
	        	    
	        	}
	        	MainWindow.lblStatus.setText("Done!");
	    	    MainWindow.btnRun.setEnabled(true);
        	}if (Operation.equals("Telnet")){
	        	for (int i = 0; i < hostList.size(); i++) {
	            	String host = hostList.get(i);
	            	Runnable worker = new TELNETMyRunnable(host,username,password,commandList);
	            	executor.execute(worker);
	        	}
	        	executor.shutdown();
	        	while (!executor.isTerminated()) {
	        	    
	        	    MainWindow.lblStatus.setText("Running: \\\r");
	        	    Thread.sleep(100);
	        	    
	        	    MainWindow.lblStatus.setText("Running: |\r");
	        	    Thread.sleep(100);
	        	    
	        	    MainWindow.lblStatus.setText("Running: /\r");
	        	    Thread.sleep(100);
	        	    
	        	    MainWindow.lblStatus.setText("Running: -\r");
	        	    Thread.sleep(100);
	        	    
	        	}
	        	MainWindow.lblStatus.setText("Done!");
	    	    MainWindow.btnRun.setEnabled(true);
        	}
        }
        catch (Exception e){
        	System.out.println(e);
        	
        }
        
    }
 
  
    public static class SSHMyRunnable implements Runnable {
    	private final String hostname;
        private final String username;
        private final String password;
        private final ArrayList<String> commandsList;
        
        SSHMyRunnable(String hostname, String username, String password,ArrayList<String> commands) {
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
    public static class TELNETMyRunnable implements Runnable {
    	private final String hostname;
        private final String username;
        private final String password;
        private final ArrayList<String> commandsList;
        
        TELNETMyRunnable(String hostname, String username, String password,ArrayList<String> commands) {
          	this.hostname = hostname;
            this.username = username;
            this.password = password;
            this.commandsList = commands;

        }
    
        @Override
        public void run() {
	    	Telnet t = new Telnet();
	    	t.TelnetMain(username, password, hostname, commandsList);
        }       
    }
}