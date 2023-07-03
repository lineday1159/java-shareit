package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    UserRepository mockUserRepository;
    @Mock
    ItemRepository mockItemRepository;
    @Mock
    RequestRepository mockRequestRepository;
    RequestService requestService;
    User user;
    Item item;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl(mockUserRepository, mockItemRepository, mockRequestRepository);
        user = new User(1L, "user1", "user1@email.ru");
        item = new Item(1L, "item1", user, null, "description", true);
    }

    @Test
    void create() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "Test", 1L, new Date());
        ItemRequest itemRequest = new ItemRequest(1L, "Test", user, new Date());
        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());
        when(mockRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto itemRequestDto1 = requestService.create(itemRequestDto, user.getId());
        Assertions.assertEquals(itemRequest.getId(), itemRequestDto1.getId());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.create(itemRequestDto, 99L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItems() {
        ItemRequest itemRequest = new ItemRequest(1L, "Test", user, new Date());
        Item item1 = new Item(1L, "item1", user, itemRequest, "description", true);
        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());
        when(mockRequestRepository.findAllByRequesterId(Mockito.anyLong()))
                .thenReturn(List.of(itemRequest));
        when(mockItemRepository.findByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(item1));
        List<ItemRequestWithItemDto> itemRequestDto1 = requestService.getItems(user.getId());
        Assertions.assertEquals(itemRequest.getId(), itemRequestDto1.get(0).getId());
        Assertions.assertEquals(item1.getId(), itemRequestDto1.get(0).getItems().get(0).getId());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getItems(99L));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getItemsWithPagination() {
        ItemRequest itemRequest = new ItemRequest(1L, "Test", user, new Date());
        Item item1 = new Item(1L, "item1", user, itemRequest, "description", true);
        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());
        when(mockRequestRepository.findAllByRequesterIdNot(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(mockItemRepository.findByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(item1));
        List<ItemRequestWithItemDto> itemRequestDto1 = requestService.getItemsWithPagination(user.getId(), 0, 20);
        Assertions.assertEquals(itemRequest.getId(), itemRequestDto1.get(0).getId());
        Assertions.assertEquals(item1.getId(), itemRequestDto1.get(0).getItems().get(0).getId());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getItemsWithPagination(99L, 0, 20));

        Assertions.assertEquals("User not found", exception.getMessage());

        ValidationException valException = Assertions.assertThrows(
                ValidationException.class,
                () -> requestService.getItemsWithPagination(user.getId(), -1, 20));

        Assertions.assertEquals("Not correct page parameters", valException.getMessage());
    }

    @Test
    void getItemById() {
        ItemRequest itemRequest = new ItemRequest(1L, "Test", user, new Date());
        Item item1 = new Item(1L, "item1", user, itemRequest, "description", true);
        when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(mockUserRepository.findById(99L))
                .thenReturn(Optional.empty());
        when(mockRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        when(mockRequestRepository.findById(99L))
                .thenReturn(Optional.empty());
        when(mockItemRepository.findByRequestId(Mockito.anyLong()))
                .thenReturn(List.of(item1));
        ItemRequestWithItemDto itemRequestDto1 = requestService.getItemById(1L, user.getId());
        Assertions.assertEquals(itemRequest.getId(), itemRequestDto1.getId());
        Assertions.assertEquals(item1.getId(), itemRequestDto1.getItems().get(0).getId());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getItemById(1L, 99L));

        Assertions.assertEquals("User not found", exception.getMessage());

        exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.getItemById(99L, 1L));

        Assertions.assertEquals("Request not found", exception.getMessage());
    }
}