
public class Solver {

    /**
     * Solves the sudoku puzzle
     */
    public static int nrOfAssignments = 0;

    public static Sudoku solve(Sudoku sudoku) {

        // Verify if the sudoku is complete - if every cell has a value
        if (sudoku.isComplete()) {
            System.out.println("NR OF ASSIGNMENTS: " + nrOfAssignments);
            return sudoku;

        }

        // Create a backup copy of the actual sudoku
        Sudoku copy = new Sudoku(sudoku);

        // Find the cell which has the fewest possible values - which is the most constrainted cell
        Cell cell = sudoku.MVRVariable();
        Cell cellCopy = copy.MVRVariable();
        int row = cellCopy.row, column = cellCopy.column;

        if(cell.nrOfPossibleAssignments() == 0){
            return null;
        }

        // Go trough the possible values
        for (int i = 0; i < sudoku.getSize(); ++i) {

            // Set the first possible value of the selected cell
            cell.setFirstPossibleValue();
            cellCopy.setFirstPossibleValue();
            nrOfAssignments ++;

            if(cell.actualValue == -1) return null;

            // Propagate the constraints based on the actual value of the cell
            sudoku.propagateConstraint(cell);

            // Recursive call
            Sudoku result = solve(sudoku);

            // If the previous version of the sudoku was successful, return it
            if (result != null) {return result; }
            else{
                sudoku = new Sudoku(copy);
                cell = sudoku.getCell(row, column);
            }
        }

        return null;
    }

}
