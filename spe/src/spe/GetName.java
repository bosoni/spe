/**
 * @file GetName.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * avaa ikkunan ja kysyy huoneen/reitin ym nimeä.
 */
package spe;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

public class GetName extends JDialog implements ActionListener
{
    private JPanel jContentPane = null;
    private JLabel jLabel = null;
    private JTextField name = null;
    private JButton okButton = null;
    
    int dialog=0; // 1=huoneen nimi,  2=animaation nimi,  3=reitin nimi,  4=paikan nimi
    
    
    /**
     * @param owner
     */
    public GetName(Frame owner, String txt, int mode)
    {
	super(owner);
	dialog=mode;
	initialize(txt);
    }
    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize(String txt)
    {
	this.setSize(300, 95);
	this.setModal(true);
	this.setTitle(txt);
	this.setContentPane(getJContentPane(txt));
    }
    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane(String txt)
    {
	if (jContentPane == null)
	{
	    jLabel = new JLabel();
	    jLabel.setText(txt+":");
	    jContentPane = new JPanel();
	    jContentPane.setLayout(new FlowLayout());
	    jContentPane.add(jLabel, null);
	    jContentPane.add(get_Name(), null);
	    jContentPane.add(getOkButton(), null);
	}
	return jContentPane;
    }
    /**
     * This method initializes name
     *
     * @return javax.swing.JTextField
     */
    private JTextField get_Name()
    {
	if (name == null)
	{
	    name = new JTextField();
	    name.setPreferredSize(new Dimension(150, 20));
	}
	return name;
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e)
    {
	if(name!=null)
	{
	    // laita kirjoitettu nimi talteen ja sulje ikkuna
	    String str=name.getText();
	    
	    // 1=huoneen nimi,  2=animaation nimi,  3=reitin nimi
	    if(dialog==1) SPEditori.curRoom.setName(str);
	    else if(dialog==2) SPEditori.setAnimName(str);
	    else if(dialog==3) SPEditori.curRoom.setPathName(str);
	    else if(dialog==4) SPEditori.curRoom.createPos(str);
	    // ok huono toteutus, mutta toinen tapa olisi ollut tehdä
	    // 3 lähes samanlaista luokkaa joka kysyy nimeä.
	    
	    
	    this.dispose();
	}
	
    }
    
    /**
     * This method initializes okButton
     *
     * @return javax.swing.JButton
     */
    private JButton getOkButton()
    {
	if (okButton == null)
	{
	    okButton = new JButton();
	    okButton.setText("OK");
	    
	    name.registerKeyboardAction(this, "", KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), JComponent.WHEN_FOCUSED );
	    okButton.addActionListener(this);
	}
	return okButton;
    }
}
