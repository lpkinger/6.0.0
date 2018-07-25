function getUrlParam(name){
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");   
    var r = window.location.search.substr(1).match(reg);   
    if (r != null)
    	return decodeURI(r[2]); 
    return null; 
}

function setLoading (isLoading) {
	$('.loading-container').css('display', isLoading ? 'block' : 'none');
}


/**
 * 获取订阅项目明细-列表
 */
function getSubsFormulaDet(lists){
	 for (var j = 0; j < lists.length; j++) {
		 var list=lists[j];
		 showSubsFormulaDet(list,j)
     }  
}

/**
 * 展示订阅项目明细-列表
 */
function showSubsFormulaDet(list,i){			
	$.ajax({
	url:basePath + 'common/charts/getSubsFormulaDet.action',
	type: 'POST',
	data: {formulaId: list.formulaId,insId: list.insId},
	success: function(result){if(result.formulaDets && result.formulaDets.length > 0) {
		var colModel = [];	
		//var sumCol = [];	
		var filStr='',arr = new Array();
		
		for(var n=0;n<result.formulaDets.length;n++){	
			var formatter;
			filStr+='(!filter.'+result.formulaDets[n].field_+' || data.'+result.formulaDets[n].field_+'.indexOf(filter.'+result.formulaDets[n].field_+') > -1)&&';
			if(result.formulaDets[n].format_ && result.formulaDets[n].format_.trim() != ''){
				var object = new Object();
				object.field = result.formulaDets[n].field_;
				object.formulaNum = result.formulaDets[n].format_.split(':')[1];
				arr.push(object);
			}
			colModel.push({"title":result.formulaDets[n].description_,"name":result.formulaDets[n].field_,"type":result.formulaDets[n].type_,"width":result.formulaDets[n].width_});
		    //if(result.formulaDets[n].sum_==-1 && result.formulaDets[n].type_=="number")
			//{sumCol.push({"title":result.formulaDets[n].description_,"name":result.formulaDets[n].field_});}
		}		
		/*$.each(result.datas,function(i){
			$.each(sumCol,function(j){
			});
		});*/	
		
		$('#'+list.container).html('<div id="externalPager_'+[i]+'" class="external-pager"></div><div id=grid_'+i+'></div>');			
		 $('#grid_'+i).jsGrid({
				height:'auto',
				width:$('#'+list.container).width()-15,
                filtering: true,
                sorting: true,
                paging: true,
                autoload: true,				               
                pageSize: 20,
                pageButtonCount: 3,
                pagerContainer: "#externalPager_"+[i],
                pagerFormat: "{first} {pages} {last} 共 {pageCount} 页   共 {itemCount} 条",
                pageFirstText: "|<",
                pageLastText: ">|",
                pageNavigatorNextText: "&#8230;",
                pageNavigatorPrevText: "&#8230;",
                noDataContent : "暂无数据！",
                fields:colModel,
                controller: {
                    loadData: function(filter) {
                    	var grid = $(this); 
                    	//给指定字段添加链接
                    	var fieldArray = new Array();
                    	for(var k = 0; k < colModel.length; k++){
                    		fieldArray.push(colModel[k].name);
                    	}
                    	var fieldString = fieldArray.join('#'); 
                    	for(var i = 0; i < arr.length; i++){
                    		for(var j = 0; j < result.datas.length; j++){
                    			result.datas[j][arr[i].field] = '<a style="color:#428bca;text-decoration:underline" onClick="linkedClick(this,\''+fieldString+'\',\''+arr[i].field+'\',\''+arr[i].formulaNum+'\')">' + result.datas[j][arr[i].field] + '</a>';
                    		}
                    	}
	                	grid.datas = result.datas;
                        return $.grep(grid.datas, function(data) {
                        	  return eval(filStr.substring (0,filStr.length-2));
                        });
                    }
                }
            });
	
	
	}
},
	error: function(xhr,a,b,c){
		if(xhr.responseText) {
			var response =$.evalJSON(xhr.responseText);
			if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
				dialog.show('获取信息失败', '请先登录！');
			} else {
				dialog.show('错误', response.exceptionInfo,-1);
			}
		}
	}
});	}

/**
 * 点击grid链接执行操作
 * @param e		点击的DOM元素
 * @param names		订阅号参数设置中的字段
 * @param field		点击处的字段名
 * @param formulaNum	订阅项编号
 */
