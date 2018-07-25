// @author suntg
// @date 2014年11月19日17:39:28
;var caller = getUrlParam('caller');
var formCondition = getUrlParam('formCondition')?getUrlParam('formCondition').replace('IS', '='):'';
var gridCondition = getUrlParam('gridCondition')?getUrlParam('gridCondition').replace('IS', '='):'';
var _readOnly = getUrlParam('_readOnly');// 只读标示
var title;
var baseInfo = {};//基本信息存放在这个类
var detailsInfo = [];//明细信息存放在这个数组
var detailInfo = {};//存放原始的一个明细行信息
var detailIndex = 0;//明细索引，方便对明细做操作
var buttons = [];//按钮组，存放当前包含的所有按钮
var mainField;//明细表的主表主键字段
var keyField;//主表中主键字段
var dbfinds;//用于存储明细表中dbfind的字段对照关系,结构与form dbfind请求返回的dbfinds结构一致

$(document).ready(function(){

	//与其它页面不同，必须传一个caller
	var saveUrl = 'common/saveCommon.action?caller=' +caller;
	var deleteUrl = 'common/deleteCommon.action?caller=' +caller;
	var updateUrl = 'common/updateCommon.action?caller=' +caller;
	var auditUrl = 'common/auditCommon.action?caller=' +caller;
	var printUrl = 'common/printCommon.action?caller=' +caller;
	var resAuditUrl = 'common/resAuditCommon.action?caller=' +caller;
	var submitUrl = 'common/submitCommon.action?caller=' +caller;
	var bannedUrl = 'common/bannedCommon.action?caller='+caller;
	var resBannedUrl = 'common/resBannedCommon.action?caller='+caller;
	var endUrl = 'common/endCommon.action?caller='+caller;
	var resEndUrl = 'common/resEndCommon.action?caller='+caller;
	var resSubmitUrl = 'common/resSubmitCommon.action?caller=' +caller;
	var getIdUrl = 'common/getCommonId.action?caller=' +caller;
	var onConfirmUrl = 'common/ConfirmCommon.action?caller=' +caller;

	//取编号 result-form的返回结果 table-表名	param-codeField 编号字段	 	
	var getRandomNumber = function(result, table, type, codeField) {
		table = table == null ? result.tablename : table;
		type = type == null ? 2 : type;
		codeField = codeField == null ? result.codeField : codeField;
		$.ajax({
			type: 'POST',
    		url: basePath + 'common/getCodeString.action',
    		async: false,
    		data: {
	   			caller: caller,//如果table==null，则根据caller去form表取对应table
	   			table: table,
	   			type: type
	   		},
    		success: function(responseText) {
    			judgeException(responseText, function(){
    				$('[name="'+codeField+'"]').val(responseText.code);
    			});
    		}
		});
	};

	//类 -> JSON
	var object2JSON = function(obj) {
		var result = '';
		result = '{';
		$.each(obj, function(b, ba){
			if(!ba.ignore) {
				result += '"'+b+'":"'+ba.value+'",';
			}
		});
		if(result.length>1) result = result.substr(0, result.length-1);
		result += '}';
		return result;
	};

	//数组 -> JSON
	var array2JSON = function(arr) {
		var result = '';
		result += '[';
		$.each(arr, function(d, detail){
			result += '{';
			$.each(detail, function(i, de){
				if(!de.ignore) {
					result += '"'+i+'":"'+de.value+'",';
				}
			});
			if(result.length>2) result = result.substr(0, result.length-1);
			result += '}';
			if(i<(arr.length-1)) result += ',';
		});
		result += ']';
		return result;
	};

	//保存请求
	var save = function(base, details){
		var formStore = '';
		var param = '';
		formStore = unescape(escape(object2JSON(base)));
		param = unescape(escape(array2JSON(details)));
		$.ajax({
			type: 'POST',
    		url: basePath + saveUrl,
    		async: false,
    		data: {
	   			formStore: formStore,
	   			param: param
	   		},
    		success: function(responseText) {
    			judgeException(responseText, function(){
    				dialog.show('保存成功', '单据已成功保存,正在跳转...', 2, function() {
						dialog.hide();
						window.location.href = basePath+'jsps/mobile/commonForm/commonForm.jsp?caller='+
							caller+'&formCondition='+keyField+'IS'+responseText.id+'&gridCondition='+
							(mainField?mainField:'null')+'IS'+responseText.id;
					});
    			});
    		}
		});
	};

	//更新请求
	var update = function(base, details){
		var formStore = '';
		var param = '';
		formStore = unescape(escape(object2JSON(base)));
		param = unescape(escape(array2JSON(details)));
		$.ajax({
			type: 'POST',
    		url: basePath + updateUrl,
    		async: false,
    		data: {
	   			formStore: formStore,
	   			param: param
	   		},
    		success: function(responseText) {
    			judgeException(responseText, function(){
    				dialog.show('更新成功', '单据已成功更新,正在跳转...', 2, function() {
						dialog.hide();
						window.location.href = basePath+'jsps/mobile/commonForm/commonForm.jsp?caller='+
							caller+'&formCondition='+keyField+'IS'+responseText.id+'&gridCondition='+
							(mainField?mainField:'null')+'IS'+responseText.id;
					});
    			});
    		}
		});
	};

	//指定待处理人
	var showAssignWin = function(assins, nodeId) {
		$('#dbFindModal').modal('show');
		$('#myModalLabel').text('请指定节点处理人');
		var html = '';
		$.each(assins, function(a, as){
			var value = as.substring(as.lastIndexOf('(')+1, as.length-1);
			html += '<label class="radio-inline text-center" style="width:80%;margin-left:10px;margin-bottom:5px;">'+
						'<input type="radio" name="assiner" value="'+value+'">'+as+
						'<span class="glyphicon glyphicon-ok" style="display:none;">'+
					'</label>';
		});
		$('#dbFindModal .modal-body').html(html);
		var buttonHtml = '<button type="button" class="btn btn-default" id="assin">指定</button>'+
			'<button type="button" class="btn btn-default" id="cancelAssin">取消</button>';
		$('#dbFindModal .modal-footer').html(buttonHtml);
		radioLabelClick($('.modal-body .radio-inline'));
		$('#assin').click(function(){//指定
			var value = getRadioValue('assiner');//单选框按钮值
			if(value) {
				$.ajax({
					type: 'POST',
		    		url: basePath + 'common/takeOverTask.action',
		    		async: false,
		    		data: {
			   			em_code: value,
						nodeId: nodeId
			   		},
		    		success: function(responseText) {
	    				if(responseText.success){
	    					$('#dbFindModal').modal('hide');
	    					dialog.show('提示', '指定成功',2, function(){
	    						window.location.reload();
	    					});
	    				} else {
	    					dialog.show('提示', '指定失败',2, function(){
	    						window.location.reload();
	    					});
	    				}
		    		}
				});
			} else {//没有选择指定人
				$('#dbFindModal').modal('hide');
				dialog.show('提示', '请指定节点处理人',1, function(){
					dialog.hide();
					$('#dbFindModal').modal('show');
				});
			}
		});
		$('#cancelAssin').click(function(){//取消按钮
			$('#dbFindModal').modal('hide');
			window.location.reload();
		});
	};
	

	//getSingleForm 获取form信息
	var getSingleForm = function(caller,condition) {
		setLoading(true);
//		$.post(basePath + 'common/singleFormItems.action', {
//			caller: caller,
//			condition: formCondition,
//			_noc: 1
//		}, function(result) {
//			setLoading(false);
//			judgeException(result, parseSingleForm(result));
//		});
		$.ajax({
			url:basePath + 'common/singleFormItems.action',
			type: 'POST',
			data: {caller: caller, condition: formCondition, _noc: 1},
			success: function(result){
				setLoading(false);
				judgeException(result, parseSingleForm(result));
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseJSON) {
					var response = xhr.responseJSON;
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
		
		
	};

	// parseSingleForm
	var parseSingleForm = function(result) {
		var html = '';
		title = result.title;
		document.title = result.title + '-优软管理系统';
		$('#handler_name').text(result.title);
		if(result.fo_detailMainKeyField) mainField = result.fo_detailMainKeyField;
		keyField = result.keyField;
		var items = result.items, ids = [];
		for(i in items) {
			var item = items[i];
			if(ids.indexOf(item.id) == -1) {//避免出现因多字段出现字段重复
				ids.push(item.id);
				if(item.secondname && item.secondname != ''){
					ids.push(item.secondname);
				}
				html +=  parseFormItem(item, result);
			}
		}
		$('#baseinfo form').html(html);
		shrinkLabel($('#baseinfo .fieldlabel label'));
		radioLabelClick('#baseinfo .radio-inline');
		addDatetimepicker();
		dbFindButtons($('#baseinfo .dbFind'), 'form');
		initFormValidation($('#baseinfo'));
		if(result.data) {//查看单据的情况下，加载单据的数据
			initBaseInfo(eval('('+result.data+')'));
			parseObjToForm($('#baseinfo'), baseInfo, false);
		}
		// 处理按钮，只读情况下不显示按钮
		if (result.buttons && !_readOnly) {
			buttons = result.buttons.split('#');
			parseButtons(buttons, result);
		};
	};

	//parseButtons 加载按扭组
	//params: buttons 按钮字符串数组
	var parseButtons = function(buttons, result){
		$.each(buttons, function(b, button){
			$('#'+button).parent().css('display', 'table-cell');
		});

		//保存按钮
		$('#erpSaveButton').click(function(){
			//没有输入编号自动向后台获取并设值
			if(result.codeField && $('[name="'+result.codeField+'"]') && 
				($('[name="'+result.codeField+'"]').val()=='' || $('[name="'+result.codeField+'"]').val()==null)) {
				getRandomNumber(result);
			}
			//设ID号
			if(result.keyField && $('[name="'+result.keyField+'"]') && 
				($('[name="'+result.keyField+'"]').val()=='' || $('[name="'+result.keyField+'"]').val()==null)) {
				var keyId = getSeqId(getIdUrl);
				$('[name="'+result.keyField+'"]').val(keyId);
			}
			if(validateForm($('#baseinfo'))){//验证
				//空的数字型字段设值为0
				$.each($('input[type="number"], textarea[type="number"]', $('#baseinfo')), function(i, input){
					if ($(input).val()=='' || $(input).val()==null) {
						$(input).val(0);
					};
				});
				//codeField 值强制大写,自动过滤特殊字符
				var codeFieldValue =  $('[name="'+result.codeField+'"]').val();
				codeFieldValue = codeFieldValue.trim().toUpperCase().replace(/[!@#$%^&*()'":,\/?]/, '');
				$('[name="'+result.codeField+'"]').val(codeFieldValue);
				baseInfo = parseFormToObj($('#baseinfo'), baseInfo);
				if(detailsInfo.length > 0) {
					$.each(detailsInfo, function(d, de) {
						if(!de[mainField]) de[mainField] = {value:'',nacessary:false,ignore:false};
						de[mainField].value = baseInfo[result.keyField].value;
					})
				}
				save(baseInfo, detailsInfo);
			}
		});
		//删除按钮
		$('#erpDeleteButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + deleteUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			judgeException(responseText, function(){
	    				dialog.show('删除成功', '单据已成功删除,请返回!');
	    			});
	    		},
	    		error: function(xhr){
					if(xhr.response) {
						var response = $.parseJSON(xhr.response);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog('获取用户信息失败', '请先登录！');
						} else {
							dialog('错误', response.exceptionInfo);
						}
					}
				}
			});
		});
		//更新按钮
		$('#erpUpdateButton').click(function(){
			if(validateForm($('#baseinfo'))){//验证
				//空的数字型字段设值为0
				$.each($('input[type="number"], textarea[type="number"]', $('#baseinfo')), function(i, input){
					if ($(input).val()=='' || $(input).val()==null) {
						$(input).val(0);
					};
				});
				//codeField 值强制大写,自动过滤特殊字符
				var codeFieldValue =  $('[name="'+result.codeField+'"]').val();
				codeFieldValue = codeFieldValue.trim().toUpperCase().replace(/[!@#$%^&*()'":,\/?]/, '');
				$('[name="'+result.codeField+'"]').val(codeFieldValue);
				baseInfo = parseFormToObj($('#baseinfo'), baseInfo);
				if(detailsInfo.length > 0) {
					$.each(detailsInfo, function(d, de) {
						if(!de[mainField]) de[mainField] = {value:'',nacessary:false,ignore:false};
						de[mainField].value = baseInfo[result.keyField].value;
					})
				}
				update(baseInfo, detailsInfo);
			}
		});

		//新增按钮
		$('#erpAddButton').click(function(){
			window.location.href = basePath+'jsps/mobile/commonForm/commonForm.jsp?caller='+caller;
		});

		//提交按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'ENTERING') {
			$('#erpSubmitButton').parent().css('display', 'none');
		}
		$('#erpSubmitButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + submitUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			if(responseText.success){ //跳转之前  若节点指定多人则指定候选人
						$.ajax({
							type: 'POST',
				    		url: basePath + 'common/getMultiNodeAssigns.action',
				    		async: false,
				    		data: {
					   			id: baseInfo[keyField].value,
					   			caller: caller
					   		},
				    		success: function(responseTextText) {
				    			judgeException(responseTextText, function(){
				    				//下一个节点多处理人，可选定处理人
				    				//选定了下一节点处理人之后发送请求将被选中的人设置为下一个节点，并发送通知
				    				//若取消选定则只向每个候选人发送通知
									if(responseTextText.MultiAssign){
										showAssignWin(responseTextText.assigns, responseTextText.nodeId);
									} else {
										dialog.show('提交成功', '单据已成功提交', 2, function(){
											window.location.reload();
										});
									}
				    			});
				    		},
				    		error: function(xhr){
								if(xhr.response) {
									var response = $.parseJSON(xhr.response);
									if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
										dialog('获取用户信息失败', '请先登录！');
									} else {
										dialog('错误', response.exceptionInfo);
									}
								}
							}
						});
	    			} else {
	    				if(responseText.exceptionInfo){
							var str = responseText.exceptionInfo;
							//特殊情况:操作成功，但是出现警告,允许刷新页面
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
								str = str.replace('AFTERSUCCESS', '');
								dialog.show('操作成功，提示:', str, 2, function(){
									dialog.hide();
									$.ajax({
										type: 'POST',
							    		url: basePath + 'common/getMultiNodeAssigns.action',
							    		async: false,
							    		data: {
								   			id: baseInfo[keyField].value,
								   			caller: caller
								   		},
							    		success: function(responseTextText) {
							    			judgeException(responseTextText, function(){
							    				//下一个节点多处理人，可选定处理人
							    				//选定了下一节点处理人之后发送请求将被选中的人设置为下一个节点，并发送通知
							    				//若取消选定则只向每个候选人发送通知
												if(responseTextText.MultiAssign){
													showAssignWin(responseTextText.assigns, responseTextText.nodeId);
												} else {
													dialog.show('提交成功', '单据已成功提交', 2, function(){
														window.location.reload();
													});
												}
							    			});
							    		}
									});
								});
							} else {
								dialog.show('出现异常', '请重新尝试!');
							}
						}
	    			}
	    		}
			});
		});

		//反提交按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'COMMITED') {
			$('#erpResSubmitButton').parent().css('display', 'none');
		}
		$('#erpResSubmitButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + resSubmitUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			judgeException(responseText, function(){//跳转之前  若节点指定多人则指定候选人
						if(responseText.success) {
							dialog.show('反提交成功', '正在重新加载页面', 2, function(){
								window.location.reload();
							});
						}
	    			});
	    		},
	    		error: function(xhr){
					if(xhr.response) {
						var response = $.parseJSON(xhr.response);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog('获取用户信息失败', '请先登录！');
						} else {
							dialog('错误', response.exceptionInfo);
						}
					}
				}
			});
		});

		//审核按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'COMMITED') {
			$('#erpAuditButton').parent().css('display', 'none');
		}
		$('#erpAuditButton').click(function(){
			//清除流程(什么意思我也不懂)
			$.ajax({
				type: 'POST',
	    		url: basePath + 'common/deleteProcessAfterAudit.action',
	    		async: false,
	    		data: {
		   			keyValue: baseInfo[keyField].value,
					caller: caller,
					_noc:1
		   		},
	    		success: function(responseText) {
	    			//似乎不用干什么
	    		}
			});
			$.ajax({
				type: 'POST',
	    		url: basePath + auditUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			if(responseText.success) {
	    				dialog.show('审核成功', '正在重新加载...', 2, function(){
	    					window.location.reload();
	    				});
	    			} else {
	    				if(responseText.exceptionInfo){
							var str = responseText.exceptionInfo;
							//特殊情况:操作成功，但是出现警告,允许刷新页面
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
								str = str.replace('AFTERSUCCESS', '');
								dialog.show('操作成功，提示:', str, 2, function(){
									window.location.reload();
								});
							} else {
								dialog.show('出现异常', '请重新尝试!');
							}
						} 
	    			}
	    		}
			});
		});

		//反审核按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'AUDITED') {
			$('#erpResAuditButton').parent().css('display', 'none');
		}
		$('#erpResAuditButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + resAuditUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			judgeException(responseText, function() {
	    				dialog.show('反审核成功', '正在重新加载...', 2, function() {
	    					window.location.reload();
	    				})
	    			});
	    		},
	    		error: function(xhr){
					if(xhr.response) {
						var response = $.parseJSON(xhr.response);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog('获取用户信息失败', '请先登录！');
						} else {
							dialog('错误', response.exceptionInfo);
						}
					}
				}
			});
		});

		//结案按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'CANUSE' && 
			baseInfo[result.statuscodeField].value != 'AUDITED') {
			$('#erpEndButton').parent().css('display', 'none');
		}
		$('#erpEndButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + endUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			if(responseText.success) {
	    				dialog.show('结案成功', '正在重新加载...', 2, function(){
	    					window.location.reload();
	    				});
	    			} else {
	    				if(responseText.exceptionInfo){
							var str = responseText.exceptionInfo;
							//特殊情况:操作成功，但是出现警告,允许刷新页面
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
								str = str.replace('AFTERSUCCESS', '');
								dialog.show('操作成功，提示:', str, 2, function(){
									window.location.reload();
								});
							} else {
								dialog.show('出现异常', '请重新尝试!');
							}
						} 
	    			}
	    		}
			});
		});

		//反结案按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'FINISH' ) {
			$('#erpResEndButton').parent().css('display', 'none');
		}
		$('#erpResEndButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + resEndUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			if(responseText.success) {
	    				dialog.show('反结案成功', '正在重新加载...', 2, function(){
	    					window.location.reload();
	    				});
	    			} else {
	    				if(responseText.exceptionInfo){
							var str = responseText.exceptionInfo;
							//特殊情况:操作成功，但是出现警告,允许刷新页面
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
								str = str.replace('AFTERSUCCESS', '');
								dialog.show('操作成功，提示:', str, 2, function(){
									window.location.reload();
								});
							} else {
								dialog.show('出现异常', '请重新尝试!');
							}
						} 
	    			}
	    		}
			});
		});

		//禁用按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'CANUSE' && 
			baseInfo[result.statuscodeField].value != 'AUDITED') {
			$('#erpBannedButton').parent().css('display', 'none');
		}
		$('#erpBannedButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + bannedUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			if(responseText.success) {
	    				dialog.show('禁用成功', '正在重新加载...', 2, function(){
	    					window.location.reload();
	    				});
	    			} else {
	    				if(responseText.exceptionInfo){
							var str = responseText.exceptionInfo;
							//特殊情况:操作成功，但是出现警告,允许刷新页面
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
								str = str.replace('AFTERSUCCESS', '');
								dialog.show('操作成功，提示:', str, 2, function(){
									window.location.reload();
								});
							} else {
								dialog.show('出现异常', '请重新尝试!');
							}
						} 
	    			}
	    		}
			});
		});

		//反禁用按钮
		if(baseInfo[result.statuscodeField] && baseInfo[result.statuscodeField].value != 'DISABLE') {
			$('#erpResBannedButton').parent().css('display', 'none');
		}
		$('#erpResBannedButton').click(function(){
			$.ajax({
				type: 'POST',
	    		url: basePath + resBannedUrl,
	    		async: false,
	    		data: {
		   			id: baseInfo[keyField].value
		   		},
	    		success: function(responseText) {
	    			judgeException(responseText, function() {
	    				dialog.show('反禁用成功', '正在重新加载...', 2, function(){
	    					window.location.reload();
	    				});
	    			});
	    		}
			});
		});

	};

	//parse form item
	var parseFormItem = function(item, result) {
		if(_readOnly) item.readOnly = true;// 页面只读模式，所有输入框都是只读
		var type, hidden='', html;
		switch(item.xtype) {
			case 'textfield': type='text'; break;
			case 'hidden': hidden='hidden'; type='text'; break;////强制设置隐藏的字段必填为否
			case 'numberfield': type='number'; break;
		}
		if(item.name != null) {
			if(item.name == result.statusField){//状态加特殊颜色
				item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
			} else if(item.name == result.statuscodeField){//状态码字段强制隐藏
				hidden = 'hidden';
			}
		}
		if(item.group == '0') {
			html = '<div class="field-line" style="font-weight:bold;color:#000;"><div class="control-group">'+
					'<div class="fieldlabel">&nbsp;'+ $(item.html).text()+
					'</div><div class="field text-center"><span class="glyphicon glyphicon-chevron-down"></span></div>'+
					'<div class="fieldremark"></div>'+
				'</div></div>';
			return html;
		}
		baseInfo[item.name] = {};
		baseInfo[item.name].fieldlabel = item.fieldLabel;
		baseInfo[item.name].value = item.value;
		baseInfo[item.name].nacessary = !item.allowBlank;
		baseInfo[item.name].ignore = (item.logic=='ignore');
		html = '<div class="field-line ' + hidden + '">' +
				'<div class="control-group">' + 
					'<div class="fieldlabel">' + 
						'<label class="control-label text-right" for="'+item.name +'">' + item.fieldLabel + '：</label>' +
					'</div>' + 
					'<div class="field">';
		if(item.xtype == 'erpYnField' ) {//是否单选框
			html += '<label class="radio-inline text-center" style="width:30%">'+
  						'<input type="radio" name="'+item.name+'" id="'+item.id+'" value="-1"'+
  						(item.value=='是'||item.value=='-1'||item.value=='1'?'checked':'')+'>是'+
  						'<span class="glyphicon glyphicon-ok" style="display:none;">'+
					'</label>'+
					'<label class="radio-inline text-center" style="width:30%">' +
  						'<input type="radio" name="'+item.name+'" id="'+item.id+'" value="0"'+
  						(item.value=='否'||item.value=='0'?'checked':'')+'>否'+
  						'<span class="glyphicon glyphicon-ok" style="display:none;">'+
					'</label>';
			if(!item.allowBlank && !item.readOnly) html += '&nbsp;&nbsp;'+
					'<span class="required glyphicon glyphicon-exclamation-sign text-danger">必填</span>';
		} else if(item.xtype=='datefield'||item.xtype=='datetimefield'||item.xtype=='datehourminutefield') {//时间输入框
			html += '<div class="'+(item.readOnly?'':'date')+' form_datetime has-feedback">' +
    					'<input '+(item.readOnly?'readOnly':'')+' id="'+item.id+'" name="'+item.name+'" type="text" data-date-format="'+ 
    					((item.xtype=='datetimefield'||item.xtype=='datehourminutefield')?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD')+
    					'" value="'+(item.value?item.value:'')+'" class="form-control input-sm">'+
    					'<span class="add-on form-control-feedback glyphicon glyphicon-calendar"><i class="icon-calendar"></i></span>'+
					'</div>';
		}else if(item.xtype=='condatefield'||item.xtype=='condatetimefield'||item.xtype=='condatehourminutefield') {//时间端输入框
			html += '<div class="'+(item.readOnly?'':'date')+' form_datetime has-feedback">' +
    					'<input '+(item.readOnly?'readOnly':'')+' id="'+item.id+'" name="'+item.name+'" type="text" data-date-format="'+ 
    					((item.xtype=='datetimefield'||item.xtype=='datehourminutefield')?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD')+
    					'" value="'+(item.value?item.value:'')+'" class="form-control input-sm">'+
    					'<span class="add-on form-control-feedback glyphicon glyphicon-calendar"><i class="icon-calendar"></i></span>'+
					'</div>';
		} else if(item.xtype == 'combo') {//下拉单选框
			html += '<select type="text" id="'+item.id+'" name="'+item.name+'" class="form-control input-sm"'+(item.readOnly?'readOnly':'')+'>';
			for(s in item.store.data) {
				var option = item.store.data[s];
				html += '<option value="'+option.value+'">'+option.display+'</option>';
			}
			html += '</select>';
		} else if(item.xtype == 'textareafield') {//文本域输入框
			html += '<textarea id="'+item.id+'" name="'+item.name+'" row="6" value="'+
				(item.value?item.value:'')+'" '+(item.readOnly?'readOnly':'')+'  class="form-control input-sm"'+
				(item.allowBlank?'':'required placeholder="必填*"')+'></textarea>';
		}else if(item.xtype == 'textareatrigger') {//长文本输入框
			html += '<textarea id="'+item.id+'" name="'+item.name+'" row="3" value="'+
				(item.value?item.value:'')+'" '+(item.readOnly?'readOnly':'')+'  class="form-control input-sm"'+
				(item.allowBlank?'':'required placeholder="必填*"')+'></textarea>';
		}else {
			html +=	'<input type="'+type+'" id="'+item.id+'" name="'+item.name+'" value="'+
				(item.value?item.value:'')+'" '+(item.readOnly?'readOnly':'')+'  class="form-control input-sm"'+
				(item.allowBlank?'':'required placeholder="必填*"')+'>';
		}
		html +=		'</div>'+
					'<div class="fieldremark">' +
						'<div class=""></div>' +
					'</div>' +
				'</div>' +
				'</div>';
		if(item.xtype == 'dbfindtrigger') {//dbFind输入框
			html =	'<div class="field-line ' + hidden + '">' +
				'<div class="control-group dropdown">' + 
					'<div class="fieldlabel">' + 
						'<label class="control-label text-right" for="'+item.name +'">' + item.fieldLabel + '：</label>' +
					'</div>' + 
					'<div class="field">'+
						'<input type="'+type+'" id="'+item.id+'" name="'+item.name+'" value="'+
						(item.value?item.value:'')+'" '+(item.readOnly?'readOnly':'')+'  class="form-control input-sm"'+
						(item.allowBlank?'':'required placeholder="必填*"')+'>'+
					'</div>'+
					(item.readOnly?'':(/* 只读的dbFind框不需要显示放大镜*/
					'<div class="fieldremark dropdown-toggle" data-toggle="dropdown">' +
						'<button type="button" fieldname="'+item.name+'" class="btn btn-default btn-xs dbFind"><span class="glyphicon glyphicon-search"></button>'+
					'</div>' +
					'<div class="dropdown-menu suggest-box">'+
						'<div class="">'+
							'<ul class="suggest-result">'+
							'</ul>'+
						'</div>'+
					'</div>'))+
				'</div>' +
			'</div>';
		} else if(item.xtype == 'multifield' || item.xtype=='condatefield' || 
			item.xtype=='condatetimefield' || item.xtype=='condatehourminutefield') {//合并型的字段
			html =	'<div class="field-line ' + hidden + '">' +
				'<div class="control-group dropdown">' + 
					'<div class="fieldlabel">' + 
						'<label class="control-label text-right" for="'+item.name +'">' + item.fieldLabel + '：</label>' +
					'</div>';
			if(item.xtype=='condatefield'||item.xtype=='condatetimefield'||item.xtype=='condatehourminutefield'){//时间段输入框
				html += '<div class="field">';
				html += '<div class="'+(item.readOnly?'':'date')+' form_datetime has-feedback">' +
    					'<input '+(item.readOnly?'readOnly':'')+' id="'+item.id+'" name="'+item.name+'" type="text" data-date-format="'+ 
    					((item.xtype=='condatetimefield'||item.xtype=='condatehourminutefield')?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD')+
    					'" value="'+(item.value?item.value:'')+'" class="form-control input-sm">'+
    					'<span class="add-on form-control-feedback glyphicon glyphicon-calendar"><i class="icon-calendar"></i></span>'+
					'</div>&nbsp;&nbsp;至';
				html += '<div class="'+(item.readOnly?'':'date')+' form_datetime has-feedback">' +
    					'<input '+(item.readOnly?'readOnly':'')+' id="'+item.secondname+'" name="'+item.secondname+'" type="text" data-date-format="'+ 
    					((item.xtype=='condatetimefield'||item.xtype=='condatehourminutefield')?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD')+
    					'" value="'+(item.value?item.value:'')+'" class="form-control input-sm">'+
    					'<span class="add-on form-control-feedback glyphicon glyphicon-calendar"><i class="icon-calendar"></i></span>'+
					'</div>';
			} else {//其他
				html += '<div class="field">';
				html += '<input type="'+type+'" id="'+item.id+'" name="'+item.name+'" value="'+
					(item.value?item.value:'')+'" '+(item.readOnly?'readOnly':'')+'  class="form-control input-sm"'+
					(item.allowBlank?'':'required placeholder="必填*"')+'>';
				html += '<input type="'+type+'" name="'+item.secondname+'" readOnly class="form-control input-sm"'+
					(item.allowBlank?'':'required placeholder="请先输入'+item.fieldLabel+'"')+'>';
			}
			html += '</div>';
			if(item.readOnly) {
				// 只读模式，不需要加放大镜
				html += '</div>' +
						'</div>';
			} else {
				html +=	'<div class="fieldremark dropdown-toggle" data-toggle="dropdown">' ;
				//除时间段输入项外
				if(!(item.xtype=='condatefield'||item.xtype=='condatetimefield'||item.xtype=='condatehourminutefield')){//
				html +=			'<button type="button" fieldname="'+item.name+'" class="btn btn-default btn-xs dbFind"><span class="glyphicon glyphicon-search"></button>';
				}
				html +=	'</div>' +
						'<div class="dropdown-menu suggest-box">'+
							'<div class="">'+
								'<ul class="suggest-result">'+
								'</ul>'+
							'</div>'+
						'</div>'+
					'</div>' +
				'</div>';
			}
			baseInfo[item.secondname] = {};
			baseInfo[item.secondname].fieldlabel = item.fieldLabel;
			baseInfo[item.secondname].value = item.value;
			baseInfo[item.secondname].nacessary = !item.allowBlank;
			baseInfo[item.secondname].ignore = (item.logic=='ignore');
		}
		return html;
	};

	//初始化baseInfo对象，在当前有数据的情况下
	var initBaseInfo = function(data) {
		$.each(baseInfo, function(b, bi){
			bi.value = data[b];
		});
	};

	//初始化detailsInfo对象，在当前有数据的情况下
	var initDetailsInfo = function(data) {
		$.each(data, function(i, d){
			var object = $.extend(true, new Object(), detailInfo);
			$.each(object, function(o, obj){
				obj.value = d[o];
			});
			detailsInfo.push(object);
		});
	};

	//通过控制label的字体大小控制fieldLabel的长度
	var shrinkLabel = function(labels) {
		$.each(labels, function(i, label){
			label = $(label);
			var labelFontSize = parseInt(label.css('font-size')) + 1;
			var labelLength = label.text().length+1;
			if(labelFontSize*labelLength>parseInt(label.parent().width())){
				label.css('font-size', (parseInt(label.parent().width())/labelLength)+'px');
			}
		});
	};

	//根据单选框是否checked修改单选框样式
	var initRadio = function(labels){
		$.each(labels, function(l, label){
			label = $(label);
			var checked = label.find('input[type="radio"]').attr('checked');
			if(checked) {
				label.css('background-color', '#339933').css('color', '#FFFFFF');
				label.find('.glyphicon').show();
				label.siblings('.radio-inline').css('background-color', '#EEEEEE').css('color', '#333333').
					css('font-weight', 'normal').css('border', '1px solid #339933');
				label.siblings('.radio-inline').find('.glyphicon').hide();
			}
		});
	};

	//添加单选框点击样式
	var radioLabelClick = function(radioes){
		initRadio($('.radio-inline'));
		$(radioes).click(function(){
			initRadio($(this));
			$(this).siblings('.required').removeClass('text-danger glyphicon-exclamation-sign').
				addClass('text-success glyphicon-ok-sign');
		});
	};

	//为所有.date添加datetimepicker
	var addDatetimepicker = function(){
		$('.date').datetimepicker({
			language: 'zh-CN',
			pickTime: ($(this).find('input').attr('data-date-format')=='YYYY-MM-DD'?false:true)
		});
	};

	//转换处理dbfind返回的结果
	var parseToDbFinds = function(result, which, fieldName) {
		var dbFinds = new Array();
		var dbPrototype = new Object();
		$.each(result.columns, function(c, co) {
			dbPrototype[co.dataIndex] = {};
			dbPrototype[co.dataIndex].hidden = co.hidden;
		});
		if (which == 'form') {
			$.each(result.dbfinds, function(d, db) {
				dbPrototype[db.dbGridField].field = db.field;
			});
		} else if(which == 'grid') {
			$.each(dbfinds, function(d, db) {
				if (db.trigger == fieldName) {
					console.log(db);
					if(dbPrototype[db.dbGridField]) dbPrototype[db.dbGridField].field = db.field;
				};
			});
		}
		$.each(eval('('+result.data+')'), function(d, da) {
			var object = $.extend(true, {}, dbPrototype);
			$.each(da, function(key, value) {
				if(object[key]) object[key].value = value;
			});
			var i = 1;//去前两个不隐藏的值做显示
			object.list = '';
			$.each(object, function(o, obj) {
				if(i > 2) return;
				if(!obj.hidden){
					if (i>1) {object.list += '&nbsp;|&nbsp;';};
					object.list += obj.value;
					i++;
				}
			});
			dbFinds.push(object);
		});
		return dbFinds;
	};

	//dbFind效果与功能
	var dbFindButtons = function(buttons, which){
		$.each(buttons, function(b, button) {
			$(button).click(function(){
				var me = $(this);
				var ul = me.parent().parent().find('ul');
				var fieldName = me.attr('fieldname');
				var input = $('input[name="'+fieldName+'"]');
				var keyWord = input.val().toLocaleUpperCase();
				var dbfind;//明细表中(which==grid)存储dbfind对应的caller和fieldName值
				if (which == 'grid') {
					dbfind = me.attr('dbfind');
				};
				if(keyWord) {
					setLoading(true);
					$.ajax({
						type: 'POST',
			    		url: basePath + 'common/dbfind.action',
			    		data: {
				   			which: which,
							caller: (which=='form'?caller:(dbfind.substr(0, dbfind.indexOf('|')))),
							field: fieldName,
							condition: "upper("+(which=='form'?fieldName:dbfind.substr(dbfind.indexOf('|')+1, dbfind.length))+") like '%"+keyWord+"%' ",
							ob: '',
							page: 1,
							pageSize: 8
				   		},
			    		success: function(result) {
			    			setLoading(false);
			    			ul.empty();
			    			var html = '';
			    			if(!result.exceptionInfo){
			    				var dbFinds = parseToDbFinds(result, which, fieldName);
			    				$.each(dbFinds, function(i, item){
			    					html = '<li>'+item.list+'</li>';
			    					delete item.list;
			    					var html = $(html);
			    					html.click(function() {
			    						$.each(item, function(key, obj) {
			    							$('[name="'+obj.field+'"]').val(obj.value);
			    							if(which == 'grid'){
			    								$.each(dbfinds, function(index, db){
				    								if(db.dbGridField == key) {
				    									$('[name="'+db.field+'"').val(obj.value);
				    								}
				    							});
			    							};
			    						});
			    					});
			    					ul.append(html);
			    				});
			    			}
			    			

			    		}
					});
				} else {
					ul.html('<li>请先输入关键字</li>');
				}
			});
		})
	}

	//getSingleGrid获取明细表信息
	var getSingleGrid = function(caller, condition){
		setLoading(true);
		$.post(basePath + 'common/singleGridPanel.action', {
			caller: caller,
			condition: gridCondition,
			_noc: 1
		}, function(result) {
			setLoading(false);
			judgeException(result, parseSingleGrid(result));
		});
	};

	//parseSingleGrid处理展现对明细表
	var parseSingleGrid = function(result){
		if(_readOnly) {
			$('#add-detail').remove();
			$('#detail-handlers').remove();
		}
		dbfinds = result.dbfinds;
		if(result.columns && result.columns.length){//明细表有列-有明细表
			parseDeForm(result);
			$('#details').removeClass('hidden');
			$('#add-detail').click(function() {//添加明细行按钮
				setLoading(true);
				$('#baseinfo form').hide();
				$('.spread-baseinfo').show();
				$('#add-detail').hide();
				$('#de-form').show();
				$('#detail-handlers').show();
				$('#detno').val(detailsInfo.length+1);
				$('#de-delete').attr('disabled', true);
				$('#de-modify').attr('disabled', true);
				$('#de-add').removeAttr('disabled');
				setLoading(false);
				$('#de-form input:first').focus();
			});
			$('.spread-baseinfo').click(function(){//基本信息中的展开按钮
				setLoading(true);
				$('.spread-baseinfo').hide();
				$('#baseinfo form').show();
				$('#de-form').hide();
				$('#detail-handlers').hide();
				$('#add-detail').show();
				setLoading(false);
				// 平滑移到'明细信息'头部
				$('html,body').animate({scrollTop:$('#title').offset().top}, 200);
			});
			$('#de-cancle').click(function(){//明细信息中的取消按钮
				setLoading(true);
				$('#baseinfo form').show();
				$('#de-form').hide();
				$('#detail-handlers').hide();
				$('#add-detail').show();
				$('.spread-baseinfo').hide();
				setLoading(false);
			});
			$('#de-add').click(function() {
				if(validateForm($('#de-form'))){
					var item = parseFormToObj($('#de-form'), detailInfo);
					detailsInfo.push(item);
					parseCurrentDetails();
					$('#detno').val(detailsInfo.length+1);
				}
			});
			if(result.data) {
				initDetailsInfo(eval('('+result.data+')'));
				parseCurrentDetails();
			};
		}
		
	};

	//初始化明细行表单
	var parseDeForm = function(result) {
		var columns = result.columns;
		var html = '';
		$.each(columns, function(c, column) {
			html +=  dealDeFormItem(column, result);
		});
		$('#de-form form').html(html);
		radioLabelClick($('#de-form .radio-inline'));
		addDatetimepicker();
		initFormValidation($('#de-form'));
		dbFindButtons($('#de-form .dbFind'), 'grid');
	}

	//处理明细行的每一个输入框
	var dealDeFormItem = function(item, result){
		var hidden = item.hidden?'hidden': '';
		if(_readOnly) item.readOnly = true;// 只读模式所有
		var readOnly = item.readOnly?'readOnly': '';
		var required = null;
		var ignore = false;
		var type = 'text';
		var html = '';
		// column有取别名
		if(item.dataIndex.indexOf(' ') > -1) {
			item.dataIndex = item.dataIndex.split(' ')[1];
		}
		if(item.logic == 'necessaryField') required = 'required';
//		if(hidden) required = null;//强制设置隐藏的字段必填为否
		if(item.xtype == 'numbercolumn') {
			type = 'number';
		} else if (item.xtype == 'datecolumn') {
			type = 'date';
		}
		if(item.logic == 'ignore') ignore = true;
		if(!mainField && item.logic == 'mainField') mainField = item.dataIndex;
		detailInfo[item.dataIndex] = {};
		detailInfo[item.dataIndex].fieldlabel = item.header;
		detailInfo[item.dataIndex].value = item.value;
		detailInfo[item.dataIndex].nacessary = required?true:false;
		detailInfo[item.dataIndex].ignore = ignore;
		html = '<div class="field-line ' + hidden + '">' +
				'<div class="control-group">' + 
					'<div class="fieldlabel">' + 
						'<label class="control-label text-right" for="'+item.dataIndex +'">'+item.header+'：</label>' +
					'</div>' + 
					'<div class="field">';
		if(item.editor && item.editor.xtype){//需要显示输入框的
			if(item.editor.xtype == 'erpYnField' ) {//是否单选框
				html += '<label class="radio-inline text-center" style="width:40%">'+
	  						'<input ignore="'+ignore+'" type="radio" name="'+item.dataIndex+'" value="-1">是'+
	  						'<span class="glyphicon glyphicon-ok" style="display:none;">'+
						'</label>'+
						'<label class="radio-inline text-center" style="width:40%">' +
	  						'<input ignore="'+ignore+'" type="radio" name="'+item.dataIndex+'" value="0" checked>否'+
	  						'<span class="glyphicon glyphicon-ok" style="display:none;">'+
						'</label>';
				if(required && !item.readOnly) html += '&nbsp;&nbsp;'+
						'<span class="required glyphicon glyphicon-exclamation-sign text-danger">必填</span>';
			} else if(item.editor.xtype=='datefield'||item.editor.xtype=='datetimefield'||item.editor.xtype=='datehourminutefield') {//时间输入框
				html += '<div class="'+(readOnly?'':'date')+' form_datetime has-feedback">' +
	    					'<input ignore="'+ignore+'" '+(readOnly?'readOnly':'')+' id="'+item.dataIndex+'" name="'+item.dataIndex+'" type="text" data-date-format="'+ 
	    					((item.xtype=='datetimecolumn'||item.xtype=='datehourminutecolumn')?'YYYY-MM-DD HH:mm:ss':'YYYY-MM-DD')+
	    					'" value="'+(item.value?item.value:'')+'" '+required+' class="form-control input-sm">'+
	    					'<span class="add-on form-control-feedback glyphicon glyphicon-calendar"><i class="icon-calendar"></i></span>'+
						'</div>';
			} else if(item.editor.xtype == 'dbfindtrigger') {//dbFind输入框
				html +=	'<input ignore="'+ignore+'" type="'+type+'" id="'+item.dataIndex+'" name="'+item.dataIndex+'" value="'+
					(item.value?item.value:'')+'" '+readOnly+'  class="form-control input-sm"'+
					(!required?'':'required placeholder="必填*"')+'>';
			} else if(item.logic=='detno') {//序号
				html +=	'<input ignore="'+ignore+'"  type="'+type+'" id="detno" name="'+item.dataIndex+'" value="'+
					(detailsInfo.length+1)+'" readOnly class="form-control input-sm">';
				detailInfo[item.dataIndex].detno = 'detno';//序号特有的特殊标志
			} else if(item.editor.xtype == 'combo') {//下拉选择框
				html +=	'<select ignore="'+ignore+'" name="'+item.dataIndex+'"class="form-control input-sm">';
				$.each(item.editor.store.data, function(d, da) {
					html += '<option value="' + da.value + '" >'+ da.display + '</option>';
				});
				html += '</select>';
			} else {
				html +=	'<input ignore="'+ignore+'" type="'+type+'" id="'+item.dataIndex+'" name="'+item.dataIndex+'" value="'+
					(item.value?item.value:'')+'" '+readOnly+'  class="form-control input-sm"'+
					(!required?'':'required placeholder="必填*"')+'>';
			}
		} else {
			html +=	'<input ignore="'+ignore+'" type="'+type+'" id="'+item.dataIndex+'" name="'+item.dataIndex+'" value="'+
				(item.value?item.value:'')+'" '+readOnly+'  class="form-control input-sm"'+
				(!required?'':'required placeholder="必填*"')+'>';
		}
		html +=		'</div>'+
					'<div class="fieldremark">' +
						'<div class=""></div>' +
					'</div>' +
				'</div>' +
				'</div>';
		if(item.dbfind) {//dbFind输入框
			html =	'<div class="field-line ' + hidden + '">' +
				'<div class="control-group dropdown">' + 
					'<div class="fieldlabel">' + 
						'<label class="control-label text-right" for="'+item.dataIndex+'">' +item.header+ '：</label>' +
					'</div>' + 
					'<div class="field">'+
						'<input ignore="'+ignore+'" type="'+type+'" id="'+item.dataIndex+'" name="'+item.dataIndex+'" value="'+
						(item.value?item.value:'')+'" '+readOnly+'  class="form-control input-sm"'+
						(!required?'':'required placeholder="必填*"')+'>'+
					'</div>'+
					(item.readOnly ? '' : (
					'<div class="fieldremark dropdown-toggle" data-toggle="dropdown">' +
						'<button type="button" dbfind="'+item.dbfind+'" fieldname="'+item.dataIndex+'" class="btn btn-default btn-xs dbFind"><span class="glyphicon glyphicon-search"></button>'+
					'</div>' +
					'<div class="dropdown-menu suggest-box">'+
						'<div class="">'+
							'<ul class="suggest-result">'+
							'</ul>'+
						'</div>'+
					'</div>' )) +
				'</div>' +
			'</div>';
		}
		return html;
	};

	//初始化验证表单输入是否符合条件
	var initFormValidation = function(form) {
		$('input, textarea', form).each(function(i, input){
			var me = $(input);
			if(me.attr('required')) {
				me.blur(function(){
					if(me.val() == '') {
						me.parent('.field').addClass(' has-error');
					} else {
						me.parent('.field').removeClass('has-error');
					}
				});
			}
		});
	}

	//验证表单，通过返回true，不通过返回false，定位到不合格的输入框
	var validateForm = function(form) {
		var validated = true;
		$('input, textarea', form).each(function(i, input){
			var me = $(input);
			if(me.attr('required')) {
				if(me.val() == '') {
					me.parent('.field').addClass(' has-error');
					me.focus();
					validated = false;
					return false;
				} else {
					me.parent('.field').removeClass('has-error');
				}
			}
		});
		return validated;
	};

	//获取radio输入框的值
	var getRadioValue = function(name){
		var value; 
		$.each($('[name="'+name+'"]'), function(r, radio){
			if($(radio).attr('checked')) value=$(radio).val(); 
			return;
		});
		return value;
	};

	// 把表单转换成object
	// item = {'de_no':{'field':'序号','value':'1','ignore':'false','nacessary':'true'}, 
	// 'de_name':{'field':'编号','value':'xxxxxx','ignore':'false','nacessary':'true}...}
	var parseFormToObj = function(form, obj) {
		if(obj){
			var object = $.extend(true, {}, obj);
			$('input, textarea, select', form).each(function(i, input){
				var me = $(input);
				if(me.attr('type') == 'radio'){//是否选择框
					if(me.attr('checked')) object[me.attr('name')].value = me.val();
				} else {
					object[me.attr('name')].value=me.val();
				}
			});
		} else {
			var object = {}
			$('input, textarea, select', form).each(function(i, input){
				var me = $(input);
				object[me.attr('name')] = {value:'', fieldlabel:'', nacessary: false, ignore:false};
				if(me.attr('type') == 'radio'){//是否选择框
					if(me.attr('checked')) object[me.attr('name')].value = me.val();
				} else {
					object[me.attr('name')].value = me.val();
				}
			});
		}
		return object;
	};

	//将对应的对象显示在表单中
	var parseObjToForm = function(form, obj, isDetail) {
		$('input, textarea, select', form).each(function(i, input){
			var attrName = $(input).attr('name');
			if(_readOnly && !obj[attrName].value) {// 只读模式下所有无值的字段不显示
				$(input).parent().parent().parent().addClass('hidden');
			} else {
				// 只读模式下所有有值的字段都显示
				// 明细行会出现个别名字在某个字段没有值，但不能影响到其他明细行
				if(_readOnly && isDetail) $(input).parent().parent().parent().removeClass('hidden');
				if($(input).attr('type')=='radio'){//是否单选框
					if(obj[attrName].value == $(input).val()) {
						$(input).attr('checked', true);
					} else {
						$(input).removeAttr('checked');
					}
				} else {
					$(input).val(obj[attrName].value);//其他输入框
				}
			}
		});
		initRadio($('.radio-inline'));
	}

	//展现所有的已添加的明细行快照（所谓快照就是只显示一点点东西哦。。）
	var parseCurrentDetails = function(){
		var html = '';
		$.each(detailsInfo, function (d, de) {
			html += '<div class="detail-sub">'+
						'<div class="sub-order"><span class="number">'+(d+1)+'</span></div>';
			$.each(de, function(i, o) {
				if(o.detno){
					o.value = (d+1);
				}
				if(o.nacessary){
					html+= '<b>'+o.fieldlabel+':</b>'+o.value+'&nbsp;&nbsp;&nbsp;&nbsp'
				}
			});
			html += '<br><div class="text-center text-info">点击查看详情</div></div>';
		});
		$('#de-content').html(html);
		$('.detail-sub').click(function(){//展现单个明细行
			var i = parseInt($(this).find('.number').text())-1;
			var de = detailsInfo[i];
			detailIndex = i;
			setLoading(true);
			$('#baseinfo form').hide();
			$('.spread-baseinfo').show();
			$('#add-detail').hide();
			$('#de-form').show();
			$('#detail-handlers').show();
			parseObjToForm($('#de-form'), de, true);
			$('#de-delete').removeAttr('disabled');//删除按钮
			$('#de-modify').removeAttr('disabled');//修改按钮
			$('#de-add').attr('disabled', true);
			setLoading(false);
			// 平滑移到'明细信息'头部
			$('html,body').animate({scrollTop:$('#details').offset().top}, 200);
		});
	};

	$('#de-delete').click(function(){//删除按钮
		detailsInfo.splice(detailIndex, 1);
		parseCurrentDetails();
		$('#detno').val(detailsInfo.length+1);
		$('#de-delete').attr('disabled', true);
		$('#de-modify').attr('disabled', true);
		$('#de-add').removeAttr('disabled');
	});

	$('#de-modify').click(function(){//修改按钮
		detailsInfo[detailIndex] = parseFormToObj($('#de-form'), detailInfo);
		parseCurrentDetails();
	});

	if(caller) {
		getSingleForm(caller, formCondition);//主表
		getSingleGrid(caller, gridCondition);//明细表
	};

	if(username){
		$('#username').text(username);
	}

});
