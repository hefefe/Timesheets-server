package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.mapper.CommonMapper;
import com.mw.timesheets.commons.mapper.MapperConfiguration;
import com.mw.timesheets.domain.person.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfiguration.class)
public interface PersonMapper extends CommonMapper<PersonEntity, PersonDTO> {

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToEntity")
    @Mapping(source = "contact", target = "contact", qualifiedByName = "contactToEntity")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToEntity")
    PersonEntity toEntity(PersonDTO dto);

    @Override
    @Mapping(source = "address", target = "address", qualifiedByName = "addressToDTO")
    @Mapping(source = "contact", target = "contact", qualifiedByName = "contactToDTO")
    @Mapping(source = "user", target = "user", qualifiedByName = "userToDTO")
    PersonDTO toDto(PersonEntity entity);

    @Named("addressToDTO")
    @Mapping(source = "country", target = "country", qualifiedByName = "CountryToDTO")
    AddressDTO addressToDTO(AddressEntity address);

    @Named("addressToEntity")
    @Mapping(source = "country", target = "country", qualifiedByName = "countryToEntity")
    AddressEntity addressToEntity(AddressDTO addressDTO);

    @Named("contactToDTO")
    ContactDTO contactToDTO(ContactEntity contact);

    @Named("contactToEntity")
    ContactEntity contactToEntity(ContactDTO contactDTO);

    @Named("userToDTO")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToDTO")
    UserDTO userToDTO(UserEntity user);

    @Named("userToEntity")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToEntity")
    UserEntity userToEntity(UserDTO userDTO);

    @Named("roleToDTO")
    RoleDTO roleToDTO(RoleEntity role);

    @Named("roleToEntity")
    RoleEntity roleToEntity(RoleDTO roleDTO);

    @Named("CountryToDTO")
    CountryDTO countryToDTO(CountryEntity country);

    @Named("countryToEntity")
    CountryEntity countryToEntity(CountryDTO countryDTO);
}
