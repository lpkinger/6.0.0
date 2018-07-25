Ext.define('erp.view.crm.customermgr.customervisit.RecordDetailDet',{
	extend:'Ext.grid.Panel',
	alias:'widget.recordDetailDet',
	requires:['erp.view.crm.customermgr.customervisit.RecordDettoolbar'],
	layout:'fit',
	id:'recordDetailDet',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    detno:'pl_detno',
    keyField:'pl_id',
    mainField:'pl_vrid',
    columns:[],
    caller:'Players',
    bodyStyle:'bachgroud-color:#f1f1f1;',
    plugins:Ext.create('Ext.grid.plugin.CellEditing',{
    	clicksToEdit:1
    }),
	bbar:{
		xtype: 'recordDettoolbar',
		id:'recordDettoolbar'
	},
	test:0,
	GridUtil:Ext.create('erp.util.GridUtil'),
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	initComponent:function(){
		this.callParent(arguments);
		//得到页面上显示的formCondition属性
		var urlCondition = this.BaseUtil.getUrlParam('formCondition'), pp_id = -1;
		//定义通过IS拆分后的数值
		if(urlCondition) {
			urlCondition = urlCondition.replace(/IS/g, '=');
			pp_id = urlCondition.indexOf('=') > 0 ? urlCondition.substr(urlCondition.indexOf('=') + 1) : -1;
		}
		var condition = "pl_vrid=" + pp_id;	
		this.getMyData(condition);
	},
	getMyData:function(condition){
		var me = this;
		var params = {
				caller:"Players",
				condition:condition
		};		
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me,params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me,'common/singleGridPanel.action',params);						
		}
	}	
});