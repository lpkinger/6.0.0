Ext.define('erp.view.oa.fee.ContractType',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'center',
				width: '62%',
				saveUrl: 'common/saveCommon.action',
				deleteUrl: 'common/deleteCommon.action',
				updateUrl: 'common/updateCommon.action',
				submitUrl: 'common/submitCommon.action',
				resSubmitUrl: 'common/resSubmitCommon.action',
				auditUrl: 'common/auditCommon.action',
				resAuditUrl: 'common/resAuditCommon.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTKIND_SEQ',
				keyField: 'ct_id',
			    codeField: 'CT_DCODE',
			    refresh: function(form) {
			    	var id = form.down('#ct_id').getValue();
			    	if(id) {
			    		form.FormUtil.loadNewStore(form, {
				    		caller: caller, 
				    		condition: "ct_id=" + form.down('#ct_id').getValue()
				    	});
			    	}
			    }
			},{
				region: 'east',
				width: '38%',
				xtype: 'contracttypetree',
				allKind:true,				
				tbar: [{
					iconCls: 'tree-add',
					name: 'add',
					text: $I18N.common.button.erpAddButton
				},{
					iconCls: 'tree-delete',
					name: 'delete',
					text: $I18N.common.button.erpDeleteButton
				},{
					width:120,
			        xtype: 'contracttypesearchfield',
			        caller:'ContractType',
			        id: 'searchField'
				},{
			        iconCls: 'tree-back',
			        cls: 'x-btn-tb',
			        width: 15,
			        tooltip: $I18N.common.main.treeBack,
			        hidden: false,
			    	handler: function(){
			    		Ext.getCmp('tree-panel').getTreeRootNode(0);
			    		Ext.getCmp('searchField').setValue(null);
			    	}
			    }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});