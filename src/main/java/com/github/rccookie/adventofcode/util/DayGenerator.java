package com.github.rccookie.adventofcode.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.rccookie.common.util.Console;

class DayGenerator {

    static String username, packageUsername, password;
    static boolean loggedIn = false;
    static final Scanner inScanner = new Scanner(System.in);
    static final HtmlUnitDriver driver = new HtmlUnitDriver();


    private final int day, year;
    private boolean gotUsername = false, gotPassword = false;



    public DayGenerator(int day, int year) {
        this.day = day;
        this.year = year;
        ensureGotUsername();
    }

    /**
     * Generates neccecary files for the day.
     * 
     * @return Weather the day can be executed immediatly afterwards, or a recompiling is neccecary
     */
    public boolean generateNeccecaryFiles() {
        ensureInput();
        return ensureDayFile();
    }



    private void ensureGotUsername() {
        if(gotUsername) return;
        getUsername();
        gotUsername = true;
    }

    private void getUsername() {
        final File loginData = new File("recources/data/login.login");
        if(Launcher.ALWAYS_ASK_FOR_LOGIN || !loginData.exists()) {
            saveUsername();
        }
        else {
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(loginData));
                String line = reader.readLine();
                if(line == null || line.replaceAll(" ", "").length() == 0) saveUsername();
                else {
                    username = line;
                    packageUsername = username.toLowerCase().replaceAll("-", "");
                }
                reader.close();
            } catch(IOException e) {
                Console.log("Failed to read username");
                saveUsername();
            }
        }
    }

    private void saveUsername() {
        askForUsername();
        new File("recources/data").mkdirs();
        try{
            new Formatter(new File("recources/data/login.login")).format(username + "\n").close();
        } catch(FileNotFoundException e) {
            throw new RuntimeException("Failed to save username");
        }
    }

    private void askForUsername() {
        System.out.print("Please enter your username on GitHub: ");
        username = inScanner.nextLine();
        packageUsername = username.toLowerCase().replaceAll("-", "");
    }



    private void ensureGotPassword() {
        if(gotPassword) return;
        getPassword();
        gotPassword = true;
    }

    private void getPassword() {
        if(password != null) return;
        final File loginData = new File("recources/data/login.login");
        if(Launcher.ALWAYS_ASK_FOR_LOGIN || !loginData.exists()) {
            savePassword();
        }
        else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(loginData));
                reader.readLine();
                String line = reader.readLine();
                if(line == null || line.replaceAll(" ", "").length() == 0) {
                    savePassword();
                }
                else password = line;
                reader.close();
            } catch(IOException e) {
                Console.log("Failed to read password");
                savePassword();
            }
        }
    }

    private void savePassword() {
        ensureGotUsername();
        new File("recources/data").mkdirs();
        askForPassword();
        if(!Launcher.SAVE_PASSWORD) return;
        try{
            new Formatter(new File("recources/data/login.login")).format(username + (Launcher.SAVE_PASSWORD ? "\n" + password : "") + "\n").close();
        } catch(FileNotFoundException e) {
            throw new RuntimeException("Failed to save password");
        }
    }

    private void askForPassword() {
        System.out.print("Please enter the password for '" + username + "': ");
        password = inScanner.nextLine();
    }



    private void ensureInput() {
        ensureGotUsername();
        final File inputFile = new File("recources/input/" + packageUsername + "/year" + year + "/day" + day + ".input");
        try{
            if(!inputFile.exists() || Files.readString(Path.of("recources/input/" + packageUsername + "/year" + year + "/day" + day + ".input")).startsWith("Input not available")) {
                new File("recources/input/" + packageUsername + "/year" + year).mkdirs();
                // Use PrintWriter as input may contain '%' characters
                final PrintWriter writer = new PrintWriter(inputFile);
                writer.print(getInput());
                writer.close();
            }
        } catch(IOException e) {
            Console.log("Failed to get input");
        }
    }



    private boolean ensureDayFile() {
        ensureGotUsername();
        final File dayFile = new File(Launcher.FILE_ROOT + packageUsername + "/year" + year + "/day" + day + "/Day.java");
        if(!dayFile.exists()) {
            try {
                new File(Launcher.FILE_ROOT + packageUsername + "/year" + year + "/day" + day).mkdirs();
                final Formatter writer = new Formatter(dayFile);
                writer.format(Prefabs.DAY, packageUsername, year, day, getFirstDescription(), getSecondDescription(), Prefabs.CODE);
                writer.close();
                Console.log("Generated template for day " + day);
            } catch(IOException e) {
                Console.log("Failed to generate template file");
            }
            return false;
        }
        try {
            String currentCode = Files.readString(Path.of(Launcher.FILE_ROOT + packageUsername + "/year" + year + "/day" + day + "/Day.java"));
            if(currentCode.contains(Prefabs.DESC_1) && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) >= day) {
                String desc = getFirstDescription();
                new PrintWriter(dayFile).append(currentCode = currentCode.replace(Prefabs.DESC_1, desc)).close();
            }
            if(currentCode.contains(Prefabs.DESC_2) &&! currentCode.contains(Prefabs.CODE) && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) >= day) {
                String desc = getSecondDescription();
                new PrintWriter(dayFile).append(currentCode.replace(Prefabs.DESC_2, desc)).close();
            }
        } catch(IOException e) {
            Console.log("Failed to generate description");
        }
        return true;
    }





    private final String getInput() {
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.YEAR) > year || (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.DAY_OF_MONTH) < day)) {
            Console.log("Input for day " + day + " not available yet.");
            return "Input not available yet.";
        }

        Console.log("Downloading input for day " + day + "...");

        // Disable CSS error logging
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        try {
            ensureLoggedIn();

            // Go to input page
            driver.get("https://adventofcode.com/" + year + "/day/" + day + "/input");
            String out = driver.getPageSource(); // Page source is for this page identical with the displayed text
            if(out.startsWith("Please don't")) throw new RuntimeException();

            Console.log("Successfully downloaded input for day " + day);
            System.out.println(); // To seperate from other logging
            return out;
        } catch(Exception e) {
            System.out.println(); // To seperate from other logging
            return "Input not available yet.";
        }
    }

    private void ensureLoggedIn() {
        if(loggedIn) return;

        // Advent of code login page
        driver.get("https://adventofcode.com/auth/login");
        driver.findElements(By.tagName("a")).get(13).click(); // Github login link
        Console.log("Logging in...");

        ensureGotUsername();
        ensureGotPassword();
        // Github login page
        driver.findElement(By.id("login_field")).sendKeys(username);
        WebElement e = driver.findElement(By.id("password"));
        e.sendKeys(password);
        e.submit();
        Console.log("Logged in");
        loggedIn = true;
    }

    private final String getFirstDescription() {
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < day) {
            Console.log("Description part 1 for day " + day + " not avaliable yet.");
            return Prefabs.DESC_1;
        }

        Console.log("Downloading description part 1 for day " + day + "...");

        // Disable CSS error logging
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        WebDriver driver = new HtmlUnitDriver();
        try {
            // Go to day page
            driver.get("https://adventofcode.com/2020/day/" + day);
            String out = driver.findElements(By.className("day-desc")).get(0).getAttribute("innerHTML");

            Console.log("Successfully downloaded description part 1 for day " + day);
            System.out.println(); // To seperate from other logging

            out = out.replaceAll("\n", "\n     * ");
            return out;
        } catch(NoSuchElementException e) {
            Console.log("Description part 1 is not avalialble yet");
            System.out.println(); // To seperate from other logging
        } catch(Exception e) {
            e.printStackTrace();
            Console.log("Failed to download description part 1");
            System.out.println(); // To seperate from other logging
        } finally {
            driver.close();
        }
        return Prefabs.DESC_1;
    }

    private final String getSecondDescription() {
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < day) {
            Console.log("Description part 2 for day " + day + " not avaliable yet.");
            return Prefabs.DESC_2;
        }

        Console.log("Downloading description part 2 for day " + day + "...");

        // Disable CSS error logging
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        WebDriver driver = new HtmlUnitDriver();
        try {
            ensureLoggedIn();

            // Go to day page
            driver.get("https://adventofcode.com/2020/day/" + day);
            String out = driver.findElements(By.className("day-desc")).get(1).getAttribute("innerHTML");

            Console.log("Successfully downloaded description part 2 for day " + day);
            System.out.println(); // To seperate from other logging

            out = out.replaceAll("\n", "\n     * ");
            return out;
        } catch(NoSuchElementException e) {
            Console.log("Description part 2 is not avalialble yet (you propably haven't completed part 1)");
            System.out.println(); // To seperate from other logging
        } catch(IndexOutOfBoundsException e) {
            Console.log("Description part 2 is not avalialble yet");
            System.out.println(); // To seperate from other logging
        } catch(Exception e) {
            e.printStackTrace();
            Console.log("Failed to download description part 2");
            System.out.println(); // To seperate from other logging
        } finally {
            driver.close();
        }
        return Prefabs.DESC_2;
    }
}
