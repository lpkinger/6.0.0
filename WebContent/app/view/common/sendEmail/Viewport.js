

Ext.define('erp.view.common.sendEmail.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'common/sendEmail/saveSendEmail.action',
				deleteUrl : 'common/sendEmail/deleteSendEmail.action',
				updateUrl : 'common/sendEmail/updateSendEmail.action',
				auditUrl : 'common/sendEmail/auditSendEmail.action',
				resAuditUrl : 'common/sendEmail/resAuditSendEmail.action',
				submitUrl : 'common/sendEmail/submitSendEmail.action',
				resSubmitUrl : 'common/sendEmail/resSubmitSendEmail.action',
				getIdUrl : 'common/getId.action?seq=sendEmail_SEQ',
				keyField : 'se_id',
				codeField : 'se_code',
				statusField : 'se_status',
				statuscodeField : 'se_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'sed_detno',
				necessaryField: '',
				keyField: 'sed_id',
				mainField: 'sed_secode',
				allowExtraButtons: true,
				/*binds: [{
					refFields:['sed_sourcecode'],
					fields:['sed_prodcode']
				}]*/
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
			    }), 
			    Ext.create('erp.view.core.plugin.CopyPasteMenu')],
			} ]
		});

		me.callParent(arguments);
	}
});