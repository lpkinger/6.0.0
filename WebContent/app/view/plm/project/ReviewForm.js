Ext.define('erp.view.plm.project.ReviewForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.ReviewPanel',
	id: 'form', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       focusCls: 'x-form-field-cir',
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	saveUrl: '',
	updateUrl: '',
	deleteUrl: '',
	auditUrl: '',
	resAuditUrl: '',
	submitUrl: '',
	resSubmitUrl: '',
	bannedUrl: '',
	resBannedUrl: '',
	postUrl:'',
	printUrl: '',
	getIdUrl: '',
	keyField: '',
	codeField: '',
	statusField: '',
	params: null,
	caller: null,
	Contextvalue:null,
	LastValue:null,
	enableTools: true,
	enableKeyEvents: true,
	//固定分组
	base_group:['erpAddButton','erpUpdateButton','erpDeleteButton','erpSaveButton','erpCopyButton','erpExecuteOperationButton','erpQueryButton'],
	logic_group:['erpSubmitButton','erpResSubmitButton','erpAuditButton','erpResAuditButton','erpEndButton','erpResEndButton',
	             'erpAccountedButton','erpResAccountedButton','erpPostButton','erpResPostButton','erpBannedButton','erpResBannedButton',
	             'erpForBiddenButton','erpResForBiddenButton','erpAutoInvoiceButton','erpCheckButton','erpResCheckButton','erpVoCreateButton',
	             'erpFreezeButton','erpNullifyButton','erpResAbateButton','erpAbateButton','erpModifyCommonButton'],
	work_group:['erpExportExcelButton','erpImportExcelButton'],
	close_group:['erpCloseButton'],
	turn_group:['erpConsignButton'],
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	var param = {caller: this.caller || caller, condition: formCondition};
    	this.getItemsAndButtons(this, 'common/singleFormItems.action', this.params || param);//从后台拿到formpanel的items
		this.callParent(arguments);
		//加prev、next、refresh等tool
		if(this.enableTools) {
			/*this.setTools();*/
		}
		//给页面加上ctrl+alt+s键盘事件,自动跳转form配置界面
		if(this.enableKeyEvents) {
			this.addKeyBoardEvents();
		}		
	},
	getItemsAndButtons: function(form, url, param){
		var me = this;
		me.FormUtil.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		me.FormUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		form.fo_id = res.fo_id;
        		form.fo_keyField = res.fo_keyField;
        		form.tablename = res.tablename;//表名
        		if(res.keyField){//主键
        			form.keyField = res.keyField;
        		}
        		if(res.statusField){//状态
        			form.statusField = res.statusField;
        		}
        		if(res.statuscodeField){//状态码
        			form.statuscodeField = res.statuscodeField;
        		}
        		if(res.codeField){//Code
        			form.codeField = res.codeField;
        		}
        		form.fo_detailMainKeyField = res.fo_detailMainKeyField;//从表外键字段
        		var grids = Ext.ComponentQuery.query('gridpanel');
        		//如果该页面只有一个form，而且form字段少于8个，则布局改变
        		if(grids.length == 0 && res.items.length <= 8){
        			Ext.each(res.items, function(item){
        				item.columnWidth = 0.5;
        			});
        			form.layout = 'column';
    			}
        		//data&items
				var items = me.setItems(form, res.items, res.data, res.limits, {
					labelColor: res.necessaryFieldColor
				});
        		form.add(items);     	
        		//title
        		if(res.title && res.title != ''){
        			/*form.setTitle(res.title);*/
        		}
        		//解析buttons
        		me.FormUtil.setButtons(form, res.buttons);
        		//form第一个可编辑框自动focus
        		me.FormUtil.focusFirst(form);
        	}
        });
	},
	setItemWidth: function(form, items) {
		var grids = Ext.ComponentQuery.query('gridpanel');
		if(!form.fixedlayout && !form.minMode && grids.length == 1 && 
				form.detailpercent && form.mainpercent && form.detailpercent>0 && form.mainpercent>0 && (form.detailpercent+form.mainpercent)==100){			
			form.anchor='100% '+form.mainpercent+'%';
			grids[0].anchor='100% '+form.detailpercent+'%';
			if(form.ownerCt && form.ownerCt.ownerCt && form.ownerCt.ownerCt.fireResize)form.ownerCt.ownerCt.fireResize();
			if(form.ownerCt && form.ownerCt.fireResize)form.ownerCt.fireResize();
		}
		var formWidth = window.innerWidth, maxSize = 0.097 * formWidth,
			// 宽屏
			wide = screen.width > 1280,
			// 如果该页面只有一个form，而且form字段少于8个
			sm = (!form.fixedlayout && !form.minMode && grids.length == 0 && items.length <= 8),
			// 如果该页面字段过多
			lg = items.length > maxSize;
		Ext.each(items, function(item){
			if(sm) {
				item.columnWidth = 0.5;
			} else if(lg) {
				// 4.0.7版本下必须使用固定宽度
				//新UI暂时不用
				item.width = formWidth*(item.columnWidth);  /*- item.columnWidth*4*10*/
			} else if(form.minMode) {// 布局里面设置为minMode模式
				if(item.columnWidth >= 0 && item.columnWidth < 0.5){
					item.columnWidth = 0.5;
				} else if(item.columnWidth >= 0.5) {
					item.columnWidth = 1;
				}
			} else {
				if(wide) {
					if(item.columnWidth > 0.25 && item.columnWidth < 0.5){
						item.columnWidth = 1/3;
					} else if(item.columnWidth > 0.5 && item.columnWidth < 0.75){
						item.columnWidth = 2/3;
					}
				} else {
					if(item.columnWidth > 0 && item.columnWidth <= 0.25){
						item.columnWidth = 1/3;
					} else if(item.columnWidth > 0.25 && item.columnWidth <= 0.5){
						item.columnWidth = 2/3;
					} else if(item.columnWidth >= 1){
						item.columnWidth = 1;
					}
				}
			}
		});
		if(sm) {
			form.layout = 'column';
		}
	},
	/**
	 * @param necessaryCss 必填项样式
	 */
	setItems: function(form, items, data, limits, necessaryCss){
		var me = this,edit = !form.readOnly,hasData = true,limitArr = new Array();
		if(limits != null && limits.length > 0) {//权限外字段
			limitArr = Ext.Array.pluck(limits, 'lf_field');
		}
		if (data) {
			data = Ext.decode(data);
			if(form.statuscodeField && data[form.statuscodeField] != null && data[form.statuscodeField] != '' &&  
					['ENTERING', 'UNAUDIT', 'UNPOST', 'CANUSE'].indexOf(data[form.statuscodeField]) == -1){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			}
			if(form.statusCode && data[form.statusCode] == 'POSTED'){//存在单据状态  并且单据状态不等于空 并且 单据状态等于已过账
				form.readOnly = true;
				edit = false;
			}
		} else {
			hasData = false;
		}
		me.setItemWidth(form, items);
		
		Ext.each(items, function(item){
			if(item.labelAlign&&item.labelAlign!='top'){
				item.labelAlign = 'right';
			}
			if(item.labelAlign=='top'&&item.columnWidth>=1){
				item.margin = '3 0 3 30';
			}
			if(item.group == '0'){
				item.margin = '7 0 0 0';
			}
			//基本背景颜色
			item.labelStyle = "color:#1e1e1e";
			item.fieldStyle = 'background:#fff;color:#313131;';
			if(item.xtype == 'textareafield'){
				item.grow=true;
				item.growMax=300;
			}
			if(!item.allowBlank && item.fieldLabel && necessaryCss.labelColor) {//必填
				/*item.labelStyle = 'color:#' + necessaryCss.labelColor;*/
				item.fieldLabel = "<font color=\"red\" style=\"position:relative; top:2px;right:2px; font-weight: bolder;\">*</font>"+item.fieldLabel;
				if(item.xtype=='mfilefield'){//附件类型必填设置title颜色
					 this.title = '<font color=#'+necessaryCss.labelColor+'>'+this.title|| '附件'+'</font>' ;
				}
			}
			if(item.readOnly) {
				item.fieldStyle = 'background:#f3f3f3;';
			}
						
			if(item.name != null) {
				if(item.name == form.statusField){//状态加特殊颜色
					item.fieldStyle = item.fieldStyle + ';font-weight:bold;';
				} else if(item.name == form.statuscodeField){//状态码字段强制隐藏
					item.xtype = 'hidden';
				}
			}
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
				item.width = 0;
				item.margin = '0';
			}
			if(item.xtype == 'checkbox') {
				item.fieldStyle = '';
		        item.margin = '3 0 3 80';
		        if(item.columnWidth<0.25){
					 item.margin = '3 0 3 0';
				}
				item.focusCls = '';
			}
			if(item.maskRe!=null){
				item.maskRe=new RegExp(item.regex);
			}
			if (hasData) {
				item.value = data[item.name];
				if(item.secondname){//针对合并型的字段MultiField
					item.secondvalue = data[item.secondname];
				}
				if(!edit){
					form.readOnly = true;
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				} 
				if(item.xtype == 'checkbox'){
					item.checked = Math.abs(item.value || 0) == 1;
					item.fieldStyle = '';
					item.margin = '3 0 3 80';
					if(item.columnWidth<0.25){
						 item.margin = '3 0 3 0';
					}
				}
			}else{
				if(form.source=='allnavigation'){
					item.fieldStyle = item.fieldStyle + ';background:#f1f1f1;';
					item.readOnly = true;
				}
			}
			if(limitArr.length > 0 && Ext.Array.contains(limitArr, item.name)) {
				item.hidden = true;
			}
			if(item.renderfn){
				var args = new Array();
    			var arr = item.renderfn.split(':');
    			//hey start 主表字段背景颜色
    			if(arr&&arr[0]!='itemstyle'){//判断是否是itemstyle
    				if(contains(item.renderfn, ':', true)){	    		
    					Ext.each(item.renderfn.split(':'), function(a, index){
    						if(index == 0){
    							renderName = a;
    						} else {
    							args.push(a);
    						}
    					});
    				} else {renderName=item.renderfn;}
    				me[renderName](item, args, form);
    			}
    			else{
    		    	switch(arr.length)
    		    	{
    		    		case 2:						
    		    			if(data&&data[item.name]&&data[item.name]==arr[1]) item.fieldStyle = item.fieldStyle + ';background:#c0c0c0;';
    		    			break;
    		    		case 3:	 
    		    			if(data&&data[item.name]&&data[item.name]==arr[1]) item.fieldStyle = item.fieldStyle + ';background:'+arr[2]+';';
    		    			break;
    		    		default:
    		    	}	
    			}
    			//hey end 主表字段背景颜色
			}
		});
		return items;
	},
	/**
	 * FormHeader Tools
	 * 包括:查看日志、查看流程、查看列表、最大化、最小化、刷新、关闭、上一条、下一条
	 */
	setTools: function(){
		var datalistId = getUrlParam('datalistId');
		if(datalistId){
			this.tools = [{
				type: 'search',
				tooltip: '查看单据日志',
				listeners:{
					click: function(btn){
						var form = btn.up('form');
						var id = Ext.getCmp(form.keyField).value;
						if(id != null && id != 0){
							form.getLogs(id);
						}
					}
				}
			},{
				type: 'save',
				tooltip: '导出Excel',
				listeners:{
					click: function(btn){
						var form = btn.ownerCt.ownerCt;
						var id = Ext.getCmp(form.keyField).value;
						form.saveAsExcel(id,caller);
					}
				}
			},{
				type: 'expand',
				tooltip: '查看流程处理',
				listeners:{
					click: function(btn){
						var form = btn.ownerCt.ownerCt;
						if(!form.statuscodeField){
							btn.disable(true);
						} else {
							var f = form.statuscodeField;
							if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
								btn.disable(true);
							} else {
								var id = Ext.getCmp(form.keyField).value;
								if(id != null && id != 0){
									form.getProcess(id);
								}
							}
						}
					}
				}
			},{
				type: 'gear',
				tooltip: '设置流程处理人',
				listeners:{
					click: function(btn){
						var form = btn.ownerCt.ownerCt;
						if(!form.statuscodeField){
							btn.disable(true);
						} else {
							var f = form.statuscodeField;
							if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
								btn.disable(true);
							} else {
								var id = Ext.getCmp(form.keyField).value;
								if(id != null && id != 0){
									form.SetNodeDealMan(id);
								}
							}
						}
					}
				}
			},{
				type: 'collapse',
				tooltip: '查看列表',
				listeners:{
					click: function(btn){
						var datalist = parent.Ext.getCmp(datalistId);
			    		if(!datalist){
			    			var form = btn.ownerCt.ownerCt;
			    			var url = 'jsps/common/datalist.jsp?whoami=' + caller;
			    			if(btn.urlcondition){
			    				url += '&urlcondition=' + btn.urlcondition;
			    			}
			    			form.FormUtil.onAdd(caller + '_scan', parent.Ext.getCmp('content-panel').getActiveTab().title + 'DataList', url);
			    		} else {
			    			datalist.ownerCt.setActiveTab(datalist);
			    		}
					}
				}
			},{
				type: 'maximize',
				tooltip: '最大化',
				listeners:{
					render: function(btn){
						var datalist = parent.Ext.getCmp(datalistId);
			    		if(!datalist){
			    			btn.disable(true);
			    		}
					},
					click: function(btn){
						var height = window.screen.height*0.87;
						var width = window.screen.width;
						//弹出框显示，可以锁定住地址栏和工具栏，防止用户不合理操作
						window.open(window.location.href, '', 'width=' + width + ',height=' + height + ',top=0,left=0,toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
					}
				}
			},{
				type: 'minus',
				tooltip: '最小化',
				listeners:{
					render: function(btn){
						var datalist = parent.Ext.getCmp(datalistId);
			    		if(!datalist){
			    			btn.disable(true);
			    		}
					},
					click: function(btn){
						var p = parent.Ext.getCmp('content-panel');
						if(p){
							var t = p.getActiveTab();
							var b = parent.Ext.getCmp('bottom');
							if(b){
								b.insert(1, {
									text: t.title,
									tooltip: t.tabConfig.tooltip,
									tab: t,
									handler: function(btn){
										p.add(btn.tab);
										var a = p.add(btn.tab); 
							    		p.setActiveTab(a);
							    		btn.destroy();
									}
								});
								p.remove(t, false);//并不销毁
							}
						}
					}
				}
			},{
				type: 'refresh',
				tooltip: '刷新',
				listeners:{
					click: function(btn){
						window.location.href = window.location.href;
					}
				}
			},{
				type: 'close',
				tooltip: '关闭',
				listeners:{
					click: function(btn){
						var p = parent.Ext.getCmp('content-panel');
						if(p){
							p.getActiveTab().close();
						} else {
							window.close();
						}
					}
				}
			},{
			    type:'prev',
			    id: 'prev',
			    tooltip: '上一条',
			    listeners:{
			    	render: function(btn){
			    		var datalist = parent.Ext.getCmp(datalistId);
			    		if(datalist){
			    			var datalistStore = datalist.currentStore;
					    	Ext.each(datalistStore, function(){
					    		if(this.selected == true){
					    			if(this.prev == null){
					    				btn.disable(true);
					    			}
					    		}
					    	});
			    		} else {
			    			btn.disable(true);
			    		}
			    	},
			    	click: function(btn){
			    		var datalist = parent.Ext.getCmp(datalistId);
			    		if(datalist){
			    			var datalistStore = datalist.currentStore;
					    	var form = btn.up('form');
					    	var newId = 0;
					    	var idx = 0;
					    	Ext.each(datalistStore, function(s, index){
					    		if(this.selected == true){
					    			if(this.prev != null){
					    				newId = this.prev;
					    				idx = index;
					    			}
					    		}
					    	});
					    	form.FormUtil.loadNewStore(form, {caller: caller, condition: form.keyField + "=" + newId});
					    	var grid = Ext.getCmp('grid');
					    	if(grid){
					    		grid.GridUtil.loadNewStore(grid, {caller: caller, condition: grid.mainField + "=" + newId});
					    	}
					    	datalistStore[idx].selected = false;
		    				datalistStore[idx-1].selected = true;
		    				if(datalistStore[idx-1].prev == null){
		    					btn.disable(true);
		    				} else {
		    					btn.setDisabled(false);
		    				}
		    				Ext.getCmp('next').setDisabled(false);
			    		}
			    	}
			    }
			},{
			    type: 'next',
			    id: 'next',
			    tooltip: '下一条',
			    listeners:{
			    	render: function(btn){
			    		var datalist = parent.Ext.getCmp(datalistId);
			    		if(datalist){
			    			var datalistStore = datalist.currentStore;
					    	Ext.each(datalistStore, function(){
					    		if(this.selected == true){
					    			if(this.next == null){
					    				btn.disable(true);
					    			}
					    		}
					    	});
			    		} else {
			    			btn.disable(true);
			    		}
			    	},
			    	click: function(btn){
			    		var datalist = parent.Ext.getCmp(datalistId);
			    		if(datalist){
			    			var datalistStore = datalist.currentStore;
					    	var form = btn.up('form');
					    	var newId = 0;
					    	var idx = 0;
					    	Ext.each(datalistStore, function(s, index){
					    		if(s.selected == true){
					    			if(s.next != null){
					    				newId = s.next;
					    				idx = index;
					    			}
					    		}
					    	});
					    	form.FormUtil.loadNewStore(form, {caller: caller, condition: form.keyField + "=" + newId});
					    	var grid = Ext.getCmp('grid');
					    	if(grid){
					    		grid.GridUtil.loadNewStore(grid, {caller: caller, condition: grid.mainField + "=" + newId});
					    	}
					    	datalistStore[idx].selected = false;
		    				datalistStore[idx+1].selected = true;
		    				if(datalistStore[idx+1].next == null){
		    					btn.disable(true);
		    				} else {
		    					btn.setDisabled(false);
		    				}
		    				Ext.getCmp('prev').setDisabled(false);
			    		}
			    	}
			    }
			}];
		}
	},
	saveAsExcel:function(id,caller){
		window.location.href=basePath+'excel/savePanelAsExcel.action?id='+id+"&caller="+caller+"&_noc=1";
	},
	/**
	 * 监听一些事件,
	 * 如Ctrl+Alt+S
	 */
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					if(Ext.ComponentQuery.query('gridpanel').length > 0){//有grid
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
					} else {
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id);
					}
				}
			});
			document.body.attachEvent("onmouseover", function(){
				if(window.event.ctrlKey){
					var e = window.event;
					me.Contextvalue = e.target.textContent == "" ? e.target.value : e.target.textContent;
					textarea_text = parent.document.getElementById("textarea_text");
					textarea_text.value = me.Contextvalue;
						textarea_text.focus();
						textarea_text.select();
					}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					if(Ext.ComponentQuery.query('gridpanel').length > 0){//有grid
						var forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [],gridSet = [];
						if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
						};
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
					} else {
						me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + 
								"&gridCondition=fd_foidIS" + me.fo_id);
					}
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				} 
			});
			document.body.addEventListener("mouseover", function(e){
				if(Ext.isFF5){
					e = e || window.event;
				}
				if(e.ctrlKey){
					me.Contextvalue = e.target.textContent == "" ? e.target.value : e.target.textContent;
					textarea_text = parent.document.getElementById("textarea_text");
					textarea_text.value = me.Contextvalue;
						textarea_text.focus();
						textarea_text.select();
					}
			});
		}
	},
	/**
	 * 拿到操作日志
	 */
	getLogs: function(id){
		if(Ext.getCmp('win' + id)){
			Ext.getCmp('win' + id).show();
		} else {
			Ext.Ajax.request({//拿到grid的columns
	        	url : basePath + 'common/getMessageLogs.action',
	        	async: false,
	        	params: {
	        		caller: caller,
	        		id:  id
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exception || res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        			return;
	        		}
	        		var logs = res.logs;
	        		logs = logs.length == 0 ? [{ml_date: $I18N.common.grid.emptyText, ml_man: $I18N.common.grid.emptyText, 
	        			ml_content: $I18N.common.grid.emptyText, ml_result: $I18N.common.grid.emptyText}] : logs;
	        		Ext.create('Ext.window.Window', {
   			    		id : 'win' + id,
   			    		title: '<span style="color:#CD6839;">操作日志</span>',
   			    		iconCls: 'x-button-icon-set',
   			    		closeAction: 'hide',
	   				    height: "100%",
	   				    width: "80%",
	   				    maximizable : true,
	   					buttonAlign : 'center',
	   					layout : 'anchor',
	   				    items: [{
	   				    	anchor: '100% 100%',
	   				    	xtype: 'gridpanel',
	   				    	bodyStyle: 'background:#f1f1f1;',
		        			autoScroll: true,
		        		    store: Ext.create('Ext.data.Store', {
								    fields: ['ml_date', 'ml_man', 'ml_content', 'ml_result'],
								    data: logs
							}),
							columnLines: true,
		        		    columns: [
		        		        { header: '时间', dataIndex: 'ml_date', flex: 1.5 , renderer: function(val){
		        		        	if(val != '无数据'){
		        		        		return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
		        		        	}
		        		        }},
		        		        { header: '操作人员', dataIndex: 'ml_man', flex: 1 ,renderer: function(val){
		        		        	if(val == em_name){
		        		        		return '<font color=red>' + val + '</font>';
		        		        	} else {
		        		        		return val;
		        		        	}
		        		        }},
		        		        { header: '操作', dataIndex: 'ml_content', flex: 1.5},
		        		        { header: '结果', dataIndex: 'ml_result', flex: 3}
		        		    ]
	   				    }],
	   				    buttons : [{
	   				    	text : '关  闭',
	   				    	iconCls: 'x-button-icon-close',
	   				    	cls: 'x-btn-gray',
	   				    	handler : function(){
	   				    		Ext.getCmp('win' + id).close();
	   				    	}
	   				    }]
	   				}).show();
	        	}
	        });
		}
	},
	/**
	 * 拿到流程处理情况
	 */
	getProcess: function(id){
		var me = this;
		//先获取jprocess的nodeId
		Ext.Ajax.request({
			url : basePath + 'common/getJProcessByForm.action',
			async: false,
			params: {
				caller: caller,
				keyValue: id,
				_noc:1
			},
			method : 'post',
			callback : function(options,success,response){
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.node && localJson.node != -1){
					//再根据nodeId调取流程信息
					if(Ext.getCmp('win-flow' + id)){
						Ext.getCmp('win-flow' + id).show();
					} else {
						var form = Ext.create('Ext.form.Panel', {
							layout: 'column',
							defaultType: 'textfield',
							anchor: '100% 20%' ,
							bodyStyle: 'background:#f1f1f1;',
							fieldDefaults: {
								columnWidth: 0.33,
								readOnly: true,
								cls : "form-field-allowBlank",
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'
							},
							items: [{
								id: 'jp_name',
								name: 'jp_name',
								fieldLabel: '流程名称',
								columnWidth: 0.33
							},{
								columnWidth: 0.33,
								xtype: 'textfield',
								fieldLabel: '发起时间',
								name: 'jp_launchTime',
								id:'jp_launchTime',
								readOnly: true,
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'
							},{
								fieldLabel: '发起人', 
								columnWidth: 0.33,
								xtype: 'textfield',
								id:'jp_launcherName',
								name: 'jp_launcherName',
								readOnly: true,
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'	   				    		
							},{
								fieldLabel: '节点名称', 
								id: 'jp_nodeName',
								name: 'jp_nodeName',
								xtype: 'textfield',
								readOnly: true,   
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;'
							},{
								fieldLabel: '处理人',  
								id: 'jp_nodeDealMan',
								name: 'jp_nodeDealMan',
								xtype: 'textfield',
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;',
								readOnly: true,  
								listeners:{
									change:function(field){			
										var em=Ext.getCmp('jp_nodeDealMan').getValue();
										var btn=Ext.getCmp('dealbutton');
										if(em!=em_code) btn.setDisabled(true);
									}
								}
							},{
								fieldLabel: '审批状态',
								id:'jp_status',
								name:'jp_status',
								xtype: 'textfield',
								fieldStyle: 'background:#f0f0f0;border: 1px solid #8B8970;',
								readOnly: true    
							}],
							loader: {
								url: basePath + 'common/getCurrentNode.action',
								renderer: function(loader, response, active) {
									var res = Ext.decode(response.responseText);
									if(res.info.currentnode.jp_nodeDealMan){
										res.info.currentnode.jp_nodeDealMan=res.info.dealmanname+"("+res.info.currentnode.jp_nodeDealMan+")";
									}else res.info.currentnode.jp_nodeDealMan=res.info.dealmanname+"("+res.info.currentnode.jp_candidate+")";
									res.info.currentnode.jp_launchTime = Ext.Date.format(new Date(res.info.currentnode.jp_launchTime), 'Y-m-d H:i:s');
									this.target.getForm().setValues(res.info.currentnode);
									return true;
								},
								autoLoad: true,
								params: {
									jp_nodeId: localJson.node,
									_noc:1
								}
							},
							buttonAlign: 'center',
							buttons: [{
								text: $I18N.common.button.erpFlowButton,
								iconCls: 'x-button-icon-scan',
								cls: 'x-btn-gray',
								id:'dealbutton',
								handler: function(btn){
									me.FormUtil.onAdd(caller + '_flow', '流程处理', 'jsps/common/jprocessDeal.jsp?formCondition=jp_nodeidIS' + localJson.node);		   
								}
							},{
								text : '关  闭',
								iconCls: 'x-button-icon-close',
								cls: 'x-btn-gray',
								handler : function(){
									Ext.getCmp('win-flow' + id).close();
								}
							}]
						});
						Ext.create('Ext.window.Window', {
							id : 'win-flow' + id,
							title: '<span style="color:#CD6839;">流程处理情况</span>',
							iconCls: 'x-button-icon-set',
							closeAction: 'hide',
							height: "100%",
							width: "90%",
							maximizable : true,
							buttonAlign : 'center',
							layout : 'fit',
							items:[{
								xtype:'tabpanel',
								frame:true,
								layout:'fit',
								items:[{
									title:'处理明细',
									layout:'anchor',
									frame:true,
									items:[form, Ext.create("erp.view.common.JProcess.GridPanel",{
										anchor: '100% 80%' ,
										nodeId: localJson.node
									})]
								},{ 
									title : '节点信息',
									items:[{
										tag : 'iframe',
										style:{
											background:'#f0f0f0',
											border:'none'
										},						  
										frame : true,
										border : false,
										layout : 'fit',
										height:window.innerHeight*0.9,
										iconCls : 'x-tree-icon-tab-tab',
										html : '<iframe id="iframe_maindetail_" src="'+basePath+'workfloweditor/workfloweditorscan.jsp?jdId='+localJson.jd+"&type="+localJson.type+"&nodeId="+localJson.node+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'	
									}]				    			
								}]

							}]
						}).show();
					}
				} else {
					showMessage("提示", "当前单据无流程处理!");
				}
			}
		});
	},
	autoSetBtnStyle : function(from,async) {
		var t = Ext.getCmp('form_toolbar');
		Ext.each(t.items.items,function(group,index){
			if(group.items){
				var _first = _last = -1;
				Ext.each(group.items.items,function(item,index){
					if(!item.hidden){						
						if(_first==-1){_first = index;}
						_last = index;
					}
				});
				if(_first>-1){
					if(group.items.items[_first].el&&group.items.items[_last].el){
						group.items.items[_first].el.dom.classList.add('x-group-btn-first');
						group.items.items[_last].el.dom.classList.add('x-group-btn-last');
					}
				}else{
					group.hide();
				}
			}
		});
		t.doLayout();
	},
	SetNodeDealMan:function(id){
		var me=this;
		var nodewin=Ext.getCmp('win-nodeflow'+id);
		if(nodewin){
			nodewin.show();
		}else {
			Ext.create('Ext.window.Window', {
				id : 'win-nodeflow' + id,
				title: '<span style="color:#CD6839;">设置节点处理人</span>',
				iconCls: 'x-button-icon-set',
				closeAction: 'hide',
				height: "100%",
				width: "90%",
				maximizable : true,
				buttonAlign : 'center',
				layout : 'fit',
				items:[{
					anchor: '100% 90%' ,
					xtype:'SetNodeGridPanel',
					keyValue: id,
					FlowCaller:caller
				}],
				buttons:['->',{
					text:'保 存',
					iconCls: 'x-button-icon-save',
					cls: 'x-btn-gray',
					width : 65,
					style: {
			    		marginLeft: '10px'
			        },
			        handler:function(btn){
			           var win=btn.ownerCt.ownerCt;
			           var grid=win.items.items[0];
			           var msg=grid.GridUtil.checkGridDirty(grid);
			           if(msg==''){
			        	   Ext.Msg.alert('提示','无任何修改!');
			           }else {
			        	  var param= grid.GridUtil.getGridStore(grid);
			        	  param= unescape("[" + param.toString().replace(/\\/g,"%") + "]");
                          me.FormUtil.setLoading(true);
			        	  Ext.Ajax.request({
			      	   		url : basePath + 'common/updateJnodePerson.action?_noc=1',			      	   		
			      	   		params: {
			      	   			param:param,
			      	   			caller:caller,
			      	   			keyValue:id
			      	   		},
			      	   		method : 'post',
			      	   		callback : function(options,success,response){
			      	   		me.FormUtil.setLoading(false);
			      	   			var localJson = new Ext.decode(response.responseText);
			          			if(localJson.sucess){
			          				showMessage('提示', '保存成功!', 1000);
			          				grid.loadNewStore(grid,caller,id);
			          			}else if(localJson.exceptionInfo != null){
			            			showError(res.exceptionInfo);return;
			            		}
			                }
			        	});
			          }
			        }
				},{
					text:'关 闭',
					iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	width: 65,
			    	style: {
			    		marginLeft: '10px'
			        },
			        handler:function(btn){			        	
			        	btn.ownerCt.ownerCt.close();			        	
			        }
				},'->']
			}).show();
		}
		
	}
});