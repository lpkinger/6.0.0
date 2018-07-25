Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.StepCollection', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'pm.mes.StepCollection','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({     	
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
			},
    		'erpSaveButton': {
    			click: function(btn){
    				var grid = Ext.getCmp("grid");
    				var s = grid.getStore().data.items;//获取store里面的数据
    				var cd_stepcode = Ext.getCmp("cd_stepcode").value;
    				var cr_prodcode = Ext.getCmp("cr_prodcode").value;
				    for(var i=0;i<s.length;i++){//添加sp_stepcode 为cd_stepcode
					    s[i].data['sp_stepcode'] = cd_stepcode;	    
					    s[i].data['sp_mothercode'] = cr_prodcode;	
	    			}
	    			this.FormUtil.onUpdate(this);
	    		},
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value == 'AUDITED'){
    					btn.hide();
    				}
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		}   			
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});