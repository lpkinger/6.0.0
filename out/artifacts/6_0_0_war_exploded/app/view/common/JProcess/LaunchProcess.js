Ext.define('erp.view.common.JProcess.LaunchProcess',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout:'anchor', 
	initComponent : function(){ 
		var me = this;
		 Ext.apply(this, {
	            id: 'app-viewport',
	            layout: {
	                type: 'border',
	                padding: '0 5 5 5' // pad the layout from the window edges
	            },
	            items: [{
	                id: 'app-header',
	                xtype: 'box',
	                region: 'north',
	                height: 40,
	                style:'color: #596F8F;font-size: 22px;font-weight: 200;padding: 8px 15px;text-shadow: 0 1px 0 #fff;',
	                html: '发起流程'
	            },{
	                xtype: 'erpLaunchContainer',
	                autoScroll:true,
	                region: 'center',
	                layout:'column',
	                frame:true,
	                defaults:{
	                	 columnWidth:'0.5',
	                	 margin: '1 1 1 1',
	                	 style: {border:'3px solid #8E8E8E' }
	                }
	            }]
	        });
		me.callParent(arguments); 
	} 
});