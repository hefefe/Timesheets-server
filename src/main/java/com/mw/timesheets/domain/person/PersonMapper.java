package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.model.*;
import com.mw.timesheets.domain.person.type.Position;
import com.mw.timesheets.domain.person.type.Roles;
import org.mapstruct.*;

@Mapper(config = MapperConfiguration.class)
public interface PersonMapper extends CommonMapper<PersonEntity, PersonDTO> {

    @Override
    @Mapping(source = "user", target = "user", qualifiedByName = "userToEntity")
    @Mapping(target = "photo", ignore = true)
    PersonEntity toEntity(PersonDTO dto);

    @Override
    @Mapping(source = "user", target = "user", qualifiedByName = "userToDTO")
    PersonDTO toDto(PersonEntity entity);

    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateOfEmployment", ignore = true)
    PersonEntity updateEntity(PersonDTO dto, @MappingTarget PersonEntity person);

    @Named("userToDTO")
    UserDTO userToDTO(UserEntity user);

    @Named("userToEntity")
    UserEntity userToEntity(UserDTO userDTO);

    BasicPersonDataDTO toBasicData(PersonEntity person);
}
