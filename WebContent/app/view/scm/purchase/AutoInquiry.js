Ext.define('erp.view.scm.purchase.AutoInquiry',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 45%',
				saveUrl: 'scm/purchase/saveAutoInquiry.action',
				updateUrl: 'scm/purchase/updateAutoInquiry.action',
				deleteUrl: 'scm/purchase/deleteAutoInquiry.action',					
				getIdUrl: 'common/getId.action?seq=AUTOINQUIRY_SEQ',
				keyField: 'ai_id',
				codeField: 'ai_code'
			},{
				xtype: 'erpGridPanel2',
				detno: 'pk_detno',
				requires: [ 'erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu','Ext.ux.grid.GridHeaderFilters'],
				headerCt: Ext.create("Ext.grid.header.Container",{
			 	    forceFit: false,
			        sortable: true,
			        enableColumnMove:true,
			        enableColumnResize:true,
			        enableColumnHide: true
			     }),
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu'),Ext.create('Ext.ux.grid.GridHeaderFilters')],
			    selModel: Ext.create('Ext.selection.CheckboxModel',{
					headerWidth: 0
				}),
				anchor: '100% 55%',
				necessaryField: 'pk_code',
				keyField: 'pk_id',
				mainField: 'aid_aiid',
				allowExtraButtons: true,
				bbar: {xtype: 'erpToolbar',id:'toolbar2'},
				features:null
			}]
		}); 
		me.callParent(arguments); 
	} 
});