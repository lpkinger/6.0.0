/**
 * ERP项目gridpanel通用样式4:treegrid
 */
Ext.define('erp.view.core.grid.TreeGrid',{ 
	extend: 'Ext.tree.Panel', 
	alias: 'widget.erpTreeGrid',
	region: 'south',
	layout : 'fit',
	id: 'treegrid', 
 	emptyText : $I18N.common.grid.emptyText,
    useArrows: true,
    rootVisible: false,
    singleExpand: true,
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
		iconCls: 'tree-add',
		text: $I18N.common.button.erpAddButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid'),
				nodes = treegrid.store.tree.root.childNodes;
			var items = treegrid.selModel.selected.items;
			if(items.length > 0){
				var id = items[0].data[treegrid.mainField];
				Ext.each(nodes, function(){
					if(this.data[treegrid.mainField] == id){
						var o = {
							cls: 'x-tree-cls-node',
							leaf: true
						};
						o.parentId = id;
						o[treegrid.mainField] = id;
						o[treegrid.detno] = treegrid.getMaxIndex() + 1;
						if(treegrid.detailDetno){
							o[treegrid.detailDetno] = this.data[treegrid.detailDetno];
						}
						this.appendChild(o);
					}
				});
			} else if(treegrid.getExpandItem()){
				var record = treegrid.getExpandItem();
				var id = record.data[treegrid.mainField];
				Ext.each(nodes, function(){
					if(this.data[treegrid.mainField] == id){
						var o = {
							cls: 'x-tree-cls-node',
							leaf: true
						};
						o.parentId = id;
						o[treegrid.mainField] = id;
						o[treegrid.detno] = treegrid.getMaxIndex() + 1;
						if(treegrid.detailDetno){
							o[treegrid.detailDetno] = record.data[treegrid.detailDetno];
						}
						this.appendChild(o);
					}
				});
			}
		}
	},{
		iconCls: 'tree-delete',
		text: $I18N.common.button.erpDeleteButton,
		handler: function(){
			var treegrid = Ext.getCmp('treegrid');
			var items = treegrid.selModel.selected.items;
			if(items.length > 0 && items[0].isLeaf() == true){
				items[0].remove(true);
			}
		}
	}],
	bodyStyle:'background-color:#f1f1f1;',
    necessaryField: '',//必填字段
    detno: '',//编号字段
    keyField: '',//主键字段
	mainField: '',//对应主表主键的字段
	groupbyField: '',//
	initComponent : function(){ 
		Ext.override(Ext.data.AbstractStore,{
			indexOf: Ext.emptyFn
		});
		this.callParent(arguments);
	},
	listeners: {//滚动条有时候没反应，添加此监听器
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	getTreeGridNode: function(param){
		var me = this;
		Ext.Ajax.request({//拿到tree数据
        	url : basePath + 'common/singleGridPanel.action',
        	params: param,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.data){
        			var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        			//singleGridPanel.action请求得到的是一个GridPanel
        			//下面将grid解析成一个TreePanel
        			//需要配置groupbyField
        			var tree = [];
        			Ext.each(data, function(d){
        				d.parentId = d[me.groupbyField];
        				d.cls = 'x-tree-cls-node';
        				d.leaf = true;
        				var o = [];
        				o[me.groupbyField] = d.parentId;
        				o[me.detno] = 'group' + d.parentId;
        				if(me.detailDetno){
        					o[me.detailDetno] = d[me.detailDetno];
        				}
        				if(me.isGroup(tree, o, me.groupbyField)){
        					o.cls = 'x-tree-cls-parent';
        					o.leaf = false;
        					o.children = me.getChildren(data, o, me.groupbyField);
        					tree.push(o);
        				}
        			});
        			me.store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: tree
                	});
        		} else if(res.exceptionInfo){
        			showError(res.exceptionInfo);
        		}
        	}
        });
	},
	getExpandItem: function(){
		var nodes = this.store.tree.root.childNodes;
		var node = null;
		Ext.each(nodes, function(){
			if(this.isExpanded()){
				node = this;
			}
		});
		return node;
	},
	isGroup: function(tree, d, field){
		var count = 0;
		Ext.each(tree, function(){
			if(this[field] == d[field]){
				count++ ;
			}
		});
		return count == 0;
	},
	getChildren: function(data, d, field){
		var o = [];
		Ext.each(data, function(){
			if(this[field] == d[field]){
				o.push(this);
			}
		});
		return o;
	},
	getMaxIndex: function(record){//record下子节点的index的最大值
		record = record == null ? this.getExpandItem() : record;
		var me = this;
		var index = 0;
		Ext.each(record.childNodes, function(){
			var detno = this.data[me.detno];
			index = detno > index ? detno : index;
		});
		return Number(index);
	}
});