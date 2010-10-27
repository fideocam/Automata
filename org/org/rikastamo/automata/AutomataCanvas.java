package org.rikastamo.automata;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import java.awt.Color;

  public class AutomataCanvas extends java.awt.Canvas implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
         public java.awt.Point point, point2 = null;
         public boolean alku = true;
         public  boolean kesken = false;
         public boolean snap2grid = true;
        // public Vector rectangles = new Vector();
         public Vector elements = new Vector();
         int[] kultainen_x, kultainen_y;
         int height, width;
         public int tool;
         int selected_element;
         boolean dragging;
         Point closest = new Point(0,0);
         AutomataApp app;
          public AutomataCanvas(AutomataApp appa, int h, int w){
            app = appa;
            height = h;
            width = w;
            setBackground(new java.awt.Color(255, 255, 255));
            this.addMouseListener(this);
            this.addMouseMotionListener(this);  
          }         /**
          * constructor
          **/
         public AutomataCanvas(AutomataApp appa, int[] x, int[] y, int h,int w){
             app = appa;
             kultainen_x = x;
             kultainen_y = y;
             height = h;
             width = w;
         //    rectangles.add(new Rectangle(0, 0, height, width));
             AutomataElement root = new AutomataElement(new Rectangle(0, 0, height, width)); 
             root.selected = true;
             root.color = Color.white;
             elements.add(root);
             setBackground(new java.awt.Color(255, 255, 255));
             this.addMouseListener(this);
             this.addMouseMotionListener(this);  
         }
         public AutomataCanvas(int[] x, int[] y, int h,int w){
             kultainen_x = x;
             kultainen_y = y;
             height = h;
             width = w;
         //    rectangles.add(new Rectangle(0, 0, height, width));
             AutomataElement root = new AutomataElement(new Rectangle(0, 0, height, width)); 
             root.selected = true;
             root.color = Color.white;
             elements.add(root);
             setBackground(new java.awt.Color(255, 255, 255));
             this.addMouseListener(this);
             this.addMouseMotionListener(this);  
         }
         public void setTool(int a){
            tool = a;
         }
         public void mouseListener(MouseEvent e){
                if(point2 == null) point2 = new Point(e.getX(),e.getY());
            }
            public void mouseDragged(MouseEvent evt) {
                //piirretään kultaisen leikkauksen viivat
                int x1 = evt.getX();
                int y1 = evt.getY(); //+50
                //int x1_g = getClosestGolden(x1, true);
                //int y1_g = getClosestGolden(y1, false);  
                //          contentPane.getGraphics().drawLine(x1_g-5,y1_g-5,x1_g+5,y1_g+5);
                //   g.drawLine(x1_g-5,y1_g-5,x1_g+5,y1_g+5);
                if (alku)
                    return;  // Nothing to do because the user isn't drawing.
                if(point == null) point = new java.awt.Point();
                point.x = x1;
                point.y = y1;
                repaint();
            } // end mouseDragged.
            public void mouseReleased(MouseEvent evt) { 
                repaint();
            }
            public void mousePressed(MouseEvent e) {
                //TODO pitäis valita nykyinen dokumentti aktiiviseksi jotenkin!!
                //koska eventille ei löydy luovaa dokumenttia...
                //app.frame.setSelected(true);
                
                //TODO pitäskö lisättyjen elementtien aligrideiks lisätä vain niiden kultaiset leikkaukset eli 4 viivaa yhteensä (tarkkuus = 1?) 
              
                int x;
                int y;
            //riippuen työkalusta, tehdään eri asioita!!   
     
                /**
                 * snap to grid
                 */
                 if(snap2grid){
                    Point close = getClosestGolden(e.getX(), e.getY());
                    x = (int)close.getX();
                    y = (int)close.getY();     
                 }else{
                   x = e.getX();
                   y = e.getY();
                 }
             if(tool == 0){  
                //tallennetaan joko alku tai loppu
                if (alku) {
                    if(point == null) point = new Point();
                    point.x = x;
                    point.y = y;
                    alku = false;
                    kesken = true;
                    //  point2 = new Point(x,y);
                }else if(!alku && point.x != point2.x && point.y != point2.y){
                    if(point2 == null) point2 = new Point();
                    point2.x = x;
                    point2.y = y;
                    //add point to vector - laske koko ja mistä suunnasta tullaan!!!
                    x = point.x;
                    y = point.y;
                    int x2 = point2.x;
                    int y2 = point2.y;
            if(x <= x2 && y >= y2){
            //    rectangles.add(new Rectangle(x, y2, x2-x, y-y2));
                elements.add(new AutomataElement(new Rectangle(x, y2, x2-x, y-y2)));
            }else if(x <= x2 && y <= y2){
              //  rectangles.add(new Rectangle(x, y, x2-x, y2-y));
                 elements.add(new AutomataElement(new Rectangle(x, y, x2-x, y2-y)));
            }
            else if(x2 <= x && y2 >= y){
                //rectangles.add(new Rectangle(x2, y, x-x2, y2-y));
                elements.add(new AutomataElement(new Rectangle(x2, y, x-x2, y2-y)));
            }else{
               // rectangles.add(new Rectangle(x2, y2, x-x2, y-y2));
                elements.add(new AutomataElement(new Rectangle(x2, y2, x-x2, y-y2)));
            }
                    //((AutomataElement)(elements.elementAt(selected_element))).selected = false;
                    //selected_element = elements.size()-1;
                    kesken = false;
                    alku = true;
                    point = null;
                    point2 = null;             
                   //update automataproperties
                   updateProperties();
                }
            }else if(tool == 1){
                //select an element
                   for(int a = 0; a <= elements.size()-1; a++){
                        AutomataElement ae = (AutomataElement)elements.elementAt(a);
                        if(ae.Contains(x, y)){
                            selected_element = a;
                            ae.setSelected(true);
                            //change data in properties toolbar to correspond with the chosen element      
                            app.jTable1.setValueAt(ae.name, 0,1);
                            app.jTable1.setValueAt(ae.type, 1,1);
                            app.jTable1.setValueAt(ae.bgcolor, 2,1);
                            app.jTable1.setValueAt(ae.text, 3,1);
                            app.jTable1.setValueAt(ae.textcolor, 4,1);
                            app.jTable1.setValueAt((new Integer(ae.height)).toString(), 5,1);
                            app.jTable1.setValueAt((new Integer(ae.width)).toString(), 6,1);
                            //jump out of loop
                            //on move set dragging to start
                        }
                    } 
                  
   
            }
            }
            public void updateProperties(){
              
                    //add all elements to Layers-tree
                   for(int a = 0; a <= elements.size()-1; a++){
                       AutomataElement ae = (AutomataElement)elements.elementAt(a);
                      //app.jTree2.add(ae.name, new jComponent());
//http://www.exampledepot.com/egs/javax.swing.tree/AddNode.html
                     /*
                   javax.swing.tree.DefaultTreeModel model = (javax.swing.tree.DefaultTreeModel)app.jTree2.getModel();
                   // Find node to which new node is to be added
                   int startRow = 0; String prefix = "J";
                   javax.swing.tree.TreePath path = app.jTree2.getNextMatch(prefix, startRow, javax.swing.text.Position.Bias.Forward);
                   javax.swing.tree.MutableTreeNode node = (javax.swing.tree.MutableTreeNode)path.getLastPathComponent();
                   // Create new node
                   javax.swing.tree.MutableTreeNode newNode = new javax.swing.tree.DefaultMutableTreeNode(ae.name);
                   // Insert new node as last child of node
                   model.insertNodeInto(newNode, node, node.getChildCount());
                   }
                   */
                     //tyhjää puu?
                   //päivitä puu
                   app.updateLayersTree();
        }
            }
            /**
             * Used to delete elements from 
             */
            public void deleteLayer(int numero){
                elements.remove(numero);
            }
            public void mouseEntered(MouseEvent evt) { }   // Some empty routines.
            public void mouseExited(MouseEvent evt) { }    //    (Required by the MouseListener
            public void mouseClicked(MouseEvent evt) { }   //    and MouseMotionListener
            public void mouseMoved(MouseEvent evt) {
                if(!dragging){
                if (alku)
                    return;  // Nothing to do because the user isn't drawing.        
                 if(snap2grid){
                    Point close = getClosestGolden(evt.getX(), evt.getY());
                    if(point2 == null) point2 = new java.awt.Point();
                    point2.x = (int)close.getX();
                    point2.y = (int)close.getY();     
                 }else{
                   if(point2 == null) point2 = new java.awt.Point();
                    point2.x = evt.getX();
                    point2.y =  evt.getY();
                 }
                }
                repaint();
                
            }     //    interfaces).  
    
    /**
     * Piirretään canvakseen dokumentissa olevat asiat xml:stä sekä tällä hetkellä aktiivinen piirtoprosessi
     */
        public void paint(Graphics g){
            //TODO haittaaks tää jotain ett aina piirrettäessä työkalu menee nollaks?
            int tool = 0;
            boolean kesken = false;
            /*
            g.setColor(Color.LIGHT_GRAY); //netscape tykkäs LIGHT_GRAY:stä kyttyrää
            for(int d = 0; d <= kultainen_x.length-1; d++){
                g.drawLine(0, kultainen_x[d], height, kultainen_x[d]);
            }      
             for(int d = 0; d <= kultainen_y.length-1; d++){
                g.drawLine(kultainen_y[d], 0, kultainen_y[d], width);
            }
             **/
            g.setColor(Color.BLACK);
            /*
            //start with one, because of the bg rectangle
            for(int x = 1; x <= rectangles.size()-1; x++){
                //each rectangle should have its own x, y and dimensions
                g.drawRect((int)((Rectangle)(rectangles.elementAt(x))).getX(),(int)((Rectangle)(rectangles.elementAt(x))).getY(), (int)((Rectangle)(rectangles.elementAt(x))).getWidth(), (int)((Rectangle)(rectangles.elementAt(x))).getHeight());  
               // g.fillRect(rectangles.get(x).x, rectangles.get(x).y, 20, 20);
              // AutomataElement ae = new AutomataElement(((Rectangle)(rectangles.elementAt(x))));
              // ae.draw(g);
            }
             **/
             for(int x = 0; x <= elements.size()-1; x++){
                AutomataElement ae = (AutomataElement)elements.elementAt(x);
                ae.draw(g);
            }
           
            if(tool==0 && point !=null && point2 !=null){
                if(kesken) g.setColor(java.awt.Color.red);
            //yläoikea
                int x = point.x;
                int x2 = point2.x;
                int y = point.y;
                int y2 = point2.y;          
            if(x <= x2 && y >= y2){
                //      System.out.println("yläoikea- ok");
                g.drawRect(x, y2, x2-x, y-y2);
            }else if(x <= x2 && y <= y2){
                g.drawRect(x, y, x2-x, y2-y);
                //System.out.println("alaoikea - ok");
            }
            else if(x2 <= x && y2 >= y){
                g.drawRect(x2, y, x-x2, y2-y);
                // System.out.println("alavasen");
            }else{
                g.drawRect(x2, y2, x-x2, y-y2);
                // System.out.println("ylävasen");
            }
            }
            
            g.setColor(Color.RED);  
            if (alku) {
                if(point != null)closest = getClosestGolden(point.x, point.y);
            }else{
                if(point2 != null) closest = getClosestGolden(point2.x, point2.y);
            }
            //TODO jos piirtäminen jää kesken jää pyörylä ruudulle
            if(closest!= null)    g.drawOval((int)(closest.getX())-5,(int)(closest.getY())-5, 10, 10);
        }
        /**
         * returns the closest point 
         * takes into account table on selected layer
         * 
         * antaa piirrellä vasemmassa reunassa mitä vaan????
         */
        public Point getClosestGolden(int point_x, int point_y){
                AutomataElement ae = (AutomataElement)elements.elementAt(selected_element);
                kultainen_x = ae.kultainen_x;
                kultainen_y = ae.kultainen_y;
                int alaraja = point_x; //onko ongelma?
                int x = 0;
                int y = 0; 
                int a = 0;
                if(kultainen_x.length >= 1){
                while(kultainen_x[a] <= alaraja){            
                    if(a <= kultainen_x.length-2){ 
                    a++;
                    }else{
                     break;
                    }
                }
               if(a-1>=0)
                alaraja = kultainen_x[a-1];
                int yläraja = kultainen_x[a];
                if(point_x-alaraja >= yläraja-point_x){
                    x = yläraja;   
                }else{
                    x = alaraja;
                }
                int b = 0;
                alaraja = point_y;
                 while(kultainen_y[b] <= alaraja){            
                    if(b<= kultainen_y.length-2){ b++;
                }else{
                    break;
                }
           
                }
                if(b>=1) alaraja = kultainen_y[b-1];
                yläraja = kultainen_y[b];
                if(point_y-alaraja >= yläraja-point_y){
                    y = yläraja;   
                }else{
                    y = alaraja;
                }
                }
            return new Point(x, y);
        }
        /**
         * returns the closest point - useless!
         */
        public Point getClosestGolden2(int point_x, int point_y){
        int alaraja = point_x;
        int x = 0;
        int y = 0; 
        int a = 0;
        if(kultainen_x.length >= 1){
        while(kultainen_x[a] <= alaraja){            
           if(a <= kultainen_x.length-2){ 
               a++;
           }else{
            break;
           }
        }
        alaraja = kultainen_x[a-1];
        int yläraja = kultainen_x[a];
        if(point_x-alaraja >= yläraja-point_x){
         x = yläraja;   
        }else{
        x = alaraja;
        }
        int b = 0;
        alaraja = point_y;
        while(kultainen_y[b] <= alaraja){            
           if(b<= kultainen_y.length-2){ b++;
           }else{
            break;
           }
           
        }
        if(b>=1) alaraja = kultainen_y[b-1];
        yläraja = kultainen_y[b];
        if(point_y-alaraja >= yläraja-point_y){
            y = yläraja;   
        }else{
            y = alaraja;
        }
    }
        return new Point(x, y);
    }
        
      
        }