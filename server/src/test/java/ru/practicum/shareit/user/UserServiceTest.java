package ru.practicum.shareit.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUserService() {
        this.userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testCreate_InvalidEmail_ThenThrow() {
        UserDto userDto = new UserDto(1L, "John", "123");

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> userService.create(userDto)
        );

        assertThat(e.getMessage(), is(equalTo("Email '123' is not valid!")));
    }

    @Test
    public void testCreate_ValidEmail_ThenOK() {
        UserDto userDto = new UserDto(1L, "John", "john@ya.ru");
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                        .thenReturn(UserMapper.toUser(userDto));
        userService.create(userDto);
        Mockito.verify(userRepository, Mockito.times(1)).save(UserMapper.toUser(userDto));
    }

    @Test
    public void testUpdate_InvalidEmail_ThenThrow() {
        UserDto userDto = new UserDto(null, "John", "123");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> userService.update(1L, userDto)
        );

        assertThat(e.getMessage(), is(equalTo("Email '123' is not valid!")));
    }

    @Test
    public void testUpdate_UpdateOnlyName_ThenOK() {
        UserDto userDto = new UserDto(null, "Sam", null);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User());

        userService.update(1L, userDto);

        Mockito.verify(userRepository).save(new User(1L, "Sam", "john@ya.ru"));
    }

    @Test
    public void testUpdate_UpdateOnlyEmail_ThenOK() {
        UserDto userDto = new UserDto(null, null, "sam@ya.ru");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User());

        userService.update(1L, userDto);

        Mockito.verify(userRepository).save(new User(1L, "John", "sam@ya.ru"));
    }

    @Test
    public void testUpdate_UpdateBothNameAndEmail_ThenOK() {
        UserDto userDto = new UserDto(null, "Sam", "sam@ya.ru");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(new User());

        userService.update(1L, userDto);

        Mockito.verify(userRepository).save(new User(1L, "Sam", "sam@ya.ru"));
    }
}