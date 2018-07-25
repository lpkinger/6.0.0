/**
 * ERP项目gridpanel通用样式1
 */
Ext.define('erp.view.core.grid.Panel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel',
	region: 'south',
	layout : 'fit',
	id: 'grid1', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
	initComponent : function(){ 
    	var gridParam = {caller: caller + "!Child", condition: ""};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments);  
	}
});