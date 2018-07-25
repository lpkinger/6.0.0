Ext.QuickTips.init();
Ext.define('erp.controller.sys.SysInit', {
	extend: 'Ext.app.Controller',
	stores: ['sys.JobStore'],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
	views:['sys.base.DefaultPanel','sys.InitPortal','erp.view.core.form.YnField','core.grid.YnColumn','sys.ViewPort','sys.NavigationView','sys.Header','sys.base.EnterprisePortal',
	'sys.base.ParamsetPortal','sys.base.DetailWindow','sys.base.FormPortal','core.trigger.DbfindTrigger','sys.base.ComboSetGrid','sys.base.SimpleActionGrid','sys.base.ImportPortal',
	'sys.base.ModuleSetPortal','sys.init.InitImportGrid','sys.init.InitDataCheck','core.form.ColorField','sys.base.Toolbar','core.form.MultiField',
	'core.trigger.MultiDbfindTrigger','core.trigger.MultiDbfindTrigger2','core.trigger.AddDbfindTrigger','sys.sale.CurrencyPortal',
	'sys.base.ProgressBar','sys.job.JobSetGrid','sys.job.JobPersonGrid','core.form.MonthDateField'],
	init:function(){
		var me=this;
		this.control({
			'button[itemId=sa_addButton]':{
				click:function(btn,e){
					var tabP=btn.up('tabpanel'),_activeTab=tabP.activeTab,caller=_activeTab.caller;			
					if(caller){
						var win =  Ext.widget('detailwindow',{
							title:_activeTab.title,
							items:[Ext.widget('formportal',{
								region:'center', 
								caller:caller,
								saveUrl:_activeTab.saveUrl,
								deleteUrl: _activeTab.deleteUrl,
								updateUrl: _activeTab.updateUrl,
								getIdUrl:_activeTab.getIdUrl,
								currentTab:_activeTab,
								emptyGrid:_activeTab.emptyGrid,
								saveSuccess:me.DetailUpdateSuccess,
								defaultValues:_activeTab.defaultValues
							})]
						});
						win.showRelyBtn(win,btn);
					}
				}
			},
			/*'tab':{
				activate:function(tab){
					if(tab.card.xtype=='simpleactiongrid'){
						if(!tab.card.columnRendered){
							tab.card.getGridColumnsAndStore(tab.card,'common/singleGridPanel.action',tab.card.params);
							tab.card.columnRendered=true;
						}else tab.card.loadNewStore(tab.card,tab.card.params);
					}

				}			
			},*/
			'navigationview':{
				'itemclick':function(dataview,record){
                   if(record.data.module=='progress') me.getProgress();
                   else if(record.data.module=='import') me.getInitPortal();
                   else me.getCheckPortal();
				}	
			},
			'modulesetportal>checkbox':{
				'change':function(field,value){
					var obj=new Object(),property;
					if(field.id.indexOf('configs')>-1) property='data';
					else property='enable';
					obj[property]= typeof value === 'boolean' ? (value ? 1 : 0) : (field.xtype == 'radiogroup' ? Ext.Object.getValues(value)[0] : value);
					obj.id=field.id.split("-")[1];
					me.saveParamSet(field, value,unescape(escape(Ext.JSON.encode(obj))),me.changeInputValue);
				}
			},
			'modulesetportal>radiogroup':{
				'change':function(field,value){
					var obj=new Object();
					obj.data= typeof value === 'boolean' ? (value ? 1 : 0) : (field.xtype == 'radiogroup' ? Ext.Object.getValues(value)[0] : value);
					obj.id=field.id.split("-")[1];
					me.saveParamSet(field,value, unescape(escape(Ext.JSON.encode(obj))),me.changeInputValue);
				}
			},
			'modulesetportal>numberfield':{
				'blur':function(field){
					if(field.originalValue!=field.value){
						me.onSaveConfigs(field, field.value);
					}
				}
			},
			'modulesetportal>dbfindtrigger': {
    			aftertrigger: function(field, record, dbfinds) {
    				Ext.Array.each(dbfinds, function(d){
    					if(d.field == field.name) {
    						field.setValue(record.get(d.dbGridField));
    						me.onSaveConfigs(field, field.value);
    					}
    				});
    			}
    		},
    		'modulesetportal>colorfield':{
    			change:function(field,newvalue){
    				me.onSaveConfigs(field,newvalue);
    			}
    		},
    		'button[cls=x-dd-drop-ok-add]': {
    			click: function(btn) {
    				var f = btn.ownerCt, c = btn.config;
    				f.insert(f.items.length - 1, {
    					xtype: (c.dbfind ? 'dbfindtrigger' : 'textfield'),
	    				name: c.dbfind || c.code,
	    				readOnly: !c.dbfind && c.editable == 0,
	    				editable: c.editable == 1,
	    				clearable: true
    				});
    			}
    		},
    		'button[itemId=initfinish]':{
    			click:function(btn){
    				Ext.Ajax.request({//拿到tree数据
						url : basePath + 'ma/sysinit/finishInit.action',
						method:'post',
			        	callback : function(options,success,response){
			        		showResult('提示','初始化完毕!');
			        		// 跳转主页
			        		window.location.href = basePath;
			        	}
			        });
    			}
    		}
		});
	},
	onSaveConfigs:function(field,value){
		var obj=new Object(),value=value || field.value;
		obj.data= typeof value === 'boolean' ? (value ? 1 : 0) : (field.xtype == 'radiogroup' ? Ext.Object.getValues(value)[0] : value);
		obj.id=field.id.split("-")[1];
		this.saveParamSet(field,value, unescape(escape(Ext.JSON.encode(obj))),this.changeInputValue);
	},
	saveParamSet:function(field,value,update,fn){
		var params=new Object();
		if(field)params.argType=field.id.split("-")[0]; 
		params.update=update;
		Ext.Ajax.request({
			url: basePath + 'ma/sysinit/saveParamSet.action',
			params:params,
			method: 'POST',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					var res = Ext.JSON.decode(r.responseText);
					fn.call(null,field,value);
				}
			}
		});
	},
	changeInputValue:function(field,value){
		showResult('提示','修改成功!');
		field.originalValue=value;
		
	},
	getConfigs: function(caller, callback) {
		Ext.Ajax.request({
			url: basePath + 'ma/setting/configs.action?caller=' + caller,
			method: 'GET',
			callback: function(opt, s, r) {
				if(r && r.status == 200) {
					var res = Ext.JSON.decode(r.responseText);
					callback.call(null, res);
				}
			}
		});
	},
	getProgress:function(){console.log('getProgress');
		var win= Ext.create('widget.window', {
			title: '初始化进展',
			header: {
				titlePosition: 2,
				titleAlign: 'center'
			},
			closable: true,
			closeAction: 'hide',
			width:'80%',
			minWidth: 350,
			height: '60%',
			modal:true,
			tools: [{type: 'pin'}],
			layout: {
				type: 'border'
				//padding: 5
			},
			frame:true,
			items: [{
				xtype:'panel',
				layout:'fit',
				region: 'center',
				items:[{
					xtype:'dataview',
					id:'desktop',
					itemSelector:'div.normal',
					listeners:{
						'itemclick':function(view,record,item){
							Ext.get(record.data.itemId+'-yearitem').child('.bottom').addCls('bottomOver');
							 var detailsPanel = Ext.getCmp('detailspanel'); 
							 var panel = Ext.widget('panel', { // Equivalent to Ext.create('widget.panel')
							     title: record.data.desc,
							     html:'<h1>'+record.data.desc+'</h1>'
							 });
							detailsPanel.getEl().mask('updating details...');
						        detailsPanel.removeAll();
						        detailsPanel.add(panel);
						        detailsPanel.getEl().unmask();
						}
					},
					tpl:[
					     '<div class="yearlist">',
					     '<div class="start year">',
					     '<div class="top">开始</div>',
					     '<div class="bottom"></div></div>',								    
					     '<div class="space"><div class="line">&nbsp;</div></div>',
					     '<tpl for=".">',
					     '<div class="normal year" year="{year}" id="{itemId}-yearitem">',
					     '<div class="top"></div>',
					     '<div class="bottom">{year}</div>',
					     '</div>',
					     '<div class="space"><div class="line">&nbsp;</div></div>',
					     '</tpl>',
					     '<div class="now year">',
					     '<div class="top"></div>',
					     '<div class="bottom">完成</div></div</div></div>',
					     '<div style="clear:both"></div>',
					     '</div>'],
					     store:Ext.create('Ext.data.Store', {
					    	 fields: [{ name: 'year' },
					    	          {name: 'itemId'},
					    	          {name:'desc'}
					    	          ],
					    	          data: [
					    	                 { year: '1',itemId:'1',desc:'企业信息'},
					    	                 { year: '2',itemId:'2',desc:'系统设置'},
					    	                 { year: '3',itemId:'3',desc:'组织人员'},
					    	                 { year: '4',itemId:'4', desc:'销售管理'},
					    	                 { year: '5',itemId:'5',desc :'采购管理'},
					    	                 { year: '6',itemId:'6',desc:'物料信息'},
					    	                 { year: '7',itemId:'7',desc:'生产制造'},
					    	                 { year: '8',itemId:'8',desc:'委外加工'},
					    	                 { year: '9',itemId:'9',desc:'财务会计'}
					    	                 ]
					     })
				}]
			}, {
				region: 'south',
				xtype:'container',
				id:'detailspanel',
				layout:{
					type:'vbox',
					align:'stretch',
					autoSize:true
				}

			}]
		});
		win.show();
	},
	getProgress1:function(){
		var win= Ext.create('widget.window', {
			title: '初始化进展',
			header: {
				titlePosition: 2,
				titleAlign: 'center'
			},
			closable: true,
			closeAction: 'hide',
			width:'80%',
			minWidth: 350,
			height: '60%',
			modal:true,
			tools: [{type: 'pin'}],
			layout: {
				type: 'border'
				//padding: 5
			},
			frame:true,
			items: [{
				xtype:'panel',
				layout:'fit',
				region: 'center',
				items:[{
					xtype:'dataview',
					id:'desktop',
					itemSelector:'div.step',
					listeners:{
						'itemclick':function(view,record,item){
							Ext.get(record.data.itemId+'-yearitem').child('.bottom').addCls('bottomOver');
							 var detailsPanel = Ext.getCmp('detailspanel'); 
							 var panel = Ext.widget('panel', { // Equivalent to Ext.create('widget.panel')
							     title: record.data.desc,
							     html:'<h1>'+record.data.desc+'</h1>'
							 });
							detailsPanel.getEl().mask('updating details...');
						        detailsPanel.removeAll();
						        detailsPanel.add(panel);
						        detailsPanel.getEl().unmask();
						}
					},
					tpl:[ 
					      '<div id="MyWizard" class="wizard"><ul class="steps">',
					      '<tpl for=".">',
					     '<li>',
					     '<span class="circle"></span>',
					     '<span class="font">初次沟通</span>',
					     '</li>',
					     '</tpl></ul></div>'],
					     store:Ext.create('Ext.data.Store', {
					    	 fields: [{ name: 'year' },
					    	          {name: 'itemId'},
					    	          {name:'desc'}
					    	          ],
					    	          data: [
					    	                 { year: '1',itemId:'1',desc:'企业信息'},
					    	                 { year: '2',itemId:'2',desc:'系统设置'},
					    	                 { year: '3',itemId:'3',desc:'组织人员'},
					    	                 { year: '4',itemId:'4', desc:'销售管理'},
					    	                 { year: '5',itemId:'5',desc :'采购管理'},
					    	                 { year: '6',itemId:'6',desc:'物料信息'},
					    	                 { year: '7',itemId:'7',desc:'生产制造'},
					    	                 { year: '8',itemId:'8',desc:'委外加工'},
					    	                 { year: '9',itemId:'9',desc:'财务会计'}
					    	                 ]
					     })
				}]
			}, {
				region: 'south',
				xtype:'container',
				id:'detailspanel',
				layout:{
					type:'vbox',
					align:'stretch',
					autoSize:true
				}

			}]
		});
		win.show();
	},
	getInitPortal:function (){
		var win= Ext.create('widget.window', {
			title: '数据导入',
			header: {
				titlePosition: 0,
				titleAlign: 'center'
			},
			closable: true,
			closeAction: 'hide',
			width:800,
			minWidth: 350,
			height: '80%',
			modal:true,
			layout: {
				type: 'fit'
			},
			frame:true,
			items: [{
				xtype:'initimportgrid'
			}]
		});
		win.show();
	},
	getCheckPortal:function(){
		var win= Ext.create('widget.window', {
			title: '数据检查',
			header: {
				titlePosition: 0,
				titleAlign: 'center'
			},
			closable: true,
			closeAction: 'hide',
			width:800,
			minWidth: 350,
			height: '80%',
			modal:true,
			layout: {
				type: 'fit'
			},
			frame:true,
			items: [{
				xtype:'initdatacheck'
			}],
			buttons:[{
				text:'检查'
			},{
				text:'确认完毕',
				itemId:'initfinish',
				tooltip:'确认初始化完毕'	
			}]
		});
		win.show();
	},
	DetailUpdateSuccess:function(activeTab,btn){
		activeTab.loadNewStore(activeTab,activeTab.params);
		var win=btn.up('window');
		if(win) win.close();
	}
});