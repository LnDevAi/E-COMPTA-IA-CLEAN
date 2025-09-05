# Health Check Monitoring E-COMPTA-IA
Write-Host "�� Vérification de la santé des services..." -ForegroundColor Yellow

$Services = @(
    @{Name="prometheus"; Port=9090},
    @{Name="grafana"; Port=3000},
    @{Name="node-exporter"; Port=9100},
    @{Name="postgres-exporter"; Port=9187},
    @{Name="redis-exporter"; Port=9121},
    @{Name="alertmanager"; Port=9093},
    @{Name="cadvisor"; Port=8080}
)

foreach ($service in $Services) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$($service.Port)" -TimeoutSec 5 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ $($service.Name) est opérationnel (port $($service.Port))" -ForegroundColor Green
        } else {
            Write-Host "⚠️ $($service.Name) répond avec le code $($response.StatusCode) (port $($service.Port))" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "❌ $($service.Name) n'est pas accessible (port $($service.Port))" -ForegroundColor Red
    }
}
