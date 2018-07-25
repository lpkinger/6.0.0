Ext.QuickTips.init();
Ext.define('erp.view.plm.task.ProjectWeekPlanTree', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.erpProjectWeekPlanTree',
	title : '产品',
	bodyStyle : 'background-color:#f1f1f1;',
	width : 300,
	id : 'prjWeekPlanTree',
	collapsible : true,
	collapsed:true,
	split: true, 
	bodyStyle : 'background-color:#f1f1f1;',
	FormUtil : Ext.create('erp.util.FormUtil'),
	columns : [{
		xtype : 'treecolumn',
		text : '文件名称',
		dataIndex : 'name',
		sortable : true,
		flex : 1
	},{
		text:'类型',
		dataIndex:'type',
		hidden:true
	}],
	store : Ext.create('Ext.data.TreeStore', {
		fields : ['pt_id','pt_code','name','pt_description','pt_subof','type'],
		autoLoad:false,
		root : {
			text : 'Root',
			id : 0,
			expanded : true
		}
	}),
	rootVisible : false,
	hideHeaders : true,
	initComponent:function(){
		this.setRootNode(this);
		this.callParent(arguments);
	},

	dockedItems : [{
		xtype : 'toolbar',
		dock : 'top',
		layout : {
			pack : 'center'
		},
		items : [{
			xtype:'button',
			text : '刷新',
			id : 'refreshButton',
			width : 70,
			icon:basePath + 'resource/images/refresh.gif',
			cls : 'x-btn-gray',
			handler:function(btn){
				var tree = Ext.getCmp('prjWeekPlanTree');
				Ext.getCmp('prjWeekPlanTree').setRootNode();									
				var grid = Ext.getCmp('batchDealGridPanel');
				grid.defaultCondition = '';
				
				//清空项目信息
				var form = Ext.getCmp('dealform');
				Ext.Array.each(form.items.items,function(item,index){
					var index = item.dataIndex;
					if(index=='prj_name'||index=='prj_class'||index=='prj_assignto'||index=='prj_status'||index=='prj_producttype'){
						item.setValue(null);
					}
				});
			}
		}],
		border : false
	}],

	listeners:{
		beforeitemclick:function(tree,record,item,index,e,eOpts){
			if(e.target.className.indexOf('x-tree-expander')>-1){
				return true;
			}
			return false;
		}
	},

	setRootNode:function(){
		var me = this;
		Ext.Ajax.request({
			url:basePath + 'plm/task/getProjectAndProductList.action',
			method:'post',
			async: false,
			callback:function(options,success,resp){
				var res = new Ext.decode(resp.responseText);
				if(res.success){
					if(res.datas){
						me.getStore().setRootNode({
							text: 'root',
							id: 'root',
							expanded: true,
							children: res.datas
						});	
					}
				}else if(res.exceptionInfo){
					showError(res.exceptionInfo);	
				}
			}
		});

	}
});
