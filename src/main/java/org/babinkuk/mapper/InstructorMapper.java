package org.babinkuk.mapper;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Instructor;
import org.babinkuk.entity.InstructorDetail;
import org.babinkuk.vo.CourseVO;
import org.babinkuk.vo.InstructorVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * mapper for the entity @link {@link Instructor} and its DTO {@link InstructorVO}
 * 
 * @author BabinKuk
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class},
	uses = {InstructorDetailMapper.class}
)
public interface InstructorMapper {
	
	public InstructorMapper instructorMapperInstance = Mappers.getMapper(InstructorMapper.class);
	public InstructorDetailMapper instructorDetailMapperInstance = Mappers.getMapper(InstructorDetailMapper.class);
	
//	@BeforeMapping
//	default void beforeMapInstructorDetail(@MappingTarget Instructor entity, InstructorVO instructorVO) {
//		System.out.println(StringUtils.stripToEmpty("@BeforeMapping instructor: " + new Throwable().getStackTrace()[0].getFileName() + ":" + (new Throwable().getStackTrace()[0].getLineNumber())));
//		if (StringUtils.isNotBlank(instructorVO.getYoutubeChannel()) && StringUtils.isNotBlank(instructorVO.getHobby())) {
//			InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO);
//			instructorDetail.setInstructor(entity);
//			entity.setInstructorDetail(instructorDetail);
//			System.out.println(instructorDetail.toString());
//		}
//		System.out.println(entity.toString());
//	}
	
	@Named("setDetails")
	default InstructorDetail setDetails(InstructorVO instructorVO) {
		System.out.println(StringUtils.stripToEmpty("setDetails instructor: " + new Throwable().getStackTrace()[0].getFileName() + ":" + (new Throwable().getStackTrace()[0].getLineNumber())));
		// instructor details
		InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO);
		Instructor entity = new Instructor();
		entity.setId(instructorVO.getId());
		instructorDetail.setInstructor(entity);
		System.out.println(instructorDetail.toString());
		return instructorDetail;
	}
	
	@AfterMapping
	default void afterMapInstructor(@MappingTarget Instructor entity, InstructorVO instructorVO) {
		System.out.println(StringUtils.stripToEmpty("@AfterMapping instructor: " + new Throwable().getStackTrace()[0].getFileName() + ":" + (new Throwable().getStackTrace()[0].getLineNumber())));
		
		// instructor details
		InstructorDetail instructorDetail = instructorDetailMapperInstance.toEntity(instructorVO, entity);
		instructorDetail.setInstructor(entity);
		entity.setInstructorDetail(instructorDetail);
		//System.out.println(entity.toString());
	}
	
	// for insert
	@Named("toEntity")
//	@Mapping(source = "email", target = "email")
	@Mapping(source = "instructorVO", target = "instructorDetail", qualifiedByName = "setDetails")
	Instructor toEntity(InstructorVO instructorVO);
	
	// for update
	@Named("toEntity")
//	@Mapping(source = "email", target = "email")
	Instructor toEntity(InstructorVO instructorVO, @MappingTarget Instructor instructor);
	
	// when saving course
	@Named("toEntity")
	@Mapping(target = "firstName", ignore = true)
	@Mapping(target = "lastName", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "instructorDetail", ignore = true)
	Instructor toEntity(CourseVO courseVO);
    
	@Named("toVO")
//	@Mapping(source = "email", target = "email")
	//@Mapping(target = "courses.instructorVO", ignore= true)
	//@Mapping(target = "courses.reviewsVO", ignore= true)
	//@Mapping(target = "courses.studentsVO", ignore= true)
	InstructorVO toVO(Instructor instructor);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<InstructorVO> toVO(Iterable<Instructor> instructorLst);
	
	@AfterMapping
	default void setDetails(@MappingTarget InstructorVO instructorVO, Instructor entity) {
		// instructor details
		if (entity.getInstructorDetail() != null) {
			//System.out.println(entity.getInstructorDetail());
			instructorVO.setYoutubeChannel(entity.getInstructorDetail().getYoutubeChannel());
			instructorVO.setHobby(entity.getInstructorDetail().getHobby());
		}
	}
}