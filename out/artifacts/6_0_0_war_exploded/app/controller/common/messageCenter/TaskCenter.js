Ext.QuickTips.init();
Ext.define('erp.controller.common.messageCenter.TaskCenter', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:['common.messageCenter.TaskCenter','common.messageCenter.TaskCenterFormPanel','common.messageCenter.TaskCenterGridPanel','common.messageCenter.InformationForm','core.button.StatButton','core.button.SwitchButton','core.form.BtnDateField'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpTaskCenterGridPanel':{
    			afterrender:function(grid){
    				me.reconfigureGrid(grid);						
					me.loadGridData();
    			},
    			'headerfiltersapply': function() {//触发筛选时调用函数
					me.headerfiltersapplyFn();
				}
    		},
    		'erpTaskCenterFormPanel':{
    			afterrender:function(form){
    				var tab = me.FormUtil.getActiveTab();
    				
					var condition = me.getCondition();
    				
    				//me.getDatas(condition);       				
    				
					me.onActivateEvent(tab,condition); //绑定onactivate事件
					
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
    		'erpStatButton':{
				click:function(btn){					
					var grid = Ext.getCmp('taskGrid');
					me.reconfigureGrid(grid);
					me.loadGridData();						
					if(btn.groupName=='taskType'){
						if(btn.getCount()>0){
							var sceneBtns = Ext.getCmp('scene');
							var statusBtns = Ext.getCmp('status');
							var activeSceneBtn = sceneBtns.getActive();
							var activeStatusBtn = statusBtns.getActive();
							if(activeSceneBtn.id!='myTask'){
								sceneBtns.setActive(Ext.getCmp('myTask'));
							}
							if(activeStatusBtn.id!='doing'){
								statusBtns.setActive(Ext.getCmp('doing'));
							}
							
							me.reconfigureGrid(grid);
							
							me.loadGridData();
						}
					}
				}
    		},
    		'button[id=addTaskBtn]':{
    			click: function(b){
		        	var win = Ext.create('erp.view.core.window.Task');
		        	win.show();				
    			}
    		},
    		'displayfield[id=msgNotice]':{
				afterrender:function(dfield){
					var fieldDom = dfield.el.dom;
					var form = Ext.getCmp('centerForm');
					
					//dfield.el.setX((document.body.clientWidth/2) - 128 );
					dfield.el.setX((form.getWidth()/2) - 128 ); //定位到form的中心
					dfield.el.setY(-2);
					var grid = Ext.getCmp('taskGrid');
					fieldDom.onclick = function(){
						var msgNotice = Ext.getCmp('msgNotice');
						msgNotice.hidden = true;
						Ext.getCmp('msgNotice').el.slideOut('t', { duration: 0 });
						
						grid.store.load({
							callback:me.callbackFn
						});
					}
				}        			
    		}
    	});
    },
    reconfigureGrid:function(grid){
    	var me = this;
		var group = me.getGroup();
		grid.reconfigureColumn(group);       	
    },
    getGroup:function(){
    	var group = '';
		var form = Ext.getCmp('centerForm');
		var switchButtons = form.query('erpSwitchButton');					
		if(switchButtons.length>0){
			Ext.Array.each(switchButtons,function(item){
					group += '&' + item.activeButton.id;	
			});							
		}
		return group.substring(1);        	
    },
    loadGridData:function(){
    	var grid = Ext.getCmp('taskGrid');
    	var me = this;
		var condition = me.getCondition();		
		grid.store.loadPage(1,{
			callback:me.callbackFn
		});			
    },
    callbackFn:function(options,response,success){
		var res = Ext.decode(response.response.responseText);
		if(res.success){
			var normalTaskBtn = Ext.getCmp('normalTask');						
			var projectTaskBtn = Ext.getCmp('projectTask');
			var grid = Ext.getCmp('taskGrid');
			console.log(res);
			normalTaskBtn.setStat(res.count.normalTaskCount);
			projectTaskBtn.setStat(res.count.projectTaskCount);
			if(res.count.normalTaskCount>99){
				normalTaskBtn.setTooltip(res.count.normalTaskCount);
			}				
			if(res.count.projectTaskCount>99){
				projectTaskBtn.setTooltip(res.count.projectTaskCount);
			}	
			//grid.store.loadData(res.data);
		}else if(res.exceptionInfo){
			showError(res.exceptionInfo);
		}
	},
    getCondition:function(){
    	var condition = '';
		var form = Ext.getCmp('centerForm');
		var switchButtons = form.query('erpSwitchButton');
		var activeStatusBtn = Ext.getCmp('status').getActive()
		if(activeStatusBtn.id=='doing'){
			activeStatusBtn.condition = "ra_taskpercentdone<100 AND nvl(ra_statuscode,' ')<>'ENDED' and nvl(ra_statuscode,' ')<>'UNACTIVE' or (recorderid="+emuu+" and ra_statuscode='UNCONFIRMED') and " +  Ext.getCmp('type').activeButton.condition;
		}
		if(switchButtons.length>0){
			var condition = '';
				Ext.Array.each(switchButtons,function(item){
					condition += " and " + item.activeButton.condition;	
			});							
		}
		condition = condition.substring(5);  
		var form = Ext.getCmp('centerForm');
		form.defaultCondition = condition;
		return condition;
    },
    showTip:function(){
		var df = Ext.getCmp('msgNotice');
		if(df.hidden){
			df.hidden = false;
			df.el.slideIn('t', { duration: 2000 });
		}
    },
    getDatas:function(condition){
    	var grid = Ext.getCmp('taskGrid');
		Ext.Ajax.request({
			url:basePath + 'common/getTaskData.action',
			method:'post',
			params:{
				condition:condition
//					page:1,
//					limit:grid.store.pageSize
			},
			callback:function(options,success,response){
				var res = Ext.decode(response.responseText);
				if(res.success){
					var normalTaskBtn = Ext.getCmp('normalTask');						
					var projectTaskBtn = Ext.getCmp('projectTask');
					if(res.count.normalTaskCount>99){
						normalTaskBtn.setTooltip(res.count.normalTaskCount);
					}	
					normalTaskBtn.setStat(res.count.normalTaskCount);
					projectTaskBtn.setStat(res.count.projectTaskCount);
					if(res.count.projectTaskCount>99){
						projectTaskBtn.setTooltip(res.count.projectTaskCount);
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);
				}
			}
		});        	
    },
    onActivateEvent:function(tab,condition){
    	var me = this;
    	var grid = Ext.getCmp('taskGrid');
		tab.on('activate',function(tab){
			//进行刷新				
			Ext.getCmp('taskGrid').store.loadPage(1,{
				callback:me.callbackFn
			});
		});        	
    },
    // 筛选触发后台筛选并载入数据进grid
	headerfiltersapplyFn: function() {
		var me = this;
		var form = Ext.getCmp('centerForm');
		var grid = Ext.getCmp('taskGrid');
		var filters = grid.gridFilters;
		var condition = me.getCondition();
		var likestr = me.getLikeStr(grid, form, filters);
		//设置将所有的过滤操作推迟到服务器
		grid.store.remoteFilter=true;
		grid.store.loadPage(1,{
			callback:me.callbackFn//第二次调用主要是更新form
		});	
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