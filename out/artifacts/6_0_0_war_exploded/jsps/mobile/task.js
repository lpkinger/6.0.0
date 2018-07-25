//读取链接参数
function getUrlParam(name, link){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); 
    var search;  
    if (link) {search = link.substr(link.indexOf("?"),link.length);}
    else { search = window.location.search;}
    var r = search.substr(1).match(reg);   
    if (r != null)
    	return decodeURI(r[2]); 
    return null; 
}
//处理日期
function parseDate(date){
	var y = date.getFullYear(), m = date.getMonth() + 1, d = date.getDate(),
		h = date.getHours(), i = date.getMinutes();
	var now = new Date(), _y = now.getFullYear(), _m = now.getMonth() + 1, _d = now.getDate();
	if(_y != y) {
		return y + '-' + m + '-' + d + ' ' + h + ':' + i;
	} else {
		if(_m != m) {
			return m + '月' + d + '号' + h + '点' + i + '分';
		} else {
			if(_d != d) {
				return d + '号' + h + '点' + i + '分';
			} else {
				return (h < 12 ? '上午' : '下午' ) + h + '点' + i + '分';
			}
		}
	}
}
/**
 * 锁定表头和列
 * 
 * 参数定义
 * 	table - 要锁定的表格元素或者表格ID
 * 	freezeRowNum - 要锁定的前几行行数，如果行不锁定，则设置为0
 * 	freezeColumnNum - 要锁定的前几列列数，如果列不锁定，则设置为0
 * 	width - 表格的滚动区域宽度
 */
