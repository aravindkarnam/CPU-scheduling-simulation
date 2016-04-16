/**
 * Created by Aravind on 4/15/2016.
 */
import java.io.File;
import java.util.*;
public class CPUScheduler {
    private class Scheduler
    {
        String type;
        int timeQuanta;
        Scheduler(String type,int timeQuanta)
        {
            this.timeQuanta=timeQuanta;
            this.type=type;
        }
        public String toString() {
            return "Scheduler: "+type+" timeQuanta: "+timeQuanta;
        }
    }
    private Scanner scan;
    Set<Scheduler> Scheduler_settings =new HashSet<>();
    private int processCount;
    //Create a Scanner object from input file, throws a exception when file not found
    private static Scanner setup(String fileName) {
        Scanner scan=null;
        try{
            scan=new Scanner(new File(fileName));
        }
        catch(Exception e){
            System.out.println("file not found");
        }
        return scan;
    }
    private void organizeInput(){
        scan=setup("input.txt");
        Scanner firstLine=new Scanner(scan.nextLine());
        Scanner secondLine=new Scanner(scan.nextLine());
        while(firstLine.hasNext()){
            String type=firstLine.next();
            int timeQuanta=0;
            if(type.equals("RR")) {
                timeQuanta= firstLine.nextInt();
            }
            Scheduler_settings.add(new Scheduler(type,timeQuanta));
        }
        processCount=secondLine.nextInt();
        while(scan.hasNextLine())
        {
            scan.nextLine();
        }
        Iterator<Scheduler> i= Scheduler_settings.iterator();
        while(i.hasNext())
        {
            System.out.println(i.next());
        }

    }
    public static void main(String[] args){
        CPUScheduler cpu=new CPUScheduler();
        cpu.organizeInput();
    }
}
