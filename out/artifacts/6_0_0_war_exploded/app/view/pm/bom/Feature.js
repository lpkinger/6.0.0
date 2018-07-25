Ext.define('erp.view.pm.bom.Feature',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 28%',
				saveUrl: 'pm/bom/saveFeature.action',
				deleteUrl: 'pm/bom/deleteFeature.action',
				updateUrl: 'pm/bom/updateFeature.action',
				auditUrl: 'pm/bom/auditFeature.action',
				resAuditUrl: 'pm/bom/resAuditFeature.action',
				submitUrl: 'pm/bom/submitFeature.action',
				resSubmitUrl: 'pm/bom/resSubmitFeature.action',
				getIdUrl: 'common/getId.action?seq=FEATURE_SEQ',
				keyField: 'fe_id',
				fename:'',//记录初始特征名称
				codeField: 'fe_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 72%', 
				id:'grid',
				detno: 'fd_detno',
				keyField: 'fd_id',
				mainField: 'fd_feid',
				plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
				     clicksToEdit: 1
				})],
				tbar:[{
					xtype:'tbtext',
					text:'允许特征值列表',
				},'->',{
					xtype:'button',
					id: 'toyf',
					text:'转非标准'
				},'-',{
					xtype:'button',
					id: 'tobz',
					text:'转标准'
				},'-',{
					xtype:'button',
					id:'addDetail',
					text:'新增明细'
				},'-',{
					xtype:'button',
					id:'forbidden',
					text:'禁用明细'
				},'-',{
					xtype:'button',
					id:'noforbidden',
					text:'反禁用明细'
				},'-',{
					xtype:'button',
					id:'updateRemark',
					text:'修改明细备注'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});