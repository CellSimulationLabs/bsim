package BSimGUI;

import java.awt.BorderLayout;
import java.lang.Object;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DebugGraphics;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.SpinnerListModel;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
//import javax.vecmath.Vector3d;

import java.io.*;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class BSimGUI extends javax.swing.JFrame {
	
	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private JMenuBar jMenuBar1;
	private JButton writeButton;
	private JPanel jPanel26;
	private JTextField jTextField40;
	private JTextField jTextField39;
	private JTextField jTextField38;
	private JLabel jLabel48;
	private JTextField jTextField37;
	private JLabel jLabel47;
	private JTextArea productionRateTextArea;
	private JTextPane jTextPane16;
	private JTextArea drawerTextArea;
	private JTextArea initialConditionsTextArea;
	private JTextPane initialConditionsCaption;
	private JTextPane signallingchemicalCaption;
	private JFrame jFrame1;
	private JFileChooser jFileChooser3;
	private JDialog loggerFileChooser;
	private JButton jButton21;
	private JButton jButton20;
	private JTextField jTextField36;
	private JPanel jPanel54;
	private JLabel jLabel46;
	private JPanel jPanel53;
	private JPanel jPanel52;
	private JPanel jPanel51;
	private JDialog loggerDialog;
	private JFileChooser jFileChooser2;
	private JDialog movieFileChooser;
	private JButton jButton19;
	private JTextField jTextField35;
	private JLabel jLabel45;
	private JTextField jTextField34;
	private JLabel jLabel44;
	private JTextField jTextField33;
	private JTextField jTextField32;
	private JTextField jTextField31;
	private JLabel jLabel43;
	private JLabel jLabel42;
	private JLabel jLabel41;
	private JButton jButton18;
	private JPanel jPanel50;
	private JPanel jPanel49;
	private JPanel jPanel48;
	private JPanel jPanel47;
	private JDialog movieExportDialog;
	private JButton jButton17;
	private JPanel jPanel46;
	private JTextField jTextField30;
	private JTextField jTextField29;
	private JLabel jLabel40;
	private JLabel jLabel39;
	private JPanel jPanel45;
	private JPanel jPanel44;
	private JPanel jPanel43;
	private JDialog pictureExportDialog;
	private JComboBox jComboBox19;
	private JTextPane numEqCaption;
	private JTextField numEqInputField;
	private JTextArea parametersInputTextArea;
	private JTextPane parametersCaption;
	private JTextArea odeInputTextArea;
	private JTextPane odeCaption;
	private JButton odeSignallingDialogNextButton;
	private JDialog odeSignallingDialog;
	private JTextField jTextField26;
	private JComboBox jComboBox18;
	private JLabel jLabel38;
	private JTextField jTextField28;
	private JLabel jLabel37;
	private JTextField jTextField27;
	private JLabel jLabel36;
	private JPanel jPanel39;
	private JPanel jPanel32;
	private JButton jButton15;
	private JTextField jTextField25;
	private JTextPane jTextPane10;
	private JTextField jTextField24;
	private JTextPane jTextPane9;
	private JTextPane jTextPane8;
	private JTextField jTextField23;
	private JTextPane jTextPane7;
	private JComboBox jComboBox15;
	private JPanel jPanel42;
	private JComboBox jComboBox14;
	private JRadioButton jRadioButton3;
	private JButton jButton14;
	private JTextPane jTextPane2;
	private JComboBox jComboBox17;
	private JPanel jPanel41;
	private JCheckBox jCheckBox2;
	private JPanel jPanel40;
	private JCheckBox jCheckBox3;
	private JPanel jPanel38;
	private JRadioButton jRadioButton4;
	private JTextPane jTextPane6;
	private JTextField jTextField14;
	private JTextPane jTextPane1;
	private JPanel jPanel37;
	private JCheckBox jCheckBox1;
	private JPanel jPanel36;
	private JComboBox jComboBox2;
	private JPanel jPanel35;
	private JRadioButton jRadioButton2;
	private JRadioButton jRadioButton1;
	private JTextPane jTextPane5;
	private JTextPane jTextPane3;
	private JComboBox jComboBox16;
	private JComboBox recieverFieldChoice;
	private JPanel jPanel34;
	private JCheckBox chemicalRecieverCheck;
	private JPanel jPanel33;
	private JDialog addBacteriaDialog;
	private JComboBox jComboBox13;
	private JLabel jLabel35;
	private JButton jButton13;
	private JTextField jTextField12;
	private JTextField jTextField10;
	private JLabel jLabel34;
	private JLabel jLabel33;
	private JPanel jPanel31;
	private JPanel jPanel30;
	private JPanel jPanel29;
	private JDialog addField;
	private JComboBox jComboBox12;
	private JLabel jLabel32;
	private JButton jButton12;
	private JTextField jTextField4;
	private JPanel jPanel28;
	private JLabel jLabel13;
	private JPanel jPanel27;
	private JDialog jDialog8;
	private JButton jButton11;
	private JComboBox jComboBox11;
	private JLabel jLabel19;
	private JDesktopPane jDesktopPane3;
	private JLabel jLabel17;
	private JDesktopPane jDesktopPane2;
	private JButton jButton10;
	private JLabel jLabel2;
	private JDesktopPane jDesktopPane1;
	private JTextArea jTextArea0;
	private JScrollPane jScrollPane1;
	private JPanel jPanel25;
	private JLabel jLabel31;
	private JTextField jTextField22;
	private JButton jButton9;
	private JPanel jPanel24;
	private JLabel jLabel30;
	private JLabel jLabel29;
	private JLabel jLabel27;
	private JPanel jPanel23;
	private JComboBox jComboBox10;
	private JTextField jTextField21;
	private JTextField jTextField20;
	private JPanel jPanel22;
	private JLabel jLabel28;
	private JLabel jLabel26;
	private JLabel jLabel23;
	private JPanel jPanel21;
	private JButton jButton8;
	private JPanel jPanel20;
	private JComboBox jComboBox9;
	private JTextField jTextField19;
	private JTextField jTextField17;
	private JPanel jPanel19;
	private JLabel jLabel25;
	private JLabel jLabel24;
	private JLabel jLabel22;
	private JLabel jLabel21;
	private JPanel jPanel18;
	private JButton jButton7;
	private JPanel jPanel17;
	private JComboBox jComboBox7;
	private JTextField jTextField18;
	private JTextField jTextField16;
	private JTextField jTextField15;
	private JPanel jPanel16;
	private JLabel jLabel20;
	private JTextField jTextField13;
	private JLabel jLabel18;
	private JLabel jLabel16;
	private JPanel jPanel15;
	private JButton jButton6;
	private JPanel jPanel14;
	private JComboBox jComboBox8;
	private JTextField jTextField11;
	private JPanel jPanel13;
	private JComboBox jComboBox6;
	private JLabel jLabel15;
	private JLabel jLabel14;
	private JLabel jLabel12;
	private JPanel jPanel12;
	private JButton jButton5;
	private JPanel jPanel11;
	private JComboBox jComboBox5;
	private JTextField jTextField9;
	private JPanel jPanel10;
	private JButton jButton4;
	private JPanel jPanel9;
	private JComboBox jComboBox4;
	private JTextField jTextField8;
	private JTextField jTextField7;
	private JTextField jTextField6;
	private JPanel jPanel8;
	private JButton jButton3;
	private JPanel jPanel7;
	private JLabel jLabel11;
	private JLabel jLabel10;
	private JLabel jLabel9;
	private JLabel jLabel8;
	private JPanel jPanel6;
	private JButton jButton2;
	private JPanel jPanel5;
	private JTextField jTextField5;
	private JLabel jLabel7;
	private JComboBox jComboBox1;
	private JTextField jTextField2;
	private JTextField jTextField1;
	private JPanel jPanel3;
	private JLabel jLabel6;
	private JLabel jLabel5;
	private JLabel jLabel4;
	private JPanel jPanel1;
	private JComboBox jComboBox3;
	private JTextField jTextField3;
	private JPanel jPanel4;
	private JLabel jLabel3;
	private JLabel jLabel1;
	private JPanel jPanel2;
	private JDialog randomBacteria;
	private JDialog jDialog7;
	private JDialog jDialog6;
	private JDialog jDialog5;
	private JDialog jDialog4;
	private JDialog jDialog3;
	private JDialog jDialog2;
	private AbstractAction openDialog1;
	private JButton jButton1;
	private JDialog jDialog1;
	private AbstractAction exitAction;
	private AbstractAction dumpToFileAction;
	private AbstractAction writeAction;
	private JPanel buttonPanel;
	private JMenuItem aboutMenu;
	private JMenuItem exitMenu;
	private JMenu helpMenu;
	private JMenu fileMenu;
	public Integer ClickNum=0;
	public int collisionBactNum=0;
	public int fieldNum = 0;
	public int ODEnum = 0;
	public int addBactNum = 0;
	private String tickerMoving1;
	private String tickerGeneral2;
	private String tickerCollision1;
	private String classThresholdSignalling1;
	private String startParameters1;
	private String classPosition;
	private String classChemotaxis1;
	private String classChemicalCreator1;
	private String classGeneral1;
	private String classGeneral2;
	private String classGeneral3;
	private String classGeneral4;
	private String classGeneral5;
	private String classODEsignalling1;
	private String classODEsignalling3;
	private String classODEsignalling4;
	private String drawerGeneral2;
	private String drawerGeneral3;
	private String tickerField1="";
	private String tickerField;
	private String drawerField1="";
	private String drawerField;
	private String drawerGeneral1;
	private String startField1="";
	private String startField;
	private String startDimensions1;
	private String ParametersChunkTotal = "";
	private String ClassChunkTotal = "";
	private String TickerChunkTotal = "";
	private String DrawerChunkTotal = "";
	private String OutputChunkTotal = "";
	private String ParameterChunk;
	private String ClassChunk;
	private String TickerChunk;
	private String DrawerChunk;
	private String CustomClass = "CustomClass";
	private String CustomTicker = "CustomTicker";
	private String CustomDrawer = "CustomDrawer";
	private String CustomStart = "CustomStart";
	private String CustomOutput = "CustomOutput";
	private String creatorFieldIndexS;
	
	
	public String[] fieldChoice = null;

	/* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BSimGUI inst = new BSimGUI();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public BSimGUI() {
		super();
		initGUI();
	}
	
	public void FileWrite(){
	    try{
	    // Create file 
	    FileWriter fstream = new FileWriter("./MyBSimSimulation.java");
	        BufferedWriter out = new BufferedWriter(fstream);
	        
	        //collecting all strings to be written into the file
	        String startGeneral1f = "startGeneral1f";
	        String startGeneral2f = "startGeneral2f";
	        String tickerGeneral1f = "tickerGeneral1f";
	        String tickerGeneral4f = "tickerGeneral4f";
	        String drawerGeneral1f = "drawerGeneral1f";
	        String drawerGeneral4f = "drawerGeneral4f";
	        
	        String startGeneral1 = ReadTextFileExample(startGeneral1f, CustomStart);
	        String startGeneral2 = ReadTextFileExample(startGeneral2f, CustomStart);
	        
	        String tickerGeneral1 = ReadTextFileExample(tickerGeneral1f, CustomTicker);
	        String tickerGeneral4 = ReadTextFileExample(tickerGeneral4f, CustomTicker);
	        
	        String drawerGeneral4 = ReadTextFileExample(drawerGeneral4f, CustomDrawer);
	        drawerGeneral4 = popNumUpdate(drawerGeneral4, "bacterium", ClickNum);
	        drawerGeneral4 = popNumUpdate(drawerGeneral4, "simpleColour", ClickNum);
	        
	        String concatonatedText = startGeneral1 + "\n" + ParametersChunkTotal  + "\n" + startGeneral2 + "\n" + startDimensions1 + "\n" + startField1 + "\n" + ClassChunkTotal + "\n" + tickerGeneral1 + "\n" + tickerField1 + "\n" + TickerChunkTotal + "\n" + tickerGeneral4 + "\n" + drawerGeneral1 + "\n" + drawerField1 + "\n" + DrawerChunkTotal + "\n" + drawerGeneral4 + "\n" + OutputChunkTotal;
	    //    String concatonatedText = (jTextArea0.getText() + jTextArea1.getText()
	    //    		+jTextArea2.getText() + jTextArea3.getText()+ jTextArea4.getText());
	        out.write(concatonatedText);
	    //Close the output stream
	    out.close();
	    }catch (Exception e){//Catch exception if any
	      System.err.println("Error: " + e.getMessage());
	    }
	}
	
	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setBackground(new java.awt.Color(200,202,225));
			{
				buttonPanel = new JPanel();
				getContentPane().add(buttonPanel, BorderLayout.WEST);
				getContentPane().add(getJPanel25(), BorderLayout.CENTER);
				buttonPanel.setPreferredSize(new java.awt.Dimension(216, 347));
				buttonPanel.setLayout(null);
				buttonPanel.setBackground(new java.awt.Color(128,128,255));
				buttonPanel.add(getJDesktopPane1());
				buttonPanel.add(getJDesktopPane2());
				buttonPanel.add(getJDesktopPane3());
			}
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					fileMenu = new JMenu();
					jMenuBar1.add(fileMenu);
					fileMenu.setText("File");
					{
						exitMenu = new JMenuItem();
						fileMenu.add(exitMenu);
						exitMenu.setText("Exit");
						exitMenu.setAction(getExitAction());
					}
				}
				{
					helpMenu = new JMenu();
					jMenuBar1.add(helpMenu);
					helpMenu.setText("Help");
					{
						aboutMenu = new JMenuItem();
						helpMenu.add(aboutMenu);
						aboutMenu.setText("About");
					}
				}
			}
			pack();
			this.setSize(526, 404);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private AbstractAction getWriteAction() {
		if(writeAction == null) {
			writeAction = new AbstractAction("Write", null) {
				public void actionPerformed(ActionEvent evt) {
					String outputPreview;
					String outputPreviewf = "outputPreviewf";
//					String str = jTextArea0.getText();
//					jTextArea0.setText(str + "\n" + "Check this out");
					int outputIndex = jComboBox11.getSelectedIndex();
					if (outputIndex == 0) {     //Preview
						String OutputChunk = "";
						outputPreview = ReadTextFileExample(outputPreviewf, CustomOutput);
						OutputChunk = outputPreview;
						OutputChunkTotal += OutputChunk;
						FileWrite();
						System.exit(0);
					}
					else if (outputIndex == 1) {
						getMovieExportDialog().pack();
						getMovieExportDialog().setLocationRelativeTo(null);
						getMovieExportDialog().setVisible(true);					
					}
					else if (outputIndex == 2) {
						getPictureExportDialog().pack();
						getPictureExportDialog().setLocationRelativeTo(null);
						getPictureExportDialog().setVisible(true);	
					}
					else if (outputIndex == 3) {
						getLoggerDialog().pack();
						getLoggerDialog().setLocationRelativeTo(null);
						getLoggerDialog().setVisible(true);	
					}
					
					//this.initGUI().dispose();
				}
			};
		}
		return writeAction;
	}

	private AbstractAction getDumpToFileAction() {
		if(dumpToFileAction == null) {
			dumpToFileAction = new AbstractAction("Dump to File", null) {
				public void actionPerformed(ActionEvent evt) {
					FileWrite();
				}
			};
		}
		return dumpToFileAction;
	}
	
	private AbstractAction getExitAction() {
		if(exitAction == null) {
			exitAction = new AbstractAction("Exit", null) {
				public void actionPerformed(ActionEvent evt) {
					;
				}
			};
		}
		return exitAction;
	}
	
	private JDialog getJDialog1() {
		if(jDialog1 == null) {
			jDialog1 = new JDialog(this);
			jDialog1.setPreferredSize(new java.awt.Dimension(231, 206));
			jDialog1.getContentPane().add(getJPanel1(), BorderLayout.WEST);
			jDialog1.getContentPane().add(getJPanel3(), BorderLayout.EAST);
			jDialog1.getContentPane().add(getJPanel5(), BorderLayout.SOUTH);
			jDialog1.setSize(231, 206);
		}
		return jDialog1;
	}
	
	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Add Bacteria");
			
			jButton1.setBounds(12, 37, 166, 26);
			jButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});
		}
		return jButton1;
	}
	
	private AbstractAction getOpenDialog1() {
		if(openDialog1 == null) {
			openDialog1 = new AbstractAction("OK", null) {
				public void actionPerformed(ActionEvent evt) {
					getJDialog1().pack();
					getJDialog1().setLocationRelativeTo(null);
					getJDialog1().setVisible(true);
				}
			};
		}
		return openDialog1;
	}

	private void jButton1ActionPerformed(ActionEvent evt) {
		ClickNum +=1;
		//Integer index = jComboBox2.getSelectedIndex();
		//System.out.println(index);
		//String s = index.toString();
		getAddBacteriaDialog().pack();
		getAddBacteriaDialog().setLocationRelativeTo(null);
		getAddBacteriaDialog().setVisible(true);
		
	}
	
	private JDialog getJDialog2() {
		if(jDialog2 == null) {
			jDialog2 = new JDialog(this);
			jDialog2.setPreferredSize(new java.awt.Dimension(219, 200));
			jDialog2.getContentPane().add(getJPanel6(), BorderLayout.WEST);
			jDialog2.getContentPane().add(getJPanel7(), BorderLayout.SOUTH);
			jDialog2.getContentPane().add(getJPanel8(), BorderLayout.EAST);
			jDialog2.setSize(219, 200);
		}
		return jDialog2;
	}
	
	private JDialog getJDialog3() {
		if(jDialog3 == null) {
			jDialog3 = new JDialog(this);
			jDialog3.setPreferredSize(new java.awt.Dimension(253, 202));
			jDialog3.getContentPane().add(getJPanel10(), BorderLayout.EAST);
			jDialog3.getContentPane().add(getJPanel11(), BorderLayout.SOUTH);
			jDialog3.getContentPane().add(getJPanel12(), BorderLayout.WEST);
			jDialog3.setSize(253, 202);
		}
		return jDialog3;
	}
	
	private JDialog getJDialog4() {
		if(jDialog4 == null) {
			jDialog4 = new JDialog(this);
			jDialog4.setPreferredSize(new java.awt.Dimension(287, 166));
			jDialog4.getContentPane().add(getJPanel13(), BorderLayout.EAST);
			jDialog4.getContentPane().add(getJPanel14(), BorderLayout.SOUTH);
			jDialog4.getContentPane().add(getJPanel15(), BorderLayout.WEST);
			jDialog4.setSize(287, 166);
		}
		return jDialog4;
	}
	
	private JDialog getJDialog5() {
		if(jDialog5 == null) {
			jDialog5 = new JDialog(this);
			jDialog5.setPreferredSize(new java.awt.Dimension(216, 207));
			jDialog5.getContentPane().add(getJPanel17(), BorderLayout.SOUTH);
			jDialog5.getContentPane().add(getJPanel18(), BorderLayout.WEST);
			jDialog5.getContentPane().add(getJPanel16(), BorderLayout.EAST);
			jDialog5.setSize(216, 207);
		}
		return jDialog5;
	}
	
	private JDialog getJDialog6() {
		if(jDialog6 == null) {
			jDialog6 = new JDialog(this);
			jDialog6.setPreferredSize(new java.awt.Dimension(218, 167));
			jDialog6.getContentPane().add(getJPanel19(), BorderLayout.EAST);
			jDialog6.getContentPane().add(getJPanel20(), BorderLayout.SOUTH);
			jDialog6.getContentPane().add(getJPanel21(), BorderLayout.CENTER);
			jDialog6.setSize(218, 167);
		}
		return jDialog6;
	}
	
	private JDialog getJDialog7() {
		if(jDialog7 == null) {
			jDialog7 = new JDialog(this);
			jDialog7.setPreferredSize(new java.awt.Dimension(211, 207));
			jDialog7.getContentPane().add(getJPanel22(), BorderLayout.EAST);
			jDialog7.getContentPane().add(getJPanel23(), BorderLayout.WEST);
			jDialog7.getContentPane().add(getJPanel24(), BorderLayout.SOUTH);
			jDialog7.setSize(211, 207);
		}
		return jDialog7;
	}
	
	private JDialog getJDialog0() {
		if(randomBacteria == null) {
			randomBacteria = new JDialog(this);
			randomBacteria.setPreferredSize(new java.awt.Dimension(229, 164));
			{
				jPanel2 = new JPanel();
				randomBacteria.getContentPane().add(getJPanel2(), BorderLayout.WEST);
				jPanel2.setPreferredSize(new java.awt.Dimension(119, 152));
				jPanel2.add(getJLabel1());
				jPanel2.add(getJLabel3());
				jPanel2.add(getJLabel32());
			}
			{
				randomBacteria.getContentPane().add(getJPanel4(), BorderLayout.EAST);
				randomBacteria.getContentPane().add(getJPanel9(), BorderLayout.SOUTH);
			}
			randomBacteria.setSize(229, 164);
		}
		return randomBacteria;
	}
	
	private JPanel getJPanel2() {
		if(jPanel2 == null) {
			jPanel2 = new JPanel();
		}
		return jPanel2;
	}
	
	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Population size:");
			jLabel1.setPreferredSize(new java.awt.Dimension(90, 27));
		}
		return jLabel1;
	}

	private JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText("Colour:");
			jLabel3.setPreferredSize(new java.awt.Dimension(90, 27));
		}
		return jLabel3;
	}
	
	private JPanel getJPanel4() {
		if(jPanel4 == null) {
			jPanel4 = new JPanel();
			jPanel4.setPreferredSize(new java.awt.Dimension(108, 81));
			jPanel4.add(getJTextField3());
			jPanel4.add(getJComboBox3());
			jPanel4.add(getJComboBox12());
		}
		return jPanel4;
	}
	
	private JTextField getJTextField3() {
		if(jTextField3 == null) {
			jTextField3 = new JTextField();
			jTextField3.setPreferredSize(new java.awt.Dimension(89, 26));
		}
		return jTextField3;
	}

	private JComboBox getJComboBox3() {
		if(jComboBox3 == null) {
			ComboBoxModel jComboBox3Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink"});
			jComboBox3 = new JComboBox();
			jComboBox3.setModel(jComboBox3Model);
			jComboBox3.setPreferredSize(new java.awt.Dimension(89, 26));
		}
		return jComboBox3;
	}
	
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setPreferredSize(new java.awt.Dimension(110, 149));
			jPanel1.add(getJLabel4());
			jPanel1.add(getJLabel5());
			jPanel1.add(getJLabel6());
			jPanel1.add(getJLabel7());
		}
		return jPanel1;
	}
	
	private JLabel getJLabel4() {
		if(jLabel4 == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Population size:");
			jLabel4.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel4;
	}
	
	private JLabel getJLabel5() {
		if(jLabel5 == null) {
			jLabel5 = new JLabel();
			jLabel5.setText("Radius:");
			jLabel5.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel5;
	}
	
	private JLabel getJLabel6() {
		if(jLabel6 == null) {
			jLabel6 = new JLabel();
			jLabel6.setText("Production rate:");
			jLabel6.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel6;
	}
	
	private JPanel getJPanel3() {
		if(jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setPreferredSize(new java.awt.Dimension(118, 142));
			jPanel3.add(getJTextField1());
			jPanel3.add(getJTextField2());
			jPanel3.add(getJTextField5());
			jPanel3.add(getJComboBox1());
		}
		return jPanel3;
	}
	
	private JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField1;
	}
	
	private JTextField getJTextField2() {
		if(jTextField2 == null) {
			jTextField2 = new JTextField();
			jTextField2.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField2;
	}
	
	private JComboBox getJComboBox1() {
		if(jComboBox1 == null) {
			ComboBoxModel jComboBox1Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Purple" });
			jComboBox1 = new JComboBox();
			jComboBox1.setModel(jComboBox1Model);
			jComboBox1.setPreferredSize(new java.awt.Dimension(84, 26));
		}
		return jComboBox1;
	}
	
	private JLabel getJLabel7() {
		if(jLabel7 == null) {
			jLabel7 = new JLabel();
			jLabel7.setText("Colour:");
			jLabel7.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel7;
	}
	
	private JTextField getJTextField5() {
		if(jTextField5 == null) {
			jTextField5 = new JTextField();
			jTextField5.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField5;
	}
	
	private JPanel getJPanel5() {
		if(jPanel5 == null) {
			jPanel5 = new JPanel();
			jPanel5.setPreferredSize(new java.awt.Dimension(223, 37));
			jPanel5.add(getJButton2());
		}
		return jPanel5;
	}
	
	private JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("OK");
			jButton2.setPreferredSize(new java.awt.Dimension(72, 28));
			jButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton2ActionPerformed(evt);
				}
			});
		}
		return jButton2;
	}
	
	private JPanel getJPanel6() {
		if(jPanel6 == null) {
			jPanel6 = new JPanel();
			jPanel6.setPreferredSize(new java.awt.Dimension(110, 149));
			jPanel6.add(getJLabel8());
			jPanel6.add(getJLabel9());
			jPanel6.add(getJLabel10());
			jPanel6.add(getJLabel11());
		}
		return jPanel6;
	}
	
	private JLabel getJLabel8() {
		if(jLabel8 == null) {
			jLabel8 = new JLabel();
			jLabel8.setText("Population size:");
			jLabel8.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel8;
	}
	
	private JLabel getJLabel9() {
		if(jLabel9 == null) {
			jLabel9 = new JLabel();
			jLabel9.setText("Radius:");
			jLabel9.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel9;
	}
	
	private JLabel getJLabel10() {
		if(jLabel10 == null) {
			jLabel10 = new JLabel();
			jLabel10.setText("Threshold:");
			jLabel10.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel10;
	}
	
	private JLabel getJLabel11() {
		if(jLabel11 == null) {
			jLabel11 = new JLabel();
			jLabel11.setText("Colour:");
			jLabel11.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel11;
	}
	
	private JPanel getJPanel7() {
		if(jPanel7 == null) {
			jPanel7 = new JPanel();
			jPanel7.setPreferredSize(new java.awt.Dimension(223, 37));
			jPanel7.add(getJButton3());
		}
		return jPanel7;
	}
	
	private JButton getJButton3() {
		if(jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("OK");
			jButton3.setPreferredSize(new java.awt.Dimension(72, 28));
			jButton3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton3ActionPerformed(evt);
				}
			});
		}
		return jButton3;
	}
	
	private JPanel getJPanel8() {
		if(jPanel8 == null) {
			jPanel8 = new JPanel();
			jPanel8.setPreferredSize(new java.awt.Dimension(118, 142));
			jPanel8.add(getJTextField6());
			jPanel8.add(getJTextField7());
			jPanel8.add(getJTextField8());
			jPanel8.add(getJComboBox4());
		}
		return jPanel8;
	}
	
	private JTextField getJTextField6() {
		if(jTextField6 == null) {
			jTextField6 = new JTextField();
			jTextField6.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField6;
	}
	
	private JTextField getJTextField7() {
		if(jTextField7 == null) {
			jTextField7 = new JTextField();
			jTextField7.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField7;
	}
	
	private JTextField getJTextField8() {
		if(jTextField8 == null) {
			jTextField8 = new JTextField();
			jTextField8.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField8;
	}
	
	private JComboBox getJComboBox4() {
		if(jComboBox4 == null) {
			ComboBoxModel jComboBox4Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Purple" });
			jComboBox4 = new JComboBox();
			jComboBox4.setModel(jComboBox4Model);
			jComboBox4.setPreferredSize(new java.awt.Dimension(84, 26));
		}
		return jComboBox4;
	}
	
	private JPanel getJPanel9() {
		if(jPanel9 == null) {
			jPanel9 = new JPanel();
			jPanel9.setPreferredSize(new java.awt.Dimension(264, 36));
			jPanel9.add(getJButton4());
		}
		return jPanel9;
	}
	
	


private String ReadTextFileExample(String filename, String foldername)
	{

			File file = new File("./Final GUI text/"+foldername+"/"+filename+".txt");
			StringBuffer contents = new StringBuffer();
			String ourText;
			BufferedReader reader = null;
			 
			try
			  {
			   reader = new BufferedReader(new FileReader(file));
			   String text = null;
			 
			   // repeat until all lines is read
			   while ((text = reader.readLine()) != null)
			   {
			         contents.append(text)
			            .append(System.getProperty(
			               "line.separator"));
			    }
			   } catch (FileNotFoundException e)
			        {
			            e.printStackTrace();
			        } catch (IOException e)
			        {
			            e.printStackTrace();
			        } finally
			        {
			            try
			            {
			                if (reader != null)
			                {
			                    reader.close();
			                }
			            } catch (IOException e)
			            {
			                e.printStackTrace();
			            }
			        
			        
			        // show file contents here
			        ourText = contents.toString();}
			        
			        return ourText;
	}	
	
	
	
	
	private JButton getJButton4() {
		
		if(jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("OK");
			jButton4.setPreferredSize(new java.awt.Dimension(84, 28));
			jButton4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton4ActionPerformed(evt);
					
				}
			});
		}
		return jButton4;
	}
	
	private JPanel getJPanel10() {
		if(jPanel10 == null) {
			jPanel10 = new JPanel();
			jPanel10.setPreferredSize(new java.awt.Dimension(108, 131));
			jPanel10.add(getJTextField9());
			jPanel10.add(getJComboBox5());
			jPanel10.add(getJComboBox6());
		}
		return jPanel10;
	}
	
	private JTextField getJTextField9() {
		if(jTextField9 == null) {
			jTextField9 = new JTextField();
			jTextField9.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField9;
	}

	private JComboBox getJComboBox5() {
		if(jComboBox5 == null) {
			ComboBoxModel jComboBox5Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink" });
			jComboBox5 = new JComboBox();
			jComboBox5.setModel(jComboBox5Model);
			jComboBox5.setPreferredSize(new java.awt.Dimension(84,26));
		}
		return jComboBox5;
	}
	
	private JPanel getJPanel11() {
		if(jPanel11 == null) {
			jPanel11 = new JPanel();
			jPanel11.setPreferredSize(new java.awt.Dimension(223,37));
			jPanel11.add(getJButton5());
		}
		return jPanel11;
	}
	
	private JButton getJButton5() {
		if(jButton5 == null) {
			jButton5 = new JButton();
			jButton5.setText("OK");
			jButton5.setPreferredSize(new java.awt.Dimension(72,28));
			jButton5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton5ActionPerformed(evt);
				}
			});
		}

		return jButton5;
	}
	
	private JPanel getJPanel12() {
		if(jPanel12 == null) {
			jPanel12 = new JPanel();
			jPanel12.setPreferredSize(new java.awt.Dimension(149, 131));
			jPanel12.add(getJLabel12());
			jPanel12.add(getJLabel14());
			jPanel12.add(getJLabel15());
		}
		return jPanel12;
	}
	
	private JLabel getJLabel12() {
		if(jLabel12 == null) {
			jLabel12 = new JLabel();
			jLabel12.setText("Population size:");
			jLabel12.setPreferredSize(new java.awt.Dimension(127, 28));
		}
		return jLabel12;
	}

	private JLabel getJLabel14() {
		if(jLabel14 == null) {
			jLabel14 = new JLabel();
			jLabel14.setText("Colour before collision:");
			jLabel14.setPreferredSize(new java.awt.Dimension(127, 28));
		}
		return jLabel14;
	}
	
	private JLabel getJLabel15() {
		if(jLabel15 == null) {
			jLabel15 = new JLabel();
			jLabel15.setText("Colour after collision:");
			jLabel15.setPreferredSize(new java.awt.Dimension(127, 28));
		}
		return jLabel15;
	}
	
	private JComboBox getJComboBox6() {
		if(jComboBox6 == null) {
			ComboBoxModel jComboBox6Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink" });
			jComboBox6 = new JComboBox();
			jComboBox6.setModel(jComboBox6Model);
			jComboBox6.setPreferredSize(new java.awt.Dimension(84,26));
		}
		return jComboBox6;
	}
	
	private JPanel getJPanel13() {
		if(jPanel13 == null) {
			jPanel13 = new JPanel();
			jPanel13.setPreferredSize(new java.awt.Dimension(108, 131));
			jPanel13.add(getJTextField11());
			jPanel13.add(getJTextField13());
			jPanel13.add(getJComboBox8());
		}
		return jPanel13;
	}
	
	private JTextField getJTextField11() {
		if(jTextField11 == null) {
			jTextField11 = new JTextField();
			jTextField11.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField11;
	}

	private JComboBox getJComboBox8() {
		if(jComboBox8 == null) {
			ComboBoxModel jComboBox8Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink" });
			jComboBox8 = new JComboBox();
			jComboBox8.setModel(jComboBox8Model);
			jComboBox8.setPreferredSize(new java.awt.Dimension(84, 26));
		}
		return jComboBox8;
	}
	
	private JPanel getJPanel14() {
		if(jPanel14 == null) {
			jPanel14 = new JPanel();
			jPanel14.setPreferredSize(new java.awt.Dimension(304, 37));
			jPanel14.add(getJButton6());
		}
		return jPanel14;
	}
	
	private JButton getJButton6() {
		if(jButton6 == null) {
			jButton6 = new JButton();
			jButton6.setText("OK");
			jButton6.setPreferredSize(new java.awt.Dimension(72,28));
			jButton6.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton6ActionPerformed(evt);
				}
			});
		}
		return jButton6;
	}
	
	private JPanel getJPanel15() {
		if(jPanel15 == null) {
			jPanel15 = new JPanel();
			jPanel15.setPreferredSize(new java.awt.Dimension(181, 141));
			jPanel15.add(getJLabel16());
			jPanel15.add(getJLabel18());
			jPanel15.add(getJLabel20());
		}
		return jPanel15;
	}
	
	private JLabel getJLabel16() {
		if(jLabel16 == null) {
			jLabel16 = new JLabel();
			jLabel16.setText("Population size:");
			jLabel16.setPreferredSize(new java.awt.Dimension(171, 28));
		}
		return jLabel16;
	}

	private JLabel getJLabel18() {
		if(jLabel18 == null) {
			jLabel18 = new JLabel();
			jLabel18.setText("Bact. surface area growth rate:");
			jLabel18.setPreferredSize(new java.awt.Dimension(171, 28));
		}
		return jLabel18;
	}

	private JTextField getJTextField13() {
		if(jTextField13 == null) {
			jTextField13 = new JTextField();
			jTextField13.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField13;
	}

	private JLabel getJLabel20() {
		if(jLabel20 == null) {
			jLabel20 = new JLabel();
			jLabel20.setText("Colour:");
			jLabel20.setPreferredSize(new java.awt.Dimension(171, 28));
		}
		return jLabel20;
	}
	
	private JPanel getJPanel16() {
		if(jPanel16 == null) {
			jPanel16 = new JPanel();
			jPanel16.setPreferredSize(new java.awt.Dimension(108, 138));
			jPanel16.add(getJTextField15());
			jPanel16.add(getJTextField16());
			jPanel16.add(getJTextField18());
			jPanel16.add(getJComboBox7());
		}
		return jPanel16;
	}
	
	private JTextField getJTextField15() {
		if(jTextField15 == null) {
			jTextField15 = new JTextField();
			jTextField15.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField15;
	}
	
	private JTextField getJTextField16() {
		if(jTextField16 == null) {
			jTextField16 = new JTextField();
			jTextField16.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField16;
	}

	private JTextField getJTextField18() {
		if(jTextField18 == null) {
			jTextField18 = new JTextField();
			jTextField18.setPreferredSize(new java.awt.Dimension(84, 28));
		}
		return jTextField18;
	}
	
	private JComboBox getJComboBox7() {
		if(jComboBox7 == null) {
			ComboBoxModel jComboBox7Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Purple" });
			jComboBox7 = new JComboBox();
			jComboBox7.setModel(jComboBox7Model);
			jComboBox7.setPreferredSize(new java.awt.Dimension(84, 26));
		}
		return jComboBox7;
	}
	
	private JPanel getJPanel17() {
		if(jPanel17 == null) {
			jPanel17 = new JPanel();
			jPanel17.setPreferredSize(new java.awt.Dimension(304, 37));
			jPanel17.add(getJButton7());
		}
		return jPanel17;
	}
	
	private JButton getJButton7() {
		if(jButton7 == null) {
			jButton7 = new JButton();
			jButton7.setText("OK");
			jButton7.setPreferredSize(new java.awt.Dimension(72,28));
			jButton7.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton7ActionPerformed(evt);
				}
			});
		}
		return jButton7;
	}
	
	private JPanel getJPanel18() {
		if(jPanel18 == null) {
			jPanel18 = new JPanel();
			jPanel18.setPreferredSize(new java.awt.Dimension(111, 135));
			jPanel18.add(getJLabel21());
			jPanel18.add(getJLabel22());
			jPanel18.add(getJLabel24());
			jPanel18.add(getJLabel25());
		}
		return jPanel18;
	}
	
	private JLabel getJLabel21() {
		if(jLabel21 == null) {
			jLabel21 = new JLabel();
			jLabel21.setText("Population size:");
			jLabel21.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel21;
	}
	
	private JLabel getJLabel22() {
		if(jLabel22 == null) {
			jLabel22 = new JLabel();
			jLabel22.setText("Radius:");
			jLabel22.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel22;
	}

	private JLabel getJLabel24() {
		if(jLabel24 == null) {
			jLabel24 = new JLabel();
			jLabel24.setText("Threshold:");
			jLabel24.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel24;
	}
	
	private JLabel getJLabel25() {
		if(jLabel25 == null) {
			jLabel25 = new JLabel();
			jLabel25.setText("Colour:");
			jLabel25.setPreferredSize(new java.awt.Dimension(90, 28));
		}
		return jLabel25;
	}
	
	private JPanel getJPanel19() {
		if(jPanel19 == null) {
			jPanel19 = new JPanel();
			jPanel19.setPreferredSize(new java.awt.Dimension(108,138));
			jPanel19.add(getJTextField17());
			jPanel19.add(getJTextField19());
			jPanel19.add(getJComboBox9());
		}
		return jPanel19;
	}
	
	private JTextField getJTextField17() {
		if(jTextField17 == null) {
			jTextField17 = new JTextField();
			jTextField17.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField17;
	}
	
	private JTextField getJTextField19() {
		if(jTextField19 == null) {
			jTextField19 = new JTextField();
			jTextField19.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField19;
	}

	private JComboBox getJComboBox9() {
		if(jComboBox9 == null) {
			ComboBoxModel jComboBox9Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Purple"});
			jComboBox9 = new JComboBox();
			jComboBox9.setModel(jComboBox9Model);
			jComboBox9.setPreferredSize(new java.awt.Dimension(84,26));
		}
		return jComboBox9;
	}
	
	private JPanel getJPanel20() {
		if(jPanel20 == null) {
			jPanel20 = new JPanel();
			jPanel20.setPreferredSize(new java.awt.Dimension(210, 36));
			jPanel20.add(getJButton8());
		}
		return jPanel20;
	}
	
	private JButton getJButton8() {
		if(jButton8 == null) {
			jButton8 = new JButton();
			jButton8.setText("OK");
			jButton8.setPreferredSize(new java.awt.Dimension(72,28));
			jButton8.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton8ActionPerformed(evt);
				}
			});
		}
		return jButton8;
	}
	
	private JPanel getJPanel21() {
		if(jPanel21 == null) {
			jPanel21 = new JPanel();
			jPanel21.setPreferredSize(new java.awt.Dimension(102, 120));
			jPanel21.add(getJLabel23());
			jPanel21.add(getJLabel26());
			jPanel21.add(getJLabel28());
		}
		return jPanel21;
	}
	
	private JLabel getJLabel23() {
		if(jLabel23 == null) {
			jLabel23 = new JLabel();
			jLabel23.setText("Population size:");
			jLabel23.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel23;
	}
	
	private JLabel getJLabel26() {
		if(jLabel26 == null) {
			jLabel26 = new JLabel();
			jLabel26.setText("Radius:");
			jLabel26.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel26;
	}

	private JLabel getJLabel28() {
		if(jLabel28 == null) {
			jLabel28 = new JLabel();
			jLabel28.setText("Colour:");
			jLabel28.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel28;
	}
	
	private JPanel getJPanel22() {
		if(jPanel22 == null) {
			jPanel22 = new JPanel();
			jPanel22.setPreferredSize(new java.awt.Dimension(108,138));
			jPanel22.add(getJTextField20());
			jPanel22.add(getJTextField21());
			jPanel22.add(getJTextField22());
			jPanel22.add(getJComboBox10());
		}
		return jPanel22;
	}
	
	private JTextField getJTextField20() {
		if(jTextField20 == null) {
			jTextField20 = new JTextField();
			jTextField20.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField20;
	}
	
	private JTextField getJTextField21() {
		if(jTextField21 == null) {
			jTextField21 = new JTextField();
			jTextField21.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField21;
	}
	
	private JComboBox getJComboBox10() {
		if(jComboBox10 == null) {
			ComboBoxModel jComboBox10Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Purple" });
			jComboBox10 = new JComboBox();
			jComboBox10.setModel(jComboBox10Model);
			jComboBox10.setPreferredSize(new java.awt.Dimension(84,26));
		}
		return jComboBox10;
	}
	
	private JPanel getJPanel23() {
		if(jPanel23 == null) {
			jPanel23 = new JPanel();
			jPanel23.setPreferredSize(new java.awt.Dimension(102, 120));
			jPanel23.add(getJLabel27());
			jPanel23.add(getJLabel29());
			jPanel23.add(getJLabel30());
			jPanel23.add(getJLabel31());
		}
		return jPanel23;
	}
	
	private JLabel getJLabel27() {
		if(jLabel27 == null) {
			jLabel27 = new JLabel();
			jLabel27.setText("Population size:");
			jLabel27.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel27;
	}
	
	private JLabel getJLabel29() {
		if(jLabel29 == null) {
			jLabel29 = new JLabel();
			jLabel29.setText("Radius:");
			jLabel29.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel29;
	}
	
	private JLabel getJLabel30() {
		if(jLabel30 == null) {
			jLabel30 = new JLabel();
			jLabel30.setText("Production rate:");
			jLabel30.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel30;
	}
	
	private JPanel getJPanel24() {
		if(jPanel24 == null) {
			jPanel24 = new JPanel();
			jPanel24.setPreferredSize(new java.awt.Dimension(210, 36));
			jPanel24.add(getJButton9());
		}
		return jPanel24;
	}
	
	private JButton getJButton9() {
		if(jButton9 == null) {
			jButton9 = new JButton();
			jButton9.setText("OK");
			jButton9.setPreferredSize(new java.awt.Dimension(72,28));
			jButton9.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton9ActionPerformed(evt);
				}
			});
		}
		return jButton9;
	}
	
	private JTextField getJTextField22() {
		if(jTextField22 == null) {
			jTextField22 = new JTextField();
			jTextField22.setPreferredSize(new java.awt.Dimension(84,28));
		}
		return jTextField22;
	}
	
	private JLabel getJLabel31() {
		if(jLabel31 == null) {
			jLabel31 = new JLabel();
			jLabel31.setText("Colour:");
			jLabel31.setPreferredSize(new java.awt.Dimension(90,28));
		}
		return jLabel31;
	}
	//add suffix number to a chosen word
	private String popNumUpdate(String SearchedIn, String SearchedFor, Integer BacteriaPopNumber) {
		int m=1;
		int n=0;
		int o=1;
		int p=0;
		int i;
		int j;
		String output = null; 
		String output2 = null;
		String BacteriaPopNumberString = BacteriaPopNumber.toString();
		StringBuffer ourBuffer = new StringBuffer(SearchedIn);
		if (SearchedIn.indexOf(SearchedFor)!=-1){
		for (i=SearchedIn.indexOf(SearchedFor, 0); i!=-1; i = SearchedIn.indexOf(SearchedFor, m)) {			
			output = ourBuffer.insert(n+i+SearchedFor.length(),BacteriaPopNumberString).toString();
			//System.out.println(i);
			m=i+1;	
			n+=1;
		}
		
		StringBuffer ourBuffer2 = new StringBuffer(output);
		output2 = output;
		
		for (j=output.indexOf(SearchedFor+".", 0); j!=-1; j = output.indexOf(SearchedFor+".", o)) {			
			output2 = ourBuffer2.insert(p+j+SearchedFor.length(),BacteriaPopNumberString).toString();
			//System.out.println(j);
			o=j+1;	
			p+=1;
		}
		
		return output2; }
		
		else return SearchedIn;
			
	}
	
	private String StringInserter(String SearchedIn, String SearchedFor, String toBeInserted) {
		int m=1;
		int n=0;
		int o=1;
		int p=0;
		int i;
		int j;
		String output = null; 
		String output2 = null;
		//String BacteriaPopNumberString = BacteriaPopNumber.toString();
		StringBuffer ourBuffer = new StringBuffer(SearchedIn);
		if (SearchedIn.indexOf(SearchedFor)!=-1){
		for (i=SearchedIn.indexOf(SearchedFor, 0); i!=-1; i = SearchedIn.indexOf(SearchedFor, m)) {			
			output = ourBuffer.insert(n+i+SearchedFor.length(),toBeInserted).toString();
			//System.out.println(i);
			m=i+1;	
			n+=1;
		}
		
		StringBuffer ourBuffer2 = new StringBuffer(output);
		output2 = output;
		
		return output; }
		
		else return SearchedIn;
			
	}
	
	private void jButton4ActionPerformed(ActionEvent evt) {
		String test2 = "test2";
		String test3 = "test3";
		String test4 = "test4";
		String BacteriaColour = null;
		Integer index2 = jComboBox3.getSelectedIndex();
		if (index2==0) {
			BacteriaColour = "RED";
		}
		else if (index2==1) {
			BacteriaColour = "GREEN";
			}
		else if (index2==2) {
			BacteriaColour = "BLUE";
			}
		else if (index2==3) {
			BacteriaColour = "YELLOW";
			}
		else {
			BacteriaColour = "PINK";
			}					

		//String importText2 = ReadTextFileExample(test2);
		//String modifiedText2 = popNumUpdate(importText2,"bacteria", ClickNum);
		//String modifiedText3 = popNumUpdate(modifiedText2,"bacterium", ClickNum);
		//String modifiedFinal = popNumUpdate(modifiedText3,"populationSize", ClickNum);
		String size = jTextField3.getText();
		String populationSize = "int populationSize = "+size+";";
		String modPopulationSize = popNumUpdate(populationSize,"populationSize", ClickNum);
		//jTextArea1.setText(jTextArea1.getText()+ "\n" + modPopulationSize + "\n" + modifiedFinal);
		
		//String importText3 = ReadTextFileExample(test3);
		//String modifiedText4 = popNumUpdate(importText3,"bacteria", ClickNum);
		//String modifiedText5 = popNumUpdate(modifiedText4,"bacterium", ClickNum);
		//jTextArea2.setText(jTextArea2.getText()+ "\n" + modifiedText5); 
		
		String Colour = "draw(bacterium,Color."+BacteriaColour+");"+ "\n" + "}";
		//String importText4 = ReadTextFileExample(test4);
		//String popSpecific = importText4 + Colour;
		//String modifiedText6 = popNumUpdate(popSpecific,"bacteria", ClickNum);
		//String modifiedText7 = popNumUpdate(modifiedText6,"bacterium", ClickNum);

		//jTextArea3.setText(jTextArea3.getText()+ "\n" + modifiedText7+ "\n");
		
		this.getJDialog0().dispose();
	}
	
	private void jButton2ActionPerformed(ActionEvent evt) {
		//System.out.println("jButton2.actionPerformed, event="+evt);
		this.getJDialog1().dispose();
	}
	
	private void jButton3ActionPerformed(ActionEvent evt) {
		//System.out.println("jButton2.actionPerformed, event="+evt);
		this.getJDialog2().dispose();
	}
	
	private void jButton5ActionPerformed(ActionEvent evt) {
		
		String collision1 = "collision1";
		String collision2 = "collision2";
		String collision3 = "collision3";
		String collision4 = "collision4";
		
		String BacteriaColourBefore = null;
		String BacteriaColourAfter = null;
		
		Integer index5 = jComboBox5.getSelectedIndex();
		if (index5==0) {
			BacteriaColourBefore = "RED";
		}
		else if (index5==1) {
			BacteriaColourBefore = "GREEN";
			}
		else if (index5==2) {
			BacteriaColourBefore = "BLUE";
			}
		else if (index5==3) {
			BacteriaColourBefore = "YELLOW";
			}
		else {
			BacteriaColourBefore = "PINK";
			}	
		
		Integer index6 = jComboBox6.getSelectedIndex();
		if (index6==0) {
			BacteriaColourAfter = "RED";
		}
		else if (index6==1) {
			BacteriaColourAfter = "GREEN";
			}
		else if (index6==2) {
			BacteriaColourAfter = "BLUE";
			}
		else if (index6==3) {
			BacteriaColourAfter = "YELLOW";
			}
		else {
			BacteriaColourAfter = "PINK";
			}
		
	/*	if (collisionBactNum < 1) {
		String importCollision1 = ReadTextFileExample(collision1);
		jTextArea1.setText(jTextArea1.getText()+ "\n" + importCollision1);
		collisionBactNum +=1; } */
		//String importCollision2 = ReadTextFileExample(collision2);
		//String modifiedCollision2 = popNumUpdate(importCollision2,"bacteria", ClickNum);
		//String modifiedCollision3 = popNumUpdate(modifiedCollision2,"bacterium", ClickNum);
		//String modifiedCollisionFinal = popNumUpdate(modifiedCollision3,"populationSize", ClickNum);
		String size = jTextField9.getText();
		String populationSize = "int populationSize = "+size+";";
		String modCollisionPopulationSize = popNumUpdate(populationSize,"populationSize", ClickNum);
		//jTextArea1.setText(jTextArea1.getText()+ "\n" + modCollisionPopulationSize + "\n" + modifiedCollisionFinal);
		
		//String importCollision3 = ReadTextFileExample(collision3);
		//String modifiedCollision4 = popNumUpdate(importCollision3,"bacteria", ClickNum);
		//String modifiedCollision5 = popNumUpdate(modifiedCollision4,"bacterium", ClickNum);
		//jTextArea2.setText(jTextArea2.getText()+ "\n" + modifiedCollision5); 
		                          
		String collisionColour = "draw(bacterium, bacterium.collision? Color." +BacteriaColourAfter+ " : Color." +BacteriaColourBefore+ ");"+ "\n" + "}";
		//String importCollision4 = ReadTextFileExample(collision4);
		//String collisionPopSpecific = importCollision4 + collisionColour;
		//String modifiedCollision6 = popNumUpdate(collisionPopSpecific,"bacteria", ClickNum);
		//String modifiedCollision7 = popNumUpdate(modifiedCollision6,"bacterium", ClickNum);

	//	jTextArea3.setText(jTextArea3.getText()+ "\n" + modifiedCollision7+ "\n"); 
		
		this.getJDialog3().dispose();
	}
	
	private void jButton6ActionPerformed(ActionEvent evt) {

		String replication2 = "replication2";
		String replication3 = "replication3";
		String replication4 = "replication4";
		String replicationBacteriaColour = null;
		Integer index8 = jComboBox8.getSelectedIndex();
		if (index8==0) {
			replicationBacteriaColour = "RED";
		}
		else if (index8==1) {
			replicationBacteriaColour = "GREEN";
			}
		else if (index8==2) {
			replicationBacteriaColour = "BLUE";
			}
		else if (index8==3) {
			replicationBacteriaColour = "YELLOW";
			}
		else {
			replicationBacteriaColour = "PINK";
			}					

		String surfaceGrowthRate = jTextField13.getText();
		String growthRate = "int surfaceGrowthRate = "+surfaceGrowthRate+";";
		String modGrowthRate = popNumUpdate(growthRate,"surfaceGrowthRate", ClickNum);
		//String importReplication2 = ReadTextFileExample(replication2);
		//String modifiedReplication2 = popNumUpdate(importReplication2,"bacteria", ClickNum);
		//String modifiedReplication3 = popNumUpdate(modifiedReplication2,"bacterium", ClickNum);
		//String modifiedReplication4 = popNumUpdate(modifiedReplication3,"populationSize", ClickNum);
		//String modifiedReplication5 = popNumUpdate(modifiedReplication4,"children", ClickNum);
		//String modifiedReplicationFinal = popNumUpdate(modifiedReplication5,"surfaceGrowthRate", ClickNum);
		String size = jTextField11.getText();
		String populationSize = "int populationSize = "+size+";";
		String modPopulationSize = popNumUpdate(populationSize,"populationSize", ClickNum);
		//jTextArea1.setText(jTextArea1.getText()+ "\n" + modPopulationSize + "\n" + modGrowthRate + "\n" + modifiedReplicationFinal);
		
		//String importReplication3 = ReadTextFileExample(replication3);
		//String modifiedReplication6 = popNumUpdate(importReplication3,"bacteria", ClickNum);
		//String modifiedReplication7 = popNumUpdate(modifiedReplication6,"bacterium", ClickNum);
		//String modifiedReplication8 = popNumUpdate(modifiedReplication7,"children", ClickNum);
		//jTextArea2.setText(jTextArea2.getText()+ "\n" + modifiedReplication8); 
		
		//String replicationColour = "draw(bacterium,Color."+replicationBacteriaColour+");"+ "\n" + "}";
		//String importReplication4 = ReadTextFileExample(replication4);
		//String replicationPopSpecific = importReplication4 + replicationColour;
		//String modifiedReplication9 = popNumUpdate(replicationPopSpecific,"bacteria", ClickNum);
		//String modifiedReplication10 = popNumUpdate(modifiedReplication9,"bacterium", ClickNum);

		//jTextArea3.setText(jTextArea3.getText()+ "\n" + modifiedReplication10+ "\n"); 
		
		this.getJDialog4().dispose();
	}
	
	private void jButton7ActionPerformed(ActionEvent evt) {
		//System.out.println("jButton2.actionPerformed, event="+evt);
		this.getJDialog5().dispose();
	}
	
	private void jButton8ActionPerformed(ActionEvent evt) {
		//System.out.println("jButton2.actionPerformed, event="+evt);
		this.getJDialog6().dispose();
	}
	
	private void jButton9ActionPerformed(ActionEvent evt) {
		this.getJDialog7().dispose();
	}
	
	private JPanel getJPanel25() {
		if(jPanel25 == null) {
			jPanel25 = new JPanel();
			FlowLayout jPanel25Layout = new FlowLayout();
			jPanel25.setLayout(jPanel25Layout);
			jPanel25.setPreferredSize(new java.awt.Dimension(280, 347));
			jPanel25.setBackground(new java.awt.Color(211,211,228));
			jPanel25.add(getJScrollPane1());
		}
		return jPanel25;
	}

	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setPreferredSize(new java.awt.Dimension(286, 335));
			jScrollPane1.setViewportView(getJTextArea0());
		}
		return jScrollPane1;
	}
