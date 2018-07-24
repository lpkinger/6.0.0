Ext.define('erp.view.oa.batchMail.personalGroup', {
	extend: 'Ext.tree.Panel',
	alias: 'widget.personalGroupPanel',
	border: false, 
	enableDD: false, 
	split: true, 
	id: 'group-tree',
	width: '100%',
	height: '100%',
	expandedNodes: [],
	collapsible: true,
	rootVisible: false, 
	singleExpand: true,
	containerScroll: true,
	autoScroll: true,
    bodyStyle:'background-color:#f1f1f1 !important;',
	initComponent: function(){
		var me = this;
		me.getRootNode();
		me.callParent(arguments);
	},
	listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		},
	},
	getRootNode: function(){
		Ext.Ajax.request({
			url: basePath + 'oa/batchmail/getGroupsTree.action',
			success: function(response){
				var res = new Ext.decode(response.responseText);
				if(res.tree){
					Ext.getCmp('group-tree').store.setRootNode({
                		text: 'root',
                	    id: 'root',
                		expanded: true,
                		children: res.tree
                	});
				}
			}
		});
	},
	showFolderAddWin: function(title, url, record){
		/*//如果是第一次点击新增菜单且该节点未展开过
		if(!record.data.leaf && (record.childNodes == null || record.childNodes == '')){
			Ext.Ajax.request({
				url: basePath + 'oa/batchmail/getPersonByGroupName.action',
				params: {
					groupName: record.data.text
				},
				success: function(response){
					var res = Ext.decode(response.responseText);
					if(res.tree){
						 Ext.Array.each(res.tree, function(item){
          			    	item.leaf = true;
          			    });
						record.appendChild(res.tree);
					}
				}
			});
		}*/
		var win = Ext.create('Ext.window.Window', {
			title: title,
			height: 130,
			width: 400,
			closeAction: 'hide',
			listeners: {
				show: function(){
					Ext.getBody().mask();
				},
				hide: function(){
					Ext.getBody().unmask();
				}
			},
			items: [{
				xtype: 'form',
				layout: 'anchor',
				bodyPadding: 5,
				items: [{
					xtype: 'textfield',
					anchor: '100%',
					name: 'name',
					value: record.data.leaf ? record.data.text : null,
					fieldLabel: '姓名',
					maxLength: 50,
					allowBlank: false
				},{
					xtype: 'textfield',
					anchor: '100%',
					name: 'email',
					value: record.data.leaf ? record.data.cg_email : null,
					fieldLabel: '邮箱地址',
					allowBlank: false,
					maxLength: 50,
					regex: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
					regexText: '输入的邮箱不合法'
				}],
				buttons: [{
					text: '确定',
					formBind: true,
					handler: function(btn){
						var form = btn.ownerCt.ownerCt,
							obj = form.getForm().getFieldValues(),
							name = obj['name'],
							email = obj['email'],
							tree = Ext.getCmp('group-tree');
						Ext.Ajax.request({
							url: basePath + url,
							params: {
								name: name,
								email: email,
								group: record.data.text,
								cgId: record.data.cg_id
							},
							success: function(response){
								//关闭窗口
								btn.ownerCt.ownerCt.ownerCt.close();
								var res = new Ext.decode(response.responseText);
						        Ext.Msg.alert('提示', res.msg);
						        if(title == '新增收件人'){
									//刷新树
									tree.reloadTree(record);
						        }else{
						        	//刷新树
									tree.reloadTree(record);
						        }
						        
							}
						});
					}
				},{
					text: '取消',
					handler: function(btn){
						btn.ownerCt.ownerCt.ownerCt.close();
					}
				}]
			}]
		});
		win.show();
	},
	showFolderUpdateWin: function(record, url){
		var win = Ext.create('Ext.window.Window', {
			title: '编辑',
			height: 100,
			width: 400,
			closeAction: 'hide',
			listeners: {
				show: function(){
					Ext.getBody().mask();
				},
				hide: function(){
					Ext.getBody().unmask();
				}
			},
			items: [{
				xtype: 'form',
				layout: 'anchor',
				bodyPadding: 5,
				items: [{
					xtype: 'textfield',
					anchor: '100%',
					name: 'name',
					value: record.data.text,
					fieldLabel: '通讯组名',
					allowBlank: false
				}],
				buttons: [{
					text: '确定',
					formBind: true,
					handler: function(btn){
						var form = btn.ownerCt.ownerCt,
							name = form.getForm().getFieldValues()['name'];
						Ext.Ajax.request({
							url: basePath + url,
							params: {
								name: name,
								group: record.data.text
							},
							success: function(response){
								var res = new Ext.decode(response.responseText);
						        Ext.Msg.alert('提示', res.msg);
						        //关闭窗口
								btn.ownerCt.ownerCt.ownerCt.close();
								//刷新树
								record.set('text',name);
								Ext.getCmp('group-tree').reloadTree(record);
								
							}
						});
					}
				},{
					text: '取消',
					handler: function(btn){
						btn.ownerCt.ownerCt.ownerCt.close();
					}
				}]
			}]
		});
		win.show();
	},
	reloadTree: function(record, func){
		Ext.Ajax.request({
			url: basePath + 'oa/batchmail/getPersonByGroupName.action',
			params: {
				groupName: record.data.cg_group ? record.data.cg_group : record.data.text
			},
			success: function(response){
				var res = Ext.decode(response.responseText),node = record;
				Ext.Array.each(res.tree,function(item){
					item.leaf = true;
				});
				if(node.data.leaf){
					var groupNode = node.parentNode;
					groupNode.removeAll();
					groupNode.appendChild(res.tree);
				}else{
					node.removeAll();
					node.appendChild(res.tree);
					if(func) {
						func();
					}
				}
			}
		});
	}
		
});