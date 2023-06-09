package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestWithItemDto> getRequests(Long userId);

    List<ItemRequestWithItemDto> getRequestsWithPagination(Long userId, int from, int size);

    ItemRequestWithItemDto getRequestById(Long requestId, Long userId);
}
