# Script PowerShell pour corriger les imports wildcards dans les fichiers Java
# Ce script remplace les imports wildcards par des imports spécifiques

Write-Host "🔧 CORRECTION AUTOMATIQUE DES IMPORTS WILDCARDS" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

# Fonction pour corriger un fichier Java
function Update-JavaImports {
    param([string]$FilePath)
    
    Write-Host "📁 Traitement de: $FilePath" -ForegroundColor Yellow
    
    $content = Get-Content $FilePath -Raw
    $modified = $false
    
    # Remplacer les imports wildcards par des imports spécifiques
    if ($content -match "import com\.ecomptaia\.entity\.\*;") {
        $content = $content -replace "import com\.ecomptaia\.entity\.\*;", @"
import com.ecomptaia.entity.Account;
import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.entity.FinancialPeriod;
import com.ecomptaia.entity.LigneEcriture;
import com.ecomptaia.entity.TemplateEcriture;
import com.ecomptaia.entity.User;
import com.ecomptaia.entity.ThirdParty;
import com.ecomptaia.entity.CostCenter;
import com.ecomptaia.entity.Project;
"@
        $modified = $true
    }
    
    if ($content -match "import com\.ecomptaia\.repository\.\*;") {
        $content = $content -replace "import com\.ecomptaia\.repository\.\*;", @"
import com.ecomptaia.repository.AccountRepository;
import com.ecomptaia.repository.CompanyRepository;
import com.ecomptaia.repository.EcritureComptableRepository;
import com.ecomptaia.repository.FinancialPeriodRepository;
import com.ecomptaia.repository.LigneEcritureRepository;
import com.ecomptaia.repository.TemplateEcritureRepository;
import com.ecomptaia.repository.UserRepository;
"@
        $modified = $true
    }
    
    if ($content -match "import com\.ecomptaia\.model\.ohada\.\*;") {
        $content = $content -replace "import com\.ecomptaia\.model\.ohada\.\*;", @"
import com.ecomptaia.model.ohada.ChartOfAccounts;
import com.ecomptaia.model.ohada.ChartOfAccountsSYSCOHADA;
import com.ecomptaia.model.ohada.ChartOfAccountsSMT;
"@
        $modified = $true
    }
    
    if ($content -match "import java\.util\.\*;") {
        $content = $content -replace "import java\.util\.\*;", @"
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;
"@
        $modified = $true
    }
    
    if ($content -match "import org\.springframework\.web\.bind\.annotation\.\*;") {
        $content = $content -replace "import org\.springframework\.web\.bind\.annotation\.\*;", @"
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
"@
        $modified = $true
    }
    
    if ($content -match "import static org\.junit\.jupiter\.Assertions\.\*;") {
        $content = $content -replace "import static org\.junit\.jupiter\.Assertions\.\*;", @"
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
"@
        $modified = $true
    }
    
    if ($content -match "import static org\.mockito\.ArgumentMatchers\.\*;") {
        $content = $content -replace "import static org\.mockito\.ArgumentMatchers\.\*;", @"
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
"@
        $modified = $true
    }
    
    if ($content -match "import static org\.springframework\.test\.web\.servlet\.request\.MockMvcRequestBuilders\.\*;") {
        $content = $content -replace "import static org\.springframework\.test\.web\.servlet\.request\.MockMvcRequestBuilders\.\*;", @"
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
"@
        $modified = $true
    }
    
    if ($content -match "import static org\.springframework\.test\.web\.servlet\.result\.MockMvcResultMatchers\.\*;") {
        $content = $content -replace "import static org\.springframework\.test\.web\.servlet\.result\.MockMvcResultMatchers\.\*;", @"
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
"@
        $modified = $true
    }
    
    if ($content -match "import com\.itextpdf\.layout\.element\.\*;") {
        $content = $content -replace "import com\.itextpdf\.layout\.element\.\*;", @"
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
"@
        $modified = $true
    }
    
    # Sauvegarder le fichier modifié
    if ($modified) {
        Set-Content -Path $FilePath -Value $content -Encoding UTF8
        Write-Host "✅ Fichier corrigé: $FilePath" -ForegroundColor Green
        return $true
    } else {
        Write-Host "ℹ️  Aucune modification nécessaire: $FilePath" -ForegroundColor Blue
        return $false
    }
}
