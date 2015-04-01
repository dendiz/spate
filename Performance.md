# Introduction #
Performance metrics of the simulation engine through out its evaluation. To evaluate the Botpit simulation engine in a fair way only the bots of types CallBot which only return a call or check action every time without doing any calculations (0.000025ms) were included in the game.
The number of bots included in the game effect performance (due to more hand rank lookups,etc.) so the number of players in the simulation are included with the results.


# Details #
In all simulations the application logging is disabled. Trace logging is enabled when indicated. Even though Spate uses an array buffer to hold 30K entries and dumps them in batch using a buffered output writer, the impact is still high on performance.

|Date|01.05.2010|
|:---|:---------|
|CPU|4xAmd xp 2600|
|RAM|8gb|
|Jvm|Java 1.6.0.15|

```
Botpit v2j
Sim rounds: 100000 , num players: 10, trace: off, log:off
..........
Stacks:
bots.CallBot : 6632.923
bots.CallBot : 4612.9272
bots.CallBot : 8307.968
bots.CallBot : 5647.917
bots.CallBot : 3569.6885
bots.CallBot : 3299.679
bots.CallBot : 4046.2507
bots.CallBot : 3986.2637
bots.CallBot : 2884.6833
bots.CallBot : 7011.2573
Duration (ms):2951
```

```
Botpit v2j
Sim rounds: 100000 , num players: 10, trace: on, log: off
..........
Stacks:
bots.CallBot : 6957.581
bots.CallBot : 3970.925
bots.CallBot : 4522.655
bots.CallBot : 3167.6392
bots.CallBot : 5867.6177
bots.CallBot : 5127.6587
bots.CallBot : 9380.957
bots.CallBot : 3764.2566
bots.CallBot : 5074.26
bots.CallBot : 2165.9543
Duration (ms):127485
```

Circa 3 seconds for 100K hands of 10 players, and 120 seconds with the same configuration but trace logging enabled.