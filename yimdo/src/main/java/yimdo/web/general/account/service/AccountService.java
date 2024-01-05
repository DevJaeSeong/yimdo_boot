package yimdo.web.general.account.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import yimdo.web.general.account.mapper.AccountMapper;
import yimdo.web.general.account.vo.CreateAccountVo;

@Service
@Slf4j
public class AccountService {
	
	private AccountMapper accountMapper;
	
	public AccountService(AccountMapper accountMapper) {

		this.accountMapper = accountMapper;
	}
	
	/**
	 * DB에 해당ID으로 등록된 계정이 있는지 확인.
	 * 
	 * @param userId 찾으려는 계정ID
	 * @return 있다면 true
	 * 		   없다면 false
	 */
	public boolean isExistsAccount(String userId) {

		boolean isExists = true;
		
		try {
			
			isExists = accountMapper.selectAccountById(userId);
			
		} catch (Exception e) {

			log.error("계정 조회도중 에러발생.");
			e.printStackTrace();
		}
		
		return isExists;
	}

	public boolean createAccount(CreateAccountVo createAccountVo) {

		try {
			
			accountMapper.createAccount(createAccountVo);
			return true;
			
		} catch (Exception e) {
			
			log.debug("DB에 계정 등록도중 에러 발생.");
			e.printStackTrace();
		}
		
		return false;
	}

}
