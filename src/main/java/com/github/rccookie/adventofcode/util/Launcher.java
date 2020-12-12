package com.github.rccookie.adventofcode.util;

import java.lang.reflect.Constructor;
import java.util.Calendar;

import com.github.rccookie.adventofcode.util.Day.NotImplementedException;
import com.github.rccookie.common.util.Console;

public class Launcher {

    /**
     * Overrides the current day. If null the actual day will be used.
     */
    public static final Integer DAY_OVERRIDE = null;


    public static final boolean RUN_ALL = false;



    // ----------------------------------------------------------------



    public static void main(String[] args) {
        try {
            int day = DAY_OVERRIDE != null ? DAY_OVERRIDE : Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            if(args != null && args.length > 0) day = Integer.parseInt(args[0]);
            if(RUN_ALL) {
                for(int i=1; i<day; i++) {
                    runDay(i);
                    System.out.println("\n-------------------------------------------------\n-------------------------------------------------\n");
                }
            }
            runDay(day);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static final void runDay(int day) throws Exception {
        if(DayGenerator.generateFilesForDay(day)) {
            Console.log("Generated template for day " + day);
            return;
        }

        @SuppressWarnings("unchecked")
        Class<Day> cls = (Class<Day>)Class.forName("day" + day + ".Day");
        Constructor<Day> ctor = cls.getDeclaredConstructor();
        ctor.setAccessible(true);
        Day dayInstance = ctor.newInstance();

        Console.map("Result of part 1 from day " + dayInstance.getDay(), dayInstance.resultPart1());
        System.out.println("\n-------------------------------------------------\n");
        try {
            Console.map("Result of part 2 from day " + dayInstance.getDay(), dayInstance.resultPart2());
        } catch(NotImplementedException e) { }
    }
}
