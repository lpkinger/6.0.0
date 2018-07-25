Ext.define('erp.view.pm.mould.MouldProdHour',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MouldProdHourViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/mould/saveMouldProdHour.action',
					deleteUrl: 'pm/mould/deleteMouldProdHour.action',
					updateUrl: 'pm/mould/updateMouldProdHour.action',
					auditUrl: 'pm/mould/auditMouldProdHour.action',
					printUrl: 'pm/mould/printsingleMouldProdHour.action',
					resAuditUrl: 'pm/mould/resAuditMouldProdHour.action',
					submitUrl: 'pm/mould/submitMouldProdHour.action',
					resSubmitUrl: 'pm/mould/resSubmitMouldProdHour.action',
					getIdUrl: 'common/getId.action?seq=MouldProdHour_SEQ',
					keyField: 'mph_id',
					statusField: 'mph_status',
					statuscodeField: 'mph_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'mphd_detno',
					keyField: 'mphd_id',
					mainField: 'mphd_mphid',
					bbar: {xtype: 'erpToolbar',id:'toolbar',enableUp: false, enableDown: false},
					allowExtraButtons : true
				/*	viewConfig: {
						getRowClass: function(record) {
							if(record.data.bd_usestatus=="DISABLE"){ 
								return 'disable';
							}
						} 
					}*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});