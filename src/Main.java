import model.Process;
import priority.PreemptivePriorityScheduler;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<model.Process> processes = List.of(
                new model.Process("P1", 2, 0, 5),
                new model.Process("P2", 5, 2, 3),
                new model.Process("P3", 3, 4, 2),
                new Process("P4", 5, 5, 4)
        );
        new PreemptivePriorityScheduler().run(processes);
    }
}