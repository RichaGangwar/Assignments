Documentation for Warmup Assignment 2
=====================================

+-------+
| BUILD |
+-------+

Comments:
 1) Type "make" to generate the object files, to link them and generate the executables.
 2) Run the testing scripts by pasting them into the terminal.
 3) Type "make clean" to remove all generated files.


+------+
| SKIP |
+------+

None

+---------+
| GRADING |
+---------+

Basic running of the code : 100 out of 100 pts

Missing required section(s) in README file : All sections are present
Cannot compile : It gets compiled
Compiler warnings : No compiler warnings
"make clean" : works
Segmentation faults : None
Separate compilation : works
Using busy-wait : No
Handling of commandline arguments:
    1) -n : working
    2) -lambda : working
    3) -mu : working
    4) -r : working
    5) -B : working
    6) -P : working
Trace output :
    1) regular packets: Present and trace statements printed.
    2) dropped packets: Present and trace statements printed.
    3) removed packets: Present and trace statements printed.
    4) token arrival (dropped or not dropped): (dropped) Present and trace statements printed.
Statistics output :
    1) inter-arrival time : Valid real number with precision upto 6-8 digits.
    2) service time : Valid real number with precision upto 6-8 digits.
    3) number of customers in Q1 : Valid real number with precision upto 6-8 digits.
    4) number of customers in Q2 : Valid real number with precision upto 6-8 digits.
    5) number of customers at a server : Valid real number with precision upto 6-8 digits.
    6) time in system : Valid real number, prints "no packets arrived " when no packets have arrived
    7) standard deviation for time in system : Valid real number,prints "no packets arrived " when no packets have arrived
    8) drop probability : Valid real number
Output bad format : No
Output wrong precision for statistics (should be 6-8 significant digits) : No,precision upto 6-8 digits
Large service time test : works
Large inter-arrival time test : works
Tiny inter-arrival time test : works
Tiny service time test : works
Large total number of customers test : works
Large total number of customers with high arrival rate test : works
Dropped tokens test : works
Cannot handle <Cntrl+C> at all (ignored or no statistics) : handled
Can handle <Cntrl+C> but statistics way off : handled ans statistics printed
Not using condition variables and do some kind of busy-wait : using condition variables
Synchronization check : mutex used
Deadlocks : no

+------+
| BUGS |
+------+

No bugs found with the grading guidelines. 

+-------+
| OTHER |
+-------+

Comments on design decisions: Command line values are taken, if no values are provided then default is used. Threads are created and emulation starts either in deterministic mode or trace driven mode as specified by user. Once all the arriving packets get serviced, the emulation ends. Condition variables are used to wake up the server. If Ctrl+C is pressed all the packets from both the queues are removed and the packet currently servicing in the server complete its service.
Comments on deviation from spec: none
