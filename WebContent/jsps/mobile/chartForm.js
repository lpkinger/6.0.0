/**
 * 构造JSON数据，用于生成表单
 * @param datas
 * @returns {Array}
 */
function createElesJson(datas){
	var ele, eles = [];
	for(var i = 0; i < datas.length; i++){
		var data = datas[i];
		var obj = {
				name : data.FIELD_,
				title: data.TITLE_,
				type: data.TYPE_,
				defaultValue: data.DEFAULTVALUE_
		}
		if (obj.type == 'sql' || obj.type == 'combo' || obj.type == 'editcombo'){
			obj.type = 'select';
		}
		if(obj.type == 'select' || obj.type == 'radio' || obj.type == 'checkbox'){
			obj.withNull = true;
			var value = data.VALUE_,
				itemObj;
			if(value != '' && value != null){
				obj.items = [];
				for(var j = 0; j < value.length; j++){
					if(value[j].indexOf('#')>-1){
						var arr = value[j].split('#');
						if(arr.length == 2){
							itemObj = {text: arr[0], value: arr[1]};
						}
					}else{
						itemObj = {text: value[j], value: value[j]};
					}
					
					obj.items.push(itemObj)
				}
			}else{
				continue;
			}
		}else if(obj.type == 'date' || obj.type == 'time' || obj.type == 'timestamp'){
			obj.type = 'datetime';
			if(data.DEFAULTVALUE_ == 'sysdate'){
				obj.value = dateFormat('yyyy-MM-dd', new Date());
			}
		}else if(obj.type == 'number'){
			obj.type = 'text';
			obj.remarks = 'number';
		}else if(obj.type == 'scanner'){
			obj.type = 'text',
			obj.remarks = 'scanner'
		}
			
		ele = {ele: obj};
		eles.push(ele);
	}
	//添加'是否记住所选项'的checkbox
	obj = {
		name: 'isSave',
		//title: '记住选项',
		type: 'checkbox',
		items: [{text:'记住选项',value:'true'}]
	};
	ele = {ele: obj};
	eles.push(ele)
	return eles;
}

/**
 * 生成表单
 * @param data
 * @param result
 */
