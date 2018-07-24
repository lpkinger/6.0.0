Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.resourcemgr.TrainOrder', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'crm.marketmgr.resourcemgr.TrainOrder','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.grid.YnColumn','core.button.Scan',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.button.ResSubmit',
    			'core.trigger.DbfindTrigger','core.form.YnField','core.button.DeleteDetail','core.button.Upload','core.form.FileField',
    			'core.trigger.MultiDbfindTrigger','core.form.MultiField','core.button.TurnReturn','core.trigger.TextAreaTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpTurnReturnButton':{
    			beforerender:function(btn){
    				btn.setText('复制表单');
    			},
    			click:function(){
    				Ext.Ajax.request({
 			        	url : basePath + 'plm/project/copyProjectTG.action',
 			        	params: {
 			        		id: Ext.getCmp('to_id').value
 			        	},
 			        	method : 'post',
 			        	callback : function(options,success,response){
     			        	var res = new Ext.decode(response.responseText);
     			        	if(res.exceptionInfo){
     			        		showError(res.exceptionInfo);return;
     			        	} 
     			        	if(res.log)
    							showMessage('提示', res.log);
     			        }
 			        });
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}			
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addTrainOrder', '新增产品考核下达', 'jjsps/crm/marketmgr/resourcemgr/trainOrder.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){    			
    				me.FormUtil.onDelete((Ext.getCmp('to_id').value));
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('to_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('to_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('to_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('to_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('to_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},    			
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('to_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('to_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},     			
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('to_id').value);
    			}
    		},
    		'field[name=to_tpname]': {
	 			   afterrender:function(f){
	 					 f.setFieldStyle({
		   					 'color': 'blue'
		   				  });
		   				  f.focusCls = 'mail-attach';
		   				   var c = Ext.Function.bind(me.openRelative, me);
		   				   Ext.EventManager.on(f.inputEl, {
		   					   mousedown : c,
		   					   scope: f,
		   					   buffer : 100
		   				   });
	 			   }
	   		}
    	});
    },
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	openRelative:function(e, el, obj){
		if(Ext.getCmp('to_tpname').value=='')return;
		var tpcode=Ext.getCmp('to_tpcode').value;
		var url='jsps/crm/marketmgr/marketresearch/preView.jsp?_noc=1&formCondition=px_codeIS'+tpcode;
		this.FormUtil.onAdd('PXReporttemplate!PreView', '模板预览', 
				url);
		
	}
});