Ext.define('erp.view.pm.bom.Bomlevel',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
						layout: 'anchor',
						items: [{
							xtype: 'erpFormPanel',
							saveUrl: 'pm/bom/saveBomlevel.action',
							deleteUrl: 'pm/bom/deleteBomlevel.action',
							updateUrl: 'pm/bom/updateBomlevel.action',
							getIdUrl: 'common/getId.action?seq=Bomlevel_SEQ',
							auditUrl: 'pm/bom/auditBomlevel.action',
							resAuditUrl: 'pm/bom/resAuditBomlevel.action',
							submitUrl: 'pm/bom/submitBomlevel.action',
							resSubmitUrl: 'pm/bom/resSubmitBomlevel.action',
							keyField: 'bl_id',
							anchor: '100% 50%'
						},{
							xtype:'tabpanel',
							anchor: '100% 50%',
							items:[{
								xtype: 'erpGridPanel2',
								title:'物料等级明细',
								detno: 'pd_detno',
    							keyField: 'pd_id',
    							necessaryField: 'pd_plcode',
    							mainField: 'pd_blid'
							},{
								id: 'billtypegrid',
								title:'订单类型明细',
								xtype: 'billtypegrid',
								keyField: 'bd_id',
								mainField: 'bd_blid',
								necessaryField: 'bd_type',
								detno: 'bd_detno',
								listeners:{
									activate:function(panel){
										panel.ownerCt.items.items[0].down('erpToolbar').down('erpDeleteDetailButton').grid=panel;
									} 
								}
							},{
								id: 'maketypegrid',
								xtype: 'maketypegrid',
								title:'制造单类型明细',
								mainField: 'md_blid',
								detno: 'md_detno',
								necessaryField: 'md_prodtype',
								keyField: 'md_id'
							}]
						}]
		}); 
		me.callParent(arguments); 
	} 
});