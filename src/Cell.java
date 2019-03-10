
import static java.util.Arrays.*;

public class Cell {

    public int row;
    public int column;
    public int actualValue;
    private byte[] possibleAssignments;
    private int puzzleSize;
    public boolean isStatic;

    /**
     * Constructor
     * */
    public Cell(int puzzleSize, int i, int j){
        this.puzzleSize = puzzleSize;
        possibleAssignments = new byte[puzzleSize + 1];
        fill(possibleAssignments, (byte)1);
        possibleAssignments[0] = 0;
        isStatic = false;
        row = i;
        column = j;
    }

    /**
     * Copy constructor
     * */
    public Cell(Cell originalCell) {

        actualValue = originalCell.actualValue;
        puzzleSize = originalCell.puzzleSize;
        possibleAssignments = new byte[puzzleSize+1];
        isStatic = originalCell.isStatic;
        row = originalCell.row;
        column = originalCell.column;
        for(int i=1; i<=puzzleSize; ++i){
            possibleAssignments[i] = originalCell.possibleAssignments[i];
        }
    }

    /**
     * Returns the number of possible assignments of the actual cells
     * */
    public int nrOfPossibleAssignments(){
        int nr = 0;
        for(int i=1; i<=puzzleSize; ++i){
            if(possibleAssignments[i] == 1){
                nr ++;
            }
        }
        return  nr;
    }

    /**
     * Creates a new byte array having 0 on all of its positions, and -1 for the value of the cell
     * */
    public void resetPossibleAssignments(){
        if(actualValue != 0){
            possibleAssignments = new byte[puzzleSize + 1];
            possibleAssignments[actualValue] = -1;
        }
    }

    /**
     * Sets the first possible value
     * */
    public void setFirstPossibleValue(){
        int value = -1;
        for(int i=1; i<=puzzleSize; ++i){
            if(possibleAssignments[i] == 1){
                possibleAssignments[i] =0;
                value = i;
                break;
            }
        }
        actualValue = value;
    }

    /**
     * Returns the array of the possible assignments
     * */
    public byte[] getPossibleAssignments() {
        return possibleAssignments;
    }

    /**
     * Returns true if the two cells have the same possible assignments
     */
    public boolean areTwins(Cell cell){
        for(int i=1; i< puzzleSize; ++i){
            if(possibleAssignments[i] != cell.possibleAssignments[i]) return false;
        }

        return true;
    }
}
