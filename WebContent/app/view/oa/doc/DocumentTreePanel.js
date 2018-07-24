Ext.define('erp.view.oa.doc.DocumentTreePanel', {
	extend: 'Ext.tree.Panel',
	xtype: 'erpDocumentTreePanel',
	/*   title: '文档管理',*/
	cls: 'doclist',
	id:'doctree',
	lines:false,
	rootVisible: false, 
	containerScroll : true, 
	autoScroll: false, 
	useArrows: true,
	store: Ext.create('Ext.data.TreeStore', {
		root: {
			expanded: true,
			children: [{
				text: '公共文档',
				expanded: false,
				iconCls:'x-tree-icon-topFolder',
				url:"/公共文档",
				id:0
			},
			{
				text: '我的文档',
				expanded: false,
				iconCls:'x-tree-icon-myFolder',
				url:"/我的文档",
				id:1
			},{
				text: '回收站',
				url:"/回收站",
				expanded: false,
				leaf:true,
				iconCls:'x-tree-icon-trash',
				id:5
			}
			//20170929 问题反馈:2017090484 隐藏项目文档，只能去掉，项目文档id为-1
			/*{
				text: '项目文档',
				url:"/项目文档",
				expanded: false,
				iconCls:'x-tree-icon-topFolder',
				id:-1
			},*/
			/*,
			{
				text: '我的常用文档',
				url:"/我的常用文档",
				expanded: false,
				leaf:true,
				iconCls:'x-tree-icon-myfav',
				id:2
			},
			{
				text: '借给我的文档',
				url:"/借给我的文档",
				expanded: false,
				leaf:true,
				iconCls:'x-tree-icon-mylendings',
				id:3
			},
			{
				text: '我借出的文档',
				url:"/我借出的文档",
				expanded: false,
				leaf:true,
				iconCls:'x-tree-icon-mylends' ,
				id:4
			}*/
			]
		}
	}),
	refreshNodeByParentId:function (parentId,tree,record){
		var condition = this.baseCondition;
        var record=record||tree.getSelectionModel().lastFocused;
        record.removeAll(true);
        var me = this;
		Ext.Ajax.request({//拿到tree数据
			url : basePath + 'oa/documetlist/loadDir.action',
			params: {
				parentId: parentId,
				condition: condition
			},
			callback : function(options,success,response){
				tree.setLoading(false);
				var res = new Ext.decode(response.responseText);
				if(res.tree){
					if(!record.get('level')) {
						record.set('level', 0);
					}
					Ext.each(res.tree, function(n){
						if(n.showMode == 2){//openBlank
							n.text = "<a href='" + basePath + me.parseUrl(n.url) + "' target='_blank'>" + n.text + "</a>";
						}
						if(!n.leaf) {
							n.level = record.get('level') + 1;
							n.iconCls = 'x-tree-icon-level-' + n.level;
						}
					});
					record.appendChild(res.tree);
					record.expand(false,true);//展开
					me.flag = true;
				} else if(res.exceptionInfo){
					showError(res.exceptionInfo);
					me.flag = true;
				}
			}
		});
	}
});
