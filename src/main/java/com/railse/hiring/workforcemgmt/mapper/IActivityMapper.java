package com.railse.hiring.workforcemgmt.mapper;

import com.railse.hiring.workforcemgmt.dto.ActivityDto;
import com.railse.hiring.workforcemgmt.model.Activity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IActivityMapper {
    ActivityDto modelToDto(Activity activity);
    List<ActivityDto> modelListToDtoList(List<Activity> activities);
}
