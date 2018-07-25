/**
 * gridpanel通用样式2
 */
Ext.define('erp.view.plm.request.ProjectPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.ProjectPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	region: 'south',
	layout : 'fit', 
	directImport:false,//直接将Excel数据导入从表 false：不支持
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
	bbar: {xtype: 'erpToolbar'},
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
				if(urlCondition&&urlCondition.split('IS')[1]){
					var str = this.mainField + 'IS' + urlCondition.split('IS')[1];
					this.gridCondition = str.replace(/IS/g, "=");
					condition = this.gridCondition;
				}else if(urlCondition&&urlCondition.split('=')[1]) {
					var str = this.mainField + 'IS' + urlCondition.split('=')[1];
					this.gridCondition = str.replace(/IS/g, "=");
					condition = this.gridCondition;
				}
			}
			var gridParam = {caller: this.caller || caller, condition: this.gridCondition||condition, _m: 0};
			var master = getUrlParam('newMaster'),_config=getUrlParam('_config');
			if(master){
				gridParam.master = master;
			}
			if(_config)gridParam._config=_config; 
			var _copyConf=getUrlParam('_copyConf');
			if(_copyConf!=null){//复制来源单据的条件
				gridParam._copyConf=_copyConf;
			}
			this.GridUtil.getGridColumnsAndStore(this, 'common/singleGridPanel.action', gridParam, "");//从后台拿到gridpanel的配置及数据
		}
		this.callParent(arguments);
		if(!this.boxready) {
			if(this.allowExtraButtons)// 加载其它按钮
				this.on('reconfigure', this.loadExtraButton, this, {single: true, delay: 1000});
			this.on('summary', this.generateSummaryData, this, {single: true, delay: 1000});
		}
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
				caller: me.caller || caller
			},
			method : 'post',
			async: false,
			callback : function(options, success, response){
				var r = new Ext.decode(response.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);
				}
				if(r.buttons){
					var buttons = Ext.decode(r.buttons), tb = me.getDockedItems('toolbar[dock="bottom"]')[0];
					if(tb) {
						Ext.each(buttons, function(b){
							try {
								tb.add({
									xtype: b.xtype, 
									disabled: false,
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
	showTrigger:function(val){//明细行文本框
		val = unescape(val);
		Ext.MessageBox.minPromptWidth = 600;
        Ext.MessageBox.defaultTextHeight = 200;
        Ext.MessageBox.style= 'background:#e0e0e0;';
        Ext.MessageBox.prompt("详细内容", '',
        function(btn, text) {},
        this, true, //表示文本框为多行文本框    
        val);
	}
});