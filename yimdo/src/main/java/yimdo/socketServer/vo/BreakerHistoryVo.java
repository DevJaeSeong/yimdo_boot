package yimdo.socketServer.vo;

import lombok.Data;

@Data
public class BreakerHistoryVo {

	private int breakerHistoryId;
	private String yimdoCode;
	private String yimdoName;
	private String breakerId;
	private String breakerPolicyCode;
	private String breakerPolicyName;
	private String modifyReqDate;
	private String modifyReciveDate;
	private String modifyResDate;
	private String modifier;
	private String elementCode;
	private String elementName;
	private String modifyDetail;
	private String dbReqDate;
	private String systemControl;
}
