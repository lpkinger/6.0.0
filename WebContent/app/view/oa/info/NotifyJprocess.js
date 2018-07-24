Ext.define('erp.view.oa.info.NotifyJprocess',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 100%',
			    formCondition:formCondition,
			    readOnly: true
			}]
		}); 
		me.callParent(arguments); 
	} 
});