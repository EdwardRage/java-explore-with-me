package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.dto.NewCommentParams;
import ru.practicum.ewm.events.EventMapper;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exception.CustomException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;


    @Override
    public CommentResponseDto create(NewCommentParams commentDto) {
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + commentDto.getUserId() + " не найден"));
        Event event = eventRepository.findById(commentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id = " + commentDto.getEventId() + " не найдено"));
        Comment comment = commentMapper.toComment(commentDto, user, event);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment, user, eventMapper.toEventShortDto(event));
    }

    @Override
    public CommentResponseDto updateComment(long userId, long commentId, String text) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));
        if (comment.getUser().getId() == userId) {
            comment.setText(text);
            commentRepository.save(comment);
            return commentMapper.mapToCommentDto(comment, eventMapper.toEventShortDto(comment.getEvent()));
        }
        throw new CustomException("Комментарий может изменить только автор комментария");
    }

    @Override
    public void deleteComment(long commentId, long userId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));

        if (comment.getUser().getId() == userId) {
            commentRepository.delete(comment);
        } else {
            throw new CustomException("Удалить комментарий может только автор комментария");
        }
    }

    @Override
    public List<CommentResponseDto> get(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));
        EventShortDto eventShortDto = eventMapper.toEventShortDto(event);
        return commentRepository.findAllByEventId(event.getId()).stream()
                .map(comment -> commentMapper.mapToCommentDto(comment, eventShortDto))
                .toList();
    }
}
