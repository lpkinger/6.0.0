Ext.define('erp.view.common.ButtonGroupSetPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.ButtonGroupSetPanel',
	id: 'ButtonGroupSetPanel',
	layout: 'anchor',
	initComponent: function() {
		var me = this;
		updateButton = function(caller, groupid, text, index) {
			me.updateButton(caller, groupid, text, index)
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
			    	'<tpl if=\"!group\">x-module-child </tpl>" ', // 子项
			    	'<tpl if="!group"></tpl>>',
			    		'<span data-qtip="{text}  {_xtype}">{text}</span>',
			    		'<tpl if="!group"><div class="x-module-delete-icon" onClick="updateButton(\'{caller}\',\'{groupid}\',\'{text}\',{index})" data-qtip="<tpl if=\"group\"></tpl><tpl if=\"!group\">修改按钮名称</tpl>"></div></tpl>',
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
			bbar: ['->',{
				xtype: 'button',
				cls: 'x-btn-gray',
				height:22,
				text: '保存',
				handler: function() {
					me.fireEvent('onSave', me);
				}
			},{xtype:'splitter',width:10},{
				xtype: 'button',
				cls: 'x-btn-gray',
				height:22,
				text: '还原',
				handler: function() {
					me.fireEvent('reSet', me);
				}
			},{xtype:'splitter',width:10},{
				xtype: 'button',
				cls: 'x-btn-gray',
				height:22,
				text: '关闭',
				handler: function() {
					parent.Ext.getCmp('buttonGroupSetWin').close();
				}
			},'->']
		});
		me.view = view;
		me.callParent(arguments); 
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
	/**
	 * 导入分组数据
	 */
	importGroupData: function(groupData) {
		var me = this;
		var viewData = [];
		for(var i = 0; i < groupData.length; i++) {
			var group = groupData[i];
			viewData.push({
				caller: group.caller,
				text: group.name,
				expanded: false,
				group: group.group,
				groupid: group.groupid,
				index: viewData.length
			});
			for(var j = 0; j < group.items.length; j++) {
				var item = group.items[j];
				viewData.push({
					caller: item.caller,
					text: item.text,
					_xtype: item._xtype,
					group: item.group,
					groupid: item.groupid,
					index: viewData.length
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
					groupid: data.groupid,
					index: me.groupData.length + 1,
					text: data.text,
					group: true,
					expanded: data.expanded || false,
					items: []
				}
			}else {
				// 这种情况有游离的条目在最开头（不属于任何分组）
				if(!me.group) {
					Ext.Msg.alert('错误', '获取分组信息错误');
					return null;
				}
				me.group.items.push({
					groupid: data.groupid,
					xtype: data._xtype,
					index: me.group.items.length + 1,
					text: data.text,
					group: false
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
    },
    updateButton: function(caller, groupid, text, index){
    	var win =new Ext.window.Window({
			title: '<span style="color:#115fd8;">修改按钮名称</span>',
			draggable:true,
			height: '30%',
			width: '30%',
			resizable:false,
			id:'updateButton',
			iconCls:'x-button-icon-set',
	   		modal: true,
	   		bbar:['->',{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			text:'保存',
	   			height:22,
	   			handler:function(btn){
	   				var t = Ext.getCmp('change');
	   				if(!t.value || (t.value&& t.value.trim()=='')){
	   					Ext.Msg.alert('提示','请填写正确的名称');
	   					return;
	   				}
	   				var newText = t.value.trim();
	   				if(newText==text){
	   					Ext.Msg.alert('提示','请修改名称后保存');
	   					return;
	   				}
					warnMsg('确定修改按钮名称吗？', function(btn){
					 	if(btn == 'yes'){
							Ext.Ajax.request({
								url: basePath + 'crm/updateButton.action',
								params: {
									caller:caller,
									groupid:groupid,
									oldText:text,
									newText:newText
								},
								async:false,
								method : 'post',
								callback : function(options,success,response){
									var localJson = new Ext.decode(response.responseText);
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}
									if(localJson.success){
										var groupid = localJson.groupid;
										var newText = localJson.newText;
										var oldText = localJson.oldText;
										var allData = Ext.getCmp('ButtonGroupSetPanel').view.store.data.items;
										Ext.Array.each(allData,function(item){
											if(item.get('groupid')==groupid&&item.get('text')==oldText){
												item.set('text', newText)
											}
										});
										showMessage('提示', '修改成功!', 3000);
										var win = Ext.getCmp('updateButton');
	   									win.close();
									} else {
										showMessage('提示', '修改失败!', 3000);
									}
								}
							});
						}
					});
	   			}
	   		},{xtype:'splitter',width:10},{
	   			cls:'x-btn-gray',
	   			xtype:'button',
	   			height:22,
	   			text:'取消',
	   			handler:function(btn){
	   				var win = Ext.getCmp('updateButton');
	   				win.close();
	   			}
	   		},'->'],
		   	items: [{
		   		id:'change',
				padding:'10 0 0 0',
				fieldLabel:"新按钮名称",
				fieldStyle:"background:#fff;",
				hideTrigger:false,
				labelAlign:"left",
				labelStyle:"color:black",							
				maxLength:50,
				maxLengthText:"字段长度不能超过50字符!",
				name:"changeHandler",
				readOnly:false,
				xtype:"textfield",
				value:text
    		}]
		});
		win.show();
    }
});