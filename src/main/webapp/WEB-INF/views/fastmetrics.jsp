<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fastmetrics Server Validation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Style.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/css/images/icon-amazon.png">
    <style>
        body {
            display: flex;
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f9f9f9;
        }

        .sidebar {
            width: 220px;
            background-color: #2c3e50;
            color: #ecf0f1;
            padding: 20px;
            box-sizing: border-box;
            height: auto;
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .sidebar a {
            color: inherit;
            text-decoration: none;
            font-weight: bold;
            transition: 0.3s;
        }

        .sidebar a:hover {
            color: #1abc9c;
        }

        .main {
            flex: 1;
            padding: 40px;
            background-color: white;
            overflow-y: auto;
        }

        h1 {
            color: #2c3e50;
            font-size: 28px;
            margin-bottom: 10px;
        }

        h4 {
            color: #555;
            margin-top: 10px;
            font-weight: normal;
        }

        form {
            margin-top: 20px;
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 10px;
            background-color: #fafafa;
        }

        input[type="text"], textarea {
            font-size: 16px;
            padding: 10px;
            margin: 10px 0;
            border-radius: 12px;
            border: 1px solid #ccc;
            display: block;
            width: 90%;
            box-sizing: border-box;
        }

        #filePath {
            max-width: 300px;
        }

        textarea#inputValue {
            height: 200px;
            resize: vertical;
        }

        button {
            font-size: 16px;
            font-weight: bold;
            padding: 10px 20px;
            border-radius: 12px;
            background-color: #3498db;
            color: white;
            border: none;
            cursor: pointer;
            margin-top: 15px;
        }

        .result-label {
            color: #2c3e50;
            margin-top: 40px;
        }

        .result-box {
            margin-top: 10px;
            padding: 20px;
            border-radius: 10px;
            background-color: #f4f4f4;
            font-size: 16px;
            line-height: 1.6;
        }

        @media (max-width: 768px) {
            body {
                flex-direction: column;
            }

            .sidebar {
                width: 100%;
                flex-direction: row;
                height: auto;
                overflow-x: auto;
            }

            .main {
                padding: 20px;
            }

            input[type="text"], textarea {
                width: 100%;
            }
        }
    </style>
</head>
<body>

<div class="sidebar">
    <a href="${pageContext.request.contextPath}/home">🏠 Home</a>
    <a href="${pageContext.request.contextPath}/minerva">🔍 Minerva</a>
    <a href="${pageContext.request.contextPath}/fastmetrics">⚡ Fastmetrics</a>
</div>

<div class="main">
    <h1>Fastmetrics Server Validation</h1>
    <h4>Enter the Server Log File Location and Log Line</h4>

    <form id="upload-box" action="${pageContext.request.contextPath}/fastmetrics" method="post"
          onsubmit="cleanFilePath(); saveFilePath(); return trimInput()" autocomplete="off">

        <p style="font-size: 0.9rem; color: #aaa;">
            You can enter either a path to a `.csv` file or a folder containing multiple `.csv` files.
        </p>

        <input type="text" id="filePath" name="filePath" placeholder="Enter file location" required>
        <textarea id="fm_inputValue" name="fm_inputValue" placeholder="Enter one or more log lines separated by newline" rows="8" required style="width:100%; font-size:16px; padding:10px; border-radius:12px; border:1px solid #ccc;"></textarea>
        <button type="submit">Check</button>
    </form>

    <%
        String filePath = (String) request.getAttribute("filePath");
        String fm_inputValue = (String) request.getAttribute("fm_inputValue");
        Boolean exists = (Boolean) request.getAttribute("exists");
        String error = (String) request.getAttribute("error");
        List<String> logs = (List<String>) request.getAttribute("logs");
    %>

    <div class="result-container">
        <h2 class="result-label">Result</h2>
        <div class="upload-box" id="validationResults">
            <% if (error != null) { %>
                <p style="color:red;"><%= error %></p>
            <% } else if (fm_inputValue != null && exists != null && logs != null) { %>
                <% for (String log : logs) {
                    String trimmedLog = log.trim().replaceAll("^\\s+", ""); // remove leading spaces
                    if (!trimmedLog.isEmpty()) {
                        boolean isFound = trimmedLog.toLowerCase().contains("exists") || trimmedLog.toLowerCase().contains("✅");
                %>
                    <p style="margin: 5px 0; color:<%= isFound ? "green" : "red" %>;"><%= trimmedLog %></p>
                <%  } } %>
            <% } %>
        </div>
    </div>

    <button class="btn liquid report-fixed" onclick="downloadReport()">Download Report</button>
</div>

<script>
    function saveFilePath() {
        const filePath = document.getElementById('filePath').value;
        if (filePath) {
            sessionStorage.setItem('savedFilePath', filePath);
        }
    }

    function cleanFilePath() {
        const input = document.getElementById('filePath');
        let value = input.value.trim();
        value = value.replace(/^["'“”‘’]+|["'“”‘’]+$/g, '');
        input.value = value;
    }

    function trimInput() {
        const input = document.getElementById("fm_inputValue");
        input.value = input.value.trim();
        return true;
    }

    function downloadReport() {
        const resultBox = document.getElementById("validationResults");
        const lines = Array.from(resultBox.children)
            .map(div => div.textContent.trim().replace(/^\s+/, '')) // trim each line and remove leading spaces
            .filter(line => line.length > 0) // remove empty lines
            .join('\n');

        const blob = new Blob([lines], { type: "text/plain;charset=utf-8" });
        const url = URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = "Fastmetrics_Validation_Report.txt";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        URL.revokeObjectURL(url);
    }


    window.onload = function () {
        const savedPath = sessionStorage.getItem('savedFilePath');
        if (savedPath) {
            document.getElementById('filePath').value = savedPath;
        }
    }
</script>

</body>
</html>
