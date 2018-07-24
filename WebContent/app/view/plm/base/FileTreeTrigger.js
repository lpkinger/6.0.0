/**
 * dbfind trigger
 * 支持带条件dbfind
 */
Ext.define('erp.view.plm.base.FileTreeTrigger', {
	extend: 'Ext.form.field.Trigger',
	alias: 'widget.filetreetrigger',
	triggerCls: 'x-form-search-trigger',
	triggerName:null,
	title:null,
	url:null,
	nodeId:null,
	filetrees:null,
	initComponent: function() {
		var me = this;
		me.addEvents({
			aftertrigger: true,
			beforetrigger: true
		});
		me.callParent(arguments);
		if(!me.ownerCt) {
			Ext.defer(function(){
				me.getOwner();
			}, 50);
		}
	},
	getOwner: function() {
		var me = this;
		if (me.el) {
			var gridEl = me.el.up('.x-grid');
			if (gridEl) {
				var grid = Ext.getCmp(gridEl.id);
				if (grid) {
					me.owner = grid;
					me.column = grid.down('gridcolumn[dataIndex=' + me.name + ']');
				}
			}
		}
	},
	onTriggerClick: function() {
		var trigger = this,
		bool = true; // 放大镜所在	
		bool = trigger.fireEvent('beforetrigger', trigger);
		if (bool == false) {
			return;
		}
		this.setFieldStyle('background:#C6E2FF;');
		
		BaseCondition = '',
		treeCondition = '',
		findConfig = this.findConfig;
		window.onTriggerClick = this.id;
		// 存在查询条件的字段
		if (findConfig) {
			treeCondition = (typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (this.BaseCondition) {
			BaseCondition = this.BaseCondition;
		}
		var win = Ext.getCmp('trwin');
		if(!win){
			trigger.createWindow();
		}else{
			win.show();
		}
	},
	createWindow:function(){
		var trigger = this;
		var width = Ext.isIE ? screen.width * 0.7 * 0.4 : '40%',
		height = Ext.isIE ? screen.height * 0.9 : '90%';
		//针对有些特殊窗口显示较小
		width =this.winWidth ? this.winWidth:width;
		height=this.winHeight ? this.winHeight:height;
		var _config=getUrlParam('_config');
		condition = trigger.getCondition();
		Ext.Ajax.request({
			url:basePath + trigger.url,
			params:{
				condition:condition
			},
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.tree){
						var fields = ['id','text'];
						for(var i=0;i<trigger.filetrees.length;i++){
							fields.push(trigger.filetrees[i].treefield);
						}
						var store =Ext.create('Ext.data.TreeStore', {
								fields : fields,
								autoLoad:false,
								root : {
									text : 'Root',
									id : 0,
									expanded : true
								}
							})
						store.setRootNode({
							text: 'root',
	                	    id: 0,
	                		expanded: true,
	                		children: res.tree
						});	
						trigger.setChecked(store);
						var tree = Ext.create('Ext.tree.Panel', {
							id:'findfiletree',
							bodyStyle : 'background-color:white;',
							rootVisible : false,
							autoScroll:true,
							hideHeaders : true,
							border:false,
							store:store,
							columns : [{
								xtype : 'treecolumn',
								dataIndex:'text',
								sortable : true,
								flex : 1
							}]
						});
						var trwin = new Ext.window.Window({
							id: 'trwin',
							title: trigger.title,
							height: height,
							width: width,
							maximizable: true,
							modal:true,
							buttonAlign: 'center',
							layout: 'fit',
							items: [tree],
							buttons: [{
								text: '确认',
								iconCls: 'x-button-icon-save',
								cls: 'x-btn-gray',
								style:'margin-right:20px;',
								handler: function(btn) {
									var tree = btn.ownerCt.ownerCt.down('treepanel');
									trigger.setValues(tree);
									btn.ownerCt.ownerCt.close();
								}
							},{
								text: '关  闭',
								iconCls: 'x-button-icon-close',
								cls: 'x-btn-gray',
								style:'margin-left:20px;',
								handler: function(btn) {
									btn.ownerCt.ownerCt.close();
								}
							}]
						});
						trwin.show();
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});

	},
	setValues:function(tree){
		var trigger = this;
		var checks = tree.getChecked();
		for(var i=0;i<trigger.filetrees.length;i++){
			var str ='';
			Ext.Array.each(checks,function(check){
				str+=check.data[trigger.filetrees[i].treefield]+',';
			});
			str = str.substring(0,str.length-1);	
			if (!trigger.ownerCt || trigger.column) { // 如果是grid	
				var grid = trigger.owner;
				var select = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] 
        				|| grid.selModel.lastSelected||grid.selModel.selected.items[0];//selected的数据
				select.set(trigger.filetrees[i].field,str);
			}else{
				var f = Ext.getCmp(trigger.filetrees[i].field);
				if(f){
					f.setValue(str);
				}
			}			
		}
	},
	setChecked:function(store){
		var trigger = this;
		var ids = new Array();
		if(trigger.nodeId&&trigger.nodeId!=''){
			if (!trigger.ownerCt || trigger.column) { // 如果是grid	
				var grid = trigger.owner;
				var select = grid.lastSelectedRecord || trigger.record || grid.getSelectionModel().selected.items[0] 
        				|| grid.selModel.lastSelected||grid.selModel.selected.items[0];//selected的数据
        		if(select.data[trigger.name]&&select.data[trigger.name]!=''){
        			ids = select.data[trigger.nodeId].split(',');
        		}
			}else{			
				if(trigger.value&&trigger.value!=''){	
					ids = Ext.getCmp(trigger.nodeId).value.split(',');
					
				}
			}
			for(var i=0;i<ids.length;i++){
				var node = store.getNodeById(ids[i]);
				if(node&&!node.data.checked){
					node.data.checked=true;
					trigger.expandAll(node);
				}
			}
		}
	},
	expandAll:function(node){
		if(node.parentNode){
			node.parentNode.expand();
			this.expandAll(node.parentNode);
		}
	},
	getCondition: function(triggerCond) {
		var condition = [], findConfig = this.findConfig;
		// 存在查询条件的字段
		if (findConfig) {
			condition.push(typeof findConfig == 'function' ? findConfig.call(null) : findConfig);
		}
		if (this.BaseCondition) {
			condition.push(this.BaseCondition);
		}
		
		triggerCond && (condition.push(triggerCond));
		return condition.join(" AND ");
	}
});