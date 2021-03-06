package ca.ulaval.glo4003.infrastructure.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.ulaval.glo4003.domain.user.ExistingUsernameException;
import ca.ulaval.glo4003.domain.user.InvalidEmailAddressException;
import ca.ulaval.glo4003.domain.user.User;
import ca.ulaval.glo4003.domain.user.UserNotFoundException;
import ca.ulaval.glo4003.infrastructure.persistence.FileAccessor;

public class JSONUserRepositoryTest {

    private static final String A_USERNAME = "auser@name.com";
    private static final String INVALID_USERNAME = "a_username";
    private static final String A_PASSWORD = "a_password";
    private static final Integer AN_ACCESS_LEVEL = 0;
    private List<String> VALID_FILES_NAME_IN_A_DIRECTORY;
    private static final String ANOTHER_USERNAME = "another@username.com";

    private JSONUserRepository userRepository;

    @Mock
    private JSONUserMarshaller JSONUserMarshaller;
    @Mock
    private FileAccessor fileAccessor;
    @Mock
    private User user;
    @Mock
    private Logger logger;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        VALID_FILES_NAME_IN_A_DIRECTORY = new ArrayList<String>();
        userRepository = new JSONUserRepository(fileAccessor, JSONUserMarshaller, logger);
        VALID_FILES_NAME_IN_A_DIRECTORY.add("ValidFileA.json");
    }

    @Test
    public void newRepositoryContainsNoEntries() {
        boolean repositoryIsEmpty = userRepository.isEmpty();
        assertTrue(repositoryIsEmpty);
    }

    @Test
    public void whenLoadingAllUsersRepositoryContainsAllUsers() throws FileNotFoundException {
        doReturn(VALID_FILES_NAME_IN_A_DIRECTORY).when(fileAccessor).getFilesNameInDirectory(anyString());
        doReturn(user).when(JSONUserMarshaller).load(anyString());
        userRepository.loadAll();

        boolean repositoryIsEmpty = userRepository.isEmpty();
        assertFalse(repositoryIsEmpty);
    }

    @Test
    public void canRetrieveUserByItsUsername() throws FileNotFoundException {
        doReturn(VALID_FILES_NAME_IN_A_DIRECTORY).when(fileAccessor).getFilesNameInDirectory(anyString());
        doReturn(user).when(JSONUserMarshaller).load(anyString());
        doReturn(true).when(user).hasEmailAddress(A_USERNAME);
        userRepository.loadAll();

        User retrievedUser = userRepository.getUser(A_USERNAME);
        assertEquals(user, retrievedUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void cannotRetrieveInexistantUser() throws FileNotFoundException {
        userRepository.getUser(A_USERNAME);
    }

    @Test
    public void canAddNewUser() {
        doReturn(true).when(user).hasEmailAddress(A_USERNAME);
        userRepository.addNewUser(A_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);

        User retrievedUser = userRepository.getUser(A_USERNAME);
        assertTrue(retrievedUser.hasEmailAddress(A_USERNAME));
    }

    @Test
    public void userIsSavedAfterAdding() throws IOException {
        userRepository.addNewUser(A_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);
        verify(JSONUserMarshaller).save((User) anyObject(), anyString());
    }

    @Test(expected = ExistingUsernameException.class)
    public void cannotAddTheSameUsernameTwice() {
        userRepository.addNewUser(A_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);
        userRepository.addNewUser(A_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);
    }

    @Test(expected = InvalidEmailAddressException.class)
    public void usernameCannotBeInvalidEMailAddress() {
        userRepository.addNewUser(INVALID_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);
    }

    @Test
    public void canAddMultipleUsersIfTheyHaveDifferentUsernames() {
        userRepository.addNewUser(A_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);
        userRepository.addNewUser(ANOTHER_USERNAME, A_PASSWORD, AN_ACCESS_LEVEL);
    }

    @Test
    public void test() throws FileNotFoundException {
        doReturn(VALID_FILES_NAME_IN_A_DIRECTORY).when(fileAccessor).getFilesNameInDirectory(anyString());
        doThrow(FileNotFoundException.class).when(JSONUserMarshaller).load(anyString());

        userRepository.loadAll();

        verify(logger).info(anyString());
    }

}
