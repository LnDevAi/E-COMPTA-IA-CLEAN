ackage com.ecomptaia.accounting.entity;

import com.ecomptaia.entity.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountClass {
    
    private String classNumber;
    private String name;
    private String description;
    private AccountType type;
    private List<Account> accounts;
    
    public AccountClass(String classNumber, String name, AccountType type) {
        this.classNumber = classNumber;
        this.name = name;
        this.type = type;
        this.accounts = new ArrayList<>();
    }
    
    public AccountClass(String classNumber, String name, String description, AccountType type) {
        this.classNumber = classNumber;
        this.name = name;
        this.description = description;
        this.type = type;
        this.accounts = new ArrayList<>();
    }
    
    // Getters et Setters
    public String getClassNumber() { return classNumber; }
    public void setClassNumber(String classNumber) { this.classNumber = classNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }
    
    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }
    
    public void addAccount(Account account) {
        this.accounts.add(account);
    }
    
    @Override
    public String toString() {
        return classNumber + " - " + name;
    }
}