function createForm(data,result,configs){
	var buttonCount = 0;
	var bsForm = new BSForm({ eles: data, autoLayout: '3,9' }).Render('form');
	$(".date-picker").next('span').children().css('padding','9px 12px');
	$(".modal-footer").css("padding","5px");
	$(".modal-header").css("padding","5px");
	/* 日期插件 */
	$(".date-picker").attr("readonly","readonly");
	/*$(".date-picker").datetimepicker({
		 language: 'zh-CN',
		 format: 'yyyy-mm-dd',
		 minView: "month",
		 initialDate: new Date(),
		 autoclose: true,
		 todayBtn: 'linked'
	});*/
	/*$(".date-picker").next().children().click(function(){
		$(".date-picker").datetimepicker('show')
	});*/
	
	for(var i = 0; i < data.length; i++){
		/* 设置数字类型的input */
		if(data[i].ele.remarks == 'number'){
			var name = "#" + data[i].ele.name;
			$(name).attr("type","number");
		}
		/* 设置支持扫条码的文本框 */
		if(data[i].ele.remarks == 'scanner'){
			buttonCount++;
//			<img src="img/scan.png" width="16" height="16" />
			//构造HTML片段
			var html = '<div class="input-group">'
					 + '<input type="text" id="'+data[i].ele.name+'" name="'+data[i].ele.name+'" class="form-control">'
					 + '<span class="input-group-btn"><button id="scanner'+(buttonCount)+'" onClick="scanStart('+(buttonCount)+')" class="btn btn-default scanner"></button></span></div>';
			var inputParent = document.getElementById(data[i].ele.name).parentNode;
			inputParent.innerHTML = html;
			
			/* 扫码按钮事件 */
			/*var name = "#" + data[i].ele.name;
			$(name).next().children().bind('click',scanStart);*/
			
		}
		/* 日期插件 */
		if(data[i].ele.type == 'datetime'){
			var fieldName = "#" + data[i].ele.name;
			$(fieldName).datetimepicker({
				 language: 'zh-CN',
				 format: 'yyyy-mm-dd',
				 minView: "month",
				 initialDate: new Date(),
				 autoclose: true,
				 todayBtn: 'linked',
			});
			$(fieldName).next().children().click(function(a){
				$(a.target).parent().parent().prev().datetimepicker('show')
			});
		}
	}
	
	
	/* 可编辑下拉框   */
	//var height = $('#formModelBody')[0].offsetHeight;
	for(var j = 0; j < configs.length; j++){
	 	if(configs[j].TYPE_ == 'editcombo'){
	 		$("#"+configs[j].FIELD_).editableSelect({
				effects: 'slide',
				onSelect: function (element) {
					$('.shift-info').attr('data-val',element.val());
				}
			});
	 		/* 给可编辑下拉框添加focus事件,完成置顶效果 */
	 		/*$("#"+configs[j].FIELD_).focus(function(){
	 			document.getElementById('formModelBody').scrollTop = 700;
	 		});*/
	 	}
	}
	
	//确认按钮事件
	$("#submit").click(function(){
		/*var bool = formValid(data);		//验证表单是否有未填的必填项
		if(bool)*/
			submitForm(data,result);
	});
	
	/* 将后台配置的默认值赋值给相应表单项 */
	for(var m = 0; m < configs.length; m++){
		if(configs[m].DEFAULTVALUE_ != null && configs[m].DEFAULTVALUE_ != ''){
			if(configs[m].TYPE_ != 'date'){
				$("#" + configs[m].FIELD_).val(configs[m].DEFAULTVALUE_);
			}
		}
	}
	
	/* 将历史填入的值设入表单中  */
	var obj = JSON.parse(result);
	if(obj && obj['isSave'] == true){
		for(var key in obj){
			if(key == 'isSave'){
				$("input[type=checkbox]").attr('checked',obj[key]);
			}else if(obj[key].indexOf("'")){
				$("#"+key).val(obj[key].replace("to_date('",'').replace("','yyyy-mm-dd')",""));
			}else{
				$("#"+key).val(obj[key]);
			}
		}
	}
	
	/* 给表单项添加检测事件 */
	for(var x = 0; x < data.length; x++){
		var name = "#" + data[x].ele.name;
		$(name).bind('blur',function(){
			if($(this).val() == '' || $(this).val() == null){
				$(this).css("border","1px solid red");
			}else{
				$(this).css("border","1px solid #ccc");
			}
		});
	}
	
	/* 根据后台配置,添加相应的下拉框change事件,以支持联动下拉框 */
	$.ajax({
		url: basePath + 'mobile/getRefConfig.action',
		type: 'POST',
		data: {
			numId: numId
		},
		success: function(data){
			var arr = data.data;
			for(var i = 0; i < arr.length; i++){
				$("#" + arr[i]).bind('change',function(){
					var value = this.value,
						name = this.name;
					if(value && !value.trim() == ''){
						//修改相应的关联下拉项的值
						$.ajax({
							url: basePath + 'mobile/getComboData.action',
							type: 'POST',
							async: false,
							data: {
								numId: numId,
								fieldName: name,
								value: value
							},
							success: function(response){
								var datas = response.data;
								for(var j = 0; j < datas.length; j++){
									if(datas[j].type == 'editcombo'){
										var html = "";
										//可输入下拉框
										$("#"+ datas[j].field).val('');
										$("#"+ datas[j].field).next().children().remove();	//清空下拉项的值
										//重新添加下拉项
										var optionArr = datas[j].value;
										for(var k = 0; k < optionArr.length; k++){
											html += "<li value='"+optionArr[k]+"' class='es-visible'>"+optionArr[k]+"</li>";
										}
										$("#"+datas[j].field).next().append(html);
									}else{
										var html = "";
										//不可编辑下拉框
										$("#"+datas[j].field).empty();
										var optionArr = datas[j].value;
										for(var k = 0; k < optionArr.length; k++){
											html = "<option value='"+optionArr[k]+"'>" + optionArr[k] + "</option>";
											$("#"+datas[j].field).append(html);
										}
										$("#"+datas[j].field).val('');
									}
								}
							}
						});
					}
				});
			}
		}
	});
	
}


