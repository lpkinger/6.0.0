/**
 * ERP项目gridpanel通用样式5
 */
Ext.define('erp.view.core.grid.Panel5',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpGridPanel5',
	layout : 'fit',
	id: 'grid',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    condition:null,
    store: [],
    columns: [],
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    plugins:[ Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
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
    caller: null,
	initComponent : function(){ 
	    var me = this, condition = me.condition || '';
	    if(typeof me.getCondition === 'function'){
	    	condition = me.getCondition.call(null, me);
	    	me.condition=me.condition||condition;
	    }
    	var gridParam = {caller: this.caller || caller, condition: condition};
    	var _copyConf=getUrlParam('_copyConf');
		if(_copyConf!=null){//复制来源单据的条件
			gridParam._copyConf=_copyConf;
		}
    	this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		this.callParent(arguments);  
		if(!this.boxready) {
			if(this.allowExtraButtons)// 加载其它按钮
				this.on('reconfigure', this.loadExtraButton, this, {single: true, delay: 1000});
			this.on('summary', this.generateSummaryData, this, {single: true, delay: 1000});
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
		me.fireEvent('reconfigure', me, store, columns, oldStore, oldColumns);	
		this.fireEvent("summary", this);
	},
	generateSummaryData : function() {
		var store = this.store,
		columns = this.columns, s = this.features[this.features.length - 1],
		i = 0, length = columns.length, comp, bar = this.down('erpToolbar');
		if (!bar) return;
		var limitArr=this.limitArr;
		//将feature的data打印在toolbar上面
		for (; i < length; i++ ) {
			comp = columns[i];
			if((limitArr.length == 0 || !Ext.Array.contains(limitArr, comp.dataIndex))&&comp.summaryType) {
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
	loadExtraButton: function() {
		var me = this;
		Ext.Ajax.request({
			url : basePath + "common/gridButton.action",
			params: {
				caller: me.caller
			},
			method : 'post',
			async: false,
			callback : function(options, success, response){
				var r = new Ext.decode(response.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				}
				if(r.buttons){
					var buttons = Ext.decode(r.buttons), tb = me.down('toolbar');
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
	}
});