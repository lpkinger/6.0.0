Ext.QuickTips.init();
Ext.define('erp.controller.hr.emplmana.Trainmaterail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.emplmana.Trainmaterail','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Upload','core.button.Update','core.button.Delete','core.button.DownLoad',
    		'core.trigger.TextAreaTrigger','core.form.YnField'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				/*var form = me.getForm(btn);
    				//物料编号
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//申请编号
    				if(Ext.getCmp('pre_thisid').value == null || Ext.getCmp('pre_thisid').value == ''){
    					me.BaseUtil.getRandomNumber(null, null, 'pre_thisid');
    				}*/
    				this.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addTrainmaterail', '新增培训资料', 'jsps/hr/emplmana/train/trainmaterail.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				this.FormUtil.onDelete(Ext.getCmp('tm_id').value);
    			}
    		}
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});