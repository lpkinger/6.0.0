Ext.define('erp.view.crm.marketmgr.marketresearch.Team',{ 
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
					anchor: '100% 35%',
					saveUrl: 'common/saveCommon.action?caller='+caller,
					deleteUrl: 'common/deleteCommon.action?caller='+caller,
					updateUrl: 'common/updateCommon.action?caller='+caller,
					getIdUrl: 'common/getId.action?seq=TEAM_SEQ',
					keyField: 'team_id',
					codeField:'team_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
					detno: 'tm_detno',
					//necessaryField: 'tm_employeeid',
					keyField:'tm_id',
					mainField: 'tm_teamid'
				 }
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});