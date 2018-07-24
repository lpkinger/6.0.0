Ext.define('erp.view.fs.credit.CustFAReport',{ 
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
					saveUrl: 'fs/credit/saveCustFAReport.action',
					updateUrl: 'fs/credit/updateCustFAReport.action',
					deleteUrl: 'fs/credit/deleteCustFAReport.action',
					getIdUrl: 'common/getId.action?seq=CUSTFAREPORT_SEQ',
					keyField: 'cr_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%', 
					detno: 'crd_detno',
					keyField: 'crd_id',
					mainField: 'crd_crid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});