private JTextArea getJTextArea0() {
		if (jTextArea0 == null) {
			jTextArea0 = new JTextArea();
			jTextArea0.setText("SIMULATION INFORMATION:" + "\n\n");
		}
		return jTextArea0;
	}

	private JDesktopPane getJDesktopPane1() {
		if(jDesktopPane1 == null) {
			jDesktopPane1 = new JDesktopPane();
			jDesktopPane1.setBounds(8, 13, 195, 116);
			jDesktopPane1.add(getJLabel2(), JLayeredPane.DEFAULT_LAYER);
			jDesktopPane1.add(getJButton10(), JLayeredPane.DEFAULT_LAYER);
			jDesktopPane1.add(getJButton11(), JLayeredPane.DEFAULT_LAYER);
		}
		return jDesktopPane1;
	}

	private JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("STEP 1      ENVIRONMENT");
			jLabel2.setBounds(12, 11, 158, 17);
			jLabel2.setForeground(new java.awt.Color(255,255,255));
		}
		return jLabel2;
	}

	private JButton getJButton10() {
		if(jButton10 == null) {
			jButton10 = new JButton();
			jButton10.setText("Add Chemical Environment");
			jButton10.setBounds(8, 72, 177, 28);
			jButton10.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton10ActionPerformed(evt);
				}
			});
		}
		return jButton10;
	}
	
	private JDesktopPane getJDesktopPane2() {
		if(jDesktopPane2 == null) {
			jDesktopPane2 = new JDesktopPane();
			jDesktopPane2.setBounds(8, 142, 195, 74);
			jDesktopPane2.add(getJLabel17(), JLayeredPane.DEFAULT_LAYER);
			jDesktopPane2.add(getJButton1(), JLayeredPane.DEFAULT_LAYER);
		}
		return jDesktopPane2;
	}
	
	private JLabel getJLabel17() {
		if(jLabel17 == null) {
			jLabel17 = new JLabel();
			jLabel17.setText("STEP 2       BACTERIA");
			jLabel17.setForeground(new java.awt.Color(255,255,255));
			jLabel17.setBounds(12, 11, 136, 17);
		}
		return jLabel17;
	}
	
	private JDesktopPane getJDesktopPane3() {
		if(jDesktopPane3 == null) {
			jDesktopPane3 = new JDesktopPane();
			jDesktopPane3.setBounds(9, 230, 195, 107);
			jDesktopPane3.add(getJLabel19(), JLayeredPane.DEFAULT_LAYER);
			jDesktopPane3.add(getJComboBox11(), JLayeredPane.DEFAULT_LAYER);
			{
				writeButton = new JButton();
				jDesktopPane3.add(writeButton, JLayeredPane.DEFAULT_LAYER);
				writeButton.setLayout(null);
				writeButton.setText("Write");
				writeButton.setAction(getWriteAction());
				writeButton.setBounds(13, 70, 166, 26);
			}
		}
		return jDesktopPane3;
	}
	
	private JLabel getJLabel19() {
		if(jLabel19 == null) {
			jLabel19 = new JLabel();
			jLabel19.setText("STEP 3     OUTPUT");
			jLabel19.setForeground(new java.awt.Color(255,255,255));
			jLabel19.setBounds(12, 11, 116, 17);
		}
		return jLabel19;
	}
	
	private JComboBox getJComboBox11() {
		if(jComboBox11 == null) {
			ComboBoxModel jComboBox11Model = 
				new DefaultComboBoxModel(
						new String[] { "Preview", "Export as Video", "Export as Pictures", "Export data" });
			jComboBox11 = new JComboBox();
			jComboBox11.setModel(jComboBox11Model);
			jComboBox11.setBounds(13, 38, 166, 26);
		}
		return jComboBox11;
	}

	private void jButton11ActionPerformed(ActionEvent evt) {
		//System.out.println("jButton11.actionPerformed, event="+evt);
		getJDialog8().pack();
		getJDialog8().setLocationRelativeTo(null);
		getJDialog8().setVisible(true);
	}
	
	private JButton getJButton11() {
		if(jButton11 == null) {
			jButton11 = new JButton();
			jButton11.setText("Set Simulation Options");
			jButton11.setBounds(7, 37, 177, 28);
			jButton11.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton11ActionPerformed(evt);
				}
			});
		}
		return jButton11;
	}
	
	private JDialog getJDialog8() {
		if(jDialog8 == null) {
			jDialog8 = new JDialog(this);
			jDialog8.setPreferredSize(new java.awt.Dimension(264, 168));
			jDialog8.getContentPane().add(getJPanel26(), BorderLayout.EAST);
			jDialog8.getContentPane().add(getJPanel27(), BorderLayout.WEST);
			jDialog8.getContentPane().add(getJPanel28(), BorderLayout.SOUTH);
			jDialog8.setSize(264, 168);
		}
		return jDialog8;
	}

	private JPanel getJPanel26() {
		if(jPanel26 == null) {
			jPanel26 = new JPanel();
			jPanel26.setPreferredSize(new java.awt.Dimension(109, 77));
			jPanel26.setLayout(null);
			jPanel26.add(getJTextField4());
			jPanel26.add(getJTextField37());
			jPanel26.add(getJTextField38());
			jPanel26.add(getJTextField39());
			jPanel26.add(getJTextField40());
		}
		return jPanel26;
	}
	
	private JPanel getJPanel27() {
		if(jPanel27 == null) {
			jPanel27 = new JPanel();
			jPanel27.setPreferredSize(new java.awt.Dimension(136, 42));
			jPanel27.add(getJLabel13());
			jPanel27.add(getJLabel47());
			jPanel27.add(getJLabel48());
		}
		return jPanel27;
	}
	
	private JLabel getJLabel13() {
		if(jLabel13 == null) {
			jLabel13 = new JLabel();
			jLabel13.setText("Set Simulation Size:");
			jLabel13.setPreferredSize(new java.awt.Dimension(117, 29));
		}
		return jLabel13;
	}
	
	private JPanel getJPanel28() {
		if(jPanel28 == null) {
			jPanel28 = new JPanel();
			jPanel28.setPreferredSize(new java.awt.Dimension(276, 30));
			jPanel28.add(getJButton12());
		}
		return jPanel28;
	}
	
	private JTextField getJTextField4() {
		if(jTextField4 == null) {
			jTextField4 = new JTextField();
			jTextField4.setText("100,100,100");
			jTextField4.setBounds(14, 5, 81, 28);
		}
		return jTextField4;
	}
	
	private JButton getJButton12() {
		if(jButton12 == null) {
			jButton12 = new JButton();
			jButton12.setText("OK");
			jButton12.setPreferredSize(new java.awt.Dimension(72, 24));
			jButton12.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton12ActionPerformed(evt);
				}
			});
		}
		return jButton12;
	}
	
	private void jButton12ActionPerformed(ActionEvent evt) {
		String drawerGeneral1f = "drawerGeneral1f";
		String simulation = jTextField4.getText();
		String boundaries = jTextField37.getText();
		String Angle1 = jTextField38.getText();
		String Angle2 = jTextField39.getText();
		String Angle3 = jTextField40.getText();
		startDimensions1 = "sim.setBound("+simulation+");";
		startDimensions1 = "sim.setSolid("+boundaries+");";
        drawerGeneral1 = ReadTextFileExample(drawerGeneral1f, CustomDrawer);
		drawerGeneral1 += "\n" + "p3d.camera(-(float)bound.x*" + Angle1 + "f,-(float)bound.y*" + Angle2 + "f,-(float)bound.z*" + Angle3 + "f,(float)bound.x,(float)bound.y,(float)bound.z,0,1,0);";
		
		String SimulationText = "Simulation Space Size (x,y,z): " + simulation + "\n" + "Simulation Space boundaries (x,y,z): " + boundaries + "\n" + "Disaplay Angle (x,y,z): " + Angle1 + "," + Angle2 + "," + Angle3;
		getJTextArea0().setText(getJTextArea0().getText() + SimulationText);
		this.getJDialog8().dispose();
	}
	
	private JLabel getJLabel32() {
		if(jLabel32 == null) {
			jLabel32 = new JLabel();
			jLabel32.setText("Motion:");
			jLabel32.setPreferredSize(new java.awt.Dimension(90, 27));
		}
		return jLabel32;
	}
	
	private JComboBox getJComboBox12() {
		if(jComboBox12 == null) {
			ComboBoxModel jComboBox12Model = 
				new DefaultComboBoxModel(
						new String[] { "Stationary", "Randomly moving" });
			jComboBox12 = new JComboBox();
			jComboBox12.setModel(jComboBox12Model);
			jComboBox12.setPreferredSize(new java.awt.Dimension(89, 26));
		}
		return jComboBox12;
	}
	
	private JDialog getAddField() {
		if(addField == null) {
			addField = new JDialog(this);
			addField.setPreferredSize(new java.awt.Dimension(175, 170));
			addField.getContentPane().add(getJPanel29(), BorderLayout.SOUTH);
			addField.getContentPane().add(getJPanel30(), BorderLayout.WEST);
			addField.getContentPane().add(getJPanel31(), BorderLayout.EAST);
			addField.setSize(175, 170);
		}
		return addField;
	}
	
	private JPanel getJPanel29() {
		if(jPanel29 == null) {
			jPanel29 = new JPanel();
			jPanel29.setPreferredSize(new java.awt.Dimension(203, 39));
			jPanel29.add(getJButton13());
		}
		return jPanel29;
	}
	
	private JPanel getJPanel30() {
		if(jPanel30 == null) {
			jPanel30 = new JPanel();
			jPanel30.setPreferredSize(new java.awt.Dimension(89, 114));
			jPanel30.setLayout(null);
			jPanel30.add(getJLabel33());
			jPanel30.add(getJLabel34());
			jPanel30.add(getJLabel35());
		}
		return jPanel30;
	}
	
	private JPanel getJPanel31() {
		if(jPanel31 == null) {
			jPanel31 = new JPanel();
			jPanel31.setPreferredSize(new java.awt.Dimension(77, 114));
			jPanel31.add(getJTextField10());
			jPanel31.add(getJTextField12());
			jPanel31.add(getJComboBox13());
		}
		return jPanel31;
	}
	
	private JLabel getJLabel33() {
		if(jLabel33 == null) {
			jLabel33 = new JLabel();
			jLabel33.setText("Diffusivity:");
			jLabel33.setBounds(15, 0, 80, 42);
		}
		return jLabel33;
	}
	
	private JLabel getJLabel34() {
		if(jLabel34 == null) {
			jLabel34 = new JLabel();
			jLabel34.setText("Decay Rate:");
			jLabel34.setBounds(15, 34, 66, 40);
		}
		return jLabel34;
	}
	
	private JTextField getJTextField10() {
		if(jTextField10 == null) {
			jTextField10 = new JTextField();
			jTextField10.setText("890");
			jTextField10.setPreferredSize(new java.awt.Dimension(36, 28));
		}
		return jTextField10;
	}
	
	private JTextField getJTextField12() {
		if(jTextField12 == null) {
			jTextField12 = new JTextField();
			jTextField12.setText("0.9");
			jTextField12.setPreferredSize(new java.awt.Dimension(37, 28));
		}
		return jTextField12;
	}
	
	private JButton getJButton13() {
		if(jButton13 == null) {
			jButton13 = new JButton();
			jButton13.setText("Add Chemical Field");
			jButton13.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton13ActionPerformed(evt);
				}
			});
		}
		return jButton13;
	}
	
	private void jButton10ActionPerformed(ActionEvent evt) {
		fieldNum += 1;
		fieldChoice = new String[fieldNum];
		for (Integer x = 0 ; x < fieldChoice.length ; x++) {  
			Integer fieldCustomNum = x+1;
			fieldChoice[x] = "Field "+ fieldCustomNum.toString();
		}
		getAddField().pack();
		getAddField().setLocationRelativeTo(null);
		getAddField().setVisible(true);
		
	}
	
	private void jButton13ActionPerformed(ActionEvent evt) {
		startField ="";
		drawerField = "";
		tickerField = "";
		String diffusivity = jTextField10.getText();
		String decayRate = jTextField12.getText();
		startField = "final BSimChemicalField field = new BSimChemicalField(sim, new int[]{20,20,20},"+diffusivity+","+decayRate+");";
		startField = popNumUpdate(startField, "field", fieldNum);
		tickerField = "field.update();";
		tickerField = popNumUpdate(tickerField, "field", fieldNum);
	
		String fieldColour = null;
		
		Integer index5 = jComboBox13.getSelectedIndex();
		if (index5==0) {
			fieldColour = "RED";
		}
		else if (index5==1) {
			fieldColour = "GREEN";
			}
		else if (index5==2) {
			fieldColour = "BLUE";
			}
		else if (index5==3) {
			fieldColour = "YELLOW";
			}
		else {
			fieldColour = "PINK";
			}	
		
		drawerField = "draw(field, Color."+fieldColour+", (float)(255/12.0e4));";
		drawerField = popNumUpdate(drawerField, "field", fieldNum);
		
		startField1 += startField;
		drawerField1 += drawerField;
		tickerField1 += tickerField;
		
		if (fieldNum ==1)   getJTextArea0().setText(getJTextArea0().getText() + "\n" + "\n" + "Chemical field(s) added:");
		
		String fieldText = "- field :  diffusivity = " + diffusivity + "    decay rate: " + decayRate + "    colour: " + fieldColour;
		fieldText = popNumUpdate(fieldText, "field", fieldNum);
		getJTextArea0().setText(getJTextArea0().getText() + "\n" + fieldText);
		
		this.getAddField().dispose();
	}
	
	private JLabel getJLabel35() {
		if(jLabel35 == null) {
			jLabel35 = new JLabel();
			jLabel35.setText("Colour:");
			jLabel35.setBounds(15, 72, 66, 24);
		}
		return jLabel35;
	}
	
	private JComboBox getJComboBox13() {
		if(jComboBox13 == null) {
			ComboBoxModel jComboBox13Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink" });
			jComboBox13 = new JComboBox();
			jComboBox13.setModel(jComboBox13Model);
			jComboBox13.setPreferredSize(new java.awt.Dimension(69, 25));
		}
		return jComboBox13;
	}
	
	private JDialog getAddBacteriaDialog() {
		if(addBacteriaDialog == null) {
			addBacteriaDialog = new JDialog(this);
			addBacteriaDialog.getContentPane().setLayout(null);
			addBacteriaDialog.setPreferredSize(new java.awt.Dimension(576, 441));
			addBacteriaDialog.getContentPane().add(getJPanel35());
			addBacteriaDialog.getContentPane().add(getJPanel36());
			addBacteriaDialog.getContentPane().add(getJPanel38());
			addBacteriaDialog.getContentPane().add(getJPanel40());
			addBacteriaDialog.getContentPane().add(getJButton14());
			addBacteriaDialog.getContentPane().add(getJPanel33());
			addBacteriaDialog.getContentPane().add(getJButton15());
			addBacteriaDialog.getContentPane().add(getJPanel32());
			addBacteriaDialog.setSize(576, 441);
		}
		return addBacteriaDialog;
	}

	private JPanel getJPanel33() {
		if(jPanel33 == null) {
			jPanel33 = new JPanel();
			jPanel33.setLayout(null);
			jPanel33.setBackground(new java.awt.Color(205,205,205));
			jPanel33.setBounds(6, 49, 254, 164);
			jPanel33.add(getChemicalRecieverCheck());
			jPanel33.add(getJPanel34());
		}
		return jPanel33;
	}
	
	private JCheckBox getChemicalRecieverCheck() {
		if(chemicalRecieverCheck == null) {
			chemicalRecieverCheck = new JCheckBox();
			chemicalRecieverCheck.setText("Chemical Receiver");
			chemicalRecieverCheck.setBackground(new java.awt.Color(255,255,255));
			chemicalRecieverCheck.setPreferredSize(new java.awt.Dimension(126, 18));
			chemicalRecieverCheck.setBounds(5, 14, 126, 18);
			chemicalRecieverCheck.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					chemicalRecieverCheckActionPerformed(evt);
				}
			});
		}
		return chemicalRecieverCheck;
	}
	
	private JPanel getJPanel34() {
		if(jPanel34 == null) {
			jPanel34 = new JPanel();
			jPanel34.setBounds(6, 38, 242, 120);
			jPanel34.setLayout(null);
			jPanel34.add(getRecieverFieldChoice());
			jPanel34.add(getJTextPane3());
			jPanel34.add(getJTextPane5());
			jPanel34.add(getJRadioButton1());
			jPanel34.add(getJRadioButton2());
			jPanel34.add(getJComboBox16());
			jPanel34.add(getJTextField26());
		}
		return jPanel34;
	}
	
	private JComboBox getRecieverFieldChoice() {
		if(recieverFieldChoice == null) {
			ComboBoxModel recieverFieldChoiceModel = 
				new DefaultComboBoxModel(fieldChoice);
			recieverFieldChoice = new JComboBox();
			recieverFieldChoice.setModel(recieverFieldChoiceModel);
			recieverFieldChoice.setBounds(104, 2, 83, 28);
		}
		return recieverFieldChoice;
	}
	
	private JComboBox getJComboBox16() {
		if(jComboBox16 == null) {
			ComboBoxModel jComboBox16Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink" });
			jComboBox16 = new JComboBox();
			jComboBox16.setModel(jComboBox16Model);
			jComboBox16.setBounds(102, 86, 84, 26);
		}
		return jComboBox16;
	}
	
	private JTextPane getJTextPane3() {
		if(jTextPane3 == null) {
			jTextPane3 = new JTextPane();
			jTextPane3.setText("Reacts to field #");
			jTextPane3.setFont(new java.awt.Font("SansSerif",0,10));
			jTextPane3.setBounds(15, 6, 89, 22);
		}
		return jTextPane3;
	}
	
	private JTextPane getJTextPane5() {
		if(jTextPane5 == null) {
			jTextPane5 = new JTextPane();
			jTextPane5.setText("Signal Colour");
			jTextPane5.setFont(new java.awt.Font("SansSerif",0,10));
			jTextPane5.setBounds(15, 90, 81, 22);
		}
		return jTextPane5;
	}
	
	private JRadioButton getJRadioButton1() {
		if(jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setText("Signalling above Threshold:");
			jRadioButton1.setFont(new java.awt.Font("SansSerif",0,10));
			jRadioButton1.setBounds(15, 40, 163, 18);
			jRadioButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jRadioButton1ActionPerformed(evt);
				}
			});
		}
		return jRadioButton1;
	}
	
	private JRadioButton getJRadioButton2() {
		if(jRadioButton2 == null) {
			jRadioButton2 = new JRadioButton();
			jRadioButton2.setText("ODE Signalling");
			jRadioButton2.setFont(new java.awt.Font("SansSerif",0,10));
			jRadioButton2.setBounds(15, 62, 109, 18);
			jRadioButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jRadioButton2ActionPerformed(evt);
				}
			});
		}
		return jRadioButton2;
	}
	
	private JPanel getJPanel35() {
		if(jPanel35 == null) {
			jPanel35 = new JPanel();
			jPanel35.setBackground(new java.awt.Color(205,205,205));
			jPanel35.setBounds(6, 6, 254, 37);
			jPanel35.setLayout(null);
			jPanel35.add(getJComboBox2());
			jPanel35.add(getJLabel38());
			jPanel35.add(getJComboBox18());
		}
		return jPanel35;
	}
	
	private JComboBox getJComboBox2() {
		if(jComboBox2 == null) {
			ComboBoxModel jComboBox2Model = 
				new DefaultComboBoxModel(
						new String[] { "Stationary", "Randomly Moving" });
			jComboBox2 = new JComboBox();
			jComboBox2.setModel(jComboBox2Model);
			jComboBox2.setBounds(5, 5, 87, 26);
		}
		return jComboBox2;
	}
	
	private JPanel getJPanel36() {
		if(jPanel36 == null) {
			jPanel36 = new JPanel();
			jPanel36.setBackground(new java.awt.Color(205,205,205));
			jPanel36.setBounds(7, 219, 254, 157);
			jPanel36.setLayout(null);
			jPanel36.add(getJCheckBox1());
			jPanel36.add(getJPanel37());
		}
		return jPanel36;
	}
	
	private JCheckBox getJCheckBox1() {
		if(jCheckBox1 == null) {
			jCheckBox1 = new JCheckBox();
			jCheckBox1.setText("Chemical Creator");
			jCheckBox1.setBackground(new java.awt.Color(255,255,255));
			jCheckBox1.setBounds(21, 5, 229, 26);
			jCheckBox1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jCheckBox1ActionPerformed(evt);
				}
			});
		}
		return jCheckBox1;
	}
	
	private JPanel getJPanel37() {
		if(jPanel37 == null) {
			jPanel37 = new JPanel();
			jPanel37.setBackground(new java.awt.Color(255,255,255));
			jPanel37.setBounds(6, 39, 243, 113);
			jPanel37.setLayout(null);
			jPanel37.add(getJTextPane1());
			jPanel37.add(getJTextField14());
			jPanel37.add(getJTextPane6());
			jPanel37.add(getJRadioButton4());
			jPanel37.add(getJRadioButton3());
			jPanel37.add(getJComboBox14());
		}
		return jPanel37;
	}
	
	private JTextPane getJTextPane1() {
		if(jTextPane1 == null) {
			jTextPane1 = new JTextPane();
			jTextPane1.setText("Production Rate (molecules per second)");
			jTextPane1.setBackground(new java.awt.Color(255,255,128));
			jTextPane1.setFont(new java.awt.Font("SansSerif",0,10));
			jTextPane1.setBounds(6, 37, 202, 25);
		}
		return jTextPane1;
	}
	
	private JTextField getJTextField14() {
		if(jTextField14 == null) {
			jTextField14 = new JTextField();
			jTextField14.setText("2e9");
			jTextField14.setBounds(163, 63, 68, 26);
		}
		return jTextField14;
	}

	private JTextPane getJTextPane6() {
		if(jTextPane6 == null) {
			jTextPane6 = new JTextPane();
			jTextPane6.setText("Adds to field #");
			jTextPane6.setFont(new java.awt.Font("SansSerif",0,10));
			jTextPane6.setBounds(126, 6, 80, 24);
		}
		return jTextPane6;
	}

	private JRadioButton getJRadioButton4() {
		if(jRadioButton4 == null) {
			jRadioButton4 = new JRadioButton();
			jRadioButton4.setText("ODE production rate function");
			jRadioButton4.setFont(new java.awt.Font("SansSerif",0,10));
			jRadioButton4.setBounds(6, 91, 166, 15);
			jRadioButton4.setEnabled(false);
			jRadioButton4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jRadioButton4ActionPerformed(evt);
				}
			});
		}
		return jRadioButton4;
	}

	private JPanel getJPanel38() {
		if(jPanel38 == null) {
			jPanel38 = new JPanel();
			jPanel38.setLayout(null);
			jPanel38.setBackground(new java.awt.Color(205,205,205));
			jPanel38.setBounds(266, 6, 297, 187);
			jPanel38.add(getJCheckBox3());
			jPanel38.add(getJPanel42());
		}
		return jPanel38;
	}
	
	private JCheckBox getJCheckBox3() {
		if(jCheckBox3 == null) {
			jCheckBox3 = new JCheckBox();
			jCheckBox3.setText("Chemotaxis");
			jCheckBox3.setBackground(new java.awt.Color(255,255,255));
			jCheckBox3.setBounds(6, 6, 88, 22);
			jCheckBox3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jCheckBox3ActionPerformed(evt);
				}
			});
		}
		return jCheckBox3;
	}

	private JPanel getJPanel40() {
		if(jPanel40 == null) {
			jPanel40 = new JPanel();
			jPanel40.setLayout(null);
			jPanel40.setBackground(new java.awt.Color(205,205,205));
			jPanel40.setBounds(268, 199, 295, 86);
			jPanel40.add(getJCheckBox2());
			jPanel40.add(getJPanel41());
		}
		return jPanel40;
	}
	
	private JCheckBox getJCheckBox2() {
		if(jCheckBox2 == null) {
			jCheckBox2 = new JCheckBox();
			jCheckBox2.setText("Collision Signalling");
			jCheckBox2.setBackground(new java.awt.Color(255,255,255));
			jCheckBox2.setPreferredSize(new java.awt.Dimension(129, 18));
			jCheckBox2.setBounds(5, 14, 129, 18);
			jCheckBox2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jCheckBox2ActionPerformed(evt);
				}
			});
		}
		return jCheckBox2;
	}
	
	private JPanel getJPanel41() {
		if(jPanel41 == null) {
			jPanel41 = new JPanel();
			jPanel41.setBounds(6, 44, 283, 37);
			jPanel41.setLayout(null);
			jPanel41.add(getJComboBox17());
			jPanel41.add(getJTextPane2());
		}
		return jPanel41;
	}
	
	private JComboBox getJComboBox17() {
		if(jComboBox17 == null) {
			ComboBoxModel jComboBox17Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink"});
			jComboBox17 = new JComboBox();
			jComboBox17.setModel(jComboBox17Model);
			jComboBox17.setBounds(5, 5, 135, 26);
			jComboBox17.setEnabled(false);
		}
		return jComboBox17;
	}
	
	private JTextPane getJTextPane2() {
		if(jTextPane2 == null) {
			jTextPane2 = new JTextPane();
			jTextPane2.setText("Colour after Collision");
			jTextPane2.setBackground(new java.awt.Color(255,255,128));
			jTextPane2.setBounds(146, 6, 133, 26);
		}
		return jTextPane2;
	}
	
	private JButton getJButton14() {
		if(jButton14 == null) {
			jButton14 = new JButton();
			jButton14.setText("Next");
			jButton14.setBounds(511, 368, 52, 28);
			jButton14.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton14ActionPerformed(evt);
				}
			});
		}
		return jButton14;
	}
	
	private JRadioButton getJRadioButton3() {
		if(jRadioButton3 == null) {
			jRadioButton3 = new JRadioButton();
			jRadioButton3.setText("Constant Production Rate");
			jRadioButton3.setFont(new java.awt.Font("SansSerif",0,10));
			jRadioButton3.setBounds(5, 68, 146, 14);
			jRadioButton3.setEnabled(false);
			jRadioButton3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jRadioButton3ActionPerformed(evt);
				}
			});
		}
		return jRadioButton3;
	}
	
	private JComboBox getJComboBox14() {
		if(jComboBox14 == null) {
			ComboBoxModel jComboBox14Model = 
				new DefaultComboBoxModel(fieldChoice);
			jComboBox14 = new JComboBox();
			jComboBox14.setModel(jComboBox14Model);
			jComboBox14.setBounds(6, 5, 114, 26);
			jComboBox14.setEditable(true);
			jComboBox14.setEnabled(false);
		}
		return jComboBox14;
	}

	private JPanel getJPanel42() {
		if(jPanel42 == null) {
			jPanel42 = new JPanel();
			jPanel42.setBounds(6, 34, 285, 142);
			jPanel42.setLayout(null);
			jPanel42.add(getJComboBox15());
			jPanel42.add(getJTextPane7());
			jPanel42.add(getJTextField23());
			jPanel42.add(getJTextPane8());
			jPanel42.add(getJTextPane9());
			jPanel42.add(getJTextField24());
			jPanel42.add(getJTextPane10());
			jPanel42.add(getJTextField25());
		}
		return jPanel42;
	}
	
	private JComboBox getJComboBox15() {
		if(jComboBox15 == null) {
			ComboBoxModel jComboBox15Model = 
				new DefaultComboBoxModel(fieldChoice);
			jComboBox15 = new JComboBox();
			jComboBox15.setModel(jComboBox15Model);
			jComboBox15.setBounds(6, 6, 127, 26);
			jComboBox15.setEnabled(false);
		}
		return jComboBox15;
	}
	
	private JTextPane getJTextPane7() {
		if(jTextPane7 == null) {
			jTextPane7 = new JTextPane();
			jTextPane7.setText("Acts on field #");
			jTextPane7.setBounds(139, 8, 86, 21);
		}
		return jTextPane7;
	}
	
	private JTextField getJTextField23() {
		if(jTextField23 == null) {
			jTextField23 = new JTextField();
			jTextField23.setText("1/1.07");
			jTextField23.setBounds(114, 43, 80, 30);
			jTextField23.setEnabled(false);
		}
		return jTextField23;
	}
	
	private JTextPane getJTextPane8() {
		if(jTextPane8 == null) {
			jTextPane8 = new JTextPane();
			jTextPane8.setText("pEndRunUp");
			jTextPane8.setFont(new java.awt.Font("SansSerif",0,12));
			jTextPane8.setBounds(12, 44, 96, 24);
		}
		return jTextPane8;
	}
	
	private JTextPane getJTextPane9() {
		if(jTextPane9 == null) {
			jTextPane9 = new JTextPane();
			jTextPane9.setText("pEndTumble");
			jTextPane9.setBounds(12, 76, 96, 24);
		}
		return jTextPane9;
	}
	
	private JTextField getJTextField24() {
		if(jTextField24 == null) {
			jTextField24 = new JTextField();
			jTextField24.setText("1/0.86");
			jTextField24.setBounds(114, 73, 80, 32);
			jTextField24.setEnabled(false);
		}
		return jTextField24;
	}
	
	private JTextPane getJTextPane10() {
		if(jTextPane10 == null) {
			jTextPane10 = new JTextPane();
			jTextPane10.setText("pEndRunElse");
			jTextPane10.setBounds(12, 106, 96, 24);
		}
		return jTextPane10;
	}
	
	private JTextField getJTextField25() {
		if(jTextField25 == null) {
			jTextField25 = new JTextField();
			jTextField25.setText("1/0.14");
			jTextField25.setBounds(114, 105, 80, 32);
			jTextField25.setEnabled(false);
		}
		return jTextField25;
	}
	
	private void jRadioButton1ActionPerformed(ActionEvent evt) {
		//System.out.println("jRadioButton1.actionPerformed, event="+evt);
		jTextField26.setEnabled(true);
		jRadioButton2.setSelected(false);
	}
	
	private void jRadioButton2ActionPerformed(ActionEvent evt) {
		//System.out.println("jRadioButton2.actionPerformed, event="+evt);
		if (jRadioButton2.isSelected() == true)   {
		jRadioButton1.setSelected(false);
		jTextField26.setEnabled(false);
		}
		else  jTextField26.setEnabled(true);
	}
	
	private void chemicalRecieverCheckActionPerformed(ActionEvent evt) {
		//System.out.println("chemicalRecieverCheck.actionPerformed, event="+evt);
		if (chemicalRecieverCheck.isSelected() == true){
			recieverFieldChoice.setEnabled(true);
			jRadioButton1.setEnabled(true);
			jRadioButton2.setEnabled(true);
			jComboBox16.setEnabled(true); }
		else {
		recieverFieldChoice.setEnabled(false);
		jRadioButton1.setEnabled(false);
		jRadioButton2.setEnabled(false);
		jComboBox16.setEnabled(false); }
	}
	
	private void jRadioButton3ActionPerformed(ActionEvent evt) {
		//System.out.println("jRadioButton3.actionPerformed, event="+evt);
		jRadioButton4.setSelected(false);
	}
	
	private void jRadioButton4ActionPerformed(ActionEvent evt) {
		//System.out.println("jRadioButton4.actionPerformed, event="+evt);
		if (jRadioButton4.isSelected() == true) {
		jRadioButton3.setSelected(false);
		jTextField14.setEnabled(false);
		}
		else  jTextField14.setEnabled(true);
	}
	
	private void jRadioButton5ActionPerformed(ActionEvent evt) {
		//System.out.println("jRadioButton5.actionPerformed, event="+evt);
		jRadioButton3.setSelected(false);
		jRadioButton4.setSelected(false);
	}

	private JButton getJButton15() {
		if(jButton15 == null) {
			jButton15 = new JButton();
			jButton15.setText("Cancel");
			jButton15.setBounds(432, 368, 67, 28);
			jButton15.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButton15ActionPerformed(evt);
				}
			});
		}
		return jButton15;
	}
	
	private JPanel getJPanel32() {
		if(jPanel32 == null) {
			jPanel32 = new JPanel();
			jPanel32.setBounds(267, 291, 296, 70);
			jPanel32.setBackground(new java.awt.Color(205,205,205));
			jPanel32.setLayout(null);
			jPanel32.add(getJPanel39());
		}
		return jPanel32;
	}
	
	private JPanel getJPanel39() {
		if(jPanel39 == null) {
			jPanel39 = new JPanel();
			jPanel39.setBounds(6, 6, 284, 58);
			jPanel39.setLayout(null);
			jPanel39.add(getJLabel36());
			jPanel39.add(getJTextField27());
			jPanel39.add(getJLabel37());
			jPanel39.add(getJTextField28());
			jPanel39.add(getJComboBox19());
		}
		return jPanel39;
	}
	
	private JLabel getJLabel36() {
		if(jLabel36 == null) {
			jLabel36 = new JLabel();
			jLabel36.setText("Population size");
			jLabel36.setBounds(11, 7, 84, 23);
		}
		return jLabel36;
	}
	
	private JTextField getJTextField27() {
		if(jTextField27 == null) {
			jTextField27 = new JTextField();
			jTextField27.setText("1");
			jTextField27.setBounds(99, 1, 60, 28);
		}
		return jTextField27;
	}
	
	private void jButton15ActionPerformed(ActionEvent evt) {
		//System.out.println("jButton15.actionPerformed, event="+evt);
		this.getAddBacteriaDialog().dispose();
	}
	
	private void jCheckBox1ActionPerformed(ActionEvent evt) {
		//System.out.println("jCheckBox1.actionPerformed, event="+evt);
		if (jCheckBox1.isSelected() == true) {
		jComboBox14.setEnabled(true);
		jRadioButton3.setEnabled(true);
		jRadioButton4.setEnabled(true);}
		else {
			jComboBox14.setEnabled(false);
		jRadioButton3.setEnabled(false);
		jRadioButton4.setEnabled(false);}
	}
	
	private void jCheckBox3ActionPerformed(ActionEvent evt) {
		//System.out.println("jCheckBox3.actionPerformed, event="+evt);
		if (jCheckBox3.isSelected() == true) {
		jComboBox15.setEnabled(true);
		jTextField23.setEnabled(true);
		jTextField24.setEnabled(true);
		jTextField25.setEnabled(true);  }
		else {
			jComboBox15.setEnabled(false);
		jTextField23.setEnabled(false);
		jTextField24.setEnabled(false);
		jTextField25.setEnabled(false); }
	}
	
	private void jCheckBox2ActionPerformed(ActionEvent evt) {
		//System.out.println("jCheckBox2.actionPerformed, event="+evt);
		if (jCheckBox2.isSelected() == true) 	jComboBox17.setEnabled(true);
		else jComboBox17.setEnabled(false);
		
	}
	
	private void jButton14ActionPerformed(ActionEvent evt) {
	    String bacteriaText = ""; 
		startParameters1 = "";
		classThresholdSignalling1 = "";
		classChemicalCreator1 = "";
		classChemotaxis1 = "";
		tickerCollision1 = "";
		classPosition = "";
		tickerGeneral2 = "";
		classGeneral1 = "";
		classGeneral2 = "";
		classGeneral3 = "";
		classODEsignalling1 = "";
		drawerGeneral2 = "";
		ParameterChunk = "";
		ClassChunk = "";
		TickerChunk = "";
		DrawerChunk = "";
		String classThresholdSignalling1f = "classThresholdSignalling1f";
		String classPositionA = "classPositionA";
		
		if (ClickNum ==1)  bacteriaText = "\n\n" + "Bacteria population(s) added: " + "\n";
		bacteriaText += "- bacteria :   motion: ";
		bacteriaText = popNumUpdate(bacteriaText, "bacteria", ClickNum);
		int movingIndex = jComboBox2.getSelectedIndex();
		//String tickerMoving1;
		if (movingIndex == 0)  {
			tickerMoving1 = "";
			bacteriaText += "stationary";
		}
		else {
			tickerMoving1 = "bacterium.updatePosition();"; 
			bacteriaText += "randomly moving";
		}
		bacteriaText += "    initial colour: ";
		
		int simpleColourIndex = jComboBox18.getSelectedIndex();
		if (simpleColourIndex == 0)  {
			startParameters1 += "\n" + 	"static Color simpleColour = new Color(255,0,0);";  //red
			bacteriaText += "RED";
		}
		if (simpleColourIndex == 1)  {
			startParameters1 += "\n" + 	"static Color simpleColour = new Color(0,255,0);";  //green
			bacteriaText += "GREEN";
		}
		if (simpleColourIndex == 2)  {
			startParameters1 += "\n" + 	"static Color simpleColour = new Color(0,0,255);";  //blue
			bacteriaText += "BLUE";
		}
		if (simpleColourIndex == 3)  {
			startParameters1 += "\n" + 	"static Color simpleColour = new Color(255,255,0);";  //yellow
			bacteriaText += "YELLOW";
		}
		if (simpleColourIndex == 4)  {
			startParameters1 += "\n" + 	"static Color simpleColour = new Color(255,20,147);";  //pink
			bacteriaText += "PINK";
		}
		bacteriaText += "\n" + "    functions: ";
		if (chemicalRecieverCheck.isSelected() == true) {
			bacteriaText +=  "\n" + "    chemical receiver: ";
			Integer receiverFieldIndex = (recieverFieldChoice.getSelectedIndex()) + 1; //starting from zero!
			bacteriaText += "reacts to field" + receiverFieldIndex.toString() + ", ";
			if (jRadioButton1.isSelected() == true) {	
			String thresholdConstant = jTextField26.getText();
			startParameters1 += "\n" + 	"static  double threshold = " + thresholdConstant + ";"; 
			classThresholdSignalling1 = ReadTextFileExample(classThresholdSignalling1f, CustomClass);	
			classThresholdSignalling1 = popNumUpdate(classThresholdSignalling1, "field", receiverFieldIndex);
			bacteriaText += "signals above threshold " + thresholdConstant + ", signal colour ";
			}
			int signalColourIndex = jComboBox16.getSelectedIndex();

			if (jRadioButton1.isSelected() == true) {
				if (signalColourIndex == 0)  {
					startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(255,0,0);";  //red
					bacteriaText += "RED";
				}
				if (signalColourIndex == 1)  {
					startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(0,255,0);";  //green
					bacteriaText += "GREEN";
				}
				if (signalColourIndex == 2)  {
					startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(0,0,255);";  //blue
					bacteriaText += "BLUE";
				}
				if (signalColourIndex == 3)  {
					startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(255,255,0);";  //yellow
					bacteriaText += "YELLOW";
				}
				if (signalColourIndex == 4)  {
					startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(255,20,147);";  //pink
					bacteriaText += "PINK";
				}
			}
			else startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(255,0,0);";  //red
			
			if (jRadioButton2.isSelected() == true) {
				getDrawerTextArea().setEnabled(true);
				bacteriaText += "signals according to ODEs";
			}
			
		}
		else startParameters1 += "\n" + 	"static Color thresSignallingColour = new Color(255,0,0);";  //red
		
		Integer creatorFieldIndex = (jComboBox14.getSelectedIndex()) + 1; //starting from zero!
		creatorFieldIndexS = creatorFieldIndex.toString();
		if (jCheckBox1.isSelected() == true) {
			bacteriaText +=  "\n" + "    chemical creator: adds to field" + creatorFieldIndex + ", ";
			String productionConstant = jTextField14.getText();
			if (jRadioButton3.isSelected()== true) 	{
				//startParameters1 += "\n" + 	"static  double productionRate = " + productionConstant + ";"; 
				classChemicalCreator1 = "field"+creatorFieldIndexS+".addQuantity(position, " + productionConstant + "*sim.getDt());";
				bacteriaText +=  "constant production rate " + productionConstant;
			}
			if (jRadioButton4.isSelected() == true)  {
				getJTextArea5().setEnabled(true);
				bacteriaText +=  "ODE production rate function";
			}
		}
		String classChemotaxis = "classChemotaxis";
		if (jCheckBox3.isSelected() == true) {
			classChemotaxis1 = ReadTextFileExample(classChemotaxis,CustomClass);
			int chemotaxisFieldIndex = (jComboBox15.getSelectedIndex()) + 1; //starting from zero!
			classChemotaxis1 = popNumUpdate(classChemotaxis1, "field", chemotaxisFieldIndex);
			bacteriaText +=  "\n" + "    chemotaxis: acts on field" + chemotaxisFieldIndex + ", ";
			String pEndRunUp = jTextField23.getText();
			String pEndTumble = jTextField24.getText();
			String pEndRunElse = jTextField25.getText();
			String chemotaxisParameters = "static double runUp = " + pEndRunUp + ", runElse = " + pEndRunElse + ", tumble = " + pEndTumble + ";";
			bacteriaText += "pEndRunUp = " + pEndRunUp + ", pEndTumble = " + pEndTumble +  ", pEndRunElse = " + pEndRunElse;
			
			startParameters1 += "\n" + chemotaxisParameters; 
		}
		
		
		String tickerCollision = "tickerCollision";
		int collisionColourIndex = jComboBox17.getSelectedIndex();
	if (jCheckBox2.isSelected() == true) {
		bacteriaText +=  "\n" + "    collision signalling: colour after collision ";
		tickerCollision1 = ReadTextFileExample(tickerCollision,CustomTicker);
		if (collisionColourIndex == 0)  {
			startParameters1 += "\n" + 	"static Color collisionColour = new Color(255,0,0);";  //red
			bacteriaText += "RED";
		}
		if (collisionColourIndex == 1)  {
			startParameters1 += "\n" + 	"static Color collisionColour = new Color(0,255,0);";  //green
			bacteriaText += "GREEN";
		}
		if (collisionColourIndex == 2)  {
			startParameters1 += "\n" + 	"static Color collisionColour = new Color(0,0,255);";  //blue
			bacteriaText += "BLUE";
		}
		if (collisionColourIndex == 3)  {
			startParameters1 += "\n" + 	"static Color collisionColour = new Color(255,255,0);";  //yellow
			bacteriaText += "YELLOW";
		}
		if (collisionColourIndex == 4)  {
			startParameters1 += "\n" + 	"static Color collisionColour = new Color(255,20,147);";  //pink
			bacteriaText += "PINK";
		}
	}
	else startParameters1 += "\n" + 	"static Color collisionColour = new Color(255,0,0);";  //red
	String populationSize = jTextField27.getText();
	bacteriaText +=  "\n" + "    population size: " + populationSize;
	startParameters1 += "\n" + 	"static  int populationSize = " + populationSize + ";";
	
	if (jTextField28.isEnabled() == true)  {
		String initialPosition = jTextField28.getText();
		bacteriaText +=  ", initial position (" + initialPosition + ")";
		classPosition = "CustomBacterium bacterium = new CustomBacterium(sim, new Vector3d("+initialPosition+"));"+"\n"+"bacteria.add(bacterium);"; }
	else classPosition = ReadTextFileExample(classPositionA, CustomClass);
	
	bacteriaText += "\n";
	getJTextArea0().setText(getJTextArea0().getText() + bacteriaText);
	
	String tickerGeneral2f = "tickerGeneral2f";
	String classGeneral1f = "classGeneral1f";
	String classGeneral2f = "classGeneral2f";
	String classGeneral3f = "classGeneral3f";
	String classGeneral4f = "classGeneral4f";
	String classGeneral5f = "classGeneral5f";
	String drawerGeneral2f = "drawerGeneral2f";
	String drawerGeneral3f = "drawerGeneral3f";
	
	tickerGeneral2 = ReadTextFileExample(tickerGeneral2f,CustomTicker);
	classGeneral1 = ReadTextFileExample(classGeneral1f,CustomClass);
	classGeneral2 = ReadTextFileExample(classGeneral2f,CustomClass);
	classGeneral3 = ReadTextFileExample(classGeneral3f,CustomClass);
	classGeneral4 = ReadTextFileExample(classGeneral4f,CustomClass);
	classGeneral5 = ReadTextFileExample(classGeneral5f,CustomClass);
	
	drawerGeneral2 = ReadTextFileExample(drawerGeneral2f,CustomDrawer);
	drawerGeneral3 = ReadTextFileExample(drawerGeneral3f,CustomDrawer);
	
	if (jRadioButton2.isSelected() == true)    classODEsignalling1 = "ODEsignalling = true;";
		
		if (jRadioButton2.isSelected() == true || jRadioButton4.isSelected() == true ) {
		
		getOdeSignallingDialog().pack();
		getOdeSignallingDialog().setLocationRelativeTo(null);
		getOdeSignallingDialog().setVisible(true); }
		else { 
			ParameterChunk += "\n" + "static int numEq = 0;";

			drawerGeneral2 += "\n" + "draw(bacterium,new Color(4*(int)bacterium.y[2],255 - 4*(int)bacterium.y[2],0));";
			classODEsignalling4 = "double[] ics = {1,0};";
			classODEsignalling3 = "dy[0] = -1*y[0] + 1*Math.pow(y[1],2);\r\ndy[1] = -1*y[1] + 1*y[0];";
			
			ParameterChunk += startParameters1;
			ClassChunk = classGeneral1 + "\n" + classODEsignalling1 + "\n" + classGeneral2 + "\n" + classChemotaxis1 + "\n" + classThresholdSignalling1 + "\n" + classChemicalCreator1 + "\n" + classGeneral3 + classODEsignalling3 + classGeneral4 + classODEsignalling4 + classGeneral5 + classPosition; 
			TickerChunk += tickerCollision1 + "\n" + tickerGeneral2 + "\n" + tickerMoving1 + "\n" + "}";
			DrawerChunk += drawerGeneral2 + "\n" + drawerGeneral3;
			
			ParameterChunk = popNumUpdate(ParameterChunk, "simpleColour", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "threshold", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "thresSignallingColour", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "productionRate", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "runUp", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "runElse", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "tumble", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "collisionColour", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "populationSize", ClickNum); 
			ParameterChunk = popNumUpdate(ParameterChunk, "numEq", ClickNum);
			ParameterChunk = popNumUpdate(ParameterChunk, "ODESignallingColour", ClickNum); 
			
			ClassChunk = popNumUpdate(ClassChunk, "threshold", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "productionRate", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "runUp", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "runElse", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "tumble", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "populationSize", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "bacterium", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "bacteria", ClickNum);
			ClassChunk = popNumUpdate(ClassChunk, "CustomBacterium", ClickNum); 
			ClassChunk = popNumUpdate(ClassChunk, "numEq", ClickNum);
			
			TickerChunk = popNumUpdate(TickerChunk, "bacterium", ClickNum);
			TickerChunk = popNumUpdate(TickerChunk, "bacteria", ClickNum);
			TickerChunk = popNumUpdate(TickerChunk, "CustomBacterium", ClickNum);
			
			DrawerChunk = popNumUpdate(DrawerChunk, "bacterium", ClickNum);
			DrawerChunk = popNumUpdate(DrawerChunk, "bacteria", ClickNum);
			DrawerChunk = popNumUpdate(DrawerChunk, "CustomBacterium", ClickNum);
			DrawerChunk = popNumUpdate(DrawerChunk, "simpleColour", ClickNum);
			DrawerChunk = popNumUpdate(DrawerChunk, "thresSignallingColour", ClickNum);
			DrawerChunk = popNumUpdate(DrawerChunk, "collisionColour", ClickNum);
			DrawerChunk = popNumUpdate(DrawerChunk, "ODESignallingColour", ClickNum); 
			
			ParametersChunkTotal += ParameterChunk;
			ClassChunkTotal += ClassChunk;
			TickerChunkTotal += TickerChunk;
			DrawerChunkTotal += DrawerChunk;
			
		}
		


		
		//System.out.println(classODEsignalling3);
		/*
		ParameterChunk += startParameters1;
		ClassChunk = classGeneral1 + "\n" + classGeneral2 + "\n" + classChemotaxis1 + "\n" + classThresholdSignalling1 + "\n" + classChemicalCreator1 + "\n" + classGeneral3 + classODEsignalling3 + classGeneral4 + classODEsignalling4 + classGeneral5 + classPosition; 
		TickerChunk += tickerCollision1 + "\n" + tickerGeneral2 + "\n" + tickerMoving1 + "\n" + "}";
		DrawerChunk += drawerGeneral2;
		
		ParameterChunk = popNumUpdate(ParameterChunk, "simpleColour", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "threshold", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "thresholdSignallingColour", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "productionRate", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "runUp", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "runElse", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "tumble", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "collisionColour", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "populationSize", ClickNum); 
		ParameterChunk = popNumUpdate(ParameterChunk, "numEq", ClickNum);
		ParameterChunk = popNumUpdate(ParameterChunk, "ODESignallingColour", ClickNum); 
		
		ClassChunk = popNumUpdate(ClassChunk, "threshold", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "productionRate", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "runUp", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "runElse", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "tumble", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "populationSize", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "bacterium", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "bacteria", ClickNum);
		ClassChunk = popNumUpdate(ClassChunk, "CustomBacterium", ClickNum); 
		ClassChunk = popNumUpdate(ClassChunk, "numEq", ClickNum);
		
		TickerChunk = popNumUpdate(TickerChunk, "bacterium", ClickNum);
		TickerChunk = popNumUpdate(TickerChunk, "bacteria", ClickNum);
		TickerChunk = popNumUpdate(TickerChunk, "CustomBacterium", ClickNum);
		
		DrawerChunk = popNumUpdate(DrawerChunk, "bacterium", ClickNum);
		DrawerChunk = popNumUpdate(DrawerChunk, "bacteria", ClickNum);
		DrawerChunk = popNumUpdate(DrawerChunk, "CustomBacterium", ClickNum);
		DrawerChunk = popNumUpdate(DrawerChunk, "simpleColour", ClickNum);
		DrawerChunk = popNumUpdate(DrawerChunk, "thresholdSignallingColour", ClickNum);
		DrawerChunk = popNumUpdate(DrawerChunk, "collisionColour", ClickNum);
		DrawerChunk = popNumUpdate(DrawerChunk, "ODESignallingColour", ClickNum); 
		
		ParametersChunkTotal += ParameterChunk;
		ClassChunkTotal += ClassChunk;
		TickerChunkTotal += TickerChunk;
		DrawerChunkTotal += DrawerChunk; */
		
		System.out.println(startParameters1);
		addBactNum += 1;
		
		this.getAddBacteriaDialog().dispose();
	}
	
	private JLabel getJLabel37() {
		if(jLabel37 == null) {
			jLabel37 = new JLabel();
			jLabel37.setText("Initial position");
			jLabel37.setBounds(11, 30, 76, 28);
		}
		return jLabel37;
	}
	
	private JTextField getJTextField28() {
		if(jTextField28 == null) {
			jTextField28 = new JTextField();
			jTextField28.setText("50,50,50");
			jTextField28.setBounds(99, 30, 60, 28);
			jTextField28.setEnabled(false);
		}
		return jTextField28;
	}
	
	private JLabel getJLabel38() {
		if(jLabel38 == null) {
			jLabel38 = new JLabel();
			jLabel38.setText("Colour:");
			jLabel38.setBounds(122, 10, 40, 16);
		}
		return jLabel38;
	}
	
	private JComboBox getJComboBox18() {
		if(jComboBox18 == null) {
			ComboBoxModel jComboBox18Model = 
				new DefaultComboBoxModel(
						new String[] { "Red","Green","Blue", "Yellow", "Pink" });
			jComboBox18 = new JComboBox();
			jComboBox18.setModel(jComboBox18Model);
			jComboBox18.setBounds(168, 5, 76, 26);
		}
		return jComboBox18;
	}
	
	private JTextField getJTextField26() {
		if(jTextField26 == null) {
			jTextField26 = new JTextField();
			jTextField26.setBounds(177, 34, 47, 28);
			jTextField26.setText("1e3");
			jTextField26.setEnabled(false);
		}
		return jTextField26;
	}



