Ext.define('erp.view.ma.update.Empdbfind', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			layout:'border',
			items : [ {
				xtype : "empgridleft",
				region: 'west',
			   	flex:1,
			   	tbar: [{ xtype: 'button',id:'addEmp', text: '添加',iconCls : 'x-button-icon-add',cls: 'x-btn-gray' ,
					   	handler : function(b){
					   	   	var gridL=Ext.getCmp('dbfindGridPanel');
					   	   	gridL.addToRight();
					   	}
			   	}],
				bbar : {
					xtype : 'erpMultiDbfindToolbar',
					id : 'pagingtoolbar',
					displayInfo : true
				}
			},{
				region: 'center',
				xtype: 'empgridright',
				id:'selectgrid',
			   	flex:1,
			   	emptyText : $I18N.common.grid.emptyText,
			   	tbar: [
				  { xtype: 'button',id:'removeAll', text: '移除',iconCls : 'x-button-icon-close',cls: 'x-btn-gray' ,
				  	handler : function(b){
					   	   	var gridL=Ext.getCmp('selectgrid');
					   	   	gridL.removeFromRight();
					   	}}
				]
			}]
		});
		me.callParent(arguments);
	}
});