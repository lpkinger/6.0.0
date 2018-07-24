Ext.define('erp.view.ma.copy.GridPanel', {
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpCopyGridPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region: 'south',
	layout : 'fit',
	id: 'grid', 
	deleteBeforeImport : false,
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true, 
	store: [],
	columns: [],
	binds:null,
	limitArr:[],
	bodyStyle: 'background-color:#f1f1f1;',
	plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1,
		listeners:{
			beforeedit:function(e){
				var g=e.grid,r=e.record,f=e.field;
				if(g.binds){
					var bool=true;
					Ext.Array.each(g.binds,function(item){
						if(Ext.Array.contains(item.fields,f)){
							Ext.each(item.refFields,function(field){
								if(r.get(field)!=null && r.get(field)!=0 && r.get(field)!='' && r.get(field)!='0'){
									bool=false;
								} 
							});							
						} 
					});
					return bool;
				}
			}
		}

	}), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	features : [Ext.create('Ext.grid.feature.GroupingSummary',{
		startCollapsed: true,
		groupHeaderTpl: '{name} (共:{rows.length}条)'
	}),{
		ftype : 'summary',
		showSummaryRow : false,//不显示默认合计行
		generateSummaryData: function(){
			// 避开在grid reconfigure后的计算，节约加载时间50~600ms
			return {};
		}
	}],
	bbar: {xtype: 'erpToolbar',id:'toolbar'},
	GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	necessaryField: '',//必填字段
	detno: '',//编号字段
	keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	dbfinds: [],
	caller: null,
	condition: null,
	gridCondition:null,
	initComponent : function(){
		if(!this.boxready) {
			if(this._buttons)
				this.bbar._buttons = this._buttons;// 在toolbar加入grid固定按钮
			var condition = this.condition;
			if(!condition){
				var urlCondition = this.BaseUtil.getUrlParam('gridCondition');
				urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
				urlCondition = urlCondition.replace(/cc_callerIS/g, "");
				condition = "cc_caller="+"'"+urlCondition+"'";
			}
			var gridParam = {caller: this.caller || caller, condition: condition, _m: 0};
			var master = getUrlParam('newMaster'),_config=getUrlParam('_config');
			if(master){
				gridParam.master = master;
			}
			if(_config)gridParam._config=_config; 
			var _copyConf=getUrlParam('_copyConf');
			if(_copyConf!=null){//复制来源单据的条件
				gridParam._copyConf=_copyConf;
			}
			this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "" , true);//从后台拿到gridpanel的配置及数据
		}
		this.callParent(arguments);
	}
});