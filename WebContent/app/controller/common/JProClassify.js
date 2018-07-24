Ext.QuickTips.init();
Ext.define('erp.controller.common.JProClassify', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','common.JProcess.JProClassify','core.grid.Panel','core.button.Add','core.button.Submit','core.button.Audit',
    		'core.button.Save','core.button.Close','core.button.Print','core.button.Upload','core.button.Update','core.button.Delete',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpSaveButton': {
    			click: function(btn){
    				/*var form = me.getForm(btn);*/
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    		    					me.BaseUtil.getRandomNumber();//自动添加编号
    		    				}
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpDeleteButton' : {
    			beforerender:function(btn){
    				btn.hidden=false;
    			},
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('jc_id').value);
    			}
    		},
    		'erpPostButton' : {
    			click:function(btn){
    				me.FormUtil.onPost(Ext.getCmp('jc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender:function(btn){
    				btn.show();
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('JProClassify', '新增流程设置', 'jsps/common/jproClassify.jsp?whoami='+caller);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    		
    });},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	}             
});