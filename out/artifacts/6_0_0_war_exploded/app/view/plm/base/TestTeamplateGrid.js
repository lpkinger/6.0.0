Ext.define('erp.view.plm.base.TestTeamplateGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpTestTeamplateGrid',
	layout : 'fit',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    condition:null,
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
    caller: null,
	initComponent : function(){ 
	    var condition='';
	    if(this.condition!=null){
	    condition=this.condition;
	    }
    	var gridParam = {caller: this.caller || caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments);  
	}
});