Ext.define('erp.view.fa.ars.ChangeSellerForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.ChangeSeller',
	id: 'form', 
	title: '业务员转移',
    frame : true,
	autoScroll : true,
	buttonAlign : 'center',
	FormUtil: Ext.create('erp.util.FormUtil'),
	confirmUrl:'',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},       
	initComponent : function(){ 
		this.callParent(arguments);
		/*this.title = this.FormUtil.getActiveTab().title;*/
	},
	items: [{		
		xtype: 'fieldcontainer',
		fieldLabel: '从业务员',
		labelWidth: 80,
		height: 23,
		layout: 'hbox',
		columnWidth: 0.5,
		id: 'sellercode1',
		items: [{
			labelWidth: 35,
			xtype: 'dbfindtrigger',
			flex: 0.32,
			id: 'sa_sellercode',
			name: 'sa_sellercode'
		},{
			xtype: 'textfield',
			id: 'sa_seller',
			name: 'sa_seller',
			flex:0.32,
			readOnly: true,
			fieldStyle: 'background:#f1f1f1;'
		}],
		getValue: function() {
			var a = Ext.getCmp('sa_sellercode');
			if(!Ext.isEmpty(a.value)) {
				return a.value;
			}
			return null;
		}
	},{		
		xtype: 'fieldcontainer',
		fieldLabel: '到业务员',
		labelWidth: 80,
		height: 23,
		layout: 'hbox',
		columnWidth: 0.5,
		id: 'sellercode2',
		defaults: {
			fieldStyle : "background:#FFFAFA;color:#515151;"
		},
		items: [{
			labelWidth: 35,
			xtype: 'dbfindtrigger',
			flex: 0.32,
			id: 'sn_sellercode',
			name: 'sn_sellercode'
		},{
			xtype: 'textfield',
			id: 'sn_sellername',
			name: 'sn_sellername',
			flex:0.32,
			readOnly: true,
			fieldStyle: 'background:#f1f1f1;'
		}],
		getValue: function() {
			var a = Ext.getCmp('sn_sellercode');
			if(!Ext.isEmpty(a.value)) {
				return a.value;
			}
			return null;
		}
	},/*{
		xtype: 'checkbox',
		id: 'customer',
		checked: true,
		boxLabel: '客户资料'
	},*/{
		xtype: 'checkbox',
		id: 'sale',
		checked: true,
		boxLabel: '销售订单'
	},{
		xtype: 'checkbox',
		id: 'sendnotify',
		checked: true,
		boxLabel: '出货通知单'
	},{
		xtype: 'checkbox',
		id: 'prodio',
		checked: true,
		boxLabel: '出货单/销售退货单'
	},{
		xtype: 'checkbox',
		id: 'arbill',
		checked: true,
		boxLabel: '应收发票'
	},{
		xtype: 'checkbox',
		id: 'sellermonth',
		boxLabel: '当月SellerMonth'
	}],
	buttons: [{
		xtype: 'erpConfirmButton'
	},{
		xtype:'erpCloseButton'
	}]
});