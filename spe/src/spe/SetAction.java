/**
 * @file SetAction.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * Asetetaan polygonin sis‰lle olevaalle alueelle toiminnot.
 * Se voi olla este jonka l‰pi ei silloin voi kulkea.
 * Selitykseen kirjoitetaan mit‰ tulostetaan jos siihen kohtaan katsoo.
 * Jos ota/k‰yt‰ toiminto suoritetaan siihen kohtaan, "Ota/k‰yt‰ toiminnon palaute" voidaan kirjoittaa.
 * Ja jos ota/k‰yt‰, h‰vitet‰‰nkˆ polygoni (eli jos ottaa jonkun esineen joka ensin est‰‰ kulun), polygonin
 * h‰vitetty‰ siit‰ voi kulkea (esimerkkin‰ suljettu ovi, avattua se ovi este poistetaan).
 *
 * Esineen tiedot:
 * Voidaan valita mihin esineeseen vaikutaa ja mill‰ tavalla.
 * Jos esineeseen Ota/k‰yt‰, siirtyykˆ se tavaroihin ja pit‰‰kˆ sen h‰vit‰ ruudulta.
 * Jos k‰ytet‰‰n esinett‰, voi m‰‰r‰t‰ h‰vi‰‰kˆ se tavaroista. Ja sille oma teksti.
 *
 * Huoneen vaihto seuraavasti:
 *
 * Vaihda huonetta textboxiin kirjoitetaan huoneen nimi (saman niminen tiedosto)
 * ja sen per‰‰n linkki toisen huoneen aloituskohta..linkki on siis joku nimi
 * mill‰ nimell‰ toisessa huoneessa on joku poly (siihen kohtaan asetetaan sit ukko).
 * esim Vaihda huonetta:  ulko1 RAPPU
 * ulko1:ss‰ on pari kohtaa josta ukko voi tulla ruutuun, ovesta tai talon takaa.
 * oven edess‰ on linkki OVI, talon takaa RAPPU. ukko asetetaan RAPPU polyn p‰‰lle.
 * Linkkipolyt tehd‰‰n kirjoittamalla Vaihda huonetta: kentt‰‰n PAIKKA RAPPU <- eli PAIKKA ja nimi.
 * Vaikka Este olisi p‰‰ll‰, poly ei ole este, pelk‰st‰‰n alkukohta.
 *
 * -- 30.6 nyt "Aseta paikka" napilla ohjelma hoitaa nuo hommelit eli laittaa ite sen PAIKKA paikannimi
 */

package spe;

import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.JComboBox;

public class SetAction extends JDialog
{
    static int selected = -1;
    
    private JPanel jContentPane = null;
    private JCheckBox blockCheckBox = null;
    private JLabel descLabel = null;
    private JTextField descTextField = null;
    private JLabel useLabel = null;
    private JTextField descTextField1 = null;
    private JCheckBox toInventoryCheckBox = null;
    private JCheckBox removeFromScreenCheckBox = null;
    private JCheckBox removePolyCheckBox = null;
    private JLabel itemLabel = null;
    private JComboBox itemComboBox = null;
    
    private JLabel needItemLabel = null;
    
    private JComboBox needItemComboBox = null;
    
    private JTextField changeRoomTextField = null;
    
    private JLabel jLabel = null;
    
    private JCheckBox removeFromInventoryCheckBox = null;
    
    private JLabel jLabel1 = null;
    
    private JTextField successUseTextField = null;
    
    /**
     * @param owner
     */
    public SetAction(Frame owner, int selected)
    {
	super(owner);
	this.selected = selected;
	
	initialize();
	updateCombos();
    }
    
    public void updateCombos()
    {
	itemComboBox.removeAllItems();
	needItemComboBox.removeAllItems();
	
	// lis‰‰ kahteen alasvetovalikkoon esineiden nimet
	// toinen m‰‰r‰‰ mik‰ objekti poly on (jos on), toinen mink‰ objektin
	// poly vaatii (jos vaatii)
	itemComboBox.addItem("ei mit‰‰n");
	needItemComboBox.addItem("ei mit‰‰n");
	for(int q=0; q<SPEditori.items.size(); q++)
	{
	    itemComboBox.addItem(SPEditori.items.get(q));
	    needItemComboBox.addItem(SPEditori.items.get(q));
	}
	
	/*
	for (int q = 0; q < SPEditori.curRoom.objs.size(); q++)
	{
	    itemComboBox.addItem(SPEditori.curRoom.objs.get(q).name);
	    needItemComboBox.addItem(SPEditori.curRoom.objs.get(q).name);
	}*/
	
    }
    
    
    
