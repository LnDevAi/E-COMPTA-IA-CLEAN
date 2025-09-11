ackage com.ecomptaia.controller;

import com.ecomptaia.accounting.entity.AccountingStandard;
import com.ecomptaia.accounting.entity.ChartOfAccounts;
import com.ecomptaia.service.AccountingStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ContrÃ´leur pour la gestion des standards comptables
 */
@RestController
@RequestMapping("/api/accounting-standards")
@CrossOrigin(origins = "*")
public class AccountingStandardController {
    
    @Autowired
    private AccountingStandardService accountingStandardService;
    
    /**
     * RÃ©cupÃ¨re tous les standards comptables disponibles
     */
    @GetMapping
    public ResponseEntity<List<AccountingStandard>> getAllStandards() {
        List<AccountingStandard> standards = accountingStandardService.getAllStandards();
        return ResponseEntity.ok(standards);
    }
    
    /**
     * RÃ©cupÃ¨re un plan comptable selon le standard
     */
    @GetMapping("/{standard}/chart-of-accounts")
    public ResponseEntity<ChartOfAccounts> getChartOfAccounts(@PathVariable String standard) {
        try {
            AccountingStandard accountingStandard = AccountingStandard.fromString(standard);
            ChartOfAccounts chartOfAccounts = accountingStandardService.getChartOfAccounts(accountingStandard);
            return ResponseEntity.ok(chartOfAccounts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * RÃ©cupÃ¨re un plan comptable selon le code pays
     */
    @GetMapping("/by-country/{countryCode}")
    public ResponseEntity<ChartOfAccounts> getChartOfAccountsByCountry(@PathVariable String countryCode) {
        ChartOfAccounts chartOfAccounts = accountingStandardService.getChartOfAccountsByCountry(countryCode);
        return ResponseEntity.ok(chartOfAccounts);
    }
    
    /**
     * RÃ©cupÃ¨re les standards par rÃ©gion
     */
    @GetMapping("/by-region")
    public ResponseEntity<Map<String, List<AccountingStandard>>> getStandardsByRegion() {
        Map<String, List<AccountingStandard>> standardsByRegion = accountingStandardService.getStandardsByRegion();
        return ResponseEntity.ok(standardsByRegion);
    }
    
    /**
     * RÃ©cupÃ¨re les mÃ©tadonnÃ©es d'un standard
     */
    @GetMapping("/{standard}/metadata")
    public ResponseEntity<Map<String, Object>> getStandardMetadata(@PathVariable String standard) {
        try {
            AccountingStandard accountingStandard = AccountingStandard.fromString(standard);
            Map<String, Object> metadata = accountingStandardService.getStandardMetadata(accountingStandard);
            return ResponseEntity.ok(metadata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * VÃ©rifie si un standard est supportÃ©
     */
    @GetMapping("/{standard}/supported")
    public ResponseEntity<Boolean> isStandardSupported(@PathVariable String standard) {
        try {
            AccountingStandard accountingStandard = AccountingStandard.fromString(standard);
            boolean isSupported = accountingStandardService.isStandardSupported(accountingStandard);
            return ResponseEntity.ok(isSupported);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        }
    }
    
    /**
     * RÃ©cupÃ¨re le standard recommandÃ© pour un pays
     */
    @GetMapping("/recommended/{countryCode}")
    public ResponseEntity<AccountingStandard> getRecommendedStandardForCountry(@PathVariable String countryCode) {
        AccountingStandard standard = accountingStandardService.getRecommendedStandardForCountry(countryCode);
        return ResponseEntity.ok(standard);
    }
    
    /**
     * RÃ©cupÃ¨re les devises supportÃ©es par un standard
     */
    @GetMapping("/{standard}/currencies")
    public ResponseEntity<List<String>> getSupportedCurrencies(@PathVariable String standard) {
        try {
            AccountingStandard accountingStandard = AccountingStandard.fromString(standard);
            List<String> currencies = accountingStandardService.getSupportedCurrencies(accountingStandard);
            return ResponseEntity.ok(currencies);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * RÃ©cupÃ¨re les rÃ©gions supportÃ©es par un standard
     */
    @GetMapping("/{standard}/regions")
    public ResponseEntity<List<String>> getSupportedRegions(@PathVariable String standard) {
        try {
            AccountingStandard accountingStandard = AccountingStandard.fromString(standard);
            List<String> regions = accountingStandardService.getSupportedRegions(accountingStandard);
            return ResponseEntity.ok(regions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}




