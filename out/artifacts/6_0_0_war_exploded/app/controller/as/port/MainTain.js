Ext.QuickTips.init();
Ext.define('erp.controller.as.port.MainTain', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'core.form.Panel','core.grid.Panel2','as.port.MainTain','core.form.MultiField','core.form.FileField','core.button.AfterMarket',
    		'core.button.Add','core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print','core.button.TurnCustomer',
			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.trigger.AutoCodeTrigger',
	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': {
				itemclick: this.onGridItemClick
			},
			'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('mt_id').value);
    			}
    		},
    		'#mt_reason':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text4':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_date1':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text6_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_date2':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_date4_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text7_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text1' : {
    			beforerender : function(f){
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value != 'COMMITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    			}
    		},
    		'#mt_date3_user' : {
    			beforerender : function(f){
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value != 'COMMITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    			}
    		},
    		'#mt_text11_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text5':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text9_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value != 'COMMITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text12_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value != 'COMMITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text22_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text24_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text13_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_upload2':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text8_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
    		'#mt_text10_user':{
    			beforerender: function(f) {
    				var status = Ext.getCmp('mt_statuscode');
    				if(status.value == 'AUDITED'){
    					f.readOnly=false;
    					f.fieldStyle="background:#FFFAFA;color:#515151;;background:##fbfbfb;";
    				}
    				
    			}
    		},
			'#mt_type':{
    			change:function(field,newValue,oldValue,eOpts ){
    				if(newValue=='back'||newValue=='索赔'||newValue=='其它'){
    					Ext.getCmp('mt_ckcode').setReadOnly(true);
    					Ext.getCmp('mt_row').setReadOnly(true);
    					Ext.getCmp('mt_ckcode').setFieldStyle('background:#e0e0e0;');
    				}
    				if(newValue=='charge'){
    					Ext.getCmp('mt_ckcode').setReadOnly(false);
    					Ext.getCmp('mt_ckcode').allowBlank=false;
    					Ext.getCmp('mt_ckcode').setFieldStyle('background:rgb(255, 250, 250);color:#FF0000;');
    				}
    			}
    			
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn), codeField = Ext.getCmp(form.codeField);
					if(codeField.value == null || codeField.value == ''){
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},   		
			'erpUpdateButton': {
				afterrender:function(btn){
					var status = Ext.getCmp('mt_statuscode');
    				if(status.value != 'COMMITED'){
    					btn.show();
    				}
				},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('MainTain', '新增物料客户', 'jsps/as/port/maintain.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mt_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), c = Ext.getCmp('mt_code').value,
    				    items = grid.store.data.items, recorddate = Ext.getCmp('mt_applicationdate').value;
    				var bool = true;
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('mt_id').value);
    				}
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mt_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('mt_id').value);
    			}
    		},
    		'erpOnlineButton': {
    			afterrender: function(btn){
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mt_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mt_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('mt_id').value);
    			}
    		},
    		'erpAfterMarketButton': {
    			afterrender: function(btn){
    				btn.hide();
    				
    				/*btn.hide();*/
    				var status = Ext.getCmp('mt_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.show();
    					btn.setText('售后分析');
    				}else if(status && status.value == 'ONLINE'){
    					btn.show();
    					btn.setText('上线');
    				}else if(status && status.value == 'REPAIRING'){
    					btn.show();
    					btn.setText('返修结束');
    				}else if(status && status.value == 'RETURNING'){
    					btn.show();
    					btn.setText('归还');
    				}
    			},
    			click: function(btn){
    				var me = this;
    				var status = Ext.getCmp('mt_statuscode');
    				var form = Ext.getCmp('form');
    				var id=Ext.getCmp('mt_id').value;
    				if(form && form.getForm().isValid()){
    					form.marketUrl = form.marketUrl + "?caller=" + caller;
    					form.setLoading(true);//loading...
    					//清除流程
    					/*Ext.Ajax.request({
    						url : basePath + me.deleteProcess,
    						params: {
    							keyValue:id,
    							caller:caller,
    							_noc:1
    						},
    						method:'post',
    						async:false,
    						callback : function(options,success,response){
    			
    						}
    					});*/
    					Ext.Ajax.request({
    						url : basePath + form.marketUrl,
    						params: {
    							id: id,
    							value:status.value
    						},
    						method : 'post',
    						callback : function(options,success,response){
    							form.setLoading(false);
    							var localJson = new Ext.decode(response.responseText);
    							if(localJson.success){
    								//audit成功后刷新页面进入可编辑的页面 
    								showMessage('提示', '确认成功!', 1000);
    								window.location.reload();
    							} else {
    								if(localJson.exceptionInfo){
    									var str = localJson.exceptionInfo;
    									if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    										str = str.replace('AFTERSUCCESS', '');
    										showMessage("提示", str);
    										auditSuccess(function(){
    											window.location.reload();
    										});
    									} else {
    										showError(str);return;
    									}
    								}
    							}
    						}
    					});
    				} else {
    					me.checkForm();
    				}
    			}
    		},
    		
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('mt_statuscode');
    				if(status && (status.value == 'COMMITED' || status.value == 'ENTERING')){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('mt_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
				var reportName="application";
				var condition='{Application.ap_id}='+Ext.getCmp('ap_id').value+'';
				var id=Ext.getCmp('ap_id').value;
				me.FormUtil.onwindowsPrint(id,reportName,condition);
    			}
    		},	
		});
	},
	onGridItemClick: function(selModel, record){//grid行选择
		var status = Ext.getCmp('mt_statuscode');
		var grid=Ext.getCmp('grid');
		if(status.value == 'AUDITED'){
			grid.readOnly=false;
		}
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getStore : function(condition) {
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		grid.setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "MainTain",
				condition : condition
			},
			method : 'post',
			callback : function(options, success, response) {
				grid.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				var data = [];
				if (!res.data || res.data.length == 2) {
					me.GridUtil.add10EmptyItems(grid);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(
							/,]/g, ']'));
					if (data.length > 0) {
						grid.store.loadData(data);
					}
				}
			}
		});
	}
});