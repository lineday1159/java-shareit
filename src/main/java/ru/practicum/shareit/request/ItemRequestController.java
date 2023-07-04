package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    @Autowired
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("POST request to add item request by user - {}", userId);
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestWithItemDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GET request to get item requestList by user - {}", userId);
        return requestService.getRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                           @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("GET request to get all item requestList by user - {}", userId);
        return requestService.getRequestsWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        log.info("GET request to get item request by id - {}, by user - {}", requestId, userId);
        return requestService.getRequestById(requestId, userId);
    }
}
