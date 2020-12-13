package com.github.rccookie.adventofcode.util;

class Prefabs {
    public static final String DAY = "package %s.year%d.day%d;\n\nimport com.github.rccookie.common.util.Console;\n\npublic class Day extends com.github.rccookie.adventofcode.util.Day {\n\n    private final String[] input;\n\n    {\n        input = inputInLines();\n    }\n\n    /**\n     * %s\n     * <p>This method will return the result for the personal input.\n     */\n    @Override\n    public long resultPart1() throws Exception {\n        Console.map(\"Input\", input);\n        return -1;\n    }\n\n    /**\n     * %s\n     * <p>This method will return the result for the personal input.\n     */\n    @Override\n    public long resultPart2() throws Exception {\n        %s\n        return -1;\n    }\n}\n";
    
    public static final String DESC_1 = "This description will be generated once it is available. Please don't change anything!";
    public static final String DESC_2 = "This description will be generated once the first task is completed. Please don't change anything!";
    public static final String CODE = "super.resultPart2(); //Don't edit this until task 1 is done or the code will try to download the second description every time";
}
