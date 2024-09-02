<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>User Dashboard</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 0;
        background: linear-gradient(to right, #000000, #ffffff); /* Black to white gradient */
        color: #333;
    }

    header, footer {
        background-color: #ffffff; /* White background */
        color: #333;
        text-align: center;
        padding: 15px 0;
        border-bottom: 2px solid #e0e0e0; /* Light gray border */
    }

    nav {
        background-color: #333; /* Dark background for navbar */
        color: #fff;
        padding: 10px 0;
        display: flex;
        justify-content: center;
        align-items: center;
        position: sticky;
        top: 0;
        z-index: 1000;
    }

    .nav-links {
        list-style: none;
        padding: 0;
        margin: 0;
        display: flex;
    }

    .nav-links li {
        margin: 0 15px;
    }

    .nav-links a {
        color: #fff;
        text-decoration: none;
        padding: 10px 15px;
        display: block;
        font-weight: bold;
        border-radius: 4px;
    }

    .nav-links a:hover {
        background-color: #555; /* Slightly lighter black on hover */
    }

    .container {
        max-width: 900px;
        margin: 20px auto;
        padding: 20px;
        background-color: #ffffff; /* White background for the container */
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
    }

    .section {
        margin-bottom: 20px;
    }

    .section h2 {
        margin-bottom: 15px;
        color: #000; /* Black text for section titles */
    }

    .form-group {
        margin-bottom: 15px;
    }

    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }

    .form-group input, .form-group select, .form-group button {
        width: 100%;
        padding: 10px;
        box-sizing: border-box;
        border: 1px solid #d1d1d1; /* Light gray border */
        border-radius: 4px;
    }

    .form-group input, .form-group select {
        background-color: #f9f9f9; /* Very light gray background */
    }

    .form-group button {
        background-color: #333; /* Black background */
        color: #fff; /* White text */
        border: none;
        cursor: pointer;
        transition: background-color 0.3s;
    }

    .form-group button:hover {
        background-color: #555; /* Slightly lighter black on hover */
    }

    .dropdown {
        width: 100%;
    }

    footer {
        font-size: 0.9em;
    }
</style>
</head>
<body>
<header>
    <h1>User Dashboard</h1>
</header>

<nav>
    <ul class="nav-links">
        <li><a href="http://localhost:8009/uamm/webapi/myresource/user">Check Resources of a User</a></li>
        <li><a href="http://localhost:8009/uamm/webapi/myresource/requestforresources">Request New Resource</a></li>
        <li><a href="http://localhost:8009/uamm/webapi/myresource/checkapprovals">Check Approval</a></li>
        <li><a href="http://localhost:8009/uamm/webapi/myresource/requestfor">Request for Manager/Admin</a></li>
        <li><a href="http://localhost:8009/uamm/webapi/myresource/removeownresource">Remove Own Resource</a></li>
        <li><a href="http://localhost:8009/uamm/webapi/myresource/changepassword">Change Password</a></li>
    </ul>
     <div>
        <button id="logout-button" class="button">Logout</button></div>
    </div>
</nav>
	<%
		String username=session.getAttribute("uname").toString();
	%>
     <div class="container">
       <h2 class="username">Welcome, <%=username%></h2>
        <div class="content">
        
            <!-- Content goes here -->
        </div>
    </div>
    <script>
  
  
        document.getElementById('logout-button').addEventListener('click', function() {
            // Optionally open the login page in a new tab
            window.close();
            window.open('index.jsp', '_blank');
            // Close the current window/tab
        });
        </script>
    
   
</body>
</html>
        
