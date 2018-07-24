Ext.define('erp.view.pm.mould.BOMMould',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BOMMouldViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 48%',
					saveUrl: 'pm/mould/saveBOMMould.action',
					deleteUrl: 'pm/mould/deleteBOMMould.action',
					updateUrl: 'pm/mould/updateBOMMould.action',
					auditUrl: 'pm/mould/auditBOMMould.action',
					printUrl: 'pm/mould/printsingleBOMMould.action',
					resAuditUrl: 'pm/mould/resAuditBOMMould.action',
					submitUrl: 'pm/mould/submitBOMMould.action',
					resSubmitUrl: 'pm/mould/resSubmitBOMMould.action',
					getIdUrl: 'common/getId.action?seq=BOM_SEQ',
					keyField: 'bo_id',
					statusField: 'bo_status',
					statuscodeField: 'bo_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 52%', 
					detno: 'bd_detno',
					keyField: 'bd_id',
					mainField: 'bd_bomid',
					necessaryField: 'bd_soncode',
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