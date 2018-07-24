function getUrlParam(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");   
    var r = window.location.search.substr(1).match(reg);   
    if (r != null)
    	return decodeURI(r[2]); 
    return null; 
}
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
/*
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
/*
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
// close page
function closePage() {
	top.window.opener = top; 
	top.window.open('','_self',''); 
	top.window.close();
}
$(document).ready(function() {
	//init custom event(trigger when tabpane active for the first time)
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
	// toggle tabs
	$('.nav-tabs>li>a').click(function(){
		if(!$(this).hasClass(cls)) {
			var nav = $(this).parent().parent(), bd = nav.next(),
					old = nav.children('.' + cls).index(),
					index = $(this).parent().index();
			$('.tab-pane:eq(' + old + ')', bd).removeClass(cls);
			$('.tab-pane:eq(' + index + ')', bd).addClass(cls);
			nav.children('.' + cls).removeClass(cls);
			$(this).parent().addClass(cls);
		}
	});
	// modal dialog
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
	// loading
	var setLoading = function(isLoading) {
		$('.loading-container').css('display', isLoading ? 'block' : 'none');
	};
	// get instance
	var getInstance = function(id, callback) {
//		$.post(basePath + 'common/getProcessInstanceId.action', {
//			jp_nodeId: id,
//			_noc: 1
//		}, function(result, text) {
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('错误', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				instance = result.processInstanceId;
//				callback && callback.call(null, instance);
//			}
//		});
		$.ajax({
			url:basePath + 'common/getProcessInstanceId.action',
			type: 'POST',
			data: {jp_nodeId: id, _noc: 1},
			success: function(result){
				instance = result.processInstanceId;
				callback && callback.call(null, instance);
			},
			error: function(xhr){
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取信息失败', '请先登录！');
					 } else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	// get process
	var getProcess = function(id) {
		setLoading(true);
//		$.post(basePath + 'common/getCurrentNode.action', {
//			jp_nodeId: id,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('错误', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				instance = result.info.InstanceId;
//				parseNode(result.info.currentnode);
//				// result.info.button 额外的按钮，以供修改单据(例如客户拜访)，在这里不考虑
//			}
//		});
		$.ajax({
			url:basePath + 'common/getCurrentNode.action',
			type: 'POST',
			data: {jp_nodeId: id, _noc: 1},
			success: function(result){
				setLoading(false);
				instance = result.info.InstanceId;
				parseNode(result.info.currentnode);
			},
			error: function(xhr,a,b,c){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo,-1);
					}
				}
			}
		});
	};
	// get main point
	var getMainPoint = function(id) {
//		$.post(basePath + 'common/getCustomSetupOfTask.action', {
//			nodeId: id,
//			_noc: 1
//		}, function(result, text) {
//			if(result.cs) {
//				var points = [], data = result.data ? result.data.split(';') : [];
//				$.each(result.cs, function(i, c){
//					var m = c.indexOf('^'), n = c.indexOf('$'), q = c.indexOf('@');
//					points.push({
//						type: c.substring(m + 1, n),
//						text: c.substring(0, m),
//						required: c.substr(n + 1, 1) === 'Y',
//						value: data[i] ? data[i].substring(data[i].lastIndexOf("(") + 1, data[i].lastIndexOf(")")) : null,
//						logic: q > 0 ? c.substring(q + 1) : null
//					});
//				});
//				if(points.length > 0)
//					parseMainPoint(points);
//			}
//		});
		$.ajax({
			url:basePath + 'common/getCustomSetupOfTask.action',
			type: 'POST',
			data: {nodeId: id, _noc: 1},
			success: function(result){
				if(result.cs) {
				var points = [], data = result.data ? result.data.split(';') : [];
				$.each(result.cs, function(i, c){
					var m = c.indexOf('^'), n = c.indexOf('$'), q = c.indexOf('@');
					points.push({
						type: c.substring(m + 1, n),
						text: c.substring(0, m),
						required: c.substr(n + 1, 1) === 'Y',
						value: data[i] ? data[i].substring(data[i].lastIndexOf("(") + 1, data[i].lastIndexOf(")")) : null,
						logic: q > 0 ? c.substring(q + 1) : null
					});
				});
				if(points.length > 0)
					parseMainPoint(points);
				}
			},
			error: function(xhr){
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	// get history
	var getHistory = function(instanceId, callback) {
		setLoading(true);
//		$.post(basePath + 'common/getAllHistoryNodes.action', {
//			processInstanceId: instanceId,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('错误', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				if(callback)
//					callback.call(null, result.nodes);
//				if(result.nodes && result.nodes.length > 0) {
//					parseHistory(result.nodes);
//				}
//			}
//		});
		$.ajax({
			url:basePath + 'common/getAllHistoryNodes.action',
			type: 'POST',
			data: {processInstanceId: instanceId, _noc: 1},
			success: function(result){
				setLoading(false);
				if(callback)
					callback.call(null, result.nodes);
				if(result.nodes && result.nodes.length > 0) {
					parseHistory(result.nodes);
				}
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	// get bill main
	var getBillMain = function(caller, url, cond, billId) {
//		$.post(basePath + 'common/rsForm.action', {
//			caller: caller,
//			condition: cond,
//			url: url,
//			_noc: 1
//		}, function(result, text) {
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('错误', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				if(result.data) {
//					parseBillMain(result);
//					if(caller == 'VisitRecord' || caller == 'VisitRecord!Vender') {
//						initVisitRecord(result, billId);
//					}
//				}
//			}
//		});
		$.ajax({
			url:basePath + 'common/rsForm.action',
			type: 'POST',
			data: {caller: caller, condition: cond, url: url, _noc: 1},
			success: function(result){
				if(result.data) {
					parseBillMain(result);
					if(caller == 'VisitRecord' || caller == 'VisitRecord!Vender') {
						initVisitRecord(result, billId);
					}
				}
			},
			error: function(xhr){
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	//init extra element when VisitRecord
	var initVisitRecord = function(main, billId) {
		$('#ex').css('display', 'block');
		var data = main.data['评价'], title = data ? (data['评分'] || '良') : '良', 
				msg = data ? data['评语'] : '';
		if(title) {
			$('#ex-rating button[title="' + title + '"]').addClass('active');
		}
		$('#ex-msg').val(msg);
		$('#ex-rating button').click(function(){
			$('#ex-rating button').each(function(){
				$(this).removeClass('active');
			});
			$(this).addClass('active');
		});
		$('#ex-confirm').click(function(){
			msg = $('#ex-msg').val() || '-';
			title = '';
			$('#ex-rating button').each(function(){
				if($(this).hasClass('active'))
					title = $(this).attr('title');
			});
			setLoading(true);
//			$.post(basePath + 'crm/customermgr/updateVisitRecordPingjia.action', {
//				id: billId,
//				vr_newtitle: title,
//				vr_purpose: msg,
//				_noc: 1
//			}, function(result, text) {
//				setLoading(false);
//				var e = result.exceptionInfo;
//				if(e) {
//					if(e == 'ERR_NETWORK_SESSIONOUT') {
//						dialog.show('错误', '请先登录！');
//					} else {
//						dialog.show('出现异常', '请稍后再试...');
//					}
//				} else {
//					dialog.show('提示', '评价成功', -1);
//				}
//			});
			$.ajax({
				url:basePath + 'crm/customermgr/updateVisitRecordPingjia.action',
				type: 'POST',
				data: {id: billId, vr_newtitle: title, vr_purpose: msg, _noc: 1},
				success: function(result){
					setLoading(false);
					dialog.show('提示', '评价成功', -1);
				},
				error: function(xhr){
					setLoading(false);
					if(xhr.responseText) {
						var response =$.evalJSON(xhr.responseText);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog.show('获取信息失败', '请先登录！');
						} else {
							dialog.show('错误', response.exceptionInfo);
						}
					}
				}
			});
		});
	};
	// get bill detail
	var getBillDetail = function(caller, url, cond){
		setLoading(true);
//		$.post(basePath + 'common/rsGrid.action', {
//			caller: caller,
//			condition: cond,
//			url: url,
//			start: 0,
//			end: 100,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			var e = result.exceptionInfo;
//			if(e) {
//				if(e == 'ERR_NETWORK_SESSIONOUT') {
//					dialog.show('错误', '请先登录！');
//				} else {
//					dialog.show('出现异常', '请稍后再试...');
//				}
//			} else {
//				if(result.data)
//					parseBillDetail(result.data, $('#bill-detail'));
//			}
//		});
		$.ajax({
			url:basePath + 'common/rsGrid.action',
			type: 'POST',
			data: {caller: caller, condition: cond, url: url, start: 0, end: 100, _noc: 1},
			success: function(result){
				setLoading(false);
				if(result.data)
				   parseBillDetail(result.data, $('#bill-detail'));
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	// parse current node
	var parseNode = function(node) {
		current = node;
		$('#jp_name').text(node.jp_name);
		$('#jp_name').css('margin-left', "-" + node.jp_name.replace(/[^\x00-\xff]/g, 'xx').length * 7 + "px");
		$('#jp_nodeName').text(node.jp_nodeName);
		$('#jp_launcherName').text(node.jp_launcherName);
		$('#jp_launchTime').text(parseDate(new Date(node.jp_launchTime)));
		$('#jp_codevalue').text(node.jp_codevalue);
		// get bill main data
		if(node.jp_keyName && node.jp_keyValue) {
			getBillMain(node.jp_caller, node.jp_url, node.jp_keyName + '=\'' + node.jp_keyValue + '\'', node.jp_keyValue);
		}
		// has detail
		if(node.jp_formDetailKey) {
			$('#detail-header').bind('boxready', function(){
				getBillDetail(node.jp_caller, node.jp_url, node.jp_formDetailKey + '=\'' + node.jp_keyValue + '\'');
			});
		} else {
			$('#detail-header').css('display', 'none');
		}
		// main point
		getMainPoint(node.jp_nodeId);
		// deal relative
		if(true){
			dealRelative(node.jp_caller);
		}
	};
	// parse main point
	var parseMainPoint = function(points) {
		var html = '<ul class="list-group">';
		html += '<li class="list-group-item disabled"><strong>问题要点</strong></li>';
		$.each(points, function(i, p){
			html += '<li class="list-group-item">';
			html += '<span>' + p.text + '</span>';
			if(p.type === 'B') {
				html += '<div class="pull-right"><div class="has-switch switch-small"><div class="' + (p.value && p.value != '是' ? 'switch-off' : 'switch-on') + ' switch-animate"><input type="checkbox" ' + (p.value && p.value != '是' ? '' : 'checked') + ' title="' + p.text + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') + '> <span class="switch-left switch-success switch-small">是</span> <label class="switch-small">&nbsp;</label> <span class="switch-right switch-warning switch-small">否</span></div></div></div>';
			} else if(p.type === 'S' || p.type === 'N') {
				html += '<div class="pull-right"><input class="form-control input-xs" type="text" placeholder="..." value="' + (p.value || '') + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') + ' title="' + p.text + '" ' + (p.required ? 'required' : '') + '></div>';
			} else if(p.type === 'D') {
				html += '<div class="pull-right"><input class="form-control input-xs" type="date" placeholder="..." value="' + (p.value || '') + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') + ' title="' + p.text + '" ' + (p.required ? 'required' : '') + '></div>';
			}
			html += '</li>';
		});
		html += '</ul>';
		$('#points').html(html);
		// toggle switch
		$('#points .has-switch').click(function(){
			var e = $(this), box = e.find('input'), checked = box.is(':checked');
			if(checked)
				box.removeAttr('checked');
			else
				box.attr('checked','checked');
			e.find('>div').removeClass(checked ? 'switch-on' : 'switch-off');
			e.find('>div').addClass(checked ? 'switch-off' : 'switch-on');
		});
	};
	// parse bill main
	var parseBillMain = function(main) {
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
		$('#bill-main').html(html);
	};
	// parse bill detail
	var parseBillDetail = function(detail, content){
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
		content.html(html);
		var table = $('table', content);
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
	// parse history
	var parseHistory = function(hist) {
		var html = '<ul class="list-unstyled list-inline">';
		$.each(hist, function(i, h){
			var res = h.jn_dealResult == '同意' ? 'success' : (h.jn_dealResult == '不同意' ? 'error' : 'warning');
			html += '<li>';
			html += '<div class="text-top">';
			html += '<span>' + h.jn_name + '</span>';
			html += '</div>';
			html += '<blockquote>';
			html += '<strong>' + h.jn_dealManName + '</strong>';
			html += '<div class="text-trans ' + res + '">';
			html += '<span>' + h.jn_dealResult + '</span>';
			html += '</div>';
			html += '<footer>' + h.jn_dealTime + '</footer>';
			html += '</blockquote>';
			if(h.jn_operatedDescription || h.jn_nodeDescription || h.jn_infoReceiver) {
				html += '<div class="highlight">';
				if(h.jn_nodeDescription)
					html += '<div>' + h.jn_nodeDescription + '</div>';
				if(h.jn_infoReceiver)
					html += '<p class="text-muted"><i>' + h.jn_infoReceiver + '</i></p>';
				if(h.jn_operatedDescription) {
					if(h.jn_operatedDescription.indexOf('(是)') > 0 || 
							h.jn_operatedDescription.indexOf('(否)') > 0) {
						html += '<ul class="list-group">';
						var descs = h.jn_operatedDescription.split(';');
						$.each(descs, function(j, d){
							res = d.substr(d.length - 3) == '(是)' ? 'glyphicon glyphicon-ok text-success' : 
								'glyphicon glyphicon-remove text-warning';
							html += '<li class="list-group-item"><span class="pull-right ' + res + '"></span>' + d.substr(0, d.length-3) + '</li>';
						});
						html += '</ul>';
					} else {
						html += '<div>' + h.jn_operatedDescription + '</div>';
					}
				}
				html += '</div>';
			}
			html += '</li>';
		});
		html += '</ul>';
		$('#history').html(html);
	};
	// get process by node id
	var nodeId = getUrlParam('nodeId');
	if (nodeId) {
		getProcess(nodeId);
		// get history by instance id
		$('#history-header').bind('boxready', function(){
			if(instance) {
				getHistory(instance);
			} else {
				getInstance(nodeId, function(instanceId){
					getHistory(instanceId);
				});
			}
		});
	}
	// get radio,checkbox value
	var getBoxValue = function(selector) {
		var value = null;
		$(selector).each(function(){
			var t = $(this);
			if(t.is(":checked")) {
				value = t.val();
			}
		});
		return value;
	};
	// node next step
	var nextStep = function(callback) {
		$.post(basePath + 'common/dealNextStepOfPInstance.action', {
			processInstanceId: instance,
			_noc: 1
		}, function(result, text) {
			if(result.hasNext) {
				if (result.actorUsers.length > 0 && result.actorUsers[0].JP_CANDIDATES.length>0) {
					var html = '<p>';
					html += "下一步审批节点有多位处理人,请指定一位：";
					html += '</p>';
					html += '<form>';
					html += '<div class="form-group" style="max-height:150px;overflow-y:auto;">';
					$.each(result.actorUsers[0].JP_CANDIDATES, function(i, u){
						html += '<label class="radio-inline" style="margin-bottom:10px;margin-right:20px;">';
						html += '<input type="radio" name="user" value="' + u + '"' + 
							(i == 0 ? ' checked="checked"' : '') + ' style="position:static;"> ' + u;
						html += '</label>';
					});
					html += '</div>';
					html += '<div class="btn-group btn-group-xs btn-group-justified">';
					html += '<div class="btn-group"><button type="button" class="btn btn-primary" id="confirm">指定</button></div>';
					html += '<div class="btn-group"><button type="button" class="btn btn-default" id="cancel">暂不指定</button></div>';
					html += '</div>';
					html += '</form>';
					dialog.show('下一步', html);
					// choose one to handle next node
					$('#confirm').click(function(){
						appointNext(result.actorUsers[0].JP_NODEID, getBoxValue('form input[type="radio"]'), callback);
					});
					// notice evenyone to receive next node
					$('#cancel').click(function(){
						callback && callback.call();
					});
				} else {
					callback && callback.call();
				}
			}
		});
	};
	// appoint one to deal next
	var appointNext = function(nodeId, user, callback) {
//		$.post(basePath + 'common/takeOverTask.action', {
//			em_code: user.substring(user.lastIndexOf('(') + 1, user.length - 1),
//			nodeId: nodeId,
//			_noc: 1
//		}, function(result, text) {
//			if(result.success)
//				callback && callback.call();
//			else
//				dialog.show('错误', '指派失败，请稍后再试！');
//		});
		$.ajax({
			url:basePath + 'common/takeOverTask.action',
			type: 'POST',
			data: {em_code: user.substring(user.lastIndexOf('(') + 1, user.length - 1), nodeId: nodeId, _noc: 1},
			success: function(result){
				if(result.success)
					callback && callback.call();
			},
			error: function(xhr){
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取信息失败', '请先登录！');
					} else {
						dialog.show('指派失败', response.exceptionInfo);
					}
				}
			}
		});
	};

	// load next process
	var processNext = function(id) {
		if(id != '-1') {
			var url = window.location.href;
			window.location.href = url.substr(0, url.indexOf('?') + 1) + 'nodeId=' + id;
		} else {
			dialog.show('提示', '暂时没有新的审批任务了！');
		}
	};
	// is valid
	var isValid = function() {
		var deals = $('#points input');
		if(deals.length > 0) {
			$.each(deals, function(i, d){
				var e = $(d), type = e.attr('type'), val = e.val();
				if((type === 'text' || type === 'date') && !val)
					return false;
			});
		}
		return true;
	};
	// get your points
	var getDealPoints = function() {
		var deals = $('#points input');
		if(deals.length > 0) {
			var points = [];
			$.each(deals, function(i, d){
				var e = $(d), text = e.attr('title'), type = e.attr('type'), val = e.val(),
					lg = e.attr('logic');
				val = type == 'checkbox' ? (e.is(':checked') ? '是' : '否') : val;
				points.push(text + '(' + val + ')' + (lg ? '@' + lg + '@' : ''));
			});
			return points.join(';');
		}
		return null;
	};
	// agree
	var agree = function() {
		if(!isValid()) {
			dialog.show(' 警告', '您还有审批要点问题没处理！', -1);
			return;
		}
		setLoading(true);
//		$.post(basePath + 'common/review.action', {
//			taskId: nodeId,
//			nodeName: current.jp_nodeName,
//			nodeLog: $('#deal-msg').val(),
//			holdtime: ((new Date() - readyTime) / 1000).toFixed(0),
//			customDes: getDealPoints(),
//			result: true,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			if(result.success) {
//				if (result.nextnode == '0') {
//					dialog.show('审批成功', '您暂时没有其它的审批任务了。');
//				} else {
//					if(result.after) {
//						if (result.after.trim().substr(0, 12) == 'AFTERSUCCESS') { 
//							dialog.show('审批出现提示：' + result.after.replace('AFTERSUCCESS', ''), 
//									'自动为您跳转到下一条...', 5, function(){
//								nextStep(function(){
//									processNext(result.nextnode);
//								});
//							});
//						} else {
//							dialog.show('审批出现异常', result.after);
//						}
//					} else {
//						dialog.show('审批成功', '自动为您跳转到下一条...', 1, function(){
//							nextStep(function(){
//								processNext(result.nextnode);
//							});
//						});
//					}
//				}
//			} else if (result.exceptionInfo){
//				dialog.show('无法审批', result.exceptionInfo);
//			} else {
//				dialog.show('处理结果', "该任务已处理,不能重复操作！");
//			}
//		});
		$.ajax({
			url:basePath + 'common/review.action',
			type: 'POST',
			data: {taskId: nodeId, nodeName: current.jp_nodeName, nodeLog: $('#deal-msg').val(),
				   holdtime: ((new Date() - readyTime) / 1000).toFixed(0),customDes: getDealPoints(),result: true,_noc: 1},
			success: function(result){
				setLoading(false);
				if(result.success) {
					if (result.nextnode == '0') {
						dialog.show('审批成功', '您暂时没有其它的审批任务了。');
					} else {
						if(result.after) {
							if (result.after.trim().substr(0, 12) == 'AFTERSUCCESS') { 
								dialog.show('审批出现提示：' + result.after.replace('AFTERSUCCESS', ''), 
										'自动为您跳转到下一条...', 5, function(){
									nextStep(function(){
										processNext(result.nextnode);
									});
								});
							} else {
								dialog.show('审批出现异常', result.after);
							}
						} else {
							dialog.show('审批成功', '自动为您跳转到下一条...', 1, function(){
								nextStep(function(){
									processNext(result.nextnode);
								});
							});
						}
					}
				}
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('无法审批', response.exceptionInfo);
					}
				}
			}
		});
	};
	$('#agree').click(function(){
		agree();
	});
	// disagree -> return back
	var disagree = function(backNode, msg){
		setLoading(true);
//		$.post(basePath + 'common/review.action', {
//			taskId: nodeId,
//			nodeName: current.jp_nodeName,
//			backTaskName: backNode,
//			nodeLog: msg,
//			holdtime: ((new Date() - readyTime) / 1000).toFixed(0),
//			result: false,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			if(result.success) {
//				if(result.after) {
//					dialog.show('回退过程出现提示：' + result.after);
//				} else {
//					dialog.show('回退成功', '自动为您跳转到下一条...', 1, function(){
//						nextStep(function(){
//							processNext(result.nextnode);
//						});
//					});
//				}
//			} else {
//				dialog.show('处理结果', "该任务已处理,不能重复操作！");
//			}
//		});
		$.ajax({
			url:basePath + 'common/review.action',
			type: 'POST',
			data: {taskId: nodeId, nodeName: current.jp_nodeName, backTaskName: backNode, nodeLog: msg,
					holdtime: ((new Date() - readyTime) / 1000).toFixed(0), result: false, _noc: 1},
			success: function(result){
				setLoading(false);
				if(result.success) {
					if(result.after) {
						dialog.show('回退过程出现提示：' + result.after);
					} else {
						dialog.show('回退成功', '自动为您跳转到下一条...', 1, function(){
							nextStep(function(){
								processNext(result.nextnode);
							});
						});
					}
				} else {
					dialog.show('处理结果', "该任务已处理,不能重复操作！");
				}
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	$('#disagree').click(function(){
		getHistory(instance, function(nodes){
			var html = '<form>';
			html += '<div class="form-group">';
			html += '<label class="radio-inline">';
			html += '<input type="radio" name="node" value="RECORDER" checked="checked"> 制单人';
			html += '</label>';
			$.each(nodes, function(i, n){
				html += '<label class="radio-inline">';
				html += '<input type="radio" name="node" value="' + n.jn_name + '"> ' + n.jn_name;
				html += '</label>';
			});
			html += '</div>';
			html += '<div class="form-group"><textarea id="back-msg" rows="2" placeholder="填写您不同意的原因..." class="form-control">' + $('#deal-msg').val() + '</textarea><span class="help-block text-lg text-error" id="back_err"></span></div>';
			html += '<div class="btn-group btn-group-xs btn-group-justified">';
			html += '<div class="btn-group"><button type="button" class="btn btn-primary" id="back">回退</button></div>';
			html += '<div class="btn-group"><button type="button" class="btn btn-default" id="cancel2">取消</button></div>';
			html += '</div>';
			html += '</form>';
			dialog.show('回退到节点', html);
			// choose one to back
			$('#back').click(function(){
				var msg = $('#back-msg').val();
				if (!msg) {
					$('#back_err').text('请填写您不同意的原因!');
					$('#back-msg').focus();
				} else {
					disagree(getBoxValue('form input[type="radio"]'), msg);
				}
			});
			// cancel
			$('#cancel2').click(function(){
				dialog.hide();
			});
		});
	});
	// end
	var end = function(){
		setLoading(true);
//		$.post(basePath + 'common/endProcessInstance.action', {
//			processInstanceId: instance,
//			holdtime: ((new Date() - readyTime) / 1000).toFixed(0),
//			nodeId: nodeId,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			if(result.success) {
//				dialog.show('流程已结束', '自动为您跳转到下一条...', 1, function(){
//					nextStep(function(){
//						processNext(result.nextnode);
//					});
//				});
//			} else {
//				dialog.show('警告', "流程实例不存在！");
//			}
//		});
		$.ajax({
			url:basePath + 'common/endProcessInstance.action',
			type: 'POST',
			data: {processInstanceId: instance, holdtime: ((new Date() - readyTime) / 1000).toFixed(0),
				   nodeId: nodeId, _noc: 1},
			success: function(result){
				setLoading(false);
				if(result.success) {
					dialog.show('流程已结束', '自动为您跳转到下一条...', 1, function(){
						nextStep(function(){
							processNext(result.nextnode);
						});
					});
				} else {
					dialog.show('警告', "流程实例不存在！");
				}
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	$('#end').click(function(){
		end();
	});
	// change
	var change = function(user, msg){
		if(!user) {
			$('#change_err').text('您还没选择变更人！');
			return;
		}
		if(!isValid()) {
			$('#change_err').text('您还有审批要点问题没处理！');
			return;
		}
		setLoading(true);
//		$.post(basePath + 'common/setAssignee.action', {
//			taskId: nodeId,
//			assigneeId: user,
//			processInstanceId: instance,
//			customDes: getDealPoints(),
//			description: msg,
//			_noc: 1
//		}, function(result, text) {
//			setLoading(false);
//			if(result.result) {
//				dialog.show('处理结果', '节点已成功变更');
//			} else {
//				dialog.show('处理结果', "任务不存在，无法变更！");
//			}
//		});
		$.ajax({
			url:basePath + 'common/setAssignee.action',
			type: 'POST',
			data: {taskId: nodeId, assigneeId: user, processInstanceId: instance,
				   ncustomDes: getDealPoints(), description: msg, _noc: 1},
			success: function(result){
				setLoading(false);
				if(result.result) {
					dialog.show('处理结果', '节点已成功变更');
				} else {
					dialog.show('处理结果', "任务不存在，无法变更！");
				}
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response =$.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};
	$('#change').click(function(){
		var html = '<form>';
		html += '<div class="form-group has-feedback dropdown"><input id="em_new" name="em_new" class="form-control  dropdown-toggle" type="text" placeholder="新的处理人，点击搜索"/><span id="change_search_icon" class="glyphicon glyphicon-search form-control-feedback"></span><div class="dropdown-menu"><div id="em_search" class="list-group"></div></div>' +
				'<span class="help-block text-lg">' +
					'<span id="change_name"></span>' +
					'<span id="em_code"></span>' +
				'</span></div>';
		html += '<div class="form-group"><textarea id="change-msg" rows="2" placeholder="填写您要变更的原因..." class="form-control">' + $('#deal-msg').val() + '</textarea><span class="help-block text-lg text-error" id="change_err"></span></div>';
		html += '<div class="btn-group btn-group-xs btn-group-justified">';
		html += '<div class="btn-group"><button type="button" class="btn btn-primary" id="change3">变更</button></div>';
		html += '<div class="btn-group"><button type="button" class="btn btn-default" id="cancel3">取消</button></div>';
		html += '</div>';
		html += '</form>';
		dialog.show('变更处理人', html);
		// search deal man
		$('#em_new').bind('focus', function(){
			if($(this).val()) {
				$(this).parent().children('.dropdown-menu').css('display', 'block');
			}
		});
		$('#em_new').bind('change', function(){// 点击获取变更的候选人
			var v = $(this).val();
			var view = $(this).parent().children('.dropdown-menu');
			if(v) {
				view.css('display', 'block');
				getSearchResult(v);
			} else {
				view.css('display', 'none');
			}
		});
		$('#em_new').bind('keyup', function(event){// 回车获取变更的候选人
			if(event.keyCode == 13) {
				event.preventDefault();
				var v = $(this).val();
				var view = $(this).parent().children('.dropdown-menu');
				if(v) {
					view.css('display', 'block');
					getSearchResult(v);
				} else {
					view.css('display', 'none');
				}
			}
		});
		
		$('#change_search_icon').bind('click', function(event){// 变更查询放大镜图标点击获取变更的候选人
			var v = $('#em_new').val();
			var view = $('#em_new').parent().parent().children('.dropdown-menu');
			if(v) {
				view.css('display', 'block');
				getSearchResult(v);
			} else {
				view.css('display', 'none');
			}
		});
		
		// change
		$('#change3').click(function(){
			var msg = $('#change-msg').val();
			change($('#em_code').text(), msg);
		});
		// cancel
		$('#cancel3').click(function(){
			dialog.hide();
		});
	});
	// get search result
	var getSearchResult = function(input) {
		setLoading(true);
		$.post(basePath + 'hr/emplmana/search.action', {
			keyword: input
		}, function(result, text){
			setLoading(false);
			if(result.length > 0) {
				parseSearchResult(result);
			}
		});
	};
	// parse search result
	var parseSearchResult = function(datas){
		var html = '';
		$.each(datas, function(i, d){
			var e = d.split('\n');
			html += '<a href="javascript:onItemClick(\'' + e[1] + '\',\'' + e[2] + '\');" class="list-group-item">';
			html += '<span class="left">' + (e[0] || '（空）') + '</span>';
			html += '<span class="right">' + e[2] + '(' + e[1] + ')</span>';
			html += '</a>';
		});
		$('#em_search').html(html);
	};

	//关联查询
	var dealRelative = function(caller) {
		$('#expand').html('展开&nbsp;<span '+
			'class="glyphicon glyphicon-share-alt"></span>');
		$('#expand').click(function(){
			$('#expand').addClass('hidden');
			$('#re-content').removeClass('hidden');
			$('#shrink').removeClass('hidden');
		});
		$('#expand').bind('boxready', function(){
			setLoading(true);
//			$.post(basePath + 'common/form/relativeSearchMobile.action', {
//				caller: caller
//			}, function(result){
//				setLoading(false);
//				if(result.exceptionInfo) {
//					dialog.show('错误', '数据加载错误，请重试', 1, function(){dialog.hide();})
//				} else {
//					$('#relative .title .text').css('width', (138+result.data[0].form.title.length*18)+'px');
//					$('#relative .title .text').append('-'+result.data[0].form.title);
//					var html = '';
//					html += '<div class="control-group">'+
//								'<label class="control-lable" for="'+result.data[0].form.items[0].name+'">'+
//								result.data[0].form.items[0].fieldLabel+':</label>'+
//								'<select type="text" id="'+result.data[0].form.items[0].name+'" name="'+
//								result.data[0].form.items[0].name+'" class="form-control"></select>'+
//								'<button id="re-search" type="button" class="btn btn-success">筛选</button>'+
//							'</div>';
//					$('#re-filtrate').html(html);
//				}
//			});
			$.ajax({
				url:basePath + 'common/form/relativeSearchMobile.action', 
				type: 'POST',
				data: {caller: caller},
				success: function(result){
					setLoading(false);
					$('#relative .title .text').css('width', (138+result.data[0].form.title.length*18)+'px');
					$('#relative .title .text').append('-'+result.data[0].form.title);
					var html = '';
					html += '<div class="control-group">'+
								'<label class="control-lable" for="'+result.data[0].form.items[0].name+'">'+
								result.data[0].form.items[0].fieldLabel+':</label>'+
								'<select type="text" id="'+result.data[0].form.items[0].name+'" name="'+
								result.data[0].form.items[0].name+'" class="form-control"></select>'+
								'<button id="re-search" type="button" class="btn btn-success">筛选</button>'+
							'</div>';
					$('#re-filtrate').html(html);
				},
				error: function(xhr){
					setLoading(false);
					if(xhr.responseText) {
						var response =$.evalJSON(xhr.responseText);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog.show('获取信息失败', '请先登录！', 1, function(){dialog.hide();})
						} else {
							dialog.show('错误', response.exceptionInfo,1, function(){dialog.hide();});
						}
					}
				}
			});
		});
		$('#shrink').click(function(){
			$('#expand').removeClass('hidden');
			$('#re-content').addClass('hidden');
			$('#shrink').addClass('hidden');
		});
	}; 

	// touch on mobile need jquery-mobile.js & event 'tap'
	window.onItemClick = function(code, name) {
		$('#em_search').parent().css('display', 'none');
		$('#change_name').text(name + ' - ')
		$('#em_code').text(code);
	};
});