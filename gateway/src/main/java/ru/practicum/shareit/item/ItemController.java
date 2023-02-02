package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Creating item {} by user with id = {}.", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable(name = "itemId") long itemId,
                                         @RequestBody ItemDto itemDto) {
        log.info("Updating item with id = {} by user with id = {} to {}.", itemId, userId, itemDto);
       return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Getting item with id = {} by user with id = {}.", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                   @RequestParam(required = false) Integer size) {
        log.info("Getting items by user with id = {}.", userId);
        return itemClient.getItemByUser(userId,from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                     @RequestParam(required = false, defaultValue = "0") Integer from,
                                     @RequestParam(required = false) Integer size) {
        log.info("Searching items by substring = '{}'.", text);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable Long itemId,
                                 @Validated @RequestBody CommentCreateDto commentDto) {
        log.info("Commenting item with id = {} by user with id = {}.", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
