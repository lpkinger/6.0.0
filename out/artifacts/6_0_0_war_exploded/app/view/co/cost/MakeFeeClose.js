Ext.define('erp.view.co.cost.MakeFeeClose',{ 
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
			title: '制造费用结转凭证制作',
			height: 400,
			width: 560,
			layout: 'vbox',
			cls: 'singleWindowForm',
			bodyCls: 'singleWindowForm',
			bodyStyle: 'background: #f1f1f1;border-left:1px solid #bdbdbd;border-right:1px solid #bdbdbd;',
			defaults: {
				width: 360,
				margin: '5 0 0 30'
			},
			items: [{
				xtype: 'displayfield',
				fieldLabel: '期间',
				name: 'yearmonth'
			},{
				fieldLabel: '制造费用科目',
				labelWidth: 100,
				layout: 'column',
				height: 36,
				xtype: 'fieldcontainer',
				items: [{
					xtype: 'dbfindtrigger',
					name: 'ca_code',
					id: 'makeCatecode',
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
				fieldLabel: '生产成本科目',
				labelWidth: 100,
				layout: 'column',
				height: 36,
				xtype: 'fieldcontainer',
				items: [{
					xtype: 'dbfindtrigger',
					name: 'ca_code',
					id: 'makeToCatecode',
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
				fieldLabel: '材料成本差异科目',
				labelWidth: 100,
				layout: 'column',
				height: 36,
				xtype: 'fieldcontainer',
				items: [{
					xtype: 'dbfindtrigger',
					name: 'ca_code',
					id: 'materialsCatecode',
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
				fieldLabel: '直接人工科目',
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
					id: 'manMakeCatecode',
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
				boxLabel: '将生产成本结转产生的凭证立即登账'
			},{
				xtype: 'checkbox',
				id: 'account2',
				name: 'account2',
				boxLabel: '材料成本差异余额结转到生产成本-制造费用'
			},{
				xtype: 'checkbox',
				id: 'account3',
				name: 'account3',
				boxLabel: '生产成本-直接人工余额结转'
			}],
			buttonAlign: 'center',
			buttons: [{
				xtype: 'erpMakeFeeCloseButton'
			},{
				xtype:'erpCloseButton'
			}]
		});
		return this.form;
	}
});