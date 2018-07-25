function reActive(){
	//激活tabpanel
	var cls = 'active';
	$('.nav-tabs>li>a').click(function(){
		if(!$(this).hasClass(cls)) {
			var nav = $(this).parent().parent(), bd = nav.next(),
					old = nav.children('.' + cls).index(),
					index = $(this).parent().index();
			$('.tab-pane:eq(' + old + ')', bd).removeClass(cls);
			$('.tab-pane:eq(' + index + ')', bd).addClass(cls);
			var width = $('#topToolbar').width()-$('.nav-tabs>li:eq('+index+')').attr("oldwidth")-2;
			var lis = $('.nav-tabs>li');
			for(var j=0; j<lis.length;j++){		
				if(lis[j].id != $('.nav-tabs>li:eq('+index+')')[0].id){			 
				  lis[j].style.width = width/(lis.length-1)+'px';			 
				}
			}
			$('.nav-tabs>li:eq('+index+')')[0].style.width = $('.nav-tabs>li:eq('+index+')').attr("oldwidth")+'px';
			nav.children('.' + cls).removeClass(cls);
			$(this).parent().addClass(cls);
		}
	});
}
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

function setLoading (isLoading) {
		$('.loading-container').css('display', isLoading ? 'block' : 'none');
}
function ValidateNumber(e, pnumber) {
	if (!/^\d+[.]?\d*$/.test(pnumber)) {
		$(e).val(/^\d+[.]?\d*/.exec($(e).val()));
	}
	return false;
} 
function changeEscapeCharacter(da){
	var e = da;
	if(isNaN(e)){//不是数字
	   e = e.replace(/\'/g, "&apos;")
	        .replace(/\"/g, "&quot;")
	        .replace(/\>/g, "&gt;")
	        .replace(/\</g, "&lt;");	        
	}		
	return e;
}
var ignores = [],items = [],dbfinds = {},pageCaller = '',button = {},doo='';
var canexecute = getUrlParam('canexecute')?true:false; //判断是否修改必填项
//更新明细行，并且重新加载数据
function updateRow (gridId,rowId,caller,instance,keyName,keyValue,mad_code){
	if(doo==1){
	$('#jb_buttonname').hide();
	}
	 $('#'+gridId).jqGrid('setSelection',rowId);
	 $('#'+gridId).jqGrid('editGridRow',rowId,{
       	     editCaption:'编辑',bSubmit:'保存',bCancel:'关闭',checkOnSubmit:true,checkOnUpdate:true,
       	     closeAfterEdit:true,closeOnEscape:true,      	
       	     serializeEditData :function(a,b){//序列化传至服务器的参数      	   
       	     	delete a.oper;
       	     	delete a.id;       	     	   	     	
       	     	if(items.length !=0 && dbfinds.length != 0){//      	     		
					$.each(dbfinds,function(key,value){
						if(typeof(items[value.dbGridField])!= "undefined" )
						   a[value.field]= items[value.dbGridField];					
					});
       	     	}      	     
       	     	//判断是否存在忽略类型 ignore
       	     	if(ignores.length > 0 ){
       	     		$.each(ignores,function(key,value){
       	     			delete a[value];
       	     		});
       	     	}
       	     	var b = new Object();
       	     	b._noc = 1;
       	     	b.caller = caller;
       	     	b.processInstanceId = instance ;   
       	     	b.formStore = '{'+keyName+':'+keyValue+'}';
       	     	b.param = $.toJSON(a);
       	     	return b;
       	     },
       	     errorTextFormat:function(a,b){//錯誤捕獲
       	     	if(a.responseJSON.exceptionInfo== 'ERR_NETWORK_SESSIONOUT') {
				    return '保存信息失败，请先登录！';
				} else {
					return '错误：'+ a.responseJSON.exceptionInfo;
				}           
       	     },
       	     afterSubmit : function(response, postdata) {
       	        $.showtip("修改明细成功!", 2000);
       	        setLoading(true);
       	        $.ajax({
					url:basePath + 'common/loadNewGridStore.action',
					type: 'POST',
					data: {caller:caller,condition:mad_code +"='"+keyValue+"'",_noc: 1},
					success: function(result){	
					   setLoading(false);
					   $('#'+gridId)[0].addJSONData(result.data);
					},
					error:function(){
						setLoading(false);
					}
       	        });              	                    	        
       	        return true;
       	       }
       	   });      	  
}
var requiredFields = null,instance = null,forknode=0;
function saveForm(keyName,keyValue,caller){
	//requiredFields array格式	
	var d = {};
	$("#mainForm select[readonly]").removeProp("disabled");
    var t = $('#mainForm').serializeArray();
    $("#mainForm select[readonly]").prop("disabled", true);
    $.each(t, function() { 
      if($('#'+this.name).attr("logic") || $('#'+this.name).attr("logic") != 'ignore'){ //判断logic的属性是否为ignore 忽略
      	d[this.name] = this.value;
      }
    });	
	var bool = true;
	if (requiredFields != null) {//判断必填项是否填写
		$.each(requiredFields, function(key, field){
			if (d[field] == null || d[field] == "") {
				bool = false;
				$('#tips').text('保存之前请先填写必填的信息!');
				$('#'+field).focus();
				return;
			}
		});		
	}
	//获取改变了的值
	$.each(d,function(key,field){//只将修改的值提交到后台
		if(d[key] == bill[key])					
		   delete d[key];	
	});
	if($.isEmptyObject(d)){
		$.showtip("还未添加或修改数据!", 2000);
		return;
	}
	if (bool) {//获取编辑的字段值
		d[keyName] = keyValue;		
		var b = new Object();
		b._noc = 1;
		b.caller = caller;
		b.processInstanceId = instance;    		
		b.formStore = $.toJSON(d);
		setLoading(true);
		$.ajax({
			url:basePath+"common/processUpdate.action",
			type: 'POST',
			data: b,
			success: function(result){	
			   setLoading(false);
			   canexecute = true;
			   $.showtip("修改成功!", 2000);
			   var url = window.location.href;
			   window.location.href = url.substr(0, url.indexOf('?') + 1) + 'nodeId=' + getUrlParam('nodeId') + '&canexecute=1';
			},
			error:function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response = $.evalJSON(xhr.responseText);
					if(response.exceptionInfo) {
						$.showtip(response.exceptionInfo, 2000);						
					}
				}				
			}
		});       
	}
}
function loadNewGridStore (gridId,condition){
		setLoading(true);
        $.ajax({
			url:basePath + 'common/loadNewGridStore.action',
			type: 'POST',
			data: {caller:pageCaller,condition:condition,_noc: 1},
			success: function(result){	
			   setLoading(false);
			   $('#'+gridId).trigger("reloadGrid");
			   $('#'+gridId)[0].addJSONData(result.data);
			},
			error:function(){
				setLoading(false);
			}
        });   
	}	
