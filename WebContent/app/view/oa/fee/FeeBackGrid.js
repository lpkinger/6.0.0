Ext.define('erp.view.oa.fee.FeeBackGrid',{
	extend:'Ext.grid.Panel',
	alias:'widget.FeeBackGrid',
	requires:['erp.view.oa.fee.FeeBacktoolbar'],
	layout:'fit',
	id:'FeeBackGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    detno:'fb_detno',
    keyField:'fb_id',
    mainField:'fb_fpid',
    caller:'FeeBack',
    columns:[],
    bodyStyle:'bachgroud-color:#f1f1f1;',
    plugins:Ext.create('Ext.grid.plugin.CellEditing',{
    	clicksToEdit:1
    }),
	bbar:{
		xtype: 'FeeBacktoolbar',
		id:'FeeBacktoolbar'
	},
	test:0,
	GridUtil:Ext.create('erp.util.GridUtil'),
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	initComponent:function(){
		this.callParent(arguments);
//		console.log(urlCondition);
		//得到页面上显示的formCondition属性
		var urlCondition = this.BaseUtil.getUrlParam('formCondition');
		//定义通过IS拆分后的数值
		var cons=null;
		//存在urlCondition的情况下
		if(urlCondition){
		//对urlCondition进行拆分  urlCondition的格式一半为pp_idIS1
			urlCondition=urlCondition.replace(/'/g,'');
			if(urlCondition.indexOf('IS')>-1){
				cons = urlCondition.split("IS");
			}else if(urlCondition.indexOf('=')>-1){
				cons = urlCondition.split("=");
			}
		}
		var pp_id=0;
		if(cons!=null){
			if(cons[0]&&cons[1]){
				if(cons[0]!=null&&cons[0]!=''){
					if(cons[1]-0>0){
						pp_id=cons[1];
					}else{
						pp_id=0;
					}
				}
				
			}
		}
		var condition = " fb_fpid='"+pp_id+"'";
//		console.log(condition);
		this.getMyData(condition);
		
	},
	getMyData:function(condition){
//		console.log(condition);
		var me = this;
		var params = {
				caller:"FeeBack",
				condition:condition
		};
		
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me,params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me,'common/singleGridPanel.action',params);			
			
		}
	}	
});