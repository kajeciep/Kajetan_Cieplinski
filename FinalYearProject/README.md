###Final Year Project

This code was used for testing in my final year project of my computer science MSci at Birmingham. The project explored how self-adaptation of
selection rates in Evolutionary Algorithms, and measures its effects on optimisation. This is done through a genetic algorithm, where the population
is able to move to other sub-populations with different selection rates, by mutating its island gene.


###Instructions for use:

##Requirements

#Language Requirements:
Python 3.7

#Python Library Requirements:
PySAT @ https://pysathq.github.io/installation.html
NumPy @ https://numpy.org/
Matplotlib @ matplotmib.org

##Running the Code:

Open the terminal in the same directory as sasiga.py (cd project).

To run with same parameters as used in the Results section on the LO problem:

$ python sasiga.py

sasiga.py uses ArgumentParser, in order for custom parameters to be used. If 
these parameters are left unspecified, default values will be used.

'-op':   Specifies optimisation problem to be used. Possible values are 'LO'
	 (leading ones), 'PLO' (peaked leading ones), 'PLOZ' (peaked leading
	 ones, where population is initialised as 0-bit bitstrings), '2M' 
	 (2Max), or 'MS' (MaxSat). Default is 'LO'.

'-gt':	 Specifies whether mixed global tournament selection is to be used.
	 Possible values are 0 (false) or 1 (true). Default is 'False'.

'-mr':	 Specifies mutation rate to be used. Possible values are any float
	 between 0 and 1. Default is '1 / l' (where l is the length of the
	 bitstrings).

'-sru':  Specifies upper limit of selection rate. Possible values are any
	 float between 0 and 1. Default is '0.7'. MUST BE GREATER THAN 
	 VALUE OF '-srl'.

'-srl':  Specifies lower limit of selection rate. Possible values are any
	 float between 0 and 1. Default is '0.4'. MUST BE LOWER THAN 
	 VALUE OF '-sru'.

'-pop':  Specifies population size. Possible values are any positive integer.
	 Default is '250'.

'-gs':	 Specifies generation split (used for choosing at which generations
	 we graph a boxplot, e.g. with a population size of 200, and a 
	 generation split of 10, we will get 20 boxplots). Possible values 
	 are any positive integer. Default is '10'. MUST BE AN INTEGER, 
	 THAT THE NUMBER OF GENERATIONS CAN BE DIVIDED BY.

'-e':	 Specifies number of experiments to be ran. Possible values are
	 any positivie integer. Default is '30'.

'-g': 	 Specifies number of generations to be ran. Possible values are
	 any positivie integer. Default is '250'. MUST BE DIVISIBLE BY 
	 GENERATION SPLIT.

'-l': 	 Specifies number of bits in a bitstring (excluding island_no gene) 
	 to be ran. Possible values are any positivie integer. Default is '20'.
	 When running MaxSat, this value is ignored, and number of bits in a 
	 bitstring equals the number of variables in the MaxSat problem.

'-i':	 Specifies number of islands. Possible values are any positive 
	 integer.



Example of setting own parameters:

$ python sasiga.py -op PLOZ -gt 1 -mr 0.01 -sru 0.8 -srl 0.3 -pop 200 -gs 20 -e 10 -g 200 -l 30 -i 10


Enjoy!