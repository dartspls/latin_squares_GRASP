Run SolveLatinSquares
arguments: csv file, number of seconds to run for/number of iterations, alpha value (0..1), t/i t: treat args1 as time value in seconds, i: treat args1 as number of iterations

Example usage:
java SolveLatinSquares partial_squares/hard.csv 30 0.1 t

Latin squares generated at: https://cs.uwaterloo.ca/~dmasson/tools/latin_square/
partial squares created by me randomly deleting cells in libre office.

20/09/2022:
Added multithreading, runs on the number of threads returned by "Runtime.getRuntime().availableProcessors()".
On a i5 8400 results in approx 4x improvement over single threaded execution.
Currently all threads run to completion (either reaching 0-score or hitting the time/iteration limit).
