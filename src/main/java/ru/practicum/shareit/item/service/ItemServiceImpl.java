package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataErrorException;
import ru.practicum.shareit.exception.NotFoundException;
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


    @Override
    public List<ItemsDto> getItems(Long userId) {
        Sort sortAsc = Sort.by("start").ascending();
        Sort sortDesc = Sort.by("start").descending();
        List<ItemsDto> itemsDtos = new ArrayList<>();
        for (Item item : itemRepository.findByOwnerIdOrderByIdAsc(userId)) {
            BookingInItemDto nextItemBooking = BookingMapper.bookingToBookingInItemDto(
                    bookingRepository.findTopByItemIdAndStartIsAfterAndStatus(item.getId(), new Date(), BookingStatus.APPROVED, sortAsc));
            BookingInItemDto lastItemBooking = BookingMapper.bookingToBookingInItemDto(
                    bookingRepository.findTopByItemIdAndStartIsBeforeAndStatus(item.getId(), new Date(), BookingStatus.APPROVED, sortDesc));
            itemsDtos.add(ItemMapper.toItemsDto(item, CommentMapper.commentsToCommentsDto(commentRepository.findByItemId(item.getId())), lastItemBooking, nextItemBooking));
        }
        return itemsDtos;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.search(text)
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item, null))
                    .collect(Collectors.toList());
        }
    }

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

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = ItemMapper.toItem(itemDto, null, user, null);
        return ItemMapper.toItemDto(itemRepository.save(item), null);
    }

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

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndStatusAndStartIsBefore(itemId, userId, BookingStatus.APPROVED, new Date());
        if (bookings.size() == 0) {
            throw new DataErrorException("booking not found");
        }
        Comment comment = CommentMapper.commentDtoToComment(commentDto, user, item);
        comment.setCreated(new Date());
        return CommentMapper.commentToCommentDto(commentRepository.save(comment));
    }
}
