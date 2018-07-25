//读取cookies 
function getCookie(name) 
{ 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
} 

em_master = em_master || getCookie('master');//为防止从session读取数据失败

//标准时间格式配置
Date.prototype.format =function(format){
    var o = {
        "M+" : this.getMonth()+1, //month
        "d+" : this.getDate(), //day
        "h+" : this.getHours(), //hour
        "m+" : this.getMinutes(), //minute
        "s+" : this.getSeconds(), //second
        "q+" : Math.floor((this.getMonth()+3)/3), //quarter
        "S" : this.getMilliseconds() //millisecond
    }
    if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
        (this.getFullYear()+"").substr(4- RegExp.$1.length));
    for(var k in o)if(new RegExp("("+ k +")").test(format))
        format = format.replace(RegExp.$1,
                RegExp.$1.length==1? o[k] :
                ("00"+ o[k]).substr((""+ o[k]).length));
    return format;
};

var vr_id = null;
var vr_class;//表单字段

//根据getIdUrl获取对应的序列值
function getSeqId(getIdUrl) {
	var id;
	$.ajax({
		async: false,
		type: 'GET',
		url: basePath + getIdUrl,
		dataType: 'json',
		success: function(data){
			id = data.id;
		}
	});
	return id;
};
vr_id = getSeqId('common/getId.action?seq=VISITRECORD_SEQ');

//dbFind
// function addDbFinf(config) {
// 	$('#dbFindModal .modal-content .modal-title').text(config.title);

// }



