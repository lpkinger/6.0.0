package com.uas.erp.controller.common;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.DataListWrap;
import com.uas.erp.model.DumpFile;
import com.uas.erp.model.FARepSet;
import com.uas.erp.model.FormWrap;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.JProcessWrap;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.model.SearchTemplate;
import com.uas.erp.model.SubsFormula;
import com.uas.erp.model.SubsNum;
import com.uas.erp.model.UpdateScheme;
import com.uas.erp.model.VoucherStyle;
import com.uas.erp.service.common.ChartsService;
import com.uas.erp.service.common.InitService;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.fa.FARepSetService;
import com.uas.erp.service.fa.VoucherCreateService;
import com.uas.erp.service.ma.MADataListService;
import com.uas.erp.service.ma.MAFormService;
import com.uas.erp.service.ma.RelativeSearchService;
import com.uas.erp.service.ma.SearchTemplateService;
import com.uas.erp.service.ma.UpdateSchemeService;

/**
 * 数据工具
 * 
 * @author yingp
 * 
 */
@Controller
@RequestMapping("/common/dump")
public class DumpController extends BaseController {

	@Autowired
	private UpdateSchemeService updateSchemeService;

	@Autowired
	private RelativeSearchService relativeSearchService;

	@Autowired
	private SearchTemplateService searchTemplateService;

	@Autowired
	private FARepSetService farepSetService;

	@Autowired
	private VoucherCreateService voucherCreateService;

	@Autowired
	private ChartsService chartsService;

	@Autowired
	private ProcessService processService;

	@Autowired
	private InitService initService;

	@Autowired
	private MAFormService formService;

	@Autowired
	private MADataListService dataListService;

	/**
	 * 导出
	 */
	@RequestMapping("/exp.action")
	public ResponseEntity<byte[]> export(String type, String identity) {
		DumpType dumpType = DumpType.valueOf(type);
		Object content = null;
		String desc = null;
		switch (dumpType) {
		case UpdateScheme:
			content = updateSchemeService.exportUpdateScheme(identity);
			desc = DumpType.UpdateScheme.desc() + "-" + identity;
			break;
		case RelativeSearch:
			content = relativeSearchService.getRelativeSearch(Integer.parseInt(identity));
			desc = DumpType.RelativeSearch.desc() + "-" + identity;
			break;
		case SearchTemplate:
			content = searchTemplateService.exportSearchTemplates(identity);
			desc = DumpType.SearchTemplate.desc() + "-" + identity;
			break;
		case FARepSet:
			content = farepSetService.getFARepSet(Integer.parseInt(identity));
			desc = DumpType.FARepSet.desc() + "-" + identity;
			break;
		case VoucherStyle:
			JSONObject params = JSON.parseObject(identity);
			content = voucherCreateService.getVoucherStyleByClass(params.getIntValue("vs_id"), params.getString("vd_class"));
			desc = DumpType.VoucherStyle.desc() + "-" + params.getString("vd_class");
			break;
		case SubsNum:
			content = chartsService.getSubsNum(Integer.parseInt(identity));
			desc = DumpType.SubsNum.desc() + "-" + identity;
			break;
		case SubsFormula:
			content = chartsService.getSubsFormula(Integer.parseInt(identity));
			desc = DumpType.SubsFormula.desc() + "-" + identity;
			break;
		case JProcessDeploy:
			content = processService.getJProcessWrap(Integer.parseInt(identity));
			desc = DumpType.JProcessDeploy.desc() + "-" + identity;
			break;
		case Import:
			content = initService.getInitDetails(identity);
			desc = DumpType.Import.desc() + "-" + identity;
			break;
		case Form:
			JSONObject params2 = JSON.parseObject(identity);
			Object formIds = params2.get("form");
			Object gridCallers = params2.get("grid");
			content = formService.exportForms(null == formIds ? new String[] {} : formIds.toString().split(","),
					null == gridCallers ? new String[] {} : gridCallers.toString().split(","));
			desc = DumpType.Form.desc() + "-" + formIds;
			break;
		case DataList:
			content = dataListService.exportDataList(Integer.parseInt(identity));
			desc = DumpType.DataList.desc() + "-" + identity;
			break;
		default:
			break;
		}
		DumpFile file = new DumpFile(type, desc, content);
		String fileName = String.format("%s-%s-%s.json", file.getFrom(), desc, file.getExpDate());
		return outputStream(fileName, StringUtils.getBytesUtf8(JSON.toJSONString(file)));
	}

	/**
	 * 导入
	 */
	@RequestMapping("/imp.action")
	@ResponseBody
	public ModelMap imp(String jsonData) {
		DumpFile file = JSON.parseObject(jsonData, DumpFile.class);
		DumpType dumpType = DumpType.valueOf(file.getType());
		String content = JSON.toJSONString(file.getContent());
		switch (dumpType) {
		case UpdateScheme:
			updateSchemeService.importUpdateScheme(JSON.parseObject(content, UpdateScheme.class));
			break;
		case RelativeSearch:
			relativeSearchService.saveRelativeSearch(JSON.parseObject(content, RelativeSearch.class));
			break;
		case SearchTemplate:
			searchTemplateService.saveSearchTemplates(JSON.parseArray(content, SearchTemplate.class));
			break;
		case FARepSet:
			farepSetService.saveFARepSet(JSON.parseObject(content, FARepSet.class));
			break;
		case VoucherStyle:
			voucherCreateService.saveVoucherStyle(JSON.parseObject(content, VoucherStyle.class));
			break;
		case SubsNum:
			chartsService.saveSubsNum(JSON.parseObject(content, SubsNum.class));
			break;
		case SubsFormula:
			chartsService.saveSubsFormula(JSON.parseObject(content, SubsFormula.class));
			break;
		case JProcessDeploy:
			processService.saveJProcessWrap(JSON.parseObject(content, JProcessWrap.class));
			break;
		case Import:
			initService.importInitDetail(JSON.parseArray(content, InitDetail.class));
			break;
		case Form:
			formService.importForms(JSON.parseObject(content, FormWrap.class));
			break;
		case DataList:
			dataListService.importDataList(JSON.parseObject(content, DataListWrap.class));
			break;
		default:
			return error(404, "不支持的导入格式");
		}
		return success();
	}

	private enum DumpType {
		/**
		 * 更新方案
		 */
		UpdateScheme("更新方案"),
		/**
		 * 关联查询
		 */
		RelativeSearch("关联查询"),
		/**
		 * 查询方案
		 */
		SearchTemplate("查询方案"),
		/**
		 * 报表公式
		 */
		FARepSet("报表公式"),
		/**
		 * 凭证公式
		 */
		VoucherStyle("凭证公式"),
		/**
		 * 订阅号
		 */
		SubsNum("订阅号"),
		/**
		 * 订阅项
		 */
		SubsFormula("订阅项"),
		/**
		 * 流程
		 */
		JProcessDeploy("流程"),
		/**
		 * 导入方案
		 */
		Import("导入方案"),
		/**
		 * 表单
		 */
		Form("表单"),
		/**
		 * 列表
		 */
		DataList("列表");

		private final String desc;

		private DumpType(String desc) {
			this.desc = desc;
		}

		public String desc() {
			return this.desc;
		}
	}

}
