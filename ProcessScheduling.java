import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.Entry;

public class ProcessScheduling {

    public static class Process {
        private int id;
        private int priority;
        private int duration;
        private int arrivalTime;
        private int waitTime;
        final int maxWaitTime;
        private int executionDuration;
        private int originalDuration;
        private int totalWaitTime;


        /**
         * Constructor for Process class.
         *
         * @param id          ID of process
         * @param priority    priority of process
         * @param duration    duration of process
         * @param arrivalTime arrival time of process
         * @param maxWaitTime longest time a process can wait in queue before priority is increased.
         */
        public Process(int id, int priority, int duration, int arrivalTime, int maxWaitTime, int executionDuration, int totalWaitTime) {
            this.id = id;
            this.priority = priority;
            this.duration = duration;
            this.arrivalTime = arrivalTime;
            this.waitTime = 0;
            this.maxWaitTime = maxWaitTime;
            this.executionDuration = executionDuration;
            this.originalDuration = duration;
            this.totalWaitTime = totalWaitTime;



        }
        // create get and set methods for all parameters.

        // get methods.
        public int getId() {
            return id;
        }

        public int getPriority() {
            return priority;
        }

        public int getDuration() {
            return duration;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public int getWaitTime() {
            return waitTime;
        }

        // set methods
        public void setId(int id) {
            this.id = id;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void setArrivalTime(int arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public static void main(String[] args) {
            // create heap adaptable priority queue
            /*
            I use this heap adaptable priority queue to manage the processes added from the array list based on
            their priorities. This allows me to maintain a dynamic priority queue, where only the highest priority
            process is executed first. This also allows priorities to change a given wait time, so processes do not
            get backed up in the queue.
             */
            HeapAdaptablePriorityQueue<Integer, Process> Q = new HeapAdaptablePriorityQueue<>();
            // create list to store input from file
            /*
            I use this array list to store the list of processes from the input file allowing me to dynamically
            change it and remove processes based on their arrival times, then inserting them into the queue.
             */
            ArrayList<Process> D = new ArrayList<>();
            // initialize current time
            int currentTime = 0;
            // allow for max wait time of 30
            int maxWaitTime = 30;
            // initialize the duration of each process execution
            int executionDuration = 0;
            // initialize the total wait time of processes
            int totalWaitTime = 0;

            // add contents of file to the array list
            try {
                Scanner processes = new Scanner(new File("src\\process_scheduling_input"));
                // get the id, priority, duration, and arrival time for each process.
                while (processes.hasNext()) {
                    // store the id
                    int id = processes.nextInt();
                    // store the priority
                    int priority = processes.nextInt();
                    // store the duration
                    int duration = processes.nextInt();
                    // store the arrival time
                    int arrivalTime = processes.nextInt();
                    // create a new process
                    Process p = new Process(id, priority, duration, arrivalTime, maxWaitTime, executionDuration, totalWaitTime);
                    D.add(p);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // store initial size of array for calculating the average time
            int numOfProcesses = D.size();
            // write information to file
            try (PrintStream output = new PrintStream(new FileOutputStream("src\\process_scheduling_output"))) {
                System.setOut(output);
                // get each process in the array and print out the information
                for (Process p : D) {
                    System.out.println("Id = " + p.getId() + ", priority = " + p.getPriority() +
                            ", duration = " + p.getDuration() + ", arrival time = " + p.getArrivalTime());
                }
                // display max wait time
                System.out.println("\n" + "Maximum wait time = " + maxWaitTime + "\n");
                // create boolean for if a process was added
                boolean processAdded;
                // check if queue and array are not empty
                while (!Q.isEmpty() || !D.isEmpty()) {
                    // process currently is not added to the queue
                    processAdded = false;
                    // iterate through the array and get the processes
                    for (int i = 0; i < D.size(); i++) {
                        Process p = D.get(i);
                        // check if a processes arrival time is less than or equal to the current time.
                        if (p.getArrivalTime() <= currentTime) {
                            // insert the process priority into queue.
                            Q.insert(p.getPriority(), p);
                            // remove the processes added to the queue from the array.
                            D.remove(p);
                            // decrement index to keep looking at the first index in the array
                            // this is done because each process shifts down one index once we remove a process
                            i--;
                        }
                    }
                    // check if the queue is not empty
                    if (!Q.isEmpty()) {
                        // get the minimum process priority for running
                        Entry<Integer, Process> entry = Q.min();
                        // get the values of the process
                        Process currentProcess = entry.getValue();
                        // print out the process that is running
                        System.out.println("Now running Process id = " + currentProcess.id);
                        System.out.println("Arrival = " + currentProcess.arrivalTime);
                        System.out.println("Duration = " + currentProcess.originalDuration);
                        System.out.println("Run time left = " + currentProcess.duration);
                        System.out.println(" at time " + currentTime);
                        // loop while the duration of the running process is greater than 0
                        while (currentProcess.duration > 0) {
                            // decrement the duration
                            currentProcess.duration--;
                            // increment the execution duration
                            currentProcess.executionDuration++;
                            // show what process is currently running and the time it is running at
                            System.out.println("Executed process ID:" + currentProcess.id + ", at time " +
                                    currentTime + " Remaining: " + currentProcess.duration);
                            // iterate through processes in the queue
                            for (Entry<Integer, Process> processEntry : Q) {
                                // get the values of the processes
                                Process p = processEntry.getValue();
                                // check which processes are not running
                                if (p != currentProcess) {
                                    // increment the wait time
                                    p.waitTime++;
                                    // increment the total wait time
                                    p.totalWaitTime++;
                                    // check if a process has reached the max waiting time
                                    if (p.waitTime == maxWaitTime) {
                                        // decrement the priority if it reaches the max wait time
                                        p.priority--;
                                        // reset the wait time
                                        p.waitTime = 0;
                                        // update the priority
                                        Q.replaceKey(processEntry, p.priority);
                                        // state that a process has reached its max wait time
                                        System.out.println("Process " + p.id + " reached maximum wait time... decreasing priority to " + p.priority);
                                    }
                                }
                            }
                            // check if a process is finished
                            if (currentProcess.getDuration() == 0) {
                                // state that the process is finished with the process information
                                System.out.println("Finished running Process id = " + currentProcess.id);
                                System.out.println("Arrival = " + currentProcess.arrivalTime);
                                System.out.println("Duration = " + currentProcess.executionDuration);
                                System.out.println("Run time left = " + currentProcess.duration);
                                System.out.println(" at time " + currentTime);
                                // remove the process from the queue
                                Q.remove(entry);
                                // get the total wait time of the finished process and add it to the overall total wait time
                                totalWaitTime += currentProcess.totalWaitTime;
                            }
                            // increment the time
                            currentTime++;
                            // check for any other processes that need to be added to the queue from the array
                            for (int i = 0; i < D.size(); i++) {
                                Process p = D.get(i);
                                // check for an arrival time of a process in the array that is less than or equal to the current time
                                if (p.getArrivalTime() <= currentTime) {
                                    // insert the process into the queue
                                    Q.insert(p.priority, p);
                                    // remove the process from the array
                                    D.remove(p);
                                    i--;
                                    // note that a process has been added
                                    processAdded = true;
                                }
                            }
                            // check if a process is added to the queue and if the queue is not empty
                            if (processAdded || D.isEmpty()) {
                                if (!Q.isEmpty()) {
                                    // get the minimum priority
                                    Entry<Integer, Process> minEntry = Q.min();
                                    Process minProcess = minEntry.getValue();
                                    // break out of current process execution to start the new lowest priority process if
                                    // the priority is less than our current process
                                    if (minProcess.priority < currentProcess.priority) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // if nothing is in the queue, increment the time, so that we can eventually match an arrival time
                    // to the current time
                     else{
                         currentTime++;
                    }
                }
                // decrement the current time to account for the final addition made when the queue was empty
                currentTime--;
                // calculate the average wait time of all the processes
                double averageWaitTime = (double) totalWaitTime / numOfProcesses;
                // state when all processes are finished with the average wait time.
                System.out.println("Finished running all processes at time " + currentTime);
                System.out.println("Average Wait Time: " + averageWaitTime);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
