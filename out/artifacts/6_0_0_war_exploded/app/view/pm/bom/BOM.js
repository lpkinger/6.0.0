Ext.define('erp.view.pm.bom.BOM',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'BOMViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 48%',
					saveUrl: 'pm/bom/saveBOM.action',
					deleteUrl: 'pm/bom/deleteBOM.action',
					updateUrl: 'pm/bom/updateBOM.action',
					auditUrl: 'pm/bom/auditBOM.action',
					printUrl: 'pm/bom/printsingleBOM.action',
					resAuditUrl: 'pm/bom/resAuditBOM.action',
					submitUrl: 'pm/bom/submitBOM.action',
					resSubmitUrl: 'pm/bom/resSubmitBOM.action',
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