private JDialog getOdeSignallingDialog() {
	if(odeSignallingDialog == null) {
		odeSignallingDialog = new JDialog(this);
		odeSignallingDialog.getContentPane().setLayout(null);
		odeSignallingDialog.setPreferredSize(new java.awt.Dimension(614, 431));
		odeSignallingDialog.setSize(614, 431);
		odeSignallingDialog.getContentPane().add(getOdeSignallingDialogNextButton());
		odeSignallingDialog.getContentPane().add(getOdeCaption(), "West");
		odeSignallingDialog.getContentPane().add(getOdeInputTextArea(), "Center");
		odeSignallingDialog.getContentPane().add(getParametersCaption());
		odeSignallingDialog.getContentPane().add(getParametersInputTextArea());
		odeSignallingDialog.getContentPane().add(getNumEqInputField(), "Center");
		odeSignallingDialog.getContentPane().add(getNumEqCaption());
		odeSignallingDialog.getContentPane().add(getSignallingchemicalCaption());
		odeSignallingDialog.getContentPane().add(getInitialConditionsCaption());
		odeSignallingDialog.getContentPane().add(getInitialConditionsTextArea());
		odeSignallingDialog.getContentPane().add(getDrawerTextArea());
		odeSignallingDialog.getContentPane().add(getJTextPane16());
		odeSignallingDialog.getContentPane().add(getJTextArea5());
	}
	return odeSignallingDialog;
}

