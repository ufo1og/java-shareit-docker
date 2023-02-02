package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequestDto add(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllOwnRequests(Long id);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Optional<Integer> size);

    ItemRequestDto getById(Long userId, Long requestId);
}
