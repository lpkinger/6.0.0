Ext.define('erp.view.crm.marketmgr.annualPlans.Merchandising',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'Merchandising', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'common/updateCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=MERCHANDISING_SEQ',
					keyField: 'mh_id',
					codeField: 'mh_code',
					statusField: 'mh_statuscode'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'mhd_detno',
					necessaryField: 'mhd_prodcode',
					keyField: 'mhd_id',
					mainField: 'mhd_mhid',
					
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});