private JButton getOdeSignallingDialogNextButton() {
	if(odeSignallingDialogNextButton == null) {
		odeSignallingDialogNextButton = new JButton();
		odeSignallingDialogNextButton.setText("Next");
		odeSignallingDialogNextButton.setBounds(525, 361, 75, 34);
		odeSignallingDialogNextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				odeSignallingDialogNextButtonActionPerformed(evt);
			}
		});
	}
	return odeSignallingDialogNextButton;
}

private JTextPane getOdeCaption() {
	if(odeCaption == null) {
		odeCaption = new JTextPane();
		odeCaption.setText("Set of Ordinary Differential Equations");
		odeCaption.setBounds(6, 6, 594, 25);
		odeCaption.setFont(new java.awt.Font("SansSerif",1,14));
	}
	return odeCaption;
}

private JTextArea getOdeInputTextArea() {
	if(odeInputTextArea == null) {
		odeInputTextArea = new JTextArea();
		odeInputTextArea.setText("dy[0] = -alpha*y[0] + k1*Math.pow(y[1],nExp);\r\ndy[1] = -beta*y[1] + k2*y[0];");
		odeInputTextArea.setBounds(6, 34, 594, 95);
	}
	return odeInputTextArea;
}

private JTextPane getParametersCaption() {
	if(parametersCaption == null) {
		parametersCaption = new JTextPane();
		parametersCaption.setText("Parameters");
		parametersCaption.setBounds(6, 132, 386, 26);
		parametersCaption.setFont(new java.awt.Font("SansSerif",1,14));
	}
	return parametersCaption;
}

