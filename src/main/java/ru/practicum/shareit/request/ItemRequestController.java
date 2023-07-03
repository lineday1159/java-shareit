package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @Autowired
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestWithItemDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getItems(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "20") int size) {
        return requestService.getItemsWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        return requestService.getItemById(requestId, userId);
    }
}