function linkedClick(e,names,field,formulaNum){
	
	//构造要发往后台的数据
	var arr = names.split('#'), obj = {}; 
	obj.rowData={};
	obj.formData = {};
	var datas = $(e.parentNode.parentNode).children();
	//点击所在行的行数据
	for(var i = 0; i < datas.length; i++){
		obj.rowData[arr[i]] = $(datas[i]).text();
	}
	//'筛选表单' 填写的值
	$.ajax({
		url: basePath + "mobile/getSubsConditionsConfig.action",
		type: 'POST',
		async: false,
		data: {
			numId : numId
		},
		success: function(result){
			var configs = result.configs,
				data = JSON.parse(result.data);
			for(var key in data){
				if(key != 'isSave'){
					var index = data[key].indexOf('to_date');
					if(index != -1){
						var value = data[key].substring(index+9,index+19);
						obj.formData[key] = value;
					}else{
						obj.formData[key] = data[key];
					}
				}
			}
		}
	});
	//获取点击链接后的数据
	$.ajax({
		url: basePath + "mobile/getGridLinkedDate.action",
		type: 'POST',
		async: false,
		data: {
			data: JSON.stringify(obj),
			formulaNum: formulaNum,
			field: field
		},
		success: function(result){
			var colModel = [];	
			var filStr='',arr = new Array();
			for(var n=0;n<result.config.length;n++){	
				var formatter;
				filStr+='(!filter.'+result.config[n].field_+' || data.'+result.config[n].field_+'.indexOf(filter.'+result.config[n].field_+') > -1)&&';
				colModel.push({"title":result.config[n].description_,"name":result.config[n].field_.toUpperCase(),"type":result.config[n].type_,"width":result.config[n].width_});
			}	
			//弹出模态框
			$("#gridModel").modal('show');
			//设置模态框内容
			var width = $('body').width();
			$("#gridModelLabel").text(result.gridTitle);	//标题
			/*$("#gridModel").on('show.bs.modal',function(e){		//body
				setTimeout(function(){*/
					$('#grid').jsGrid({
						height:'auto',
						width: width-8,
		                filtering: true,
		                sorting: true,
		                paging: true,
		                autoload: true,				               
		                pageSize: 10,
		                pageButtonCount: 3,
		                pagerContainer: "#externalGridPager_",
		                pagerFormat: "{first} {pages} {last} 共 {pageCount} 页   共 {itemCount} 条",
		                pageFirstText: "|<",
		                pageLastText: ">|",
		                pageNavigatorNextText: "&#8230;",
		                pageNavigatorPrevText: "&#8230;",
		                noDataContent : "暂无数据！",
		                fields:colModel,
		                controller: {
		                    loadData: function(filter) {
		                    	var grid = $(this); 
			                	grid.datas = result.data;
		                        return $.grep(grid.datas, function(data) {
		                        	  return eval(filStr.substring (0,filStr.length-2));
		                        });
		                    }
		                }
		            });
				$('.jsgrid-table').css('width','');
				/*},200);
			});*/
		}
	});
}

/**
 * 获取ids获取用于生成图表的订阅数据
 */
function getSubsDatas(numId,mainId,insId){
	setLoading(true);
	$.ajax({
		url:basePath + 'common/charts/getSubsDatas.action',
		type: 'POST',
		data: {
			numId: numId,
			mainId:mainId,
			insId:insId,
			emId:emId
		},
		success: function(result){
			setLoading(false);	
			if(result.subsDatas) {
				parseDatas(result.subsDatas);
			}
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
}


/**
 * 根据每一条订阅项目数据动态生成图表配置
 */
function parseDatas(subsDatas){
	var sumhtml='<div class="sum_info">';
	var listhtml='';
	var html='';
	var nodata=true;
	var charts =new Array();
	var lists =new Array();
	if(subsDatas.length>0){
	$.each(subsDatas, function(i, s){
		if(s.type!='sum' && s.type!='list' && s.data!='[]'){
			nodata=false;
			if(s.type == 'column'){
				var data = $.evalJSON(s.data);
			}else{
				var data = $.evalJSON(s.data.replace(/"yField":"/g, '"yField":').replace(/"}/g, '}').replace(/"yField":\./g, '"yField":0.').replace(/"yField":-\./g, '"yField":-0.'));
			}
			switch (s.type)
			{			
			case 'pie':	
				if(isMobile && isMobile==1) {
					html+='<div id=chart_'+i+' class="mobilechartpie">';
					var opt = HighChart.ChartOptionTemplates.MobilePie(data,s.unit, s.title);	
					}	
				else {
					html+='<div id=chart_'+i+' class="chartpie">';
					var opt = HighChart.ChartOptionTemplates.Pie(data,s.unit, s.title);
					}							
			  	break;
			case 'column':
				html+='<div id=chart_'+i+' class="chart">';
				var opt = HighChart.ChartOptionTemplates.Column(data,s.unit, s.title,s.valueDisp,'column');
				  break;
			case 'bar':
				html+='<div id=chart_'+i+' class="chart">';
				var opt = HighChart.ChartOptionTemplates.Column(data,s.unit, s.title,s.valueDisp,'bar');         
				  break;
			case 'line':
				html+='<div id=chart_'+i+' class="chart">';
				var opt = HighChart.ChartOptionTemplates.Line(data,s.unit,s.title,s.valueDisp);
				  break;				
			}
			html+='</div>';	
			charts.push({ opt:opt, container:'chart_'+i});
		}else if(s.type=='sum'){
			nodata=false;
			sumhtml+='<span class="sum_title">' +s.title+'</span><span class="sum_detail">'+s.data+'</span></br>';
		}else if(s.type=='list'){
			nodata=false;
			var unit=s.unit==null?'':'('+s.unit+')';
			listhtml+='<div class="list_div" id=list_div_'+i+'><span class="list_title">'+s.title+unit+'</span><div id=list_'+i+' class="list"></div></div>';
			lists.push({formulaId:s.formulaId,insId:insId,container:'list_'+i});
		}			
		});	
	if (sumhtml!='<div class="sum_info">') 
		$('#sum').html(sumhtml+'</div>');
	if (listhtml!='') 
		$('#list').html(listhtml);
	if (html!='') 
		$('#container').html(html);
	HighChart.RenderChart(charts);	
	getSubsFormulaDet(lists);
	}
	if(nodata) $('#sum').html('<span>暂无数据</span>');
};

$(document).ready(function() {
	 dialog = {
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
	$("#clearbutton").click(function(){
      	dialog.hide();
    });
	 $(document).attr("title",title);		 
	if (numId && mainId && insId) {
		getSubsDatas(numId,mainId,insId);
	}
});