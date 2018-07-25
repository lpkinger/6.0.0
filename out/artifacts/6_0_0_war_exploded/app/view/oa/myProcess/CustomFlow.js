Ext.define('erp.view.oa.myProcess.CustomFlow',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 0%',
					saveUrl: 'oa/saveCustomFlow.action',
					deleteUrl: 'oa/deleteCustomFlow.action',
					updateUrl: 'oa/updateCustomFlow.action',
					getIdUrl: 'common/getId.action?seq=CUSTOMFLOW_SEQ',
					keyField: 'cf_id',
					codeField: 'cf_id',
					//statusField: 'cf_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 100%', 
					detno: 'cfd_detno',
					necessaryField: 'cfd_actorUsers',
					keyField: 'cfd_id',
					mainField: 'cfd_cfid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});