Ext.define('erp.view.plm.project.ProjectFileList',{ 
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
				region:'north',
				style:{
					background:'#F2F2F2'
				},
				height:30				
			},{
				xtype:'erpProjectFileListTree',
				region:'west'
			},{
				xtype:'erpProjectFileListTreeGrid',
				region:'center'
			}]
		}); 
		me.callParent(arguments); 
	} 
});