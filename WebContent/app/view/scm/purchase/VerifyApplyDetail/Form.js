Ext.define('erp.view.scm.purchase.VerifyApplyDetail.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpVerifyApplyDetailFormPanel',
	id: 'verifyApplyDetailForm', 
	region: 'north',
	frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	cls: 'u-form-default',
	fieldDefaults : {
		fieldStyle : "background:#FFFAFA;color:#515151;",
		focusCls: 'x-form-field-cir-focus',
		labelAlign : "right",
		msgTarget: 'side',
		blankText : $I18N.common.form.blankText
	},
	requires: ['erp.view.common.JProcess.SetNodeGridPanel'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
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
	formCondition:null,
	Contextvalue:null,
	LastValue:null,
	enableTools: true,
	enableKeyEvents: true,
	_noc: 0,
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		//集团版
		var master=getUrlParam('newMaster');
		var param = {caller: this.caller || caller, condition: this.formCondition || formCondition, _noc: (getUrlParam('_noc') || this._noc)};
		if(master){
			param.master=master;
		}
		this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', this.params || param);//从后台拿到formpanel的items
		this.callParent(arguments);
		//加prev、next、refresh等tool
		if(this.enableTools) {
			this.setTools();
		}
		//给页面加上ctrl+alt+s键盘事件,自动跳转form配置界面
		if(this.enableKeyEvents) {
			this.addKeyBoardEvents();
		}		
	},
	/**
	 * FormHeader Tools
	 * 包括:查看日志、查看流程、查看列表、最大化、最小化、刷新、关闭、上一条、下一条
	 */
	setTools: function(){
		var me = this, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
			hasVoucher = !!me.voucherConfig, dumpable = me.dumpable,
			isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;
		me.tools = [
					{	xtype:'button',				
						text:'选项',
						id:'buttons',
						margin:'0 0 0 2',					
						menu: [{
							iconCls: 'x-nbutton-icon-log',
							text: '操作日志',	
							listeners:{	
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click: function(btn){								
									var form = Ext.getCmp('verifyApplyDetailForm');
									var id = Ext.getCmp(form.keyField).value;
									if(id != null && id != 0){
										form.getLogs(id);
									}
								}
							}							
						},{
							iconCls: 'x-nbutton-icon-message',
							text: '消息日志',
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(btn){
									var form = Ext.getCmp('form');
									var id = Ext.getCmp(form.keyField).value;
									if(id != null && id != 0){										
									me.getMessageInfo(id,caller);}
								}	
							}							
						},{
							iconCls: 'x-nbutton-icon-link',
							text: '关联查询',
							hidden: !isNormalPage,
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(btn){
									var form = Ext.getCmp('verifyApplyDetailForm');
									form.showRelativeQuery();
								}
							}
						},{
							iconCls: 'x-nbutton-icon-download',
							text: '下载数据',
							hidden: !isNormalPage,
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(){
									var form = Ext.getCmp('verifyApplyDetailForm');
									var id = Ext.getCmp(form.keyField).value;
									form.saveAsExcel(id,caller);
								}
							}							
						},{
							iconCls: 'x-nbutton-icon-plan',
							text: '导出方案',
							hidden: !dumpable,
							listeners:{
								afterrender:function(btn){
									formCondition = getUrlParam('formCondition');
									if(formCondition==null||formCondition==''){
										btn.disable();
									}
								},
								click:function(){
									// 用于配置方案导出
									me.expData();
								}
							}							
						},{
							iconCls: 'x-nbutton-icon-process',
							text: '流程处理',
							hidden: !isNormalPage,
							listeners:{	
								afterrender:function(btn){
									var form = Ext.getCmp('verifyApplyDetailForm');
									if(!form.statuscodeField){
										btn.disable(true);
									} else {
										var f = form.statuscodeField;
									if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
										btn.disable(true);
									} }
								},
								click :function(btn){
									var form = Ext.getCmp('verifyApplyDetailForm');
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
							iconCls: 'x-nbutton-icon-report',
							text: '单据设置',
							listeners:{
								afterrender:function(v){
									me.ifadmin(v);							
								},
								click:function(){
									me.reportset();
								}
							}
							
						}]
					},
		{
			xtype : 'button',
			text:'凭证',
			margin:'0 0 0 2',
			hidden : !hasVoucher,
			listeners : {
				click : function(t) {
					var form = t.ownerCt.ownerCt;
					form.createVoucher(form.voucherConfig);
				}
			}
		},		
		{
			xtype: 'button',
			text:'帮助',
			margin:'0 0 0 2',
			hidden : !isNormalPage,
			listeners:{
				click: function(t){
					var form = t.ownerCt.ownerCt;
					form.showHelpWindow();
				}
			}
		},
		{
			id: 'prev',
			iconCls: 'x-nbutton-icon-left',
			xtype:'button',
			hidden : !hasVoucher,
			listeners:{
				render: function(btn){
					if(parent.Ext) {
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
					}
				},
				click: function(btn){
					var datalist = parent.Ext.getCmp(datalistId);
					if(datalist){
						var datalistStore = datalist.currentStore;
						var form = Ext.getCmp('verifyApplyDetailForm');
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
						datalistStore[idx].selected = false;
						datalistStore[idx-1].selected = true;
						var url = window.location.href;
						if(form.keyField) {
							url = url.replace(/formCondition=(\w*)(IS|=)(\d*)/, 'formCondition=$1$2' + newId);
							url = url.replace(/gridCondition=(\w*)(IS|=)(\d*)/, 'gridCondition=$1$2' + newId);
						}
						window.location.href = url;
					}
				}
			}
		},{
			xtype: 'button',
			iconCls: 'x-nbutton-icon-right',
			id: 'next',
			tooltip: '下一条',
			hidden : !hasVoucher,
			listeners:{
				render: function(btn){
					if(parent.Ext) {
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
					}
				},
				click: function(btn){
					var datalist = parent.Ext.getCmp(datalistId);
					if(datalist){
						var datalistStore = datalist.currentStore;
						var form = Ext.getCmp('verifyApplyDetailForm');
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
						datalistStore[idx].selected = false;
						datalistStore[idx+1].selected = true;
						var url = window.location.href;
						if(form.keyField) {
							url = url.replace(/formCondition=(\w*)(IS|=)(\d*)/, 'formCondition=$1$2' + newId);
							url = url.replace(/gridCondition=(\w*)(IS|=)(\d*)/, 'gridCondition=$1$2' + newId);
						}
						window.location.href = url;
					}
				}
			}
		}];
	},
	ifadmin:function(v){
		var table='EMPLOYEE';
		var field='EM_TYPE';
		Ext.Ajax.request({
			url : basePath + '/common/isadmin.action',
			params: {
				table:table,
				field:field
			},
			method : 'get',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					
					if(r.data!='admin'){
						v.disable();
					}
					
				} 
			}
		});
	},
	reportset:function(){
		var me=this;
		var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
		forms = Ext.ComponentQuery.query('form'); 
		grids = Ext.ComponentQuery.query('gridpanel');
		formSet = [], gridSet = [];
		if(forms.length > 0) {
			Ext.Array.each(forms, function(f){
				f.fo_id && (formSet.push(f.fo_id));
			});
		}
		if(grids.length > 0) {
			Ext.Array.each(grids, function(g){
				gridSet.push(g.caller || window.caller);
			});
			gridSet = Ext.Array.unique(gridSet);
		}
		if(formSet.length > 0 || gridSet.length > 0) {
			url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
		}
		var myurl=basePath+url;
		Ext.create('Ext.window.Window', {
			title: '<span style="color:#CD6839;">单据设置</span>',
			iconCls: 'x-button-icon-set',
			closeAction: 'hide',
			height: "90%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{	
				xtype:'tabpanel',
				anchor: '100% 100%',
				layout : 'anchor',
				items:[{
					xtype: 'panel',
					title:'界面配置',
					anchor: '100% 100%',	
					items:[
					{	xtype: 'component',
						id:'iframe_detail_multiform',									
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: myurl}
					}]
					
				},{
					title:'逻辑配置',
					xtype: 'panel',
					items:[
					{	xtype: 'component',
						id:'iframe_detail_config',									
						autoEl: {
								tag: 'iframe',
								style: 'height: 100%; width: 100%; border: none;',
								src: basePath + 'jsps/ma/logic/config.jsp?whoami=' +caller}
					}]					
				},{
					
					xtype: 'panel',
					title:'知会设置',
					items:[{
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						autoScroll:true,
						html:'<iframe src="' + basePath + 'jsps/sysmng/MsgSetting.jsp?whoami=' +caller+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
					}]
				}]
			}],
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					this.ownerCt.ownerCt.close();
				}
			}]
			}).show();
		
	},
		getMessageInfo:function(id,caller){
		if(Ext.getCmp('msgwin' + id)){
			Ext.getCmp('msgwin' + id).show();
		} else {
		var me=this;
		Ext.Ajax.request({
			url : basePath + 'common/getMessageInfo.action',
			params: {
				caller:caller,
				id:id
			},
			method : 'get',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					
					me.showmessagelog(r.logs,id);	
				} 
			}
		});
	}
		
	},
	showmessagelog:function(logs,id){
		if(logs.length<1){
			Ext.create('Ext.window.Window', {
			title: '<span style="color:#CD6839;">消息日志</span>',
			iconCls: 'x-button-icon-set',
			closeAction: 'hide',
			height: "90%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'form',			
				ignore: true,
				bodyStyle: 'background:#f1f1f1;',
				autoScroll: true,
				html:'<div style="left:35%;position:absolute;top:30%;font-weight:bold;font-size:25px;color:rgba(144, 143, 143, 0.5)">本单据不存在消息日志</div>'
			}],
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					this.ownerCt.ownerCt.close();
				}
			}]
			}).show();
		}else{
			Ext.create('Ext.window.Window', {
			id : 'msgwin'+id,
			title: '<span style="color:#CD6839;">消息日志</span>',
			iconCls: 'x-button-icon-set',
			closeAction: 'hide',
			height: "100%",
			width: "90%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'gridpanel',
				ignore: true,
				bodyStyle: 'background:#f1f1f1;',
				autoScroll: true,
				store: Ext.create('Ext.data.Store', {
					fields: ['IH_DATE', 'IHD_RECEIVE', 'IH_CONTEXT', 'IHD_READSTATUS','IHD_READTIME'],
					data: logs
				}),
				columnLines: true,
				columns: [
				          { header: '发出时间', 
				          	dataIndex: 'IH_DATE',
				          	flex: 1.5 , 
				          	renderer: function(val){
				        	  if(val != '无数据'){
				        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
				        	  }
				          }},
				          { header: '接收人', 
				          	dataIndex: 'IHD_RECEIVE', 
				          	flex: 1 ,
				          	renderer: function(val){
				        	  if(val == em_name){
				        		  return '<font color=red>' + val + '</font>';
				        	  } else {
				        		  return val;
				        	  }
				          }},
				          { header: '信息描述', 
				          	dataIndex: 'IH_CONTEXT', 
				          	flex: 3.5
				          },
				          { header: '阅读状态', 
				          	dataIndex: 'IHD_READSTATUS', 
				          	flex: 1,
					        renderer:function(value,meta,record){
							    if(value==0){
							    	return '<font color="#ff0000">未读</font>';
							    }else if(value==-1){
							    	return '<font color="#4795ef">已读</font>'; 
							    }
							     
							}  
						  },
						  { header: '阅读时间', 
						 	dataIndex: 'IHD_READTIME', 
						 	flex: 1.5,
						 	renderer: function(val){
				        	  if(val != ''&&val!=null){
				        		  return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');
				        	  }
				          }
						  }],
					}],
			
			buttons : [{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(){
					Ext.getCmp('msgwin' + id).close();
				}
			}]
			
			}).show();	
		}
		
	},
	/**
	 * 关联查询
	 */
	showRelativeQuery: function() {
		var me = this,
		win = Ext.getCmp('ext-relative-query');
		if(!win) {
			win = Ext.create('Ext.Window', {
				id: 'ext-relative-query',
				width: '90%',
				height: '90%',
				closeAction: 'hide',
				//title: '<font style="font-family: KaiTi;color:#333;">' + me.title + '</font>--查询',
				title: '<span style="color:#CD6839;">关联查询</span>',
				iconCls: 'x-button-icon-set',
				layout: 'anchor',
				items: [{
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe src="' + basePath + 'jsps/common/relativeSearch.jsp?whoami=' + caller + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			});
		}
		win.show();
	},
	/**
	 * 监听一些事件
	 * <br>
	 * Ctrl+Alt+S	单据配置维护
	 * Ctrl+Alt+P	参数、逻辑配置维护
	 */
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url = "jsps/ma/form.jsp?formCondition=fo_idIS" + me.fo_id + "&gridCondition=fd_foidIS" + me.fo_id, 
						forms = Ext.ComponentQuery.query('form'), 
						grids = Ext.ComponentQuery.query('gridpanel'),
						formSet = [], gridSet = [];
					if(forms.length > 0) {
						Ext.Array.each(forms, function(f){
							f.fo_id && (formSet.push(f.fo_id));
						});
					}
					if(grids.length > 0) {
						Ext.Array.each(grids, function(g){
							if(g.xtype.indexOf('erpVerifyApplyDetailGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
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
							height: "90%",
							width: "90%",
							maximizable : true,
							buttonAlign : 'center',
							layout : 'anchor',
							items: [{
								anchor: '100% 100%',
								xtype: 'gridpanel',
								ignore: true,
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
		getProcess: function(id){
		var me = this;
		//先获取jprocess的nodeId
		Ext.Ajax.request({
			url : basePath + 'common/getJProcessByForm.action',
			async: false,
			params: {
				caller: me.realCaller||caller,
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
							height: "90%",
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
									title : '节点设置',
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
								},{ 
									title : '流程图',
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
								},{
									title:'历史处理明细',
									layout:'anchor',
									frame:true,
									items:[Ext.create("erp.view.common.JProcess.AllHistoryGridPanel",{
										anchor: '100% 100%' ,
										nodeId: localJson.node
									})]								
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
	saveAsExcel:function(id,caller){
		if(id==null || id =='') showMessage('提示','无法导出空数据单据',1000);
		else window.location.href=basePath+'excel/savePanelAsExcel.action?id='+id+"&caller="+caller+"&_noc=1";
	},

	showHelpWindow:function(){
		var me = this,
		win = Ext.getCmp('ext-help'),path;
		Ext.Ajax.request({
			url : basePath + 'common/form/getHelpDoc.action',
			params: {
				caller:caller
			},
			method : 'post',
			async:false,
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				} else if(r.success){
					path=r.path;
				} 
			}
		});
		if(!win) {
			win = Ext.create('Ext.Window', {
				id: 'ext-help',
				width: '90%',
				height: '90%',
				closeAction: 'hide',
				title: '帮助文档',
				layout: 'border',
				items: [{
					region:'center',
					tag : 'iframe',
					layout : 'fit',
					html : '<iframe src="' + basePath + path + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{
					region: 'south',
					height: 100,
					split: true,
					collapsible: true,
					title: '相关信息',
					minHeight:60,
					collapsed: true,
					html: '相关信息'	
				}]
			});
		}
		win.show();
	},
	parseVoucherConfig : function(config) {
		var form = this, keys = Ext.Object.getKeys(config), args = {};
		Ext.each(keys, function(k){
			if (typeof config[k] === 'function') {
				args[k] = config[k].call(null, form);
			} else if (k == 'yearmonth') {
				args[k] = form.getYearmonthByField(config[k]);
			} else if (k == 'datas') {
				args[k] = form.getDataByField(config[k]);
			} else {
				args[k] = config[k];
			}
		});
		return args;
	},
	createVoucher: function(config) {
		var me = this, args = this.parseVoucherConfig(config);
		if('unneed' == args.kind) {
			showMessage('提示', '该类型单据无需制作凭证！');
			return;
		}
		me.getVoucher(function(data){
			var vf = me.voucherConfig.voucherField;
			if (!Ext.isEmpty(data[vf])) {
				if(data[vf] != 'UNNEED') {
					var box = Ext.create('Ext.window.MessageBox', {
						buttonAlign : 'center',
						buttons: [{
							text: '查看凭证',
							handler: function(b) {
								me.linkVoucher(data);
								b.ownerCt.ownerCt.close();
							}
						},{
							text: '取消凭证',
							handler: function(b) {
								me.onVoucherUnCreate(args);
								b.ownerCt.ownerCt.close();
							}
						},{
							text: '关闭',
							handler : function(b) {
								b.ownerCt.ownerCt.close();
							}
						}]
					});
					box.show({
						title : "提示",  
						msg : "当前单据已制作过凭证:" + data[vf],  
						icon : Ext.MessageBox.QUESTION
					});
				} else
					showMessage('提示', '该类型单据无需制作凭证！');
			} else {
				warnMsg('现在制作凭证?', function(b){
					if (b == 'ok' || b == 'yes') {
						me.beforeVoucherCreate(args);
					}
				});
			}
		});
	},
	beforeVoucherCreate : function(args) {
		var me = this;
		if(me.keyField && me.statusField) {
			var val = me.down('#' + me.keyField).getValue(),
			params = {
				caller: me.tablename,
				field: me.voucherConfig.status || me.statusField,
				condition: me.keyField + '=' + val 
			};
			Ext.Ajax.request({
				url : basePath + 'common/getFieldData.action',
				params: params,
				method : 'post',
				callback : function(opt, s, res){
					var r = new Ext.decode(res.responseText);
					if(r.exceptionInfo){
						showError(r.exceptionInfo);
					} else if(r.success && (args.statusValue || 'POSTED') == r.data){
						me.onVoucherCreate(args);
					} else {
						showError('单据还未过账!');
					}
				}
			});
		}
	},
	onVoucherCreate : function(args) {
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'fa/vc/createVoucher.action',	
			params: args,
			callback: function(opt, s, r) {
				me.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
    				showError(rs.exceptionInfo);
    			} else {
    				if(rs.success && rs.content){
		   				var msg = "";
		   				Ext.Array.each(rs.content, function(item){
		   					if(item.errMsg) {
		   						msg += item.errMsg + '<hr>';
		   					} else if(item.id) {
		   						msg += '凭证号:<a href="javascript:openUrl2(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' 
	    							+ item.id + '&gridCondition=vd_voidIS' + item.id + '\',\'凭证\',\'vo_id\','+item.id+');">' + item.code + '</a><hr>';	
		   					}
		   				});
    					showMessage('提示', msg);
		   			}
    			}
			}
		});
	},
	onVoucherUnCreate : function(args) {
		var me = this;
		me.setLoading(true);
		Ext.Ajax.request({
			url: basePath + 'fa/vc/unCreateVoucher.action',
			params: args,
			callback: function(opt, s, r) {
				me.setLoading(false);
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else if(rs.error) {
					showMessage('提示', rs.error);
				} else {
					alert('取消成功');
					var vf = me.voucherConfig.voucherField, v = me.down('#' + vf);
					v && v.setValue(null);
				}
			}
		});
	},
	getDataByField : function(field) {
		var form = this, f = form.child('#' + field);
		return f ? "'" + f.getValue() + "'" : '';
	},
	getYearmonthByField : function(field) {
		var form = this;
		var f = form.child('#' + field),
		v = f ? (Ext.isDate(f.value) ? f.value : Ext.Date.parse(f.value, 'Y-m-d')) : new Date();
		return Ext.Date.format(v, 'Ym');
	},
	getVoucher : function(callback) {
		var me = this, vf = me.voucherConfig.voucherField;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			params: {
				caller : me.tablename + ' left join voucher on vo_code=' + vf,
				fields : 'vo_id,' + vf,
				condition : me.keyField + '=' + me.down('#' + me.keyField).value
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				}
				if(r.success){
					callback.call(null, r.data || {});
				}
			}
		});
	},
	linkVoucher : function(data) {
		this.FormUtil.onAdd(null, '凭证', 'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' +
				data.vo_id + '&gridCondition=vd_voidIS' + data.vo_id);
	},
	addTask : function() {
		var me = this, taskWin = me.taskWin;
		if(!taskWin) {
			me.taskWin = taskWin = Ext.create('erp.view.core.window.Task', {
				sourceform : me
			});
		}
		taskWin.show();
	},
	getCurrentStore: function(value){
    	var grid = parent.Ext.getCmp('grid');
		var items = grid.store.data.items;
		var array = new Array();
		var o = null;
		Ext.each(items, function(item, index){
			o = new Object();
			o.selected = false;
			if(index == 0){
				o.prev = null;
			} else {
				o.prev = items[index-1].data["vad_id"];
			}
			if(index == items.length - 1){
				o.next = null;
			} else {
				o.next = items[index+1].data["vad_id"];
			}
			var v = item.data["vad_id"];
			o.value = v;
			if(v == value)
				o.selected = true;
			array.push(o);
		});
		return array;
    }
});