function scanStart(index){
	if (/(Android)/i.test(navigator.userAgent)) {
		window.JSWebView.openScan(index);
	}
}

function scanCompleted(arg){
	var data = arg.split(',');
	$("#scanner"+data[0]).parent().prev('input').val(data[1]);
}

/**
 * 获取表单提交的值
 * @param datas
 * @returns 
 */
function getFormValue(datas){
	var data = {};
	for(var i = 0; i < datas.length; i++){
		var type = datas[i].ele.type, name = datas[i].ele.name;
		if(type == 'text' || type == 'datetime' || type == 'date'){
			var value = $("#form input" + '[name=' + name + ']').val();
			if(type == 'datetime' || type == 'date' || type == 'time'){
				data[name] = "to_date('" + value + "','yyyy-mm-dd')";
			}else{
				data[name] = value;
			}
		}else{
			if(type == 'checkbox'){
				data[name] = $("input[type=checkbox]").prop('checked');
			}else if(type == 'select'){
				data[name] = $("#"+name).val();
			}else{
				var value = $("#form " + type + '[name=' + name + ']').val();
				data[name] = value;
			}
		}
	}
	return data;
}

/**
 * 验证表单
 * @param data
 * @returns {Boolean}
 */
function formValid(data){
	var flag = true;
	for(var i = 0; i < data.length; i++){
		var name = "#" + data[i].ele.name;
		if(name == '#isSave'){
			
		}else if($(name).val().trim() == '' || $(name).val().trim() == null || $(name).val().trim() == '--请选择--'){
			$(name).focus();
			$(name).css("border","1px solid red")
			return !flag;
		}
	}
	return flag;
}


/**
 * 表单提交
 * @param datas
 */
function submitForm(datas,result){
	//获取要提交的信息
	var value = getFormValue(datas);
	//提交
	$.ajax({
		url: basePath + "mobile/updateSubsConditionsInstanceByFormSubmit.action",
		type: 'POST',
		async: false,
		data: {
			numId: numId,
			data: JSON.stringify(value)
		},
		success: function(data){
			//关闭模态框
			$('#myModal').modal('hide');
			$("#cancel").attr('disabled',false);
			$("button[class=close]").attr("data-dismiss","modal");
			//重新生成图表数据
			insId = data.insId;
			mainId = data.mainId;
			getSubsDatas(numId,mainId,insId);
		
			//修改顶部标签页的值
			setTagValue(datas);
		},
		error: function(data){
			$('#myModal').modal('hide');
		}
		
	});

}

/**
 * 日期格式化
 * @param fmt	表达式 'yyyy-MM-dd'
 * @param date	日期 new Date()
 * @returns
 */
