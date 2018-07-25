Ext.define('erp.view.hr.wage.base.WageAttend',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				saveUrl: 'hr/wage/attend/save.action',
				deleteUrl: 'hr/wage/attend/delete.action',
				updateUrl: 'hr/wage/attend/update.action',
				auditUrl: 'hr/wage/attend/audit.action',
				resAuditUrl: 'hr/wage/attend/resAudit.action',
				submitUrl: 'hr/wage/attend/submit.action',
				resSubmitUrl: 'hr/wage/attend/resSubmit.action',
				getIdUrl: 'common/getId.action?seq=WAGEATTEND_SEQ',
				keyField: 'wa_id',
				codeField: 'wa_code',
				statusField: 'wa_status',
				statuscodeField: 'wa_statuscode',
				voucherConfig:true
			},
			{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'wa_detno',
//				necessaryField: 'pd_prodcode',
				keyField: 'wad_id',
				mainField: 'wad_waid',
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