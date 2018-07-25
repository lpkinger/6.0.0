/**
 * setBarcodeRule的Form
 */
Ext.define('erp.view.scm.reserve.SetBarcodeRuleForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpSetBarcodeRuleForm',
	id: 'form',
	/*title : caller=='BarCodeSet!BATCH'?'条码产生规则维护':'包装箱号产生规则维护',*/
	frame : true,
	layout: 'column',
	autoScroll : true,
	defaultType : 'textfield',
	buttonAlign : 'center',
	cls: 'u-form-default',
	enableTools: false,
	fieldDefaults : {
		fieldStyle : "background:#FFFAFA;color:#515151;",
		focusCls: 'x-form-field-cir-focus',
		labelAlign : "right",
		msgTarget: 'side',
		blankText : $I18N.common.form.blankText
	},
	defaults:{
		columnWidth:0.25,
		labelAlign:"right"
	},
	//requires: ['erp.view.common.JProcess.SetNodeGridPanel','erp.view.oa.task.TaskPanel'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	items : [{	xtype : "container",
				html : '<div class="x-form-group-label" id="group1" style="background-color: #bfbfbe;height:22px!important;" title="收拢"><h6>'+(caller=='BarCodeSet!BATCH'?"P+V+D+N":"P+N")+'</h6></div>',
				columnWidth:1,
				style:"padding-bottom: 4px;"
			  },{
				allowBlank:false,
				allowDecimals:false,
				cls:"form-field-allowBlank-hidden",
				columnWidth:0.25,
				dataIndex:"bs_id",
				fieldLabel:"ID",
				id:"bs_id",
				name:"bs_id",
				xtype:"hidden"
		     },{
				allowBlank:false,
				allowDecimals:false,
				cls:"form-field-allowBlank-hidden",
				dataIndex:"bs_type",
				fieldLabel:"类型",
				id:"bs_type",
				name:"bs_type",
				value : formCondition.split("=")[1].replace(/'/g,""),
				xtype:"hidden"
		     },{
				allowBlank:false,
				allowDecimals:false,
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_lenprid",
				editable:true,
				fieldLabel:"物料长度(P)",
				labelSeparator : ':',
				fieldStyle:"background:#FFF;color:#515151;",
				id:"bs_lenprid",
				name:"bs_lenprid",
				xtype:"numberfield",
				allowDecimals: false,
				minValue: 6,
				maxValue: 12,
				autoStripChars:true
		     },{
				allowBlank:false,
				allowDecimals:false,
				cls:"form-field-allowBlank",
				dataIndex:"bs_lenveid",
				editable:true,
				fieldLabel:"供应商长度(V)",
				labelSeparator : ':',
				fieldStyle:"background:#FFF;color:#515151;",
				id:"bs_lenveid",
				name:"bs_lenveid",
				xtype:caller=='BarCodeSet!BATCH'?"numberfield":"hidden",
				minValue: 0,
				maxValue: 10,
				autoStripChars:true
		     },{
				allowBlank:true,
				allowDecimals:false,
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_datestr",
				displayField: 'display',
				valueField: 'value',
				editable:false,
				fieldLabel:"日期信息(D)",
				labelSeparator : ':',
				fieldStyle:"background:#FFF;color:#515151;",
				id:"bs_datestr",
				name:"bs_datestr",
				xtype:caller=='BarCodeSet!BATCH'?"combo":"hidden",
				store:Ext.create("Ext.data.Store",{
					fields:["display","value"],
					data : [{display: "YYMMDD", value: "YYMMDD"},
							{display: "YYMM", value: "YYMM"},
							{display: "MMDD", value: "MMDD"},
							{display: "无", value: "0"}]
				})
		     },{
				allowBlank:false,
				allowDecimals:false,
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_lennum",
				editable:true,
				fieldLabel:"流水长度(N)",
				labelSeparator : ':',
				fieldStyle:"background:#FFF;color:#515151;",
				id:"bs_lennum",
				name:"bs_lennum",
				xtype:"numberfield",
				minValue: 1,
				maxValue: 10,
				autoStripChars:true
	    	 },{
	    		xtype : "container",
				html : '<div  class="x-form-group-label" id="group2" style="background-color: #bfbfbe;height:22px!important;" title="收拢2"><h6>其他信息</h6></div>',
				columnWidth:1,
				style:"padding-bottom: 4px;"
	    	 },{
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_maxnum",
				editable:true,
				fieldLabel:"当前流水",
				fieldStyle:"background:#eeeeee;color:#515151;",
				labelSeparator : ':',
				id:"bs_maxnum",
				name:"bs_maxnum",
				readOnly:true,
				value:"0",
				xtype:"textfield"
		     },{
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_totallen",
				editable:true,
				fieldLabel:"总长度",
				fieldStyle:"background:#eeeeee;color:#515151;",
				labelSeparator : ':',
				id:"bs_totallen",
				name:"bs_totallen",
				readOnly:true,
				value:"",
				xtype:"numberfield"
		     },{
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_recorder",
				editable:true,
				fieldLabel:"录入人",
				fieldStyle:"background:#eeeeee;color:#515151;",
				labelSeparator : ':',
				id:"bs_recorder",
				name:"bs_recorder",
				readOnly:true,
				value:"",
				xtype:"textfield"
		     },{
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_date",
				editable:true,
				fieldLabel:"录入日期",
				fieldStyle:"background:#eeeeee;color:#515151;",
				labelSeparator : ':',
				id:"bs_date",
				name:"bs_date",
				readOnly:true,
				xtype:"datefield"
		     },{
				cls:"form-field-allowBlank",
				columnWidth:0.25,
				dataIndex:"bs_maxdate",
				editable:true,
				fieldLabel:"上次日期",
				fieldStyle:"background:#eeeeee;color:#515151;",
				labelSeparator : ':',
				id:"bs_maxdate",
				name:"bs_maxdate",
				readOnly:true,
				xtype:caller=='BarCodeSet!BATCH'?"textfield":"hidden"
		    }],
	buttonAlign:'center',
	tbar:{style:{background:'#fff'},margin:'0 0 5 0',items:[{
		xtype: 'erpSaveButton',
		id:"saveButton"
	},{
		xtype: 'erpUpdateButton',
		id:"updateButton"
	},{
		margin:'0 0 0 5',
		cls:'x-btn-gray',
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
		margin:'0 0 0 5',
		cls:'x-btn-gray',
		iconCls: 'x-nbutton-icon-download',
		text: '下载数据',
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
				var grids=Ext.ComponentQuery.query('grid');									
				var obj =new Object();
				if(grids){
					Ext.each(grids,function(g,index){
						if(!g.caller){
							if(g.mainField&&g.mainField!='null')
							obj[caller]=g.mainField;
						
						}else{
							if(g.mainField&&g.mainField!='null')
							obj[g.caller]=g.mainField;
						}
					
					});
				}							
				form.saveAsExcel(id,caller,encodeURI(Ext.JSON.encode(obj)));
			}
		}
	},'->',{
		xtype: 'erpCloseButton',
		id:'close'
	}]},
	initComponent : function(){
		var me = this;
	    	me.FormUtil=Ext.create('erp.util.FormUtil');
	    	me.GridUtil=Ext.create('erp.util.GridUtil');
	    	me.BaseUtil=Ext.create('erp.util.BaseUtil');
			formCondition = getUrlParam('formCondition');
			this.getData();
			this.callParent(arguments); 
			if(this.enableTools) {
				me.setTools();
			}
	},
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
	saveAsExcel:function(id,caller){
		if(id==null || id =='') showMessage('提示','无法导出空数据单据',1000);
		else window.location.href=basePath+'excel/savePanelAsExcel.action?id='+id+"&caller="+caller+"&_noc=1";
	},
	getData:function(){
		var me = this;
		//从url解析参数
		if(formCondition != null && formCondition != '')
			Ext.Ajax.request({
	        	url : basePath + 'scm/reserve/getBarcodeRuleData.action',
	        	params: {
	        		caller: caller, 
	        		condition: formCondition.split("&")[0],
	        		_noc: (getUrlParam('_noc') || me._noc)
	        	},
	        	method : 'post',
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		if(res.exceptionInfo != null){
	        			showError(res.exceptionInfo);return;
	        		}else if(res.data==null){
	        			Ext.Ajax.request({
							url : basePath + "scm/reserve/getCurrent.action",
							params: {},
							method : 'post',
							callback : function(options,success,response){
								var res = new Ext.decode(response.responseText);
								if(res.exceptionInfo != null){
									showError(res.exceptionInfo);return;
								}
								if(res.man&&res.date){
									Ext.getCmp("bs_recorder").setValue(res.man);
									Ext.getCmp("bs_date").setValue(res.date);
								}
							}
						});
						Ext.getCmp("updateButton").hide();
	        		}else{
	        			Ext.getCmp("saveButton").hide();
	        			me.setFormValues(res.data);
	        		}
	        	}
	        });
	},
	setFormValues : function(data){
		var form = Ext.getCmp('form');
		form.getForm().setValues(data);
	}
});