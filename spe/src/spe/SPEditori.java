/**
 * @file SPEditori.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * seikkailupeli editori
 *
 * Ensin luodaan huone, annetaan sille nimi ja taustakuva.
 * Syvyyskartta kuvan voi myös antaa jolloin piirrettävät objektit voi mennä
 * huoneessa olevien objektien taakse.
 *
 * Sitten asetetaan viivatyökalulla polygoneja jotka voi olla
 *  * esteitä, eli hahmot ei voi kulkea niiden läpi
 *  * esineitä jotka antaa palautetta riippuen mitä toimintoa koittaa
 *     * katso-toiminto, jolloin tulostetaan mitä tietoa sille objektille on annettu
 *     * ota/käytä toiminto jolloin siirretään tavara inventaarioon tai annetaan palautetta mitä tapahtui
 *     * toiminto, jossa poly vaatii jonkun esineen ennenkuin tapahtuma suoritetaan (esim ovi vaatii avaimen)
 *
 * esineiden nimet tallennetaan items.lst tiedostoon ja se ladataan joka huoneessa,
 * näin saadaan toiminto ikkunan alasvetovalikoihin objektien nimet.
 * tallennettaessa tallennetaan lista, uusi huone/lataa huone ladataan myös lista.
 * uusi esine asetettaessa lisätään se listaan (mutta ei tallenneta tässä vaiheessa).
 *
 *
 */
