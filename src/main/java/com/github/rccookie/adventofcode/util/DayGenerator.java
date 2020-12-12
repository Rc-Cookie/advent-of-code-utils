package com.github.rccookie.adventofcode.util;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Formatter;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.rccookie.common.util.Console;

public class DayGenerator {

    private static final String DESC_REPLACEMENT_1 = "This description will be generated once it is available. Please don't change anything!";
    private static final String DESC_REPLACEMENT_2 = "This description will be generated once the first task is completed. Please don't change anything!";
    private static final String PREFAB_CODE = "super.resultPart2(); //Don't edit this until task 1 is done or the code will try to download the second description every time";

    public static final boolean generateFilesForDay(int day) {
        try {
            File dir = new File("day" + day + "");
            if(!dir.exists()) dir.mkdir();

            File inputFile = new File("recources/input/day" + day + ".input");
            if(!inputFile.exists() || (Files.readString(Path.of("recources/input/day" + day + ".input")).startsWith("Please don't") || Files.readString(Path.of("recources/input/day" + day + ".input")).startsWith("Input not available"))) {
                Formatter f = new Formatter(inputFile);
                f.format(getInput(day));
                f.close();
            }

            File file = new File("day" + day + "/Day.java");
            if(file.exists()) {
                String currentCode = Files.readString(Path.of("day" + day + "/Day.java"));
                if(currentCode.contains(DESC_REPLACEMENT_1) && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) >= day) {
                    String desc = getFirstDescription(day);
                    new PrintWriter(file).append(currentCode = currentCode.replace(DESC_REPLACEMENT_1, desc)).close();
                }
                if(currentCode.contains(DESC_REPLACEMENT_2) &&! currentCode.contains(PREFAB_CODE) && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) >= day) {
                    String desc = getSecondDescription(day);
                    new PrintWriter(file).append(currentCode.replace(DESC_REPLACEMENT_2, desc)).close();
                }
                return false;
            }

            String desc1 = getFirstDescription(day), desc2 = getSecondDescription(day);
            Formatter f = new Formatter(file);
            f.format(Files.readString(Path.of("recources/data/Day.template")), day, desc1, desc2, PREFAB_CODE);

            f.close();
            return true;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }





    private static final String getInput(int day) {
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < day) {
            Console.log("Input for day " + day + " not available yet.");
            return "Input not available yet.";
        }

        Console.log("Downloading input for day " + day + "...");

        // Disable CSS error logging
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        WebDriver driver = new HtmlUnitDriver();
        try {
            // Advent of code login page
            driver.get("https://adventofcode.com/auth/login");
            driver.findElements(By.tagName("a")).get(13).click(); // Github login link

            Console.log("Logging in...");

            // Github login page
            driver.findElement(By.id("login_field")).sendKeys("Rc-Cookie");
            WebElement e = driver.findElement(By.id("password"));
            e.sendKeys(getPassword());
            e.submit();

            Console.log("Logged in");

            // Go to input page
            driver.get("https://adventofcode.com/2020/day/" + day + "/input");
            String out = driver.getPageSource(); // Page source is for this page identical with the displayed text

            Console.log("Successfully downloaded input for day " + day);
            System.out.println(); // To seperate from other logging
            return out;
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println(); // To seperate from other logging
            return null;
        } finally {
            driver.close();
        }
    }

    private static final String getPassword() throws Exception {
        return Files.readString(Path.of("data/password.password"), StandardCharsets.US_ASCII);
    }

    private static final String getFirstDescription(int day) {
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < day) {
            Console.log("Description part 1 for day " + day + " not avaliable yet.");
            return DESC_REPLACEMENT_1;
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
            return DESC_REPLACEMENT_1;
        } catch(Exception e) {
            e.printStackTrace();
            Console.log("Failed to download description part 1");
            System.out.println(); // To seperate from other logging
            return DESC_REPLACEMENT_1;
        } finally {
            driver.close();
        }
    }

    private static final String getSecondDescription(int day) {
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < day) {
            Console.log("Description part 2 for day " + day + " not avaliable yet.");
            return DESC_REPLACEMENT_2;
        }

        Console.log("Downloading description part 2 for day " + day + "...");

        // Disable CSS error logging
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        WebDriver driver = new HtmlUnitDriver();
        try {
            // Advent of code login page
            driver.get("https://adventofcode.com/auth/login");
            driver.findElements(By.tagName("a")).get(13).click(); // Github login link

            Console.log("Logging in...");

            // Github login page
            driver.findElement(By.id("login_field")).sendKeys("Rc-Cookie");
            WebElement e = driver.findElement(By.id("password"));
            e.sendKeys(getPassword());
            e.submit();

            Console.log("Logged in");

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
            return DESC_REPLACEMENT_2;
        } catch(IndexOutOfBoundsException e) {
            Console.log("Description part 2 is not avalialble yet");
            System.out.println(); // To seperate from other logging
            return DESC_REPLACEMENT_2;
        } catch(Exception e) {
            e.printStackTrace();
            Console.log("Failed to download description part 2");
            System.out.println(); // To seperate from other logging
            return DESC_REPLACEMENT_2;
        } finally {
            driver.close();
        }
    }
}
