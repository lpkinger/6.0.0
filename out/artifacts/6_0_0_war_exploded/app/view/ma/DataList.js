Ext.define('erp.view.ma.DataList', {
	extend : 'Ext.Viewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype : 'erpFormPanel',
				anchor : '100% 40%',
				saveUrl : 'ma/saveDataList.action',
				deleteUrl : 'ma/deleteDataList.action',
				updateUrl : 'ma/updateDataList.action',
				getIdUrl : 'common/getId.action?seq=DATALIST_SEQ',
				keyField : 'dl_id',
				dumpable : true
			}, {
				xtype : 'mydatalist',
				anchor : '100% 60%',
				detno : 'dld_detno',
				necessaryField : 'dld_field',
				keyField : 'dld_id',
				mainField : 'dld_dlid'
			} ]
		});
		me.callParent(arguments);
	}
});