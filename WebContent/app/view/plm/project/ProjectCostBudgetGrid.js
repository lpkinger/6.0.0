Ext.define('erp.view.plm.project.ProjectCostBudgetGrid',{
	extend:'Ext.grid.Panel',
	alias:'widget.projectcostbudget',
	requires:['erp.view.plm.project.PBPDtoolbar'],
	layout:'fit',
	id:'projectCostBudgetGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    caller:'ProjectCostBudget',
    detno:'pcb_detno',
    keyField:'pcb_id',
    mainField:'pcb_prid',
    columns:[],
    bodyStyle:'bachgroud-color:#f1f1f1;',
    plugins:Ext.create('Ext.grid.plugin.CellEditing',{
    	clicksToEdit:1
    }),
   	features : [Ext.create('Ext.grid.feature.Grouping',{
   		//startCollapsed: true,
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
	bbar:{
		xtype: 'erpPBPDtoolbar'
	},
	GridUtil:Ext.create('erp.util.GridUtil'),
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	setReadOnly: function(bool){
		this.readOnly = bool;
	},
	/**
	 * 修改为selection改变时，summary也动态改变
	 */
	getMultiSelected: function(){
		var grid = this;
        var items = grid.selModel.getSelection();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		grid.multiselected.push(item);
        	}
        });
		return Ext.Array.unique(grid.multiselected);
	},
	initComponent:function(){
		this.callParent(arguments);
		//得到页面上显示的formCondition属性
		var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
		console.log(urlCondition);
		var condition='';
		if(urlCondition){
			condition = urlCondition.replace('pp_prid','pcb_prid');
			condition = condition.replace('IS','=');
		}
		//通过pp_id的值按条件查找PayPleaseDetail 表中的数据  condition为查找PayPleaseDetail的时候拼成的条件
		
		this.getMyData(condition);
	},

	getMyData:function(condition){
		var me = this;
		var caller = (me.caller&&me.caller!='')?me.caller:'ProjectCostBudget';
		var params = {
				caller:caller,
				condition:condition
		};
		
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me,params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me,'common/singleGridPanel.action',params);			
			
		}
	}
});