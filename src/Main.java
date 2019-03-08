public class Main {

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        sudoku.createFromFile("sudoku.txt");
        sudoku.print();
        sudoku = Solver.solve(sudoku);
        if (sudoku != null) sudoku.print();
    }
}
