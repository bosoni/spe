/**
 * @file ItemName.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 */
package spe;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class ItemName extends JDialog implements ActionListener
{
    private JPanel jContentPane = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JTextField nameTextField = null;
    private JButton jButton = null;
    /**
     * @param owner
     */
    public ItemName(Frame owner)
    {
	super(owner);
	initialize();
    }
    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize()
    {
	this.setSize(300, 113);
	this.setModal(true);
	this.setResizable(false);
	this.setTitle("Esineen nimi");
	this.setContentPane(getJContentPane());
    }
    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
	if (jContentPane == null)
	{
	    jLabel1 = new JLabel();
	    jLabel1.setText("Huom täysin saman nimisiä esineitä ei saa olla!");
	    jLabel = new JLabel();
	    jLabel.setText("Anna esineen nimi");
	    jLabel.setName("jLabel");
	    jContentPane = new JPanel();
	    jContentPane.setLayout(new FlowLayout());
	    jContentPane.add(jLabel, null);
	    jContentPane.add(jLabel1, null);
	    jContentPane.add(getNameTextField(), null);
	    jContentPane.add(getJButton(), null);
	}
	return jContentPane;
    }
    /**
     * This method initializes nameTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getNameTextField()
    {
	if (nameTextField == null)
	{
	    nameTextField = new JTextField();
	    nameTextField.setPreferredSize(new Dimension(200, 20));
	}
	return nameTextField;
    }
    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton()
    {
	if (jButton == null)
	{
	    jButton = new JButton();
	    jButton.setText("OK");
	    
	    nameTextField.registerKeyboardAction(this, "", KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), JComponent.WHEN_FOCUSED );
	    jButton.addActionListener(this);
	    
	    jButton.addActionListener(this);
	}
	return jButton;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
	SPEditori.items.add(nameTextField.getText());
	
	// aseta esineen nimi
	SPEditori.curRoom.objs.get(SPEditori.curRoom.objs.size()-1).name = nameTextField.getText();
	this.dispose();
	
    }
    
}
