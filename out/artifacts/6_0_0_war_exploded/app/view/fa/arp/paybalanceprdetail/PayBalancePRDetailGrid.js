Ext.define('erp.view.fa.arp.paybalanceprdetail.PayBalancePRDetailGrid',{
	extend:'Ext.grid.Panel',
	alias:'widget.paybalanceprdetail',
	requires:['erp.view.fa.arp.paybalanceprdetail.PBPDtoolbar'],
	layout:'fit',
	id:'paybalanceprdetailGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    detno:'pbpd_detno',
    keyField:'pbpd_id',
    mainField:'pbpd_pbid',
    columns:[],
    lastSelectedRecord:[],
    multiselected: [],
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
	selModel: Ext.create('Ext.selection.CheckboxModel',{
		checkOnly:true,
    	ignoreRightMouseSelection : false,
        getEditor: function(){
        	return null;
        },
        onHeaderClick: function(headerCt, header, e) {
            if (header.isCheckerHd) {
                e.stopEvent();
                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
                if (isChecked) {
                    this.deselectAll(true);
                    var grid = Ext.getCmp('paypleasedetailGrid');
                    this.deselect(grid.multiselected);
                    grid.multiselected = new Array();
                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
	                Ext.each(els, function(el, index){
	                	el.setAttribute('class','x-grid-row-checker');
	                });
                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
                } else {
                	var grid = Ext.getCmp('paypleasedetailGrid');
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
			condition = urlCondition.replace('pbd_pbid','pbpd_pbid');
			condition = condition.replace('IS','=');
		}
		//通过pp_id的值按条件查找PayPleaseDetail 表中的数据  condition为查找PayPleaseDetail的时候拼成的条件
		
		this.getMyData(condition);
	},

	getMyData:function(condition){
		var me = this;
		var caller = (me.caller&&me.caller!='')?me.caller:'PBPDGird';
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