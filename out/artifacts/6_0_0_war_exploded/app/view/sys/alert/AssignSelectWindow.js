Ext.define('erp.view.sys.alert.AssignSelectWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.assignselectwin',
	id: 'assignSelectWin',
	title: '选择',
	modal: true,
	width: '50%',
	initComponent : function(){
		var me = this;
		var argsgrid = Ext.getCmp('grid');
		var man = argsgrid.getStore().getAt(me.storeIndex-1).get('aia_mans');
		var manCode = argsgrid.getStore().getAt(me.storeIndex-1).get('aia_mancode');
		Ext.apply(me, {
			items: [{
				xtype: 'form',
				id: 'assignSelectForm',
				layout: 'fit',
				items: [{
					xtype: 'HrOrgSelectfield',
					fieldLabel: '推送人',
					value: man,
					name: 'assignName',
					id: 'assignName',
					logic: 'assignCode',
					secondname: 'assignCode'
				}, {
					xtype: 'textarea',
					fieldLabel: '推送人编码',
					value: manCode,
					name: 'assignCode',
					id: 'assignCode',
					hidden: true
				}]
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'button',
				text: '确定',
				handler: function() {
					var assignSelectWin = Ext.getCmp('assignSelectWin'),
						form = Ext.getCmp('assignSelectForm'),
						formValue = form.getForm().getValues(),
						argsgrid = Ext.getCmp('grid'),
						store = argsgrid.getStore(),
						rowStore = store.getAt(Number(assignSelectWin.storeIndex)-1);
					
					if(Ext.getCmp('aii_statuscode').value!='ENTERING') {
						assignSelectWin.close();
						return;
					}
					if(rowStore){
						rowStore.set('aia_mans', formValue['assignName']);
						rowStore.set('aia_mancode', formValue['assignCode']);
					}
					assignSelectWin.close();
				}
			}]
		});
		me.callParent(arguments);
	}
});