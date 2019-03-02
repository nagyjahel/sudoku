
public class Solver {

    public static Sudoku solve(Sudoku sudoku) {

        if (sudoku.isComplete()) { return sudoku; }
        Sudoku copy = new Sudoku(sudoku);
        Cell cell = sudoku.getMostConstraintedCell();
        if(cell == null){
            return null;
        }
        if (cell.nrOfPossibleAssignments() == 0 && cell.actualValue != 0) { return solve(sudoku);}

        for (int i = 0; i < sudoku.getSize(); ++i) {
            cell.setFirstPossibleValue();

            if (cell.actualValue == -1) { break; }
            sudoku.propagateConstraint(cell);
            sudoku.print();
            Sudoku result = solve(sudoku);
            if (result != null) {return result; }
            else{
                sudoku = new Sudoku(copy);

            }
        }

        return null;
    }

}
