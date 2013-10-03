package ca.ulaval.glo4003.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import ca.ulaval.glo4003.model.User;

public class JSONUserConverter extends JSONConverter<User> implements UserConverter{
    public JSONUserConverter() {
        super(User.class);
    }
    
    public User load(String path) throws FileNotFoundException {
        return super.load(path);
    }
    
    public void save(User user, String path) throws IOException {
        super.save(user, path);
    }
}
