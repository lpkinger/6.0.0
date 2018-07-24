Ext.define('erp.view.common.FormsDoc.FormsDoc',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				padding:'7 5 5 5',
				xtype : 'tbtext',
				id : 'toolbartext',
				text:'<span class="toolbartext">单据编号:</span><span class="toolbarcontent">' + formscode + '</span>',
				region:'north',
				style:{
					background:'#F2F2F2'
				},
				height:30				
			},{
				xtype:'erpFormsDocTree',
				region:'west'
			},{
				xtype:'erpFormsDocTreeGrid',
				region:'center'
			}]
		}); 
		me.callParent(arguments); 
	} 
});