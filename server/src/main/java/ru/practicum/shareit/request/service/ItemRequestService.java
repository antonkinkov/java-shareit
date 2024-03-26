package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getAllByUser(Long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getRequestById(long userId, long requestId);
}
