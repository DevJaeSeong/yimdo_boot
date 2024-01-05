package yimdo.web.admin.weatherData.vo;

import lombok.Data;

@Data
public class WeatherDataVo {

	private String num;
	private String breakerId;
	private String regDate;
	private String dir;
	private String tmp;
	private String hm;
	private String wind;
	private String gust;
	private String rain;
	private String uv;
	private String light;
}
