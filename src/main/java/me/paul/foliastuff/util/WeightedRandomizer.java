package me.paul.foliastuff.util;

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class WeightedRandomizer<T> {

    private final HashMap<T, Integer> weights = new HashMap<>();
    private final Random random;

    public WeightedRandomizer(Random random) {
        if (random == null)
            throw new NullPointerException("random cannot be null");
        this.random = random;
    }

    public WeightedRandomizer() {
        this(new Random());
    }

    /**
     * Adds the given Object to the collection of entries.
     *
     * @param weight The chance to select the object. The chances are all
     *               normalized, so this number is relative to the total. For example,
     *               if two entries were added with a chance of 100, they would have
     *               equal chances.
     */
    public void add(T element, int weight) {
        Validate.validState(weight >= 0, "Cannot assign a negative weight.");

        if (weight == 0) {
            getWeightMap().remove(element);
            return;
        }

        getWeightMap().put(element, weight);
    }

    public int getTotalWeight() {
        int total = 0;
        for (int weight : getWeightMap().values())
            total += weight;
        return total;
    }

    public int getWeight(T element) {
        Integer weight = getWeightMap().get(element);
        return weight == null ? 0 : weight;
    }

    public T select() {
        if (getWeightMap().isEmpty())
            return null;
        int number = random.nextInt(getTotalWeight());
        int cumulative = 0;
        for (Entry<T, Integer> entry : getWeightMap().entrySet()) {
            cumulative += entry.getValue();
            if (number < cumulative)
                return entry.getKey();
        }
        return null;
    }

    public Map<T, Integer> getWeightMap() {
        return weights;
    }


}
