Ext.define('erp.view.hr.wage.base.WageBase',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				saveUrl: 'hr/wage/base/save.action',
				deleteUrl: 'hr/wage/base/delete.action',
				updateUrl: 'hr/wage/base/update.action',
				auditUrl: 'hr/wage/base/audit.action',
				resAuditUrl: 'hr/wage/base/resAudit.action',
				submitUrl: 'hr/wage/base/submit.action',
				resSubmitUrl: 'hr/wage/base/resSubmit.action',
				getIdUrl: 'common/getId.action?seq=WageBase_SEQ',
				keyField: 'wb_id',
				codeField: 'wb_code',
				statusField: 'wb_status',
				statuscodeField: 'wb_statuscode',
				voucherConfig:true
			},
			{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'wbd_detno',
//				necessaryField: 'pd_prodcode',
				keyField: 'wbd_id',
				mainField: 'wbd_wbid',
				allowExtraButtons: true,
//				binds: [{
//					refFields:['pd_sourcecode'],
//					fields:['pd_prodcode']
//				}],
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
			    , Ext.create('erp.view.scm.purchase.plugin.Reply')]
			}]
		}); 
		me.callParent(arguments); 
	} 
});