package com.iafenvoy.rollable.expression;

import java.util.Map;

public interface Expression {
    double eval(Map<String, Double> vars);
}
