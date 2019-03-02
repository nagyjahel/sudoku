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
                Cell cell = new Cell(size);
                cells[i][j] = cell;
            }
        }
    }

    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    public void addCellToList(int row, int column, int value) {
        getCell(row, column).actualValue = value;
        getCell(row, column).resetPossibleAssignments();

        if (value != 0) {
            getCell(row, column).isStatic = true;
            propagateConstraint(row, column, value, true);
        }

    }

    public void propagateConstraint(int row, int column, int value, boolean isPropagation) {
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

        System.out.println("_______________________________________________________________________________________");
        System.out.println();


    }


}
