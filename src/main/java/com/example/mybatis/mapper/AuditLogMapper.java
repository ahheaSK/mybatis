package com.example.mybatis.mapper;

import com.example.mybatis.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper {

    int insert(AuditLog auditLog);
}
