/**
 * 
 */
 Ext.define('erp.view.common.subs.SubsRelationConfig',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.subsRelationConfig',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region: 'south',
	layout : 'fit',
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
	bbar: {xtype: 'erpToolbar',id:'toolbar3'},
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
				gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
				gridCondition = gridCondition + urlCondition;
				gridCondition = gridCondition.replace(/IS/g, "=");
				condition = gridCondition;
			}
			var gridParam = {caller: this.caller || caller, condition: this.gridCondition||condition, _m: 0};
			var master = getUrlParam('newMaster'),_config=getUrlParam('_config');
			if(master){
				gridParam.master = master;
			}
			if(_config)gridParam._config=_config; 
			this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		}
		this.callParent(arguments);
	},
	setReadOnly: function(bool){
		this.readOnly = bool;
	}
});