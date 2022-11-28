package com.spiritlight.invgui.exceptions;

import javax.security.auth.login.LoginException;

/**
 * Exception specifically for failing to decrypting encrypted Session hashes.
 */
public class InvalidCredentialException extends LoginException {
    public InvalidCredentialException(String s) {
        super(s);
    }
}
