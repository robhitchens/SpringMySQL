package netgloo.controllers;

import netgloo.models.Account;
import netgloo.models.AccountDao;

import netgloo.models.AccountJson;
import netgloo.models.AccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Controller
public class AccountController {

    /**
     * GET /createaccount  --> Create a new user and save it in the database.
     */
    @RequestMapping("/createaccount")
    @ResponseBody
    public String create(String _id, String type, String nickname, int rewards, double balance) {
        String accountId = "";
        try {
            Account account = new Account(_id, nickname);
            accountDao.save(account);
           // accountId = String.valueOf(account.getId());
        }
        catch (Exception ex) {
            return "Error creating the account: " + ex.toString();
        }
        return "Account succesfully created with id = " + accountId;
    }

    @RequestMapping(value="/createAccount", method=RequestMethod.POST)
    public ResponseEntity<?> createAccount(/*@RequestBody AccountJson acct*/){
        RestTemplate restTemplater = new RestTemplate();
        /*HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        AccountJson acct = new AccountJson("Savings", "dsfjao", 0, 250);
        restTemplater.exchange("http://api.reimaginebanking.com/customers/56d5b78b480cf02f0f88a45a/accounts?key=4ccc60cc8e267df78ac28a88b00abe0d", HttpMethod.POST, entity, AccountResponse.class)*/
            ;
        AccountJson acct = new AccountJson("Savings", "an account", 0, 250);
        System.out.println("got here");
        AccountResponse returned = null;
        //try {
            returned = restTemplater.postForObject("http://api.reimaginebanking.com/customers/56d5b78b480cf02f0f88a45a/accounts?key=4ccc60cc8e267df78ac28a88b00abe0d", acct, AccountResponse.class);
        //}catch(RestClientException e){
            System.out.println("before stack trace");
          //  e.printStackTrace(System.err);
        //}
        return new ResponseEntity<Object>(returned.getObjectCreated(), HttpStatus.valueOf(returned.getCode()));
    }

    /**
     * GET /deleteaccount  --> Delete the account having the passed id.
     */
    @RequestMapping("/deleteaccount")
    @ResponseBody//fix this somehow
    public String delete(long id) {
        try {
            Account account = new Account();
            accountDao.delete(account);
        }
        catch (Exception ex) {
            return "Error deleting the account:" + ex.toString();
        }
        return "Account succesfully deleted!";
    }

    /**
     *
     * @param id
     * @return response Entity.
     * responseEntity: successful, unsuccessful, and error I AM A TEAPOT.
     *
     * deletes account by id provided from database and capital one api.
     */
    @RequestMapping(value="/deleteanaccount", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteAccount(@RequestBody String id){
        try{
            Account accountToDelete = accountDao.findBy_id(id);
            if(accountToDelete!=null){
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.delete("http://api.reimaginebanking.com/accounts?key=4ccc60cc8e267df78ac28a88b00abe0d", id);
                accountDao.delete(accountToDelete);
                return new ResponseEntity<Object>("account deleted", null, HttpStatus.OK);
            }else{
                return new ResponseEntity<Object>("account provided does not exist", null, HttpStatus.NOT_ACCEPTABLE);
            }
        }catch(Exception ex){
            return new ResponseEntity<Object>("an error has occurred while deleting account", null, HttpStatus.I_AM_A_TEAPOT);
        }
    }


    /**
     * GET /get-account-by-CapOneid  --> Return the account having the passed
     * CapOne _id.
     */
    @RequestMapping(value = "/get-account-by-CapOneid", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Account getBy_id(String _id) {
        Account account;
        try {
            account = accountDao.findBy_id(_id);
        }
        catch (Exception ex) {
            return null;
        }
        return account;
    }



    /**
     * GET /pull-account-from-CapOne-by-ID  --> Return an account object based on the CapOne ID
     */
    @RequestMapping(value = "/pull-account-from-CapOne-by-ID", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Account getAccountFromCapOne_id(@RequestBody String _id, boolean savetodatabase) {
        RestTemplate restTemplate = new RestTemplate();
        Account account = restTemplate.getForObject("http://api.reimaginebanking.com/enterprise/accounts/" + _id + "?key=bb1a1852a7e6b8c788147bf3d172726e", Account.class);

        if(savetodatabase){
            accountDao.save(account);
        }

        return account;
    }

    @RequestMapping(value = "/get-account-by-customer_id", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Account getBycustomer_id(@RequestBody String customer_id) {
        Account account;
        try {
            account = accountDao.findBy_id(customer_id);
        }
        catch (Exception ex) {
            return null;
        }
        return account;
    }

    /**
     * GET /updateaccount  --> Update the details for the account in the
     * database having the passed account id.
     * will update nickname of account.
     * encryption of account id should be required.
     */
    @RequestMapping(value="/updateaccount/{_id}/", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAccount(@PathVariable String _id, @RequestBody String nickname) {
        try {
            Account account = accountDao.findBy_id(_id);
            RestTemplate restTemplate = new RestTemplate();
            //need to map nickname to object//
            restTemplate.put("http://api.reimaginebanking.com/accounts/"+_id+"?key=4ccc60cc8e267df78ac28a88b00abe0d", nickname);
            account.setNickname(nickname);
            accountDao.save(account);
        }
        catch (Exception ex) {
            return new ResponseEntity<Object>("error occured: update failed.", null, HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity<Object>("User succesfully updated!", null, HttpStatus.OK);
    }

    // Private fields

    @Autowired
    private AccountDao accountDao;

}