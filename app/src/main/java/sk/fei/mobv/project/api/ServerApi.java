package sk.fei.mobv.project.api;


import sk.fei.mobv.project.model.User;

public interface ServerApi {
    User userSignIn(final String username, final String password);

    int sendRandomNumber(String token);
}
