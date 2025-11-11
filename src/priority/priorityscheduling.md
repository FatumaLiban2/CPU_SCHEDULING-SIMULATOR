# Preemptive Priority Scheduling Guide

## Table of Contents
1. [What is Priority Scheduling?](#what-is-priority-scheduling)
2. [How Preemptive Priority Scheduling Works](#how-preemptive-priority-scheduling-works)
3. [Understanding the Code](#understanding-the-codes-technical-terms)
4. [Key Differences from Other Scheduling](#key-differences-from-other-scheduling)
5. [Advantages and Disadvantages](#advantages-and-disadvantages)

---

## What is Priority Scheduling?

**Priority Scheduling** assigns each process a priority number. The CPU always executes the **highest priority** process in the ready queue.

### Two Types:
1. **Non-preemptive**: Once a process starts, it runs until completion
2. **Preemptive**: A higher priority process can interrupt (preempt) the currently running process

---

## How Preemptive Priority Scheduling Works

### Core Concept
At **every time unit**, the scheduler checks:
- Which processes have arrived?
- Which has the highest priority?
- Is it different from the current running process?

If a higher priority process arrives, it **immediately takes over** the CPU.

### Example Scenario

**Processes:**

P1: Priority=2, Arrival=0, Burst=5
P2: Priority=5, Arrival=2, Burst=3

**Timeline:**

t=0: P1 starts (only process available)
t=1: P1 continues
t=2: P2 arrives with priority 5 > P1's priority 2
→ P2 PREEMPTS P1 (P1 pauses with 3 units remaining)
t=3: P2 continues
t=4: P2 continues
t=5: P2 finishes → P1 resumes
t=6: P1 continues
t=7: P1 continues
t=8: P1 finishes


**Key point:** P1 was interrupted mid-execution when a higher priority process (P2) arrived.

---

## Understanding the Code's Technical Terms

### 1. Process Class

new Process("P1", 2, 0, 5)

Creates a process with:
- **`"P1"`** - Process ID (name)
- **`2`** - Priority (higher number = higher priority)
- **`0`** - Arrival time (when it enters the system)
- **`5`** - Burst time (CPU time needed)

### 2. Ready Queue

List<Process> ready = new ArrayList<>();
for (Process p : all) {
    if (!p.isFinished() && p.getArrivalTime() <= time) {
        ready.add(p);
    }
}

**What it does:** Collects all processes that:
- Have **arrived** (arrival time ≤ current time)
- Are **not finished** (still need CPU time)

**Real-world analogy:** People waiting in line at a service counter who have checked in and aren't done yet.

### 3. Priority Sorting

ready.sort(Comparator
    .comparingInt(Process::getPriority).reversed()  // Highest priority first
    .thenComparingInt(Process::getArrivalTime)     // If tied, earlier arrival
    .thenComparing(Process::getId));               // If still tied, alphabetical

**What it does:** Sorts the ready queue by:
1. **Priority** (5 before 2)
2. **Arrival time** (if priorities match, earlier arrival wins)
3. **Process ID** (if still tied, alphabetical order)

**Example:**

Before: [P1(priority=2), P2(priority=5), P3(priority=5, arrival=1)]
After:  [P2(priority=5, arrival=2), P3(priority=5, arrival=1), P1(priority=2)]
         ↑ highest priority, later arrival  ↑ same priority, earlier arrival


### 4. Preemption Mechanism

Process current = ready.get(0);  // Get highest priority
current.runFor(1, time);         // Run for 1 time unit only

**Why 1 time unit?**
- After every unit, the scheduler **re-checks** the ready queue
- If a new higher priority process arrived, it gets selected next
- This is how preemption happens automatically

### 5. Idle CPU Handling
java
if (ready.isEmpty()) {
    OptionalInt nextArr = all.stream()
        .filter(p -> !p.isFinished() && p.getArrivalTime() > currentTime)
        .mapToInt(Process::getArrivalTime)
        .min();
    time = nextArr.orElse(time + 1);
}

**What it does:** If no process is ready:
- Finds the **next arrival time** of unfinished processes
- **Jumps time forward** to that moment (no point simulating empty CPU cycles)

**Example:**

t=5: No ready processes
Next arrival: P5 at t=10
→ Jump to t=10 immediately (skip t=6,7,8,9)


### 6. Metrics Calculation
java
int turnaround = p.getCompletionTime() - p.getArrivalTime();
int waiting = turnaround - p.getOriginalBurst();

**Turnaround Time:** Total time from arrival to completion
- P1 arrives at t=0, finishes at t=8 → turnaround = 8

**Waiting Time:** Time spent NOT executing
- P1 needed 5 units, but took 8 total → waited 3 units

---

## How the Scheduler Works Step-by-Step

### Step 1: Initialization

List<Process> all = new ArrayList<>(processes);
int time = 0;
all.sort(Comparator.comparingInt(Process::getArrivalTime));
int finished = 0;

- Creates a working copy of processes
- Starts simulation at `time = 0`
- Sorts by arrival time for efficient tracking
- Counts finished processes

### Step 2: Main Loop
java
while (finished < n) {

Runs until all processes complete.

### Step 3: Build Ready Queue
Finds processes that have **arrived** and are **not finished**.

### Step 4: Handle Idle CPU
If no process is ready, jumps time forward to the next arrival.

### Step 5: Select Highest Priority Process
Sorts ready queue and selects the first process (highest priority).

### Step 6: Execute Process
- Runs process for **1 time unit**
- Checks if process completed
- Increments time

### Step 7: Calculate Metrics
Computes turnaround and waiting times, then averages.

---

## Key Differences from Other Scheduling

| Algorithm | When to Switch Process |
|-----------|------------------------|
| **FCFS** | Only when current finishes |
| **SJF (Non-preemptive)** | Only when current finishes |
| **Round Robin** | After fixed time quantum |
| **Preemptive Priority** | **Every time unit** (checks for higher priority) |

---

## Advantages and Disadvantages

### Advantages
- Critical tasks get immediate attention
- Better for real-time systems (deadlines matter)
- Flexible priority assignment

### Disadvantages
- **Starvation:** Low priority processes may never run
- More context switching overhead
- Complex implementation

---

## Example Execution

With processes from `Main.java`:
java
P1: priority=2, arrival=0, burst=5  (low priority, first)
P2: priority=5, arrival=2, burst=3  (high priority, arrives soon)
P3: priority=3, arrival=4, burst=2  (medium priority)
P4: priority=5, arrival=5, burst=4  (high priority, arrives last)


**Expected Timeline:**
1. **t=0-1:** P1 starts and runs (only process available)
2. **t=2:** P2 arrives and **preempts P1** (priority 5 > 2)
3. **t=2-4:** P2 runs
4. **t=5:** P2 finishes, P4 arrives and takes over (priority 5 > P3's 3)
5. **t=5-8:** P4 runs
9. **t=9:** P4 finishes, P3 runs (priority 3 > P1's 2)
10. **t=9-10:** P3 runs
11. **t=11:** P3 finishes, P1 resumes
12. **t=11-13:** P1 completes remaining burst
13. **t=14:** All processes finished

This demonstrates **multiple preemptions** as higher priority processes arrive throughout execution.

---

## Key Takeaway

The scheduler **re-evaluates** which process should run at **every single time unit**. This constant checking is what enables preemption - when a higher priority process arrives, it's immediately detected and scheduled in the next time unit.


Save this as `src/priority/priorityscheduling.md` (note: `.md` extension, not `.txt`).

To create the file in IntelliJ:
1. Right-click on `src/priority/`
2. Select **New → File**
3. Name it `priorityscheduling.md`
4. Paste the content above