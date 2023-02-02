package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto add(ItemRequestDto itemRequestDto, Long userId) {
        User creator = userRepository.findById(userId).orElseThrow();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, creator);
        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Created new ItemRequest: {}.", createdItemRequest);
        return ItemRequestMapper.toItemRequestDto(createdItemRequest, Collections.emptyList());
    }

    @Override
    public List<ItemRequestDto> getAllOwnRequests(Long id) {
        User owner = userRepository.findById(id).orElseThrow();
        List<ItemRequest> foundItemRequests = itemRequestRepository.findAllByCreatorIdOrderByCreatedDesc(owner.getId());
        List<ItemShortDto> itemsAnsweredToRequests = getRequestsAnsweredItems(foundItemRequests);
        log.info("Found ItemRequests: {}.", foundItemRequests);
        return ItemRequestMapper.toItemRequestDtos(foundItemRequests, itemsAnsweredToRequests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Optional<Integer> size) {
        if (size.isEmpty()) {
            return Collections.emptyList();
        }
        int pageSize = size.get();
        if (from < 0 || pageSize < 0) {
            throw new ValidationFailException("Parameters 'from' and 'size' must be positive!");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Pageable itemRequestsPageRequest = PageRequest.of(from, pageSize);
        List<ItemRequest> foundItemRequests = itemRequestRepository
                .findAllByCreatorIdNotOrderByCreatedDesc(user.getId(), itemRequestsPageRequest);
        List<ItemShortDto> itemsAnsweredToRequests = getRequestsAnsweredItems(foundItemRequests);
        log.info("Found ItemRequests: {}.", foundItemRequests);
        return ItemRequestMapper.toItemRequestDtos(foundItemRequests, itemsAnsweredToRequests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow();
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow();
        List<ItemShortDto> itemsAnsweredToRequest = getRequestsAnsweredItems(Collections.singletonList(itemRequest));
        log.info("Found ItemRequest: {}.", itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsAnsweredToRequest);
    }

    private List<ItemShortDto> getRequestsAnsweredItems(List<ItemRequest> foundItemRequests) {
        List<Long> requestIds = foundItemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        return itemRepository.findAllByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());
    }
}