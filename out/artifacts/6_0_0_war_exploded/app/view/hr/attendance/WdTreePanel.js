/*
 * 默认班次人员
 */
Ext.define('erp.view.hr.attendance.WdTreePanel', {
	extend: 'Ext.tree.Panel',
	xtype: 'erpWdTreePanel',
	lines:true,
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	split:true,
	closeAction:'destroy',
	border : false, 
	enableDD : false,
	FormUtil:Ext.create('erp.util.FormUtil'),
	store: Ext.create('Ext.data.TreeStore', {
		root : {
			text: 'root',
			id: 'root',
			expanded: true
		}
	}),
	initComponent : function(){ 
		this.callParent(arguments);
		this.getTreeRootNode(this);
	},
	listeners:{
		itemmousedown: function(selModel, record){
			var tree=selModel.ownerCt;
			if(! tree.itemselector) tree.itemselector=Ext.getCmp('itemselector-field');
			var data=new Array();
			Ext.Array.each(record.raw.data,function(item){
				data.push({
					text:item.em_name,
					value1:item.em_name,
				    value:item.em_id+''
				})
			});
			tree.itemselector.fromField.store.loadData(data);
		}
	},
	getTreeRootNode: function(treepanel){
		treepanel.setLoading(true);
		treepanel.store.removeAll(true);
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'hr/attendance/getWdTreeAndEmployees.action',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				treepanel.setLoading(false);
				if(res.tree){
					var tree = res.tree;		
					treepanel.store.setRootNode({
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
	}
});
