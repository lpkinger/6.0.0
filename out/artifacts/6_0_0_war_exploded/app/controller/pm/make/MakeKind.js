Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeKind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.make.MakeKind','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger',
    		'core.trigger.AddDbfindTrigger','core.trigger.MultiDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
    				this.FormUtil.beforeSave(this);
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
    		'textfield[id=MK_TYPE]':{
    			change:function(f,newValue,oldValue){
    				var mk_batchtype = Ext.getCmp('mk_batchtype').value;
    				if(mk_batchtype=='TEST'){
    					if(newValue =='R'||newValue =='D'){
    						showError("批量类型为试产时，注意此时加工类型为返修或拆件");
    						return;
    					}
    				}
    			}
    		},
    		'textfield[id=mk_batchtype]':{
    			change:function(f,newValue,oldValue){
    				var mk_type = Ext.getCmp('MK_TYPE').value;
    				if(mk_type=='R' || mk_type=='D' ){
    					if(newValue =='TEST'){
    						showError("批量类型为试产时，注意此时加工类型为返修或拆件");
    						return;
    					}
    				}
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('mk_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMakeKind', '新增制造类型', 'jsps/pm/make/MakeKind.jsp');
    			}
    		},
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});