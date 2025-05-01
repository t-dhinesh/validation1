package com.server.validation;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FmCsvController {

    @GetMapping({"/", "/fastmetrics"})
    public String showFastmetricsPage() {
        return "fastmetrics";
    }

    @PostMapping("/fastmetrics")
    public String checkCsv(@RequestParam("filePath") String filePath,
                           @RequestParam("fm_inputValue") String fm_inputValue,
                           Model model) {
        try {
            String[] logLines = fm_inputValue.split("\\r?\\n");
            List<String> resultLogs = new ArrayList<>();
            boolean anyExists = false;

            for (String line : logLines) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty()) continue;

                boolean exists = FmSearchAndValidating.compareLogWithCSV(trimmedLine, filePath);
                anyExists = anyExists || exists;

                String result = "Checked value: " + trimmedLine +
                        " - " + (exists ? "~~~~ ✅ Exists" : "~~~~ ❌ Not-Found");
                resultLogs.add(result);
                FastmetricsLogStorage.addLog(result);
            }

            model.addAttribute("fm_inputValue", fm_inputValue);
            model.addAttribute("logs", resultLogs);
            model.addAttribute("exists", anyExists);

        } catch (Exception e) {
            model.addAttribute("error", "The mismatch in the log. Error: " + e.getMessage());
        }

        model.addAttribute("filePath", filePath);
        return "fastmetrics";
    }


}
