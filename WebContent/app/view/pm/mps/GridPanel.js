Ext.define('erp.view.common.batchDeal.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpBatchDealGridPanel',
	id: 'batchDealGridPanel', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    multiselected: [],
    store: [],
    columns: [],
    bodyStyle:'background-color:#f1f1f1;',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }),
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		//startCollapsed: true,
   		hideGroupedHeader: true,
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
    selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	ignoreRightMouseSelection : false,
			listeners:{
	            selectionchange:function(selectionModel, selected, options){
	          
	            }
	        },
	        getEditor: function(){
	        	return null;
	        },
	        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
	        	var me = Ext.getCmp('batchDealGridPanel');
	        	var bool = true;
	        	var items = me.selModel.getSelection();
	            Ext.each(items, function(item, index){
	            	if(this.index == record.index){
	            		bool = false;
	            		me.selModel.deselect(record);
	            		Ext.Array.remove(items, item);
	            		Ext.Array.remove(me.multiselected, record);
	            	}
	            });
	            Ext.each(me.multiselected, function(item, index){
	            	items.push(item);
	            });
	            me.selModel.select(items);
	        	if(bool){
	        		view.el.focus();
		        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
		        	if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
		        		//checkbox.setAttribute('class','x-grid-row-checker-checked');//只是修改了其样式，并没有将record加到selModel里面
		        		me.multiselected.push(record);
		        		items.push(record);
		        		me.selModel.select(items);
		        	} else {
		        		me.selModel.deselect(record);
		        		Ext.Array.remove(me.multiselected, record);
		        		//checkbox.setAttribute('class','x-grid-row-checker');
		        	}
	        	}
	        },
	        onHeaderClick: function(headerCt, header, e) {
	            if (header.isCheckerHd) {
	                e.stopEvent();
	                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
	                if (isChecked) {
	                    this.deselectAll(true);
	                    var grid = Ext.getCmp('batchDealGridPanel');
	                    this.deselect(grid.multiselected);
	                    grid.multiselected = new Array();
	                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                } else {
	                	var grid = Ext.getCmp('batchDealGridPanel');
	                	this.deselect(grid.multiselected);
		                grid.multiselected = new Array();
		                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
		                Ext.each(els, function(el, index){
		                	el.setAttribute('class','x-grid-row-checker');
		                });
	                    this.selectAll(true);
	                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
	                }
	            }
	        }
	}),
	initComponent : function(){ 
		var gridParam = {caller: caller, condition: ''};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action?', gridParam, "");
    	this.callParent(arguments); 
	}
});