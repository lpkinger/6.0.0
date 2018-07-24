Ext.define('erp.view.scm.reserve.BarOrPackReport',{ 
	extend: 'Ext.Viewport',
	layout: 'fit',
	hideBorders: true,
	initComponent : function(){
		var me = this;
		Ext.apply(me, {
			items: [{
				layout: 'anchor',
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					deleteUrl: 'scm/reserve/deleteReportFile.action',
					updateUrl: 'scm/reserve/updateReportFile.action',
					getIdUrl: 'common/getId.action?seq=ReportFiles_SEQ',
					keyField: 'fo_caller',
					formCondition: getUrlParam('formCondition')?"fo_caller='"+getUrlParam('formCondition').split('fo_callerIS')[1]+"'":''
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					keyField: 'id',
					gridCondition : getUrlParam('gridCondition')?"caller='"+getUrlParam('gridCondition').split('callerIS')[1]+"'":'',
					mainField: 'foid'
				}]
			}]
		});
		me.callParent(arguments);
	}
});