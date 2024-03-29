package com.terransky.stuffnthings.utilities.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Code courtesy of <a href="https://stackoverflow.com/a/60911654">Gogu CelMare</a>.
 */
public class DegreeToQuadrant {

    List<QuadrantItem> quadrants;
    String[] QuadrantNames = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    /**
     * Initializes a DegreeToQuadrant object.
     */
    public DegreeToQuadrant() {
        double leftValue = 360 - 22.5;
        double rightValue = 22.5;
        double step = 45;
        quadrants = new ArrayList<>();
        for (String quadrant : QuadrantNames) {
            QuadrantItem quadrantItem;
            if (quadrant.equals("N"))
                quadrantItem = new QuadrantItem(quadrant, leftValue, rightValue, Operation.OR);
            else {
                leftValue = rightValue;
                rightValue = leftValue + step;
                quadrantItem = new QuadrantItem(quadrant, leftValue, rightValue, Operation.AND);
            }
            quadrants.add(quadrantItem);
        }
    }

    public String getQuadrantName(double degree) {
        for (QuadrantItem quadrantItem : quadrants)
            if (quadrantItem.isIn(degree))
                return quadrantItem.m_quadrantName;
        return "";
    }

    /**
     * Pre-defined operations
     */
    private enum Operation {
        AND,
        OR
    }

    public static class QuadrantItem {
        String m_quadrantName;
        Operation m_operation;
        double m_left;
        double m_right;

        /**
         * A quadrant
         *
         * @param name      Name of quadrant
         * @param left      Degree minimum
         * @param right     Degree maximum
         * @param operation And, Or
         */
        QuadrantItem(String name, double left, double right, Operation operation) {
            m_quadrantName = name;
            m_operation = operation;
            m_left = left;
            m_right = right;
        }

        boolean isIn(double degree) {
            if (m_operation.equals(Operation.AND))
                return degree > m_left && degree < m_right;
            else
                return degree > m_left || degree < m_right;
        }
    }
}