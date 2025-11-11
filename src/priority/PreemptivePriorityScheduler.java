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


    }




}
