/**
 * @file Background.java
 * @author mjt, 2006-07
 * mixut@hotmail.com
 *
 * Background ikkunaan avataan kuva, sitten hiirell‰ kliksauttelemalla
 * merkataan esteet ja esineet, luomalla polygoneja. Hiiren vasen luo verteksej‰,
 * oikea sulkee polygonin.
 *
 * Mutta jos asetetaan esine (mode==2), valitaan sille paikka ja klikataan jolloin se j‰‰
 * ruudulle.
 * reitti==3
 * paikka==4
 *
 */
package spe;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Background extends JPanel implements MouseListener, MouseMotionListener
{
    /** index */
    int curItem = -1;
    BufferedImage itemPic = null;
    
    /** 1=polygonin piirto moodi, 2=esineen asetus moodi, 3=reitti, 4=paikka*/
    int mode = 1;
    int selected = -1;
    BufferedImage image = null;
    
    JFrame frame=null;
    
    /**
     * @param owner
     */
    public Background(Frame owner, String name, BufferedImage image)
    {
	if (image == null) return;
	
	frame=new JFrame(name);
	
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
	this.setPreferredSize( new Dimension(image.getWidth()+20, image.getHeight()+20));
	frame.setSize(640, 480);
	frame.getContentPane().add(this);
	frame.pack();
	frame.setVisible(true);
	
	this.image = image;
	repaint();
    }
    
    public void destroy()
    {
	frame.dispose();
    }
    
    /**
     * aseta esine huoneeseen
     *
     * @param item
     *            ladattu esine
     */
    void setItem(BufferedImage item, int i)
    {
	mode = 2; // esineen asetus moodi
	itemPic = item;
	curItem = i;
    }
    
    /**
     * aseta reitti
     */
    void setPath()
    {
	mode=3;
    }
    /**
     * aseta paikka
     */
    void setPos()
    {
	mode=4;
    }
    
    /**
     * aseta valittu poly ja se piirret‰‰n eri v‰rill‰
     *
     */
    public void setSelected(int sel)
    {
	selected = sel;
	repaint();
    }
    
    Polygon poly = null;
    
    /**
     * jos hiiren nappia painetaan
     *
     */
    //public void mouseClicked(MouseEvent me)
    public void mousePressed(MouseEvent me)
    {
	if (mode == 2 || mode==4) // jos esineen asetus tai paikan asennus, poistu
	    return;
	
	if(mode==-1) mode=1;
	
	// vasen nappi lis‰‰ verteksej‰
	if (me.getButton() == MouseEvent.BUTTON1)
	{
	    if (poly == null)
	    {
		poly = new Polygon();
	    }
	    Vector2i v = new Vector2i();
	    v.x = me.getX()-10;
	    v.y = me.getY()-10;
	    
	    poly.verts.add(v); // lis‰‰ verteksi polygoniin
	}
	else if (me.getButton() == MouseEvent.BUTTON3) // oikea nappi sulkee polyn tai lopettaa reitin
	{
	    if (poly == null)
		return;
	    
	    if(mode==1) // jos poly, sulje
	    {
		if (poly.verts.size() < 3)
		    return;
		Vector2i v = new Vector2i();
		
		v.x = poly.verts.get(0).x; // ota eka verteksi
		v.y = poly.verts.get(0).y;
		
		poly.verts.add(v); // vika verteksi on eka verteksi eli poly sulkeutuu
	    }
	    else
	    {
		// aseta reitin nimi
		poly.descStr="PATH "+SPEditori.curRoom.pathName;
		mode=1; // takaisin poly moodiin
	    }
	    SPEditori.curRoom.polys.add(poly); // lis‰t‰‰n poly/reitti huoneeseen
	    
	    // p‰ivit‰ lista jos ikkuna auki
	    SPEditori.updatePolygonList();
	    
	    // valitse luotu poly
	    ListObjs.selected=SPEditori.curRoom.polys.size()-1;
	    SPEditori.polygonList.setSelectedIndex(ListObjs.selected);
	    
	    poly = null;
	}
	
	repaint();
    }
    
    public void mouseClicked(MouseEvent me)
    {
    }
    public void mouseDragged(MouseEvent me)
    {
    }
    public void mouseMoved(MouseEvent me)
    {
	if (mode == 2)
	{
	    SPEditori.curRoom.objs.get(curItem).x = me.getX()-10;
	    SPEditori.curRoom.objs.get(curItem).y = me.getY()-10;
	    repaint();
	}
    }
    public void mouseReleased(MouseEvent me)
    {
	// aseta esine
	if (mode == 2)
	{
	    mode = -1;
	    
	    SPEditori.curRoom.objs.get(curItem).x = me.getX()-10;
	    SPEditori.curRoom.objs.get(curItem).y = me.getY()-10;
	    
	    repaint();
	    return;
	}
	
	// aseta paikka
	if(mode==4)
	{
	    poly = new Polygon();
	    Vector2i v = new Vector2i();
	    v.x = me.getX()-10;
	    v.y = me.getY()-10;
	    
	    poly.verts.add(v); // lis‰‰ verteksi
	    poly.descStr="PAIKKA "+SPEditori.curRoom.posName;
	    
	    SPEditori.curRoom.polys.add(poly); // lis‰t‰‰n PAIKKA huoneeseen
	    
	    // p‰ivit‰ lista jos ikkuna auki
	    SPEditori.updatePolygonList();
	    ListObjs.selected=SPEditori.curRoom.polys.size()-1;
	    SPEditori.polygonList.setSelectedIndex(ListObjs.selected);
	    
	    poly=null;
	    
	    SPEditori.curRoom.posName="";
	    mode=-1;
	}
	
	
    }
    public void mouseEntered(MouseEvent me)
    {
    }
    public void mouseExited(MouseEvent me)
    {
    }
    
    boolean rectfl=false;
    public void paint(Graphics g)
    {
	if(rectfl==false)
	{
	    g.fillRect(0,0, getWidth(), getHeight());
	    rectfl=true;
	}
	
	// siirr‰ taustaa v‰h‰n keskemm‰lle
	g.translate(10, 10);
	
	// piirr‰ taustakuva
	g.drawImage(image, 0, 0, null);
	
	g.translate(0, 0);
	
	// piirr‰ esineet
	for (int q = 0; q < SPEditori.curRoom.objs.size(); q++)
	{
	    Item2D item = SPEditori.curRoom.objs.get(q);
	    g.drawImage(item.pic, item.x, item.y, null);
	}
	
	if (mode == 2)
	{
	    g.drawImage(itemPic, SPEditori.curRoom.objs.get(curItem).x, SPEditori.curRoom.objs.get(curItem).y, null);
	}
	
	// piirr‰ kaikki huoneen polyt
	for (int q = 0; q < SPEditori.curRoom.polys.size(); q++)
	{
	    // jos poly valittu, piirr‰ se eri v‰rill‰ mit‰ normaalisti
	    if (q == selected)
		g.setColor(Color.yellow);
	    else
		g.setColor(Color.green);
	    
	    
	    // jos PAIKKA, piirr‰ siihen kohtaan ruksi
	    if(SPEditori.curRoom.polys.get(q).descStr.length()>=6 && SPEditori.curRoom.polys.get(q).descStr.substring(0, 6).equals("PAIKKA"))
	    {
		// pisteen koordinaatit
		int xx=SPEditori.curRoom.polys.get(q).verts.get(0).x;
		int yy=SPEditori.curRoom.polys.get(q).verts.get(0).y;
		
		g.drawLine(xx-10, yy, xx+10, yy);
		g.drawLine(xx, yy-10, xx, yy+10);
		g.drawLine(xx-10, yy-10, xx+10, yy+10);
		g.drawLine(xx+10, yy-10, xx-10, yy+10);
		
	    }
	    else // muuten poly
		for (int w = 0; w < SPEditori.curRoom.polys.get(q).verts.size() - 1; w++)
		{
		g.drawLine(SPEditori.curRoom.polys.get(q).verts.get(w).x, SPEditori.curRoom.polys.get(q).verts.get(w).y,
		     SPEditori.curRoom.polys.get(q).verts.get(w + 1).x, SPEditori.curRoom.polys.get(q).verts
		     .get(w + 1).y);
		}
	    
	}
	
	// piirr‰ poly jota luodaan
	if (poly != null && poly.verts.size() > 1)
	{
	    for (int q = 0; q < poly.verts.size() - 1; q++)
	    {
		g.setColor(Color.red);
		g.drawLine(poly.verts.get(q).x, poly.verts.get(q).y, poly.verts.get(q + 1).x, poly.verts.get(q + 1).y);
	    }
	}
	
    }
}
