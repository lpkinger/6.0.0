Ext.define('erp.view.sys.pu.PuTabPanel',{
	extend: 'Ext.tab.Panel', 
	alias: 'widget.putabpanel', 
	animCollapse: false,
	bodyBorder: false,
	border: false,
	autoShow: true, 
	//requires:['erp.view.sys.base.SimpleActionGrid'],
	tabPosition:'bottom',
	frame:true,
	defaults:{
	    plugins: [{
	        ptype: 'cellediting',
	        clicksToEdit: 2,
	        pluginId: 'cellplugin'
	    }]
	},
	dockedItems: [Ext.create('erp.view.sys.base.Toolbar')],
	items: [{
		title: '采购类型',
		xtype:'simpleactiongrid',
		caller:'PurchaseKind',
		saveUrl: 'common/saveCommon.action?caller=PurchaseKind',
		deleteUrl: 'common/deleteCommon.action?caller=PurchaseKind',
		updateUrl: 'common/updateCommon.action?caller=PurchaseKind',
		getIdUrl: 'common/getCommonId.action?caller=PurchaseKind',
		keyField:'pk_id',
		params:{
			caller:'PurchaseKind!Grid',
			condition:'1=1'
		}
	},{
		title: '付款方式',
		xtype:'simpleactiongrid',
		caller:'Payments!Purchase',
		saveUrl: 'scm/purchase/savePayments.action',
		deleteUrl: 'scm/purchase/deletePayments.action',
		updateUrl: 'scm/purchase/updatePayments.action',
		getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
		keyField: 'pa_id',
		codeField: 'pa_code',
		statusField: 'pa_auditstatuscode',
		params:{
			caller:'Payments!Purchase!Grid',
			condition:"pa_class='付款方式'"
		}
	},{
		title:'其它采购入库类型',
		xtype:'simpleactiongrid',
		caller:'BorrowCargoType',
		saveUrl: 'common/saveCommon.action?caller=BorrowCargoType',
		deleteUrl: 'common/deleteCommon.action?caller=BorrowCargoType',
		updateUrl: 'common/updateCommon.action?caller=BorrowCargoType',
		getIdUrl: 'common/getCommonId.action?caller=BorrowCargoType',
		keyField:'bt_id',
		codeField:'bt_code',
		defaultValues:[{
			bt_piclass:'其它采购入库单'
		}],
		params:{
			caller:'BorrowCargoType!Grid',
			condition:"bt_piclass='其它采购入库单'"
		}
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});