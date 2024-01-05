package yimdo.web.general.account.mapper;

import org.apache.ibatis.annotations.Mapper;

import yimdo.web.general.account.vo.CreateAccountVo;

@Mapper
public interface AccountMapper {

	boolean selectAccountById(String userId) throws Exception;
	void createAccount(CreateAccountVo createAccountVo) throws Exception;
}
