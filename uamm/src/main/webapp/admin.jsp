<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 0;
        background: linear-gradient(to right, #000000, #ffffff); /* Black to white gradient */
        color: #333;
        display: flex;
        flex-direction: column;
        min-height: 100vh;
    }

    header {
        background-color: #ffffff; /* White background */
        color: #333;
        text-align: center;
        padding: 15px 0;
        border-bottom: 2px solid #e0e0e0; /* Light gray border */
        width: 100%;
    }

    .navbar {
        display: flex;
        flex-wrap: wrap; /* Allow wrapping to the next line */
        justify-content: center;
        background-color: #333; /* Dark background for navbar */
        color: #fff;
        padding: 10px 0;
        border-bottom: 2px solid #444; /* Slightly darker border */
    }

    .navbar ul {
        list-style: none;
        padding: 0;
        margin: 0;
        display: flex;
        flex-wrap: wrap; /* Allow wrapping of navbar items */
        justify-content: center;
    }

    .navbar ul li {
        margin: 5px 10px; /* Adjust margin for spacing */
    }

    .navbar ul li a {
        color: #fff;
        text-decoration: none;
        padding: 10px 15px;
        display: block;
        font-weight: bold;
        border-radius: 4px;
    }

    .navbar ul li a:hover {
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

    .title {
        text-align: center;
        font-size: 1.5em;
        margin-bottom: 20px;
        color: #000; /* Black text color */
    }

    .content {
        /* Ensure this is styled properly if needed */
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

    .small-button {
        width: auto;
        padding: 5px 10px;
        font-size: 0.9em;
    }

    .dropdown {
        width: 100%;
    }
    a {
        color: #007bff; /* Blue link color */
        text-decoration: none;
        font-weight: bold;
    }
    a:hover {
        text-decoration: underline;
    }

    footer {
        background-color: #ffffff; /* White background */
        color: #333;
        text-align: center;
        padding: 15px 0;
        border-top: 2px solid #e0e0e0; /* Light gray border */
        font-size: 0.9em;
        width: 100%;
    }
</style>
</head>
<body>
    <header>
        <h1>Admin Dashboard</h1>                     
    </header>   
    <div class="navbar">
        <ul class="links">
            <li><a href="http://localhost:8009/uamm/checkRequest.html">Check Requests</a></li>
            <li><a href="http://localhost:8009/uamm/webapi/myresource/AddResourceForm">Add Resource</a></li>
            <li><a href="http://localhost:8009/uamm/webapi/myresource/removeResourceFrom">Remove Resource From DB</a></li>
            <li><a href="http://localhost:8009/uamm/webapi/myresource/removeUserSelection">Remove Resource from User</a></li>
            <li><a href="http://localhost:8009/uamm/webapi/myresource/showResourceDropdown">Check Users for a Resource</a></li>
            <li><a href="http://localhost:8009/uamm/webapi/myresource/showUser">Check Resources for a User</a></li>         
            <li><a href="http://localhost:8009/uamm/webapi/myresource/addUserForm">Add User</a></li>
             <li><a href="http://localhost:8009/uamm/webapi/myresource/removeUserFromDB">Remove User</a></li>
             <li><a href="http://localhost:8009/uamm/webapi/myresource/changePassword">Change Password</a></li>           
        </ul>
        <div>
        <button id="logout-button" class="button">Logout</button></div>
    </div>
    <div class="container">
        <div class="title">Welcome Admin</div>
        
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
