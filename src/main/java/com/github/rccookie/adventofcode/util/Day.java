package com.github.rccookie.adventofcode.util;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.Files.readString;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Day {

    public abstract long resultPart1() throws Exception;

    public long resultPart2() throws Exception {
        throw new NotImplementedException();
    };

    protected String input() {
        try {
            return readString(Path.of("input/day" + getDay() + ".input"), US_ASCII);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String[] inputInLines() {
        try {
            ArrayList<String> input = new ArrayList<>();
            Scanner sc = inputScanner();
            while(sc.hasNextLine()) input.add(sc.nextLine());
            return input.toArray(new String[] { });
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected long[] inputNumbers() {
        String[] input = inputInLines();
        long[] numbers = new long[input.length];
        for(int i=0; i<input.length; i++) numbers[i] = Long.parseLong(input[i]);
        return numbers;
    }

    protected Scanner inputScanner() {
        try {
            return new Scanner(new File("input/day" + getDay() + ".input"));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getDay() {
        String name = getClass().getName();
        return Integer.parseInt(name.substring(3, name.indexOf(".")));
    }

    public class NotImplementedException extends Exception {
        private static final long serialVersionUID = -4881948333783821683L;
    }
}
