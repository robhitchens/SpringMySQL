package netgloo.controllers;

import netgloo.models.User;
import netgloo.models.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    /**
     * GET /create  --> Create a new user and save it in the database.
     */
    @RequestMapping("/create")
    @ResponseBody
    public String create(String username, String password, String customerId) {
        String userId = "";

        try {
            User user = new User(username, password, customerId);
            userDao.save(user);
            userId = String.valueOf(user.getId());
        }
        catch (Exception ex) {
            return "Error creating the user: " + ex.toString();
        }
        return "User succesfully created with id = " + userId;
    }

    /**
     * GET /delete  --> Delete the user having the passed id.
     */
    @RequestMapping("/delete")
    @ResponseBody
    public String delete(long id) {
        try {
            User user = new User(id);
            userDao.delete(user);
        }
        catch (Exception ex) {
            return "Error deleting the user:" + ex.toString();
        }
        return "User succesfully deleted!";
    }

    /**
     * GET /checkusernameavailable  --> Delete the user having the passed id.
     */
    @RequestMapping(value = "/checkusernameavailable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public boolean checkUsernameAvailable(String username) {
        User user;
        try {
            user = userDao.findByusername(username);
        } catch (Exception ex) {
            return false;
        }
        if(user == null) {
            return true;
        }else{
            return false;
        }
    }

        /**
         * GET /get-by-username  --> Return the id for the user having the passed
         * username.
         */
    //@RequestMapping(value = "/get-by-username", produces = {MediaType.APPLICATION_XML_VALUE})
    @RequestMapping(value = "/get-by-username", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public User getByUserame(String username) {
        String userId = "";
        User user;
        try {
            user = userDao.findByusername(username);
        }
        catch (Exception ex) {
            return null;
        }
        return user;
    }

    @RequestMapping(value = "/get-by-customer_id", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public User getByCustomerId(String customerId) {
        User user;
        try {
            user = userDao.findByCustomerId(customerId);
        }
        catch (Exception ex) {
            return null;
        }
        return user;
    }

    /**
     * GET /update  --> Update the email and the name for the user in the
     * database having the passed id.
     */
    @RequestMapping("/update")
    @ResponseBody
    public String updateUser(long id, String username, String password, String customerId) {
        try {
            User user = userDao.findOne(id);
            user.setUsername(username);
            user.setPassword(password);
            user.setCustomerId(customerId);
            userDao.save(user);
        }
        catch (Exception ex) {
            return "Error updating the user: " + ex.toString();
        }
        return "User succesfully updated!";
    }

    // Private fields

    @Autowired
    private UserDao userDao;

}
