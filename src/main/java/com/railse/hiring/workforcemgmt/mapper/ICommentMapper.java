package com.railse.hiring.workforcemgmt.mapper;

import com.railse.hiring.workforcemgmt.dto.CommentDto;
import com.railse.hiring.workforcemgmt.model.Comment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ICommentMapper {
    CommentDto modelToDto(Comment comment);
    List<CommentDto> modelListToDtoList(List<Comment> comments);
}
