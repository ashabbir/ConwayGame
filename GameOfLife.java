
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
* @author AShabbir
* @description this will execute the game open file over command line args if present or would take default file
*/
public class GameOfLife {

    public static void main(String[] args) throws FileNotFoundException {
        
		//check args if any set filename to the args else use default file name then open read file 
        String fileName;
        if (args.length > 0) {
            fileName = args[0].trim();
        } else {
            fileName = "life.txt";
			//fileName = "test\test.txt";
        }

		//put it in input array 1 dimension
        int rows = 0;
        int columns = 0;

        String line;
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        if (scanner.hasNext()) {
            String delimiter = " ";
            line = scanner.nextLine().trim();
            String tempHolder[] = line.split(delimiter);
            for (String element : tempHolder) {
                if (element != null && element.trim().length() > 0 && rows == 0) {
                    rows = Integer.parseInt(element);
                } else if (element != null && element.trim().length() > 0 && columns == 0) {
                    columns = Integer.parseInt(element);
                }
            }
        }

		//if input file is not of the right format dont crash; exit gracefully
		if(rows ==0 || columns == 0)
		{
			System.out.println("rows or columns not set in file!!!!");
			return;
		}
		
		//Create World
        World world = new World(rows, columns);
        int tempRow = 0;

		//read remaining file and create your world
        while (scanner.hasNext()) {
            char tempHolder[] = scanner.nextLine().toCharArray();
            int tempCol = 0;
            for (char element : tempHolder) {
                if ((element == '*') && 
					(tempRow < rows) && 
					(tempCol < columns)) {
                    world.initializeCell(tempRow, tempCol);
                }
                tempCol++;
            }
            tempRow++;
        }
        scanner.close();
		
		
		//print initial world
		world.printWorld();
		
		//process world 10 times
		world.processWorld(10);   
    }	

}





/**
* @description World class. contains dimension (rows/columns)  and a 2D array (collect of cells in a world)
*/
class World {

	//Class variable declaration section
    private Cell[][] cells;
    private int rows;
    private int columns;


	/**
	* @param int rows and int columns
	* @description no default ctor present. this ctor will initalize world over rows/columns and create cells
	*/
    public World(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        cells = new Cell[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                cells[row][column] = new Cell();
            }
        }
    }

	
	/**
	* @param int noOfTimes
	* @return none
	* @description processes ticks noOfTime and prints world by calling printworld method
	*/
	public void processWorld(int noOfTimes){
		for (int count = 0; count < noOfTimes ; count ++) {    
            tick();
			printWorld();
        }
	}


	/**
	* @param int rows and int column
	* @return none
	* @description initializes(brings to live) cell over row/column position 
	*/
    public void initializeCell(int rowPosition, int columnPosition) {
        cells[rowPosition][columnPosition].setState(true);
    }


	/**
	* @param none
	* @return none
	* @description utility function that would print the world to STD output.
	*/
    public void printWorld() {
		//loop through the world and print
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                System.out.print(cells[row][column]);
            }
			
			//change line 
            System.out.println();
        }

		//print line seperator
        for (int i = 0; i < columns; i++) {
            System.out.print("=");
        }

        //change line
        System.out.println();
    }


	/**
	* @param none
	* @return none
	* @description performs the tick even for a world, Checks next state
	*	then switches the state for all cell
	*/
    public void tick() {
        //get live neighbours count for each Cell

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int liveNeighboutCount = 0;
                for (int x = r - 1; x <= r + 1; x++) {
                    for (int y = c - 1; y <= c + 1; y++) {
	
						//dont count self in live enighbours
                        if (x == r && y == c) {
                            continue;
                        }
						
						//lower bound check
                        if (x < 0 || y < 0) {
                            continue;
                        }

						//upper bound check
                        if (x >= rows || y >= columns) {
                            continue;
                        }

                        if (cells[x][y].getState()) {
                            liveNeighboutCount++;
                        }

                    }
                }
                cells[r][c].setLiveNeighbours(liveNeighboutCount);
            }
        }

        //Set nextState to Live or Dead 
        for (Cell[] sellRow : cells) {
            for (Cell singleCell : sellRow) {
                if (singleCell.getState()) {
                    if (singleCell.getLiveNeighbours() == 2 || singleCell.getLiveNeighbours() == 3) {
                        singleCell.setNextState(true);
                    } else {
                        singleCell.setNextState(false);
                    }
                } else {
                    if (singleCell.getLiveNeighbours() == 3) {
                        singleCell.setNextState(true);
                    } else {
                        singleCell.setNextState(false);
                    }
                }
            }
        }


        //call finish Tick for all Cells, that would move nextState to isLive
        for (Cell[] sellRow : cells) {
            for (Cell singleCell : sellRow) {
                singleCell.finishTick();
            }
        }
    }
}







/**
* @description Cell class. shows current state, future state and count of all live neighbours
*/
class Cell {
	
	//Class variable declaration section
    private boolean isLive;
    private boolean nextState;
    private int liveNeighbours;

	//constructors  section
    public Cell() {
    }


	/**
	* @param none
	* @return none
	* @description switches state between live and dead, called from world Tick method.
	*/
    public void finishTick() {
        if (this.nextState) {
            this.isLive = true;
        } else {
            this.isLive = false;
        }

    }

	/**
	* @param none
	* @return boolean
	* @description getter for isLive.
	*/
    public Boolean getState() {
        return isLive;
    }


	/**
	* @param boolean
	* @return none
	* @description setter for isLive.
	*/
    public void setState(Boolean isLive) {
        this.isLive = isLive;
    }

	/**
	* @param boolean
	* @return none
	* @description setter for nextState.
	*/
    public void setNextState(Boolean nextState) {
        this.nextState = nextState;
    }

	/**
	* @param int
	* @return none
	* @description setter for liveNeighburs.
	*/
    public void setLiveNeighbours(int liveNeighbours) {
        this.liveNeighbours = liveNeighbours;
    }

	/**
	* @param none
	* @return int
	* @description getter for liveNeighbours.
	*/
    public int getLiveNeighbours() {
        return this.liveNeighbours;
    }


    @Override
    public String toString() {
        return (isLive ? "*" : " ");
    }
}