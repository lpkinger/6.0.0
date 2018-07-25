/* 费用报销转银行登记
 */	
Ext.define('erp.view.core.button.TurnBankDJ',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpTurnBankDJButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpTurnBankDJButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			/*var status = Ext.getCmp('cr_statuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}*/
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('Complaint-win');
		if(!win) {
			/*var result = Ext.getCmp('cr_result'), man = Ext.getCmp('cr_dutyman'), dep = Ext.getCmp('cr_dutydepartment'),cont=Ext.getCmp('cr_content'),
				val1 = result ? result.value : '', val2 =  man ? man.value : '', val3 =   dep ? dep.value : '', val4 =   cont ? cont.value : '';*/
			win = Ext.create('Ext.Window', {
				id: 'Complaint-win',
				title: '转 银行登记',
				height: 300,
				width: 450,
				items: [{
					margin: '3 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '银行账号',
					name:'bank_name',
					value: ''
				}],
				closeAction: 'hide',
				buttonAlign: 'center',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						var form = btn.ownerCt.ownerCt,
							a = form.down('textfield[name=bank_name]');
						if((a.isDirty() && !Ext.isEmpty(a.value))) {
							me.updateComplaint(Ext.getCmp('fc_id').value, a.value);
						}
					}
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	updateComplaint: function(id, val1) {/*
		Ext.Ajax.request({
			url: basePath + 'scm/qc/updateComplaint.action',
			params: {
				id: id,
				val1: val1,
				val2: val2,
				val3: val3,
				val4: val4
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					Ext.Msg.alert("提示","更新成功！");
					window.location.reload();
				}
			}
		});
	*/}
});