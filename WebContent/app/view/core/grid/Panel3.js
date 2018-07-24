/**
 * form配置维护，从表配置gridpanel
 */
Ext.define('erp.view.core.grid.Panel3',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel3',
	region: 'south',
	layout : 'fit',
	id: 'grid3', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    bbar: {xtype: 'erpToolbar'},
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    necessaryField: '',//必填字段
    detno: '',//编号字段
    keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	initComponent : function(){ 
		gridCondition = this.BaseUtil.getUrlParam('gridCondition'); 
    	gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
    	var gridParam = {caller: 'DetailGrid', condition: gridCondition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments); 
	}
});