Ext.define('erp.view.oa.persontask.myAgenda.SeeAgenda',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'border', 
				items: [{
					xtype: 'erpSeeAgendaFormPanel'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});