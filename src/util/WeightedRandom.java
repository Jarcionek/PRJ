package util;

import exceptions.IncorrectUsageException;
import exceptions.ShouldNeverHappenException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jaroslaw Pawlak
 */
public class WeightedRandom {
    
    private final List<Integer> values;
    private final List<Integer> weights;
    private int sum = 0;
    
    public WeightedRandom() {
        values = new ArrayList<Integer>();
        weights = new ArrayList<Integer>();
    }
    
    public void add(int value, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("weight cannot be negative");
        }
        values.add(value);
        weights.add(weight);
        sum += weight;
    }
    
    public int getRandomValue() {
        if (values.isEmpty()) {
            throw new IncorrectUsageException("No values added");
        }
        
        int choice = new Random().nextInt(sum);
        
        int count = weights.get(0);
        if (choice < count) {
            return values.get(0);
        }
        
        for (int i = 1; i < weights.size(); i++) {
            count += weights.get(i);
            if (choice < count) {
                return values.get(i);
            }
        }
        
        throw new ShouldNeverHappenException();
    }
    
    public static void main(String[] args) {
        WeightedRandom wr = new WeightedRandom();
        wr.add(0, 1);
        wr.add(1, 1);
        wr.add(2, 2);
        int count0 = 0;
        int count1 = 0;
        int count2 = 0;
        for (int tests = 0; tests < 100000; tests++) {
            switch(wr.getRandomValue()) {
                case 0: count0++; break;
                case 1: count1++; break;
                case 2: count2++; break;
            }
        }
        System.out.println(count0);
        System.out.println(count1);
        System.out.println(count2);
    }
}