package spe;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;
import java.util.Vector;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class SPEditori extends JFrame
{
    public static Vector items=new Vector();
    
    String NEWROOM="Ensin luo huone ja lataa taustakuva.";
    
    /** työn alla oleva huone */
    public static Room curRoom = new Room(); // @jve:decl-index=0:
    public static Background background = null;
    private JPanel jContentPane = null;
    private JButton newRoom = null;
    private JButton setBackGround = null;
    private JButton setZBuffer = null;
    private JButton setAction = null;
    private JButton listPolys = null;
    private JButton saveRoom = null;
    private JButton setObjsButton = null;
    
    private JButton pathButton=null, animButton=null, posButton=null;
    
    static String animName="";
    public static void setAnimName(String txt)
    {
	animName=txt;
    }
    
    static void MessageBox(String str)
    {
	System.out.println(str);
	JOptionPane.showMessageDialog(null, str, "SPE", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args)
    {
	MessageBox("SPEditori v0.2.4\nby mjt, 2006-07\nmixut@hotmail.com.");
	
	// varmuuden vuoksi luo hakemistot jossei niitä olisikaan
	(new File("project")).mkdir();
	(new File("project/pics")).mkdir();
	(new File("project/rooms")).mkdir();
	
	SPEditori editor=new SPEditori();
	editor.setVisible(true);
	
    }
    
    // current directory
    String dirRoomStr = null; // mistä ladataan huoneinfot
    String dirPicStr = null; // mistä ladataan kuvat
    
    /**
     * This method initializes newRoom
     *
     * @return javax.swing.JButton
     */
    private JButton getNewRoom()
    {
	final Frame owner = this;
	
	if (newRoom == null)
	{
	    newRoom = new JButton();
	    newRoom.setText("Uusi huone");
	    
	    newRoom.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(polygonList!=null) polygonList.dispose();
		    if(actionDialog!=null) actionDialog.dispose();
		    
		    curRoom.objs.clear();
		    curRoom.polys.clear();
		    
		    curRoom = null;
		    curRoom = new Room();
		    
		    // kysytään huoneen nimi
		    GetName nw = new GetName(owner, "Huoneen nimi", 1);
		    nw.setVisible(true);
		    
		    curRoom.loadItemList(); // lataa esineiden nimet
		}
		
	    });
	    
	}
	return newRoom;
    }
    
    /**
     * This method initializes setBackGround
     *
     * @return javax.swing.JButton
     */
    private JButton getSetBackGround()
    {
	final Frame owner = this;
	
	if (setBackGround == null)
	{
	    setBackGround = new JButton();
	    setBackGround.setText("Aseta taustakuva");
	    setBackGround.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (curRoom.name.equals(""))
		    {
			MessageBox(NEWROOM);
			return;
		    }
		    
		    // avataan dialogi jossa voidaan valita taustakuva
		    JFileChooser chooser = new JFileChooser(dirPicStr);
		    int returnVal = chooser.showOpenDialog(owner);
		    
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			curRoom.loadBackground(chooser.getSelectedFile().getPath());
			
			if(background!=null) background.destroy();
			background = new Background(owner, curRoom.name, curRoom.bgImage);
			
			// ota uusi hakemisto talteen
			dirPicStr=chooser.getSelectedFile().getPath();
			
		    }
		}
	    });
	}
	return setBackGround;
    }
    /**
     * This method initializes setZBuffer
     *
     * @return javax.swing.JButton
     */
    private JButton getSetZBuffer()
    {
	final Frame owner = this;
	if (setZBuffer == null)
	{
	    setZBuffer = new JButton();
	    setZBuffer.setText("Aseta syvyyskartta");
	    setZBuffer.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (curRoom.backGroundImage.equals(""))
		    {
			MessageBox(NEWROOM);
			return;
		    }
		    
		    // avataan dialogi jossa voidaan valita syvyyskartta kuva
		    JFileChooser chooser = new JFileChooser(dirPicStr);
		    
		    int returnVal = chooser.showOpenDialog(owner);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			curRoom.loadZBuf(chooser.getSelectedFile().getPath());
			
			// ota uusi hakemisto talteen
			dirPicStr=chooser.getSelectedFile().getPath();
		    }
		    
		    
		}
	    });
	    
	}
	return setZBuffer;
    }
    
    public static SetAction actionDialog = null;
    /**
     * This method initializes setAction
     *
     * @return javax.swing.JButton
     */
    private JButton getSetAction()
    {
	final Frame owner = this;
	if (setAction == null)
	{
	    setAction = new JButton();
	    setAction.setText("Aseta toiminto");
	    
	    setAction.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (curRoom.backGroundImage.equals(""))
		    {
			MessageBox(NEWROOM);
			return;
		    }
		    if(ListObjs.selected==-1 || ListObjs.selected>=curRoom.polys.size()) return;
		    
		    if(actionDialog!=null)
		    {
			actionDialog.dispose();
			actionDialog=null;
		    }
		    actionDialog = new SetAction(owner, ListObjs.selected);
		    actionDialog.setVisible(true);
		    actionDialog.updateDialog(ListObjs.selected);
		    
		}
	    });
	}
	return setAction;
    }
    
    static ListObjs polygonList = null;
    private JButton openButton = null;
    /**
     * This method initializes listPolys
     *
     * @return javax.swing.JButton
     */
    private JButton getListPolys()
    {
	final Frame owner = this;
	if (listPolys == null)
	{
	    listPolys = new JButton();
	    listPolys.setText("Listaa kaikki");
	    listPolys.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (curRoom.backGroundImage.equals(""))
		    {
			MessageBox(NEWROOM);
			return;
		    }
		    if(polygonList!=null)
		    {
			polygonList.dispose();
			polygonList=null;
		    }
		    updatePolygonList();
		    
		}
	    });
	}
	return listPolys;
    }
    
    /**
     * päivitä lista. jos ikkunaa ei ole, luo se, muuten tyhjennä lista ja täytä uudelleen.
     */
    public static void updatePolygonList()
    {
	if(polygonList==null)
	{
	    polygonList = new ListObjs(null);
	    polygonList.setVisible(true);
	}
	
	polygonList.removeList();
	
	
	int q;
	/**
	 * näytä listassa yksinkertainen tieto ruudulla olevista kamoista. se voi olla paikka, reitti,
	 * scriptikutsu tai sitten vaan infotaan että poly.
	 *
	 * esineetkin VOISI näkyä tässä listassa mutta ei näy ainakaan vielä. (todo,ehkä)
	 */
	for (q = 0; q < curRoom.polys.size(); q++)
	{
	    // jos PATH tai PAIKKA, pistä se listaan
	    if((curRoom.polys.get(q).descStr.length()>=6 && curRoom.polys.get(q).descStr.substring(0, 6).equals("PAIKKA")) ||
		 (curRoom.polys.get(q).descStr.length()>=4 && curRoom.polys.get(q).descStr.substring(0, 4).equals("PATH")) ||
		 (curRoom.polys.get(q).descStr.length()>0 && curRoom.polys.get(q).descStr.charAt(0)=='$')) // script kutsu
	    {
		polygonList.addToList(q, curRoom.polys.get(q).descStr);
	    }
	    else // jos newroomis tekstii, pistä se
		if(!curRoom.polys.get(q).newRoom.equals(""))
		{
		polygonList.addToList(q, curRoom.polys.get(q).newRoom);
		} // jos scriptikutsu
		else
		if(curRoom.polys.get(q).actionStr.length()>0 && curRoom.polys.get(q).actionStr.charAt(0)=='$')
		{
		    polygonList.addToList(q, curRoom.polys.get(q).actionStr);
		}
		else
		if(curRoom.polys.get(q).successUseStr.length()>0 && curRoom.polys.get(q).successUseStr.charAt(0)=='$')
		{
		    polygonList.addToList(q, curRoom.polys.get(q).successUseStr);
		}
		else // muuten vain poly -text
		    polygonList.addToList(q, "Poly"+q);
	}
	
	
	// lisää listan loppuun esineet
	for(int w=0; w<curRoom.objs.size(); w++)
	{
	    polygonList.addToList(q++, curRoom.objs.get(w).name);//+" (x:"+curRoom.objs.get(w).x+",y:"+curRoom.objs.get(w).y+")"); todo näyttäis oikeet objektin koordinaatit
	}

    }
    
    public SPEditori()
    {
	super();
	initialize();
	
	// otetaan ohjelman hakemisto talteen
	try
	{
	    File dir = new File(".");
	    dirRoomStr = dir.getCanonicalPath()+"/project/rooms";
	    dirPicStr = dir.getCanonicalPath()+"/project/pics";
	}
	catch(IOException e)
	{
	    System.out.println(e);
	}
	
	System.out.println(dirRoomStr);
	System.out.println(dirPicStr);
	
    }
    
    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize()
    {
	//this.setSize(169, 290);
	this.setSize(170, 380);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setResizable(false);
	this.setContentPane(getJContentPane());
	this.setTitle("SPEditori");
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
	    jContentPane.add(getNewRoom(), null);
	    jContentPane.add(getSetBackGround(), null);
	    jContentPane.add(getSetZBuffer(), null);
	    jContentPane.add(getLoadAnim(), null);
	    jContentPane.add(getSetPos(), null);
	    jContentPane.add(getSetPath(), null);
	    jContentPane.add(getSetObjsButton(), null);
	    jContentPane.add(getListPolys(), null);
	    jContentPane.add(getSetAction(), null);
	    jContentPane.add(getSaveRoom(), null);
	    jContentPane.add(getOpenButton(), null);
	}
	return jContentPane;
    }
    
    /**
     * kysyy animaation nimeä, sitten open dialogista valitaan animaatioon kuuluvat tiedostot
     */
    private JButton getLoadAnim()
    {
	final Frame owner = this;
	
	animButton = new JButton();
	animButton.setText("Lataa animaatio");
	
	animButton.addActionListener(new java.awt.event.ActionListener()
	{
	    public void actionPerformed(java.awt.event.ActionEvent e)
	    {
		// kysytään animaation nimi
		GetName nw = new GetName(owner, "Animaation nimi", 2);
		nw.setVisible(true);
		if(animName.equals(""))
		{
		    return;
		}
		
		JFileChooser chooser = new JFileChooser(dirPicStr);
		chooser.setMultiSelectionEnabled(true);
		int returnVal = chooser.showOpenDialog(owner);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
		    File[] fs=chooser.getSelectedFiles();
		    
		    String data=animName+"\n";
		    data+=fs.length+"\n";
		    
		    for(int q=0; q<fs.length; q++)
		    {
			data+=fs[q].getName()+"\n";
		    }
		    
		    // tallenna nimet edellisten perään
		    File file = new File("project/anim.cfg");
		    boolean append=false;
		    if(file.exists()) append=true;
		    
		    try
		    {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
			out.print(data);
			out.flush();
			out.close();
		    }
		    catch (IOException ex)
		    {
			ex.printStackTrace();
			return;
		    }
		    animName="";
		    
		    // ota uusi hakemisto talteen
		    dirPicStr=chooser.getSelectedFile().getPath();
		    
		}
		
	    }
	});
	return animButton;
    }
    /**
     * voidaan luoda reitti animaatioita varten.
     */
    private JButton getSetPath()
    {
	final Frame owner = this;
	
	pathButton = new JButton();
	pathButton.setText("Aseta reitti");
	
	pathButton.addActionListener(new java.awt.event.ActionListener()
	{
	    public void actionPerformed(java.awt.event.ActionEvent e)
	    {
		if(curRoom.backGroundImage.equals(""))
		{
		    SPEditori.MessageBox(NEWROOM);
		    return;
		}
		
		
		// kysytään reitin nimi
		GetName nw = new GetName(owner, "Reitin nimi", 3);
		nw.setVisible(true);
		
	    }
	});
	return pathButton;
    }
    /**
     * asetetaan paikka johon ukko asetetaan kun tulee huoneeseen
     */
    private JButton getSetPos()
    {
	final Frame owner = this;
	
	posButton = new JButton();
	posButton.setText("Aseta paikka");
	
	posButton.addActionListener(new java.awt.event.ActionListener()
	{
	    public void actionPerformed(java.awt.event.ActionEvent e)
	    {
		if(curRoom.backGroundImage.equals(""))
		{
		    SPEditori.MessageBox(NEWROOM);
		    return;
		}
		
		// kysytään paikan nimi
		GetName nw = new GetName(owner, "Paikan nimi", 4);
		nw.setVisible(true);
	    }
	});
	return posButton;
    }
    
    /**
     * This method initializes saveRoom
     *
     * @return javax.swing.JButton
     */
    private JButton getSaveRoom()
    {
	if (saveRoom == null)
	{
	    saveRoom = new JButton();
	    saveRoom.setText("Tallenna huone");
	    
	    saveRoom.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if (curRoom.name.equals(""))
			return;
		    
		    curRoom.save();
		    curRoom.saveItemList();
		}
	    });
	    
	}
	return saveRoom;
    }
    
    /**
     * This method initializes setObjsButton
     *
     * @return javax.swing.JButton
     */
    private JButton getSetObjsButton()
    {
	final Frame owner = this;
	if (setObjsButton == null)
	{
	    setObjsButton = new JButton();
	    setObjsButton.setText("Aseta esine");
	    setObjsButton.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    if(curRoom.backGroundImage.equals(""))
		    {
			SPEditori.MessageBox(NEWROOM);
			return;
		    }
		    if (background == null)
			return;
		    
		    // ladataan kuva joka asetetaan ruudulle
		    // avataan dialogi jossa valitaan esine
		    JFileChooser chooser = new JFileChooser(dirPicStr);
		    
		    int returnVal = chooser.showOpenDialog(owner);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			
			
			Item2D obj = new Item2D();
			obj.fileName = chooser.getSelectedFile().getPath();
			obj.pic = curRoom.loadImage(chooser.getSelectedFile().getPath());
			
			background.setItem(obj.pic, curRoom.objs.size());
			
			curRoom.objs.add(obj);
			
			// kysytään vielä esineen nimi
			ItemName in = new ItemName(owner);
			in.setVisible(true);
			
			// päivitä alasvetovalikot toiminto ikkunassa (jos ikkuna näkyvissä)
			if(actionDialog!=null)
			    if(SPEditori.actionDialog.isShowing())
				actionDialog.updateCombos();
			
			// ota uusi hakemisto talteen
			dirPicStr=chooser.getSelectedFile().getPath();
			
			
			updatePolygonList();
		    }
		    
		}
	    });
	}
	
	return setObjsButton;
    }
    
    /**
     * This method initializes openButton
     *
     * @return javax.swing.JButton
     */
    private JButton getOpenButton()
    {
	final Frame owner = this;
	if (openButton == null)
	{
	    openButton = new JButton();
	    openButton.setText("Lataa huone");
	    openButton.addActionListener(new java.awt.event.ActionListener()
	    {
		public void actionPerformed(java.awt.event.ActionEvent e)
		{
		    // avataan dialogi jossa voidaan valita huone
		    JFileChooser chooser = new JFileChooser(dirRoomStr);
		    
		    int returnVal = chooser.showOpenDialog(owner);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
		    {
			// poista vanhat tiedot ja sulje ikkunat
			if (background != null)
			{
			    if (polygonList != null)
				polygonList.dispose();
			    if (actionDialog != null)
				actionDialog.dispose();
			    curRoom.objs.clear();
			    curRoom.polys.clear();
			    background.destroy();
			    background=null;
			}
			
			// lataa tiedot
			curRoom.load(chooser.getSelectedFile().getPath());
			
			// lataa esineiden nimet
			curRoom.loadItemList();
			
			// jos epäonnistui, palaa
			if (curRoom.backGroundImage.equals(""))
			{
			    System.out.println("Huoneen lataaminen epäonnistui!");
			    SPEditori.ErrorMessage("Huoneen lataaminen epäonnistui!");
			    return;
			}
			
			// lataa tausta
			curRoom.loadBackground("project/pics/"+curRoom.backGroundImage);
			background = new Background(owner, curRoom.name, curRoom.bgImage);
			
			// lataa zbuffer
			if(curRoom.zBufImage.length()>0) curRoom.loadZBuf("project/pics/"+curRoom.zBufImage);
			
			// esineet
			for (int q = 0; q < curRoom.objs.size(); q++)
			{
			    // lataa esine
			    curRoom.objs.get(q).pic = curRoom.loadImage("project/pics/"+curRoom.objs.get(q).fileName);
			}
			
			// ota uusi hakemisto talteen
			dirRoomStr=chooser.getSelectedFile().getPath();
			
		    }
		}
	    });
	}
	return openButton;
    }
    
    
    /**
     * näytä virheilmoitus
     */
    static void ErrorMessage(String msg)
    {
	if (msg.equals("")) return;
	System.out.println(msg);
	JOptionPane.showMessageDialog(null, msg, "Virhe", JOptionPane.ERROR_MESSAGE);
    }
    
    
}

