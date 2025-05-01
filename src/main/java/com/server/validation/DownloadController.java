//package com.server.validation;
//
//
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
//@Controller
//public class DownloadController {
//
//    @GetMapping("/downloadReport")
//    public void downloadReport(@RequestParam(name = "tool", defaultValue = "fastmetrics") String tool,
//                               HttpServletResponse response) throws IOException {
//        List<String> logs;
//
//        // Choose logs based on 'tool' parameter
//        if ("minerva".equalsIgnoreCase(tool)) {
//            logs = LogStorage.getLogs();
//        } else {
//            logs = FastmetricsLogStorage.getLogs();  // default to fastmetrics
//        }
//
//        response.setContentType("text/plain");
//        response.setHeader("Content-Disposition", "attachment; filename=" + tool + "_report.txt");
//
//        try (PrintWriter writer = response.getWriter()) {
//            for (String log : logs) {
//                writer.println(log);
//            }
//        }
//    }
//}