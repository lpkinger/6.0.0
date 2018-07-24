Ext.define('erp.view.fa.VoucherStyle', { 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				anchor: '100% 40%',
				xtype: 'erpFormPanel',
				saveUrl: 'fa/vc/saveVoucherStyle.action?caller=' + caller,
				updateUrl: 'fa/vc/updateVoucherStyle.action?caller=' + caller,
				keyField:'vs_id',
				getIdUrl: 'common/getId.action?seq=VOUCHERSTYLE_SEQ',
				dumpable: true,
				identity: function() {
					var form = this, grid = form.up('viewport').down('grid'), 
						vs_id = form.down('#vs_id').getValue(),
						firstRow = grid.getStore().first(), vd_class = (firstRow ? firstRow.get('vd_class') : null);
					if (vd_class) {
						return Ext.JSON.encode({vs_id: vs_id, vd_class: vd_class});
					}
				}
			},{
				anchor: '100% 60%',
				xtype: 'erpGridPanel2',
				_buttons: [{
					text: '辅助核算',
					width: 85,
					disabled: true,
			    	cls: 'x-btn-gray',
			    	id: 'assdetail'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});