private JTextArea getParametersInputTextArea() {
	if(parametersInputTextArea == null) {
		parametersInputTextArea = new JTextArea();
		parametersInputTextArea.setText("alpha = 1, beta = 1, k1 = 1, k2 = 1, nExp = 2");
		parametersInputTextArea.setBounds(6, 161, 386, 89);
	}
	return parametersInputTextArea;
}

private JTextField getNumEqInputField() {
	if(numEqInputField == null) {
		numEqInputField = new JTextField();
		numEqInputField.setText("2");
		numEqInputField.setBounds(314, 357, 78, 34);
	}
	return numEqInputField;
}

private JTextPane getNumEqCaption() {
	if(numEqCaption == null) {
		numEqCaption = new JTextPane();
		numEqCaption.setText("Number of Equations");
		numEqCaption.setBounds(6, 359, 302, 30);
		numEqCaption.setFont(new java.awt.Font("SansSerif",1,14));
	}
	return numEqCaption;
}

private JComboBox getJComboBox19() {
	if(jComboBox19 == null) {
		ComboBoxModel jComboBox19Model = 
			new DefaultComboBoxModel(
					new String[] { "Random initial position", "User-specified initial position" });
		jComboBox19 = new JComboBox();
		jComboBox19.setModel(jComboBox19Model);
		jComboBox19.setBounds(164, 1, 113, 26);
		jComboBox19.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jComboBox19ActionPerformed(evt);
			}
		});
	}
	return jComboBox19;
}

