/**
 * Created by Aravind on 4/15/2016.
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class CPUScheduler {
    private static final int priorityOrder=3,pidOrder=0,burstTimeOrder=2,arrivalTimeOrder=1;
    private Set<Scheduler> Scheduler_settings = new HashSet<>();
    private Scanner scan;
    private int processCount;
    private processData processes[];
    private int orderMode = 2;
    private PrintWriter toFile;

    //Create a Scanner object from input file, throws a exception when file not found
    private void setup(String fileName) {
        try {
            scan = new Scanner(new File(fileName));
        } catch (Exception e) {
            toFile.println("file not found");
        }
        try{
            toFile=new PrintWriter("output.txt","UTF-8");
        }
        catch(Exception e) {
            toFile.println("file not found");
        }
    }
    public void execute(){
        organizeInput();
        simulator();
        toFile.close();
    }

    public static void main(String[] args) {
        CPUScheduler cpu = new CPUScheduler();
        cpu.execute();

    }
    private void calculateAverageRunningTime(){
        int totalWaitingTime=0;
        for(int i=0;i<processCount;i++)
        {
            totalWaitingTime+=processes[i].waitingTime;
            processes[i].reset();
        }
        toFile.println("AVG Waiting Time: "+(float)totalWaitingTime/(float)processCount);
    }
    private void implementRR(int timeQuanta){
        Queue<processData> readyQueue=new LinkedList<>();//This is ready Queue
        //Ordering process list according to arrival time
        orderMode=arrivalTimeOrder;
        Arrays.sort(processes);
        //variables to hold time, previous states and job status
        int last=-1,time=0,allDone=0;
        //Process in control of CPU will hold this variable processINcpu
        processData processINcpu=null;
        //Run until all jobs are done
        while(allDone<processCount){
            //loading processeses into ready queue based on arrival time
            for(int i=0;i<processes.length;i++)
            {
                if(last<processes[i].arrivalTime && processes[i].arrivalTime<=time)
                    readyQueue.add(processes[i]);
            }
            //loading process in cpu at the end of ready queue based upon its completion status
            if(processINcpu!=null && processINcpu.workDone!=0)
            {
                readyQueue.add(processINcpu);
            }
            //Giving CPU to process at the front of queue
            processINcpu=readyQueue.remove();
            //storing current status into variables for calculation purposes
            last=time;
            // printing cpu status at current time
            toFile.println(time+" "+processINcpu.pid);
            //updating time variable
            time+=processINcpu.work(timeQuanta,time);
            //updating number of process that are done
            if(processINcpu.workDone==0) {
                allDone++;
                processINcpu.setWaitingTime(time-processINcpu.burstTime-processINcpu.arrivalTime);
            }
        }
        // calculating average waiting time
        calculateAverageRunningTime();
    }
    private void implementSJF(){
        List<processData> readyQueue =new ArrayList<>();
        orderMode=burstTimeOrder;
        Arrays.sort(processes);
        processData processINcpu=null;
        int allDone=0,time=0,last=-1;
        while(allDone<processCount){
            for(int i=0;i<processCount;i++)
            {
                if(last<processes[i].arrivalTime && processes[i].arrivalTime<=time)
                    readyQueue.add(processes[i]);
            }
            Collections.sort(readyQueue);
            processINcpu=readyQueue.remove(0);
            last=time;
            toFile.println(time+" "+processINcpu.pid);
            time+=processINcpu.work(0,time);
            if(processINcpu.workDone==0) {
                allDone+=1;
                processINcpu.setWaitingTime(time-processINcpu.burstTime-processINcpu.arrivalTime);
            }
        }
        calculateAverageRunningTime();
    }
    private void implementPR_noPREMP(){
        List<processData> readyQueue =new ArrayList<>();
        orderMode=arrivalTimeOrder;
        Arrays.sort(processes);
        processData processINcpu=null;
        int allDone=0,time=0,last=-1;
        while(allDone<processCount){
            for(int i=0;i<processCount;i++)
            {
                if(last<processes[i].arrivalTime && processes[i].arrivalTime<=time)
                    readyQueue.add(processes[i]);
            }
            orderMode=priorityOrder;
            Collections.sort(readyQueue);
            processINcpu=readyQueue.remove(0);
            last=time;
            toFile.println(time+" "+processINcpu.pid);
            time+=processINcpu.work(0,time);
            if(processINcpu.workDone==0) {
                allDone+=1;
                processINcpu.setWaitingTime(time-processINcpu.burstTime-processINcpu.arrivalTime);
            }
        }
        calculateAverageRunningTime();
    }
    private void organizeInput() {
        setup("input.txt");
        Scanner firstLine = new Scanner(scan.nextLine());
        Scanner secondLine = new Scanner(scan.nextLine());
        while (firstLine.hasNext()) {
            String type = firstLine.next();
            int timeQuanta = 0;
            if (type.equals("RR")) {
                timeQuanta = firstLine.nextInt();
            }
            Scheduler_settings.add(new Scheduler(type, timeQuanta));
        }
        processCount = secondLine.nextInt();
        processes = new processData[processCount];
        for (int i = 0; i < processCount; i++) {
            Scanner thisLine = new Scanner(scan.nextLine());
            processes[i] = new processData(thisLine.nextInt(), thisLine.nextInt(), thisLine.nextInt(), thisLine.nextInt());
        }
    }
    private void implementPR_withPREMP(){
        List<processData> readyQueue =new ArrayList<>();
        orderMode=arrivalTimeOrder;
        Arrays.sort(processes);
        processData processINcpu=null;
        int allDone=0,time=0,last=-1;
        while(allDone<processCount){
            for(int i=0;i<processCount;i++)
            {
                if(last<processes[i].arrivalTime && processes[i].arrivalTime<=time)
                    readyQueue.add(processes[i]);
            }
            if(processINcpu!=null && processINcpu.workDone!=0)
            {
                readyQueue.add(processINcpu);
            }
            orderMode=priorityOrder;
            Collections.sort(readyQueue);
            processINcpu=readyQueue.remove(0);
            int timeTaken=time+processINcpu.workDone;
            int interruptTime=0;
            for(int i=0;i<processCount;i++)
            {
                if(last<processes[i].arrivalTime && processes[i].arrivalTime<timeTaken && processes[i].priority<processINcpu.priority)
                {
                    interruptTime=processes[i].arrivalTime-time;
                    break;
                }
            }
            last=time;
            toFile.println(time+" "+processINcpu.pid);
            time+=processINcpu.work(interruptTime,time);
            if(processINcpu.workDone==0) {
                allDone+=1;
                processINcpu.setWaitingTime(time-processINcpu.burstTime-processINcpu.arrivalTime);
            }
        }
        calculateAverageRunningTime();
    }
    private void simulator(){
        Iterator<Scheduler> schedulers=Scheduler_settings.iterator();
        while(schedulers.hasNext())
        {
            Scheduler currentSetting=schedulers.next();
            if(currentSetting.type.equals("RR")){
                toFile.println("RR");
                implementRR(currentSetting.timeQuanta);
            }
            else if(currentSetting.type.equals("SJF")){
                toFile.println("SJF");
                implementSJF();
            }
            else if(currentSetting.type.equals("PR_noPREMP")){
                toFile.println("PR_noPREMP");
                implementPR_noPREMP();
            }
            else if(currentSetting.type.equals("PR_withPREMP")){
                toFile.println("PR_withPREMP");
                implementPR_withPREMP();
            }
        }
    }
    private class Scheduler {
        String type;
        int timeQuanta;

        Scheduler(String type, int timeQuanta) {
            this.timeQuanta = timeQuanta;
            this.type = type;
        }

        public String toString() {
            return "Scheduler: " + type + " timeQuanta: " + timeQuanta;
        }
    }

    private class processData implements Comparable {
        int pid;
        int arrivalTime;
        int burstTime;
        int priority;
        int workDone;
        int waitingTime;

        processData(int pid, int arrivalTime, int burstTime, int priority) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
            this.workDone=burstTime;
            this.waitingTime=0;
        }
        public void reset()
        {
            waitingTime=0;
            workDone=burstTime;
        }
        public int work(int timeQuanta,int time)
        {
            int retTime=0;
            if(timeQuanta>0) {
                if(timeQuanta<=workDone) {
                    workDone -= timeQuanta;
                    retTime=timeQuanta;
                }
                else
                {
                    retTime=workDone;
                    workDone=0;
                }
                return retTime;
            }
            else
            {
                retTime=workDone;
                workDone=0;
            }
            return retTime;
        }
        public void setWaitingTime(int waitingTime){
            this.waitingTime=waitingTime;
        }
        public int compareTo(Object p) {
            switch (orderMode) {
                case 0:
                    if (this.pid > ((processData) p).pid)
                        return 1;
                    else if (this.pid < ((processData) p).pid)
                        return -1;
                    else
                        return 0;

                case 1:
                    if (this.arrivalTime > ((processData) p).arrivalTime)
                        return 1;
                    else if (this.arrivalTime < ((processData) p).arrivalTime)
                        return -1;
                    else
                        return 0;
                case 2:
                        if (this.burstTime > ((processData) p).burstTime)
                            return 1;
                        else if (this.burstTime < ((processData) p).burstTime)
                            return -1;
                        else
                            return 0;
                case 3:
                    if (this.priority > ((processData) p).priority)
                        return 1;
                    else if (this.priority < ((processData) p).priority)
                        return -1;
                    else
                        return 0;
                default:
                    return 0;
            }
        }
    }
}
