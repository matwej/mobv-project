package sk.fei.mobv.pivarci.api;


import sk.fei.mobv.pivarci.model.User;

public interface ServerApi {
    User userSignIn(final String username, final String password);

    int sendRandomNumber(String token);
}
