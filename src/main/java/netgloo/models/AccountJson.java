package netgloo.models;


/**
 * Created by roberthitchens3 on 3/7/16.
 */

public class AccountJson {

    String type;
    String nickname;
    int rewards;
    int balance;

    public AccountJson(){}

    public AccountJson(String type, String nickname, int rewards, int balance){
        this.type = type;
        this.nickname = nickname;
        this.rewards = rewards;
        this.balance = balance;
    }


    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getRewards() {
        return rewards;
    }
    public void setRewards(int rewards) {
        this.rewards = rewards;
    }
    public int getBalance() {
        return balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }
    public String toString(){
        return "{\"type\":"+type+",\"nickname\":"+nickname+",\"rewards\":"+rewards+",\"balance\":"+balance+"}";
    }
}
