#  Proof of Heuristic Admissibility and Consistency

##  **Heuristic Definition**
The heuristic function estimates the total cost to move all balls from their current positions to their goal positions. It is defined as:

\[
h(n) = \sum_{b \in B} \min_{g \in G_b} \Big(c_b \cdot D(b, g)\Big)
\]

Where:
- **B**: Set of all balls on the board.
- **G_b**: Set of goal positions for ball **b**.
- **D(b, g)**: Circular Manhattan distance between the current position of ball **b** and goal position **g**.
- **c_b**: Cost of moving ball **b**.

---

##  **1. Admissibility Proof**

### **Definition:**
A heuristic function \( h(n) \) is **admissible** if, for every state \( n \):
\[
h(n) \leq d(n, \text{goal})\]
Where:
- **h(n)**: Heuristic estimate of the cost from state **n** to the goal.
- **d(n, goal)**: Actual cost of the shortest path to the goal.

### **Proof:**
1. The heuristic calculates the minimum distance \( D(b, g) \) using the **Circular Manhattan Distance**, which satisfies the triangle inequality.
2. For every ball **b**:
   \[
   D(b, g) \leq d(b, g)
   \]
3. After multiplying by the movement cost:
   \[
   c_b \cdot D(b, g) \leq c_b \cdot d(b, g)
   \]
4. Summing over all balls:
   \[
   \sum_{b \in B} \min_{g \in G_b} \Big(c_b \cdot D(b, g)\Big) \leq \sum_{b \in B} \Big(c_b \cdot d(b, g)\Big)
   \]
5. Therefore:
   \[
h(n) \leq d(n, \text{goal})\]

**Conclusion:** The heuristic never overestimates the true cost, thus it is **admissible**.

---

## **2. Consistency Proof**

### **Definition:**
A heuristic function \( h(n) \) is **consistent** if, for every state \( n \) and its neighbor \( n' \):
\[
h(n) \leq c(n, n') + h(n')\]
Where:
- **c(n, n')**: Cost of moving from **n** to **n'**.
- **h(n')**: Heuristic estimate from the neighbor state **n'**.

### **Proof:**
1. The heuristic uses **Circular Manhattan Distance**, which satisfies the triangle inequality:
   \[
   D(b, g) \leq 1 + D'(b, g)
   \]
2. After multiplying by the movement cost:
   \[
   c_b \cdot D(b, g) \leq c_b + c_b \cdot D'(b, g)
   \]
3. Summing over all balls:
   \[
   \sum_{b \in B} \Big(c_b \cdot D(b, g)\Big) \leq c(n, n') + \sum_{b \in B} \Big(c_b \cdot D'(b, g)\Big)
   \]
4. Therefore:
   \[
h(n) \leq c(n, n') + h(n')\]

**Conclusion:** The heuristic satisfies the consistency condition, thus it is **consistent**.

---

## **Final Conclusion**
- The heuristic is **Admissible**: It never overestimates the true cost.
- The heuristic is **Consistent**: It satisfies the triangle inequality for neighboring states.

This makes the heuristic suitable for optimal pathfinding algorithms such as **A***.

 **Ready for Implementation in Search Algorithms!**
