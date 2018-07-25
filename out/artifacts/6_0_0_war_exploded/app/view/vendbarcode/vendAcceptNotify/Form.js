Ext.define('erp.view.vendbarcode.vendAcceptNotify.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.vendAcceptNotifyForm',
	hideBorders: true, 
	id:'form',
	title:'送货通知单',
	autoWidth : true,
	bodyPadding:'15 0 0 10',
	bodyStyle: 'background: #f1f1f1;',
	autoScroll:true,
	enableTools: true,
	readOnly:false,
	layout: 'column', 
	initComponent : function(){
		var me = this;
    	me.FormUtil=Ext.create('erp.util.FormUtil');
    	me.GridUtil=Ext.create('erp.util.GridUtil');
    	me.BaseUtil=Ext.create('erp.util.BaseUtil');
		formCondition = getUrlParam('formCondition');
		if(formCondition){
			this.getData();
		}
		this.callParent(arguments); 
		if(this.enableTools) {
			me.setTools();
		}
	},
	defaults:{
		xtype:'textfield',
		focusCls: 'x-form-field-cir-focus',
		columnWidth:0.25,
		/*cls: "form-field-allowBlank", */
		labelAlign: "left" ,
		margin:'5 0 0 0',
		labelWidth:80
	},
		items:[{
			name:'an_id',
			fieldLabel:'ID',
			id : 'AN_ID',
			hidden:true,
			xtype:'numberfield',
			width:0,
	    },{ 
		    name:'an_code',
		    fieldLabel:'通知单号',
		    id : 'AN_CODE',
		    readOnly:true,
		    fieldStyle:'background:#e0e0e0;'
		},{
			name:'an_vendcode',
			fieldLabel:'供应商编号',
			id : 'AN_VENDCODE',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
	    	name:'an_vendname',
	    	id : 'AN_VENDNAME',
	    	fieldLabel:'供应商名称',
	    	readOnly:true,
	    	fieldStyle:'background:#e0e0e0;',
        },{
			name:'an_recorder',
			id:'AN_RECORDER',
			fieldLabel:'录入人',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
			name:'an_buyer',
			id:'AN_BUYER',
			xtype:'textfield',
			fieldLabel:'采购员',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
			name:'an_indate',
			id:'AN_INDATE',
			xtype:'textfield',
			fieldLabel:'录入日期',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
			name:'an_currency',
			id:'AN_CURRENCY',
			xtype:'textfield',
			fieldLabel:'币别',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
			name:'an_payment',
			id:'AN_PAYMENT',
			xtype:'textfield',
			fieldLabel:'付款方式',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
			name:'an_paydate',
			id:'AN_PAYDATE',
			xtype:'textfield',
			fieldLabel:'付款日期',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
	    },{
			name:'an_status',
			id:'AN_STATUS',
			xtype:'textfield',
			fieldLabel:'状态',
			readOnly:true,
			fieldStyle:'background:#e0e0e0;',
			value:'在录入',
	    },{
			name:'an_statuscode',
			id:'AN_STATUSCODE',
			xtype:'textfield',
			fieldLabel:'状态码',
			hidden:true,
	    },{
			name:'an_sendcode',
			id:'AN_SENDCODE',
			xtype:'textfield',
			fieldLabel:'送货通知单',
	    }],
	    getData:function(){
			var me = this;
			//从url解析参数
			if(formCondition != null && formCondition != '')
				formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=").replace(/\'/g,"");
				this.setLoading(true);
				Ext.Ajax.request({
		        	url : basePath + '/vendbarcode/datalist/getAcceptNotifyForm.action',
		        	params: {
		        		caller: caller, 
		        		id: formCondition.split("=")[1],
		        		_noc: (getUrlParam('_noc') || me._noc)
		        	},
		        	method : 'post',
		        	/*async: false,*/
		        	callback : function(options,success,response){
		        		getbyUUid=false;
		        		me.setLoading(false);
		        		var res = new Ext.decode(response.responseText);
		        		if(res.exceptionInfo != null){
		        			showError(res.exceptionInfo);return;
		        		}else{
		        			me.setFormValues(res.data);
		        			var submitBtn = Ext.getCmp('submit');
		        			var resSubmitBtn = Ext.getCmp('resSubmit');
		        			var auditButton = Ext.getCmp('cofirmdeliverybtn');
		        			var resAuditButton = Ext.getCmp('canceldeliverybtn');
		        			var deleteButton = Ext.getCmp('deletebutton');
		        			var updateButton = Ext.getCmp('updatebutton');
		        			if(res.data['AN_STATUSCODE']!='ENTERING'){
		        				submitBtn&&submitBtn.hide();
		        				deleteButton&&deleteButton.hide();
		        				updateButton&&updateButton.hide();
		        			}
		        			if(res.data['AN_STATUSCODE']!='COMMITED'){
		        				resSubmitBtn&&resSubmitBtn.hide();
		        			}
		        			if(res.data['AN_STATUSCODE']!='AUDITED'){
		        				resAuditButton&&resAuditButton.hide();
		        			}
		        			if(res.data['AN_STATUSCODE']!='COMMITED'){
		        				auditButton&&auditButton.hide();
		        			}
		        		}
		        	}
		        });
		},
	buttonAlign:'center',
	buttons:[{
		xtype: 'erpUpdateButton',
		/*hidden:Ext.isEmpty(formCondition)*/
	},{
		xtype: 'erpDeleteButton',
	},{
		xtype: 'erpSubmitButton',
	},{
		xtype: 'erpResSubmitButton',
	},{
		xtype: 'erpBarcodeButton',
	},{
		xtype: 'erpConfirmDeliveryButton',
	},{
		xtype: 'erpCancelDeliveryButton',
	},{
		xtype: 'erpPrintButton',
	},{
		xtype: 'erpCloseButton',
		id:'close'
	}],
	setFormValues : function(data){
		var form = Ext.getCmp('form');
		form.getForm().setValues(data);
		var o = {};
		var status = Ext.getCmp('AN_STATUSCODE');
		if (status && status.value != 'ENTERING') {
			form.readOnly = true;
			form.getForm().getFields().each(function(field) {  
		        field.setReadOnly(true);  
		        field.setFieldStyle("background:#e0e0e0;");
		    });  
		    form.fireEvent('afterload', form);
		}
	},
	/*getDataByField : function(field) {
		var form = this, f = form.child('#' + field);
		return f ? "'" + f.getValue() + "'" : '';
	},*/
	/*getYearmonthByField : function(field) {
		var form = this;
		var f = form.child('#' + field),
		v = f ? (Ext.isDate(f.value) ? f.value : Ext.Date.parse(f.value, 'Y-m-d')) : new Date();
		return Ext.Date.format(v, 'Ym');
	},*/
	/*getIdentity : function(){
		// 取唯一值
		var me = this, identity = me.identity, kfield = me.keyField;
		if (identity) {
			return (typeof identity === 'function' ? identity.call(me) : identity);
		} else if (kfield) {
			return Ext.getCmp(kfield).value;
		}
		return null;
	},*/
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
									var form = Ext.getCmp('form');
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
									var form = Ext.getCmp('form');
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
									var form = Ext.getCmp('form');
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
									var form = Ext.getCmp('form');
									if(!form.statuscodeField){
										btn.disable(true);
									} else {
										var f = form.statuscodeField;
									if(!Ext.getCmp(f) || Ext.getCmp(f).value == 'ENTERING'){
										btn.disable(true);
									} }
								},
								click :function(btn){
									var form = Ext.getCmp('form');
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
						var form = Ext.getCmp('form');
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
						var form = Ext.getCmp('form');
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
	expData : function() {
		/*window.open(basePath + 'common/dump/exp.action?type=' + caller + "&identity=" + encodeURIComponent(this.getIdentity()));*/
	}
});