package ca.ulaval.glo4003.domain.user;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UserRepository {

    protected List<User> users = new ArrayList<User>();

    public boolean isEmpty() {
        return users.isEmpty();
    };

    public abstract void loadAll();

    public User getUser(String emailAddress) {
        for (User user : users) {
            if (user.hasEmailAddress(emailAddress)) {
                return user;
            }
        }
        throw new UserNotFoundException("User \"" + emailAddress + "\" is not found");
    }

    public void addNewUser(String emailAddress, String password, Integer access) throws ExistingUsernameException {
        emailAddress = emailAddress.toLowerCase();
        assertEmailAddressIsValid(emailAddress);
        assertEmailAddresIsAvailable(emailAddress);
        addAndSaveNewUser(emailAddress, password, access);
    }

    private void addAndSaveNewUser(String emailAddress, String password, Integer access) {
        User user = new User(emailAddress, password, access);
        users.add(user);
        saveUser(user);
    }

    private void assertEmailAddresIsAvailable(String emailAddress) {
        if (!emailAddressIsAvailable(emailAddress)) {
            throw new ExistingUsernameException("Username \"" + emailAddress + "\" is already taken");
        }
    }

    private void assertEmailAddressIsValid(String emailAddress) {
        if (!validateEmailAddress(emailAddress)) {
            throw new InvalidEmailAddressException("Username \"" + emailAddress + "\" is not a valid email address");
        }
    }

    private boolean validateEmailAddress(String emailAddress) {
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(emailAddress);
        return m.find();
    }

    private boolean emailAddressIsAvailable(String emailAddress) {
        for (User user : users) {
            if (user.hasEmailAddress(emailAddress)) {
                return false;
            }
        }
        return true;
    }

    protected abstract void saveUser(User user);
}