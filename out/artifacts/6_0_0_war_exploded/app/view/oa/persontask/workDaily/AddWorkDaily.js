Ext.define('erp.view.oa.persontask.workDaily.AddWorkDaily',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'oa/persontask/saveWorkDaily.action',
				deleteUrl: 'oa/persontask/deleteWorkDaily.action',
				updateUrl: 'oa/persontask/updateWorkDaily.action',
				submitUrl: 'oa/persontask/submitWorkDaily.action',
				resSubmitUrl:'oa/persontask/resSubmitWorkDaily.action',
				auditUrl: 'oa/persontask/auditWorkDaily.action',
				resAuditUrl:'oa/persontask/resAuditWorkDaily.action',
				getIdUrl: 'common/getId.action?seq=WORKDAILY_SEQ',
				keyField: 'wd_id',
				statusField:'wd_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				keyField: 'wdd_id',
				mainField: 'wdd_wdid',
				allowExtraButtons: true,
				binds:[{
					refFields:['wdd_workkind'],
					fields:['wdd_hour']
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
											if(r.get(field)!=null && r.get(field)!=0 && r.get(field)!='' && r.get(field)!='0' && r.get(field)=='项目任务'){
												bool=false;
											} 
										});							
									} 
								});
								return bool;
							}
						}
					}

				}),Ext.create('erp.view.core.plugin.CopyPasteMenu')],	
			}]
		});
		me.callParent(arguments); 
	}
});