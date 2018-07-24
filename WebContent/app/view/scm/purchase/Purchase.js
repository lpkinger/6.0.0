Ext.define('erp.view.scm.purchase.Purchase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/purchase/savePurchase.action',
				deleteUrl: 'scm/purchase/deletePurchase.action',
				updateUrl: 'scm/purchase/updatePurchase.action',
				auditUrl: 'scm/purchase/auditPurchase.action',
				resAuditUrl: 'scm/purchase/resAuditPurchase.action',
				printUrl: 'scm/purchase/printPurchase.action',
				b2bUrl: 'scm/purchase/b2bPurchase.action',
				submitUrl: 'scm/purchase/submitPurchase.action',
				resSubmitUrl: 'scm/purchase/resSubmitPurchase.action',
				getIdUrl: 'common/getId.action?seq=PURCHASE_SEQ',
				keyField: 'pu_id',
				codeField: 'pu_code',
				statusField: 'pu_status',
				statuscodeField: 'pu_statuscode',
				voucherConfig:true
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'pd_detno',
				necessaryField: 'pd_prodcode',
				keyField: 'pd_id',
				mainField: 'pd_puid',
				allowExtraButtons: true,
				binds: [{
					refFields:['pd_sourcecode'],
					fields:['pd_prodcode']
				}],
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
			        clicksToEdit: 1,
			        listeners:{
						beforeedit:function(e){
							var g=e.grid,r=e.record,f=e.field;
							if(g.binds){
								var bool=true;
								Ext.Array.each(g.binds,function(item){
									if(Ext.Array.contains(item.fields,f)){
										Ext.each(item.refFields,function(field){
											if(r.get(field)!=null && r.get(field)!=0 && r.get(field)!='' && r.get(field)!='0'){
												bool=false;
											} 
										});							
									} 
								});
								return bool;
							}
						}
					}
			    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')
			    , Ext.create('erp.view.scm.purchase.plugin.Reply')],
			}]
		}); 
		me.callParent(arguments); 
	} 
});