function dateFormat(fmt,date)   
{ //author: meizz   
  var o = {   
    "M+" : date.getMonth()+1,                 //月份   
    "d+" : date.getDate(),                    //日   
    "h+" : date.getHours(),                   //小时   
    "m+" : date.getMinutes(),                 //分   
    "s+" : date.getSeconds(),                 //秒   
    "q+" : Math.floor((date.getMonth()+3)/3), //季度   
    "S"  : date.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
} 

/**
 * 获取要展示在标签页上的值（因为存在下拉框显示值与实际值），这里在标签页上显示“显示值”
 * @param datas
 */
function setTagValue(datas){
	var data = {};
	for(var i = 0; i < datas.length; i++){
		var type = datas[i].ele.type, name = datas[i].ele.name;
		if(type == 'text' || type == 'datetime' || type == 'date'){
			var value = $("#form input" + '[name=' + name + ']').val();
			if(type == 'datetime' || type == 'date' || type == 'time'){
				data[name] = "to_date('" + value + "','yyyy-mm-dd')";
			}else{
				data[name] = value;
			}
		}else{
			if(type == 'select'){
				var value = $("#"+name).val();
				data[name] = value;
			}else if(type == 'checkbox'){
				data[name] = $("input[type=checkbox]").prop('checked');
			}else{
				var value = $("#form " + type + '[name=' + name + ']').val();
				data[name] = value;
			}
			
		}
	}
	var values = Object.values(data);
	var html = "";
	for(var i = 0; i < values.length; i++){
		if(typeof(values[i]) != 'boolean'){
			var index = values[i].indexOf("to_date");
			if(index != -1){
				values[i] = values[i].replace("to_date('","").replace("','yyyy-mm-dd')","")
			}
			html += "<span>" + values[i] + "</span>";
		}
	}
	$("#showLabel").html(html);
	$("#showLabel span").addClass("span");
	$("#showLabel").addClass("filterDiv");
	var s = Number($("#showLabel").css("height").replace("px","")) + 1 + "px";
	document.getElementById("panel").style.paddingTop = s;
}

//页面加载完成，向后台发起Ajax请求
$(function(){
	$("#showButton").hide();
	$.ajax({
		url: basePath + "mobile/getSubsConditionsConfig.action",
		type: 'POST',
		data: {
			numId : numId
		},
		success:function(result){
			var configs = result.configs,
				resultData = result.data,
				relation = result.relation;
			if(configs != null && configs != ''){
				var data = createElesJson(configs);
				if(data!=''&&data!=null){
					//生成表单
					createForm(data,resultData,configs);
					$('input[type=checkbox]').parent().parent().prev().remove();
					//判断表单是否隐藏
					if(!result.data){
						//如果第一次进入订阅号，弹出模态框、禁用取消按钮和右上角关闭按钮
						$("#myModal").modal({backdrop: 'static'});
						$("#cancel").attr('disabled',true);
						$("button[class=close]").removeAttr("data-dismiss");
					}else{
						//将值设置到顶部作为标签展示
						var datas = JSON.parse(result.data);
						var values = Object.values(datas);
						var html = "";
						for(var i = 0; i < values.length; i++){
							if(typeof(values[i]) != 'boolean'){
								var index = values[i].indexOf("to_date");
								if(index != -1){
									values[i] = values[i].substring(index+9,index+9+10);
									var nowDate = dateFormat('yyyy-MM-dd',new Date());
									
								}
								html += "<span>" + values[i] + "</span>";
							}
						}
						$("#showLabel").append(html);
						$("#showLabel span").addClass("span");
						$("#showLabel").addClass("filterDiv");
						var s = Number($("#showLabel").css("height").replace("px","")) + 1 + "px";
						document.getElementById("panel").style.paddingTop = s;
						/* 启用配置的默认时间  */
						//判断是否设置了时间字段且配置了默认值sysdate且是今天第一次进入该订阅号
						var bool = false;
						var obj = JSON.parse(resultData);
						for(var i = 0; i < data.length; i++){
							if(data[i].ele.type == 'datetime'){
								//获取数据库中存的日期
								if(obj[data[i].ele.name].indexOf("'")){
									var time = obj[data[i].ele.name].replace("to_date('",'').replace("','yyyy-mm-dd')","");
								}
								//如果数据库中日期的和当前日期不一致
								if(data[i].ele.value!=null && data[i].ele.value != time){
									//设置表单的对应值
									$('#' + data[i].ele.name).val(data[i].ele.value);
									var bool = true;
								}
							}
						}
						//提交表单
						if(bool){
							submitForm(data);
						}
						
					}
					//设置按钮功能
					$("#showButton").show();
					$("#showButton").click(function(){
						$('#myModal').modal('show')
					});
					$("#myModal").modal({backdrop: 'static'});
					$("#cancel").attr('disabled',true);
					$("#close1").removeAttr("data-dismiss");
				}
			}
		}
	});

});