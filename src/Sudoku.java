import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Sudoku {

    private int size;
    private Cell[][] cells;

    /**
     * Default constructor
     * */
    public Sudoku() {
    }


    /**
     * Copy constructor
     * */
    public Sudoku(Sudoku original) {

        size = original.size;
        cells = new Cell[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                cells[i][j] = new Cell(original.getCell(i, j));
            }
        }

    }

    /**
     * Creates a Sudoku based on the values of a file
     * */
    public void createFromFile(String fileName) {

        try {
            Scanner scanner = new Scanner(new File(fileName));
            if (scanner.hasNextInt()) {
                size = scanner.nextInt();
            }
            cells = new Cell[size][size];
            initCells();
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (scanner.hasNextInt()) {
                        addCellToList(i, j, scanner.nextInt());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    /**
     * Creates the cell entities of the sudoku
     * */
    private void initCells() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                Cell cell = new Cell(size,i,j);
                cells[i][j] = cell;
            }
        }
    }

    /**
     * Returns the most constrainted cell - the one which has the fewest possible assignments
     * */
    public Cell MVRVariable(){
        int row = -1, column = -1, minimumAssignmentNumber = size * size;
        for(int i=0; i<size; ++i){
            for(int j=0; j<size; ++j){
                if (getCell(i,j).nrOfPossibleAssignments() < minimumAssignmentNumber && getCell(i,j).actualValue == 0 && !getCell(i,j).isStatic){
                    row = i;
                    column = j;
                    minimumAssignmentNumber = getCell(i,j).nrOfPossibleAssignments();
                }
            }
        }

        if(row == -1 && column == -1){
            return null;
        }
        Cell cell = getCell(row,column);
        return cell;
    }

    /**
     * Propagates the constraints in the entire puzzle based on the selected cell
     * */
    public void propagateConstraint(Cell originalCell) {
        forwardCheking(originalCell);
        singlePossibleAssignment(originalCell);
    }

    /**
     * If a single value is possible for a cell, it will be set - and its constraints will be propagated as well
     * */
    public void singlePossibleAssignment(Cell originalCell){
        for (int i = 0; i < size; ++i) {
            if (i != originalCell.row) {
                Cell cellOfTheSameColumn = getCell(i, originalCell.column);
                if (!cellOfTheSameColumn.isStatic) {
                        if(cellOfTheSameColumn.nrOfPossibleAssignments() == 1 && cellOfTheSameColumn.actualValue== 0){
                            cellOfTheSameColumn.setFirstPossibleValue();
                            forwardCheking(cellOfTheSameColumn);
                        }
                }
            }

            if (i != originalCell.column) {
                Cell cellOfTheSameRow = getCell(originalCell.row, i);
                if (!cellOfTheSameRow.isStatic) {
                        if(cellOfTheSameRow.nrOfPossibleAssignments() == 1 && cellOfTheSameRow.actualValue==0){
                            cellOfTheSameRow.setFirstPossibleValue();
                            forwardCheking(cellOfTheSameRow);
                        }
                }
            }
        }

        int unitSize = (int) Math.sqrt(size);
        for (int i = originalCell.row / unitSize * unitSize; i <= originalCell.row / unitSize * unitSize + unitSize - 1; ++i) {
            for (int j = originalCell.column / unitSize * unitSize; j <= originalCell.column / unitSize * unitSize + unitSize - 1; ++j) {
                if (i != originalCell.row && j != originalCell.column) {
                    Cell cellOfTheSameSquare = getCell(i, j);
                    if (!cellOfTheSameSquare.isStatic) {
                            if(cellOfTheSameSquare.nrOfPossibleAssignments() == 1 && cellOfTheSameSquare.actualValue == 0){
                                cellOfTheSameSquare.setFirstPossibleValue();
                                forwardCheking(cellOfTheSameSquare);
                            }

                    }
                }

            }
        }
    }

    /**
     * Deletes the value of the cell from the possible assignments of the cells from its row, column, and square/
     * */
    public void forwardCheking(Cell originalCell) {
        for (int i = 0; i < size; ++i) {
            if (i != originalCell.row) {
                Cell cellOfTheSameColumn = getCell(i, originalCell.column);
                    if (originalCell.isStatic) {
                        cellOfTheSameColumn.getPossibleAssignments()[originalCell.actualValue] = -1;
                    } else {
                        if (cellOfTheSameColumn.getPossibleAssignments()[originalCell.actualValue] != -1) {
                            cellOfTheSameColumn.getPossibleAssignments()[originalCell.actualValue] = 0;
                        }
                    }
            }

            if (i != originalCell.column) {
                Cell cellOfTheSameRow = getCell(originalCell.row, i);
                if (originalCell.isStatic) {
                    cellOfTheSameRow.getPossibleAssignments()[originalCell.actualValue] = -1;
                    } else {
                        if (cellOfTheSameRow.getPossibleAssignments()[originalCell.actualValue] != -1) {
                            cellOfTheSameRow.getPossibleAssignments()[originalCell.actualValue] = 0;
                        }
                    }
            }
        }

        int unitSize = (int) Math.sqrt(size);
        for (int i = originalCell.row / unitSize * unitSize; i <= originalCell.row / unitSize * unitSize + unitSize - 1; ++i) {
            for (int j = originalCell.column / unitSize * unitSize; j <= originalCell.column / unitSize * unitSize + unitSize - 1; ++j) {
                if (i != originalCell.row && j != originalCell.column) {
                    Cell cellOfTheSameSquare = getCell(i, j);
                        if (originalCell.isStatic) {
                            cellOfTheSameSquare.getPossibleAssignments()[originalCell.actualValue] = -1;
                        } else {
                            if (cellOfTheSameSquare.getPossibleAssignments()[originalCell.actualValue] != -1) {
                                cellOfTheSameSquare.getPossibleAssignments()[originalCell.actualValue] = 0;
                            }
                        }
                }

            }
        }

    }

    /**
     * Checks if every cell has a value
     * */
    public boolean isComplete() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (cells[i][j].actualValue == 0) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Prints the sudoku
     * */
    public void print() {
        double space = Math.sqrt(size);
        for (int i = 0; i < size; ++i) {
            if (i % space == 0 && i != 0) {
                for (int j = 0; j < size + space - 1; ++j) {
                    System.out.print("---");
                }
                System.out.println();
            }

            for (int j = 0; j < size; ++j) {
                System.out.print(cells[i][j].actualValue + " ");
                if (cells[i][j].actualValue < 10) {
                    System.out.print(" ");
                }

                if (j % space == space - 1 && j != size - 1) {
                    System.out.print("| ");
                }

            }
            System.out.println();

        }
        System.out.println();
        System.out.println("_______________________________________________________________________________________");
        System.out.println();


    }

    /**
     * Returns the size of the sudoku (e.g. 9, 16, 25)
     * */
    public int getSize() {
        return size;
    }

    /**
     * Returns a specific cell of the sudoku
     * */
    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    /**
     * Adds a cell to the sudoku
     * */
    public void addCellToList(int row, int column, int value) {
        Cell cell = getCell(row, column);
        cell.row = row;
        cell.column = column;
        cell.actualValue = value;
        cell.resetPossibleAssignments();

        if (value != 0) {
            getCell(row, column).isStatic = true;
            propagateConstraint(getCell(row, column));
        }

    }
}
