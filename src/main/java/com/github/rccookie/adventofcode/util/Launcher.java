package com.github.rccookie.adventofcode.util;

import java.lang.reflect.Constructor;
import java.util.Calendar;

import com.github.rccookie.adventofcode.util.Day.NotImplementedException;
import com.github.rccookie.common.util.Console;

public class Launcher {

    /**
     * Overrides the current day. If null the actual day will be used.
     */
    public static Integer DAY_OVERRIDE = null;

    /**
     * Overrides the current year. If null the actual year will be used.
     */
    public static Integer YEAR_OVERRIDE = null;

    /**
     * If true all days of the current year wil run after each other. False by default.
     */
    public static boolean RUN_ALL = false;

    /**
     * If true you will always be prompted to enter your login information. False by default.
     */
    public static boolean ALWAYS_ASK_FOR_LOGIN = false;

    /**
     * If true your password will be saved with your username. Make sure to not upload that file
     * anywhere! False by default. The file is stored at "recources/data/login.login".
     */
    public static boolean SAVE_PASSWORD = false;

    /**
     * The relative or absolute root for java files, pointing at the default package.
     * By default this is the current directory. Has to end with a '/' unless its value is "".
     */
    public static String FILE_ROOT = "";



    // ----------------------------------------------------------------



    public static void main(String[] args) {
        run(args);
    }



    public static void run() { run(new String[0]); }

    public static void run(String[] args) {
        try {
            DayGenerator.password = null;
            int day = DAY_OVERRIDE != null ? DAY_OVERRIDE : Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int year = YEAR_OVERRIDE != null ? YEAR_OVERRIDE : Calendar.getInstance().get(Calendar.YEAR);
            if(args != null && args.length > 0) day = Integer.parseInt(args[0]);
            if(RUN_ALL) {
                for(int i=1; i<day; i++) {
                    runDay(i, year);
                    System.out.println("\n-------------------------------------------------\n-------------------------------------------------\n");
                }
            }
            runDay(day, year);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DayGenerator.driver.close();
            DayGenerator.inScanner.close();
        }
    }



    private static final void runDay(int day, int year) throws Exception {
        if(!new DayGenerator(day, year).generateNeccecaryFiles()) return;

        @SuppressWarnings("unchecked")
        Class<Day> cls = (Class<Day>)Class.forName(DayGenerator.packageUsername + ".year" + year + ".day" + day + ".Day");
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
