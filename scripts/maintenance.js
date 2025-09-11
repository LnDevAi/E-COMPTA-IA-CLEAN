#!/usr/bin/env node

/**
 * Maintenance Script for E-COMPTA IA Frontend
 * 
 * This script performs various maintenance tasks:
 * - Database cleanup
 * - Cache management
 * - Performance optimization
 * - Security updates
 * - Backup operations
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

class MaintenanceManager {
  constructor() {
    this.logFile = path.join(__dirname, '../logs/maintenance.log');
    this.ensureLogDirectory();
  }

  ensureLogDirectory() {
    const logDir = path.dirname(this.logFile);
    if (!fs.existsSync(logDir)) {
      fs.mkdirSync(logDir, { recursive: true });
    }
  }

  log(message) {
    const timestamp = new Date().toISOString();
    const logMessage = `[${timestamp}] ${message}\n`;
    console.log(logMessage.trim());
    fs.appendFileSync(this.logFile, logMessage);
  }

  async runMaintenance() {
    this.log('Starting maintenance tasks...');
    
    try {
      await this.cleanupBuildCache();
      await this.optimizeAssets();
      await this.updateDependencies();
      await this.runSecurityAudit();
      await this.generateReports();
      await this.backupConfiguration();
      
      this.log('Maintenance completed successfully');
    } catch (error) {
      this.log(`Maintenance failed: ${error.message}`);
      throw error;
    }
  }

  async cleanupBuildCache() {
    this.log('Cleaning up build cache...');
    
    const cachePaths = [
      'frontend/.angular/cache',
      'frontend/node_modules/.cache',
      'frontend/dist',
      'frontend/coverage'
    ];

    for (const cachePath of cachePaths) {
      const fullPath = path.join(__dirname, '..', cachePath);
      if (fs.existsSync(fullPath)) {
        execSync(`rm -rf "${fullPath}"`, { stdio: 'inherit' });
        this.log(`Cleaned: ${cachePath}`);
      }
    }
  }

  async optimizeAssets() {
    this.log('Optimizing assets...');
    
    const frontendPath = path.join(__dirname, '../frontend');
    
    // Optimize images
    try {
      execSync('npm run optimize:images', { 
        cwd: frontendPath, 
        stdio: 'inherit' 
      });
      this.log('Images optimized');
    } catch (error) {
      this.log(`Image optimization failed: ${error.message}`);
    }

    // Bundle analysis
    try {
      execSync('npm run analyze:bundle', { 
        cwd: frontendPath, 
        stdio: 'inherit' 
      });
      this.log('Bundle analysis completed');
    } catch (error) {
      this.log(`Bundle analysis failed: ${error.message}`);
    }
  }

  async updateDependencies() {
    this.log('Checking for dependency updates...');
    
    const frontendPath = path.join(__dirname, '../frontend');
    
    try {
      // Check for outdated packages
      const outdated = execSync('npm outdated --json', { 
        cwd: frontendPath,
        encoding: 'utf8'
      });
      
      const outdatedPackages = JSON.parse(outdated);
      const packageCount = Object.keys(outdatedPackages).length;
      
      if (packageCount > 0) {
        this.log(`Found ${packageCount} outdated packages`);
        
        // Update minor and patch versions
        execSync('npm update', { 
          cwd: frontendPath, 
          stdio: 'inherit' 
        });
        this.log('Dependencies updated');
      } else {
        this.log('All dependencies are up to date');
      }
    } catch (error) {
      this.log(`Dependency update failed: ${error.message}`);
    }
  }

  async runSecurityAudit() {
    this.log('Running security audit...');
    
    const frontendPath = path.join(__dirname, '../frontend');
    
    try {
      execSync('npm audit --audit-level=moderate', { 
        cwd: frontendPath, 
        stdio: 'inherit' 
      });
      this.log('Security audit completed');
    } catch (error) {
      this.log(`Security audit found issues: ${error.message}`);
    }
  }

  async generateReports() {
    this.log('Generating maintenance reports...');
    
    const reportsDir = path.join(__dirname, '../reports');
    if (!fs.existsSync(reportsDir)) {
      fs.mkdirSync(reportsDir, { recursive: true });
    }

    // Generate performance report
    const performanceReport = {
      timestamp: new Date().toISOString(),
      buildSize: this.getBuildSize(),
      dependencyCount: this.getDependencyCount(),
      testCoverage: this.getTestCoverage(),
      securityIssues: this.getSecurityIssues()
    };

    fs.writeFileSync(
      path.join(reportsDir, 'performance-report.json'),
      JSON.stringify(performanceReport, null, 2)
    );

    this.log('Performance report generated');
  }

  getBuildSize() {
    const distPath = path.join(__dirname, '../frontend/dist');
    if (!fs.existsSync(distPath)) {
      return { total: 0, files: [] };
    }

    let totalSize = 0;
    const files = [];

    const getDirectorySize = (dir) => {
      const items = fs.readdirSync(dir);
      for (const item of items) {
        const itemPath = path.join(dir, item);
        const stats = fs.statSync(itemPath);
        
        if (stats.isDirectory()) {
          getDirectorySize(itemPath);
        } else {
          totalSize += stats.size;
          files.push({
            path: path.relative(distPath, itemPath),
            size: stats.size
          });
        }
      }
    };

    getDirectorySize(distPath);

    return {
      total: totalSize,
      files: files.sort((a, b) => b.size - a.size)
    };
  }

  getDependencyCount() {
    const packageJsonPath = path.join(__dirname, '../frontend/package.json');
    if (!fs.existsSync(packageJsonPath)) {
      return 0;
    }

    const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
    const dependencies = Object.keys(packageJson.dependencies || {});
    const devDependencies = Object.keys(packageJson.devDependencies || {});
    
    return dependencies.length + devDependencies.length;
  }

  getTestCoverage() {
    const coveragePath = path.join(__dirname, '../frontend/coverage/coverage-summary.json');
    if (!fs.existsSync(coveragePath)) {
      return null;
    }

    const coverage = JSON.parse(fs.readFileSync(coveragePath, 'utf8'));
    return coverage.total;
  }

  getSecurityIssues() {
    // This would typically parse npm audit results
    return {
      vulnerabilities: 0,
      high: 0,
      moderate: 0,
      low: 0
    };
  }

  async backupConfiguration() {
    this.log('Backing up configuration files...');
    
    const backupDir = path.join(__dirname, '../backups');
    if (!fs.existsSync(backupDir)) {
      fs.mkdirSync(backupDir, { recursive: true });
    }

    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const backupPath = path.join(backupDir, `config-backup-${timestamp}`);

    const configFiles = [
      'frontend/angular.json',
      'frontend/package.json',
      'frontend/tsconfig.json',
      'frontend/cypress.config.ts',
      '.github/workflows/frontend-ci.yml'
    ];

    fs.mkdirSync(backupPath, { recursive: true });

    for (const configFile of configFiles) {
      const sourcePath = path.join(__dirname, '..', configFile);
      if (fs.existsSync(sourcePath)) {
        const destPath = path.join(backupPath, configFile);
        const destDir = path.dirname(destPath);
        
        if (!fs.existsSync(destDir)) {
          fs.mkdirSync(destDir, { recursive: true });
        }
        
        fs.copyFileSync(sourcePath, destPath);
      }
    }

    this.log(`Configuration backed up to: ${backupPath}`);
  }
}

// CLI Interface
if (require.main === module) {
  const maintenance = new MaintenanceManager();
  
  const command = process.argv[2];
  
  switch (command) {
    case 'run':
      maintenance.runMaintenance()
        .then(() => process.exit(0))
        .catch((error) => {
          console.error('Maintenance failed:', error);
          process.exit(1);
        });
      break;
      
    case 'cleanup':
      maintenance.cleanupBuildCache()
        .then(() => process.exit(0))
        .catch((error) => {
          console.error('Cleanup failed:', error);
          process.exit(1);
        });
      break;
      
    case 'audit':
      maintenance.runSecurityAudit()
        .then(() => process.exit(0))
        .catch((error) => {
          console.error('Audit failed:', error);
          process.exit(1);
        });
      break;
      
    default:
      console.log(`
Usage: node maintenance.js <command>

Commands:
  run      - Run all maintenance tasks
  cleanup  - Clean build cache only
  audit    - Run security audit only

Examples:
  node maintenance.js run
  node maintenance.js cleanup
  node maintenance.js audit
      `);
      process.exit(1);
  }
}

module.exports = MaintenanceManager;








