Ext.define('erp.view.crm.customermgr.customervisit.InfoGrid',{
	extend:'Ext.grid.Panel',
	alias:'widget.InfoGrid',
	requires:['erp.view.crm.customermgr.customervisit.Infotoolbar'],
	layout:'fit',
	id:'InfoGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    detno:'pi_detno',
    keyField:'pi_id',
    mainField:'pi_vrid',
    caller:'ProductInfo',
    columns:[],
    bodyStyle:'bachgroud-color:#f1f1f1;',
    plugins:Ext.create('Ext.grid.plugin.CellEditing',{
    	clicksToEdit:1
    }),
	bbar:{
		xtype: 'Infotoolbar',
		id:'Infotoolbar'
	},
	test:0,
	GridUtil:Ext.create('erp.util.GridUtil'),
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	initComponent:function(){
		this.callParent(arguments);
		//得到页面上显示的formCondition属性
		var urlCondition = this.BaseUtil.getUrlParam('formCondition'), pp_id = 0;
		//定义通过IS拆分后的数值
		if(urlCondition) {
			urlCondition = urlCondition.replace(/IS/g, '=');
			pp_id = urlCondition.indexOf('=') > 0 ? urlCondition.substr(urlCondition.indexOf('=') + 1) : 0;
		}
		var condition = "pi_vrid=" + pp_id;		
		this.getMyData(condition);
	},
	getMyData:function(condition){
		var me = this;
		var params = {
				caller:"ProductInfo",
				condition:condition
		};		
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me,params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me,'common/singleGridPanel.action',params);						
		}
	}	
});