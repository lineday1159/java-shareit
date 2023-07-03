package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
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
import ru.practicum.shareit.mapper.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository mockItemRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    CommentRepository mockCommentRepository;
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    RequestRepository mockRequestRepository;

    ItemService itemService;

    Item item;
    ItemDto itemDto;
    User user;
    User user1;
    Booking nextBooking;
    Booking lastBooking;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(mockItemRepository, mockUserRepository,
                mockCommentRepository, mockBookingRepository, mockRequestRepository);
        user = new User(1L, "user1", "user1@email.ru");
        user1 = new User(2L, "user2", "user2@email.ru");
        item = new Item(1L, "item1", user, null, "description", true);
        nextBooking = new Booking(1L, new Date(), new Date(), item, user, BookingStatus.APPROVED);
        lastBooking = new Booking(2L, new Date(), new Date(), item, user, BookingStatus.WAITING);
        itemRequest = new ItemRequest(1L, "description", user1, new Date());
        itemDto = new ItemDto(1L, "item1", "description", true, 1L, null);
    }

    @Test
    void getItems() {
        when(mockItemRepository.findByOwnerId(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        when(mockBookingRepository.findTopByItemIdAndStartIsAfterAndStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBooking);

        when(mockBookingRepository.findTopByItemIdAndStartIsBeforeAndStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBooking);

        List<ItemsDto> itemsDtos = itemService.getItems(1L, 1, 1);

        Assertions.assertEquals(1, itemsDtos.size());
        Assertions.assertEquals(item.getName(), itemsDtos.get(0).getName());
        Assertions.assertEquals(item.getDescription(), itemsDtos.get(0).getDescription());
        Assertions.assertEquals(nextBooking.getId(), itemsDtos.get(0).getNextBooking().getId());
        Assertions.assertEquals(lastBooking.getId(), itemsDtos.get(0).getLastBooking().getId());
    }

    @Test
    void getItemsWithExc() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.getItems(1L, -1, 1));

        Assertions.assertEquals("Not correct page parameters", exception.getMessage());
    }

    @Test
    void searchItems() {
        when(mockItemRepository.search(Mockito.any(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(item)));


        List<ItemDto> itemDtos = itemService.searchItems("item", 1, 1);

        Assertions.assertEquals(1, itemDtos.size());
        Assertions.assertEquals(item.getName(), itemDtos.get(0).getName());
        Assertions.assertEquals(item.getDescription(), itemDtos.get(0).getDescription());
    }

    @Test
    void searchItemsWithExc() {

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> itemService.searchItems("test", -1, 1));

        Assertions.assertEquals("Not correct page parameters", exception.getMessage());
    }

    @Test
    void getItem() {
        when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        when(mockBookingRepository.findTopByItemIdAndStartIsAfterAndStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(nextBooking);

        when(mockBookingRepository.findTopByItemIdAndStartIsBeforeAndStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(lastBooking);
        //вызов сервиса владельцем вещи
        ItemsDto itemsDtos = itemService.getItem(1L, 1L);

        Assertions.assertEquals(item.getName(), itemsDtos.getName());
        Assertions.assertEquals(item.getDescription(), itemsDtos.getDescription());
        Assertions.assertEquals(nextBooking.getId(), itemsDtos.getNextBooking().getId());
        Assertions.assertEquals(lastBooking.getId(), itemsDtos.getLastBooking().getId());

        //вызов сервиса НЕ владельцем вещи
        itemsDtos = itemService.getItem(1L, 2L);

        Assertions.assertEquals(item.getName(), itemsDtos.getName());
        Assertions.assertEquals(item.getDescription(), itemsDtos.getDescription());
        Assertions.assertNull(itemsDtos.getNextBooking());
        Assertions.assertNull(itemsDtos.getLastBooking());
    }

    @Test
    void getItemWithExc() {

        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getItem(1L, 1L));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void createItem() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        when(mockRequestRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(itemRequest));

        when(mockItemRepository.save(Mockito.any()))
                .thenReturn(ItemMapper.toItem(itemDto, 1L, user, itemRequest));

        itemService.create(itemDto, 1L);

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .save(ItemMapper.toItem(itemDto, null, user, itemRequest));

    }

    @Test
    void createItemWithExcUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createItemWithExcReq() {

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        when(mockRequestRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Request not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        Assertions.assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void updateItem() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));

        when(mockItemRepository.save(Mockito.any()))
                .thenReturn(item);

        ItemDto newItemDto = new ItemDto(null, null, "newDescription", null, null, null);
        item.setDescription(newItemDto.getDescription());
        itemService.update(newItemDto, 1L, 1L);
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .save(item);

        newItemDto = new ItemDto(null, "newName", null, null, null, null);
        item.setName(newItemDto.getName());
        itemService.update(newItemDto, 1L, 1L);
        Mockito.verify(mockItemRepository, Mockito.times(2))
                .save(item);

        newItemDto = new ItemDto(null, null, null, true, null, null);
        item.setIsAvailable(newItemDto.getAvailable());
        itemService.update(newItemDto, 1L, 1L);
        Mockito.verify(mockItemRepository, Mockito.times(3))
                .save(item);
    }

    @Test
    void updateItemWithExcUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, 1L, 1L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateItemWithExcItem() {

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, 1L, 1L));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void saveComment() {
        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user1));

        when(mockItemRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(item));

        when(mockBookingRepository.findByItemIdAndBookerIdAndStatusAndStartIsBefore(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking(1L, new Date(), new Date(), item, user1, BookingStatus.APPROVED)));

        when(mockCommentRepository.save(Mockito.any()))
                .thenReturn(new Comment(1L, "text", item, user1, new Date()));

        CommentDto commentDto = itemService.createComment(1L, 1L, new CommentDto(null, "text", null, null));
        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .save(Mockito.any());
        Assertions.assertEquals("text", commentDto.getText());
        Assertions.assertEquals(user1.getName(), commentDto.getAuthorName());
    }

    @Test
    void saveCommentWithExcUser() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("User not found"));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(1L, 1L, new CommentDto(null, "text", null, null)));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void saveCommentWithExcItem() {

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        when(mockItemRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Item not found"));


        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.createComment(1L, 1L, new CommentDto(null, "text", null, null)));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void saveCommentWithExcBooking() {

        when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        when(mockItemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        when(mockBookingRepository.findByItemIdAndBookerIdAndStatusAndStartIsBefore(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        final DataErrorException exception = Assertions.assertThrows(
                DataErrorException.class,
                () -> itemService.createComment(1L, 1L, new CommentDto(null, "text", null, null)));

        Assertions.assertEquals("Booking not found", exception.getMessage());
    }
}