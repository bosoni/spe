/**
 * @file ListObjs.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * listaa luodut polygonit ja esineet joita voi sitten poistaa.
 *
 */
package spe;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;

public class ListObjs extends JDialog
{
    public static int selected=-1; /** valittu poly  / esine */
    
    private JPanel jContentPane = null;
    private JList jList = null;
    private JButton delButton = null;
    
    DefaultListModel model = new DefaultListModel();
    
    private JScrollPane jScrollPane = null;
    
    /**
     * @param owner
     */
    public ListObjs(Frame owner)
    {
	super(owner);
	initialize();
    }
    
    public void setSelectedIndex(int sel)
    {
	if(selected!=-1) jList.setSelectedIndex(sel);
    }
    
    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize()
    {
	this.setSize(150, 248);
	this.setResizable(false);
	this.setTitle("Objektit");
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
	    jContentPane = new JPanel();
	    jContentPane.setLayout(new FlowLayout());
	    jContentPane.add(getJScrollPane(), null);
	    jContentPane.add(getDelButton(), null);
	}
	return jContentPane;
    }
    /**
     * This method initializes jList
     *
     * @return javax.swing.JList
     */
    private JList getJList()
    {
	if (jList == null)
	{
	    jList = new JList(model);
	    jList.setSelectedIndex(0);
	    jList.addListSelectionListener(new javax.swing.event.ListSelectionListener()
	    {
		public void valueChanged(javax.swing.event.ListSelectionEvent e)
		{
		    selected = jList.getSelectedIndex();
		    SPEditori.background.setSelected(selected);
		    
		    // jos action dialog avattu, päivitä se
		    if(SPEditori.actionDialog!=null) SPEditori.actionDialog.updateDialog(selected);
		}
	    });
	}
	return jList;
    }
    /**
     * This method initializes delButton
     *
     * @return javax.swing.JButton
     */
    private JButton getDelButton()
    {
	if (delButton == null)
	{
	    delButton = new JButton();
	    delButton.setText("Poista");
	    delButton.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (selected != -1 && model.size()>0)
		    {
			// jos polygoni
			if(selected < SPEditori.curRoom.polys.size()) SPEditori.curRoom.polys.remove(selected); // poista valittu poly
			else // esine
			    SPEditori.curRoom.objs.remove(selected-SPEditori.curRoom.polys.size());
			
			model.remove(selected);
			selected=0;
			SPEditori.background.repaint();
			SPEditori.updatePolygonList();
		    }
		    
		}
	    });
	}
	return delButton;
    }
    
    public void removeList()
    {
	model.removeAllElements();
    }
    
    public void addToList(int i, String str)
    {
	model.add(i, str);
    }
    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane()
    {
	if (jScrollPane == null)
	{
	    jScrollPane = new JScrollPane(getJList());
	    jScrollPane.setPreferredSize(new Dimension(140, 175));
	    jScrollPane.setBorder(null);
	    jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	return jScrollPane;
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,3"
