Ext.define('erp.view.sys.mo.MoTabPanel',{
	extend: 'Ext.tab.Panel', 
	alias: 'widget.motabpanel',
	id:'motabpanel',
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
		title:'工单类型',
		xtype:'simpleactiongrid',
		caller:'MakeKind',
		saveUrl: 'pm/make/saveMakeKind.action',
		deleteUrl: 'pm/make/deleteMakeKind.action',
		updateUrl: 'pm/make/updateMakeKind.action',
		getIdUrl: 'common/getId.action?seq=makekind_SEQ',
		keyField: 'mk_id',
		autoRender:true,
		params:{
			caller:'MakeKind!Grid',
			condition:'1=1'
		}
	},{
		title: '工作中心',
		xtype:'simpleactiongrid',
		caller:'WorkCenter',
		saveUrl: 'scm/sale/saveWorkCenter.action',
		deleteUrl: 'scm/sale/deleteWorkCenter.action',
		updateUrl: 'scm/sale/updateWorkCenter.action',
		getIdUrl: 'common/getId.action?seq=WORKCENTER_SEQ',
		keyField:'wc_id',
		codeField:'wc_code',
		autoRender:true,
		params:{
			caller:'WorkCenter!Grid',
			condition:'1=1'
		}
	},{
		title: '线别',
		xtype:'simpleactiongrid',
		caller:'TeamCode',
		autoRender:true,
		saveUrl: 'common/saveCommon.action?caller=TeamCode',
		deleteUrl: 'common/deleteCommon.action?caller=TeamCode',
		updateUrl: 'common/updateCommon.action?caller=TeamCode',
		getIdUrl: 'common/getId.action?seq=TeamCode_SEQ',
		keyField: 'CT_ID',
		params:{
			caller:'TeamCode!Grid',
			condition:"1=1"
		}
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});