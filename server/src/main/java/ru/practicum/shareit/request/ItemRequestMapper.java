package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemShortDto> itemDtos) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDtos
        );
    }

    public static List<ItemRequestDto> toItemRequestDtos(List<ItemRequest> itemRequests, List<ItemShortDto> itemDtos) {
        Map<Long, List<ItemShortDto>> mappedItemDtos = itemRequests.stream()
                .collect(Collectors.toMap(ItemRequest::getId, l -> new ArrayList<>()));
        for (ItemShortDto itemDto : itemDtos) {
            mappedItemDtos.get(itemDto.getRequestId()).add(itemDto);
        }
        return itemRequests.stream()
                .map(request -> {
                    Long requestId = request.getId();
                    List<ItemShortDto> items = mappedItemDtos.get(requestId);
                    return toItemRequestDtoWithItemDtos(request, items);
                })
                .collect(Collectors.toList());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User creator) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreatorId(creator.getId());
        return itemRequest;
    }

    private static ItemRequestDto toItemRequestDtoWithItemDtos(ItemRequest itemRequest, List<ItemShortDto> itemDtos) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDtos
        );
    }
}
