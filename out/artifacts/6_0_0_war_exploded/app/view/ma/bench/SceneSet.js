Ext.define('erp.view.ma.bench.SceneSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, {
			items : [{
				xtype : 'erpFormPanel',
				anchor : '100% 30%',
				title : '场景设置',
				saveUrl : 'bench/ma/saveScene.action',
				deleteUrl : 'bench/ma/deleteScene.action',
				updateUrl : 'bench/ma/updateScene.action',
				dumpable : true
			}, {
				xtype:'tabpanel',
				id: 'tab',
			 	anchor : '100% 70%',
				items:[{
					title :'场景按钮',
					id : 'SceneButtonSet',
					xtype : 'erpGridPanel2',
					caller :'SceneButtonSet',
					condition : gridCondition!=null?gridCondition.replace(/IS/g, "=").replace('sg','sb'):'',
					keyField : 'sb_id'
				}]
			}]
		});
		me.callParent(arguments); 
	} 
});