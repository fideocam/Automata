package org.rikastamo.automata;
//import smart-resing
/**
 *
 * @author Raino
 * 
 * adds the functionality to take elements in the image and resize them according to the grid
 */
public class AutomataResizeCanvas { //extends AutomataImageCanvas

    public AutomataResizeCanvas(){
         createGrid();
         //getElements();
         resizeElements();
    }
        /**
     * Automatically analyzes the background image for elements 
     */
    public void getElements(){
            //find elements using pattern recognition
            //store identified element locations to table
    }
    /*
     * method for manually adding elements while automation does not work
     * potentially also used to manually select areas
     * Q: Is rectangle selection ok or should there be pixel accurate selection? 
     */
    public void addElement(int x, int y, int width, int height){
    
    }
    /**
     * takes in object dimensions
     * tries to find matching locations inside a golden square
     * output input for crop chooser?
     */
    public void findGoldenDimensionMatch(int x, int y){
        
    }
    /**
     * Analyse the object dimensions, match to golden grid
     * AutomateEngine.laskeKultainen2d()-metodi hoitaa image cropping vaihtoehdon
     */
    public void createGrid(){
        //check canvas dimensions (Is the picture square, horizontal or vertical?)
            /*if(width >= height){
                //picture is horizontal or square
             }else{
              //picture is vertical
             } 
             */
        //create a square based on height
        //create a grid based on middle square size
        //store grid to table
    }
    /**
     * Makes changes to the image using smart resizing algorithms
     */
    public void resizeElements(){
        //compare the element locations to grid
        //if necessary crop image to better  
        //resize elements to fit areas 
       //if motion vector is available leave room in its direction           
   } 
}