$(document).ready(function(){

	//重置所有表单
	function resetAllForm() {
		$('#formStore')[0].reset();
		$('#pl')[0].reset();
		$('#cup')[0].reset();
		$('#pi')[0].reset();
		$('#fb')[0].reset();
	};
	
	// modal dialog
	var dialog = {
		show: function(title, content, timeout, callback){
			var back = $('.modal-backdrop'), modal = $('#dialog'),
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
			var back = $('.modal-backdrop'), modal = $('#dialog');
			back.css('display', 'none');
			modal.css('display', 'none');
		}
	};

	//获取基本信息输入的JSON字符串，在拼接一些必须提交的数据
	function getBaseInfo () {
		var result = '{';
		result += '"vr_status":"在录入"' + ',"vr_recorder":"' + username + '"' + ',"vr_id":"' + 
			vr_id + '"' + ',"vr_recorddate":"' + (new Date().format('yyyy-MM-dd')) +'","vr_statuscode":"ENTERING",' +
			'"vr_class":"' + vr_class + '"';
		for(var i=0;i < $('#baseinfo .form-control').length; i ++) {
			result += ',"';
			result += $($('#baseinfo .form-control').get(i)).attr('name') + '":"' + 
			$($('#baseinfo .form-control').get(i)).val() + '"';
		}
		result += '}';
		return result;
	}

	//获取拜访人员输入的JSON字符串
	function getVisitPerpeo() {
		var result = '[';
		var visit_perpeo = $('#visit_perpeo .pl');
		for (var i = 0; i < visit_perpeo.length; i++) {
			if ($(visit_perpeo.get(i)).find('[name="pl_name"]').val()!='') {
				result += '{"pl_detno":' +  (i+1) +',"pl_age":"0","pl_id":"0","pl_vrid":"' + vr_id + '",';
				result += '"pl_name":"' + $(visit_perpeo.get(i)).find('[name="pl_name"]').val() +
					'","pl_position":"' + $(visit_perpeo.get(i)).find('[name="pl_position"]').val()
					+ '"}';
				if (i < visit_perpeo.length-1) {result += ','};
			};
		};
		result += ']';
		return result;
	}

	//获取洽谈对象输入的JSON字符串
	function getCustomerPerpeo () {
		var result = '[';
		var customer_perpeo = $('#customer_perpeo .cup');
		for (var i = 0; i < customer_perpeo.length; i++) {
			if ($(customer_perpeo.get(i)).find('[name="cup_name"]').val()!='') {
				result += '{"cup_detno":' +  (i+1) +',"cup_age":"0","cup_id":"0","cup_vrid":"' + vr_id + '",';
				result += '"cup_name":"' + $(customer_perpeo.get(i)).find('[name="cup_name"]').val() +
					'","cup_position":"' + $(customer_perpeo.get(i)).find('[name="cup_position"]').val() + 
					'","cup_tel":"' + $(customer_perpeo.get(i)).find('[name="cup_tel"]').val()
					+ '"}';
				if (i < customer_perpeo.length-1) {result += ','};
			};			
		};
		result += ']';
		return result;
	}

	//获取推广项目信息输入的JSON字符串
	function getProductInfo() {
		var result = '[';
		var productInfo = $('#productInfo .pi');
		for (var i = 0; i < productInfo.length; i++) {
			if ($(productInfo.get(i)).find('[name="pi_prodname"]').val()!='') {
				result += '{"pi_detno":' +  (i+1) +',"pi_id":"0","pi_vrid":"' + vr_id + '",';
				result += '"pi_prodname":"' + $(productInfo.get(i)).find('[name="pi_prodname"]').val() +
					'","pi_brand":"' + $(productInfo.get(i)).find('[name="pi_brand"]').val() + 
					'","pi_model":"' + $(productInfo.get(i)).find('[name="pi_model"]').val() + 
					'","pi_projprogress":"' + $(productInfo.get(i)).find('[name="pi_projprogress"]').val()
					+ '"}';
				if (i < productInfo.length-1) {result += ','};
			};			
		};
		result += ']';
		return result;
	}

	//获取费用报销输入的JSON字符串
	function getFeedBack() {
		var result = '[';
		var feedBack = $('#feedBack .fb');
		for (var i = 0; i < feedBack.length; i++) {
			if ($(feedBack.get(i)).find('[name="vrd_d1"]').val()!='') {
				result += '{"vrd_detno":' +  (i+1) +',"vrd_id":"0","vrd_vrid":"' + vr_id + '",';
				result += '"vrd_d1":"' + $(feedBack.get(i)).find('[name="vrd_d1"]').val() +
					'","vrd_n7":"' + $(feedBack.get(i)).find('[name="vrd_n7"]').val() + 
					'","vrd_d3":"' + $(feedBack.get(i)).find('[name="vrd_d3"]').val() + 
					'"}';
				if (i < feedBack.length-1) {result += ','};
			};			
		};
		result += ']';
		return result;
	};

	//设置单据编号
	function setVrCode() {
		$.ajax({
			type: 'GET',
			url: basePath + 'common/getCodeString.action',
			dataType: 'json',
			data: {caller:'VisitRecord', table:'VisitRecord', type:2, _noc: 1},
			success: function(result){
				$('#vr_code').val(result.code);		
			}
		});	
	};
	setVrCode();

	//为拜访时间输入框加载时间控件
  	$('#datetimepicker').datetimepicker({
		language: 'zh-CN',
		format: "yyyy-MM-dd hh:mm:ss"
		// weekStart: 1,
		// todayBtn: true,
		// autoclose: true,
		// todayHighlight: true,
		// minView: 0
	});

  	//判断用户是否登录
	if (username && username != 'null' && username != '') {
		$('#username').text(username);
	} else {
		dialog.show('错误', '请先登录！&nbsp;<a href="' + basePath + '">登录</a>');
	};

	//输入当前时间
	$('#settime_now').click(function() {
		$('#vr_visittime').val(new Date().format('yyyy-MM-dd hh:mm:ss'));
	});

	//定位
	$('#location').click(function () {
		var map = new BMap.Map("location_map");
		var point = new BMap.Point(116.331398,39.897445);
		map.centerAndZoom(point,12);
		var geolocation = new BMap.Geolocation();
		geolocation.getCurrentPosition(function(r){
		    if(this.getStatus() == BMAP_STATUS_SUCCESS){
		        $('#vr_visitplace').val(r.address.city + r.address.district + r.address.street);
		    }
		    else {
		        alert('failed'+this.getStatus());
		    }
		});
	});

	//添加拜访人员输入行
	$('#add_visit_perpeo').click(function () {
		var newTr = '<tr class="pl">' + 
						'<td width="30%"><div><input name="pl_name" class="form-control" type="text"></div></td>' +
						'<td width="50%"><div><input name="pl_position" class="form-control" type="text"></div></td>' +
						'<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>' +
					'</tr> ';
		$(this).parent().next().find('.table').append(newTr);
		$('.glyphicon-remove').click(function() {
			$(this).parent().parent().remove();
		});
	});

	//添加洽谈对象输入行
	$('#add_customer_perpeo').click(function () {
		var newTr = '<tr class="cup">' +
						'<td width="20%"><div><input name="cup_name" class="form-control" type="text"></div></td>' +
						'<td width="30%"><div><input name="cup_position" class="form-control" type="text"></div></td>' + 
						'<td width="40%"><div><input name="cup_tel" class="form-control" type="number"></div></td>' +
						'<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>' +
					'</tr>';
		$(this).parent().next().find('.table').append(newTr);
		$('.glyphicon-remove').click(function() {
			$(this).parent().parent().remove();
		});
	});

	//添加推广项目信息输入行
	$('#add_productInfo').click(function () {
		var newTr = '<tr class="pi">' + 
						'<td width="30%">' +
							'<div><input name="pi_prodname" class="form-control" type="text" readonly placeholder="点击选择"></div>' + 
						'</td>' + 
						'<td width="30%">' + 
							'<div><input name="pi_brand" class="form-control" type="text" readonly></div>' + 
						'</td>' + 
						'<td width="0%" style="display:none;">' + 
							'<div><input name="pi_model" class="form-control" type="text" readonly></div>' + 
						'</td>' + 
						'<td width="30%">' + 
							'<div>' + 
								'<select name="pi_projprogress" class="form-control" type="number">' + 
									'<option value="初次推广">初次推广</option>' + 
									'<option value="报价">报价</option>' + 
									'<option value="送样">送样</option>' + 
									'<option value="样品验证">样品验证</option>' + 
									'<option value="量产">量产</option>' + 
									'<option value="结案">结案</option>' + 
								'</select>' + 
							'</div>' + 
						'</td>' + 
						'<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>' + 
					'</tr>';
		$(this).parent().next().find('.table').append(newTr);
		$('.glyphicon-remove').click(function() {
			$(this).parent().parent().remove();
		});
		prodnameDbFind();
	});

	//添加费用报销输入行
	$('#add_feedBack').click(function () {
		var newTr = '<tr class="fb">' + 
						'<td width="35%">' + 
							'<div><input name="vrd_d1" class="form-control" type="text" readonly placeholder="点击选择"></div>' + 
						'</td>' + 
						'<td width="20%">' + 
							'<div><input name="vrd_n7" class="form-control" type="number"></div>' + 
						'</td>' + 
						'<td width="35%"><div><input name="vrd_d3" class="form-control" type="text"></div></td>' + 
						'<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>' + 
					'</tr>';
		$(this).parent().next().find('.table').append(newTr);
		$('.glyphicon-remove').click(function() {
			$(this).parent().parent().remove();
		});
	});

	//注册删除某一输入行事件
	$('.glyphicon-remove').click(function() {
		$(this).parent().parent().remove();
	});

	//预设单据编号 ----功能被移除
	// $('#vr_code_preset').click(function() {
	// 	if($(this).attr('checked')) {
	// 		$('#vr_code').attr('disabled', '');
	// 		$('#vr_code').val('');
	// 	}
	// 	else {
	// 		$('#vr_code').removeAttr('disabled');
	// 	}
	// });

	//加载下拉选项
	$.ajax({
		type: 'GET',
		url: basePath + 'common/singleFormItems.action',
		dataType: 'json',
		data: {caller: 'VisitRecord',condition: '',_noc: 1},
		success: function(result){
			var items = result.items;
			$.each(items, function(i, item){
				if(item.name == 'vr_type') {
					$.each(item.store.data, function(j, data){
						$('#vr_type').append('<option value="' + data.value +'">' + data.display +'</option>');
					});
				};
				if(item.name == 'vr_way') {
					$.each(item.store.data, function(j, data){
						$('#vr_way').append('<option value="' + data.value +'">' + data.display +'</option>');
					});
				};
				if (item.name == 'vr_uu') {};
				if (item.name == 'vr_class') {
					vr_class=item.value;
				};
			});
			
		}
	});

	//所属组织设为用户所在部门
	$('#vr_defaultorname').val(em_depart);

	//拜访时间设为当前的时间
	$('#vr_visittime').val(new Date().format('yyyy-MM-dd hh:mm:ss'));

	//客户编号输入提醒
	$('#cuuuDb').bind('click', function() {
    	$.ajax({
    		type: 'GET',
    		url: basePath + 'mobile/crm/getLikeCuCode.action',
    		data: {code: $('#vr_cuuu').val(), size: 6, page: 1},
    		success: function(result) {
    			$('#suggest_cuuu ul').empty();
    			var items = result.result;
    			var html = '';
    			$.each(eval('('+items+')'), function(i, item){
    				html += '<li cuname="' + item.cu_name + '">' + item.cu_code + '</li>'
    			});
    			$('#suggest_cuuu ul').append(html);
    			$('#suggest_cuuu ul li').click(function() {
    				var cu_code = $(this).text();
    				var cu_name = $(this).attr('cuname');
    				$('#vr_cuuu').val(cu_code);
    				$('#vr_cuname').val(cu_name);
    			});
    		}
    	});
	});

	//客户名称输入提醒
	$('#cunameDb').bind('click', function() {
    	$.ajax({
    		type: 'GET',
    		url: basePath + 'mobile/crm/getLikeCuName.action',
    		data: {name: encodeURI($('#vr_cuname').val()), size: 6, page: 1},
    		success: function(result) {
    			$('#suggest_cuname ul').empty();
    			var items = result.result;
    			var html = '';
    			$.each(eval('('+items+')'), function(i, item){
    				html += '<li cucode="' + item.cu_code + '">' + item.cu_name + '</li>'
    			});
    			$('#suggest_cuname ul').append(html);
    			$('#suggest_cuname ul li').click(function() {
    				var cu_code = $(this).attr('cucode');
    				var cu_name = $(this).text();
    				$('#vr_cuuu').val(cu_code);
    				$('#vr_cuname').val(cu_name);
    			});
    		}
    	});
	});

	//推广项目信息 项目名称dbFind效果
	function prodnameDb(input, page, size) {
		var count;
		var html = '<table class="table .table-striped text-center"><tr><th width="30%">项目名称</th>' + 
				'<th width="30%">推广品牌</th><th width="40">推广产品型号</th></tr>';
		$.ajax({
			url: basePath + 'common/dbfind.action',
			type: 'POST',
			data: {which:'grid', caller:'Project!TG',field:'pi_prodname',
				condition:"upper(prj_name) like '%" + $(input).val() + "%' AND prd_emname='" + username + "' ",
				ob:'',page:page,pageSize:size},
			success: function (result) {
				var data = result.data;
				count = eval('('+data+')').length;
				$.each(eval('('+data+')'), function(i, item){
					html += '<tr><td class="pi_prodname" width="30%">' + item.prj_name +'</td>' +
						'<td class="pi_brand" width="30%">' + item.prj_others + '</td>' + 
						'<td class="pi_model" width="40%">' + item.prj_producttype +'</td></tr>';
				});
				html += '</table>';
				$('#dbFindModal .modal-content .modal-body').html(html);
				$('#dbFindModal').modal('show');
				$('#dbFindModal .modal-content .modal-body .table td').click(function () {
					var td = this;
					var tr = $(td).parent();
					$(input).val(tr.find('.pi_prodname').text());
					$(input).parent().parent().parent().find('input[name="pi_brand"]').val(tr.find('.pi_brand').text());
					$(input).parent().parent().parent().find('input[name="pi_model"]').val(tr.find('.pi_model').text());
					$('#dbFindModal').modal('hide');
				});
				var buttonHtml = '';
				//添加【重置条件】
				buttonHtml += '<button type="button" class="btn btn-default" id="resetCondition">重置条件</button>';
				//添加【上一页】
				if(page > 1) {
					buttonHtml += '<button type="button" class="btn btn-default" id="prePage">上一页</button>';
				}
				//添加【下一页】
				if(count == size) {
					buttonHtml += '<button type="button" class="btn btn-default" id="nextPage">下一页</button>';
				}
				buttonHtml += '<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>';
				$('#dbFindModal .modal-footer').html(buttonHtml);
				$('#resetCondition').click(function(){
					$(input).val('');
					$(input).parent().parent().parent().find('input[name="pi_brand"]').val('');
					$(input).parent().parent().parent().find('input[name="pi_model"]').val('');
					prodnameDb(input, page, size);
				});
				$('#prePage').click(function(){
					prodnameDb(input, page-1, size);
				});
				$('#nextPage').click(function(){
					prodnameDb(input, page+1, size);
				});
			}
		});
	};
	var prodnameDbFind = function () {
		$('input[name="pi_prodname"]').click(function(){
			prodnameDb(this, 1, 10);
		});
	};
	prodnameDbFind();

	//费用报销 费用用途dbFind效果
	function feedBackDb(input, page, size) {
		var count;
		var html = '<table class="table .table-striped text-center"><tr><th width="30%">费用用途</th>' + 
				'<th width="30%">部门名称</th><th width="40">项目描述</th></tr>';
		$.ajax({
			url: basePath + 'common/dbfind.action',
			type: 'POST',
			data: {which:'grid', caller:'FeeCategorySet',field:'vrd_d1',
				condition: "upper(fcs_itemname) like '%" + $(input).val() + "%' AND fcs_departmentname='" +
					($('#vr_defaultorname').val()?$('#vr_defaultorname').val():em_depart) + "' ",
				ob:'',page:1,pageSize:10},
			success: function (result) {
				var data = result.data;
				$.each(eval('('+data+')'), function(i, item){
					html += '<tr><td class="vrd_d1" width="30%">' + item.fcs_itemname +'</td>' +
						'<td width="30%">' + item.fcs_departmentname + '</td>' + 
						'<td width="40%">' + item.fcs_itemdescription +'</td></tr>';
				});
				html += '</table>';
				$('#dbFindModal .modal-content .modal-body').html(html);
				$('#dbFindModal').modal('show');
				$('#dbFindModal .modal-content .modal-body .table td').click(function () {
					var td = this;
					var tr = $(td).parent();
					$(input).val(tr.find('.vrd_d1').text());
					$('#dbFindModal').modal('hide');
				});
				var buttonHtml = '';
				//添加【重置条件】
				buttonHtml += '<button type="button" class="btn btn-default" id="resetCondition">重置条件</button>';
				//添加【上一页】
				if(page > 1) {
					buttonHtml += '<button type="button" class="btn btn-default" id="prePage">上一页</button>';
				}
				//添加【下一页】
				if(count == size) {
					buttonHtml += '<button type="button" class="btn btn-default" id="nextPage">下一页</button>';
				}
				buttonHtml += '<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>';
				$('#dbFindModal .modal-footer').html(buttonHtml);
				$('#resetCondition').click(function(){
					$(input).val('');
					feedBackDb(input, page, size);
				});
				$('#prePage').click(function(){
					feedBackDb(input, page-1, size);
				});
				$('#nextPage').click(function(){
					feedBackDb(input, page+1, size);
				});
			}
		});
	};
	var feedBackDbFind = function () {
		$('input[name="vrd_d1"]').click(function(){
			feedBackDb(this, 1, 10);
		});
	};
	feedBackDbFind();

	//保存按钮，执行提交
	$('#btn_save').click(function () {
		var cu_code = $('#vr_cuuu').val();
		if(! cu_code) {// 没有选择客户不可以保存
			dialog.show('提示，客户不能为空', '关闭提示框', 1, function() {
						dialog.hide();
			});
		} else {
			$.ajax({
				url: basePath + 'crm/customermgr/saveVisitRecord.action',
				type: 'POST',
				async: false,
				data: {
					formStore: getBaseInfo(),//基本信息
					param1: getFeedBack(),//费用报销
					param2: getCustomerPerpeo(),//洽谈对象
					param3: getVisitPerpeo(),//拜访人员
					param4: '',
					param5: getProductInfo(),//推广项目信息
					param6: '',
					param7: ''
				},
				success: function (result) {
					if (result.success) {
						dialog.show('保存成功', '正在提交单据...', 2, function() {
							dialog.hide();
							resetAllForm();
							$.ajax({//提交单据，将单据状态修改为已提交
								url: basePath + 'crm/customermgr/submitVisitRecord.action?caller=VisitRecord',
								data: {id: vr_id},
								async: false,
								success: function (result) {
									if(result.success) {
										dialog.show('单据提交成功', '继续...', 2, function() {
											dialog.hide();
										});
									} else {
										dialog.show('单据提交失败', result.exceptionInfo, 2, function() {
											dialog.hide();
										});
									}
									
								}
							});
							vr_id = getSeqId('common/getId.action?seq=VISITRECORD_SEQ');
							setVrCode();
							$('#vr_defaultorname').val(em_depart);
						});
					} else {
						dialog.show('保存失败', '修改数据...', 2, function() {
							dialog.hide();
						});
					};
				}
			});
		}
	});

	//重置按钮
	$('#btn_cancel').click(function(){
		resetAllForm();
	});


});