/**
 *  multi dbfind trigger
 * 改了下MultiDbfindTrigger的赋值方式，在grid中也会把多选的值用#号连接
 */
Ext.define('erp.view.core.trigger.MultiDbfindTrigger2', {
	extend: 'Ext.form.field.Trigger',
	alias: 'widget.multidbfindtrigger2',
	triggerCls: 'x-form-search-trigger',
	onTriggerClick: function() {
		this.setFieldStyle('background:#C6E2FF;');
		var trigger = this,// 放大镜所在
			key = this.name,// name属性
			dbfind = '',// 需要dbfind的表和字段
			dbBaseCondition = '',
			dbCondition = '',
			dbGridCondition = '',
			dbKey = this.dbKey,
			mappingKey = this.mappingKey,
			mappinggirdKey = this.mappinggirdKey,
			gridKey = this.gridKey,
			gridErrorMessage = this.gridErrorMessage;
		
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if(dbKey){
			var dbKeyValue = Ext.getCmp(dbKey).value;
			if(dbKeyValue){
				dbCondition = mappingKey + " IS '" + dbKeyValue + "'";
			} else {
				showError(this.dbMessage);
				return
			}
		}
		if(gridKey){

			
			var gridKeys = gridKey.split('|');
			var mappinggirdKeys;
			var gridErrorMessages;
			if(mappinggirdKey){
				mappinggirdKeys = mappinggirdKey.split('|');
				gridErrorMessages = this.gridErrorMessage.split('|');
			}
			
			for(var i=0;i<gridKeys.length;i++){
				var gridkeyvalue = Ext.getCmp(gridKeys[i]).value;
				
				if(i==0){
					if(gridkeyvalue){
						dbGridCondition = mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
					}else{
						showError(gridErrorMessages[i]);
				    	return
					}
				}else{
					if(gridkeyvalue){
						dbGridCondition =dbGridCondition+" AND "+ mappinggirdKeys[i] + " IS '"+gridkeyvalue+"' ";
					}else{
						showError(gridErrorMessages[i]);
				    	return
					}
					
				}
				
			}
		}
		if(this.dbBaseCondition){
			dbBaseCondition = this.dbBaseCondition;
		}
		if(!trigger.ownerCt){// 如果是grid的dbfind
			var grid = Ext.ComponentQuery.query('gridpanel');
			Ext.Array.each(grid, function(g, index){
				Ext.Array.each(g.columns,function(column){
					if(column.dataIndex == key) {
						dbfind = column.dbfind;
						trigger.owner = g;
					}
				});
			});
			if(trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
				trigger.record = trigger.owner.editingPlugin.activeRecord;
			} else {
				trigger.record = trigger.owner.selModel.lastSelected;
			}
		}
		var keyValue = this.value;// 当前值
		keyValue = keyValue == null ? '' : keyValue;
		var dbwin = this.createWin();
		dbwin.show();
		if(this.multistore){
			this.showButtons();
		} else {
			trigger.multiValue = new Object();
			var iframe = dbwin.getEl().down('iframe');
			if(!iframe) {
				dbwin.add({
					tag : 'iframe',
					frame : true,
					anchor : '100% 100%',
					layout : 'fit',
					html : '<iframe src="#" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				});
				iframe = dbwin.getEl().down('iframe');
			}
			iframe.dom.src = basePath + 'jsps/common/multidbfind.jsp?key=' + 
				key + "&dbfind=" + dbfind + 
				"&dbGridCondition=" + dbGridCondition + "&dbCondition=" + dbCondition + 
				"&dbBaseCondition=" + dbBaseCondition + "&keyValue=&caller=" + caller + 
				"&trigger=" + trigger.id;
		}
	},
	createWin: function() {
		var trigger = this;
		this.win = Ext.create('Ext.Window', {
			title: '查找',
			height: "100%",
			width: "80%",
			maximizable : true,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [],
			dbtriggr: trigger,
			closeAction: 'hide',
			buttons : [{
			  boxLabel  : '<span style="font-size:13px;font-weight:bold;">只显示已选中数据</span>',
				xtype:'checkbox',
				style:'margin-left:10px;',
				align:'left',
				hidden:true,
				width:140,
				id:'onlyChecked',
				listeners:{
					change:function( f, newValue,  oldValue, eOpts ){
						var win = trigger.win;
						var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
						var resgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindresultgrid');//选中
						if(newValue){
							findgrid.hide();
							var datachecked=new Array();
							Ext.each(Ext.Object.getKeys(resgrid.selectObject),function(k){
								datachecked.push(resgrid.selectObject[k]);
							});
							resgrid.selectAll=false;
							resgrid.store.loadData(datachecked);
							resgrid.selModel.selectAll();
							resgrid.show();
						}else{
							findgrid.show();
							findgrid.selectAll=false;
							findgrid.selModel.deselectAll();
							findgrid.selectDefaultRecord();
						    resgrid.hide();
						}
					}
				}
			 }, '->',{
				text : '确  认',
				iconCls: 'x-button-icon-save',
				id:'mutidbaffirm',
				cls: 'x-btn-gray',
				handler : function(){		   				    		
					trigger.onConfirm();
				}
			},{
				text : '关  闭',
				iconCls: 'x-button-icon-close',
				cls: 'x-btn-gray',
				handler : function(btn){
					btn.ownerCt.ownerCt.close();
				}
			}, '->']
		});
		return this.win;
	},
	showButtons: function() {
		var value = this.value, dbwin = this.win;
		dbwin.add({
			id: 'multigrid',
			xtype: 'gridpanel',
			height: '100%',
			autoScroll: true,
			columnLines : true,
			columns: [{ 
				header: 'Button',  
				dataIndex: 'display' ,
				flex: 1
			}, { 
				header: 'name', 
				dataIndex: 'value', 
				flex: 1
			}],
			store: this.multistore,
			selModel: Ext.create('Ext.selection.CheckboxModel',{
				ignoreRightMouseSelection : false,
				listeners:{
					selectionchange:function(selectionModel, selected, options){
   			    		            	
					}
				},
				onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
					view.el.focus();
					var me = Ext.getCmp('multigrid');
					var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
					if(contains(value, record.data['value'], true)){
						me.selModel.deselect(record);
						me.multiselected = me.multiselected.replace('#' + record.data['value'], '');
						value = value.replace(record.data['value'], '');
						checkbox.setAttribute('class','x-grid-row-checker');
					} else {
						if(checkbox.getAttribute('class') == 'x-grid-row-checker'){
							checkbox.setAttribute('class','x-grid-row-checker-checked');//只是修改了其样式，并没有将record加到selModel里面
							me.multiselected = me.multiselected + '#' + record.data['value'];
						} else {
							me.multiselected = me.multiselected.replace('#' + record.data['value'], '');
							checkbox.setAttribute('class','x-grid-row-checker');
						}
					}
				}
			}),
			listeners: {
				afterrender: function(){
					var me = this;
					var selected = new Array();
					me.multiselected = '';
					Ext.each(me.store.data.items, function(){
						if(contains(value, this.data['value'], true)){
							selected.push(this);
							me.multiselected = me.multiselected + '#' + this.data['value'];
						}
					});
					me.selModel.select(selected);
				}
			}
		});
	},
	onConfirm: function() {
		var trigger = this;
		
		if(trigger.multistore){
			var me = Ext.getCmp('multigrid');
			trigger.setValue(me.multiselected.substring(1));
			this.win.close();
		} else {
			
			if(!trigger.ownerCt){
				var grid = trigger.owner;
				var record = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] || grid.selModel.lastSelected;//detailgrid里面selected
				var win = trigger.win;
				var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
				findgrid.setMultiValues();
				Ext.each(trigger.multiValue, function(item, index){
					if(record) {
						if(item){
							Ext.Array.each(Ext.Object.getKeys(item), function(k){
								Ext.Array.each(grid.dbfinds,function(ds){
									if(Ext.isEmpty(ds.trigger) || ds.trigger == trigger.name) {
										if(Ext.Array.contains(ds.dbGridField.split(';'), k)) {
											var value=record.get(ds.field)?record.get(ds.field)+'':'';
											if(!Ext.Array.contains(value.split('#'), item[k])){
												record.set(ds.field,value==""? item[k]:value+'#'+item[k]);
											}
										}
									}
								});
							});
						}
					}
				});
			} else {
				var win = trigger.win;
					var findgrid = win.getEl().down('iframe').dom.contentWindow.document.defaultView.Ext.getCmp('dbfindGridPanel');//所有
					findgrid.setMultiValues();
				var k = Ext.Object.getKeys(trigger.multiValue),cp;
				Ext.each(k, function(key){
					cp = Ext.getCmp(key);
					if(cp.setValue !== undefined)
						cp.setValue(trigger.multiValue[key]);
				});
				trigger.setValue(trigger.multiValue[trigger.name]);
			}
			trigger.fireEvent('aftertrigger', trigger, trigger.multiRecords);
			this.win.close();
		}
	},
	/**
	 * 递归grid的下一条 
	 */
	next: function(grid, record){
		record = record || grid.selModel.lastSelected;
		if(record){
			//递归查找下一条，并取到数据
			var store = grid.store, idx = store.indexOf(record),
				d = store.getAt(idx + 1), len = store.data.items.length;
			if(d){
				return d;
			} else {
				if(idx + 1 < len){
					this.next(grid, d);
				} else {
					if (grid.GridUtil) {
						grid.GridUtil.add10EmptyItems(grid);
						return this.next(grid, record);
					}
				}
			}
		}
	}
});