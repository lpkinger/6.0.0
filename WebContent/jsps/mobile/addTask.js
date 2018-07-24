'use strict';
$(function(){
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
	
	//给任务名和任务处理人默认值
	if ($('input[name="name"]').val() == "") {
		$('input[name="name"]').val(getUrlParam('name'));
	}
	if(getUrlParam('resourcename')) {
		showDefault(getUrlParam('resourcename'));
	} 
	
	// 默认需要回复
	$('input[name="type"]').trigger('click');
	
	// 输入框右侧图标点击使输入框获得焦点
	$('.weui_cell_input .weui_cell_ft').on('click', function(){
		$('input', $(this).parent()).trigger('focus');
	});
	
	// 已选择指定人的容器
	var checkedUsers = [];
	
	// Loading框，封装了show 和 hide 方法
	var Loading = function(){
		var loading = $('#loadingToast');
		
		return {
			show: function(){loading.css('display', 'block')},
			hide: function(){loading.css('display', 'none')}
		};
	}();
	
	//系统默认时间
	Date.prototype.Format = function (fmt) { //author: meizz 
	    var o = {
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "h+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	}
	
	// dialog对话框
	var dialog = function(title, content, footer, fn){
		$('#dialog .weui_dialog .weui_dialog_title').text(title);
		$('#dialog .weui_dialog .weui_dialog_bd').text(content);
		if(footer) {// 如果传了按钮字符，设置按钮字符
			$('#dialog .weui_dialog .primary').text(footer);
		}
		$('#dialog .weui_dialog .weui_dialog_ft').on('click', function() {
			$('#dialog').css('display', 'none');
			if(fn) {// 如果传了回调函数，调用回调函数
				fn.call(null);
			}
		});
		$('#dialog').css('display', 'block');
	};
	
	// 检验用户是否已经被选中，未被选中返回-1，选中返回index
	function checked(em) {
		var r = -1;
		$.each(checkedUsers, function(k, v) {
			if(v.em_code == em.em_code) {
				r = k;
				return r;
			}
		});
		return r;
	};
	
	// 打开指定人的页面
	function openUserTab(){
		$('#userTab').addClass('open');
		//初始化数据
		var rootData = [];
		//节点层级数 最多10层
		var leafNumber = ['containerFirst','containerTwo','containerThree','containerFour','containerFive','containerSix','containerSeven','containerEight','containerNine','containerTen']
		var parentEle = 'containerFirst';
		function spanNumConvert(num) {
			var str = num.slice(0,num.indexOf('span'));
			var lenfNum = 'container' + str.replace(/(^|\s+)\w/g,function(s){return s.toUpperCase();});
			return lenfNum;
		}
		function StrConvert(str){
			switch(str)
			{
			case 'First':
			  return 'twospan';
			  break;
			case 'Two':
				  return 'threespan';
				  break;
			case 'Three':
				  return 'fourspan';
				  break;
			case 'Four':
				  return 'fivespan';
				  break;
			case 'Five':
				  return 'sixspan';
				  break;
			case 'Six':
				  return 'sevenspan';
				  break;
			case 'Seven':
				  return 'eightspan';
				  break;
			case 'Eight':
				  return 'ninespan';
				  break;
			case 'Nine':
				  return 'tenspan';
				  break;
			default:
			 return str;
			 break;
			}
		}
		function NumConvert(str) {
			switch(str)
			{
			case 'containerFirst':
			  return 1;
			  break;
			case 'containerTwo':
				  return 2;
				  break;
			case 'containerThree':
				  return 3;
				  break;
			case 'containerFour':
				  return 4;
				  break;
			case 'containerFive':
				  return 5;
				  break;
			case 'containerSix':
				  return 6;
				  break;
			case 'containerSeven':
				  return 7;
				  break;
			case 'containerEight':
				  return 8;
				  break;
			case 'containerNine':
				  return 9;
				  break;
			default:
			 return str;
			 break;
			}
		}
		var leafInfo = {
			or_id: null,
			page: 1,
			pageSize: 100
		};
		//查看根节点
		function getRoot(str) {
			$('#userTab .firstdepto .'+ str +' .uas-seft').on('click', function(e) {
				parentEle = $(this).parent('div').attr('class');
				$('.'+ parentEle).css('display', 'none');
				$('#userTab .uas span').removeClass('active');
				var v_id = $(this).attr('id');
				var hrorg = {};
				//第几层数据
				var num = NumConvert(str);
				if(rootData[num-1].hasOwnProperty('hrorgs')) {
					$.each(rootData[num-1].hrorgs, function(k, v){
						if(v.or_code  == v_id) {
							hrorg = v;
						}
					});
				}
				// 获取对应层级
				var spanNumber = StrConvert(parentEle.substr(9));
				var parentNumber = parentEle.substr(9).toLowerCase() + "span"
				var html = '<span id="' + spanNumber + '" class="active">'+ hrorg.or_name +'<i class="fa fa-caret-right"></i></span>';
				$('#userTab .uas #' + parentNumber).next('span').remove();
				$(html).insertAfter('#userTab .uas #' + parentNumber);
				leafInfo.or_id = hrorg.or_id;
				getLeaf(parentEle);
				$('#userTab .uas #' + spanNumber).on('click', function(event) {
					$('#' + spanNumber).addClass('active');
					$('.'+ spanNumConvert(spanNumber)).css('display', 'block');
					if($(this).next('span').length >= 1) {
						rootData.splice(NumConvert(spanNumConvert(spanNumber)),rootData.length);
					}
 					while($('.' + spanNumConvert(spanNumber)).next('div') && $('.' + spanNumConvert(spanNumber)).next('div').length != 0) {
						$('.' + spanNumConvert(spanNumber)).next('div').remove();
					}
					while($(this).next('span') && $(this).next('span').length != 0) {
						$(this).next('span').remove();
						
					}
					event.stopPropagation();   //  阻止事件冒泡
				});
			});
		}
		//查询叶子节点目录
		function getLeaf(str) {
			function getNextContainer(str) {
				for (var i=0;i<=leafNumber.length;i++) {
						if(leafNumber[i] == str){
							return leafNumber[i+1]; 
						}
				}
			}
			Loading.show();
			$.ajax({
				url: 'mobile/getLeafHrorg.action',
				type: 'GET',
				data: leafInfo,
				success: function(data) {
					if(data.hrorgs.length == 0 && data.employees.length == 0) {
						dialog('提示', '未查询到数据，请返回上一层');
					}
					if(rootData.length == NumConvert(str)) {
						rootData.push(data);
					}
					var nextContainer = getNextContainer(str);
					var htmlTwo = '<div class="' + nextContainer + '"></div>';
					$('#userTab .firstdepto').append(htmlTwo);
					$.each(data.hrorgs,function(k,v) {
						if(data.hrorgs.length == k+1) { 
						var html3 = '<a id="' + v.or_code + '" class="weui_cell uas-seft" href="javascript:;" style="margin-bottom: -10px;">' +
										'<div class="weui_cell_hd">' +
											'<img src="jsps/mobile/img/iconfont_bumen.png" alt="icon"style="width: 20px; margin-right: 5px; display: block">' +
										'</div>' + 
										'<div class="weui_cell_bd weui_cell_primary">' +
											'<p>' + v.or_name + '</p></div><div class="weui_cell_ft">'+ v.or_name + 
										'</div>' +
									'</a>';
						}else {
							var html3 = '<a id="' + v.or_code + '" class="weui_cell uas-seft" href="javascript:;">' +
											'<div class="weui_cell_hd">' +
												'<img src="jsps/mobile/img/iconfont_bumen.png" alt="icon"style="width: 20px; margin-right: 5px; display: block">' +
											'</div>' + 
											'<div class="weui_cell_bd weui_cell_primary">' +
												'<p>' + v.or_name + '</p></div><div class="weui_cell_ft">'+ v.or_name + 
											'</div>' +
						'				</a>';
						}
						$('#userTab .firstdepto .' + nextContainer).append(html3);
					})
					
					if(data.hasOwnProperty('employees')) {
						$('#userTab .firstdepto .' + nextContainer).append('<div class="weui_cells weui_cells_checkbox" style="margin-left: 2px;"></div>')
						var html = '';
						$.each(data.employees, function(k, v){
							html += userLabel(v);// 增加一个指定人选中项
						});
						$('#userTab .firstdepto .' + nextContainer +' .weui_cells_checkbox').append(html);
						// 绑定点击列表添加到指定人的事件
						$('#userTab .firstdepto .' + nextContainer +' .userLabel input').on('change', function(e){
							var em_name = $('.em_name', $(this).parent().parent()).text();// 用户编号
							var em_code = $(this).attr('name');// 用户名
							checkUserName({em_code: em_code, em_name: em_name});// 改变已选择的人
						});
					}
					Loading.hide();
					getRoot(nextContainer);
				},
				error: function(data){
					Loading.hide();// 关闭加载框
					dialog('系统错误', '任务添加失败，请联系系统管理员');
				}
			})
		}
		
		//查询顶级目录
		Loading.show();
		$.ajax({
			url: 'mobile/getRootHrorg.action',
			type: 'POST',
			data: {master: 'UAS'},
			success: function(data){
				if (!$('#userTab .firstdepto div').hasClass('containerFirst')) {
					var htmlFirst = '<div class="containerFirst"></div>';
					$('#userTab .firstdepto').append(htmlFirst);
					$.each(data.hrorgs, function(k, v){
						if(data.hrorgs.length == k+1) {
							var html2 = '<a id="' + v.or_code + '" class="weui_cell uas-seft" href="javascript:;" style="margin-bottom: -10px;">' +
											'<div class="weui_cell_hd">' +
												'<img src="jsps/mobile/img/iconfont_bumen.png" alt="icon"style="width: 20px; margin-right: 5px; display: block">' +
											'</div>' + 
											'<div class="weui_cell_bd weui_cell_primary">' +
												'<p>' + v.or_name + '</p></div><div class="weui_cell_ft">'+ v.or_name + 
											'</div>' +
										'</a>';
						} else {
							var html2 = '<a id="' + v.or_code + '" class="weui_cell uas-seft" href="javascript:;">' +
											'<div class="weui_cell_hd">' +
												'<img src="jsps/mobile/img/iconfont_bumen.png" alt="icon"style="width: 20px; margin-right: 5px; display: block">' +
											'</div>' + 
											'<div class="weui_cell_bd weui_cell_primary">' +
												'<p>' + v.or_name + '</p></div><div class="weui_cell_ft">'+ v.or_name + 
											'</div>' +
										'</a>';
						}
						$('#userTab .firstdepto .containerFirst').append(html2);
					});
					if(data.hasOwnProperty('employees')) {
						$('#userTab .firstdepto .containerFirst').append('<div class="weui_cells weui_cells_checkbox" style="margin-top: -5px;"></div>')
						var html = '';
						$.each(data.employees, function(k, v){
							html += userLabel(v);// 增加一个指定人选中项
						});
						$('#userTab .firstdepto .containerFirst .weui_cells_checkbox').append(html);
						// 绑定点击列表添加到指定人的事件
						$('#userTab .firstdepto .containerFirst .userLabel input').on('change', function(e){
							var em_name = $('.em_name', $(this).parent().parent()).text();// 用户编号
							var em_code = $(this).attr('name');// 用户名
							checkUserName({em_code: em_code, em_name: em_name});// 改变已选择的人
						});
					}
					if(rootData.length == 0) {
						rootData.push(data);
					}
					getRoot('containerFirst');
					$('#userTab .uas #firstspan').on('click', function(event) {
						if(rootData.length > 0) {
							rootData.splice(1,rootData.length);
						}
						$('#firstspan').addClass('active');
						$('.containerFirst').css('display', 'block');
						$('.containerFirst').siblings('div').remove();
						while($(this).next('span') && $(this).next('span').length != 0){
							$(this).next('span').remove();
						}
						event.stopPropagation();   //  阻止事件冒泡
					});
					Loading.hide();// 关闭加载框
				}else {
					Loading.hide();// 关闭加载框
				}
			},
			error: function(data){
				Loading.hide();// 关闭加载框
				dialog('系统错误', '任务添加失败，请联系系统管理员');
			}
		})
	};
	
	// 关闭指定人的页面
	function closeUserTab() {
		$('#userTab').removeClass('open');
	};
	
	$('.openUserTab').on('click', openUserTab);
	$('.closeUserTab').on('click', closeUserTab);
	
	// 将用户转化为指定人选中Lebel
	function userLabel(em) {
		var em_position = em.em_position ? '[' + em.em_position + ']': '';
		var em_name = em.em_name;
		var em_code = em.em_code;
		var em_checked = checked(em);// 是否已经选择了
		var html = '<label class="weui_cell weui_check_label userLabel" for="' + em_code + '">' +
                       '<div class="weui_cell_bd weui_cell_primary">' +
                           '<p>' +
                            	'<i class="fa fa-user"></i> <span class="em_name">' + em_name + '</span> ' + em_position +
                            '</p>' +
                       '</div>' +
                       '<div class="weui_cell_ft">' +
                           '<input type="checkbox" class="weui_check" name="' + em_code + '" id="' + em_code + '" ' + (em_checked != -1 ? 'checked="checked"': '') +'>' +
                           '<i class="weui_icon_checked"></i>' +
                       '</div>' +
                   '</label>';
        return html;
	};
	// 将已选择的人转化为展示的按钮
	function addUserButtonName(em) {
		var html = '<button class="weui_btn weui_btn_mini weui_btn_warn" title="' + em.em_code + '">' + em.em_name + ' <i class="fa fa-close"></i></button> ';
		$('.checkedUsers .weui_cell_bd').append(html);
		// 绑定点击按钮删除的事件
		$('button[title="' + em.em_code + '"]').on('click', function(){
			var inputs = $('.firstdepto input[name="' + $(this).attr('title') + '"]');
			if(inputs.length > 0) {
				inputs.trigger('click');
			} else {
				checkUserName({em_code: em.em_code, em_name: em.em_name});// 改变已选择的人
			}
		});
	};
	// 点击一个用户选择，添加或删除指定人
	function checkUserName(em){
		var i = checked(em);
		if(i != -1) {// 已选择的，去掉
			checkedUsers.splice(i, 1);
			$('.checkedUsers .weui_cell_bd button[title="' + em.em_code + '"]').remove()
		} else {// 未选择的，添加
			checkedUsers.push(em);
			addUserButtonName(em);
		}
		$('.checkedSize').text(checkedUsers.length);
	};
	
	// 将已选择的人转化为展示的按钮
	function addUserButton(em) {
		var html = '<button class="weui_btn weui_btn_mini weui_btn_warn" title="' + em.em_code + '">' + em.em_name + ' <i class="fa fa-close"></i></button> ';
		$('.checkedUsers .weui_cell_bd').append(html);
		// 绑定点击按钮删除的事件
		$('button[title="' + em.em_code + '"]').on('click', function(){
			$('#searchResult input[name="' + $(this).attr('title') + '"]').trigger('click');
		});
	};
	
	// 点击一个用户选择，添加或删除指定人
	function checkUser(em){
		var i = checked(em);
		if(i != -1) {// 已选择的，去掉
			checkedUsers.splice(i, 1);
			$('.checkedUsers .weui_cell_bd button[title="' + em.em_code + '"]').remove()
		} else {// 未选择的，添加
			checkedUsers.push(em);
			addUserButton(em);
		}
		$('.checkedSize').text(checkedUsers.length);
	};
	
	// 根据关键词查询用户结果
	function showDefault(resourcename) {
		var keyword = resourcename;
		try {
			$.ajax({
				url: 'hr/employee/getEmployees.action',
				type: 'POST',
				data: {condition: "em_name like '%" + keyword + "%' and nvl(em_class,' ')<>'离职'"},
				success: function(data, status) {
					if(data.success) {
						var html = '';
						$.each(data.employees, function(k, v){
							html += userLabel(v);// 增加一个指定人选中项
						});
						$('#searchResult .weui_cells_checkbox').html(html);
						$('#searchResult .weui_cells_title').text('共找到' + data.employees.length + '个可选的指定人');
						checkUser({em_code: data.employees[0].em_code, em_name: data.employees[0].em_name});
						$('.userLabel input').attr('checked', 'checked');
						// 绑定点击列表添加到指定人的事件
						$('.userLabel input').on('change', function(e){
							var em_name = $('.em_name', $(this).parent().parent()).text();// 用户编号
							var em_code = $(this).attr('name');// 用户名
							checkUser({em_code: em_code, em_name: em_name});// 改变已选择的人
						});
					}
					Loading.hide();
				},
				error: function(xhr) {
					Loading.hide();
					if(xhr.response) {
						var response = $.parseJSON(xhr.response);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog('提示', '您还未登录，请先登录');
						} else {
							dialog('错误', response.exceptionInfo);
						}
					}
				}
			});
		} catch(error) {
			Loading.hide();
			document.write(error.name + ' | ' + error.message);
		}
	};
	
	// 根据关键词查询用户结果
	function search() {
		var keyword = $('#search_input').val();
		if(!keyword) {// 没有输入关键词
			dialog('提示', '未指定关键词，点击确定重新加载界面');
			$('#searchResult .weui_cells_checkbox>label').remove();
			return;
		}
		Loading.show();
		try {
			$.ajax({
				url: 'hr/employee/getEmployees.action',
				type: 'POST',
				data: {condition: "em_name like '%" + keyword + "%' and nvl(em_class,' ')<>'离职'"},
				success: function(data, status) {
					if(data.success) {
						var html = '';
						$.each(data.employees, function(k, v){
							html += userLabel(v);// 增加一个指定人选中项
						});
						$('#searchResult .weui_cells_checkbox').html(html);
						$('#searchResult .weui_cells_title').text('共找到' + data.employees.length + '个可选的指定人');
						// 绑定点击列表添加到指定人的事件
						$('.userLabel input').on('change', function(e){
							var em_name = $('.em_name', $(this).parent().parent()).text();// 用户编号
							var em_code = $(this).attr('name');// 用户名
							checkUser({em_code: em_code, em_name: em_name});// 改变已选择的人
						});
					}
					Loading.hide();
				},
				error: function(xhr) {
					Loading.hide();
					if(xhr.response) {
						var response = $.parseJSON(xhr.response);
						if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
							dialog('提示', '您还未登录，请先登录');
						} else {
							dialog('错误', response.exceptionInfo);
						}
					}
				}
			});
		} catch(error) {
			Loading.hide();
			document.write(error.name + ' | ' + error.message);
		}
	};
	
	// 成功提示
	function showSuccess(form) {
		$('#successPage .msg_title').text(form.name);
		$('#successPage .msg_startdate').text(form.startdate);
		$('#successPage .msg_continuetime').text(form.duration);
		$('#successPage .msg_resourcename').text(form.resourcename);
		if(form.type) {
			$('#successPage .msg_type').text('需要');
		} else {
			$('#successPage .msg_type').text('不需要');
		}
		
		$('#successPage').css('display', 'block');
	}

	// 搜索框图标点击事件
	$('#search_icon').on('click' , search);
	//给开始时间默认值
	if($('input[name="startdate"]').val() == "") {
		$('input[name="startdate"]').val(new Date().Format("yyyy-MM-ddThh:mm"));
	}
	// 确定按钮事件
	$('#commit').on('click', function(){
		var form = {
			name: $('input[name="name"]').val(),
			duration: $('input[name="continuetime"]').val(),
			startdate: $('input[name="startdate"]').val().replace('T', ' ').substring(0, 16) + ':00',
			description: $('textarea[name="description"]').val()
		};
		
		// 是否需要回复
		if($('input[name="type"]').attr('checked')) {
			form.type = 1;
		} else {
			form.type = 0;
		}
		
		// 处理人
		form.resourcename = '';
		
		$.each(checkedUsers, function(k, v){
			if(form.resourcename) form.resourcename += ',';
			form.resourcename += v.em_name;
		});
		
		if(!form.name) {
			dialog('提示', '任务名称未输入');
			return;
		}
		if(form.startdate.length != 19) {
			dialog('提示', '开始日期未输入');
			return;
		}
		if(!form.resourcename) {
			dialog('提示', '您还未指定任务处理人');
			return;
		}
		if(!form.description) {
			dialog('提示', '任务描述未输入');
			return;
		}
		
		Loading.show();// 显示加载框
		// 提交请求
		$.ajax({
			url: 'plm/task/addbilltask.action',
			type: 'POST',
			data: {formStore: JSON.stringify(form)},
			success: function(data){
				Loading.hide();// 关闭加载框
				showSuccess(form);
			},
			error: function(data){
				Loading.hide();// 关闭加载框
				dialog('系统错误', '任务添加失败，请联系系统管理员');
			}
		});
	});
	
});