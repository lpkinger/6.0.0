Ext.define('erp.view.hr.kpi.KpidesigngradeLevelGrid',{
	extend:'Ext.grid.Panel',
	alias:'widget.KpidesigngradeLevelGrid',
	requires:['erp.view.hr.kpi.Kpitoolbar'],
	layout:'fit',
	id:'KpidesigngradeLevelGrid',
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
    autoScroll : true,
    detno:'kl_detno',
    keyField:'kl_id',
    mainField:'kl_kdid',
    caller:'KpidesigngradeLevel',
    readOnly:true,
    columns:[],
    bodyStyle:'bachgroud-color:#f1f1f1;',
    plugins:Ext.create('Ext.grid.plugin.CellEditing',{
    	clicksToEdit:1
    }),
	tbar:{
		xtype: 'Kpitoolbar',
		id:'Kpitoolbar3'
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
		var condition = " kl_kdid='"+pp_id+"'";
		this.getMyData(condition);
	},
	getMyData:function(condition){
		var me = this;
		var params = {
				caller:"KpidesigngradeLevel",
				condition:condition
		};		
		if(me.columns && me.columns.length > 2){
			me.GridUtil.loadNewStore(me,params);
		} else {
			me.GridUtil.getGridColumnsAndStore(me,'common/singleGridPanel.action',params);			
			
		}
	},
	reconfigure: function(store, columns){
		var me = this,
		view = me.getView(),
		originalDeferinitialRefresh,
		oldStore = me.store,
		headerCt = me.headerCt,
		oldColumns = headerCt ? headerCt.items.getRange() : me.columns;
		if (columns) {
			columns = Ext.Array.slice(columns);
		}
		me.fireEvent('beforereconfigure', me, store, columns, oldStore, oldColumns);
		if (me.lockable) {
			me.reconfigureLockable(store, columns);
		} else {
			Ext.suspendLayouts();
			if (columns) {
				delete me.scrollLeftPos;
				headerCt.removeAll();
				headerCt.add(columns);
			}
			if (store && (store = Ext.StoreManager.lookup(store)) !== oldStore) {
				originalDeferinitialRefresh = view.deferInitialRefresh;
				view.deferInitialRefresh = false;
				try {
					me.bindStore(store);
				} catch ( e ) {

				}
				view.deferInitialRefresh = originalDeferinitialRefresh;
			} else {
				me.getView().refresh();
			}
			Ext.resumeLayouts(true);
		}	   
		if(store.data.items.length==0){
			me.fireEvent('reconfigure', me, store, columns, oldStore, oldColumns);	
		}
		this.fireEvent("summary", this);
	}
});