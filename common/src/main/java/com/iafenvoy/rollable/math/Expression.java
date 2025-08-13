package com.iafenvoy.rollable.math;

import java.util.Map;

public interface Expression {
    double eval(Map<String, Double> vars);
}
