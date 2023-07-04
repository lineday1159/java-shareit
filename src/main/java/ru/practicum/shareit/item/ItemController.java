package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemsDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                   @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET request to get ItemList by user - {}", userId);
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemsDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable long itemId) {
        log.info("GET request to get Item by id - {}, by user - {}", itemId, userId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET request to search ItemList by text - {}", text);
        return itemService.searchItems(text, from, size);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("POST request to add Item by user - {}", userId);
        return itemService.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody CommentDto commentDto,
                             @PathVariable long itemId) {
        log.info("POST request to add comment to item with id - {}, by user - {}", itemId, userId);
        return itemService.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        log.info("PATCH request to update item with id - {}, by user - {}", itemId, userId);
        return itemService.update(itemDto, userId, itemId);
    }
}
