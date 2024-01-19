# proof-of-qualification-sr-assignment-c

### What is this repository for? ###
* This module handles the assignment C. Details are given below:

### Details for Assignment C ###

**Objective:**

Design a program that implements k-means algorithm for an arbitrary set of vectors.

**Design Specification:**

The program takes it’s input from a text file:
- The first line of the file contains the values of M, N, K and d, represented as three integer values and one floating-point value, separated by one or more blanks;
- The next M lines contain uniform values of vectors from the set, represented as N floating-point values, separated by one or more blanks;
  The program outputs the cluster centroids and the classified vectors as:
- K lines with one integer and N floating-point values, representing the cluster index and the centroid vector
- a blank line
- M lines with one integer and N floating-point values, representing the cluster index of a vector and the vector from the initial set.

Example input:

300 4 2 0.001

0.1 0.55 12.2 1.55

2.3 4 1.8 0.33

….

Example output:

0 0.28 0.33 1.14 3.85

1 3.66 9.21 5.39 0.02

[Space]

0 0.1 0.55 12.2 1.55

1 2.3 4 1.8 0.33

….

Note:
Please note that the above examples are for illustration purposes only and should not be used as test data.

**Remarks:**
- The program should terminate on invalid input;
- The program should terminate if the algorithm does not converge for more than pre-defined number of iterations, represented as a constant in code.


### How do I get set up? ###

* Keep a file containing input in resource path as mentioned above

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* apeksha.ambala@gmail.com