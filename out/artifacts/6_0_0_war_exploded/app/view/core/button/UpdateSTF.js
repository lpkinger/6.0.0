/**
 * 更新信息
 */	
Ext.define('erp.view.core.button.UpdateSTF',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateSTFButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '移模',
    	id: 'erpUpdateSTFButton',
    	text: $I18N.common.button.erpUpdateSTFButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 70,
		listeners: {
			afterrender: function(btn) {
				var me = this;
				var status = Ext.getCmp('ws_statuscode');
				if(status && status.value == 'ENTERING'){
					btn.hide();
				}
			}
		},
		handler: function() {
			var me = this, win = Ext.getCmp('Complaint-win');
			if(!win) {
				var vc = Ext.getCmp('ws_stf'),val1 = vc ? vc.value : '';
				win = Ext.create('Ext.Window', {
					id: 'Complaint-win',
					title: '更新保管书 ' + Ext.getCmp('ws_code').value + ' 的委托方',
					height: 200,
					width: 400,
					items: [{
						xtype: 'form',
						height: '100%',
						width: '100%',
						bodyStyle: 'background:#f1f2f5;',
						items: [{
							margin: '10 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '受托方',
							name:'ws_stf',
							allowBlank: false,
							value: val1
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
									a = form.down('dbfindtrigger[name=ws_stf]');
								if(form.getForm().isDirty()) {
									me.updatestf(Ext.getCmp('ws_id').value, a.value);
								}
							}
						}, {
							text: $I18N.common.button.erpCloseButton,
							cls: 'x-btn-blue',
							handler: function(btn) {
								btn.up('window').hide();
							}
						}]
					}]
				});
			}
			win.show();
		},
		updatestf: function(id, val1) {
			Ext.Ajax.request({
				url: basePath + 'pm/mould/updatestf.action',
				params: {
					id: id,
					vend: val1
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
		}
	});