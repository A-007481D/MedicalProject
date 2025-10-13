package org.medxpertise.medicaltelexpertise.application.service.exception;

public class BusinessRuleException extends RuntimeException{

    public BusinessRuleException(String message) {
        super(message);
    }

}