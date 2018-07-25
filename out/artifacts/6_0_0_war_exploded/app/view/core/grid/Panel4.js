/**
 * ERP项目gridpanel通用样式1
 */
Ext.define('erp.view.core.grid.Panel4',{ 
	extend: 'Ext.grid.Panel', 
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	alias: 'widget.erpGridPanel4',
	region: 'south',
	layout : 'fit',
	id: 'grid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    tbar: {xtype: 'erpToolbar3'},
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		startCollapsed: true,
        groupHeaderTpl: '{name} (Count:{rows.length})'
    }),{
        ftype : 'summary',
        showSummaryRow : false,//不显示默认合计行
        generateSummaryData: function(){
            var me = this,
	            data = {},
	            store = me.view.store,
	            columns = me.view.headerCt.getColumnsForTpl(),
	            i = 0,
	            length = columns.length,
	            //fieldData,
	            //key,
	            comp;
            //将feature的data打印在toolbar上面
	        for (i = 0, length = columns.length; i < length; ++i) {
	            comp = Ext.getCmp(columns[i].id);
	            data[comp.id] = me.getSummary(store, comp.summaryType, comp.dataIndex, false);
	            var tb = Ext.getCmp(columns[i].dataIndex + '_' + comp.summaryType);
	            if(tb){
	            	tb.setText(tb.text.split(':')[0] + ':' + data[comp.id]);
	            }
	        }
	        return data;
        }
    }],
	initComponent : function(){ 
		// 通过参数传递getUrl，可以获取非标准的grid配置及数据
		var me = this, getUrl = decodeURIComponent(me.BaseUtil.getUrlParam('getUrl') || 'common/singleGridPanel.action'),
			urlCondition = me.BaseUtil.getUrlParam('gridCondition');
		urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
		gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
		gridCondition = gridCondition + urlCondition;
    	gridCondition = gridCondition.replace(/IS/g,"=");
		if(gridCondition.search(/!/)!=-1){
			gridCondition = gridCondition.substring(0,gridCondition.length-4);
		}
		gridCondition = gridCondition == "" ? (me.defaultCondition || "") : gridCondition;
    	var gridParam = {caller: me.caller || caller , condition:this.condition || gridCondition, start: 1, end: 500};
    	me.GridUtil.getGridColumnsAndStore(me, getUrl, gridParam, "");
    	me.callParent(arguments);
	},
	getGridStore: function(){
		var util = this.GridUtil,
			msg = util.checkGridDirty(this);
		if(msg == '') {
			showMessage('警告', '没有新增或修改数据.');
			return null;
		} else {
			return util.getGridStore(this);
		}
	}
});