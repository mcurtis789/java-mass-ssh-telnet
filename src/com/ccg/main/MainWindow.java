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
/*
*
* @author Michael Curtis
*/
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;import java.io.PrintStream;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.ccg.main.lib.CustomOutputStream;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class MainWindow {

	public JFrame frame;
	
	public static JLabel lblStatus = new JLabel("Not Running");
	
	public static JButton btnRun = new JButton("Run");

	private JTextField textFieldUsername;
	private JPasswordField passwordField;

	private String username = "";
	private String password = "";
	private String Operation = "SSH";
	
	private String commands[];
	private String hosts[];
	private final ButtonGroup buttonGroup = new ButtonGroup();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
				
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 939, 369);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog (null, "THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,\n"
						+ "INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND\n"
						+ "FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,\n"
						+ "INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
						+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT\n"
						+ "LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,\n"
						+ "OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF\n"
						+ "LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n"
						+ "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,\n"
						+ "EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n"
						, "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmAbout);
		frame.getContentPane().setLayout(null);
		
		JTabbedPane tabbedPaneMain = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPaneMain);
		tabbedPaneMain.setBounds(0, 0, 729, 310);
		
		JPanel panelCommands = new JPanel();
		tabbedPaneMain.addTab("Commands", null, panelCommands, null);
		panelCommands.setLayout(null);
		
		JTextPane textPaneCommands = new JTextPane();
		textPaneCommands.setText("term len 0\r\nshow run\r\nexit");
		textPaneCommands.setBounds(0, 5, 714, 277);
		panelCommands.add(textPaneCommands);
		
		JPanel panelHosts = new JPanel();
		tabbedPaneMain.addTab("Hosts", null, panelHosts, null);
		panelHosts.setLayout(null);
		
		JTextPane textPaneHosts = new JTextPane();
		textPaneHosts.setText("10.10.10.1\r\n10.10.9.1\r\n10.10.8.1\r\n");
		textPaneHosts.setBounds(0, 0, 724, 282);
		panelHosts.add(textPaneHosts);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPaneMain.addTab("Console", null, scrollPane, null);
		
		JTextArea textAreaConsole = new JTextArea();
		scrollPane.setViewportView(textAreaConsole);
		PrintStream printStream = new PrintStream(new CustomOutputStream(textAreaConsole)); 
		System.setErr(printStream);
		System.setOut(printStream);
		
		JPanel panelConnectionInfo = new JPanel();
		panelConnectionInfo.setBounds(725, 0, 198, 310);
		frame.getContentPane().add(panelConnectionInfo);
		panelConnectionInfo.setLayout(null);
		
		JTextPane textPane = new JTextPane();
		textPane.setBounds(49, 6, 6, 20);
		panelConnectionInfo.add(textPane);
		
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread(new UserMyRunnable());
				String strcommands[] = textPaneCommands.getText().split("\\r\\n");
				String strhosts[] = textPaneHosts.getText().split("\\r\\n");
				commands = strcommands;
				hosts = strhosts;
				try {
					btnRun.setEnabled(false);
					tabbedPaneMain.setSelectedIndex(2);
					t.start();
				} catch (Exception e1) {
					System.out.println(e1);
				}
			}
		});
		btnRun.setBounds(92, 276, 89, 23);
		panelConnectionInfo.add(btnRun);
		
		lblStatus.setBounds(10, 280, 72, 14);
		panelConnectionInfo.add(lblStatus);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(10, 25, 72, 14);
		panelConnectionInfo.add(lblUsername);
		
		textFieldUsername = new JTextField();
		textFieldUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				username = textFieldUsername.getText();
			}
		});
		textFieldUsername.setBounds(80, 22, 108, 20);
		panelConnectionInfo.add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 50, 73, 14);
		panelConnectionInfo.add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				password = String.valueOf(passwordField.getPassword());
			}
		});
		passwordField.setBounds(80, 47, 108, 20);
		panelConnectionInfo.add(passwordField);
		
		JRadioButton rdbtnTelnet = new JRadioButton("Telnet");
		buttonGroup.add(rdbtnTelnet);
		rdbtnTelnet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Operation="Telnet";
			}
		});
		rdbtnTelnet.setBounds(6, 71, 109, 23);
		panelConnectionInfo.add(rdbtnTelnet);
		
		JRadioButton rdbtnSsh = new JRadioButton("SSH");
		buttonGroup.add(rdbtnSsh);
		rdbtnSsh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Operation="SSH";
			}
		});
		rdbtnSsh.setBounds(6, 89, 109, 23);
		panelConnectionInfo.add(rdbtnSsh);
		frame.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{tabbedPaneMain, scrollPane, textAreaConsole, panelConnectionInfo, textPane}));
	}
	public class UserMyRunnable extends SwingWorker<List<Object>, Void> {
	    private List<Object> list;
	    public UserMyRunnable(){
	    	
    	}

	    @Override
	    public List<Object> doInBackground() throws Exception {
	    	//SSH sm = new SSH();
	    	//sm.RunSSHMain(username,password,hosts,commands);
	    	ThreadControl tc =new ThreadControl();
	    	tc.RunMain(username, password, hosts, commands, Operation);
	    	
	    	return list;
	    }

	    @Override
	    public void done() {
	    	// Update the GUI with the updated list.
	    	
	    }
	}
}
