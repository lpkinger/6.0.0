Ext.define('erp.view.sys.fa.FATabPanel',{
	extend: 'Ext.tab.Panel', 
	alias: 'widget.fatabpanel',
	id:'fatabpanel',
	animCollapse: false,
	bodyBorder: false,
	border: false,
	autoShow: true, 
	tabPosition:'bottom',
	frame:true,
	dockedItems: [Ext.create('erp.view.sys.base.Toolbar')],
	defaults:{
	    plugins: [{
	        ptype: 'cellediting',
	        clicksToEdit: 2,
	        pluginId: 'cellplugin'
	    }]
	},
	items: [{
		title: '其它出入库客科目',
		xtype:'simpleactiongrid',
		caller:'ProdIOCateSet',
		saveUrl: 'co/cost/saveProdIOCateSet.action',
		deleteUrl: 'co/cost/deleteProdIOCateSet.action',
		updateUrl: 'co/cost/updateProdIOCateSet.action',
		getIdUrl: 'common/getId.action?seq=PRODIOCATESET_SEQ',
		keyField: 'pc_id',
		codeField: 'pc_code',
		params:{
			caller:'ProdIOCateSet!Grid',
			condition:'1=1'
		}
	 },{
		title: '币别',
		xtype:'simpleactiongrid',
		caller:'Currencys',
		saveUrl: 'fa/ars/saveCurrencys.action',
		deleteUrl: 'fa/ars/deleteCurrencys.action',
		updateUrl: 'fa/ars/updateCurrencys.action',
		getIdUrl: 'common/getId.action?seq=CURRENCYS_SEQ',
		keyField:'cr_id',
		codeField:'cr_code',
		params:{
			caller:'Currencys!Grid',
			condition:'1=1'
		}
	},{
		title: '月度汇率',
		xtype:'simpleactiongrid',
		caller:'CurrencysMonth',
		keyField:'cm_id',
		deleteUrl: 'fa/fix/CurrencysController/deleteCurrencysMonth.action',
		keyField: 'cm_id',
		params:{
			caller:'CurrencysMonth!Grid',
			condition:'1=1'
		}
	},{
		title:'固定资产类型',
		xtype:'simpleactiongrid',
		caller:'AssetsKind',
		saveUrl: 'fa/fix/saveAssetsKind.action',
		deleteUrl: 'fa/fix/deleteAssetsKind.action',
		updateUrl: 'fa/fix/updateAssetsKind.action',
		getIdUrl: 'common/getId.action?seq=ASSETSKIND_SEQ',
		keyField : 'ak_id',
		params:{
			caller:'AssetsKind!Grid',
			condition:'1=1'
		}
	},{
		title:'初始化期间设置',
		xtype:'simpleactiongrid',
		caller:'Periods',
		updateUrl: 'common/updateCommon.action',
		getIdUrl: 'common/getId.action?seq=PERIODS_SEQ',
		keyField : 'pe_id',
		params:{
			caller:'Periods!Grid',
			condition:'1=1'
		}
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});