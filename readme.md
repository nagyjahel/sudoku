This project resolves a single sudoku, which is set up from an input file.
The input file contains different kind of sudokus (empty, with constraints, 4*4, 9*9, 16*16, 25*25) and the first be always solved.

Different kind of optimizations are implemented, and the way they are getting out the best of the solving algorithm are different:
- The 25 * 25 sudoku (with predefined constraints) can be resolved only with the forward checking (lines 97, 98-101 must be commented out).
- The 16 * 16 and 9*9 sudokus (with predefined constraints) are really optimised using all the 3 constraint propagation functions (forward checking, single possible assignments, and nakedTwins) (lines 97-101 should not be commented out).
- The 16 * 16, 9*9 and 4*4 empty sudokus can be filled using the forward checking, and the single responsability constraint propagation (the second part of the line 99 must be commented out)

The current version of the project is set up to resolve the 25*25 sudoku.