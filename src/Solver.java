
public class Solver {

    /**
     * Solves the sudoku puzzle
     */

    public static Sudoku solve(Sudoku sudoku) {

        // Verify if the sudoku is complete - if every cell has a value
        if (sudoku.isComplete()) { return sudoku; }

        // Create a backup copy of the actual sudoku
        Sudoku copy = new Sudoku(sudoku);

        // Find the cell which has the fewest possible values - which is the most constrainted cell
        Cell cell = sudoku.MVRVariable();

        // If no cell of this kind exist, go back
        if(cell == null){
            return  null;
        }

        if(cell.nrOfPossibleAssignments() == 0){
            return null;
        }

        // Go trough the possible values
        for (int i = 0; i < sudoku.getSize(); ++i) {
            // Set the first possible value of the selected cell
            cell.setFirstPossibleValue();
            if(cell.actualValue == -1) return null;
            // Propagate the constraints based on the actual value of the cell
            sudoku.propagateConstraint(cell);
            //copy.print();
            // Recursive call
            Sudoku result = solve(sudoku);
            // If the previous version of the sudoku was successful, return it
            if (result != null) {return result; }
            else{
                int value = cell.actualValue;
                sudoku = new Sudoku(copy);
                cell = sudoku.MVRVariable();
                cell.getPossibleAssignments()[value] = 0;

            }
            // Otherwise restore it based on the backup copy
            //else{ return copy;}
        }

        return null;
    }

}
