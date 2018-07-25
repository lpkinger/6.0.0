Ext.QuickTips.init();
Ext.define('erp.controller.pm.mould.ModifyProcessing', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    RenderUtil: Ext.create('erp.util.RenderUtil'),
    views:[
    		'pm.mould.ModifyProcessing','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Add','core.button.Save','core.button.Close','core.button.DeleteDetail','core.grid.detailAttachCustom',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.grid.detailAttach','core.form.FileField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.button.SetMain'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpFormPanel': {},
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				me.onGridItemClick(selModel, record);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender:function(btn){
	   				 var statuscode=Ext.getCmp('bo_statuscode');
	   				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
	   		   			btn.hide(); 
	   				 }
   			 	},
    			click: function(btn){
    				var grid = Ext.getCmp('grid');
    				var items = grid.store.data.items;
    				var soncode = Ext.getCmp('bd_soncode').value;
    				me.GridUtil.onUpdate(Ext.getCmp('grid'));
    			}
    		}, 
   		 	'erpDeleteDetailButton':{
			   afterrender:function(btn){   				   
				 var statuscode=Ext.getCmp('bo_statuscode');
 				 if (statuscode&&statuscode.getValue()!='ENTERING'){  
 		   			btn.hide(); 
 				 }
			   }
		    }
			
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择 
    	//按照gridbutton表中的gb_conf取的 ID
    	if (Ext.getCmp('pic')) {
			Ext.getCmp('pic').setDisabled(false);
		}
    	if (Ext.getCmp('cust')) {
			Ext.getCmp('cust').setDisabled(false);
		}
    	this.GridUtil.onGridItemClick(selModel, record);
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});