function freezeTable(table, freezeRowNum, freezeColumnNum, width) {
	if (typeof(freezeRowNum) == 'string')
		freezeRowNum = parseInt(freezeRowNum);
		
	if (typeof(freezeColumnNum) == 'string')
		freezeColumnNum = parseInt(freezeColumnNum);

	var tableId;
	if (typeof(table) == 'string') {
		tableId = table;
		table = $('#' + tableId);
	} else
		tableId = table.attr('id');
	if(table.width() <= width) return;
	height = table.height() + 10;
	
	var divTableLayout = $("#" + tableId + "_tableLayout");
	
	if (divTableLayout.length != 0) {
		divTableLayout.before(table);
		divTableLayout.empty();
	} else {
		table.after("<div id='" + tableId + "_tableLayout' style='overflow:hidden;height:" + height + "px; width:" + width + "px;'></div>");
		
		divTableLayout = $("#" + tableId + "_tableLayout");
	}
	
	var html = '';
	if (freezeRowNum > 0 && freezeColumnNum > 0)
		html += '<div id="' + tableId + '_tableFix" style="padding: 0px;"></div>';
		
	if (freezeRowNum > 0)
		html += '<div id="' + tableId + '_tableHead" style="padding: 0px;"></div>';
		
	if (freezeColumnNum > 0)
		html += '<div id="' + tableId + '_tableColumn" style="padding: 0px;"></div>';
		
	html += '<div id="' + tableId + '_tableData" style="padding: 0px;"></div>';
	
	
	$(html).appendTo("#" + tableId + "_tableLayout");
	
	var divTableFix = freezeRowNum > 0 && freezeColumnNum > 0 ? $("#" + tableId + "_tableFix") : null;
	var divTableHead = freezeRowNum > 0 ? $("#" + tableId + "_tableHead") : null;
	var divTableColumn = freezeColumnNum > 0 ? $("#" + tableId + "_tableColumn") : null;
	var divTableData = $("#" + tableId + "_tableData");
	
	divTableData.append(table);
	
	if (divTableFix != null) {
		var tableFixClone = table.clone(true);
		tableFixClone.attr("id", tableId + "_tableFixClone");
		divTableFix.append(tableFixClone);
	}
	
	if (divTableHead != null) {
		var tableHeadClone = table.clone(true);
		tableHeadClone.attr("id", tableId + "_tableHeadClone");
		divTableHead.append(tableHeadClone);
	}
	
	if (divTableColumn != null) {
		var tableColumnClone = table.clone(true);
		tableColumnClone.attr("id", tableId + "_tableColumnClone");
		divTableColumn.append(tableColumnClone);
	}
	
	$("#" + tableId + "_tableLayout table").css("margin", "0");
	
	if (freezeRowNum > 0) {
		var HeadHeight = 0;
		var ignoreRowNum = 0;
		$("#" + tableId + "_tableHead tr:lt(" + freezeRowNum + ")").each(function () {
			if (ignoreRowNum > 0)
				ignoreRowNum--;
			else {
				var td = $(this).find('td:first, th:first');
				HeadHeight += td.outerHeight(true);
				
				ignoreRowNum = td.attr('rowSpan');
				if (typeof(ignoreRowNum) == 'undefined')
					ignoreRowNum = 0;
				else
					ignoreRowNum = parseInt(ignoreRowNum) - 1;
			}
		});
		HeadHeight += 2;
		
		divTableHead.css("height", HeadHeight);
		divTableFix != null && divTableFix.css("height", HeadHeight);
	}
	
	if (freezeColumnNum > 0) {
		var ColumnsWidth = 0;
		var ColumnsNumber = 0;
		$("#" + tableId + "_tableColumn tr:eq(" + freezeRowNum + ")").find("td:lt(" + freezeColumnNum + "), th:lt(" + freezeColumnNum + ")").each(function () {
			if (ColumnsNumber >= freezeColumnNum)
				return;
				
			ColumnsWidth += $(this).outerWidth(true);
			
			ColumnsNumber += $(this).attr('colSpan') ? parseInt($(this).attr('colSpan')) : 1;
		});
		ColumnsWidth += 2;

		divTableColumn.css("width", ColumnsWidth);
		divTableFix != null && divTableFix.css("width", ColumnsWidth);
	}
	
	divTableData.scroll(function () {
		divTableHead != null && divTableHead.scrollLeft(divTableData.scrollLeft());
		
		divTableColumn != null && divTableColumn.scrollTop(divTableData.scrollTop());
	});
	
	divTableFix != null && divTableFix.css({ "overflow": "hidden", "position": "absolute", "z-index": "50" });
	divTableHead != null && divTableHead.css({ "overflow": "hidden", "width": width, "position": "absolute", "z-index": "45" });// - 17
	divTableColumn != null && divTableColumn.css({ "overflow": "hidden", "height": height, "position": "absolute", "z-index": "40" });// - 17
	divTableData.css({ "overflow": "scroll", "width": width, "height": height, "position": "absolute" });
	
	divTableFix != null && divTableFix.offset(divTableLayout.offset());
	divTableHead != null && divTableHead.offset(divTableLayout.offset());
	divTableColumn != null && divTableColumn.offset(divTableLayout.offset());
	divTableData.offset(divTableLayout.offset());
}
/**
 * 调整锁定表的宽度和高度，这个函数在resize事件中调用
 * 
 * 参数定义
 * 	table - 要锁定的表格元素或者表格ID
 * 	width - 表格的滚动区域宽度
 */
function adjustTableSize(table, width) {
	var tableId;
	if (typeof(table) == 'string')
		tableId = table;
	else
		tableId = table.attr('id');
	height = $("#" + tableId).height() + 10;
	$("#" + tableId + "_tableLayout").width(width).height(height);
	$("#" + tableId + "_tableHead").width(width);// - 17
	$("#" + tableId + "_tableColumn").height(height);// - 17
	$("#" + tableId + "_tableData").width(width).height(height);
}
//返回当前页面宽度
function pageWidth() {
	if ($.browser.msie) {
		return document.compatMode == "CSS1Compat" ? document.documentElement.clientWidth : document.body.clientWidth;
	} else {
		//  - padding 
		return self.innerWidth - 30;
	}
};

