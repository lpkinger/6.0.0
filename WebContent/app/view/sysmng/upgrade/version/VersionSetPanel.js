/**
 *冻结字段筛选勾选保存面板
 */
Ext.define('erp.view.sysmng.upgrade.version.VersionSetPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.VersionSetPanel',
	id: 'versionsetpanel', 
	closeAction:'hide',	
	border:false,
		layout: {
	    type: 'hbox',
	    pack: 'start',
	    align: 'stretch'
	},
	//title:'功能标识',
	cls:"x-panel-header-default",
	requires: [],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	items:[{
				 
				xtype:'VersionAllTreePanel',				
				width:580
				
			},
			{
				 
				xtype:'VersionPanelPanel',
				autoScroll:true,
				flex:2
			}
			],
	initComponent : function(){ 
		this.callParent(arguments);		
	}
	
});