Ext.define('erp.view.fa.arp.payplease.PayPleaseDetailDetGrid',{
	extend:'Ext.grid.Panel',
	alias:'widget.paypleasedetaildetGrid',
	requires:['erp.view.core.toolbar.Toolbar'],
	layout:'fit',
	id:'paypleasedetaildetGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    detno:'ppdd_detno',
    keyField:'ppdd_id',
    mainField:'ppdd_ppdid',
    columns:[],
    bodyStyle:'bachgroud-color:#f1f1f1;',
    plugins:Ext.create('Ext.grid.plugin.CellEditing',{
    	clicksToEdit:1
    }),
	bbar:{
		xtype: 'erpToolbar',
		enableExport: false,
		//enableDelete: false
	},
	test:0,
	GridUtil:Ext.create('erp.util.GridUtil'),
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	initComponent:function(){
		this.callParent(arguments);
		//得到页面上显示的formCondition属性
		var urlCondition = this.BaseUtil.getUrlParam('formCondition');
		//定义通过IS拆分后的数值
		var cons=null;
		//存在urlCondition的情况下
		if(urlCondition){
		//对urlCondition进行拆分  urlCondition的格式一半为pp_idIS1
			cons = urlCondition.split(/IS|=/);
		}
		var pp_id = cons ? cons[1] : 0;
//		按照pp_id的值得到 paypleaseDetail 表中的数据 然后按照ppd_detno 排序 取排序的第一条数据的ppd_id 
		var condition = "ppdd_ppdid=(select ppd_id from (select ppd_id,row_number() over (order by ppd_detno) rn from paypleasedetail where ppd_ppid="+pp_id+") where rn < 2)";
		this.getMyData(condition);
	},
	caller: 'PayPleaseDet',
	getMyData:function(condition){
		var me = this;
		var params = {
				caller:"PayPleaseDet",
				condition:condition
		};
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me,params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me,'common/singleGridPanel.action',params);			
		}
	},
	features : [{
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
	            	var val = data[comp.id];
	            	if(columns[i].xtype == 'numbercolumn' || /^numbercolumn-\d*$/.test(columns[i].columnId)) {
	            		val = Ext.util.Format.number(val, (columns[i].format || '0,000.000'));
	    			}
	            	tb.setText(tb.text.split(':')[0] + ':' + val);
	            }
	        }
	        return data;
        }
    }]
});