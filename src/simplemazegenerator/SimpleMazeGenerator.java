/*
 * 
 */

package simplemazegenerator;

/**
 *
 * @author Noah
 */
public class SimpleMazeGenerator {
    
    public static final int MAZE_WIDTH = 20;
    public static final int MAZE_HEIGHT = 10;

    public static void main(String[] args) {
        
        MazeCell[][] mazeGrid = new MazeCell[MAZE_WIDTH][MAZE_HEIGHT];
        int numTotalCells = MAZE_WIDTH * MAZE_HEIGHT;
        
        //Initialize the maze grid
        for(int i = 0; i < MAZE_WIDTH; i++){
            for(int j = 0; j < MAZE_HEIGHT; j++){
                mazeGrid[i][j] = new MazeCell(i, j);
            }
        }
        
        //Run the processCell method on the maze grid
        processCell(mazeGrid[0][0], 0, numTotalCells, mazeGrid);
        
        //Traverse all cells in the maze grid, setting each one's visited value to false.
        //This is necessary for the solveMaze method to work correctly, as solveMaze relies
        //upon every cell having visited equal to false so that it can process the cells and
        //find the solution.
        for(int i = 0; i < MAZE_WIDTH; i++){
            for(int j = 0; j < MAZE_HEIGHT; j++){
                mazeGrid[i][j].setVisited(false);
            }
        }
        
        //Run the solveMaze method on the maze grid. Note that the current first cell of the maze
        //is set as cell 0,0 but any cell could be set as the first cell and this method would
        //then find the solution from that cell to the given last cell of the maze.
        solveMaze(mazeGrid[0][0], mazeGrid);
        
        //Display the generated and solved maze grid
        display(mazeGrid);
    }
    
    /**
     * Method which solves the maze using recursive backtracking. Note that this outcome could probably
     * be achieved with modifications to processCell, but I suck at recursive backtracking so I decided
     * to practice that instead.
     * @param c
     * @param mazeGrid
     * @return
     *      true if the end cell can be reached from the current cell, false otherwise.
     */
    public static boolean solveMaze(MazeCell c, MazeCell[][] mazeGrid){
        
        /* Method first sets the solution value of the current cell to true. This is necessary so that when
        displaying the maze, solution cells can be marked accordingly. Method initially assumes that the end
        cell can be reached from the current cell. */
        c.setSolution(true);
        
        /* Method then sets the visited value of the current cell to true. This prevents the method from running
        forever, as it will not continue to recursively call solveMaze on previously visited cells. */
        c.setVisited(true);
        
        /* Method checks whether or not current cell is the end cell of the maze. At this point, the end cell is set
        as the bottom right cell of the maze, if 0,0 is the cell in the top right-hand corner. Note that the end
        cell could just as easily be set as any cell in the maze, and the program would then find a solution from
        the beginning cell to that cell. */
        if(c.getX() == MAZE_WIDTH - 1 && c.getY() == MAZE_HEIGHT - 1)
            return true;   
        
        /* Method checks if the current cell is connected to the cells to the left, right, top, and bottom of it. If it
        is connected to the cell to the left of it, and that cell has not yet been visited, solveMaze is then called on 
        that cell. solveMaze will only return true if the cell it is called on, or one of the cells that can be reached
        from it, is the end cell of the maze. */
        if((!c.hasLeftWall() && !mazeGrid[c.getX() - 1][c.getY()].isVisited() && solveMaze(mazeGrid[c.getX() - 1][c.getY()], mazeGrid)) ||
            (!c.hasRightWall() && !mazeGrid[c.getX() + 1][c.getY()].isVisited() && solveMaze(mazeGrid[c.getX() + 1][c.getY()], mazeGrid)) ||
            (!c.hasTopWall() && !mazeGrid[c.getX()][c.getY() - 1].isVisited() &&solveMaze(mazeGrid[c.getX()][c.getY() - 1], mazeGrid)) ||
            (!c.hasBottomWall() && !mazeGrid[c.getX()][c.getY() + 1].isVisited() && solveMaze(mazeGrid[c.getX()][c.getY() + 1], mazeGrid)))
            //If the end cell can be reached from the current cell, method returns true
            return true;
        else{
            //If the end cell cannot be reached from the current cell, solution is set to false and the method returns false.
            c.setSolution(false);
            return false;
        }
    }
    
