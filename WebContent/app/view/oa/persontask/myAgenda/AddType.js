Ext.define('erp.view.oa.persontask.myAgenda.AddType',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'desk', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'oa/persontask/myAgenda/saveAgendaType.action',
					deleteUrl: 'oa/persontask/myAgenda/deleteAgendaType.action',
					updateUrl: 'oa/persontask/myAgenda/updateAgendaType.action',
					getIdUrl: 'common/getId.action?seq=AGENDATYPE_SEQ',
					keyField: 'at_id',
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});