/********************************************************
 *******************************************************/

/**
 * esineiden luokka
 *
 */
class Item2D
{
    /** esineen nimi */
    String name = "";
    /** kuvatiedoston nimi */
    String fileName = "";
    /** esineen paikka huoneessa */
    int x = 0, y = 0;
    /** true niin piirretään */
    boolean visible = true;
    
    /** kuva */
    BufferedImage pic = null;
    
    public void save()
    {
	File f=new File(fileName);
	String fn=f.getName();
	
	Room.dataString += "" + name + "\n" + fn + "\n" + x + "\n" + y + "\n" + ((visible==true) ? "true" : "false") + "\n";
    }
}

class Vector2i
{
    public int x = 0, y = 0;
    
    public void save()
    {
	Room.dataString += "" + x + "\n" + y + "\n";
    }
    
}

class Polygon
{
    public void save()
    {
	if(descStr.length()>=6) if(descStr.substring(0, 6).equals("PAIKKA")) block=false;
	
	Room.dataString += "" + descStr + "\n" + actionStr + "\n" + removePoly + "\n" + block + "\n" + itemNum + "\n"
	     + removeFromScreen + "\n" + toInventory + "\n" + needsItem + "\n" + verts.size() + "\n" + newRoom
	     + "\n" + removeFromInventory + "\n" + successUseStr + "\n";
	
	for (int q = 0; q < verts.size(); q++)
	    verts.get(q).save();
    }
    
