package netgloo.controllers;

import netgloo.models.User;
import netgloo.models.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
     * dummy login with encryption test
     */
    @RequestMapping(value="/userLogin", method = RequestMethod.GET)
    public ResponseEntity<?> userlogin(@PathVariable User user){
//waiting for conner's crap for now for encryption
       /* if() {
            return new ResponseEntity<String>("login accepted", null, HttpStatus.OK);
        }else {
*/
            return new ResponseEntity<String>("login failed", null, HttpStatus.BAD_REQUEST);
  //      }
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
     * creates user if username does not already exist in the database
     */
    @RequestMapping(value="/create-user-json", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user){
        if(!checkUsernameAvailable(user.getUsername())){
            return new ResponseEntity<Object>("user already exists", null, HttpStatus.CONFLICT);
        }else {
            userDao.save(user);
            return new ResponseEntity<Object>("object created", null, HttpStatus.CREATED);
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
