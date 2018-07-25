Ext.define('erp.view.sys.alert.AssignSqlWindow2', {
	extend: 'Ext.window.Window',
	alias: 'widget.assignSqlWin',
	id: 'assignSqlWin2',
	title: '定义推送人sql',
	modal: true,
	width: 660,
	height: '70%',
	autoScroll: true,
	resizable: false,
	idkey: 0,
	initComponent : function(){
		var me = this;
		var argsgrid = Ext.getCmp('grid');
		var sqls = argsgrid.getStore().getAt(me.storeIndex-1).get('aia_mansql');
		Ext.apply(me, {
			tbar: [{
				xtype:'button',
				text:'新增级别',
				width:90,				
				iconCls:'newaddpanelico',
				handler:function(){
					var form = me.down("form");
					me.insertFieldSet();
				}
			}, '-', {
				xtype: 'tbtext',
				text: '插入参数'
			}, {
				xtype: 'container',
				width: 400,
				height: 30,
				autoScroll: true
			}, '->', {
				xtype:'button',
				text:'清空级别',
				iconCls:'clearconditionspico',
				width:90,
				listeners:{
					click:function(){
						var conpanel=this.ownerCt.ownerCt.down('form');
						conpanel.removeAll();
					}
				}
			}],
			items: [{
				xtype: 'form',
				bodyPadding: '5 10 5 10',
				autoScroll: true,
				layout: 'column'
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'button',
				text: '确定',
				handler: function() {
					var form = me.down('form');
					var values = form.getForm().getValues();
					var arr1 = [];
					var arr2 = [];
					var argsgrid = Ext.getCmp('grid'),
						store = argsgrid.getStore(),
						rowStore = store.getAt(Number(me.storeIndex)-1);
					var count = 0;	
					var sqls = [];
					
					if(Ext.getCmp('aii_statuscode').value!='ENTERING') {
						me.close();
						return;
					}
					for(var key in values) {
						count++;
					}
					for(var key in values) {
						if(('' + values[key]).length == 0) {
							showMessage('提示', '级别' + count + '未设置推送sql', 1000);
							return;
						}
						sqls.push(count+"$"+values[key]);
						count--;
					}
					
					if(rowStore){
						rowStore.set('aia_mansql', sqls.join('#'));
					}
					me.close();
				}
			}],
			listeners: {
				afterrender: function(win) {
    				var itemId = Ext.getCmp('aii_itemid').value;
					Ext.Ajax.request({
						url: basePath + 'sys/alert/getOutputParams.action',
						params: {
							itemId: itemId,
							caller: caller
						},
						method: 'post',
						callback: function(options,success,response) {
				   			var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								var data = [];
								Ext.Array.each(localJson.data, function(d, i) {
									data.push({
										text: d['ao_resultdesc'],
										value: d['ao_resultname']
									});
								});
								
								win.insertParam(data);
								win.init(sqls);
							} else {
								delFailure();
							}
						}
					});
    			}
			}
		});
		me.callParent(arguments);
	},
	init: function(sqls) {
		var me = this;
		
		if(!sqls) {
			me.insertFieldSet();
			return;
		}
		var o = {};
		sqls.split('#').map(function(v, i) {
			if(v.indexOf('$') == -1) {
				v = '1$'+v;
			}
			var k = v.split('$');
			if(o.hasOwnProperty(k[0])) {
				o[k[0]].push(k[1]);
			}else {
				o[k[0]] = [k[1]]
			}
		});
		
		var level = 1;
		for(var key in o) {
			if(key+'' == level+'') {
				me.insertFieldSet(o[key].join(';'), o[key].join(';'));
			}
			level++;
		}
	},
	insertParam: function(data) {
		var me = this;
		var items = {};
		if(data.length == 0) {
			me.dockedItems.items[1].items.items[3].add(Ext.create('Ext.toolbar.TextItem', {
				text: '无参数',
				style: 'color: red !important;margin-top: 7px;margin-left: 10px;',
				listeners: {
					afterrender: function(tb) {
						tb.el.dom.setAttribute('style', "color: red !important; margin-top: 7px; margin-left: 10px;")
					}
				}
			}));
			return;
		}
		if(data.length < 8) {
			for(var i = 0; i < data.length; i ++) {
				var d = data[i];
				me.dockedItems.items[1].items.items[3].add(Ext.create('Ext.button.Button', {
					text: d.text,
					value: d.value,
					tooltip: d.value,
					style: 'border-radius: 4px;margin-left: 4px;margin-top: 2px;',
					handler: function(btn) {
						if(Ext.getCmp(me.focusid)) {
							me.insertValue(Ext.getCmp(me.focusid), btn.value);
						}
					}
				}));
			}
		}else {
			var its = data.map(function(d, i) {
				return {
					xtype: 'button',
					text: d.text,
					value: d.value,
					tooltip: d.value,
					style: 'border-radius: 4px;',
					handler: function(item) {
						if(Ext.getCmp(me.focusid)) {
							me.insertValue(Ext.getCmp(me.focusid), item.value);
						}
					}
				}
			});
			me.dockedItems.items[1].items.items[3].add(Ext.create('Ext.button.Button', {
			    text: '选择...',
			    style: 'border-radius: 4px;margin-left: 4px;margin-top: 2px;',
			    width: 100,
			    menu: Ext.create('Ext.menu.Menu', {
			    	items: its,
			    	width: 'auto',
			    	style: 'border-radius: 4px;',
			    	listeners: {
			    		mouseover: function() {
							this.over = true;
						},
						mouseleave: function(menu, e) {
							var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
							var box = menu.el.dom.getBoundingClientRect();
							if( cx <= (box.left) || cx >= (box.left+box.width) || /*cy <= (box.top-15) ||*/ cy >= (box.top+box.height) ) {
								menu.over = false;
								menu.hide();
							}
						}
			    	}
			    }),
			    listeners: {
			    	mouseover:function(btn){
						btn.menu.isVisible() ? '' : btn.showMenu();
					},
					mouseout: function(btn, e) {
						var cx = e.browserEvent.clientX, cy = e.browserEvent.clientY;
						var btnLayout = btn.el.dom.getBoundingClientRect();
						if(cx <= btnLayout.left || cx >= btnLayout.left+btnLayout.width || cy <= btnLayout.top) {
							btn.hideMenu();
						}
					}
			    }
			}));
		}
		me.doLayout();
	},
	insertFieldSet: function(sql) {
		var me = this;
		var form = me.down("form");
		var level = form.items.getCount() + 1;
		var fieldSet = {
			xtype: 'fieldset',
			id: 'fieldset'+me.idkey,
			title: '<span style="padding-right: 16px;">'+(level) + '级推送人'+'</span><i onClick="removeSqlFieldSet(\''+'fieldset'+me.idkey+'\');" class="x-btn-icon x-button-icon-close datalist-close" style="width: 16px;position: absolute;margin-left: -16px;cursor: pointer" data-qtip="删除">&nbsp;</i>',
			collapsible: true,
			columnWidth: 1,
			padding: 0,
			level: level,
			layout: 'fit',
			items: [{
				xtype: 'textarea',
				padding: 0,
				height: 100,
				name: 'assignSql'+me.idkey,
				id: 'assignSql'+me.idkey,
				value: sql,
				listeners: {
					focus: function(field) {
						me.focusid = field.id;
					}
				}
			}]
		};
		form.insert(0, fieldSet);
		me.idkey++;
	},
	/**
	 * 新增删除fieldset之后重新设置fieldset标题名称
	 * 只修改删除级别之上的组件
	 */
	refreshView: function(clevel) {
		var me = this;
		var form = me.down('form');
		var fieldsets = form.items.items;
		for(var i = 0; i < fieldsets.length; i++) {
			var a = fieldsets[i];
			if(a.level > clevel) {
				a.level--;
		 		a.setTitle('<span style="padding-right: 16px;">'+a.level + '级推送人'+'</span><i onClick="removeSqlFieldSet(\''+a.id+'\');" class="x-btn-icon x-button-icon-close datalist-close" style="width: 16px;position: absolute;margin-left: -16px;cursor: pointer" data-qtip="删除">&nbsp;</i>');
			}
		}
	},
	insertValue: function(el, value) {
        if (el.inputEl.dom.setSelectionRange) {  
            var withIns = el.inputEl.dom.value.substring(0,  
                el.inputEl.dom.selectionStart)  
                + value;// 获取光标前的文本+value  
            var pos = withIns.length;// 获取光标前文本的长度  
            el.inputEl.dom.value = withIns  
                + el.inputEl.dom.value.substring(  
                    el.inputEl.dom.selectionEnd,  
                    el.inputEl.dom.value.length);// 光标前文本+获取光标后文本  
            el.focus(); // 重新选中输入框
            el.inputEl.dom.setSelectionRange(pos, pos);// 设定光标位置  
        } else if (document.selection) {  
            document.selection.createRange().text = value;// 获取激活文本块  
        }  
    }
});
var removeSqlFieldSet = function(id) {
	var win = Ext.getCmp('assignSqlWin2');
	var form = win.down('form');
	var fieldSet = Ext.getCmp(id);
	form.remove(fieldSet);
	win.refreshView(fieldSet.level);
}