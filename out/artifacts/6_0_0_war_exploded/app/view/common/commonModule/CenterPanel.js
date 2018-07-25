Ext.define('erp.view.common.commonModule.CenterPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.centerpanel',
	id: 'centerpanel',
	layout: 'anchor',
	initComponent: function() {
		var me = this;
		deleteCommonItem = function(id, text, index, group) {
			Ext.Msg.confirm('警告', '确定'+(group?'清空分组':'删除')+'['+'<a style="color:red;">'+text+'</a>'+']?', function(yes) {
				if(yes === 'yes') {
					if(group) {
						me.clearGroup(id, index);
					}else {
						me.deleteItem(index);
					}
				}
			});
		}
		var view = new Ext.DataView({
			store : Ext.create('Ext.data.Store', {
				fields: ['cuid', 'id', 'parentId', 'text', 'group', 'groupid', 'index', 'url', 'addurl', 'expanded', 'url', 'addurl'],
				data:[]
			}),
			tpl : new Ext.XTemplate(
				'<div class="x-module-parent">',
					'<tpl for=".">',
			    	'<div class="x-module-item ',
			    	'<tpl if=\"group\">x-module-title </tpl>', // 分组
			    	'<tpl if=\"id==\'-1\'\">x-module-ungroup-title </tpl>', //[未分组]添加特殊样式
			    	'<tpl if=\"!group\">x-module-child </tpl>" ', // 子项
			    	'<tpl if="!group"></tpl>>',
			    		'<span data-qtip="{text}">{text}</span>',
			    		'<div class="x-module-delete-icon" onClick="deleteCommonItem(\'{id}\',\'{text}\',\'{index}\',{group})" data-qtip="<tpl if=\"group\">清空</tpl><tpl if=\"!group\">删除</tpl>"></div>',
			    	'</div>',
			    	'</tpl>',
				'</div>'
			),
			trackOver: true,
			overItemCls : 'x-module-over',
			selectedClass : 'selected',
			singleSelect : true,
			itemSelector : '.x-module-item'
		});
		
		view.on('render', function() {
			me.fireEvent('addDragListener', view);
			me.fireEvent('addDropListener', view);
		});
		view.on('beforerefresh', function(view, eOpts) {
			var div = document.getElementsByClassName('x-module-parent');
			view.scrollTop = div.length>0?div[0].scrollTop:0;
		});
		view.on('refresh', function() {
			var centerPanel = Ext.getCmp('dataviewpanel');
			me.resetViewSize(centerPanel.getWidth(), centerPanel.getHeight());
		});
		
		Ext.apply(me,{
			items:[{
				xtype: 'panel',
				anchor: '100% 100%',
				id: 'dataviewpanel',
				items: [view]
			}],
			tbar: [{
				xtype: 'tbtext', text: '自定义导航', style: 'font-weight:bold'
			}, '&nbsp;&nbsp;&nbsp;', '提示：可通过拖动进行模块的添加以及位置的调整', '->', {
				xtype: 'button',
				text: '分组管理',
				handler: function() {
					var win = Ext.create('erp.view.common.commonModule.ModuleGroupWin', {
						groupData: me.getAllGroup()
					});
					win.show();
				}
			}, {
				xtype: 'button',
				text: '保存',
				handler: function() {
					me.fireEvent('onSave', me);
				}
			}, {
				xtype: 'button',
				text: '同步',
				handler: function() {
					var win = Ext.getCmp('synchronousWin') || Ext.create('erp.view.common.commonModule.SynchronousWin');
					win.show();
				}
			}]
		});
		me.view = view;
		me.callParent(arguments); 
	},
	/**
	 * 新增项目
	 */
	addItem: function(data) {
		var me = this,
			cuid = data.cuid,
			groupid = data.groupid,
			group = me.getGroupByID(groupid),
			store = me.view.store;
			
		if(group) {
			var viewData = me.getViewData();
			for(var i = 0; i < viewData.length; i++) {
				var d = viewData[i];
				if(d.group && d.id == groupid) {
					var index = i + group.items.length + 1;
					var newItem = {
			    		cuid: data.cuid,
			    		id: data.id,
			    		parentId: data.parentId,
			    		index: index,
			    		text: data.text,
			    		group: data.group,
			    		groupid: data.groupid,
			    		url: data.url,
			    		addurl: data.addurl
			    	};
					
					store.insert(index, newItem);
					break;
				}
			}
			// 调整序号
			store.data.each(function(d,i){if(d.get('index')>=index){d.set('index', i);}});
			return true;
		}
		return false;
	},
	/**
	 * 通过id获得item
	 */
	getItemByID: function(id) {
		var me = this;
		var idx = -1;
		var viewData = me.getViewData();
		for(var i = 0; i < viewData.length; i++) {
			if((''+viewData[i].id) == (''+id)) {
				idx = i;
				break;
			}
		}
		if(idx != -1) {
			return me.view.store.getAt(idx);
		}
		return null;
	},
	save: function() {
		var me = this,
			groupData = me.getAllGroup();
	},
	/**
	 * 导入分组数据
	 */
	importGroupData: function(groupData) {
		var me = this;
		var viewData = [];
		for(var i = 0; i < groupData.length; i++) {
			var group = groupData[i];
			viewData.push({
				cuid: group.cuid,
				id: group.id,
				text: group.text,
				expanded: group.expanded,
				group: group.group,
				index: viewData.length,
				url: group.url,
				addurl: group.addurl
			});
			for(var j = 0; j < group.items.length; j++) {
				var item = group.items[j];
				viewData.push({
					cuid: item.cuid,
					id: item.id,
					parentId: item.parentId,
					text: item.text,
					group: item.group,
					groupid: item.groupid,
					index: viewData.length,
					url: item.url,
					addurl: item.addurl
				});
			}
		}
		me.view.store.removeAll();
		me.view.store.add(viewData);
	},
	/**
	 * 通过groupid获得分组数据
	 */
	getGroupByID: function(groupid) {
		var me = this;
		var groupData = me.getAllGroup();
		var group;
		for(var i = 0; i < groupData.length; i++) {
			if(groupData[i].id == groupid) {
				group = groupData[i];
				break;
			}
		}
		return group;
	},
	/**
	 * 通过group索引获得分组数据
	 */
	getGroupByIndex: function(index) {
		var me = this;
		var groupData = me.getAllGroup();
		var group = groupData[index];
		return group;
	},
	/**
	 * 获得全部分组数据
	 */
	getAllGroup: function() {
		var me = this,
			store = me.view.store;
		me.groupData = [];
		me.group = false;
		store.data.items.map(function(d, i) {
			var data = d.data;
			// 如果是分组则创建一个group
			if(data.group) {
				// 如果之前已存在group则说明上一个group已经完结，将其加入groupData
				if(me.group) {
					me.groupData.push(me.group);
				}
				me.group = {
					cuid: data.cuid || null,
					id: data.id,
					index: me.groupData.length,
					text: data.text,
					group: true,
					expanded: data.expanded || false,
					items: [],
					url: data.url,
					addurl: data.addurl
				}
			}else {
				// 这种情况有游离的条目在最开头（不属于任何分组）
				if(!me.group) {
					Ext.Msg.alert('错误', '获取分组信息错误');
					return null;
				}
				me.group.items.push({
					cuid: data.cuid || null,
					id: data.id,
					parentId: data.parentId,
					index: me.group.items.length,
					text: data.text,
					group: false,
					groupid: data.groupid,
					url: data.url,
					addurl: data.addurl
				})
			}
			// 如果这是最后一条记录，该分组已经完结，将其返回
			if(i==store.data.items.length-1) {
				me.groupData.push(me.group);
			}
		});
		delete me.group;
		return me.groupData;
	},
	/**
	 * 获得dataview中的数据
	 */
	getViewData: function() {
		var me = this;
		return me.view.store.data.items.map(function(i) {return i.data;});
	},
	/**
	 * 删除条目
	 */
	deleteItem: function(index) {
		var me = this;
		var nodeRecord = Ext.getCmp('tree-panel').getStore().tree.root.findChild('id',me.view.store.getAt(index).get('id'),true);
		me.view.store.removeAt(index);
		me.view.store.data.each(function(d,i){d.set('index', i);});
		me.viewData = me.getViewData();
		if(nodeRecord) {
			nodeRecord.set('commonuse', false);
		}
	},
	/**
	 * 清空分组
	 */
	clearGroup: function(groupid, index) {
		index = Number(index);
		var me = this;
		me.groupData = me.getAllGroup();
		var group = me.getGroupByID(groupid);

		if(group) {
			for(var i = index+1; i <= group.items.length + index; i++) {
				var nodeRecord = Ext.getCmp('tree-panel').getStore().tree.root.findChild('id',me.view.store.getAt(i).get('id'),true);
				if(nodeRecord) {
					nodeRecord.set('commonuse', false);
				}
			}
			me.groupData[group.index].items = [];
		}
		me.importGroupData(me.groupData);
	},
	/**
	 * 重设view视图界面的大小以及滚动条位置 
	 */
	resetViewSize: function(width, height) {
		var me = this,
			parentDiv = document.getElementsByClassName('x-module-parent')[0];
		if(!parentDiv) {
			return;
		}
		parentDiv.style.width = height + 'px';
		parentDiv.style.height = width + 'px';
		parentDiv.style.transform = 'rotate(-90deg) translate(-' + height + 'px)';
		parentDiv.style['-webkit-transform'] = 'rotate(-90deg) translate(-' + height + 'px)';
		parentDiv.style['-moz-transform'] = 'rotate(-90deg) translate(-' + height + 'px)';
		parentDiv.style['-o-transform'] = 'rotate(-90deg) translate(-' + height + 'px)';
		if(parentDiv.offsetHeight < parentDiv.scrollHeight) {
			parentDiv.style['overflow-y'] = 'scroll';
			parentDiv.scrollTop = me.view.scrollTop;
		}else {
			parentDiv.style['overflow-y'] = 'hidden';
		}
    }
});