package com.server.validation;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class CsvController {
    @GetMapping({"/", "/home"})
    public String showHomePage() {
        return "home";
    }

    @GetMapping({"/", "/minerva"})
    public String showMinervaPage() {
        return "minerva";
    }

    @PostMapping("/minerva")
    public String checkCsv(@RequestParam("filePath") String filePath, @RequestParam("inputValue") String inputValue, Model model) {
        try {
            List<String> logs = new ArrayList<>();
            List<String> logLines = extractLogLines(inputValue);
            CsvController ref = new CsvController();

            String res = null;
            String log = null;



            boolean exists = false;
            List<String> resultArray = new ArrayList<>();
            List<String> statusArray = new ArrayList<>();

            for (String line : logLines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.matches("^Line \\d+:.*")) {
                    line = line.replaceFirst("^Line \\d+:\\s*", " ");
                }

                exists = ref.find(line, filePath);

                System.out.println("\nLog: " + line);
                String statusOutput = exists ? "✅ Found" : "❌ Not Found";
                System.out.println(statusOutput);

                // Store results
                String status = (exists ? "✅ Exists in CSV!" : "❌ Not Found in CSV!") + " → " + line;
                resultArray.add(status);
                statusArray.add(statusOutput); // storing the simple found/not found status
            }

            System.out.println(resultArray);


            model.addAttribute("exists", exists);
            model.addAttribute("filePath", filePath);
            model.addAttribute("inputValue", inputValue);
            model.addAttribute("resultArray", resultArray);  // <-- Add this line


            //+++++++++++++++++++++++++++++++


        } catch (Exception e) {
            model.addAttribute("error", "Error while validating logs: " + e.getMessage());
            e.printStackTrace();  // Print stack trace for debugging in console
        }
        return "minerva";
    }

    private List<String> extractLogLines(String inputValue) {
        List<String> logLines = new ArrayList<>();

        // Regular expression to match the log lines.
        // Ensures that the log line starts with a timestamp and includes other log data.
        Pattern pattern = Pattern.compile("\\d{6}:\\d{6} demd\\[\\d+\\]: D acehal_log::protometric\\.protometric:protometric: .+?(?=(?=\\d{6}:\\d{6} demd\\[)|$)", Pattern.DOTALL);

        Matcher matcher = pattern.matcher(inputValue);

        // Find matches in the input string and process each one.
        while (matcher.find()) {
            String logLine = matcher.group().trim(); // Trim any extra spaces

            // Remove the "Line X:" prefix if it exists
            logLine = logLine.replaceAll("^Line \\s*\\d+:, ", "").trim();

            // Add the log line to the list
            logLines.add(logLine);
        }

        // Print out extracted log lines for debugging
        System.out.println("Extracted log lines: " + logLines);

        // Return the list of log lines
        return logLines;
    }




    public boolean find(String inputValue, String fileLocation) {
        SearchAndValidating ref = new SearchAndValidating();
        return ref.find(inputValue, fileLocation); // Uses your properly working logic
    }


    public boolean userInputValue(String searchValue, String filepath) {
        LogData data = logSplitter(searchValue);
        return isValueFound(filepath, data.getTimestamp(), data.getEvent(), data.getSequence(), data.getSequenceSession());
    }

    public boolean isValueFound(String folderPath, String targetTimestamp, String targetEvent, String targetSequence, String targetSequenceSession) {
        boolean matchFound = false;

        File folder = new File(folderPath);
        File[] csvFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) return false;

        for (File csvFile : csvFiles) {
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

                                matchFound = true;
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                if (matchFound) break;

            } catch (Exception e) {
                e.printStackTrace(); // Show error in console for debugging
            }
        }

        return matchFound;
    }

    public static LogData logSplitter(String logLine) {
        // Remove the "Line X:" prefix if present
        logLine = logLine.replaceAll("^Line \\d+:\\s*", "").trim(); // Remove "Line X:" part

        // Now, extract the timestamp (it should be the first part of the remaining string)
        String[] parts = logLine.split(" ", 2);  // Split once into timestamp and the rest of the log
        String timestamp = timestampConverter(parts[0].trim());  // Extract the timestamp (e.g., "241125:235511")

        // Extract event and sequence session info
        String[] details = parts[1].substring(parts[1].lastIndexOf(":") + 1).split(",");
        String sequenceSession = "'" + details[0].trim() + "'";
        String sequence = details[1].trim();
        String event = details[2].trim();

        return new LogData(timestamp, event, sequence, sequenceSession);
    }

    public static String timestampConverter(String convertEpocFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd:HHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(convertEpocFormat, formatter);
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        ZonedDateTime istZoned = localDateTime.atZone(istZone);
        long epochMillis = istZoned.toInstant().toEpochMilli();
        return String.valueOf(epochMillis);
    }
}
