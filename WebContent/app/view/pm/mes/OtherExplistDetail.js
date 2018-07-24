Ext.define('erp.view.pm.mes.OtherExplistDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 30%',
				updateUrl: 'pm/mes/updateOtherExplistDetail.action',
				getIdUrl: 'common/getId.action?seq=OTHEREXPLISTDETAILL_SEQ',
				keyField: 'md_id',
				codeField: 'md_code',
				statusField:'ma_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 70%', 
				detno: 'oed_detno',
				keyField: 'oed_id',
				mainField: 'oed_mdid'
			}]
		}); 
		me.callParent(arguments); 
	} 
});