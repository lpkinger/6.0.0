Ext.QuickTips.init();
Ext.define('erp.controller.common.messageCenter.Information', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views: ['common.messageCenter.Information','common.messageCenter.InformationForm',    
    		'common.messageCenter.InformationGrid','core.button.StatButton','core.button.SwitchButton','core.form.BtnDateField'],
    init: function(){ 
    	var me = this;
    	this.control({ 
			'erpInformationForm':{
				afterrender:function(form){
					var width = (form.getWidth()/2) - 128;
					form.add({
						xtype:'displayfield',
						id:'msgNotice',
						renderTo:Ext.getBody(),
						html:'您有新的消息，点击此处即会刷新列表',
						height:30,
						width:221,
						hidden:true,
						style:'font-size:12px;top:0px;left:'+width+'px;height:35px!important;background-color:#ec971f;padding:8px;color:white;position:fixed;z-index:1;width:256px!important;cursor:pointer'		
			    	});
				}
			},
			'InformationGrid':{
				afterrender:function(grid){
    				me.reconfigureGrid(grid);						
					me.loadGridData();
    			},
    			'headerfiltersapply': function() {//触发筛选时调用函数
					me.headerfiltersapplyFn();
				},
				cellclick:function(grid ,td,cellIndex,record ,tr,rowIndex,e){
        			var me=this;
        			var informationgrid = Ext.getCmp('informationgrid');
        			var field = grid.ownerCt.columns[cellIndex].dataIndex;
        			var IHD_ID=record.data.IHD_ID;
        			var CURRENTMASTER=record.data.CURRENTMASTER;
        			var str='{"IHD_ID":'+IHD_ID+',"CURRENTMASTER":\''+CURRENTMASTER+'\'}';
        			informationgrid.readStatusData =str;
        			informationgrid.currentmaster =CURRENTMASTER;
        			if (field == 'IH_CONTEXT'){
        				if(informationgrid.flag==null||informationgrid.flag==0){    					
	        				me.showdetail(record,grid);       					
        				}
        				informationgrid.flag=0;
        			}
        		}
			},
			'#msgNotice':{
				afterrender:function(dfield){
					var fieldDom = dfield.el.dom;
					var form = Ext.getCmp('informationform');
					dfield.el.setX((form.getWidth()/2) - 128 ); //定位到form的中心
					dfield.el.setY(-2);
					var grid = Ext.getCmp('informationgrid');
					fieldDom.onclick = function(){
						var msgNotice = Ext.getCmp('msgNotice');
						msgNotice.hidden = true;msgNotice
						Ext.getCmp('msgNotice').el.slideOut('t', { duration: 0 });						
						grid.store.load({
							callback:me.callbackFn
						});
					}
				}        			
			},
			'button[id=readBtn]':{				
				click:function(v,b){
					var listRecord = new Array();  
					var grid=Ext.getCmp("informationgrid");				
					var items = grid.store.data.items;					
					Ext.each(items, function(item){						
						if (item.dirty) {
							listRecord.push(item.data);
						}										
					});
					if(listRecord.length<1)showError("请先勾选数据!");
					else{
						var data=Ext.encode(listRecord); 
						this.updateReadstatus(data);
						var messageredpoint=parent.document.getElementById("messageredpoint");
						if(listRecord.length==items.length){
                			messageredpoint.style.display = 'none';
						}else{
	                		messageredpoint.style.display = 'block';
						}
					}					 
				}				
			},
			'erpStatButton':{
				click:function(v){
					var grid = Ext.getCmp('informationgrid');
					me.reconfigureGrid(grid);	
					me.loadGridData();
					if(v.groupName=='infoType'){
						if(v.getCount()>0){
							var sceneBtns = Ext.getCmp('scene');
							var statusBtns = Ext.getCmp('status');
							var activeSceneBtn = sceneBtns.getActive();
							var activeStatusBtn = statusBtns.getActive();
							if(activeSceneBtn.id!='recived'){
								sceneBtns.setActive(Ext.getCmp('recived'));
							}
							if(activeStatusBtn.id!='unread'){
								statusBtns.setActive(Ext.getCmp('unread'));
							}		
							me.reconfigureGrid(grid);							
							me.loadGridData();
						}						
					}
				}
			}    
    	});
    },   
    getGroup:function(){
    	var group = '';
		var form = Ext.getCmp('informationform');
		var switchButtons = form.query('erpSwitchButton');					
		if(switchButtons.length>0){
			Ext.Array.each(switchButtons,function(item){
					group += '&' + item.activeButton.id;	
			});							
		}
		return group.substring(1);        	
    },
     loadGridData:function(){
    	var me = this;
    	var grid = Ext.getCmp('informationgrid');
    	var form = Ext.getCmp('informationform');
    	var filters = grid.gridFilters;
		var condition = me.getCondition();//获取并设置condition
		var likestr = me.getLikeStr(grid, form, filters);//获取并设置likestr		
		grid.store.loadPage(1,{
			callback:me.callbackFn
		});	
    },
    getCondition:function(){
    	var condition = '';
		var form = Ext.getCmp('informationform');
		var switchButtons = form.query('erpSwitchButton');					
		if(switchButtons.length>0){
			Ext.Array.each(switchButtons,function(item){
				condition += " and " + item.activeButton.condition;							
			});
			condition = condition.substring(5);
			form.defaultCondition = condition;						
		}
		return condition;
    },
    showdetail:function(record,grid){
    	var me=this;
    	var data=record.data.IH_DATE;
    	var from=record.data.IH_FROM;
    	var context=record.data.IH_CONTEXT;
    	var IHD_ID=record.data.IHD_ID;
    	var CURRENTMASTER=record.data.CURRENTMASTER;
    	var str='{"IHD_ID":'+IHD_ID+',"CURRENTMASTER":\''+CURRENTMASTER+'\'}';
    	if(me.getGroup().indexOf('recived')>-1){
    		var call=record.data.IH_CALL;   		
    	var windows= Ext.create('Ext.window.Window', {   
     		x: Ext.getBody().getWidth()/2-200, 
			y: Ext.getBody().getHeight()/2-200,
     		width:500,
     		modal:true,
     		id:'mywindows',
     		CURRENTMASTER:CURRENTMASTER,
     		closable:false,     	
     		border: false,
     		frame:false,
     		resizable :false,
     		header: false,
     		draggable: false,
     		cls:'Windetail',
			items:[
				{
					xtype : 'tbtext',
					text : '<div style="text-align:center;color:#000000;font-weight:normal;font-size: 20px;">信息详情</div>',
					cls:'Wintitle'
				},
				{
				xtype:'panel',
				buttonAlign :'center',
				id:'paneldetail',
				border:false,
				layout:'fit',				
				items:[
					{	
						xtype : 'displayfield',
						fieldLabel: '发起人',
						value:call,
						labelAlign:'left',
						labelWidth:70,
						cls:'Wincontext'
						
					},
					{		
						xtype : 'displayfield',
						fieldLabel: '发起时间',
						value:data,
						labelAlign:'left',
						labelWidth:70,
						cls:'Wincontext'						
					},
					{
						xtype : 'displayfield',
						cls:'Wincontext',
						labelAlign:'left',
						labelWidth:70,
						fieldLabel: '消息分类',
						value:from,
						renderer: function (v) {
        				switch(v){
        					case 'system':
        						return '知会消息';
        						break;
        					case 'crm':
        						return 'CRM提醒';
        						break;
        					case 'note':
        						return '通知公告';
        						break;
        					case 'kpi':
        						return '考勤提醒';
        						break;
        					case 'meeting':
        						return '会议';
        						break;
        					case 'process':
        						return '审批';
        						break;
        					case 'task':
        						return '任务';
        						break;
        					case 'job':
        						return '稽核提醒';
        						break;
        					case 'b2b':
        						return 'B2B提醒';
        						break;
        					case 'ptzh':
        						return '普通知会';
        						break;	
        				}
        			
        			}},
					{
						xtype : 'displayfield',
						fieldLabel: '内容',
						labelAlign:'left',
						cls:'Wincontext',
        				name: '',
        				labelWidth:70,
        				value:context ,
        				//value:'<div style="height:250px;width:385px;margin:0;text-align:left;border:1px solid #A3A3A3;">'+context+'</div>' 
						renderer: function (v) {
							//return '<div style="height:150px;width:336px;margin:0;text-align:left;border:1px solid #A3A3A3;">'+context.replace(/javascript:openUrl/g,"javascript:openTmpUrl").replace(/openMessageUrl/g,'openTmpMessageUrl')+'</div>'							
							return '<div style="height:150px;width:336px;margin:0;text-align:left;border:1px solid #A3A3A3;">'+context.replace(/javascript:openUrl\(/g,"javascript:openTmpUrl('',").replace(/javascript:parent.openUrl\(/g,"javascript:openTmpUrl('patrnt',").replace(/openMessageUrl/g,'openTmpMessageUrl')+'</div>' 
						
						}
					}],
		buttons:[
			{
				xtype:'button',
				text:'<div style="color: white !important">确定</div>',
				align:'center',
				height:30,
				cls:'readbutton',			
				handler:function(v){
					var mywindows = Ext.getCmp('mywindows');
					v.ownerCt.ownerCt.ownerCt.close();
				}		
		}]							
		}]		
		 });
		 windows.show();		 
		var gridLength=grid.store.data.items.length;
		var messageredpoint=parent.document.getElementById("messageredpoint");
		if(record.data.IHD_READSTATUS==0){
			this.updateReadstatus(str);
			if(gridLength<=1){
				messageredpoint.style.display = 'none';
			}else{
				messageredpoint.style.display = 'block';
			}
		}
    	}else{
    		var call=record.data.IHD_RECEIVE;
    		var windows= Ext.create('Ext.window.Window', {   
     		x: Ext.getBody().getWidth()/2-200, 
			y: Ext.getBody().getHeight()/2-200,
     		//heigth:380,
			modal:true,
     		width:500,
     		id:'mywindows',
     		CURRENTMASTER:CURRENTMASTER,
     		closable:false,
     		border: false,
     		frame:false,
     		resizable :false,
     		header: false,
     		draggable: false,
     		cls:'Windetail',
			items:[
				{
					xtype : 'tbtext',
					text : '<div style="text-align:center;color:#1B79E5;font-weight:normal;font-size: 20px;">信息详情</div>',
					cls:'Wintitle'
				},
				{
				xtype:'panel',
				heigth:900,
				border:false,
				buttonAlign :'center',
				id:'paneldetail',
				items:[
					{
						xtype : 'displayfield',
						fieldLabel: '接收人',
						value:call,
						labelAlign:'left',
						labelWidth:70,
						//cls:'Wincontext'
						cls:'Wincontext'
					},
					{
						xtype : 'displayfield',
						fieldLabel: '接收时间',
						value:data,
						labelAlign:'left',
						labelWidth:70,
						cls:'Wincontext'
					},
					{xtype : 'displayfield',
						//text : '消息分类:'+from,
						cls:'Wincontext',
						labelAlign:'left',
						labelWidth:70,
						fieldLabel: '消息分类',
						value:from,
						renderer: function (v) {
        				switch(v){
        					case 'system':
        						return '知会消息';
        						break;
        					case 'crm':
        						return 'CRM提醒';
        						break;
        					case 'note':
        						return '通知公告';
        						break;
        					case 'kpi':
        						return '考勤提醒';
        						break;
        					case 'meeting':
        						return '会议';
        						break;
        					case 'process':
        						return '审批';
        						break;
        					case 'task':
        						return '任务';
        						break;
        					case 'job':
        						return '稽核提醒';
        						break;
        					case 'b2b':
        						return 'B2B提醒';
        						break;
        					case '':
        						return '普通知会';
        						break;	
        				}
        			
        			}},
					{
						xtype : 'displayfield',
						fieldLabel: '内容',
						labelAlign:'left',
						cls:'Wincontext',
        				name: '',
        				labelWidth:70,
        				value:context ,
        				//value:'<div style="height:250px;width:385px;margin:0;text-align:left;border:1px solid #A3A3A3;">'+context+'</div>' 
						renderer: function (v) {
							return '<div style="height:150px;width:336px;margin:0;text-align:left;border:1px solid #A3A3A3;">'+context.replace(/javascript:openUrl\(/g,"javascript:openTmpUrl('',").replace(/javascript:parent.openUrl\(/g,"javascript:openTmpUrl('patrnt',").replace(/openMessageUrl/g,'openTmpMessageUrl')+'</div>' 
						}
					}],
			buttons:[{
				xtype:'button',
				text:'<div style="color: white !important">确定</div>',
				cls:'readbutton',
				height:30,
				//cls:'Wincontext',
				handler:function(v){
					v.ownerCt.ownerCt.ownerCt.close();
				}		
		}]			
			}]
		 });
		 windows.show();
    	}
    	
    },
    updateReadstatus:function(data){
    	var me = this;
    	   Ext.Ajax.request({
                url: basePath + "common/updateReadstatus.action",
                params: {
                    data: data     
                },
                method: 'post',
                callback: function(options, success, response) {
                    var res = Ext.decode(response.responseText); 
            		if (res.exceptionInfo) {
						showError(res.exceptionInfo);
						return;
					}				
                    if (res.success){ 
                    var grid = Ext.getCmp('informationgrid');
                    grid.store.remoteFilter=true;
                	grid.store.loadPage(1,{
						callback:me.callbackFn
					});	
                    }
               }
            });
    },
    reconfigureGrid:function(grid){
    	var me = this;
		var group = me.getGroup();
		grid.reconfigureColumn(group); 
    },
    callbackFn:function(options,response,success){
		var res = Ext.decode(response.response.responseText);
		if(res.success){
			var form = Ext.getCmp('informationform');
			var statBtns = form.query('erpStatButton');
			Ext.Array.each(statBtns,function(btn){
				
					if(res.count[btn.type]>=0){
						var count = res.count[btn.type];
						btn.setStat(count);
						if(count>99){
							btn.setTooltip(count);
						}					
					}					
				
			});
		}else if(res.exceptionInfo){
			showError(res.exceptionInfo);
		}
		var a = Ext.get('informationgrid').select('a');
		var elements = a.elements;
		for (var i = 0; i < elements.length; i++) {
			elements[i].onclick = function() {
				var informationgrid = Ext.getCmp('informationgrid');
				informationgrid.flag = 1;
			}
		}
	},
//wyx 筛选触发后台筛选并载入数据进grid
	headerfiltersapplyFn: function() {
		var me = this;
		var form = Ext.getCmp('informationform');
		var grid = Ext.getCmp('informationgrid');
		var filters = grid.gridFilters;
		var condition = me.getCondition();
		var likestr = me.getLikeStr(grid, form, filters);
		//设置将所有的过滤操作推迟到服务器
		grid.store.remoteFilter=true;
		
	},
	getLikeStr: function(grid, form, filters) {
		var likestr = '';
		var me = this;
		for(var fn in filters) {
			var value = filters[fn],
				f = grid.getHeaderFilterField(fn);
			if(!Ext.isEmpty(value)) {
				if("null" != value) {
					if(f.originalxtype == 'numberfield') {
						if(value.indexOf('>=') == 0 || value.indexOf('<=') == 0 || value.indexOf('>') == 0 || value.indexOf('<') == 0 || value.indexOf('!=') == 0 || value.indexOf('=') == 0) {
							if(value.indexOf('!=') == 0) {
								value = "(" + fn + value + " or " + fn + " is null) ";
							} else {
								value = fn + value + " ";
							}
						} else if(value.indexOf('~') > -1) {
							var arr = value.split('~');
							value = fn + " between " + arr[0] + " and " + arr[1] + " ";
						} else {
							value = fn + "=" + value + " ";
						}
					} else if(f.originalxtype == 'datefield') {
						if(value.indexOf('=') > -1) {
							var valueX = value.split('=')[1];
							var length = valueX.split('-').length;
							if(length < 3) {
								if(length == 1) {
									var value1 = Ext.Date.toString(new Date(valueX + '-01-01'));
									var value2 = Ext.Date.toString(new Date(valueX + '-12-31'));
									value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '" + value2 + "'";
								} else if(length == 2) {
									var day = new Date(valueX.split('-')[0], valueX.split('-')[1], 0);
									var value1 = Ext.Date.toString(new Date(valueX + '-01'));
									var value2 = Ext.Date.toString(new Date(valueX + '-' + day.getDate()));
									value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '" + value2 + "'";
								}
							} else {
								if(value.indexOf('>=') == 0) {
									value = Ext.Date.toString(new Date(valueX));
									value = "to_char(" + fn + ",'yyyy-MM-dd')>='" + value + "' ";
								} else if(value.indexOf('<=') == 0) {
									value = Ext.Date.toString(new Date(valueX));
									value = "to_char(" + fn + ",'yyyy-MM-dd')<='" + value + "' ";
								} else {
									value = Ext.Date.toString(new Date(valueX));
									value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
								}
							}
						} else if(value.indexOf('~') > -1) {
							var value1 = Ext.Date.toString(new Date(value.split('~')[0]));
							var value2 = Ext.Date.toString(new Date(value.split('~')[1]));
							value = "to_char(" + fn + ",'yyyy-MM-dd') between '" + value1 + "' and '" + value2 + "'";
						} else {
							value = Ext.Date.toString(new Date(value));
							value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
						}
					} else {
						var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
							exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
						if(exp_d.test(value)) {
							value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
						} else if(exp_t.test(value)) {
							value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
						} else {
							if(f.xtype == 'combo' || f.xtype == 'combofield') {
								if(value == '-所有-') {
									value = ' 1=1 ';
								} else if(value == 'ptzh' && fn == 'IH_FROM') {
									value = fn + ' is null ';
								} else {
									if(f.column && f.column.xtype == 'yncolumn') {
										if(value == '-无-') {
											value = fn + ' is null ';
										} else {
											value = fn + ((value == '是' || value == '-1' || value == '1') ? '<>0' : '=0');
										}
									} else {
										if(value == 'none') {
											value = 'nvl(to_char(' + fn + '),\' \')=\' \'';
										} else {
											if(value) value = value.replace(/\'/g, "''");
											value = fn + " LIKE '" + value + "%' ";
										}
									}
								}
							} else if(f.xtype == 'datefield') {
								value = "to_char(" + fn + ",'yyyy-MM-dd') like '%" + value + "%' ";
							} else if(f.column && f.column.xtype == 'numbercolumn') {
								if(f.column.format) {
									var precision = f.column.format.substr(f.column.format.indexOf('.') + 1).length;
									//防止to_char去除小数点前面的0
									if(-1 < value && value < 1) {
										var number = value;
										value = "to_char(round(" + fn + "," + precision + "),";
										value += "'fm0.";
										for(var i = 0; i < precision; i++) {
											value += "0";
										}
										value += "') like '%" + number + "%' ";
									} else {
										value = "to_char(round(" + fn + "," + precision + ")) like '%" + value + "%' ";
									}
								} else
									value = "to_char(" + fn + ") like '%" + value + "%' ";
							} else {
								/**字符串转换下简体*/
								if(value) value = value.replace(/\'/g, "''");
								var SimplizedValue = me.BaseUtil.Simplized(value);
								//可能就是按繁体筛选  
								if(f.ignoreCase) { // 忽略大小写
									fn = 'upper(' + fn + ')';
									value = value.toUpperCase();
								}
								if(!f.autoDim) {
									if(SimplizedValue != value) {
										value = "(" + fn + " LIKE '" + value + "%' or " + fn + " LIKE '" + SimplizedValue + "%')";
									} else value = fn + " LIKE '" + value + "%' ";

								} else if(f.filterSelect || f.inputEl.dom.disabled || (f.rawValue == '' && f.emptyText == value)) {
									if(f.filterType == 'direct') {
										value = fn + "='" + value + "'";
									} else if(f.filterType == 'nodirect') {
										value = "nvl(" + fn + ",' ')<>'" + value + "'";
									} else if(f.filterType == 'head') {
										value = fn + " LIKE '" + value + "%' ";
									} else if(f.filterType == 'end') {
										value = fn + " LIKE '%" + value + "' ";
									} else if(f.filterType == 'null') {
										value = fn + " is null";
									} else if(f.filterType == 'novague') {
										if(SimplizedValue != value) {
											value = "(" + fn + " not LIKE '%" + value + "%' and " + fn + " not LIKE '%" + SimplizedValue + "%' or " + fn + " is null)";
										} else value = "(" + fn + " not LIKE '%" + value + "%' or " + fn + " is null)";
									} else {
										if(SimplizedValue != value) {
											value = "(" + fn + " LIKE '%" + value + "%' or " + fn + " LIKE '%" + SimplizedValue + "%')";
										} else value = fn + " LIKE '%" + value + "%' ";
										f.filterType = '';
									}
									f.filterSelect = false;
								} else {
									if(SimplizedValue != value) {
										value = "(" + fn + " LIKE '%" + value + "%' or " + fn + " LIKE '%" + SimplizedValue + "%')";
									} else value = fn + " LIKE '%" + value + "%' ";
									f.filterType = '';
								}
							}
						}
					}
				} else value = "nvl(" + fn + ",' ')=' '";
				if(likestr == '') {
					likestr = value;
				} else {
					likestr = likestr + " and " + value;
				}

			}
		}
		form.likestr = likestr;
		return likestr;
	}
});