private void jComboBox19ActionPerformed(ActionEvent evt) {
	//System.out.println("jComboBox19.actionPerformed, event="+evt);
	int populationIndex = jComboBox19.getSelectedIndex();
	if (populationIndex==1) {
		String populationSize = jTextField27.getText();
		int populationSizeInt = Integer.parseInt(populationSize);
		if (populationSizeInt==1) jTextField28.setEnabled(true);
	}
	
}

private JDialog getPictureExportDialog() {
	if(pictureExportDialog == null) {
		pictureExportDialog = new JDialog(this);
		pictureExportDialog.setPreferredSize(new java.awt.Dimension(278, 176));
		pictureExportDialog.getContentPane().add(getJPanel44(), BorderLayout.SOUTH);
		pictureExportDialog.getContentPane().add(getJPanel45(), BorderLayout.CENTER);
		pictureExportDialog.getContentPane().add(getJPanel43(), BorderLayout.WEST);
		pictureExportDialog.getContentPane().add(getJPanel46(), BorderLayout.EAST);
		pictureExportDialog.setSize(278, 176);
	}
	return pictureExportDialog;
}

private JPanel getJPanel43() {
	if(jPanel43 == null) {
		jPanel43 = new JPanel();
		jPanel43.setPreferredSize(new java.awt.Dimension(125, 109));
		jPanel43.setLayout(null);
		jPanel43.add(getJLabel44());
		jPanel43.add(getJLabel40());
		jPanel43.add(getJLabel39());
	}
	return jPanel43;
}

