Ext.define('erp.view.sys.alert.AssignSqlWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.assignsqlwin',
	id: 'assignSqlWin',
	title: 'SQL设置',
	modal: true,
	width: '50%',
	initComponent : function(){
		var me = this;
		var argsgrid = Ext.getCmp('grid');
		var v = argsgrid.getStore().getAt(me.storeIndex-1).get('aia_mansql');
		Ext.apply(me, {
			items: [{
				xtype: 'form',
				id: 'assignSqlForm',
				layout: 'column',
				items: [{
					xtype: 'textarea',
					padding: 0,
					height: 200,
					columnWidth: 1,
					value: v,
					name: 'assignSql',
					id: 'assignSql'
				}]
			}],
			dockedItems: [{
				xtype: 'toolbar',
				dock: 'top',
				layout: 'column',
				items: [{
					xtype: 'tbtext',
					margin: '3 0 0 0',
					text: '插入参数：'
				}]
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'button',
				text: '确定',
				handler: function() {
					var assignSqlWin = Ext.getCmp('assignSqlWin'),
						form = Ext.getCmp('assignSqlForm'),
						formValue = form.getForm().getValues(),
						argsgrid = Ext.getCmp('grid'),
						store = argsgrid.getStore(),
						rowStore = store.getAt(Number(assignSqlWin.storeIndex)-1);
					
					if(Ext.getCmp('aii_statuscode').value!='ENTERING') {
						assignSqlWin.close();
						return;
					}
					if(rowStore){
						rowStore.set('aia_mansql', formValue['assignSql']);
					}
					assignSqlWin.close();
				}
			}]
		});
		me.callParent(arguments);
	},
	insertParam: function(text, value) {
		var me = this;
		var form = Ext.getCmp('assignSqlForm');
		
		me.dockedItems.items[1].items.add(Ext.create('Ext.button.Button', {
			text: text,
			value: value,
			handler: function(btn) {
				if(btn.value) {
					me.insertValue(Ext.getCmp('assignSql'), '@'+btn.value);
				}
			}
		}));
		me.height = undefined; // 每次设置容器高度为未知以触发源码中重新布局功能，否则按钮显示不出来
		me.doLayout();
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