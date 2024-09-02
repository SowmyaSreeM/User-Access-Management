package uam.uamm;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.sun.research.ws.wadl.Response;

@Path("myresource") // Root path for the resource
public class MyResource {

    // Login 
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public void login_page(@FormParam("username") String username,
                           @FormParam("password") String password, @Context HttpServletRequest req,@Context HttpServletResponse response) throws Exception {
        User obj = new User();
        HttpSession session=req.getSession();
        session.setAttribute("uname", username);
        if (obj.validateUserCredentials(username, password)) {
            String query = "SELECT * FROM userdetails WHERE username = ?";
            Connection c = db.connect();
            PreparedStatement pst = c.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                if ("Admin".equals(rs.getString("usertype")) || "admin".equals(rs.getString("usertype"))) {
                	response.sendRedirect("/uamm/admin.jsp");
                } else if ("User".equals(rs.getString("usertype")) || "user".equals(rs.getString("usertype"))) {
                    response.sendRedirect("/uamm/user.jsp");
                } else {
                    response.sendRedirect("/uamm/Manager.jsp");
                }
            }
        } else {
            response.sendRedirect("/uamm/UserNotFound.html");
        }
    }
    
   

    // Registration 
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String registration_page(@FormParam("firstname") String firstname,
                                    @FormParam("lastname") String lastname,
                                    @FormParam("email") String email,
                                    @FormParam("password") String password,
                                    @FormParam("confirmPassword") String confirmPassword) throws Exception {
        User obj = new User(firstname, lastname,null, email, password, confirmPassword);
        if (!obj.passMatch()) {
            return "Passwords did not match<br><a href='/uamm/registration.html'>Register again</a>";
        }
        return "User created successfully! Your username is: " + obj.createUser() + "<br><a href='/uamm/index.jsp'>Login</a>";
    }
    
    //forgot password
    @Path("forgotpassword")
    @GET
    public String displayForgotPasswordForm() throws Exception {
        User obj = new User();
        return obj.forgotPassword(); // Directly returns the form as a string
    }


    @Path("forgotpassword")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String forgotPassword(@FormParam("username") String username,
                                 @FormParam("email") String email) throws Exception {
        User obj = new User();
        if (obj.validateUserDetails(username, email)) {
            return obj.ChangePasswordLogin(); // Redirects to change password form
        } else {
            return "<h1>User not found or email doesn't match.</h1>" + 
                   "<br><a href='http://localhost:8009/uamm/webapi/myresource/forgotpassword'>Back</a>";
        }
    }
    
    //change password in login page
    @Path("ChangePasswordLogin")
    @GET
    public String ChangePasswordLogin(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String s = obj.ChangePasswordLogin();
        return fobj.addDataAfter(50, s, "webapp/removeRes.html", req);
    }

    @Path("ChangePassword1Login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String ChangePassword1Login(@FormParam("newpassword") String p, @FormParam("confirmnewpassword") String q, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        if (p.equals(q)) {
            HttpSession session = req.getSession();
            String name = (String) session.getAttribute("uname");
            return obj.ChangePassword1Login(name, p) + "<br><a href='http://localhost:8009/uamm/index.jsp'>click here to login</a>";
        }
        return "<h1>Passwords did not match</h1>" + "<br><a href='http://localhost:8009/uamm/index.jsp'>click here to login</a>";
    }

    
   
    //check request
    @POST
    @Path("checkrequests")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String check_requests(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("uname");
        FileUtils fobj = new FileUtils();

        String list = obj.checkRequests(); // Assume checkRequests() generates the necessary HTML
        return fobj.addDataAfter(50, list, "webapp/requestRes.html", req);
    }

    //approve request
    @POST
    @Path("aprroverequest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String approve_request(
        @FormParam("requestId") String requestId,
        @FormParam("requestFrom") String requestFrom,
        @FormParam("requestName") String requestName,
        @FormParam("dor") String dor,
        @FormParam("approvalStatus") String approvalStatus,
        @Context HttpServletRequest req) throws Exception {

        User obj = new User();
        HttpSession session = req.getSession();
        String approverName = (String) session.getAttribute("uname"); // Get the admin's username
        FileUtils fobj = new FileUtils();

        // Pass the approver's name to the approveRequests method
        String result = obj.approveRequests(requestId, requestFrom, requestName, dor, approvalStatus, approverName);
        return fobj.addDataAfter(50, result, "webapp/requestRes.html", req);
    }

   
    @POST
    @Path("rejectrequest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String reject_request(
        @FormParam("requestId") String requestId,
        @Context HttpServletRequest req) throws Exception {

        User obj = new User();
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("uname");
        FileUtils fobj = new FileUtils();

        String result = obj.rejectRequests(requestId);
        return fobj.addDataAfter(50, "Rejected request with ID " + requestId, "webapp/requestRes.html", req);
    }

    
  //add resource
    
    @Path("AddResourceForm")
    @GET
    public String display_add_resource_form(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("uname");
        FileUtils fobj = new FileUtils();

        // Generate the form using the User object method
        String form = obj.AddResourceForm();
        
        // Inject the form into the admin.jsp file
        return fobj.addDataAfter(176, form, "webapp/admin.jsp", req);
    }

    @Path("processResourceAddition")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String handle_resource_addition(@FormParam("resourceName") String resourceName, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("uname");
        FileUtils fobj = new FileUtils();

        // Process the resource addition
        String result = obj.processResourceAddition(resourceName);

        // Inject the result into the admin.jsp file or return a specific page
        return fobj.addDataAfter(176, result, "webapp/admin.jsp", req);
    }
 
    
    //remove resource from db
    @Path("removeResourceFrom")
    @GET
    public String showResourceRemovalForm(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String form = obj.generateResourceRemovalForm();
        return fobj.addDataAfter(177, form, "webapp/admin.jsp", req);
    }
    
    @Path("processResourceRemoval")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String processResourceRemoval(@FormParam("resourceName") String resourceName) throws Exception {
        User obj = new User();
        return obj.removeResource(resourceName) +
               "<br><a href='http://localhost:8009/uamm/webapi/myresource/removeResourceFrom'>Home</a>";
    }
    
  //remove resource from a user
    @Path("removeUserSelection")
    @GET
    public String showUserSelectionForm(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String form = obj.generateUserSelectionForm();
        return fobj.addDataAfter(176, form, "webapp/admin.jsp", req);
    }
    
    @Path("processUserSelection")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String processUserSelection(@FormParam("selectedUser") String selectedUser, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj=new FileUtils();
        String form = obj.generateResourceSelectionForm(selectedUser);
        return fobj.addDataAfter(176, form, "webapp/admin.jsp", req);
    }
    
    @Path("processResourceDeletion")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String processResourceDeletion(@FormParam("selectedResource") String selectedResource) throws Exception {
        User obj = new User();
        return obj.deleteResourceFromUser(selectedResource) +
               "<br><a href='http://localhost:8009/uamm/webapi/myresource/removeUserSelection'>Home</a>";
    }
    
    
    //check user for a resource
    @Path("showResourceDropdown")
    @GET
    public String showResourceDropdown(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        String form = obj.generateResourceDropdown();
        FileUtils fobj = new FileUtils();
        return fobj.addDataAfter(176, form, "webapp/admin.jsp", req);
    }
    
    @Path("processResourceSelection")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String processResourceSelection(@FormParam("selectedResource") String selectedResource, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        return obj.getUsersForResource(selectedResource) +
               "<br><a href='http://localhost:8009/uamm/webapi/myresource/showResourceDropdown'>Home</a>";
    }
   
    
    //check resource of a user
    @Path("showUser")
    @GET
    public String showUser(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        String form = obj.checkResourceForUser();
        FileUtils fobj = new FileUtils();
        return fobj.addDataAfter(176, form, "webapp/admin.jsp", req);
    }

    @Path("selectUser")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String selectUser(@FormParam("selectedUser") String selectedUser, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        return obj.checkResourceForUser1(selectedUser) +
               "<br><a href='http://localhost:8009/uamm/webapi/myresource/showUser'>Back</a>";
    }

   
    
    //add users    
    @Path("addUserForm")
    @GET
    public String addUserForm(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String form = obj.addUsersinfo();
        return fobj.addDataAfter(176, form, "webapp/admin.jsp", req);
    }
    
    @Path("addUser")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String addUser(@FormParam("firstname") String firstname,
                          @FormParam("lastname") String lastname,
                          @FormParam("email") String email) throws Exception {
        User obj = new User(firstname, lastname, null, email, null, null);
        String result = obj.addUser();
        if (result.length() < 20) {
            return "User added successfully! User's username is: " + result + 
            		"<br><a href='http://localhost:8009/uamm/admin.jsp'>Home</a>";
        } else {
            return result + 
            		"<br><a href='http://localhost:8009/uamm/admin.jsp'>Home</a>";
        }
    }

    
    
    //remove user from db
    
    @Path("removeUserFromDB")
    @GET
    public String remove_user_page(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String s = obj.listUsersForRemoval();
        return fobj.addDataAfter(176, s, "webapp/admin.jsp", req);
    }

    @Path("removeSelectedUser")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_user(@FormParam("username") String username) throws Exception {
        User obj = new User();
        return obj.removeSelectedUser(username) + "<br><a href='http://localhost:8009/uamm/admin.jsp'>Home</a>";
    }
    
    //change password
    
    @Path("changePassword")
    @GET
    public String change_Password(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String s = obj.changePassword();
        return fobj.addDataAfter(50, s, "webapp/removeRes.html", req);
    }

    @Path("changePassword1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change_Password1(@FormParam("newpassword") String p, @FormParam("confirmnewpassword") String q, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        if (p.equals(q)) {
            HttpSession session = req.getSession();
            String name = (String) session.getAttribute("uname");
            return obj.changePassword1(name, p) + "<br><a href='http://localhost:8009/uamm/admin.jsp'>home</a>";
        }
        return "<h1>Passwords did not match</h1>" + "<br><a href='http://localhost:8009/uamm/webapi/myresource/changePassword'>Click here to try again</a>";

    }

    

    
    //****************************user page check resources**********************
    
    //check new resource
    @Path("user")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String check_resources_for_logged_in_user(@Context HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return "<h1>No session found. Please log in first.</h1>";
        }

        String loggedInUsername = (String) session.getAttribute("uname"); // Get the logged-in username from the session

        User obj = new User();
        String resources = obj.checkresources(loggedInUsername);

        FileUtils fobj = new FileUtils();
        return fobj.addDataAfter(50, resources, "webapp/requestRes.html", req);
    }
    
    
    //request resource from user
    @Path("requestforresources")
    @GET
    public String displayResourceRequestForm(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String name = (String) session.getAttribute("uname");
        FileUtils fobj = new FileUtils();
        String formHtml = obj.requestforresources(name);
        return fobj.addDataAfter(50, formHtml, "webapp/requestRes.html", req);
    }

    @Path("requestforresources1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String handleResourceRequestSubmission(@FormParam("options") String resourceName, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String name = session.getAttribute("uname").toString();
        return obj.requestforresource1(resourceName, name) + "<br><a href='http://localhost:8009/uamm/user.jsp'>home</a>";
    }

    
    //check approvals
    
    @Path("checkapprovals")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String check_approvals(@Context HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("uname");
        if (username == null) {
            throw new Exception("User not logged in");
        }
        User obj = new User();
        return obj.checkApprovedRequests(username);
    }    
    
    
    //request for manager or admin
    
    @Path("requestfor")
    @GET
    public String request_for(@Context HttpServletRequest req) throws Exception {
    	User obj=new User();
    	HttpSession session=req.getSession();
		String name=session.getAttribute("uname").toString();
		FileUtils fobj=new FileUtils();
		String s=obj.requestfor(name);
		return  fobj.addDataAfter(50, s, "webapp/requestRes.html", req);
    }
    
    @Path("requestfor1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String request_for1(@FormParam("options") String options, @Context HttpServletRequest req) throws Exception {
        if (options == null || options.isEmpty()) {
            // No role selected; return an error message with a link to retry
            return "No role selected. Please <a href='http://localhost:8009/uamm/webapi/myresource/requestfor'>go back</a> and select a role to request.";
        }

        User obj = new User();
        HttpSession session = req.getSession();
        String name = session.getAttribute("uname").toString();
        String result = obj.requestfor1(options, name);
        
        return result + "<br><a href='http://localhost:8009/uamm/user.jsp'>Home</a>";
    }
    
    
    //remove own resources
    
    @Path("removeownresource")
    @GET
    public String remove_own_resource(@Context HttpServletRequest req) throws Exception {
    	User obj=new User();
    	FileUtils fobj=new FileUtils();
    	HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String s=obj.removeownresource(name);
    	return fobj.addDataAfter(50, s,"webapp/removeRes.html" , req);
    	
    }
    
    @Path("removeresource1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String remove_resource1(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
		User obj=new User();
		return  obj.removeresource1(options)+"<br><a href='http://localhost:8009/uamm/user.jsp'>home</a>";
	}    
    
    
 //change password
    
    @Path("changepassword")
    @GET
    public String change_password(@Context HttpServletRequest req) throws Exception {
    	User obj=new User();
		FileUtils fobj=new FileUtils();
		String s=obj.changepassword();
		return  fobj.addDataAfter(50, s,"webapp/removeRes.html" , req);
    }
    
    @Path("changepassword1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String change_password1(@FormParam("newpassword")String p,@FormParam("confirmnewpassword")String q,@Context HttpServletRequest req) throws Exception {
    	User obj=new User();
    	if(p.equals(q)) {
    		HttpSession session=req.getSession();
    		String name=session.getAttribute("uname").toString();
        	return obj.changepassword1(name,p)+"<br><a href='http://localhost:8009/uamm/user.jsp'>home</a>";
    	}
    	return "<h1>Passwords did not match</h1>"+ "<br><a href='http://localhost:8009/uamm/webapi/myresource/changepassword'>Try again</a>";
    	
    }
    
    //**************************************manager**************************************
    //showteam
    
    @Path("showteam")
    @GET
    public String show_team(@Context HttpServletRequest req) throws Exception {
    	User obj=new User();
    	HttpSession session=req.getSession();
    	String name=(String) session.getAttribute("uname");
    	return obj.showteam(name);
    }
    
    //get a team
    
    @Path("getateammember")
    @GET
    public String get_a_team_member(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        String s = obj.getateammember();
        FileUtils fobj = new FileUtils();
        return fobj.addDataAfter(50, s, "webapp/managerFun.html", req);
    }

    @Path("getateammember1")
    @POST
    public String get_a_team_member1(@FormParam("options") String options, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String name = (String) session.getAttribute("uname");

        if (name == null) {
            throw new Exception("Session attribute 'uname' is not set.");
        }

        return obj.getateammember1(options, name) +  "<br><a href='http://localhost:8009/uamm/Manager.jsp'>home</a>";
    }
    
   
    //check resources
    
    @Path("Manager")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String check_resources_for_logged_in_user1(@Context HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return "<h1>No session found. Please log in first.</h1>";
        }

        String loggedInUsername = (String) session.getAttribute("uname"); // Get the logged-in username from the session

        User obj = new User();
        String resources = obj.checkManagerResources(loggedInUsername);

        FileUtils fobj = new FileUtils();
        return fobj.addDataAfter(50, resources, "webapp/requestRes.html", req);
    }
    
    //check approvals
    
    @Path("checkapprovalsManger")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String check_approvals1(@Context HttpServletRequest req) throws Exception {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("uname");
        if (username == null) {
            throw new Exception("User not logged in");
        }
        User obj = new User();
        return obj.checkManagerApprovals(username);  // Use the method to check approvals for managers
    }

    
    //remove own resource
    
    @Path("removeManagerownresource")
    @GET
    public String removeManagerownresource(@Context HttpServletRequest req) throws Exception {
    	User obj=new User();
    	FileUtils fobj=new FileUtils();
    	HttpSession session=req.getSession();
    	String name=session.getAttribute("uname").toString();
    	String s=obj.removemanagerownresource(name);
    	return fobj.addDataAfter(50, s,"webapp/removeRes.html" , req);
    	
    }
    
    @Path("removemanagerresource1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String removeManagerresource1(@FormParam("options") String options,@Context HttpServletRequest req) throws Exception {
		User obj=new User();
		return  obj.removemanagerresource1(options)+ "<br><a href='http://localhost:8009/uamm/Manager.jsp'>home</a>";
	}
    
   
   
 // request for admin
  
    @Path("requestforAdmin")
    @GET
    public String requestforAdmin(@Context HttpServletRequest req) throws Exception {
        User obj = new User();  // Ensure that User class has the requestforAdmin method
        HttpSession session = req.getSession();
        String uname = (String) session.getAttribute("uname");
        return obj.requestforAdmin(uname);  // Calls the method defined in User class
    }


    @Path("requestforAdmin1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String requestforAdmin1(@FormParam("uname") String uname) throws Exception {
        User obj = new User();
        // Process the admin request and provide a link to the team member promotion page
        return obj.requestforAdmin1(uname);
    }

    @Path("selectTeamMemberForPromotion")
    @GET
    public String selectTeamMemberForPromotion(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        HttpSession session = req.getSession();
        String managerName = (String) session.getAttribute("uname");
        return obj.selectTeamMemberForPromotion(managerName);
    }

    @Path("promoteToManager")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String promoteToManager(@FormParam("teamMember") String teamMember) throws Exception {
        User obj = new User();
        return obj.promoteToManager(teamMember)+"<br><a href='http://localhost:8009/uamm/Manager.jsp'>home</a>";
    }
    
    
    //change password
    
    @Path("Change_Password")
    @GET
    public String Change_Password(@Context HttpServletRequest req) throws Exception {
        User obj = new User();
        FileUtils fobj = new FileUtils();
        String s = obj.ChangePassword();
        return fobj.addDataAfter(50, s, "webapp/removeRes.html", req);
    }

    @Path("Change_Password1")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String Change_Password1(@FormParam("newpassword") String p, @FormParam("confirmnewpassword") String q, @Context HttpServletRequest req) throws Exception {
        User obj = new User();
        if (p.equals(q)) {
            HttpSession session = req.getSession();
            String name = (String) session.getAttribute("uname");
            return obj.Change_Password1(name, p) + "<br><a href='http://localhost:8009/uamm/Manager.jsp'>home</a>";
        }
        return "<h1>Passwords did not match</h1>" +"<br><a href='http://localhost:8009/uamm/webapi/myresource/Change_Password'>Try again</a>";

    }

}