$(document).ready(function() {
	//自定义事件类型 boxready(当第一次点击时触发，当Tab页面第一次打开时触发)
	$.event.special.boxready = {
	    /**
		 * 初始化事件处理器 - this指向元素
		 * @param 附加的数据
		 * @param 事件类型命名空间
		 * @param 回调函数
		 */
	    setup: function(data, namespaces, eventHandle) {
	    	var elem = this;
	    	$.event.add(elem, 'click', function (event) {
	    		if ($.data(elem, '@loaded') !== true) {
	    			$.event.trigger('boxready', null, elem);
	    			$.data(elem, '@loaded', true);
	    		}
	    	});
	    },
	    /**
		 * 卸载事件处理器 - this指向元素
		 * @param 事件类型命名空间
		 */
	    teardown: function(namespaces) {
	    	var elem = this;
	    	$.event.remove(elem, 'click');
            $.removeData(elem, '@loaded');
	    }
	};
	
	var cls = 'active', instance = null, current = null, readyTime = new Date();
	// Tab切换开关增加点击事件
	$('.nav-tabs>li>a').click(function(){
		if(!$(this).hasClass(cls)) {
			var nav = $(this).parent().parent(), bd = nav.parent().next(),
					old = nav.children('.' + cls).index(),
					index = $(this).parent().index();
			$('.tab-pane:eq(' + old + ')', bd).removeClass(cls);
			$('.tab-pane:eq(' + index + ')', bd).addClass(cls);
			nav.children('.' + cls).removeClass(cls);
			$(this).parent().addClass(cls);
		}
	});
	
	// 模态对话框
	var dialog = {
		show: function(title, content, timeout, callback){
			var back = $('.modal-backdrop'), modal = $('.modal'),
				tt = $('.modal-title', modal), tb = $('.modal-body', modal);
			back.css('display', 'block');
			modal.css('display', 'block');
			tt.text(title);
			if(timeout && timeout > 0) {
				content = '<div><strong class="text-success text-xl" id="timeout">' + timeout + '</strong>&nbsp;秒后，' + content + '</div>';
			}
			tb.html(content);
			if(timeout) {
				var pre = 1000;
				if(timeout <=0 ) {// auto close
					pre = 1500;
					if(!callback)
						callback = dialog.hide;
				}
				var timer = function(t) {
					setTimeout(function(){
						if(t > -1) {
							$('#timeout').text(t--);
							timer(t);
						} else {
							callback && callback.call();
						}
					}, pre);
				};
				timer(timeout);
			}
		},
		hide: function() {
			var back = $('.modal-backdrop'), modal = $('.modal');
			back.css('display', 'none');
			modal.css('display', 'none');
		}
	};
	
	// 加载框
	var setLoading = function(isLoading) {
		$('.loading-container').css('display', isLoading ? 'block' : 'none');
	};
	
	//处理singleFormItems.action结果请求，返回parseBillMain处理的格式
	var dealForm = function(result) {
		var taskInfo = {data:{}, group:false};
		var data = eval('(' + result.data + ')');
		for(i in result.items) {
			var item = result.items[i];
			if(item.group == 0) {
				taskInfo.data[$(item.html).text()] = {};
				taskInfo.group = true;
			} else {
				if(item.groupName) {
					if(item.xtype!='hidden'){
						if(data[item.dataIndex]!='')
							taskInfo.data[item.groupName][item.fieldLabel] = 
								(data[item.dataIndex]||data[item.dataIndex]==0)?data[item.dataIndex]:
								((item.value||item.value==0)?item.value:'无');
					}
				} else {
					if (item.xtype!='hidden') {
						if(data[item.dataIndex]!='')
							taskInfo.data[item.fieldLabel]=
								(data[item.dataIndex]||data[item.dataIndex]==0)?data[item.dataIndex]:
								((item.value||item.value==0)?item.value:'无');
					};
				}
			}
		}
		return taskInfo;
	};

	//处理singleGridPanel.action的结果，返回parseBillDetail处理的格式
	var dealGrid = function(result) {
		var resultData = {};
		var data = eval('(' + result.data + ')');
		for(i in result.columns) {
			var column = result.columns[i];
			if(!column.hidden){
				resultData[column.header] = [];
				for (d in data) {
					resultData[column.header].push(data[d][column.dataIndex])
				};
			}
		}
		return resultData;
	};
	
	//获取相关单据的明细
	var getRelationDetail = function(caller, gridCondition, container){
		setLoading(true);
//		$.post(basePath + 'common/singleGridPanel.action', {
//			caller: caller,
//			condition: gridCondition,
//			_m: 0
//		}, function(result, text) {
//			setLoading(false);
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('获取用户信息失败', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				parseBillDetail(dealGrid(result), container);
//			}
//		});
		$.ajax({
			url:basePath + 'common/singleGridPanel.action',
			type: 'POST',
			data: {caller: caller, condition: gridCondition, _m: 0},
			success: function(result){
				setLoading(false);
				parseBillDetail(dealGrid(result), container);
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseJSON) {
					var response = xhr.responseJSON;
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	//收缩监听事件
	$('#shrink').click(function(){
			setLoading(true);
			$('#relative #relation').addClass('hidden');
			$('#relative #expand').removeClass('hidden');
			setLoading(false);
	});
	
	//获取相关单据
	var getRelation = function(billLink) {
		$('#relation').removeClass('hidden');

		var c = getUrlParam('whoami', billLink);
		var fc = getUrlParam('formCondition', billLink).replace('IS', '=');
		var gc = getUrlParam('gridCondition', billLink).replace('IS', '=');
		setLoading(true);
//		$.post(basePath + 'common/singleFormItems.action', {
//			caller: c,
//			condition: fc,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('获取用户信息失败', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				parseBillMain(dealForm(result), $('#relation-main'));
//				$('#relation-detail-header').bind('boxready', function(){
//					getRelationDetail(c, gc, $('#relation-detail'));
//				});
//			}
//		});
		$.ajax({
			url:basePath + 'common/singleFormItems.action',
			type: 'POST',
			data: {caller: c, condition: fc, _noc: 1},
			success: function(result){
				setLoading(false);
				parseBillMain(dealForm(result), $('#relation-main'));
				$('#relation-detail-header').bind('boxready', function(){
					getRelationDetail(c, gc, $('#relation-detail'));
				});
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseJSON) {
					var response = xhr.responseJSON;
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	
	//get record 获取任务的操作记录
	var getRecord = function(id) {
//		$.post(basePath + 'common/getFieldsDatas.action', {
//			caller: 'WorkRecord',
//			fields: 'wr_recorder,wr_recorddate,wr_redcord',
//			condition: 'wr_raid='+ id + ' order by wr_recorddate'
//		}, function(result){
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('获取用户信息失败', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				parseRecord(result);
//			}
//		});
		$.ajax({
			url:basePath + 'common/getFieldsDatas.action',
			type: 'POST',
			data: {
				caller: 'WorkRecord', 
				fields: 'wr_recorder,wr_recorddate,wr_redcord', 
				condition: 'wr_raid='+ id + ' order by wr_recorddate'
				},
			success: function(result){
				parseRecord(result);
			},
			error: function(xhr){
				if(xhr.responseJSON) {
					var response = xhr.responseJSON;
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	
	//parseRecord 显示任务记录的信息
	var parseRecord = function(result){
		var data = eval('(' + result.data + ')');
		var html = '';
		for(d in data) {
			var datetime = new Date(data[d].WR_RECORDDATE);
			if(isNaN(datetime)) {
				datetime = new Date(data[d].WR_RECORDDATE.replace(' ','T'))
			}
			html += '<div class="record">' + 
				'<div><span class="recorder">' + data[0].WR_RECORDER + '</span>&nbsp;&nbsp;' +
				'<span class="recorddate">' + parseDate(datetime) + '<span></div>' + 
				'<div class="record-content">' + data[0].WR_REDCORD + '</div>' + 
				'</div>'
		};
		$('#record').html(html);
	};
	
	//get task 获取task
	var getTask = function(caller, id) {
		setLoading(true);
		var url = basePath + 'common/singleFormItems.action';
		if(caller == 'WorkRecord') url = basePath + 'plm/RecordFormItemsAndData.action';
//		$.post(url, {
//			caller: caller,
//			condition: 'ra_id=' + id,
//			_noc:1
//		}, function(result, text){
//			setLoading(false);
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('获取用户信息失败', '请先登录！');
//				} else {
//					dialog.show('出现异常', e + '，请稍后再试...');
//				}
//			} else {
//				parseTask(result);
//			}
//		});
		$.ajax({
			url:url,
			type: 'POST',
			data: {caller: caller, condition: 'ra_id=' + id, _noc:1},
			success: function(result){
				setLoading(false);
				parseTask(result);
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseJSON) {
					var response = xhr.responseJSON;
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	
	// 增加被指定任务处理人回复任务的按钮
	var addReplyButton = function() {
		$('#deal .form').append('<textarea id="deal-msg" rows="2" placeholder="回复您的任务完成情况..."' + 
				'class="form-control"></textarea>');
		$('#buttons').append('<div class="btn-group">' + 
						'<button id="reply" type="button" class="btn btn-default line">' + 
							'<span class="glyphicon glyphicon-edit"></span>&nbsp;回复' +
						'</button>' + 
					'</div>');
		$('#reply').click(function(){
			replyTask($('#deal-msg').val());
		});
	}
	
	// 被指定任务处理人回复任务处理情况
	var replyTask = function(record) {
		if(record) {
			setLoading(true);
			$.ajax({
				url: basePath + 'plm/record/endBillTask.action',
				type: 'POST',
				dataType: 'json',
				data: {caller:'ResourceAssignment!Bill', ra_id: id, record: record, _noc: 1},
				success: function(data){
					setLoading(false);
					if(data.success) {
						dialog.show('任务回复成功', '回复内容已提交任务发起人确认，请关闭页面');
					}	
				},
				error: function(xhr){
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
		} else {
			dialog.show('提示', '回复前请先填写任务完成情况', 1);
		}
	};
	
	// 增加任务发起人确认和驳回任务的按钮
	var addConfirmButton = function() {
		$('#deal .form').append('<textarea id="deal-msg" rows="2" placeholder="请说说您对任务完成情况的意见..."' + 
				'class="form-control"></textarea>');
		$('#buttons').append('<div class="btn-group">' + 
						'<button id="confirm" type="button" class="btn btn-default line">' + 
							'<span class="glyphicon glyphicon-thumbs-up"></span>&nbsp;确认' +
						'</button>' + 
					'</div>' +
					'<div class="btn-group">' +
						'<button id="noConfirm" type="button" class="btn btn-default line">' + 
							'<span class="glyphicon glyphicon-thumbs-down"></span>&nbsp;驳回' +
						'</button>' + 
					'</div>');
		$('#confirm').click(function(){
			confirmTask($('#deal-msg').val());
		});
		$('#noConfirm').click(function(){
			noConfirmTask($('#deal-msg').val());
		});
	}
	
	// 任务发起人确认任务
	var confirmTask = function(record) {
		if(record) {
			setLoading(true);
			$.ajax({
				url: basePath + 'plm/record/confirmBillTask.action',
				type: 'POST',
				dataType: 'json',
				data: {caller:'ResourceAssignment!Bill', ra_id: id, record: record, _noc: 1},
				success: function(data){
					setLoading(false);
					if(data.success) {
						dialog.show('任务确认成功', '任务已成功结束，正在刷新...', 1, function(){
							location.reload();
						});
					}
				},
				error: function(xhr){
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
		} else {
			dialog.show('提示', '确认前请先填写你的意见', 1);
		}
	};
	
	// 任务发起人驳回任务
	var noConfirmTask = function(record) {
		if(record) {
			setLoading(true);
			$.ajax({
				url: basePath + 'plm/record/noConfirmBillTask.action',
				type: 'POST',
				dataType: 'json',
				data: {caller:'ResourceAssignment!Bill', ra_id: id, record: record, _noc: 1},
				success: function(data){
					setLoading(false);
					if(data.success) {
						dialog.show('任务驳回成功', '任务处理人将接收到您的意见，并重新处理任务，请关闭页面');
					}
				},
				error: function(xhr){
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
		} else {
			dialog.show('提示', '驳回前请先填写你的意见', 1);
		}
	};
	
	// 展现任务数据
	var parseTask = function(task) {
		if(task.title && task.title!='') {
			$('#jp_name').text(task.title);
		} else {
			$('#jp_name').text('任务处理');
		}
		$('#jp_name').css('margin-left', "-" + $('#jp_name').text().replace(/[^\x00-\xff]/g, 'xx').length * 7 + "px");
		var taskInfo = dealForm(task);
		var data = eval('(' + task.data + ')');
		var buttons = task.buttons;
		parseBillMain(taskInfo, $('#bill-main'));
		if(data.sourcelink) {
			var caller = getUrlParam('whoami', data.sourcelink);
			if(!caller || caller=='') {
				$('#relative #expand').text('无法显示任务相关单据');
			}else {
				$('#relative #expand').click(function(){
					$('#relative #expand').addClass('hidden');
					getRelation(data.sourcelink);
				});
			};
		} else {
			$('#relative #expand').text('本任务无相关单据');
		}
		
		if(data.ra_statuscode == 'START') {// 进行中的任务，被指定的处理人可以回复任务
			addReplyButton();
		} else if(data.ra_statuscode == 'UNCONFIRMED') {// 待确认的任务，发起人可以确认或驳回
			addConfirmButton();
		} else {// 已结束的任务，不能做处理
			console.log('已结束');
		}
//		if(buttons.indexOf('erpOverButton') != -1) {//添加结束处理
//			addEndButoon();
//		}
		// 不加关闭按钮，2015年9月28日15:32:37
	}
	
	// parse bill main 把相关单据主表信息展现到页面中
	var parseBillMain = function(main, container) {
		var bill = main.data, html = '<table class="table table-condensed table-bordered table-striped">', g = null;
		if(main.group) {
			for(k in bill) {
				g = bill[k];
				html += '<tr><td colspan="2" class="text-center text-success"><strong>' + k + '</strong>&nbsp;<span class="glyphicon glyphicon-chevron-down"></span></td></tr>';
				for(b in g) {
					html += '<tr>';
					html += '<td class="text-right special"><strong>' + b + '</strong></td>';
					html += '<td class="text-center">' + g[b] + '</td>';
					html += '</tr>';
				};
			}
		} else {
			for(b in bill) {
				html += '<tr>';
				html += '<td class="text-right special"><strong>' + b + '</strong></td>';
				html += '<td class="text-center">' + bill[b] + '</td>';
				html += '</tr>';
			};
		}
		html += '</table>';
		container.html(html);
	};
	
	// parse bill detail 把相关单据明细表数据展现到页面中
	var parseBillDetail = function(detail, container){
		var html = '<table id="bill-detail-table" class="table table-condensed table-bordered table-striped">';
		for(c in detail) {
			html += '<tr>';
			html += '<td class="text-right special"><strong>' + c + '</strong></td>';
			if(detail[c] && detail[c].length > 0) {
				$.each(detail[c], function(i, p){
					html += '<td class="text-center">' + p + '</td>';
				});
			} else {
				html += '<td class="text-center text-muted">(无)</td>';
			}
			html += '</tr>';
		}
		html += '</table>';
		container.html(html);
		var table = container.children('table');
		freezeTable(table, 0, 1, pageWidth());
		var flag = false;
		$(window).resize(function() {
			if (flag) 
				return ;
			setTimeout(function() { 
				adjustTableSize(table.attr('id'), pageWidth()); 
				flag = false; 
			}, 100);
			flag = true;
		});
	};
	
	//id参数，对应allprocess_view_undo视图中的id字段，resourceAssignment表中的ra_id字段
	var id = getUrlParam('id');
	
	// caller参数，billtask\mrptask\kbitask 类型的任务为ResourceAssignment!Bill ， projecttask\worktask 为 WorkRecord
	var caller = getUrlParam('caller');
	
	if(id && caller) {
		getTask(caller, id);// 获取任务的信息和（如有）相关单据
	}
	if(id) {
		getRecord(id);// 获取任务的操作记录
	}
});