# AI Agents for Non-Deterministic Tic Tac Toe
*Quickly written for a final project for CS4100: Artificial Intelligence at NEU, so lacking documentation and polish, but it does the trick.*

This contains AI agents for non-deterministic variants of Tic Tac Toe who can be matched/trained against each other, and/or played against by you. Non-determinism in this game just means selecting a cell does not guarantee that your marker will be placed in that cell.

New variants can be quickly added by extending NonDeterministicBoard and BaseTicTacToe, but the one in there right now (called randomX) will distribute moves in an X pattern centered at the selected cell. Games are configured with a success rate, which represents the chance of your move going in the cell you selected. The rest of the chance is distributed among the other cells in the pattern on the board. Any move resulting in a marker being placed in an occupied cell will result in that turn being ignored and lost.
```
    A   B   C
  +---+---+---+    For example, if success rate is 70% and the board looks like this,
1 |   |   |   |    and player O wants to move in B2, then it will have a 70% chance of
  +---+---+---+    landing in B2, and a 7.5% chance each of landing in A1, C1, A3, C3.
2 |   |   | X |
  +---+---+---+    If player O wants to move to B3, then it will have a 70% chance of
3 |   |   |   |    landing in B3, and a 15% chance each of landing in A2 or C2. If it
  +---+---+---+    lands in C2, nothing will happen and it will become X's turn.
  O's turn.
  ```

### AI Agents
##### Search Agent
This agent uses depth-limited expectiminimax search in order to decide what moves it will make. Since the branching factor gets pretty high in non-deterministic versions of this game, as well as games theoretically being able to last forever due to ignoring moves on occupied spaces, we need to stop searching if the depth limit is reached before a terminal state happens. The heuristic for evaluating these non-terminal states is done through basically counting the number of markers (X/O pieces) in the 8 win lines (3 columns, 3 rows, 2 diagonals). Each of these lines has a pair associated with it of (#X in line, #O in line). Pairs with both an X and O in it are filtered out before summing the pairs together, and then subtracting based on which player the agent is. For example, if the total is (5,2) and the agent is X, then the value of that state will be 3, and -3 if the agent is O.

##### Q-Learning Agent
Uses Q-Learning to learn how to play more optimally during its training phase, before matching up with an opponent for real. A mapping of state -> qValMatrix is kept for each state it encounters, and these are incrementally updated upon reward/penalty on game end. A history of (state, action) is kept during each game so rewards on game end can be propagated back through to the very first move. The Q-Learning agent can be trained against any agent before it plays the opponent it is matched against for real. However, it needs a bunch of trainingIterations before it starts being effective, as there are tons of states (each unique board layout is the key for the qvalmatrix representing q vals for each action from that given state), so training it against a Random agent or another Q-Learning agent is quickest.

##### Random Agent
Just picks a random move out of the valid actions for the current state. Useful for benchmarking and evaluating performance.

### Running
This got messy but whatever. Options listed below:
- **-g, --gamemode** *[standard, randomX]* --- which game mode will be played --- *default: standard*
- **-p1, --p1Strategy** *[human, search, qlearning, random]* --- strategy for player one --- *default: random*
- **-p2, --p2Strategy** *[human, search, qlearning, random]* --- strategy for player two --- *default: qlearning*
- **-c, --moveSuccessChance** *between 0.0, 1.0* --- success rate for moving where you want to move in non-deterministic variants --- *default: 0.7*
- **-i, --iterations** *number* --- how many games should be played between the two players --- *default: 100*
- **-t, --trainingIterations** *number* --- how many games the Q-Learning Agent should train for. Needs quite a few to be effective (10kish is solid) --- *default: 10000*
- **--depth** *number* --- how deep search should go. More than 3-4 on non-deterministic search will take a while due to high branching factor --- *default: 2*
- **-l, --learningRate** *honestly no idea what the valid range is here but i was using 0.1 to 1* --- learning rate for the Q-Learning Agent --- *default: 0.3*
- **-e, --exploreRate** *between 0.0, 1.0* --- explore rate for the Q-Learning Agent during its training phase --- *default: 0.3*
- **-d, --discount** *between 0.0, 1.0* --- discount for future rewards for both Search/Q-Learning --- *default: 0.95*
- **--trainingAgentStrategy** *[human, search, qlearn, random]* --- strategy for the training agent that Q-Learning Agents will play against. Search will take a while, not recommended for non-deterministic versions. --- *default: random*

### Stats
Ran tests matching them against each other and put results in some of the .txt files in root. Also has interesting stats on the performance of the Q-Learning agent as the number of trainingIterations increases.
