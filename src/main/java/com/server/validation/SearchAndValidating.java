package com.server.validation;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SearchAndValidating {

    public boolean find(String inputValue, String fileLocation) {

        return userInputValue(inputValue, fileLocation);
    }

    public boolean userInputValue(String searchValue, String filepath) {
        LogData data = logSplitter(searchValue);
        return isValueFound(filepath, data.getTimestamp(), data.getEvent(), data.getSequence(), data.getSequenceSession());
    }

    public boolean isValueFound(String path, String targetTimestamp, String targetEvent, String targetSequence, String targetSequenceSession) {
        File fileOrDir = new File(path);

        System.out.println("Path received: " + path);
        System.out.println("Exists: " + fileOrDir.exists());
        System.out.println("Is File: " + fileOrDir.isFile());
        System.out.println("Is Directory: " + fileOrDir.isDirectory());

        if (!fileOrDir.exists()) {
            System.out.println("Path does not exist!");
            return false;
        }

        if (fileOrDir.isFile() && path.toLowerCase().endsWith(".csv")) {
            System.out.println("Validating single CSV file: " + fileOrDir.getName());
            return checkCsvFile(fileOrDir, targetTimestamp, targetEvent, targetSequence, targetSequenceSession);
        }

        if (fileOrDir.isDirectory()) {
            File[] csvFiles = fileOrDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (csvFiles == null || csvFiles.length == 0) {
                System.out.println("No CSV files found in folder.");
                return false;
            }

            for (File csvFile : csvFiles) {
                System.out.println("Validating file in folder: " + csvFile.getName());
                if (checkCsvFile(csvFile, targetTimestamp, targetEvent, targetSequence, targetSequenceSession)) {
                    return true;
                }
            }
        }

        System.out.println("Not a CSV file or directory!");
        return false;
    }




    private boolean checkCsvFile(File csvFile, String targetTimestamp, String targetEvent, String targetSequence, String targetSequenceSession) {
        try (Reader reader = new FileReader(csvFile)) {
            String[] headers = {
                    "servicereceivetime", "devicerecordtime", "softwareversion", "buildtype",
                    "platform", "model", "hardware", "devicetype", "timezone", "marketplaceid",
                    "countryofresidence", "devicelanguage", "otagroupname", "sequence", "trigger",
                    "event", "values", "protoversion", "deviceid", "customerid", "sequencesession"
            };

            CSVParser parser = CSVFormat.DEFAULT.withHeader(headers).withSkipHeaderRecord().parse(reader);
            long targetEpoch = Long.parseLong(targetTimestamp.trim());

            for (CSVRecord record : parser) {
                try {
                    String epochStr = record.get("devicerecordtime").trim();
                    if (epochStr.isEmpty()) continue;

                    long epochValue = new BigDecimal(epochStr).longValue();

                    if (epochValue == targetEpoch) {
                        String sequence = record.get("sequence").trim();
                        String event = record.get("event").trim();
                        String session = record.get("sequencesession").trim();

                        if (sequence.equals(targetSequence.trim()) &&
                                event.equalsIgnoreCase(targetEvent.trim()) &&
                                session.replace("'", "").equalsIgnoreCase(targetSequenceSession.trim().replace("'", ""))) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


//    public boolean isValueFound(String filePath, String targetTimestamp, String targetEvent, String targetSequence, String targetSequenceSession) {
//        boolean matchFound = false;
//        File fileOrFolder = new File(filePath);
//        File[] csvFiles;
//
//        if (fileOrFolder.isFile() && filePath.toLowerCase().endsWith(".csv")) {
//            csvFiles = new File[]{fileOrFolder}; // Single file
//        } else if (fileOrFolder.isDirectory()) {
//            csvFiles = fileOrFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv")); // Folder
//        } else {
//            return false;
//        }
//
//        if (csvFiles == null || csvFiles.length == 0) return false;
//
//        for (File csvFile : csvFiles) {
//            try (Reader reader = new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8)) {
//                String[] headers = {
//                        "servicereceivetime", "devicerecordtime", "softwareversion", "buildtype",
//                        "platform", "model", "hardware", "devicetype", "timezone", "marketplaceid",
//                        "countryofresidence", "devicelanguage", "otagroupname", "sequence", "trigger",
//                        "event", "values", "protoversion", "deviceid", "customerid", "sequencesession"
//                };
//
//                CSVParser parser = CSVFormat.DEFAULT.withHeader(headers).withSkipHeaderRecord().parse(reader);
//                long targetEpoch = Long.parseLong(targetTimestamp.trim());
//
//                for (CSVRecord record : parser) {
//                    try {
//                        String epochStr = record.get("devicerecordtime").trim();
//                        if (epochStr.isEmpty()) continue;
//
//                        long epochValue = new BigDecimal(epochStr).longValue();
//
//                        if (epochValue == targetEpoch) {
//                            String sequence = record.get("sequence").trim();
//                            String event = record.get("event").trim();
//                            String session = record.get("sequencesession").trim();
//
//                            if (sequence.equals(targetSequence.trim()) &&
//                                    event.equalsIgnoreCase(targetEvent.trim()) &&
//                                    cleanSession(session).equalsIgnoreCase(cleanSession(targetSequenceSession))) {
//                                matchFound = true;
//                                break;
//                            }
//                        }
//                    } catch (Exception ignored) {}
//                }
//
//                if (matchFound) break;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return matchFound;
//    }

    public static LogData logSplitter(String logLine) {
        try {
            String timestamp = timestampConverter(logLine.split(" ")[0]);
            String[] parts = logLine.substring(logLine.lastIndexOf(":") + 1).split(",");

            if (parts.length < 3) {
                throw new IllegalArgumentException("Log line does not contain enough comma-separated values.");
            }

            String sequenceSession = "'" + parts[0].trim() + "'";
            String sequence = parts[1].trim();
            String event = parts[2].trim();
            return new LogData(timestamp, event, sequence, sequenceSession);
        } catch (Exception e) {
            System.out.println("Error parsing log line: " + logLine);
            e.printStackTrace();
            return new LogData("0", "", "", "");
        }
    }

    public static String timestampConverter(String convertEpocFormat) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd:HHmmss");
            LocalDateTime localDateTime = LocalDateTime.parse(convertEpocFormat, formatter);
            ZoneId istZone = ZoneId.of("Asia/Kolkata");
            ZonedDateTime istZoned = localDateTime.atZone(istZone);
            long epochMillis = istZoned.toInstant().toEpochMilli();
            return String.valueOf(epochMillis);
        } catch (Exception e) {
            System.out.println("Error converting timestamp: " + convertEpocFormat);
            e.printStackTrace();
            return "0";
        }
    }

    private String cleanSession(String session) {
        return session.trim().replace("'", "");
    }
}
