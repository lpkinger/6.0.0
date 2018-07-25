Ext.define('erp.view.ma.jprocess.JprocessTemplateSet',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){
		var me = this; 
		Ext.apply(me, { 
			items: [{
				id: 'Viewport',
				layout: 'auto',
				autoScroll: true,
				xtype: 'panel',
				style: {
					background: '#FFFFFF'
				},
				items: [{
					id: 'app-header',
					xtype: 'box',
					height: 5,
					style: 'background: #D4D4D4;color: #596F8F;font-size: 16px;font-weight: 200;padding: 5px 5px;text-shadow: 0 1px 0 #fff'
				},{
					xtype: 'toolbar',
					id: 'currentNodeToolbar',
					layout: {
						type: 'hbox',
						align: 'right'
					},
					bodyStyle: {
						background: "#D4D4D4",
						border: 'none'
					},
					style: {
						background: "#D4D4D4",
						border: 'none'
					},
					items: [
					        '->', {
					        	xtype: 'tbtext',
					        	id: 'label1',
					        	text: '<span style="font-weight: bold !important;font-size:18px">普通流程模板</span>'
					        },'->']

				},{
					xtype:'tabpanel',
					bodyStyle:{
						border:'none'
					},
					bodyBorder:false,
					items: [{
						xtype: 'erpFormPanel',
						anchor: '100% 100%',
						saveUrl: 'common/saveJprocessTemplate.action',
						deleteUrl: 'common/deleteJprocessTemplate.action',
						updateUrl: 'common/updateJprocessTemplate.action',
						auditUrl: 'common/auditJprocessTemplate.action',
						resAuditUrl: 'common/resAuditJprocessTemplate.action',
						submitUrl: 'common/submitJprocessTemplate.action',
						resSubmitUrl: 'common/resSubmitJprocessTemplate.action',
						getIdUrl: 'common/getId.action?seq=JPROCESSTEMPLATE_SEQ',
						keyField: 'pt_id',
						statuscodeField: 'pt_statuscode'
					},{
						title:'审批流程',
						frame:true,
						id:'workflow'
					}]
				}]			
			}]
		}); 
		me.callParent(arguments); 
	} 
});