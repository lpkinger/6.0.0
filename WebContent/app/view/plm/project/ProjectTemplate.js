Ext.define('erp.view.plm.project.ProjectTemplate',{ 
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
					anchor: '100% 50%',
					saveUrl: 'plm/project/saveProjectTemplate.action',
					deleteUrl:'plm/project/deleteProjectTemplate.action',
					updateUrl:'plm/project/updateProjectTemplate.action',
					getIdUrl: 'common/getId.action?seq=PROJECTTEMPLATE_SEQ',
					keyField: 'pt_id',
                    codeField:'pt_code',
				},
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});