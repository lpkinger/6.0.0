Ext.define('erp.view.hr.kpi.ApplyGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpApplyGrid',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region: 'south',
	layout : 'fit',
	id: 'grid', 
	emptyText : $I18N.common.grid.emptyText,
	columnLines : true,
	autoScroll : true, 
	store: [],
	columns: [],
	binds:null,
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
	bbar: [],//{xtype: 'erpToolbar',id:'toolbar'},
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
			var master = getUrlParam('newMaster');
			if(master){
				gridParam.master = master;
			}
			if(condition){
				this.GridUtil.getGridColumnsAndStore(this, 'hr/kpi/getGridPanel.action', gridParam, "");
 				this.bbar={ xtype: 'erpToolbar', id:'toolbar'};
			}
			this.callParent(arguments);
	},
	getEffectiveData: function(){
		var me = this;
		var effective = new Array();
		var s = this.store.data.items;
		for(var i=0;i<s.length;i++){
			var data = s[i].data;
			if(data[me.keyField] != null && data[me.keyField] != ""){
				effective.push(data);
			}
		}
		return effective;
	},
	setReadOnly: function(bool){
		this.readOnly = bool;
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
		me.fireEvent('reconfigure', me, store, columns, oldStore, oldColumns);	
		this.fireEvent("summary", this);
	},
	generateSummaryData : function() {
		var store = this.store,
		columns = this.columns, s = this.features[this.features.length - 1],
		i = 0, length = columns.length, comp, bar = this.down('erpToolbar');
		if (!bar) return;
		//将feature的data打印在toolbar上面
		for (; i < length; i++ ) {
			comp = columns[i];
			if(comp.summaryType) {
				var tb = Ext.getCmp(comp.dataIndex + '_' + comp.summaryType);
				if(!tb){
					bar.add('-');
					tb = bar.add({
						id: comp.dataIndex + '_' + comp.summaryType,
						itemId: comp.dataIndex,
						xtype: 'tbtext'
					});
				}
				var val = s.getSummary(store, comp.summaryType, comp.dataIndex, false);
				if(comp.xtype == 'numbercolumn') {
					val = Ext.util.Format.number(val, (comp.format || '0,000.000'));
				}
				tb.setText(comp.text + ':' + val);
			}
		}   	
	},
	/**
	 * Grid上一条
	 */
	prev: function(grid, record){
		grid = grid || Ext.getCmp('grid');
		record = record || grid.selModel.lastSelected;
		if(record){
			//递归查找上一条，并取到数据
			var d = grid.store.getAt(record.index - 1);
			if(d){
				try {
					grid.selModel.select(d);
					return d;
				} catch (e){

				}
			} else {
				if(record.index - 1 > 0){
					return this.prev(grid, d);
				} else {
					return null;
				}
			}
		}
	},
	/**
	 * Grid下一条
	 */
	next: function(grid, record){
		grid = grid || Ext.getCmp('grid');
		record = record || grid.selModel.lastSelected;
		if(record){
			//递归查找下一条，并取到数据
			var d = grid.store.getAt(record.index + 1);
			if(d){
				try {
					grid.selModel.select(d);
					return d;
				} catch (e){

				}
			} else {
				if(record.index + 1 < grid.store.data.items.length){
					return this.next(grid, d);
				} else {
					return null;
				}
			}
		}
	},
	allowExtraButtons: false,// 加载其它按钮，从GridButton加载
	loadExtraButton: function() {
		var me = this;
		Ext.Ajax.request({
			url : basePath + "common/gridButton.action",
			params: {
				caller: caller
			},
			method : 'post',
			async: false,
			callback : function(options, success, response){
				var r = new Ext.decode(response.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				}
				if(r.buttons){
					var buttons = Ext.decode(r.buttons), tb = me.down('#toolbar');
					if(tb) {
						Ext.each(buttons, function(b){
							try {
								tb.add({
									xtype: b.xtype, 
									disabled: true,
									style: {
										marginLeft: '0'
									}
								});
							} catch(e) {
								tb.add({
									text: $I18N.common.button[b.xtype],
									id: b.xtype,
									cls: 'x-btn-gray',
									disabled: true
								});
							}
						});
					}
				}
			}
		});
	},
	onExport: function(){
    	var me = this;
    	me.BaseUtil.exportGrid(me,me.title,'',1);//1,不导出合计
    }
});