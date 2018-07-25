Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.MJProjectChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mould.MJProjectChange','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
  			'core.button.ResSubmit','core.form.FileField',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField'      
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
			},
			'field[name=in_code]':{
				afterrender:function(f){
	 			   f.setFieldStyle({
	 				   'color': 'blue'
	 			   });
	 			   f.focusCls = 'mail-attach';
	 			   var c = Ext.Function.bind(me.openMJProject, me);
	 			   Ext.EventManager.on(f.inputEl, {
	 					   mousedown : c,
	 					   scope: f,
	 					   buffer : 100
	 			   });
				}
			},
			'erpSaveButton': {
				click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber(caller);//自动添加编号
    				}
    				this.FormUtil.beforeSave(this);
    			}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('wsc_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('wsc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addMJProjectChange', '新增模具委托保管书变更', 'jsps/pm/mould/MJProjectChange.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wsc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('wsc_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wsc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('wsc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('wsc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('wsc_id').value);
    			}
    		},
    		'textfield[name=wsc_wscode]': {
    			aftertrigger: function(field){
    				var form=field.ownerCt;
    				if(!Ext.isEmpty(field.getValue())){
    					Ext.getCmp('ws_newstf').setValue(Ext.getCmp('ws_stf').value);
    				}
    			}
    		},
		});
	}, 
	openMJProject : function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#wsc_wsid');
		if(i && i.value) {
			url = 'jsps/pm/mould/MJProject.jsp?formCondition=ws_idIS' + i.value + '&gridCondition=wd_wsidIS' + i.value;
			openUrl(url);
		}
	},
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});