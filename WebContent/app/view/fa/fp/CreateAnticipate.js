Ext.define('erp.view.fa.fp.CreateAnticipate',{ 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){
		var me = this;
		Ext.apply(me, {
			items: [ this.createForm() ]
		}); 
		me.callParent(arguments); 
	},
	createForm: function() {
		this.form = Ext.create('Ext.form.Panel', {
			title: '逾期数据生成',
			height: 250,
			width: 550,
			layout: 'vbox',
			bodyStyle: 'background: #f1f1f1;border-left:1px solid #bdbdbd;border-right:1px solid #bdbdbd;',
			cls: 'singleWindowForm',
			bodyCls: 'singleWindowForm',
			defaults: {
				width: 400,
				margin: '5 0 0 30'
			},
			items: [{
				xtype: 'datefield',
				fieldLabel: '截至日期',
				name: 'date',
				id: 'date',
				format:'Y-m-d',
				value: Ext.Date.format(new Date(), 'Y-m-d')
			},{
				fieldLabel: '客户',
				labelWidth: 100,
				layout: 'column',
				height: 33,
				xtype: 'fieldcontainer',
				items: [{
					xtype: 'dbfindtrigger',
					name: 'cu_code',
					id: 'cu_code',
					columnWidth: 0.4,
					listeners: {
						aftertrigger: function(f, d) {
							f.setValue(d.data.cu_code);
							f.ownerCt.down('textfield[name=cu_name]').setValue(d.data.cu_name);
						}
					}
				},{
					xtype: 'textfield',
					name: 'cu_name',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}]
			},{
				fieldLabel: '业务员',
				labelWidth: 100,
				layout: 'column',
				height: 33,
				xtype: 'fieldcontainer',
				items: [{
					xtype: 'dbfindtrigger',
					name: 'em_code',
					id: 'em_code',
					columnWidth: 0.4,
					listeners: {
						aftertrigger: function(f, d) {
							f.setValue(d.data.em_code);
							f.ownerCt.down('textfield[name=em_name]').setValue(d.data.em_name);
						}
					}
				},{
					xtype: 'textfield',
					name: 'em_name',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}]
			},{
				fieldLabel: '部门',
				labelWidth: 100,
				layout: 'column',
				height: 33,
				xtype: 'fieldcontainer',
				items: [{
					xtype: 'dbfindtrigger',
					name: 'dp_code',
					id: 'dp_code',
					columnWidth: 0.4,
					listeners: {
						aftertrigger: function(f, d) {
							f.setValue(d.data.dp_code);
							f.ownerCt.down('textfield[name=dp_name]').setValue(d.data.dp_name);
						}
					}
				},{
					xtype: 'textfield',
					name: 'dp_name',
					columnWidth: 0.6,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}]
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