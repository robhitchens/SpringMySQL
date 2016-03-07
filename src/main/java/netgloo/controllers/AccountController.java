package netgloo.controllers;

import netgloo.models.Account;
import netgloo.models.AccountDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class AccountController {

    /**
     * GET /createaccount  --> Create a new user and save it in the database.
     */
    @RequestMapping("/createaccount")
    @ResponseBody
    public String create(String _id, String type, String nickname, int rewards, double balance, String username) {
        String accountId = "";
        try {
            Account account = new Account(_id, type, nickname, rewards, balance, username);
            accountDao.save(account);
            accountId = String.valueOf(account.getId());
        }
        catch (Exception ex) {
            return "Error creating the account: " + ex.toString();
        }
        return "Account succesfully created with id = " + accountId;
    }

    /**
     * GET /deleteaccount  --> Delete the account having the passed id.
     */
    @RequestMapping("/deleteaccount")
    @ResponseBody
    public String delete(long id) {
        try {
            Account account = new Account(id);
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
    public Account getAccountFromCapOne_id(String _id, boolean savetodatabase, String username) {
        RestTemplate restTemplate = new RestTemplate();
        Account account = restTemplate.getForObject("http://api.reimaginebanking.com/enterprise/accounts/" + _id + "?key=bb1a1852a7e6b8c788147bf3d172726e", Account.class);

        if(savetodatabase){
            account.setUsername(username);
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
    public String updateAccount(long id, String _id, String accounttype, String nickname, int rewards, int balance, String username) {
        try {
            Account account = accountDao.findOne(id);
            account.set_id(_id);
            account.setType(accounttype);
            account.setNickname(nickname);
            account.setRewards(rewards);
            account.setBalance(balance);
            account.setUsername(username);
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