    /** polygonin verteksit */
    Vector<Vector2i> verts = new Vector<Vector2i>();
    
    /**
     * selitys jos sitä katsoo
     */
    String descStr = "";
    /** jos ota/käytä niin pitääkö kirjoittaa jotain */
    String actionStr = "";
    
    /** hävitetäänkö poly jos ota/käytä */
    boolean removePoly = false;
    /** jos true, poly toimii esteenä */
    boolean block = true;
    
    /** esine tiedot */
    /** jos vaikuttaa esineeseen, sen index */
    int itemNum = 0;
    /** jos ota/käytä, hävitetäänkö esine ruudulta */
    boolean removeFromScreen = false;
    /** tuleeko esine omiin tavaroihin */
    boolean toInventory = false;
    
    /**
     * jos vaatii tavaran ennenkuin yllä olevat tapahtumat tapahtuu. esim ovi
     * vaatii avaimen. needsItem on vaikka 1 joka olisi avain. pelkkä ota/käytä
     * ei avaa ovea koska needsItem>=0 ja jos avaimella klikkaa polya, niin
     * sitten toteutetaan removeFromScreen jos true, removePoly jos true jne.
     */
    int needsItem = -1;
    
    /**
     * jos polygon on linkki uuteen huoneeseen, tässä sen nimi.
     */
    String newRoom = "";
    