function orient() {
	var orientation = (window.innerWidth > window.innerHeight) ?'portrait':'landscape';
     document.body.parentNode.setAttribute('class',orientation);
	if (window.orientation == 90 || window.orientation == -90) {
	   //ipad、iphone竖屏；Andriod横屏
		$("body").attr("class", "landscape");
		orientation = 'landscape';
		return false;
	}else if (window.orientation == 0 || window.orientation == 180) {
		//ipad、iphone横屏；Andriod竖屏
		$("body").attr("class", "portrait");
		orientation = 'portrait';
		return false;
	}
}
function autoTextarea (elem, extra, maxHeight) {
        extra = extra || 0;
        var isFirefox = !!document.getBoxObjectFor || 'mozInnerScreenX' in window,
        isOpera = !!window.opera && !!window.opera.toString().indexOf('Opera'),
                addEvent = function (type, callback) {
                        elem.addEventListener ?
                                elem.addEventListener(type, callback, false) :
                                elem.attachEvent('on' + type, callback);
                },
                getStyle = elem.currentStyle ? function (name) {
                        var val = elem.currentStyle[name];
                        if (name === 'height' && val.search(/px/i) !== 1) {
                                var rect = elem.getBoundingClientRect();
                                return rect.bottom - rect.top -
                                        parseFloat(getStyle('paddingTop')) -
                                        parseFloat(getStyle('paddingBottom')) + 'px';        
                        };

                        return val;
                } : function (name) {
                                return getComputedStyle(elem, null)[name];
                },
                minHeight = parseFloat(getStyle('height'));

        elem.style.resize = 'none';

        var change = function () {
                var scrollTop, height,
                        padding = 0,
                        style = elem.style;

                if (elem._length === elem.value.length) return;
                elem._length = elem.value.length;

                if (!isFirefox && !isOpera) {
                        padding = parseInt(getStyle('paddingTop')) + parseInt(getStyle('paddingBottom'));
                };
                scrollTop = document.body.scrollTop || document.documentElement.scrollTop;

                elem.style.height = minHeight + 'px';
                if (elem.scrollHeight > minHeight) {
                        if (maxHeight && elem.scrollHeight > maxHeight) {
                                height = maxHeight - padding;
                                style.overflowY = 'auto';
                        } else {
                                height = elem.scrollHeight - padding;
                                style.overflowY = 'hidden';
                        };
                        style.height = height + extra + 'px';
                        scrollTop += parseInt(style.height) - elem.currHeight;
                        document.body.scrollTop = scrollTop;
                        document.documentElement.scrollTop = scrollTop;
                        elem.currHeight = parseInt(style.height);
                };
        };
        addEvent('propertychange', change);
        addEvent('input', change);
        addEvent('focus', change);
        change();
};
$(document).ready(function() {
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
	var readyTime = new Date();
	//激活tabpanel
	reActive();
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
	$("#clearbutton").click(function(){
      	dialog.hide();
    });
	var parseBillDetail = function(content,i,detail,field,keyValue,keyName){//field 可编辑的字段，keyValue关联主表字段值		
		var caller = detail[i].mad_caller;
		var mad_code = detail[i].mad_code;
		var detno_code = detail[i].mad_detnocode;
		var condition ,ok = i;
		condition = mad_code +"='"+keyValue+"'";
		setLoading(true);
		//获取表的列名，列值
		$.ajax({
			url:basePath + 'common/singleGridPanel.action',
			type: 'POST',
			data: {caller:caller,condition:condition,_noc: 1},
			success: function(result){
				var i = ok;
				setLoading(false);
				var colModel = [];
				if(field.length != 0 && doo !=1){
					//增加一列编辑列
					colModel.push({"label":'编辑',"name":'act',"index":'act',"sorttype":false,"width":75,"editable":false,"frozen":true,"align":'center'});
				}
				dbfinds = result.dbfinds;
				var limits = result.limits,limitarray=new Array();
				if(limits && limits.length>0){
					for(var i=0;i<limits.length;i++){
						limitarray.push(limits[i].lf_field);
					}
				}
				for(var n=0;n<result.columns.length;n++){
					var hidden = false;
					if(result.columns[n].hidden || result.columns[n].width == 0){
						hidden = true;
					}
					if($.inArray(result.fields[n].name, limitarray) > -1){
						hidden = true;
					}
					//是否为忽略
					if(result.columns[n].logic == 'ignore'){//将忽略的保存起来
						ignores.push(result.fields[n].name);
					}
					var editable = false;//是否可编辑列，//编辑类型
					var edittype = '', number = false,required = false;
					if($.inArray(result.fields[n].name.toLowerCase(), field) > -1){
						required = editable = true;
					}
					if(result.columns[n].xtype == 'numbercolumn'){ //数字类型
					    edittype = 'text';
					    number = true;
					}else if(result.columns[n].xtype == '' && result.columns[n].editor && result.columns[n].editor.xtype == 'dbfindtrigger'){//dbfind
						 edittype = 'custom';
						 var resultDatas = {};
						 var name = result.fields[n].name;
						 if(result.columns[n].dbfind){
							 var ars = result.columns[n].dbfind.split('|');								
							  editoptions = {
							    dataInit: function (element) {
								   $(element).autocomplete({	//dbfind字段模糊查询		
								   	  minLength:1,
	 						          source: function(query, proxy) {
											$.ajax({
												url: basePath+'common/dbfind.action',
												dataType: "json",
												data: {
												    which:'grid',
													caller:ars[0],
													field:name,
													condition:"upper("+ars[1]+") like '%"+query.term.toUpperCase()+"%'",
													ob:'',
													page:1,
													pageSize:13
												},
												success: function (result) {    
									                resultDatas = $.evalJSON(result.data) ;    
									                proxy($.evalJSON(result.data)); 														
									            }							            
											});										
										},
										select: function(event, ui) {		
											items = ui.item;
											$.each(dbfinds,function(key,value){
												if($('input#'+value.field).length != 0 && typeof(ui.item[value.dbGridField])!= "undefined"){
				                         	    	$('input#'+value.field).val(ui.item[value.dbGridField]);  
												}			                         		
											});
											return false;
							            }							           
								   }).blur(function(event){
								    	if(resultDatas.length != 0){
								         	  var id = $(this)[0].id,val= $(this).val(),field;
								         	  $.each(dbfinds,function(key,value){
										          if(value.field == id){
										  	         field = value.dbGridField ;
										         }
									          });
									          $.each(resultDatas,function(key,value){	
									             if(value[field] == val){
									             	items = value;
									         		$.each(dbfinds,function(key,da){
														if($('input#'+da.field).length != 0 && typeof(value[da.dbGridField])!= "undefined"){
									             	    	$('input#'+da.field).val(value[da.dbGridField]);  
														}			                         		
												     });
									         	 }
									         });
								    	}else{
								    		items = [];
								    	}
								   }).autocomplete("instance" )._renderItem = function( ul, item ) {
								      return $( "<li>" )
								        .append( "<a>" + item[ars[1]] + "</a>" )
								        .appendTo( ul );
								   };
	                            }
						    }	
					 }
					}else if(result.columns[n].xtype == ''){//基本类型
						edittype = 'text';
					}else if(result.columns[n].xtype == 'combocolumn' || result.columns[n].xtype == 'yncolumn'){//下拉框类型
						edittype='select';
					}
					if((caller == 'StandbyOut' &&  result.fields[n].name == 'sod_sourceid' ) || (caller == 'MaterielOut' &&  result.fields[n].name == 'amod_amadid')){
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":true,editoptions: {readonly: 'readonly',edithidden:true},hidden:hidden});
					}else if(caller == 'Inquiry' && result.fields[n].name == 'id_isagreed'){			
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":editable,"edittype":"select",formatter:'select',editoptions:{value:"0:否; 1:未选择; -1:是"},hidden:hidden});
					}else if(result.fields[n].name == detno_code){
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":true,editoptions: {readonly: 'readonly'},"align":'center'});
					}else if(result.columns[n].logic == 'keyField'){//主鍵字段
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":true,editoptions: {readonly: 'readonly',edithidden:true},key:true,hidden:hidden});
					}else if(edittype == 'select'){//下拉框
						var values = '';
						if(result.columns[n].xtype == 'yncolumn'){
							values = '0:否;-1:是;1:是';
						}else{
							//下拉框可能没值
							if(result.columns[n].editor){
								if(result.columns[n].editor.store){
									var data = result.columns[n].editor.store.data;					
								    $.each(data,function(d,va){
								    	values += va.value+":"+va.display+";";
								    });
								}
							}
						}
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":editable,"edittype":edittype,formatter:'select',"editoptions":{value:values},hidden:hidden});						
					}else if(editable && result.fields[n].type == 'date'){//日期类型
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":editable,"edittype":edittype,hidden:hidden,
							"editoptions":{dataInit: function (element) {
	                                $(element).datepicker({
	                                    id: 'orderDate_datePicker',
	                                    dateFormat: 'yy-mm-dd',
	                                    showOn: 'focus'
	                                });
	                            }}
						});				
					}else if(edittype == 'custom'){//dbfind
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":editable,"edittype":'text',"editoptions": editoptions ,hidden:hidden});						
					}else{
						colModel.push({"label":result.columns[n].header,"name":result.fields[n].name,"index":result.fields[n].name,"sorttype":result.fields[n].type,"width":result.columns[n].width,"editable":editable,"edittype":edittype,"editrules":{required:required,number:number},hidden:hidden});						
					}			
				}		
				var editurl	= "common/processUpdate.action";
				if(caller == 'StandbyOut' || caller == 'MaterielOut' ){
					editurl = "as/port/updateMaterialQtyChangeInProcss.action";
				}
				var data = $.evalJSON(result.data.replace(/,}/g, '}').replace(/,]/g, ']'));
				content.html('<table id="grid'+i+'"class="content"></table><div id="pager'+i+'"></div>');
				if(caller == 'Inquiry' && button != null){
					inquirydetail(i,data,colModel,condition);
				}else{
					jQuery('#grid'+i).jqGrid({
						"hoverrows":false,
						"viewrecords":true,	
						 //"styleUI": 'Bootstrap',
						"gridview":true,			
						"data": data,
						"scrollPaging":true,
						"shrinkToFit":false,
						"autoScroll": true,  
						"width":$('#topToolbar').width(),
						"rowNum":20,
						"rowList" : [20,40,60],
						"datatype": "local",
						"colModel":colModel,
						 gridComplete : function() {//增加一列编辑列						 	
						 	var grid = $(this);  
					        var ids = grid.getDataIDs();  
				              for ( var i = 0; i < ids.length; i++) {
				                var cl = ids[i];			            			         
				                var id = $(this)[0].id;
		                		 be = "<input  type='image' src='img/edit.png' id='image1' onclick=\"updateRow('"+$(this)[0].id+"','"+cl+"','"+caller+"','"+instance+"','"+keyName+"','"+keyValue+"','"+mad_code+"');\"/>";	                	
				                 grid.jqGrid('setRowData', ids[i],
				                    {
				                      act : be
				                    });
				                  grid.setRowData(cl, false, {height: 35} );  
				              }
	                     },	      			   
						"pager":"#pager"+i,
						"editurl":basePath+editurl
					});
					jQuery('#grid'+i).jqGrid('navGrid','#pager'+i,{add:false,edit:false,del:false,refresh:false},{reloadAfterSubmit:false},{},{},{multipleSearch:true}); 		
				}
			},
			error: function(xhr){
				setLoading(false);
				if(xhr.responseText) {
					var response = $.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}					
		});
	};
	//包含明细增加tab 切换
	var addTabPanel = function(cn,detail,fields,keyValue,keyName){
		//增加多个tab，如果有明细行				
		var html = '',html2 = '';
		for(var i=0;i<cn;i++){
		   html += '<li id="detail-header'+i+'"><a><span class="glyphicon glyphicon-list-alt"></span>&nbsp;'+detail[i].mad_name+'</a></li>';
		   html2 +='<div id="bill-detail'+i+'" class="tab-pane"><div class="empty"></div></div>';
		}
		$('.nav-tabs>#main-header').after(html);
		$('#bill-main').after(html2);
		var width = $('#topToolbar').width()-$('.nav-tabs>li.active').width()-2;
		var d = width/(i+1)+'px';
		var lis = $('.nav-tabs>li');
		for(var j=0; j<lis.length;j++){			
			 $('#'+lis[j].id).attr("oldwidth",$('.nav-tabs>li:eq('+j+')').width());
			if(lis[j].id != $('.nav-tabs>li.active')[0].id){			 
			  lis[j].style.width = d;			 
			}
		}
		for(var i=0;i<cn;i++){	
			//生成明细表grid
			$('#detail-header'+i).bind('boxready', function(a){
				parseBillDetail($('#bill-detail'+a.target.id.split("header")[1]),a.target.id.split("header")[1],detail,fields,keyValue,keyName);
			});
		}			
	};
	//获取关联明细表
	var getDetail = function(fields,keyValue,caller,keyName){	
		$.ajax({
			url:basePath + 'mobile/common/getAuditDetail.action',
			type: 'POST',
			data: {caller: caller,_noc: 1},
			success: function(result){
				var detail = result.detail;
				if(detail) {
					addTabPanel(detail.length,detail,fields,keyValue,keyName);			
					reActive();
				}
			},
			error: function(xhr){
				if(xhr.responseText) {
					var response = $.evalJSON(xhr.responseText);
					if(response.exceptionInfo == 'ERR_NETWORK_SESSIONOUT') {
						dialog.show('获取用户信息失败', '请先登录！');
					} else {
						dialog.show('错误', response.exceptionInfo);
					}
				}
			}
		});
	};	
	// parse bill main
	var parseBillMain = function(main,button,cond,caller) {
		bill = $.evalJSON(main.data.replace(/,}/g, '}').replace(/,]/g, ']'));
		/*statuscode = bill[main.statuscodeField];*/
		var doo=getUrlParam('_do');
		if(doo == 1){
			$('#deal-msg').hide();
			$('#buttons').hide();
		}
		var items = main.items;
		var bo = false, ifGroup = false;
		var sortItems;
		$.each(items, function(key, val){				
		   val['readOnly'] = true;
		});
		if(button != null && doo != 1){//判断是否有可编辑的字段
		    var fieldsArray = button.jb_fields.split(";");
		    var neccessaryField = null;
		    if(button.jt_neccessaryfield){
		       neccessaryField = button.jt_neccessaryfield.toLowerCase().split(",") ;
		    }
		    requiredFields = neccessaryField;
			$.each(items, function(key, val){				
				if (val.groupName == button.jb_buttonname && button.jb_fields.indexOf("#") > 0) {
				    val['readOnly'] = false;
				    bo = true;
				} else {			
					if ($.inArray(val.groupName,fieldsArray) > -1) {
						val['readOnly'] = false;	
						bo = true;
					}
					if (neccessaryField != null && $.inArray(val.name,neccessaryField) > -1) {//编辑字段中是否有必填字段
					    val.allowBlank = false;
						val.fieldStyle = "background:#fffac0;color:#515151;";
					} else val.fieldStyle = "background:#FFFAFA;color:#515151;"
				}
			});			
		}
		if(bo){//有可编辑字段需要按照可编辑排序,和组别排序，通过引入的thenBy.js实现
			sortItems = items.sort(
			    firstBy(function (v1, v2) {
			      if(!v1.readOnly){
	                return -1;
	                }
	              }
			    ).thenBy(function (v1, v2) {			    	
			    	  return v1.group - v2.group; 			    	
			     })
			);
		}else{//没有不要排序，按原有顺序显示
			sortItems = items;
		}
		//分组，按照groupName分组
        var getMixedGroups = function (items, fields) {
	        var data = new Object(), k, o; 
	        $.each(items,function(key,d) {
	            k = '';
	            o = new Object();
		        if(d[fields] != null && d[fields] != ''){
		            k += fields + ':' + d[fields] + ',';
		            o[fields] = d[fields];            
		            if (k.length > 0) {
		                if (!data[k]) {
		                    data[k] = {
		                        keys: o,
		                        groups: [d]
		                    };
		                } else {		                	
		                    data[k].groups.push(d);
		                }
		             }
		         }
	        });
         return data;
      }
      var tempItems = getMixedGroups(sortItems,'groupName');
      if(! $.isEmptyObject(tempItems)){//有分组
      	 sortItems = tempItems;
      	 ifGroup = true;
      }
      var html = '<form  method="post" id="mainForm"> ' +
      		      ' <table class="table table-condensed table-bordered table-striped"><colgroup><col width="40%"></col><col width="60%"></col></colgroup>';
      if(bo){//有可编辑的增加保存按钮
      	var cd = cond.split("=");
      	html +='<div class="list-group"> <div class="text-center col-xs-8" id="tips" style="color:#FF0000;"> </div>';
      	html +="<div class='text-right'><button type='button' class='btn-sm' id='jb_buttonid' name='"+button.jb_buttonname+ "' onclick=\"saveForm('"+cd[0]+"',"+cd[1]+",'"+caller+"');\">"+button.jb_buttonname+"</button></div></div>";
      }
	  $.each(sortItems,function(key,d) {
	  	   var strData,seData;
	      	if(d.keys){//有分组
		      	if(d.keys.groupName != null){
		      		html += ' <tbody><tr><td colspan="2" class="text-center text-success" id="'+d.keys.groupName+'"><strong>' + d.keys.groupName + '</strong>&nbsp;<span class="glyphicon glyphicon-chevron-down"></span></td></tr> <tbody>';
		      		html += ' <tbody id="'+d.keys.groupName+'0">';		   
		      	}
		      	for(var i=0;i<d.groups.length;i++){
		      		if(d.groups[i].id != null && d.groups[i].xtype != "hidden" && d.groups[i].fieldLabel != null){
			      		html += '<tr>';
						html += '<td class="text-right special" ><strong><label for='+d.groups[i].id+'>' + d.groups[i].fieldLabel+ '</label></strong></td>';
						strData = changeEscapeCharacter(bill[d.groups[i].id]);
						if(!d.groups[i].readOnly){//可编辑字段
							//编辑的类型，下拉框，dbfind，数字类型【需要新增】
							if(d.groups[i].xtype == 'combo'){//下拉框
								html +='<td class="text-center"><select name="'+d.groups[i].id+'" id="'+d.groups[i].id+'" class="cellElement form-control" style="'+d.groups[i].fieldStyle+'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +'>';
								html +='<option value=""></option>';
								$.each(d.groups[i].store.data,function(key,da){
									if(bill[d.groups[i].id] == da.value){
							    		html +='<option value="'+da.value+'" selected="selected">'+changeEscapeCharacter(da.display)+'</option>';
									}else{
										html +='<option value="'+da.value+'">'+changeEscapeCharacter(da.display)+'</option>';
									}
								});
								html+='</select></td>';
							}else if(d.groups[i].xtype == "dbfindtrigger"){//dbfind字段								                             
								html += '<td class="text-center">' +
									'<input type="text" id="'+d.groups[i].id+'" style='+d.groups[i].fieldStyle+
											' name='+ d.groups[i].id+' style='+d.groups[i].fieldStyle +
											' class="cellElement form-control" dbfind value="'+strData +'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +'/>' +
									'</td>';
							}else if (d.groups[i].xtype == "multifield"){//MT类型						
								seData = changeEscapeCharacter(bill[d.groups[i].secondname]);
							    html += '<td class="text-center">' +
									'<input type="text" id="'+d.groups[i].id+'" style='+d.groups[i].fieldStyle+
											' name='+ d.groups[i].id+' style='+d.groups[i].fieldStyle +
											' class="cellElement form-control" dbfind value="'+strData +'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +'/>' +
									  '<textarea id='+ d.groups[i].secondname+' readonly class="cellElement setInput" name='+ d.groups[i].secondname+
								           ' value="'+seData +'">' +seData+'</textarea>'+
									 '</td>';
							}else if(d.groups[i].xtype == 'numberfield'){//数字类型					
								html += '<td class="text-center">' +
									'<input type="text" id='+ d.groups[i].id+' style='+d.groups[i].fieldStyle+
											' name='+ d.groups[i].id+' style='+d.groups[i].fieldStyle +
											' class="cellElement form-control" value="'+bill[d.groups[i].id] +'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +'onkeyup="return ValidateNumber($(this),value)"/>' +									
									'</td>';
							}else if(d.groups[i].xtype == "erpYnField"){//YN类型，是否有默认值
								html +='<td class="text-center"><select name="'+d.groups[i].id+'" id="'+d.groups[i].id+'" class="cellElement form-control" style="'+d.groups[i].fieldStyle+'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +'>';
								if(d.groups[i].value == '-1' || d.groups[i].value == '0'){
									if(bill[d.groups[i].id] == '-1' || bill[d.groups[i].id] == '0'){
									    html +='<option value="-1"'+(bill[d.groups[i].id] == '-1' ? 'selected="selected"' : '')+'>是</option>';
										html +='<option value="0"'+(bill[d.groups[i].id] == '0' ? 'selected="selected"' : '')+'>否</option>';
									}else{
										 html +='<option value="-1"'+(d.groups[i].value == '-1'? 'selected="selected"' : '')+'>是</option>';
										html +='<option value="0"'+(d.groups[i].value == '0'? 'selected="selected"' : '')+'>否</option>';
									}
								}else{
									html +='<option value=""></option>';
									html +='<option value="-1"'+(bill[d.groups[i].id] == '-1' ? 'selected="selected"' : '')+'>是</option>';
									html +='<option value="0"'+(bill[d.groups[i].id] == '0' ? 'selected="selected"' : '')+'>否</option>';
								}
								html+='</select></td>';					
							}else {//其他类型字段
								html += '<td class="text-center">' +
									'<input type="text" id='+ d.groups[i].id+' style='+d.groups[i].fieldStyle+
											' name='+ d.groups[i].id+' style='+d.groups[i].fieldStyle +
											' class="cellElement form-control" value="'+strData +'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +(d.groups[i].xtype =="datefield" ? ' datefield' : '')+'/>' +
									'</td>';									
							}
						}else{//不可编辑字段				
							if(d.groups[i].xtype == "multifield"){//MT类型
							   seData = changeEscapeCharacter(bill[d.groups[i].secondname]);
							   html += '<td class="text-center">' +
									' <input type="text" id='+ d.groups[i].id+' readonly name='+ d.groups[i].id+
										' class="cellElement setInput" value="'+strData +'"'+(d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '')+'/>' +
										'<textarea id='+ d.groups[i].secondname+' readonly class="cellElement setInput" name='+ d.groups[i].secondname+
								     ' value="'+seData +'">' +seData+'</textarea>'+
								   '</td>';
							}//goua  此处针对问题反馈2016120375添加了对于CDHM格式的判断
							else if (d.groups[i].xtype == "condatehourminutefield"){//condatehourminutefield 				
								seData = changeEscapeCharacter(bill[d.groups[i].secondname]);
							    html += '<td class="text-left">' +
									'<input type="text" readonly="readonly" id="'+d.groups[i].id+'name='+ d.groups[i].id +
									'readonly class="cellElement setInput" value="'+ strData +'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +'/>' +
									 '<textarea id='+ d.groups[i].secondname+'  readonly class="cellElement setInput" name='+ d.groups[i].secondname+
								           ' value="'+seData +'">' +seData+'</textarea>'+
									 '</td>';
							}else if(d.groups[i].xtype == "erpYnField"){//是否类型				
								html +='<td class="text-center"><select  readonly="readonly" name="'+d.groups[i].id+'" id="'+d.groups[i].id+'" class="cellElement" style="'+d.groups[i].fieldStyle+'"'+ (d.groups[i].logic ? 'logic="' + d.groups[i].logic + '"' : '') +(d.groups[i].allowBlank ? '' : 'required') +'>';								
							    if(bill[d.groups[i].id] == '-1' || bill[d.groups[i].id] == '0'){
								    html +='<option value="-1"'+(bill[d.groups[i].id] == '-1' ? 'selected="selected"' : '')+'>是</option>';
									html +='<option value="0"'+(bill[d.groups[i].id] == '0' ? 'selected="selected"' : '')+'>否</option>';
								}else{
									html +='<option value=""></option>';
									html +='<option value="-1"'+(bill[d.groups[i].id] == '-1' ? 'selected="selected"' : '')+'>是</option>';
									html +='<option value="0"'+(bill[d.groups[i].id] == '0' ? 'selected="selected"' : '')+'>否</option>';
								}
								html+='</select></td>';	
							}else if(d.groups[i].xtype == "htmleditor" || d.groups[i].xtype == "HrOrgSelectfield"){//html格式的直接显示
							    html += '<td><div style="overflow-y:scroll;width:100px;" class="htmleditor">' +bill[d.groups[i].id]+'</td>';
							}else{
								html += '<td class="text-center">' +
								      '<textarea id='+ d.groups[i].id+' readonly class="cellElement setInput" name='+d.groups[i].id+
								         ' value="'+strData +'">' +strData+'</textarea>'+
								   '</td>';
							}
						}				
						html += '</tr>';
		      		}
		      	} 
		      	html+='</tbody>';
	      	}else{//无分组
	      		strData = changeEscapeCharacter(bill[d.id]);
	      		if(d.id != null && d.xtype != "hidden" && d.fieldLabel != null){
			      		html += '<tr>';
						html += '<td class="text-right special"><strong><label for='+d.id+'>' + d.fieldLabel+ '</label></strong></td>';										
						if(d.xtype == "multifield"){ //MT
						 seData = changeEscapeCharacter(bill[d.secondname]);
						  html += '<td class="text-center">' +
								'<input type="text" id='+ d.id+' readonly class="cellElement setInput" name='+ d.id+
										' value="'+strData +'"'+(d.logic ? 'logic="' + d.logic + '"' : '')+'/>' +
								'<textarea id='+ d.secondname+' readonly class="cellElement setInput" name='+ d.secondname+
								' value="'+seData +'">' +seData+'</textarea>'+
								'</td>';		
						}else if(d.xtype == "erpYnField"){
							html +='<td class="text-center"><select readonly name="'+d.id+'" id="'+d.id+'" class="cellElement " style="'+d.fieldStyle+'"'+ (d.logic ? 'logic="' + d.logic + '"' : '') +(d.allowBlank ? '' : 'required') +'>';
							if(strData == '-1' || strData == '0'){
							    html +='<option value="-1"'+(strData == '-1' ? 'selected="selected"' : '')+'>是</option>';
								html +='<option value="0"'+(strData == '0' ? 'selected="selected"' : '')+'>否</option>';
							}else{	
								html +='<option value=""></option>';
								html +='<option value="-1"'+(strData == '-1' ? 'selected="selected"' : '')+'>是</option>';
								html +='<option value="0"'+(strData == '0' ? 'selected="selected"' : '')+'>否</option>';
							}
							html+='</select></td>';	
						}else if(d.xtype == "htmleditor" || d.xtype == "HrOrgSelectfield"){//html格式的直接显示
							html += '<td><div style="overflow-y:scroll;width:100px;" class="htmleditor">' +bill[d.id]+'<div></td>';
						}//goua  此处针对问题反馈2016120375添加了对于CDHM格式的判断
						else if(d.xtype=='condatehourminutefield'){
						    seData = changeEscapeCharacter(bill[d.secondname]);
						    html += '<td class="text-center">' +
								'<input type="text" id='+ d.id+' readonly class="cellElement setInput" name='+ d.id+
										' value="'+strData +'"'+(d.logic ? 'logic="' + d.logic + '"' : '')+'/>' +
								'<textarea id='+ d.secondname+' readonly class="cellElement setInput" name='+ d.secondname+
								' value="'+seData +'">' +seData+'</textarea>'+
								'</td>';	
						}else{							
							html += '<td class="text-center">' +
							      '<textarea id='+ d.id+' readonly class="cellElement setInput" name='+ d.id+
										' value="'+strData +'"'+(d.logic ? 'logic="' + d.logic + '"' : '')+'>' +strData+'</textarea>'
								   '</td>';	
						}
						html += '</tr>';
		      	}else if(d.id != null){//隐藏字段
		      		html += '<input type="hidden" id='+ d.id+' readonly  name='+ d.id+
								' value="'+ strData +'"'+(d.logic ? 'logic="' + d.logic + '"' : '')+'/>';
		      	}
	      	}
	    });
		html += '</table></form>';
		$('#bill-main').html(html);
		$("#mainForm :input[required]:eq(0)").focus();
		$('.htmleditor').width($('.htmleditor').parent().width());
        if(ifGroup){//有分组点击收缩
        	$.each(sortItems,function(key,d){
        		$("#"+d.keys.groupName).click(function(){
			        $("#"+d.keys.groupName+"0").slideToggle("normal");
			    });
           });			
        }              
        var formDbfinds = {},resultData = {};
        var dbs = $("#mainForm :input[dbfind]") ;         
        $.each(dbs,function(key,value){
	        $('#'+value.id).autocomplete({	//form表DBfind获取数据
	        	 minLength: 1,
		         source: function(query, proxy) {
					$.ajax({
						url: basePath+'common/dbfind.action',
						dataType: "json",
						type:'POST',
						data: {
						    which:'form',
							caller:caller,
							field:$(this)[0].element[0].id,
							condition:"upper("+$(this)[0].element[0].id+") like '%"+query.term.toUpperCase()+"%'",
							ob:'',
							page:1,
							pageSize:13													
						},
						success: function (result) { 
							resultData = $.evalJSON(result.data);
			                formDbfinds = result.dbfinds;
			                proxy($.evalJSON(result.data)); 														
			            }							            
					});										
				},
				select: function(event, ui) {		
					$.each(formDbfinds,function(key,value){
						if($('#'+value.field).length != 0 && typeof(ui.item[value.dbGridField])!= "undefined"){
	             	    	$('#'+value.field).val(ui.item[value.dbGridField]);  
						}						
					});
					return false;
				}	       
		    })/*.focus(function () {
               $(this).autocomplete("search");
	        })*/.blur(function(event){
	        	if(formDbfinds.length != 0){
		         	  var id = $(this)[0].id,val= $(this).val(),field;
		         	  $.each(formDbfinds,function(key,value){
				          if(value.field == id){
				  	         field = value.dbGridField ;
				         }
			          });
			          $.each(resultData,function(key,value){	         			         
			             if(value[field] == val){
			         		$.each(formDbfinds,function(key,da){
								if($('#'+da.field).length != 0 && typeof(value[da.dbGridField])!= "undefined"){
			             	    	$('#'+da.field).val(value[da.dbGridField]);  
								}									
						     });
			         	 }
			         });
	        	   }
				}).autocomplete("instance" )._renderItem = function( ul, item ) {
			        var id = $(this)[0].element[0].id;
			        var data ;
			        $.each(formDbfinds,function(key,value){
					    if(value.field == id){
					  	   data = value.dbGridField ;
					    }
				     });
			        return $( "<li>" )
			           .append( "<a>" + item[data]+ "</a>" )
			           .appendTo( ul );
			    };
	    });
	    
	      var areas = $("#mainForm textarea");
	       $.each(areas,function(key,value){
	            autoTextarea(value);
	       });
	       $("#mainForm select[readonly]").prop("disabled", true);
	       $("#mainForm :input[datefield]").datepicker({	                              
                    dateFormat: 'yy-mm-dd',
                    showOn: 'focus'
                });
	};	
	//获取主表数据字段
	var getBillMain = function(caller, url, cond, billId,button) {
		$.ajax({
			url:basePath + 'common/singleFormItems.action',
			type: 'POST',
			data: {caller: caller, condition: cond, _noc: 1},
			success: function(result){
				if(result.data) {
					parseBillMain(result,button,cond,caller);
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
	//根据审批流caller 获取页面caller
	var getPageCaller = function(nodeCaller,url,callback) {
		$.ajax({
			url:basePath + 'common/form/getPageCaller.action',
			type: 'POST',
			data: {caller : nodeCaller,url:url, _noc: 1},
			success: function(result){
				callback && callback.call(null, result);
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
	// parse main point
	var parseMainPoint = function(points) {
		var html = '<ul class="list-group">';
		html += '<li class="list-group-item disabled"><strong>问题要点</strong></li>';
		$.each(points, function(i, p){
			html += '<li class="list-group-item">';
			html += '<span>' + p.text + '</span>';
			if(p.type === 'B') {//boolean
				html += '<div class="pull-right"><div class="has-switch switch-small"><div class="' + (p.value && p.value != '是' ? 'switch-off' : 'switch-on') + ' switch-animate"><input type="checkbox" ' + (p.value && p.value != '是' ? '' : 'checked') + ' title="' + p.text + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') +(p.required ? 'required' : '') + '> <span class="switch-left switch-success switch-small">是</span> <label class="switch-small">&nbsp;</label> <span class="switch-right switch-warning switch-small">否</span></div></div></div>';
			} else if(p.type === 'S' ) {//字符串
				html += '<div class="pull-right"><input class="form-control input-xs" type="text" placeholder="..." value="' + (p.value || '') + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') + ' title="' + p.text + '" ' + (p.required ? 'required' : '') + '></div>';
			} else if(p.type === 'D') {//日期
				html += '<div class="pull-right"><input class="form-control input-xs" type="date" placeholder="..." value="' + (p.value || '') + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') + ' title="' + p.text + '" ' + (p.required ? 'required' : '') + '></div>';
			}else if(p.type === 'C'){//下拉框
				html += '<div class="pull-right"><select class="form-control input-xs" '+ (p.logic ? 'logic="' + p.logic + '"' : '') + ' title="' + p.text + '" ' + (p.required ? 'required' : '') + ' ><option></option>';
				$.each(p.data,function(i,da){
					html+='<option value="'+da+'"'+(da==p.value ? 'selected="selected"' : ' ')+'>'+da+'</option>'
				});
				html +='</select></div>';
			}else if(p.type === 'N'){//数字型
				html += '<div class="pull-right">'+'<input class="form-control input-xs numbertype" type="text" placeholder="..." value="' + (p.value || '') + '" ' + (p.logic ? 'logic="' + p.logic + '"' : '') + ' title="' + p.text + '" ' + (p.required ? 'required' : '') + ' onkeyup="return ValidateNumber($(this),value)"></div>';
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
	var getMainPoint = function(id) {
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
						logic: q > 0 ? c.substring(q + 1) : null,
						data : c.substring(c.indexOf('[')+1,c.indexOf(']')).split(";")
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
	var parseNode = function(node,button) {
		current = node;
		$('#jp_name').html('<div class="text-center" style="white-space:nowrap;">'+node.jp_name+'</div>');
		$('#jp_name').css('margin-left', "-" + node.jp_name.replace(/[^\x00-\xff]/g, 'xx').length * 7.5 + "px");
		$('#jp_nodeName').text(node.jp_nodeName);
		$('#jp_launcherName').text(node.jp_launcherName);
		$('#jp_launchTime').text(parseDate(new Date(node.jp_launchTime)));
		$('#jp_codevalue').text(node.jp_codevalue);
		getPageCaller(node.jp_caller,node.jp_url,function(caller){		
			//根据node 
			var fields = [];var bol = false;
			pageCaller = caller;
			if(button != null){//判断编辑字段
				var buttontype = button.jb_fields;
				var neccessaryField = button.jt_neccessaryfield ;
				if(neccessaryField != null){
					neccessaryField = neccessaryField.toLowerCase();
				}
				if(buttontype == 'updatedetail'){
					if (neccessaryField != null){
						 fields = neccessaryField.split(",");
					}
				}else if (buttontype.indexOf('#') > 0) {
					if (neccessaryField != null){
						 fields = neccessaryField.split(",");
					}
				}else{//主表编辑字段
					bol = true;					
				}
			}	
			// get bill main data
			if(node.jp_keyName && node.jp_keyValue && bol) {
				getBillMain(caller, node.jp_url, node.jp_keyName + '=\'' + node.jp_keyValue + '\'', node.jp_keyValue,button);
			}else{
				getBillMain(caller, node.jp_url, node.jp_keyName + '=\'' + node.jp_keyValue + '\'', node.jp_keyValue);
			}
			// has detail
			if(node.jp_formDetailKey) {
				//获取明细表个数		
				getDetail(fields,node.jp_keyValue,caller,node.jp_keyName); 
			}
		});		
		// main point
		getMainPoint(node.jp_nodeId);
		//deal relative
		if(true){
			dealRelative(node.jp_caller);
		}
	};
	
	//获取审批流设置的信息
	var getProcess = function(id) {
		setLoading(true);
		var master = getUrlParam('master');
		$.ajax({
			url:basePath + 'common/getCurrentNode.action',
			type: 'POST',
			data: {jp_nodeId: id, _noc: 1,master:master},
			success: function(result){
				setLoading(false);
				instance = result.info.InstanceId;
				button = result.info.button;
				forknode=result.info.forknode
				parseNode(result.info.currentnode,result.info.button);
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
	var getHistory = function(instanceId, callback) {
		setLoading(true);
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
	
	var getAllHistory = function(nodeId, callback) {
		setLoading(true);
		$.ajax({
			url:basePath + 'common/getAllHistoryNodesByNodeId.action',
			type: 'POST',
			data: {nodeId:nodeId, _noc: 1},
			success: function(result){
				setLoading(false);
				if(callback)
					callback.call(null, result.nodes);
				if(result.nodes && result.nodes.length > 0) {
					parseAllHistory(result.nodes);
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
	
	var getInstance = function(id, callback) {
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
			html += '<div>审批意见：' + (h.jn_nodeDescription == null ? "无":h.jn_nodeDescription) + '</div>';
			html += '<div class="text-trans ' + res + '">';
			html += '<span>' + h.jn_dealResult + '</span>';
			html += '</div>';
			html += '<footer>' + h.jn_dealTime + '</footer>';
			html += '</blockquote>';
			if(h.jn_operatedDescription || h.jn_infoReceiver) {
					html += '<div class="highlight">';
					if(h.jn_infoReceiver)
						html += '<p class="text-muted"><i>变更处理人:' + h.jn_infoReceiver + '</i></p>';
					if(h.jn_operatedDescription) {
						if(h.jn_operatedDescription.indexOf('(是)') > 0 || 
								h.jn_operatedDescription.indexOf('(否)') > 0) {
							html += '<ul class="list-group">';
							var descs = h.jn_operatedDescription.split(';');
							$.each(descs, function(j, d){
								res = d.substr(d.length - 3) == '(是)' ? 'glyphicon glyphicon-ok text-success' : 
									'glyphicon glyphicon-remove text-warning';
								html += '<li class="list-group-item"><span class="pull-right ' + res + '"></span>' + d + '</li>';
							});
							html += '</ul>';
						} else {
							if("null"!=h.jn_operatedDescription)
								html += '<div>管理要点:' + h.jn_operatedDescription + '</div>';
						}
						}
					html += '</div>';
				}
				html += '</li>';
			});
		html += '</ul>';
		$('#history').html(html);
	};	
	
	var parseAllHistory = function(hist) {
		var html = '<ul class="list-unstyled list-inline">';
		$.each(hist, function(i, h){
			var res = h.jn_dealResult == '同意' ? 'success' : (h.jn_dealResult == '不同意' ? 'error' : 'warning');
			html += '<li>';
			html += '<div width:100% class="text-top">';
			html += '<span>' + h.jn_name + '</span>';
			html += '</div>';
			html += '<blockquote>';
			html += '<strong>' + h.jn_dealManName + '</strong>';
			html += '<div>审批意见：' + (h.jn_nodeDescription == null ? "无":h.jn_nodeDescription) + '</div>';
			html += '<div class="text-trans ' + res + '">';
			html += '<span>' + h.jn_dealResult + '</span>';
			html += '</div>';
			html += '<footer>' + h.jn_dealTime + '</footer>';
			html += '</blockquote>';

			if(h.jn_operatedDescription || h.jn_infoReceiver) {
				html += '<div class="highlight">';
				if(h.jn_infoReceiver)
					html += '<p class="text-muted" ><i> 变更处理人:' + h.jn_infoReceiver + '</i></p>';
				if(h.jn_operatedDescription) {	
					if(h.jn_operatedDescription.indexOf('(是)') > 0 || 
							h.jn_operatedDescription.indexOf('(否)') > 0) {
						html += '<ul class="list-group">';
						var descs = h.jn_operatedDescription.split(';');
						$.each(descs, function(j, d){
							res = d.substr(d.length - 3) == '(是)' ? 'glyphicon glyphicon-ok text-success' : 
								'glyphicon glyphicon-remove text-warning';
							html += '<li class="list-group-item"><span class="pull-right ' + res + '"></span>' + d + '</li>';
						});
						html += '</ul>';
					} else {
						if("null"!=h.jn_operatedDescription)
							html += '<div>管理要点:' + h.jn_operatedDescription + '</div>';
					}		
			}
				html += '</div>';
			}
			html += '</li>';
		});
		html += '</ul>';
		$('#all-history').html(html);
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
		$('#all-history-header').bind('boxready',function(){
			getAllHistory(nodeId);
			
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
		var bo = true;
		var deals = $('#points input');
		if(deals.length > 0) {
			$.each(deals, function(i, d){
				var e = $(d), type = e.attr('type'), val = e.val();
				if((type === 'text' || type === 'date') && (e.attr('required') === 'required') && !val ){
					bo = false;
				}
			});
		}
	   deals = $('#points select');
	   if(deals.length > 0) {
			$.each(deals, function(i, d){
				var e = $(d), val = e.val();
				if((e.attr('required') === 'required') && !val)
					bo = false;
			});
		}
		return bo;
	};
	// get your points
	var getDealPoints = function() {
		var deals = $('#points input');
		var points = [];
		var selPoints = [];
		if(deals.length > 0) {
			
			$.each(deals, function(i, d){
				var e = $(d), text = e.attr('title'), type = e.attr('type'), val = e.val(),
					lg = e.attr('logic');
				val = type == 'checkbox' ? (e.is(':checked') ? '是' : '否') : val;
				points.push(text + '(' + val + ')' + (lg ? '@' + lg + '@' : ''));
			});
		}
		
	   deals = $('#points select');
	   if(deals.length > 0) {
			$.each(deals, function(i, d){
				var e = $(d),text = e.attr('title'),val = e.val(),lg = e.attr('logic');
				selPoints.push(text + '(' + val + ')' + (lg ? '@' + lg + '@' : ''));
			});
		}
		if(points.length>0||selPoints.length>0){
			var arr = new Array();
			return arr.concat(points,selPoints).join(';');
		}
		return null;
	};
	// agree
	var agree = function() {
		var d = {};
		var bool = true;
		
		if(document.getElementById("jb_buttonid")){
			$("#mainForm select[readonly]").removeProp("disabled");
		    var t = $('#mainForm').serializeArray();
		    $("#mainForm select[readonly]").prop("disabled", true);
		    $.each(t, function() { 
		      if($('#'+this.name).attr("logic") || $('#'+this.name).attr("logic") != 'ignore'){ //判断logic的属性是否为ignore 忽略
		      	d[this.name] = this.value;
		      }
		    });			    
			//判断主表是否存在未填写的必填字段
			if (requiredFields != null) {//判断必填项是否填写
/*				if(!canexecute){
					$.showtip("保存之前请先填写必填的信息!", 2000);
					return;
				}*/
				
				$.each(requiredFields, function(key, field){
					if (d[field] == null || d[field] == "") {
						bool = false;
						$.showtip("保存之前请先填写必填的信息!", 2000);
						$('#'+field).focus();
						return;
					}
				});		
		    }
		    if(!bool){
		    	return ;
		    }
		    //获取改变了的值
		    var e='';
		    var f='';
			$.each(d,function(key,field){//只将修改的值提交到后台
				e = d[key];
				if(isNaN(e)){
					e = e.replace(/\n/g,'').replace(/\r/g,'');
				}			
				f = bill[key];
				if(isNaN(f)){
					f = f.replace(/\n/g,'').replace(/\r/g,'');
				}
				if(e == f)	{	
				   delete d[key];	
				}
			});
			if(!$.isEmptyObject(d)){
				$.showtip("请先保存修改的数据，再执行后续操作!", 2000);
				return;
			}
			}		
	    //判断从表是否存在未填写的必填字段
		if(!isValid()) {
			dialog.show(' 警告', '您还有审批要点问题没处理！', -1);
			return;
		}
		setLoading(true);
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
	$('#agree').click(function(){//点击我同意按钮事件
		agree();
	});
	// disagree -> return back
	var disagree = function(backNode, msg){
		setLoading(true);
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
			if(forknode==0){//并行节点只能回退至制单人
			$.each(nodes, function(i, n){
			if(n.jn_attach=='T'){
				html += '<label class="radio-inline">';
				html += '<input type="radio" name="node" value="' + n.jn_name + '"> ' + n.jn_name;
				html += '</label>';
			}
			});}
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
		$.ajax({
			url:basePath + 'common/setAssignee.action',
			type: 'POST',
			data: {taskId: nodeId, assigneeId: user, processInstanceId: instance,
				   ncustomDes: getDealPoints(), description: msg, _noc: 1},
			success: function(result){
				setLoading(false);
				if(result.result) {
					dialog.show('处理结果', '节点已成功变更');
					
					//变更成功之后跳转到下一条
					processNext(result.nextnode);
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
		html += '<div class="form-group has-feedback dropdown"><input id="em_new" name="em_new" class="form-control  dropdown-toggle" type="text" placeholder="新的处理人，点击搜索"/><span id="change_search_icon" class="glyphicon glyphicon-search form-control-feedback"></span><div class="dropdown-menuman dropdown-menu"><div id="em_search" class="list-group"></div></div>' +
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
				$(this).parent().children('.dropdown-menuman').css('display', 'block');
			}
		});
		$('#em_new').bind('change', function(){// 点击获取变更的候选人
			var v = $(this).val();
			var view = $(this).parent().children('.dropdown-menuman');
			if(v) {
				view.css('display', 'block');
				getSearchResult(v);
			} else {
				view.css('display', 'none');
			}
		});
		$('#em_new').keypress(function(e){
            if(e.keyCode==13){
                e.preventDefault();
            }
		});

		$('#em_new').bind('keyup', function(event){// 回车获取变更的候选人
			if(event.keyCode == 13) {
				/*event.preventDefault();*/
				var v = $(this).val();
				var view = $(this).parent().children('.dropdown-menuman');
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
			var view = $('#em_new').parent().parent().children('.dropdown-menuman');
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
	$('#next').click(function(){
		var taskId = nodeId;
		$.ajax({
			url: basePath + 'common/getNextProcess.action',
			type: 'POST',
			data: {
				taskId: taskId,
				_noc: 1
			},
			success: function(result) {
				var jsonData = result;
				if (jsonData.success && jsonData.nodeId!=-1) {
					window.location.href = basePath + "jsps/mobile/process.jsp?nodeId=" + jsonData.nodeId;
				} else {
					dialog.show('提示', '已无待审批的单据');
				}

			}
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