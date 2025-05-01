<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Minerva Server Validation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}css/Style.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}css/images/icon-amazon.png">

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
    <h1>ACS Minerva Athena Server Validation</h1>
    <h4>Enter the Server Log File Location and Log Lines</h4>

    <form id="upload-box" action="minerva" method="post"  onsubmit="cleanFilePath(); saveFilePath();" enctype="multipart/form-data"; autocomplete="off">

         <p style="font-size: 0.9rem; color: #aaa;">
                    You can enter either a path to a `.csv` file or a folder containing multiple `.csv` files.
                </p>

        <input type="text" id="filePath" name="filePath" required
               placeholder="Enter file location">

        <textarea id="inputValue" name="inputValue" required
                  placeholder="Paste multiple device logs here"></textarea>

        <button type="submit">Check</button>
    </form>

    <%
        String filePath = (String) request.getAttribute("filePath");
        String inputValue = (String) request.getAttribute("inputValue");
        Boolean exists = (Boolean) request.getAttribute("exists");
        String error = (String) request.getAttribute("error");
        String res = (String) request.getAttribute("res");

    %>

    <h2 class="result-label">Validation Results</h2>
    <div class="result-box" id="validationResults"></div>
    <button class="btn liquid report-fixed" onclick="downloadReport()">
        <span>Download Report</span>
    </button>

</div>

<script>
     window.onload = function () {
            const savedPath = sessionStorage.getItem('savedFilePath');
            if (savedPath) {
                document.getElementById('filePath').value = savedPath;
            }
        }

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

     function downloadReport() {
         const resultBox = document.getElementById("validationResults");
         const lines = Array.from(resultBox.children).map(div => div.textContent).join('\n');

         const blob = new Blob([lines], { type: "text/plain;charset=utf-8" });
         const url = URL.createObjectURL(blob);

         const a = document.createElement("a");
         a.href = url;
         a.download = "Minerva_Validation_Report.txt";
         document.body.appendChild(a);
         a.click();
         document.body.removeChild(a);

         URL.revokeObjectURL(url);
     }


     function trimInput() {
            const input = document.getElementById("inputValue");
            input.value = input.value.trim();
            return true;
     }
    const rawResults = `${resultArray}`;
    const entries = rawResults.split(/(?=❌|✅)/);
    const resultBox = document.getElementById("validationResults");

    entries.forEach(entry => {
        entry = entry.trim();
        if (!entry) return;
        const line = document.createElement("div");

        if (entry.startsWith("❌")) {
            line.textContent = entry;
            line.style.color = "red";
        } else if (entry.startsWith("✅")) {
            line.textContent = entry;
            line.style.color = "green";
        } else {
            line.textContent = entry;
        }

        resultBox.appendChild(line);
    });

</script>

</body>
</html>
