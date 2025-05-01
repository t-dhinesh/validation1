<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Server Validation</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}css/Style.css">
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}css/images/icon-amazon.png">
  <style>
    body {
      margin: 0;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      display: flex;
      background-color: #f9f9f9;
    }

    .sidebar {
      width: 220px;
      background-color: #2c3e50;
      color: #ecf0f1;
      height: 100vh;
      padding: 20px;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      gap: 15px;
    }

    .sidebar a {
      color: inherit;
      text-decoration: none;
      font-weight: bold;
      transition: all 0.2s;
    }

    .sidebar a:hover {
      color: #1abc9c;
    }

    .sidebar div {
      margin-top: auto;
      background-color: #234737;
      padding: 10px;
      border-radius: 8px;
      text-align: center;
      font-size: 14px;
    }

    .main {
      flex: 1;
      padding: 40px;
      background-color: #ffffff;
      display: flex;
      flex-direction: column;
      justify-content: center;
      box-shadow: -2px 0 5px rgba(0, 0, 0, 0.05);
    }

    h1 {
      color: #34495e;
      font-size: 28px;
      margin-bottom: 10px;
    }

    h2 {
      color: #2c3e50;
      margin-top: 30px;
      font-size: 22px;
    }

    p {
      color: #555;
      font-size: 16px;
      margin-top: 10px;
    }

    ul {
      padding-left: 20px;
      color: #333;
      font-size: 16px;
    }

    ul li {
      margin-bottom: 10px;
    }

    @media screen and (max-width: 768px) {
      body {
        flex-direction: column;
      }

      .sidebar {
        width: 100%;
        height: auto;
        flex-direction: row;
        overflow-x: auto;
      }

      .sidebar a {
        margin: 0 10px;
      }

      .sidebar div {
        display: none;
      }

      .main {
        padding: 20px;
      }
    }
  </style>
</head>
<body>

<div class="sidebar">
  <a href="${pageContext.request.contextPath}/home">🏠 Home</a>
  <a href="${pageContext.request.contextPath}/minerva">🔍 Minerva</a>
  <a href="${pageContext.request.contextPath}/fastmetrics">⚡ Fastmetrics</a>
  <div>Select a tool above.</div>
</div>

<div class="main">
  <h1>Welcome to Server Validation Tools</h1>
  <p>Use these tools to validate server logs efficiently with either Minerva or Fastmetrics. Navigate using the sidebar to get started.</p>

  <h2>How to Use</h2>
  <ul>
    <li>Choose a tool from the sidebar.</li>
    <li>Paste the file or folder location of the server log (e.g., DSN.csv).</li>
    <li>Enter one or more log entries for validation.</li>
    <li>Review results and download reports as needed.</li>
  </ul>
</div>

</body>
</html>