    /**
     * Recursive method to process all cells in the array and generate the maze.
     * @param toProcess
     * @param numVisitedCells
     * @param numTotalCells
     * @param mazeGrid 
     */
    public static int processCell(MazeCell toProcess, int numVisitedCells, int numTotalCells, MazeCell[][] mazeGrid){
        
        /* Method first sets the current cell's visited value to true. This prevents it from being recursively called
        on cells which it has already been called on. */
        toProcess.setVisited(true);
        numVisitedCells++;
        
        /* Stopping case. If all the cells in the maze have been visited, the method should return without doing anything. */
        if(numVisitedCells == numTotalCells)
            return numVisitedCells;
        
        /* Otherwise, while the current cell has unvisited neighbors, a random unvisited neighbor is selected and the wall
        between it and the current cell is removed. processCell is then called on that neighbor. This continues until the 
        current cell no longer has unvisited neighbors. */
        while(hasUnvisitedNeighbors(toProcess, mazeGrid)){
            MazeCell nextCell = getRandomUnvisitedNeighbor(toProcess, mazeGrid);
            numVisitedCells = processCell(nextCell, numVisitedCells, numTotalCells, mazeGrid);
        }
        
        /* The method returns the number of cells which have been visited. */
        return numVisitedCells;
    }
    
    /**
     * Checks whether or not the given cell has unvisited neighbors.
     * @param c
     * @param mazeGrid
     * @return 
     */
    public static boolean hasUnvisitedNeighbors(MazeCell c, MazeCell[][] mazeGrid){
        if(!(c.getX() - 1 < 0) && !mazeGrid[c.getX() - 1][c.getY()].isVisited())
            return true;
        if(!(c.getX() + 1 >= MAZE_WIDTH) && !mazeGrid[c.getX() + 1][c.getY()].isVisited())
            return true;
        if(!(c.getY() - 1 < 0) && !mazeGrid[c.getX()][c.getY() - 1].isVisited())
            return true;
        if(!(c.getY() + 1 >= MAZE_HEIGHT) && !mazeGrid[c.getX()][c.getY() + 1].isVisited())
            return true;
        
        return false;
    }
    
    /**
     * Gets a random unvisited neighbor of the given cell and removes the walls separating it
     * and the given cell. Note that this method should not result in an infinite loop, since 
     * it will only be called when the given cell has already been shown to have unvisited neighbors.
     * @param c
     * @param mazeGrid
     * @return 
     */
    public static MazeCell getRandomUnvisitedNeighbor(MazeCell c, MazeCell[][] mazeGrid){
        MazeCell toReturn = new MazeCell(-1, -1);
        toReturn.setVisited(true);
        do{
            int randomInt = (int)(Math.random() * 4);
            if(randomInt == 0 && !(c.getX() - 1 < 0) && !mazeGrid[c.getX() - 1][c.getY()].isVisited()){
                toReturn = mazeGrid[c.getX() - 1][c.getY()];
                toReturn.setHasRightWall(false);
                c.setHasLeftWall(false);
            }
            else if(randomInt == 1 && !(c.getY() - 1 < 0) && !mazeGrid[c.getX()][c.getY() - 1].isVisited()){
                toReturn = mazeGrid[c.getX()][c.getY() - 1];
                toReturn.setHasBottomWall(false);
                c.setHasTopWall(false);
            }
            else if(randomInt == 2 && !(c.getX() + 1 >= MAZE_WIDTH) && !mazeGrid[c.getX() + 1][c.getY()].isVisited()){
                toReturn = mazeGrid[c.getX() + 1][c.getY()];
                toReturn.setHasLeftWall(false);
                c.setHasRightWall(false);
            }
            else if(randomInt == 3 && !(c.getY() + 1 >= MAZE_HEIGHT) && !mazeGrid[c.getX()][c.getY() + 1].isVisited()){
                toReturn = mazeGrid[c.getX()][c.getY() + 1];
                toReturn.setHasTopWall(false);
                c.setHasBottomWall(false);
            }
        }
        while(toReturn.getX() == -1 && toReturn.getY() == -1);
        
        return toReturn;
    }
    
    /**
     * Prints a display of the maze grid. Note that each solution cell will be marked by four stars
     * in the middle of the cell.
     * @param mazeGrid 
     */
    public static void display(MazeCell[][] mazeGrid){
        for(int i = 0; i < MAZE_HEIGHT; i++){
            
            //Print the top wall
            for(int j = 0; j < MAZE_WIDTH; j++){
                if(mazeGrid[j][i].hasTopWall())
                    System.out.print("+------");
                else
                    System.out.print("+      ");
            }
            System.out.print("+\n");
            
            //Print the left wall
            for(int k = 0; k < 2; k++){
                for(int j = 0; j < MAZE_WIDTH; j++){
                    if(mazeGrid[j][i].hasLeftWall())
                        System.out.print("|");
                    else
                        System.out.print(" ");
                    
                    if(mazeGrid[j][i].getSolution())
                        System.out.print("  **  ");
                    else
                        System.out.print("      ");
                }
                System.out.print("|\n");
            }
        }
        
        //Print the bottom wall
        for(int j = 0; j < MAZE_WIDTH; j++){
                System.out.print("+------");
        }
        System.out.print("+");
    }
}
