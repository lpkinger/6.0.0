Ext.define('erp.view.oa.fee.feePlease2',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 38%',
				saveUrl: 'oa/fee/saveFeePlease.action',
				deleteUrl: 'oa/fee/deleteFeePlease.action',
				updateUrl: 'oa/fee/updateFeePlease.action',
				auditUrl: 'oa/fee/auditFeePlease.action',
				printUrl: 'oa/fee/printFeePlease.action',
				resAuditUrl: 'oa/fee/resAuditFeePlease.action',
				submitUrl: 'oa/fee/submitFeePlease.action',
				resSubmitUrl: 'oa/fee/resSubmitFeePlease.action',
				endUrl: 'oa/fee/endFeePlease.action',
				resEndUrl: 'oa/fee/resEndFeePlease.action',
				getIdUrl: 'common/getId.action?seq=FEEPLEASE_SEQ',
				keyField: 'fp_id',
				codeField: 'fp_code',
				statusField: 'fp_status',
				statuscodeField: 'fp_statuscode',
				listeners : {
					afterrender : function(form) {
						Ext.override(Ext.getClass(this), {
							getValues : function(){
								if(this.id != form.id) {
									return this.getForm().getValues();
								}
								var r = this.getForm().getValues(), 
									c = this.ownerCt.down('container[itemId=item2]'),
									f = c.down('form');
								if (f) {
									r = Ext.Object.merge(r, f.getValues());
								}
								return r;
							}
						});
					}
				}
			},{
				xtype : 'container',
				itemId : 'item2',
				anchor : '100% 62%',
				layout: 'anchor'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});