package com.ecomptaia.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final String resourceId;
    
    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(String.format("%s non trouvé avec l'identifiant : %s", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
    
    public ResourceNotFoundException(String resourceName, Long resourceId) {
        this(resourceName, String.valueOf(resourceId));
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String getResourceId() {
        return resourceId;
    }
}

