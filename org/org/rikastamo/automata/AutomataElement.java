/*
 * AutomataElement.java
 *
 * Created on 19. marraskuuta 2004, 23:27
 */

package org.rikastamo.automata;
   import java.awt.Graphics;
   import java.awt.Color;
   
/**
 *
 * @author  Raino
 */
public class AutomataElement {
    int x = 10;
    int y = 20;
    int width = 30;
    int height = 40;
    int border = 15;
    float suhde = 1.00f;
    public java.awt.Rectangle rec;
    boolean selected = false;
    Color color = Color.lightGray;
    Color color2 = Color.pink;
    Color textcolor = Color.white;
    Color gridcolor = Color.GRAY;
    String name = "element";
    String type = "div";
    String bgcolor = "blue";
    int[] kultainen_x, kultainen_y;
    String text = "lorem ipsum doloris amet";
    java.awt.Image img;
    //enum type = {text, menu, image}; 
    
    /** Creates a new instance of AutomataElement */
    public AutomataElement() {
    }
    public AutomataElement(java.awt.Rectangle rect){
            rec = rect;   
            x = rect.x;
           y = rect.y;
           width = rect.width;
           height = rect.height;
           suhde = width/height;
           //create the grid
              AutomataEngine engine = new AutomataEngine();
             kultainen_x =  engine.laskeKultainen(4, width); 
             kultainen_y =  engine.laskeKultainen(4, height);
             name = name + ": " +  width + "*" + height;
    }
    public AutomataElement(java.awt.Image img2, int x2, int y2){
            img = img2;
            rec = new java.awt.Rectangle(x2, y2);
           y = rec.y;
           width = rec.width;
           height = rec.height;
           suhde = width/height;
           //create the grid
              AutomataEngine engine = new AutomataEngine();
             kultainen_x =  engine.laskeKultainen(4, width);
             kultainen_y =  engine.laskeKultainen(4, height);
             name = name + ": " +  width + "*" + height;
    }
   public void setSelected(boolean bol){
       if(selected && bol){
            selected = false;
       }else{
            selected = bol;
       } 
   }
    public boolean Contains(int x, int y){
        return rec.contains(x, y);
    }
    public boolean Contains(java.awt.Rectangle rect){
        return rec.contains(rect);
    }
     /*
     * draw the element
     **/
    public void draw(Graphics g){
         //TODO siirrä kuvan piirtäminen tänne AutomaElementtiin

         if(img != null){
            //draw image
                g.drawImage(img, width, height, null);
         }else{
             //TODO kuva piirrettäväksi
            //System.out.println("ei kuvaa piirrettäväksi");
         }
        g.setColor(color);
        if(selected) g.setColor(color2);
        g.fillRect(x, y, width, height);
   
        if(selected){
           g.setColor(gridcolor); 
            for(int d = 0; d <= kultainen_x.length-1; d++){          
                g.drawLine(x+kultainen_x[d], y, x+kultainen_x[d], y+height);
            }      
             for(int d = 0; d <= kultainen_y.length-1; d++){
                g.drawLine(x, y+kultainen_y[d], x+width, y+kultainen_y[d]);
          }
        }else{
            /*
             *we should still figure out a way to add \n to fit in the rows inside the element
            g.setColor(textcolor);
            g.drawString(text, x+border, y+border);   
        */
             }
    }
}
