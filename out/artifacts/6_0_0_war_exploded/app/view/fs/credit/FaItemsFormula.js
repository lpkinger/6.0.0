Ext.define('erp.view.fs.credit.FaItemsFormula',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
			Ext.apply(this, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'fs/credit/saveFaItemsFormula.action',
					updateUrl: 'fs/credit/updateFaItemsFormula.action',
					deleteUrl: 'fs/credit/deleteFaItemsFormula.action',
					getIdUrl: 'common/getId.action?seq=FaItemsFormula_SEQ',
					keyField: 'fif_id',
					codeField: 'fif_code'
				}]
			}); 
		this.callParent(arguments); 
	}
});