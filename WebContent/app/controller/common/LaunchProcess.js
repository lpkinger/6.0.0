Ext.QuickTips.init();
Ext.define('erp.controller.common.LaunchProcess', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'common.JProcess.LaunchProcess','common.JProcess.LaunchContainer','core.button.Save','core.button.Close'
    	
    	],
    init:function(){
    	var me = this;
    	this.control({
    		
    	})},
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getTools: function(){
        return [{
        	xtype:'tool',
        	type:'up'
        },{
            xtype: 'tool',
            type: 'gear',
            handler: function(e, target, panelHeader, tool){
                var portlet = panelHeader.ownerCt;
                portlet.setLoading('Working...');
                Ext.defer(function() {
                    portlet.setLoading(false);
                }, 2000);
            }
        },{
        	xtype:'tool',
        	type:'close'
        }];
    },
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	}             
});