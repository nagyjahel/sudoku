
public class Solver {

    public static Sudoku solve(Sudoku sudoku, int index) {

        if (sudoku.isComplete()) { return sudoku; }
        int row = index / sudoku.getSize();
        int column = index % sudoku.getSize();
        Cell cell = sudoku.getCell(row, column);

        if (cell.nrOfPossibleAssignments() == 0 && cell.actualValue != 0) { return solve(new Sudoku(sudoku), index + 1);}

        for (int i = 0; i < sudoku.getSize(); ++i) {
            cell.setFirstPossibleValue();

            if (cell.actualValue == -1) { break; }
            sudoku.propagateConstraint(row, column, cell.actualValue, true);
            Sudoku result = solve(new Sudoku(sudoku), index + 1);
            if (result != null) {return result; }
            sudoku.propagateConstraint(row,column,cell.actualValue, false);
        }

        return null;
    }

}
