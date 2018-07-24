Ext.define('erp.view.plm.project.TaskPanel',{
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpTaskPanel',
	id: 'taskPanel', 
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true,
	store: [],
	columns: [],
	GridUtil: Ext.create('erp.util.GridUtil'),
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	bodyStyle:'background-color:#f1f1f1;',
	multiselected: new Array(),
	features : [Ext.create('Ext.grid.feature.Grouping',{
		groupHeaderTpl: '{name} (Count:{rows.length})'
	})],
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		ignoreRightMouseSelection : false,
		checkOnly: true,
		listeners:{
			selectionchange:function(selectionModel, selected, options){

			}
		},
		getEditor: function(){
			return null;
		}
	}),
	caller: null,
	condition: null,
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	initComponent : function(){
		// 额外的plugin
		if(this.pluginConfig)
			this.plugins = Ext.Array.merge(this.plugins, this.pluginConfig);
		var param={
				caller: this.caller || caller,
				condition: condition,
		};
		this.GridUtil.getGridColumnsAndStore(this,'common/singleGridPanel.action',param);
		this.addEvents({
			storeloaded: true
		});
		this.callParent(arguments); 
	},
});