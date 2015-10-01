Documentation for Warmup Assignment 1
=====================================

+-------+
| BUILD |
+-------+

Comments:
(A) Doubly-linked Circular List :
 1) Type "make" to generate the object files, to link them and generate the executables.
 2) Run the testing scripts by pasting them into the terminal.
 3) Type "make clean" to remove all generated files.
 
(B) Sort :
 1) Type "make warmup1" to generate the object files, to link them and generate the executables.
 2) Run the testing scripts by pasting them into the terminal.
 3) Type "make clean" to remove all generated files.

+------+
| SKIP |
+------+

(1) Malformed input: cat $srcdir/f104 | ./warmup1 sort
Error required to print: "Amount field is empty."
Error being printed from my code: "Invalid Input.The amount does not contain the decimal point."
If both the scenarios are considered same, then please run the test else skip it.

(2) Bad commandline: ./warmup1 sort /etc/inet/secret/xyzz
Error required to print: "input file /etc/inet/secret/xyzz cannot be opened - access denies"
Error being printed from my code: "Input File /usr/bin/xyzz does not exist."
If both the scenarios are considered same, then please run the test else skip it.

+---------+
| GRADING |
+---------+

(A) Doubly-linked Circular List : 40 out of 40 pts

(B.1) Sort (file) : 30 out of 30 pts
(B.2) Sort (stdin) : 30 out of 30 pts

Missing required section(s) in README file : (Comments?)NO
Cannot compile : (Comments?)It can compile
Compiler warnings : (Comments?)No compiler warnings
"make clean" : (Comments?)Working
Segmentation faults : (Comments?)NO
Separate compilation : (Comments?)NO,compiles by typing make or make warmup1
Malformed input : (Comments?)1 test(mentioned in skip section) does not produce expected result.
Too slow : (Comments?)NO
Bad commandline : (Comments?)1 test(mentioned in skip section) does not produce expected result.
Bad behavior for random input : (Comments?)NO
Did not use My402List and My402ListElem to implement "sort" in (B) : (Comments?)YES

+------+
| BUGS |
+------+

Comments: No bugs found with the grading guidelines. 


+-------+
| OTHER |
+-------+

Comments on design decisions: None
Comments on deviation from spec: No deviations
