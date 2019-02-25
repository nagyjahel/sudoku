
public class Solver {

    public static int solve(Sudoku sudoku, int index) {

        if (sudoku.isComplete()) { return 0; }

        if (index == sudoku.getSize() * sudoku.getSize()) { return 0; }

        int row = index / sudoku.getSize();
        int column = index % sudoku.getSize();
        Cell cell = sudoku.getCell(row, column);

        if (cell.nrOfPossibleAssignments() == 0 && cell.actualValue != 0) { return solve(new Sudoku(sudoku), index + 1);}

        for (int i = 0; i < sudoku.getSize(); ++i) {
            cell.setFirstPossibleValue();

            if (cell.actualValue == -1) { break; }

            sudoku.propagateConstraint(row, column, cell.actualValue, true);
            //sudoku.print();

            int result = solve(new Sudoku(sudoku), index + 1);
            if (result == 0) { return 0; }
            sudoku.propagateConstraint(row,column,cell.actualValue, false);
        }

        return 1;
    }

}
