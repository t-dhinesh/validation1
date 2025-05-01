package com.server.validation;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FmSearchAndValidating {


    public static boolean compareLogWithCSV(String logLine, String path) {
        File fileOrDir = new File(path);

        if (!fileOrDir.exists()) {
            System.out.println("❌ Path does not exist.");
            return false;
        }

        if (fileOrDir.isFile() && path.toLowerCase().endsWith(".csv")) {
            // Handle single file
            return validateAgainstSingleCSV(logLine, path);
        } else if (fileOrDir.isDirectory()) {
            // Handle folder of CSVs
            File[] csvFiles = fileOrDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (csvFiles == null || csvFiles.length == 0) {
                System.out.println("⚠️ No CSV files found in the folder.");
                return false;
            }

            for (File csvFile : csvFiles) {
                boolean matchFound = validateAgainstSingleCSV(logLine, csvFile.getAbsolutePath());
                if (matchFound) {
                    System.out.println("✅ Match found in file: " + csvFile.getName());
                    return true;
                }
            }

            System.out.println("❌ No match found in any CSV files.");
            return false;
        } else {
            System.out.println("❌ Provided path is neither a .csv file nor a directory.");
            return false;
        }
    }
    public static boolean validateAgainstSingleCSV(String logLine,String filepath) {

        String[] parts = logLine.split("\\|");

        if (parts.length < 8) {
            System.out.println("Invalid log line format.");
            return false;
        }

        // Extract values
        String logSchemaName = parts[1];
        String logSequenceNumber = parts[6];
        String epochMillisStr = parts[7];

        String logFormattedDate;
        try {
            long epochMillis = Long.parseLong(epochMillisStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            outputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = new Date(epochMillis);
            logFormattedDate = outputFormat.format(date);
        } catch (NumberFormatException e) {
            System.out.println("Invalid epoch in log line.");
            return false;
        }

        System.out.println("🔍 Log Values:");
        System.out.println("Schema Name: " + logSchemaName);
        System.out.println("Sequence Number: " + logSequenceNumber);
        System.out.println("Created Timestamp: " + logFormattedDate);

        //String csvFilePath = "C:\\Users\\dhine\\Music\\ereader_framework_connectivity (1).csv";
        int schemaCol = 0;
        int sequenceCol = 5;
        int timestampCol = 6;

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try (
                FileReader reader = new FileReader(Paths.get(filepath).toFile());
                CSVParser parser = CSVFormat.DEFAULT.parse(reader)
        ) {
            int rowIndex = 0;
            for (CSVRecord record : parser) {
                rowIndex++;
                if (rowIndex == 1) continue;

                if (record.size() > timestampCol) {
                    String schemaName = record.get(schemaCol).trim();
                    String sequenceNumber = record.get(sequenceCol).trim();
                    String timestampValue = record.get(timestampCol).trim();

                    try {
                        timestampValue = padMilliseconds(timestampValue);
                        Date parsedDate = inputFormat.parse(timestampValue);
                        String formattedDate = outputFormat.format(parsedDate);

                        if (schemaName.equals(logSchemaName) &&
                                sequenceNumber.equals(logSequenceNumber) &&
                                formattedDate.equals(logFormattedDate)) {

                            System.out.println("\n🎯 Match Details:");
                            System.out.println("Schema: " + schemaName);
                            System.out.println("Sequence: " + sequenceNumber);
                            System.out.println("Created: " + formattedDate);
                            return true;
                        }

                    } catch (ParseException e) {
                        System.out.println("⚠️ Skipped invalid timestamp at row " + rowIndex + ": " + timestampValue);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String padMilliseconds(String datetime) {
        if (!datetime.contains(".")) return datetime + ".000";
        String[] parts = datetime.split("\\.");
        if (parts.length != 2) return datetime;
        String ms = parts[1];
        if (ms.length() == 1) ms += "00";
        else if (ms.length() == 2) ms += "0";
        else if (ms.length() > 3) ms = ms.substring(0, 3);
        return parts[0] + "." + ms;
    }



}
