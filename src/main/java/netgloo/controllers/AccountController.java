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
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        AccountJson acct = new AccountJson("Savings", "dsfjao", 0, 250);
        restTemplater.exchange("http://api.reimaginebanking.com/customers/56d5b78b480cf02f0f88a45a/accounts?key=4ccc60cc8e267df78ac28a88b00abe0d", HttpMethod.POST, entity, AccountResponse.class);
        //AccountResponse returned = restTemplater.postForObject("http://api.reimaginebanking.com/customers/56d5b78b480cf02f0f88a45a/accounts?key=4ccc60cc8e267df78ac28a88b00abe0d", acct, AccountResponse.class);

        return new ResponseEntity<Object>("Hello", null, HttpStatus.OK);
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
    public Account getAccountFromCapOne_id(String _id, boolean savetodatabase) {
        RestTemplate restTemplate = new RestTemplate();
        Account account = restTemplate.getForObject("http://api.reimaginebanking.com/enterprise/accounts/" + _id + "?key=bb1a1852a7e6b8c788147bf3d172726e", Account.class);

        if(savetodatabase){
            accountDao.save(account);
        }

        return account;
    }

    @RequestMapping(value = "/get-account-by-customer_id", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Account getBycustomer_id(String customer_id) {
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
     * database having the passed id.
     */
    @RequestMapping("/updateaccount")
    @ResponseBody
    public String updateAccount(long id, String _id, String nickname) {
        try {
            Account account = accountDao.findOne(id);
            account.set_id(_id);
            account.setNickname(nickname);
            accountDao.save(account);
        }
        catch (Exception ex) {
            return "Error updating the account: " + ex.toString();
        }
        return "User succesfully updated!";
    }

    // Private fields

    @Autowired
    private AccountDao accountDao;

}