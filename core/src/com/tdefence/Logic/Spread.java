package com.tdefence.Logic;

/**
 * Created by Marcel Jurišta on 25.10.2017.
 */

public class Spread {
    int minimum;
    int maximum;

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public Spread(int minimum, int maximum){
        this.maximum = maximum;

        this.minimum = minimum;
    }
}
