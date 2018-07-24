/**
 * ERP项目gridpanel样式7:DocumentPower专用treegrid
 */
Ext.define('erp.view.core.grid.DocumentPowerTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpDocumentPowerTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
    updateNodes: [],
    store: Ext.create('Ext.data.TreeStore', {
    	fields: fields,
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
    columns: columns,
    plugins: Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}),
	tbar: [{
		iconCls: 'tree-save',
		text: $I18N.common.button.erpSaveButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid'),
			nodes = treegrid.store.tree.root.childNodes;
			Ext.each(nodes, function(){
				treegrid.checkChild(this);
			});
			treegrid.saveNode();
		}
	},{
		xtype: 'erpDistributeButton',
		disabled: false
	}],
	bodyStyle:'background-color:#f1f1f1;',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		this.callParent(arguments);
		this.getTreeGridNode(0);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getTreeGridNode: function(id){
		var me = this;
		var activeTab = me.getActiveTab();
		activeTab.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/singleGridPanel.action',
        	params: {
        		caller: caller, 
        		condition: "dcp_parentid=" + id
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.data){
        			var tree = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        			Ext.each(tree, function(d){
        				d.powername = d.dcp_powername;
        				d.isempower = d.dcp_isempower;
        				d.id = d.dcp_id;
        				d.parentId = d.dcp_parentid;
        				d.cls = 'x-tree-cls-node';
        				d.leaf = d.dcp_isleaf == 'T';
        				if(!d.leaf){
        					d.cls = 'x-tree-cls-root';
        				}
        			});
        			me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        			Ext.each(me.store.tree.root.childNodes, function(){
        				this.dirty = false;
        			});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	checkChild: function(record){
		var me = this;
		if(!record.data['leaf']){
			if(record.childNodes.length > 0){
				Ext.each(record.childNodes, function(){
					me.checkChild(this);
				});
			}
		} else {
			if(record.dirty){	
				me.updateNodes.push(record);
			}
		}
	},
	saveNode: function(){
		var me = this;
		me.getUpdateNodes();
		var update = new Array();
		var index = 0;
		Ext.each(me.updateNodes, function(){
			if(this.data.dcp_powername != null && this.data.dcp_powername != ''){
				var o = {
						dcp_id: this.data.dcp_id,
						dcp_powername: this.data.dcp_powername,
						dcp_parentid: this.data.dcp_parentid,
						dcp_isleaf: this.data.dcp_isleaf,
						dcp_isempower: this.data.dcp_isempower
				};
				update[index++] = Ext.JSON.encode(o);
			}
		});
		if(update.length > 0){
			var activeTab = me.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'ma/saveDocumentPower.action',
	        	params: {
	        		update: unescape(update.toString().replace(/\\/g,"%"))
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		activeTab.setLoading(false);
	        		if(res.success){
	        			me.updateNodes = [];
	        			me.getTreeGridNode(0);
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
		}
	},
	getExpandItem: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
		}
		var node = null;
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.isExpanded()){
					node = this;
					if(this.childNodes.length > 0){
						var n = me.getExpandItem(this);
						node = n == null ? node : n;
					}
				}
			});
		}
		return node;
	},
	getUpdateNodes: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
			me.updateNodes = [];
		}
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.dirty){
					if(this.data['dcp_id'] != null && this.data['dcp_id'] != ''){
						me.updateNodes.push(this);
					}
				}
				if(this.data['leaf'] == false && this.childNodes.length > 0){
					me.getUpdateNodes(this);
				}
			});
		} else {
			if(root.dirty){
				if(root.data['dcp_id'] != null && root.data['dcp_id'] != ''){
					me.updateNodes.push(root);
				}
			}
		}
	},
	getActiveTab: function(){
		var tab = null;
		if(Ext.getCmp("content-panel")){
			tab = Ext.getCmp("content-panel").getActiveTab();
		}
		if(!tab){
			var win = parent.Ext.ComponentQuery.query('window');
			if(win.length > 0){
				tab = win[win.length-1];
			}
		}
    	if(!tab && parent.Ext.getCmp("content-panel"))
    		tab = parent.Ext.getCmp("content-panel").getActiveTab();
    	if(!tab  && parent.parent.Ext.getCmp("content-panel"))
    		tab = parent.parent.Ext.getCmp("content-panel").getActiveTab();
    	return tab;
	}
});