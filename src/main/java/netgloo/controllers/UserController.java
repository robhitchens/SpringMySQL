package netgloo.controllers;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import netgloo.models.AccountResponse;
import netgloo.models.User;
import netgloo.models.UserDao;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
    @ApiOperation(value = "Checks to see if username is not in database", notes="Checks the given username against usernames in teh application database.", response = Boolean.class)
    @ApiResponses(value = {@ApiResponse(code=201, message="User does not exist in the database", response=Boolean.class),
            @ApiResponse(code=500, message="user already exists int the database", response=Boolean.class) } )
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

    @RequestMapping(value = "/verifycustomer", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Verifies teh customer_id with the capital one api", notes="returns a response code", response = Integer.class)
    @ApiResponses(value = {@ApiResponse(code=201, message="the customer_id provided is a capital one customer", response=Integer.class),
            @ApiResponse(code=400, message="MalformedURL exception", response=Integer.class),
            @ApiResponse(code=418, message="IOexception", response=Integer.class)} )
    public int verifyCustomerId(@RequestBody String id){
        /*RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("parametes", headers);
        String responseCode = restTemplate.exchange("http://api.reimaginebanking.com/customers/"+id+"?key=4ccc60cc8e267df78ac28a88b00abe0d", HttpMethod.GET, entity,);
        */
        try {
            URL url = new URL("http://api.reimaginebanking.com/customers/"+id+"?key=4ccc60cc8e267df78ac28a88b00abe0d");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return connection.getResponseCode();
        }catch(MalformedURLException e) {
            e.printStackTrace();
            return 400;
        }catch(IOException ioe){
            ioe.printStackTrace();
            return 418;
        }
    }

    /**
     * creates user if username does not already exist in the database and //verify that customer id provided exists within cap one api
     */
    @RequestMapping(value="/create-user-json", method = RequestMethod.POST)
    @ApiOperation(value = "Creates a new User", notes="Creates a new user in the app Database.", response = Void.class)
    @ApiResponses(value = {@ApiResponse(code=201, message="User Created Successfully", response=ResponseEntity.class),
            @ApiResponse(code=418, message="Error creating User", response=ResponseEntity.class),
            @ApiResponse(code=406, message="Customer_id does not exist with capital one.", response=ResponseEntity.class),
            @ApiResponse(code=409, message="Customer_id already exists", response=ResponseEntity.class)} )
    public ResponseEntity<?> createUser(@RequestBody User user){
        try {
            if (!checkUsernameAvailable(user.getUsername())) {
                return new ResponseEntity<Object>("user already exists", null, HttpStatus.CONFLICT);
            } else {
                if(verifyCustomerId(user.getCustomerId())==200) {
                    userDao.save(user);
                    return new ResponseEntity<Object>("object created", null, HttpStatus.CREATED);
                }else{
                    return new ResponseEntity<Object>("Customer_id does not exist with capital one.", null, HttpStatus.NOT_ACCEPTABLE);
                }
            }
        }catch(Exception e){
            return new ResponseEntity<Object>("error. failed to create user", null, HttpStatus.I_AM_A_TEAPOT);
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
