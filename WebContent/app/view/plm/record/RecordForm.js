Ext.define('erp.view.plm.record.RecordForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpRecordPanel',
	id: 'form', 
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '7 0 3 0',
	       fieldStyle : "background:#FFF;color:#515151;",
	       labelWidth:120,
	       blankText : $I18N.common.form.blankText
	},
	margin:'0 0 5 0',
	style:'background:#f2f2f2;border-bottom:1px solid #bdbdbd',
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
	getIdUrl: '',
	keyField: '',
	codeField: '',
	statusField: '',
	enableKeyEvents:true,
	//固定分组
	base_group:['erpAddButton','erpUpdateButton','erpDeleteButton','erpSaveButton','erpCopyButton','erpExecuteOperationButton','erpQueryButton'],
	logic_group:['erpSubmitButton','erpResSubmitButton','erpAuditButton','erpResAuditButton','erpEndButton','erpResEndButton',
	             'erpAccountedButton','erpResAccountedButton','erpPostButton','erpResPostButton','erpBannedButton','erpResBannedButton',
	             'erpForBiddenButton','erpResForBiddenButton','erpAutoInvoiceButton','erpCheckButton','erpResCheckButton','erpVoCreateButton',
	             'erpFreezeButton','erpNullifyButton'],
	work_group:['erpModifyCommonButton','erpExportExcelButton','erpImportExcelButton'],
	close_group:['erpCloseButton'],
	turn_group:['erpConsignButton'],
	initComponent : function(){ 
		formCondition =formCondition+getUrlParam('formCondition');
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	var param = {caller: caller, condition: formCondition};
        var me = this;
		me.getActiveTab().setLoading(true);
		Ext.Ajax.request({//拿到form的items
        	url : basePath +'plm/RecordFormItemsAndData.action',
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		me.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		var attach = new Array();
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		me.fo_id = res.fo_id;
        		me.fo_keyField = res.fo_keyField;
        		me.tablename = res.tablename;
        		if(res.keyField){
        			me.keyField = res.keyField;
        		}
        		if(res.statusField){
        			me.statusField = res.statusField;
        		}
        		if(res.statuscodeField){
        			me.statuscodeField = res.statuscodeField;
        		}
        		if(res.codeField){
        			me.codeField = res.codeField;
        		}
        		me.fo_detailMainKeyField = res.fo_detailMainKeyField;
        		Ext.each(res.items, function(item){
        			if(item.labelAlign&&item.labelAlign!='top'){
						item.labelAlign = 'right';
					}
					if(item.group == '0'){
						item.margin = '5 0 0 0';
					}
					//基本背景颜色
					item.labelStyle = "color:#515151";
					item.fieldStyle = 'background:#fff;color:#515151;';
					if(item.readOnly) {
						item.fieldStyle = 'background:#f3f3f3;';
					}
        			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
        				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
        					item.columnWidth = 0.25;
        				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
        					item.columnWidth = 0.5;
        				}
        			}
        		});
        		attach=res.attach != null ? res.attach : [];
        		var items = new Array();
			Ext.each(attach, function(){
				var path = this.fa_path;
				var name = '';
				if(contains(path, '\\', true)){
					name = path.substring(path.lastIndexOf('\\') + 1);
				} else {
					name = path.substring(path.lastIndexOf('/') + 1);
				}
				items.push({
					html: '<img src="' + basePath + 'resource/images/mainpage/things.png" width=16 height=16/>' + 
					 '<span>文件:' + name + '<a href="' + basePath + "common/download.action?path=" + path + '">下载</a></span><br/>'
				});
			});
			Ext.getCmp('attachs').add(items);
        		var grids = Ext.ComponentQuery.query('gridpanel');
        		//如果该页面只有一个form，而且form字段少于8个，则布局改变
        		if(grids.length == 0 && res.items.length <= 8){
        			Ext.each(res.items, function(item){
        				item.columnWidth = 0.5;
        			});
        			me.layout = 'column';
    			}
        		var data=new Ext.decode(res.data);
        		var formitems=me.setItems(this,res.items,data);
        		me.add(formitems);
        		if(res.data){
        		    Ext.getCmp('taskdescription').setValue(data.description);
        			//me.getForm().setValues(Ext.decode(res.data));
        			me.getForm().getFields().each(function (item,index,length){
        				item.originalValue = item.value;
        			});
        		}
        		me.FormUtil.setButtons(me,res.buttons);
        		//解析buttons字符串，并拼成json格式
        		/*var buttonString = res.buttons;
        		if(buttonString != null && buttonString.trim() != ''){
	        		var buttons = [];
	        		buttons.push('->');
	        		Ext.each(buttonString.split('#'), function(btn, index){
	        			var o = {};
	        			o.xtype = btn;
	        			buttons.push(o);
	        		});
	        		buttons.push('->');
	        		me.addDocked({//未到12个的
	        			xtype: 'toolbar',
	        	        dock: 'bottom',
	        	        style: {
	        				paddingLeft: (50 - buttons.length*4) + '%'
	        				//background: 'transparent'
	        			}, 
	        			defaults: {
	        				style: {
	        					marginLeft: '14px'
	        				}
	        			},
	        	        items: buttons
	        		});
        		}*/
        		
        		//触发afterload事件
				me.fireEvent('afterload',me);
        	}
        }); 
    	//this.FormUtil.getItemsAndButtons(this, 'plm/RecordFormItemsAndData.action', param);//
		this.callParent(arguments);
		//给页面加上ctrl+alt+s键盘事件,自动跳转form配置界面
		if(this.enableKeyEvents) {
			this.addKeyBoardEvents();
		}

	},
	setItems: function(form, items, data){
		var edit = true,hasData = true;
		if (data) {
			if(form.statuscodeField && data[form.statuscodeField] != null && data[form.statuscodeField] != '' &&  
					!(data[form.statuscodeField] == 'ENTERING' || data[form.statuscodeField] == 'UNAUDIT' || data[form.statuscodeField] == 'UNPOST')){//非在录入和已提交均设置为只读// && data[form.statuscodeField] != 'COMMITED'
				form.readOnly = true;
				edit = false;
			}
			if(form.statusCode && data[form.statusCode] == 'POSTED'){//存在单据状态  并且单据状态不等于空 并且 单据状态等于已过账
				form.readOnly = true;
				edit = false;
			}
//			statusCode
		} else {
			hasData = false;
		}
		var bool = 'a';
		if(items.length > 110&&items.length <=190){
			bool = 'b';
		}else if(items.length>190){
			bool = 'c';
		}
		Ext.each(items, function(item){
			if(item.xtype == 'hidden') {
				item.columnWidth = 0;
			}
			if(item.xtype == 'checkbox') {
				item.focusCls = '';
			}
			if (hasData && data[item.name]!=undefined) {
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
				}
			}
		});
		// 字段少的form
		if(form.minMode) {
			Ext.each(items, function(item){
				if(item.columnWidth >= 0 && item.columnWidth < 0.5){
					item.columnWidth = 0.5;
				} else if(item.columnWidth >= 0.5) {
					item.columnWidth = 1;
				}
			});
		}
		return items;
	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	return tab;
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
							if(g.xtype.indexOf('erpGridPanel') > -1)
								gridSet.push(window.caller);
							else if(g.caller)
								gridSet.push(g.caller);
						});
					}
					if(formSet.length > 0 || gridSet.length > 0) {
						url = "jsps/ma/multiform.jsp?formParam=" + formSet.join(',') + '&gridParam=' + gridSet.join(',');
					}
					//用于生成datalist 时要用的sn_lockpage
					parent.Ext.getCmp("content-panel").lockPage = window.location.pathname.replace('/ERP/', '').split("?")[0];
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', url);
				} else if(e.keyCode == Ext.EventObject.P) {
					me.FormUtil.onAdd('configs-' + caller, '逻辑配置维护(' + caller + ')', "jsps/ma/logic/config.jsp?whoami=" + caller);
				}
			}
		});
	},
	autoSetBtnStyle : function(from) {
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
	setTools: function(){
		var me = this, datalistId = getUrlParam('datalistId'), isRefererList = !!datalistId, 
			hasVoucher = !!me.voucherConfig, dumpable = me.dumpable,
			isNormalPage = !me.dumpable && !me.adminPage, hasList = !me.singlePage;
		return ['->',
					{	xtype:'button',				
						text:'选项',
						id:'buttons',
						cls:'x-btn-flowhelp',
						margin:'0 0 0 2',	
						listeners:{
							mouseover:function(btn){
								btn.showMenu();	
							},
							mouseout: function(btn) {
								setTimeout(function() {
									if(!btn.menu.over) {
										btn.hideMenu();
									}
			                    }, 20);
							}
						},
						menu: {
							listeners:{
								mouseover: function() {
									this.over = true;
								},
								mouseleave: function() {
									this.over = false;
									this.hide();
								}
							},
							items:[{
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
							},{
								iconCls: 'x-nbutton-icon-task',
								text: '发起任务',
								hidden: !isNormalPage,
								listeners : {
									click : function(btn) {
										var form = Ext.getCmp('form');
										if(!form.codeField){
											btn.disable(true);
										} 
										else form.addTask(form);
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
								
							}]}
					},
		{
			xtype : 'button',
			text:'凭证',
			cls:'x-btn-flowhelp',
			margin:'0 0 0 2',
			id:'Voucher',
			hidden : !hasVoucher,
			listeners : {
				click : function(t) {
					var form = t.ownerCt.ownerCt;
					form.createVoucher(form.voucherConfig);
				}
			}
		},		
		/*{
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
			cls:'x-btn-flowhelp',
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
			cls:'x-btn-flowhelp',
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
		},*/' '];
	}
});