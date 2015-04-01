# Introduction #
All bots that are to be included in the simulation must implement the IBot interface provided by spate.

# Details #

Here are the descriptions of the methods:

**public int get\_seat();**
Return the seat number that this bot has been assigned to.

**public void new\_hand();**
This method is called when a new hand is beginning.

**public String get\_name();**
This method returns the name of the bot.

**public void game\_start();**
This method is called by the simulation engine at the start of the simulation.

**public void hole\_cards(String c1, String c2);**
This method is called by the simulation engine during the preflop stage when the hole cards are dealt. The cards are represented as "ranksuit" where rank is one of {2,3,4,5,6,7,8,9,T,J,Q,K,A} and suit is one of {c,d,s,h}. For example "2c" would represent the card the two of clubs and "Kd" would be the king of diamonds.

**public String[.md](.md) get\_cards();**
This method returns the hole cards of the bot.


**public Action get\_action();**
This method returns the action for the bot.

**public void player\_action(Action ac);**
This method is called by the simulation engine to inform the bot of the action of another bot.