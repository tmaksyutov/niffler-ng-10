package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {

    UserJson create(UserJson user);
}