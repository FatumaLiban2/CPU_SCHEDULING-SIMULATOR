package priority;

import model.Process;
import java.util.*;

public class PreemptivePriorityScheduler {

    public void run(List<Process> processes) {
        List<Process> all = new ArrayList<>(processes);
        int time = 0;
        all.sort(Comparator.comparingInt(Process::getArrivalTime));
        int finished = 0;
        int n = all.size();

        while (finished < n) {
            List<Process> ready = new ArrayList<>();
            for (Process p : all) {
                if (!p.isFinished() && p.getArrivalTime() <= time) {
                    ready.add(p);
                }
            }

            if (ready.isEmpty()) {
                final int currentTime = time;  // Create final copy for lambda
                OptionalInt nextArr = all.stream()
                        .filter(p -> !p.isFinished() && p.getArrivalTime() > currentTime)
                        .mapToInt(Process::getArrivalTime)
                        .min();
                time = nextArr.orElse(time + 1);
                continue;
            }

            ready.sort(Comparator
                    .comparingInt(Process::getPriority).reversed()
                    .thenComparingInt(Process::getArrivalTime)
                    .thenComparing(Process::getId));

            Process current = ready.get(0);
            System.out.println("t=" + time + ": running " + current);
            current.runFor(1, time);
            if (current.isFinished()) {
                finished++;
                System.out.println("t=" + current.getCompletionTime() + ": finished " + current.getId());
            }
            time++;
        }

        printMetrics(all);

    }

    private void printMetrics(List<Process> processes) {
        System.out.println("\nMetrics:");
        double totalTurnaround = 0;
        double totalWaiting = 0;
        int n = processes.size();

        for (Process p : processes) {
            int turnaround = p.getCompletionTime() - p.getArrivalTime();
            int waiting = turnaround - p.getOriginalBurst();
            totalTurnaround += turnaround;
            totalWaiting += waiting;
            System.out.println(p.getId() + ": completion=" + p.getCompletionTime()
                    + ", turnaround=" + turnaround + ", waiting=" + waiting);
        }
        System.out.printf("Average turnaround=%.2f, average waiting=%.2f%n",
                totalTurnaround / n, totalWaiting / n);
    }
}
