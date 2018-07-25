Ext.define('erp.view.common.VisitERP.BomTemplate',{ 
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
					//bbar: {xtype: 'erpToolbar',id:'toolbar',enableUp: false, enableDown: false},
					allowExtraButtons : true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});