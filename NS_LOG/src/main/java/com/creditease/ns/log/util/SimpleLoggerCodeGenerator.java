package com.creditease.ns.log.util;

import java.io.*;
import java.text.MessageFormat;

/**
 * Created by liuyang on 2017/3/22.
 *
 * @author liuyang
 */
public class SimpleLoggerCodeGenerator {

    private static String[] TYPES_TO_STRING = new String[]{"varchar", "text", "char", "timestamp"};
    private static String[] TYPES_TO_LONG = new String[]{"varchar", "text", "char", "timestamp"};
    private static boolean allString = true;


    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(new File("")), "utf-8"));

        String line = null;
        StringBuilder mdcCodes = new StringBuilder();
        StringBuilder logConstantsCodes = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {

            String[] columnNameAndType = parseLine(line);
            String columnName = normalizeColumnName(columnNameAndType[0]);
            String logContantsKey = normalizeLogConstantsKeyName(columnNameAndType[0]);
            String parameType = analyzeType(columnNameAndType[1]);
            logConstantsCodes.append(generateLogConstantsCodes(logContantsKey));
            mdcCodes.append(generateMDCCodes("",parameType,columnName, logContantsKey));
        }

        System.out.printf(mdcCodes.toString());
        System.out.printf(logConstantsCodes.toString());
        bufferedReader.close();
    }

    private static String generateLogConstantsCodes(String logContantsKey) {
        String codeTemplate = "public static final String MDC_KEY_{0}= \"{1}\";\n";
        return MessageFormat.format(codeTemplate,logContantsKey.toUpperCase(),logContantsKey);
    }


    public static String normalizeLogConstantsKeyName(String s) {
        String[] columnNameWords = s.split("_");
        for (int i = 0; i < columnNameWords.length; i++) {
            //全部小写
            if (i == 0) {
                columnNameWords[i] = columnNameWords[i].toLowerCase();
            } else {
                char[] chars = columnNameWords[i].toLowerCase().toCharArray();
                chars[0] -= 32;
                columnNameWords[i] = new String(chars);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnNameWords.length; i++) {
            sb.append(columnNameWords[i]);
        }

        return sb.toString();
    }

    private static String generateMDCCodes(String type, String parameType, String columnName,String logContantsKey) {

        String template = "public {0} set{2}({1} {3}) '{'\n" +
            "\t\t\n" +
            "\t\tMDC.put(FuqianlaLogConstants.MDC_KEY_{4}, {3});\n" +
            "\t\treturn this;\n" +
            "\t'}'\n";

        return MessageFormat.format(template,type,parameType,columnName,logContantsKey,logContantsKey.toUpperCase());
    }

    private static String analyzeType(String s) {
        if (allString) {
            return "String";
        }

        return null;
    }

    private static String normalizeColumnName(String s) {
        String[] columnNameWords = s.split("_");
        for (int i = 0; i < columnNameWords.length; i++) {
            //头字母大写
            char[] chars = columnNameWords[i].toLowerCase().toCharArray();
            chars[0] -= 32;
            columnNameWords[i] = new String(chars);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnNameWords.length; i++) {
            sb.append(columnNameWords[i]);
        }

        return sb.toString();
    }

    private static String[] parseLine(String line) {
        String splitPattern = "\\s+";
        return line.split(splitPattern);
    }

}
