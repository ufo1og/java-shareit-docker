package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;

    @Test
    public void testFindAllByCreatorIdOrderByCreatedDesc_WhenFoundRequests_ThenReturnRequests() {
        List<ItemRequest> foundItemRequests = itemRequestRepository.findAllByCreatorIdOrderByCreatedDesc(8L);

        assertThat(foundItemRequests.size(), is(equalTo(2)));
        assertThat(foundItemRequests.get(0).getId(), is(equalTo(2L)));
        assertThat(foundItemRequests.get(1).getId(), is(equalTo(4L)));
    }

    @Test
    public void testFindAllByCreatorIdOrderByCreatedDesc_WhenNotFoundRequests_ThenReturnEmptyList() {
        List<ItemRequest> foundItemRequests = itemRequestRepository.findAllByCreatorIdOrderByCreatedDesc(100L);

        assertThat(foundItemRequests, is(equalTo(Collections.emptyList())));
    }

    @Test
    public void testFindAllByCreatorIdNotOrderByCreatedDesc_StandardBehaviour_ThenReturnItemRequests() {
        List<ItemRequest> foundItemRequests = itemRequestRepository.findAllByCreatorIdNotOrderByCreatedDesc(8L,
                PageRequest.of(0, 3));

        assertThat(foundItemRequests.size(), is(equalTo(3)));
        assertThat(foundItemRequests.get(0).getId(), is(equalTo(5L)));
        assertThat(foundItemRequests.get(1).getId(), is(equalTo(3L)));
        assertThat(foundItemRequests.get(2).getId(), is(equalTo(1L)));
    }
}
