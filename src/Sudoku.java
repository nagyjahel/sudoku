import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Sudoku {

    private int size;
    private Cell[][] cells;

    public Sudoku() {
    }

    public Sudoku(Sudoku original) {

        size = original.size;
        cells = new Cell[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                cells[i][j] = new Cell(original.getCell(i, j));
            }
        }

    }

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

    public int getSize() {
        return size;
    }

    private void initCells() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                Cell cell = new Cell(size,i,j);
                cells[i][j] = cell;
            }
        }
    }

    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    public Cell getMostConstraintedCell(){
        int row = -1, column = -1, minimumAssignmentNumber = size * size;
        for(int i=0; i<size; ++i){
            for(int j=0; j<size; ++j){
                if (getCell(i,j).nrOfPossibleAssignments() > 0  && getCell(i,j).nrOfPossibleAssignments() < minimumAssignmentNumber){
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

    public void singlePossibleAssignment(Cell originalCell){
        for (int i = 0; i < size; ++i) {
            if (i != originalCell.row) {
                Cell cellOfTheSameColumn = getCell(i, originalCell.column);
                if (!cellOfTheSameColumn.isStatic) {
                        if(cellOfTheSameColumn.nrOfPossibleAssignments() == 1){
                            cellOfTheSameColumn.setFirstPossibleValue();
                            forwardCheking(cellOfTheSameColumn);
                        }
                }
            }

            if (i != originalCell.column) {
                Cell cellOfTheSameRow = getCell(originalCell.row, i);
                if (!cellOfTheSameRow.isStatic) {
                        if(cellOfTheSameRow.nrOfPossibleAssignments() == 1){
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
                            if(cellOfTheSameSquare.nrOfPossibleAssignments() == 1){
                                cellOfTheSameSquare.setFirstPossibleValue();
                                forwardCheking(cellOfTheSameSquare);
                            }

                    }
                }

            }
        }
        //this.print();
    }

    public void propagateConstraint(Cell originalCell) {
       // System.out.println("Constraint propagation: (" + originalCell.row + ", " + originalCell.column + ") - " + originalCell.actualValue);
        forwardCheking(originalCell);
        //singlePossibleAssignment(originalCell);
    }

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

    public void propagateConstraintBackup(Cell cell, boolean isPropagation) {
        int row = cell.row, column = cell.column, value = cell.actualValue;
        for (int i = 0; i < size; ++i) {
            if (i != row) {
                if (isPropagation) {
                    if (getCell(row, column).isStatic) {
                        getCell(i, column).getPossibleAssignments()[value] = -1;
                    } else {
                        if (getCell(i, column).getPossibleAssignments()[value] != -1) {
                            getCell(i, column).getPossibleAssignments()[value] = 0;
                        }
                    }
                } else {
                    if (!getCell(i, column).isStatic) {
                        if (getCell(i, column).getPossibleAssignments()[value] != -1) {
                            getCell(i, column).getPossibleAssignments()[value] = 1;
                        }
                    }

                }
            }

            if (i != column) {
                if (isPropagation) {
                    if (getCell(row, column).isStatic) {
                        getCell(row, i).getPossibleAssignments()[value] = -1;
                    } else {
                        if (getCell(row, i).getPossibleAssignments()[value] != -1) {
                            getCell(row, i).getPossibleAssignments()[value] = 0;
                        }
                    }
                } else {
                    if (!getCell(row, i).isStatic) {
                        if (getCell(row, i).getPossibleAssignments()[value] != -1) {
                            getCell(row, i).getPossibleAssignments()[value] = 1;
                        }
                    }

                }
            }
        }

        int unitSize = (int) Math.sqrt(size);
        for (int i = row / unitSize * unitSize; i <= row / unitSize * unitSize + unitSize - 1; ++i) {
            for (int j = column / unitSize * unitSize; j <= column / unitSize * unitSize + unitSize - 1; ++j) {
                if (i != row && j != column) {
                    if (isPropagation) {
                        if (getCell(row, column).isStatic) {
                            getCell(i, j).getPossibleAssignments()[value] = -1;
                        } else {
                            if (getCell(i, j).getPossibleAssignments()[value] != -1) {
                                getCell(i, j).getPossibleAssignments()[value] = 0;
                            }
                        }

                    } else {
                        if (!getCell(i, j).isStatic) {
                            if (getCell(i, j).getPossibleAssignments()[value] != -1) {
                                getCell(i, j).getPossibleAssignments()[value] = 1;
                            }
                        }

                    }
                }

            }
        }

    }
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

        for(int i=0; i<9;++i){
            System.out.print("( " + 0 + ", " + i + "): Possible assignments: ");
            for(int j=1; j<=9; ++j){
                System.out.print(getCell(0,i).getPossibleAssignments()[j] + " ");
            }
            System.out.println();

        }


        System.out.println("_______________________________________________________________________________________");
        System.out.println();


    }




}
