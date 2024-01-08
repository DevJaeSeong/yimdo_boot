package yimdo.ftpServer;

import java.io.IOException;
import java.util.Arrays;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import yimdo.serverConfig.server.ServerConfig;

@Component
@Slf4j
public class FtpServerManager {
	
	private SaltedPasswordEncryptor saltedPasswordEncryptor = new SaltedPasswordEncryptor();
	
	public void openServer() {
		
		FtpServer ftpServer = null;
		
		try {
			
			ftpServer = createFtpServer();
			ftpServer.start();
			log.debug("\"{}\" 포트로 FTP 서버 시작.", ServerConfig.FTP_PORT);
			
			Thread.sleep(Long.MAX_VALUE);
			
		} catch (Exception e) {

			log.error("FTP 서버 문제 발생.");
			e.printStackTrace();
			
		} finally {
			
            if (ftpServer != null && !ftpServer.isStopped()) {
            	
                ftpServer.stop();
                log.debug("FTP 서버 종료.");
            }
		}
	}

	/**
	 * ftp 서버 생성.
	 * 
	 * @return new {@link DefaultFtpServer}
	 * @throws IOException
	 * @throws FtpException
	 */
	private FtpServer createFtpServer() throws IOException, FtpException {
		
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(ServerConfig.FTP_PORT);
        
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        ftpServerFactory.addListener("default", listenerFactory.createListener());
        addUser(ftpServerFactory);
        
		return ftpServerFactory.createServer();
	}

	private void addUser(FtpServerFactory ftpServerFactory) throws IOException, FtpException {
		
        PropertiesUserManagerFactory propertiesUserManagerFactory = new PropertiesUserManagerFactory();
        propertiesUserManagerFactory.setPasswordEncryptor(saltedPasswordEncryptor);
        
        BaseUser user = new BaseUser();
        user.setName("yimdo");
        user.setPassword(saltedPasswordEncryptor.encrypt("yimdo"));
        user.setHomeDirectory("/opt/Yimdo/ftp");
        user.setAuthorities(Arrays.asList(new WritePermission()));
        
        UserManager userManager = propertiesUserManagerFactory.createUserManager();
        userManager.save(user);
        
        ftpServerFactory.setUserManager(userManager);
	}
}
