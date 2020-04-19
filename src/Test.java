import java.util.*;
import java.io.*;
public class Test {

    public static void main(String[] args){
        int factorial = 1;
        ArrayList<ArrayList<Integer>> allperm = new ArrayList<>();
        ArrayList<Integer> num = new ArrayList<>();
        num.add(1);
        num.add(2);
        num.add(3);

        allperm.add(num);
        while(factorial < 6){

            ArrayList<Integer> temporary = new ArrayList<>(num);
            Collections.shuffle(temporary);
            System.out.println(temporary);
            for(ArrayList<Integer> arr : allperm){
                if (!temporary.equals(arr)){
                    System.out.println("test");
                    allperm.add(temporary);
                    break;
                }
            }
            factorial++;
        }
        System.out.println(allperm);
    }

}