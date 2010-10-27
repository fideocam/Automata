/*
 * automataEngine.java
 *
 * Created on 5. joulukuuta 2003, 21:54
 */

package org.rikastamo.automata;
import java.util.Vector;
/**
 *
 * @author  rannala
 */
public class AutomataEngine {
        static double phi = 1.6180339;
    static double antiphi = 0.61803;
        int[] kultainenleikkaus, kultainenleikkaus2 = null;
    /** Creates a new instance of automataEngine */
        public AutomataEngine() {
    }
       /**
     * bubble sort for arranging the items in the array order
     */
    private int[] sortArray(int theArray[], int size) {
        // The bubble sort algorithm makes size-1 passes through the array.  In
        // each pass, it swaps adjacent pairs of elements if they are in the
        // wrong order.
        int pass; // the number of the pass we're currently on
        int pairStart; // the position of the first element of the pair being
        // tested and possibly swapped
        int tempInt; // a temporary integer used for swapping integers
        for (pass = 0; pass < size-1; pass++) {
            for (pairStart = 0; pairStart < size-pass-1; pairStart++) {
                // If this pair is out of order, swap them
                if (theArray[pairStart] > theArray[pairStart+1]) {
                    tempInt = theArray[pairStart];
                    theArray[pairStart] = theArray[pairStart+1];
                    theArray[pairStart+1] = tempInt;
                } // end if
            } // end for pairStart
        } // end for pass
        //remove zeros from the beginning!!
        int laskuri = 0;
        for(int a = 0; a < size; a++){
            if(theArray[a] == 0) laskuri ++;
        }
        int[] array = new int[size-laskuri];
        for(int a = 0; a < size-laskuri; a++){
            array[a] = theArray[laskuri+a];
        }
        return array;
    } // end sortArray
      /**
     * funktio laskee kultaisen leikkauksen mukaiset 
     *
     */
    public int[] laskeKultainen(int tarkkuus, int leveys){
        //pitäis osata arvioida kuinka monta tarvitaan - geometrinen sarja?
        
        int[] golden = new int[tarkkuus*4]; // = {1/phi, 1-1/phi};
        golden[0] = (int)(leveys*antiphi);
        for (int a = 1; a <= tarkkuus-1; a++){
            golden[a] = (int)(antiphi*golden[a-1]);
            //  System.out.print(" - " + golden[a]);
            if(golden[a] <= 1) a = tarkkuus;
        }
        //  System.out.println("\npisteet 2");
        //lisätään vielä toisinpäin
        //golden[tarkkuus/2 + b] <= leveys-1
        for (int b = 1; tarkkuus/2+b <= tarkkuus; b++){
            golden[tarkkuus/2+b] = leveys-golden[b];
            //    System.out.print(golden[tarkkuus/2+b] + " - ");
            if(golden[tarkkuus/2+b] >= leveys-1) b = tarkkuus;
        }         
       //pitäis lisätä vielä reun
         golden[tarkkuus+1] = leveys;
        //lisätään keskeisleikkaus
        golden[tarkkuus+2] = leveys/2;       
        //lisätään myös keskeisleikkauksen suhteen sopivat
        for (int c = 0; c <= tarkkuus-1; c++){
            //lähdetään puolesta välistä ulospäin
            golden[c+tarkkuus*2-1] = (int)(leveys/2 + golden[c]/2);
        }
            for (int c = 0; c <= tarkkuus-2; c++){
            //lähdetään puolesta välistä ulospäin
            golden[c+tarkkuus*3-1] = (int)(leveys/2 - golden[c]/2);
        }
        //järjestä suuruusjärjestykseen
        golden = sortArray(golden, tarkkuus*4);
        /** printtaa
        System.out.println("\npisteet 3");
        for (int a = 0; a <= golden.length - 1; a++){
            System.out.print(golden[a] + " - ");
        }
        */
        return golden;
    }
    /**
     * Recursive method for creating the golden section table
     */
    public int[] laskeKultainen2(int kierroksia, int a, int ala, int ylä){      
        if(kultainenleikkaus != null){
            //hello
        }else{ 
            int[] kultainenleikkaus = new int[kierroksia*2];
        }
        kultainenleikkaus[a] = (int)(ala + (ylä-ala)*antiphi);
        kultainenleikkaus[a+1] = (int)(ala + (ylä-ala)*(1-antiphi));
        
         kierroksia--;
         a = a + 2;
         
         //syntyy aina kolme palaa, jotka pitäisi taas jakaa edelleen (rekursiivisesti?)
         //toisaalta pitäiskö piste lisättyä katsoa suhteet kaikkiin olemassa oleviin pisteisiin?
         
        if(kierroksia >= 1) kultainenleikkaus = laskeKultainen2(kierroksia, a, ala, ylä);
         return kultainenleikkaus;
    }
    /**
     * Analyses canvas dimensions and creates grid based on document orientation
     * poikkeaa muista metodeista sillä, että vaatii sekä leveyden, että korkeuden
     */
    public int[][] laskeKultainen2D(int height, int width, int tarkkuus){
        int[][] kultainenleikkaus2d = new int[tarkkuus*2][tarkkuus*2];
        //check canvas dimensions (Is the picture square, horizontal or vertical?)
            if(width == height){
                //picture is square
                //tarvii laskea vain sisällä olevat leikkaukset
             }else if(width >= height){
                 //picture is horizontal
                 //tarvii laskea leveyssuunnassa ylimääräisiä leikkauksia keskusneliöön pohjautuen
             }else{
              //picture is vertical
             //tarvitsee laskea korkeussuunnassa ylimääräisiä leikkkauksia keskusneliöön pohjautuen
             } 
        //create a square based on height
        //create a grid based on middle square size
        //store grid to table
         return kultainenleikkaus2d;
    }
    /**
     * creates proportion array
     * palauttaa kaikkien kultaisten leikkauksien muodostamien suorakaiteiden suhteet
     */
    public Vector laskeSuhteet(int[] kultainen_x, int[] kultainen_y,  int width, int height){
        //TODO ei määritelty aikaisemmin!!
        int tarkkuus_x = kultainen_x.length;
        int tarkkuus_y = kultainen_y.length;
        
        //TODO tallennetaan suhteet automataelementeiksi suhteet vektoriin x, y, width, height, 
        Vector suhteet = new Vector();
              
        //etsitäänkö suorakaiteet vain kultaisten leikkausten ymäriltä? -> ei rajoihin rajoittuvia???
        //vai otetaanko huomioon reunasta lähteävät vain kultaisen leikkauksen rajaan tulevat?
        
        //käy taulut läpi
         for (int x1 = 1; x1 <= tarkkuus_x-1; x1++){
             for (int y1 = 1; y1 <= tarkkuus_y-1; y1++){
                //hae kaikki pisteen vasemmalla olevat
                for (int x2 = 1; x2 <= tarkkuus_x-1; x2++){
                    for (int y2 = 1; y2 <= tarkkuus_y-2; y2++){
                    //tsekkaa ettei ittensä kanssa, ei voi jakaa nollalla
                        if(x1 != x2 && y1 != y2){
                           
                            int w = java.lang.Math.abs(kultainen_x[x1]-kultainen_x[x2]);
                            int h = java.lang.Math.abs(kultainen_y[y1]-kultainen_y[y2]);
                            //vaadi minimi pinta-ala
                            if(w*h >= 100){
                                float suhde = w/h;  
                               java.awt.Rectangle rec = new java.awt.Rectangle(kultainen_x[x1], kultainen_y[y1],w, h);
                               suhteet.add(new AutomataElement(rec));
                            }                          
                        }                 
                    }
                }
             }
        }
        //duplikaatteja ei tarvi poistaa, koska se on ihan oikea mahis kuiteskin
        //TODO järjestä vai tehdäänkö vasta hakuvaiheessa?
         //suhteet = sortVector(suhteet); 
        return suhteet;
    }
       /**
     * Sets the given width and height as one and attempts to find the next golden value matches. 
     * Method is used for coming up with nice cropping for photos. Calculation is based on a selected rectangular elements in the picture.
     */
    public void laskeUlkoKultainen(int one, int two){
        //find closest golden match for the suhde of the given number pair
        //calculate the following widths in the picture
    }
    /**
     * returns golden curves
     * vectors where consisting box dimensions are according to golden proportions
     * containing box proportions calculated by laskeSuhteet + antennas 1/3 tai 2/3 
     */
    public void laskeKultaisetKurvit(){
    
    }
    /**
     * creates number of golden degrees
     * does only primary golden values??? 
     * is the 360 right? atan gives out radians??? should it be 360/2pii
     */
    public void laskeKultaisetKulmat(int montako){
        int[] kulmat = new int[montako];
        for(int a = 0; a <= montako-1; a++){
            kulmat[a] = (int)(java.lang.Math.atan(1/java.lang.Math.pow(phi,a))*360);   
            System.out.println(kulmat[a]);
        }
    }
}   