    /**
     * p‰ivitt‰‰ dialogin tiedot. kutsutaan ListObjs luokasta kun valitaan
     * joku polygoni ja sen polygonin tiedot n‰ytet‰‰n saman tien
     * jos poly on PAIKKA, poistetaan este (PAIKKA ei ole koskaan esteen‰)
     */
    public void updateDialog(int selected)
    {
	this.selected = selected;
	if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
	
	Polygon r = SPEditori.curRoom.polys.get(selected);
	descTextField.setText(r.descStr);
	removePolyCheckBox.setSelected(r.removePoly);
	toInventoryCheckBox.setSelected(r.toInventory);
	removeFromScreenCheckBox.setSelected(r.removeFromScreen);
	
	// tsekkaa onko PAIKKA
	if(r.descStr.length()>=6) if(r.descStr.substring(0, 6).equals("PAIKKA")) r.block=false;
	blockCheckBox.setSelected(r.block);
	
	descTextField1.setText(r.actionStr);
	changeRoomTextField.setText(r.newRoom);
	
	if (r.itemNum < 0)
	    r.itemNum = 0;
	if (r.needsItem < 0)
	    r.needsItem = 0;
	
	itemComboBox.setSelectedIndex(r.itemNum);
	needItemComboBox.setSelectedIndex(r.needsItem);
    }
    
    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize()
    {
	this.setSize(255, 372);
	this.setResizable(false);
	this.setTitle("Aseta toiminto");
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
	    jLabel1.setText("Onnistuneen k‰ytˆn palaute");
	    jLabel = new JLabel();
	    jLabel.setText("Huone: ");
	    needItemLabel = new JLabel();
	    needItemLabel.setText("Tarvitsee esineen");
	    needItemLabel.setPreferredSize(new Dimension(120, 16));
	    itemLabel = new JLabel();
	    itemLabel.setText("Esineen tiedot");
	    itemLabel.setPreferredSize(new Dimension(200, 16));
	    useLabel = new JLabel();
	    useLabel.setText("Ota/k‰yt‰ toiminnon palaute");
	    useLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
	    descLabel = new JLabel();
	    descLabel.setText("Selitys");
	    descLabel.setPreferredSize(new Dimension(40, 16));
	    descLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
	    jContentPane = new JPanel();
	    jContentPane.setLayout(new FlowLayout());
	    jContentPane.add(getBlockCheckBox(), null);
	    jContentPane.add(jLabel, null);
	    jContentPane.add(getChangeRoomTextField(), null);
	    jContentPane.add(descLabel, null);
	    jContentPane.add(getDescTextField(), null);
	    jContentPane.add(useLabel, null);
	    jContentPane.add(getDescTextField1(), null);
	    jContentPane.add(getRemovePolyCheckBox(), null);
	    jContentPane.add(itemLabel, null);
	    jContentPane.add(getItemComboBox(), null);
	    jContentPane.add(getToInventoryCheckBox(), null);
	    jContentPane.add(getRemoveFromScreenCheckBox(), null);
	    jContentPane.add(getRemoveFromInventoryCheckBox(), null);
	    jContentPane.add(needItemLabel, null);
	    jContentPane.add(getNeedItemComboBox(), null);
	    jContentPane.add(jLabel1, null);
	    jContentPane.add(getSuccessUseTextField(), null);
	}
	return jContentPane;
    }
    /**
     * This method initializes blockCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getBlockCheckBox()
    {
	if (blockCheckBox == null)
	{
	    blockCheckBox = new JCheckBox();
	    blockCheckBox.setText("Este (polygonin l‰pi ei voi kulkea)");
	    blockCheckBox.setSelected(true);
	    blockCheckBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		    
		    SPEditori.curRoom.polys.get(selected).block = blockCheckBox.isSelected();
		}
	    });
	}
	return blockCheckBox;
    }
    /**
     * This method initializes descTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getDescTextField()
    {
	if (descTextField == null)
	{
	    descTextField = new JTextField();
	    descTextField.setPreferredSize(new Dimension(200, 20));
	    descTextField.addCaretListener(new javax.swing.event.CaretListener()
	    {
		public void caretUpdate(javax.swing.event.CaretEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		    
		    // selitys talteen
		    SPEditori.curRoom.polys.get(selected).descStr = descTextField.getText();
		}
	    });
	}
	return descTextField;
    }
    /**
     * This method initializes descTextField1
     *
     * @return javax.swing.JTextField
     */
    private JTextField getDescTextField1()
    {
	if (descTextField1 == null)
	{
	    descTextField1 = new JTextField();
	    descTextField1.setPreferredSize(new Dimension(200, 20));
	    descTextField1.addCaretListener(new javax.swing.event.CaretListener()
	    {
		public void caretUpdate(javax.swing.event.CaretEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		    
		    // ota/k‰yt‰ palaute talteen
		    SPEditori.curRoom.polys.get(selected).actionStr = descTextField1.getText();
		}
	    });
	}
	return descTextField1;
    }
    /**
     * This method initializes toInventoryCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getToInventoryCheckBox()
    {
	if (toInventoryCheckBox == null)
	{
	    toInventoryCheckBox = new JCheckBox();
	    toInventoryCheckBox.setText("Siirr‰ tavaroihin");
	    toInventoryCheckBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;

		    // aseta polyn tiedot
		    SPEditori.curRoom.polys.get(selected).toInventory = toInventoryCheckBox.isSelected();
		    
		}
	    });
	}
	return toInventoryCheckBox;
    }
    /**
     * This method initializes removeFromScreenCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveFromScreenCheckBox()
    {
	if (removeFromScreenCheckBox == null)
	{
	    removeFromScreenCheckBox = new JCheckBox();
	    removeFromScreenCheckBox.setText("H‰vit‰ ruudulta");
	    removeFromScreenCheckBox.setPreferredSize(new Dimension(110, 24));
	    removeFromScreenCheckBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		
		    // aseta polyn tiedot
		    SPEditori.curRoom.polys.get(selected).removeFromScreen = removeFromScreenCheckBox.isSelected();
		}
	    });
	}
	return removeFromScreenCheckBox;
    }
    /**
     * This method initializes removePolyCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemovePolyCheckBox()
    {
	if (removePolyCheckBox == null)
	{
	    removePolyCheckBox = new JCheckBox();
	    removePolyCheckBox.setText("H‰vit‰ polygoni");
	    removePolyCheckBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;

		    // aseta polyn tiedot
		    SPEditori.curRoom.polys.get(selected).removePoly = removePolyCheckBox.isSelected();
		    
		}
	    });
	}
	return removePolyCheckBox;
    }
    /**
     * This method initializes itemComboBox
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getItemComboBox()
    {
	if (itemComboBox == null)
	{
	    itemComboBox = new JComboBox();
	    itemComboBox.setPreferredSize(new Dimension(100, 25));
	    itemComboBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		    
		    // valitaan objekti johon poly linkataan
		    // ja "siirr‰ tavaroihin" ja "h‰vit‰ ruudulta" checkboxit
		    // vaikuttaa
		    SPEditori.curRoom.polys.get(selected).itemNum = itemComboBox.getSelectedIndex();
		    
		}
	    });
	}
	return itemComboBox;
    }
    
    /**
     * This method initializes needItemComboBox
     *
     * @return javax.swing.JComboBox
     */
    private JComboBox getNeedItemComboBox()
    {
	if (needItemComboBox == null)
	{
	    needItemComboBox = new JComboBox();
	    needItemComboBox.setPreferredSize(new Dimension(100, 25));
	    needItemComboBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		    
		    // valitaan objekti jonka kyseinen poly tarvitsee
		    SPEditori.curRoom.polys.get(selected).needsItem = needItemComboBox.getSelectedIndex();
		}
	    });
	}
	return needItemComboBox;
    }
    
    /**
     * This method initializes changeRoomTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getChangeRoomTextField()
    {
	if (changeRoomTextField == null)
	{
	    changeRoomTextField = new JTextField();
	    changeRoomTextField.setPreferredSize(new Dimension(170, 20));
	    changeRoomTextField.addCaretListener(new javax.swing.event.CaretListener()
	    {
		public void caretUpdate(javax.swing.event.CaretEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;

		    SPEditori.curRoom.polys.get(selected).newRoom = changeRoomTextField.getText();
		}
	    });
	}
	return changeRoomTextField;
    }
    
    /**
     * This method initializes removeFromInventoryCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveFromInventoryCheckBox()
    {
	if (removeFromInventoryCheckBox == null)
	{
	    removeFromInventoryCheckBox = new JCheckBox();
	    removeFromInventoryCheckBox.setText("Poista tavaroista");
	    removeFromInventoryCheckBox.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;

		    // poistetaanko esine tavaroista k‰ytˆn j‰lkeen
		    SPEditori.curRoom.polys.get(selected).removeFromInventory = removeFromInventoryCheckBox.isSelected();
		}
	    });
	}
	return removeFromInventoryCheckBox;
    }
    
    /**
     * This method initializes successUseTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getSuccessUseTextField()
    {
	if (successUseTextField == null)
	{
	    successUseTextField = new JTextField();
	    successUseTextField.setPreferredSize(new Dimension(200, 20));
	    successUseTextField.addCaretListener(new javax.swing.event.CaretListener()
	    {
		public void caretUpdate(javax.swing.event.CaretEvent e)
		{
		    if(selected==-1 || selected>=SPEditori.curRoom.polys.size()) return;
		    
		    // jos tavaraa k‰ytettiin onnistuneesti, eli oikea tavara
		    // oikeaan paikkaan
		    SPEditori.curRoom.polys.get(selected).successUseStr = successUseTextField.getText();
		}
	    });
	}
	return successUseTextField;
    }
    
}
