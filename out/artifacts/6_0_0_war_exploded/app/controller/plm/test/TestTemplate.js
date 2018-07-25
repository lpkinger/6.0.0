Ext.QuickTips.init();
Ext.define('erp.controller.plm.test.TestTemplate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'plm.test.TestTemplate','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
      var me=this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){    			
    				this.save(btn);
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
    				this.FormUtil.onDelete(Ext.getCmp('tt_id').value);
    			}
    		},
    		  'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTestTemplate', '新建项目模板', 'jsps/plm/test/testtemplate.jsp');
    			}
    		},
    		'erpSubmitButton': {    		
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('tt_id').value);
    			}
    		},
    	   'erpAuditButton': {
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('tt_id').value);
    			}
    		},    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	save: function(btn){
		var me = this;
		var form = me.getForm(btn);
		if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
		me.FormUtil.beforeSave(me);
	}
});