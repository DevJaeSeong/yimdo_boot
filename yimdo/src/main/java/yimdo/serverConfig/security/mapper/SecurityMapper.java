package yimdo.serverConfig.security.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import yimdo.serverConfig.security.vo.UserDetailVo;
import yimdo.serverConfig.security.vo.UserVo;

@Mapper
public interface SecurityMapper {

	UserVo getUserByIdForLogin(String userId) throws Exception;
	UserDetailVo getUserDetailById(String userId) throws Exception;
	void createAccount(Map<String, Object> map) throws Exception;
}
