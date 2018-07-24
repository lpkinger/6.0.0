/**
 * 更新供应商返还
 */	
Ext.define('erp.view.core.button.UpdateChargeStatus',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateChargeStatusButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '更新收款状态',
    	id: 'erpUpdateChargeStatusButton',
    	text: $I18N.common.button.erpUpdateChargeStatusButton,
    	width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn) {
				var me = this;
				var status = Ext.getCmp('msa_statuscode');
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
			        {"abbr":"未收款", "name":"未收款"},
			        {"abbr":"部分收款", "name":"部分收款"},
			        {"abbr":"已收款", "name":"已收款"}
		    	]
			});
			if(!win) {
				var cs = Ext.getCmp('msa_chargestatus'), cr = Ext.getCmp('msa_chargeremark'),
					val1 = cs ? cs.value : '', val2 =  cr ? cr.value : '';
				win = Ext.create('Ext.Window', {
					id: 'Complaint-win',
					title: '更新收款状态',
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
							fieldLabel: '收款状态',
							name:'msa_chargestatus',
							allowBlank: false,
							store: states,
							queryMode: 'local',
					    	displayField: 'name',
					    	valueField: 'abbr',
							value: val1
						},{
							margin: '3 0 0 0',
							xtype: 'textareatrigger',
							name:'msa_chargeremark',
							fieldLabel: '收款备注',
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
									cs = form.down('textfield[name=msa_chargestatus]'),
									cr = form.down('textfield[name=msa_chargeremark]')
								if(form.getForm().isDirty()) {
									me.updatevendReturn(Ext.getCmp('msa_id').value, cs.value, cr.value);
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
				url: basePath + 'pm/mould/mouldsale/updatechargestatus.action',
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