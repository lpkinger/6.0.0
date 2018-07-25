Ext.define('erp.view.co.cost.EngineeringFeeClose',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	},
	style: 'background: #f1f2f5;',
	initComponent : function(){
		var me = this;
		Ext.apply(me, {
			items: [ this.createForm() ]
		}); 
		me.callParent(arguments); 
	},
	createForm: function() {
		this.form = Ext.create('Ext.form.Panel', {
			title: '生产成本-工程成本结转凭证制作',
			height: 300,
			width: 580,
			layout: 'vbox',
			bodyStyle: 'background: #f1f1f1;',
			defaults: {
				width: 360,
				margin: '5 0 0 30'
			},
			items: [{
				xtype: 'displayfield',
				fieldLabel: '期间',
				name: 'yearmonth'
			},{
				fieldLabel: '生产成本-工程成本科目',
				labelWidth: 100,
				layout: 'column',
				height: 36,
				xtype: 'fieldcontainer',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					xtype: 'dbfindtrigger',
					name: 'ca_code',
					id: 'enCatecode',
					columnWidth: 0.4,
					listeners: {
						aftertrigger: function(f, d) {
							f.setValue(d.data.ca_code);
							f.ownerCt.down('textfield[name=ca_name]').setValue(d.data.ca_name);
						}
					}
				},{
					xtype: 'textfield',
					name: 'ca_name',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}]
			},{
				fieldLabel: '发出商品-工程科目',
				labelWidth: 100,
				layout: 'column',
				height: 36,
				xtype: 'fieldcontainer',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					xtype: 'dbfindtrigger',
					name: 'ca_code',
					id: 'gsCatecode',
					columnWidth: 0.4,
					listeners: {
						aftertrigger: function(f, d) {
							f.setValue(d.data.ca_code);
							f.ownerCt.down('textfield[name=ca_name]').setValue(d.data.ca_name);
						}
					}
				},{
					xtype: 'textfield',
					name: 'ca_name',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}]
			},{
				xtype: 'checkbox',
				id: 'account',
				name: 'account',
				boxLabel: '将生产成本-工程成本结转的凭证立即登账'
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'erpConfirmButton'
			},{
				xtype:'erpCloseButton'
			}]
		});
		return this.form;
	}
});