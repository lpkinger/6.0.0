Ext.define('erp.view.scm.purchase.InquiryInlet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'inquiryinletform',
				anchor: '100% 50%',
				id:'form',
			},{
				xtype: 'tabpanel',
				anchor: '100% 50%',
				id:'tab',
				items:[{
					xtype: 'erpGridPanel2',
					id : 'prodtab',
					title : '物料明细',
					detno: 'ip_detno',
					necessaryField: 'ip_prodcode',
					keyField: 'ip_id',
					caller : 'InquiryInlet1',
					allowExtraButtons: true,
					bbar: {xtype: 'erpToolbar',id:'toolbar1'},
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				},{
					xtype: 'erpGridPanel2',
					id: 'vendtab' ,
					detno: 'iv_detno',
					title : '供&nbsp应&nbsp商',
					necessaryField: 'iv_vendcode',
					caller : 'InquiryInlet2',
					condition:condition!=null?condition.replace(/IS/g, "=").replace('bip_biid','biv_biid'):'',
					keyField: 'iv_id',
					allowExtraButtons: true,
					bbar: {xtype: 'erpToolbar',id:'toolbar2'},
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				},{
					xtype: 'erpGridPanel2',
					id: 'kindtab' ,
					detno: 'ik_detno',
					title : '物料种类',
					necessaryField: 'ik_pkcode',
					caller : 'InquiryInlet3',
					condition:condition!=null?condition.replace(/IS/g, "=").replace('bip_biid','biv_biid'):'',
					keyField: 'ik_id',
					allowExtraButtons: true,
					bbar: {xtype: 'erpToolbar',id:'toolbar3'},
					plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				        clicksToEdit: 1
				    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')]
				}],
			}]
		}); 
		me.callParent(arguments); 
	} 
});