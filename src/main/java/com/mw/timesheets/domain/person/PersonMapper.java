package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfiguration.class)
public interface PersonMapper extends CommonMapper<PersonEntity, PersonDTO> {

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToEntity")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToEntity")
    PersonEntity toEntity(PersonDTO dto);

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToDTO")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToDTO")
    PersonDTO toDto(PersonEntity entity);

    @Named("addressToDTO")
    AddressDTO addressToDTO(AddressEntity address);

    @Named("addressToEntity")
    AddressEntity addressToEntity(AddressDTO addressDTO);

    @Named("userToDTO")
    UserDTO userToDTO(UserEntity user);

    @Named("userToEntity")
    UserEntity userToEntity(UserDTO userDTO);

    BasicPersonDataDTO toBasicData(PersonEntity person);
}
