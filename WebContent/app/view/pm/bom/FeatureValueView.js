Ext.define('erp.view.pm.bom.FeatureValueView',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'FeatureProductViewport', 
				layout: 'anchor', 
				items: [{
				  xtype:'erpFormPanel',
				  anchor: '100% 25%',
				  params:{
					caller:caller,
					condition:formCondition
				  }
				},{
					xtype:'textfield',
					columnWidth: 0.2,
					cls: "form-field-allowBlank",
					fieldLabel:'ID',
					hidden:true,
					readOnly:true,
					id:'id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 75%', 
					condition:gridCondition,
					bbar:['->',{
						text:'BOM多级展开',
						id: 'expand'
					},{
						text:'BOM树型查看',
						id: 'find'
					}]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});