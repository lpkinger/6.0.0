Ext.define('erp.view.common.multiFileUpload.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders: true, 
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				region: 'north',  
				xtype : "erpMultiFileUploadFormPanel",
				getIdUrl: 'common/getId.action?seq=FILEUPLOADTEMP_SEQ',
				FileUploadUrl:'common/uploadMulti.action',
				keyField: 'fl_id'
			}, {
				region: 'south',  
				xtype : "erpMultiFileUploadGridPanel",//选中数据grid
				getGridDataUrl:'common/getGridData.action',
				putGridDataUrl:'common/putGridData.action',
				anchor : '100% 95%'
			}]
		});
		me.callParent(arguments);
	}
});