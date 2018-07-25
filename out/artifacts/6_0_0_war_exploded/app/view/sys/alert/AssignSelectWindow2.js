Ext.define('erp.view.sys.alert.AssignSelectWindow2', {
	extend: 'Ext.window.Window',
	alias: 'widget.conditionsqlwin2',
	id: 'assignSelectWin2',
	title: '指定推送人',
	modal: true,
	width: 630,
	height: '70%',
	autoScroll: true,
	resizable: false,
	idkey: 0,
	initComponent : function(){
		var me = this;
		var argsgrid = Ext.getCmp('grid');
		var man = argsgrid.getStore().getAt(me.storeIndex-1).get('aia_mans');
		var manCode = argsgrid.getStore().getAt(me.storeIndex-1).get('aia_mancode');
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
					
					if(Ext.getCmp('aii_statuscode').value!='ENTERING') {
						me.close();
						return;
					}
					for(var key in values) {
						if(key.startsWith('assignName')) {
							count++;
						}
					}
					for(var key in values) {
						if(('' + values[key]).length < 2) {
							showMessage('提示', '级别' + count + '未设置推送人', 1000);
							return;
						}
						if(key.startsWith('assignName')) {
							count--;
							arr1.push(values[key]);
						}else if(key.startsWith('assignCode')) {
							arr2.push(values[key]);
						}
					}
					var names = [], codes = [];
					Ext.Array.each(arr1, function(a, i) {
						a.split(';').map(function(x) {names.push((arr1.length - i) + '$' + x)});
					});
					Ext.Array.each(arr2, function(a, i) {
						a.split(';').map(function(x) {codes.push((arr2.length - i) + '$' + x)});
					});
					
					if(rowStore){
						rowStore.set('aia_mans', names.join(';'));
						rowStore.set('aia_mancode', codes.join(';'));
					}
					me.close();
				}
			}],
			listeners: {
				afterrender: function(win) {
					win.init(man, manCode);
				}
			}
		});
		me.callParent(arguments);
	},
	init: function(man, manCode) {
		var me = this;
		
		if(!man || !manCode) {
			me.insertFieldSet();
			return;
		}
		var o1 = {};
		man.split(';').map(function(v, i) {
			if(v.indexOf('$') == -1) {
				v = '1$'+v;
			}
			var k = v.split('$');
			if(o1.hasOwnProperty(k[0])) {
				o1[k[0]].push(k[1]);
			}else {
				o1[k[0]] = [k[1]]
			}
		});
		
		var o2 = {};
		manCode.split(';').map(function(v, i) {
			if(v.indexOf('$') == -1) {
				v = '1$'+v;
			}
			var k = v.split('$');
			if(o2.hasOwnProperty(k[0])) {
				o2[k[0]].push(k[1]);
			}else {
				o2[k[0]] = [k[1]]
			}
		});
		var level = 1;
		for(var key in o1) {
			if(key+'' == level+'') {
				me.insertFieldSet(o1[key].join(';'), o2[key].join(';'));
			}
			level++;
		}
	},
	insertFieldSet: function(names, codes) {
		var me = this;
		var form = me.down("form");
		var level = form.items.getCount() + 1;
		var fieldSet = {
			xtype: 'fieldset',
			id: 'fieldset'+me.idkey,
			title: '<span style="padding-right: 16px;">'+(level) + '级推送人'+'</span><i onClick="removeManFieldSet(\'fieldset'+me.idkey+'\');" class="x-btn-icon x-button-icon-close datalist-close" style="width: 16px;position: absolute;margin-left: -16px;cursor: pointer" data-qtip="删除">&nbsp;</i>',
			collapsible: true,
			columnWidth: 1,
			padding: 0,
			level: level,
			items: [{
				xtype: 'HrOrgSelectfield',
				height: 80,
				name: 'assignName'+me.idkey,
				id: 'assignName'+me.idkey,
				logic: 'assignCode'+me.idkey,
				secondname: 'assignCode'+me.idkey,
				value: names
			}, {
				xtype: 'textarea',
				name: 'assignCode'+me.idkey,
				id: 'assignCode'+me.idkey,
				value: codes,
				hidden: true
			}]
		};
		form.insert(0, fieldSet);
		me.idkey++;
	},
	/**
	 * 新增删除fieldset之后重新设置fieldset标题名称
	 */
	refreshView: function(clevel) {
		var me = this;
		
		var form = me.down('form');
		var fieldsets = form.items.items;
		for(var i = 0; i < fieldsets.length; i++) {
			var a = fieldsets[i];
			if(a.level > clevel) {
				a.level--;
				a.setTitle('<span style="padding-right: 16px;">'+a.level + '级推送人'+'</span><i onClick="removeManFieldSet(\''+a.id+'\');" class="x-btn-icon x-button-icon-close datalist-close" style="width: 16px;position: absolute;margin-left: -16px;cursor: pointer" data-qtip="删除">&nbsp;</i>');
			}
		}
	}
});
var removeManFieldSet = function(id) {
	var win = Ext.getCmp('assignSelectWin2');
	var form = win.down('form');
	var fieldSet = Ext.getCmp(id);
	form.remove(fieldSet);
	win.refreshView(fieldSet.level);
}