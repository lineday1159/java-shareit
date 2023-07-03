package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final RequestRepository requestRepository;


    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = ItemRequestMapper.toRequestItem(itemRequestDto, null, requester, new Date());
        return ItemRequestMapper.toRequestItemDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestWithItemDto> getItems(Long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterId(userId);
        List<ItemRequestWithItemDto> itemRequestWithItemDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestWithItemDtos.add(ItemRequestMapper.toRequestItemWithItemDto(itemRequest, itemRepository.findByRequestId(itemRequest.getId())));
        }
        return itemRequestWithItemDtos;
    }

    @Override
    public List<ItemRequestWithItemDto> getItemsWithPagination(Long userId, int from, int size) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (from < 0 || size <= 0) {
            throw new ValidationException("Not correct page parameters");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterIdNot(userId, pageable).toList();
        List<ItemRequestWithItemDto> itemRequestWithItemDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestWithItemDtos.add(ItemRequestMapper.toRequestItemWithItemDto(itemRequest, itemRepository.findByRequestId(itemRequest.getId())));
        }
        return itemRequestWithItemDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestWithItemDto getItemById(Long requestId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
        return ItemRequestMapper.toRequestItemWithItemDto(itemRequest, itemRepository.findByRequestId(itemRequest.getId()));
    }
}
