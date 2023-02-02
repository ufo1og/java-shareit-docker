package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentDto {
    @Positive
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
