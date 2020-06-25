package com.rahilhusain.hxevent.mappers;

import com.rahilhusain.hxevent.domain.User;
import com.rahilhusain.hxevent.security.LoggedInUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    LoggedInUser map(User source);
}
