package com.sbs.springBootService.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sbs.springBootService.dto.GenFile;

@Mapper
public interface GenFileDao {
	void saveMeta(Map<String, Object> param);
	
	GenFile getGenFile(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId, @Param("typeCode") String typeCode, @Param("type2Code") String type2Code, @Param("fileNo") int fileNo);

	void changeRelId(@Param("id") int id, @Param("relId") int relId);
	
	void deleteFiles(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId);

	List<GenFile> getGenFilesByRelTypeCodeAndRelId(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId);
	
	List<GenFile> getGenFiles(@Param("relTypeCode") String relTypeCode, @Param("relId") int relId, @Param("typeCode") String typeCode, @Param("type2Code") String type2Code);

	void deleteFile(@Param("id") int id);
	
	GenFile getGenFileById(@Param("id") int id);
	
	List<GenFile> getGenFilesRelTypeCodeAndRelIdsAndTypeCodeAndType2Code(@Param("relTypeCode") String relTypeCode,
			@Param("relIds") List<Integer> relIds, @Param("typeCode") String typeCode,
			@Param("type2Code") String type2Code);
}
