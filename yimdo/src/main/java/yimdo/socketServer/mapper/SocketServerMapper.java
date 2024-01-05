package yimdo.socketServer.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import yimdo.socketServer.vo.SocketServerVo;
import yimdo.web.admin.weatherData.vo.WeatherDataVo;

@Mapper
public interface SocketServerMapper {

	void insertBreakerInfo(SocketServerVo socketServerVo) throws Exception;
	void insertPresenceReport(Map<String, String> map) throws Exception;
	void updateBreakerStatus(Map<String, String> map) throws Exception;
	String selectBreakerPolicy(String breakerId) throws Exception;
	void updateBreakerPolicy(Map<String, String> map) throws Exception;
	void updateBreakerHistory(Map<String, Object> map) throws Exception;
	void insertWeatherData(WeatherDataVo weatherDataVo) throws Exception;

}