private JPanel getJPanel44() {
	if(jPanel44 == null) {
		jPanel44 = new JPanel();
		jPanel44.setPreferredSize(new java.awt.Dimension(270, 33));
		jPanel44.setLayout(null);
		jPanel44.add(getJButton17());
	}
	return jPanel44;
}

private JPanel getJPanel45() {
	if(jPanel45 == null) {
		jPanel45 = new JPanel();
		jPanel45.setPreferredSize(new java.awt.Dimension(122, 109));
		jPanel45.setLayout(null);
		jPanel45.add(getJTextField29());
		jPanel45.add(getJTextField30());
		jPanel45.add(getJTextField34());
	}
	return jPanel45;
}

private JLabel getJLabel39() {
	if(jLabel39 == null) {
		jLabel39 = new JLabel();
		jLabel39.setText("Save Pics to Directory:");
		jLabel39.setBounds(0, 73, 125, 30);
	}
	return jLabel39;
}

private JLabel getJLabel40() {
	if(jLabel40 == null) {
		jLabel40 = new JLabel();
		jLabel40.setText("Simulation time:");
		jLabel40.setBounds(6, 37, 92, 30);
	}
	return jLabel40;
}

private JTextField getJTextField29() {
	if(jTextField29 == null) {
		jTextField29 = new JTextField();
		jTextField29.setText("0.01");
		jTextField29.setBounds(6, 6, 39, 28);
	}
	return jTextField29;
}

private JTextField getJTextField30() {
	if(jTextField30 == null) {
		jTextField30 = new JTextField();
		jTextField30.setBounds(6, 40, 39, 28);
		jTextField30.setText("0.1");
	}
	return jTextField30;
}

private JPanel getJPanel46() {
	if(jPanel46 == null) {
	jPanel46 = new JPanel();
		jPanel46.setPreferredSize(new java.awt.Dimension(3, 109));
		jPanel46.setLayout(null);
	}
	return jPanel46;
}

private JButton getJButton17() {
	if(jButton17 == null) {
		jButton17 = new JButton();
		jButton17.setText("OK");
		jButton17.setBounds(103, -1, 69, 28);
		jButton17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton17ActionPerformed(evt);
			}
		});
	}
	return jButton17;
}

//private void jButton16ActionPerformed(ActionEvent evt) {
//	getJFileChooser1().setDialogType(1);
//	getJFileChooser1().setDialogTitle("Save Result");
//	int retVal = getJFileChooser1().showOpenDialog(getPictureFileChooser());
//
//	switch( retVal )
//	{
//	    case JFileChooser.CANCEL_OPTION:
//	        System.out.println( "Cancel chosen" );
//	        this.getPictureFileChooser().dispose();
//	        
//	        break;
//	    case JFileChooser.APPROVE_OPTION:
//	        //System.out.println( "Save/Approve chosen" );
//	        //jTextField35.setText(jFileChooser2.getCurrentDirectory().getName());
//	        jTextField34.setText(jFileChooser1.getSelectedFile().getAbsolutePath());
//	        //System.out.println( "File name chosen is" + jFileChooser1.getSelectedFile().getName() );
//	        this.getPictureFileChooser().dispose();
//	        break;
//	}
//}

private JDialog getMovieExportDialog() {
	if(movieExportDialog == null) {
		movieExportDialog = new JDialog(this);
		movieExportDialog.setPreferredSize(new java.awt.Dimension(323, 184));
		movieExportDialog.getContentPane().add(getJPanel47(), BorderLayout.WEST);
		movieExportDialog.getContentPane().add(getJPanel50(), BorderLayout.EAST);
		movieExportDialog.getContentPane().add(getJPanel49(), BorderLayout.CENTER);
		movieExportDialog.getContentPane().add(getJPanel48(), BorderLayout.SOUTH);
		movieExportDialog.setSize(323, 184);
	}
	return movieExportDialog;
}

private JPanel getJPanel47() {
	if(jPanel47 == null) {
		jPanel47 = new JPanel();
		jPanel47.setPreferredSize(new java.awt.Dimension(99, 83));
		jPanel47.setLayout(null);
		jPanel47.add(getJLabel41());
		jPanel47.add(getJLabel42());
		jPanel47.add(getJLabel43());
		jPanel47.add(getJLabel45());
	}
	return jPanel47;
}

private JPanel getJPanel48() {
	if(jPanel48 == null) {
		jPanel48 = new JPanel();
		jPanel48.setPreferredSize(new java.awt.Dimension(355, 38));
		jPanel48.add(getJButton18());
	}
	return jPanel48;
}

private JPanel getJPanel49() {
	if(jPanel49 == null) {
		jPanel49 = new JPanel();
		jPanel49.setPreferredSize(new java.awt.Dimension(124, 98));
		jPanel49.setLayout(null);
		jPanel49.add(getJTextField31());
		jPanel49.add(getJTextField33());
		jPanel49.add(getJTextField32());
		jPanel49.add(getJTextField35());
	}
	return jPanel49;
}

private JPanel getJPanel50() {
	if(jPanel50 == null) {
		jPanel50 = new JPanel();
		jPanel50.setPreferredSize(new java.awt.Dimension(80, 133));
		jPanel50.setLayout(null);
		jPanel50.add(getJButton19());
	}
	return jPanel50;
}

private JButton getJButton18() {
	if(jButton18 == null) {
		jButton18 = new JButton();
		jButton18.setText("OK");
		jButton18.setBounds(126, 21, 45, 28);
		jButton18.setPreferredSize(new java.awt.Dimension(74, 28));
		jButton18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton18ActionPerformed(evt);
			}
		});
	}
	return jButton18;
}

private JLabel getJLabel41() {
	if(jLabel41 == null) {
		jLabel41 = new JLabel();
		jLabel41.setText("Time Step:");
		jLabel41.setBounds(6, 7, 59, 16);
	}
	return jLabel41;
}

private JLabel getJLabel42() {
	if(jLabel42 == null) {
		jLabel42 = new JLabel();
		jLabel42.setText("Speed:");
		jLabel42.setBounds(6, 35, 39, 16);
	}
	return jLabel42;
}

private JLabel getJLabel43() {
	if(jLabel43 == null) {
		jLabel43 = new JLabel();
		jLabel43.setText("Simulation Time:");
		jLabel43.setBounds(6, 63, 100, 16);
	}
	return jLabel43;
}

private JTextField getJTextField31() {
	if(jTextField31 == null) {
		jTextField31 = new JTextField();
		jTextField31.setText("0.01");
		jTextField31.setBounds(6, 2, 36, 28);
	}
	return jTextField31;
}

private JTextField getJTextField32() {
	if(jTextField32 == null) {
		jTextField32 = new JTextField();
		jTextField32.setText("2");
		jTextField32.setBounds(6, 30, 36, 28);
	}
	return jTextField32;
}

private JTextField getJTextField33() {
	if(jTextField33 == null) {
		jTextField33 = new JTextField();
		jTextField33.setBounds(6, 58, 36, 28);
		jTextField33.setText("0.1");
	}
	return jTextField33;
}

private JLabel getJLabel44() {
	if(jLabel44 == null) {
		jLabel44 = new JLabel();
		jLabel44.setText("Time Step:");
		jLabel44.setBounds(6, 6, 66, 26);
	}
	return jLabel44;
}

