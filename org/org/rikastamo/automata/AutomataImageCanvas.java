/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rikastamo.automata;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.awt.Color;
import java.awt.Image;
/**
 *
 * @author Raino
 * adds the support for background image and rescaling its elements
 */
public class AutomataImageCanvas extends AutomataCanvas{
    //boolean snap2grid;
    Vector suhteet;
    Image img2;
      /**
     * 
     * @param appa
     * @param x
     * @param y
     * @param h
     * @param w
     */
    public AutomataImageCanvas(AutomataApp appa, int[] x, int[] y, int h,int w){ 
        super(appa, x, y, h, w);
        snap2grid = false;      
        //create proportions table
        AutomataEngine engine = new AutomataEngine();
        suhteet = engine.laskeSuhteet(kultainen_x, kultainen_y, width, height);
    }
       /**
     *
     * @param appa
     * @param x
     * @param y
     * @param h
     * @param w
     */
    public AutomataImageCanvas(AutomataApp appa, Image img, int[] x, int[] y, int h,int w){
        super(appa, x, y, h, w);
        img2 = img;
        snap2grid = false;
        //create proportions table
        AutomataEngine engine = new AutomataEngine();
        //TODO tarvii taulukot
        suhteet = engine.laskeSuhteet(kultainen_x, kultainen_y, width, height);
        //TODO lis‰‰ taulukkoon elementti, jossa image
        AutomataElement image2 = new AutomataElement(img2, height, width);
        elements.add(image2);
    }
  /**
   * No grid restrictions, but automatically scales and moves drawn rectangle
   * eiks snap2grid = false riit‰?
   * @param e
   */
    public void mousePressed2(MouseEvent e){
        int x;
        int y;
        //draw rectangle
        //rescale it using proportions table
        //getClosestGoldenRect(suhteet, height, width);
        //animate scaling?
  }
    /**
     * 
     * @param g
     */
     public void paint(Graphics g){
          for(int x = 0; x <= elements.size()-1; x++){
                AutomataElement ae = (AutomataElement)elements.elementAt(x);
                ae.draw(g);
            }
          //mites skaalaus animointi???
     }
       /**
         * returns the mactching rectangle from the proportions table 
         */
        public Rectangle getClosestGoldenRect(Vector<AutomataElement> suhteet, int height, int width){
            float suhde = height/width;
            float closest = 1.00f;
            int height2 = 0;
            int width2 = 0;
            int x = 0;
            int y = 0;
            //K‰yd‰‰n l‰pi suhteet-vektorissa olevat AutomataElementit ja haetaan se jonka suhde on mahdollisimman l‰hell‰
            /**/
            for(AutomataElement ae : suhteet){
                float koe = java.lang.Math.abs(ae.suhde - suhde);
                if(koe <= closest){
                    closest = koe; 
                    height2 = ae.height;
                    width2 = ae.width;
                    x = ae.x;
                    y = ae.y;                    
                }
            }
/*             */
            //pit‰‰ verrata viel‰ suhteen vastalukuun jos joku olisi l‰hemp‰n‰ sit‰?
            //float antisuhde = width/height;
            return new Rectangle(height2, width2, x, y);
        }
}