    /**
     * poistetaanko esine käytön jälkeen tavaroista
     */
    boolean removeFromInventory = false;
    
    /**
     * onnistuneen esineen käytön teksti
     */
    String successUseStr = "";
}

/**
 * Room luokka tänne asetukset eli taustakuva, syvyysbufferi, huoneessa olevat
 * objektit ym..
 *
 */
class Room
{
    /** tähän tallennettava/ladattava ja käsiteltävä teksti */
    static String dataString;
    
    /**
     * tallenna huoneen tiedot
     */
    public void save()
    {
	FileIO file = new FileIO();
	if (file.createFile("project/rooms/"+name) == false)
	    return; // tiedostonimi on huoneen nimi
	
	dataString = "";
	
	File f=new File(backGroundImage);
	String fnbg=f.getName();
	File f2=new File(zBufImage);
	String fnz=f2.getName();
	
	dataString += name + "\n" + fnbg + "\n" + fnz + "\n" + polys.size() + "\n";
	
	f=null; f2=null;
	
	for (int q = 0; q < polys.size(); q++)
	{
	    polys.get(q).save();
	}
	
	dataString += "" + objs.size()+"\n";
	
	for (int q = 0; q < objs.size(); q++)
	{
	    objs.get(q).save();
	}
	
	file.writeFile(dataString); // tallenna datat
	
	SPEditori.MessageBox(name + " tallennettu..");
	
    }
    
