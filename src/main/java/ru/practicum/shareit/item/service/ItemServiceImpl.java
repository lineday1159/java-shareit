package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.mapper.CommentMapper;
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemsDto> getItems(Long userId, int from, int size) {
        Sort sortAsc = Sort.by("start").ascending();
        Sort sortDesc = Sort.by("start").descending();
        List<ItemsDto> itemsDtos = new ArrayList<>();
        if (from < 0 || size <= 0) {
            throw new ValidationException("Not correct page parameters");
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        for (Item item : itemRepository.findByOwnerId(userId, pageable)) {
            BookingInItemDto nextItemBooking = BookingMapper.bookingToBookingInItemDto(
                    bookingRepository.findTopByItemIdAndStartIsAfterAndStatus(item.getId(), new Date(), BookingStatus.APPROVED, sortAsc));
            BookingInItemDto lastItemBooking = BookingMapper.bookingToBookingInItemDto(
                    bookingRepository.findTopByItemIdAndStartIsBeforeAndStatus(item.getId(), new Date(), BookingStatus.APPROVED, sortDesc));
            itemsDtos.add(ItemMapper.toItemsDto(item, CommentMapper.commentsToCommentsDto(commentRepository.findByItemId(item.getId())), lastItemBooking, nextItemBooking));
        }
        return itemsDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            if (from < 0 || size <= 0) {
                throw new ValidationException("Not correct page parameters");
            }
            Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
            return itemRepository.search(text, pageable)
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item, null))
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ItemsDto getItem(Long itemId, Long userId) {
        Sort sortAsc = Sort.by("start").ascending();
        Sort sortDesc = Sort.by("start").descending();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        if (item.getOwner().getId().equals(userId)) {
            BookingInItemDto nextItemBooking = BookingMapper.bookingToBookingInItemDto(
                    bookingRepository.findTopByItemIdAndStartIsAfterAndStatus(item.getId(), new Date(), BookingStatus.APPROVED, sortAsc));
            BookingInItemDto lastItemBooking = BookingMapper.bookingToBookingInItemDto(
                    bookingRepository.findTopByItemIdAndStartIsBeforeAndStatus(item.getId(), new Date(), BookingStatus.APPROVED, sortDesc));
            return ItemMapper.toItemsDto(item, CommentMapper.commentsToCommentsDto(commentRepository.findByItemId(item.getId())), lastItemBooking, nextItemBooking);
        } else {
            return ItemMapper.toItemsDto(item, CommentMapper.commentsToCommentsDto(commentRepository.findByItemId(item.getId())), null, null);
        }

    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Request not found"));
        }
        Item item = ItemMapper.toItem(itemDto, null, user, request);
        return ItemMapper.toItemDto(itemRepository.save(item), null);
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            item.setIsAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        return ItemMapper.toItemDto(itemRepository.save(item), null);
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatusAndStartIsBefore(itemId, userId, BookingStatus.APPROVED, new Date());
        if (bookings.size() == 0) {
            throw new DataErrorException("Booking not found");
        }
        Comment comment = CommentMapper.commentDtoToComment(commentDto, user, item);
        comment.setCreated(new Date());
        return CommentMapper.commentToCommentDto(commentRepository.save(comment));
    }
}
