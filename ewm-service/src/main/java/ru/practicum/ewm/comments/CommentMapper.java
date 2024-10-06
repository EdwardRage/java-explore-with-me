package ru.practicum.ewm.comments;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.comments.dto.CommentResponseDto;
import ru.practicum.ewm.comments.dto.NewCommentParams;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserShortDto;

@Component
public class CommentMapper {

    public Comment toComment(NewCommentParams commentDto, User user, Event event) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(commentDto.getText());
        return comment;
    }

    public CommentResponseDto toCommentDto(Comment comment, User user, EventShortDto eventShortDto) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(comment.getId());
        commentResponseDto.setUser(toUserShortDto(user));
        commentResponseDto.setEvent(eventShortDto);
        commentResponseDto.setText(comment.getText());
        return commentResponseDto;
    }

    public CommentResponseDto mapToCommentDto(Comment comment, EventShortDto eventShortDto) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(comment.getId());
        commentResponseDto.setUser(toUserShortDto(comment.getUser()));
        commentResponseDto.setEvent(eventShortDto);
        commentResponseDto.setText(comment.getText());
        return commentResponseDto;
    }

    private UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setId(user.getId());
        userShortDto.setName(user.getName());
        return userShortDto;
    }
}