    /**
     * lataa huoneen tiedot muistiin, kuvat, toiminnot, ja pistä ruutuun
     */
    public void load(String fileName)
    {
	FileIO file = new FileIO();
	file.openFile(fileName); // avaa tiedosto
	
	dataString = "";
	dataString = file.readFile(); // koko tiedosto dataStringiin
	
	if (dataString == null)
	{
	    JOptionPane.showMessageDialog(null, "Lataaminen epäonnistui", "Virhe", JOptionPane.ERROR_MESSAGE);
	    return;
	}
	String[] strs = dataString.split("\n"); // palasiksi
	
	// dataString+=name+"\n"+backGroundImage+"\n"+zBufImage+"\n"+polys.size();
	int pos = 0;
	name = strs[pos++];
	backGroundImage = strs[pos++];
	zBufImage = strs[pos++];
	int polys = Integer.parseInt(strs[pos++]);
	
	// aseta polygonin tiedot ja lisää huoneeseen
	for (int q = 0; q < polys; q++)
	{
	    // Room.dataString += "" + descStr + "\n" + actionStr + "\n" +
	    // removePoly + "\n" + block + "\n" + itemNum + "\n"
	    // + removeFromScreen + "\n" + toInventory + "\n" + needsItem + "\n"
	    // + verts.size() + "\n" + newRoom
	    // + "\n" + removeFromInventory + "\n" + successUseStr + "\n";
	    
	    Polygon tmppoly = new Polygon();
	    tmppoly.descStr = strs[pos++];
	    tmppoly.actionStr = strs[pos++];
	    tmppoly.removePoly = (strs[pos++].equals("false") ? false : true);
	    tmppoly.block = (strs[pos++].equals("false") ? false : true);
	    tmppoly.itemNum = Integer.parseInt(strs[pos++]);
	    tmppoly.removeFromScreen = (strs[pos++].equals("false") ? false : true);
	    tmppoly.toInventory = (strs[pos++].equals("false") ? false : true);
	    tmppoly.needsItem = Integer.parseInt(strs[pos++]);
	    int verts = Integer.parseInt(strs[pos++]);
	    
	    tmppoly.newRoom = strs[pos++];
	    tmppoly.removeFromInventory = (strs[pos++].equals("false") ? false : true);
	    tmppoly.successUseStr = strs[pos++];
	    
	    // aseta polyn verteksit
	    for (int w = 0; w < verts; w++)
	    {
		Vector2i v = new Vector2i();
		v.x = Integer.parseInt(strs[pos++]);
		v.y = Integer.parseInt(strs[pos++]);
		
		// lisää verteksit
		tmppoly.verts.add(v);
	    }
	    
	    // ja aseteltu poly huoneeseen
	    SPEditori.curRoom.polys.add(tmppoly);
	}
	
	// aseta objektin tiedot ja lisää huoneeseen
	int size = Integer.parseInt(strs[pos++]);
	for (int q = 0; q < size; q++)
	{
	    Item2D item = new Item2D();
	    item.name = strs[pos++];
	    item.fileName = strs[pos++];
	    item.x = Integer.parseInt(strs[pos++]);
	    item.y = Integer.parseInt(strs[pos++]);
	    item.visible = (strs[pos++].equals("false") ? false : true);
	    
	    // lisää nimi alasvetovalikkoon
	    objs.add(item);
	    
	    
	}
	
    }
    /** huoneen nimi */
    String name = "";
    /** taustakuva */
    String backGroundImage = "";
    /** syvyyskartta */
    String zBufImage = "";
    
