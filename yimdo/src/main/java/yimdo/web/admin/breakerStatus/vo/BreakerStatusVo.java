package yimdo.web.admin.breakerStatus.vo;

import java.util.List;

import lombok.Data;

@Data
public class BreakerStatusVo {

	private List<String> selectedBreakers;
	private String selectedElement;
	private String selectedPolicy;
	private String modifyDetail;
	private String modifier;
	private String systemControl;
}
