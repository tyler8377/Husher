package org.tyler.husher.core.exceptions;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super("Wrong password !");
    }
}
