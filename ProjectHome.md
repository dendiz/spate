**Update** this project has moved to http://labs.dendiz.com/index.php?n=Projects.SPATE

# Description #
Spate is a test bed for cash limit texas hold'em poker bots where they can play against each other and game analysis data may be collected. Spate is an attempt to create an open source and much much faster version of the poker academy application (http://www.poker-academy.com). Code your bots implementing the IBot interface that is provided by spate and include them in the configuration file and you are set to go. 2 example bots (CallBot and Meerkat Bot) are included for reference in the code base as a starting point. I wanted spate to be able to perform very fast and a lot of optimizations in the code are made to make it run fast. Compared to opentestbed which simulates 100K games in 10871 msecs with no hand history, spate simulates 100K games in 1917 msecs with no game trace which makes spate at least 5 times faster.

# Architecture #
This is an overview of the architecture

![http://dendiz.com/static/spate-overall-view.jpg](http://dendiz.com/static/spate-overall-view.jpg)

Agents are plugged into the simulation engine which governs the game and produces a trace log of the game that contains every aspect of the game for later offline analysis.

# Getting started #

## Running the application ##
**Quick start**
To run the application either import the code into eclipse, or type "ant run".

**Details**
The Botpit simulation engine requires the spate-eval hand evaluation library to function
correctly. This is a sub-project that can be found the spate-eval directory. More info about spate-eval in the wiki.

**Installation in eclipse**
After you import the project into eclipse, hit the arrow on the run button and select edit run configurations. From the dialog, create a new java application run configuration and select Botpit as the main class and click apply. Now you should be able to run the code from within eclipse. The first time you run it, spade-eval will create a hand look up file, which will take approx. 30 seconds to generate. This is a one time thing, on the consequent runs, it will load the table from the file it generates.


## Writing bots ##
  * Write the bot code that implements the IBot interface.
  * Either add this code under the bots package, or package it into a jar and place it in the classpath
  * Run the application.
  * Check out the example bots.

See the wiki pages for details of the IBot interface and the API it provides.

# Screen shot #
here's what the simulation engine looks like when you run it with ant

![http://dendiz.com/static/spate-botpit-ss.png](http://dendiz.com/static/spate-botpit-ss.png)

Spate has only got a console interface at the moment, but a graphical interface is in the making, one that would not compromise the performance.
