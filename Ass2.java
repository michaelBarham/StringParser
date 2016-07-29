import java.io.*;
/**
 * Created by Michael on 10/03/2015.
 */
public class Ass2 {
    public static void main(String[] args) {
        // used to determine if exception thrown or if user wants to enter new line
        boolean completed = false;
        System.out.println("Michael Barham 1304335\nWelcome to Michael’s expression evaluation program. Please type an expression");
        while (!completed){
            try{
                ExpTree myTree = new Parser().parseLine();
                System.out.println("Post Order: " + myTree.DisplayPostOrder());
                System.out.println("In Order: " + myTree.DisplayInOrder());
                System.out.println("Answer: "+myTree.Evaluation());
                completed = true;
            } catch (ParseException e){
                // throw exception then try new line
                System.out.println("Exception thrown: " + e);
            }
            if(completed){
                System.out.println("Another expression (y/n)?");
                BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
                String line = "";
                do
                    try {
                        line = buf.readLine().trim();
                    } catch(Exception e) {
                        System.out.println("Error in input");
                    }
                while (line.length()==0);
                // check first character entered is y otherwise just leave program
                if(line.charAt(0) == 'y'){
                    completed = false;
                    System.out.println("Please enter next expression:");
                } else {
                    System.exit(0);
                }
            }
        }
    }
}
