Ext.define('erp.view.fa.fix.mQuery.GridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpMQueryGridPanel',
	id: 'mquerygrid', 
 	emptyText : '无数据',
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
	tbar: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray'
	}, '->', {
		name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(btn){
    		var grid = Ext.getCmp('querygrid');
    		var condition = grid.defaultCondition || '';
    		grid.BaseUtil.createExcel(caller, 'detailgrid', btn.ownerCt.ownerCt.spellCondition(condition));
    	}
	}, '-', {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
    GridUtil: Ext.create('erp.util.GridUtil'),
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		headerWidth: 0
	}),
    plugins: [Ext.create('Ext.ux.grid.GridHeaderFilters')],
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	FormUtil: Ext.create('erp.util.FormUtil'),
  	features : [Ext.create('Ext.grid.feature.Grouping',{
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
		condition = this.BaseUtil.getUrlParam('urlcondition');
		condition = (condition == null) ? "" : condition;
		condition = condition.replace(/@/,"'%").replace(/@/,"%'");
		this.defaultCondition = condition;
		var gridParam = {caller: caller, condition: condition};
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");
		this.callParent(arguments); 
	},
	summary: function(){
		var me = this,
			store = this.store,
			value;
		Ext.each(me.columns, function(c){
			if(c.summaryType == 'sum'){
                value = store.getSum(store.data.items, c.dataIndex);
                me.down('tbtext[id=' + c.dataIndex + '_sum]').setText(c.header + '(sum):' + value);		
			} else if(c.summaryType == 'count'){
                value = store.getCount();
                me.down('tbtext[id=' + c.dataIndex + '_count]').setText(c.header + '(count):' + value);			
			} else if(c.summaryType == 'average'){
				value = store.getAverage(c.dataIndex);        		        			
				me.down('tbtext[id=' + c.dataIndex + '_average]').setText(c.header + '(average):' + value);
			}		
		});
	},
	listeners: {
        'headerfiltersapply': function(grid, filters) {
        	grid.summary();
        	return true;
        }/*,
        'itemclick':function(grid,record){
        	var me = this;
        	if(record.data.cm_showtype!='1'){
        		
        		me.FormUtil.onAdd('showCmDetail', "查询明细", 'jsps/fa/ars/showCmDetail.jsp?showtype='+record.data.cm_showtype+'&cmid='+record.data.cm_id+'&currency='+record.data.cm_currency+'&custcode='+record.data.cm_custcode+'&custname='+record.data.cu_name+'&yearmonth='+record.data.cm_yearmonth);
        	}
        	
        }*/
	},
	viewConfig: { 
        getRowClass: function(record) { 
        	return record.get('cm_showtype')=='1'?'custom':'custom-alt';
//            return record.get('index')%2 == 0 ? (!Ext.isEmpty(record.get('cm_id')) ? 'custom-first' : 'custom') : 
//            	(!Ext.isEmpty(record.get('cm_id')) ? 'custom-alt-first' : 'custom-alt');
        } 
    }
});