private JTextField getJTextField34() {
	if(jTextField34 == null) {
		jTextField34 = new JTextField();
		jTextField34.setBounds(6, 74, 146, 28);
		jTextField34.setText("C:\\Documents and Settings\\user\\My documents\\pics");
	}
	return jTextField34;
}

private JLabel getJLabel45() {
	if(jLabel45 == null) {
		jLabel45 = new JLabel();
		jLabel45.setText("Save to File:");
		jLabel45.setBounds(6, 91, 66, 16);
	}
	return jLabel45;
}

private JTextField getJTextField35() {
	if(jTextField35 == null) {
		jTextField35 = new JTextField();
		jTextField35.setBounds(6, 86, 127, 28);
	}
	return jTextField35;
}

private JButton getJButton19() {
	if(jButton19 == null) {
		jButton19 = new JButton();
		jButton19.setText("Browse");
		jButton19.setBounds(4, 86, 70, 28);
		jButton19.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton19ActionPerformed(evt);
			}
		});
	}
	return jButton19;
}

private JDialog getMovieFileChooser() {
	if(movieFileChooser == null) {
		movieFileChooser = new JDialog(this);
		//movieFileChooser.getContentPane().add(getJFileChooser2(), BorderLayout.NORTH);
		//useJFileChooser2();
	}
	return movieFileChooser;
}

private JFileChooser getJFileChooser2() {
	if(jFileChooser2 == null) {
		jFileChooser2 = new JFileChooser();
		jFileChooser2.setBounds(-93, -68, 565, 309);

		
	}
	return jFileChooser2;
}

private JDialog getLoggerDialog() {
	if(loggerDialog == null) {
		loggerDialog = new JDialog(this);
		loggerDialog.setPreferredSize(new java.awt.Dimension(285, 109));
		loggerDialog.getContentPane().add(getJPanel51(), BorderLayout.WEST);
		loggerDialog.getContentPane().add(getJPanel52(), BorderLayout.SOUTH);
		loggerDialog.getContentPane().add(getJPanel53(), BorderLayout.EAST);
		loggerDialog.getContentPane().add(getJPanel54(), BorderLayout.CENTER);
		loggerDialog.setSize(285, 109);
	}
	return loggerDialog;
}

private JPanel getJPanel51() {
	if(jPanel51 == null) {
		jPanel51 = new JPanel();
		jPanel51.setPreferredSize(new java.awt.Dimension(80, 77));
		jPanel51.add(getJLabel46());
	}
	return jPanel51;
}

private JPanel getJPanel52() {
	if(jPanel52 == null) {
		jPanel52 = new JPanel();
		jPanel52.setPreferredSize(new java.awt.Dimension(277, 42));
		jPanel52.add(getJButton21());
	}
	return jPanel52;
}

private JPanel getJPanel53() {
	if(jPanel53 == null) {
		jPanel53 = new JPanel();
		jPanel53.setPreferredSize(new java.awt.Dimension(77, 77));
		jPanel53.add(getJButton20());
	}
	return jPanel53;
}

private JLabel getJLabel46() {
	if(jLabel46 == null) {
		jLabel46 = new JLabel();
		jLabel46.setText("Save to File:");
		jLabel46.setPreferredSize(new java.awt.Dimension(70, 27));
	}
	return jLabel46;
}

private JPanel getJPanel54() {
	if(jPanel54 == null) {
		jPanel54 = new JPanel();
		jPanel54.setPreferredSize(new java.awt.Dimension(117, 77));
		jPanel54.add(getJTextField36());
	}
	return jPanel54;
}

private JTextField getJTextField36() {
	if(jTextField36 == null) {
		jTextField36 = new JTextField();
		jTextField36.setBounds(0, 6, 120, 28);
		jTextField36.setPreferredSize(new java.awt.Dimension(120, 28));
	}
	return jTextField36;
}

private JButton getJButton20() {
	if(jButton20 == null) {
		jButton20 = new JButton();
		jButton20.setText("Browse");
		jButton20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton20ActionPerformed(evt);
			}
		});
	}
	return jButton20;
}

private JButton getJButton21() {
	if(jButton21 == null) {
		jButton21 = new JButton();
		jButton21.setText("OK");
		jButton21.setPreferredSize(new java.awt.Dimension(62, 28));
		jButton21.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton21ActionPerformed(evt);
			}
		});
	}
	return jButton21;
}

private JDialog getLoggerFileChooser() {
	if(loggerFileChooser == null) {
		loggerFileChooser = new JDialog(this);
		loggerFileChooser.getContentPane().add(getJFileChooser3());
	}
	return loggerFileChooser;
}

private JFileChooser getJFileChooser3() {
	if(jFileChooser3 == null) {
		jFileChooser3 = new JFileChooser();
		jFileChooser3.setBounds(-93, -68, 565, 309);
	}
	return jFileChooser3;
}

private void jButton18ActionPerformed(ActionEvent evt) {
	//System.out.println("jButton18.actionPerformed, event="+evt);
	String Dt = jTextField31.getText();
	String speed = jTextField32.getText();
	String simulationTime = jTextField33.getText();
	String saveFile = jTextField35.getText();
	saveFile = StringInserter(saveFile, "\\" , "\\");
	String OutputChunk = "BSimMovExporter movExporter = new BSimMovExporter(sim,drawer,\"" + saveFile + "\");" + "\n" + "movExporter.setDt(" + Dt + ");" + "\n" + "movExporter.setSpeed(" + speed + ");" + "\n" + "sim.addExporter(movExporter);" + "\n" + "sim.setSimulationTime(" + simulationTime + ");" + "\n" + "sim.export(); " + "\n" + "}}";
/*	BSimMovExporter movExporter = new BSimMovExporter(sim,drawer,"simulation.mov");
	movExporter.setDt(0.03);
	sim.addExporter(movExporter);
	sim.setSimulationTime(20);
	sim.export(); */
	OutputChunkTotal += OutputChunk;
	FileWrite();
	//this.movieExportDialog.dispose();
	System.exit(0);
}

private void jButton19ActionPerformed(ActionEvent evt) {
	getJFileChooser2().setDialogType(1);
	getJFileChooser2().setDialogTitle("Save Result");
	int retVal = getJFileChooser2().showOpenDialog(getMovieFileChooser());

	switch( retVal )
	{
	    case JFileChooser.CANCEL_OPTION:
	        System.out.println( "Cancel chosen" );
	        this.getMovieFileChooser().dispose();
	        
	        break;
	    case JFileChooser.APPROVE_OPTION:
	        //System.out.println( "Save/Approve chosen" );
	        //jTextField35.setText(jFileChooser2.getCurrentDirectory().getName());
	        jTextField35.setText(jFileChooser2.getSelectedFile().getAbsolutePath());
	        //System.out.println( "File name chosen is" + jFileChooser1.getSelectedFile().getName() );
	        this.getMovieFileChooser().dispose();
	        break;
	}

}

//private JFrame getJFrame1() {
//	if(jFrame1 == null) {
//		jFrame1 = new JFrame();
//		
//	}
//	 jFileChooser1 = new JFileChooser();
//     jFileChooser1.setDialogType(1);
//     jFileChooser1.setDialogTitle("Save Result");
//     int retVal = jFileChooser1.showOpenDialog(jFrame1);
//
//     switch( retVal )
//     {
//         case JFileChooser.CANCEL_OPTION:
//             System.out.println( "Cancel chosen" );
//             this.getJFrame1().dispose();
//             break;
//         case JFileChooser.APPROVE_OPTION:
//             //System.out.println( "Save/Approve chosen" );
//             jTextField35.setText(jFileChooser1.getSelectedFile().getName());
//             //System.out.println( "File name chosen is" + jFileChooser1.getSelectedFile().getName() );
//             this.getJFrame1().dispose();
//             break;
//     }
//
//	return jFrame1;
//}

private void jButton17ActionPerformed(ActionEvent evt) {
	
	String timeStep = jTextField29.getText();
	String simulationTime = jTextField30.getText();
	String fileSave = jTextField34.getText();
	fileSave = StringInserter(fileSave, "\\" , "\\");
	String OutputChunk = "BSimPngExporter imageExporter = new BSimPngExporter(sim,drawer,\"" + fileSave + "\");" + "\n" + "imageExporter.setDt(" + timeStep + ");" + "\n" + "sim.addExporter(imageExporter);" + "\n" + "sim.setSimulationTime(" + simulationTime + ");" + "\n" + "sim.export(); " + "\n" + "}  }";
	OutputChunkTotal += OutputChunk;
	FileWrite();
	//this.pictureExportDialog.dispose();
	System.exit(0);
}

private void jButton20ActionPerformed(ActionEvent evt) {
	getJFileChooser3().setDialogType(1);
	getJFileChooser3().setDialogTitle("Save Result");
	int retVal = getJFileChooser3().showOpenDialog(getLoggerFileChooser());

	switch( retVal )
	{
	    case JFileChooser.CANCEL_OPTION:
	        this.getLoggerFileChooser().dispose();
	        
	        break;
	    case JFileChooser.APPROVE_OPTION:
	        //System.out.println( "Save/Approve chosen" );
	        //jTextField35.setText(jFileChooser2.getCurrentDirectory().getName());
	        jTextField36.setText(jFileChooser3.getSelectedFile().getAbsolutePath());
	        //System.out.println( "File name chosen is" + jFileChooser1.getSelectedFile().getName() );
	        this.getLoggerFileChooser().dispose();
	        break;
	}
}

private void jButton21ActionPerformed(ActionEvent evt) {
	String saveFile = jTextField36.getText();
	String OutputChunk = "blablabla";

	OutputChunkTotal += OutputChunk;
	//this.movieExportDialog.dispose();
	FileWrite();
	System.exit(0);
}

private JTextPane getSignallingchemicalCaption() {
	if(signallingchemicalCaption == null) {
		signallingchemicalCaption = new JTextPane();
		signallingchemicalCaption.setText("Signalling Chemical");
		signallingchemicalCaption.setBounds(398, 133, 197, 25);
		signallingchemicalCaption.setFont(new java.awt.Font("SansSerif",1,14));
	}
	return signallingchemicalCaption;
}

private JTextPane getInitialConditionsCaption() {
	if(initialConditionsCaption == null) {
		initialConditionsCaption = new JTextPane();
		initialConditionsCaption.setText("Initial Conditions");
		initialConditionsCaption.setBounds(6, 253, 386, 21);
		initialConditionsCaption.setFont(new java.awt.Font("SansSerif",1,14));
	}
	return initialConditionsCaption;
}

private JTextArea getInitialConditionsTextArea() {
	if(initialConditionsTextArea == null) {
		initialConditionsTextArea = new JTextArea();
		initialConditionsTextArea.setText("1,0");
		initialConditionsTextArea.setBounds(6, 280, 386, 73);
	}
	return initialConditionsTextArea;
}

private JTextArea getDrawerTextArea() {
	if(drawerTextArea == null) {
		drawerTextArea = new JTextArea();
		drawerTextArea.setText("((int)bacterium.y[0]/((int)bacterium.y[0]+1))*255, 0 , 0");
		drawerTextArea.setBounds(398, 164, 197, 81);
		drawerTextArea.setEnabled(false);
	}
	return drawerTextArea;
}

private void odeSignallingDialogNextButtonActionPerformed(ActionEvent evt) {
	classChemicalCreator1 = "";
	classODEsignalling3 = "";
	classODEsignalling4 = "";
	String drawerGeneral3f = "drawerGeneral3f";
	
	String odeInputText = odeInputTextArea.getText();
	String parametersInputText = parametersInputTextArea.getText();
	String drawerText = drawerTextArea.getText(); 
	String initialConditionsText = initialConditionsTextArea.getText();
	String numEq = numEqInputField.getText();
	String ChemicalProductionRate = productionRateTextArea.getText();
	
	classChemicalCreator1 = "field"+creatorFieldIndexS+".addQuantity(position, " + ChemicalProductionRate + "*sim.getDt());";
	
	ParameterChunk += "static double " + parametersInputText + ";";
	ParameterChunk += "static int numEq = " + numEq + ";";
	
	String ODESignallingColour = "new Color(" + drawerText + ")";
	drawerGeneral2 += "\n" + "draw(bacterium," + ODESignallingColour + ");";	
	
	classODEsignalling4 = "double[] ics = {" + initialConditionsText + "};";
	classODEsignalling3 = odeInputText;
	
	//System.out.println(classODEsignalling3);
	drawerGeneral3 = ReadTextFileExample(drawerGeneral3f,CustomDrawer);
	
	ParameterChunk += startParameters1;
	ClassChunk = classGeneral1 + "\n" + classODEsignalling1 + "\n" + classGeneral2 + "\n" + classChemotaxis1 + "\n" + classThresholdSignalling1 + "\n" + classChemicalCreator1 + "\n" + classGeneral3 + classODEsignalling3 + classGeneral4 + classODEsignalling4 + classGeneral5 + classPosition; 
	TickerChunk += tickerCollision1 + "\n" + tickerGeneral2 + "\n" + tickerMoving1 + "\n" + "}";
	DrawerChunk += drawerGeneral2 + "\n" + drawerGeneral3;
	
	ParameterChunk = popNumUpdate(ParameterChunk, "simpleColour", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "threshold", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "thresholdSignallingColour", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "productionRate", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "runUp", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "runElse", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "tumble", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "collisionColour", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "populationSize", ClickNum); 
	ParameterChunk = popNumUpdate(ParameterChunk, "numEq", ClickNum);
	ParameterChunk = popNumUpdate(ParameterChunk, "ODESignallingColour", ClickNum); 
	
	ClassChunk = popNumUpdate(ClassChunk, "threshold", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "productionRate", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "runUp", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "runElse", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "tumble", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "populationSize", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "bacterium", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "bacteria", ClickNum);
	ClassChunk = popNumUpdate(ClassChunk, "CustomBacterium", ClickNum); 
	ClassChunk = popNumUpdate(ClassChunk, "numEq", ClickNum);
	
	TickerChunk = popNumUpdate(TickerChunk, "bacterium", ClickNum);
	TickerChunk = popNumUpdate(TickerChunk, "bacteria", ClickNum);
	TickerChunk = popNumUpdate(TickerChunk, "CustomBacterium", ClickNum);
	
	DrawerChunk = popNumUpdate(DrawerChunk, "bacterium", ClickNum);
	DrawerChunk = popNumUpdate(DrawerChunk, "bacteria", ClickNum);
	DrawerChunk = popNumUpdate(DrawerChunk, "CustomBacterium", ClickNum);
	DrawerChunk = popNumUpdate(DrawerChunk, "simpleColour", ClickNum);
	DrawerChunk = popNumUpdate(DrawerChunk, "thresholdSignallingColour", ClickNum);
	DrawerChunk = popNumUpdate(DrawerChunk, "collisionColour", ClickNum);
	DrawerChunk = popNumUpdate(DrawerChunk, "ODESignallingColour", ClickNum); 
	
	ParametersChunkTotal += ParameterChunk;
	ClassChunkTotal += ClassChunk;
	TickerChunkTotal += TickerChunk;
	DrawerChunkTotal += DrawerChunk;

	this.odeSignallingDialog.dispose();
}



private JTextPane getJTextPane16() {
	if(jTextPane16 == null) {
		jTextPane16 = new JTextPane();
		jTextPane16.setText("Chemical Production Rate");
		jTextPane16.setFont(new java.awt.Font("SansSerif",1,14));
		jTextPane16.setBounds(398, 251, 202, 25);
	}
	return jTextPane16;
}

private JTextArea getJTextArea5() {
	if(productionRateTextArea == null) {
		productionRateTextArea = new JTextArea();
		productionRateTextArea.setText("(4e10)*y[1]");
		productionRateTextArea.setBounds(398, 281, 197, 81);
		productionRateTextArea.setEnabled(false);
	}
	return productionRateTextArea;
}

private JLabel getJLabel47() {
	if(jLabel47 == null) {
		jLabel47 = new JLabel();
		jLabel47.setText("Set Boundaries:");
		jLabel47.setPreferredSize(new java.awt.Dimension(117, 29));
	}
	return jLabel47;
}

private JTextField getJTextField37() {
	if(jTextField37 == null) {
		jTextField37 = new JTextField();
		jTextField37.setText("false,false,false");
		jTextField37.setPreferredSize(new java.awt.Dimension(101, 28));
		jTextField37.setBounds(4, 38, 101, 28);
	}
	return jTextField37;
}

private JLabel getJLabel48() {
	if(jLabel48 == null) {
		jLabel48 = new JLabel();
		jLabel48.setText("Set Display Angle:");
		jLabel48.setPreferredSize(new java.awt.Dimension(117, 29));
	}
	return jLabel48;
}

private JTextField getJTextField38() {
	if(jTextField38 == null) {
		jTextField38 = new JTextField();
		jTextField38.setText("0.7");
		jTextField38.setBounds(6, 70, 29, 28);
	}
	return jTextField38;
}

private JTextField getJTextField39() {
	if(jTextField39 == null) {
		jTextField39 = new JTextField();
		jTextField39.setText("0.3");
		jTextField39.setBounds(41, 70, 29, 28);
	}
	return jTextField39;
}

private JTextField getJTextField40() {
	if(jTextField40 == null) {
		jTextField40 = new JTextField();
		jTextField40.setText("0.5");
		jTextField40.setBounds(76, 70, 29, 28);
	}
	return jTextField40;
}

}
