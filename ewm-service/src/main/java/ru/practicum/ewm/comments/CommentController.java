package ru.practicum.ewm.comments;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.dto.NewCommentParams;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto create(@NotBlank @RequestBody String text,
                                     @PathVariable long eventId,
                                     @PathVariable long userId) {
        NewCommentParams commentParams = NewCommentParams.builder()
                .userId(userId)
                .eventId(eventId)
                .text(text)
                .build();
        CommentResponseDto comment = commentService.create(commentParams);
        log.info("Новый комментарий добавлен {}", comment);
        return comment;
    }

    @PatchMapping("/{userId}/events/comments/{commentId}")
    public CommentResponseDto update(@PathVariable long userId,
                                     @PathVariable long commentId,
                                     @NotBlank @RequestBody String text) {
        CommentResponseDto comment = commentService.updateComment(userId, commentId, text);
        log.info("Комментарий с id = {} обновлен", commentId);
        return comment;
    }

    @DeleteMapping("/{userId}/events/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId,
                       @PathVariable long userId) {
        commentService.deleteComment(commentId, userId);
        log.info("Комментарий с id = " + commentId + " удален");
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentResponseDto> get(@PathVariable long eventId) {
        List<CommentResponseDto> list = commentService.get(eventId);
        log.info("Комментарии для события с id = {} получены", eventId);
        return list;
    }
}
