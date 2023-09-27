package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Address;
import org.babinkuk.entity.Student;
import org.babinkuk.vo.StudentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Address} and its DTO {@link InstructorDetailVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class}
	//if needed add uses = {add different classes for complex objects} 
)
public interface AddressMapper {
	
	public AddressMapper addressMapperInstance = Mappers.getMapper(AddressMapper.class);
	
	@Named("toEntity")
	@Mapping(source = "studentVO.street", target = "street")
	@Mapping(source = "studentVO.city", target = "city")
	@Mapping(source = "studentVO.zipCode", target = "zipCode")
	Address toEntity(StudentVO studentVO, Student student);
	
	@Named("toEntity")
	@Mapping(source = "studentVO.street", target = "street")
	@Mapping(source = "studentVO.city", target = "city")
	@Mapping(source = "studentVO.zipCode", target = "zipCode")
	Address toEntity(StudentVO studentVO);
		
}