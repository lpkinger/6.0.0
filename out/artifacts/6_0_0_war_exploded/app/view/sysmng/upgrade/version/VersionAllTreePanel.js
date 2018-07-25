/**
 *冻结字段筛选勾选保存面板
 */
Ext.define('erp.view.sysmng.upgrade.version.VersionAllTreePanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.VersionAllTreePanel',
	id: 'VersionAllTreePanel', 
	closeAction:'hide',
	 //layout: 'hbox',
	layout: 'anchor', 	
	border:false,
	autoScroll:true,
	title:'导航栏',
	//hidden : true,
	cls:"x-panel-header-default",
	requires: [],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	items:[{
				 
				xtype:'VersionTreePanel',
				anchor: '100% 100%'
				
			}
		
			],
	initComponent : function(){ 
		this.callParent(arguments);		
	}
	
});