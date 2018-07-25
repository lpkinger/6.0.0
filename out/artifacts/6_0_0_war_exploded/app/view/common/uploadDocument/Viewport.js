

Ext.define('erp.view.common.uploadDocument.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 50%',
				saveUrl : 'common/uploadDocument/saveUploadDocument.action',
				deleteUrl : 'common/uploadDocument/deleteUploadDocument.action',
				updateUrl : 'common/uploadDocument/updateUploadDocument.action',
				auditUrl : 'common/uploadDocument/auditUploadDocument.action',
				resAuditUrl : 'common/uploadDocument/resAuditUploadDocument.action',
				submitUrl : 'common/uploadDocument/submitUploadDocument.action',
				resSubmitUrl : 'common/uploadDocument/resSubmitUploadDocument.action',
				getIdUrl : 'common/getId.action?seq=uploadDocument_SEQ',
				keyField : 'ud_id',
				codeField : 'ud_code',
				statusField : 'ud_status',
				statuscodeField : 'ud_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%', 
				detno: 'udd_detno',
				necessaryField: '',
				keyField: 'udd_id',
				mainField: 'udd_udid',
				allowExtraButtons: true,
				/*binds: [{
					refFields:['udd_sourcecode'],
					fields:['udd_prodcode']
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
			    })],
			} ]
		});

		me.callParent(arguments);
	}
});