Ext.define('erp.view.fa.fundPlan.FundPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%',
				//与其它页面不同，必须传一个caller
				saveUrl: 'fa/fundPlan/save.action',
				deleteUrl: 'fa/fundPlan/delete.action',
				updateUrl: 'fa/fundPlan/update.action',
				getIdUrl: 'common/getId.action?seq=FundPlan_SEQ',
				keyField: 'fp_id'
	    	},{
				xtype: 'erpGridPanel2',
				anchor: '100% 80%', 
				detno: 'fpd_detno',
//				necessaryField: 'pd_prodcode',
				keyField: 'fpd_id',
				mainField: 'fpd_fpid',
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
			    ]
			}]
		}); 
		me.callParent(arguments); 
	}  
});