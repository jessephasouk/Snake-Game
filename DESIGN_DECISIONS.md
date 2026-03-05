# Snake Game - Coding Decisions

## Data Structure Choices

### 1. **LinkedList for Snake Body**
```java
private final LinkedList<Point> snake;
```

**Why LinkedList over ArrayList?**
- **O(1) addFirst()**: Insert new head at front without shifting elements
- **O(1) removeLast()**: Remove tail without array reallocation
- **Movement operation**: Each game tick requires both operations sequentially
  ```java
  snake.addFirst(newHead);        // O(1)
  snake.removeLast();             // O(1) - when not eating food
  ```
- **vs ArrayList**: Would require O(n) for inserting at index 0 (shift all elements)
- **Scalability**: With snake potentially 400 elements (full board), avoiding O(n) operations is critical

**Trade-off**: Random access (get by index) is O(n) instead of O(1), but we only use it for rendering all elements anyway.

### 2. **Queue for Direction Input**
```java
private final Queue<Character> directionQueue;
```

**Why Queue (FIFO) over a simple variable?**
- **Input Buffering**: Multiple key presses between game ticks are not lost
- **Decoupling**: Separates keyboard event timing from game loop timing
- **Prevents Race Conditions**: User input at tick boundary doesn't conflict with movement calculation
- **Implementation**: 
  ```java
  directionQueue.offer('U');      // Enqueue direction
  if (!directionQueue.isEmpty()) {
      direction = directionQueue.poll();  // Dequeue and apply
  }
  ```
- **Validation**: Check peek() to validate against current direction without consuming
  ```java
  Character lastDirObj = directionQueue.peek();  // Look ahead without dequeuing
  ```

**Why not List?**
- Queue semantics make intent clear: FIFO ordering of inputs
- peek()/poll() are more readable than get(0)/remove(0)

### 3. **Point for Coordinates**
```java
private Point head = new Point(x, y);
```

**Why Point over custom Coordinate class?**
- Built-in `equals()` for collision detection:
  ```java
  if (newHead.equals(food))       // Works correctly
  if (snake.contains(newHead))    // Uses equals() for membership test
  ```
- Simplicity: No need to implement equals/hashCode ourselves
- Immutable semantics for this use case

### 4. **Boolean Flags for State**
```java
private boolean running;  // Is game active?
private boolean started;  // Has player pressed first direction?
private boolean won;      // Did player win?
```

**Why separate flags instead of enum?**
- **Simple state machine**: Only 2-3 relevant combinations
- **Clarity**: Each flag has single responsibility
- **Performance**: Faster than enum switch statements
- **State combinations**:
  - `running=true, started=true`: Active gameplay
  - `running=false, started=true, won=true`: Victory screen
  - `running=false, started=true, won=false`: Game over (collision)
  - `running=true, started=false`: Waiting for first input

## Algorithm Decisions

### 5. **Collision Detection Order**
```java
// Check in this order:
if (newHead.x < 0 || ...) {      // Wall collision - early exit
    running = false;
    return;
}
// Check collision with self
if (snake.contains(newHead)) {   // O(n) traversal
    running = false;
    return;
}
// Check food only if no collision
if (newHead.equals(food)) {      // O(1) point comparison
    score++;
}
```

**Why this order?**
- **Early exit**: Wall collision exits before expensive O(n) self-collision check
- **Optimization**: Don't check for food if game is already over
- **Correctness**: Wall collision takes precedence (hits wall, not food at same spot)

### 6. **Food Spawning with Loop**
```java
private void spawnFood() {
    int x, y;
    do {
        x = random.nextInt(BOARD_WIDTH);
        y = random.nextInt(BOARD_HEIGHT);
    } while (snake.contains(new Point(x, y)));  // O(n)
    food = new Point(x, y);
}
```

**Why not more complex logic?**
- **Simple**: Do-while is clear and easy to understand
- **Practical**: With max snake of 400 tiles in 400-tile board, worst case is rare
- **Trade-off**: Could use HashSet for O(1) lookup, but memory overhead not worth it for small board

## Pattern Decisions

### 7. **Timer-Based Game Loop**
```java
timer = new Timer(DELAY, this);  // 100ms interval
timer.start();
```

**Why Swing Timer over manual Thread?**
- **Thread Safety**: Swing Timer callbacks on Event Dispatch Thread
- **Simplicity**: No manual synchronization needed
- **Integration**: Works naturally with Swing components
- **Pause Built-in**: Can stop/start easily

### 8. **Immediate Started Flag Check**
```java
if (!started && validMove) {
    started = true;
    startGameTimer();  // Start timer on first input
}
```

**Why not check in actionPerformed()?**
- **Separation of Concerns**: Input handling separate from game logic
- **Cleaner**: keyPressed() focuses on keyboard, actionPerformed() on game loop
- **Earlier Start**: Timer begins immediately when direction pressed, not at next tick
