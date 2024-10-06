package ru.practicum.ewm.comments;

import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.dto.NewCommentParams;

import java.util.List;

public interface CommentService {

    CommentResponseDto create(NewCommentParams commentDto);

    CommentResponseDto updateComment(long userId, long commentId, String text);

    void deleteComment(long commentId, long userId);

    List<CommentResponseDto> get(long eventId);
}
