/**
 * ERP项目gridpanel样式6:jobPower专用treegrid
 */
Ext.define('erp.view.core.grid.JobPowerTreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpJobPowerTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
    updateNodes: [],
    store: Ext.create('Ext.data.TreeStore', {
    	fields: [{
    		name: 'po_powername',
    		type: 'string'
    	},{
        	name:'pp_see',
        	type:'bool'
        },{
        	name:'pp_seeall',
        	type:'bool'
        },{
        	name:'pp_add',
        	type:'bool'
        },{
        	name:'pp_delete',
        	type:'bool'
        },{
        	name:'pp_save',
        	type:'bool'
        },{
        	name:'pp_commit',
        	type:'bool'
        },{
        	name:'pp_uncommit',
        	type:'bool'
        },{
        	name:'pp_audit',
        	type:'bool'
        },{
        	name:'pp_unaudit',
        	type:'bool'
        },{
        	name:'pp_print',
        	type:'bool'
        },{
        	name:'pp_disable',
        	type:'bool'
        },{
        	name:'pp_undisable',
        	type:'bool'
        },{
        	name:'pp_closed',
        	type:'bool'
        },{
        	name:'pp_unclosed',
        	type:'bool'
        },{
        	name:'pp_posting',
        	type:'bool'
        },{
        	name:'pp_unposting',
        	type:'bool'
        },{
        	name: 'po_id',
        	type: 'number'
        },{
        	name: 'po_parentid',
        	type: 'number'
        },{
        	name: 'po_isleaf',
        	type: 'string'
        },{
        	name: 'pp_id',
        	type: 'number'
        },{
        	name: 'pp_joid',
        	type: 'number'
        },{
        	name: 'pp_poid',
        	type: 'number'
        }],
    	root : {
        	text: 'root',
        	id: 'root',
    		expanded: true
    	}
    }),
    columns: [{
    	text: '权限名称',
    	flex: 1,
    	dataIndex: 'po_powername', 
    	xtype: 'treecolumn'
    },{
    	text: '浏览',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_see'
    },{
    	text: '浏览他人',
    	flex: 0.3,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_seeall'
    },{
    	text: '新增',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_add'
    },{
    	text: '删除',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_delete'
    },{
    	text: '保存',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_save'
    },{
    	text: '提交',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_commit'
    },{
    	text: '反提交',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_uncommit'
    },{
    	text: '审核',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_audit'
    },{
    	text: '反审核',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_unaudit'
    },{
    	text: '打印',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_print'
    },{
    	text: '禁用',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_disable'
    },{
    	text: '反禁用',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_undisable'
    },{
    	text: '结案',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_closed'
    },{
    	text: '反结案',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_unclosed'
    },{
    	text: '过账',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_posting'
    },{
    	text: '反过账',
    	flex: 0.2,
        xtype: 'checkcolumn',
        editor: {
            xtype: 'checkbox',
            cls: 'x-grid-checkheader-editor'
        },
        dataIndex: 'pp_unposting'
    },{
    	dataIndex: 'po_id',
    	hidden: true
    },{
    	dataIndex: 'po_parentid',
    	hidden: true
    },{
    	dataIndex: 'po_isleaf',
    	hidden: true
    },{
    	dataIndex: 'pp_id',
    	hidden: true
    },{
    	dataIndex: 'pp_joid',
    	hidden: true
    },{
    	dataIndex: 'pp_poid',
    	hidden: true
    }],
	bodyStyle:'background-color:#f1f1f1;',
	selType: 'rowmodel',
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		this.callParent(arguments);
		gridCondition = getUrlParam('gridCondition');
    	gridCondition = (gridCondition == null) ? "" : gridCondition.replace(/IS/g,"=");
		this.getTreeGridNode(0);
	},
    selModel: Ext.create('Ext.selection.CheckboxModel',{
		listeners: {
			select: function(selModel, record){
				if(record.isLeaf()){
					var grid = Ext.getCmp('treegrid');
		    		grid.checkAll(record, true);
				}
			},
			deselect: function(selModel, record){
				if(record.isLeaf()){
					var grid = Ext.getCmp('treegrid');
		    		grid.checkAll(record, false);
				}
			} 
		}
	}),
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
		var joid = Number(gridCondition.replace('pp_joid=', ''));
		activeTab.setLoading(true);
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + '/hr/employee/getJobPower.action',
        	params: {
        		parentid: id,
        		joid: joid
        	},
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		activeTab.setLoading(false);
        		if(res.powers){
        			var tree = [];
        			Ext.each(res.powers, function(d){
        				d.id = d.po_id;
        				d.parentId = d.po_parentid;
        				d.cls = 'x-tree-cls-node';
        				d.leaf = d.po_isleaf == 'T';
        				d.pp_poid = d.id;
						d.pp_joid = joid;
        				Ext.each(res.positionPowers, function(){
        					if(d.po_id == this.pp_poid){
        						d.pp_id = this.PP_id;
        						d.pp_unposting = this.pp_unposting == 1;
                				d.pp_posting = this.pp_posting == 1;
                				d.pp_unclosed = this.pp_unclosed == 1;
                				d.pp_closed = this.pp_closed == 1;
                				d.pp_undisable = this.pp_undisable == 1;
                				d.pp_disable = this.pp_disable == 1;
                				d.pp_print = this.pp_print == 1;
                				d.pp_unaudit = this.pp_unaudit == 1;
                				d.pp_audit = this.pp_audit == 1;
                				d.pp_uncommit = this.pp_uncommit ==1;
                				d.pp_commit = this.pp_commit == 1;
                				d.pp_save = this.pp_save == 1;
                				d.pp_delete = this.pp_delete == 1;
                				d.pp_add = this.pp_add == 1;
                				d.pp_seeall = this.pp_seeall == 1;
                				d.pp_see = this.pp_see == 1;
        					}
        				});
        				if(!d.leaf){
        					if(d.parentId == 0){
        						d.cls = 'x-tree-cls-root';
        					} else {
        						d.cls = 'x-tree-cls-parent';
        					}
        				}
        				tree.push(d);
        			});
        			var record = me.selModel.lastSelected;
        			if(id == 0){
        				me.store.setRootNode({
                    		text: 'root',
                    	    id: 'root',
                    		expanded: true,
                    		children: tree
                    	});
        			} else {
        				record.appendChild(tree);
        				record.expand(false, true);
        				Ext.each(record.childNodes, function(){
        					this.dirty = false;
        				});
        			}
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	getChildren: function(data, d){
		var o = [];
		Ext.each(data, function(){
			if(this.po_parentid == d.po_id){
				o.push(this);
			}
		});
		return o;
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
	},
	getUpdateNodes: function(root){
		var me = this;
		if(!root){
			root = this.store.tree.root;
			me.updateNodes = new Array();
		}
		if(root.childNodes.length > 0){
			Ext.each(root.childNodes, function(){
				if(this.leaf){
					if(this.dirty){
						if(me.checkDirty(this.modified)){
							me.updateNodes.push(this);
						}
					}
				} else {
					me.getUpdateNodes(this);
				}
			});
		} else {
			if(root.dirty){
				if(me.checkDirty(root.modified)){
					me.updateNodes.push(root);
				}
			}
		}
	},
	/**
	 * treegrid用到了checkcolumn时，由于其store的差异，根据recordIndex不能直接得到record，
	 * 采用下面的方法可以在点击checkbox时，得到当前的record，再进而就可以修改checkbox的check属性等...
	 */
	getRecordByRecordIndex: function(recordIndex, node){
		var me = this;
		if(!node){
			node = this.store.tree.root;
			me.findIndex = 0;
			me.findRecord = null;
		}
		if(me.findRecord == null){
			if(node.childNodes.length > 0 && node.isExpanded()){
				Ext.each(node.childNodes, function(){
					if(me.findIndex == recordIndex){
						me.findRecord = this;
						me.findIndex++;
					} else {
						me.findIndex++;
						me.getRecordByRecordIndex(recordIndex, this);
					}
				});
			} else {
				if(me.findIndex == recordIndex){
					me.findRecord = node;
				}
			}
		}
	},
	checkRecord: function(record, dataIndex, checked){
		var me = this;
		if(record.childNodes.length > 0){
			Ext.each(record.childNodes, function(){
				this.set(dataIndex, checked);
				me.checkRecord(this, dataIndex, checked);
			});
		}
	},
	checkAll: function(record, checked){
    	record.set('pp_see', checked);
    	record.set('pp_seeall', checked);
    	record.set('pp_add', checked);
    	record.set('pp_delete', checked);
    	record.set('pp_save', checked);
    	record.set('pp_commit', checked);
    	record.set('pp_uncommit', checked);
    	record.set('pp_audit', checked);
    	record.set('pp_unaudit', checked);
    	record.set('pp_print', checked);
    	record.set('pp_disable', checked);
    	record.set('pp_undisable', checked);
    	record.set('pp_closed', checked);
    	record.set('pp_unclosed', checked);
    	record.set('pp_posting', checked);
    	record.set('pp_unposting', checked);
	},
	checkDirty: function(modfied){
		var change = Ext.Object.getKeys(modfied);
		var bool = false;
		Ext.each(change, function(){
			if(this == 'pp_see' || this == 'pp_seeall' || this == 'pp_add' || this == 'pp_delete' || 
					this == 'pp_save' || this == 'pp_commit' || this == 'pp_uncommit' || this == 'pp_audit' || this == 'pp_unaudit' || 
					this == 'pp_print' || this == 'pp_disable' || this == 'pp_undisable' || this == 'pp_closed' || this == 'pp_unclosed' ||
					this == 'pp_posting' || this == 'pp_unposting'){
				bool = true;
			}
		});
		return bool;
	}
});