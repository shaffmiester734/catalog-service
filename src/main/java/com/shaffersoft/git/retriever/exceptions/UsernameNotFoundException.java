package com.shaffersoft.git.retriever.exceptions;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String username) {
        super("Username: %s does not exist".formatted(username));
    }
}
