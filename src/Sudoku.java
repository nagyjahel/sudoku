import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Sudoku {

    private int size;
    private Cell[][] cells;

    /**
     * Default constructor
     */
    public Sudoku() {
    }


    /**
     * Copy constructor
     */
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
     */
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
     */
    private void initCells() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                Cell cell = new Cell(size, i, j);
                cells[i][j] = cell;
            }
        }
    }

    /**
     * Returns the most constrainted cell - the one which has the minimum remained values
     */
    public Cell MVRVariable() {
        int row = -1, column = -1, minimumAssignmentNumber = size * size;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (getCell(i, j).nrOfPossibleAssignments() < minimumAssignmentNumber && getCell(i, j).actualValue == 0 && !getCell(i, j).isStatic) {
                    row = i;
                    column = j;
                    minimumAssignmentNumber = getCell(i, j).nrOfPossibleAssignments();
                }
            }
        }

        if (row == -1 && column == -1) {
            return null;
        }
        Cell cell = getCell(row, column);
        return cell;
    }

    /**
     * Propagates the constraints in the entire puzzle based on the selected cell
     */
    public void propagateConstraint(Cell originalCell) {
        while (true) {
            forwardCheking(originalCell);
            if (singlePossibleAssignment(originalCell) == false && nakedTwins(originalCell) == false) {
                break;
            }
        }

    }
    /**
     * If a single value is possible for a cell, it will be set - and its constraints will be propagated as well
     */
    public boolean singlePossibleAssignment(Cell originalCell) {
        boolean changed = false;
        for (int i = 0; i < size; ++i) {
            if (i != originalCell.row) {
                Cell cellOfTheSameColumn = getCell(i, originalCell.column);
                if (!cellOfTheSameColumn.isStatic) {
                    if (cellOfTheSameColumn.nrOfPossibleAssignments() == 1 && cellOfTheSameColumn.actualValue == 0) {
                        cellOfTheSameColumn.setFirstPossibleValue();
                        forwardCheking(cellOfTheSameColumn);
                        changed = true;
                    }
                }
            }

            if (i != originalCell.column) {
                Cell cellOfTheSameRow = getCell(originalCell.row, i);
                if (!cellOfTheSameRow.isStatic) {
                    if (cellOfTheSameRow.nrOfPossibleAssignments() == 1 && cellOfTheSameRow.actualValue == 0) {
                        cellOfTheSameRow.setFirstPossibleValue();
                        forwardCheking(cellOfTheSameRow);
                        changed = true;

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
                        if (cellOfTheSameSquare.nrOfPossibleAssignments() == 1 && cellOfTheSameSquare.actualValue == 0) {
                            cellOfTheSameSquare.setFirstPossibleValue();
                            forwardCheking(cellOfTheSameSquare);
                            changed = true;

                        }

                    }
                }

            }
        }

        return changed;
    }

    /**
     * Deletes the value of the cell from the possible assignments of the cells from its row, column, and square/
     */
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
     * If in a row or in a column 2 elements have only the same 2 possible assignments, then these two assignments will be deleted from the rest of the elements from the column/row
     */
    public boolean nakedTwins(Cell originalCell) {
        ArrayList<Cell> nakedTwinsFromRow = getNakedTwins(originalCell, true);
        ArrayList<Cell> nakedTwinsFromColumn = getNakedTwins(originalCell,false);

        boolean changed = false;
        if(nakedTwinsFromColumn.size() != 0 || nakedTwinsFromColumn.size()!= 0) {
            for (int i = 0; i < size; ++i) {
                if (i != originalCell.column && nakedTwinsFromRow.size()!=0) {
                    Cell cellOfTheSameRow = cells[originalCell.row][i];
                    if (!cellOfTheSameRow.isStatic && cellOfTheSameRow.nrOfPossibleAssignments() > 0) {
                        if (!isPartOfTheList(nakedTwinsFromRow, cellOfTheSameRow)) {
                            for (Cell nakedTwinFromRow : nakedTwinsFromRow) {
                                for (int j = 1; j <= size; j++) {
                                    if (nakedTwinFromRow.getPossibleAssignments()[j] == 1 && cellOfTheSameRow.getPossibleAssignments()[j] != -1) {
                                        cellOfTheSameRow.getPossibleAssignments()[j] = 0;
                                        changed = true;

                                    }
                                }
                            }

                        }
                    }
                }

                if (i != originalCell.row && nakedTwinsFromColumn.size()!=0) {
                    Cell cellOfTheSameColumn = cells[i][originalCell.column];
                    if (!cellOfTheSameColumn.isStatic && cellOfTheSameColumn.nrOfPossibleAssignments() > 0) {
                        if (!isPartOfTheList(nakedTwinsFromColumn, cellOfTheSameColumn)) {
                            for (Cell nakedTwinFromColumn : nakedTwinsFromColumn) {
                                for (int j = 1; j <= size; j++) {
                                    if (nakedTwinFromColumn.getPossibleAssignments()[j] == 1 && cellOfTheSameColumn.getPossibleAssignments()[j] != -1) {
                                        cellOfTheSameColumn.getPossibleAssignments()[j] = 0;
                                        changed = true;
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Searches for the elements which have only two possible assingmegts, then returns the naked twins from them
     * */
    private ArrayList<Cell> getNakedTwins(Cell ofCell, boolean fromRow){
        ArrayList<Cell> cellsWithTwoPossibleAssigments = new ArrayList<>();

        for(int i=0; i<size; ++i){
            Cell cell = null;
            if(fromRow){
                cell = cells[ofCell.row][i];
            }
            else {
                cell = cells[i][ofCell.column];
            }

            if(cell.nrOfPossibleAssignments() == 2){
                cellsWithTwoPossibleAssigments.add(cell);
            }
        }

        return searchNakedTwins(cellsWithTwoPossibleAssigments);
    }

    /**
     * Gets the naked twins from a list of cells with two possible assignments
     * */

    private ArrayList<Cell> searchNakedTwins(ArrayList<Cell> list){
        ArrayList<Cell> nakedTwins = new ArrayList<>();
        int[] count = new int[list.size()] ;
        Arrays.fill(count,0);

        for(int i=0; i<list.size(); ++i){
            for(int j=0; j<list.size(); ++j){
                if(i != j && list.get(i).areTwins(list.get(j))){
                    count[i] ++;
                    count[j] ++;
                }
            }
        }
        for(int i=0; i<list.size(); ++i){
            if(count[i] == 2){
                nakedTwins.add(list.get(i));
            }
        }
        return nakedTwins;
    }

    /**
     * It realizes as the arrayList.indexOf(element) for the cell type
     * */
    private boolean isPartOfTheList(ArrayList<Cell> list, Cell searchedCell){
        for(Cell cell:list){
            if(cell == searchedCell){
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if every cell has a value
     */
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
     */
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
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns a specific cell of the sudoku
     */
    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    /**
     * Adds a cell to the sudoku
     */
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
