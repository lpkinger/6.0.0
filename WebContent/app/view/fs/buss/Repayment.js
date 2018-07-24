Ext.define('erp.view.fs.buss.Repayment',{ 
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
			title: '手工还款',
			height: 460,
			width: 550,
			layout: 'vbox',
			bodyStyle: 'background: #f1f1f1;',
			defaults: {
				width: 400,
				margin: '5 0 0 30'
			},
			items: [{
				fieldLabel: '客户编号',
				labelWidth: 100,
				layout: 'column',
				xtype: 'fieldcontainer',
				defaults: {
					fieldStyle : "background:#FFFAFA;color:#515151;"
				},
				items: [{
					xtype: 'dbfindtrigger',
					name: 're_custcode',
					id: 're_custcode',
					columnWidth: 0.35,
					listeners: {
						aftertrigger: function(f, d) {
							f.setValue(d.data.cu_code);
							f.ownerCt.down('textfield[name=re_custname]').setValue(d.data.cu_name);
						}
					}
				},{
					xtype: 'textfield',
					name: 're_custname',
					columnWidth: 0.65,
					readOnly: true,
					fieldStyle: 'background:#f1f1f1;'
				}]
			},{
				xtype: 'dbfindtrigger',
				fieldLabel: '借据编号',
				labelStyle : 'color:#FF0000',
				name: 're_aacode',
				id: 're_aacode',
				columnWidth: 1,
				listeners: {
					aftertrigger: function(f, d) {
						f.setValue(d.data.aa_code);
						f.ownerCt.down('textfield[name=re_principal]').setValue(d.data.aa_leftamount);
						f.ownerCt.down('textfield[name=re_interest]').setValue(d.data.aa_interest);
						f.ownerCt.down('textfield[name=re_odamount]').setValue(d.data.aa_overamount);
						f.ownerCt.down('textfield[name=re_odinterest]').setValue(d.data.aa_overinterest);
						f.ownerCt.down('textfield[name=re_yftotal]').setValue(d.data.aa_yftotal);
						f.ownerCt.down('textfield[name=re_yqtotal]').setValue(d.data.aa_yqtotal);
						f.ownerCt.down('textfield[name=re_total]').setValue(d.data.aa_total);
						f.ownerCt.down('textfield[name=re_kind]').setValue(d.data.aa_kind);
					}
				}
			},{
				xtype: 'textfield',
				fieldLabel: '应付本金',
				name: 're_principal',
				id: 're_principal',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
				xtype: 'textfield',
				fieldLabel: '应付利息',
				name: 're_interest',
				id: 're_interest',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
				xtype: 'textfield',
				fieldLabel: '正常应付小计',
				labelStyle : 'color:#7A67EE',
				name: 're_yftotal',
				id: 're_yftotal',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
				xtype: 'textfield',
				fieldLabel: '逾期本金',
				name: 're_odamount',
				id: 're_odamount',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
				xtype: 'textfield',
				fieldLabel: '逾期利息',
				name: 're_odinterest',
				id: 're_odinterest',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
				xtype: 'textfield',
				fieldLabel: '逾期应付小计',
				labelStyle : 'color:#7A67EE',
				name: 're_yqtotal',
				id: 're_yqtotal',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
				xtype: 'textfield',
				fieldLabel: '应还金额合计',
				labelStyle : 'color:#7A67EE',
				name: 're_total',
				id: 're_total',
				columnWidth: 0.5,
				readOnly: true,
				fieldStyle: 'background:#f1f1f1;'
			},{
    		    fieldLabel:'退还客户金额',
    		    xtype:'separnumberfield',
    		    labelStyle : 'color:#FF0000',
    		    columnWidth: 1,
    		    readOnly: false,
    		    id:'re_backcustamount'
    		},{
    		    fieldLabel:'本次还款金额',
    		    xtype:'separnumberfield',
    		    labelStyle : 'color:#FF0000',
    		    columnWidth: 1,
    		    readOnly: false,
    		    id:'re_thisamount'
    		},{
    		    fieldLabel:'申请还款日期',
    		    xtype:'datefield',
    		    labelStyle : 'color:#FF0000',
    		    columnWidth: 1,
    		    readOnly: false,
    		    id:'re_backdate'
    		},{
    		    fieldLabel:'单据类型',
    		    xtype: 'textfield',
    		    columnWidth: 1,
    		    readOnly: true,
    		    hidden:true,
    		    id:'re_kind',
    		    name: 're_kind',
				fieldStyle: 'background:#f1f1f1;'
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