    /** esteet ja esineiden alueet */
    Vector<Polygon> polys = new Vector<Polygon>();
    /** esineiden tiedot */
    Vector<Item2D> objs = new Vector<Item2D>();
    
    BufferedImage bgImage = null;
    BufferedImage zImage = null;
    
    
    /** reitin nimi */
    String pathName = "";
    /** paikan nimi */
    String posName="";
    public void setPathName(String name)
    {
	this.pathName = name;
	JOptionPane.showMessageDialog(null, "Ohje: klikkaile ruutuun haluttu reitti ja hiiren oikea lopettaa.", "Aseta reitti", JOptionPane.INFORMATION_MESSAGE);
	
	SPEditori.background.setPath();
    }
    public void createPos(String name)
    {
	posName=name;
	
	JOptionPane.showMessageDialog(null, "Ohje: klikkaa ukon paikka ruudulle.", "Aseta paikka", JOptionPane.INFORMATION_MESSAGE);
	
	SPEditori.background.setPos();
    }
    
    public void setName(String name)
    {
	this.name = name;
    }
    
    public void loadBackground(String name)
    {
	bgImage = loadImage(name);
	backGroundImage = name;
    }
    
    public void loadZBuf(String name)
    {
	zImage = loadImage(name);
	zBufImage = name;
    }
    
    /**
     * lataa kuva
     *
     * @param file
     *            tiedostonimi
     */
    BufferedImage loadImage(String file)
    {
	if (file.equals(""))
	    return null;
	
	BufferedImage bufimage = null;
	BufferedImage image = null;
	System.out.println("loadImage("+file+")");
	
	try
	{
	    URL url = Thread.currentThread().getContextClassLoader().getResource(file);
	    if (url != null)
	    {
		bufimage = ImageIO.read(url);
	    }
	    else
	    {
		bufimage = ImageIO.read(new File(file));
	    }
	    
	    // luo kuva
	    image = new BufferedImage(bufimage.getWidth(), bufimage.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    
	    // piirrä sinne bufimage
	    Graphics g = image.createGraphics();
	    g.drawImage(bufimage, 0, 0, null);
	    
	}
	catch (IOException exc)
	{
	    System.out.println(exc);
	    SPEditori.ErrorMessage(exc.getMessage());
	}
	
	return image;
    }
    
    /**
     * lataa esineiden nimet ja lisää alasvetovalikkoihin joten esineitä voi käyttää
     * joka huoneessa
     */
    void loadItemList()
    {
	SPEditori.items.removeAllElements();
	
	FileIO file = new FileIO();
	File f=new File("project/rooms/items.lst");
	if(f.exists()==false) return;
	
	file.openFile("project/rooms/items.lst");
	
	String dataString = "";
	dataString = file.readFile(); // koko tiedosto dataStringiin
	
	if (dataString == null)
	{
	    return;
	}
	String[] strs = dataString.split("\n"); // palasiksi
	
	for(int q=0; q<strs.length; q++)
	{
	    SPEditori.items.add(strs[q]);
	}
    }
    
    /**
     * tallenna esineiden nimet
     */
    void saveItemList()
    {
	FileIO file = new FileIO();
	if(file.openFile("project/rooms/items.lst") == false) return;
	
	String dataString = "";
	for (int q = 0; q < SPEditori.items.size(); q++)
	{
	    dataString+=SPEditori.items.get(q)+"\n";
	}
	file.writeFile(dataString);
    }
    
    
}
