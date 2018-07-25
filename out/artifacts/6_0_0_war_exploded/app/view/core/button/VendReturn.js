/**
 * 更新供应商返还
 */	
Ext.define('erp.view.core.button.VendReturn',{
		extend: 'Ext.Button', 
		alias: 'widget.erpVendReturnButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '供应商返还',
    	id: 'erpVendReturnButton',
    	text: $I18N.common.button.erpVendReturnButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		listeners: {
			afterrender: function(btn) {
				var me = this;
				var status = Ext.getCmp('ps_statuscode');
				if(status && (status.value != 'AUDITED')){
					btn.hide();
				}
			}
		},
		handler: function() {
			var me = this, win = Ext.getCmp('Complaint-win');
			var states = Ext.create('Ext.data.Store', {
			    fields: ['abbr', 'name'],
			    data : [
			        {"abbr":"未返还", "name":"未返还"},
			        {"abbr":"部分返还", "name":"部分返还"},
			        {"abbr":"已返还", "name":"已返还"}
		    	]
			});
			if(!win) {
				var cs = Ext.getCmp('ps_vendreturnstatus'), cr = Ext.getCmp('ps_vendreturnremark'),
					val1 = cs ? cs.value : '', val2 =  cr ? cr.value : '';
				win = Ext.create('Ext.Window', {
					id: 'Complaint-win',
					title: '更新客户返还状态',
					height: 200,
					width: 400,
					items: [{
						xtype: 'form',
						height: '100%',
						width: '100%',
						bodyStyle: 'background:#f1f2f5;',
						items: [{
							margin: '10 0 0 0',
							xtype: 'combo',
							fieldLabel: '返还状态',
							name:'ps_vendreturnstatus',
							allowBlank: false,
							store: states,
							queryMode: 'local',
					    	displayField: 'name',
					    	valueField: 'abbr',
							value: val1
						},{
							margin: '3 0 0 0',
							xtype: 'textareatrigger',
							name:'ps_vendreturnremark',
							fieldLabel: '供应商返还备注',
							value: val2
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
									cs = form.down('textfield[name=ps_vendreturnstatus]'),
									cr = form.down('textfield[name=ps_vendreturnremark]')
								if(form.getForm().isDirty()) {
									me.updatevendReturn(Ext.getCmp('ps_id').value, cs.value, cr.value);
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
		updatevendReturn: function(id, cs, cr) {
			Ext.Ajax.request({
				url: basePath + 'pm/mould/updateVendReturn.action',
				params: {
					id: id,
					returnstatus: cs,
					returnremark: cr
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