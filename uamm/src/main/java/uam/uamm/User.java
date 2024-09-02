package uam.uamm;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
public class User {

    // Fields for registration
     String firstname;
     String lastname;
     String username;
     String email;
     String password;
     String confirmPassword;
     String usertype;
     Date dateOfJoining;
     String managerName;

     User(){
    	 
     }
    // Constructor for registration
    public User(String firstname, String lastname, String username,String email, String password, String confirmPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public User(String firstname, String lastname, String username,String managerName) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.managerName=managerName;
    }
    
    public User(String firstname, String lastname, String username) {
    	this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
    }
    
    public User(String username,String password) {
    	this.username = username;
    	this.password = password;
    }

    public boolean passMatch() {
    	return password != null && password.equals(confirmPassword);
	}
     
    //Username Generation
    public String generate_username() throws Exception {
        Connection c = db.connect();
        String username = firstname + "." + lastname;
        String s = "SELECT COUNT(*) FROM userdetails WHERE username LIKE ?";
        PreparedStatement pstmt = c.prepareStatement(s);
        pstmt.setString(1, username + "%");
        ResultSet rs = pstmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        if (count != 0) {
            username = username + count;
        }
        return username;
    }   
    
    //determine  the type of user
    public String TypeOfUser() throws Exception {
        Connection c = db.connect();
        String query = "SELECT COUNT(*) FROM userdetails";
        PreparedStatement pstmt = c.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            if (rs.getInt(1) == 0) {
                return "Admin";
            } else {
                return "User";
            }
        } else {
            return "";
        }
    }
    
    public String encrypt(String password) {
        // Define the character grid as a multi-line string
        String grid = "ABCDEFGHI\n" +
                      "JKLMNOPQR\n" +
                      "STUVWXYZa\n" +
                      "bcdefghij\n" +
                      "klmnopqrs\n" +
                      "tuvwxyz01\n" +
                      "23456789`\n" +
                      "~!@#$%^&*\n" +
                      "()-_=+[{]\n" +
                      "}|;:',<.>\n" +
                      "/?";

        // Split the grid string into individual rows
        String[] rows = grid.split("\n");

        // StringBuilder to build the encrypted password
        StringBuilder encryptedPassword = new StringBuilder();

        // Iterate over each character in the password
        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);

            // Search for the character in each row of the grid
            for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
                String row = rows[rowIndex];
                int columnIndex = row.indexOf(currentChar);

                if (columnIndex != -1) {
                    // Append the position in the format rowIndex+1-columnIndex+1
                    encryptedPassword.append(rowIndex + 1).append(columnIndex + 1);
                    break;
                }
            }
        }
        return encryptedPassword.toString();
    }
     
    
    
  //Password constraint
    public boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        boolean containsUppercase = false;
        boolean containsLowercase = false;
        boolean containsSpecialChar = false;
        boolean containsDigit = false;
     
        for (char character : password.toCharArray()) {
            if (Character.isUpperCase(character)) {
                containsUppercase = true;
            } else if (Character.isLowerCase(character)) {
                containsLowercase = true;
            } else if (Character.isDigit(character)) {
                containsDigit = true;
            } else if (!Character.isLetterOrDigit(character)) {
                containsSpecialChar = true;
            }
        }
     
        return containsUppercase && containsLowercase && containsSpecialChar && containsDigit;
    }


    //Password Validation
    public String validatePassword() {
        // Check if the password meets the constraints
        if (!isPasswordValid(password)) {
            return "Password does not meet the required constraints. It must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.";
        }

        // Check if the password and confirm password match
        if (password == null || !password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        return null; // Indicate that the password is valid
    }
    

    // inserting user details into the userdetails table.
    public String createUser() throws Exception {
        // Validate password first
        String passwordValidationMessage = validatePassword();
        if (passwordValidationMessage != null) {
            return passwordValidationMessage;
        }

        // Proceed with user creation
        Connection c = db.connect();
        String username = generate_username();
        String usertype = TypeOfUser();
        LocalDate date = LocalDate.now();
        String query = "INSERT INTO userdetails (firstname, lastname, email, password, username, dateOfjoining, usertype) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setString(1, firstname);
        ps.setString(2, lastname);
        ps.setString(3, email);
        ps.setString(4, encrypt(password));
        ps.setString(5, username);
        ps.setString(6, date.toString());
        ps.setString(7, usertype);
        ps.executeUpdate();
        return  username;
    }

    //verify the provided username and password match a record in the userdetails table. 
    public boolean validateUserCredentials(String username,String password) throws Exception {
		String query = "SELECT * FROM userdetails WHERE password = ?";
        Connection c=db.connect();
        User obj=new User();
        PreparedStatement pst = c.prepareStatement(query);
        String ans=obj.encrypt(password);
        pst.setString(1, ans);
        ResultSet rs = pst.executeQuery();
        String z="";
        boolean flag=false;
        while (rs.next()) {
        	z=rs.getString("username");
        	if(z.equals(username)) {
        		flag=true;
        		z=username;
        		break;
        	}
        }
        return flag;
    }
    

	//forgot password
	public String forgotPassword() throws Exception {
	    String s = "<form action='forgotpassword' method='post'>";
	    s += "<label for='username'>Username:</label>";
	    s += "<input type='text' id='username' name='username' required>";
	    s += "<br>";
	    s += "<br>";
	    s += "<label for='email'>Email:</label>";
	    s += "<input type='email' id='email' name='email' required>";
	    s += "<br>";
	    s += "<br>";
	    s += "<button type='submit' style='background-color: #000000; color: white;'>Submit</button>";
	    s += "</form>";
	    return s;
	}

	public boolean validateUserDetails(String username, String email) throws Exception {
	    Connection c = db.connect();
	    String query = "SELECT * FROM userdetails WHERE username = ? AND email = ?";
	    PreparedStatement pst = c.prepareStatement(query);
	    pst.setString(1, username);
	    pst.setString(2, email);
	    ResultSet rs = pst.executeQuery();
	    return rs.next(); // Returns true if a matching user is found
	}
	
	//change password for login when we click on forgot password
	public String ChangePasswordLogin() throws Exception {
	    String s = "<form action='ChangePassword1Login' method='post'>";
	    s += "<input type='password' name='newpassword' placeholder='Enter password to change' required " +
	         "pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}' " +
	         "title='Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.'>";
	    s += "<br>";
	    s += "<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
	    s += "<br>";
	    s += "<br>";
	    s += "<button type='submit' style='background-color: #000000; color: white;'>Submit</button>";
	    s += "</form>";
	    return s;    
	}

	public String ChangePassword1Login(String name, String p) throws Exception {
	    Connection c = db.connect();
	    String query = "UPDATE userdetails SET password=? WHERE username=?";
	    try (PreparedStatement pst = c.prepareStatement(query)) {
	        pst.setString(1, encrypt(p));
	        pst.setString(2, name);
	        pst.executeUpdate();
	    }
	    return "<h3>Password changed successfully.</h3>";    
	}

	
	
    
    //fetch pending requests from the database
    public String checkRequests() throws Exception {
        Connection c = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Connect to the database
            c = db.connect();

            // Prepare SQL query to select all pending requests
            String pending = "pending";
            String sql = "SELECT requestId, requestFrom, requestName, dor, approvalStatus FROM request WHERE approvalStatus = ?";
            pstmt = c.prepareStatement(sql);
            pstmt.setString(1, pending);
          

            // Execute the query
            rs = pstmt.executeQuery();

            // Build the HTML table with adjusted columns
            StringBuilder tableBuilder = new StringBuilder(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body {" +
                "    font-family: Arial, sans-serif;" +
                "    margin: 0;" +
                "    padding: 0;" +
                "    background-color: #f4f4f4;" +
                "}" +
                "table {" +
                "    width: 80%;" +   // Adjusted table width
                "    margin: 20px auto;" +
                "    border-collapse: collapse;" +
                "    background-color: #ffffff;" +
                "    box-shadow: 0 4px 8px rgba(0,0,0,0.1);" +
                "}" +
                "th, td {" +
                "    border: 1px solid #ddd;" +
                "    padding: 10px;" +   // Adjusted padding
                "    text-align: left;" +
                "}" +
                "th {" +
                "    background-color: #000000;" +
                "    color: white;" +
                "}" +
                "tr:nth-child(even) {" +
                "    background-color: #f2f2f2;" +
                "}" +
                "tr:hover {" +
                "    background-color: #e0e0e0;" +
                "}" +
                "form {" +
                "    display: inline;" +
                "}" +
                "input[type='submit'] {" +
                "    padding: 6px 10px;" +   // Adjusted button padding
                "    border: none;" +
                "    border-radius: 4px;" +
                "    color: white;" +
                "    cursor: pointer;" +
                "    font-size: 12px;" +   // Adjusted font size
                "}" +
                "input[type='submit'].approve {" +
                "    background-color: #4CAF50;" +
                "}" +
                "input[type='submit'].reject {" +
                "    background-color: #f44336;" +
                "}" +
                "input[type='submit']:hover {" +
                "    opacity: 0.8;" +
                "}" +
                "th:nth-child(1), td:nth-child(1) {" +
                "    width: 10%;" +   // Adjusted width for columns
                "}" +
                "th:nth-child(2), td:nth-child(2) {" +
                "    width: 15%;" +
                "}" +
                "th:nth-child(3), td:nth-child(3) {" +
                "    width: 20%;" +
                "}" +
                "th:nth-child(4), td:nth-child(4) {" +
                "    width: 20%;" +
                "}" +
                "th:nth-child(5), td:nth-child(5) {" +
                "    width: 15%;" +
                "}" +
                "th:nth-child(6), td:nth-child(6) {" +
                "    width: 20%;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<table>" +
                "<tr>" +
                "<th>Request ID</th>" +
                "<th>Request From</th>" +
                "<th>Request Name</th>" +
                "<th>Date of Request</th>" +
                "<th>Approval Status</th>" +
                "<th>Action</th>" +
                "</tr>"
            );

            // Append rows to the table
            while (rs.next()) {
                String requestId = rs.getString("requestId");
                String requestFrom = rs.getString("requestFrom");
                String requestName = rs.getString("requestName");
                String dor = rs.getString("dor");
                String approvalStatus = rs.getString("approvalStatus");  

                tableBuilder.append("<tr>")
                    .append("<td>").append(requestId).append("</td>")
                    .append("<td>").append(requestFrom).append("</td>")
                    .append("<td>").append(requestName).append("</td>")
                    .append("<td>").append(dor).append("</td>")
                    .append("<td>").append(approvalStatus).append("</td>")
                    .append("<td>")
                    .append("<form action='aprroverequest' method='post'>")
                    .append("<input type='hidden' name='requestId' value='").append(requestId).append("'/>")
                    .append("<input type='hidden' name='requestFrom' value='").append(requestFrom).append("'/>")
                    .append("<input type='hidden' name='requestName' value='").append(requestName).append("'/>")
                    .append("<input type='hidden' name='dor' value='").append(dor).append("'/>")
                    .append("<input type='hidden' name='approvalStatus' value='").append(approvalStatus).append("'/>")
                    .append("<input type='submit' class='approve' value='Approve'/>")
                    .append("</form>")
                    .append("<form action='rejectrequest' method='post'>")
                    .append("<input type='hidden' name='requestId' value='").append(requestId).append("'/>")
                    .append("<input type='hidden' name='requestFrom' value='").append(requestFrom).append("'/>")
                    .append("<input type='hidden' name='requestName' value='").append(requestName).append("'/>")
                    .append("<input type='hidden' name='dor' value='").append(dor).append("'/>")
                    .append("<input type='hidden' name='approvalStatus' value='").append(approvalStatus).append("'/>") 
                    .append("<input type='submit' class='reject' value='Reject'/>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }

            // Close the table and HTML tags
            tableBuilder.append(
                "</table>" +
                "</body>" +
                "</html>"
            );

            // Return the HTML table as a string
            return tableBuilder.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while retrieving pending requests", e);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (c != null) c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
 //  handle the approval of requests
    
    public String approveRequests(String requestId, String requestFrom, String requestName, String dor, String approvalStatus, String approverName) throws Exception {
        Connection c = db.connect();
        c.setAutoCommit(false); // Begin transaction

        String updateRequestSql = "UPDATE request SET approvalStatus = ? WHERE requestId = ? AND approvalStatus = 'pending'";
        PreparedStatement pstmt = c.prepareStatement(updateRequestSql);
        pstmt.setString(1, "approved");
        pstmt.setString(2, requestId);

        int rowsAffected = pstmt.executeUpdate();
        
        if (rowsAffected == 0) {
            c.rollback(); // Rollback transaction if no rows affected
            c.close();
            return "No matching request found or request was already approved.";
        }

        if (requestName.equals("Manager") || requestName.equals("Admin") || requestName.equals("user")) {
            // Update userdetails table with new role and set managerName to approverName
            String updateUserSql = "UPDATE userdetails SET usertype = ?, managerName = ? WHERE username = ?";
            try (PreparedStatement pstmt1 = c.prepareStatement(updateUserSql)) {
                pstmt1.setString(1, requestName);
                pstmt1.setString(2, approverName); // Set the approver as manager
                pstmt1.setString(3, requestFrom);
                pstmt1.executeUpdate();
            }
        }

        // Insert into userResource table if the request is not for a role change
        if (!requestName.equals("Manager") && !requestName.equals("Admin") && !requestName.equals("user")) {
            String insertResourceSql = "INSERT INTO userResource (resourceName, username) VALUES (?, ?)";
            try (PreparedStatement pstmt1 = c.prepareStatement(insertResourceSql)) {
                pstmt1.setString(1, requestName);
                pstmt1.setString(2, requestFrom);
                pstmt1.executeUpdate();
            }
        }

        c.commit(); // Commit transaction
        c.close();
        return checkRequests() + "Request approved successfully";
    }

    
 //handles rejecting a request
    public String rejectRequests(String requestId) throws Exception {
        String sql = "UPDATE request SET approvalStatus = ? WHERE requestId = ? AND approvalStatus = 'pending'";

        try (Connection c = db.connect();
             PreparedStatement pstmt = c.prepareStatement(sql)) {

            pstmt.setString(1, "rejected");
            pstmt.setString(2, requestId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                return "Request not found, or it has already been approved.";
            }

            return checkRequests()+"Request updated successfully";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while updating the request status.", e);
        }
    }
     
     
     //add resource
    
    public String AddResourceForm() throws Exception {
        String form = "<div class='container'>" +
                          "<h2>Add Resource</h2>" +
                          "<form action='processResourceAddition' method='POST'>" +
                              "<div class='form-group'>" +
                                  "<label for='newResource'>Resource Name:</label>" +
                                  "<input type='text' id='newResource' name='resourceName' placeholder='Enter Resource Name'>" +
                              "</div>" +
                              "<button type='submit'>Submit</button>" +
                          "</form>" +
                        "</div>";

        return form;
    }

    public String processResourceAddition(String resourceName) throws Exception {
        Connection c = db.connect();
        String checkQuery = "SELECT COUNT(*) FROM resources WHERE resourceName = ?";
        String insertQuery = "INSERT INTO resources (resourceName) VALUES (?)";

        try (PreparedStatement checkPst = c.prepareStatement(checkQuery)) {
            checkPst.setString(1, resourceName);
            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return AddResourceForm() + "Resource already exists";
                }
            }

            try (PreparedStatement insertPst = c.prepareStatement(insertQuery)) {
                insertPst.setString(1, resourceName);
                insertPst.executeUpdate();
                return AddResourceForm() + "<br>Resource added successfully";
            }
        }
    }

     
     //remove resource from db
     public String generateResourceRemovalForm() throws Exception {
    	    String dropDown = "<form action='processResourceRemoval' method='post'>" +
    	                      "<label for='dropdown'>Select resource to delete:</label>" +
    	                      "<select id='dropdown' name='resourceName'>" +
    	                      "<option value='' disabled selected>Select a resource</option>";
    	    
    	    Connection c = db.connect();
    	    String query1 = "SELECT * FROM resources";
    	    PreparedStatement pst1 = c.prepareStatement(query1);
    	    ResultSet rs = pst1.executeQuery();
    	    
    	    boolean flag=false;
    	    while (rs.next()) {
    	        String value = rs.getString(1);
    	        dropDown += "<option value='" + value + "'>" + value + "</option>";
    	        flag=true;
    	    }
    	    
    	    dropDown += "</select>";
    	    dropDown += "<button type='submit'>Submit</button>";
    	    dropDown += "</form>";
    	    
    	    return dropDown;
    	}

     public String removeResource(String resourceName) throws Exception {
    	    Connection c = db.connect();
    	    c.setAutoCommit(false); // Start transaction

    	    // Delete from resources table
    	    String query1 = "DELETE FROM resources WHERE resourceName=?";
    	    PreparedStatement pst1 = c.prepareStatement(query1);
    	    pst1.setString(1, resourceName);
    	    int rowsAffected1 = pst1.executeUpdate();

    	    // Delete from request table
    	    String query2 = "DELETE FROM request WHERE requestName=?";
    	    PreparedStatement pst2 = c.prepareStatement(query2);
    	    pst2.setString(1, resourceName);
    	    int rowsAffected2 = pst2.executeUpdate();

    	    // Delete from userResource table
    	    String query3 = "DELETE FROM userResource WHERE resourceName=?";
    	    PreparedStatement pst3 = c.prepareStatement(query3); // Use query3 for userResource table
    	    pst3.setString(1, resourceName);
    	    int rowsAffected3 = pst3.executeUpdate();

    	    // Commit transaction
    	    c.commit();

    	    // Close resources
    	    pst1.close();
    	    pst2.close();
    	    pst3.close();
    	    c.close();

    	    if (rowsAffected1 > 0 || rowsAffected2 > 0 || rowsAffected3 > 0) {
    	        return "<h3>Resource deleted successfully</h3>";
    	    } else {
    	        return "The specified resource name does not exist.";
    	    }
    	}

    	
    	//remove resource from user
    	public String generateUserSelectionForm() throws Exception {
    	    Connection c = db.connect();
    	    String query = "SELECT * FROM userdetails"; 
    	    PreparedStatement pst = c.prepareStatement(query);
    	    ResultSet rs = pst.executeQuery();
    	    String dropDown = "<form action='processUserSelection' method='post'>" +
    	                      "<label for='dropdown' placeholder='Select one'>Select A User:</label>" +
    	                      "<select id='dropdown' name='selectedUser'>" +
    	                      "<option value='' disabled selected>Select a user</option>";
    	    boolean hasUsers = false;
    	    while (rs.next()) {
    	        String value = rs.getString("username");
    	        dropDown += "<option value='" + value + "'>" + value + "</option>";
    	        hasUsers = true;
    	    }
    	    if (!hasUsers) {
    	        dropDown += "<option value='' disabled selection>No Users Available</option>";
    	    }
    	    dropDown += "</select>";    
    	    dropDown += "<button type='submit'>View Resources</button>";
    	    dropDown += "</form>";
    	    
    	    return dropDown.toString();
    	}
    	
    	public String generateResourceSelectionForm(String selectedUser) throws Exception {
    	    Connection c = db.connect();
    	    String query = "SELECT resourceName FROM userResource WHERE username=?";
    	    PreparedStatement pst = c.prepareStatement(query);
    	    pst.setString(1, selectedUser);
    	    ResultSet rs = pst.executeQuery();
    	    String dropDown = "<form action='processResourceDeletion' method='post'>" +
    	                      "<label for='dropdown' placeholder='Select one'>Select A Resource To Delete:</label>" +
    	                      "<select id='dropdown' name='selectedResource'>" +
    	                      "<option value='' disabled selected>Select a resource</option>";
    	    boolean hasResource = false;
    	    while (rs.next()) {
    	        String value = rs.getString("resourceName");
    	        dropDown += "<option value='" + value + "'>" + value + "</option>";
    	        hasResource = true;
    	    }
    	    if (!hasResource) {
    	        dropDown += "<option value='' disabled selected>No Resources Available</option>";
    	    }
    	    dropDown += "</select>";    
    	    dropDown += "<button type='submit'>Delete Resource</button>";
    	    dropDown += "</form>";
    	    return dropDown;
    	}
    	
    	public String deleteResourceFromUser(String selectedResource) throws Exception {
    	    Connection c = db.connect();
    	    String query = "DELETE FROM userResource WHERE resourceName=?";
    	    PreparedStatement pst = c.prepareStatement(query);
    	    pst.setString(1, selectedResource);
    	    int rowsAffected = pst.executeUpdate();
    	    if (rowsAffected > 0) {
    	        return "<h3>Resource deleted successfully</h3>";
    	    } else {
    	        return "<h3>Failed to delete the resource</h3>";
    	    }
    	}
    	
    	
    	//check users for resource
    	public String generateResourceDropdown() throws Exception {
            Connection c = db.connect();
            String query = "SELECT resourceName FROM resources";
            PreparedStatement pst = c.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            String dropDown = "<form action='processResourceSelection' method='post'>" +
                              "<label for='dropdown' placeholder='Select one'>Select A Resource To Check For Users:</label>" +
                              "<select id='dropdown' name='selectedResource'>" +
                              "<option value='' disabled selected>Select a resource</option>";
            boolean hasResource = false;
            while (rs.next()) {
                hasResource = true;
                String value = rs.getString("resourceName");
                dropDown += "<option value='" + value + "'>" + value + "</option>";
            }
            if (!hasResource) {
                dropDown += "<option value=''></option>";
            }
            dropDown += "</select>";    
            dropDown += "<button type='submit' style='background-color: #000000; color: white;'>Find Users</button>";
            dropDown += "</form>";
            return dropDown;
        }
        
        public String getUsersForResource(String resourceName) throws Exception {
            Connection c = db.connect();
            String query1 = "SELECT username FROM userResource WHERE resourceName=?";
            PreparedStatement pst1 = c.prepareStatement(query1);
            pst1.setString(1, resourceName); // Bind the selected resourceName
            ResultSet rs = pst1.executeQuery();
            
            boolean hasUsers = false;
            String result = "<table border='1'><tr><th>Username</th></tr>";
            
            while (rs.next()) {
                hasUsers = true;
                result += "<tr>";
                result += "<td>" + rs.getString("username") + "</td>";
                result += "</tr>";
            }
            
            if (!hasUsers) {
                return "<h3>No Users Available</h3>";
            }
            
            result += "</table>";
            return result;
        }
        
        
        //check resource of a user
        public String checkResourceForUser() throws Exception {
            Connection c = db.connect();
            String query = "SELECT * FROM userdetails";
            PreparedStatement pst = c.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            String dropDown = "<form action='selectUser' method='post'>" +
                              "<label for='dropdown' placeholder='Select one'>Select A User To Check His Resources:</label>" +
                              "<select id='dropdown' name='selectedUser'>";
            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                String value = rs.getString("userName");
                dropDown += "<option value='" + value + "'>" + value + "</option>";
            }
            if (!hasUsers) {
                dropDown += "<option value=''>No Users Available</option>";
            }
            dropDown += "</select>";    
            dropDown += "<button type='submit' style='background-color: #000000; color: white;'>Check For Resources</button>";
            dropDown += "</form>";
            return dropDown;
        }

        public String checkResourceForUser1(String selectedUser) throws Exception {
            Connection c = db.connect();
            String query1 = "SELECT resourceName FROM userResource WHERE username=?";
            PreparedStatement pst1 = c.prepareStatement(query1);
            pst1.setString(1, selectedUser);
            ResultSet rs = pst1.executeQuery();
            String show = "<table border='1'><tr><th>Resource Names</th></tr>";
            while (rs.next()) {
                show += "<tr>";
                show += "<td>" + rs.getString("resourceName") + "</td>";
                show += "</tr>";
            }
            show += "</table>";
            return show;
        }

        
     //add user     
     public String addUsersinfo() throws Exception {
    	    String form = "<div class='container'>" +
    	                 "<h2>Add Users</h2>" +
    	                 "<form action='addUser' method='post'>" +
    	                     "<label for='firstname'>First name:</label>" +
    	                     "<input type='text' id='firstname' name='firstname' required>" +
    	                     "<label for='lastname'>Last name:</label>" +
    	                     "<input type='text' id='lastname' name='lastname' required>" +
    	                     "<label for='email'>Email:</label>" +
    	                     "<input type='email' id='email' name='email' required>" +
    	                     "<input type='submit' value='SUBMIT'>" +
    	                 "</form>" +
    	                 "</div>";

    	    return form;
    	}
     
     public String addUser() throws Exception {
    	    Connection c = db.connect();
    	    String password = firstname + lastname; // Automatically generate the password
    	    String username = generate_username();
    	    String usertype = TypeOfUser();
    	    LocalDate date = LocalDate.now();

    	    String query = "INSERT INTO userdetails (firstname, lastname, email, password, username, dateOfjoining, usertype) VALUES (?, ?, ?, ?, ?, ?, ?)";
    	    PreparedStatement ps = c.prepareStatement(query);
    	    ps.setString(1, firstname);
    	    ps.setString(2, lastname);
    	    ps.setString(3, email);
    	    ps.setString(4, encrypt(password)); // Encrypt the generated password
    	    ps.setString(5, username);
    	    ps.setString(6, date.toString());
    	    ps.setString(7, usertype);
    	    ps.executeUpdate();

    	    return username;
    	}

  
     //remove user from db 
     
     public String listUsersForRemoval() throws Exception {
    	    String dropDown = "<form action='removeSelectedUser' method='post'>" +
    	                      "<label for='dropdown'>Select username to delete:</label>" +
    	                      "<select id='dropdown' name='username'>" +
    	                      "<option value='' disabled selected>Select a user</option>";

    	    Connection c = db.connect();
    	    String query1 = "SELECT username FROM userdetails";
    	    PreparedStatement pst1 = c.prepareStatement(query1);
    	    ResultSet rs = pst1.executeQuery();

    	    while (rs.next()) {
    	        String value = rs.getString("username");
    	        dropDown += "<option value='" + value + "'>" + value + "</option>";
    	    }

    	    dropDown += "</select>";
    	    dropDown += "<button type='submit'>Submit</button>";
    	    dropDown += "</form>";

    	    return dropDown;
    	}
     
     public String removeSelectedUser(String username) throws Exception {
    	    Connection c = db.connect();
    	    try {
    	        // Start a transaction
    	        c.setAutoCommit(false);

    	        // Delete from userdetails table
    	        String deleteUserQuery = "DELETE FROM userdetails WHERE username=?";
    	        try (PreparedStatement pst = c.prepareStatement(deleteUserQuery)) {
    	            pst.setString(1, username);
    	            int userRowsAffected = pst.executeUpdate();

    	            if (userRowsAffected == 0) {
    	                // User not found
    	                c.rollback();
    	                return "No user found with the given username.";
    	            }
    	        }

    	        // Delete from userResource table
    	        String deleteUserResourceQuery = "DELETE FROM userResource WHERE username=?";
    	        try (PreparedStatement pst = c.prepareStatement(deleteUserResourceQuery)) {
    	            pst.setString(1, username);
    	            pst.executeUpdate();
    	        }

    	        // Delete from request table
    	        String deleteRequestQuery = "DELETE FROM request WHERE requestFrom=?";
    	        try (PreparedStatement pst = c.prepareStatement(deleteRequestQuery)) {
    	            pst.setString(1, username);
    	            pst.executeUpdate();
    	        }

    	        // Commit the transaction
    	        c.commit();

    	        return "<h3>User and associated data have been removed successfully.</h3>";
    	    } catch (Exception e) {
    	        // Rollback the transaction in case of any error
    	        c.rollback();
    	        throw e; // Re-throw the exception after rollback
    	    } finally {
    	        // Restore auto-commit mode
    	        c.setAutoCommit(true);
    	        // Close the connection
    	        c.close();
    	    }
    	}


     
    	//change password
     public String changePassword() throws Exception {
    	    String s = "<form action='changePassword1' method='post'>";
    	    s += "<input type='password' name='newpassword' placeholder='Enter password to change' required " +
    	         "pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}' " +
    	         "title='Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.'>";
    	    s += "<br>";
    	    s += "<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
    	    s += "<br>";
    	    s += "<br>";
    	    s += "<button type='submit' style='background-color: #000000; color: white;'>Submit</button>";
    	    s += "</form>";
    	    return s;    
    	}

    	public String changePassword1(String name, String p) throws Exception {
    	    Connection c = db.connect();
    	    String query = "UPDATE userdetails SET password=? WHERE username=?";
    	    try (PreparedStatement pst = c.prepareStatement(query)) {
    	        pst.setString(1, encrypt(p));
    	        pst.setString(2, name);
    	        pst.executeUpdate();
    	    }
    	    return "<h3>Password changed successfully.</h3>";    
    	}

    	
   
	//**************************************************User*****************************************************
	
	//check resources of a user
	
	public String checkresources(String loggedInUsername) throws Exception {
	    // Check if username is not null
	    if (loggedInUsername == null || loggedInUsername.isEmpty()) {
	        return "<h3>No user is logged in.</h3>";
	    }

	    Connection c = db.connect();
	    String query1 = "SELECT resourceName FROM userResource WHERE username=?";
	    PreparedStatement pst1 = c.prepareStatement(query1);
	    pst1.setString(1, loggedInUsername);
	    ResultSet rs = pst1.executeQuery();

	    String show = "<table border='1'><tr><th>Resource Names</th></tr>";
	    boolean hasResources = false;
	    while (rs.next()) {
	        hasResources = true;
	        show += "<tr>";
	        show += "<td>" + rs.getString("resourceName") + "</td>";
	        show += "</tr>";
	    }
	    if (!hasResources) {
	        show += "<tr><td>No Resources Available</td></tr>";
	    }
	    show += "</table>";

	    return show;
	}
	

	//request for a new resources
    public String requestforresources(String name) throws Exception {
	    Connection c = db.connect();
	    String query1 = "select resourceName from userresource where username=?";
	    PreparedStatement pst1 = c.prepareStatement(query1);
	    pst1.setString(1, name);
	    ResultSet rs1= pst1.executeQuery();
	    HashSet<String> hs = new HashSet<>();
	    while (rs1.next()) {
	        hs.add(rs1.getString(1));
	    }
	    String query2 = "select requestName from request where requestFrom=? and approvalStatus='pending'";
	    PreparedStatement pst2 = c.prepareStatement(query2);
	    pst2.setString(1, name);
	    ResultSet rs2= pst2.executeQuery();
	    HashSet<String> hs2 = new HashSet<>();
	    while (rs2.next()) {
	        hs2.add(rs2.getString(1));
	    }
	    String query3 = "select resourceName from resources";
	    PreparedStatement pst3 = c.prepareStatement(query3);
	    ResultSet rs3 = pst3.executeQuery();
	    StringBuilder dropDown = new StringBuilder("<form action='requestforresources1' method='post'>");
	    dropDown.append("<label for='dropdown' placeholder='Select one'>Select resource name to request:</label>");
	    dropDown.append("<select id='dropdown' name='options'>"+
	    		"<option value='' disabled selected>Select a resource</option>");
	    boolean hasAvailableResources = false;
	    while (rs3.next()) {
	        String resourceName = rs3.getString(1);
	        if (!hs.contains(resourceName) && !hs2.contains(resourceName)) {
	            dropDown.append("<option value='").append(resourceName).append("'>").append(resourceName).append("</option>");
	            hasAvailableResources = true;
	        }
	    }
	    if (!hasAvailableResources) {
	        dropDown.append("<option value='' disabled selected>No resources available</option>");
	    }
	    
	    dropDown.append("</select>");
	    dropDown.append("<button type='submit' style='background-color: #000000; color: white;'>Submit</button>");
	    dropDown.append("</form>");
	    return dropDown.toString();
	}
    
    
    // Request a resource
    public String requestforresource1(String options, String username) throws Exception {
        Connection c = db.connect();
        
        // Generate next request ID
        String query1 = "SELECT MAX(requestId) FROM request";
        PreparedStatement pst1 = c.prepareStatement(query1);
        ResultSet rs1 = pst1.executeQuery();
        int nextRequestId = 1; // Default if no requests exist
        if (rs1.next()) {
            nextRequestId = rs1.getInt(1) + 1;
        }

        // Insert new request
        String query2 = "INSERT INTO request (requestId, requestFrom, dor, approvalStatus, requestName) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst2 = c.prepareStatement(query2);
        pst2.setInt(1, nextRequestId);
        pst2.setString(2, username);
        pst2.setString(3, LocalDate.now().toString());
        pst2.setString(4, "pending");
        pst2.setString(5, options);
        pst2.executeUpdate();
        
        return "Resource is requested";
    }
    
    //check approvals
    public String checkApprovedRequests(String username) throws Exception {
        Connection c = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Connect to the database
            c = db.connect();

            // Prepare SQL query to select all requests (approved, rejected, pending) for the given user
            String sql = "SELECT requestId, requestFrom, requestName, dor, approvalStatus " +
                         "FROM request WHERE requestFrom = ?";
            pstmt = c.prepareStatement(sql);
            pstmt.setString(1, username);

            // Execute the query
            rs = pstmt.executeQuery();

            // Build the HTML table with adjusted columns
            StringBuilder tableBuilder = new StringBuilder(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body {" +
                "    font-family: Arial, sans-serif;" +
                "    margin: 0;" +
                "    padding: 0;" +
                "    background-color: #f4f4f4;" +
                "}" +
                "table {" +
                "    width: 80%;" +
                "    margin: 20px auto;" +
                "    border-collapse: collapse;" +
                "    background-color: #ffffff;" +
                "    box-shadow: 0 4px 8px rgba(0,0,0,0.1);" +
                "}" +
                "th, td {" +
                "    border: 1px solid #ddd;" +
                "    padding: 10px;" +
                "    text-align: left;" +
                "}" +
                "th {" +
                "    background-color: #000000;" +
                "    color: white;" +
                "}" +
                "tr:nth-child(even) {" +
                "    background-color: #f2f2f2;" +
                "}" +
                "tr:hover {" +
                "    background-color: #e0e0e0;" +
                "}" +
                "th:nth-child(1), td:nth-child(1) {" +
                "    width: 10%;" +
                "}" +
                "th:nth-child(2), td:nth-child(2) {" +
                "    width: 15%;" +
                "}" +
                "th:nth-child(3), td:nth-child(3) {" +
                "    width: 20%;" +
                "}" +
                "th:nth-child(4), td:nth-child(4) {" +
                "    width: 20%;" +
                "}" +
                "th:nth-child(5), td:nth-child(5) {" +
                "    width: 15%;" +
                "}" +
                "th:nth-child(6), td:nth-child(6) {" +
                "    width: 20%;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<table>" +
                "<tr>" +
                "<th>Request ID</th>" +
                "<th>Request From</th>" +
                "<th>Request Name</th>" +
                "<th>Date of Request</th>" +
                "<th>Approval Status</th>" +
                "</tr>"
            );

            // Append rows to the table for all requests
            while (rs.next()) {
                String requestId = rs.getString("requestId");
                String requestFrom = rs.getString("requestFrom");
                String requestName = rs.getString("requestName");
                String dor = rs.getString("dor");
                String approvalStatus = rs.getString("approvalStatus");

                tableBuilder.append("<tr>")
                    .append("<td>").append(requestId).append("</td>")
                    .append("<td>").append(requestFrom).append("</td>")
                    .append("<td>").append(requestName).append("</td>")
                    .append("<td>").append(dor).append("</td>")
                    .append("<td>").append(approvalStatus).append("</td>")
                    .append("</tr>");
            }

            // Close the table and HTML tags
            tableBuilder.append(
                "</table>" +
                "</body>" +
                "</html>"
            );

            // Return the HTML table as a string
            return tableBuilder.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while retrieving requests", e);
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (c != null) c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

  //request Admin/manager 
    public String requestfor(String uname) throws Exception {
        Connection c = db.connect();
        String queryAdmin = "SELECT 1 FROM request WHERE requestFrom=? AND requestName='Admin'";
        String queryManager = "SELECT 1 FROM request WHERE requestFrom=? AND requestName='Manager'";

        try (PreparedStatement pstAdmin = c.prepareStatement(queryAdmin);
             PreparedStatement pstManager = c.prepareStatement(queryManager)) {

            pstAdmin.setString(1, uname);
            ResultSet rsAdmin = pstAdmin.executeQuery();

            pstManager.setString(1, uname);
            ResultSet rsManager = pstManager.executeQuery();

            StringBuilder dropDown = new StringBuilder("<form action='requestfor1' method='post'>");
  	        dropDown.append("<label for='dropdown'>Select role to request:</label>");
  	        dropDown.append("<select id='dropdown' name='options'>"+
  	        		"<option value='' disabled selected>Select a role</option>");

            boolean adminRequested = rsAdmin.next();
            boolean managerRequested = rsManager.next();

            if (adminRequested && managerRequested) {
                dropDown.append("<option value='' disabled selected>No roles available</option>");
            } else if (!adminRequested && !managerRequested) {
                dropDown.append("<option value='Admin'>Admin</option>");
                dropDown.append("<option value='Manager'>Manager</option>");
            } else if (adminRequested) {
                dropDown.append("<option value='Manager'>Manager</option>");
            } else if (managerRequested) {
                dropDown.append("<option value='Admin'>Admin</option>");
            }

            dropDown.append("</select>");
            dropDown.append("<button type='submit' style='background-color: #000000; color: white;'>Submit</button>");
            dropDown.append("</form>");

            return dropDown.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while generating request form", e);
        }
    }

  	
  	public String requestfor1(String options, String uname) throws Exception {
  	    if (options == null || options.isEmpty()) {
  	        // Return an error message if no role was selected
  	        return "No role selected. Please <a href='http://localhost:8009/uamm/webapi/myresource/requestfor'>go back</a> and select a role to request.";
  	    }

  	    Connection c = db.connect();
  	    String queryCount = "SELECT COUNT(*) FROM request";
  	    String queryInsert = "INSERT INTO request(requestId, requestFrom, dor, approvalStatus, requestName) VALUES (?, ?, ?, ?, ?)";
  	    String queryCheckExistingRequest = "SELECT * FROM request WHERE requestFrom=? AND requestName=?";
  	    
  	    try (PreparedStatement pstCount = c.prepareStatement(queryCount);
  	         ResultSet rsCount = pstCount.executeQuery()) {
  	         
  	        int count = 0;
  	        if (rsCount.next()) {
  	            count = rsCount.getInt(1);
  	        }
  	        
  	        // Check if there is already a pending request for the same role
  	        try (PreparedStatement pstCheckExistingRequest = c.prepareStatement(queryCheckExistingRequest)) {
  	            pstCheckExistingRequest.setString(1, uname);
  	            pstCheckExistingRequest.setString(2, options);
  	            ResultSet rsExistingRequest = pstCheckExistingRequest.executeQuery();
  	            
  	            // Insert a new request only if no existing request found
  	            if (!rsExistingRequest.next()) {
  	                try (PreparedStatement pstInsert = c.prepareStatement(queryInsert)) {
  	                    pstInsert.setInt(1, count + 1);
  	                    pstInsert.setString(2, uname);
  	                    pstInsert.setString(3, LocalDate.now().toString());
  	                    pstInsert.setString(4, "pending");
  	                    pstInsert.setString(5, options);
  	                    pstInsert.executeUpdate();
  	                }
  	            }
  	        }
  	        
  	        return "Role request submitted successfully";
  	    } catch (SQLException e) {
  	        e.printStackTrace();
  	        throw new Exception("Database error occurred while processing the request", e);
  	    }
  	}


  	
  	//remove own resources
	
  	public String removeownresource(String uname) throws Exception {
  		Connection c=db.connect();
  		String query="select resourceName from userResource where username=?";
  		PreparedStatement pst=c.prepareStatement(query);
  		pst.setString(1, uname);
  		ResultSet rs=pst.executeQuery();
  		String dropDown="<form action='removeresource1' method='post'>"+"<label for='dropdown' placeholder='Select one'>Select resourcename to delete:</label>"+
  				"<select id='dropdown' name='options'>"+
  			    "<option value='' disabled selected>Select a resource</option>";
  		boolean flag=false;
  		while(rs.next()) {
  			String value=rs.getString(1);
          	dropDown+="<option value='"+value+"'>"+value+"</option>";
          	flag=true;
  		}
  		if(!flag) {
  			dropDown+="<option value=''disabled selected>No options available</option>";
  		}
  		dropDown+="</select>";	
  		dropDown+="<button type='submit' style='background-color: #000000; color: white;'>Submit</button>";
          dropDown+="</form>";
  		return dropDown;
  	}
  	
  	public String removeresource1(String option) throws Exception {
  		Connection c=db.connect();
  		String query="delete from userResource where resourceName=?";
  		PreparedStatement pst=c.prepareStatement(query);
  		pst.setString(1, option);
  		pst.executeUpdate();
  		return "Resource is removed successfully";
  	}
  	
  	
  //change password
	
  	public String changepassword() throws Exception {
  	    String s = "<form action='changepassword1' method='post'>";
  	    s += "<input type='password' name='newpassword' placeholder='Enter password to change' required " +
  	         "pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}' " +
  	         "title='Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.'>";
  	    s += "<br>";
  	    s += "<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
  	    s += "<br>";
  	    s += "<br>";
  	    s += "<button type='submit' style='background-color: #000000; color: white;'>Submit</button>";
  	    s += "</form>";
  	    return s;    
  	}

  	
  	public String changepassword1(String name,String p) throws Exception {
  		Connection c=db.connect();
  		String query="update userdetails set password=? where username=?";
  		PreparedStatement pst=c.prepareStatement(query);
  		pst.setString(1, encrypt(p));
  		pst.setString(2, name);
  		pst.executeUpdate();
  		return "<h3>Password changed successfully.</h3S>";	
  	}
  	
    
  //******************************************manager**********************************
	
  	
  	
 // Show team
    public String showteam(String name) throws Exception {
        Connection c = db.connect();
        String query = "SELECT username, firstname, lastname, email, dateOfjoining FROM userdetails WHERE managerName=?";
        PreparedStatement pst = c.prepareStatement(query);
        pst.setString(1, name);
        ResultSet rs = pst.executeQuery();

        StringBuilder show = new StringBuilder("<table border='1'>");
        show.append("<tr><th>Username</th><th>First Name</th><th>Last Name</th><th>Email</th><th>Date of Joining</th></tr>");

        while (rs.next()) {
            show.append("<tr>");
            show.append("<td>").append(rs.getString("username")).append("</td>");
            show.append("<td>").append(rs.getString("firstname")).append("</td>");
            show.append("<td>").append(rs.getString("lastname")).append("</td>");
            show.append("<td>").append(rs.getString("email")).append("</td>");
            show.append("<td>").append(rs.getString("dateOfjoining")).append("</td>");
            show.append("</tr>");
        }

        show.append("</table>");
        return show.toString();
    }

    // Get a team
    public String getateammember() throws Exception {
        Connection c = db.connect();
        String query = "SELECT username FROM userdetails WHERE usertype=? AND managerName IS NULL";
        PreparedStatement pst = c.prepareStatement(query);
        pst.setString(1, "User");
        ResultSet rs = pst.executeQuery();

        StringBuilder dropDown = new StringBuilder("<form action='getateammember1' method='post'>");
        dropDown.append("<label for='dropdown'>Select A User To Add Into Your Team</label>");
        dropDown.append("<select id='dropdown' name='options'>");
        dropDown.append("<option value='' disabled selected>Select a user</option>");

        boolean hasOptions = false;
        while (rs.next()) {
            String value = rs.getString("username");
            dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>");
            hasOptions = true;
        }

        if (!hasOptions) {
            dropDown.append("<option value='' disabled selected>No Users Available</option>");
        }

        dropDown.append("</select>");
        dropDown.append("<button type='submit' style='background-color: #000000; color: white;'>Add To Team</button>");
        dropDown.append("</form>");

        return dropDown.toString();
    }

    // Add team member
    public String getateammember1(String options, String name) throws Exception {
        Connection c = db.connect();
        String query = "UPDATE userdetails SET managerName=? WHERE username=?";
        PreparedStatement pst = c.prepareStatement(query);
        pst.setString(1, name);
        pst.setString(2, options);
        pst.executeUpdate();
        return "<h3>User has been successfully added to your team</h3>";
    }
    
   
	//check resource in manager
	
	public String checkManagerResources(String loggedInUsername) throws Exception {
	    // Check if username is not null
	    if (loggedInUsername == null || loggedInUsername.isEmpty()) {
	        return "<h3>No user is logged in.</h3>";
	    }

	    Connection c = db.connect();
	    // Update query to exclude 'Admin' and 'Manager'
	    String query1 = "SELECT resourceName FROM userResource WHERE username=? AND resourceName NOT IN ('Admin', 'Manager')";
	    
	    try (PreparedStatement pst1 = c.prepareStatement(query1)) {
	        pst1.setString(1, loggedInUsername);
	        ResultSet rs = pst1.executeQuery();

	        StringBuilder show = new StringBuilder("<table border='1'><tr><th>Resource Names</th></tr>");
	        boolean hasResources = false;
	        while (rs.next()) {
	            hasResources = true;
	            show.append("<tr>");
	            show.append("<td>").append(rs.getString("resourceName")).append("</td>");
	            show.append("</tr>");
	        }
	        if (!hasResources) {
	            show.append("<tr><td>No Resources Available</td></tr>");
	        }
	        show.append("</table>");

	        return show.toString();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new Exception("Database error occurred while checking resources", e);
	    }
	}
	
	
    
	//check approvals
	public String checkManagerApprovals(String username) throws Exception {
	    Connection c = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    // Connect to the database
	    c = db.connect();

	    // Prepare SQL query to select all requests for the manager
	    String sql = "SELECT * FROM request WHERE requestFrom=?";
	    pstmt = c.prepareStatement(sql);
	    pstmt.setString(1, username);

	    // Execute the query
	    rs = pstmt.executeQuery();

	    // Build the HTML table with adjusted columns
	    StringBuilder tableBuilder = new StringBuilder(
	        "<!DOCTYPE html>" +
	        "<html>" +
	        "<head>" +
	        "<style>" +
	        "body {" +
	        "    font-family: Arial, sans-serif;" +
	        "    margin: 0;" +
	        "    padding: 0;" +
	        "    background-color: #f4f4f4;" +
	        "}" +
	        "table {" +
	        "    width: 80%;" +   // Adjusted table width
	        "    margin: 20px auto;" +
	        "    border-collapse: collapse;" +
	        "    background-color: #ffffff;" +
	        "    box-shadow: 0 4px 8px rgba(0,0,0,0.1);" +
	        "}" +
	        "th, td {" +
	        "    border: 1px solid #ddd;" +
	        "    padding: 10px;" +   // Adjusted padding
	        "    text-align: left;" +
	        "}" +
	        "th {" +
	        "    background-color: #000000;" +
	        "    color: white;" +
	        "}" +
	        "tr:nth-child(even) {" +
	        "    background-color: #f2f2f2;" +
	        "}" +
	        "tr:hover {" +
	        "    background-color: #e0e0e0;" +
	        "}" +
	        "form {" +
	        "    display: inline;" +
	        "}" +
	        "input[type='submit'] {" +
	        "    padding: 6px 10px;" +   // Adjusted button padding
	        "    border: none;" +
	        "    border-radius: 4px;" +
	        "    color: white;" +
	        "    cursor: pointer;" +
	        "    font-size: 12px;" +   // Adjusted font size
	        "}" +
	        "input[type='submit'].approve {" +
	        "    background-color: #4CAF50;" +
	        "}" +
	        "input[type='submit'].reject {" +
	        "    background-color: #f44336;" +
	        "}" +
	        "input[type='submit']:hover {" +
	        "    opacity: 0.8;" +
	        "}" +
	        "th:nth-child(1), td:nth-child(1) {" +
	        "    width: 10%;" +   // Adjusted width for columns
	        "}" +
	        "th:nth-child(2), td:nth-child(2) {" +
	        "    width: 15%;" +
	        "}" +
	        "th:nth-child(3), td:nth-child(3) {" +
	        "    width: 20%;" +
	        "}" +
	        "th:nth-child(4), td:nth-child(4) {" +
	        "    width: 20%;" +
	        "}" +
	        "th:nth-child(5), td:nth-child(5) {" +
	        "    width: 15%;" +
	        "}" +
	        "th:nth-child(6), td:nth-child(6) {" +
	        "    width: 20%;" +
	        "}" +
	        "</style>" +
	        "</head>" +
	        "<body>" +
	        "<table>" +
	        "<tr>" +
	        "<th>Request ID</th>" +
	        "<th>Request From</th>" +
	        "<th>Request Name</th>" +
	        "<th>Date of Request</th>" +
	        "<th>Approval Status</th>" +
	        "</tr>"
	    );

	    // Append rows to the table
	    while (rs.next()) {
	        String requestId = rs.getString("requestId");
	        String requestFrom = rs.getString("requestFrom");
	        String requestName = rs.getString("requestName");
	        String dor = rs.getString("dor");
	        String approvalStatus = rs.getString("approvalStatus");

	        tableBuilder.append("<tr>")
	            .append("<td>").append(requestId).append("</td>")
	            .append("<td>").append(requestFrom).append("</td>")
	            .append("<td>").append(requestName).append("</td>")
	            .append("<td>").append(dor).append("</td>")
	            .append("<td>").append(approvalStatus).append("</td>")
	            .append("</tr>");
	    }

	    // Close the table and HTML tags
	    tableBuilder.append(
	        "</table>" +
	        "</body>" +
	        "</html>"
	    );

	    // Return the HTML table as a string
	    return tableBuilder.toString();
	}


	//remove own resources from manager
	
	public String removemanagerownresource(String uname) throws Exception {
	    Connection c = db.connect();
	    
	    // Update query to exclude 'Manager' and optionally 'Admin'
	    String query = "SELECT resourceName FROM userResource WHERE username=? AND resourceName NOT IN ('Manager', 'Admin')";
	    
	    try (PreparedStatement pst = c.prepareStatement(query)) {
	        pst.setString(1, uname);
	        ResultSet rs = pst.executeQuery();
	        
	        StringBuilder dropDown = new StringBuilder("<form action='removeresource1' method='post'>" +
	            "<label for='dropdown'>Select resource name to delete:</label>" +
	            "<select id='dropdown' name='options'>"+
	            "<option value='' disabled selected>Select a resource</option>");
	        
	        boolean hasOptions = false;
	        while (rs.next()) {
	            String value = rs.getString("resourceName");
	            dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>");
	            hasOptions = true;
	        }
	        
	        if (!hasOptions) {
	            dropDown.append("<option value='' disabled selected>No options available</option>");
	        }
	        
	        dropDown.append("</select>");
            dropDown.append("<button type='submit' style='background-color: #000000; color: white;'>Submit</button>");
	        dropDown.append("</form>");
	        
	        return dropDown.toString();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new Exception("Database error occurred while retrieving resources", e);
	    }
	}
	
	public String removemanagerresource1(String option) throws Exception {
	    Connection c = db.connect();
	    String query = "DELETE FROM userResource WHERE resourceName=?";
	    
	    try (PreparedStatement pst = c.prepareStatement(query)) {
	        pst.setString(1, option);
	        pst.executeUpdate();
	        return "Resource is removed successfully";
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new Exception("Database error occurred while removing resource", e);
	    }
	}

	
	//request for admin
	
	public String requestforAdmin(String uname) throws Exception {
        Connection c = db.connect();
        String queryCheckRequest = "SELECT 1 FROM request WHERE requestFrom=? AND requestName='Admin'";
        String queryUserType = "SELECT usertype FROM userdetails WHERE username=?";

        try (PreparedStatement pstCheckRequest = c.prepareStatement(queryCheckRequest);
             PreparedStatement pstUserType = c.prepareStatement(queryUserType)) {

            // Check if there is already a pending request for Admin
            pstCheckRequest.setString(1, uname);
            ResultSet rsCheckRequest = pstCheckRequest.executeQuery();
            
            // Get current user type
            pstUserType.setString(1, uname);
            ResultSet rsUserType = pstUserType.executeQuery();
            
            if (rsUserType.next()) {
                String currentUserType = rsUserType.getString("usertype");

                StringBuilder dropDown = new StringBuilder("<form action='requestforAdmin1' method='post'>");
                dropDown.append("<label for='dropdown'>Request Admin Role:</label>");
                
                // Only show the form if the user is a manager and no admin request exists
                if ("Manager".equals(currentUserType) && !rsCheckRequest.next()) {
                    dropDown.append("<input type='hidden' name='uname' value='" + uname + "'/>");
                    dropDown.append("<button type='submit' style='background-color: #000000; color: white;'>Request Admin Role</button>");
                } else {
                    dropDown.append("<p>You cannot request Admin role at this time.</p>");
                }
                
                dropDown.append("</form>");
                
                return dropDown.toString();
            } else {
                throw new Exception("User details not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while generating request form", e);
        }
    }

    // Method to process admin role request
    public String requestforAdmin1(String uname) throws Exception {
        Connection c = db.connect();
        String queryCount = "SELECT COUNT(*) FROM request";
        String queryInsert = "INSERT INTO request(requestId, requestFrom, dor, approvalStatus, requestName) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstCount = c.prepareStatement(queryCount);
             ResultSet rsCount = pstCount.executeQuery()) {
            
            int count = 0;
            if (rsCount.next()) {
                count = rsCount.getInt(1);
            }
            
            // Insert a new request
            try (PreparedStatement pstInsert = c.prepareStatement(queryInsert)) {
                pstInsert.setInt(1, count + 1);
                pstInsert.setString(2, uname);
                pstInsert.setString(3, LocalDate.now().toString());
                pstInsert.setString(4, "Pending");  // Request status
                pstInsert.setString(5, "Admin");    // Requested role
                pstInsert.executeUpdate();
            }
            
            // Redirect to the admin page to show team members for promotion
            return "<a href='http://localhost:8009/uamm/webapi/myresource/selectTeamMemberForPromotion'>show team members</a>";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while processing the request", e);
        }
    }
    
 // Method to display the dropdown for team member promotion
    public String selectTeamMemberForPromotion(String managerName) throws Exception {
        Connection c = db.connect();
        String query = "SELECT username FROM userdetails WHERE managerName=?";
        PreparedStatement pst = c.prepareStatement(query);
        pst.setString(1, managerName);
        ResultSet rs = pst.executeQuery();

        StringBuilder dropDown = new StringBuilder("<form action='promoteToManager' method='post'>" +
                                                   "<label for='dropdown'>Select a Team Member to Promote to Manager:</label>" +
                                                   "<select id='dropdown' name='teamMember'>" +
                                                   "<option value='' disabled selected>Select a team member</option>");

        boolean hasOptions = false;
        while (rs.next()) {
            hasOptions = true;
            String value = rs.getString("username");
            dropDown.append("<option value='").append(value).append("'>").append(value).append("</option>");
        }

        if (!hasOptions) {
            dropDown.append("<option value=''>No Team Members Available</option>");
        }

        dropDown.append("</select>");
        dropDown.append("<button type='submit' style='background-color: #000000; color: white;'>Promote to Manager</button>");
        dropDown.append("</form>");

        return dropDown.toString();
    }
    
   

    public String promoteToManager(String teamMember) throws Exception {
        Connection c = db.connect();
        String queryUpdateUser = "UPDATE userdetails SET usertype='Manager' WHERE username=?";

        try (PreparedStatement pstUpdateUser = c.prepareStatement(queryUpdateUser)) {
            pstUpdateUser.setString(1, teamMember);
            pstUpdateUser.executeUpdate();
            return "<h3>" + teamMember + " has been promoted to Manager successfully.</h3>";
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred while processing the promotion", e);
        }
    }


    
    
    //change password
    public String ChangePassword() throws Exception {
        String s = "<form action='Change_Password1' method='post'>";
        s += "<input type='password' name='newpassword' placeholder='Enter password to change' required " +
             "pattern='(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}' " +
             "title='Password must be at least 8 characters long, contain at least one digit, one uppercase letter, one lowercase letter, and one special character.'>";
        s += "<br>";
        s += "<input type='password' name='confirmnewpassword' placeholder='Confirm password' required>";
        s += "<br>";
        s += "<br>";
        s += "<button type='submit' style='background-color: #000000; color: white;'>Submit</button>";
        s += "</form>";
        return s;    
    }

    public String Change_Password1(String name, String p) throws Exception {
        Connection c = db.connect();
        String query = "UPDATE userdetails SET password=? WHERE username=?";
        try (PreparedStatement pst = c.prepareStatement(query)) {
            pst.setString(1, encrypt(p));
            pst.setString(2, name);
            pst.executeUpdate();
        }
        return "<h3>Password changed successfully.</h3>";    
    }

}
