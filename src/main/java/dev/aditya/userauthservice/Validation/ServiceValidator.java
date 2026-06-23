package dev.aditya.userauthservice.Validation;

import dev.aditya.userauthservice.Exceptions.SessionNotExistException;
import dev.aditya.userauthservice.Model.Session;
import dev.aditya.userauthservice.Model.Status;
import dev.aditya.userauthservice.Model.TokenType;

import java.util.Optional;

//public class ServiceValidator {
//
//    public void  validate() {
//        return null;
//    }
//
//    private Session validateSession(String token, TokenType tokenType) throws SessionNotExistException {
//        Optional<Session> existingSession;
//        if(tokenType == TokenType.REFRESH){
//            existingSession= sessionRepository.findByRefreshToken(token);
//        }
//        else{
//            existingSession = sessionRepository.findByAuthToken(token);
//        }
//        if(existingSession.isEmpty() || existingSession.get().getCurrentStatus()== Status.DELETED){
//            throw new SessionNotExistException("Session doesn't exist! Please Login again!!");
//        }
//        return existingSession.get();
//    }
//}
