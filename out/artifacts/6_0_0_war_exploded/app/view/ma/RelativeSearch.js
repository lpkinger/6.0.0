Ext.define('erp.view.ma.RelativeSearch', {
	extend : 'Ext.Viewport',
	hideBorders : true,
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			layout : 'anchor',
			items : [ {
				xtype : 'erpFormPanel',
				saveUrl : 'ma/saveRelativeSearch.action',
				updateUrl : 'ma/updateRelativeSearch.action',
				getIdUrl : 'common/getId.action?seq=RelativeSearch_SEQ',
				keyField : 'rs_id',
				anchor : '100% 30%',
				dumpable : true
			}, {
				xtype : 'tabpanel',
				anchor : '100% 70%',
				items : [ {
					xtype : 'erpGridPanel2',
					title : '查询Form显示',
					detno : 'rsf_detno',
					keyField : 'rsf_id',
					necessaryField : 'rsf_field',
					mainField : 'rsf_rsid'
				}, {
					id : 'relativesearchgrid',
					title : '查询Grid显示',
					xtype : 'relativesearchgrid',
					keyField : 'rsg_id',
					mainField : 'rsg_rsid',
					necessaryField : 'rsg_field',
					detno : 'rsg_detno',
					listeners : {
						activate : function(panel) {
							panel.ownerCt.items.items[0].down('erpToolbar').down('erpDeleteDetailButton').grid = panel;
						}
					}
				} ]
			} ]
		});
		me.callParent(arguments);
	}
});